package com.majalis.save;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Base64Coder;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.IntSet;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.SerializationException;
import com.majalis.battle.BattleAttributes;
import com.majalis.character.EnemyCharacter;
import com.majalis.character.Item;
import com.majalis.character.Perk;
import com.majalis.character.PlayerCharacter;
import com.majalis.character.PlayerCharacter.QuestFlag;
import com.majalis.character.SexualExperience;
import com.majalis.character.AbstractCharacter.Stat;
import com.majalis.character.Techniques;
import com.majalis.encounter.EncounterCode;
import com.majalis.scenes.ShopScene.Shop;
/*
 * Used for file handling, both reading and writing - both game files and encounter replay files.
 */
public class SaveManager implements SaveService, LoadService {
    
	private boolean encoded;
    private final FileHandle file; 
    private final FileHandle profileFile;
    private GameSave save;
    private ProfileSave profileSave;
   
    public SaveManager(SaveManager original, String path) {
    	this.encoded = original.encoded;
    	this.profileFile = original.profileFile;
    	this.file = Gdx.files.local(path);
    	save = original.save;
    	profileSave = original.profileSave;
    	saveToJson(save);
    }
    
    public SaveManager(boolean encoded, String path, String profilePath) {
        this.encoded = encoded;
        file = Gdx.files.local(path);   
        profileFile = Gdx.files.local(profilePath);
        save = getSave();
        profileSave = getProfileSave();
    }
    
    public void manualSave(String path) {
    	new SaveManager(this, path);
    }
    
    public void saveDataValue(ProfileEnum key, Object object) {
    	saveDataValue(key, object, true);
    }
    
    public void saveDataValue(ProfileEnum key, Object object, boolean saveToJson) {
    	switch (key) {
			case KNOWLEDGE:		profileSave.addKnowledge((String) object); break;
		}
    	if (saveToJson) {
    		saveToProfileJson(profileSave);
    	}
    }
    
    @SuppressWarnings("unchecked")
    public <T> T loadDataValue(ProfileEnum key, Class<?> type) {
    	switch (key) {
			case KNOWLEDGE: 		return (T) (ObjectMap<String, Integer>) profileSave.enemyKnowledge;
		}
    	return null;
    }
    
	public String saveDataValue(SaveEnum key, Object object) {
		return saveDataValue(key, object, true);
    }
    
	public String saveDataValue(SaveEnum key, Object object, boolean saveToJson) {
		String result = null;
    	switch (key) {
	    	case PLAYER: 			save.player = (PlayerCharacter) object; break;
	    	case ENEMY: 			save.enemy = (EnemyCharacter) object; break;
	    	case SCENE_CODE: 		save.sceneCode = (Integer) object; if ((Integer)object == 0) save.player.refresh(); break;
	    	case CONTEXT: 			save.context = (GameContext) object; break;
	    	case RETURN_CONTEXT: 	save.returnContext = (GameContext) object; break;
	    	case NODE_CODE: 		save.nodeCode = (Integer) object; break;
	    	case CAMERA_POS:		save.cameraPos = new Vector3((Vector2) object, 0); break;
	    	case ENCOUNTER_CODE:	save.encounterCode = (EncounterCode) object; break;
	    	case VISITED_LIST:		save.visitedList.add((Integer) object); break;
	    	case BATTLE_CODE:		save.battleAttributes = (BattleAttributes) object; break;
	    	case CLASS:				save.player.setJobClass((JobClass) object); save.player.load(); break;
	    	case WORLD_SEED:		save.worldSeed = (Integer) object; break;
	    	case HEALTH: 			result = save.player.modHealth((Integer) object); break; 
	    	case SKILL: 			save.player.addSkill((Techniques) object, 1); result = "Gained" + ((Techniques) object).toString() + " technique!"; break; // this should get a result back from addSkill
	    	case PERK:				save.player.addPerk((Perk) object, 1); result = "Gained" + ((Perk) object).getLabel() + " perk!"; break; // this should get a result back from addPerk
	    	case FOOD:				result = save.player.modFood((Integer) object); break; // this should get a result back from modFood
	    	case TIME:				save.time += (Integer) object; result = save.player.timePass((Integer) object);  if(save.time % 6 == 0) result += "\n" + save.player.debtTick((Integer) object); break;
	    	case EXPERIENCE:		save.player.modExperience((Integer) object); result = "+" + ((Integer) object).toString() + " XP!"; break; // this should get a result back from modExperience
	    	case GOLD:				result = save.player.modMoney((Integer) object); break; 
	    	case DEBT:				result = save.player.modDebt((Integer) object); break;
	    	case MODE:				save.mode = (GameMode) object; if ((GameMode) object == GameMode.SKIRMISH) save.player.load() ; break;
	    	case MUSIC:				save.music = (String) object; break;
	    	case CONSOLE:			save.console = extracted(object); break;
	    	case ANAL:				result = save.player.receiveSex((SexualExperience) object); break;
	    	case ITEM:				result = save.player.receiveItem((Item) object); break;
	    	case SHOP:				save.shops.put(((Shop) object).getShopCode(), (Shop) object); break;
	    	case GOBLIN_VIRGIN:		save.player.setGoblinVirginity((Boolean) object); break;
	    	case QUEST: 			QuestFlag flag = (QuestFlag) object; save.player.setQuestStatus(flag.type, flag.value); break;
	    	case ENCOUNTER_END:		save.player.popPortraitPath(); break;
    	}	
    	if (saveToJson) {
    		saveToJson(save); //Saves current save immediately.
    	}
        return result;
	}

	@SuppressWarnings("unchecked")
	private Array<String> extracted(Object object) {
		return (Array<String>) object;
	}
	
    @SuppressWarnings("unchecked")
    public <T> T loadDataValue(SaveEnum key, Class<?> type) {
    	switch (key) {
	    	case PLAYER: 			return (T) (PlayerCharacter)save.player;
	    	case ENEMY: 			return (T) (EnemyCharacter)save.enemy;
	    	case SCENE_CODE: 		return (T) (Integer)save.sceneCode;
	    	case CONTEXT: 			return (T) save.context;
	    	case RETURN_CONTEXT: 	return (T) save.returnContext;
	    	case NODE_CODE: 		return (T) (Integer)save.nodeCode;
	    	case CAMERA_POS: 		return (T) (Vector3)save.cameraPos;
	    	case ENCOUNTER_CODE:	return (T) save.encounterCode;
	    	case VISITED_LIST:		IntSet set = new IntSet();
	    							set.addAll(save.visitedList);
	    							return (T) set;
	    	case BATTLE_CODE:		return (T) save.battleAttributes;
	    	case CLASS:				return (T) save.player.getJobClass();
	    	case WORLD_SEED:		return (T) (Integer) save.worldSeed;
	    	case HEALTH:			return (T) (Integer) save.player.getCurrentHealth();
	    	case SKILL:				return (T) (ObjectMap<Techniques, Integer>) save.player.getSkills();	
	    	case PERK:				return (T) (ObjectMap<Perk, Integer>) save.player.getPerks();	
	    	case FOOD: 				return (T) (Integer) save.player.getFood();
	    	case EXPERIENCE:		return (T) (Integer) save.player.getExperience();
	    	case MODE:				return (T) (GameMode) save.mode;
	    	case MUSIC:				return (T) (String) save.music;
	    	case CONSOLE:			return (T) (Array<String>) save.console;
	    	case ANAL:			
	    	case ITEM:
	    	case GOLD:
	    	case DEBT:
	    	case ENCOUNTER_END:
	    	case GOBLIN_VIRGIN:		break;
	    	case SHOP:				return (T) (ObjectMap<String, Shop>) save.shops;
	    	case QUEST:				break;
	    	case TIME :				return (T) (Integer) save.time;
    	}	
    	return null;
    }
    
    private void saveToJson(GameSave save) {
        Json json = new Json();
        json.setOutputType(OutputType.json);
        if(encoded) file.writeString(Base64Coder.encodeString(json.prettyPrint(save)), false);
        else {   	
        	Group playerParent = save.player.getParent();
       
        	if (playerParent != null) playerParent.removeActor(save.player, false);
        	Group enemyParent = new Group();
        	if (save.enemy != null) {
            	enemyParent = save.enemy.getParent();
            	if (enemyParent != null) {
            		enemyParent.removeActor(save.enemy, false);
            	}	
        	}
        	try {
        		file.writeString(json.prettyPrint(save), false);
        	}
        	catch (GdxRuntimeException ex) {
        		ex.printStackTrace();
        	}
        	
        	if (playerParent != null) playerParent.addActor(save.player);
        	if (save.enemy != null) {
        		if (enemyParent != null) {
        			enemyParent.addActor(save.enemy);
        		}	
        	}
        }
    }
    
    private void saveToProfileJson(ProfileSave save) {
        Json json = new Json();
        json.setOutputType(OutputType.json);
        if(encoded) profileFile.writeString(Base64Coder.encodeString(json.prettyPrint(save)), false);
        else {   	
        	profileFile.writeString(json.prettyPrint(save), false);
        }
    }
    
    private GameSave getSave() {
        GameSave save;
        if(file.exists()) {
	        Json json = new Json();
	        try {
		        if(encoded)save = json.fromJson(GameSave.class, Base64Coder.decodeString(file.readString()));
		        else {
		        	save = json.fromJson(GameSave.class,file.readString());
		        }
	        }
	        catch(SerializationException ex) {
	        	System.err.println(ex.getMessage());
	        	save = getDefaultSave();
	        }
        }
        else {
        	save = getDefaultSave();
        }
        return save==null ? new GameSave(true) : save;
    }
    
    private ProfileSave getProfileSave() {
    	ProfileSave save;
        if(profileFile.exists()) {
	        Json json = new Json();
	        if(encoded)save = json.fromJson(ProfileSave.class, Base64Coder.decodeString(profileFile.readString()));
	        else {
	        	save = json.fromJson(ProfileSave.class,profileFile.readString());
	        }
        }
        else {
        	save = getDefaultProfileSave();
        }
        return save==null ? new ProfileSave(true) : save;
    }
    
    public void newSave() {
    	save = getDefaultSave();
    }
    
    public void newSave(String path) {
    	save = new SaveManager(encoded, path, profileFile.path()).save;
    	saveToJson(save);
    }
    
    private GameSave getDefaultSave() {
    	GameSave tempSave = new GameSave(true);
    	saveToJson(tempSave);
    	return tempSave;
    }
    
    private ProfileSave getDefaultProfileSave() {
    	ProfileSave tempSave = new ProfileSave(true);
    	saveToProfileJson(tempSave);
    	return tempSave;
    }
    
    public static class GameSave {
		
		private GameContext context;
		private GameContext returnContext;
    	private GameMode mode;
    	private String music;
    	private int worldSeed;
    	private int sceneCode;
    	private EncounterCode encounterCode;
    	private int nodeCode;
    	private Array<String> console;
    	private Vector3 cameraPos;
    	private IntArray visitedList;
    	// this can probably be refactored to contain a particular battle, but may need to duplicate the player character
    	private BattleAttributes battleAttributes;
    	private int time;
    	private PlayerCharacter player;
    	private EnemyCharacter enemy;
    	private ObjectMap<String, Shop> shops;
    	
    	// 0-arg constructor for JSON serialization: DO NOT USE
		@SuppressWarnings("unused")
		private GameSave() {}
    	
		// default save values-
    	public GameSave(boolean defaultValues) {
    		if (defaultValues) {
    			context = GameContext.ENCOUNTER;
    			worldSeed = (int) (Math.random()*10000);
    			// -1 sceneCode is the magic number to designate that a scene doesn't need to be loaded; just use the first (last) scene in the list
    			sceneCode = -1;
    			encounterCode = EncounterCode.INITIAL;
    			returnContext = GameContext.WORLD_MAP;
        		nodeCode = 1;
        		console = new Array<String>();
        		shops = new ObjectMap<String, Shop>();
        		cameraPos = new Vector3(500, 500, 0);
        		visitedList = new IntArray(true, new int[]{1}, 0, 1);
        		player = new PlayerCharacter(true);
        		time = 0;
    		}
    	}
    }
    
    public static class ProfileSave {
    	
    	private ObjectMap<String, Integer> enemyKnowledge;
    	
    	// 0-arg constructor for JSON serialization: DO NOT USE
		private ProfileSave() {}
    	private ProfileSave(boolean defaultValues) {
    		enemyKnowledge = new ObjectMap<String, Integer>();
    	}
    	
    	protected void addKnowledge(String knowledge) { addKnowledge(knowledge, 1); }
    	protected void addKnowledge(String knowledge, int amount) { enemyKnowledge.put(knowledge, amount); }
    	
    }
    
	public enum JobClass {
		WARRIOR ("Warrior") {
			@Override
			public int getBaseStat(Stat stat) {
				switch (stat) {
					case STRENGTH:    return 7;
					case ENDURANCE:   return 5;
					case AGILITY:     return 4;
					case PERCEPTION:  return 3;
					case MAGIC:       return 1;
					case CHARISMA:    return 2;
					default: return -1;
				}
			}}, 
		PALADIN ("Paladin") {
			@Override
			public int getBaseStat(Stat stat) {
				switch (stat) {
					case STRENGTH:    return 4;
					case ENDURANCE:   return 7;
					case AGILITY:     return 2;
					case PERCEPTION:  return 1;
					case MAGIC:       return 3;
					case CHARISMA:    return 5;
					default: return -1;
				}
			}}, 
		THIEF ("Thief") {
			@Override
			public int getBaseStat(Stat stat) {
				switch (stat) {
					case STRENGTH:    return 3;
					case ENDURANCE:   return 3;
					case AGILITY:     return 7;
					case PERCEPTION:  return 4;
					case MAGIC:       return 1;
					case CHARISMA:    return 4;
					default: return -1;
				}
			}}, 
		RANGER ("Ranger") {
			@Override
			public int getBaseStat(Stat stat) {
				switch (stat) {
					case STRENGTH:    return 3;
					case ENDURANCE:   return 2;
					case AGILITY:     return 5;
					case PERCEPTION:  return 7;
					case MAGIC:       return 1;
					case CHARISMA:    return 4;
					default: return -1;
				}
			}}, 
		MAGE ("Mage") {
			@Override
			public int getBaseStat(Stat stat) {
				switch (stat) {
					case STRENGTH:    return 2;
					case ENDURANCE:   return 2;
					case AGILITY:     return 2;
					case PERCEPTION:  return 5;
					case MAGIC:       return 7;
					case CHARISMA:    return 4;
					default: return -1;
				}
			}}, 
		ENCHANTRESS ("Enchanter") {
			@Override
			public int getBaseStat(Stat stat) {
				switch (stat) {
					case STRENGTH:    return 3;
					case ENDURANCE:   return 3;
					case AGILITY:     return 3;
					case PERCEPTION:  return 3;
					case MAGIC:       return 3;
					case CHARISMA:    return 7;
					default: return -1;
				}
			}};
		private final String label;

		JobClass(String label) {
		    this.label = label;
		 }
		public String getLabel() {return label;}
		public abstract int getBaseStat(Stat stat);
	}
	
	public enum GameContext {
		ENCOUNTER,
		WORLD_MAP,
		BATTLE, 
		LEVEL,
		TOWN,
		GAME_OVER
	}
	
	public enum GameMode {
		STORY,
		SKIRMISH
	}
	
}
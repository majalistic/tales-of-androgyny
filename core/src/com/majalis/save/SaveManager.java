package com.majalis.save;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Base64Coder;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.IntSet;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.badlogic.gdx.utils.ObjectMap;
import com.majalis.battle.BattleCode;
import com.majalis.character.EnemyCharacter;
import com.majalis.character.Perk;
import com.majalis.character.PlayerCharacter;
import com.majalis.character.AbstractCharacter.Stat;
import com.majalis.character.Techniques;
/*
 * Used for file handling, both reading and writing - both game files and encounter replay files.
 */
public class SaveManager implements SaveService, LoadService{
    
	private boolean encoded;
    private final FileHandle file; 
    private final FileHandle profileFile;
    private GameSave save;
    private ProfileSave profileSave;
   
    public SaveManager(boolean encoded, String path, String profilePath){
        this.encoded = encoded;
        file = Gdx.files.local(path);   
        profileFile = Gdx.files.local(profilePath);
        save = getSave();
        profileSave = getProfileSave();
    }
    
    public void saveDataValue(ProfileEnum key, Object object){
    	switch (key) {
    		case KNOWLEDGE: 		profileSave.addKnowledge((String) object); break;
    	}
    	saveToProfileJson(profileSave);
    }
    
    @SuppressWarnings("unchecked")
    public <T> T loadDataValue(ProfileEnum key, Class<?> type){
    	switch (key) {
			case KNOWLEDGE: 		return (T) (ObjectMap<String, Integer>) profileSave.enemyKnowledge;
		}
    	return null;
    }
    
	public void saveDataValue(SaveEnum key, Object object){
    	switch (key) {
	    	case PLAYER: 			save.player = (PlayerCharacter) object; break;
	    	case ENEMY: 			save.enemy = (EnemyCharacter) object; break;
	    	case SCENE_CODE: 		save.sceneCode = (Integer) object; break;
	    	case CONTEXT: 			save.context = (GameContext) object; break;
	    	case NODE_CODE: 		save.nodeCode = (Integer) object; break;
	    	case CAMERA_POS:		save.cameraPos = new Vector3((Vector2) object, 0); break;
	    	case ENCOUNTER_CODE:	save.encounterCode = (Integer) object; break;
	    	case VISITED_LIST:		save.visitedList.add((Integer) object); break;
	    	case BATTLE_CODE:		save.battleCode = (BattleCode) object; break;
	    	case CLASS:				save.player.setJobClass((JobClass) object); break;
	    	case WORLD_SEED:		save.worldSeed = (Integer) object; break;
	    	case HEALTH: 			save.player.modHealth((Integer) object); break;
	    	case SKILL: 			save.player.addSkill((Techniques) object, 1); break;
	    	case PERK:				save.player.addPerk((Perk) object, 1); break;
	    	case FOOD:				save.player.modFood((Integer) object); break;
	    	case EXPERIENCE:		save.player.modExperience((Integer) object); break;
    	}	
        saveToJson(save); //Saves current save immediately.
    }
    
    @SuppressWarnings("unchecked")
    public <T> T loadDataValue(SaveEnum key, Class<?> type){
    	switch (key) {
	    	case PLAYER: 			return (T) (PlayerCharacter)save.player;
	    	case ENEMY: 			return (T) (EnemyCharacter)save.enemy;
	    	case SCENE_CODE: 		return (T) (Integer)save.sceneCode;
	    	case CONTEXT: 			return (T) save.context;
	    	case NODE_CODE: 		return (T) (Integer)save.nodeCode;
	    	case CAMERA_POS: 		return (T) (Vector3)save.cameraPos;
	    	case ENCOUNTER_CODE:	return (T) (Integer) save.encounterCode;
	    	case VISITED_LIST:		IntSet set = new IntSet();
	    							set.addAll(save.visitedList);
	    							return (T) set;
	    	case BATTLE_CODE:		return (T) save.battleCode;
	    	case CLASS:				return (T) save.player.getJobClass();
	    	case WORLD_SEED:		return (T) (Integer) save.worldSeed;
	    	case HEALTH:			return (T) (Integer) save.player.getCurrentHealth();
	    	case SKILL:				return (T) (ObjectMap<Techniques, Integer>) save.player.getSkills();	
	    	case PERK:				return (T) (ObjectMap<Perk, Integer>) save.player.getPerks();	
	    	case FOOD: 				return (T) (Integer) save.player.getFood();
	    	case EXPERIENCE:		return (T) (Integer) save.player.getExperience();
    	}	
    	return null;
    }
    
    private void saveToJson(GameSave save){
        Json json = new Json();
        json.setOutputType(OutputType.json);
        if(encoded) file.writeString(Base64Coder.encodeString(json.prettyPrint(save)), false);
        else {   	
        	Group playerParent = save.player.getParent();
       
        	if (playerParent != null) playerParent.removeActor(save.player, false);
        	Group enemyParent = new Group();
        	if (save.enemy != null){
            	enemyParent = save.enemy.getParent();
            	if (enemyParent != null){
            		enemyParent.removeActor(save.enemy, false);
            	}	
        	}
        	file.writeString(json.prettyPrint(save), false);
        	if (playerParent != null) playerParent.addActor(save.player);
        	if (save.enemy != null){
        		if (enemyParent != null){
        			enemyParent.addActor(save.enemy);
        		}	
        	}
        }
    }
    
    private void saveToProfileJson(ProfileSave save){
        Json json = new Json();
        json.setOutputType(OutputType.json);
        if(encoded) profileFile.writeString(Base64Coder.encodeString(json.prettyPrint(save)), false);
        else {   	
        	profileFile.writeString(json.prettyPrint(save), false);
        }
    }
    
    private GameSave getSave(){
        GameSave save;
        if(file.exists()){
	        Json json = new Json();
	        if(encoded)save = json.fromJson(GameSave.class, Base64Coder.decodeString(file.readString()));
	        else {
	        	save = json.fromJson(GameSave.class,file.readString());
	        }
        }
        else {
        	save = getDefaultSave();
        }
        return save==null ? new GameSave(true) : save;
    }
    
    private ProfileSave getProfileSave(){
    	ProfileSave save;
        if(profileFile.exists()){
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
    
    public void newSave(){
    	save = getDefaultSave();
    }
    
    private GameSave getDefaultSave(){
    	GameSave tempSave = new GameSave(true);
    	saveToJson(tempSave);
    	return tempSave;
    }
    
    private ProfileSave getDefaultProfileSave(){
    	ProfileSave tempSave = new ProfileSave(true);
    	saveToProfileJson(tempSave);
    	return tempSave;
    }
    
    public static class GameSave {
    	
    	private GameContext context;
    	private int worldSeed;
    	private int sceneCode;
    	private int encounterCode;
    	private int nodeCode;
    	private Vector3 cameraPos;
    	private IntArray visitedList;
    	// this can probably be refactored to contain a particular battle, but may need to duplicate the player character
    	private BattleCode battleCode;
    	private PlayerCharacter player;
    	private EnemyCharacter enemy;
    	
    	// 0-arg constructor for JSON serialization: DO NOT USE
		@SuppressWarnings("unused")
		private GameSave() {}
    	
		// default save values-
    	public GameSave(boolean defaultValues) {
    		if (defaultValues){
    			context = GameContext.ENCOUNTER;
    			worldSeed = (int) (Math.random()*10000);
    			// 0 sceneCode is the magic number to designate that a scene doesn't need to be loaded; just use the first (last) scene in the list
    			sceneCode = 0;
    			encounterCode = -2;
        		nodeCode = 1;
        		cameraPos = new Vector3(500, 500, 0);
        		visitedList = new IntArray(true, new int[]{1}, 0, 1);
        		player = new PlayerCharacter(true);
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
    	
    	protected void addKnowledge(String knowledge){ addKnowledge(knowledge, 1); }
    	protected void addKnowledge(String knowledge, int amount){ enemyKnowledge.put(knowledge, amount); }
    	
    }
    
	public enum JobClass {
		WARRIOR ("Warrior") {
			@Override
			public int getBaseStat(Stat stat) {
				switch (stat){
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
				switch (stat){
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
				switch (stat){
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
				switch (stat){
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
				switch (stat){
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
				switch (stat){
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
		public String getLabel(){return label;}
		public abstract int getBaseStat(Stat stat);
	}
	
	public enum GameContext {
		ENCOUNTER,
		WORLD_MAP,
		BATTLE, 
		LEVEL,
		GAME_OVER
	}
}
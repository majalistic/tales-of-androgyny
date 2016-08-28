package com.majalis.traprpg;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Base64Coder;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.badlogic.gdx.utils.ObjectSet;
/*
 * Used for file handling, both reading and writing - both game files and encounter replay files.
 */
public class SaveManager implements SaveService, LoadService{
    
	private boolean encoded;
    private final FileHandle file;   
    private GameSave save;
   
    public SaveManager(boolean encoded){
        this.encoded = encoded;
        file = Gdx.files.local("bin/save.json");   
        save = getSave();
    }
    
    @SuppressWarnings("unchecked")
	public void saveDataValue(String key, Object object){
    	// currently stringly-typed if else chain, just to get the GameSave modification stood up, need to think of a better way (at least enum-ize it and switch/case)
    	// likely will need to refactor and have each of these elements saved into the gamestate as a serialized encounter instead of an encounter code
    	if (key.equals("Player")){
    		save.player = (PlayerCharacter) object;
    	}
    	else if (key.equals("SceneCode")){
    		save.sceneCode = (Integer) object;
    	}
    	else if (key.equals("Context")){
        	save.context = (GameContext) object;
        }
    	else if (key.equals("NodeCode")){
    		save.nodeCode = (Integer) object;
    	}
    	else if (key.equals("EncounterCode")){
    		save.encounterCode = (Integer) object;
    	}
    	else if (key.equals("VisitedList")){
    		save.visitedList = castToIntArray((ObjectSet<Integer>) object);
    	}
    	else if (key.equals("BattleCode")){
    		save.battleCode = (BattleCode) object;
    	}
    	else if (key.equals("Class")){
    		save.jobClass = (JobClass) object;
    	}
        saveToJson(save); //Saves current save immediately.
    }
    
    @SuppressWarnings("unchecked")
    public <T> T loadDataValue(String key, Class<?> type){
    	if (key.equals("Player")){
    		return (T) (PlayerCharacter)save.player;
    	}
    	else if (key.equals("SceneCode")){
    		return (T) (Integer)save.sceneCode;
    	}
    	else if (key.equals("NodeCode")){
    		return (T) (Integer)save.nodeCode;
    	}
    	else if (key.equals("Context")){
    		return (T) save.context;
    	}
    	else if (key.equals("EncounterCode")){
    		return (T) (Integer) save.encounterCode;
    	}
    	else if (key.equals("VisitedList")){
    		ObjectSet<Integer> set = new ObjectSet<Integer>();
    		for (int member : save.visitedList){
    			set.add(member);
    		}
    		return (T) set;
    	}
    	else if (key.equals("BattleCode")){
    		return (T) save.battleCode;
    	}
    	else if (key.equals("Class")){
    		return (T) save.jobClass;
    	}
        else return null;   //this if() avoids an exception, but check for null on load.
    }
    
    private void saveToJson(GameSave save){
        Json json = new Json();
        json.setOutputType(OutputType.json);
        if(encoded) file.writeString(Base64Coder.encodeString(json.prettyPrint(save)), false);
        else {   	
        	file.writeString(json.prettyPrint(save), false);
        }
    }
    
    private GameSave getSave(){
        GameSave save;
        if(file.exists()){
	        Json json = new Json();
	        if(encoded)save = json.fromJson(GameSave.class, Base64Coder.decodeString(file.readString()));
	        else save = json.fromJson(GameSave.class,file.readString());
        }
        else {
        	save = getDefaultSave();
        }
        return save==null ? new GameSave(true) : save;
    }
    
    public void newSave(){
    	save = getDefaultSave();
    }
    
    private GameSave getDefaultSave(){
    	GameSave tempSave = new GameSave(true);
    	saveToJson(tempSave);
    	return tempSave;
    }
    
    private int[] castToIntArray(ObjectSet<Integer> set){
    	int[] array = new int[set.size];
    	int ii = 0;
    	for (int member : set){
    		array[ii++] = member;
    	}
    	return array;
    }
    
    public static class GameSave{
    	
		public GameContext context;
		public int sceneCode;
		public int encounterCode;
    	public int nodeCode;
    	public int[] visitedList;
    	public JobClass jobClass;
    	public BattleCode battleCode;
    	public PlayerCharacter player;
    	
    	// 0-arg constructor for JSON serialization: DO NOT USE
    	@SuppressWarnings("unused")
		private GameSave(){}
    	
    	// default save values-
    	public GameSave(boolean defaultValues){
    		if (defaultValues){
    			context = GameContext.ENCOUNTER;
    			// 0 sceneCode is the magic number to designate that a scene doesn't need to be loaded; just use the first (last) scene in the list
    			sceneCode = 0;
    			encounterCode = 0;
        		nodeCode = 1;
        		visitedList = new int[]{1};
        		player = new PlayerCharacter(true);
    		}
    	}
    }
    
	public enum JobClass {
		WARRIOR ("Warrior"),
		PALADIN ("Paladin"),
		THIEF ("Thief"),
		RANGER ("Ranger"),
		MAGE ("Mage"),
		ENCHANTRESS ("Enchanter");
		
		private final String label;

		JobClass(String label) {
		    this.label = label;
		 }
		public String getLabel(){return label;}
	}
	
	public enum GameContext {
		ENCOUNTER,
		WORLD_MAP,
		BATTLE
	}
}
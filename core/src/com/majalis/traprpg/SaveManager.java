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
    	if (key.equals("NodeCode")){
    		save.nodeCode = (Integer) object;
    	}
    	else if (key.equals("Context")){
    		save.context = (GameContext) object;
    	}
    	else if (key.equals("EncounterCode")){
    		save.encounterCode = (Integer) object;
    	}
    	else if (key.equals("VisitedList")){
    		save.visitedList = castToIntArray((ObjectSet<Integer>) object);
    	}
    	else if (key.equals("Class")){
    		save.jobClass = (JobClass) object;
    	}
        saveToJson(); //Saves current save immediately.
    }
    
    private int[] castToIntArray(ObjectSet<Integer> set){
    	int[] array = new int[set.size];
    	int ii = 0;
    	for (int member : set){
    		array[ii++] = member;
    	}
    	return array;
    }
    
    public void newSave(){
    	saveToJson(getDefaultSave());
    }
    
    @SuppressWarnings("unchecked")
    public <T> T loadDataValue(String key, Class<?> type){
    	if (key.equals("NodeCode")){
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
    	else if (key.equals("Class")){
    		return (T) save.jobClass;
    	}
        else return null;   //this if() avoids an exception, but check for null on load.
    }
    
    private void saveToJson(){
    	saveToJson(save);
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
        GameSave save = new GameSave();
        if(file.exists()){
	        Json json = new Json();
	        if(encoded)save = json.fromJson(GameSave.class, Base64Coder.decodeString(file.readString()));
	        else save = json.fromJson(GameSave.class,file.readString());
        }
        else {
        	save = getDefaultSave();
        }
        return save==null ? new GameSave() : save;
    }
    
    private GameSave getDefaultSave(){
    	save = new GameSave();
    	return save;
    }
    
    // this can be replaced with a GameState
    private static class GameSave{
    	private GameContext context;
    	private int encounterCode;
    	private int nodeCode;
    	private int[] visitedList;
    	private JobClass jobClass;
    	
    	// default save values
    	public GameSave(){
    		context = GameContext.ENCOUNTER;
    		jobClass = JobClass.WARRIOR;
    		encounterCode = 0;
    		nodeCode = 1;
    		visitedList = new int[]{1};        	
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
		WORLD_MAP
	}
}
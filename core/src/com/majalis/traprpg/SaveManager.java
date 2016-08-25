package com.majalis.traprpg;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Base64Coder;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.badlogic.gdx.utils.ObjectMap;
/*
 * Used for file handling, both reading and writing - both game files and encounter replay files.
 */
public class SaveManager implements SaveService, LoadService{
    
    private static ObjectMap<String, Object> defaultSaveData;
    static{
    	defaultSaveData = new ObjectMap<String, Object>();
    	defaultSaveData.put("Class", " ");
    	defaultSaveData.put("EncounterCode", 0);
    	// initial node is code "1" for now
    	defaultSaveData.put("NodeCode", 1);
    	defaultSaveData.put("VisitedList", new Integer[]{1});
    	defaultSaveData.put("Context", GameWorldManager.GameContext.ENCOUNTER);
    }
	private boolean encoded;
    private final FileHandle file;   
    private Save save;
   
    public SaveManager(boolean encoded){
        this.encoded = encoded;
        file = Gdx.files.local("bin/save.json");   
        save = getSave();
    }
    
    public void saveDataValue(String key, Object object){
        save.data.put(key, object);
        saveToJson(); //Saves current save immediately.
    }
    
    public void newSave(){
    	saveToJson(getDefaultSave());
    }
    
    @SuppressWarnings("unchecked")
    public <T> T loadDataValue(String key, Class<?> type){
        if(save.data.containsKey(key))return (T) save.data.get(key);
        else return null;   //this if() avoids an exception, but check for null on load.
    }
    
    private void saveToJson(){
    	saveToJson(save);
    }
    
    private void saveToJson(Save save){
        Json json = new Json();
        json.setOutputType(OutputType.json);
        if(encoded) file.writeString(Base64Coder.encodeString(json.prettyPrint(save)), false);
        else file.writeString(json.prettyPrint(save), false);
    }
    
    private Save getSave(){
        Save save = new Save();
        if(file.exists()){
	        Json json = new Json();
	        if(encoded)save = json.fromJson(Save.class, Base64Coder.decodeString(file.readString()));
	        else save = json.fromJson(Save.class,file.readString());
        }
        else {
        	save = getDefaultSave();
        }
        return save==null ? new Save() : save;
    }
    
    private Save getDefaultSave(){
    	Save defaultSave = new Save(defaultSaveData);
    	save = defaultSave;
    	return defaultSave;
    }
    
    private static class Save{
    	public ObjectMap<String, Object> data = new ObjectMap<String, Object>();
    	public Save(){
    		
    	}
    	public Save(ObjectMap<String, Object> data){
    		for (String key: data.keys()){
    			this.data.put(key, data.get(key));
    		}
    	}        
    }
}
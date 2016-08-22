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
public class SaveManager {
    
    private boolean encoded;
    private FileHandle file;   
    private Save save;
    private SaveType saveType;
   
    public SaveManager(boolean encoded){
        this.encoded = encoded;
        file = Gdx.files.local("bin/save.json");   
        save = getSave();
    }
   
    // this needs to be removed - instead the savemanager should only be "loaded" once someone selects to do so - before then, it should be in a dormant state
    // once someone begins a new game, a save should be generated
    public enum SaveType{
    	NEW, LOAD
    }
    
    public void setSaveType(SaveType type){
    	saveType = type;
    }
    
    @SuppressWarnings("unchecked")
    public <T> T loadDataValue(String key, Class<?> type){
    	if (saveType == SaveType.NEW) return (T) "";
        if(save.data.containsKey(key))return (T) save.data.get(key);
        else return null;   //this if() avoids an exception, but check for null on load.
    }
    
    public void saveDataValue(String key, Object object){
        save.data.put(key, object);
        saveToJson(); //Saves current save immediately.
    }
    
    private void saveToJson(){
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
        return save==null ? new Save() : save;
    }
    
    private static class Save{
        public ObjectMap<String, Object> data = new ObjectMap<String, Object>();
    }
}
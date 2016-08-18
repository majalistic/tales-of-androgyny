package com.majalis.traprpg;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Base64Coder;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.badlogic.gdx.utils.ObjectMap;

public class SaveManager {
    
    private boolean encoded;
    
    private FileHandle file = Gdx.files.local("bin/save.json"); 
    
    private Save save = getSave();
    
    public SaveManager(boolean encoded){
        this.encoded = encoded;
    }
    
    public static class Save{
        public ObjectMap<String, Object> data = new ObjectMap<String, Object>();
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

    public void saveToJson(){
        Json json = new Json();
        json.setOutputType(OutputType.json);
        if(encoded) file.writeString(Base64Coder.encodeString(json.prettyPrint(save)), false);
        else file.writeString(json.prettyPrint(save), false);
    }
    
    @SuppressWarnings("unchecked")
    public <T> T loadDataValue(String key, Class<?> type){
        if(save.data.containsKey(key))return (T) save.data.get(key);
        else return null;   //this if() avoids an exception, but check for null on load.
    }
    
    public void saveDataValue(String key, Object object){
        save.data.put(key, object);
        saveToJson(); //Saves current save immediately.
    }
    
    public ObjectMap<String, Object> getAllData(){
        return save.data;
    }
}
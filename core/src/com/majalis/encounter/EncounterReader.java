package com.majalis.encounter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.badlogic.gdx.utils.ObjectMap;
/*
 * Controls the logic for reading from the script file.
 */
public class EncounterReader {
	private final FileHandle file;   
	private final ObjectMap<String, String[]> scriptData;
	
    public EncounterReader(String path){
        file = Gdx.files.internal(path);
        scriptData = getScriptData();
    }
   
	private ObjectMap<String, String[]> getScriptData(){
		ObjectMap<String, String[]> data = new ObjectMap<String, String[]>();
        if(file.exists()){
        	data = convertToIntMap(new Json().fromJson(FullScript.class, file.readString()));
        }
        else {
        	saveToJson(new FullScript(new ScriptData[]{new ScriptData("000-00", new String[]{"Test", "This is"}), new ScriptData("001-00", new String[]{"Second", "A"})}));
        }
        return data;
    }
	
	private ObjectMap<String, String[]> convertToIntMap(FullScript data){
		ObjectMap<String, String[]> convertedData = new ObjectMap<String, String[]>();
		for (ScriptData datum: data.script){
			convertedData.put(datum.key, datum.scriptLines);
		}
		return convertedData;
	}
	
    private void saveToJson(FullScript data){
        Json json = new Json();
        json.setOutputType(OutputType.json);
        file.writeString(json.prettyPrint(data), false);
    }
  
    public String[] loadScript(String key){
    	return scriptData.get(key);
    }
    
    private static class FullScript{
    	public ScriptData[] script;
    	// 0-arg constructor for JSON serialization: DO NOT USE
    	@SuppressWarnings("unused")
		private FullScript(){}
    	protected FullScript(ScriptData[] script){
    		this.script = script;
    	}
    }
    
    /* package for containing the data in a pretty format, an array of which deserializes to an IntMap */
    private static class ScriptData{
    	public String key;
    	public String[] scriptLines;
    	// 0-arg constructor for JSON serialization: DO NOT USE
    	@SuppressWarnings("unused")
		private ScriptData(){}
    	protected ScriptData(String key, String[] scriptLines){
    		this.key = key;
    		this.scriptLines = scriptLines;
    	}
    }
}

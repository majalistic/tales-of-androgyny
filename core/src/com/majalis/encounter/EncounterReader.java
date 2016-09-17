package com.majalis.encounter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
/*
 * Controls the logic for reading from the script file.
 */
public class EncounterReader {
	private final FileHandle file;   
	private final IntMap<String[]> scriptData;
	
    public EncounterReader(String path){
        file = Gdx.files.local(path);
        scriptData = getScriptData();
    }
   
	private IntMap<String[]> getScriptData(){
    	IntMap<String[]> data = new IntMap<String[]>();
        if(file.exists()){
        	data = convertToIntMap(new Json().fromJson(FullScript.class, file.readString()));
        }
        else {
        	saveToJson(new FullScript(new ScriptData[]{new ScriptData(0, new String[]{"Test", "This is"}), new ScriptData(1, new String[]{"Second", "A"})}));
        }
        return data;
    }
	
	private IntMap<String[]> convertToIntMap(FullScript data){
		IntMap<String[]> convertedData = new IntMap<String[]>();
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
  
    public String[] loadScript(int key){
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
    	public int key;
    	public String[] scriptLines;
    	// 0-arg constructor for JSON serialization: DO NOT USE
    	@SuppressWarnings("unused")
		private ScriptData(){}
    	protected ScriptData(int key, String[] scriptLines){
    		this.key = key;
    		this.scriptLines = scriptLines;
    	}
    }
}

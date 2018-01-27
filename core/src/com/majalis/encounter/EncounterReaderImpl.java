package com.majalis.encounter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;
import com.majalis.encounter.EncounterBuilder.SceneToken;

public class EncounterReaderImpl implements EncounterReader {
		private final FileHandle file;   
		private final ObjectMap<String, SceneToken[]> scriptData;
		
	    public EncounterReaderImpl(String path) {
	        file = Gdx.files.internal(path);
	        scriptData = getScriptData();
	    }
	   
		private ObjectMap<String, SceneToken[]> getScriptData() {
			ObjectMap<String, SceneToken[]> data = new ObjectMap<String, SceneToken[]>();
	        if(file.exists()) {
	        	data = convertToIntMap(new Json().fromJson(FullScript.class, file.readString()));
	        }
	        return data;
	    }
		
		private ObjectMap<String, SceneToken[]> convertToIntMap(FullScript data) {
			ObjectMap<String, SceneToken[]> convertedData = new ObjectMap<String, SceneToken[]>();
			for (ScriptData datum: data.script) {
				convertedData.put(datum.key, datum.scriptLines);
			}
			return convertedData;
		}
		@Override
	    public SceneToken[] loadScript(String key) { return scriptData.get(key); }
	    private static class FullScript {
	    	public ScriptData[] script;
	    	// 0-arg constructor for JSON serialization: DO NOT USE
			private FullScript() {}
	    }
	    
	    /* package for containing the data in a pretty format, an array of which deserializes to an IntMap */
	    private static class ScriptData {
	    	public String key;
	    	public SceneToken[] scriptLines;
	    	// 0-arg constructor for JSON serialization: DO NOT USE
			private ScriptData() {}
	    }
	}
	
	
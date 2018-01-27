package com.majalis.encounter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;
import com.majalis.encounter.EncounterBuilder.SceneToken;

public class EncounterReaderTest implements EncounterReader {
	private final String path;   
	private final ObjectMap<String, SceneToken[]> scriptData;
	
	public EncounterReaderTest(String path) {
		this.path = path;
        scriptData = getScriptData();
    }

	@Override
    public SceneToken[] loadScript(String key) { return scriptData.get(key); }
	
	private ObjectMap<String, SceneToken[]> getScriptData() {
		ObjectMap<String, SceneToken[]> data = new ObjectMap<String, SceneToken[]>();
        try {
			data = convertToIntMap(new Json().fromJson(FullScript.class, new String(Files.readAllBytes(Paths.get(path)))));
		} catch (IOException e) {
			e.printStackTrace();
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
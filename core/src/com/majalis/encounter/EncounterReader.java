package com.majalis.encounter;

import com.majalis.encounter.EncounterBuilder.SceneToken;

public interface EncounterReader {

	SceneToken[] loadScript(String key);

}

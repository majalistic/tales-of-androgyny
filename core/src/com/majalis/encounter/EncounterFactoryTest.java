package com.majalis.encounter;

import org.junit.Test;

import com.badlogic.gdx.utils.ObjectMap;
import com.majalis.asset.SafeAssetManager;
import com.majalis.save.SaveManager;
import com.majalis.talesofandrogyny.GameTest;

public class EncounterFactoryTest extends GameTest {

	@Test
	public void test() {
		ObjectMap<String, EncounterReader> encounterReaders = new ObjectMap<String, EncounterReader>();
		for (EncounterCode encounter : EncounterCode.values()) {
			String path = encounter.getScriptPath();
			EncounterReader reader = encounterReaders.get(path);
			if (reader == null) reader = new EncounterReaderTest("bin/test/resources/script/encounters.json");
			encounterReaders.put(path, reader);
		}
		
		EncounterFactory testFactory = new EncounterFactory(encounterReaders, new SafeAssetManager(),  new SaveManager(false, ".toa-data/save.json", ".toa-data/profile.json"));
		for (EncounterCode code : EncounterCode.values()) {
			testFactory.getEncounter(code, null);
		}
	}
}



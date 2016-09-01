package com.majalis.encounter;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.majalis.battle.BattleCode;
import com.majalis.save.LoadService;
import com.majalis.save.SaveEnum;
import com.majalis.save.SaveManager;
import com.majalis.save.SaveService;
/*
 * Retrieves encounters from internal files given an encounterId.  Need to create some kind of encounter builder helper class.
 */
public class EncounterFactory {
	
	private final EncounterReader reader;
	private final AssetManager assetManager;
	private final SaveService saveService;
	private final LoadService loadService;

	
	public EncounterFactory(EncounterReader reader, AssetManager assetManager, SaveManager saveManager){
		this.reader = reader;
		this.assetManager = assetManager;
		this.saveService = saveManager;
		this.loadService = saveManager;
	}
	
	public Encounter getEncounter(int encounterCode, BitmapFont font) {
		// temporarily stored in a static switch block until file retrieval for encounters is implemented
		Integer sceneCode = loadService.loadDataValue(SaveEnum.SCENE_CODE, Integer.class);
		BattleCode battle = loadService.loadDataValue(SaveEnum.BATTLE_CODE, BattleCode.class);
		int battleCode = -1;
		if (battle != null) battleCode = battle.battleCode;
		EncounterBuilder builder = new EncounterBuilder(reader, saveService, font, sceneCode, battleCode);
		switch (encounterCode){
			case 0: return builder.getClassChoiceEncounter(assetManager);
			case 1:	
			case 2: 
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
			case 8:
			case 9: return builder.getRandomEncounter();
			default: return builder.getDefaultEncounter();
		}
	}
	
}

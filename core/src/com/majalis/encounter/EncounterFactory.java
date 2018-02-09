package com.majalis.encounter;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.ObjectMap;
import com.majalis.character.PlayerCharacter;
import com.majalis.encounter.EncounterBuilder.Branch;
import com.majalis.save.LoadService;
import com.majalis.save.MutationResult;
import com.majalis.save.SaveEnum;
import com.majalis.save.SaveManager;
import com.majalis.save.SaveManager.GameMode;
import com.majalis.save.SaveService;
import com.majalis.scenes.ShopScene.Shop;
/*
 * Retrieves encounters from internal files given an encounterId.  Need to create some kind of encounter builder helper class.
 */
public class EncounterFactory {
	
	private final ObjectMap<String, EncounterReader> readers;
	private final AssetManager assetManager;
	private final SaveService saveService;
	private final LoadService loadService;

	public EncounterFactory(ObjectMap<String, EncounterReader> readers, AssetManager assetManager, SaveManager saveManager) {
		this.readers = readers;
		this.assetManager = assetManager;
		this.saveService = saveManager;
		this.loadService = saveManager;
	}
	
	@SuppressWarnings("unchecked")
	public Branch getEncounter(EncounterCode encounterCode, BitmapFont font) {
		IntArray sceneCode = loadService.loadDataValue(SaveEnum.SCENE_CODE, IntArray.class);
		return encounterCode.getEncounter(new EncounterBuilder(
			readers.get(encounterCode.getScriptPath()), assetManager, saveService, font, sceneCode, (ObjectMap<String, Shop>)loadService.loadDataValue(SaveEnum.SHOP, Shop.class), (PlayerCharacter) loadService.loadDataValue(SaveEnum.PLAYER, PlayerCharacter.class),
			 (Array<MutationResult>) loadService.loadDataValue(SaveEnum.RESULT, Array.class), (Array<MutationResult>) loadService.loadDataValue(SaveEnum.BATTLE_RESULT, Array.class)), (GameMode) loadService.loadDataValue(SaveEnum.MODE, GameMode.class)); 
	}
}

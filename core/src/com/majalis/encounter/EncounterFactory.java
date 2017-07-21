package com.majalis.encounter;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.ObjectMap;
import com.majalis.character.PlayerCharacter;
import com.majalis.save.LoadService;
import com.majalis.save.SaveEnum;
import com.majalis.save.SaveManager;
import com.majalis.save.SaveManager.GameContext;
import com.majalis.save.SaveManager.GameMode;
import com.majalis.save.SaveService;
import com.majalis.scenes.ShopScene.Shop;
/*
 * Retrieves encounters from internal files given an encounterId.  Need to create some kind of encounter builder helper class.
 */
public class EncounterFactory {
	
	private final EncounterReader reader;
	private final AssetManager assetManager;
	private final SaveService saveService;
	private final LoadService loadService;

	public EncounterFactory(EncounterReader reader, AssetManager assetManager, SaveManager saveManager) {
		this.reader = reader;
		this.assetManager = assetManager;
		this.saveService = saveManager;
		this.loadService = saveManager;
	}
	
	@SuppressWarnings("unchecked")
	public Encounter getEncounter(EncounterCode encounterCode, BitmapFont font) {
		IntArray sceneCode = loadService.loadDataValue(SaveEnum.SCENE_CODE, IntArray.class);
		GameContext context = loadService.loadDataValue(SaveEnum.RETURN_CONTEXT, GameContext.class);
		return new EncounterBuilder(
			reader, assetManager, saveService, font, sceneCode, (ObjectMap<String, Shop>)loadService.loadDataValue(SaveEnum.SHOP, Shop.class), (PlayerCharacter) loadService.loadDataValue(SaveEnum.PLAYER, PlayerCharacter.class), context,
			(GameMode) loadService.loadDataValue(SaveEnum.MODE, GameMode.class)).getEncounter(encounterCode); 
	}
}

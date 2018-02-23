package com.majalis.screens;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.utils.Array;
import com.majalis.asset.AssetEnum;
import com.majalis.encounter.Encounter;
import com.majalis.encounter.EncounterBuilder.Branch;
import com.majalis.save.SaveEnum;
import com.majalis.save.SaveManager;
import com.majalis.save.SaveService;

public class LevelUpScreen extends AbstractScreen {
	private static final Array<AssetDescriptor<?>> resourceRequirements = new Array<AssetDescriptor<?>>();
	private static Array<AssetDescriptor<?>> requirementsToDispose = new Array<AssetDescriptor<?>>();
	static {
		resourceRequirements.add(AssetEnum.DEFAULT_BACKGROUND.getTexture());
		resourceRequirements.add(AssetEnum.LEVEL_UP_SKIN.getSkin());
	}
	
	private final SaveService saveService;
	private final Encounter encounter;
	protected LevelUpScreen(ScreenFactory screenFactory, ScreenElements elements, SaveService saveService, Encounter encounter) {
		super(screenFactory, elements, null);
		this.saveService = saveService;
		this.encounter = encounter;
	}

	@Override
	public void buildStage() {
		this.addActor(encounter.getSceneGroup());
	}
	
	@Override
	public void render(float delta) {
		super.render(delta);
		encounter.gameLoop();
		if (encounter.encounterOver || encounter.gameExit) {
			saveService.saveDataValue(SaveEnum.CONTEXT, SaveManager.GameContext.WORLD_MAP);
			saveService.saveDataValue(SaveEnum.SCENE_CODE, 0);
			showScreen(ScreenEnum.CHARACTER);
		}
	}

	@Override
	public void dispose() {
		for (AssetDescriptor<?> path : requirementsToDispose) {
			if (path.fileName.equals(AssetEnum.BUTTON_SOUND.getSound().fileName) || path.type == Music.class)
				continue;
			assetManager.unload(path.fileName);
		}
		requirementsToDispose = new Array<AssetDescriptor<?>>();
	}
	
	public static Array<AssetDescriptor<?>> getRequirements(Branch encounter) {
		Array<AssetDescriptor<?>> requirements = new Array<AssetDescriptor<?>>(resourceRequirements);
		requirements.addAll(encounter.getRequirements());
		requirementsToDispose = requirements;
		return requirements;
	}
}

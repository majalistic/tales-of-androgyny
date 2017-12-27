package com.majalis.screens;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.majalis.asset.AssetEnum;
import com.majalis.encounter.Encounter;
import com.majalis.encounter.EncounterCode;
import com.majalis.save.SaveEnum;
import com.majalis.save.SaveManager;
import com.majalis.save.SaveService;

public class LevelUpScreen extends AbstractScreen {

	public static final Array<AssetDescriptor<?>> resourceRequirements = new Array<AssetDescriptor<?>>();
	static {
		resourceRequirements.add(AssetEnum.SKILL_SELECTION_BACKGROUND.getTexture());
		resourceRequirements.add(AssetEnum.NORMAL_BOX.getTexture());
		resourceRequirements.addAll(EncounterScreen.getRequirements(EncounterCode.LEVEL_UP));
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
		for (Actor actor: encounter.getActors()) {
			this.addActor(actor);
		}  
	}
	
	@Override
	public void render(float delta) {
		super.render(delta);
		encounter.gameLoop();
		if (encounter.encounterOver) {
			saveService.saveDataValue(SaveEnum.CONTEXT, SaveManager.GameContext.WORLD_MAP);
			saveService.saveDataValue(SaveEnum.SCENE_CODE, 0);
			showScreen(ScreenEnum.CHARACTER);
		}
		if (encounter.gameExit) {
			showScreen(ScreenEnum.MAIN_MENU);
		}
	}

	@Override
	public void dispose() {
		for(AssetDescriptor<?> path: resourceRequirements) {
			if (path.fileName.equals(AssetEnum.BUTTON_SOUND.getSound().fileName) || path.type == Music.class) continue;
			assetManager.unload(path.fileName);
		}
	}
}

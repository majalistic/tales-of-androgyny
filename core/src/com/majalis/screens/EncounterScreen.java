package com.majalis.screens;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ObjectMap;
import com.majalis.asset.AssetEnum;
import com.majalis.encounter.Encounter;
import com.majalis.save.SaveEnum;
import com.majalis.save.SaveManager;
import com.majalis.save.SaveService;
/*
 *Screen for displaying Encounters.  UI that Handles player input while in an encounter.
 */
public class EncounterScreen extends AbstractScreen {

	private final AssetManager assetManager;
	private final SaveService saveService;
	private final Encounter encounter;
	// these are required for all encounters, possibly - requirements for an individual encounter must be parsed by the EncounterFactory
	public static final ObjectMap<String, Class<?>> resourceRequirements = new ObjectMap<String, Class<?>>();
	static {
		resourceRequirements.put("uiskin.json", Skin.class);
		resourceRequirements.put("sound.wav", Sound.class);
		resourceRequirements.put("DefaultBackground.jpg", Texture.class);
		resourceRequirements.put(AssetEnum.VIGNETTE.getPath(), Texture.class);
		resourceRequirements.put("GameTypeSelect.jpg", Texture.class);
		resourceRequirements.put("ClassSelect.jpg", Texture.class);
		resourceRequirements.put("DryadApple.jpg", Texture.class);
		resourceRequirements.put("StickEncounter.jpg", Texture.class);
		resourceRequirements.put(AssetEnum.WEREBITCH.getPath(), Texture.class);
		resourceRequirements.put(AssetEnum.SLIME.getPath(), Texture.class);
		resourceRequirements.put(AssetEnum.STRENGTH.getPath(), Texture.class);
		resourceRequirements.put(AssetEnum.ENDURANCE.getPath(), Texture.class);
		resourceRequirements.put(AssetEnum.AGILITY.getPath(), Texture.class);
		resourceRequirements.put(AssetEnum.PERCEPTION.getPath(), Texture.class);
		resourceRequirements.put(AssetEnum.MAGIC.getPath(), Texture.class);
		resourceRequirements.put(AssetEnum.CHARISMA.getPath(), Texture.class);
		
	}
	protected EncounterScreen(ScreenFactory screenFactory, ScreenElements elements, AssetManager assetManager, SaveService saveService, Encounter encounter) {
		super(screenFactory, elements);
		this.assetManager = assetManager;
		this.saveService = saveService;
		this.encounter = encounter;
	}

	@Override
	public void buildStage() {
		for (Actor actor: encounter.getActors()){
			this.addActor(actor);
		}        	
	}
	
	@Override
	public void render(float delta) {
		super.render(delta);
		encounter.gameLoop();
		if (encounter.battle){
			saveService.saveDataValue(SaveEnum.CONTEXT, SaveManager.GameContext.BATTLE);
			showScreen(ScreenEnum.BATTLE);
		}
		if (encounter.encounterOver){
			saveService.saveDataValue(SaveEnum.CONTEXT, SaveManager.GameContext.WORLD_MAP);
			saveService.saveDataValue(SaveEnum.SCENE_CODE, 0);
			showScreen(ScreenEnum.LOAD_GAME);
		}
		if (encounter.gameExit){
			showScreen(ScreenEnum.MAIN_MENU);
		}
		else if (encounter.gameOver){
			showScreen(ScreenEnum.GAME_OVER);
		}
		else {
			draw();
		}
	}
	
	public void draw(){
		batch.begin();
		OrthographicCamera camera = (OrthographicCamera) getCamera();
        batch.setTransformMatrix(camera.view);
		batch.setProjectionMatrix(camera.combined);
		camera.update();
		batch.end();
	}

	@Override
	public void dispose() {
		for(String path: resourceRequirements.keys()){
			if (path.equals("sound.wav")) continue;
			assetManager.unload(path);
		}
	}
}

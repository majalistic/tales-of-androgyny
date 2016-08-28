package com.majalis.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ObjectMap;
import com.majalis.encounter.Encounter;
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
			saveService.saveDataValue("Context", SaveManager.GameContext.BATTLE);
			showScreen(ScreenEnum.BATTLE);
		}
		if (encounter.encounterOver){
			saveService.saveDataValue("Context", SaveManager.GameContext.WORLD_MAP);
			saveService.saveDataValue("SceneCode", 0);
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
		if (encounter.displayHUD){
			font.draw(batch, "FPS: " + MathUtils.ceil(1/Gdx.graphics.getDeltaTime()), camera.position.x-200+(400), camera.position.y+220);
		}
		super.draw();
		batch.end();
	}

	@Override
	public void dispose() {
		for(String path: resourceRequirements.keys()){
			assetManager.unload(path);
		}
	}
}

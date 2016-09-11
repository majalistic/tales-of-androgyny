package com.majalis.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ObjectMap;
import com.majalis.save.LoadService;
import com.majalis.save.SaveEnum;
import com.majalis.world.GameWorld;
/*
 * The screen that displays the world map.  UI that Handles player input while on the world map - will delegate to other screens depending on the gameWorld state.
 */
public class GameScreen extends AbstractScreen {

	private final AssetManager assetManager;
	private final GameWorld world;
	public static final ObjectMap<String, Class<?>> resourceRequirements = new ObjectMap<String, Class<?>>();
	static {
		resourceRequirements.put("uiskin.json", Skin.class);
		resourceRequirements.put("sound.wav", Sound.class);
	}
	public GameScreen(ScreenFactory factory, ScreenElements elements, AssetManager assetManager, LoadService loadService, GameWorld world) {
		super(factory, elements);
		this.assetManager = assetManager;
		Vector3 initialTranslation = loadService.loadDataValue(SaveEnum.CAMERA_POS, Vector3.class);
		initialTranslation = new Vector3(initialTranslation);
		OrthographicCamera camera = (OrthographicCamera) getCamera();
		initialTranslation.x -= camera.position.x;
		initialTranslation.y -= camera.position.y;
		camera.translate(initialTranslation);
		camera.update();
		this.world = world;
	}

	@Override
	public void buildStage() {
		for (Actor actor: world.getActors()){
			this.addActor(actor);
		}   
	}
	
	@Override
	public void render(float delta) {
		super.render(delta);
		Vector3 translationVector = new Vector3(0,0,0);

		int speed = 5;
		
		if (Gdx.input.isKeyPressed(Keys.LEFT)){
			translationVector.x -= speed;
		}
		else if (Gdx.input.isKeyPressed(Keys.RIGHT)){
			translationVector.x += speed;
		}
		if (Gdx.input.isKeyPressed(Keys.UP)){
			translationVector.y += speed;
		}
		else if (Gdx.input.isKeyPressed(Keys.DOWN)){
			translationVector.y -= speed;
		}
		
		getCamera().translate(translationVector);
		
		world.gameLoop();
		if (world.gameExit){
			showScreen(ScreenEnum.MAIN_MENU);
		}
		else if (world.encounterSelected){
			showScreen(ScreenEnum.ENCOUNTER);
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
		super.draw();
		batch.end();
	}
	
	@Override
	public void show() {
		super.show();
	}
	
	@Override
	public void dispose() {
		for(String path: resourceRequirements.keys()){
			assetManager.unload(path);
		}
	}
	
}
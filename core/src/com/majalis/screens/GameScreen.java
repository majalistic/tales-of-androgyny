package com.majalis.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.majalis.asset.AssetEnum;
import com.majalis.character.PlayerCharacter;
import com.majalis.save.LoadService;
import com.majalis.save.SaveEnum;
import com.majalis.world.GameWorld;
/*
 * The screen that displays the world map.  UI that Handles player input while on the world map - will delegate to other screens depending on the gameWorld state.
 */
public class GameScreen extends AbstractScreen {

	private final AssetManager assetManager;
	private final GameWorld world;
	private final Texture food;
	private final Array<Texture> grasses;
	private final int[][] grassMap;
	private final Texture cloud;
	private final Texture UI;
	private final int foodAmount;
	private final PlayerCharacter character;
	public static final ObjectMap<String, Class<?>> resourceRequirements = new ObjectMap<String, Class<?>>();
	static {
		resourceRequirements.put("uiskin.json", Skin.class);
		resourceRequirements.put("node_sound.wav", Sound.class);
		resourceRequirements.put("TinySprite0.png", Texture.class);
		resourceRequirements.put(AssetEnum.MOUNTAIN_ACTIVE.getPath(), Texture.class);
		resourceRequirements.put(AssetEnum.MOUNTAIN_INACTIVE.getPath(), Texture.class);
		resourceRequirements.put(AssetEnum.FOREST_ACTIVE.getPath(), Texture.class);
		resourceRequirements.put(AssetEnum.FOREST_INACTIVE.getPath(), Texture.class);
		resourceRequirements.put(AssetEnum.APPLE.getPath(), Texture.class);
		resourceRequirements.put(AssetEnum.MEAT.getPath(), Texture.class);
		resourceRequirements.put(AssetEnum.GRASS0.getPath(), Texture.class);
		resourceRequirements.put(AssetEnum.GRASS1.getPath(), Texture.class);
		resourceRequirements.put(AssetEnum.GRASS2.getPath(), Texture.class);
		resourceRequirements.put(AssetEnum.CLOUD.getPath(), Texture.class);
		resourceRequirements.put(AssetEnum.ROAD.getPath(), Texture.class);
		resourceRequirements.put(AssetEnum.WORLD_MAP_UI.getPath(), Texture.class);
		resourceRequirements.put(AssetEnum.WORLD_MAP_HOVER.getPath(), Texture.class);
	}
	public GameScreen(ScreenFactory factory, ScreenElements elements, AssetManager assetManager, LoadService loadService, GameWorld world) {
		super(factory, elements);
		this.assetManager = assetManager;
		int arb = loadService.loadDataValue(SaveEnum.NODE_CODE, Integer.class);
		food = arb % 2 == 0 ? assetManager.get(AssetEnum.APPLE.getPath(), Texture.class) : assetManager.get(AssetEnum.MEAT.getPath(), Texture.class);
		foodAmount = loadService.loadDataValue(SaveEnum.FOOD, Integer.class);
		grasses = new Array<Texture>(true, new Texture[]{assetManager.get(AssetEnum.GRASS0.getPath(), Texture.class), assetManager.get(AssetEnum.GRASS1.getPath(), Texture.class), assetManager.get(AssetEnum.GRASS2.getPath(), Texture.class)}, 0, 3);
		cloud = assetManager.get(AssetEnum.CLOUD.getPath(), Texture.class);
		UI = assetManager.get(AssetEnum.WORLD_MAP_UI.getPath(), Texture.class);
		Vector3 initialTranslation = loadService.loadDataValue(SaveEnum.CAMERA_POS, Vector3.class);		
		initialTranslation = new Vector3(initialTranslation);
		OrthographicCamera camera = (OrthographicCamera) getCamera();
		initialTranslation.x -= camera.position.x;
		initialTranslation.y -= camera.position.y;
		camera.translate(initialTranslation);
		camera.update();
		this.world = world;
		red = .137f;
		green = .007f;
		blue = .047f;
		callClear = false;
		grassMap = new int[102][102];
		for (int ii = 101; ii >= 0; ii--){
			for (int jj = 100; jj >= 0; jj--){
				grassMap[ii][jj] = (int)(Math.random()*100) % 3;
			}	
		}
		this.character = loadService.loadDataValue(SaveEnum.PLAYER, PlayerCharacter.class);
	}

	@Override
	public void buildStage() {
		for (Actor actor: world.getActors()){
			this.addActor(actor);
		}   
	}
	
	@Override
	public void render(float delta) {
		super.clear();
		
		Vector3 translationVector = new Vector3(0,0,0);

		int speed = 5;
		
		if (Gdx.input.isKeyPressed(Keys.LEFT) && getCamera().position.x > 100){
			translationVector.x -= speed;
		}
		else if (Gdx.input.isKeyPressed(Keys.RIGHT) && getCamera().position.x < 2000){
			translationVector.x += speed;
		}
		if (Gdx.input.isKeyPressed(Keys.DOWN) && getCamera().position.y > 200){
			translationVector.y -= speed;
		}
		else if (Gdx.input.isKeyPressed(Keys.UP) && getCamera().position.y < 2000){
			translationVector.y += speed;
		}
		
		getCamera().translate(translationVector);
			
		if (world.gameExit){
			showScreen(ScreenEnum.MAIN_MENU);
		}
		else if (world.encounterSelected){
			showScreen(ScreenEnum.ENCOUNTER);
		}
		else {			
			draw();
			super.render(delta);
			drawClouds();
			world.gameLoop(batch, getCamera());
		}
	}
	
	public void draw(){
		batch.begin();
		OrthographicCamera camera = (OrthographicCamera) getCamera();
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		
		// draw the base grass texture
		for (int ii = 101; ii >= 0; ii-=2){
			for (int jj = 100; jj >= 0; jj--){
				batch.draw(grasses.get(grassMap[ii][jj]), ii*56, jj*55);
				batch.draw(grasses.get(grassMap[ii-1][jj]), ((ii-1)*56), (jj*55)+30);
			}	
		}
		batch.end();
	}
	
	public void drawClouds(){
		batch.begin();
		OrthographicCamera camera = (OrthographicCamera) getCamera();
		Matrix4 temp = new Matrix4(batch.getTransformMatrix());
		batch.setTransformMatrix(camera.view);
		batch.setColor(1.0f, 1.0f, 1.0f, .3f);
		batch.draw(cloud, 300, 800, 800, 800);
		batch.draw(cloud, 2200, 600, 800, 800);
		batch.draw(cloud, 1400, 1300, 800, 800);
		batch.setColor(1.0f, 1.0f, 1.0f, 1);
		batch.setTransformMatrix(temp);
		batch.draw(food, camera.position.x+3, camera.position.y+3, 50, 50);
		batch.draw(UI, camera.position.x+3, camera.position.y+3);
		font.draw(batch, String.valueOf(character.getCurrentHealth()), camera.position.x+295, camera.position.y+125);
		font.draw(batch, "X " + foodAmount, camera.position.x+23, camera.position.y+17);
		batch.end();
	}
	
	@Override
	public void show() {
		super.show();
	}
	
	@Override
	public void dispose() {
		for(String path: resourceRequirements.keys()){
			if (path.equals("node_sound.wav")) continue;
			assetManager.unload(path);
		}
	}
	
}
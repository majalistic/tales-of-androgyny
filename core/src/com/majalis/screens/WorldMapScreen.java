package com.majalis.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.majalis.asset.AssetEnum;
import com.majalis.character.PlayerCharacter;
import com.majalis.save.LoadService;
import com.majalis.save.SaveEnum;
import com.majalis.save.SaveService;
import com.majalis.world.Cloud;
import com.majalis.world.GameWorld;
/*
 * The screen that displays the world map.  UI that Handles player input while on the world map - will delegate to other screens depending on the gameWorld state.
 */
public class WorldMapScreen extends AbstractScreen {

	private final AssetManager assetManager;
	private final SaveService saveService;
	private final GameWorld world;
	private final Texture food;
	private final Array<Texture> grasses;
	private final int[][] grassMap;
	private final Texture cloud;
	private final Texture UI;
	private final PlayerCharacter character;
	private final Group group;
	private final Group cloudGroup;
	private final Music music;
	private final FrameBuffer frameBuffer;
	private boolean backgroundRendered = false;
	private TextureRegion scenery;
	private Image ui;
	private Image foodIcon;
	
	private TextButton characterButton;
	private TextButton camp;
	
	
	public static final ObjectMap<String, Class<?>> resourceRequirements = new ObjectMap<String, Class<?>>();
	static {
		resourceRequirements.put(AssetEnum.UI_SKIN.getPath(), Skin.class);
		resourceRequirements.put(AssetEnum.CLICK_SOUND.getPath(), Sound.class);
		resourceRequirements.put(AssetEnum.CHARACTER_SPRITE.getPath(), Texture.class);
		resourceRequirements.put(AssetEnum.MOUNTAIN_ACTIVE.getPath(), Texture.class);
		resourceRequirements.put(AssetEnum.FOREST_ACTIVE.getPath(), Texture.class);
		resourceRequirements.put(AssetEnum.FOREST_INACTIVE.getPath(), Texture.class);
		resourceRequirements.put(AssetEnum.CASTLE.getPath(), Texture.class);
		resourceRequirements.put(AssetEnum.TOWN.getPath(), Texture.class);
		resourceRequirements.put(AssetEnum.APPLE.getPath(), Texture.class);
		resourceRequirements.put(AssetEnum.MEAT.getPath(), Texture.class);
		resourceRequirements.put(AssetEnum.GRASS0.getPath(), Texture.class);
		resourceRequirements.put(AssetEnum.GRASS1.getPath(), Texture.class);
		resourceRequirements.put(AssetEnum.GRASS2.getPath(), Texture.class);
		resourceRequirements.put(AssetEnum.CLOUD.getPath(), Texture.class);
		resourceRequirements.put(AssetEnum.ROAD.getPath(), Texture.class);
		resourceRequirements.put(AssetEnum.WORLD_MAP_UI.getPath(), Texture.class);
		resourceRequirements.put(AssetEnum.WORLD_MAP_HOVER.getPath(), Texture.class);
		resourceRequirements.put(AssetEnum.ARROW.getPath(), Texture.class);
		resourceRequirements.put(AssetEnum.CHARACTER_SCREEN.getPath(), Texture.class);
		resourceRequirements.put(AssetEnum.WORLD_MAP_MUSIC.getPath(), Music.class);
	}
	public WorldMapScreen(ScreenFactory factory, ScreenElements elements, AssetManager assetManager, SaveService saveService, LoadService loadService, GameWorld world) {
		super(factory, elements);
		this.assetManager = assetManager;
		this.saveService = saveService;
		
		group = new Group();
		this.addActor(group);
		
		int arb = loadService.loadDataValue(SaveEnum.NODE_CODE, Integer.class);
		food = arb % 2 == 0 ? assetManager.get(AssetEnum.APPLE.getPath(), Texture.class) : assetManager.get(AssetEnum.MEAT.getPath(), Texture.class);
		grasses = new Array<Texture>(true, new Texture[]{assetManager.get(AssetEnum.GRASS0.getPath(), Texture.class), assetManager.get(AssetEnum.GRASS1.getPath(), Texture.class), assetManager.get(AssetEnum.GRASS2.getPath(), Texture.class)}, 0, 3);
		cloud = assetManager.get(AssetEnum.CLOUD.getPath(), Texture.class);
		UI = assetManager.get(AssetEnum.WORLD_MAP_UI.getPath(), Texture.class);
		music = assetManager.get(AssetEnum.WORLD_MAP_MUSIC.getPath(), Music.class);
		Vector3 initialTranslation = loadService.loadDataValue(SaveEnum.CAMERA_POS, Vector3.class);		
		initialTranslation = new Vector3(initialTranslation);
		OrthographicCamera camera = (OrthographicCamera) getCamera();
		initialTranslation.x -= camera.position.x;
		initialTranslation.y -= camera.position.y;
		camera.translate(initialTranslation);
		camera.update();
		this.world = world;
		grassMap = new int[102][102];
		for (int ii = 101; ii >= 0; ii--){
			for (int jj = 100; jj >= 0; jj--){
				grassMap[ii][jj] = (int)(Math.random()*100) % 3;
			}	
		}
		this.character = loadService.loadDataValue(SaveEnum.PLAYER, PlayerCharacter.class);
		this.cloudGroup = new Group();
		cloudGroup.addActor(new Cloud(cloud, 300, 800, getCamera()));
		cloudGroup.addActor(new Cloud(cloud, 2200, 600, getCamera()));
		cloudGroup.addActor(new Cloud(cloud, 3000, 3000, getCamera()));
		cloudGroup.addActor(new Cloud(cloud, 4000, 2000, getCamera()));
		cloudGroup.addActor(new Cloud(cloud, 0, 3600, getCamera()));
		cloudGroup.addActor(new Cloud(cloud, 300, 2600, getCamera()));
		
		frameBuffer = new FrameBuffer(Pixmap.Format.RGB888, 2000, 1000, false);
	}

	@Override
	public void buildStage() {
		for (Actor actor: world.getActors()){
			group.addActor(actor);
		}   
		group.addActor(cloudGroup);
		final Sound buttonSound = assetManager.get("node_sound.wav", Sound.class); 
		Skin skin = assetManager.get("uiskin.json", Skin.class);
		int storedLevels = character.getStoredLevels();
		characterButton = new TextButton(storedLevels > 0 ? "Level Up!" : "Character", skin);
		
		if (storedLevels > 0){
			TextButtonStyle style = new TextButtonStyle(characterButton.getStyle());
			style.fontColor = Color.OLIVE;
			characterButton.setStyle(style);
		}
		
		characterButton.setWidth(120); 
		characterButton.setHeight(40);
		characterButton.addListener(
			new ClickListener(){
				@Override
		        public void clicked(InputEvent event, float x, float y) {
					buttonSound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
					showScreen(ScreenEnum.CHARACTER);		   
		        }
			}
		);
		group.addActor(characterButton);
		
		camp = new TextButton("Camp", skin);
		
		if (character.getFood() < 4){
			TextButtonStyle style = new TextButtonStyle(camp.getStyle());
			style.fontColor = Color.RED;
			camp.setStyle(style);
			camp.setTouchable(Touchable.disabled);
		}
		
		camp.setWidth(120); 
		camp.setHeight(40);
		camp.addListener(
			new ClickListener(){
				@Override
		        public void clicked(InputEvent event, float x, float y) {
					buttonSound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
					saveService.saveDataValue(SaveEnum.FOOD, -4);	   
					saveService.saveDataValue(SaveEnum.HEALTH, 10);	
					if (character.getFood() < 4){
						TextButtonStyle style = new TextButtonStyle(camp.getStyle());
						style.fontColor = Color.RED;
						camp.setStyle(style);
						camp.setTouchable(Touchable.disabled);
					}
		        }
			}
		);
		group.addActor(camp);
		ui = new Image(UI);
		group.addActor(ui);
		foodIcon = new Image(food);
		foodIcon.setWidth(50);
		foodIcon.setHeight(50);
		group.addActor(foodIcon);

		
		music.setVolume(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("musicVolume", 1) * .6f);
		music.setLooping(true);
		music.play();
	}
	
	@Override
	public void render(float delta) {
		if (!backgroundRendered){
			generateBackground();
		}
		translateCamera();
		
		characterButton.addAction(Actions.moveTo(getCamera().position.x-450, getCamera().position.y-200));
		camp.addAction(Actions.moveTo(getCamera().position.x-320, getCamera().position.y-200));
		foodIcon.addAction(Actions.moveTo(getCamera().position.x-630, getCamera().position.y-350));
		ui.addAction(Actions.moveTo(getCamera().position.x-630, getCamera().position.y-350));		
		
		
		if (Gdx.input.isKeyJustPressed(Keys.ENTER)){
			showScreen(ScreenEnum.CHARACTER);
		}			
		else if (world.gameExit){
			music.stop();
			showScreen(ScreenEnum.MAIN_MENU);
		}
		else if (world.encounterSelected){
			music.stop();
			showScreen(ScreenEnum.LOAD_GAME);
		}
		else {			
			super.render(delta);
			drawText(delta);
			world.gameLoop(batch, getCamera().position);
		}
	}
	
	public void drawText(float delta){
		batch.begin();
		OrthographicCamera camera = (OrthographicCamera) getCamera();
		batch.setColor(1.0f, 1.0f, 1.0f, 1);
		font.draw(batch, String.valueOf(character.getCurrentHealth()), camera.position.x+295, camera.position.y+135);
		font.draw(batch, "X " + character.getFood(), camera.position.x+23, camera.position.y+17);
		batch.end();
	}
	
	private void translateCamera(){
		Vector3 translationVector = new Vector3(0,0,0);
		int speed = 5;
		if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)) speed = 10;
		
		if (Gdx.input.isKeyPressed(Keys.LEFT) && getCamera().position.x > 100){
			translationVector.x -= speed;
		}
		else if (Gdx.input.isKeyPressed(Keys.RIGHT) && getCamera().position.x < 4000){
			translationVector.x += speed;
		}
		if (Gdx.input.isKeyPressed(Keys.DOWN) && getCamera().position.y > 200){
			translationVector.y -= speed;
		}
		else if (Gdx.input.isKeyPressed(Keys.UP) && getCamera().position.y < 4600){
			translationVector.y += speed;
		}
		getCamera().translate(translationVector);
	}
	
	private void generateBackground(){
		backgroundRendered = true;
		frameBuffer.begin();
		SpriteBatch frameBufferBatch = new SpriteBatch();
		frameBufferBatch.begin();
		// draw the base grass texture
		for (int ii = 101; ii >= 0; ii-=2){
			for (int jj = 100; jj >= 0; jj--){
				frameBufferBatch.draw(grasses.get(grassMap[ii][jj]), ii*56, jj*55);
				frameBufferBatch.draw(grasses.get(grassMap[ii-1][jj]), ((ii-1)*56), (jj*55)+30);
			}	
		}
		frameBufferBatch.end();
		frameBuffer.end();		
		frameBufferBatch.dispose();
		scenery = new TextureRegion(frameBuffer.getColorBufferTexture());
		scenery.setRegion(50, 50, scenery.getRegionWidth() - 50, scenery.getRegionHeight() - 50); 
		scenery.flip(false, true);
		for (int ii = 0; ii < 3; ii++){
			for (int jj = 0; jj < 6; jj++){
				Image background = new Image(scenery);
				background.addAction(Actions.moveTo(-700+ii*scenery.getRegionWidth(), -300+jj*scenery.getRegionHeight()));
				group.addActorAt(0, background);
			}
		}
	}
	
	@Override
	public void dispose() {
		for(String path: resourceRequirements.keys()){
			if (path.equals("node_sound.wav")) continue;
			assetManager.unload(path);
		}
		frameBuffer.dispose();
	}
	
}
package com.majalis.screens;

import com.badlogic.gdx.Gdx;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.majalis.asset.AssetEnum;
import com.majalis.character.PlayerCharacter;
import com.majalis.save.LoadService;
import com.majalis.save.SaveEnum;
import com.majalis.save.SaveService;
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
	private final Texture cloud;
	private final Texture characterUITexture;
	private final PlayerCharacter character;
	private final Stage worldStage;
	private final PerspectiveCamera camera;
	private final Stage cloudStage;
	private final PerspectiveCamera cloudCamera;
	private final Group group;
	private final Group cloudGroup;
	private final Music music;
	private final FrameBuffer frameBuffer;
	private final InputMultiplexer multi;
	private boolean backgroundRendered = false;
	private TextureRegion scenery;
	private Image characterUI;
	private Image foodIcon;
	
	private TextButton characterButton;
	private TextButton camp;
	
	public static final ObjectMap<String, Class<?>> resourceRequirements = new ObjectMap<String, Class<?>>();
	static {
		resourceRequirements.put(AssetEnum.UI_SKIN.getPath(), Skin.class);
		resourceRequirements.put(AssetEnum.CLICK_SOUND.getPath(), Sound.class);
		resourceRequirements.put(AssetEnum.CHARACTER_ANIMATION.getPath(), Texture.class);
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
		
		camera = new PerspectiveCamera(70, 0, 1000);
        FitViewport viewport =  new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), camera);
		worldStage = new Stage3D(viewport, batch);
		
		camera.position.set(0, 0, 500);
		camera.near = 1f;
		camera.far = 10000;
		camera.lookAt(0,0,0);
		camera.translate(1280/2, 720/2, 200);
		
		cloudCamera = new PerspectiveCamera(70, 0, 1000);
        FitViewport viewport2 =  new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), cloudCamera);
		cloudStage = new Stage3D(viewport2, batch);
		
		cloudCamera.position.set(0, 0, 500);
		cloudCamera.near = 1f;
		cloudCamera.far = 10000;
		cloudCamera.lookAt(0,0,0);
		cloudCamera.translate(1280/2, 720/2, -200);
		
		// create base group for world stage
		group = new Group();
		worldStage.addActor(group);
		
		// load assets
		int arb = loadService.loadDataValue(SaveEnum.NODE_CODE, Integer.class);
		food = arb % 2 == 0 ? assetManager.get(AssetEnum.APPLE.getPath(), Texture.class) : assetManager.get(AssetEnum.MEAT.getPath(), Texture.class);
		grasses = new Array<Texture>(true, new Texture[]{assetManager.get(AssetEnum.GRASS0.getPath(), Texture.class), assetManager.get(AssetEnum.GRASS1.getPath(), Texture.class), assetManager.get(AssetEnum.GRASS2.getPath(), Texture.class), assetManager.get(AssetEnum.FOREST_INACTIVE.getPath(), Texture.class)}, 0, 4);
		cloud = assetManager.get(AssetEnum.CLOUD.getPath(), Texture.class);
		characterUITexture = assetManager.get(AssetEnum.WORLD_MAP_UI.getPath(), Texture.class);
		music = assetManager.get(AssetEnum.WORLD_MAP_MUSIC.getPath(), Music.class);
		
		// move camera to saved position
		Vector3 initialTranslation = loadService.loadDataValue(SaveEnum.CAMERA_POS, Vector3.class);		
		initialTranslation = new Vector3(initialTranslation);
		initialTranslation.x -= camera.position.x;
		initialTranslation.y -= camera.position.y;
		camera.translate(initialTranslation);
		camera.update();
		
		this.world = world;
		
		this.character = loadService.loadDataValue(SaveEnum.PLAYER, PlayerCharacter.class);
		this.cloudGroup = new Group();
		for (int ii = 0; ii < 50; ii++){
			Actor actor = new Image(cloud);
			actor.setPosition((float)Math.random()*5000-1000, (float)Math.random()*5000-1000);
			actor.addAction(Actions.alpha(.3f));
			float speed = 10f;
			int leftWrap = -1000;
			int rightWrap = 5000;
			// move from starting position to leftWrap, then warp to rightWrap, then repeat those two actions forever
			actor.addAction(sequence(moveTo(leftWrap, actor.getY(), (actor.getX() - leftWrap) / speed), moveTo(rightWrap, actor.getY()), repeat(RepeatAction.FOREVER, sequence(moveTo(leftWrap, actor.getY(), rightWrap - leftWrap / speed), moveTo(rightWrap, actor.getY())))));
			cloudGroup.addActor(actor);
		}
		
		cloudStage.addActor(cloudGroup);
		
		frameBuffer = new FrameBuffer(Pixmap.Format.RGB888, 2016, 1008, false);
		
		clearScreen = false;
		
		multi = new InputMultiplexer();
		multi.addProcessor(this);
		multi.addProcessor(worldStage);
	}

	@Override
	public void buildStage() {
		for (Actor actor: world.getActors()){
			group.addActor(actor);
		}   
		final Sound buttonSound = assetManager.get(AssetEnum.CLICK_SOUND.getPath(), Sound.class); 
		Skin skin = assetManager.get(AssetEnum.UI_SKIN.getPath(), Skin.class);
		int storedLevels = character.getStoredLevels();
		
		characterUI = new Image(characterUITexture);
		this.addActor(characterUI);
		characterUI.setScale(1.1f);
		
		characterButton = new TextButton(storedLevels > 0 ? "Level Up!" : "Character", skin);
		
		if (storedLevels > 0){
			TextButtonStyle style = new TextButtonStyle(characterButton.getStyle());
			style.fontColor = Color.OLIVE;
			characterButton.setStyle(style);
		}
		
		Table table = new Table();
		table.setPosition(377, 65);
		this.addActor(table);
		
		table.add(characterButton).size(185, 40);
		
		characterButton.setBounds(185, 45, 185, 40);
		characterButton.addListener(
			new ClickListener(){
				@Override
		        public void clicked(InputEvent event, float x, float y) {
					buttonSound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
					showScreen(ScreenEnum.CHARACTER);		   
		        }
			}
		);
		camp = new TextButton("Camp", skin);
		
		if (character.getFood() < 4){
			TextButtonStyle style = new TextButtonStyle(camp.getStyle());
			style.fontColor = Color.RED;
			camp.setStyle(style);
			camp.setTouchable(Touchable.disabled);
		}
	
		table.add(camp).size(145, 40);
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
		foodIcon = new Image(food);
		foodIcon.setSize(75, 75);
		this.addActor(foodIcon);
		
		music.setVolume(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("musicVolume", 1) * .6f);
		music.setLooping(true);
		music.play();
		
		if (!backgroundRendered){
			generateBackground();
		}
	}
	
	@Override
	public void render(float delta) {

		translateCamera();
		Gdx.input.setInputProcessor(multi);
		
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
			clear();
			worldStage.act(delta);
			worldStage.draw();
			cloudStage.act(delta);
			cloudStage.draw();
			super.render(delta);
			drawText(delta);
			world.gameLoop(batch, getCamera().position);
		}
	}
	
	// this should be replaced with label actors
	private void drawText(float delta){
		batch.begin();
		OrthographicCamera camera = (OrthographicCamera) getCamera();
		batch.setColor(1.0f, 1.0f, 1.0f, 1);
		font.draw(batch, String.valueOf(character.getCurrentHealth()), camera.position.x+282*1.1f, camera.position.y+127*1.1f);
		font.draw(batch, "X " + character.getFood(), camera.position.x+23, camera.position.y+25);
		batch.end();
	}
	
	private void translateCamera(){
		Vector3 translationVector = new Vector3(0,0,0);
		int speed = 8;
		if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)) speed = 16;
		
		if (Gdx.input.isKeyPressed(Keys.LEFT) && camera.position.x > 500){
			translationVector.x -= speed;
		}
		else if (Gdx.input.isKeyPressed(Keys.RIGHT) && camera.position.x < 4000){
			translationVector.x += speed;
		}
		if (Gdx.input.isKeyPressed(Keys.DOWN) && camera.position.y > 500){
			translationVector.y -= speed;
		}
		else if (Gdx.input.isKeyPressed(Keys.UP) && camera.position.y < 4600){
			translationVector.y += speed;
		}
		camera.translate(translationVector);
		Vector3 cloudTranslate = new Vector3(translationVector);
		cloudTranslate.x *= 2;
		cloudTranslate.y *= 2;
		cloudCamera.translate(cloudTranslate);
	}
	
	private void generateBackground(){
		backgroundRendered = true;
		frameBuffer.begin();
		SpriteBatch frameBufferBatch = new SpriteBatch();
		frameBufferBatch.begin();
		// draw the base grass texture
		for (int ii = 101; ii >= 0; ii-=2){
			for (int jj = 100; jj >= 0; jj--){
					frameBufferBatch.draw(grasses.get((int)(Math.random()*100) % 3), ii*56, jj*56);
					frameBufferBatch.draw(grasses.get((int)(Math.random()*100) % 3), ((ii-1)*56), (jj*56)+30);
			}	
		}
		frameBufferBatch.end();
		frameBuffer.end();		
		frameBufferBatch.dispose();
		scenery = new TextureRegion(frameBuffer.getColorBufferTexture());
		scenery.setRegion(56, 56, scenery.getRegionWidth() - 56, scenery.getRegionHeight() - 56); 
		scenery.flip(false, true);
		for (int ii = 2; ii >= 0; ii--){
			for (int jj = 5; jj >= 0; jj--){
				Image background = new Image(scenery);
				background.addAction(Actions.moveTo(-700+ii*scenery.getRegionWidth(), -300+jj*scenery.getRegionHeight()));
				group.addActorAt(0, background);
			}
		}
	}
	
    @Override
    public void resize(int width, int height) {
    	super.resize(width, height);
        worldStage.getViewport().update(width, height, false);
        cloudStage.getViewport().update(width, height, false);
    }
	
	@Override
	public void dispose() {
		for(String path: resourceRequirements.keys()){
			if (path.equals(AssetEnum.CLICK_SOUND.getPath())) continue;
			assetManager.unload(path);
		}
		frameBuffer.dispose();
	}
	
}
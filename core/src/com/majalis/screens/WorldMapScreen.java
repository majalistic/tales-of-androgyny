package com.majalis.screens;

import com.badlogic.gdx.Gdx;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.assets.AssetDescriptor;
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
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
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
	private int time;
	private boolean backgroundRendered = false;
	
	public static final Array<AssetDescriptor<?>> resourceRequirements = new Array<AssetDescriptor<?>>();
	static {
		resourceRequirements.add(AssetEnum.UI_SKIN.getSkin());
		resourceRequirements.add(AssetEnum.CLICK_SOUND.getSound());
		resourceRequirements.add(AssetEnum.CHARACTER_ANIMATION.getTexture());
		resourceRequirements.add(AssetEnum.MOUNTAIN_ACTIVE.getTexture());
		resourceRequirements.add(AssetEnum.FOREST_ACTIVE.getTexture());
		resourceRequirements.add(AssetEnum.FOREST_INACTIVE.getTexture());
		resourceRequirements.add(AssetEnum.CASTLE.getTexture());
		resourceRequirements.add(AssetEnum.TOWN.getTexture());
		resourceRequirements.add(AssetEnum.COTTAGE.getTexture());
		resourceRequirements.add(AssetEnum.APPLE.getTexture());
		resourceRequirements.add(AssetEnum.MEAT.getTexture());
		resourceRequirements.add(AssetEnum.GRASS0.getTexture());
		resourceRequirements.add(AssetEnum.GRASS1.getTexture());
		resourceRequirements.add(AssetEnum.GRASS2.getTexture());
		resourceRequirements.add(AssetEnum.CLOUD.getTexture());
		resourceRequirements.add(AssetEnum.ROAD.getTexture());
		resourceRequirements.add(AssetEnum.WORLD_MAP_UI.getTexture());
		resourceRequirements.add(AssetEnum.WORLD_MAP_HOVER.getTexture());
		resourceRequirements.add(AssetEnum.ARROW.getTexture());
		resourceRequirements.add(AssetEnum.CHARACTER_SCREEN.getTexture());
		resourceRequirements.add(AssetEnum.WORLD_MAP_MUSIC.getMusic());
	}
	
	public WorldMapScreen(ScreenFactory factory, ScreenElements elements, AssetManager assetManager, SaveService saveService, LoadService loadService, GameWorld world) {
		super(factory, elements);
		this.assetManager = assetManager;
		this.saveService = saveService;
		
		camera = new PerspectiveCamera(70, 0, 1000);
        FitViewport viewport =  new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), camera);
		worldStage = new Stage3D(viewport, batch);
		
		time = loadService.loadDataValue(SaveEnum.TIME, Integer.class);
		
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
		food = arb % 2 == 0 ? assetManager.get(AssetEnum.APPLE.getTexture()) : assetManager.get(AssetEnum.MEAT.getTexture());
		grasses = new Array<Texture>(true, new Texture[]{assetManager.get(AssetEnum.GRASS0.getTexture()), assetManager.get(AssetEnum.GRASS1.getTexture()), assetManager.get(AssetEnum.GRASS2.getTexture()), assetManager.get(AssetEnum.FOREST_INACTIVE.getTexture())}, 0, 4);
		cloud = assetManager.get(AssetEnum.CLOUD.getTexture());
		characterUITexture = assetManager.get(AssetEnum.WORLD_MAP_UI.getTexture());
		music = assetManager.get(AssetEnum.WORLD_MAP_MUSIC.getMusic());
		
		// move camera to saved position
		Vector3 initialTranslation = loadService.loadDataValue(SaveEnum.CAMERA_POS, Vector3.class);		
		initialTranslation = new Vector3(initialTranslation);
		initialTranslation.x -= camera.position.x;
		initialTranslation.y -= camera.position.y;
		camera.translate(initialTranslation);
		if (camera.position.x < 500) camera.position.x = 500;
		if (camera.position.y < 500) camera.position.y = 500;
		camera.update();
		
		this.world = world;
		
		this.character = loadService.loadDataValue(SaveEnum.PLAYER, PlayerCharacter.class);
		this.cloudGroup = new Group();
		for (int ii = 0; ii < 50; ii++) {
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
		for (Actor actor: world.getActors()) {
			group.addActor(actor);
		}   
		final Sound buttonSound = assetManager.get(AssetEnum.CLICK_SOUND.getSound()); 
		Skin skin = assetManager.get(AssetEnum.UI_SKIN.getSkin());
		int storedLevels = character.getStoredLevels();
		
		Image characterUI = new Image(characterUITexture);
		this.addActor(characterUI);
		characterUI.setScale(1.1f);
		
		TextButton characterButton = new TextButton(storedLevels > 0 ? "Level Up!" : "Character", skin);
		
		if (storedLevels > 0) {
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
			new ClickListener() {
				@Override
		        public void clicked(InputEvent event, float x, float y) {
					buttonSound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
					showScreen(ScreenEnum.CHARACTER);		   
		        }
			}
		);
		final TextButton camp = new TextButton("Camp", skin);
		
		if (character.getFood() < 4) {
			TextButtonStyle style = new TextButtonStyle(camp.getStyle());
			style.fontColor = Color.RED;
			camp.setStyle(style);
			camp.setTouchable(Touchable.disabled);
		}
	
		table.add(camp).size(145, 40);
		Image foodIcon = new Image(food);
		foodIcon.setSize(75, 75);
		this.addActor(foodIcon);
		
		final Label console = new Label("", skin);
		this.addActor(console);
		console.setPosition(820, 80);
		
		camp.addListener(
				new ClickListener() {
					@Override
			        public void clicked(InputEvent event, float x, float y) {
						buttonSound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
						saveService.saveDataValue(SaveEnum.FOOD, -4);	 
						console.setText(saveService.saveDataValue(SaveEnum.TIME, 1));
						console.addAction(Actions.alpha(1));
						console.addAction(Actions.fadeOut(6));
						time++;
						tintForTimeOfDay();
						saveService.saveDataValue(SaveEnum.HEALTH, 10);	
						if (character.getFood() < 4) {
							TextButtonStyle style = new TextButtonStyle(camp.getStyle());
							style.fontColor = Color.RED;
							camp.setStyle(style);
							camp.setTouchable(Touchable.disabled);
						}
			        }
				}
			);
		
		TextButton saveButton = new TextButton("Save", skin);
		this.addActor(saveButton);
		saveButton.setBounds(600, 50, 200, 50);
		saveButton.addListener(
			new ClickListener() {
				@Override
		        public void clicked(InputEvent event, float x, float y) {
					buttonSound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
					saveService.manualSave("data/save01.json");
					console.setText("Game Saved.");
					console.addAction(Actions.alpha(1));
					console.addAction(Actions.fadeOut(4));
		        }
			}
		);	
		
		music.setVolume(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("musicVolume", 1) * .6f);
		music.setLooping(true);
		music.play();
		
		if (!backgroundRendered) {
			generateBackground();
		}
		tintForTimeOfDay();
	}
	
	@Override
	public void render(float delta) {

		translateCamera();
		Gdx.input.setInputProcessor(multi);
		
		if (Gdx.input.isKeyJustPressed(Keys.ENTER)) {
			showScreen(ScreenEnum.CHARACTER);
		}			
		else if (world.gameExit) {
			music.stop();
			showScreen(ScreenEnum.MAIN_MENU);
		}
		else if (world.encounterSelected) {
			music.stop();
			showScreen(ScreenEnum.CONTINUE);
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
	private void drawText(float delta) {
		batch.begin();
		OrthographicCamera camera = (OrthographicCamera) getCamera();
		batch.setColor(1.0f, 1.0f, 1.0f, 1);
		font.draw(batch, String.valueOf(character.getCurrentHealth()), camera.position.x + 310.2f, camera.position.y + 139.7f);
		font.draw(batch, "Day: " + (time / 6 + 1), camera.position.x + 350, camera.position.y + 150);
		font.draw(batch, getTime(), camera.position.x + 370, camera.position.y + 125);
		font.draw(batch, "X " + character.getFood(), camera.position.x + 23, camera.position.y + 25);
		batch.end();
	}
	
	private void tintForTimeOfDay() {
		for (Actor actor : group.getChildren()) {
			actor.setColor(getTimeColor());
		}
	}
	
	private Color getTimeColor() {
		switch (time%6) {
			case 0: return getColor(156, 154, 32); // 255, 239, 205
			case 1: return getColor(255, 255, 214);
			case 2: return getColor(251, 255, 255);
			case 3: return getColor(246, 212, 181);
			case 4: return getColor(75, 125, 217);
			case 5: return getColor(35, 55, 120);  //37,33,84
		}
		return null;
	}
	
	private Color getColor(float r, float g, float b) {
		return new Color(r/256f, g/256f, b/256f, 1);
	}
	
	private String getTime() {
		switch (time%6) {
			case 0: return "Dawn";
			case 1: return "Morning";
			case 2: return "Afternoon";
			case 3: return "Dusk";
			case 4: return "Evening";
			case 5: return "Night";
		}
		return null;
	}
	
	private void translateCamera() {
		Vector3 translationVector = new Vector3(0,0,0);
		int speed = 8;
		if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)) speed = 16;
		
		if (Gdx.input.isKeyPressed(Keys.LEFT) && camera.position.x > 500) {
			translationVector.x -= speed;
		}
		else if (Gdx.input.isKeyPressed(Keys.RIGHT) && camera.position.x < 4000) {
			translationVector.x += speed;
		}
		if (Gdx.input.isKeyPressed(Keys.DOWN) && camera.position.y > 500) {
			translationVector.y -= speed;
		}
		else if (Gdx.input.isKeyPressed(Keys.UP) && camera.position.y < 4600) {
			translationVector.y += speed;
		}
		camera.translate(translationVector);
		Vector3 cloudTranslate = new Vector3(translationVector);
		cloudTranslate.x *= 2;
		cloudTranslate.y *= 2;
		cloudCamera.translate(cloudTranslate);
	}
	
	private void generateBackground() {
		backgroundRendered = true;
		frameBuffer.begin();
		SpriteBatch frameBufferBatch = new SpriteBatch();
		frameBufferBatch.begin();
		// draw the base grass texture
		for (int ii = 101; ii >= 0; ii-=2) {
			for (int jj = 100; jj >= 0; jj--) {
				frameBufferBatch.draw(grasses.get((int)(Math.random()*100) % 3), ii*56, jj*56);
				frameBufferBatch.draw(grasses.get((int)(Math.random()*100) % 3), ((ii-1)*56), (jj*56)+30);
			}	
		}
		frameBufferBatch.end();
		frameBuffer.end();		
		frameBufferBatch.dispose();
		TextureRegion scenery = new TextureRegion(frameBuffer.getColorBufferTexture());
		scenery.setRegion(56, 56, scenery.getRegionWidth() - 56, scenery.getRegionHeight() - 56); 
		scenery.flip(false, true);
		for (int ii = 2; ii >= 0; ii--) {
			for (int jj = 5; jj >= 0; jj--) {
				Image background = new Image(scenery);
				background.addAction(Actions.moveTo(-700+ii*scenery.getRegionWidth(), -300+jj*(scenery.getRegionHeight()-10)));
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
		for(AssetDescriptor<?> path: resourceRequirements) {
			if (path.fileName.equals(AssetEnum.CLICK_SOUND.getSound().fileName)) continue;
			assetManager.unload(path.fileName);
		}
		frameBuffer.dispose();
	}
	
}
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
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Action;
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
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.majalis.asset.AnimatedImage;
import com.majalis.asset.AssetEnum;
import com.majalis.character.PlayerCharacter;
import com.majalis.encounter.EncounterCode;
import com.majalis.save.LoadService;
import com.majalis.save.SaveEnum;
import com.majalis.save.SaveManager;
import com.majalis.save.SaveService;
import com.majalis.world.GameWorldNode;
/*
 * The screen that displays the world map.  UI that Handles player input while on the world map - will delegate to other screens depending on the gameWorld state.
 */
public class WorldMapScreen extends AbstractScreen {

	private final AssetManager assetManager;
	private final SaveService saveService;
	private final Array<GameWorldNode> world;
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
	private final AnimatedImage currentImage;
	private final Texture hoverImage;
	private String hoverText;
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
	
	public WorldMapScreen(ScreenFactory factory, ScreenElements elements, AssetManager assetManager, SaveService saveService, LoadService loadService, Array<GameWorldNode> world) {
		super(factory, elements);
		this.assetManager = assetManager;
		this.saveService = saveService;
		
		camera = new PerspectiveCamera(70, 0, 1000);
        FitViewport viewport =  new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), camera);
		worldStage = new Stage3D(viewport, batch);
	
		hoverText = "";
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
		food = assetManager.get(AssetEnum.APPLE.getTexture());
		grasses = new Array<Texture>(true, new Texture[]{assetManager.get(AssetEnum.GRASS0.getTexture()), assetManager.get(AssetEnum.GRASS1.getTexture()), assetManager.get(AssetEnum.GRASS2.getTexture()), assetManager.get(AssetEnum.FOREST_INACTIVE.getTexture())}, 0, 4);
		cloud = assetManager.get(AssetEnum.CLOUD.getTexture());
		characterUITexture = assetManager.get(AssetEnum.WORLD_MAP_UI.getTexture());
		music = assetManager.get(AssetEnum.WORLD_MAP_MUSIC.getMusic());
		
		hoverImage = assetManager.get(AssetEnum.WORLD_MAP_HOVER.getTexture());
		
		// move camera to saved position
		Vector3 initialTranslation = loadService.loadDataValue(SaveEnum.CAMERA_POS, Vector3.class);		
		initialTranslation = new Vector3(initialTranslation);
		initialTranslation.x -= camera.position.x;
		initialTranslation.y -= camera.position.y;
		camera.translate(initialTranslation);
		if (camera.position.x < 500) camera.position.x = 500;
		if (camera.position.y < 500) camera.position.y = 500;
		camera.update();

		Texture characterSheet = assetManager.get(AssetEnum.CHARACTER_ANIMATION.getTexture());
		Array<TextureRegion> frames = new Array<TextureRegion>();
		for (int ii = 0; ii < 4; ii++) {
			frames.add(new TextureRegion(characterSheet, ii * 72, 0, 72, 128));
		}
		
		Animation animation = new Animation(.14f, frames);
		animation.setPlayMode(PlayMode.LOOP);
		currentImage = new AnimatedImage(animation, Scaling.fit, Align.right);
		currentImage.setScale(.7f);
		currentImage.setState(0);
		// this is currently placing the character based on the camera in a way that conveniently places them on their current node - this needs to instead be aware of the current node and be able to grab its position from there (will need to know current node for behavior of Camp/Enter button regardless)
		currentImage.setPosition(initialTranslation.x + 650, initialTranslation.y + 390);
		
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
		Array<Actor> actors = new Array<Actor>();
		for (GameWorldNode node : world) {
			for (Actor actor : node.getPaths()) {
				actors.add(actor);
			}
		}
		for (GameWorldNode actor : world) {
			actors.add(actor);
			actor.addListener(new ClickListener(){
				@Override
		        public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
					hoverText = actor.getHoverText();
				}
				@Override
		        public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
					hoverText = "";
				}
			});
		}
		for (Actor actor: actors) {
			group.addActor(actor);
		}   

		group.addActor(currentImage);
		
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
		
		Table actionTable = new Table();
		this.addActor(actionTable);
		actionTable.setPosition(675, 75);
		
		Table table = new Table();
		table.setPosition(377, 65);
		this.addActor(table);
		
		actionTable.add(characterButton).size(200, 50).row();
		
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
		final TextButton rest = new TextButton("Rest", skin);
		
		checkCanEat(rest);
	
		table.add(rest).size(145, 40);
		Image foodIcon = new Image(food);
		foodIcon.setSize(75, 75);
		this.addActor(foodIcon);
		
		final Label console = new Label("", skin);
		this.addActor(console);
		console.setPosition(820, 80);
		
		// rest will eventually just wait some time - eating food if possible to maintain hunger level
		rest.addListener(
			new ClickListener() {
				@Override
		        public void clicked(InputEvent event, float x, float y) {
					buttonSound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
					console.setText(saveService.saveDataValue(SaveEnum.TIME, 1));
					console.addAction(Actions.alpha(1));
					console.addAction(Actions.fadeOut(6));
					time++;
					tintForTimeOfDay();
					saveService.saveDataValue(SaveEnum.HEALTH, 10);	
					checkCanEat(rest);
		        }
			}
		);
		
		final TextButton camp = new TextButton("Camp", skin);
	
		table.add(camp).size(145, 40);
		
		camp.addListener(
			new ClickListener() {
				@Override
		        public void clicked(InputEvent event, float x, float y) {
					buttonSound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
					saveService.saveDataValue(SaveEnum.CONTEXT, SaveManager.GameContext.CAMP);
					saveService.saveDataValue(SaveEnum.RETURN_CONTEXT, SaveManager.GameContext.WORLD_MAP);
					showScreen(ScreenEnum.CONTINUE);
		        }
			}
		);
		
		TextButton saveButton = new TextButton("Save", skin);
		actionTable.add(saveButton).size(200, 50).row();
		saveButton.addListener(
			new ClickListener() {
				@Override
		        public void clicked(InputEvent event, float x, float y) {
					buttonSound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
					saveService.manualSave("data/save01.json");
					console.setText("Game Saved.");
					console.addAction(alpha(1));
					console.addAction(fadeOut(4));
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
		group.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if (event.getTarget() instanceof GameWorldNode) {
					currentImage.addAction(moveTo(actor.getX() + 12, actor.getY() + 25, 1.5f));
					int foodLeft = character.getFood() - character.getMetabolicRate();
					saveService.saveDataValue(SaveEnum.TIME, 1);
					if (foodLeft < 0) {
						saveService.saveDataValue(SaveEnum.HEALTH, 5 * foodLeft);
					}
					boolean switchScreen = false;
					if (character.getCurrentHealth() <= 0) {
						if (foodLeft < 0) {
							saveService.saveDataValue(SaveEnum.ENCOUNTER_CODE, EncounterCode.STARVATION);			
							saveService.saveDataValue(SaveEnum.CONTEXT, SaveManager.GameContext.ENCOUNTER);
							saveService.saveDataValue(SaveEnum.RETURN_CONTEXT, SaveManager.GameContext.WORLD_MAP);
							switchScreen = true;
						}
						else {
							saveService.saveDataValue(SaveEnum.ENCOUNTER_CODE, EncounterCode.CAMP_AND_EAT);			
							saveService.saveDataValue(SaveEnum.CONTEXT, SaveManager.GameContext.ENCOUNTER);
							saveService.saveDataValue(SaveEnum.RETURN_CONTEXT, SaveManager.GameContext.WORLD_MAP);
							switchScreen = true;
						}
					}
					else {
						group.addAction(sequence(delay(1.5f), new Action() {
							@Override
							public boolean act(float delta) {
								GameWorldNode node = (GameWorldNode) event.getTarget();
								time++;
								tintForTimeOfDay();
								boolean switchScreen = false;
								EncounterCode newEncounter = node.getEncounterCode();
								if(newEncounter == EncounterCode.DEFAULT) {
									// this will need to also check if the node is a town/dungeon node and appropriately swap the button from "Camp" to "Enter"
									node.setAsCurrentNode();
								}
								else {
									saveService.saveDataValue(SaveEnum.ENCOUNTER_CODE, newEncounter); 
									saveService.saveDataValue(SaveEnum.VISITED_LIST, node.getNodeCode());
									saveService.saveDataValue(SaveEnum.CONTEXT, node.getEncounterContext());
									saveService.saveDataValue(SaveEnum.RETURN_CONTEXT, SaveManager.GameContext.WORLD_MAP);
									switchScreen = true;
								}
								saveService.saveDataValue(SaveEnum.NODE_CODE, node.getNodeCode());
								saveService.saveDataValue(SaveEnum.CAMERA_POS, new Vector2(node.getX(), node.getY()));
								if (switchScreen) {
									music.stop();
									showScreen(ScreenEnum.CONTINUE);
								}
								return true;
							}
						}));
					}
					if (switchScreen) {
						music.stop();
						showScreen(ScreenEnum.CONTINUE);
					}
				}
			}			
		});
	}
	
	private void checkCanEat(TextButton camp) {
		if (character.getFood() < character.getMetabolicRate()) {
			TextButtonStyle style = new TextButtonStyle(camp.getStyle());
			style.fontColor = Color.RED;
			camp.setStyle(style);
			camp.setTouchable(Touchable.disabled);
		}
	}
	
	@Override
	public void render(float delta) {
		translateCamera();
		Gdx.input.setInputProcessor(multi);
		
		if (Gdx.input.isKeyJustPressed(Keys.ENTER)) {
			showScreen(ScreenEnum.CHARACTER);
		}			
		else if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
			music.stop();
			showScreen(ScreenEnum.MAIN_MENU);
		}
		else {
			clear();
			worldStage.act(delta);
			worldStage.draw();
			cloudStage.act(delta);
			cloudStage.draw();
			super.render(delta);
			drawText();
			drawHover();
		}
	}
	
	// this should be replaced with label actors
	private void drawText() {
		batch.begin();
		OrthographicCamera camera = (OrthographicCamera) getCamera();
		batch.setColor(1.0f, 1.0f, 1.0f, 1);
		font.draw(batch, String.valueOf(character.getCurrentHealth()), camera.position.x + 310.2f, camera.position.y + 139.7f);
		font.draw(batch, "Day: " + (time / 6 + 1), camera.position.x + 350, camera.position.y + 150);
		font.draw(batch, getTime(), camera.position.x + 370, camera.position.y + 125);
		font.draw(batch, "X " + character.getFood(), camera.position.x + 23, camera.position.y + 25);
		batch.end();
	}
	
	private void drawHover() {
		if (!hoverText.isEmpty()) {
			batch.begin();
			OrthographicCamera camera = (OrthographicCamera) getCamera();
			// render hover box
			batch.draw(hoverImage, camera.position.x + 1400,  camera.position.y + 5);
			// render hover text
			Color cache = new Color(font.getColor());
			font.setColor(0f, 0, 0, 1);
			font.draw(batch, hoverText, camera.position.x + 1450, camera.position.y + 175, 150, Align.center, true);	
			font.setColor(cache);
			batch.end();
		}
	}
	
	private void tintForTimeOfDay() {
		for (Actor actor : group.getChildren()) {
			actor.setColor(getTimeColor());
		}
	}
	
	private Color getTimeColor() { return TimeOfDay.getTime(time).getColor(); }
	private String getTime() { return TimeOfDay.getTime(time).getDisplay(); }
	
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
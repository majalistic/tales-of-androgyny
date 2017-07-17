package com.majalis.screens;

import com.badlogic.gdx.Gdx;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;
import static com.majalis.asset.AssetEnum.*;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
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
import com.majalis.character.PlayerCharacter.QuestType;
import com.majalis.character.PlayerCharacter.QuestFlag;
import com.majalis.encounter.EncounterBounty;
import com.majalis.encounter.EncounterBounty.EncounterBountyResult;
import com.majalis.encounter.EncounterCode;
import com.majalis.save.LoadService;
import com.majalis.save.SaveEnum;
import com.majalis.save.SaveManager.GameContext;
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
	private final Skin skin;
	private final Texture hoverImageTexture;
	private final Image hoverImage;
	private final Label healthLabel;
	private final Label dateLabel;
	private final Label timeLabel;
	private final Label foodLabel;
	private final Label hoverLabel;
	private GameWorldNode currentNode;
	private GameWorldNode hoveredNode;
	private int time;
	private boolean backgroundRendered = false;
	private GameContext currentContext;
	private final TextButton campButton;
	
	public static final Array<AssetDescriptor<?>> resourceRequirements = new Array<AssetDescriptor<?>>();
	static {
		resourceRequirements.add(UI_SKIN.getSkin());
		resourceRequirements.add(WORLD_MAP_MUSIC.getMusic());
		AssetEnum[] soundAssets = new AssetEnum[]{
			CLICK_SOUND, EQUIP, SWORD_SLASH_SOUND, THWAPPING
		};
		for (AssetEnum asset: soundAssets) {
			resourceRequirements.add(asset.getSound());
		}
		
		// need to refactor to get all stance textures
		AssetEnum[] assets = new AssetEnum[]{
			CHARACTER_ANIMATION, MOUNTAIN_ACTIVE, FOREST_ACTIVE, FOREST_INACTIVE, CASTLE, TOWN, COTTAGE, APPLE, MEAT, GRASS0, GRASS1, GRASS2, CLOUD, ROAD, WORLD_MAP_UI, WORLD_MAP_HOVER, ARROW, CHARACTER_SCREEN
		};
		for (AssetEnum asset: assets) {
			resourceRequirements.add(asset.getTexture());
		}
	}
	
	public WorldMapScreen(ScreenFactory factory, ScreenElements elements, AssetManager assetManager, SaveService saveService, LoadService loadService, Array<GameWorldNode> world) {
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
		hoverImageTexture = assetManager.get(AssetEnum.WORLD_MAP_HOVER.getTexture());		
		food = assetManager.get(AssetEnum.APPLE.getTexture());
		grasses = new Array<Texture>(new Texture[]{assetManager.get(AssetEnum.GRASS0.getTexture()), assetManager.get(AssetEnum.GRASS1.getTexture()), assetManager.get(AssetEnum.GRASS2.getTexture())});
		cloud = assetManager.get(AssetEnum.CLOUD.getTexture());
		characterUITexture = assetManager.get(AssetEnum.WORLD_MAP_UI.getTexture());
		music = assetManager.get(AssetEnum.WORLD_MAP_MUSIC.getMusic());
		hoverImage = new Image(hoverImageTexture);
		
		skin = assetManager.get(AssetEnum.UI_SKIN.getSkin());
		campButton = new TextButton("", skin);
		
		healthLabel = new Label("", skin);
		dateLabel = new Label("", skin);
		timeLabel = new Label("", skin);
		foodLabel = new Label("", skin);
		hoverLabel = new Label("", skin);
		
		// move camera to saved position
		Vector3 initialTranslation = loadService.loadDataValue(SaveEnum.CAMERA_POS, Vector3.class);		
		initialTranslation = new Vector3(initialTranslation);
		initialTranslation.x -= camera.position.x;
		initialTranslation.y -= camera.position.y;
		camera.translate(initialTranslation);
		if (camera.position.x < 500) camera.position.x = 500;
		if (camera.position.y < 500) camera.position.y = 500;
		camera.update();

		// this should probably be a separate class
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
	
	private void mutateLabels() {
		healthLabel.setText(String.valueOf(character.getCurrentHealth()));
		dateLabel.setText("Day: " + (time / 6 + 1));
		timeLabel.setText(getTime());
		timeLabel.setColor(getTimeColor());
		foodLabel.setText("X " + character.getFood());
		if (hoveredNode != null) {
			String text = hoveredNode.getHoverText();
			hoverLabel.setText(text);
			if (!text.equals("")) {
				hoverImage.setVisible(true);
			}
			else {
				hoverImage.setVisible(false);
			}
		}
	}
	
	private void addLabel(Group include, Actor toAdd, int x, int y, Color toSet) {
		include.addActor(toAdd);
		toAdd.setPosition(x, y);
		toAdd.setColor(toSet);		
	}
	@Override
	public void buildStage() {
		final Group uiGroup = new Group();
		uiGroup.addActor(hoverImage);
		hoverImage.setVisible(false);
		hoverImage.setBounds(1500, 5, 400, 300);
		
		addLabel(uiGroup, healthLabel, 310, 130, Color.WHITE);
		addLabel(uiGroup, dateLabel, 360,  140, Color.WHITE);
		addLabel(uiGroup, timeLabel, 380,  115, Color.WHITE);
		addLabel(uiGroup, foodLabel, 23,  15, Color.WHITE);
		addLabel(uiGroup, hoverLabel, 1575, 160, Color.BLACK);
		hoverLabel.setAlignment(Align.center);
		hoverLabel.setWrap(true);
		hoverLabel.setWidth(250);
		// need to add a pane for the hoverLabel
		
		mutateLabels();
		
		for (GameWorldNode node : world) {
			for (Actor actor : node.getPaths()) {
				group.addActor(actor);
			}
		}
		for (final GameWorldNode actor : world) {
			group.addActor(actor);
			if (actor.isCurrent()) {
				setCurrentNode(actor);
			}
			actor.addListener(new ClickListener(){
				@Override
		        public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
					String text = actor.getHoverText();
					hoverLabel.setText(text);
					if (!text.equals("")) {
						hoverImage.setVisible(true);
					}
					hoveredNode = actor;
				}
				@Override
		        public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
					hoverLabel.setText("");
					hoverImage.setVisible(false);
					hoveredNode = null;
				}
			});
		}

		group.addActor(currentImage);
		
		final Sound buttonSound = assetManager.get(AssetEnum.CLICK_SOUND.getSound()); 
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
		
		Image foodIcon = new Image(food);
		foodIcon.setSize(75, 75);
		this.addActor(foodIcon);
		
		final Label console = new Label("", skin);
		this.addActor(console);
		console.setPosition(820, 80);
		
		final TextButton rest = new TextButton("Rest", skin);
		
		checkCanEat(rest);
	
		table.add(rest).size(145, 40);
		
		
		// rest will eventually just wait some time - eating food if possible to maintain hunger level
		rest.addListener(
			new ClickListener() {
				@Override
		        public void clicked(InputEvent event, float x, float y) {
					buttonSound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
					console.setText(saveService.saveDataValue(SaveEnum.HEALTH, 10) + " " + saveService.saveDataValue(SaveEnum.TIME, 1));
					console.addAction(Actions.alpha(1));
					console.addAction(Actions.fadeOut(6));
					time++;
					tintForTimeOfDay();
					checkCanEat(rest);
					mutateLabels();
		        }
			}
		);

		final TextButton scout = new TextButton("Scout", skin);
		
		table.add(scout).size(145, 40).row();;

		// rest will eventually just wait some time - eating food if possible to maintain hunger level
		scout.addListener(
			new ClickListener() {
				@Override
		        public void clicked(InputEvent event, float x, float y) {
					buttonSound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
					if (character.getCurrentHealth() <= 0) {						
						saveService.saveDataValue(SaveEnum.ENCOUNTER_CODE, character.getFood() <= 0 ? EncounterCode.STARVATION : EncounterCode.CAMP_AND_EAT);	
						saveService.saveDataValue(SaveEnum.CONTEXT, GameContext.ENCOUNTER);
						saveService.saveDataValue(SaveEnum.RETURN_CONTEXT, GameContext.WORLD_MAP);
						music.stop();
						showScreen(ScreenEnum.CONTINUE);
					}
					else {
						console.setText(saveService.saveDataValue(SaveEnum.SCOUT, 1) + " " + saveService.saveDataValue(SaveEnum.TIME, 1));
						console.addAction(Actions.alpha(1));
						console.addAction(Actions.fadeOut(6));
						currentNode.deactivate();
						currentNode.setAsCurrentNode();
						time++;
						tintForTimeOfDay();	
						checkCanEat(rest);
						mutateLabels();
						if (character.getCurrentHealth() <= 0) {						
							saveService.saveDataValue(SaveEnum.ENCOUNTER_CODE, character.getFood() <= 0 ? EncounterCode.STARVATION : EncounterCode.CAMP_AND_EAT);	
							saveService.saveDataValue(SaveEnum.CONTEXT, GameContext.ENCOUNTER);
							saveService.saveDataValue(SaveEnum.RETURN_CONTEXT, GameContext.WORLD_MAP);
							music.stop();
							showScreen(ScreenEnum.CONTINUE);
						}
					}
		        }
			}
		);
	
		table.add(campButton).size(145, 40);
		
		campButton.addListener(
			new ClickListener() {
				@Override
		        public void clicked(InputEvent event, float x, float y) {
					buttonSound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
					saveService.saveDataValue(SaveEnum.CONTEXT, currentContext);
					saveService.saveDataValue(SaveEnum.RETURN_CONTEXT, GameContext.WORLD_MAP);
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
					console.addAction(fadeOut(6));
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
		
		this.addActor(uiGroup);
		group.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if (event.getTarget() instanceof GameWorldNode) {
					final GameWorldNode node = (GameWorldNode) event.getTarget();
					final int timePassed = 1;
					int foodLeft = character.getFood() - character.getMetabolicRate() * timePassed;
					saveService.saveDataValue(SaveEnum.TIME, timePassed);
					boolean switchScreen = false;
					if (character.getCurrentHealth() <= 0) {						
						saveService.saveDataValue(SaveEnum.ENCOUNTER_CODE, foodLeft < 0 ? EncounterCode.STARVATION : EncounterCode.CAMP_AND_EAT);	
						saveService.saveDataValue(SaveEnum.CONTEXT, GameContext.ENCOUNTER);
						saveService.saveDataValue(SaveEnum.RETURN_CONTEXT, GameContext.WORLD_MAP);
						switchScreen = true;
					} // forced Trudy encounter
					else if (time >= 11 && character.getQuestStatus(QuestType.ELF) == 0) {
						saveService.saveDataValue(SaveEnum.QUEST, new QuestFlag(QuestType.ELF, 1));	
						autoEncounter(uiGroup, EncounterCode.ELF);
					}
					else if (time >= 23 && character.getQuestStatus(QuestType.TRUDY) == 0) {
						autoEncounter(uiGroup, EncounterCode.ADVENTURER);
					}
					else {
						currentImage.addAction(moveTo(actor.getX() + 12, actor.getY() + 25, 1.5f));
						setCurrentNode(node);
						group.addAction(sequence(delay(1.5f), new Action() {
							@Override
							public boolean act(float delta) {
								time += timePassed;
								tintForTimeOfDay();
								boolean switchScreen = false;
								EncounterCode newEncounter = node.getEncounterCode();
								EncounterBounty miniEncounter = newEncounter.getMiniEncounter();
								if(newEncounter == EncounterCode.DEFAULT) {
									// this will need to also check if the node is a town/dungeon node and appropriately swap the button from "Camp" to "Enter"
									saveService.saveDataValue(SaveEnum.SCOUT, 0);
									node.deactivate();
									node.setAsCurrentNode();
								}
								else if (miniEncounter != null) {
									final Image displayNewEncounter = new Image(hoverImageTexture);
									displayNewEncounter.setBounds(250, 200, 500, 300);
									uiGroup.addActor(displayNewEncounter);
									EncounterBountyResult result = miniEncounter.execute(character.getScoutingScore(), saveService);
									final Label newEncounterText = new Label(result.displayText(), skin);
									
									if (result.soundToPlay() != null) {
										assetManager.get(result.soundToPlay()).play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f); 
									}
									
									newEncounterText.setColor(Color.GOLD);
									newEncounterText.setPosition(415, 335);
									newEncounterText.setAlignment(Align.center);
									newEncounterText.setWrap(true);
									newEncounterText.setWidth(200);
									uiGroup.addActor(newEncounterText);
									uiGroup.addAction(sequence(delay(8), new Action() {
										@Override
										public boolean act(float delta) {
											uiGroup.removeActor(newEncounterText);
											uiGroup.removeActor(displayNewEncounter);
											return true;
										}
									}));
									saveService.saveDataValue(SaveEnum.VISITED_LIST, node.getNodeCode());
									saveService.saveDataValue(SaveEnum.SCOUT, 0);
									node.deactivate();
									node.setAsCurrentNode();
								}
								else {
									saveService.saveDataValue(SaveEnum.ENCOUNTER_CODE, newEncounter); 
									saveService.saveDataValue(SaveEnum.VISITED_LIST, node.getNodeCode());
									saveService.saveDataValue(SaveEnum.CONTEXT, node.getEncounterContext());
									saveService.saveDataValue(SaveEnum.RETURN_CONTEXT, GameContext.WORLD_MAP);
									switchScreen = true;
								}
								saveService.saveDataValue(SaveEnum.NODE_CODE, node.getNodeCode());
								saveService.saveDataValue(SaveEnum.CAMERA_POS, new Vector2(node.getX(), node.getY()));
								if (switchScreen) {
									music.stop();
									showScreen(ScreenEnum.CONTINUE);
								}
								mutateLabels();
								return true;
							}
						}));
					}
					if (switchScreen) {
						music.stop();
						showScreen(ScreenEnum.CONTINUE);
					}
				}
				mutateLabels();
			}			
		});
	}

	
	private void setCurrentNode(GameWorldNode newCurrentNode) {
		currentNode = newCurrentNode;
		currentContext = currentNode.getEncounterContext() == GameContext.TOWN ? GameContext.TOWN : GameContext.CAMP;
		campButton.setText(currentContext == GameContext.TOWN ? "Enter" : "Camp");
	}

	private void autoEncounter(Group uiGroup, EncounterCode encounter) {
		saveService.saveDataValue(SaveEnum.ENCOUNTER_CODE, encounter);	
		saveService.saveDataValue(SaveEnum.CONTEXT, GameContext.ENCOUNTER);
		saveService.saveDataValue(SaveEnum.RETURN_CONTEXT, GameContext.WORLD_MAP);
		Image displayNewEncounter = new Image(hoverImageTexture);
		displayNewEncounter.setBounds(250, 200, 500, 300);
		uiGroup.addActor(displayNewEncounter);
		Label newEncounterText = new Label("Encounter!", skin);
		newEncounterText.setColor(Color.GOLD);
		newEncounterText.setPosition(430, 335);
		uiGroup.addActor(newEncounterText);
		uiGroup.addAction(sequence(delay(1.5f), new Action() {
			@Override
			public boolean act(float delta) {
				music.stop();
				showScreen(ScreenEnum.CONTINUE);
				return true;
			}
		}));
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
			// this call to clear and altering of the clearScreen var could be removed, possibly, if worldStage became UI stage (super.render would replace world stage - all nodes and such would be added to this, and uiStage stuff would be added to a new Stage, uiStage)
			clear();
			// draws the world
			worldStage.act(delta);
			worldStage.draw();
			// draws the cloud layer
			cloudStage.act(delta);
			cloudStage.draw();
			// this draws the UI
			super.render(delta);
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
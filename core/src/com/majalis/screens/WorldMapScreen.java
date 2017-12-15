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
import com.badlogic.gdx.math.RandomXS128;
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
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
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
import com.majalis.save.MutationResult;
import com.majalis.save.SaveEnum;
import com.majalis.save.SaveManager.GameContext;
import com.majalis.save.SaveManager.GameMode;
import com.majalis.scenes.MutationActor;
import com.majalis.save.SaveService;
import com.majalis.save.MutationResult.MutationType;
import com.majalis.world.GameWorldNode;
import com.majalis.world.GroundType;
/*
 * The screen that displays the world map.  UI that Handles player input while on the world map - will delegate to other screens depending on the gameWorld state.
 */
public class WorldMapScreen extends AbstractScreen {
	// this class needs major refactoring - far too many dependencies, properties, statefulness
	private final AssetManager assetManager;
	private final SaveService saveService;
	private final Array<GameWorldNode> world;
	private final Texture food;
	private final Texture cloud;
	private final Texture characterUITexture;
	private final PlayerCharacter character;
	private final Stage uiStage;
	private final PerspectiveCamera camera;
	private final Stage cloudStage;
	private final PerspectiveCamera cloudCamera;
	private final Stage dragStage;
	private final Group worldGroup;
	private final Group shadowGroup;
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
	private final TextButton campButton;
	private final boolean storyMode;
	private GameWorldNode currentNode;
	private GameWorldNode hoveredNode;
	private int time;
	private boolean backgroundRendered = false;
	private GameContext currentContext;
	
	private final RandomXS128 random;
	
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
			GROUND_SHEET, DOODADS, WORLD_MAP_BG, CHARACTER_ANIMATION, MOUNTAIN_ACTIVE, FOREST_ACTIVE, FOREST_INACTIVE, CASTLE, TOWN, COTTAGE, APPLE, MEAT, CLOUD, ROAD, WORLD_MAP_UI, WORLD_MAP_HOVER, ARROW, CHARACTER_SCREEN, EXP, GOLD, TIME, HEART, NULL
		};
		for (AssetEnum asset: assets) {
			resourceRequirements.add(asset.getTexture());
		}
		resourceRequirements.addAll(CharacterScreen.resourceRequirements);
	}
	
	public WorldMapScreen(ScreenFactory factory, ScreenElements elements, AssetManager assetManager, SaveService saveService, LoadService loadService, Array<GameWorldNode> world, RandomXS128 random) {
		super(factory, elements);
		this.assetManager = assetManager;
		this.saveService = saveService;
		this.random = random;
		
		this.storyMode = loadService.loadDataValue(SaveEnum.MODE, GameMode.class) == GameMode.STORY;
		uiStage = new Stage3D(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), getCamera()), batch);
		
		dragStage = new Stage3D(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), getCamera()), batch);
		
		camera = new PerspectiveCamera(70, 0, 1000);
		this.getViewport().setCamera(camera);

		time = loadService.loadDataValue(SaveEnum.TIME, Integer.class);
		
		camera.position.set(0, 0, storyMode ? 500 : 750);
		camera.near = 1f;
		camera.far = 10000;
		camera.lookAt(0, 0, 0);
		camera.translate(1280/2, 720/2, 200);
		
		cloudCamera = new PerspectiveCamera(70, 0, 1000);
        FitViewport viewport2 = new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), cloudCamera);
		cloudStage = new Stage3D(viewport2, batch);
		
		cloudCamera.position.set(0, 0, 500);
		cloudCamera.near = 1f;
		cloudCamera.far = 10000;
		cloudCamera.lookAt(0,0,0);
		cloudCamera.translate(1280/2, 720/2, -200);
		
		// create base group for world stage
		worldGroup = new Group();
		this.addActor(worldGroup);
		
		shadowGroup = new Group();
		
		// load assets
		hoverImageTexture = assetManager.get(AssetEnum.WORLD_MAP_HOVER.getTexture());		
		food = assetManager.get(AssetEnum.APPLE.getTexture());
		cloud = assetManager.get(AssetEnum.CLOUD.getTexture());
		characterUITexture = assetManager.get(AssetEnum.WORLD_MAP_UI.getTexture());
		music = assetManager.get(AssetEnum.WORLD_MAP_MUSIC.getMusic());
		hoverImage = new Image(hoverImageTexture);
		
		skin = assetManager.get(AssetEnum.UI_SKIN.getSkin());
		campButton = new TextButton("", skin);
		
		// these should be updated with emitters
		healthLabel = new Label("", skin);
		dateLabel = new Label("", skin);
		timeLabel = new Label("", skin);
		foodLabel = new Label("", skin);
		hoverLabel = new Label("", skin);
		
		for (final GameWorldNode actor : world) {
			if (actor.isCurrent()) {
				setCurrentNode(actor);
			}
		}
		
		// move camera to saved position
		Vector3 initialTranslation = new Vector3(currentNode.getX(), currentNode.getY(), 0);
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
		
		multi = new InputMultiplexer();
		multi.addProcessor(uiStage);
		multi.addProcessor(this);
		multi.addProcessor(dragStage);
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
		
		final Sound buttonSound = assetManager.get(AssetEnum.CLICK_SOUND.getSound()); 
		int storedLevels = character.getStoredLevels();
		
		Image characterUI = new Image(characterUITexture);
		uiStage.addActor(characterUI);
		characterUI.setScale(1.1f);
		
		TextButton characterButton = new TextButton(storedLevels > 0 ? "Level Up!" : "Character", skin);
		
		if (storedLevels > 0) {
			TextButtonStyle style = new TextButtonStyle(characterButton.getStyle());
			style.fontColor = Color.OLIVE;
			characterButton.setStyle(style);
		}
		
		Table table = new Table();
		table.setPosition(377, 65);
		uiStage.addActor(table);
		
		Table actionTable = new Table();
		uiStage.addActor(actionTable);
		actionTable.setPosition(900, 60);
		
		actionTable.add(characterButton).size(200, 50);
		
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
		
		TextButton inventoryButton = new TextButton("Inventory", skin);
		
		inventoryButton.setBounds(185, 45, 185, 40);
		inventoryButton.addListener(
			new ClickListener() {
				@Override
		        public void clicked(InputEvent event, float x, float y) {
					buttonSound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
					showScreen(ScreenEnum.INVENTORY);		   
		        }
			}
		);
		
		actionTable.add(inventoryButton).size(200, 50).row();
		
		Image foodIcon = new Image(food);
		foodIcon.setSize(75, 75);
		uiStage.addActor(foodIcon);
		
		final Label console = new Label("", skin);
		uiStage.addActor(console);
		console.setPosition(1250, 80);
		console.setWrap(true);
		console.setWidth(600);
		console.setColor(Color.CHARTREUSE);
		
		final TextButton rest = new TextButton("Rest", skin);
		
		checkCanEat(rest);
	
		table.add(rest).size(145, 40);
		
		// rest will eventually just wait some time - eating food if possible to maintain hunger level
		rest.addListener(
			new ClickListener() {
				@SuppressWarnings("unchecked")
				@Override
		        public void clicked(InputEvent event, float x, float y) {
					buttonSound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
					setConsole(console, saveService.saveDataValue(SaveEnum.HEALTH, 10), saveService.saveDataValue(SaveEnum.TIME, 1));
					console.addAction(Actions.alpha(1));
					console.addAction(Actions.fadeOut(10));
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
				@SuppressWarnings("unchecked")
				@Override
		        public void clicked(InputEvent event, float x, float y) {
					buttonSound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
					if (checkForForcedRest());
					else {
						setConsole(console, saveService.saveDataValue(SaveEnum.SCOUT, 1), saveService.saveDataValue(SaveEnum.TIME, 1));
						console.addAction(Actions.alpha(1));
						console.addAction(Actions.fadeOut(10));
						currentNode.deactivate();
						currentNode.setAsCurrentNode();
						time++;
						tintForTimeOfDay();	
						checkCanEat(rest);
						mutateLabels();
						checkForForcedRest();
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
					music.stop();
					showScreen(ScreenEnum.CONTINUE);
		        }
			}
		);
		
		TextButton questButton = new TextButton("Quest Log", skin);
		actionTable.add(questButton).size(200, 50);
		questButton.addListener(
			new ClickListener() {
				@Override
		        public void clicked(InputEvent event, float x, float y) {
					buttonSound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
					showScreen(ScreenEnum.QUEST);
		        }
			}
		);	
		
		TextButton saveButton = new TextButton("QuickSave", skin);
		actionTable.add(saveButton).size(200, 50);
		saveButton.addListener(
			new ClickListener() {
				@Override
		        public void clicked(InputEvent event, float x, float y) {
					buttonSound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
					saveService.manualSave(".toa-data/quicksave.json");
					console.setText("Game Saved.");
					console.addAction(alpha(1));
					console.addAction(fadeOut(6));
		        }
			}
		);	
		
		TextButton hardSaveButton = new TextButton("Save", skin);
		actionTable.add(hardSaveButton).size(200, 50).row();
		hardSaveButton.addListener(
			new ClickListener() {
				@Override
		        public void clicked(InputEvent event, float x, float y) {
					buttonSound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
					showScreen(ScreenEnum.SAVE);
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
		
		uiStage.addActor(uiGroup);
		// this needs refactoring - probably replace ChangeListener with a custom listener/event type, and rather than an action on a delay, have a trigger for when the character reaches a node that will perform the act() function
		worldGroup.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if (event.getTarget() instanceof GameWorldNode) {
					final GameWorldNode node = (GameWorldNode) event.getTarget();
					final int timePassed = 1;
					saveService.saveDataValue(SaveEnum.TIME, timePassed);
					boolean switchScreen = false;
					if(checkForForcedRest());
					else if (character.getCurrentDebt() >= 150 || (character.getCurrentDebt() >= 100 && character.getQuestStatus(QuestType.DEBT) < 1)) {
						autoEncounter(uiGroup, EncounterCode.BUNNY);
					}
					else if (time >= 11 && character.getQuestStatus(QuestType.ELF) == 0) { // forced elf encounter
						saveService.saveDataValue(SaveEnum.QUEST, new QuestFlag(QuestType.ELF, 1));	
						autoEncounter(uiGroup, EncounterCode.ELF);
					}
					else if (time >= 23 && character.getQuestStatus(QuestType.TRUDY) == 0) { // forced Trudy encounter
						autoEncounter(uiGroup, EncounterCode.ADVENTURER);
					}
					else {
						currentImage.addAction(moveTo(actor.getX() + 12, actor.getY() + 25, 1.5f));
						setCurrentNode(node);
						worldGroup.addAction(sequence(delay(1.5f), new Action() {
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
									displayNewEncounter.setBounds(250, 150, 500, 400);
									uiGroup.addActor(displayNewEncounter);
									EncounterBountyResult result = miniEncounter.execute(character.getScoutingScore(), saveService);
									
									final Label newEncounterText = new Label(result.displayText(), skin);
									
									if (result.soundToPlay() != null) {
										assetManager.get(result.soundToPlay()).play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f); 
									}
																										
									newEncounterText.setColor(Color.GOLD);

									final Table statusResults = new Table();
									statusResults.setPosition(350, 425);
									newEncounterText.setWrap(true);
									statusResults.align(Align.topLeft);
									statusResults.add(newEncounterText).width(325).row();
									Array<MutationResult> compactedResults = MutationResult.collapse(result.getResults()); 
									for (MutationResult miniResult : compactedResults) {
										MutationActor actor = new MutationActor(miniResult, assetManager.get(miniResult.getTexture()), skin, true);
										actor.setWrap(true);
										// this width setting is going to be tricky once we implement images for perk and skill gains and such
										statusResults.add(actor).width(miniResult.getType() == MutationType.NONE ? 325 : 50).height(50).align(Align.left).row();
									}
									
									uiGroup.addActor(statusResults); 									
									uiGroup.addAction(sequence(
										delay(1), 
										new Action(){ @Override
											public boolean act(float delta) {
												checkForForcedRest();
												return true;
										}}, 
										delay(7), 
										new Action() {
											@Override
											public boolean act(float delta) {
												uiGroup.removeActor(displayNewEncounter);
												uiGroup.removeActor(statusResults);
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
								if (switchScreen) {
									switchContext();
								}
								mutateLabels();
								return true;
							}
						}));
					}
					if (switchScreen) {
						switchContext();
					}
				}
				mutateLabels();
			}			
		});
		dragStage.addListener(new DragListener(){
			@Override
			public void drag(InputEvent event, float x, float y, int pointer) {
				translateCamera(new Vector3(getDeltaX(), getDeltaY(), 0));
			}
		});
	}

	private void setConsole(Label console, Array<MutationResult> ...allResults) {
		String consoleText = "";
		for (Array<MutationResult> results : allResults) {
			for (MutationResult result : results) {
				consoleText += result.getText() + " ";
			}
		}
		console.setText(consoleText.trim());
	}
	
	private void setCurrentNode(GameWorldNode newCurrentNode) {
		currentNode = newCurrentNode;
		currentContext = currentNode.getEncounterContext() == GameContext.TOWN ? GameContext.TOWN : GameContext.CAMP;
		campButton.setText(currentContext == GameContext.TOWN ? "Enter" : "Camp");
	}
	
	private boolean checkForForcedRest() {
		if (character.getCurrentHealth() <= 0) {		
			if (character.getFood() <= 0) {
				saveService.saveDataValue(SaveEnum.ENCOUNTER_CODE, EncounterCode.STARVATION);	
				saveService.saveDataValue(SaveEnum.CONTEXT, GameContext.ENCOUNTER);
			}
			else {
				saveService.saveDataValue(SaveEnum.CONTEXT, GameContext.CAMP);
			}			
			saveService.saveDataValue(SaveEnum.RETURN_CONTEXT, GameContext.WORLD_MAP);
			switchContext();
			return true;
		}
		return false;
	}

	private void switchContext() {
		music.stop();
		character.popPortraitPath();
		showScreen(ScreenEnum.CONTINUE);
	}
	
	private void autoEncounter(Group uiGroup, EncounterCode encounter) {
		saveService.saveDataValue(SaveEnum.ENCOUNTER_CODE, encounter);	
		saveService.saveDataValue(SaveEnum.CONTEXT, GameContext.ENCOUNTER);
		saveService.saveDataValue(SaveEnum.RETURN_CONTEXT, GameContext.WORLD_MAP);
		Image displayNewEncounter = new Image(hoverImageTexture);
		displayNewEncounter.setBounds(250, 150, 500, 400);
		uiGroup.addActor(displayNewEncounter);
		Label newEncounterText = new Label("Encounter!", skin);
		newEncounterText.setColor(Color.GOLD);
		newEncounterText.setPosition(430, 335);
		uiGroup.addActor(newEncounterText);
		uiGroup.addAction(sequence(delay(1.5f), new Action() {
			@Override
			public boolean act(float delta) {
				switchContext();
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
			// draws the world
			super.render(delta);
			// draws the cloud layer
			cloudStage.act(delta);
			cloudStage.draw();
			// this draws the UI
			uiStage.act();
			uiStage.draw();
		}
	}
	
	private void tintForTimeOfDay() {
		TimeOfDay timeOfDay = TimeOfDay.getTime(time);
		
		for (Actor actor : worldGroup.getChildren()) {
			actor.setColor(getTimeColor());
		}
		for (Actor actor : shadowGroup.getChildren()) {
			actor.setColor(timeOfDay.getShadowColor());
			actor.addAction(Actions.alpha(timeOfDay.getShadowAlpha()));
			actor.setRotation(timeOfDay.getShadowDirection());
			actor.setScaleY(timeOfDay.getShadowLength());	
		}
	}
	
	private Color getTimeColor() { return TimeOfDay.getTime(time).getColor(); }
	private String getTime() { return TimeOfDay.getTime(time).getDisplay(); }
	
	private void translateCamera() {
		Vector3 translationVector = new Vector3(0, 0, 0);
		int speed = 8;
		if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)) speed = 16;
		
		if (Gdx.input.isKeyPressed(Keys.LEFT) && camera.position.x > 500) {
			translationVector.x -= speed;
		}
		else if (Gdx.input.isKeyPressed(Keys.RIGHT) && camera.position.x < (storyMode ? 1000 : 4000)) {
			translationVector.x += speed;
		}
		if (Gdx.input.isKeyPressed(Keys.DOWN) && camera.position.y > 500) {
			translationVector.y -= speed;
		}
		else if (Gdx.input.isKeyPressed(Keys.UP) && camera.position.y < (storyMode ? 1000 : 4600)) {
			translationVector.y += speed;
		}
		translateCamera(translationVector);
	}
	
	private void translateCamera(Vector3 translationVector) {
		float x = camera.position.x;
		float y = camera.position.y;
		camera.translate(translationVector);
		Vector3 position = camera.position;
		position.x = Math.max(Math.min(position.x, 4000), 500);
		position.y = Math.max(Math.min(position.y, 4600), 500);		
		x = position.x - x;
		y = position.y - y;		
		Vector3 cloudTranslate = new Vector3(x, y, 0);
		cloudTranslate.x *= 2;
		cloudTranslate.y *= 2;
		cloudCamera.translate(cloudTranslate);
		position = cloudCamera.position;
	}
	
	private ObjectMap<String, TextureRegion> groundSlices = new ObjectMap<String, TextureRegion>();
	private final static int tileWidth = 61;
	private final static int tileHeight = 55;
	
	private final static int scalingFactor = 54;
	private final static int xFactor = -9; // to tessellate properly ÅP\_(Éc)_/ÅP
	
	private final int distance(int x, int y, int x2, int y2) {
		return Math.max(Math.max(Math.abs(x - x2), Math.abs(y - y2)), Math.abs((0 - (x + y)) - (0 - (x2 + y2))));				
	}
	
	private boolean worldCollide(int x, int y) {
		Vector2 collider = new Vector2(x, y);
		for (GameWorldNode node : world) {
			if (node.isOverlapping(collider)) return true;
		}
		return false;
	}
	
	private void generateBackground() {
		backgroundRendered = true;
		if (storyMode) {
			Image background = new Image(assetManager.get(WORLD_MAP_BG.getTexture()));
			background.setPosition(-400, 0);
			worldGroup.addActorAt(0, background);
			addWorldActors();
		}
		else {
			/* MODELLING - SHOULD BE MOVED TO GAME WORLD GEN */
			
			Array<Array<GroundType>> ground = new Array<Array<GroundType>>();
			Array<Image> trees = new Array<Image>();
			Array<Image> shadows = new Array<Image>();
			Array<Image> rocks = new Array<Image>();			
			
			Texture treeTexturesSheet = assetManager.get(AssetEnum.DOODADS.getTexture());
			Array<TextureRegion> treeTextures = new Array<TextureRegion>();
			int treeArraySize = 26;
			int treeWidth = 192;
			int treeHeight = 256;
			for (int ii = 0; ii < treeArraySize; ii++) {
				treeTextures.add(new TextureRegion(treeTexturesSheet, ii * treeWidth, 0, treeWidth, treeHeight));
			}
			
			int xScreenBuffer = 683;
			int yScreenBuffer = 165;
			
			// first figure out what all of the tiles are - dirt, greenLeaf, redLeaf, moss, or water - create a model without drawing anything	
			for (int x = 0; x < 170; x++) {
				Array<GroundType> layer = new Array<GroundType>();
				ground.add(layer);
				for (int y = 0; y < 235; y++) {
					// redLeaf should be the default			
					// dirt should be randomly spread throughout redLeaf  
					// greenLeaf might also be randomly spread throughout redLeaf
					// bodies of water should be generated as a single central river that runs through the map for now, that randomly twists and turns and bulges at the turns
					// moss should be in patches adjacent to water
					
					if (worldCollide(x, y)) {
						layer.add(GroundType.DIRT);
						continue;
					}
					
					GroundType toAdd;
					
					if (distance(x, y, 13, 90) < 5) toAdd = GroundType.WATER;
					else if (distance(x, y, 13, 90) >= 5 && distance(x, y, 13, 90) < 7) toAdd = GroundType.DIRT;
					//else if (x == 120 || x == 128 || y == 88 || y == 96) toAdd = GroundType.WATER;
					//else if (x % 8 == 0 || y % 8 == 0) toAdd = GroundType.DIRT;
					else {
						toAdd = GroundType.valueOf("RED_LEAF_" + Math.abs(random.nextInt() % 6));					
					}
					
					layer.add(toAdd);
					if (toAdd == GroundType.DIRT || toAdd == GroundType.RED_LEAF_0 || toAdd == GroundType.RED_LEAF_1) {
						if (random.nextInt() % 20 == 0) {
							TextureRegion chosenTree = treeTextures.get(Math.abs(random.nextInt() % treeArraySize));
							Image tree = new Image(chosenTree);
							Image shadow = new Image(chosenTree);
							int trueX = getTrueX(x) - (int)tree.getWidth() / 2 + tileWidth / 2;
							int trueY = getTrueY(x, y) + tileHeight / 2;
							tree.setPosition(trueX , trueY);
							shadow.setPosition(trueX, trueY);
							shadow.setOrigin(shadow.getWidth() / 2, 16);
							
							boolean treeInserted = false;
							int ii = 0;
							for (Image treeCompare : trees) {
								if (tree.getY() > treeCompare.getY()) {
									treeInserted = true;
									trees.insert(ii, tree);
									shadows.insert(ii, shadow);
									break;
								}
								ii++;
							}
							if (!treeInserted) {
								trees.add(tree);
								shadows.add(shadow);
							}
						}
					}	
				}
			}			
			
			// iterate a second pass through and determine where rocks and trees and shadows should go
			/*for (int x = 0; x < ground.size; x++) {
				int layerSize = ground.get(x).size;
				for (int y = 0; y < ground.get(x).size; y++) {
					// place random rocks on tiles adjacent to water
					
					// place random trees on redLeaf/greenLeaf/dirt tiles that aren't adjacent to water
					// for each tree, create a shadow (should be mapped shadow textures placed at the same location as the tree)

				}
			}*/
			
			/* DRAWING */
			
			
			// draw (add drawings as actors) water layer first
			
			// draw (add reflections as actors) reflections
			
			// then draw (add drawings as actors) ground layer
						
			Texture groundSheet = assetManager.get(AssetEnum.GROUND_SHEET.getTexture());

			int boxWidth = 2560;
			int boxHeight = 1440;
			
			// draw the terrain within a given box - currently attempting to draw all terrain and being truncated
			int[] layers = new int [GroundType.values().length];
			
			for (int xTile = 0; xTile < 4; xTile++) {
				for (int yTile = 0; yTile < 6; yTile++) {
					FrameBuffer frameBuffer2 = new FrameBuffer(Pixmap.Format.RGB888, boxWidth, boxHeight, false);
					frameBuffer2.begin();
					SpriteBatch frameBufferBatch = new SpriteBatch();
					frameBufferBatch.begin();
					for (int x = 0; x < ground.size; x++) {
						int trueX = getTrueX(x) - (boxWidth) * xTile + xScreenBuffer;
						if (trueX < -60 || trueX > boxWidth + 50) continue;
						int layerSize = ground.get(x).size;
						for (int y = 0; y < ground.get(x).size; y++) {
							int trueY = getTrueY(x, y) - (boxHeight) * yTile + yScreenBuffer;
							if (trueY < -60 || trueY > boxHeight + 50) continue;
							for (int i = 0; i < layers.length; i++) {
								layers[i] = 0;
							}
							
							GroundType currentHexType = ground.get(x).get(y);
							
							// check the six adjacent tiles and add accordingly
							if (x + 1 < ground.size) {
								GroundType temp = ground.get(x + 1).get(y);
								if (temp != currentHexType) layers[temp.ordinal()] += 1;
							}
							if (y - 1 >= 0)	{
								if (x + 1 < ground.size) {							
									GroundType temp = ground.get(x + 1).get(y - 1);
									if (temp != currentHexType) layers[temp.ordinal()] += 2;
									
								}
								GroundType temp = ground.get(x).get(y - 1);
								if (temp != currentHexType) layers[temp.ordinal()] += 4;
							}
							if (x - 1 >= 0)	{
								GroundType temp = ground.get(x - 1).get(y);
								if (temp != currentHexType) layers[temp.ordinal()] += 8;
							}
							if (y + 1 < layerSize)	{
								if (x - 1 >= 0) {
									GroundType temp = ground.get(x - 1).get(y + 1);
									if (temp != currentHexType) layers[temp.ordinal()] += 16;
								}		
								GroundType temp = ground.get(x).get(y + 1);
								if (temp != currentHexType) layers[temp.ordinal()] += 32;
							}
							
							for (GroundType groundType: GroundType.values()) {
								if (currentHexType == groundType) {
									frameBufferBatch.draw(getFullTexture(groundType, groundSheet), trueX, trueY); // with appropriate type
									
								}
								frameBufferBatch.draw(getTexture(groundType, groundSheet, layers[groundType.ordinal()]), trueX, trueY); // appropriate blend layer
							}
						}
					}
		
					frameBufferBatch.end();
					frameBuffer2.end();
					frameBufferBatch.dispose();
					Image background = new Image(new TextureRegion(frameBuffer2.getColorBufferTexture(), 0, boxHeight, boxWidth, -boxHeight));
					background.addAction(Actions.moveTo(-xScreenBuffer + xTile * boxWidth, -yScreenBuffer + yTile * (boxHeight)));
					worldGroup.addActorAt(0, background);
				}
			}
			
			worldGroup.addActor(shadowGroup);
			
			for (Actor shadow : shadows) {
				shadowGroup.addActor(shadow);
			}
			
			for (Actor tree : trees) {
				worldGroup.addActor(tree);
			}
			
			for (Actor rock : rocks) {
				worldGroup.addActor(rock);
			}		
			
			addWorldActors();
		}
	}
	
	private void addWorldActors() {
		for (GameWorldNode node : world) {
			for (Actor actor : node.getPaths()) {
				worldGroup.addActor(actor);
			}
		}
		
		for (final GameWorldNode actor : world) {
			worldGroup.addActor(actor);
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
		worldGroup.addActor(currentImage);
	}
	
	private int getTrueX(int x) {
		return (x - 16) * (scalingFactor + xFactor);
	}
	
	private int getTrueY(int x, int y) {
		return (y - 85) * scalingFactor + (x - 16) * scalingFactor / 2;
	}
	
	public TextureRegion getFullTexture(GroundType groundType, Texture groundSheet) {
		String key = groundType.toString();
		TextureRegion slice = groundSlices.get(key, new TextureRegion(groundSheet,  (groundType.ordinal() + 1) * (tileWidth) + 1, 0, tileWidth, tileHeight));
		groundSlices.put(key, slice);
		return slice;
	}

	public TextureRegion getTexture(GroundType groundType, Texture groundSheet, int mask) {
		String key = groundType.toString() + "-" + mask;
		TextureRegion slice = groundSlices.get(key, new TextureRegion(groundSheet, mask * (tileWidth) + 1, (groundType.ordinal()) * (tileHeight), tileWidth, tileHeight));
		groundSlices.put(key, slice);
		return slice;
	}
	
    @Override
    public void resize(int width, int height) {
    	super.resize(width, height);
        this.getViewport().update(width, height, false);
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
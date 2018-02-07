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
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Matrix4;
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
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.majalis.asset.AnimatedImage;
import com.majalis.asset.AssetEnum;
import com.majalis.character.PlayerCharacter;
import com.majalis.character.PlayerCharacter.QuestType;
import com.majalis.character.SexualExperience.SexualExperienceBuilder;
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
import com.majalis.world.GameWorld;
import com.majalis.world.GameWorld.Doodad;
import com.majalis.world.GameWorld.Shadow;
import com.majalis.world.GameWorld.SkewAction;
import com.majalis.world.GameWorldHelper;
import com.majalis.world.GameWorldNode;
import com.majalis.world.GroundType;
/*
 * The screen that displays the world map.  UI that Handles player input while on the world map - will delegate to other screens depending on the gameWorld state.
 */
public class WorldMapScreen extends AbstractScreen {
	// this class needs major refactoring - far too many dependencies, properties, statefulness
	private final AssetManager assetManager;
	private final SaveService saveService;
	private final GameWorld world;
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
	private final Group popupGroup;
	private final InputMultiplexer multi;
	private final AnimatedImage currentImage;
	private final AnimatedImage currentImageGhost;
	private final Skin skin;
	private final Texture hoverImageTexture;
	private final Image hoverImage;
	private final Label levelLabel;
	private final Label healthLabel;
	private final Label dateLabel;
	private final Label timeLabel;
	private final Label foodLabel;
	private final Label hoverLabel;
	private final TextButton campButton;
	private final boolean storyMode;
	private final float travelTime;
	private final Array<FrameBuffer> frameBuffers;
	private final SpriteBatch frameBufferBatch;
	
	private GameWorldNode currentNode;
	private GameWorldNode hoveredNode;
	private int time;
	private boolean backgroundRendered = false;
	private GameContext currentContext;
	
	public static final Array<AssetDescriptor<?>> resourceRequirements = new Array<AssetDescriptor<?>>();
	static {
		resourceRequirements.add(UI_SKIN.getSkin());
		resourceRequirements.add(WORLD_MAP_MUSIC.getMusic());
		AssetEnum[] soundAssets = new AssetEnum[]{
			CLICK_SOUND, EQUIP, SWORD_SLASH_SOUND, THWAPPING, CUM
		};
		for (AssetEnum asset: soundAssets) {
			resourceRequirements.add(asset.getSound());
		}
		
		// need to refactor to get all stance textures
		AssetEnum[] assets = new AssetEnum[]{
			EMBELLISHED_BUTTON_UP, EMBELLISHED_BUTTON_DOWN, EMBELLISHED_BUTTON_HIGHLIGHT,
			GROUND_SHEET, DOODADS, WORLD_MAP_BG, CHARACTER_ANIMATION, MOUNTAIN_ACTIVE, FOREST_ACTIVE, FOREST_INACTIVE, CASTLE, TOWN, COTTAGE, APPLE, MEAT, CLOUD, ROAD, WORLD_MAP_UI, WORLD_MAP_HOVER, ARROW, CHARACTER_SCREEN, EXP, GOLD, TIME, HEART, SEARCHING, NULL
		};
		for (AssetEnum asset: assets) {
			resourceRequirements.add(asset.getTexture());
		}
		resourceRequirements.addAll(CharacterScreen.resourceRequirements);
	}
	
	public WorldMapScreen(ScreenFactory factory, ScreenElements elements, AssetManager assetManager, SaveService saveService, LoadService loadService, GameWorld world) {
		super(factory, elements, AssetEnum.WORLD_MAP_MUSIC);
		this.assetManager = assetManager;
		this.saveService = saveService;
		this.travelTime = 1;
		this.frameBuffers = new Array<FrameBuffer>();
		this.frameBufferBatch = new SpriteBatch();
		
		this.storyMode = loadService.loadDataValue(SaveEnum.MODE, GameMode.class) == GameMode.STORY;
		uiStage = new Stage(new FitViewport(this.getViewport().getWorldWidth(), this.getViewport().getWorldHeight(), getCamera()), batch);
		uiStage.getCamera().update();
		
		dragStage = new Stage3D(new FitViewport(this.getViewport().getWorldWidth(), this.getViewport().getWorldHeight(), getCamera()), batch);
		
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
		hoverImage = new Image(hoverImageTexture);
		
		skin = assetManager.get(AssetEnum.UI_SKIN.getSkin());
		campButton = getButton(""); 
		
		levelLabel = new Label("", skin);		
		// these should be updated with emitters
		healthLabel = new Label("", skin);
		dateLabel = new Label("", skin);
		timeLabel = new Label("", skin);
		foodLabel = new Label("", skin);
		hoverLabel = new Label("", skin);
		
		for (final GameWorldNode actor : world.getNodes()) {
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
		currentImage.setPosition(initialTranslation.x + 646, initialTranslation.y + 390);
		
		currentImageGhost = new AnimatedImage(animation, Scaling.fit, Align.right);
		currentImageGhost.setScale(.7f);
		currentImageGhost.setState(0);
		currentImageGhost.getColor().a = .4f;
		
		// this is currently placing the character based on the camera in a way that conveniently places them on their current node - this needs to instead be aware of the current node and be able to grab its position from there (will need to know current node for behavior of Camp/Enter button regardless)
		currentImageGhost.setPosition(initialTranslation.x + 646, initialTranslation.y + 390);
		
		this.world = world;
		
		this.character = loadService.loadDataValue(SaveEnum.PLAYER, PlayerCharacter.class);
		this.cloudGroup = new Group();
		
		int leftWrap = -3000;
		int rightWrap = 10000;
		for (int ii = 0; ii < 200; ii++) {
			Actor actor = new Image(cloud);
			actor.setPosition((float)Math.random()*10000-1000, (float)Math.random()*10000-1000);
			actor.addAction(Actions.alpha(.3f));
			float speed = 10f;
			// move from starting position to leftWrap, then warp to rightWrap, then repeat those two actions forever
			actor.addAction(sequence(moveTo(leftWrap, actor.getY(), (actor.getX() - leftWrap) / speed), moveTo(rightWrap, actor.getY()), repeat(RepeatAction.FOREVER, sequence(moveTo(leftWrap, actor.getY(), rightWrap - leftWrap / speed), moveTo(rightWrap, actor.getY())))));
			cloudGroup.addActor(actor);
		}
		
		cloudStage.addActor(cloudGroup);

		this.popupGroup = new Group();
		
		multi = new InputMultiplexer();
		multi.addProcessor(uiStage);
		multi.addProcessor(this);
		multi.addProcessor(dragStage);
	}
	
	private TextButton getButton(String label) {
		TextButtonStyle buttonStyle = new TextButtonStyle();
		buttonStyle.up = new TextureRegionDrawable(new TextureRegion(assetManager.get(AssetEnum.EMBELLISHED_BUTTON_UP.getTexture())));
		buttonStyle.down = new TextureRegionDrawable(new TextureRegion(assetManager.get(AssetEnum.EMBELLISHED_BUTTON_DOWN.getTexture())));
		buttonStyle.over = new TextureRegionDrawable(new TextureRegion(assetManager.get(AssetEnum.EMBELLISHED_BUTTON_HIGHLIGHT.getTexture())));	
		buttonStyle.font = skin.getFont("default-font");
		buttonStyle.fontColor = Color.BLACK;
		TextButton button = new TextButton(label, buttonStyle);
		return button;
	}
	
	private void mutateLabels() {
		levelLabel.setText("Level: " + character.getLevel());
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
		uiGroup.addActor(popupGroup);
		uiGroup.addActor(hoverImage);
		hoverImage.setVisible(false);
		hoverImage.setBounds(1500, 5, 400, 300);
		
		addLabel(uiGroup, levelLabel, 190, 190, Color.LIGHT_GRAY);
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
		
		Array<Button> buttons = new Array<Button>();
		
		final Sound buttonSound = assetManager.get(AssetEnum.CLICK_SOUND.getSound()); 
		int storedLevels = character.getStoredLevels();
		
		Image characterUI = new Image(characterUITexture);
		uiStage.addActor(characterUI);
		characterUI.setScale(1.1f);
		
		TextButton characterButton = getButton(storedLevels > 0 ? "Level Up!" : "Character");
		buttons.add(characterButton);
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
		
		TextButton inventoryButton = getButton("Inventory");
		buttons.add(inventoryButton);
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
		
		actionTable.add(inventoryButton).size(200, 50);
		
		Image foodIcon = new Image(food);
		foodIcon.setSize(75, 75);
		uiStage.addActor(foodIcon);
		
		final Label console = new Label("", skin);
		uiStage.addActor(console);
		console.setPosition(1250, 80);
		console.setWrap(true);
		console.setWidth(600);
		console.setColor(Color.CHARTREUSE);
		
		final TextButton rest = getButton("Rest");
		buttons.add(rest);
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
					tintForTimeOfDay(time, .5f);
					checkCanEat(rest);
					mutateLabels();
		        }
			}
		);

		final Image scoutEye = new Image(assetManager.get(AssetEnum.SEARCHING.getTexture()));
		uiStage.addActor(scoutEye);
		scoutEye.setPosition(400, 25);
		scoutEye.setSize(40, 40);
		final UpdateLabel scoutLevel = new UpdateLabel(skin, character);
		uiStage.addActor(scoutLevel);
		scoutLevel.setPosition(450, 25);
		
		final TextButton scout = getButton("Scout");
		buttons.add(scout);
		table.add(scout).size(145, 40).row();

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
						currentNode.setAsCurrentNode();
						visit(currentNode);
						time++;
						tintForTimeOfDay(time, .5f);	
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
					showScreen(ScreenEnum.CONTINUE);
		        }
			}
		);
		
		TextButton questButton = getButton("Quest Log");
		buttons.add(questButton);
		actionTable.add(questButton).size(200, 50).row();
		questButton.addListener(
			new ClickListener() {
				@Override
		        public void clicked(InputEvent event, float x, float y) {
					buttonSound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
					showScreen(ScreenEnum.QUEST);
		        }
			}
		);	
		
		TextButton saveButton = getButton("QuickSave");
		buttons.add(saveButton);
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
		
		final TextButton quickLoadButton = getButton("QuickLoad");
		buttons.add(quickLoadButton);
		actionTable.add(quickLoadButton).size(200, 50);
		quickLoadButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
        		saveService.newSave(".toa-data/quicksave.json");
        		buttonSound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
				showScreen(ScreenEnum.LOAD_GAME);
			}
		});
		
		
		TextButton hardSaveButton = getButton("Save");
		buttons.add(hardSaveButton);
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
		
		if (!backgroundRendered) {
			generateBackground();
		}
		tintForTimeOfDay(time, 0);
		
		uiStage.addActor(uiGroup);
		buttons.add(campButton);
		Action disableButtons = new Action() {
			@Override
			public boolean act(float delta) {
				for (Button button : buttons) {
					button.setTouchable(Touchable.disabled);
				}
				return false;
		}};
		Action enableButtons = new Action() {
			@Override
			public boolean act(float delta) {
				for (Button button : buttons) {
					button.setTouchable(Touchable.enabled);
				}
				return false;
		}};
		
		// this needs refactoring - probably replace ChangeListener with a custom listener/event type
		worldGroup.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if (event.getTarget() instanceof GameWorldNode) {
					final GameWorldNode clickedNode = (GameWorldNode) event.getTarget();
					Array<GameWorldNode> pathToCurrent = clickedNode.getPathToCurrent(); // last element of this is the current node
					if (pathToCurrent.size == 0) return;
					worldGroup.addAction(disableButtons);
					buttonSound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
					pathToCurrent.reverse(); // order it from current node to clicked node
					pathToCurrent.removeIndex(0); // remove current node
					pathToCurrent.add(clickedNode); // add clicked node
					moveToNode(0, pathToCurrent, uiGroup, enableButtons);
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
	
	private void moveToNode(int nodeToMove, Array<GameWorldNode> pathToCurrent, Group uiGroup, Action enableButtons) {
		GameWorldNode node = pathToCurrent.get(nodeToMove);
		if(!inSuspendedArea(currentNode) && checkForForcedRest());
		else if (!inSuspendedArea(currentNode) && character.getCurrentDebt() >= 150 || (character.getCurrentDebt() >= 100 && character.getQuestStatus(QuestType.DEBT) < 1)) {
			autoEncounter(uiGroup, EncounterCode.BUNNY);
		}
		else if (!inSuspendedArea(currentNode) && time >= 11 && character.getQuestStatus(QuestType.ELF) == 0) { // forced elf encounter
			saveService.saveDataValue(SaveEnum.QUEST, new QuestFlag(QuestType.ELF, 1));	
			autoEncounter(uiGroup, EncounterCode.ELF);
		}
		else if (!inSuspendedArea(currentNode) && time >= 23 && character.getQuestStatus(QuestType.TRUDY) == 0) { // forced Trudy encounter
			autoEncounter(uiGroup, EncounterCode.ADVENTURER);
		}
		else {
			tintForTimeOfDay(time + 1, travelTime);
			Vector2 finish = node.getHexPosition();
			Vector2 start = new Vector2(currentNode.getHexPosition());
			Array<Action> moveActions = new Array<Action>();
			Array<Action> moveActionsGhost = new Array<Action>();
			int distance = GameWorldHelper.distance((int)start.x, (int)start.y, (int)finish.x, (int)finish.y);
			int totalDistance = distance;
			// can eventually walk along a "path" that is designated by the connections between nodes - eventually the world map screen won't need a getTrueX() function, as that would be calculated elsewhere			
			while (distance > 0) {
				if (start.x + start.y == finish.x + finish.y) { // z is constant
					if (start.x < finish.x) { // downright
						start.x++;
						start.y--;
					}
					else { // upleft
						start.x--;
						start.y++;
					}
				}
				else if (start.y == finish.y) { // y is constant
					if (start.x < finish.x) start.x++; // upright
					else start.x--; // downleft
				}
				else if (start.x == finish.x) { // x is constant
					if (start.y < finish.y) start.y++; // up
					else start.y--; // down
				}
				else {
					int startZ = (int) (0 - (start.x + start.y));
					int finishZ = (int) (0 - (finish.x + finish.y));
					if (start.x > finish.x && startZ < finishZ) {
						start.x--;
					}
					else if (finish.y > start.y && startZ > finishZ) {
						start.y++;
					}
					else {
						start.x++;
						start.y--;
					}			
				}
				moveActions.add(moveTo(getTrueX((int)start.x) + 8, getTrueY((int)start.x, (int)start.y) + 27, travelTime/totalDistance));
				moveActionsGhost.add(moveTo(getTrueX((int)start.x) + 8, getTrueY((int)start.x, (int)start.y) + 27, travelTime/totalDistance));
				distance = GameWorldHelper.distance((int)start.x, (int)start.y, (int)finish.x, (int)finish.y);
			}
			moveActions.add(new Action() { // once the character arrives
				@Override
				public boolean act(float delta) {
					final int timePassed = 1;
					saveService.saveDataValue(SaveEnum.TIME, timePassed);
					time += timePassed;
					EncounterCode newEncounter = node.getEncounterCode();
					EncounterBounty miniEncounter = newEncounter.getMiniEncounter();
					if(newEncounter == EncounterCode.DEFAULT) {
						// this will need to also check if the node is a town/dungeon node and appropriately swap the button from "Camp" to "Enter"
						saveService.saveDataValue(SaveEnum.SCOUT, 0);
						visit(node);
					}
					else if (miniEncounter != null) {
						final Image displayNewEncounter = new Image(hoverImageTexture);
						displayNewEncounter.setBounds(100, 250, 500, 600);
						popupGroup.clear();
						popupGroup.addAction(Actions.show());
						popupGroup.addAction(Actions.alpha(1));
						popupGroup.addActor(displayNewEncounter);
						EncounterBountyResult result = miniEncounter.execute(character.getScoutingScore(), saveService);
						final Label newEncounterText = new Label(result.displayText(), skin);
						
						if (result.soundToPlay() != null) {
							assetManager.get(result.soundToPlay()).play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f); 
						}
																							
						newEncounterText.setColor(Color.GOLDENROD);

						final Table statusResults = new Table();
						statusResults.setPosition(200, 725);
						newEncounterText.setWrap(true);
						statusResults.align(Align.topLeft);
						statusResults.add(newEncounterText).width(315).row();
						Array<MutationResult> compactedResults = MutationResult.collapse(result.getResults()); 
						for (MutationResult miniResult : compactedResults) {
							MutationActor actor = new MutationActor(miniResult, assetManager.get(miniResult.getTexture()), skin, true);
							actor.setWrap(true);
							// this width setting is going to be tricky once we implement images for perk and skill gains and such
							statusResults.add(actor).width(miniResult.getType() == MutationType.NONE ? 325 : 50).height(50).align(Align.left).row();
						}
						popupGroup.addActor(statusResults); 	
						Action doneAction = sequence(
								delay(1), 
								new Action(){ @Override
									public boolean act(float delta) {
										checkForForcedRest();
										return true;
								}}, 
								Actions.fadeOut(4),
								delay(4), 
								new Action() {
									@Override
									public boolean act(float delta) {
										popupGroup.clearChildren();
										popupGroup.addAction(Actions.hide());
										return true;
								}
							}
						);
					
						if (newEncounter == EncounterCode.SOLICITATION) {
							final Table tempTable = new Table();
							final TextButton yesButton = getButton("Yes");
							final TextButton noButton = getButton("No");
							final ClickListener buttonListener = new ClickListener() { 
								@Override
						        public void clicked(InputEvent event, float x, float y) {
									popupGroup.addAction(doneAction);
									tempTable.removeActor(yesButton);
									tempTable.removeActor(noButton);
									assetManager.get(AssetEnum.CLICK_SOUND.getSound()).play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f); // this should only play if you say yes - a simple boop for no
									visit(node);
									saveService.saveDataValue(SaveEnum.SCOUT, 0);
									node.setAsCurrentNode();
									setCurrentNode(node);
									worldGroup.addAction(enableButtons);
								}
							};
							yesButton.addListener(new ClickListener() { 
								@Override
						        public void clicked(InputEvent event, float x, float y) {
									buttonListener.clicked(null, 0, 0);
									assetManager.get(AssetEnum.EQUIP.getSound()).play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f); 
									assetManager.get(AssetEnum.THWAPPING.getSound()).play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f); 
									assetManager.get(AssetEnum.CUM.getSound()).play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f); 
									statusResults.clear();
									Label newEncounterText = new Label("You accept a whomping for 10 gold!", skin);
									newEncounterText.setColor(Color.GOLDENROD);
									newEncounterText.setWrap(true);
									statusResults.add(newEncounterText).width(315).row();
									saveService.saveDataValue(SaveEnum.GOLD, 10);
									// this is duplicated code, extract into method
									for (MutationResult miniResult : saveService.saveDataValue(SaveEnum.ANAL, new SexualExperienceBuilder().setAnalSex(1).setCreampie(1).build())) {
										MutationActor actor = new MutationActor(miniResult, assetManager.get(miniResult.getTexture()), skin, true);
										actor.setWrap(true);
										// this width setting is going to be tricky once we implement images for perk and skill gains and such
										statusResults.add(actor).width(miniResult.getType() == MutationType.NONE ? 325 : 50).height(50).align(Align.left).row();
									}
								}
							}); 
							noButton.addListener(buttonListener);
							tempTable.add(yesButton).size(100, 50);
							tempTable.add(noButton).size(100, 50);
							statusResults.add(tempTable);
							currentImage.clearActions();
						}
						else {								
							popupGroup.addAction(doneAction);
							visit(node);
							saveService.saveDataValue(SaveEnum.SCOUT, 0);
						}
					}
					else {
						saveService.saveDataValue(SaveEnum.ENCOUNTER_CODE, newEncounter); 
						visit(node);
						saveService.saveDataValue(SaveEnum.CONTEXT, node.getEncounterContext());
						if (node.getEncounterContext() == GameContext.TOWN) {
							saveService.saveDataValue(SaveEnum.TOWN, node.getEncounterCode().getTownCode());
						}
						saveService.saveDataValue(SaveEnum.RETURN_CONTEXT, GameContext.WORLD_MAP);
						saveService.saveDataValue(SaveEnum.NODE_CODE, node.getNodeCode());
						switchContext();
					}
					saveService.saveDataValue(SaveEnum.NODE_CODE, node.getNodeCode());
					mutateLabels();
					return true;
				}
			});
			if (nodeToMove + 1 >= pathToCurrent.size) { // we've reached the target node
				moveActions.add(new Action() {
					@Override
					public boolean act(float delta) {
						node.setAsCurrentNode();
						setCurrentNode(node);
						worldGroup.addAction(enableButtons);
						return true;
					}	
				});
			}
			else {
				moveActions.add(new Action() {
					@Override
					public boolean act(float delta) {
						moveToNode(nodeToMove + 1, pathToCurrent, uiGroup, enableButtons);
						return true;
					}	
				});
			}
			Action[] allActionArray = moveActions.toArray(Action.class);
			Action[] allActionsGhostArray = moveActionsGhost.toArray(Action.class);
			currentImage.addAction(sequence(allActionArray));
			currentImageGhost.addAction(sequence(allActionsGhostArray));
			setCurrentNode(node);
		}
	}
	
	private void visit(GameWorldNode node) {
		node.visit();
		saveService.saveDataValue(SaveEnum.VISITED_LIST, node.getVisitInfo());
	}
	
	private class UpdateLabel extends Label {
		private UpdateLabel(Skin skin, PlayerCharacter character) {
			super("X " + character.getScoutingScore(), skin);
			if (character.getScoutingScore() > 4) this.addAction(Actions.color(Color.GOLD));
		}
		@Override
		public void draw(Batch batch, float parentAlpha) {
			if (!getText().toString().equals("X " + character.getScoutingScore())) {
				setText("X " + character.getScoutingScore());
				this.clearActions();
				this.addAction(character.getScoutingScore() > 4 ? Actions.color(Color.GOLD) : Actions.sequence(Actions.color(Color.GREEN), Actions.color(Color.WHITE, 1)));
			}
			super.draw(batch, parentAlpha);
		}
	}

	private boolean inSuspendedArea(GameWorldNode node) {
		return node.getEncounterCode() == EncounterCode.MOUTH_FIEND || node.getEncounterCode() == EncounterCode.MOUTH_FIEND_ESCAPE;
	}
	
	private void setConsole(Label console, @SuppressWarnings("unchecked") Array<MutationResult> ... allResults) {
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
		character.popPortraitPath();
		showScreen(ScreenEnum.CONTINUE);
	}
	
	private void autoEncounter(Group uiGroup, EncounterCode encounter) {
		saveService.saveDataValue(SaveEnum.ENCOUNTER_CODE, encounter);	
		saveService.saveDataValue(SaveEnum.CONTEXT, GameContext.ENCOUNTER);
		saveService.saveDataValue(SaveEnum.RETURN_CONTEXT, GameContext.WORLD_MAP);
		Image displayNewEncounter = new Image(hoverImageTexture);
		displayNewEncounter.setBounds(775, 400, 500, 400);
		uiGroup.addActor(displayNewEncounter);
		Label newEncounterText = new Label("Encounter!", skin);
		newEncounterText.setColor(Color.GOLDENROD);
		newEncounterText.setPosition(955, 585);
		uiGroup.addActor(newEncounterText);
		uiGroup.addAction(sequence(delay(travelTime), new Action() {
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
		
		if (Gdx.input.isKeyJustPressed(Keys.ENTER)) {
			showScreen(ScreenEnum.CHARACTER);
		}			
		else if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
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
	// this could also check the diff between current time and target time, and make this a sequence of actions divided by the duration to cycle through, rather than transition, for instance, from evening to morning without first traversing night
	private void tintForTimeOfDay(int targetTime, float duration) {
		TimeOfDay timeOfDay = TimeOfDay.getTime(targetTime);
		
		for (Actor actor : worldGroup.getChildren()) {
			actor.addAction(Actions.color(getTimeColor(), duration));
		}
		for (Actor actor : shadowGroup.getChildren()) {
			Shadow shadow = (Shadow) actor;
			shadow.addAction(Actions.color(timeOfDay.getShadowColor(), duration));
			shadow.addAction(new SkewAction(new Vector2(timeOfDay.getShadowDirection(), timeOfDay.getShadowLength()), duration));
		}
	}
	
	private Color getTimeColor() { return TimeOfDay.getTime(time).getColor(); }
	private String getTime() { return TimeOfDay.getTime(time).getDisplay(); }
	
	private void translateCamera() {
		Vector3 translationVector = new Vector3(0, 0, 0);
		int speed = 8;
		if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)) speed = 16;
		
		if (Gdx.input.isKeyPressed(Keys.LEFT) && !Gdx.input.isKeyPressed(Keys.RIGHT) && camera.position.x > 500) {
			translationVector.x -= speed;
		}
		if (Gdx.input.isKeyPressed(Keys.RIGHT) && !Gdx.input.isKeyPressed(Keys.LEFT) && camera.position.x < (storyMode ? 1000 : 4000)) {
			translationVector.x += speed;
		}
		if (Gdx.input.isKeyPressed(Keys.DOWN) && !Gdx.input.isKeyPressed(Keys.UP) && camera.position.y > 500) {
			translationVector.y -= speed;
		}
		if (Gdx.input.isKeyPressed(Keys.UP) && !Gdx.input.isKeyPressed(Keys.DOWN) && camera.position.y < (storyMode ? 1000 : 4600)) {
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
	private final static int tileWidth = GameWorldHelper.getTileWidth();
	private final static int tileHeight = GameWorldHelper.getTileHeight();
	
	private void generateBackground() {
		backgroundRendered = true;
		if (storyMode) {
			Image background = new Image(assetManager.get(WORLD_MAP_BG.getTexture()));
			background.setSize(2332, 1633);
			background.setPosition(-400, 0);
			worldGroup.addActorAt(0, background);
			addWorldActors();
		}
		else {
			Array<Array<GroundType>> ground = world.getGround();
			Array<AnimatedImage> lilies = world.getLilies();
			Array<Image> reflections = world.getReflections();
			Array<Shadow> shadows = world.getShadows();
			Array<Doodad> doodads = world.getDoodads();
			
			Texture groundSheet = assetManager.get(AssetEnum.GROUND_SHEET.getTexture());	
			
			// draw (add drawings as actors) ground layer
			drawLayer(ground, groundSheet, false);
						
			// draw (add reflections as actors) reflections
			for (AnimatedImage lily :lilies) {
				worldGroup.addActorAt(0, lily);
			}
			
			for (Image reflection : reflections) {
				worldGroup.addActorAt(0, reflection);
			}
		
			// draw (add drawings as actors) water layer
			drawLayer(ground, groundSheet, true);
							
			worldGroup.addActor(shadowGroup);	
			
			addWorldActors();
			
			for (Shadow shadow : shadows) {
				shadowGroup.addActor(shadow);
			}
			
			for (Doodad doodad : doodads) {
				worldGroup.addActor(doodad);
			}	
			Group tempGroup = new Group();
			tempGroup.addActor(currentImageGhost);
			worldGroup.addActor(tempGroup);
		}
		frameBufferBatch.dispose();
	}
		
	private static int maxTextureSize = getMaxTextureSize();
	private static int getMaxTextureSize() {
		int textureSize = 128;
		while (textureSize <= GL20.GL_MAX_TEXTURE_SIZE) {
			textureSize *= 2;
		}
		if (textureSize > GL20.GL_MAX_TEXTURE_SIZE) textureSize /= 2;
		return textureSize;
	}
	
	private void drawLayer(Array<Array<GroundType>> ground, Texture groundSheet, boolean waterLayer) {
		int[] layers = new int [GroundType.values().length];
		int boxWidth = maxTextureSize;
		int boxHeight = maxTextureSize;
		int xScreenBuffer = 683;
		int yScreenBuffer = 165;
		Matrix4 matrix = new Matrix4();
		matrix.setToOrtho2D(0, 0, boxWidth, boxHeight); 
		frameBufferBatch.setProjectionMatrix(matrix);
		int xSize = 5888 / boxWidth + 1;
		int ySize = 5632 / boxHeight + 1;
		
		for (int xTile = 0; xTile < xSize; xTile++) {
			for (int yTile = 0; yTile < ySize; yTile++) {
				FrameBuffer frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, boxWidth, boxHeight, false); // this and matrix need to preserve a ratio
				frameBuffers.add(frameBuffer);
				frameBuffer.begin();
				Gdx.gl.glClearColor(.2f, .2f, .4f, 0);
				Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
				frameBufferBatch.begin();
				
				for (int x = 0; x < ground.size; x++) {
					int trueX = getTrueX(x) - (boxWidth) * xTile + xScreenBuffer;
					if (trueX < -60 || trueX > boxWidth) continue;
					Array<GroundType> left = x - 1 >= 0 ? ground.get(x - 1) : null;
					Array<GroundType> middle = ground.get(x);
					Array<GroundType> right = x + 1 < ground.size ? ground.get(x + 1) : null;
					int layerSize = middle.size;
					for (int y = 0; y < middle.size; y++) {
						int trueY = getTrueY(x, y) - (boxHeight) * yTile + yScreenBuffer;
						if (trueY < -60 || trueY > boxHeight) continue;
						for (int i = 0; i < layers.length; i++) {
							layers[i] = 0;
						}
						
						GroundType currentHexType = middle.get(y);
						// check the six adjacent tiles and add accordingly
						if (right != null) {
							layers[right.get(y).ordinal()] += 1;
						}
						if (y - 1 >= 0)	{
							if (right != null) {		
								layers[right.get(y - 1).ordinal()] += 2;
								
							}
							layers[middle.get(y - 1).ordinal()] += 4;
						}
						if (left != null)	{
							layers[left.get(y).ordinal()] += 8;
						}
						if (y + 1 < layerSize)	{
							if (left != null) {
								layers[left.get(y + 1).ordinal()] += 16;
							}		
							layers[middle.get(y + 1).ordinal()] += 32;
						}
						if (waterLayer) {
							if (currentHexType == GroundType.WATER) {
								frameBufferBatch.draw(getFullTexture(GroundType.WATER, groundSheet), trueX, trueY); // with appropriate type
								//frameBufferBatch.draw(getTexture(GroundType.WATER, groundSheet, layers[GroundType.WATER.ordinal()]), trueX, trueY); // appropriate blend layer
							}
						}
						else {
							for (GroundType groundType: GroundType.values()) {
								if (currentHexType == groundType) {
									if ( groundType != GroundType.WATER) {
										frameBufferBatch.draw(getFullTexture(groundType, groundSheet), trueX, trueY); // with appropriate type
									}
								}
								else {
									frameBufferBatch.draw(getBlendTexture(groundType, groundSheet, layers[groundType.ordinal()]), trueX, trueY); // appropriate blend layer
								}
								
							}
						}
					}
				}
				frameBufferBatch.end();
				frameBuffer.end();
				
				Image background = new Image(new TextureRegion(frameBuffer.getColorBufferTexture(), 0, boxHeight, boxWidth, -boxHeight));
				background.addAction(Actions.moveTo(-xScreenBuffer + xTile * boxWidth, -yScreenBuffer + yTile * (boxHeight)));
				worldGroup.addActorAt(0, background);
			}
		}
	}
	
	private void addWorldActors() {
		Group roads = new Group();
		worldGroup.addActor(roads);
		for (GameWorldNode node : world.getNodes()) {
			for (Actor actor : node.getPaths()) {
				roads.addActor(actor);
			}
		}
		
		for (final GameWorldNode actor : world.getNodes()) {
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
		return GameWorldHelper.getTrueX(x);
	}
	
	private int getTrueY(int x, int y) {
		return GameWorldHelper.getTrueY(x, y);
	}
	
	public TextureRegion getFullTexture(GroundType groundType, Texture groundSheet) {
		String key = groundType.toString();
		TextureRegion slice = groundSlices.get(key, new TextureRegion(groundSheet,  (groundType.ordinal() + 1) * (tileWidth) + 1, 0, tileWidth, tileHeight));
		groundSlices.put(key, slice);
		return slice;
	}

	public TextureRegion getBlendTexture(GroundType groundType, Texture groundSheet, int mask) {
		String key = groundType.toString() + "-" + mask;
		TextureRegion slice = groundSlices.get(key, new TextureRegion(groundSheet, (mask % 32) * (tileWidth) + 1, (((groundType.ordinal() - 1) * 2 + 1) + (mask > 31 ? 1 : 0)) * (tileHeight), tileWidth, tileHeight));
		groundSlices.put(key, slice);
		return slice;
	}
	
	@Override
    public void show() {
        Gdx.input.setInputProcessor(multi);
        font.setUseIntegerPositions(false);
    }
	
    @Override
    public void resize(int width, int height) {
    	super.resize(width, height);
        uiStage.getViewport().update(width, height, false);
        cloudStage.getViewport().update(width, height, false);
    }
	
	@Override
	public void dispose() {
		for(AssetDescriptor<?> path: resourceRequirements) {
			if (path.fileName.equals(AssetEnum.CLICK_SOUND.getSound().fileName) || path.type == Music.class) continue;
			assetManager.unload(path.fileName);
		}
		for (FrameBuffer frameBuffer : frameBuffers) {
			frameBuffer.dispose();
		}
	}	
}
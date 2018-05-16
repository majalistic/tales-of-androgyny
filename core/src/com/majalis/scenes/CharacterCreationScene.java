package com.majalis.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.OrderedMap;
import com.majalis.asset.AssetEnum;
import com.majalis.character.PlayerCharacter;
import com.majalis.character.AbstractCharacter.Stat;
import com.majalis.encounter.Background;
import com.majalis.encounter.EncounterHUD;
import com.majalis.save.SaveEnum;
import com.majalis.save.SaveManager;
import com.majalis.save.SaveService;
import com.majalis.save.SaveManager.JobClass;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class CharacterCreationScene extends Scene {

	private final SaveService saveService;
	private final Label statPointDisplay;
	private final AssetManager assetManager;
	private final Table statTable;
	private final Skin skin;
	private final Sound buttonSound, gemSound, classSelectSound; 
	private final TextButton done;
	private final PlayerCharacter character;
	private final Label classMessage, classSelection, statDescription, statMessage, helpText;
	private final Texture baubleOld, baubleNew, baubleEmpty;	
	private final Array<TextButton> classButtons;
	private final boolean story;
	private final Group hideGroup;
	private final Image characterImage;
	private ObjectMap<Stat, Integer> statMap;
	private TextButton enchanterButton;
	private int statPoints;
	private int selection;
	
	public CharacterCreationScene(OrderedMap<Integer, Scene> sceneBranches, int sceneCode, final SaveService saveService, Background background, final AssetManager assetManager, final PlayerCharacter character, final boolean story, EncounterHUD hud) {
		super(sceneBranches, sceneCode, hud);
		this.saveService = saveService;
		this.character = character;
		this.story = story;
		this.addActor(background);
		
		Group uiGroup = new Group();
		this.addActor(uiGroup);
		
		hideGroup = new Group();
		uiGroup.addActor(hideGroup);
		
		addImage(uiGroup, assetManager.get(AssetEnum.CLASS_SELECT_PANEL.getTexture()));
		addImage(uiGroup, assetManager.get(AssetEnum.CLASS_SELECT_SUBTLE_BORDER.getTexture()), 0, 938);
		addImage(uiGroup, assetManager.get(AssetEnum.CLASS_SELECT_LABEL.getTexture()), 0, 950);
		addImage(uiGroup, assetManager.get(AssetEnum.CLASS_SELECT_CHARACTER_BOX.getTexture()), 1470, 46);
		addImage(hideGroup, assetManager.get(AssetEnum.CLASS_SELECT_STAT_PANEL_FOLDOUT.getTexture()), 392, 313);
		addImage(hideGroup, assetManager.get(AssetEnum.CLASS_SELECT_TOOLTIP_SLIDEOUT.getTexture()), 742, 265);
		addImage(hideGroup, assetManager.get(AssetEnum.CLASS_SELECT_STAT_BOX.getTexture()), 685, 315);
		addImage(hideGroup, assetManager.get(AssetEnum.CLASS_SELECT_TOOLTIP_BOX.getTexture()), 600, 25);
		
		hideGroup.addAction(Actions.hide());
		
		/*
		CLASS_SELECT_FOREGROUND
		CLASS_SELECT_LEAF_BORDER
		CLASS_SELECT_HANGING_LEAVES
		CLASS_SELECT_TOP_BORDER
		*/
	
		this.assetManager = assetManager;
		
		this.skin = assetManager.get(AssetEnum.UI_SKIN.getSkin());
		this.buttonSound = assetManager.get(AssetEnum.BUTTON_SOUND.getSound());
		this.gemSound = assetManager.get(AssetEnum.GEM_CLINK.getSound());
		this.classSelectSound = assetManager.get(AssetEnum.QUICK_PAGE_TURN.getSound());
		this.baubleEmpty = assetManager.get(AssetEnum.CREATION_BAUBLE_EMPTY.getTexture());
		this.baubleOld = assetManager.get(AssetEnum.CREATION_BAUBLE_OLD.getTexture());
		this.baubleNew = assetManager.get(AssetEnum.CREATION_BAUBLE_NEW.getTexture());
		this.helpText = initLabel("Please Select a Class!", skin, Color.FOREST, 300, 800, Align.left);
		
		characterImage = new Image(); 
		characterImage.setPosition(1390, 230);
		this.addActor(characterImage);
		
		this.done = new TextButton("Done", skin);
		
		done.addListener(new ClickListener() { @Override public void clicked(InputEvent event, float x, float y) { 
			buttonSound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f); nextScene();		   
		}});
		done.setPosition(1552, 10);

		int statX = 675;
		int statY = 165;

		statPointDisplay = initLabel(String.valueOf(statPoints), skin, Color.BLACK, 1310, 888, Align.left);		
		statPointDisplay.addAction(Actions.hide());
		this.classMessage = initLabel("", skin, Color.BLACK, 1710, 235, Align.top);
		this.statMessage = initLabel("", skin, Color.RED, statX, statY, Align.left, true, 740);
		this.statDescription = initLabel("", skin, Color.FOREST, statX, statY, Align.left, true, 740);
		this.classSelection = initLabel("", skin, Color.GOLD, 1726, 970, Align.center);
		
		resetStatPoints(story, character);
		statMap = resetObjectMap();
		
		this.statTable = new Table();
		statTable.align(Align.topLeft);
		statTable.setPosition(735, 885);
		
		Table table = new Table();
		table.setPosition(412, 450);
		this.addActor(table);	
		
		TextButtonStyle buttonStyle = new TextButtonStyle(new TextureRegionDrawable(new TextureRegion(assetManager.get(AssetEnum.CREATION_BUTTON_UP.getTexture()))),  new TextureRegionDrawable(new TextureRegion(assetManager.get(AssetEnum.CREATION_BUTTON_DOWN.getTexture()))),  new TextureRegionDrawable(new TextureRegion(assetManager.get(AssetEnum.CREATION_BUTTON_CHECKED.getTexture()))), skin.getFont("default-font"));
		buttonStyle.over = new TextureRegionDrawable(new TextureRegion(assetManager.get(AssetEnum.CREATION_BUTTON_CHECKED.getTexture())));
		buttonStyle.fontColor = Color.BLACK;
		
		classButtons = new Array<TextButton>();
		boolean advancedClassesUnlocked = true; // should be dependent on some achievement
		for (final JobClass jobClass: JobClass.values()) {
			final TextButton button = new TextButton(jobClass.getLabel(), buttonStyle);
			classButtons.add(button);
			
			boolean enabled = (story && jobClass == JobClass.ENCHANTRESS) || (!story && (jobClass.ordinal() % 2 == 0 || advancedClassesUnlocked));
			if (!enabled) {
				button.setTouchable(Touchable.disabled);
				TextButtonStyle style = new TextButtonStyle(button.getStyle());
				style.fontColor = Color.RED;
				style.up = style.disabled;
				button.setStyle(style);
			}
			else {
				button.addListener(new ClickListener() {
					@Override
			        public void clicked(InputEvent event, float x, float y) {
						selectClass(button, jobClass);
			        }
					
					@Override
			        public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
						statDescription.setText(jobClass.getDescription());
						statDescription.addAction(Actions.show());
						statMessage.addAction(Actions.hide());
					}
					@Override
			        public void exit(InputEvent event, float x, float y, int pointer, Actor fromActor) {
						statDescription.setText("");
						statDescription.addAction(Actions.hide());
						statMessage.addAction(Actions.show());
					}
				});
			}
			table.add(button).size(350, 105).row();
			if (story && jobClass == JobClass.ENCHANTRESS) {
				enchanterButton = button;
			}
		}
	}
	
	private void selectClass(TextButton button, JobClass jobClass) {
		for (TextButton listButton : classButtons) {
			listButton.setChecked(false);
		}
		button.setChecked(true);
		if (!story) {
			classSelectSound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
		}
		helpText.setPosition(1000, 882);
		helpText.setText("Allocate Stat Points!");
		statPointDisplay.addAction(Actions.show());
		hideGroup.addAction(Actions.show());
		Texture jobTexture = assetManager.get(jobClass.getTexture());
		characterImage.setDrawable(new TextureRegionDrawable(new TextureRegion(jobTexture)));
		characterImage.setSize(jobTexture.getWidth(), jobTexture.getHeight());
		characterImage.setScale(730f/1080);
		classSelection.setText(jobClass.getLabel());
		classMessage.setText(getClassFeatures(jobClass));
		saveService.saveDataValue(SaveEnum.CLASS, jobClass);
		if (statPoints == 0) {
			removeActor(done);
		}
		resetStatPoints(story, character);
		statMap = resetObjectMap();
		initStatTable();
		addActor(statTable);
	}
	
	private void initStatTable() {
		statTable.clear();
		final ObjectMap<Stat, Label> statToLabel = new ObjectMap<Stat, Label>();
		for (final Stat stat: Stat.values()) {
			Image statImage = new Image(assetManager.get(stat.getAsset()));
			statImage.addListener(getStatListener(stat, statDescription, statMessage));
			
			final Label statLabel = new Label("", skin);
			statToLabel.put(stat, statLabel);
			int amount = character.getBaseStat(stat);
			setStatAppearance(statLabel, amount, stat, character);
			
			statTable.add(statImage).size(statImage.getWidth() / (statImage.getHeight() / 35), 35).padBottom(-16).align(Align.left).row();
			
			int currentStatAllocation = statMap.get(stat);
			
			Table miniTable = new Table();
			
			Button minus = getMinusButton();
			if (!canDecreaseStat(stat)) minus.setColor(Color.GRAY);
			minus.addListener(new ClickListener(){
				@Override
		        public void clicked(InputEvent event, float x, float y) {
					if (canDecreaseStat(stat)) {
						gemSound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
					}
					decreaseStat(stat, character, statMessage, statLabel, done);
					initStatTable();
		        }				
			});
			miniTable.add().width(40);
			miniTable.add(minus).width(minus.getWidth()).padRight(minus.getWidth());
			
			int size = 30;		
			int numberOfBaubles = 0;
			for ( ; numberOfBaubles < amount - Math.max(0, currentStatAllocation + (noStatAtNegative() ? 1 : 0)); numberOfBaubles++) {
				final int difference = amount - numberOfBaubles;
				miniTable.add(getBauble(baubleOld, new ClickListener(){
					@Override
			        public void clicked(InputEvent event, float x, float y) {
						gemSound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
						for (int ii = 0; ii < difference; ii++) {
							decreaseStat(stat, character, statMessage, statLabel, done);
						}
						if (difference > 0) initStatTable();
			        }
				})).size(size, size).align(Align.left);
			}

			for ( ; numberOfBaubles < amount; numberOfBaubles++) {
				final int difference = amount - numberOfBaubles;
				miniTable.add(getBauble(baubleNew, new ClickListener(){
					@Override
			        public void clicked(InputEvent event, float x, float y) {
						gemSound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
						for (int ii = 0; ii < difference; ii++) {
							decreaseStat(stat, character, statMessage, statLabel, done);
						}
						if (difference > 0) initStatTable();
			        }
				})).size(size, size).align(Align.left);
			}
			
			for ( ; numberOfBaubles < amount + Math.min(statPoints, (noStatAtMax() ? 2 : 1) - currentStatAllocation); numberOfBaubles++) {
				final int difference = 1 + numberOfBaubles - amount;
				miniTable.add(getBauble(baubleEmpty, new ClickListener(){
					@Override
			        public void clicked(InputEvent event, float x, float y) {
						gemSound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
						for (int ii = 0; ii < difference; ii++) {
							increaseStat(stat, character, statMessage, statLabel, done);
						}
						if (difference > 0) initStatTable();
			        }
				})).size(size, size).align(Align.left);
			}
			
			for ( ; numberOfBaubles < 10; numberOfBaubles++) {
				miniTable.add(new Widget()).size(size, size).padBottom(0).align(Align.left);
			}
			Button plus = getPlusButton();
			if (!canIncreaseStat(stat)) plus.setColor(Color.GRAY);
			plus.addListener(new ClickListener(){
				@Override
		        public void clicked(InputEvent event, float x, float y) {
					if (canIncreaseStat(stat)) {
						gemSound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
					}
					increaseStat(stat, character, statMessage, statLabel, done);
					initStatTable();
		        }				
			});
			miniTable.add(plus).padRight(plus.getWidth());
			Table statReadout = new Table();
			Label statAmount = new Label(String.valueOf(amount), skin);
			setFontColor(statAmount, amount);
			
			statReadout.add(statAmount).row();
			statReadout.add(statLabel);
			
			miniTable.add(statReadout).minWidth(150).align(Align.right);
			statTable.add(miniTable).row();
		}
	}
	
	private Image getBauble(Texture baubleTexture, ClickListener listener) {
		Image newBauble = new Image(baubleTexture);
		newBauble.addListener(listener);
		return newBauble;
	}
	
	private void setStatPoints(int newTotal) {
		statPoints = newTotal;
		statPointDisplay.setText(String.valueOf(statPoints));
	}
	
	private void resetStatPoints(boolean story, PlayerCharacter character) {
		setStatPoints(story ? 1 : 4 + character.getBonusStats());
	}
	
	private boolean canIncreaseStat(final Stat stat) {
		int currentStatAllocation = statMap.get(stat);
		return statPoints > 0 && (currentStatAllocation < 1 || (currentStatAllocation < 2 && noStatAtMax()));
	}
	
	private boolean canDecreaseStat(final Stat stat) {
		int currentStatAllocation = statMap.get(stat);
		return currentStatAllocation > 0 || (currentStatAllocation > -1 && noStatAtNegative());
	}
	
	private void increaseStat(final Stat stat, final PlayerCharacter character, final Label statMessage, final Label statLabel, final TextButton done) {
		int currentStatAllocation = statMap.get(stat);
		if (canIncreaseStat(stat)) {
			setStatPoints(statPoints - 1);
			if (statPoints <= 0) {
				this.addActor(done);
			}
			setStat(stat, 1, currentStatAllocation, character, statMessage, statLabel);			
		}
		else {
			if (statPoints <= 0) {
				statMessage.setText("You are out of stat points to allocate!");
				
			}
			else if (currentStatAllocation < 2) {
				statMessage.setText("Only one stat may be two points above its base score!");
			}
			else {
				statMessage.setText("Your " + stat.toString() + " is at maximum! It cannot be raised any more.");
				
			}
			statMessage.addAction(Actions.show());
		}
	}
	
	private void decreaseStat(final Stat stat, final PlayerCharacter character, final Label statMessage, final Label statLabel, final TextButton done) {
		int currentStatAllocation = statMap.get(stat);
		if (canDecreaseStat(stat)) {
			if (statPoints == 0) {
				removeActor(done);
			}
			setStatPoints(statPoints + 1);
			setStat(stat, -1, currentStatAllocation, character, statMessage, statLabel);
		}
		else {
			if (currentStatAllocation <= -1) {
				statMessage.setText("Your " + stat.toString() + " is at minimum! It cannot be lowered.");
			}
			else {
				statMessage.setText("You can only lower one stat below its base score.");
			}
			statMessage.addAction(Actions.show());
		}
	}	
	
	private void setStat(final Stat stat, final int modStat, final int currentStatAllocation, final PlayerCharacter character, final Label statMessage, final Label statLabel) {
		character.setStat(stat, character.getBaseStat(stat) + modStat);
		saveService.saveDataValue(SaveEnum.PLAYER, character);
		statMap.put(stat, currentStatAllocation + modStat);
		setStatAppearance(statLabel, character.getBaseStat(stat), stat, character);
		statMessage.addAction(Actions.hide());
		statMessage.setText("");
	}

	private ObjectMap<Stat, Integer> resetObjectMap() {
		ObjectMap<Stat, Integer> tempMap = new ObjectMap<Stat, Integer>();
		for (final Stat stat: Stat.values()) {
			tempMap.put(stat, 0);
		}
		return tempMap;
	}
	
	private boolean noStatAtMax() {
		for (Integer value: statMap.values()) {
			if (value >= 2) {
				return false;
			}
		}
		return true;
	}
	
	private boolean noStatAtNegative() {
		for (Integer value: statMap.values()) {
			if (value < 0) {
				return false;
			}
		}
		return true;
	}

	private String getClassFeatures(SaveManager.JobClass jobClass) {
		switch (jobClass) {
			case WARRIOR: return "+1 Skill point.\nUnlocked \"Blitz\" Stance.\nGained \"Weak to Anal\".";
			case PALADIN: return "Combat Heal learned.\nBreastplate equipped.\nBattle Skirt equipped.\nChastity Cage equipped.";
			case THIEF:   return "+3 Skill points.\n+30 gold.";
			case RANGER:  return "Received bow.\n+40 food.";
			case MAGE:    return "+1 Magic point.\nLimited armor.\n";
			case ENCHANTRESS: return "+1 Perk point.\n";
			default: return "";
		}
	}
	
	private void setStatAppearance(Label font, int amount, Stat stat, PlayerCharacter character) {
		//setFontColor(font, amount);
		font.setColor(Color.BLACK);
		setStatText(stat, character, font);
	}
	
	private void setFontColor(Label font, int amount) {
		Color toApply = Color.WHITE;
		switch (amount) {
			case 0: toApply = Color.BLACK; break;
			case 1: toApply = Color.DARK_GRAY; break;
			case 2: toApply = Color.GRAY; break;
			case 3: toApply = Color.NAVY; break;
			case 4: toApply = Color.ROYAL; break;
			case 5: toApply = Color.OLIVE; break;	
			case 6: toApply = Color.FOREST; break;
			case 7: toApply = Color.LIME; break;	
			case 8: toApply = Color.GOLDENROD; break;
			case 9: toApply = Color.GOLD; break;
		}
		font.setColor(toApply);
	}
	
	private void setStatText(Stat stat, PlayerCharacter character, Label label) {
		int amount = character.getBaseStat(stat);
		label.setText(stat.getRankDescription(amount));
	}
	
	//statMap.get(stat) - this tells you the difference from base
	
	private Label initLabel(String text, Skin skin, Color color, int x, int y, int alignment) { return initLabel(text, skin, color, x, y, alignment, false, 0); }
	private Label initLabel(String text, Skin skin, Color color, int x, int y, int alignment, boolean wrap, int width) {
		Label newLabel = new Label(text, skin);
		newLabel.setColor(color);
		newLabel.setPosition(x, y);
		newLabel.setAlignment(alignment);
		newLabel.setWrap(wrap);
		if (wrap) newLabel.setWidth(width);
		this.addActor(newLabel);
		return newLabel;
	}
	
	private ClickListener getStatListener(final Stat stat, final Label statDescription, final Label statMessage) {
		return new ClickListener() {
			@Override
	        public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
				statDescription.setText(stat.getDescription());
				statDescription.addAction(Actions.show());
				statMessage.addAction(Actions.hide());
			}
			@Override
	        public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
				statDescription.addAction(Actions.hide());
				statMessage.addAction(Actions.show());
			}
		};
	}
	
	private Button getPlusButton() {
		ButtonStyle buttonStyle = new ButtonStyle();
		buttonStyle.up = new TextureRegionDrawable(new TextureRegion(assetManager.get(AssetEnum.PLUS.getTexture())));
		buttonStyle.down = new TextureRegionDrawable(new TextureRegion(assetManager.get(AssetEnum.PLUS_DOWN.getTexture())));
		buttonStyle.over = new TextureRegionDrawable(new TextureRegion(assetManager.get(AssetEnum.PLUS_HIGHLIGHT.getTexture())));		
		Button button = new Button(buttonStyle);
		return button;
	}
	
	private Button getMinusButton() {
		ButtonStyle buttonStyle = new ButtonStyle();
		buttonStyle.up = new TextureRegionDrawable(new TextureRegion(assetManager.get(AssetEnum.MINUS.getTexture())));
		buttonStyle.down = new TextureRegionDrawable(new TextureRegion(assetManager.get(AssetEnum.MINUS_DOWN.getTexture())));
		buttonStyle.over = new TextureRegionDrawable(new TextureRegion(assetManager.get(AssetEnum.MINUS_HIGHLIGHT.getTexture())));		
		Button button = new Button(buttonStyle);
		return button;
	}
	
	@Override
	public void activate() {
		isActive = true;
		this.addAction(Actions.show());
		this.setBounds(0, 0, 2000, 2000);
		
		saveService.saveDataValue(SaveEnum.SCENE_CODE, sceneCode);
	}
	
	@Override
	public void act(float delta) {
		super.act(delta);
		
		// this should be based on a separate flag that will be toggleable - so that the keyboard is either controller the stat window or the class select, with up and down selecting stat, left or right decrease/increase, and enter confirming stat allocations
		if (isActive) {
			if(Gdx.input.isKeyJustPressed(Keys.UP)) {
	        	if (selection > 0) setSelection(selection - 1);
	        	else setSelection(classButtons.size - 1);
	        }
	        else if(Gdx.input.isKeyJustPressed(Keys.DOWN)) {
	        	if (selection < classButtons.size- 1) setSelection(selection + 1);
	        	else setSelection(0);
	        }
	        else if (Gdx.input.isKeyJustPressed(Keys.ENTER)) {
	        	InputEvent event1 = new InputEvent();
		        event1.setType(InputEvent.Type.touchDown);
		        classButtons.get(selection).fire(event1);
		        InputEvent event2 = new InputEvent();
		        event2.setType(InputEvent.Type.touchUp);
		        classButtons.get(selection).fire(event2);
	        }
		}
		
		if (enchanterButton != null && isActive) {
			InputEvent event1 = new InputEvent();
	        event1.setType(InputEvent.Type.touchDown);
	        enchanterButton.fire(event1);
	        InputEvent event2 = new InputEvent();
	        event2.setType(InputEvent.Type.touchUp);
	        enchanterButton.fire(event2);
	        enchanterButton = null;
		}
	}
	
	private void setSelection(int newSelection) {
		if (newSelection == this.selection) return;
		deactivate(selection);
		activate(newSelection);
	}
	
	private void deactivate(int toDeactivate) {
		TextButton button = classButtons.get(toDeactivate);
		button.setColor(Color.WHITE);
	}
	
	private void activate(int activate) {
		TextButton button = classButtons.get(activate);
		button.setColor(Color.YELLOW);
		this.selection = activate;
	}
	
	private void nextScene() {
		sceneBranches.get(sceneBranches.orderedKeys().get(0)).setActive();
		isActive = false;
		addAction(Actions.hide());
	}
}

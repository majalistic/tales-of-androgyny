package com.majalis.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.OrderedMap;
import com.majalis.asset.AssetEnum;
import com.majalis.character.PlayerCharacter;
import com.majalis.character.AbstractCharacter.Stat;
import com.majalis.encounter.Background;
import com.majalis.save.SaveEnum;
import com.majalis.save.SaveManager;
import com.majalis.save.SaveService;
import com.majalis.save.SaveManager.JobClass;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class CharacterCreationScene extends Scene {

	private final SaveService saveService;
	private ObjectMap<Stat, Integer> statMap;
	private TextButton enchanterButton;
	private int statPoints;
	private final Label statPointDisplay;
	
	public CharacterCreationScene(OrderedMap<Integer, Scene> sceneBranches, int sceneCode, final SaveService saveService, Background background, final AssetManager assetManager, final PlayerCharacter character, final boolean story) {
		super(sceneBranches, sceneCode);
		this.saveService = saveService;
		this.addActor(background);
		
		final Skin skin = assetManager.get(AssetEnum.UI_SKIN.getSkin());
		final Sound buttonSound = assetManager.get(AssetEnum.BUTTON_SOUND.getSound());
		final Sound gemSound = assetManager.get(AssetEnum.GEM_CLINK.getSound());
		final Texture baubleEmpty = assetManager.get(AssetEnum.CREATION_BAUBLE_EMPTY.getTexture());
		final Texture baubleOld = assetManager.get(AssetEnum.CREATION_BAUBLE_OLD.getTexture());
		final Texture baubleNew = assetManager.get(AssetEnum.CREATION_BAUBLE_NEW.getTexture());
		final Texture baubleReady = assetManager.get(AssetEnum.CREATION_BAUBLE_REMOVED.getTexture());
		final Label helpText = initLabel("Please Select a Class!", skin, Color.CHARTREUSE, 300, 800, Align.left);
		
		final Image characterImage = new Image(); 
		characterImage.setPosition(1390, 230);
		this.addActor(characterImage);
		
		final TextButton done = new TextButton("Done", skin);
		
		done.addListener(new ClickListener() { @Override public void clicked(InputEvent event, float x, float y) { 
			buttonSound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f); nextScene();		   
		}});
		done.setPosition(1552, 10);

		int statX = 665;
		int statY = 170;

		statPointDisplay = initLabel(String.valueOf(statPoints), skin, Color.FOREST, 1310, 882, Align.left);		
		final Label classMessage = initLabel("", skin, Color.BLACK, 1700, 230, Align.top);
		final Label statMessage = initLabel("", skin, Color.RED, statX, statY, Align.left, true, 750);
		final Label statDescription = initLabel("", skin, Color.FOREST, statX, statY, Align.left, true, 750);
		final Label classSelection = initLabel("", skin, Color.GOLD, 1715, 965, Align.center);
		
		resetStatPoints(story);
		statMap = resetObjectMap();
		
		final Table statTable = new Table();
		statTable.align(Align.topLeft);
		statTable.setPosition(750, 865);
		
		Table table = new Table();
		table.setPosition(412, 450);
		this.addActor(table);	
		
		TextButtonStyle buttonStyle = new TextButtonStyle(new TextureRegionDrawable(new TextureRegion(assetManager.get(AssetEnum.CREATION_BUTTON_UP.getTexture()))),  new TextureRegionDrawable(new TextureRegion(assetManager.get(AssetEnum.CREATION_BUTTON_DOWN.getTexture()))),  new TextureRegionDrawable(new TextureRegion(assetManager.get(AssetEnum.CREATION_BUTTON_CHECKED.getTexture()))), skin.getFont("default-font"));
		buttonStyle.fontColor = Color.BLACK;
		
		final Array<TextButton> classButtons = new Array<TextButton>();
		
		for (final JobClass jobClass: JobClass.values()) {
			final TextButton button = new TextButton(jobClass.getLabel(), buttonStyle);
			classButtons.add(button);
			if (story && jobClass != JobClass.ENCHANTRESS) {
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
						for (TextButton listButton : classButtons) {
							listButton.setChecked(false);
						}
						button.setChecked(true);
						if (!story) {
							buttonSound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
						}
						helpText.setPosition(1000, 882);
						helpText.setText("Allocate Stat Points");
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
						resetStatPoints(story);
						statMap = resetObjectMap();
						initStatTable(statTable, assetManager, skin, gemSound, done, character, statDescription, statMessage, baubleOld, baubleNew, baubleReady, baubleEmpty);
						addActor(statTable);
			        }
				});
			}
			table.add(button).size(350, 105).row();
			if (story && jobClass == JobClass.ENCHANTRESS) {
				enchanterButton = button;
			}
		}
		// this needs to be moved to a better place
		if (story) {
			character.setBaseDefense(2);
		}
	}
	
	private void initStatTable(final Table statTable, final AssetManager assetManager, final Skin skin, final Sound buttonSound, final TextButton done, final PlayerCharacter character, final Label statDescription, final Label statMessage, 
			final Texture baubleOld, final Texture baubleNew, final Texture baubleReady, final Texture baubleEmpty) {
		statTable.clear();
		final ObjectMap<Stat, Label> statToLabel = new ObjectMap<Stat, Label>();
		for (final Stat stat: Stat.values()) {
			Image statImage = new Image(assetManager.get(stat.getAsset()));
			statImage.addListener(getStatListener(stat, statDescription, statMessage));
			
			final Label statLabel = new Label("", skin);
			statToLabel.put(stat, statLabel);
			int amount = character.getBaseStat(stat);
			setStatAppearance(statLabel, amount, stat, character);

			int bottomPad = 20;
			statTable.add(statImage).size(statImage.getWidth() / (statImage.getHeight() / 35), 35).padBottom(bottomPad).padRight(3).align(Align.left);
			
			int currentStatAllocation = statMap.get(stat);
			
			int size = 30;		
			int numberOfBaubles = 0;
			for ( ; numberOfBaubles < amount - Math.max(0, currentStatAllocation + (noStatAtNegative() ? 1 : 0)); numberOfBaubles++) {
				final int difference = amount - numberOfBaubles;
				statTable.add(getBauble(baubleOld, new ClickListener(){
					@Override
			        public void clicked(InputEvent event, float x, float y) {
						buttonSound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
						for (int ii = 0; ii < difference; ii++) {
							decreaseStat(stat, character, statMessage, statLabel, done);
						}
						if (difference > 0) initStatTable(statTable, assetManager, skin, buttonSound, done, character, statDescription, statMessage, baubleOld, baubleNew, baubleReady, baubleEmpty);
			        }
				})).size(size, size).padBottom(bottomPad).align(Align.left);
			}

			for ( ; numberOfBaubles < amount; numberOfBaubles++) {
				final int difference = amount - numberOfBaubles;
				statTable.add(getBauble(baubleNew, new ClickListener(){
					@Override
			        public void clicked(InputEvent event, float x, float y) {
						buttonSound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
						for (int ii = 0; ii < difference; ii++) {
							decreaseStat(stat, character, statMessage, statLabel, done);
						}
						if (difference > 0) initStatTable(statTable, assetManager, skin, buttonSound, done, character, statDescription, statMessage, baubleOld, baubleNew, baubleReady, baubleEmpty);
			        }
				})).size(size, size).padBottom(bottomPad).align(Align.left);
			}
			
			for ( ; numberOfBaubles < amount + Math.min(statPoints, (noStatAtMax() ? 2 : 1) - currentStatAllocation); numberOfBaubles++) {
				final int difference = 1 + numberOfBaubles - amount;
				statTable.add(getBauble(baubleEmpty, new ClickListener(){
					@Override
			        public void clicked(InputEvent event, float x, float y) {
						buttonSound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
						for (int ii = 0; ii < difference; ii++) {
							increaseStat(stat, character, statMessage, statLabel, done);
						}
						if (difference > 0) initStatTable(statTable, assetManager, skin, buttonSound, done, character, statDescription, statMessage, baubleOld, baubleNew, baubleReady, baubleEmpty);
			        }
				})).size(size, size).padBottom(bottomPad).align(Align.left);
			}
			
			for ( ; numberOfBaubles < 10; numberOfBaubles++) {
				statTable.add(new Widget()).size(size, size).padBottom(bottomPad).align(Align.left);
			}
			Table statReadout = new Table();
			Label statAmount = new Label(String.valueOf(amount), skin);
			setFontColor(statAmount, amount);
			
			statReadout.add(statAmount).padBottom(-15).row();
			statReadout.add(statLabel);
			
			statTable.add(statReadout).padLeft(15).padBottom(bottomPad).minWidth(150).align(Align.right).row();
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
	
	private void resetStatPoints(boolean story) {
		setStatPoints(story ? 1 : 4);
	}
	
	private void increaseStat(final Stat stat, final PlayerCharacter character, final Label statMessage, final Label statLabel, final TextButton done) {
		int currentStatAllocation = statMap.get(stat);
		if (statPoints > 0 && (currentStatAllocation < 1 || (currentStatAllocation < 2 && noStatAtMax()))) {
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
		if (currentStatAllocation > 0 || (currentStatAllocation > -1 && noStatAtNegative())) {
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
			case THIEF:   return "+3 Skill points.\n+40 food.";
			case RANGER:  return "Received bow.";
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
		label.setText(PlayerCharacter.getStatMap().get(stat).get(amount));
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
	
	
	@Override
	public void setActive() {
		isActive = true;
		this.removeAction(Actions.hide());
		this.addAction(Actions.visible(true));
		this.addAction(Actions.show());
		this.setBounds(0, 0, 2000, 2000);
		
		saveService.saveDataValue(SaveEnum.SCENE_CODE, sceneCode);
	}
	
	@Override
	public void act(float delta) {
		super.act(delta);
		if (enchanterButton != null) {
			InputEvent event1 = new InputEvent();
	        event1.setType(InputEvent.Type.touchDown);
	        enchanterButton.fire(event1);
	        InputEvent event2 = new InputEvent();
	        event2.setType(InputEvent.Type.touchUp);
	        enchanterButton.fire(event2);
	        enchanterButton = null;
		}
	}
	
	private void nextScene() {
		sceneBranches.get(sceneBranches.orderedKeys().get(0)).setActive();
		isActive = false;
		addAction(Actions.hide());
	}
}

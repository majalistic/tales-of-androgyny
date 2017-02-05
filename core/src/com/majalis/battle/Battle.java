package com.majalis.battle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.majalis.asset.AnimatedImage;
import com.majalis.asset.AssetEnum;
import com.majalis.character.AbstractCharacter;
import com.majalis.character.AbstractCharacter.Stance;
import com.majalis.character.Attack.Status;
import com.majalis.character.Attack;
import com.majalis.character.EnemyCharacter;
import com.majalis.character.PlayerCharacter;
import com.majalis.character.Technique;
import com.majalis.encounter.Background;
import com.majalis.save.SaveEnum;
import com.majalis.save.SaveService;
/*
 * Represents the logic for the flow of a battle.  Currently only supports 1 on 1.
 */
public class Battle extends Group{

	// this needs to be refactored so that it accepts the number keys 
	private static int[] POSSIBLE_KEYS = new int[]{Keys.A, Keys.S, Keys.D, Keys.F, Keys.G, Keys.H, Keys.J, Keys.K, Keys.L, Keys.Z, Keys.X, Keys.C, Keys.V, Keys.B, Keys.N};
	private static char[] POSSIBLE_KEYS_CHAR = new char[]{'A','S','D','F','G','H','J','K','L','Z','X','C','V', 'B', 'N'};
	
	private final PlayerCharacter character;
	private final EnemyCharacter enemy;
	private final SaveService saveService;
	private final AssetManager assetManager;
	private final BitmapFont font;
	private final ObjectMap<String, Integer> outcomes;
	private final Table table;
	private final Skin skin;
	
	private final AnimatedImage slash;

	private final ObjectMap<AssetEnum, Sound> soundMap;
	
	private final Array<SoundTimer> soundBuffer;
	private final Image hoverImage;
	private final Image characterPortrait;
	private final Group hoverGroup;
	private final Label console;
	private final Label skillDisplay;
	private final Label bonusDisplay;
	private final ProgressBar characterHealth;
	private final ProgressBar characterStamina;
	private final ProgressBar characterBalance;
	private final ProgressBar characterMana;
	private final ProgressBar enemyHealth;
	private final Image healthIcon;
	private final Image staminaIcon;
	private final Image balanceIcon;
	private final Image manaIcon;
	private final Image masculinityIcon;
	private final Image enemyHealthIcon;
	private final Label healthLabel;
	private final Label staminaLabel;
	private final Label balanceLabel;
	private final Label manaLabel;
	private final Label enemyHealthLabel;
	private final Label enemyWeaponLabel;
	private final Label armorLabel;
	private final Label enemyArmorLabel;
	
	private String consoleText;
	private Array<TextButton> optionButtons;
	private Technique selectedTechnique;
	private Image characterArousal;
	private Image enemyArousal;
	private int selection;
	
	private Outcome outcome;
	public boolean gameExit;
	private boolean battleOutcomeDecided;
	private boolean battleOver;
	
	private Group uiGroup;
	private boolean uiHidden;
	
	public Battle(SaveService saveService, AssetManager assetManager, BitmapFont font, PlayerCharacter character, EnemyCharacter enemy, ObjectMap<String, Integer> outcomes, Background battleBackground, Background battleUI, String consoleText) {
		this.saveService = saveService;
		this.assetManager = assetManager;
		this.font = font;
		this.character = character;
		this.enemy = enemy;
		this.outcomes = outcomes;
		battleOver = false;
		battleOutcomeDecided = false;
		gameExit = false;	
		
		soundMap = new ObjectMap<AssetEnum, Sound>();
		AssetEnum[] battleSounds = new AssetEnum[]{AssetEnum.UNPLUGGED_POP, AssetEnum.MOUTH_POP, AssetEnum.ATTACK_SOUND, AssetEnum.HIT_SOUND, AssetEnum.SWORD_SLASH_SOUND, AssetEnum.FIREBALL_SOUND, AssetEnum.INCANTATION, AssetEnum.THWAPPING, AssetEnum.BUTTON_SOUND, AssetEnum.PARRY_SOUND, AssetEnum.BLOCK_SOUND};
		for (AssetEnum soundPath: battleSounds) {
			soundMap.put(soundPath, assetManager.get(soundPath.getPath(), Sound.class));
		}
		
		soundBuffer = new Array<SoundTimer>();	
		this.addActor(battleBackground);
		this.addCharacter(character);
		this.addCharacter(enemy);
		this.addActor(battleUI);	
		
		skin = assetManager.get(AssetEnum.BATTLE_SKIN.getPath(), Skin.class);
		
		float barX = 195;
		float hoverXPos = 317; 
		float hoverYPos = 35; 
		float consoleXPos = 1200;
		float consoleYPos = 5;
		
		characterHealth = initBar(0, 1, .05f, false, skin, 350, character.getHealthPercent(), barX , 1035);
		healthIcon = initImage(assetManager.get(character.getHealthDisplay(), Texture.class), barX+3, 1042.5f);
		healthLabel = initLabel(character.getCurrentHealth() + " / " + character.getMaxHealth(), skin, Color.BROWN, barX + 75, 1038);
		
		characterStamina = initBar(0, 1, .05f, false, skin, 350, character.getStaminaPercent(), barX, 990);
		staminaIcon = initImage(assetManager.get(character.getStaminaDisplay(), Texture.class), barX + 7.5f, 1002.5f);
		staminaLabel = initLabel(character.getCurrentStamina() + " / " + character.getMaxStamina(), skin, Color.BROWN, barX + 75, 993);

		characterBalance = initBar(0, 1, .05f, false, skin, 350, character.getBalancePercent(), barX, 945);
		balanceIcon = initImage(assetManager.get(character.getBalanceDisplay(), Texture.class), barX + 3, 952.5f);
		balanceLabel = initLabel(character.getStability() > 0 ? character.getStability() + " / " + character.getMaxStability() : "DOWN (" + -character.getStability() + ")", skin, Color.BROWN, barX + 75, 948);
		
		if (character.hasMagic()) {
			characterMana = initBar(0, 1, .05f, false, skin, 350, character.getManaPercent(), barX, 900);
			manaIcon = initImage(assetManager.get(character.getManaDisplay(), Texture.class), barX + 3, 912.5f);
			manaLabel = initLabel(character.getCurrentMana() + " / " + character.getMaxMana(), skin, Color.BROWN, barX + 75, 903);
		}
		else {
			characterMana = null;
			manaIcon = null;
			manaLabel = null;
		}
		
		enemyHealth = initBar(0, 1, .05f, false, skin, 350, enemy.getHealthPercent(), 1500 , 960);
		enemyHealthIcon = initImage(assetManager.get(enemy.getHealthDisplay(), Texture.class), 1503, 967.5f);
		enemyHealthLabel = initLabel(enemy.getCurrentHealth() + " / " + enemy.getMaxHealth(), skin, Color.BROWN, 1578, 963);
		enemyWeaponLabel = initLabel("Weapon: " + (enemy.getWeapon() != null ? enemy.getWeapon().getName() : "Unarmed"), skin, Color.LIGHT_GRAY, 1578, 700);	
		
		Texture armorTexture = assetManager.get(AssetEnum.ARMOR.getPath(), Texture.class);
		initImage(armorTexture, barX + 320, 1032, 50);
		initImage(armorTexture, 1820, 960, 50);
		armorLabel = initLabel("" + character.getDefense(), skin, Color.BROWN, barX + 332, 1037);		
		enemyArmorLabel = initLabel("" + enemy.getDefense(), skin, Color.BROWN, 1832, 965);
		
		uiHidden = false;
		uiGroup = new Group();
		Image consoleBox = new Image(assetManager.get(AssetEnum.BATTLE_TEXTBOX.getPath(), Texture.class));
		uiGroup.addActor(consoleBox);
		consoleBox.setPosition(consoleXPos, consoleYPos);
		
		this.hoverImage = new Image(assetManager.get(AssetEnum.BATTLE_HOVER.getPath(), Texture.class));
		hoverImage.setPosition(hoverXPos, hoverYPos);	
		hoverImage.setWidth(hoverImage.getWidth() + 100);
		hoverImage.setHeight(hoverImage.getHeight() + 100);
		hoverGroup = new Group();
		hoverGroup.addActor(hoverImage);
		
		characterPortrait = new Image(assetManager.get(character.getPortraitPath(), Texture.class));
		addActorAndListen(characterPortrait, -5, 615);
		characterPortrait.setScale(.9f);

		masculinityIcon = initImage(assetManager.get(character.getMasculinityPath(), Texture.class), barX - 150, 850);
		masculinityIcon.setScale(.15f);
		
		characterArousal = new Image(assetManager.get(character.getLustImagePath(), Texture.class));
		addActorAndListen(characterArousal, 102, 490);
		characterArousal.setSize(150, 150);
		
		enemyArousal = new Image(assetManager.get(enemy.getLustImagePath(), Texture.class));
		addActorAndListen(enemyArousal, 1073, 505);
		enemyArousal.setSize(150, 150);
		
		StanceActor newActor = new StanceActor(character);
		addActorAndListen(newActor, 397, 565);
		newActor.setSize(150, 172.5f);
		newActor = new StanceActor(enemy);
		addActorAndListen(newActor, 866, 574);
		newActor.setSize(150, 172.5f);
		
		hoverGroup.addAction(Actions.visible(false));
		uiGroup.addActor(hoverGroup);
			
		table = new Table();
		uiGroup.addActor(table);
		displayTechniqueOptions();
		
		Texture slashSheet = assetManager.get(AssetEnum.SLASH.getPath(), Texture.class);
		Array<TextureRegion> frames = new Array<TextureRegion>();
		for (int ii = 0; ii < 6; ii++) {
			frames.add(new TextureRegion(slashSheet, ii * 512, 0, 512, 512));
		}
		
		Animation animation = new Animation(.07f, frames);
		slash = new AnimatedImage(animation, Scaling.fit, Align.right);
		slash.setState(1);
		
		slash.setPosition(500, 0);
		this.addActor(slash);
		
		this.addActor(uiGroup);
		this.consoleText = consoleText;
		console = new Label(consoleText, skin);
		console.setSize(700, 200);
		console.setWrap(true);
		console.setColor(Color.BLACK);
		console.setAlignment(Align.top);
		ScrollPane pane = new ScrollPane(console);
		pane.setBounds(consoleXPos+50, 50, 625, 350);
		pane.setScrollingDisabled(true, false);
	
		uiGroup.addActor(pane);
		
		skillDisplay = new Label("", skin);
		skillDisplay.setWrap(true);
		skillDisplay.setColor(Color.BLACK);
		skillDisplay.setAlignment(Align.top);
		Table pane2 = new Table();
		pane2.align(Align.top);
		pane2.add(skillDisplay).width(600).row();
		
		bonusDisplay = new Label("", skin);
		bonusDisplay.setWrap(true);
		bonusDisplay.setColor(Color.FOREST);
		bonusDisplay.setAlignment(Align.top);
		pane2.add(bonusDisplay).width(600);
		
		pane2.setBounds(hoverXPos + 80, hoverYPos - 155, 600, 700);
		hoverGroup.addActor(pane2);
		
		Outcome battleOutcome = enemy.getOutcome(character);
		if (battleOutcome != null) {
			battleOutcomeDecided = true;
			outcome = battleOutcome; 
			skillDisplay.setText(enemy.getOutcomeText(character));
			character.refresh();
			bonusDisplay.setText("");
			uiGroup.removeActor(table);
			hoverGroup.clearActions();
			hoverGroup.addAction(Actions.visible(true));
			hoverGroup.addAction(Actions.moveTo(400, 380));
			hoverGroup.addAction(Actions.fadeIn(.1f));
			this.addListener(
				new ClickListener() {
			        @Override
			        public void clicked(InputEvent event, float x, float y) {
			        	battleOver = true;
			        	saveService.saveDataValue(SaveEnum.CONSOLE, "");
			        }
				}
			);
		}
	}
	
	public void battleLoop() {
		Array<SoundTimer> toRemove = new Array<SoundTimer>();
		for (SoundTimer timer : soundBuffer) {
			if (timer.decreaseTime()) {
				toRemove.add(timer);
			}
		}
		soundBuffer.removeAll(toRemove, true);
		
		if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
			gameExit = true;
		}
		else if (!battleOutcomeDecided) {
			if(Gdx.input.isKeyJustPressed(Keys.UP)) {
	        	if (selection > 0) changeSelection(selection - 1);
	        	else changeSelection(optionButtons.size-1);
			}
	        else if(Gdx.input.isKeyJustPressed(Keys.DOWN)) {
	        	if (selection < optionButtons.size- 1) changeSelection(selection + 1);
	        	else changeSelection(0);
	        }
	        else if(Gdx.input.isKeyJustPressed(Keys.ENTER)) {
	        	clickButton(optionButtons.get(selection));
	        }
			
			if (Gdx.input.isKeyJustPressed(Keys.TAB)) {
				if (uiHidden) {
					uiGroup.addAction(Actions.show());
				}
				else {
					uiGroup.addAction(Actions.hide());
				}
				uiHidden = !uiHidden;
			}
			
			if (selectedTechnique == null) {
				int ii = 0;
				for (int possibleKey : POSSIBLE_KEYS) {
					if (Gdx.input.isKeyJustPressed(possibleKey)) {
						if (ii < optionButtons.size) {
							selectedTechnique = clickButton(optionButtons.get(ii));
							break;
						}
					}
					ii++;
				}
			}
				
			if (selectedTechnique != null) {		
				soundMap.get(AssetEnum.BUTTON_SOUND).play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
				// possibly construct a separate class for this
				resolveTechniques(character, selectedTechnique, enemy, enemy.getTechnique(character));
				selectedTechnique = null;
				displayTechniqueOptions();
				saveService.saveDataValue(SaveEnum.PLAYER, character);
				saveService.saveDataValue(SaveEnum.ENEMY, enemy);
				saveService.saveDataValue(SaveEnum.CONSOLE, consoleText);				
			}

			if (character.getCurrentHealth() <= 0) {
				outcome = Outcome.DEFEAT;
				battleOutcomeDecided = true;
				skillDisplay.setText("DEFEAT!\n" + character.getDefeatMessage());
			}
			if (enemy.getCurrentHealth() <= 0) {
				outcome = Outcome.VICTORY;
				battleOutcomeDecided = true;
				skillDisplay.setText("VICTORY!\n" + enemy.getDefeatMessage());
			}
			if (battleOutcomeDecided) {
				character.refresh();
				bonusDisplay.setText("");
				uiGroup.removeActor(table);
				hoverGroup.clearActions();
				hoverGroup.addAction(Actions.visible(true));
				hoverGroup.addAction(Actions.moveTo(400, 380));
				hoverGroup.addAction(Actions.fadeIn(.1f));
				this.addListener(
					new ClickListener() {
				        @Override
				        public void clicked(InputEvent event, float x, float y) {
				        	battleOver = true;
				        	saveService.saveDataValue(SaveEnum.CONSOLE, "");
				        }
					}
				);
			}
		}
		else {
			if(Gdx.input.isKeyJustPressed(Keys.ENTER) || Gdx.input.isKeyJustPressed(Keys.SPACE)) {
	        	battleOver = true;
	        }
		}
	}
	
	private void displayTechniqueOptions() {
		table.clear();
		Array<Technique> options = character.getPossibleTechniques(enemy);
		optionButtons = new Array<TextButton>();
		
		for (int ii = 0; ii < options.size; ii++) {
			SkillButton button;
			Technique option = options.get(ii);
			button = new SkillButton(option.getTechniqueName() + (ii > POSSIBLE_KEYS_CHAR.length ? "" : " ("+POSSIBLE_KEYS_CHAR[ii]+")"), skin, assetManager.get(option.getStance().getPath(), Texture.class));
			table.add(button).size(440, 76).row();
			optionButtons.add(button);
			boolean outOfStamina = false;
			boolean outOfStability = false;
			if(character.outOfStaminaOrStability(option)) {
				outOfStamina = character.outOfStamina(option);
				outOfStability = !outOfStamina;
				TextButtonStyle style = new TextButtonStyle(button.getStyle());
				style.fontColor = Color.RED;
				button.setStyle(style);
			}
			else if (character.lowStaminaOrStability(option)) {
				TextButtonStyle style = new TextButtonStyle(button.getStyle());
				style.fontColor = Color.ORANGE;
				button.setStyle(style);
			}
			button.addListener(getListener(option, (outOfStamina && !character.getStance().isIncapacitatingOrErotic() ? "THIS WILL CAUSE YOU TO COLLAPSE!\n" : outOfStability && !character.getStance().isIncapacitatingOrErotic() ? "THIS WILL CAUSE YOU TO LOSE YOUR FOOTING!\n" : "") + option.getTechniqueDescription(), option.getBonusDescription(), ii));
		}
        table.setFillParent(true);
        table.align(Align.top);
        table.setPosition(25, 600);
        table.addAction(Actions.moveBy(125, 0, .1f));
        newSelection(0);
	}
	
	// should probably use String builder to build a string to display in the console - needs to properly be getting information from the interactions - may need to be broken up into its own class
	private void resolveTechniques(AbstractCharacter firstCharacter, Technique firstTechnique, AbstractCharacter secondCharacter, Technique secondTechnique) {
		consoleText = "";
		
		Stance oldStance = firstCharacter.getStance();
		
		printToConsole(firstCharacter.getStanceTransform(firstTechnique));
		printToConsole(secondCharacter.getStanceTransform(secondTechnique));
		
		firstTechnique = firstCharacter.extractCosts(firstTechnique);
		secondTechnique = secondCharacter.extractCosts(secondTechnique);
	
		Attack attackForFirstCharacter = secondCharacter.doAttack(secondTechnique.resolve(firstTechnique));
		Attack attackForSecondCharacter = firstCharacter.doAttack(firstTechnique.resolve(secondTechnique));
		
		
		if (attackForFirstCharacter.isAttack()) {
			slash.setState(0);
			if (!attackForFirstCharacter.isSuccessful()) {
				soundBuffer.add(new SoundTimer(soundMap.get(AssetEnum.ATTACK_SOUND), 0, .5f));
				if (attackForFirstCharacter.getStatus() == Status.PARRIED) {
					soundBuffer.add(new SoundTimer(soundMap.get(AssetEnum.PARRY_SOUND), 5, .5f));
				}
			}
			else {
				if (attackForFirstCharacter.getStatus() == Status.BLOCKED) {
					soundBuffer.add(new SoundTimer(soundMap.get(AssetEnum.BLOCK_SOUND), 5, .5f));
				}
				else {
					if (enemy.getWeapon() != null) {
						soundBuffer.add(new SoundTimer(soundMap.get(AssetEnum.SWORD_SLASH_SOUND), 5, .5f));
					}
					else {
						soundBuffer.add(new SoundTimer(soundMap.get(AssetEnum.HIT_SOUND), 20, .3f));
					}
				}
			}
		}
		if (character.getStance() == Stance.CASTING && attackForSecondCharacter.isSuccessful()) {
			soundBuffer.add(new SoundTimer(soundMap.get(AssetEnum.INCANTATION), 5, .5f));
		}
		if (attackForSecondCharacter.isAttack()) {
			if (!attackForSecondCharacter.isSuccessful()) {
				soundBuffer.add(new SoundTimer(soundMap.get(AssetEnum.ATTACK_SOUND), 15, .5f));
				if (attackForSecondCharacter.getStatus() == Status.PARRIED) {
					soundBuffer.add(new SoundTimer(soundMap.get(AssetEnum.PARRY_SOUND), 5, .5f));
				}
			}
			else {
				if (attackForSecondCharacter.isSpell()) {
					soundBuffer.add(new SoundTimer(soundMap.get(AssetEnum.FIREBALL_SOUND), 5, .5f));
				}
				else {
					if (attackForSecondCharacter.getStatus() == Status.BLOCKED) {
						soundBuffer.add(new SoundTimer(soundMap.get(AssetEnum.BLOCK_SOUND), 5, .5f));
					}
					else if (character.getWeapon() != null) {
						soundBuffer.add(new SoundTimer(soundMap.get(AssetEnum.SWORD_SLASH_SOUND), 5, .5f));
					}
					else {
						soundBuffer.add(new SoundTimer(soundMap.get(AssetEnum.HIT_SOUND), 5, .3f));
					}
				}
				enemy.hitAnimation();
			}
		}
		
		printToConsole(firstCharacter.receiveAttack(attackForFirstCharacter));
		printToConsole(secondCharacter.receiveAttack(attackForSecondCharacter));		
		
		if (attackForFirstCharacter.isAttack() && attackForFirstCharacter.isSuccessful() && attackForFirstCharacter.getStatus() != Status.BLOCKED) {
			characterPortrait.setDrawable(new TextureRegionDrawable(new TextureRegion(assetManager.get(AssetEnum.PORTRAIT_HIT.getPath(), Texture.class))));
			characterPortrait.addAction(Actions.sequence(Actions.moveBy(-10, -10), Actions.delay(.1f), Actions.moveBy(0, 20), Actions.delay(.1f), Actions.moveBy(20, -20), Actions.delay(.1f), Actions.moveBy(0, 20), Actions.delay(.1f), Actions.moveTo(-5 * 1.5f, 615 * 1.5f),  
				new Action(){
					@Override
					public boolean act(float delta) {
						characterPortrait.setDrawable(new TextureRegionDrawable(new TextureRegion(assetManager.get(character.getPortraitPath(), Texture.class))));	
						return true;
					}
				})
			);
		}
		else {
			characterPortrait.setDrawable(new TextureRegionDrawable(new TextureRegion(assetManager.get(character.getPortraitPath(), Texture.class))));	
		}
		
		if (oldStance.isAnal() && firstCharacter.getStance().isAnal()) {
			soundMap.get(AssetEnum.THWAPPING).play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
		}
		
		if (oldStance.isAnal() && !firstCharacter.getStance().isAnal()) {
			soundMap.get(AssetEnum.THWAPPING).play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
			soundBuffer.add(new SoundTimer(soundMap.get(AssetEnum.UNPLUGGED_POP), 105, .3f));
		}
		
		if (oldStance.isOral() && !firstCharacter.getStance().isOral()) {
			soundMap.get(AssetEnum.MOUTH_POP).play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume"));
		}
		
		// this needs to be secondCharacter.getOutcome() or something similar
		Outcome battleOutcome = ((EnemyCharacter) secondCharacter).getOutcome(firstCharacter);
		if (battleOutcome != null) {
			battleOutcomeDecided = true;
			outcome = battleOutcome; 
			skillDisplay.setText(((EnemyCharacter) secondCharacter).getOutcomeText(firstCharacter));
		}
		
		console.setText(consoleText);
		
		characterArousal.setDrawable(new TextureRegionDrawable(new TextureRegion(assetManager.get(character.getLustImagePath(), Texture.class))));
		enemyArousal.setDrawable(new TextureRegionDrawable(new TextureRegion(assetManager.get(enemy.getLustImagePath(), Texture.class))));
		characterHealth.setValue(character.getHealthPercent());
		characterStamina.setValue(character.getStaminaPercent());
		characterBalance.setValue(character.getBalancePercent());
		if (character.hasMagic()) {
			characterMana.setValue(character.getManaPercent());
			manaLabel.setText(character.getCurrentMana() + " / " + character.getMaxMana());
			manaIcon.setDrawable(new TextureRegionDrawable(new TextureRegion(assetManager.get(character.getManaDisplay(), Texture.class))));
		}
		enemyHealth.setValue(enemy.getHealthPercent());
		healthLabel.setText(character.getCurrentHealth() + " / " + character.getMaxHealth());
		staminaLabel.setText(character.getCurrentStamina() + " / " + character.getMaxStamina());
		balanceLabel.setText(character.getStability() > 0 ? character.getStability() + " / " + character.getMaxStability() : "DOWN (" + -character.getStability() + ")");
		enemyHealthLabel.setText(enemy.getCurrentHealth() + " / " + enemy.getMaxHealth());
		enemyWeaponLabel.setText("Weapon: " + (enemy.getWeapon() != null ? enemy.getWeapon().getName() : "Unarmed"));
		healthIcon.setDrawable(new TextureRegionDrawable(new TextureRegion(assetManager.get(character.getHealthDisplay(), Texture.class))));
		staminaIcon.setDrawable(new TextureRegionDrawable(new TextureRegion(assetManager.get(character.getStaminaDisplay(), Texture.class))));
		balanceIcon.setDrawable(new TextureRegionDrawable(new TextureRegion(assetManager.get(character.getBalanceDisplay(), Texture.class))));
		enemyHealthIcon.setDrawable(new TextureRegionDrawable(new TextureRegion(assetManager.get(enemy.getHealthDisplay(), Texture.class))));
		masculinityIcon.setDrawable(new TextureRegionDrawable(new TextureRegion(assetManager.get(character.getMasculinityPath(), Texture.class))));
		armorLabel.setText("" + character.getDefense());
		enemyArmorLabel.setText("" + enemy.getDefense());		
	}
	
	private void printToConsole(Array<String> results) {
		for (String result: results) {
			printToConsole(result);
		}
	}
	
	private void printToConsole(String result) { 
		consoleText += result + "\n";
	}
	
	private void changeSelection(int newSelection) {
		if (selection == newSelection) return;
		optionButtons.get(selection).addAction(Actions.sequence(Actions.delay(.05f), Actions.moveBy(-50, 0)));
    	newSelection(newSelection);
	}
	
	private void newSelection(int newSelection) {
    	optionButtons.get(newSelection).addAction(Actions.sequence(Actions.delay(.05f), Actions.moveBy(50, 0)));
		selection = newSelection;
	}
	
	/* Helper methods */
	
	private Texture getStanceImage(Stance stance) {
		return assetManager.get(stance.getPath(), Texture.class);
	}
	
	private ClickListener getListener(final Technique technique, final String description, final String bonusDescription, final int index) {
		return new ClickListener() {
	        @Override
	        public void clicked(InputEvent event, float x, float y) {
	        	selectedTechnique = technique;
	        }
	        @Override
	        public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
				skillDisplay.setText(description);
				bonusDisplay.setText(bonusDescription);
				changeSelection(index);		
				hoverGroup.clearActions();
				hoverGroup.addAction(Actions.visible(true));
				hoverGroup.addAction(Actions.fadeIn(.25f));
				
			}
			@Override
	        public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
				if (!battleOutcomeDecided) {
					hoverGroup.addAction(Actions.fadeOut(2f));
				}
			}
	    };
	}

	// creates a wrapper group for a character to be added to so that they can be removed and re-inserted during serialization
	private void addCharacter(AbstractCharacter character) {
		Group g = new Group();
		g.addActor(character);
		addActor(g);
	}
	// simulates a button click for the button param
	private Technique clickButton(TextButton button) {
		InputEvent event1 = new InputEvent();
        event1.setType(InputEvent.Type.touchDown);
        button.fire(event1);
        InputEvent event2 = new InputEvent();
        event2.setType(InputEvent.Type.touchUp);
        button.fire(event2);
        return selectedTechnique;
	}
	
	private ProgressBar initBar(float min, float max, float stepSize, boolean vertical, Skin skin, int width, float value, float x, float y) {
		ProgressBar newBar = new ProgressBar(min, max, stepSize, vertical, skin);
		newBar.setWidth(width);
		newBar.setPosition(x, y);
		newBar.setValue(value);
		this.addActor(newBar);
		return newBar;
	}
	
	private Image initImage(Texture texture, float x, float y) {
		Image newImage = new Image(texture);
		newImage.setPosition(x, y);
		this.addActor(newImage);
		return newImage;
	}
	private Image initImage(Texture texture, float x, float y, int height) {
		Image newImage = new Image(texture);
		newImage.setPosition(x, y);
		newImage.setHeight(height);
		newImage.setWidth((int) (texture.getWidth() / (texture.getHeight() / (1.0 * height))) );
		this.addActor(newImage);
		return newImage;
	}
	
	private Label initLabel(String value, Skin skin, Color color, float x, float y) {
		Label newLabel = new Label(value, skin);
		newLabel.setColor(color);
		newLabel.setPosition(x, y);
		this.addActor(newLabel);
		return newLabel;
	}
	
	public enum Outcome {
		VICTORY, DEFEAT, KNOT, SATISFIED
	}
	
	/* REFACTOR AND REMOVE BEYOND THIS LINE */
	
	private class StanceActor extends Actor{
		
		private final AbstractCharacter character;
		private final Texture hoverBox;
		private boolean hover;
		public StanceActor(AbstractCharacter character) {
			this.character = character;
			hover = false;
			hoverBox = assetManager.get(AssetEnum.BATTLE_HOVER.getPath(), Texture.class);
			this.addListener(new ClickListener() { 
				@Override
		        public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
					// this could actually perform the action rather than relying on a boolean
					hover = true;
				}
				@Override
		        public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
					hover = false;
				}
			});
		}

		@Override
	    public void draw(Batch batch, float parentAlpha) {
			super.draw(batch, parentAlpha);
			batch.draw(getStanceImage(character.getStance()), getX(), getY(), getWidth(), getHeight());
			if (hover) {
				batch.draw(hoverBox, getX() - 45, getY() - 60, getWidth() + 100, 40);
				font.setColor(Color.BLACK);
				font.draw(batch, character.getStance().name(), getX(), getY() - 30, 100, Align.center, false);
			}
	    }
	}
	
	public boolean isBattleOver() {
		return battleOver;
	}
	
	public int getOutcomeScene() {
		return outcomes.get(outcome.toString());
	}
	
	private class SkillButton extends TextButton {
		Texture stanceIcon;
		public SkillButton(String text, Skin skin, Texture stanceIcon) {
			super(text, skin);
			this.stanceIcon = stanceIcon;
		}
		@Override
	    public void draw(Batch batch, float parentAlpha) {
			super.draw(batch, parentAlpha);
			batch.draw(stanceIcon, getX() + 372, getY()+3, 63, 76);
		}		
	}
	
	// this should be refactored into a delayed action
	private class SoundTimer {
		int timeLeft;
		Sound sound;
		float volume;
		
		SoundTimer(Sound sound, int timeLeft, float volume) {
			this.sound = sound;
			this.timeLeft = timeLeft;
			this.volume = volume;
		}
		
		public boolean decreaseTime() {
			timeLeft--;
			boolean played = timeLeft <= 0;
			if (played) {
				sound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") * volume);
			}
			return played;
		}
	}
	
	/*
	 * Temporary debug helper methods for positioning scene2d actors 
	 */
	private void addActorAndListen(Actor actor, float x, float y) {
		this.addActor(actor);
		actor.setPosition(x*1.5f, y*1.5f);
	}
}

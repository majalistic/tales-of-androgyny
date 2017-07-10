package com.majalis.battle;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
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
import com.majalis.character.Stance;
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
	private final ScrollPane techniquePane;
	private final Table techniqueTable;
	private final Skin skin;
	
	private final AnimatedImage slash;

	private final ObjectMap<AssetEnum, Sound> soundMap;
	
	private final Image hoverImage;
	private final Image characterPortrait;
	private final Group hoverGroup;
	private final Group dialogGroup;
	private final Label console;
	private final Label dialog;
	private final Label skillDisplay;
	private final Label bonusDisplay;
	private final Label penaltyDisplay;
	private final ProgressBar characterHealth;
	private final ProgressBar characterStamina;
	private final ProgressBar characterBalance;
	private final ProgressBar characterMana;
	private final ProgressBar enemyHealth;
	private final ProgressBar enemyStamina;
	private final ProgressBar enemyBalance;
	private final Image characterArousal;
	private final Image enemyArousal;
	private final Image characterBelly;
	private final Image healthIcon;
	private final Image staminaIcon;
	private final Image balanceIcon;
	private final Image manaIcon;
	private final Image masculinityIcon;
	private final Image enemyHealthIcon;
	private final Image enemyStaminaIcon;
	private final Image enemyBalanceIcon;
	private final Image bloodImage;
	private final Image enemyBloodImage;
	
	private final Label healthLabel;
	private final Label staminaLabel;
	private final Label balanceLabel;
	private final Label manaLabel;
	private final Label enemyHealthLabel;
	private final Label enemyStaminaLabel;
	private final Label enemyBalanceLabel;
	private final Label enemyWeaponLabel;
	private final Label armorLabel;
	private final Label enemyArmorLabel;
	private final Label bloodLabel;
	private final Label enemyBloodLabel;
	
	private final AssetDescriptor<Music> musicPath;
	
	private SkillText enemySkill;
	
	private String consoleText;
	private String dialogText;
	private Array<TextButton> optionButtons;
	private Technique selectedTechnique;
	private Technique enemySelectedTechnique;
	private int selection;	
	private Outcome outcome;
	public boolean gameExit;
	private boolean battleOutcomeDecided;
	private boolean battleOver;

	private Group uiGroup;
	private boolean uiHidden;
	private boolean onload = true;
	
	public Battle(SaveService saveService, AssetManager assetManager, BitmapFont font, PlayerCharacter character, EnemyCharacter enemy, ObjectMap<String, Integer> outcomes, Background battleBackground, Background battleUI, String consoleText, String dialogText, AssetDescriptor<Music> musicPath) {
		this.saveService = saveService;
		this.assetManager = assetManager;
		this.font = font;
		this.character = character;
		this.enemy = enemy;
		this.outcomes = outcomes;
		this.consoleText = consoleText;
		this.dialogText = dialogText;
		this.musicPath = musicPath;
		battleOver = false;
		battleOutcomeDecided = false;
		gameExit = false;	
		
		soundMap = new ObjectMap<AssetEnum, Sound>();
		AssetEnum[] battleSounds = new AssetEnum[]{AssetEnum.UNPLUGGED_POP, AssetEnum.MOUTH_POP, AssetEnum.ATTACK_SOUND, AssetEnum.HIT_SOUND, AssetEnum.SWORD_SLASH_SOUND, AssetEnum.FIREBALL_SOUND, AssetEnum.INCANTATION, AssetEnum.THWAPPING, AssetEnum.BUTTON_SOUND, AssetEnum.PARRY_SOUND, AssetEnum.BLOCK_SOUND};
		for (AssetEnum soundPath: battleSounds) {
			soundMap.put(soundPath, assetManager.get(soundPath.getSound()));
		}
		
		this.addActor(battleBackground);
		this.addCharacter(character);
		this.addCharacter(enemy);

		uiGroup = new Group();
		uiHidden = false;
		
		uiGroup.addActor(battleUI);	
		
		skin = assetManager.get(AssetEnum.BATTLE_SKIN.getSkin());
		
		float barX = 195;
		float enemyBarX = 1500;
		float hoverXPos = 330; 
		float hoverYPos = 35; 
		float consoleXPos = 1200;
		float consoleYPos = 5;
		
		// these should be wrapped as components that accept a character
		characterHealth = initBar(0, 1, .05f, false, skin, 350, character.getHealthPercent(), barX , 1035);
		healthIcon = initImage(assetManager.get(character.getHealthDisplay()), barX+3, 1042.5f);
		healthLabel = initLabel(character.getCurrentHealth() + " / " + character.getMaxHealth(), skin, Color.BROWN, barX + 75, 1038);
		
		characterStamina = initBar(0, 1, .05f, false, skin, 350, character.getStaminaPercent(), barX, 990);
		staminaIcon = initImage(assetManager.get(character.getStaminaDisplay()), barX + 7.5f, 997.5f);
		staminaLabel = initLabel(character.getCurrentStamina() + " / " + character.getMaxStamina(), skin, Color.BROWN, barX + 75, 993);

		characterBalance = initBar(0, 1, .05f, false, skin, 350, character.getBalancePercent(), barX, 945);
		balanceIcon = initImage(assetManager.get(character.getBalanceDisplay()), barX + 3, 952.5f);
		balanceLabel = initLabel(character.getStability(), skin, Color.BROWN, barX + 75, 948);
		
		if (character.hasMagic()) {
			characterMana = initBar(0, 1, .05f, false, skin, 350, character.getManaPercent(), barX, 900);
			manaIcon = initImage(assetManager.get(character.getManaDisplay()), barX + 3, 912.5f);
			manaLabel = initLabel(character.getCurrentMana() + " / " + character.getMaxMana(), skin, Color.BROWN, barX + 75, 903);
		}
		else {
			characterMana = null;
			manaIcon = null;
			manaLabel = null;
		}
		
		enemyHealth = initBar(0, 1, .05f, false, skin, 350, enemy.getHealthPercent(), enemyBarX , 1035);
		enemyHealthIcon = initImage(assetManager.get(enemy.getHealthDisplay()), enemyBarX + 3, 1042.5f);
		enemyHealthLabel = initLabel(enemy.getCurrentHealth() + " / " + enemy.getMaxHealth(), skin, Color.BROWN, enemyBarX + 75, 1038);
		
		enemyStamina = initBar(0, 1, .05f, false, skin, 350, enemy.getStaminaPercent(), enemyBarX, 990);
		enemyStaminaIcon = initImage(assetManager.get(enemy.getStaminaDisplay()), enemyBarX + 7.5f, 997.5f);
		enemyStaminaLabel = initLabel(enemy.getCurrentStamina() + " / " + enemy.getMaxStamina(), skin, Color.BROWN, enemyBarX + 75, 993);

		if (character.getBattlePerception() < 4) {
			enemyStamina.addAction(Actions.hide());
			enemyStaminaIcon.addAction(Actions.hide());
			enemyStaminaLabel.addAction(Actions.hide());
		}
		
		enemyBalance = initBar(0, 1, .05f, false, skin, 350, enemy.getBalancePercent(), enemyBarX, 945);
		enemyBalanceIcon = initImage(assetManager.get(enemy.getBalanceDisplay()), enemyBarX + 3, 952.5f);
		enemyBalanceLabel = initLabel(enemy.getStability(), skin, Color.BROWN, enemyBarX + 75, 948);
		
		if (character.getBattlePerception() < 3) {
			enemyBalance.addAction(Actions.hide());
			enemyBalanceIcon.addAction(Actions.hide());
			enemyBalanceLabel.addAction(Actions.hide());
		}
		
		enemyWeaponLabel = initLabel("Weapon: " + (enemy.getWeapon() != null ? enemy.getWeapon().getName() : "Unarmed"), skin, Color.GOLDENROD, 1578, 900);	
		
		Texture armorTexture = assetManager.get(AssetEnum.ARMOR.getTexture());
		initImage(armorTexture, barX + 320, 1032, 50);
		initImage(armorTexture, 1820, 1032, 50);
		armorLabel = initLabel("" + character.getDefense(), skin, Color.BROWN, barX + 332, 1037);		
		enemyArmorLabel = initLabel("" + enemy.getDefense(), skin, Color.BROWN, 1832, 1037);
		
		Texture bloodTexture = assetManager.get(AssetEnum.BLEED.getTexture());
		bloodImage = initImage(bloodTexture, 470, 850, 50);
		enemyBloodImage = initImage(bloodTexture, 1860, 963, 50);
		bloodLabel = initLabel("" + character.getBleed(), skin, Color.RED, 470 + 8, 850 + 1);	
		bloodLabel.setAlignment(Align.center);
		enemyBloodLabel = initLabel("" + enemy.getBleed(), skin, Color.RED, 1860 + 8, 963 + 1);
		enemyBloodLabel.setAlignment(Align.center);
		
		if (character.getBleed() == 0) {
			bloodImage.addAction(hide());
			bloodLabel.addAction(hide());
		}
		if (enemy.getBleed() == 0) {
			enemyBloodImage.addAction(hide());
			enemyBloodLabel.addAction(hide());
		}
		
		Image consoleBox = new Image(assetManager.get(AssetEnum.BATTLE_TEXTBOX.getTexture()));
		uiGroup.addActor(consoleBox);
		consoleBox.setPosition(consoleXPos, consoleYPos);
		
		dialogGroup = new Group();
		
		Image dialogBox = new Image(assetManager.get(AssetEnum.BATTLE_TEXTBOX.getTexture()));
		dialogGroup.addActor(dialogBox);
		dialogBox.setBounds(consoleXPos + 140, consoleYPos + 425, 415, 150);
		
		hoverImage = new Image(assetManager.get(AssetEnum.BATTLE_HOVER.getTexture()));
		hoverImage.setBounds(hoverXPos, hoverYPos, hoverImage.getWidth() + 100, hoverImage.getHeight() + 100);
		hoverGroup = new Group();
		hoverGroup.addActor(hoverImage);
		
		characterPortrait = initImage(assetManager.get(character.popPortraitPath()), -7.5f, 922);
		characterPortrait.setScale(.9f);

		characterPortrait.addListener(getListener(character));
		
		masculinityIcon = initImage(assetManager.get(character.getMasculinityPath()), barX - 175, 790);
		masculinityIcon.setScale(.15f);
		
		characterArousal = initImage(assetManager.get(character.getLustImagePath()), 150, 735, 150, 150);
		enemyArousal = initImage(assetManager.get(enemy.getLustImagePath()), 1078 * 1.5f, 735, 150, 150);
		
		characterBelly = initImage(assetManager.get(character.getCumInflationPath()), 0, 850, 100, 100); 
		
		initStanceActor(new StanceActor(character), 600.5f, 880, 150, 172.5f);
		initStanceActor(new StanceActor(enemy), 1305, 880, 150, 172.5f);
		
		hoverGroup.addAction(Actions.visible(false));
		uiGroup.addActor(hoverGroup);
			
		
		techniqueTable = new Table();
		techniquePane = new ScrollPane(techniqueTable);
		techniquePane.setScrollingDisabled(true, false);
		techniquePane.setOverscroll(false, false);
		techniquePane.setBounds(-150, 0, 600, 700);
		uiGroup.addActor(techniquePane);
		
		displayTechniqueOptions();
		
		Texture slashSheet = assetManager.get(AssetEnum.SLASH.getTexture());
		Array<TextureRegion> frames = new Array<TextureRegion>();
		for (int ii = 0; ii < 6; ii++) {
			frames.add(new TextureRegion(slashSheet, ii * 512, 0, 512, 512));
		}
		
		Animation animation = new Animation(.07f, frames);
		slash = new AnimatedImage(animation, Scaling.fit, Align.right);
		slash.setState(1);
		
		slash.setPosition(700, 500);
		this.addActor(slash);
		
		this.addActor(uiGroup);
		console = new Label(consoleText, skin);
		console.setWrap(true);
		console.setColor(Color.BLACK);
		console.setAlignment(Align.top);
		ScrollPane pane = new ScrollPane(console);
		pane.setBounds(consoleXPos+50, 50, 625, 350);
		pane.setScrollingDisabled(true, false);
		pane.setOverscroll(false, false);
		
		uiGroup.addActor(pane);
		
		dialog = new Label(dialogText, skin);
		dialog.setWrap(true);
		dialog.setColor(Color.PURPLE);
		dialog.setAlignment(Align.top);
		ScrollPane paneDialog = new ScrollPane(dialog);
		paneDialog.setBounds(consoleXPos + 150, 350, 400, 220);
		paneDialog.setScrollingDisabled(true, false);
	
		dialogGroup.addActor(paneDialog);
		
		uiGroup.addActor(dialogGroup);
		if (dialogText.isEmpty()) {
			dialogGroup.addAction(hide());
		}
		
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
		pane2.add(bonusDisplay).width(600).row();
		
		penaltyDisplay = new Label("", skin);
		penaltyDisplay.setWrap(true);
		penaltyDisplay.setColor(Color.RED);
		penaltyDisplay.setAlignment(Align.top);
		pane2.add(penaltyDisplay).width(600);
		
		pane2.setBounds(hoverXPos + 80, hoverYPos - 155, 600, 700);
		hoverGroup.addActor(pane2);
		hideHoverGroup();
		checkEndBattle();
		
		setEnemyTechnique();
		
		saveService.saveDataValue(SaveEnum.PLAYER, character);
		saveService.saveDataValue(SaveEnum.ENEMY, enemy);
		Array<String> consoleComponents = new Array<String>();
		consoleComponents.add(consoleText);
		consoleComponents.add(dialogText);
		saveService.saveDataValue(SaveEnum.CONSOLE, consoleComponents);
	}
	public AssetDescriptor<Music> getMusicPath() {
		return musicPath;
	}
	
	public void battleLoop() {
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
					uiGroup.addAction(show());
				}
				else {
					uiGroup.addAction(hide());
				}
				uiHidden = !uiHidden;
			}
			
			if (Gdx.input.isKeyJustPressed(Keys.SHIFT_LEFT)) {
				enemy.toggle();
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
				resolveTechniques(character, selectedTechnique, enemy, enemySelectedTechnique);
				selectedTechnique = null;
				displayTechniqueOptions();
				saveService.saveDataValue(SaveEnum.PLAYER, character);
				saveService.saveDataValue(SaveEnum.ENEMY, enemy);
				Array<String> consoleComponents = new Array<String>();
				consoleComponents.add(consoleText);
				consoleComponents.add(dialogText);
				saveService.saveDataValue(SaveEnum.CONSOLE, consoleComponents);
			}

			checkEndBattle();
		}
		else {
			if(Gdx.input.isKeyJustPressed(Keys.ENTER) || Gdx.input.isKeyJustPressed(Keys.SPACE)) {
	        	battleOver = true;
	        }
		}
	}
	
	private void displayTechniqueOptions() {
		techniqueTable.clear();
		Array<Technique> options = character.getPossibleTechniques(enemy);
		optionButtons = new Array<TextButton>();
		
		for (int ii = 0; ii < options.size; ii++) {
			Technique option = options.get(ii);
			SkillButton button = new SkillButton(option.getTechniqueName() + (ii >= POSSIBLE_KEYS_CHAR.length ? "" : " ("+POSSIBLE_KEYS_CHAR[ii]+")"), skin, assetManager.get(option.getStance().getTexture()));
			techniqueTable.add(button).size(440, 76).row();
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
		techniqueTable.setFillParent(false);
		techniqueTable.align(Align.top);
		techniquePane.setPosition(-275, 0);
        techniquePane.addAction(Actions.moveBy(125, 0, .1f));
        newSelection(0);
	}
	
	private Array<Attack> doAttacks(AbstractCharacter character, Array<Attack> attacks) {
		Array<Attack> resolvedAttacks = new Array<Attack>();
		for (Attack attack : attacks) {
			resolvedAttacks.add(character.doAttack(attack));
		}
		return resolvedAttacks;
	}
	
	//  may need to be broken up into its own class or have much of it refactored into AbstractCharacter for loose coupling
	private void resolveTechniques(AbstractCharacter firstCharacter, Technique firstTechnique, AbstractCharacter secondCharacter, Technique secondTechnique) {
		consoleText = "";
		dialogText = "";
		
		// cache player character's stance from the previous turn; playerCharacter will cache stance at the start of this turn
		Stance oldStance = firstCharacter.getStance();
		Stance oldEnemyStance = secondCharacter.getStance();
		
		// no mutations occur here - characters return their stance modification text
		printToConsole(firstCharacter.getStanceTransform(firstTechnique));
		printToConsole(secondCharacter.getStanceTransform(secondTechnique));
		
		// first mutation - characters lose stamina, stability, mana, cache their current stance, change their current stance, 
		firstCharacter.extractCosts(firstTechnique);
		secondCharacter.extractCosts(secondTechnique);
	
		// receive the final state information from each character to apply to their attacks
		Array<Attack> attacksForFirstCharacter = doAttacks(secondCharacter, secondTechnique.resolve(firstTechnique));
		Array<Attack> attacksForSecondCharacter = doAttacks(firstCharacter, firstTechnique.resolve(secondTechnique));
		
		// final mutations - attacks are applied to each character, their cached state within the techniques makes the ordering her irrelevant
		for (Attack attackForFirstCharacter : attacksForFirstCharacter) {
			Array<Array<String>> results = firstCharacter.receiveAttack(attackForFirstCharacter);
			printToConsole(results.get(0));
			printToDialog(results.get(1));
		}
		
		for (Attack attackForSecondCharacter : attacksForSecondCharacter) {
			Array<Array<String>> results = secondCharacter.receiveAttack(attackForSecondCharacter);
			printToConsole(results.get(0));
			printToDialog(results.get(1));
		}
		
		for (Attack attackForFirstCharacter : attacksForFirstCharacter) {
			if (enemy.getStance() == Stance.CASTING && attackForFirstCharacter.isSuccessful()) {
				this.addAction(new SoundAction(soundMap.get(AssetEnum.INCANTATION), .5f));
			}
			if (attackForFirstCharacter.isSpell()) {
				this.addAction(new SoundAction(soundMap.get(AssetEnum.FIREBALL_SOUND), .5f));
			}
			if (attackForFirstCharacter.isAttack()) {
				slash.setState(0);
				if (!attackForFirstCharacter.isSuccessful()) {
					this.addAction(new SoundAction(soundMap.get(AssetEnum.ATTACK_SOUND), .5f));
					if (attackForFirstCharacter.getStatus() == Status.PARRIED) {
						this.addAction(sequence(delay(5/60f), new SoundAction(soundMap.get(AssetEnum.PARRY_SOUND), .5f)));
					}
				}
				else {
					if (!attackForFirstCharacter.isSpell()) {
						if (attackForFirstCharacter.getStatus() == Status.BLOCKED) {
							this.addAction(sequence(delay(5/60f), new SoundAction(soundMap.get(AssetEnum.BLOCK_SOUND), 1.5f)));
						}
						else {
							if (enemy.getWeapon() != null && enemy.getWeapon().causesBleed()) {
								this.addAction(sequence(delay(5/60f), new SoundAction(soundMap.get(AssetEnum.SWORD_SLASH_SOUND), 1.5f)));
							}
							else {
								this.addAction(sequence(delay(20/60f), new SoundAction(soundMap.get(AssetEnum.HIT_SOUND), .3f)));
							}
						}
					}
					enemy.attackAnimation();
				}
			}
			
			if (attackForFirstCharacter.isAttack() && attackForFirstCharacter.isSuccessful() && attackForFirstCharacter.getStatus() != Status.BLOCKED) {
				characterPortrait.setDrawable(getDrawable(AssetEnum.PORTRAIT_HIT.getTexture()));
				characterPortrait.addAction(Actions.sequence(Actions.moveBy(-10, -10), Actions.delay(.1f), Actions.moveBy(0, 20), Actions.delay(.1f), Actions.moveBy(20, -20), Actions.delay(.1f), Actions.moveBy(0, 20), Actions.delay(.1f), Actions.moveTo(-5 * 1.5f, 615 * 1.5f),  
					new Action() {
						@Override
						public boolean act(float delta) {
							characterPortrait.setDrawable(getDrawable(character.popPortraitPath()));	
							return true;
						}
					})
				);
			}
			else {
				characterPortrait.setDrawable(getDrawable(character.popPortraitPath()));	
			}
		}
		
		for (Attack attackForSecondCharacter : attacksForSecondCharacter) {
			if (character.getStance() == Stance.CASTING && attackForSecondCharacter.isSuccessful()) {
				this.addAction(sequence(delay(5/60f), new SoundAction(soundMap.get(AssetEnum.INCANTATION), .5f)));
			}
			if (attackForSecondCharacter.isSpell()) {
				this.addAction(sequence(delay(5/60f), new SoundAction(soundMap.get(AssetEnum.FIREBALL_SOUND), .5f)));
			}
			if (attackForSecondCharacter.isAttack()) {
				if (!attackForSecondCharacter.isSuccessful()) {
					this.addAction(sequence(delay(15/60f), new SoundAction(soundMap.get(AssetEnum.ATTACK_SOUND), .5f)));
					if (attackForSecondCharacter.getStatus() == Status.PARRIED) {
						this.addAction(sequence(delay(5/60f), new SoundAction(soundMap.get(AssetEnum.PARRY_SOUND), .5f)));
					}
				}
				else {
					if (!attackForSecondCharacter.isSpell()) {
						if (attackForSecondCharacter.getStatus() == Status.BLOCKED) {
							this.addAction(sequence(delay(5/60f), new SoundAction(soundMap.get(AssetEnum.BLOCK_SOUND), .5f)));
						}
						else if (character.getWeapon() != null) {
							this.addAction(sequence(delay(5/60f), new SoundAction(soundMap.get(AssetEnum.SWORD_SLASH_SOUND), .5f)));
						}
						else {
							this.addAction(sequence(delay(5/60f), new SoundAction(soundMap.get(AssetEnum.HIT_SOUND), .3f)));
						}
					}
					enemy.hitAnimation();
				}
			}
		}
		
		if (dialogText.isEmpty()) {
			dialogGroup.addAction(hide());
		}
		else {
			dialogGroup.addAction(show());
		}
		
		if ( (oldStance.isAnalReceptive() && firstCharacter.getStance().isAnalReceptive()) || (oldEnemyStance.isAnalReceptive() && secondCharacter.getStance().isAnalReceptive()) ) {
			soundMap.get(AssetEnum.THWAPPING).play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
		}
		
		if ((oldStance.isAnalReceptive() && !firstCharacter.getStance().isAnalReceptive()) || (oldEnemyStance.isAnalReceptive() && !secondCharacter.getStance().isAnalReceptive())) {
			soundMap.get(AssetEnum.THWAPPING).play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
			this.addAction(sequence(delay(105/60f), new SoundAction(soundMap.get(AssetEnum.UNPLUGGED_POP), .3f)));
		}
		
		if ((oldStance.isOralReceptive() && !firstCharacter.getStance().isOralReceptive()) || (oldEnemyStance.isOralReceptive() && !secondCharacter.getStance().isOralReceptive())) {
			soundMap.get(AssetEnum.MOUTH_POP).play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume"));
		}
		
		// temporary measure to ensure erotic stances are never one-sided
		if (firstCharacter.getStance().isErotic() != secondCharacter.getStance().isErotic()) {
			if (firstCharacter.getStance().isErotic()) {
				firstCharacter.setStance(Stance.BALANCED);
			}
			else {
				secondCharacter.setStance(Stance.BALANCED);
			}
		}
		
		// this needs to be secondCharacter.getOutcome() or something similar
		Outcome battleOutcome = ((EnemyCharacter) secondCharacter).getOutcome(firstCharacter);
		if (battleOutcome != null) {
			battleOutcomeDecided = true;
			outcome = battleOutcome; 
			skillDisplay.setText(((EnemyCharacter) secondCharacter).getOutcomeText(firstCharacter));
		}
		
		setEnemyTechnique();
		
		console.setText(consoleText);
		dialog.setText(dialogText);
		
		characterHealth.setValue(character.getHealthPercent());
		characterStamina.setValue(character.getStaminaPercent());
		characterBalance.setValue(character.getBalancePercent());
		if (character.hasMagic()) {
			characterMana.setValue(character.getManaPercent());
			manaLabel.setText(character.getCurrentMana() + " / " + character.getMaxMana());
			manaIcon.setDrawable(getDrawable(character.getManaDisplay()));
		}
		
		enemyHealth.setValue(enemy.getHealthPercent());
		enemyStamina.setValue(enemy.getStaminaPercent());
		enemyBalance.setValue(enemy.getBalancePercent());
		healthLabel.setText(character.getCurrentHealth() + " / " + character.getMaxHealth());
		staminaLabel.setText(character.getCurrentStamina() + " / " + character.getMaxStamina());
		balanceLabel.setText(character.getStability());
		enemyHealthLabel.setText(enemy.getCurrentHealth() + " / " + enemy.getMaxHealth());
		enemyStaminaLabel.setText(enemy.getCurrentStamina() + " / " + enemy.getMaxStamina());
		enemyBalanceLabel.setText(enemy.getStability());
		enemyWeaponLabel.setText("Weapon: " + (enemy.getWeapon() != null ? enemy.getWeapon().getName() : "Unarmed"));
		armorLabel.setText("" + character.getDefense());
		enemyArmorLabel.setText("" + enemy.getDefense());	
		bloodLabel.setText("" + character.getBleed());
		enemyBloodLabel.setText("" + enemy.getBleed());	
		
		if (character.getBleed() == 0) {
			bloodImage.addAction(hide());
			bloodLabel.addAction(hide());
		}
		else {
			bloodImage.addAction(show());
			bloodLabel.addAction(show());
		}
		if (enemy.getBleed() == 0) {
			enemyBloodImage.addAction(hide());
			enemyBloodLabel.addAction(hide());
		}
		else {
			enemyBloodImage.addAction(show());
			enemyBloodLabel.addAction(show());
		}
		
		characterBelly.setDrawable(getDrawable(character.getCumInflationPath()));
		characterArousal.setDrawable(getDrawable(character.getLustImagePath()));
		enemyArousal.setDrawable(getDrawable(enemy.getLustImagePath()));
		healthIcon.setDrawable(getDrawable(character.getHealthDisplay()));
		staminaIcon.setDrawable(getDrawable(character.getStaminaDisplay()));
		balanceIcon.setDrawable(getDrawable(character.getBalanceDisplay()));
		enemyHealthIcon.setDrawable(getDrawable(enemy.getHealthDisplay()));
		enemyStaminaIcon.setDrawable(getDrawable(enemy.getStaminaDisplay()));
		enemyBalanceIcon.setDrawable(getDrawable(enemy.getBalanceDisplay()));
				
		masculinityIcon.setDrawable(getDrawable(character.getMasculinityPath()));	
	}
	
	private void setEnemyTechnique() {
		enemySelectedTechnique = enemy.getTechnique(character);
		uiGroup.removeActor(enemySkill);
		enemySkill = new SkillText(character.getBattlePerception() < 7 ? "" : enemySelectedTechnique.getTechniqueName(), skin, assetManager.get(enemySelectedTechnique.getStance().getTexture()));		
		uiGroup.addActor(enemySkill);
		enemySkill.setPosition(1400, 750);
		if (character.getBattlePerception() < 5) {
			enemySkill.addAction(Actions.hide());
		}
	}
	
	private TextureRegionDrawable getDrawable(AssetDescriptor<Texture> AssetInfo) {
		return new TextureRegionDrawable(new TextureRegion(assetManager.get(AssetInfo)));
	}
	
	private void printToConsole(Array<String> results) {
		for (String result: results) {
			printToConsole(result);
		}
	}
	
	private void printToConsole(String result) { 
		consoleText += result + "\n";
	}

	private void printToDialog(Array<String> results) {
		for (String result: results) {
			printToDialog(result);
		}
	}
	
	private void printToDialog(String result) { 
		dialogText += result + "\n";
	}
	
	private void changeSelection(int newSelection) {
		if (selection == newSelection) return;
		optionButtons.get(selection).addAction(Actions.sequence(Actions.delay(.05f), Actions.moveBy(-50, 0)));
    	newSelection(newSelection);
	}
	
	private void newSelection(int newSelection) {
		techniquePane.setScrollY(newSelection * 76);
    	optionButtons.get(newSelection).addAction(Actions.sequence(Actions.delay(.05f), Actions.moveBy(50, 0)));
		selection = newSelection;
	}
	
	private void checkEndBattle() {
		Outcome battleOutcome = enemy.getOutcome(character);
		if (battleOutcome != null) {
			battleOutcomeDecided = true;
			outcome = battleOutcome; 
			skillDisplay.setText(enemy.getOutcomeText(character));
			character.refresh();
			bonusDisplay.setText("");
			uiGroup.removeActor(techniquePane);
			hoverGroup.clearActions();
			hoverGroup.addAction(visible(true));
			hoverGroup.addAction(moveTo(300, 380));
			hoverGroup.addAction(fadeIn(.1f));
			this.addListener(
				new ClickListener() {
			        @Override
			        public void clicked(InputEvent event, float x, float y) {
			        	battleOver = true;
			        	saveService.saveDataValue(SaveEnum.CONSOLE, new Array<String>());
			        }
				}
			);
		}
	}
	
	/* Helper methods */
	private Texture getStanceImage(Stance stance) {
		return assetManager.get(stance.getTexture());
	}
	
	private ClickListener getListener(final PlayerCharacter character) {
		return new ClickListener() {
	        @Override
	        public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
	        	if (onload) {
	        		onload = false;
	        	}
	        	else {
					skillDisplay.setText(character.getStatTextDisplay());
					bonusDisplay.setText(character.getStatBonusDisplay());
					penaltyDisplay.setText(character.getStatPenaltyDisplay());
					showHoverGroup();
	        	}
			}
			@Override
	        public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
				hideHoverGroup();
			}
	    };	
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
				bonusDisplay.setColor(Color.FOREST);
				penaltyDisplay.setText("");
				changeSelection(index);		
				showHoverGroup();
			}
			@Override
	        public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
				hideHoverGroup();
			}
	    };
	}
	
	private void showHoverGroup() {
		hoverGroup.clearActions();
		hoverGroup.addAction(visible(true));
		hoverGroup.addAction(alpha(1));
	}
	
	private void hideHoverGroup() {
		if (!battleOutcomeDecided) {
			hoverGroup.addAction(fadeOut(2f));
		}
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
		uiGroup.addActor(newBar);
		return newBar;
	}
	
	private Image initImage(Texture texture, float x, float y) { return initImage(texture, x, y, texture.getWidth(), texture.getHeight()); }
	private Image initImage(Texture texture, float x, float y, float height) { return initImage(texture, x, y,  (texture.getWidth() / (texture.getHeight() / (1.0f * height))), height); }	
	private Image initImage(Texture texture, float x, float y, float width, float height) {
		Image newImage = new Image(texture);
		newImage.setBounds(x, y, width, height);
		uiGroup.addActor(newImage);
		return newImage;
	}
	
	private Label initLabel(String value, Skin skin, Color color, float x, float y) {
		Label newLabel = new Label(value, skin);
		newLabel.setColor(color);
		newLabel.setPosition(x, y);
		uiGroup.addActor(newLabel);
		return newLabel;
	}
	
	private StanceActor initStanceActor(StanceActor actor, float x, float y, float width, float height) {
		uiGroup.addActor(actor);
		actor.setBounds(x, y, width, height);
		return actor;
	}
	
	public enum Outcome {
		VICTORY, DEFEAT, KNOT, SATISFIED, SUBMISSION
	}
	
	/* REFACTOR AND REMOVE BEYOND THIS LINE */
	

	public boolean isBattleOver() {
		return battleOver;
	}
	
	public int getOutcomeScene() {
		return outcomes.get(outcome.toString());
	}
	
	private class StanceActor extends Actor{
		
		private final AbstractCharacter character;
		private final Texture hoverBox;
		private boolean hover;
		public StanceActor(AbstractCharacter character) {
			this.character = character;
			hover = false;
			hoverBox = (Texture) assetManager.get(AssetEnum.BATTLE_HOVER.getAsset());
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
	
	private class SkillText extends Label {
		Texture stanceIcon;
		public SkillText(String text, Skin skin, Texture stanceIcon) {
			super("Next:\n" + text, skin);
			this.stanceIcon = stanceIcon;
		}
		
		@Override
	    public void draw(Batch batch, float parentAlpha) {
			super.draw(batch, parentAlpha);
			batch.setColor(Color.WHITE);
			batch.draw(stanceIcon, getX() + 75, getY()+30, 63, 76);
		}		
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
	
	private class SoundAction extends Action {
		private final Sound sound;
		private final float volume;
		
		private SoundAction(Sound sound, float volume) {
			this.sound = sound;
			this.volume = volume;
		}

		@Override
		public boolean act(float delta) {
			sound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") * volume);
			return true;
		}
	}
}

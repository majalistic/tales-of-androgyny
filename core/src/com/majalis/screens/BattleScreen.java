package com.majalis.screens;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;
import static com.majalis.asset.AssetEnum.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.OrderedMap;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.majalis.asset.AnimatedActor;
import com.majalis.asset.AnimatedImage;
import com.majalis.asset.AnimationBuilder;
import com.majalis.asset.AnimationEnum;
import com.majalis.asset.AssetEnum;
import com.majalis.battle.Battle;
import com.majalis.battle.BattleAttributes;
import com.majalis.character.AbstractCharacter;
import com.majalis.character.Armor;
import com.majalis.character.Attack;
import com.majalis.character.BalanceBar;
import com.majalis.character.DisplayWidget;
import com.majalis.character.EnemyCharacter;
import com.majalis.character.GrappleStatus;
import com.majalis.character.HealthBar;
import com.majalis.character.ManaBar;
import com.majalis.character.MasculinityDisplay;
import com.majalis.character.PlayerCharacter;
import com.majalis.character.StaminaBar;
import com.majalis.character.Stance;
import com.majalis.character.StatusType;
import com.majalis.character.Technique;
import com.majalis.character.AbstractCharacter.AttackResult;
import com.majalis.character.Attack.Status;
import com.majalis.save.MutationResult;
import com.majalis.save.SaveEnum;
import com.majalis.save.SaveManager;
import com.majalis.save.SaveService;
import com.majalis.scenes.MutationActor;
/*
 * Screen for displaying battles.
 */
public class BattleScreen extends AbstractScreen{

	public static final Array<AssetDescriptor<?>> resourceRequirements = new Array<AssetDescriptor<?>>();
	public static Array<AssetDescriptor<?>> requirementsToDispose = new Array<AssetDescriptor<?>>();
	static {
		resourceRequirements.add(BATTLE_SKIN.getSkin());
		resourceRequirements.add(BATTLE_MUSIC.getMusic());
		AssetEnum[] soundAssets = new AssetEnum[]{
			CUM, BUTTON_SOUND, UNPLUGGED_POP, MOUTH_POP, ATTACK_SOUND, SWORD_SLASH_SOUND, FIREBALL_SOUND, INCANTATION, HIT_SOUND, THWAPPING, PARRY_SOUND, BLOCK_SOUND
		};
		for (AssetEnum asset: soundAssets) {
			resourceRequirements.add(asset.getSound());
		}
		
		// need to refactor to get all stance textures
		AssetEnum[] assets = new AssetEnum[]{
			NULL, SLASH, BATTLE_HOVER, BATTLE_TEXTBOX, BATTLE_UI, BLEED, ARMOR_0, ARMOR_1, HEART, ELF, SEARCHING, XRAY, 
			GRAPPLE_HOLD, GRAPPLE_DOMINANT, GRAPPLE_ADVANTAGE, GRAPPLE_SCRAMBLE, GRAPPLE_DISADVANTAGE, GRAPPLE_SUBMISSION, GRAPPLE_HELD, GRAPPLE_BACKGROUND,
			GRAPPLE_HOLD_INACTIVE, GRAPPLE_DOMINANT_INACTIVE, GRAPPLE_ADVANTAGE_INACTIVE, GRAPPLE_SCRAMBLE_INACTIVE, GRAPPLE_DISADVANTAGE_INACTIVE, GRAPPLE_SUBMISSION_INACTIVE, GRAPPLE_HELD_INACTIVE,
			NULL_STANCE, ANAL, BLITZ, BALANCED, CASTING, COUNTER, DEFENSIVE, STONEWALL, BERSERK, HAYMAKER, FOCUS, SEDUCTION, DOGGY, DOGGY_OTHER, ERUPT, FELLATIO, FULL_NELSON, GROUND_WRESTLE, KNEELING, HANDS_AND_KNEES, HANDY, ITEM, COWGIRL, OFFENSIVE, OUROBOROS, FACEFUCK, PRONE, SUPINE, STANDING, AIRBORNE, FACE_SITTING, SIXTY_NINE, KNOTTED, SPREAD, PENETRATED, PRONEBONE, REVERSE_COWGIRL, WRAPPED,
			PORTRAIT_NEUTRAL, PORTRAIT_AHEGAO, PORTRAIT_FELLATIO, PORTRAIT_MOUTHBOMB, PORTRAIT_GRIN, PORTRAIT_HIT, PORTRAIT_LOVE, PORTRAIT_LUST, PORTRAIT_SMILE, PORTRAIT_SURPRISE, PORTRAIT_GRIMACE, PORTRAIT_POUT, PORTRAIT_HAPPY, 
			PORTRAIT_NEUTRAL_FEMME, PORTRAIT_AHEGAO_FEMME, PORTRAIT_FELLATIO_FEMME, PORTRAIT_MOUTHBOMB_FEMME, PORTRAIT_GRIN_FEMME, PORTRAIT_HIT_FEMME, PORTRAIT_LOVE_FEMME, PORTRAIT_LUST_FEMME, PORTRAIT_SMILE_FEMME, PORTRAIT_SURPRISE_FEMME, PORTRAIT_GRIMACE_FEMME, PORTRAIT_POUT_FEMME, PORTRAIT_HAPPY_FEMME, 
			HEALTH_ICON_0, STAMINA_ICON_0, BALANCE_ICON_0, MANA_ICON_0, HEALTH_ICON_1, STAMINA_ICON_1, BALANCE_ICON_1, MANA_ICON_1, HEALTH_ICON_2, STAMINA_ICON_2, BALANCE_ICON_2, MANA_ICON_2, HEALTH_ICON_3, STAMINA_ICON_3, BALANCE_ICON_3, MANA_ICON_3, 
			MARS_ICON_0, MARS_ICON_1, MARS_ICON_2, MARS_ICON_3, MARS_ICON_4, DONG_ANIMATION, ARMOR_DOLL, W, L, S, O, A, K, D
		};
		for (AssetEnum asset: assets) {
			resourceRequirements.add(asset.getTexture());
		}
		
		resourceRequirements.add(AssetEnum.BELLY_ANIMATION.getAnimation());
		resourceRequirements.add(AssetEnum.TRUDY_SPRITE_ANIMATION.getAnimation());
		
	}
	// this needs to be refactored so that it accepts the number keys 
	private static int[] POSSIBLE_KEYS = new int[]{Keys.A, Keys.S, Keys.D, Keys.F, Keys.G, Keys.H, Keys.J, Keys.K, Keys.L, Keys.Z, Keys.X, Keys.C, Keys.V, Keys.B, Keys.N};
	private static char[] POSSIBLE_KEYS_CHAR = new char[]{'A','S','D','F','G','H','J','K','L','Z','X','C','V', 'B', 'N'};
	
	private final Stage uiStage;
	private final PerspectiveCamera camera;
	private final InputMultiplexer multi;
	
	private final PlayerCharacter character;
	private final EnemyCharacter enemy;
	private final SaveService saveService;
	private final AssetManager assetManager;
	private final BitmapFont font;
	private final ObjectMap<String, Integer> outcomes;
	private final ScrollPane techniquePane;
	private final Table techniqueTable;
	private final Skin skin;
	private final Array<MutationResult> battleResults;
	private final AnimatedImage slash;
	private final ObjectMap<AssetEnum, Sound> soundMap;
	private final Group uiGroup;
	
	// basically all of this should go away
	private final Image characterPortrait;
	private final Group hoverGroup;
	private final Group dialogGroup;
	private final Table consoleTable;
	private final Table dialogTable;
	private final Label skillDisplay;
	private final Label bonusDisplay;
	private final Label penaltyDisplay;
	private final Image uiVisible;
	private final Image enemyToggle;
	
	private SkillText enemySkill;
	
	private Array<MutationResult> consoleContents;
	private Array<MutationResult> dialogContents;
	private Array<TextButton> optionButtons;
	private Technique selectedTechnique;
	private Technique enemySelectedTechnique;
	private int selection;	
	private Outcome outcome;
	public boolean gameExit;
	private boolean battleOutcomeDecided;
	private boolean battleOver;

	protected BattleScreen(ScreenFactory screenFactory, ScreenElements elements, SaveService saveService, Battle battle) {
		super(screenFactory, elements, battle.musicPath);
		this.saveService = saveService;
		this.assetManager = battle.assetManager;
		this.character = battle.character;
		this.enemy = battle.enemy;
		this.outcomes = battle.outcomes;
		this.battleResults = battle.battleResults;
		battleOver = false;
		battleOutcomeDecided = false;
		gameExit = false;	
		
		soundMap = new ObjectMap<AssetEnum, Sound>();
		AssetEnum[] battleSounds = new AssetEnum[]{AssetEnum.CUM, AssetEnum.UNPLUGGED_POP, AssetEnum.MOUTH_POP, AssetEnum.ATTACK_SOUND, AssetEnum.HIT_SOUND, AssetEnum.SWORD_SLASH_SOUND, AssetEnum.FIREBALL_SOUND, AssetEnum.INCANTATION, AssetEnum.THWAPPING, AssetEnum.BUTTON_SOUND, AssetEnum.PARRY_SOUND, AssetEnum.BLOCK_SOUND};
		for (AssetEnum soundPath: battleSounds) {
			soundMap.put(soundPath, assetManager.get(soundPath.getSound()));
		}
		
		this.addActor(battle.battleBackground);
		this.addCharacter(character);
		this.addCharacter(enemy);

		uiStage = new Stage(new FitViewport(this.getViewport().getWorldWidth(), this.getViewport().getWorldHeight(), new OrthographicCamera()), batch);
		
		camera = new PerspectiveCamera(70, 0, 1000);
		this.getViewport().setCamera(camera);
		
		camera.near = 1f;
		camera.far = 10000;
		camera.lookAt(0, 0, 0);
		camera.translate(960, 540, 771);
		this.getViewport().setCamera(camera);
		
		multi = new InputMultiplexer();
		multi.addProcessor(uiStage);
		multi.addProcessor(this);
		
		uiGroup = new Group();		
		uiStage.addActor(uiGroup);
		uiGroup.addActor(battle.battleUI);	

		skin = assetManager.get(AssetEnum.BATTLE_SKIN.getSkin());
		
		this.font = skin.getFont("default-font");
				
		float barX = 195;
		float enemyBarX = 1500;
		float hoverXPos = 330; 
		float hoverYPos = 35; 
		float consoleXPos = 1200;
		float consoleYPos = 5;
	
		initActor(new HealthBar(character, assetManager, skin), uiGroup, barX, 1035);
		initActor(new StaminaBar(character, assetManager, skin), uiGroup, barX, 990);
		initActor(new BalanceBar(character, assetManager, skin), uiGroup, barX, 945);
		if (character.hasMagic()) initActor(new ManaBar(character, assetManager, skin), uiGroup, barX, 900);
		initActor(new HealthBar(enemy, assetManager, skin), uiGroup, enemyBarX, 1035);
		if (character.getBattlePerception() >= 4) initActor(new StaminaBar(enemy, assetManager, skin), uiGroup, enemyBarX, 990);
		if (character.getBattlePerception() >= 3) initActor(new BalanceBar(enemy, assetManager, skin), uiGroup, enemyBarX, 945);
		initActor(new GrappleDisplay(character, assetManager), uiGroup, 765, 1005); 
		
		initActor(new WeaponDisplay(enemy), uiGroup, 1578, 900);
		
		Texture armorTexture = assetManager.get(AssetEnum.ARMOR_0.getTexture());
		Texture armorBrokenTexture = assetManager.get(AssetEnum.ARMOR_1.getTexture());
		Texture bloodTexture = assetManager.get(AssetEnum.BLEED.getTexture());	
		Texture armorDollTexture = assetManager.get(AssetEnum.ARMOR_DOLL.getTexture());
		// dolls - should create a class for this, including the bleed icon, status display, and armor
		initImage(armorDollTexture, barX + 150, 700, 250);
		initImage(armorDollTexture, 1425, 700, 250);
		
		initActor(new ArmorDisplay(character, character.getArmor(), armorTexture, armorBrokenTexture), uiGroup, barX + 224, 823);
		initActor(new ArmorDisplay(enemy, enemy.getArmor(), armorTexture, armorBrokenTexture), uiGroup, enemyBarX - 3, 823);
		initActor(new ArmorDisplay(character, character.getUnderwear(), armorTexture, armorBrokenTexture), uiGroup, barX + 224, 750);
		initActor(new ArmorDisplay(enemy, enemy.getUnderwear(), armorTexture, armorBrokenTexture), uiGroup, enemyBarX - 3, 750);
		initActor(new ArmorDisplay(character, character.getLegwear(), armorTexture, armorBrokenTexture), uiGroup, barX + 224, 770);
		initActor(new ArmorDisplay(enemy, enemy.getLegwear(), armorTexture, armorBrokenTexture), uiGroup, enemyBarX - 3, 770);
		initActor(new ArmorDisplay(character, character.getShield(), armorTexture, armorBrokenTexture), uiGroup, barX + 284, 810);
		initActor(new ArmorDisplay(enemy, enemy.getShield(), armorTexture, armorBrokenTexture), uiGroup, enemyBarX + 57, 810);		
		initActor(new BleedDisplay(character, bloodTexture), uiGroup, 325, 725);
		initActor(new BleedDisplay(enemy, bloodTexture), uiGroup, 1545, 725);
		
		if (character.getBattlePerception() >= 3) initActor(new OutcomeWidget(outcomes), uiGroup, 1835, 940); 
		Table statusTable = (Table) initActor(new Table(), uiGroup, 525,  850);
		statusTable.align(Align.topLeft);
		Table enemyStatusTable = (Table) initActor(new Table(), uiGroup, 1575,  700);
		enemyStatusTable.align(Align.topLeft);		
		statusTable.add(initActor(new StatusDisplay(character, skin), null, 550, 725)).align(Align.topLeft);
		enemyStatusTable.add(initActor(new StatusDisplay(enemy, skin), null, enemyBarX + 75, 725)).align(Align.topRight);
		
		initActor(new Image(assetManager.get(AssetEnum.BATTLE_TEXTBOX.getTexture())), uiGroup, consoleXPos, consoleYPos);
		dialogGroup = new Group();		
		initActor(new Image(assetManager.get(AssetEnum.BATTLE_TEXTBOX.getTexture())), dialogGroup, consoleXPos + 140, consoleYPos + 425, 415, 150);
		
		hoverGroup = new Group();
		Texture battleHover = assetManager.get(AssetEnum.BATTLE_HOVER.getTexture());
		initActor(new Image(battleHover), hoverGroup, hoverXPos, hoverYPos, battleHover.getWidth() + 100, battleHover.getHeight() + 100);
		
		characterPortrait = initImage(assetManager.get(character.popPortraitPath()), -7.5f, 922);
		characterPortrait.setScale(.9f);
		characterPortrait.addAction(Actions.sequence(Actions.delay(.5f), new Action() {@Override public boolean act(float delta) { characterPortrait.addListener(getListener(character)); return true; }}));
		
		initActor(new MasculinityDisplay(character, assetManager), uiGroup, barX - 195, 700);
		((AnimatedActor) initActor(character.getCock(assetManager), uiGroup, 0, 0)).setSkeletonPosition(225, 800);
		((AnimatedActor) initActor(enemy.getCock(assetManager), uiGroup, 0, 0)).setSkeletonPosition(1700, 800);
		((AnimatedActor) initActor(character.getBelly(assetManager), uiGroup, 0, 0)).setSkeletonPosition(75, 825);
		
		initActor(new StanceActor(character), uiGroup, 600.5f, 880, 150, 172.5f);
		initActor(new StanceActor(enemy), uiGroup, 1305, 880, 150, 172.5f);
		
		hoverGroup.addAction(Actions.hide());
		uiGroup.addActor(hoverGroup);
			
		techniqueTable = new Table();
		techniquePane = (ScrollPane) initActor(new ScrollPane(techniqueTable), uiGroup, -150, 0, 600, 700);
		techniquePane.setScrollingDisabled(true, false);
		techniquePane.setOverscroll(false, false);
		
		displayTechniqueOptions();
	
		slash = (AnimatedImage) initActor(new AnimatedImage(new AnimationBuilder(assetManager.get(AssetEnum.SLASH.getTexture()), 6, 384, 384).build(), Scaling.fit, Align.right), null, 764, 564);
		slash.setState(1);	
		
		this.consoleTable = new Table();		
		consoleTable.align(Align.top);		
		consoleContents = battle.consoleText;
	
		ScrollPane pane = (ScrollPane) initActor(new ScrollPane(consoleTable), uiGroup, consoleXPos + 25, 50, 650, 350);
		pane.setScrollingDisabled(true, false);
		pane.setOverscroll(false, false);
		
		dialogTable = new Table();
		dialogTable.align(Align.top);
		dialogContents = battle.dialogText;
		
		setTables();	
		
		ScrollPane paneDialog = (ScrollPane) initActor(new ScrollPane(dialogTable), dialogGroup, consoleXPos + 150, 350, 400, 220);
		paneDialog.setScrollingDisabled(true, false);

		uiGroup.addActor(dialogGroup);
		if (battle.dialogText.size == 0 || battle.dialogText.get(0).getText().equals("")) {
			dialogGroup.addAction(Actions.hide());
		}
		
		skillDisplay = initLabel("", skin, true, Align.top, Color.BLACK);
		Table pane2 = (Table) initActor(new Table(), hoverGroup, hoverXPos + 80, hoverYPos - 155, 600, 700);
		pane2.align(Align.top);
		pane2.add(skillDisplay).width(600).row();
		
		bonusDisplay = initLabel("", skin, true, Align.top, Color.FOREST);
		pane2.add(bonusDisplay).width(600).row();
		
		penaltyDisplay = initLabel("", skin, true, Align.top, Color.RED);
		pane2.add(penaltyDisplay).width(600);
		
		uiVisible = initImage(assetManager.get(AssetEnum.SEARCHING.getTexture()), 1850, 5);
		uiGroup.removeActor(uiVisible);
		uiStage.addActor(uiVisible);
		uiVisible.setScale(.5f);
		uiVisible.addListener(new ClickListener() { public void clicked(InputEvent event, float x, float y) {
        	toggleUI();
        }});
		
		enemyToggle = initImage(assetManager.get(AssetEnum.XRAY.getTexture()), 1850, 55);
		uiGroup.removeActor(enemyToggle);
		uiStage.addActor(enemyToggle);
		enemyToggle.setScale(.5f);
		enemyToggle.addListener(new ClickListener() { public void clicked(InputEvent event, float x, float y) {
        	toggleXray();
        }});
		
		TextButton surrenderButton = new TextButton("Surrender", skin);
		surrenderButton.setPosition(-25, 0);
		surrenderButton.addListener(new ClickListener() { public void clicked(InputEvent event, float x, float y) {
        	character.modHealth(-100);
        }});
		uiGroup.addActor(surrenderButton);
		
		hideHoverGroup();
		checkEndBattle();
		
		setEnemyTechnique();
		
		saveService.saveDataValue(SaveEnum.PLAYER, character);
		saveService.saveDataValue(SaveEnum.ENEMY, enemy);
		Array<Array<MutationResult>> consoleComponents = new Array<Array<MutationResult>>();
		consoleComponents.add(battle.consoleText);
		consoleComponents.add(battle.dialogText);
		saveService.saveDataValue(SaveEnum.CONSOLE, consoleComponents);
	}

	@Override
	public void buildStage() {}	
	
	private void toggleUI() { 
		uiGroup.addAction(visible(!uiGroup.isVisible()));
		uiVisible.addAction(alpha(uiGroup.isVisible() ? .5f : 1f));
		enemyToggle.addAction(!enemyToggle.isVisible() ? Actions.hide() : alpha(uiGroup.isVisible() ? .5f : 1f));
	}
	
	private void toggleXray() { enemy.toggle(); }
	
	public void battleLoop() {
		enemyToggle.addAction(enemy.canToggle() ? Actions.show() : Actions.hide()); 
		if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) { gameExit = true;	}
		else if (!battleOutcomeDecided) { 
			if(Gdx.input.isKeyJustPressed(Keys.UP)) { changeSelection(selection - 1 < 0 ? optionButtons.size - 1 : selection - 1); }
	        else if(Gdx.input.isKeyJustPressed(Keys.DOWN)) { changeSelection((selection + 1) % optionButtons.size); }
	        else if(Gdx.input.isKeyJustPressed(Keys.ENTER)) { clickButton(optionButtons.get(selection)); }			
			if (Gdx.input.isKeyJustPressed(Keys.TAB)) { toggleUI(); }
			if (Gdx.input.isKeyJustPressed(Keys.SHIFT_LEFT)) { toggleXray(); }
			
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
				Array<MutationResult> results = resolveTechniques(character, selectedTechnique, enemy, enemySelectedTechnique);
				selectedTechnique = null;
				displayTechniqueOptions();
				saveService.saveDataValue(SaveEnum.PLAYER, character, false);
				saveService.saveDataValue(SaveEnum.ENEMY, enemy, false);
				Array<Array<MutationResult>> consoleComponents = new Array<Array<MutationResult>>();
				consoleComponents.add(consoleContents);
				consoleComponents.add(dialogContents);
				saveService.saveDataValue(SaveEnum.CONSOLE, consoleComponents, false);
				saveService.saveDataValue(SaveEnum.BATTLE_RESULT, results);
			}

			checkEndBattle();
		}
		else if(Gdx.input.isKeyJustPressed(Keys.ENTER) || Gdx.input.isKeyJustPressed(Keys.SPACE)) { battleOver = true; }
	}
	
	private void changeButtonColor(TextButton button, Color color) {
		TextButtonStyle style = new TextButtonStyle(button.getStyle());
		style.fontColor = color;
		button.setStyle(style);
	}
	
	private String getAttackInfo(Array<Attack> attacks) { return attacks.size == 0 ? "" : attacks.get(0).getDescription(); }
	
	private void displayTechniqueOptions() {
		techniquePane.clearActions();
		techniqueTable.clear();
		Array<Technique> options = character.getPossibleTechniques(enemy);
		optionButtons = new Array<TextButton>();
		
		for (int ii = 0; ii < options.size; ii++) {
			Technique option = options.get(ii);
			Array<Attack> optionAttacks = option.resolve(enemy.getEmptyTechnique(character));
			SkillButton button = new SkillButton(option.getTechniqueName() + (ii >= POSSIBLE_KEYS_CHAR.length ? "" : " ("+POSSIBLE_KEYS_CHAR[ii]+")"), skin, assetManager.get(option.getStance().getTexture()));
			techniqueTable.add(button).size(440, 76).row();
			optionButtons.add(button);
			if (character.getStance().isIncapacitatingOrErotic()) { changeButtonColor(button, Color.FOREST); }
			else if(character.outOfStaminaOrStability(option)) {
				changeButtonColor(button, Color.RED);
			}
			else if (character.lowStaminaOrStability(option)) { changeButtonColor(button, Color.ORANGE); }
			button.addListener(getListener(option, (character.outOfStamina(option) && !character.getStance().isIncapacitatingOrErotic() ? "THIS WILL CAUSE YOU TO COLLAPSE!\n" : character.outOfStability(option) && !character.getStance().isIncapacitatingOrErotic() ? "THIS WILL CAUSE YOU TO LOSE YOUR FOOTING!\n" : "") + getAttackInfo(optionAttacks) + option.getTechniqueDescription(), option.getBonusDescription(), ii));
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
	private Array<MutationResult> resolveTechniques(AbstractCharacter firstCharacter, Technique firstTechnique, AbstractCharacter secondCharacter, Technique secondTechnique) {
		consoleContents = new Array<MutationResult>();
		dialogContents = new Array<MutationResult>();
		
		// cache player character's stance from the previous turn; playerCharacter will cache stance at the start of this turn
		Stance oldStance = firstCharacter.getStance();
		Stance oldEnemyStance = secondCharacter.getStance();
		
		// no mutations occur here - characters return their stance modification text
		consoleContents.add(firstCharacter.getStanceTransform(firstTechnique));
		consoleContents.add(secondCharacter.getStanceTransform(secondTechnique));
		
		// first mutation - characters lose stamina, stability, mana, cache their current stance, change their current stance, 
		firstCharacter.extractCosts(firstTechnique);
		secondCharacter.extractCosts(secondTechnique);
	
		// receive the final state information from each character to apply to their attacks
		Array<Attack> attacksForFirstCharacter = doAttacks(secondCharacter, secondTechnique.resolve(firstTechnique));
		Array<Attack> attacksForSecondCharacter = doAttacks(firstCharacter, firstTechnique.resolve(secondTechnique));
		Array<MutationResult> playerResults = new Array<MutationResult>();
		
		// the following stuff needs to happen on an asynchronous basis	- the attacks must be received immediately, but all screen printing and sound playing should not be immediate	
		
		// final mutations - attacks are applied to each character, their cached state within the techniques makes the ordering here irrelevant
		for (Attack attackForFirstCharacter : attacksForFirstCharacter) {
			AttackResult results = firstCharacter.receiveAttack(attackForFirstCharacter);
			consoleContents.addAll(results.getToAttackerMessages());
			consoleContents.addAll(results.getToDefenderMessages());
			dialogContents.addAll(convertToResults(results.getDialog()));
			playerResults.addAll(results.getDefenderResults());
		}
		
		for (Attack attackForSecondCharacter : attacksForSecondCharacter) {
			AttackResult results = secondCharacter.receiveAttack(attackForSecondCharacter);
			consoleContents.addAll(results.getToAttackerMessages());
			consoleContents.addAll(results.getToDefenderMessages());
			dialogContents.addAll(convertToResults(results.getDialog()));
			playerResults.addAll(results.getAttackerResults());
		}
		
		for (Attack attackForFirstCharacter : attacksForFirstCharacter) {
			processAttack(attackForFirstCharacter, enemy, 0, 5/60f);
			if (attackForFirstCharacter.isAttack()) { 
				slash.setState(0);
				if (attackForFirstCharacter.isSuccessful()) { enemy.attackAnimation(); }
			}
			if (attackForFirstCharacter.getDamage() > 0) {
				characterPortrait.setDrawable(getDrawable(AssetEnum.PORTRAIT_HIT.getTexture()));
				characterPortrait.addAction(Actions.sequence(Actions.moveBy(-10, -10), Actions.delay(.1f), Actions.moveBy(0, 20), Actions.delay(.1f), Actions.moveBy(20, -20), Actions.delay(.1f), Actions.moveBy(0, 20), Actions.delay(.1f), Actions.moveTo(-5 * 1.5f, 615 * 1.5f),  
					new Action() { @Override public boolean act(float delta) { characterPortrait.setDrawable(getDrawable(character.popPortraitPath()));	return true; }}
				));
			}
			else { characterPortrait.setDrawable(getDrawable(character.popPortraitPath()));	}
		}
		
		for (Attack attackForSecondCharacter : attacksForSecondCharacter) {
			processAttack(attackForSecondCharacter, character, 5/60f, 0);
			if (attackForSecondCharacter.isAttack() && attackForSecondCharacter.isSuccessful()) { enemy.hitAnimation(); }
		}
		
		if (dialogContents.size == 0 || dialogContents.get(0).getText().equals("")) { dialogGroup.addAction(Actions.hide()); }
		else { dialogGroup.addAction(Actions.show()); }

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
		if (firstCharacter.getStance().isErotic() != secondCharacter.getStance().isErotic() && firstCharacter.getStance() != Stance.DOGGY_KYLIRA && secondCharacter.getStance() != Stance.DOGGY_KYLIRA && firstCharacter.getStance() != Stance.DOGGY_TRUDY && secondCharacter.getStance() != Stance.DOGGY_TRUDY) {
			System.out.println("Player is in " + firstCharacter.getStance() + " stance and enemy is in " + secondCharacter.getStance() + " stance.");
			if (firstCharacter.getStance().isErotic()) { firstCharacter.setStance(Stance.BALANCED);	}
			else { secondCharacter.setStance(Stance.BALANCED); }
		}
		
		setEnemyTechnique();	
		
		// these methods use short circuit evaluation, because the hasSeenXTutorial methods also set that respective flag to prevent repeats - should probably make this less fragile eventually
		if (firstCharacter.getStance() != oldStance && !character.hasSeenStanceTutorial()) {
			popDialog("When you use a skill you will end up in that skills resulting stance, visible as an icon next to the skill's name.  When in a new stance, you will have access to new skills.  You can use a skill that will return you to the original stance, or even another, previously unavailable stance.");
		}
		else if ((firstCharacter.getHealthDegradation() > 0 || firstCharacter.getStaminaDegradation() > 0) && !character.hasSeenDegradationTutorial()) {
			popDialog("When your health or stamina is reduced, you will accrue penalties to your statistics.  To see these penalties, highlight your character portrait.  The icons next to your health and stamina bars will also indicate that this has occurred.  Recover health or stamina to remove these penalties.");
		}
		else if (firstCharacter.getGrappleStatus() != GrappleStatus.NULL && !character.hasSeenGrappleTutorial()) {
			popDialog("When you enter a grapple with an opponent, your available skills are limited by stamina and your current grapple status, indicated above.  Struggling or grappling with the enemy can improve your grapple status - but they will use skills that will have the opposite effect!");
		}
		else if (firstCharacter.getStance().isIncapacitating() && !character.hasSeenKnockdownTutorial()) {
			popDialog("When you run out of stamina or stability, you will fall to the ground!  While attempting to get to your feet, you can transition through multiple stances, including Hands-and-Knees and Kneeling stance, and your available skills are limited by your current stamina and stability.");
		}
		
		if (character.getHealthPercent() < .11f && character.hasKyliraHeal()) {
			// have Kylira heal
			playerResults.addAll(character.modHealth(character.getKyliraLevel() * 10));
			consoleContents.add(new MutationResult("Kylira heals you for " + character.getKyliraLevel() * 10 + "!  \"Good luck!\""));
			Group popupGroup = new Group();
			this.addActor(popupGroup);
			
			Texture elfTexture = assetManager.get(AssetEnum.ELF.getTexture());
			initActor(new Image(elfTexture), popupGroup, 350, 0, elfTexture.getWidth(), elfTexture.getHeight());		
			popupGroup.addAction(sequence(alpha(0), fadeIn(1), fadeOut(1)));
			soundMap.get(AssetEnum.FIREBALL_SOUND).play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume"));
		}
		else if (character.getHealthPercent() < .5f && character.hasTrudyBuff()) {
			character.getStatuses().put(StatusType.STRENGTH_BUFF.toString(), character.getTrudyLevel() * 2);
			consoleContents.add(new MutationResult("Trudy casts Titan Strength on you! \"Do better!\""));
			Group popupGroup = new Group();
			this.addActor(popupGroup);
			
			AnimatedActor trudy = AnimationEnum.TRUDY.getAnimation(assetManager);
			trudy.setSkeletonPosition(500, 500);
			initActor(trudy, popupGroup, 0, 0, trudy.getWidth(), trudy.getHeight());		
			popupGroup.addAction(sequence(alpha(0), delay(2), Actions.hide()));
			soundMap.get(AssetEnum.FIREBALL_SOUND).play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume"));
		}		
		
		setTables();		
		return playerResults;
	}
	
	private void setTables() {
		consoleTable.clear();
		dialogTable.clear();
		for (MutationResult mr : consoleContents) {
			if (mr.getText().trim().equals("")) continue;
			Label label = initLabel(mr.getText(), skin, true, Align.top, Color.BLACK);
			label.setWrap(true);
			consoleTable.add(label).width(600);
			consoleTable.add(new Image(assetManager.get(mr.getTexture()))).size(25, 25).row();
		}
		
		for (MutationResult mr : dialogContents) {
			Label label = initLabel(mr.getText(), skin, true, Align.top, Color.PURPLE);
			label.setWrap(true);
			dialogTable.add(label).width(400).row();
		}
	}
	
	private Array<MutationResult> convertToResults(Array<String> source) {
		Array<MutationResult> results = new Array<MutationResult>();
		for (String s : source) results.add(new MutationResult(s));
		return results;
	}
	
	private void processAttack(Attack attack, AbstractCharacter otherCharacter, float delay, float responseDelay) {
		if (attack.isSuccessful() && attack.isClimax()) { this.addAction(sequence(delay(delay), new SoundAction(soundMap.get(AssetEnum.CUM), .5f))); }
		if (otherCharacter.getStance() == Stance.CASTING && attack.isSuccessful()) { this.addAction(sequence(delay(delay), new SoundAction(soundMap.get(AssetEnum.INCANTATION), .5f))); }
		if (attack.isSpell()) { this.addAction(sequence(delay(delay), new SoundAction(soundMap.get(AssetEnum.FIREBALL_SOUND), .5f))); }
		if (attack.isAttack()) {
			if (!attack.isSuccessful()) { this.addAction(sequence(delay(delay * 3), new SoundAction(soundMap.get(AssetEnum.ATTACK_SOUND), .5f))); }
			else {
				if (!attack.isSpell()) {
					if (attack.getStatus() == Status.BLOCKED) { this.addAction(sequence(delay(delay + 5/60f), new SoundAction(soundMap.get(AssetEnum.BLOCK_SOUND), 1.5f))); }
					else if (attack.getStatus() == Status.PARRIED) { this.addAction(sequence(delay(delay + 5/60f), new SoundAction(soundMap.get(AssetEnum.PARRY_SOUND), .5f))); }
					else if ((attack.isMelee() && otherCharacter.getWeapon() != null && otherCharacter.getWeapon().causesBleed()) || (!attack.isMelee() &&  otherCharacter.getRangedWeapon() != null && otherCharacter.getRangedWeapon().causesBleed())) { this.addAction(sequence(delay(delay + 5/60f), new SoundAction(soundMap.get(AssetEnum.SWORD_SLASH_SOUND), 1.5f))); }
					else { this.addAction(sequence(delay(delay + 5/60f), new SoundAction(soundMap.get(AssetEnum.HIT_SOUND), .3f))); }
				}
			}
		}	
	}

	private void setEnemyTechnique() {
		int battletick = character.getHeartbeat() % 3;
		enemySelectedTechnique = enemy.getTechnique(character);
		uiGroup.removeActor(enemySkill);
		enemySkill = new SkillText(character.getBattlePerception() + battletick < 8 ? "" : enemySelectedTechnique.getTechniqueName(), skin, assetManager.get(enemySelectedTechnique.getStance().getTexture()));		
		uiGroup.addActor(enemySkill);
		enemySkill.setPosition(1300, 750);
		if (character.getBattlePerception() + battletick < 5) {
			enemySkill.addAction(Actions.hide());
		}
	}
	
	private TextureRegionDrawable getDrawable(AssetDescriptor<Texture> AssetInfo) { return getDrawable(assetManager.get(AssetInfo)); }
	public static TextureRegionDrawable getDrawable(Texture texture) { return new TextureRegionDrawable(new TextureRegion(texture)); }
	
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
			Group popupGroup = new Group();
			this.addActor(popupGroup);
			popupGroup.addAction(fadeIn(.1f));
			
			Texture boxTexture = assetManager.get(AssetEnum.BATTLE_HOVER.getTexture());
			initActor(new Image(boxTexture), popupGroup, 325, 75, boxTexture.getWidth() + 100, boxTexture.getHeight() + 100);
			Table statusResults = (Table) initActor(new Table(), popupGroup, 412, 525);
			statusResults.align(Align.topLeft);
			statusResults.add(initLabel(enemy.getOutcomeText(character), skin, true, Align.top, battleOutcome == Outcome.VICTORY || battleOutcome == Outcome.SATISFIED_ANAL || battleOutcome == Outcome.SATISFIED_ORAL ? Color.FOREST : Color.FIREBRICK)).width(580).align(Align.top).row();
			statusResults.row();			
			statusResults.add(initLabel("Results: ", skin, true, Align.left, Color.BLACK)).fillY().align(Align.left).row();
			for (MutationResult result : MutationResult.collapse(battleResults)) {
				statusResults.add(new MutationActor(result, assetManager.get(result.getTexture()), skin, true)).fillY().padLeft(50).align(Align.left).row();
			}	
			
			uiGroup.removeActor(techniquePane);
			uiGroup.removeActor(hoverGroup);
			
			this.addListener(
				new ClickListener() {
			        @Override
			        public void clicked(InputEvent event, float x, float y) {
			        	battleOver = true;
			        	saveService.saveDataValue(SaveEnum.CONSOLE, new Array<Array<MutationResult>>());
			        }
				}
			);
		}
	}
	
	private void popDialog(String dialog) {
		Group popupGroup = new Group();
		this.addActor(popupGroup);
		
		Texture boxTexture = assetManager.get(AssetEnum.BATTLE_HOVER.getTexture());
		initActor(new Image(boxTexture), popupGroup, 625, 375, boxTexture.getWidth() + 100, boxTexture.getHeight() + 100);		
		popupGroup.addAction(fadeIn(.1f));
		
		Table statusResults = (Table) initActor(new Table(), popupGroup, 712, 825);
		statusResults.align(Align.topLeft);
		statusResults.add(initLabel(dialog, skin, true, Align.top, Color.BLACK)).width(580).align(Align.top).row();		
		
		popupGroup.addAction(sequence(delay(25), Actions.hide()));
		popupGroup.addListener(new ClickListener() {
			@Override
	        public void clicked(InputEvent event, float x, float y) {
	        	popupGroup.addAction(Actions.hide());
	        }
		});
	}
	private ClickListener getListener(final PlayerCharacter character) {
		return new ClickListener() {
	        @Override
	        public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
				skillDisplay.setText(character.getStatTextDisplay().trim());
				bonusDisplay.setText(character.getStatBonusDisplay().trim());
				penaltyDisplay.setText(character.getStatPenaltyDisplay().trim());
				showHoverGroup();
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
				skillDisplay.setText(description.trim());
				bonusDisplay.setText(bonusDescription.trim());
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
	
	private void hideHoverGroup() { hoverGroup.addAction(fadeOut(2f)); }
	private void showHoverGroup() {
		hoverGroup.clearActions();
		hoverGroup.addAction(visible(true));
		hoverGroup.addAction(alpha(1));
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
	
	private Actor initActor(Actor actor, Group group, float x, float y) { return initActor(actor, group, null, x, y, actor.getWidth(), actor.getHeight()); }	
	private Actor initActor(Actor actor, Group group, float x, float y, float width, float height) { return initActor(actor, group, null, x, y, width, height); }
	private Actor initActor(Actor actor, Group group, Color color, float x, float y, float width, float height) {
		if (group != null) group.addActor(actor);
		else this.addActor(actor);
		actor.setBounds(x, y, width, height);
		if (color != null) actor.setColor(color);
		return actor;
	}
	
	private Image initImage(Texture texture, float x, float y) { return initImage(texture, x, y, texture.getWidth(), texture.getHeight()); }
	private Image initImage(Texture texture, float x, float y, float height) { return initImage(texture, x, y,  (texture.getWidth() / (texture.getHeight() / (1.0f * height))), height); }	
	private Image initImage(Texture texture, float x, float y, float width, float height) {
		Image newImage = new Image(texture);
		newImage.setBounds(x, y, width, height);
		uiGroup.addActor(newImage);
		return newImage;
	}
	
	private Label initLabel(String value, Skin skin, boolean wrap, int alignment, Color color) {
		Label label = new Label(value, skin);
		label.setWrap(wrap);
		label.setAlignment(alignment);
		label.setColor(color);
		return label;
	}
	
	public boolean isBattleOver() { return battleOver; }
	public int getOutcomeScene() { return outcomes.containsKey(outcome.toString()) ? outcomes.get(outcome.toString()) : outcomes.get(Outcome.DEFEAT.toString()); }
	
	private class StanceActor extends Actor {
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
		// this needs to not override draw, instead having this extend Group and adding stance based images
		@Override
	    public void draw(Batch batch, float parentAlpha) {
			super.draw(batch, parentAlpha);
			Stance stance = character.getStance();
			String stanceName = stance.getLabel();
			batch.setColor(getColor());
			batch.draw(assetManager.get(stance.getTexture()), getX(), getY(), getWidth(), getHeight());
			if (hover) {
				batch.draw(hoverBox, getX() + 50 - (stanceName.length() * 9), getY() - 75, 25 + stanceName.length() * 18, 50);
				font.setColor(Color.BLACK);
				font.draw(batch, stanceName, getX() + getWidth()/6, getY() - 40, 100, Align.center, false);
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
			batch.draw(stanceIcon, getX() + 372, getY() + 3, 63, 76);
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
	
	// this should display a little widget with the possible outcomes, which when an outcome is completed, the others should gray out and it should become more prominent, also when you hover it can show what the actual outcome is
	private class OutcomeWidget extends Group { 
		private final ObjectMap<String, Table> outcomeMap;
		public OutcomeWidget(ObjectMap<String, Integer> outcomes) {
			outcomeMap = new ObjectMap<String, Table>();
			Table table = new Table();
			this.addActor(table);
			table.align(Align.topLeft);
			for (String outcome : outcomes.keys()) {
				Table outcomeImage = Outcome.valueOf(outcome).getOutcomeImage(assetManager);
				outcomeImage.addListener(new ClickListener() {
			        @Override
			        public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
						skillDisplay.setText(Outcome.valueOf(outcome).getDescription());
						bonusDisplay.setText("");
						penaltyDisplay.setText("");
						showHoverGroup();
					}
					@Override
			        public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
						hideHoverGroup();
					}
			    });
				outcomeMap.put(outcome, outcomeImage);
				table.add(outcomeImage).align(Align.left).row();				
			}
		}		
		
		@Override 
		public void draw(Batch batch, float parentAlpha) {
			if(outcome != null) {
				for (Table table : outcomeMap.values()) {
					for (Cell<?> cell : table.getCells()) { cell.getActor().setColor(Color.GRAY); }
				}
				for (Cell<?> cell : outcomeMap.get(outcome.toString()).getCells()) { cell.getActor().setColor(Color.WHITE); }
			}
			super.draw(batch, parentAlpha);			
		}
	}
	
	//many of these classes have shared functionality and should be refactored		
	private class StatusDisplay extends Label {
		private final AbstractCharacter character;
		public StatusDisplay(AbstractCharacter character, Skin skin) {
			super(character.getStatusBlurb(), skin);			
			this.character = character;
			this.setColor(Color.RED);
		}
		@Override
		public void act(float delta) {
			setText(character.getStatusBlurb());
			super.act(delta);
		}
	}
		
	private class BleedDisplay extends DisplayWidget {
		private final AbstractCharacter character;
		private final Label bleedValue;
		private final Image display;
		private final Label diffValueDisplay;
		private int value;
		public BleedDisplay (AbstractCharacter character, Texture bloodTexture) {
			this.character = character;
			display = new Image(assetManager.get(AssetEnum.BLEED.getTexture()));	
		    display.setSize((bloodTexture.getWidth() / (bloodTexture.getHeight() / (1.0f * 75))), 75);
		    this.addActor(display);
			bleedValue = new Label("" + character.getBleed(), skin);
			bleedValue.setPosition(getX() + 19, getY() + 7);
			bleedValue.setColor(Color.RED);
			bleedValue.setAlignment(Align.center);
			bleedValue.setWidth(10);
			if (character.getBleed() <= 0) addAction(Actions.hide());
			this.addActor(bleedValue);
			diffValueDisplay = new Label("", skin);
			diffValueDisplay.setPosition(getX() + 50, getY() + 25);
			this.addActor(diffValueDisplay);
			value = character.getBleed();
		}
		@Override
		public void act(float delta) {
			bleedValue.setText("" + character.getBleed());
			if (this.isVisible() && character.getBleed() <= 0) {
				display.addAction(Actions.hide());
				bleedValue.addAction(Actions.hide());
			}
			else if (!this.isVisible() && character.getBleed() > 0) {
				display.addAction(Actions.show());
				bleedValue.addAction(Actions.show());
			}
			if (value != character.getBleed()) {
				setDiffLabel(diffValueDisplay, character.getBleed() - value, true);
				value = character.getBleed();
			}
			
			super.act(delta);
		}
	}
		
	private class WeaponDisplay extends DisplayWidget {
		private final Label display;
		private final AbstractCharacter character;
		private WeaponDisplay(AbstractCharacter character) {
			this.character = character;
			this.display = new Label("Weapon: " + (character.getWeapon() != null ? character.getWeapon().getName() : "Unarmed"), skin);
			display.setColor(Color.GOLDENROD);
			this.addActor(display);
		}
		@Override
		public void act(float delta) {
			display.setText("Weapon: " + (character.getWeapon() != null ? character.getWeapon().getName() : "Unarmed"));
			super.act(delta);
		}
	}
	
	private class ArmorDisplay extends DisplayWidget {
		private final Armor armor;
		private final Image display;
		private final Label displayValue;
		private final Label diffValueDisplay;
		private final Texture armorTexture;
		private final Texture armorBrokenTexture;
		private int value;
		public ArmorDisplay (AbstractCharacter character, Armor armor, Texture armorTexture, Texture armorBrokenTexture) {
			this.armor = armor;
			this.armorBrokenTexture = armorBrokenTexture;
			this.armorTexture = armorTexture;
			if (armor == null) {
				addAction(Actions.hide());
				display = new Image();
				displayValue = new Label("", skin);
			}
			else {
				display = new Image(armor.getDestructionLevel() > 0 ? armorBrokenTexture : armorTexture);
				display.setSize((armorBrokenTexture.getWidth() / (armorBrokenTexture.getHeight() / (1.0f * 50))), 50);
				value = armor.getDurability();
				displayValue = new Label("" + (armor.isShield() ? "" : armor.getShockAbsorption()), skin);				
				displayValue.setPosition(getX() + 12, getY() + 10);
				displayValue.setColor(Color.BROWN);
				this.addListener(new ClickListener() {
			        @Override
			        public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
						skillDisplay.setText((armor == null ? "" : armor.getName() + "\nCurrent damage absorption provided: " + armor.getShockAbsorption() + "\nCurrent durability: " + armor.getDurability() + "\n\n" + armor.getDescription()).trim());
						bonusDisplay.setText("");
						penaltyDisplay.setText("");
						showHoverGroup();
					}
					@Override
			        public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
						hideHoverGroup();
					}
			    });
			}
			this.addActor(display);
			this.addActor(displayValue);
			diffValueDisplay = new Label("", skin);
			diffValueDisplay.setPosition(getX() + 50, getY() + 25);
			this.addActor(diffValueDisplay);
		}
		@Override
		public void act(float delta) {
			if (armor != null) {
				display.setDrawable(new TextureRegionDrawable(new TextureRegion(armor.getDestructionLevel() > 0 ? armorBrokenTexture : armorTexture)));
				displayValue.setText("" + (armor.isShield() ? "" : armor.getShockAbsorption()));
				if (armor.getShockAbsorption() == 0) {
					display.addAction(Actions.hide());
					displayValue.addAction(Actions.hide());
				}
				if (value != armor.getDurability()) {
					setDiffLabel(diffValueDisplay, armor.getDurability() - value);
					value = armor.getDurability();
				}
			}
			super.act(delta);
		}
	}

	private class GrappleDisplay extends Group {
		private final AbstractCharacter character;
		private final OrderedMap<GrappleStatus, Image> inactiveStatuses;
		private final OrderedMap<GrappleStatus, Image> activeStatuses;
		private final Image background;
		private final static float scaleFactor = .315f;
		private GrappleDisplay(AbstractCharacter character, AssetManager assetManager) {
			this.character = character;
			this.inactiveStatuses = new OrderedMap<GrappleStatus, Image>();
			this.activeStatuses = new OrderedMap<GrappleStatus, Image>();
			background = new Image(assetManager.get(AssetEnum.GRAPPLE_BACKGROUND.getTexture()));
			background.setScale(scaleFactor);
			background.setPosition(getX() - 50, getY() - 29);
			this.addActor(background);
			int offset = 0;
			for (GrappleStatus grappleStatus : GrappleStatus.reverseValues()) {
				if (grappleStatus == GrappleStatus.NULL) continue;
				initImage(grappleStatus, grappleStatus.getInactiveTexture(assetManager), getX() + offset, getY(), scaleFactor, Color.GRAY, false);
				initImage(grappleStatus, grappleStatus.getActiveTexture(assetManager), getX() + offset, getY(), scaleFactor, Color.WHITE, true);
				offset += 74.75f;
			}
		}
		
		private void initImage(GrappleStatus grappleStatus, Texture texture, float x, float y, float scaleFactor, Color color, boolean active) {
			Image newImage = new Image(texture);
			newImage.setPosition(x, y);
			newImage.setScale(scaleFactor);
			newImage.setColor(color);
			newImage.addAction(Actions.hide());
			this.addActor(newImage);
			if (active) activeStatuses.put(grappleStatus, newImage);
			else inactiveStatuses.put(grappleStatus, newImage);
		}
	
		@Override 
		public void act(float delta) {
			// set which grapple is visible first
			if (this.isVisible()) { if(character.getGrappleStatus() == GrappleStatus.NULL) this.addAction(Actions.hide()); }
			else if (character.getGrappleStatus() != GrappleStatus.NULL) this.addAction(Actions.show());
			for (GrappleStatus status : GrappleStatus.reverseValues()) {
				if (status == GrappleStatus.NULL) continue;
				if (status == character.getGrappleStatus()) {
					inactiveStatuses.get(status).addAction(Actions.hide());
					activeStatuses.get(status).addAction(Actions.show());
				}
				else {
					activeStatuses.get(status).addAction(Actions.hide());
					inactiveStatuses.get(status).addAction(Actions.show());
				}
			}
			super.act(delta);
		}		
	}
	
	public enum Outcome {
		VICTORY("Win - the enemy is no longer able or willing to fight, usually because their HP has been reduced to 0.", AssetEnum.W), 
		DEFEAT("Lose - you are unable to continue the fight, as your HP has been reduced to 0.", AssetEnum.L), 
		KNOT_ANAL("Knot (Anal) - you and the enemy are tied together somehow, rendering you unable to fight.", AssetEnum.K, AssetEnum.A), 
		KNOT_ORAL("Knot (Oral) - you and the enemy are tied together somehow, rendering you unable to fight.", AssetEnum.K, AssetEnum.A), 
		SATISFIED_ANAL("Satisfied - the enemy is satisfied, and will no longer fight.", AssetEnum.S, AssetEnum.A), 
		SATISFIED_ORAL("Satisfied (Oral) - the enemy is satisfied (orally) and will no longer fight.", AssetEnum.S, AssetEnum.O), 
		SUBMISSION("Dominated - the enemy is no longer willing to fight because they've beccome submissive.", AssetEnum.D, AssetEnum.A),
		DEATH("Death - you have been killed, and can no longer fight because you are dead.", AssetEnum.D);

		private final String description;
		private final Array<AssetEnum> glyphs;
		private Outcome(String description, AssetEnum ... glyphs) {
			this.description = description;
			this.glyphs = new Array<AssetEnum>(glyphs);
		}
		
		public String getDescription() { return description; }
		
		public Table getOutcomeImage(AssetManager assetManager) {
			Table table = new Table();
			boolean first = true;
			for (AssetEnum glyph : glyphs) {
				int adjust = -15;
				if (first) {
					first = false;
					adjust = 0;
				}
				Texture texture = assetManager.get(glyph.getTexture());
				table.add(new Image(texture)).size(texture.getWidth(), texture.getHeight()).padLeft(adjust);
			}		
			return table;
		}
	}
	
	@Override
	protected void buildMenu() {
		super.buildMenu();
		Actor menuGroup = getActors().get(getActors().size - 1);
		menuGroup.remove();
		uiStage.addActor(menuGroup);
	}
	
	private int cameraOffset = 0;
	private boolean zoomIn = true;
	private boolean cameraShake = false;
	
	@Override
	public void render(float delta) {
		super.render(delta);
		uiStage.act();
		uiStage.draw();
		
		if (cameraShake) {
			if (cameraOffset % 2 == 0) {
				camera.translate(0, 0, zoomIn ? -1 : 1);
				if (cameraOffset >= 100) {
					cameraOffset = 0;
					zoomIn = !zoomIn;
				}
			}
			cameraOffset++;
		}
		
		
		battleLoop();
		if (gameExit) {
			showScreen(ScreenEnum.MAIN_MENU);
		}
		// this terminates the battle
		else if (isBattleOver()) {	
			saveService.saveDataValue(SaveEnum.CONTEXT, SaveManager.GameContext.ENCOUNTER);
			saveService.saveDataValue(SaveEnum.SCENE_CODE, getOutcomeScene());
			saveService.saveDataValue(SaveEnum.ENEMY, null); // this may need to be removed if the enemy needs to persist until the end of the encounter; endScenes would have to perform this save or the encounter screen itself
			showScreen(ScreenEnum.ENCOUNTER);
		}
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
    }
    
    @Override
	protected void switchFade(ScreenEnum screenRequest, AbstractScreen currentScreen, AssetEnum oldMusicPath, Music oldMusic) { 
    	super.switchFade(screenRequest, currentScreen, oldMusicPath, oldMusic);
    	uiStage.addAction(fadeOut(.2f));
    }
	
	@Override
	public void dispose() {
		for(AssetDescriptor<?> path: requirementsToDispose) {
			if (path.fileName.equals(AssetEnum.BUTTON_SOUND.getSound().fileName) || path.type == Music.class) continue;
			assetManager.unload(path.fileName);
		}
		requirementsToDispose = new Array<AssetDescriptor<?>>();
		uiStage.dispose();
		super.dispose();
	}

	// this should simply return the battlecode's requirements, rather than use a switch
	public static Array<AssetDescriptor<?>> getRequirements(BattleAttributes battleAttributes) {
		Array<AssetDescriptor<?>> requirements = new Array<AssetDescriptor<?>>(BattleScreen.resourceRequirements);
		requirements.addAll(battleAttributes.getRequirements());
		requirementsToDispose = requirements;
		return requirements;
	}
	
}

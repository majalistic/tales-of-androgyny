package com.majalis.battle;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetDescriptor;
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
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.OrderedMap;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.majalis.asset.AnimatedImage;
import com.majalis.asset.AssetEnum;
import com.majalis.character.AbstractCharacter;
import com.majalis.character.AbstractCharacter.AttackResult;
import com.majalis.character.AbstractCharacter.Stability;
import com.majalis.character.Stance;
import com.majalis.character.Attack.Status;
import com.majalis.character.BalanceBar;
import com.majalis.character.Attack;
import com.majalis.character.EnemyCharacter;
import com.majalis.character.GrappleStatus;
import com.majalis.character.HealthBar;
import com.majalis.character.ManaBar;
import com.majalis.character.PlayerCharacter;
import com.majalis.character.StaminaBar;
import com.majalis.character.Technique;
import com.majalis.encounter.Background;
import com.majalis.save.MutationResult;
import com.majalis.save.SaveEnum;
import com.majalis.save.SaveService;
import com.majalis.scenes.MutationActor;
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
	private final Array<MutationResult> battleResults;
	
	private final AnimatedImage slash;

	private final ObjectMap<AssetEnum, Sound> soundMap;
	
	// basically all of this should go away
	
	private final Image hoverImage;
	private final Image characterPortrait;
	private final Group hoverGroup;
	private final Group dialogGroup;
	private final Label console;
	private final Label dialog;
	private final Label skillDisplay;
	private final Label bonusDisplay;
	private final Label penaltyDisplay;
	private final Image characterArousal;
	private final Image enemyArousal;
	private final Image characterBelly;
	private final Image masculinityIcon;
	private final Image bloodImage;
	private final Image enemyBloodImage;
	
	private final Label enemyWeaponLabel;
	private final Label armorLabel;
	private final Label enemyArmorLabel;
	private final Label legwearLabel;
	private final Label enemyLegwearLabel;
	private final Label underwearLabel;
	private final Label enemyUnderwearLabel;
	
	private final Texture nullTexture;
	private final Texture armorTexture;
	private final Texture armorBrokenTexture;
	
	private final Image armorArmor;
	private final Image enemyArmorArmor;
	private final Image underwearArmor;
	private final Image enemyUnderwearArmor;
	private final Image legwearArmor;
	private final Image enemyLegwearArmor;
	private final Image shieldArmor;
	private final Image enemyShieldArmor;
	
	private final Label bloodLabel;
	private final Label enemyBloodLabel;
	private final Label statusLabel;
	private final Label enemyStatusLabel;
	
	private final Label characterHealthDiff;
	private final Label characterStaminaDiff;
	private final Label characterBalanceDiff;
	private final Label characterArmorDiff;
	private final Label characterLegwearDiff;
	private final Label characterUnderwearDiff;
	private final Label characterBleedDiff;
	private final Label enemyHealthDiff;
	private final Label enemyStaminaDiff;
	private final Label enemyBalanceDiff;
	private final Label enemyArmorDiff;
	private final Label enemyLegwearDiff;
	private final Label enemyUnderwearDiff;
	private final Label enemyBleedDiff;
	
	private final AssetEnum musicPath;
	
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
	
	private class GrappleDisplay extends Group {
		private final AbstractCharacter character;
		private final OrderedMap<GrappleStatus, Image> inactiveStatuses;
		private final OrderedMap<GrappleStatus, Image> activeStatuses;
		private final Image background;
		private final static float xOffset = 74.75f;
		private final static float scaleFactor = .315f;
		private final static float backgroundXOffset = -50;
		private final static float backgroundYOffset = -29;
		private GrappleDisplay(AbstractCharacter character, AssetManager assetManager) {
			this.character = character;
			this.inactiveStatuses = new OrderedMap<GrappleStatus, Image>();
			this.activeStatuses = new OrderedMap<GrappleStatus, Image>();
			background = new Image(assetManager.get(AssetEnum.GRAPPLE_BACKGROUND.getTexture()));
			background.setScale(scaleFactor);
			background.setPosition(getX() + backgroundXOffset, getY() + backgroundYOffset);
			this.addActor(background);
			int offset = 0;
			for (GrappleStatus grappleStatus : GrappleStatus.reverseValues()) {
				if (grappleStatus == GrappleStatus.NULL) continue;
				Image inactiveImage = new Image(grappleStatus.getInactiveTexture(assetManager));
				inactiveImage.addAction(hide());
				inactiveImage.setPosition(getX() + offset, getY());
				inactiveImage.setScale(scaleFactor);
				inactiveImage.setColor(Color.GRAY);
				Image activeImage = new Image(grappleStatus.getActiveTexture(assetManager));
				activeImage.addAction(hide());
				activeImage.setPosition(getX() + offset, getY());
				activeImage.setScale(scaleFactor);
				inactiveStatuses.put(grappleStatus, inactiveImage);
				activeStatuses.put(grappleStatus, activeImage);
				this.addActor(inactiveImage);
				this.addActor(activeImage);
				offset += xOffset;
			}
		}
		
		@Override
		public void setPosition(float x, float y) {
			super.setPosition(x, y);
			int offset = 0;
			for (GrappleStatus status : GrappleStatus.reverseValues()) {
				if (status == GrappleStatus.NULL) continue;
				inactiveStatuses.get(status).setPosition(getX() + offset, y);
				activeStatuses.get(status).setPosition(getX() + offset, y);
				offset += xOffset;
			}
			background.setPosition(getX() + backgroundXOffset, getY() + backgroundYOffset);
		}
		
		@Override 
		public void act(float delta) {
			// set which grapple is visible first
			if (character.getGrappleStatus() == GrappleStatus.NULL) this.addAction(hide());
			else this.addAction(show());
			for (GrappleStatus status : GrappleStatus.reverseValues()) {
				if (status == GrappleStatus.NULL) continue;
				if (status == character.getGrappleStatus()) {
					inactiveStatuses.get(status).addAction(hide());
					activeStatuses.get(status).addAction(show());
				}
				else {
					activeStatuses.get(status).addAction(hide());
					inactiveStatuses.get(status).addAction(show());
				}
			}
			super.act(delta);
		}		
	}
	
	public Battle(SaveService saveService, AssetManager assetManager, final PlayerCharacter character, final EnemyCharacter enemy, ObjectMap<String, Integer> outcomes, Background battleBackground, Background battleUI, String consoleText, String dialogText, Array<MutationResult> battleResults, AssetEnum musicPath) {
		this.saveService = saveService;
		this.assetManager = assetManager;
		this.character = character;
		this.enemy = enemy;
		this.outcomes = outcomes;
		this.consoleText = consoleText;
		this.dialogText = dialogText;
		this.battleResults = battleResults;
		this.musicPath = musicPath;
		battleOver = false;
		battleOutcomeDecided = false;
		gameExit = false;	
		
		soundMap = new ObjectMap<AssetEnum, Sound>();
		AssetEnum[] battleSounds = new AssetEnum[]{AssetEnum.CUM, AssetEnum.UNPLUGGED_POP, AssetEnum.MOUTH_POP, AssetEnum.ATTACK_SOUND, AssetEnum.HIT_SOUND, AssetEnum.SWORD_SLASH_SOUND, AssetEnum.FIREBALL_SOUND, AssetEnum.INCANTATION, AssetEnum.THWAPPING, AssetEnum.BUTTON_SOUND, AssetEnum.PARRY_SOUND, AssetEnum.BLOCK_SOUND};
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
		
		this.font = skin.getFont("default-font");
				
		float barX = 195;
		float enemyBarX = 1500;
		float hoverXPos = 330; 
		float hoverYPos = 35; 
		float consoleXPos = 1200;
		float consoleYPos = 5;
		float yAdjust = 25;
		
		characterHealthDiff = initLabel("", skin, Color.WHITE, barX + 350, 1035 + yAdjust);
		characterStaminaDiff = initLabel("", skin, Color.WHITE, barX + 350, 990 + yAdjust);
		characterBalanceDiff = initLabel("", skin, Color.WHITE, barX + 350, 945 + yAdjust);
		characterArmorDiff = initLabel("", skin, Color.WHITE, barX + 274, 823 + yAdjust);
		characterLegwearDiff = initLabel("", skin, Color.WHITE, barX + 274, 770 + yAdjust);
		characterUnderwearDiff = initLabel("", skin, Color.WHITE, barX + 274, 750 + yAdjust);
		characterBleedDiff = initLabel("", skin, Color.WHITE, 520, 800 + yAdjust);
		enemyHealthDiff = initLabel("", skin, Color.WHITE, enemyBarX + 350, 1035 + yAdjust);
		enemyStaminaDiff = initLabel("", skin, Color.WHITE, enemyBarX + 350, 990 + yAdjust);
		enemyBalanceDiff = initLabel("", skin, Color.WHITE, enemyBarX + 350, 945 + yAdjust);
		enemyArmorDiff = initLabel("", skin, Color.WHITE, 1547, 823 + yAdjust);
		enemyLegwearDiff = initLabel("", skin, Color.WHITE, 1547, 770 + yAdjust);
		enemyUnderwearDiff = initLabel("", skin, Color.WHITE, 1547, 750 + yAdjust);
		enemyBleedDiff = initLabel("", skin, Color.WHITE, 1595, 800 + yAdjust);
	
		initActor(new HealthBar(character, assetManager, skin), uiGroup, barX, 1035);
		initActor(new StaminaBar(character, assetManager, skin), uiGroup, barX, 990);
		initActor(new BalanceBar(character, assetManager, skin), uiGroup, barX, 945);
		if (character.hasMagic()) initActor(new ManaBar(character, assetManager, skin), uiGroup, barX, 900);
		
		initActor(new HealthBar(enemy, assetManager, skin), uiGroup, enemyBarX, 1035);
		Actor enemyStamina = initActor(new StaminaBar(enemy, assetManager, skin), uiGroup, enemyBarX, 990);
		if (character.getBattlePerception() < 4) {
			enemyStamina.addAction(Actions.hide());
			enemyStaminaDiff.addAction(Actions.hide());	
		}
		Actor enemyBalance = initActor(new BalanceBar(enemy, assetManager, skin), uiGroup, enemyBarX, 945);
		if (character.getBattlePerception() < 3) {
			enemyBalance.addAction(Actions.hide());
			enemyBalanceDiff.addAction(Actions.hide());	
		}
		
		initActor(new GrappleDisplay(character, assetManager), uiGroup, 765, 1005); 
		
		enemyWeaponLabel = initLabel("Weapon: " + (enemy.getWeapon() != null ? enemy.getWeapon().getName() : "Unarmed"), skin, Color.GOLDENROD, 1578, 900);	
		
		nullTexture = assetManager.get(AssetEnum.NULL.getTexture());
		armorTexture = assetManager.get(AssetEnum.ARMOR_0.getTexture());
		armorBrokenTexture = assetManager.get(AssetEnum.ARMOR_1.getTexture());
		Texture armorDollTexture = assetManager.get(AssetEnum.ARMOR_DOLL.getTexture());
		// dolls
		initImage(armorDollTexture, barX + 150, 700, 250);
		initImage(armorDollTexture, 1425, 700, 250);
		// body armor
		armorArmor = initImage(character.getArmor() == null || character.getArmor().getDestructionLevel() == 0 ? armorTexture : armorBrokenTexture, barX + 224, 823, 50);
		armorArmor.addListener(new ClickListener() {
	        @Override
	        public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
	        	if (onload) {
	        		onload = false;
	        	}
	        	else {
					skillDisplay.setText(character.getArmorStatus());
					bonusDisplay.setText("");
					penaltyDisplay.setText("");
					showHoverGroup();
	        	}
			}
			@Override
	        public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
				hideHoverGroup();
			}
	    });	
		
		enemyArmorArmor = initImage(enemy.getArmor() == null || enemy.getArmor().getDestructionLevel() == 0 ? armorTexture : armorBrokenTexture, 1497, 823, 50);
		enemyArmorArmor.addListener(new ClickListener() {
	        @Override
	        public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
	        	if (onload) {
	        		onload = false;
	        	}
	        	else {
					skillDisplay.setText(enemy.getArmorStatus());
					bonusDisplay.setText("");
					penaltyDisplay.setText("");
					showHoverGroup();
	        	}
			}
			@Override
	        public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
				hideHoverGroup();
			}
	    });	
		// underwear first
		underwearArmor = initImage(character.getUnderwear() == null || character.getUnderwear().getDestructionLevel() == 0 ? armorTexture : armorBrokenTexture, barX + 224, 750, 50);
		enemyUnderwearArmor = initImage(enemy.getUnderwear() == null || enemy.getUnderwear().getDestructionLevel() == 0 ? armorTexture : armorBrokenTexture, 1497, 750, 50);
		// then legwear
		legwearArmor = initImage(character.getLegwear() == null || character.getLegwear().getDestructionLevel() == 0 ? armorTexture : armorBrokenTexture, barX + 224, 770, 50);
		legwearArmor.addListener(new ClickListener() {
	        @Override
	        public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
	        	if (onload) {
	        		onload = false;
	        	}
	        	else {
					skillDisplay.setText(character.getLegwearStatus());
					bonusDisplay.setText("");
					penaltyDisplay.setText("");
					showHoverGroup();
	        	}
			}
			@Override
	        public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
				hideHoverGroup();
			}
	    });	
		enemyLegwearArmor = initImage(enemy.getLegwear() == null || enemy.getLegwear().getDestructionLevel() == 0 ? armorTexture : armorBrokenTexture, 1497, 770, 50);
		enemyLegwearArmor.addListener(new ClickListener() {
	        @Override
	        public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
	        	if (onload) {
	        		onload = false;
	        	}
	        	else {
					skillDisplay.setText(enemy.getLegwearStatus());
					bonusDisplay.setText("");
					penaltyDisplay.setText("");
					showHoverGroup();
	        	}
			}
			@Override
	        public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
				hideHoverGroup();
			}
	    });	
		boolean hasNoShield = character.getShield() == null || character.getShield().getDestructionLevel() == 2;
		shieldArmor = initImage(hasNoShield ? nullTexture : character.getShield().getDestructionLevel() == 0 ? armorTexture : armorBrokenTexture, barX + 284, 810, 50);
		if (!hasNoShield) {
			shieldArmor.addListener(new ClickListener() {
		        @Override
		        public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
		        	if (onload) {
		        		onload = false;
		        	}
		        	else {
						skillDisplay.setText(character.getShieldStatus());
						bonusDisplay.setText("");
						penaltyDisplay.setText("");
						showHoverGroup();
		        	}
				}
				@Override
		        public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
					hideHoverGroup();
				}
		    });		
		}
		
		boolean enemyHasNoShield = enemy.getShield() == null || enemy.getShield().getDestructionLevel() == 2;
		enemyShieldArmor = initImage(enemyHasNoShield ? nullTexture : enemy.getShield().getDestructionLevel() == 0 ? armorTexture : armorBrokenTexture, 1557, 810, 50);
		if (!enemyHasNoShield) {
			enemyShieldArmor.addListener(new ClickListener() {
		        @Override
		        public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
		        	if (onload) {
		        		onload = false;
		        	}
		        	else {
						skillDisplay.setText(enemy.getShieldStatus());
						bonusDisplay.setText("");
						penaltyDisplay.setText("");
						showHoverGroup();
		        	}
				}
				@Override
		        public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
					hideHoverGroup();
				}
		    });		
		}
		
		armorLabel = initLabel("" + character.getArmorScore(), skin, Color.BROWN, barX + 236, 833);		
		enemyArmorLabel = initLabel("" + enemy.getArmorScore(), skin, Color.BROWN, 1509, 833);
		underwearLabel = initLabel("" + character.getUnderwearScore(), skin, Color.BROWN, barX + 236, 760);		
		enemyUnderwearLabel = initLabel("" + enemy.getUnderwearScore(), skin, Color.BROWN, 1509, 760);	
		legwearLabel = initLabel("" + character.getLegwearScore(), skin, Color.BROWN, barX + 236, 780);		
		enemyLegwearLabel = initLabel("" + enemy.getLegwearScore(), skin, Color.BROWN, 1509, 780);
		
		if (character.getArmorScore() > 0) {
			armorArmor.addAction(show());
			armorLabel.addAction(show());
		}
		else {
			armorArmor.addAction(hide());
			armorLabel.addAction(hide());
		}
		
		if (enemy.getArmorScore() > 0) {
			enemyArmorArmor.addAction(show());
			enemyArmorLabel.addAction(show());
		}
		else {
			enemyArmorArmor.addAction(hide());
			enemyArmorLabel.addAction(hide());
		}
 		
		if (character.getLegwearScore() > 0) {
			underwearLabel.addAction(hide());
			legwearArmor.addAction(show());
			legwearLabel.addAction(show());
		}
		else {
			underwearArmor.addListener(new ClickListener() {
		        @Override
		        public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
		        	if (onload) {
		        		onload = false;
		        	}
		        	else {
						skillDisplay.setText(character.getUnderwearStatus());
						bonusDisplay.setText("");
						penaltyDisplay.setText("");
						showHoverGroup();
		        	}
				}
				@Override
		        public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
					hideHoverGroup();
				}
		    });				
			underwearLabel.addAction(show());
			legwearArmor.addAction(hide());
			legwearLabel.addAction(hide());
		}
		
		if (enemy.getLegwearScore() > 0) {
			enemyUnderwearLabel.addAction(hide());
			enemyLegwearArmor.addAction(show());
			enemyLegwearLabel.addAction(show());
		}
		else {
			enemyUnderwearArmor.addListener(new ClickListener() {
		        @Override
		        public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
		        	if (onload) {
		        		onload = false;
		        	}
		        	else {
						skillDisplay.setText(enemy.getUnderwearStatus());
						bonusDisplay.setText("");
						penaltyDisplay.setText("");
						showHoverGroup();
		        	}
				}
				@Override
		        public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
					hideHoverGroup();
				}
		    });	
			enemyUnderwearLabel.addAction(show());
			enemyLegwearArmor.addAction(hide());
			enemyLegwearLabel.addAction(hide());
		}
		
		Texture bloodTexture = assetManager.get(AssetEnum.BLEED.getTexture());
		bloodImage = initImage(bloodTexture, 345, 725, 75);
		enemyBloodImage = initImage(bloodTexture, 1545, 725, 75);
		bloodLabel = initLabel("" + character.getBleed(), skin, Color.RED, 345 + 19, 725 + 7);	
		bloodLabel.setAlignment(Align.center);
		bloodLabel.setWidth(10);
		enemyBloodLabel = initLabel("" + enemy.getBleed(), skin, Color.RED, 1545 + 19, 725 + 7);
		enemyBloodLabel.setAlignment(Align.center);
		enemyBloodLabel.setWidth(10);
		
		Table statusTable = new Table();
		statusTable.align(Align.topLeft);
		statusTable.setPosition(525,  850);
		uiGroup.addActor(statusTable);
		Table enemyStatusTable = new Table();
		enemyStatusTable.align(Align.topLeft);
		enemyStatusTable.setPosition(1575,  700);
		uiGroup.addActor(enemyStatusTable);
		statusLabel = initLabel(character.getStatusBlurb(), skin, Color.RED, 550, 725);
		uiGroup.removeActor(statusLabel);
		enemyStatusLabel = initLabel(enemy.getStatusBlurb(), skin, Color.RED, 1575, 725);
		uiGroup.removeActor(enemyStatusLabel);
		
		statusTable.add(statusLabel).align(Align.topLeft);
		enemyStatusTable.add(enemyStatusLabel).align(Align.topRight);
		
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
		
		initActor(new StanceActor(character), uiGroup, 600.5f, 880, 150, 172.5f);
		initActor(new StanceActor(enemy), uiGroup, 1305, 880, 150, 172.5f);
		
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
			frames.add(new TextureRegion(slashSheet, ii * 384, 0, 384, 384));
		}
		
		Animation animation = new Animation(.07f, frames);
		slash = new AnimatedImage(animation, Scaling.fit, Align.right);
		slash.setState(1);
		
		slash.setPosition(764, 564);
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
	
	public AssetEnum getMusicPath() { return musicPath; }
	
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
				Array<MutationResult> results = resolveTechniques(character, selectedTechnique, enemy, enemySelectedTechnique);
				selectedTechnique = null;
				displayTechniqueOptions();
				saveService.saveDataValue(SaveEnum.PLAYER, character);
				saveService.saveDataValue(SaveEnum.ENEMY, enemy);
				Array<String> consoleComponents = new Array<String>();
				consoleComponents.add(consoleText);
				consoleComponents.add(dialogText);
				saveService.saveDataValue(SaveEnum.CONSOLE, consoleComponents);
				saveService.saveDataValue(SaveEnum.BATTLE_RESULT, results);
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
		techniquePane.clearActions();
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
			if (character.getStance().isIncapacitatingOrErotic()) {
				TextButtonStyle style = new TextButtonStyle(button.getStyle());
				style.fontColor = Color.FOREST;
				button.setStyle(style);
			}
			else if(character.outOfStaminaOrStability(option)) {
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
	private Array<MutationResult> resolveTechniques(AbstractCharacter firstCharacter, Technique firstTechnique, AbstractCharacter secondCharacter, Technique secondTechnique) {
		consoleText = "";
		dialogText = "";
		int oldCharacterHealth = character.getCurrentHealth();
		int oldCharacterStamina = character.getCurrentStamina();
		Stability oldCharacterBalance = character.getStability();
		int oldCharacterArmor = character.getArmorScore();
		int oldCharacterLegwear = character.getLegwearScore();
		int oldCharacterUnderwear = character.getUnderwearScore();
		int oldCharacterBleed = character.getBleed();
		
		int oldEnemyHealth = enemy.getCurrentHealth();
		int oldEnemyStamina = enemy.getCurrentStamina();
		Stability oldEnemyBalance = enemy.getStability();
		int oldEnemyArmor = enemy.getArmorScore();
		int oldEnemyLegwear = enemy.getLegwearScore();
		int oldEnemyUnderwear = enemy.getUnderwearScore();
		int oldEnemyBleed = enemy.getBleed();
		
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
		Array<MutationResult> playerResults = new Array<MutationResult>();
		
		
		// the following stuff needs to happen on an asynchronous basis	- the attacks must be received immediately, but all screen printing and sound playing should not be immediate	
		
		// final mutations - attacks are applied to each character, their cached state within the techniques makes the ordering here irrelevant
		for (Attack attackForFirstCharacter : attacksForFirstCharacter) {
			AttackResult results = firstCharacter.receiveAttack(attackForFirstCharacter);
			printToConsole(results.getMessages());
			printToDialog(results.getDialog());
			playerResults.addAll(results.getDefenderResults());
		}
		
		for (Attack attackForSecondCharacter : attacksForSecondCharacter) {
			AttackResult results = secondCharacter.receiveAttack(attackForSecondCharacter);
			printToConsole(results.getMessages());
			printToDialog(results.getDialog());
			playerResults.addAll(results.getAttackerResults());
		}
		
		for (Attack attackForFirstCharacter : attacksForFirstCharacter) {
			if (attackForFirstCharacter.isSuccessful() && attackForFirstCharacter.isClimax()) {
				this.addAction(new SoundAction(soundMap.get(AssetEnum.CUM), .5f));
			}
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
				}
				else {
					if (!attackForFirstCharacter.isSpell()) {
						if (attackForFirstCharacter.getStatus() == Status.BLOCKED) {
							this.addAction(sequence(delay(5/60f), new SoundAction(soundMap.get(AssetEnum.BLOCK_SOUND), 1.5f)));
						}
						else if (attackForFirstCharacter.getStatus() == Status.PARRIED) {
							this.addAction(sequence(delay(5/60f), new SoundAction(soundMap.get(AssetEnum.PARRY_SOUND), .5f)));
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
			if (attackForSecondCharacter.isSuccessful() && attackForSecondCharacter.isClimax()) {
				this.addAction(sequence(delay(5/60f), new SoundAction(soundMap.get(AssetEnum.CUM), .5f)));
			}
			if (character.getStance() == Stance.CASTING && attackForSecondCharacter.isSuccessful()) {
				this.addAction(sequence(delay(5/60f), new SoundAction(soundMap.get(AssetEnum.INCANTATION), .5f)));
			}
			if (attackForSecondCharacter.isSpell()) {
				this.addAction(sequence(delay(5/60f), new SoundAction(soundMap.get(AssetEnum.FIREBALL_SOUND), .5f)));
			}
			if (attackForSecondCharacter.isAttack()) {
				if (!attackForSecondCharacter.isSuccessful()) {
					this.addAction(sequence(delay(15/60f), new SoundAction(soundMap.get(AssetEnum.ATTACK_SOUND), .5f)));
				}
				else {
					if (!attackForSecondCharacter.isSpell()) {
						if (attackForSecondCharacter.getStatus() == Status.BLOCKED) {
							this.addAction(sequence(delay(5/60f), new SoundAction(soundMap.get(AssetEnum.BLOCK_SOUND), .5f)));
						}
						else if (attackForSecondCharacter.getStatus() == Status.PARRIED) {
							this.addAction(sequence(delay(5/60f), new SoundAction(soundMap.get(AssetEnum.PARRY_SOUND), .5f)));
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
			System.out.println("Player is in " + firstCharacter.getStance() + " stance and enemy is in " + secondCharacter.getStance() + " stance.");
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
	
		setDiffLabel(characterHealthDiff, character.getCurrentHealth() - oldCharacterHealth);
		setDiffLabel(characterStaminaDiff, character.getCurrentStamina() - oldCharacterStamina);
		setDiffLabel(characterBalanceDiff, character.getStability().ordinal() - oldCharacterBalance.ordinal());
		setDiffLabel(characterArmorDiff, character.getArmorScore() - oldCharacterArmor);
		setDiffLabel(characterLegwearDiff, character.getLegwearScore() - oldCharacterLegwear);
		setDiffLabel(characterUnderwearDiff, character.getUnderwearScore() - oldCharacterUnderwear);
		setDiffLabel(characterBleedDiff, character.getBleed() - oldCharacterBleed, true);

		setDiffLabel(enemyHealthDiff, enemy.getCurrentHealth() - oldEnemyHealth);
		setDiffLabel(enemyStaminaDiff, enemy.getCurrentStamina() - oldEnemyStamina);
		setDiffLabel(enemyBalanceDiff, enemy.getStability().ordinal() - oldEnemyBalance.ordinal());
		setDiffLabel(enemyArmorDiff, enemy.getArmorScore() - oldEnemyArmor);
		setDiffLabel(enemyLegwearDiff, enemy.getLegwearScore() - oldEnemyLegwear);
		setDiffLabel(enemyUnderwearDiff, enemy.getUnderwearScore() - oldEnemyUnderwear);
		setDiffLabel(enemyBleedDiff, enemy.getBleed() - oldEnemyBleed, true);
		
		enemyWeaponLabel.setText("Weapon: " + (enemy.getWeapon() != null ? enemy.getWeapon().getName() : "Unarmed"));
		armorLabel.setText("" + character.getArmorScore());
		enemyArmorLabel.setText("" + enemy.getArmorScore());	
		legwearLabel.setText("" + character.getLegwearScore());
		enemyLegwearLabel.setText("" + enemy.getLegwearScore());	
		underwearLabel.setText("" + character.getUnderwearScore());
		enemyUnderwearLabel.setText("" + enemy.getUnderwearScore());	
		bloodLabel.setText("" + character.getBleed());
		enemyBloodLabel.setText("" + enemy.getBleed());	
		statusLabel.setText(character.getStatusBlurb());
		enemyStatusLabel.setText(enemy.getStatusBlurb());
		
		armorArmor.setDrawable(getDrawable(character.getArmor() == null || character.getArmor().getDestructionLevel() == 0 ? armorTexture : armorBrokenTexture));
		enemyArmorArmor.setDrawable(getDrawable(enemy.getArmor() == null || enemy.getArmor().getDestructionLevel() == 0 ? armorTexture : armorBrokenTexture));
		
		underwearArmor.setDrawable(getDrawable(character.getUnderwear() == null || character.getUnderwear().getDestructionLevel() == 0 ? armorTexture : armorBrokenTexture));
		enemyUnderwearArmor.setDrawable(getDrawable(enemy.getUnderwear() == null || enemy.getUnderwear().getDestructionLevel() == 0 ? armorTexture : armorBrokenTexture));
		
		legwearArmor.setDrawable(getDrawable(character.getLegwear() == null || character.getLegwear().getDestructionLevel() == 0 ? armorTexture : armorBrokenTexture));
		enemyLegwearArmor.setDrawable(getDrawable(enemy.getLegwear() == null || enemy.getLegwear().getDestructionLevel() == 0 ? armorTexture : armorBrokenTexture));
		
		shieldArmor.setDrawable(getDrawable(character.getShield() == null ? nullTexture : character.getShield().getDestructionLevel() == 0 ? armorTexture : character.getShield().getDestructionLevel() == 1 ? armorBrokenTexture : nullTexture));
		enemyShieldArmor.setDrawable(getDrawable(enemy.getShield() == null ? nullTexture : enemy.getShield().getDestructionLevel() == 0 ? armorTexture : enemy.getShield().getDestructionLevel() == 1 ? armorBrokenTexture : nullTexture));
	
		if (character.getLegwearScore() > 0) {
			underwearLabel.addAction(hide());
			legwearArmor.addAction(show());
			legwearLabel.addAction(show());
		}
		else {
			underwearArmor.addListener(new ClickListener() {
		        @Override
		        public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
		        	if (onload) {
		        		onload = false;
		        	}
		        	else {
						skillDisplay.setText(character.getUnderwearStatus());
						showHoverGroup();
		        	}
				}
				@Override
		        public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
					hideHoverGroup();
				}
		    });				
			underwearLabel.addAction(show());
			legwearArmor.addAction(hide());
			legwearLabel.addAction(hide());
		}
		
		if (enemy.getLegwearScore() > 0) {
			enemyUnderwearLabel.addAction(hide());
			enemyLegwearArmor.addAction(show());
			enemyLegwearLabel.addAction(show());
		}
		else {
			enemyUnderwearArmor.addListener(new ClickListener() {
		        @Override
		        public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
		        	if (onload) {
		        		onload = false;
		        	}
		        	else {
						skillDisplay.setText(enemy.getUnderwearStatus());
						showHoverGroup();
		        	}
				}
				@Override
		        public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
					hideHoverGroup();
				}
		    });	
			enemyUnderwearLabel.addAction(show());
			enemyLegwearArmor.addAction(hide());
			enemyLegwearLabel.addAction(hide());
		}
		
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
		masculinityIcon.setDrawable(getDrawable(character.getMasculinityPath()));	
		
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
				
		return playerResults;
	}
	
	private void setDiffLabel(Label label, int value) {
		setDiffLabel(label, value, false);
	}
	
	private void setDiffLabel(final Label label, int value, boolean reverse) {
		label.clearActions();
		if (value == 0) {
			label.setText("");
		}
		else if (value > 0) {
			label.setColor(reverse ? Color.RED : Color.GREEN);
			label.setText("+" + value);
			label.addAction(sequence(alpha(1), fadeOut(6)));
			label.setFontScale(1.5f);
			label.addAction(sequence(delay(1), new Action(){
				@Override
				public boolean act(float delta) {
					label.setFontScale(1);
					return false;
				} }));
		}
		else {
			label.setColor(reverse ? Color.GREEN : Color.RED);
			label.setText(String.valueOf(value));
			label.addAction(sequence(alpha(1), fadeOut(6)));
			label.setFontScale(1.5f);
			label.addAction(sequence(delay(1), new Action(){
				@Override
				public boolean act(float delta) {
					label.setFontScale(1);
					return false;
				} }));
		}
	}
	
	private void setEnemyTechnique() {
		enemySelectedTechnique = enemy.getTechnique(character);
		uiGroup.removeActor(enemySkill);
		enemySkill = new SkillText(character.getBattlePerception() < 7 ? "" : enemySelectedTechnique.getTechniqueName(), skin, assetManager.get(enemySelectedTechnique.getStance().getTexture()));		
		uiGroup.addActor(enemySkill);
		enemySkill.setPosition(1300, 750);
		if (character.getBattlePerception() < 5) {
			enemySkill.addAction(Actions.hide());
		}
	}
	
	private TextureRegionDrawable getDrawable(AssetDescriptor<Texture> AssetInfo) { return getDrawable(assetManager.get(AssetInfo)); }
	private TextureRegionDrawable getDrawable(Texture texture) { return new TextureRegionDrawable(new TextureRegion(texture)); }
	
	private void printToConsole(Array<String> results) {
		for (String result: results) {
			printToConsole(result);
		}
	}
	
	private void printToConsole(String result) { consoleText += result + "\n"; }

	private void printToDialog(Array<String> results) {
		for (String result: results) {
			printToDialog(result);
		}
	}
	private void printToDialog(String result) { dialogText += result + "\n"; }
	
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
			bonusDisplay.setText("");
			
			Group popupGroup = new Group();
			this.addActor(popupGroup);
			
			Image popupImage = new Image(assetManager.get(AssetEnum.BATTLE_HOVER.getTexture()));
			popupImage.setBounds(325, 75, popupImage.getWidth() + 100, popupImage.getHeight() + 100);
			popupGroup.addActor(popupImage);			
			popupGroup.addAction(fadeIn(.1f));
			
			Table statusResults = new Table();
			statusResults.align(Align.topLeft);
			
			Label outcomeDisplay = new Label(enemy.getOutcomeText(character), skin);
			outcomeDisplay.setWrap(true);
			outcomeDisplay.setColor(battleOutcome == Outcome.VICTORY || battleOutcome == Outcome.SATISFIED ? Color.FOREST : Color.FIREBRICK);
			outcomeDisplay.setAlignment(Align.top);
			statusResults.add(outcomeDisplay).width(580).align(Align.top).row();
			statusResults.row();			
			Label newLabel = new Label("Results: ", skin);
			newLabel.setColor(Color.BLACK);
			statusResults.add(newLabel).fillY().align(Align.left).row();
			Array<MutationResult> compactedResults = MutationResult.collapse(battleResults); 
			for (MutationResult result : compactedResults) {
				statusResults.add(new MutationActor(result, assetManager.get(result.getTexture()), skin, true)).fillY().padLeft(50).align(Align.left).row();
			}
			
			statusResults.setPosition(412, 525);
			
			popupGroup.addActor(statusResults);		
			
			uiGroup.removeActor(techniquePane);
			uiGroup.removeActor(hoverGroup);
			
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
	
	private void popDialog(String dialog) {
		Group popupGroup = new Group();
		this.addActor(popupGroup);
		
		Image popupImage = new Image(assetManager.get(AssetEnum.BATTLE_HOVER.getTexture()));
		popupImage.setBounds(625, 375, popupImage.getWidth() + 100, popupImage.getHeight() + 100);
		popupGroup.addActor(popupImage);			
		popupGroup.addAction(fadeIn(.1f));
		
		Table statusResults = new Table();
		statusResults.align(Align.topLeft);
		
		Label outcomeDisplay = new Label(dialog, skin);
		outcomeDisplay.setWrap(true);
		outcomeDisplay.setAlignment(Align.top);
		outcomeDisplay.setColor(Color.BLACK);
		statusResults.add(outcomeDisplay).width(580).align(Align.top).row();		
		statusResults.setPosition(712, 825);
		
		popupGroup.addActor(statusResults);	
		popupGroup.addAction(sequence(delay(25), hide()));
		popupGroup.addListener(new ClickListener() {
			@Override
	        public void clicked(InputEvent event, float x, float y) {
	        	popupGroup.addAction(hide());
	        }
		});
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
	
	private Actor initActor(Actor actor, Group group, float x, float y) { return initActor(actor, group, x, y, actor.getWidth(), actor.getHeight()); }	
	private Actor initActor(Actor actor, Group group, float x, float y, float width, float height) {
		group.addActor(actor);
		actor.setBounds(x, y, width, height);
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
	
	private Label initLabel(String value, Skin skin, Color color, float x, float y) {
		Label newLabel = new Label(value, skin);
		newLabel.setColor(color);
		newLabel.setPosition(x, y);
		uiGroup.addActor(newLabel);
		return newLabel;
	}
	
	public enum Outcome {
		VICTORY, DEFEAT, KNOT_ANAL, KNOT_ORAL, SATISFIED, SUBMISSION, DEATH
	}
	
	/* REFACTOR AND REMOVE BEYOND THIS LINE */
	public boolean isBattleOver() {
		return battleOver;
	}
	
	public int getOutcomeScene() {
		return outcomes.get(outcome.toString());
	}
	
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

		@Override
	    public void draw(Batch batch, float parentAlpha) {
			super.draw(batch, parentAlpha);
			Stance stance = character.getStance();
			String stanceName = stance.getLabel();
			batch.draw(getStanceImage(stance), getX(), getY(), getWidth(), getHeight());
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

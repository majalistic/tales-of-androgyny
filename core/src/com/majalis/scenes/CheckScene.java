package com.majalis.scenes;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.OrderedMap;
import com.majalis.character.PlayerCharacter;
import com.majalis.character.EnemyEnum;
import com.majalis.character.AbstractCharacter.Stat;
import com.majalis.character.PlayerCharacter.QuestType;
import com.majalis.character.Perk;
import com.majalis.encounter.Background;
import com.majalis.encounter.EncounterHUD;
import com.majalis.save.SaveService;
import com.majalis.save.SaveManager.JobClass;
import com.majalis.screens.TimeOfDay;

public class CheckScene extends AbstractTextScene {

	private final PlayerCharacter character;
	private final Scene defaultScene;
	private final Stat statToCheck;
	private final Perk perkToCheck;
	private final OrderedMap<Integer, Scene> checkValues;
	private final CheckType checkType;
	private final Scene clearScene;
	private final Background background;
	private Scene nextScene;
	private int ignores;
	private boolean success;
	
	public CheckScene(OrderedMap<Integer, Scene> sceneBranches, int sceneCode, AssetManager assetManager, SaveService saveService, BitmapFont font, Background background, Stat stat, OrderedMap<Integer, Scene> checkValues, Scene defaultScene, PlayerCharacter character, EncounterHUD hud) {
		this(sceneBranches, sceneCode, assetManager, saveService, font, background, stat, null, null, checkValues, null, defaultScene, character, hud);
	}
	
	public CheckScene(OrderedMap<Integer, Scene> sceneBranches, int sceneCode, AssetManager assetManager, SaveService saveService, BitmapFont font, Background background, Perk perk, OrderedMap<Integer, Scene> checkValues, Scene defaultScene, PlayerCharacter character, EncounterHUD hud) {
		this(sceneBranches, sceneCode, assetManager, saveService, font, background, null, perk, null, checkValues, null, defaultScene, character, hud);
	}
	
	public CheckScene(OrderedMap<Integer, Scene> sceneBranches, int sceneCode, AssetManager assetManager, SaveService saveService, BitmapFont font, Background background, CheckType checkType, Scene clearScene, Scene defaultScene, PlayerCharacter character, EncounterHUD hud) {
		this(sceneBranches, sceneCode, assetManager, saveService, font, background, null, null, checkType, null, clearScene, defaultScene, character, hud);
	}
	
	public CheckScene(OrderedMap<Integer, Scene> sceneBranches, int sceneCode, AssetManager assetManager, SaveService saveService, BitmapFont font, Background background, Stat stat, Perk perk, CheckType checkType, OrderedMap<Integer, Scene> checkValues, Scene clearScene, Scene defaultScene, PlayerCharacter character, EncounterHUD hud) {
		super(sceneBranches, sceneCode, assetManager, font, saveService, background, hud);
		this.checkValues = checkValues;
		this.clearScene = clearScene;
		this.defaultScene = defaultScene;
		this.character = character;
		this.statToCheck = stat;
		this.perkToCheck = perk;
		this.checkType = checkType;
		this.background = background;
		ignores = 0;
		success = false;
	}
	
	@Override
	public void activate() {
		super.activate();
		Table table = new Table();
		table.setPosition(1000, 500);
		table.align(Align.top);
		table.add(display);
		this.addActor(table);
		TextButton fail = new TextButton("Fail", skin);
		fail.setBounds(950, 500, 100, 75);
		this.addActor(fail);
		TextButton resume = new TextButton("Continue", skin);
		resume.setBounds(925, 575, 150, 75);
		resume.addListener(new ClickListener() { 
			@Override
	        public void clicked(InputEvent event, float x, float y) {
				nextScene();
	        }
		});
		this.addActor(resume);
		checkForSuccess(fail, resume);		
	}

	// if there was a success, add a button that will fail the next check, recalculate nextScene and if success there, do this again
	private void checkForSuccess(TextButton fail, TextButton resume) {
		nextScene = getNextScene();	
		if (success) {
			fail.clearListeners();
			fail.addAction(Actions.show());
			resume.addAction(Actions.show());
			fail.addListener(new ClickListener() { 
				@Override
		        public void clicked(InputEvent event, float x, float y) {
					ignores++;
					checkForSuccess(fail, resume);
		        }
			});
		}
		else {
			fail.addAction(Actions.hide());
			resume.addAction(Actions.hide());
		}
		if (display.getText().toString().equals("")) nextScene();
		background.setColor(TimeOfDay.getTime(character.getTime()).getColor());
	}
	
	private Scene getNextScene() {
		String passValue = "PASSED!\n";
		String failValue = "FAILURE!\n";
		String toDisplay = "";
		
		int tempIgnores = ignores;
		
		if (checkType != null) {
			if (checkType.getCheck(character) && tempIgnores == 0) {
				toDisplay += checkType.getSuccess();
				display.setText(toDisplay);
				success = true;
				return clearScene;
			}
			else {
				toDisplay += checkType.getFailure();
				display.setText(toDisplay);
				if (checkType.canBeFlubbed()) {
					success = false;
				}
				
				return defaultScene;
			}			
		}
		else if (perkToCheck != null) {
			int amount = character.getPerks().get(perkToCheck, 0);
			toDisplay += "Your " + perkToCheck.getLabel() + " score: " + amount + "\n\n";
			for (Integer threshold : checkValues.keys()) {
				if (threshold == 0) break;
				toDisplay += perkToCheck.getLabel() + " check (" + threshold + "): ";
				if (amount >= threshold && tempIgnores-- == 0) {
					toDisplay += perkToCheck.isPositive() ? passValue : failValue;
					success = perkToCheck.isPositive() ? true : false;
					display.setText(toDisplay);
					return checkValues.get(threshold);
				}
				else {
					toDisplay += perkToCheck.isPositive() ? failValue : passValue;
				}
			}
			success = false;
			display.setText(toDisplay);
			return checkValues.get(0);
		}
		else {
			int amount = statToCheck == Stat.CHARISMA ? character.getLewdCharisma() : character.getRawStat(statToCheck);
			int baseAmount = character.getBaseStat(statToCheck);
			toDisplay += "Your current " + statToCheck.toString() + " score: " + amount + " (Base: " + baseAmount + ")" + "\n" + character.getStatPenaltyDisplay() + "\n\n";
			for (Integer threshold : checkValues.keys()) {
				if (threshold == 0) break;
				toDisplay += statToCheck.toString() + " check (" + threshold + "): ";
				if (amount >= threshold  && tempIgnores-- == 0) {
					toDisplay += passValue;
					display.setText(toDisplay);
					success = true;
					return checkValues.get(threshold);
				}
				else {
					toDisplay += failValue;
				}
			}
			display.setText(toDisplay);
			display.addAction(Actions.moveBy(0, 200));
			success = false;
			return checkValues.get(0);
		}
	}
	
	@Override
	protected void nextScene() {
		clearActions();
		nextScene.setActive();
		isActive = false;
		addAction(Actions.hide());	
	}
	
	@Override
	public String getText() {
		return statToCheck != null ? statToCheck.getLabel() + " check!" : perkToCheck != null ? perkToCheck.getLabel() + " check!" : "";
	}
	
	public enum CheckType {
		VIRGIN ("Are you an anal virgin? PASSED!", "Are you an anal virgin? FAILURE!") { 
			@Override
			protected boolean getCheck(PlayerCharacter character) { return character.isVirgin(); } 
		},
		GOBLIN_VIRGIN ("You've never had goblin cock up the ass before. (PASSED)", "You've had goblin cock up the ass before. (FAILURE)") { 
			@Override
			protected boolean getCheck(PlayerCharacter character) { return character.isVirgin(EnemyEnum.GOBLIN); } 
		}, 
		GOBLIN_KNOWN ("", "") { 
			@Override
			protected boolean getCheck(PlayerCharacter character) { return character.getQuestStatus(QuestType.GOBLIN) == 0; } 
		}, 
		ORC_ENCOUNTERED ("You come across a bizarre sight.", "You see her, again.  The orc.") {
			@Override
			protected boolean getCheck(PlayerCharacter character) { return character.getQuestStatus(QuestType.ORC) == 0; } 
		},
		ORC_BRAVE ("You confidently approach her.", "You recall your cowardice.") { 
			@Override
			protected boolean getCheck(PlayerCharacter character) { return character.getQuestStatus(QuestType.ORC) == 1; } 
		}, 
		CRIER ("", "") { 
			@Override
			protected boolean getCheck(PlayerCharacter character) { return character.getQuestStatus(QuestType.CRIER) == 0; }  
		}, 
		CRIER_QUEST ("", "") { 
			@Override
			protected boolean getCheck(PlayerCharacter character) { return character.getQuestStatus(QuestType.CRIER) == 1; }  
		}, 
		CRIER_REFUSE ("", "") { 
			@Override
			protected boolean getCheck(PlayerCharacter character) { return character.getQuestStatus(QuestType.CRIER) == 2; }  
		}, 
		CRIER_KNOWLEDGE ("", "") { 
			@Override
			protected boolean getCheck(PlayerCharacter character) { return character.getQuestStatus(QuestType.CRIER) >= 3; }  
		}, 
		INN_0 ("", "") { 
			@Override
			protected boolean getCheck(PlayerCharacter character) { return character.getQuestStatus(QuestType.INNKEEP) == 0; }  
		}, 
		INN_1 ("", "") { 
			@Override
			protected boolean getCheck(PlayerCharacter character) { return character.getQuestStatus(QuestType.INNKEEP) == 1; }  
		}, 
		INN_2 ("", "") { 
			@Override
			protected boolean getCheck(PlayerCharacter character) { return character.getQuestStatus(QuestType.INNKEEP) == 2; }  
		}, 
		INN_3 ("", "") { 
			@Override
			protected boolean getCheck(PlayerCharacter character) { return character.getQuestStatus(QuestType.INNKEEP) == 3; }  
		}, 
		ADVENTURER_ENCOUNTERED ("", "") { 
			@Override
			protected boolean getCheck(PlayerCharacter character) { return character.getQuestStatus(QuestType.TRUDY) == 0; }  
		}, 
		ADVENTURER_HUNT ("", "") { 
			@Override
			protected boolean getCheck(PlayerCharacter character) { return character.getQuestStatus(QuestType.TRUDY) == 1; }  
		},
		TRUDY_GOT_IT ("", "") { 
			@Override
			protected boolean getCheck(PlayerCharacter character) { return character.getQuestStatus(QuestType.TRUDY) == 2; }  
		},
		PLAYER_GOT_IT ("", "") { 
			@Override
			protected boolean getCheck(PlayerCharacter character) { return character.getQuestStatus(QuestType.TRUDY) == 3; }  
		},
		TRUDY_LAST ("", "") { 
			@Override
			protected boolean getCheck(PlayerCharacter character) { return character.getQuestStatus(QuestType.TRUDY) == 4; }  
		},
		TRUDY_COMPANION1("", "") { 
			@Override
			protected boolean getCheck(PlayerCharacter character) { return character.getQuestStatus(QuestType.TRUDY) == 5; }  
		}, 
		TRUDY_COMPANION2("", "") { 
			@Override
			protected boolean getCheck(PlayerCharacter character) { return character.getQuestStatus(QuestType.TRUDY) == 6; }  
		}, 
		OGRE_DONE ("", "") { 
			@Override
			protected boolean getCheck(PlayerCharacter character) { return character.getQuestStatus(QuestType.OGRE) == 0; }  
		},
		SPIDER ("", "") { 
			@Override
			protected boolean getCheck(PlayerCharacter character) { return character.getQuestStatus(QuestType.SPIDER) == 0; }  
		},
		ALIVE ("You're still conscious.", "You fall unconscious!") { 
			@Override
			protected boolean getCheck(PlayerCharacter character) { return character.getCurrentHealth() > 0; }  
		}, 
		HAVE_DEBT ("You are debt free.", "You owe a debt.") { 
			@Override
			protected boolean getCheck(PlayerCharacter character) { return character.getCurrentDebt() <= 0; }  
		}, 
		BIG_DEBT ("", "You owe a tremendous debt.") { 
			@Override
			protected boolean getCheck(PlayerCharacter character) { return character.getCurrentDebt() < 100; }  
		}, 
		PROSTITUTE ("", "") { 
			@Override
			protected boolean getCheck(PlayerCharacter character) { return character.getQuestStatus(QuestType.BROTHEL) != 0; }  
		}, 
		PROSTITUTE_WARNING_GIVEN ("", "") { 
			@Override
			protected boolean getCheck(PlayerCharacter character) { return character.getQuestStatus(QuestType.BROTHEL) > 1; }  
		}, 
		SCOUT_LEVEL_2 ("You are keenly aware of this area. (Scouting success!)", "This area is unknown to you.") { 
			@Override
			protected boolean getCheck(PlayerCharacter character) { return character.getScoutingScore() >= 2; }  
		}, 
		SCOUT_LEVEL_3 ("You have scouted this area thoroughly. (Scouting success!)", "This area is relatively unknown to you.") { 
			@Override
			protected boolean getCheck(PlayerCharacter character) { return character.getScoutingScore() >= 3; }  
		}, 
		ELF_UNSEEN ("", "") { 
			@Override
			protected boolean getCheck(PlayerCharacter character) { int check = character.getQuestStatus(QuestType.ELF); return check == 0; }  
		}, 
		ELF_DECLINED ("", "") { 
			@Override
			protected boolean getCheck(PlayerCharacter character) { int check = character.getQuestStatus(QuestType.ELF); return check == 2; }  
		},
		ELF_ACCEPTED ("", "") { 
			@Override
			protected boolean getCheck(PlayerCharacter character) { int check = character.getQuestStatus(QuestType.ELF); return check == 3; }  
		},
		ELF_BROTHEL ("", "") { 
			@Override
			protected boolean getCheck(PlayerCharacter character) { int check = character.getQuestStatus(QuestType.ELF); return check == 4; }  
		}, 
		ELF_HEALER ("", "") { 
			@Override
			protected boolean getCheck(PlayerCharacter character) { int check = character.getQuestStatus(QuestType.ELF); return check == 6; }  
		},
		ELF_COMPANION1("", "") { 
			@Override
			protected boolean getCheck(PlayerCharacter character) { int check = character.getQuestStatus(QuestType.ELF); return check == 5; }  
		}, 
		ELF_COMPANION2("", "") { 
			@Override
			protected boolean getCheck(PlayerCharacter character) { int check = character.getQuestStatus(QuestType.ELF); return check == 9; }  
		}, 
		ELF_COMPANION3("", "") { 
			@Override
			protected boolean getCheck(PlayerCharacter character) { int check = character.getQuestStatus(QuestType.ELF); return check == 10; }  
		}, 
		// for encounters, can also make a RandomBranch variant on choice/check/battle/etc.
		LUCKY ("", "") { 
			@Override
			protected boolean getCheck(PlayerCharacter character) { return character.isLucky(); }  
		},
		DAY ("", "") { 
			@Override
			protected boolean getCheck(PlayerCharacter character) { return character.isDayTime(); }  
		}, 
		PLUGGED ("", "") { 
			@Override
			protected boolean getCheck(PlayerCharacter character) { return character.isPlugged(); }  
		}, 
		CHASTITIED ("", "") { 
			@Override
			protected boolean getCheck(PlayerCharacter character) { return character.isChastitied(); }  
		}, 
		PALADIN ("You've taken an oath of chastity.", "You've taken no oath of chastity.") { 
			@Override
			protected boolean getCheck(PlayerCharacter character) { return character.getJobClass() == JobClass.PALADIN; }  
		},
		DEBT_FIRST_ENCOUNTER("", "") { 
			@Override
			protected boolean getCheck(PlayerCharacter character) { int check = character.getQuestStatus(QuestType.DEBT); return check == 0; }  
		},
		DEBT_WARNING("", "") { 
			@Override
			protected boolean getCheck(PlayerCharacter character) { int check = character.getQuestStatus(QuestType.DEBT); return check == 1; }  
		}, 
		GADGETEER_MET("", "") { 
			@Override
			protected boolean getCheck(PlayerCharacter character) { int check = character.getQuestStatus(QuestType.GADGETEER); return check > 0; }  
		}, 
		GADGETEER_TESTED("", "") { 
			@Override
			protected boolean getCheck(PlayerCharacter character) { int check = character.getQuestStatus(QuestType.GADGETEER); return check == 2; }  
		}, 
		MADAME_MET("", "") { 
			@Override
			protected boolean getCheck(PlayerCharacter character) { int check = character.getQuestStatus(QuestType.MADAME); return check > 0; }  
		},
		QUETZAL_HEARD("", "") { 
			@Override
			protected boolean getCheck(PlayerCharacter character) { int check = character.getQuestStatus(QuestType.QUETZAL); return check == 1; }  
		},
		QUETZAL_MET("", "") { 
			@Override
			protected boolean getCheck(PlayerCharacter character) { int check = character.getQuestStatus(QuestType.QUETZAL); return check == 2; }  
		},
		QUETZAL_DEFEATED("", "") { 
			@Override
			protected boolean getCheck(PlayerCharacter character) { int check = character.getQuestStatus(QuestType.QUETZAL); return check == 3; }  
		},
		QUETZAL_SLAIN("", "") { 
			@Override
			protected boolean getCheck(PlayerCharacter character) { int check = character.getQuestStatus(QuestType.QUETZAL); return check >= 3; }  
		},
		WITCH_MET("", "") { 
			@Override
			protected boolean getCheck(PlayerCharacter character) { int check = character.getQuestStatus(QuestType.WITCH); return check > 0; }  
		},
		BLESSING_PURCHASED("", "") { 
			@Override
			protected boolean getCheck(PlayerCharacter character) { int check = character.getQuestStatus(QuestType.WITCH); return check == 2; }  
		}, 
		CRUEL_ORAL_UNWARNED("", "") { 
			@Override
			protected boolean getCheck(PlayerCharacter character) { int check = character.getQuestStatus(QuestType.MOUTH_FIEND); return check == 0; }  
		},  
		CRUEL_ORAL_BANNED("", "") { 
			@Override
			protected boolean getCheck(PlayerCharacter character) { int check = character.getQuestStatus(QuestType.MOUTH_FIEND); return check > 1; }  
		}, 
		MOUTH_FIEND_INTRO("", "") { 
			@Override
			protected boolean getCheck(PlayerCharacter character) { int check = character.getQuestStatus(QuestType.MOUTH_FIEND); return check == 2; }  
		}, 
		MOUTH_FIEND_CASTLE("", "") { 
			@Override
			protected boolean getCheck(PlayerCharacter character) { int check = character.getQuestStatus(QuestType.MOUTH_FIEND); return check == 3; }  
		},
		MOUTH_FIEND_DODGED("", "") { 
			@Override
			protected boolean getCheck(PlayerCharacter character) { int check = character.getQuestStatus(QuestType.MOUTH_FIEND); return check == 10; }  
		}, 
		HIGH_LUST("", "") { 
			@Override
			protected boolean getCheck(PlayerCharacter character) { return character.getCurrentLust() >= 50; }  
		}, 
		MAX_LUST("", "") { 
			@Override
			protected boolean getCheck(PlayerCharacter character) { return character.getCurrentLust() >= 100; }  
		}, 
		MERMAID_FIRST_ENCOUNTER("", "") {
			@Override
			protected boolean getCheck(PlayerCharacter character) { int check = character.getQuestStatus(QuestType.MERMAID); return check == 0; }  
		},
		MERMAID_TRYAGAIN("", "") {
			@Override
			protected boolean getCheck(PlayerCharacter character) { int check = character.getQuestStatus(QuestType.MERMAID); return check == 1; }  
		},
		MERMAID_ATTACK_ON_SIGHT("", "") {
			@Override
			protected boolean getCheck(PlayerCharacter character) { int check = character.getQuestStatus(QuestType.MERMAID); return check == 2; }  
		},
		MERMAID_EGG_HATCH("", "") {
			@Override
			protected boolean getCheck(PlayerCharacter character) { int check = character.getQuestStatus(QuestType.MERMAID); return check == 5; }  
		},
		MERMAID_EGG_ACCIDENTAL_HATCH("", "") {
			@Override
			protected boolean getCheck(PlayerCharacter character) { int check = character.getQuestStatus(QuestType.MERMAID); return check == 6; }  
		},
		MERMAID_HATCHED("", "") {
			@Override
			protected boolean getCheck(PlayerCharacter character) { int check = character.getQuestStatus(QuestType.MERMAID); return check == 7; }  
		}, 
		TRAINER_VISITED("", "") {
			@Override
			protected boolean getCheck(PlayerCharacter character) { int check = character.getQuestStatus(QuestType.TRAINER); return check == 1; }  
		},
		MERI_VISITED("", "") {
			@Override
			protected boolean getCheck(PlayerCharacter character) { int check = character.getQuestStatus(QuestType.MERI); return check == 1; }  
		},
		HAS_ICE_CREAM("", "") {
			@Override
			protected boolean getCheck(PlayerCharacter character) { return character.hasIceCream(); }  
		},
		BEEN_TO_HUMAN_TOWN("", "") {
			@Override
			protected boolean getCheck(PlayerCharacter character) { int check = character.getQuestStatus(QuestType.HUMAN_TOWN); return check == 1; }  
		},
		BEEN_TO_MONSTER_TOWN("", "") {
			@Override
			protected boolean getCheck(PlayerCharacter character) { int check = character.getQuestStatus(QuestType.MONSTER_TOWN); return check == 1; }  
		},
		IS_EGGED("", "") {
			@Override
			protected boolean getCheck(PlayerCharacter character) { return character.fullOfEggs(); }  
		}, 
		SPIDER_HATCH("", "") {
			@Override
			protected boolean getCheck(PlayerCharacter character) { int check = character.getQuestStatus(QuestType.SPIDER); return check == 5; }  
		}, 
		GOBLIN_BIRTH("", "") {
			@Override
			protected boolean getCheck(PlayerCharacter character) { int check = character.getQuestStatus(QuestType.GOBLIN); return check >= 6 && check < 9; }  
		},
		GOBLIN_BIRTH_HARPY("", "") {
			@Override
			protected boolean getCheck(PlayerCharacter character) { int check = character.getQuestStatus(QuestType.GOBLIN); return check == 6; }  
		}, 
		GOBLIN_BIRTH_WEREWOLF("", "") {
			@Override
			protected boolean getCheck(PlayerCharacter character) { int check = character.getQuestStatus(QuestType.GOBLIN); return check == 7; }  
		}, 
		WEARING_HELMET("SUCCESS! You're wearing a helmet!", "FAILURE! You're not wearing a helmet.") {
			@Override
			protected boolean getCheck(PlayerCharacter character) { return character.getHeadgear() != null; }  
		},
		WEARING_SHOES("SUCCESS! You're wearing protective shoes!", "FAILURE! You're not wearing protective shoes.") {
			@Override
			protected boolean getCheck(PlayerCharacter character) { return character.getFootwear() != null; }  
		}, 
		WEARING_GAUNTLETS("SUCCESS! You're wearing gauntlets!", "FAILURE! You're not wearing gauntlets.") {
			@Override
			protected boolean getCheck(PlayerCharacter character) { return character.getArmwear() != null; }  
		}, 
		HIGH_DIGNITY("You're too dignified for this.", "This isn't below your dignity.") {
			@Override
			protected boolean getCheck(PlayerCharacter character) { return character.getDignity().isHigh(); }  
		}, 
		ANY_DIGNITY("You still have your dignity.", "You have no dignity left.") {
			@Override
			protected boolean getCheck(PlayerCharacter character) { return character.getDignity().isAny(); }  
		}, 
		ANY_WILLPOWER("You resist the temptation!", "You have no willpower left.") {
			@Override
			protected boolean getCheck(PlayerCharacter character) { return character.getWillpower() > 0; }  
		}, 
		;
		private final String success;
		private final String failure;
		
		private CheckType(String success, String failure) { this.success = success; this.failure = failure; }
		
		public boolean canBeFlubbed() { return this != VIRGIN && this != GOBLIN_VIRGIN && this != PALADIN && this != ORC_ENCOUNTERED && this != ORC_BRAVE; }

		protected abstract boolean getCheck(PlayerCharacter character);
		protected String getSuccess() {
			return success;
		}
		protected String getFailure() {
			return failure;
		}
	}
}

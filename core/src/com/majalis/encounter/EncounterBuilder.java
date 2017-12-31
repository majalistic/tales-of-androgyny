package com.majalis.encounter;
import com.badlogic.gdx.Gdx;
/*
 * Class used for building an encounter from an encounter code and current state information.
 */
// this class currently has a lot of imports - part of its refactor will be to minimize integration points
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.OrderedMap;
import com.majalis.asset.AnimatedActor;
import com.majalis.asset.AnimationEnum;
import com.majalis.asset.AssetEnum;
import com.majalis.battle.BattleCode;
import com.majalis.battle.Battle.Outcome;
import com.majalis.character.Perk;
import com.majalis.character.PlayerCharacter;
import com.majalis.character.Stance;
import com.majalis.character.AbstractCharacter.Stat;
import com.majalis.save.MutationResult;
import com.majalis.save.ProfileEnum;
import com.majalis.save.SaveEnum;
import com.majalis.save.SaveManager;
import com.majalis.save.SaveService;
import com.majalis.save.SaveManager.GameContext; // this should probably be moved out of SaveManager
import com.majalis.save.SaveManager.GameMode; // this should probably be moved out of SaveManager
import com.majalis.scenes.AbstractChoiceScene;
import com.majalis.scenes.BattleScene; // this should be refactored so that encounterbuilder receives a scenebuilder
import com.majalis.scenes.CharacterCreationScene;
import com.majalis.scenes.CharacterCustomizationScene;
import com.majalis.scenes.CheckScene;
import com.majalis.scenes.ChoiceScene;
import com.majalis.scenes.EndScene;
import com.majalis.scenes.GameTypeScene;
import com.majalis.scenes.Mutation;
import com.majalis.scenes.Scene;
import com.majalis.scenes.ShopScene;
import com.majalis.scenes.SkillSelectionScene;
import com.majalis.scenes.TextScene;
import com.majalis.scenes.CheckScene.CheckType;
import com.majalis.scenes.ShopScene.Shop;
import com.majalis.scenes.ShopScene.ShopCode;
import com.majalis.encounter.Background.BackgroundBuilder;

public class EncounterBuilder {
	private final EncounterReader reader;
	private final AssetManager assetManager;
	private final SaveService saveService;
	private final BitmapFont font;
	private final IntArray sceneCodes;
	private final ObjectMap<String, Shop> shops;
	private final PlayerCharacter character;
	private final GameContext returnContext;
	private final GameMode mode;
	private final OrderedMap<Integer, Scene> masterSceneMap;
	private final ObjectMap<AnimationEnum, AnimatedActor> animationCache;
	private final Array<MutationResult> results;
	// can probably be replaced with a call to scenes.size
	private int sceneCounter;
	
	protected EncounterBuilder(EncounterReader reader, AssetManager assetManager, SaveService saveService, BitmapFont font, IntArray sceneCodes, ObjectMap<String, Shop> shops, PlayerCharacter character, GameContext returnContext, GameMode mode, Array<MutationResult> results) {
		this.reader = reader;
		this.assetManager = assetManager;
		this.saveService = saveService;
		this.font = font;
		this.sceneCodes = sceneCodes;
		this.shops = shops == null ? new ObjectMap<String, Shop>() : shops;
		this.character = character;
		this.returnContext = returnContext;
		this.mode = mode;
		this.results = results;
		this.animationCache = new ObjectMap<AnimationEnum, AnimatedActor>();
		sceneCounter = 1;
		masterSceneMap = new OrderedMap<Integer, Scene>();
	}
	
	// this needs to be moved into EncounterCode
	protected Encounter getEncounter(EncounterCode encounterCode) {
		switch (encounterCode) {
			case ADVENTURER:
				Branch trudyBattle = new Branch().battleScene(
					BattleCode.ADVENTURER,
					new Branch(Outcome.VICTORY).textScene("ADVENTURER-VICTORY").choiceScene(
						"What's the plan?", 
						new Branch("Mount him (Requires: Free cock)").require(ChoiceCheckType.FREE_COCK).textScene("ADVENTURER-TOPPED").encounterEnd(), 
						new Branch("MOUNT him (Requires: Catamite)").require(ChoiceCheckType.LEWD).textScene("ADVENTURER-BOTTOMED").encounterEnd(), 
						new Branch("Rob him").textScene("ADVENTURER-ROBBED").encounterEnd()
					),
					new Branch(Outcome.DEFEAT).textScene("ADVENTURER-DEFEAT").encounterEnd(),
					new Branch(Outcome.SATISFIED).textScene("ADVENTURER-SATISFIED").encounterEnd(),
					new Branch(Outcome.SUBMISSION).textScene("ADVENTURER-SUBMISSION").encounterEnd()
				);
				Branch trudyCaught = new Branch().textScene("ADVENTURER-TRUDY-CAUGHT").encounterEnd();
				Branch playerCaught = new Branch().textScene("ADVENTURER-SNARE-CAUGHT").encounterEnd();
				Branch scene1 = new Branch().textScene("ADVENTURER-TRUDY-TRIP").concat(trudyCaught);
				Branch scene2 = new Branch().textScene("ADVENTURER-STEP-OVER").checkScene(
					Stat.AGILITY, 
					new Branch(5).textScene("ADVENTURER-SNARE-DODGE").concat(trudyCaught),
					new Branch(0).textScene("ADVENTURER-SNARE-FAIL").concat(playerCaught)
				);
				return new Branch().textScene("ADVENTURER-INTRO").checkScene(
					CheckType.ADVENTURER_ENCOUNTERED,
					new Branch(true).textScene("ADVENTURER-ENTRANCE").encounterEnd(),
					new Branch(false).checkScene(
						CheckType.ADVENTURER_HUNT, 
						new Branch(true).textScene("ADVENTURER-HUNT-INTRO").checkScene(
							Stat.PERCEPTION,
							new Branch(6).textScene("ADVENTURER-SNARE").concat(scene1),
							new Branch(3).textScene("ADVENTURER-SNARE").concat(scene2),
							new Branch(0).concat(playerCaught)
						),
						new Branch(false).checkScene(
							CheckType.TRUDY_GOT_IT,
							new Branch(true).textScene("ADVENTURER-ANGRY-REUNION").concat(trudyBattle),
							new Branch(false).checkScene(
								CheckType.PLAYER_GOT_IT,
								new Branch(true).textScene("ADVENTURER-SMUG-REUNION").concat(trudyBattle),
								new Branch(false).checkScene(
									CheckType.TRUDY_LAST, 
									new Branch(true).textScene("ADVENTURER-END"), 
									new Branch(false).textScene("STICK")
								)
							)
						)
					)
				).getEncounter();
			case ANGEL:
				Branch angelOral = new Branch().choiceScene("Fuck her face?", new Branch("Hell Yes!").textScene("ANGEL-DEEPTHROAT"), new Branch("I'm Good").textScene("ANGEL-CUM-TRUMPET"));
				Branch angelConfess = new Branch("Confess your temptations").textScene("ANGEL-BJ").choiceScene(
					"She raises a good point... she DOES have a nice booty...",
					new Branch("Let her be your angel of sodomy?").require(ChoiceCheckType.FREE_COCK).checkScene(Stat.CHARISMA, new Branch(5).textScene("ANGEL-ANAL"), new Branch(0).textScene("ANGEL-ANAL-REJECTION").concat(angelOral)),
					new Branch("Let her blow your trumpet").textScene("ANGEL-BJ-CONT").concat(angelOral)
				);
				
				return new Branch().textScene("ANGEL-INTRO").choiceScene(
					"What do you do?", 
					new Branch("Pray").textScene("ANGEL-PRAY").choiceScene(
						"Are you a devotee?", 
						new Branch("Yes (Lie)").textScene("ANGEL-LIE"), 
						new Branch("No").textScene("ANGEL-TRUTH").choiceScene(
							"What do you do?", 
							angelConfess,
							new Branch("Attack Her").textScene("ANGEL-BATTLE").battleScene(BattleCode.ANGEL, 
								new Branch(Outcome.VICTORY).textScene("ANGEL-VICTORY").choiceScene(
									"What do you do?", 
									new Branch("Fuck her in the pussy").require(ChoiceCheckType.FREE_COCK).textScene("ANGEL-VAGINAL"),
									new Branch("Fuck her in the ass").require(ChoiceCheckType.FREE_COCK).textScene("ANGEL-BUTTSEX"),
									new Branch("Leave her alone")
								), 
								new Branch(Outcome.DEFEAT).textScene("ANGEL-DEFEAT"),  
								new Branch(Outcome.SUBMISSION).textScene("ANGEL-FACESIT"),
								new Branch(Outcome.SATISFIED).textScene("ANGEL-PACIFIST").concat(angelConfess) 
							),
							new Branch("Remain Silent").textScene("ANGEL-REJECT")
						)
					), 
					new Branch("Profane the Altar").require(ChoiceCheckType.FREE_COCK).textScene("ANGEL-PROFANE"), 
					new Branch("Leave")
				).getEncounter();
			case BANK:
				return new Branch().textScene("BANK").checkScene(
						CheckType.BIG_DEBT,
						new Branch(true).textScene("BANK-OVERDRAWN").choiceScene("Do you pay your debts?", new Branch("Pay Debt (50 GP").require(ChoiceCheckType.GOLD_GREATER_THAN_X, 50).textScene("BANK-PAY-50"), new Branch("Default").textScene("BANK-PAY-HARD").gameEnd()),
						new Branch(false).checkScene(	
							CheckType.HAVE_DEBT,
							new Branch(true).choiceScene("Pay back debt?", new Branch("Pay Debt (10 GP)").require(ChoiceCheckType.GOLD_GREATER_THAN_X, 10).textScene("BANK-PAY"), new Branch("Leave")),
							new Branch(false).choiceScene("Do you want to borrow?", new Branch("Borrow (50 GP)").textScene("BANK-BORROW"), new Branch("Leave"))
						)
				).getEncounter();
			case BEASTMISTRESS:
				return new Branch().textScene("BEASTMISTRESS-INTRO").choiceScene(
					"Snake or Pussy?", 
					new Branch("Snake").textScene("BEASTMISTRESS-ENTRANCE"),
					new Branch("Pussy").textScene("BEASTMISTRESS-PUSSY").battleScene(
						BattleCode.BEASTMISTRESS,
						new Branch(Outcome.VICTORY).textScene("BEASTMISTRESS-VICTORY").choiceScene(
							"Well?", 
							new Branch("Go Spelunking").require(ChoiceCheckType.FREE_COCK).textScene("BEASTMISTRESS-SPELUNKING").encounterEnd(), 
							new Branch("Go Home").textScene("BEASTMISTRESS-DECLINE").encounterEnd()
						),
						new Branch(Outcome.DEFEAT).textScene("BEASTMISTRESS-QUEEN").gameEnd(),
						new Branch(Outcome.SATISFIED).checkScene(
							Stat.AGILITY,
							new Branch(4).textScene("BEASTMISTRESS-DODGE").encounterEnd(),
							new Branch(0).textScene("BEASTMISTRESS-FAIL").encounterEnd()
						)
					)
				).getEncounter();
			case BRIGAND:
				Branch[] battleBranches2 = new Branch[]{new Branch(Outcome.VICTORY).textScene("BRIGAND-VICTORY").encounterEnd(), new Branch(Outcome.DEFEAT).textScene("BRIGAND-DEFEAT").encounterEnd(), new Branch(Outcome.SATISFIED).textScene("BRIGAND-SATISFIED").encounterEnd()};
				Branch acceptCont = new Branch().textScene("BRIGAND-ACCEPT-CONT").choiceScene(
					"Tell her to pull out?",
					new Branch("Say Nothing").textScene("BRIGAND-CATCH").encounterEnd(),
					new Branch("Ask her").textScene("BRIGAND-REQUEST").checkScene(
						Stat.CHARISMA,
						new Branch(4).textScene("BRIGAND-FACIAL").encounterEnd(),
						new Branch(0).textScene("BRIGAND-BADTASTE").encounterEnd()
					)
				);
				Branch brigandSpotted = new Branch(6).textScene("BRIGAND-SPOT").choiceScene(
					"How do you handle the brigand?",
					new Branch("Charge").battleScene(
						BattleCode.BRIGAND, Stance.OFFENSIVE, Stance.BALANCED,
						battleBranches2
					),
					new Branch("Ready an Arrow").battleScene(
						BattleCode.BRIGAND, 
						battleBranches2
					),
					new Branch("Speak").textScene("BRIGAND-HAIL").choiceScene(
						"Accept her offer?",
						new Branch("Accept (Requires: Catamite)").require(ChoiceCheckType.LEWD).checkScene(CheckType.PLUGGED, new Branch(true).textScene("BRIGAND-BUTTPLUG").concat(acceptCont), new Branch(false).concat(acceptCont)),
						new Branch("Decline").textScene("BRIGAND-DECLINE").checkScene(
							Stat.CHARISMA,
							new Branch(5).textScene("BRIGAND-CONVINCE").encounterEnd(),
							new Branch(0).textScene("BRIGAND-FAIL").battleScene(
								BattleCode.BRIGAND, 
								battleBranches2
							)
						)
					)
				);
				return new Branch().textScene("BRIGAND-INTRO").checkScene(
					CheckType.SCOUT_LEVEL_2,
					new Branch(true).concat(brigandSpotted),
					new Branch(false).checkScene(
						Stat.PERCEPTION, 
						brigandSpotted,
						new Branch(4).textScene("BRIGAND-STAB").battleScene(
							BattleCode.BRIGAND, 
							battleBranches2
						),
						new Branch(0).checkScene(
							CheckType.PLUGGED, 
							new Branch(true).textScene("BRIGAND-FOILED-BACKSTAB").battleScene(
								BattleCode.BRIGAND, Stance.FULL_NELSON_BOTTOM, Stance.FULL_NELSON,
								battleBranches2	
							), 
							new Branch(false).textScene("BRIGAND-BACKSTAB").battleScene(
								BattleCode.BRIGAND, Stance.STANDING_BOTTOM, Stance.STANDING,
								battleBranches2	
							)
						)
					)
				).getEncounter();
			case BRIGAND_STORY:
				return new Branch().textScene("STORY-BRIGAND").battleScene(
					BattleCode.BRIGAND_STORY, 
					new Branch(Outcome.VICTORY).textScene("STORY-BRIGAND-VICTORY").choiceScene(
						"What do you want from her?", 
						new Branch("Her companionship").textScene("STORY-BRIGAND-VICTORY-BANG").choiceScene(
							"Cum now?", 
							new Branch("Now").textScene("STORY-BRIGAND-NOW").choiceScene(
								"What do you do?", 
								new Branch("Nothing").textScene("STORY-BRIGAND-BLUEBALLS"), 
								new Branch("Take it").textScene("STORY-BRIGAND-BOTTOM")
							), 
							new Branch("Later").textScene("STORY-BRIGAND-LATER") 
						), 
						new Branch("Her weapon").textScene("STORY-BRIGAND-VICTORY-SWORD")
					), 
					new Branch(Outcome.DEFEAT).textScene("STORY-BRIGAND-DEFEAT").choiceScene("Steel or suck?", new Branch("Steel").textScene("STORY-BRIGAND-DEFEAT-DEATH").gameEnd(), new Branch("Suck").textScene("STORY-BRIGAND-DEFEAT-SUCK")),
					new Branch(Outcome.SATISFIED).textScene("BRIGAND-SATISFIED")
				).getEncounter();
			
			case BROTHEL:
				Branch faceCrushed = new Branch().textScene("BROTHEL-MADAME-FACECRUSHED").gameEnd();
				Branch feetLick = new Branch().textScene("BROTHEL-MADAME-FEETLICK");
				Branch talkToMadame = new Branch("Ask her about herself").checkScene(
					CheckType.MADAME_MET, 
					new Branch(true).textScene("BROTHEL-RETURN"), 
					new Branch(false).textScene("BROTHEL-MADAME").choiceScene(
						"What was so funny?", 
						new Branch("Nothing").textScene("BROTHEL-MERCENARY"), 
						new Branch("Her chair creaked").textScene("BROTHEL-MADAME-UNAMUSED").choiceScene(
							"Why is it funny?", 
							new Branch("I'm sorry").textScene("BROTHEL-MADAME-APOLOGIZE").choiceScene("Offer to kiss her pussy?", new Branch("Yes").textScene("BROTHEL-MADAME-COOCHIE").checkScene(Stat.CHARISMA, new Branch(5).textScene("BROTHEL-MADAME-CUNNILINGUS"), new Branch(0).textScene("BROTHEL-MADAME-NOCOOCHIE").concat(feetLick)), new Branch("No").concat(feetLick)), 
							new Branch("I don't know").textScene("BROTHEL-MADAME-IGNORANCE").choiceScene("Is it because she's put on weight?", new Branch("She does have a fat ass").concat(faceCrushed), new Branch("No").textScene("BROTHEL-MADAME-HEELS")), 
							new Branch("Your ass is fat").concat(faceCrushed)
						)
					)
				);
				
				Branch onceSignedUp = new Branch().textScene("BROTHEL-MEMBER").choiceScene(
						"What do you ask of her?",
						talkToMadame,
						new Branch("Offer your services").choiceScene(
							"What service do you offer?", 
							new Branch("Kissing (1 GP)").textScene("BROTHEL-KISSING"), 
							new Branch("Handjobs (2 GP)").textScene("BROTHEL-HANDJOB").checkScene(Perk.CRANK_MASTER, new Branch(3).textScene("BROTHEL-HANDJOB-MASTER"), new Branch(2).textScene("BROTHEL-HANDJOB-EXPERT"), new Branch(1).textScene("BROTHEL-HANDJOB-NOVICE"), new Branch(0).textScene("BROTHEL-HANDJOB-BEGINNER")), 
							new Branch("Blowjobs (3 GP)").textScene("BROTHEL-ORAL").checkScene(Perk.BLOWJOB_EXPERT, new Branch(3).textScene("BROTHEL-ORAL-MASTER"), new Branch(2).textScene("BROTHEL-ORAL-EXPERT"), new Branch(1).textScene("BROTHEL-ORAL-NOVICE"), new Branch(0).textScene("BROTHEL-ORAL-BEGINNER")), 
							new Branch("Ass (5 GP)").choiceScene(
								"With condoms?", 
								new Branch("Yes (5GP)").textScene("BROTHEL-ANAL-CONDOM").checkScene(Perk.PERFECT_BOTTOM, new Branch(3).textScene("BROTHEL-ANAL-CONDOM-MASTER"), new Branch(2).textScene("BROTHEL-ANAL-CONDOM-EXPERT"), new Branch(1).textScene("BROTHEL-ANAL-CONDOM-NOVICE"), new Branch(0).textScene("BROTHEL-ANAL-CONDOM-BEGINNER")),
								new Branch("Bareback (7GP)").require(ChoiceCheckType.PERK_GREATER_THAN_X, Perk.PERFECT_BOTTOM, 4).textScene("BROTHEL-ANAL-BAREBACK").checkScene(Perk.PERFECT_BOTTOM, new Branch(6).textScene("BROTHEL-ANAL-BAREBACK-MASTER"), new Branch(0).textScene("BROTHEL-ANAL-BAREBACK-EXPERT"))
							), 
							new Branch("Girlfriend Experience").textScene("BROTHEL-GFXP").require(ChoiceCheckType.PERK_GREATER_THAN_X, Perk.PERFECT_BOTTOM, 6),
							new Branch("Never mind"),
						new Branch("Leave")						
					)
				);
				
				return new Branch().textScene("BROTHEL").checkScene(
					CheckType.ELF_BROTHEL,
					new Branch(true).textScene("ELF-BROTHEL"),
					new Branch(false).checkScene(
						CheckType.PROSTITUTE_WARNING_GIVEN,
						new Branch(true).checkScene(	
							Perk.LADY_OF_THE_NIGHT, 
							new Branch(20).textScene("BROTHEL-CLASS-CHANGE").gameEnd(),
							new Branch(0).concat(onceSignedUp)
						),
						new Branch(false).checkScene(
							Perk.LADY_OF_THE_NIGHT,
							new Branch(10).textScene("BROTHEL-WARNING").concat(onceSignedUp),
							new Branch(0).checkScene(
								CheckType.PROSTITUTE, 	
								new Branch(true).concat(onceSignedUp),
								new Branch(false).choiceScene(
									"What do you ask of her?", 
									talkToMadame,
									new Branch("Ask her about joining").textScene("BROTHEL-OFFER").choiceScene(
										"Do you want to sign up? What's the worst that could happen?",
										new Branch ("Sign Up (Requires: Catamite)").require(ChoiceCheckType.LEWD).textScene("BROTHEL-SIGN-UP").concat(onceSignedUp),
										new Branch ("Don't Sign Up")
									),
									new Branch("Leave")
								)
							)	
						)
					)
				).getEncounter();
			case BUNNY:
				String bunnyScene = "BUNNY-SHOW-" + Gdx.app.getPreferences("tales-of-androgyny-preferences").getString("bunny", "CREAM");
				String bunnyAnalScene = "BUNNY-ANAL-" + Gdx.app.getPreferences("tales-of-androgyny-preferences").getString("bunny", "CREAM");
				Branch debtEncounter = new Branch().choiceScene(
					"Pay off your debt?", 
					new Branch("Pay 100").require(ChoiceCheckType.GOLD_GREATER_THAN_X, 100).textScene("BUNNY-PAY-100"), 
					new Branch("Pay 50").require(ChoiceCheckType.GOLD_GREATER_THAN_X, 50).textScene("BUNNY-PAY-50"), 
					new Branch ("Pay 10").require(ChoiceCheckType.GOLD_GREATER_THAN_X, 10).textScene("BUNNY-PAY-10"), 
					new Branch("Can't Pay").require(ChoiceCheckType.GOLD_LESS_THAN_X, 10).checkScene(CheckType.DEBT_WARNING, new Branch(true).textScene("BUNNY-PAY-FAIL").battleScene(BattleCode.BUNNY, new Branch(Outcome.VICTORY).textScene("BUNNY-VICTORY"), new Branch(Outcome.DEFEAT).textScene("BUNNY-DEFEAT").textScene(bunnyAnalScene).textScene("BUNNY-DEFEAT-CONT").gameEnd()), new Branch(false).textScene("BUNNY-PAY-WARNING"))
				);
				return new Branch().textScene("BUNNY-INTRO").checkScene(CheckType.DEBT_FIRST_ENCOUNTER, new Branch(true).textScene("BUNNY-FIRST").textScene(bunnyScene).textScene("BUNNY-SHOW").concat(debtEncounter), new Branch(false).textScene(bunnyScene).textScene("BUNNY-SHOW-REUNION").concat(debtEncounter)).getEncounter();						
			case CAMP_AND_EAT:
				return new Branch().textScene("FORCED-CAMP").encounterEnd().getEncounter();
			case CENTAUR:
				Branch[] centaurBattle = new Branch[]{new Branch(Outcome.VICTORY).textScene("CENTAUR-VICTORY").encounterEnd(), new Branch(Outcome.DEFEAT).textScene("CENTAUR-DEFEAT").gameEnd(), new Branch(Outcome.SATISFIED).textScene("CENTAUR-SATISFIED").encounterEnd()};
				Branch[] unicornBattle = new Branch[]{new Branch(Outcome.VICTORY).textScene("UNICORN-VICTORY").encounterEnd(), new Branch(Outcome.DEFEAT).textScene("UNICORN-DEFEAT")};
				Branch centaurCatamite = new Branch().textScene("CENTAUR-CATAMITE").battleScene(
					BattleCode.CENTAUR, Stance.DOGGY_BOTTOM, Stance.DOGGY,
					centaurBattle
				);
				return new Branch().textScene("CENTAUR-INTRO").checkScene(
					CheckType.VIRGIN, 
					new Branch(true).textScene("UNICORN-ENTRANCE").battleScene(
						BattleCode.UNICORN,
						unicornBattle
					),
					new Branch(false).textScene("CENTAUR-ENTRANCE").checkScene(
						Perk.ANAL_ADDICT,
						new Branch(3).checkScene(CheckType.PLUGGED, new Branch(true).textScene("CENTAUR-BUTTPLUG").concat(centaurCatamite), new Branch(false).concat(centaurCatamite)),
						new Branch(0).choiceScene(
							"Fight the centaur?",
							new Branch("Fight Her").battleScene(
								BattleCode.CENTAUR,
								centaurBattle
							),
							new Branch("Decline").encounterEnd(),
							new Branch("Ask For It").require(ChoiceCheckType.LEWD).checkScene(CheckType.PLUGGED, new Branch(true).textScene("CENTAUR-BUTTPLUG").concat(centaurCatamite), new Branch (false).concat(centaurCatamite))
						)
					)
				).getEncounter();
			case COTTAGE_TRAINER:
				return new Branch().textScene("STORY-003").characterCreation(true).encounterEnd().getEncounter(); 		
			case COTTAGE_TRAINER_VISIT:
				return new Branch().textScene("STORY-004").encounterEnd().getEncounter();
			case QUETZAL:
				Branch quetzalSeconds = new Branch().textScene("QUETZAL-SECONDS").gameEnd();
				Branch quetzalLoss = new Branch().textScene("QUETZAL-LOSS").concat(quetzalSeconds);
				Branch quetzalAttack = new Branch().checkScene(
					CheckType.BLESSING_PURCHASED, 
					new Branch(true).textScene("QUETZAL-ATTACK").battleScene(BattleCode.QUETZAL, new Branch(Outcome.VICTORY).textScene("QUETZAL-VICTORY"), new Branch(Outcome.DEFEAT).concat(quetzalLoss), new Branch(Outcome.DEATH).concat(quetzalLoss), new Branch(Outcome.KNOT_ANAL).concat(quetzalSeconds)), 
					new Branch(false).textScene("QUETZAL-AUTO").concat(quetzalLoss)
				);
				Branch quetzalFirst = new Branch().textScene("QUETZAL-INTRO").choiceScene("Attack?", new Branch("Attack").concat(quetzalAttack), new Branch("Strategic Retreat").textScene("QUETZAL-RETREAT"));
				return new Branch().checkScene(
						CheckType.QUETZAL_SLAIN, 
						new Branch(true).textScene("QUETZAL-SLAIN"), 
						new Branch(false).checkScene(
							CheckType.QUETZAL_MET, 
							new Branch(false).checkScene(
								CheckType.QUETZAL_HEARD, 
								new Branch(true).textScene("QUETZAL-CRIER").concat(quetzalFirst), 
								new Branch(false).textScene("QUETZAL-NO-CRIER").concat(quetzalFirst)
							),
							new Branch(true).textScene("QUETZAL-RETURN").concat(quetzalAttack)
						)
				).getEncounter();
			case DEFAULT:
				return new Branch().textScene("STICK").encounterEnd().getEncounter();
			case DRYAD:
				return new Branch().textScene("DRYAD-INTRO").choiceScene(
					"Do you offer her YOUR apple, or try to convince her to just hand it over?",
					new Branch("Offer (Requires: Catamite)").require(ChoiceCheckType.LEWD).textScene("DRYAD-OFFER").encounterEnd(),
					new Branch("Plead with her").checkScene(
						Stat.CHARISMA,
						new Branch(5).textScene("DRYAD-CONVINCE").encounterEnd(),
						new Branch(0).textScene("DRYAD-FAIL").encounterEnd()
					)
			    ).getEncounter();
			case ECCENTRIC_MERCHANT:
				return new Branch().textScene("STORY-MERCHANT").encounterEnd().getEncounter();
			case ELF:
				Branch careerOptions = new Branch().textScene("ELF-CAREER").choiceScene(
					"What do you say?", 
					new Branch("Try the brothel. (CHA: 7)").require(ChoiceCheckType.STAT_GREATER_THAN_X, Stat.CHARISMA, 7).textScene("ELF-BROTHEL-SUGGEST"),
					new Branch("Join me. (CHA: 5)").require(ChoiceCheckType.STAT_GREATER_THAN_X, Stat.CHARISMA, 5).textScene("ELF-JOIN-SUGGEST"),
					new Branch("Become a healer. (CHA: 3)").require(ChoiceCheckType.STAT_GREATER_THAN_X, Stat.CHARISMA, 3).textScene("ELF-HEALER-SUGGEST"),
					new Branch("Go home.").textScene("ELF-LEAVE-SUGGEST")
				);
				return new Branch().textScene("ELF-INTRO").checkScene(
					CheckType.ELF_UNLOCKED, 
					new Branch(true).textScene("ELF-ENTER").choiceScene(
						"Do you accept the breakfast invitation?", 
						new Branch("Accept").textScene("ELF-ACCEPT"), 
						new Branch("Decline").textScene("ELF-DECLINE")
					),
					new Branch(false).checkScene(
						CheckType.ELF_DECLINED, 
						new Branch(true).textScene("ELF-RETRY").choiceScene(
							"Do you accept the breakfast invitation?", 
							new Branch("Accept").textScene("ELF-ACCEPT"), 
							new Branch("Decline").textScene("ELF-DECLINE")
						),
						new Branch(false).checkScene(
							CheckType.ELF_ACCEPTED, 
							new Branch(true).textScene("ELF-REUNION").choiceScene(
								"Kiss Kylira?",
								new Branch("Kiss(Requires: Free cock)").require(ChoiceCheckType.FREE_COCK).textScene("ELF-TOP").concat(careerOptions),
								new Branch("Be Kissed").textScene("ELF-BOTTOM").concat(careerOptions),
								new Branch("Deny").textScene("ELF-DENY").concat(careerOptions)
							), 
							new Branch(false).checkScene(
								CheckType.ELF_HEALER,
								new Branch(true).textScene("ELF-HEALER"),
								new Branch(false).textScene("STICK")
							)
						)
					)
				).getEncounter();
			case ELF_COMPANION:
				return new Branch().checkScene(CheckType.ELF_COMPANION1, 
					new Branch(true).textScene("ELF-COMPANION-FIRST").choiceScene("Spend time with Kylira?", new Branch("Spend time").textScene("ELF-COMPANION-HANGOUT"), new Branch("Not now")), 
					new Branch(false).checkScene(
						CheckType.ELF_COMPANION2, 
						new Branch(true).textScene("ELF-COMPANION-SECOND").choiceScene("Learn healing magic?", new Branch("Learn").textScene("ELF-COMPANION-LEARN"), new Branch("Not now")),
						new Branch(false).textScene("ELF-COMPANION-REPEAT")
					)
				).getEncounter();
			case ERROR:
				break;
			case FIRST_BATTLE_STORY:
				return new Branch().textScene("STORY-FIGHT-FIRST").battleScene(
					BattleCode.GOBLIN_STORY,
					new Branch(Outcome.VICTORY).textScene("STORY-FIGHT-GOBLIN-VICTORY").encounterEnd(),
					new Branch(Outcome.DEFEAT).textScene("STORY-FIGHT-GOBLIN-DEFEAT").gameEnd()
				).getEncounter();
			case FORAGE: 			
				return new Branch().textScene("FORAGE-INTRO").checkScene(
					CheckType.DAY,
					new Branch(true).checkScene(
						// can use this lucky check to divide up into a binary mask for encounter structure (battle, perception check, charisma check), then split the ends up into random text / random battles
						CheckType.LUCKY, 
						new Branch(true).checkScene(
							CheckType.LUCKY, 
							new Branch(true).checkScene(
								CheckType.LUCKY, 
								new Branch(true).textScene("FORAGE-0"), // 3
								new Branch(false).textScene("FORAGE-1") // 1
							),
							new Branch(false).checkScene(
								CheckType.LUCKY, 
								new Branch(true).textScene("FORAGE-2"), // 1
								new Branch(false).textScene("FORAGE-4") // -1
							)
						),
						new Branch(false).checkScene(
							CheckType.LUCKY, 
							new Branch(true).checkScene(
								CheckType.LUCKY, 
								new Branch(true).textScene("FORAGE-3"), // 1
								new Branch(false).textScene("FORAGE-5") // -1
							),
							new Branch(false).checkScene(
								CheckType.LUCKY, 
								new Branch(true).textScene("FORAGE-6"), // -1
								new Branch(false).textScene("FORAGE-7") // -3
							)
						)
					),
					new Branch(false).textScene("FORAGE-NIGHT").checkScene(
						CheckType.LUCKY, 
						new Branch(true).checkScene(
							CheckType.LUCKY, 
							new Branch(true).checkScene(
								CheckType.LUCKY, 
								new Branch(true).textScene("FORAGE-NIGHT-GOOD").textScene("FORAGE-1"), // 3
								new Branch(false).textScene("FORAGE-4") // 1
							),
							new Branch(false).checkScene(
								CheckType.LUCKY, 
								new Branch(true).textScene("FORAGE-4"), // 1
								new Branch(false).textScene("FORAGE-5") // -1
							)
						),
						new Branch(false).checkScene(
							CheckType.LUCKY, 
							new Branch(true).checkScene(
								CheckType.LUCKY, 
								new Branch(true).textScene("FORAGE-4"), // 1
								new Branch(false).textScene("FORAGE-6") // -1
							),
							new Branch(false).checkScene(
								CheckType.LUCKY, 
								new Branch(true).textScene("FORAGE-6"), // -1
								new Branch(false).textScene("FORAGE-7") // -3
							)
						)
					)
				).getEncounter();
			case FORT:
				break;
			case GADGETEER:
				Branch no = new Branch("No thanks").textScene("GADGETEER-NO").encounterEnd();
				Branch yes = new Branch().textScene("GADGETEER-SLAVE").gameEnd();
				Branch[] yesyesyes = new Branch[]{new Branch("yes").concat(yes), new Branch("yeS").concat(yes), new Branch("YES").concat(yes)};
				Branch analLoverCheck = new Branch().checkScene(
					Perk.ANAL_ADDICT,
					new Branch(3).textScene("GADGETEER-PEGGED").choiceScene("Become hers?", yesyesyes),
					new Branch(2).textScene("GADGETEER-PLUGS"),
					new Branch(1).textScene("GADGETEER-HESITANT").choiceScene(
						"Try the toys?", 
						new Branch("Yes").textScene("GADGETEER-BALLS").encounterEnd(), 
						no
					),
					new Branch(0).textScene("GADGETEER-CONFUSED").choiceScene(
						"Try the toys?", 
						new Branch("Yes (Requires: Catamite)").require(ChoiceCheckType.LEWD).textScene("GADGETEER-BREAKINGIN").encounterEnd(),
						no
					)
				);
				
				Branch shop = new Branch().choiceScene(
					"Do you want to peruse her wares?", 
					new Branch("Peruse").shopScene(ShopCode.GADGETEER_SHOP).checkScene(
						CheckType.CHASTITIED, 
						new Branch(true).textScene("GADGETEER-CHASTITIED").checkScene(
							CheckType.PALADIN, 
							new Branch(true).textScene("GADGETEER-PALADIN").checkScene(
								Perk.ANAL_ADDICT, 
								new Branch(3).textScene("GADGETEER-SLUT-PALADIN").gameEnd(),
								new Branch(0).textScene("GADGETEER-PURE-PALADIN")
							), 
							new Branch(false).checkScene(CheckType.GADGETEER_TESTED, new Branch(true).checkScene(Perk.ANAL_ADDICT, new Branch(3).textScene("GADGETEER-HONEST").gameEnd(), new Branch(0).textScene("GADGETEER-LIE")), new Branch(false).textScene("GADGETEER-CAGE"))
						), 
						new Branch(false).checkScene(
							CheckType.PLUGGED, 
							new Branch(true).textScene("GADGETEER-PLUGGED").checkScene(
								Perk.ANAL_ADDICT,
								new Branch(3).textScene("GADGETEER-PEGGED").choiceScene("Become hers?", yesyesyes),
								new Branch(0).textScene("GADGETEER-PLUGS")
							),
							new Branch(false).checkScene(
								CheckType.GADGETEER_MET, 
								new Branch(true).checkScene(
									CheckType.GADGETEER_TESTED, 
									new Branch(true).textScene("GADGETEER-GIVECAGE"), 
									new Branch(false).textScene("GADGETEER-TEASE").concat(analLoverCheck)),
								new Branch(false).textScene("GADGETEER-POSTSHOP").concat(analLoverCheck)
							)
						)
					),
					no
				);
				return new Branch().textScene("GADGETEER-INTRO").checkScene(CheckType.GADGETEER_MET, new Branch(true).textScene("GADGETEER-REUNION").concat(shop), new Branch(false).textScene("GADGETEER-FIRST-VISIT").concat(shop)).getEncounter();
			case GHOST:
				String spookyGhostScene = Gdx.app.getPreferences("tales-of-androgyny-preferences").getBoolean("blood", true) ? "GHOST-BLOODY" : "GHOST-BLOODLESS";
				Branch ghostPossession = new Branch().textScene("GHOST-POSSESSION").gameEnd();
				Branch ghostBattle = new Branch().battleScene(BattleCode.GHOST, new Branch(Outcome.VICTORY).textScene("GHOST-VICTORY"), new Branch(Outcome.DEFEAT).concat(ghostPossession));
				Branch refuse = new Branch("Refuse").checkScene(Stat.MAGIC, new Branch(2).concat(ghostBattle), new Branch(0).concat(ghostPossession));
				Branch didEnjoy = new Branch().choiceScene("\"Did you enjoy it as much as I did?\", she asks.", new Branch("Yes").textScene("GHOST-DAY-HAPPY"), new Branch("No").textScene("GHOST-DAY-SAD").concat(ghostBattle));
				
				return new Branch().checkScene(
					CheckType.DAY, 
					new Branch(true).textScene("GHOST-DAY").choiceScene(
						"Do you follow?", 
						new Branch("Follow her").textScene("GHOST-DAY-FOLLOW").choiceScene(
							"Receive her \"affection\"?",
							new Branch("Receive it").textScene("GHOST-DAY-RECEIVE").concat(didEnjoy),
							new Branch("Give her your love").textScene("GHOST-DAY-GIVE"),
							new Branch("Reject her").textScene("GHOST-DAY-REJECT").concat(ghostBattle)
						), 
						new Branch("Ignore her")
					), 
					new Branch(false).textScene("GHOST-NIGHT").choiceScene(
						"Do you follow?", 
						new Branch("Follow her").textScene("GHOST-NIGHT-FOLLOW").textScene(spookyGhostScene).textScene("GHOST-NIGHT-CONT").choiceScene(
							"What do you do?", 
							new Branch("Apologize (CHA: 5)").require(ChoiceCheckType.STAT_GREATER_THAN_X, Stat.CHARISMA, 5).textScene("GHOST-NIGHT-APOLOGY").choiceScene(
								"Show her love?", 
								new Branch("Show her love").textScene("GHOST-BLOWJOB-WOO-WOO").choiceScene(
									"Well?", 
									new Branch("Point your ass at her").textScene("GHOST-BLASTING").concat(didEnjoy),
									refuse
								),
								refuse
							),
							refuse							
						), 
						new Branch("Ignore her").textScene("GHOST-NIGHT-IGNORE").choiceScene("Camp or Leave?", new Branch("Camp").textScene("GHOST-NIGHT-CAMP").textScene(spookyGhostScene).textScene("GHOST-NIGHT-CAMP-CONT"), new Branch("Leave").textScene("GHOST-NIGHT-LOST"))
					)
				).getEncounter();				
			case GOBLIN:
				Branch analCont = new Branch().textScene("GOBLIN-ANAL-CONT").checkScene(
					Stat.ENDURANCE, 
					new Branch(3).textScene("GOBLIN-FIGHTOFF").encounterEnd(),
					new Branch(0).textScene("GOBLIN-SECONDS").checkScene(
						Stat.ENDURANCE,
						new Branch(2).textScene("GOBLIN-FIGHTOFF").encounterEnd(),
						new Branch(0).textScene("GOBLIN-THIRDS").checkScene(
							Stat.ENDURANCE, 
							new Branch(1).textScene("GOBLIN-FIGHTOFF").encounterEnd(),
							new Branch(0).textScene("GOBLIN-FOURTHS").encounterEnd()
						)
					)
				);
				
				Branch postVirginityCheck = new Branch().choiceScene(
					"Mouth, or ass?",
					new Branch("In the Mouth").textScene("GOBLIN-MOUTH").encounterEnd(),
					new Branch("Up The Ass").textScene("GOBLIN-ANAL").checkScene(CheckType.PLUGGED, new Branch(true).textScene("GOBLIN-BUTTPLUG").concat(analCont), new Branch(false).concat(analCont))
				);
				Branch[] battleScenes = new Branch[]{
					new Branch(Outcome.VICTORY).textScene("GOBLIN-VICTORY").encounterEnd(), 
					new Branch(Outcome.DEFEAT).textScene("GOBLIN-DEFEAT").checkScene(
						CheckType.GOBLIN_VIRGIN,
						new Branch(true).textScene("GOBLIN-VIRGIN").concat(postVirginityCheck),
						new Branch(false).textScene("GOBLIN-EXPERT").concat(postVirginityCheck)
					)
				};
				Branch pantsCutDown = new Branch(0).textScene("GOBLIN-PANTS-DOWN").battleScene(BattleCode.GOBLIN, Stance.DOGGY_BOTTOM, Stance.DOGGY, battleScenes);
				
				Branch cutPants = new Branch().textScene("GOBLIN-POST-SPEAR").choiceScene(
					"Quick, what do you do?",
					new Branch("Catch Her (5 AGI)").require(ChoiceCheckType.STAT_GREATER_THAN_X, Stat.AGILITY, 5).textScene("GOBLIN-CATCH").choiceScene(
						"What do you do with her?",
						new Branch("Put Her Down").textScene("GOBLIN-RELEASE").choiceScene(
							"Accept Her Offer?",
							new Branch("Accept").textScene("GOBLIN-ACCEPT").encounterEnd(),
							new Branch("Decline").textScene("GOBLIN-DECLINE").encounterEnd()
						),
						new Branch("Turn Her Over Your Knee").textScene("GOBLIN-FLIP").checkScene(
							Stat.STRENGTH,
							new Branch(5).textScene("GOBLIN-SPANK").encounterEnd(),
							new Branch(0).textScene("GOBLIN-GETBIT").encounterEnd()
						)
					),
					new Branch("Trip Her (4 AGI)").require(ChoiceCheckType.STAT_GREATER_THAN_X, Stat.AGILITY, 4).textScene("GOBLIN-TRIP").choiceScene(
						"What do you do?",
						new Branch("Attack").battleScene(
							BattleCode.GOBLIN, Stance.OFFENSIVE, Stance.PRONE,
							battleScenes
						),
						new Branch("Run").textScene("GOBLIN-FLEE").encounterEnd()
					),
					new Branch("Disarm Her (3 AGI)").require(ChoiceCheckType.STAT_GREATER_THAN_X, Stat.AGILITY, 3).textScene("GOBLIN-DISARM").choiceScene(
						"What do you do?",
						new Branch("Attack Her").battleScene(
							BattleCode.GOBLIN, Stance.OFFENSIVE, Stance.BALANCED, true,
							battleScenes
						),
						new Branch("Block Her").battleScene(
							BattleCode.GOBLIN, Stance.BALANCED, Stance.BALANCED, true,
							battleScenes
						),
						new Branch("Let Her Go").encounterEnd()
					),
					new Branch("Avoid Her (2 AGI)").require(ChoiceCheckType.STAT_GREATER_THAN_X, Stat.AGILITY, 2).textScene("GOBLIN-DODGE").choiceScene(
						"What do you do?",
						new Branch("Attack Her").battleScene(
							BattleCode.GOBLIN, Stance.OFFENSIVE, Stance.BALANCED,
							battleScenes
						),
						new Branch("Block Her").battleScene(
							BattleCode.GOBLIN, Stance.BALANCED, Stance.BALANCED,
							battleScenes
						),
						new Branch("Let Her Go").encounterEnd()
					),
					new Branch("Nothing").concat(pantsCutDown)
				);
				Branch maleDefeatCont = new Branch().textScene("GOBLIN-MALE-DEFEAT").gameEnd();
				Branch[] goblinStrength = new Branch[]{new Branch(5).textScene("GOBLIN-SPEAR-STEAL").concat(cutPants),
						new Branch(0).textScene("GOBLIN-SPEAR-DROP").concat(cutPants)};
				Branch[] goblinSpear = new Branch[]{
					new Branch(7).textScene("GOBLIN-SPEAR-GRAB").checkScene(
						Stat.STRENGTH, 
						goblinStrength
					),
					new Branch(5).textScene("GOBLIN-SPEAR-DODGE").concat(cutPants), 
					new Branch(0).textScene("GOBLIN-SPEAR-STABBED").concat(cutPants)
				};
				
				return new Branch().checkScene(
					CheckType.GOBLIN_KNOWN,
					new Branch(true).textScene("GOBLIN-INTRO").choiceScene(
						"What path do you follow?",
						new Branch("Pass By").textScene("GOBLIN-PASSBY").encounterEnd(),
						new Branch("Enter the Small Path").textScene("GOBLIN-ENTRANCE").checkScene(
							Stat.PERCEPTION, 
							new Branch(7).textScene("GOBLIN-EAGLE-EYE").checkScene(
								Stat.AGILITY,
								new Branch(5).textScene("GOBLIN-SPEAR-GRAB").checkScene(
									Stat.STRENGTH,
									goblinStrength
								),
								new Branch(3).textScene("GOBLIN-SPEAR-DODGE").concat(cutPants),
								new Branch(0).textScene("GOBLIN-SPEAR-STABBED").concat(cutPants)
							),
							new Branch(4).textScene("GOBLIN-CONTRAPTION").checkScene(
								Stat.AGILITY,
								goblinSpear
							),
							new Branch(0).textScene("GOBLIN-AMBUSH").checkScene(
								Stat.AGILITY, 
								new Branch(5).textScene("GOBLIN-LOG-DODGE").checkScene(
									Stat.AGILITY, goblinSpear
								),
								new Branch(0).checkScene(
									Stat.ENDURANCE,
									new Branch(7).textScene("GOBLIN-OBLIVIOUS").encounterEnd(),
									new Branch(0).textScene("GOBLIN-TOTALFAIL").encounterEnd()
								)
							)
						)
					),	
					new Branch(false).textScene("GOBLIN-SECOND-QUEST").choiceScene(
						"Which path do you follow?", 
						new Branch("Familiar warning sign").textScene("GOBLIN-REUNION").encounterEnd(), 
						new Branch("Other way").textScene("GOBLIN-MALE-INTRO").battleScene(
							BattleCode.GOBLIN_MALE,
							new Branch(Outcome.VICTORY).textScene("GOBLIN-MALE-VICTORY").encounterEnd(),
							new Branch(Outcome.DEFEAT).checkScene(CheckType.PLUGGED, new Branch(true).textScene("GOBLIN-MALE-BUTTPLUG").concat(maleDefeatCont), new Branch(false).concat(maleDefeatCont))
						)
					)
				).getEncounter();
			case GOLEM:
				Branch golemHypnosis = new Branch().textScene("GOLEM-HYPNOSIS");
				Branch golemMisunderstanding = new Branch(0).textScene("GOLEM-MISUNDERSTANDING").choiceScene(
						"Where will she dump her semen tanks?",
						new Branch("In your asshole").concat(golemHypnosis),
						new Branch("Into your rectum").concat(golemHypnosis),
						new Branch("Up your shitter").concat(golemHypnosis),
						new Branch("Your gut (anally)").concat(golemHypnosis),
						new Branch("Buttsex, creampie").concat(golemHypnosis),
						new Branch("Cummies for tummies").concat(golemHypnosis)
					);
				Branch[] golemBattleOutcomes = new Branch[]{
					new Branch(Outcome.VICTORY).textScene("GOLEM-VICTORY").choiceScene(
						"What do you do?", 
						new Branch("Ask for help (CHA 5)").require(ChoiceCheckType.STAT_GREATER_THAN_X, Stat.CHARISMA, 5).textScene("GOLEM-GIFT"),
						new Branch("Ask for... help (Requires: Catamite)").require(ChoiceCheckType.LEWD).textScene("GOLEM-TOP"),
						new Branch("Bid her farewell").textScene("GOLEM-FREE")
					), 
					new Branch(Outcome.DEFEAT).textScene("GOLEM-DEFEAT").concat(golemMisunderstanding)
				};
				Branch golemBattle = new Branch().battleScene(
					BattleCode.GOLEM, 
					golemBattleOutcomes
				);
				Branch[] calmOptions = new Branch[]{
					new Branch("Ask for help (CHA 5)").require(ChoiceCheckType.STAT_GREATER_THAN_X, Stat.CHARISMA, 5).textScene("GOLEM-GIFT"),
					new Branch("Ask about her").textScene("GOLEM-SPEAK").choiceScene(
						"What do you ask of her?",
						new Branch("Ask for help (CHA 5)").require(ChoiceCheckType.STAT_GREATER_THAN_X, Stat.CHARISMA, 5).textScene("GOLEM-GIFT"),
						new Branch("Ask for... help (Requires: Catamite)").require(ChoiceCheckType.LEWD).textScene("GOLEM-TOP"),
						new Branch("Ask to fight her").concat(golemBattle),
						new Branch("Bid her farewell").textScene("GOLEM-FREE")
					),
					new Branch("Ask to fight her").concat(golemBattle),
					new Branch("Bid her farewell").textScene("GOLEM-FREE")
				};
				
				return new Branch().textScene("GOLEM-INTRO").checkScene(
					Stat.MAGIC,
					new Branch(1).textScene("GOLEM-AWARE").choiceScene(
						"Touch the statue?", 
						new Branch("Touch the statue").textScene("GOLEM-AWAKEN").choiceScene( 
							"Her energies are in flux!", 
							new Branch("Dominate Her (5 MAG)").require(ChoiceCheckType.STAT_GREATER_THAN_X, Stat.MAGIC, 5).textScene("GOLEM-DOMINATED").choiceScene(
								"What do you will of her?", 
								new Branch("Demand tribute").textScene("GOLEM-TRIBUTE"),
								new Branch("Demand pleasure").checkScene(Perk.ANAL_ADDICT, new Branch(2).textScene("GOLEM-TOP"), golemMisunderstanding),
								new Branch("Ask to fight her").concat(golemBattle),
								new Branch("Tell her to shut down").textScene("GOLEM-SHUTDOWN")
							),
							new Branch("Soothe Her (5 MAG)").require(ChoiceCheckType.STAT_GREATER_THAN_X, Stat.MAGIC, 5).textScene("GOLEM-SOOTHED").choiceScene("What do you do?", calmOptions),
							new Branch("Calm Her (3 MAG)").require(ChoiceCheckType.STAT_GREATER_THAN_X, Stat.MAGIC, 3).textScene("GOLEM-CALMED").checkScene(
								Stat.PERCEPTION, 
								new Branch(4).choiceScene("What do you do?", calmOptions), 
								golemMisunderstanding
							),
							new Branch("Brace Yourself").textScene("GOLEM-OVERDRIVE").battleScene(
								BattleCode.GOLEM, Stance.BALANCED, Stance.CASTING,
								golemBattleOutcomes
							)
						),
						new Branch("Pull your hand away").textScene("GOLEM-LEAVE")
					),
					new Branch(0).textScene("GOLEM-UNAWARE")				
				).getEncounter();			
			case HARPY:
				Branch harpyMarriage = new Branch().textScene("HARPY-MARRIAGE").gameEnd();
				Branch[] battleBranches = new Branch[]{
					new Branch(Outcome.VICTORY).textScene("HARPY-VICTORY").encounterEnd(), 
					new Branch(Outcome.DEFEAT).checkScene(
						CheckType.PLUGGED, 
						new Branch(true).textScene("HARPY-PLUGGED"),
						new Branch(false).textScene("HARPY-DEFEAT").checkScene(
							Perk.CUCKOO_FOR_CUCKOO, 
							new Branch(3).textScene("HARPY-LOVE-BIRD").concat(harpyMarriage), 
							new Branch(0).checkScene(Perk.ANAL_ADDICT, new Branch(3).textScene("HARPY-LOVE-ANAL").concat(harpyMarriage), new Branch(0).textScene("HARPY-FINISH"))
						)), 
					new Branch(Outcome.SATISFIED).textScene("HARPY-SATISFIED").encounterEnd()
				};
				Branch harpyDodge = new Branch(6).textScene("HARPY-DODGE").battleScene(
					BattleCode.HARPY, Stance.BALANCED, Stance.PRONE,
					battleBranches
				);
				return new Branch().textScene("HARPY-INTRO").checkScene(
					CheckType.SCOUT_LEVEL_2, 
					new Branch(true).concat(harpyDodge),
					new Branch(false).checkScene(
						Stat.AGILITY,
						harpyDodge,
						new Branch(4).textScene("HARPY-DUCK").battleScene(
							BattleCode.HARPY, Stance.KNEELING, Stance.BALANCED,
							battleBranches
						),
						new Branch(0).textScene("HARPY-HORK").battleScene(
							BattleCode.HARPY, Stance.FELLATIO_BOTTOM, Stance.FELLATIO,
							battleBranches
						) 
					)
			    ).getEncounter();
			case HARPY_STORY:
				return new Branch().textScene("STORY-HARPY").battleScene(
					BattleCode.HARPY_STORY,
					new Branch(Outcome.VICTORY).textScene("STORY-HARPY-VICTORY").choiceScene("Stuff the bird?", new Branch("Flip the bird").textScene("STORY-HARPY-VICTORY-BUTTSEX"), new Branch("Walk away")), 
					new Branch(Outcome.DEFEAT).textScene("STORY-HARPY-DEFEAT").gameEnd(), 
					new Branch(Outcome.KNOT_ANAL).textScene("STORY-HARPY-ANAL"), 
					new Branch(Outcome.KNOT_ORAL).textScene("STORY-HARPY-ORAL")
				).getEncounter();
			case INITIAL:
				return new Branch().textScene("INTRO").gameTypeScene(
					new Branch("Create Character").textScene("CHARACTER-CREATE").characterCreation(false).skillSelection().characterCustomization().encounterEnd(),
					new Branch("Story (Patrons)").textScene("STORY-MODE").encounterEnd()
				).getEncounter(); 	
			case INN:
				Branch afterScene = new Branch().textScene("INNKEEP-10").encounterEnd();  
				Branch leave = new Branch("Leave").encounterEnd();
				return new Branch().textScene("INNKEEP-01").choiceScene(
					"Stay the night?",
					new Branch("Rest at Inn (10 Gold)").require(ChoiceCheckType.GOLD_GREATER_THAN_X, 10).textScene("INNKEEP-02").encounterEnd(),
					new Branch("Rest at Inn (Low Funds)").require(ChoiceCheckType.GOLD_LESS_THAN_X, 10).checkScene(
						CheckType.INN_0,
						new Branch(true).textScene("INNKEEP-03").choiceScene(
							"Take his offer?",
							new Branch("Get under the table").textScene("INNKEEP-04").encounterEnd(),
							leave
						),
						new Branch(false).checkScene(
							CheckType.INN_1,
							new Branch(true).textScene("INNKEEP-07").choiceScene(
								"Take his offer?",
								new Branch("Go to his room").textScene("INNKEEP-08").checkScene(
									CheckType.VIRGIN, 
									new Branch(true).textScene("INNKEEP-09").concat(afterScene), 
									new Branch(false).concat(afterScene)
								),
								leave
							),
							new Branch(false).checkScene(
								CheckType.INN_2,
								new Branch(true).textScene("INNKEEP-12").choiceScene(
									"Take his offer?",
									new Branch("Join him").textScene("INNKEEP-13").encounterEnd(),
									leave
								), 
								new Branch(false).textScene("INNKEEP-16").choiceScene(
									"Take his offer?",
									new Branch("Marry him").textScene("INNKEEP-17").gameEnd(),
									leave
								)
							)
						)
					),
					leave
			    ).getEncounter();
			case LEVEL_UP:
				if (mode == GameMode.STORY) {
					return new Branch().textScene("NO-SKILLS").encounterEnd().getEncounter(); 	
				}
				else {
					return new Branch().skillSelection().encounterEnd().getEncounter(); 	
				}
			case MERI_COTTAGE:
				return new Branch().textScene("STORY-WITCH-COTTAGE").encounterEnd().getEncounter(); 	
			case MERI_COTTAGE_VISIT:
				return new Branch().textScene("STORY-WITCH-COTTAGE-VISIT").encounterEnd().getEncounter(); 
			case NAGA:
				return new Branch().textScene("NAGA-INTRO").checkScene(
					CheckType.SCOUT_LEVEL_3, 
					new Branch(true).textScene("NAGA-SPOTTED").battleScene(
						BattleCode.NAGA, 
						new Branch(Outcome.VICTORY).textScene("NAGA-VICTORY").choiceScene(
							"What do you do with her?", 
							new Branch("In her... cloaca").textScene("NAGA-CLOACA"), 
							new Branch("In her mouth").textScene("NAGA-FELLATIO").gameEnd(), 
							new Branch("Nothing")
						), 
						new Branch(Outcome.DEFEAT).textScene("NAGA-DEFEAT").gameEnd(), 
						new Branch(Outcome.DEATH).textScene("NAGA-CRUSHED").gameEnd()
					), 
					new Branch(false).textScene("NAGA-AMBUSH").choiceScene(
						"Front or back?", 
						new Branch("Front").textScene("NAGA-IRRUMATIO"), 
						new Branch("Back").textScene("NAGA-CLOACALICK")
					)
				).getEncounter();
			case OGRE:
				Branch passerby = new Branch().textScene("OGRE-PASSERBY").encounterEnd();
				Branch partingScene = new Branch().checkScene(
					Perk.SIZE_QUEEN,
					new Branch(3).textScene("OGRE-MARRY").gameEnd(), 
					new Branch(2).textScene("OGRE-HARDSELL").concat(passerby),  
					new Branch(1).textScene("OGRE-FLIRT").concat(passerby), 
					new Branch(0).concat(passerby)
				);
				Branch ogreFirstBattle = new Branch().battleScene(
					BattleCode.OGRE, 
					new Branch(Outcome.VICTORY).textScene("OGRE-VICTORY").concat(new Branch().textScene("OGRE-VICTORY-GOLD").encounterEnd()),
					new Branch(Outcome.SATISFIED).textScene("OGRE-SATISFIED").concat(partingScene),
					new Branch(Outcome.DEFEAT).textScene("OGRE-DEFEAT").concat(partingScene)
				);
				Branch ogreFirstBattleDisarm = new Branch().battleScene(BattleCode.OGRE, Stance.BALANCED, Stance.BALANCED, true, new Branch(Outcome.VICTORY).textScene("OGRE-VICTORY").concat(new Branch().textScene("OGRE-VICTORY-GOLD").encounterEnd()), new Branch(Outcome.DEFEAT).textScene("OGRE-DEFEAT").concat(partingScene), new Branch(Outcome.SATISFIED).textScene("OGRE-SATISFIED").concat(partingScene));
				Branch ogreSecondBattle = new Branch().battleScene(BattleCode.OGRE, new Branch(Outcome.VICTORY).textScene("OGRE-VICTORY").encounterEnd(), new Branch(Outcome.DEFEAT).textScene("OGRE-DEFEAT").concat(partingScene), new Branch(Outcome.SATISFIED).textScene("OGRE-SATISFIED").concat(partingScene));
				Branch grabbedByOgre = new Branch().textScene("OGRE-GRABBED").checkScene(
					Stat.ENDURANCE,
					new Branch(4).textScene("OGRE-ENDURE").encounterEnd(), 
					new Branch(0).gameEnd()
				);
				
				return new Branch().textScene("OGRE-INTRO").checkScene(
					CheckType.OGRE_DONE,
					new Branch(true).textScene("OGRE-ENTRANCE").checkScene(
						Stat.PERCEPTION, 
						new Branch(3).textScene("OGRE-SPOTTED").choiceScene(
							"Do you attempt to steal from the ogre or ambush him?",
							new Branch("Steal").textScene("OGRE-STEALTH").checkScene(
								Stat.AGILITY,
								new Branch(7).textScene("OGRE-STEAL").encounterEnd(),
								new Branch(5).textScene("OGRE-WAKE").concat(ogreFirstBattle),
								new Branch(0).concat(grabbedByOgre)
							), 
							new Branch("Ambush").textScene("OGRE-STEALTH").checkScene(
								Stat.AGILITY, 
								new Branch(5).choiceScene(
									"Pre-emptive ranged attack or kick away his club?",
									new Branch("Ranged Attack").concat(ogreFirstBattle),
									new Branch("Kick Away Club").concat(ogreFirstBattleDisarm)
								),
								new Branch(0).textScene("OGRE-WAKE2").concat(ogreFirstBattle)
							),
							new Branch("Leave").encounterEnd()
						),
						new Branch(0).textScene("OGRE-SURPRISE").concat(grabbedByOgre)
					),
					new Branch(false).textScene("OGRE-BATTLE").concat(ogreSecondBattle)
					
			    ).getEncounter();			
			case OGRE_STORY:
				return new Branch().textScene("STORY-OGRE").battleScene(
					BattleCode.OGRE_STORY,
					new Branch(Outcome.VICTORY).textScene("STORY-OGRE-VICTORY"), 
					new Branch(Outcome.DEFEAT).textScene("STORY-OGRE-DEFEAT").gameEnd(),
					new Branch(Outcome.SATISFIED).textScene("STORY-OGRE-DEFEAT").gameEnd()
				).getEncounter();
			case OGRE_WARNING_STORY:
				return new Branch().textScene("OGRE-WARN").encounterEnd().getEncounter();
			case ORC:
				Branch leaveOrc = new Branch().textScene("ORC-LEAVE").encounterEnd();
				Branch oralScene = new Branch().textScene("ORC-ORAL").encounterEnd();
				Branch failedCharisma = new Branch(0).textScene("ORC-OFFER-FAIL").concat(oralScene);
				Branch orcAnal = new Branch().textScene("ORC-ANAL").checkScene(CheckType.PLUGGED, new Branch(true).textScene("ORC-ANAL-PLUGGED").textScene("ORC-ANAL-CONTINUE"), new Branch(false).textScene("ORC-ANAL-CONTINUE"));
				Branch battleVictory = new Branch().textScene("ORC-VICTORY").choiceScene(
					"Front, back, or decline?", 
					new Branch("Front (Requires: Catamite)").require(ChoiceCheckType.LEWD).concat(orcAnal),
					new Branch("Back (Requires: Free cock)").require(ChoiceCheckType.FREE_COCK).textScene("ORC-BOTTOM").encounterEnd(),
					new Branch("Decline").textScene("ORC-DECLINE").encounterEnd()
				);
				
				return new Branch().textScene("ORC-INTRO").checkScene(
					CheckType.ORC_ENCOUNTERED,
					new Branch(true).textScene("ORC-OLDMEN").choiceScene(
						"Do you speak up?",
						new Branch("Speak up").textScene("ORC-VIEW").choiceScene( 
							"How do you respond?", 
							new Branch("Attack").battleScene(
								BattleCode.ORC, 4,
								new Branch(Outcome.VICTORY).textScene("ORC-VICTORY1").concat(battleVictory),
								new Branch(Outcome.DEFEAT).textScene("ORC-DEFEAT").choiceScene(
									"What do you offer?",
									new Branch("Anal (Requires: Catamite)").require(ChoiceCheckType.LEWD).concat(orcAnal),
									new Branch("Oral").textScene("ORC-OFFER-ORAL").concat(oralScene), 
									new Branch("Nasal").textScene("ORC-NASAL").encounterEnd(),
									new Branch("Penal (6 CHA)").require(ChoiceCheckType.STAT_GREATER_THAN_X, Stat.CHARISMA, 6).textScene("ORC-PENAL").encounterEnd(),
									new Branch("Facial (4 CHA)").require(ChoiceCheckType.STAT_GREATER_THAN_X, Stat.CHARISMA, 4).textScene("ORC-FACIAL").encounterEnd(),
									new Branch("Nothing").concat(failedCharisma)
								),
								new Branch(Outcome.SATISFIED).textScene("ORC-SATISFIED").encounterEnd()
							),
							new Branch("Remain still").textScene("ORC-STILL").concat(leaveOrc) 
						), 
						new Branch("Remain silent").textScene("ORC-SILENT").concat(leaveOrc) 
					),
					new Branch(false).checkScene(
						CheckType.ORC_COWARD, 
						new Branch(true).textScene("ORC-REUNION").choiceScene(
							"Accept her invitation?",
							new Branch("Accept (Requires: Catamite)").require(ChoiceCheckType.LEWD).textScene("ORC-REUNION-ACCEPT").encounterEnd(),
							new Branch("Decline").textScene("ORC-REUNION-DECLINE").encounterEnd()
						),
						new Branch(false).textScene("ORC-COWARD-CALLOUT").choiceScene(
							"Well?",
							new Branch("Yes (Requires: Catamite)").require(ChoiceCheckType.LEWD).textScene("ORC-CATAMITE").encounterEnd(),
							new Branch("No").textScene("ORC-ANGER").battleScene(
								BattleCode.ORC,
								new Branch(Outcome.VICTORY).textScene("ORC-VICTORY2").concat(battleVictory),
								new Branch(Outcome.DEFEAT).textScene("ORC-DENIGRATION").choiceScene(
									"Man or woman?", 
									new Branch("Man").textScene("ORC-BAD-END").gameEnd(),
									new Branch("Woman").textScene("ORC-WIFE").gameEnd()
								),
								new Branch(Outcome.SATISFIED).textScene("ORC-VIOLATED").gameEnd()
							)
						)
					)
				).getEncounter();
			case SHOP:
				return new Branch().textScene("TOWN-SHOP").shopScene(ShopCode.SHOP).encounterEnd().getEncounter();
			case SLIME:
				Branch loveDartCont = new Branch().textScene("SLIME-LOVEDART-CONT");
				return new Branch().textScene("SLIME-INTRO").choiceScene(
					"What do you do with the slime?",
					new Branch("Fight Her").battleScene(
						BattleCode.SLIME,
						new Branch(Outcome.VICTORY).textScene("SLIME-VICTORY").choiceScene(
							"Slay the slime?",
							new Branch("Stab the core").textScene("SLIME-STAB").checkScene(
								Stat.AGILITY,
								new Branch(6).textScene("SLIME-SHATTER").encounterEnd(),
								new Branch(0).textScene("SLIME-FAIL").gameEnd()
							),
							new Branch("Spare her").textScene("SLIME-SPARE")						
						),
						new Branch(Outcome.DEFEAT).textScene("SLIME-DEFEAT").choiceScene(
							"What do you do?",
							new Branch("Try to speak").textScene("SLIME-MOUTH").encounterEnd(),
							new Branch("Run!").checkScene(
								Stat.AGILITY, 
								new Branch(5).textScene("SLIME-FLEE").encounterEnd(),
								new Branch(0).textScene("SLIME-FALL").encounterEnd()								
							)
						)
					),
					new Branch("Smooch Her").textScene("SLIME-APPROACH").choiceScene(
						"Do you enter the slime, or...?",
						new Branch("Go In (Requires: Free cock)").require(ChoiceCheckType.FREE_COCK).textScene("SLIME-ENTER"),
						new Branch("Love Dart (Requires: Catamite)").require(ChoiceCheckType.LEWD).textScene("SLIME-LOVEDART").checkScene(CheckType.PLUGGED, new Branch(true).textScene("SLIME-BUTTPLUG").concat(loveDartCont), new Branch(false).concat(loveDartCont)),
						new Branch("Leave Her Be")
					),
					new Branch("Leave Her Be")			
				).getEncounter();
			case SPIDER:
				Branch spiderBattle = new Branch().battleScene(
					BattleCode.SPIDER, 
					new Branch(Outcome.VICTORY).textScene("SPIDER-VICTORY").encounterEnd(),
					new Branch(Outcome.DEFEAT).textScene("SPIDER-DEFEAT").choiceScene("Pick your poison.",
						new Branch("Become her lover").textScene("SPIDER-LOVER").textScene("SPIDER-OVIPOSITION").textScene("SPIDER-END").gameEnd(),
						new Branch("Become her dinner").textScene("SPIDER-BITE").gameEnd()
					),
					new Branch(Outcome.KNOT_ANAL).textScene("SPIDER-OVIPOSITION").textScene("SPIDER-NO-FERTILIZE").gameEnd());
				Branch afterSigil = new Branch().textScene("SPIDER-BABY").choiceScene(
					"What do you do?", 
					new Branch("Try to crush the tiny spider").checkScene(Stat.PERCEPTION, new Branch(6).textScene("SPIDER-AWARE").concat(spiderBattle), new Branch(0).textScene("SPIDER-AMBUSH").concat(spiderBattle)), 
					new Branch("Try to pet the tiny spider").textScene("SPIDER-GET").concat(spiderBattle), 
					new Branch("Leave the tiny spider alone").textScene("SPIDER-IGNORE").choiceScene("Do you fight or flee?", new Branch("Fight").concat(spiderBattle), new Branch("Flee").checkScene(Stat.AGILITY, new Branch(4).textScene("SPIDER-FLEE-PASS").checkScene(Stat.ENDURANCE, new Branch(4).textScene("SPIDER-FULL-FLEE").encounterEnd(), new Branch(0).textScene("SPIDER-FLEE-FAIL").concat(spiderBattle)), new Branch(0).textScene("SPIDER-FLEE-FAIL").concat(spiderBattle)))
				);
				Branch afterTrap1 = new Branch().textScene("SPIDER-SIGIL").choiceScene("Touch the sigil?", new Branch("Touch it").checkScene(Stat.MAGIC, new Branch(4).textScene("SPIDER-SIGIL-SUCCESS").concat(afterSigil), new Branch(2).textScene("SPIDER-SIGIL-PARTIAL").concat(afterSigil), new Branch(0).textScene("SPIDER-SIGIL-FAILURE").concat(afterSigil)), new Branch("Don't touch it").concat(afterSigil));
				Branch receiveTrap = new Branch().checkScene(CheckType.ALIVE, new Branch(true).concat(afterTrap1), new Branch(false).textScene("SPIDER-UNCONSCIOUS").gameEnd());
				Branch afterRoom1 = new Branch().textScene("SPIDER-TRAP-APPROACH").checkScene(Stat.AGILITY, new Branch(7).textScene("SPIDER-AVOID-TRAP").concat(afterTrap1), new Branch(0).textScene("SPIDER-FAIL-TRAP").checkScene(Stat.ENDURANCE, new Branch(7).textScene("SPIDER-ENDURE").concat(receiveTrap), new Branch(4).textScene("SPIDER-PARTIAL-ENDURE").concat(receiveTrap), new Branch(0).textScene("SPIDER-FAIL-ENDURE").concat(receiveTrap)));
				return new Branch().checkScene(
					CheckType.SPIDER, 
					new Branch(true).textScene("SPIDER-INTRO").choiceScene(
						"What do you do?",
						new Branch("Traverse the Ruins").textScene("SPIDER-ENTER").choiceScene("Enter the room?", new Branch("Enter").textScene("SPIDER-ROOM").checkScene(Stat.PERCEPTION, new Branch(5).textScene("SPIDER-FIND1").concat(afterRoom1), new Branch(0).textScene("SPIDER-FIND1-FAIL").concat(afterRoom1)), new Branch("Pass by").concat(afterRoom1)),
						new Branch("Turn Back").encounterEnd()
					),
					new Branch(false).textScene("SPIDER-REVISIT").encounterEnd()
				).getEncounter();
			case STARVATION:
				return new Branch().textScene("STARVATION-INTRO").checkScene(
					CheckType.VIRGIN,
					// if you're a virgin, it should mention it at the appropriate time
					new Branch(true).textScene("STARVATION").textScene("STARVATION-VIRGIN").textScene("STARVATION-FIRST-TIME"),
					new Branch(false).textScene("STARVATION").checkScene(Perk.BEASTMASTER, new Branch(3).textScene("STARVATION-GAME-OVER").gameEnd(), new Branch(2).textScene("STARVATION-WARNING"), new Branch(1).textScene("STARVATION-FIRST-TIME"), new Branch(0)) // can't get to Branch(0)
				).getEncounter();
			case STORY_FEM:
				return new Branch().textScene("STORY-FEM").encounterEnd().getEncounter();
			case STORY_SIGN:
				return new Branch().textScene("CROSSROADS").encounterEnd().getEncounter();
			case TOWN:
				break;
			case TOWN2:
				break;
			case TOWN_CRIER:
				Array<String> squareOptions = new Array<String>();
				for (int ii = 0; ii < 5; ii++) {
					squareOptions.add("TOWN-SQUARE-" + ii);
				}
				
				Branch goodInfo = new Branch().textScene("TOWN-SQUARE-INFORMANT-GOOD");
				Branch payHimMore = new Branch().textScene("TOWN-SQUARE-INFORMANT-OKAY").checkScene(
						Stat.CHARISMA, 
						new Branch (4).concat(goodInfo), 
						new Branch(0).textScene("TOWN-SQUARE-INFORMANT-REQUEST").choiceScene("Deal?", new Branch("Pay (10 GP)").require(ChoiceCheckType.GOLD_GREATER_THAN_X, 10).textScene("TOWN-SQUARE-INFORMANT-PAYMORE").concat(goodInfo), new Branch("Refuse"))
					);
				
				Branch payHim = new Branch().choiceScene(
					"Pay him for info?", 
					new Branch("Pay (20 GP)").require(ChoiceCheckType.GOLD_GREATER_THAN_X, 20).textScene("TOWN-SQUARE-INFORMANT-PAID").concat(payHimMore), 
					new Branch("Offer... something else").textScene("TOWN-SQUARE-INFORMANT-ALTERNATIVE").choiceScene(
						"What do you do?", 
						new Branch("Bend over (Requires: Catamite)").require(ChoiceCheckType.LEWD).checkScene(Perk.PERFECT_BOTTOM, new Branch(3).textScene("TOWN-SQUARE-INFORMANT-GOODANAL").textScene("TOWN-SQUARE-INFORMANT-OKAY").concat(goodInfo), new Branch(0).textScene("TOWN-SQUARE-INFORMANT-ANAL").concat(payHimMore)), 
						new Branch("Get on your knees").choiceScene(
							"What do you do on your knees?", 
							new Branch("Suck it").checkScene(Perk.BLOWJOB_EXPERT, new Branch(3).textScene("TOWN-SQUARE-INFORMANT-GOODORAL").textScene("TOWN-SQUARE-INFORMANT-OKAY").concat(goodInfo), new Branch(0).textScene("TOWN-SQUARE-INFORMANT-ORAL").concat(payHimMore)), 
							new Branch("Stroke it").textScene("TOWN-SQUARE-INFORMANT-HAND").checkScene(Perk.CRANK_MASTER, new Branch(3).textScene("TOWN-SQUARE-INFORMANT-GOODHAND").concat(payHimMore), new Branch(0).textScene("TOWN-SQUARE-INFORMANT-HAND").textScene("TOWN-SQUARE-INFORMANT-LIE"))
						),
						new Branch("Leave")
					),
					new Branch("Refuse")
				);
				
				Branch townSquareOptions = new Branch().choiceScene(
					"What do you do?",
					new Branch("Eavesdrop").checkScene(
						CheckType.CRIER_QUEST, 
						new Branch(true).textScene("TOWN-SQUARE-INFORMANT").concat(payHim),
						new Branch(false).checkScene(CheckType.CRIER_REFUSE, new Branch(true).textScene("TOWN-SQUARE-INFORMANT-RETURN").concat(payHim), new Branch(false).randomScene(squareOptions))
					),
					new Branch("Listen to the town crier").checkScene(
						CheckType.CRIER, 
						new Branch(true).textScene("CRIER-NEW"), 
						new Branch(false).checkScene(CheckType.QUETZAL_DEFEATED, new Branch(true).textScene("CRIER-QUETZAL"), new Branch(false).textScene("CRIER-OLD"))
					)
				);
				return new Branch().checkScene(CheckType.DAY, new Branch(true).textScene("TOWN-SQUARE-INTRO").concat(townSquareOptions), new Branch(false).textScene("TOWN-SQUARE-NIGHT")).getEncounter();
			case TOWN_STORY:
				Branch leaveTown = new Branch().textScene("STORY-007").encounterEnd();
				return new Branch().textScene("STORY-005").shopScene(ShopCode.FIRST_STORY).textScene("STORY-006A").checkScene(
					Stat.CHARISMA,
					new Branch(6).textScene("STORY-006B").concat(leaveTown),
					new Branch(0).textScene("STORY-006C").concat(leaveTown)
				).getEncounter();
			case TRUDY_COMPANION:
				return new Branch().checkScene(CheckType.TRUDY_COMPANION1, 
						new Branch(true).textScene("TRUDY-COMPANION-FIRST").choiceScene("Spend time with Trudy?", new Branch("Spend time").textScene("TRUDY-COMPANION-HANGOUT"), new Branch("Not now")), 
						new Branch(false).checkScene(
							CheckType.TRUDY_COMPANION2, 
							new Branch(true).textScene("TRUDY-COMPANION-SECOND").choiceScene("Train with Trudy?", new Branch("Train").textScene("TRUDY-COMPANION-LEARN"), new Branch("Not now")),
							new Branch(false).textScene("TRUDY-COMPANION-REPEAT")
						)
					).getEncounter();
			case WEAPON_SHOP:
				return new Branch().textScene("WEAPON-SHOP").shopScene(ShopCode.WEAPON_SHOP).getEncounter();
			case WERESLUT:
				Branch knotted = new Branch().textScene("WEREWOLF-KNOT").checkScene(Perk.BITCH, new Branch(3).textScene("WEREWOLF-BITCH-END").gameEnd(), new Branch(0).textScene("WEREWOLF-POST-KNOT"));
				Branch mated = new Branch().textScene("WEREWOLF-MATED").concat(knotted);
				Branch bitch = new Branch(2).textScene("WEREWOLF-BITCH").concat(mated);
				Branch uninterested = new Branch(0).textScene("WEREWOLF-UNINTERESTED");
				
				return new Branch().textScene("WEREWOLF-INTRO").battleScene(
			    	BattleCode.WERESLUT,
			    	// this has a reference to the first node in this branch, which gets welded with the current context node
			        new Branch(Outcome.VICTORY).textScene("WEREWOLF-VICTORY").checkScene(Stat.STRENGTH, new Branch(8).textScene("WEREWOLF-STRONG").concat(mated), new Branch(0).checkScene(Perk.BITCH, bitch, uninterested)),
			        new Branch(Outcome.KNOT_ANAL).textScene("WEREWOLF-BATTLE-KNOT").concat(knotted),
			        new Branch(Outcome.KNOT_ORAL).textScene("WEREWOLF-BATTLE-KNOT-ORAL").gameEnd(),
			        new Branch(Outcome.DEFEAT).textScene("WEREWOLF-DEFEAT").checkScene(Perk.BITCH, bitch, uninterested),
			        new Branch(Outcome.SATISFIED).textScene("WEREWOLF-SATISFIED")
			    ).getEncounter();
			case WITCH_COTTAGE:
				Branch magicShop = new Branch().textScene("WITCH-COTTAGE-STORE").choiceScene("Peruse her wares?", new Branch("Peruse").shopScene(ShopCode.MAGIC_SHOP), new Branch("Leave"));
				Branch purchase = new Branch().choiceScene(
					"Purchase the goddess' blessing?", 
					new Branch("Pay 100 GP").require(ChoiceCheckType.GOLD_GREATER_THAN_X, 100).textScene("WITCH-COTTAGE-MONEY").concat(magicShop), 
					new Branch("Pay with soulbit").textScene("WITCH-COTTAGE-SOUL").concat(magicShop), 
					new Branch("Give her the gem").require(ChoiceCheckType.HAS_GEM).textScene("WITCH-COTTAGE-GEM").concat(magicShop), 
					new Branch("Don't buy it")
				);
				return new Branch().checkScene(
					CheckType.WITCH_MET, 
					new Branch(true).checkScene(CheckType.BLESSING_PURCHASED, new Branch(true).textScene("WITCH-COTTAGE-RETURN-BOUGHT").concat(magicShop), new Branch(false).textScene("WITCH-COTTAGE-RETURN-BUY").concat(purchase)), 
					new Branch(false).checkScene(
						CheckType.CRIER_KNOWLEDGE, 
						new Branch(true).textScene("WITCH-COTTAGE").concat(purchase), 
						new Branch(false).textScene("WITCH-COTTAGE-NOQUEST")
					)
				).getEncounter();
			default: 
		}
		return new Branch().textScene("TOWN").encounterEnd().getEncounter();	
	}

	enum EndTokenType {
		Choice,
		Check,
		Battle,
		Gametype,
		EndEncounter,
		EndGame 
	}
	
	public enum ChoiceCheckType {
		LEWD,
		GOLD_GREATER_THAN_X,
		GOLD_LESS_THAN_X,
		STAT_GREATER_THAN_X,
		STAT_LESS_THAN_X,
		PERK_GREATER_THAN_X,
		PERK_LESS_THAN_X,
		FREE_COCK, 
		HAS_GEM;
		
		public boolean isValidChoice(PlayerCharacter character, Stat statToCheck, Perk perkToCheck, int target) {
			switch (this) {
				case LEWD:
					return character.isLewd();
				case GOLD_GREATER_THAN_X:
					return character.getMoney() >= target;
				case GOLD_LESS_THAN_X:
					return character.getMoney() < target;
				case PERK_GREATER_THAN_X:
					return character.getPerks().get(perkToCheck, 0) >= target;
				case PERK_LESS_THAN_X:
					return character.getPerks().get(perkToCheck, 0) < target;
				case STAT_GREATER_THAN_X:
					return character.getRawStat(statToCheck) >= target;
				case STAT_LESS_THAN_X:
					return character.getRawStat(statToCheck) < target;
				case FREE_COCK:
					return !character.isChastitied();
				case HAS_GEM:
					return character.hasGem();
			}
			return false;
		}
	}
	
	public class Branch {
		
		Array<SceneToken> sceneTokens;
		OrderedMap<Object, Branch> branchOptions;
		Object key;
		BranchToken branchToken;
		BattleCode battleCode;
		Stance playerStance;
		Stance enemyStance;
		boolean disarm;
		int climaxCounter;
		ChoiceCheckToken require;
		int concatCounter;
		
		boolean preprocessed;
		Array<Scene> scenes;
		Array<BattleScene> battleScenes;
		Array<EndScene> endScenes;
		
		public Branch () {
			this((Object)null);
		}

		public Branch (int check) {
			this((Object)check);
		}
		
		public Branch (Outcome type) {
			this((Object)type);
		}
		
		public Branch(String key) {
			this((Object)key);
		}
		
		public Branch (Object key) {
			init();
			this.key = key;
		}

		private void init() {
			sceneTokens = new Array<SceneToken>();
			branchOptions = new OrderedMap<Object, Branch>();
		}
		
		public Branch textScene(String key) {
			sceneTokens.addAll(reader.loadScript(key));
			return this;
		}
		
		public Branch characterCreation(boolean storyMode) {
			sceneTokens.add(new CharacterCreationToken(storyMode));
			return this;
		}
		
		public Branch characterCustomization() {
			sceneTokens.add(new CharacterCustomizationToken());
			return this;
		}

		public Branch skillSelection() {
			sceneTokens.add(new SkillSelectionToken());
			return this;
		}
		
		public Branch shopScene(ShopCode shopCode) {
			sceneTokens.add(new ShopSceneToken(shopCode));
			return this;
		}
		
		public Branch randomScene(Array<String> keys) {
			sceneTokens.addAll(reader.loadScript(keys.random()));
			return this;
		}
		
		public Branch concat(Branch branch) {
			return weldBranches(new Branch[]{branch});
		}
		
		public Branch choiceScene(String toDisplay, Branch ... branches) {
			branchToken = new ChoiceSceneToken(toDisplay);
			return weldBranches(branches);
		}
		
		public Branch gameTypeScene(Branch... branches) {
			branchToken = new GameTypeSceneToken();
			return weldBranches(branches);
		}
		
		public Branch checkScene(Stat toCheck, Branch ... branches) {
			branchToken = new CheckSceneToken(toCheck);
			return weldBranches(branches);
		}
		
		public Branch checkScene(Perk toCheck, Branch ... branches) {
			branchToken = new CheckSceneToken(toCheck);
			return weldBranches(branches);
		}
		
		public Branch checkScene(CheckType toCheck, Branch ... branches) {
			branchToken = new CheckSceneToken(toCheck);
			return weldBranches(branches);
		}
		
		public Branch weldBranches(Branch[] branches) {
			for (Branch branch : branches) {
				branchOptions.put(branch.getKey() != null ? branch.getKey() : "CONCAT-"+concatCounter++, branch);
			}
			return this;
		}
		
		public Branch battleScene(BattleCode battleCode, Branch ... branches) { return battleScene(battleCode, Stance.BALANCED, Stance.BALANCED, branches); }
		public Branch battleScene(BattleCode battleCode, int climaxCounter, Branch ... branches) { return battleScene(battleCode, Stance.BALANCED, Stance.BALANCED, false, climaxCounter, branches); }		
		public Branch battleScene(BattleCode battleCode, Stance playerStance, Stance enemyStance, Branch ... branches) { return battleScene(battleCode, playerStance, enemyStance, false, branches); }
		public Branch battleScene(BattleCode battleCode, Stance playerStance, Stance enemyStance, boolean disarm, Branch ... branches) { return battleScene(battleCode, playerStance, enemyStance, disarm, 0, branches); }
		public Branch battleScene(BattleCode battleCode, Stance playerStance, Stance enemyStance, boolean disarm, int climaxCounter, Branch ... branches) {
			// for each of the branches, add them to the next map with their associated code
			branchToken = new BattleSceneToken(battleCode);
			this.battleCode = battleCode;
			this.playerStance = playerStance;
			this.enemyStance = enemyStance;
			this.disarm = disarm;
			this.climaxCounter = climaxCounter;
			return weldBranches(branches);
		}
		
		public Branch require(ChoiceCheckType type) {
			require = new ChoiceCheckToken(type);
			return this;
		}
		
		public Branch require(ChoiceCheckType type, int target) {
			require = new ChoiceCheckToken(type, target);
			return this;
		}
		
		public Branch require(ChoiceCheckType type, Stat stat, int target) {
			require = new ChoiceCheckToken(type, target, stat);
			return this;
		}
		
		public Branch require(ChoiceCheckType type, Perk perk, int target) {
			require = new ChoiceCheckToken(type, target, perk);
			return this;
		}
		
		public Object getKey() {
			return key;
		}
		
		public Branch encounterEnd() {
			branchToken = new EndSceneToken(EndTokenType.EndEncounter);			
			return this;
		}
		
		public Branch gameEnd() {
			branchToken = new EndSceneToken(EndTokenType.EndGame);	
			return this;
		}
		
		private void preprocess() {
			preprocess(null, null, null);
		}
	
		private void preprocess(AssetEnum startBackground, AssetEnum startForeground, AnimationEnum startAnimatedForeground) {
			if (preprocessed) return;
			preprocessed = true;
			for (SceneToken token : sceneTokens) {
				token.preprocess(startBackground, startForeground, startAnimatedForeground);
				startBackground = token.background;
				startForeground = token.foreground;
				startAnimatedForeground = token.animatedForeground;
			}
			
			for (OrderedMap.Entry<Object, Branch> next : branchOptions) {
				next.value.preprocess(startBackground, startForeground, startAnimatedForeground);				
			}
		}
		
		private Scene weld(Array<Scene> scenes, Array<BattleScene> battleScenes, Array<EndScene> endScenes, OrderedMap.Entry<Object, Branch> next, OrderedMap<Integer, Scene> sceneMap) {
			battleScenes.addAll(next.value.getBattleScenes());
			endScenes.addAll(next.value.getEndScenes());
			Array<Scene> nextScenes = next.value.getScenes();
			scenes.addAll(nextScenes);
			Scene nextScene = nextScenes.first();
			sceneMap.put(nextScene.getCode(), nextScene);
			return nextScene;
		}
		
		private OrderedMap<Integer, Scene> addScene(Array<Scene> scenes, Scene toAdd, boolean addToMasterMap) {
			OrderedMap<Integer, Scene> sceneMap = new OrderedMap<Integer, Scene>();
			scenes.add(toAdd);
			sceneMap = new OrderedMap<Integer, Scene>();
			if (addToMasterMap) masterSceneMap.put(toAdd.getCode(), toAdd);
			sceneMap.put(toAdd.getCode(), toAdd);
			sceneCounter++;
			return sceneMap;
		}
		
		private void upsertScenes() {
			if (scenes != null) return;
			preprocess();
			// set fields
			scenes = new Array<Scene>();
			battleScenes = new Array<BattleScene>();
			endScenes = new Array<EndScene>();
					
			Skin skin = assetManager.get(AssetEnum.UI_SKIN.getSkin());
			
			// set shadows
		    Array<Scene> scenes = new Array<Scene>();
		    Array<BattleScene> battleScenes = new Array<BattleScene>();
		    Array<EndScene> endScenes = new Array<EndScene>();
			OrderedMap<Integer, Scene> sceneMap = new OrderedMap<Integer, Scene>();
			
			boolean reverse = false;
			
			if (branchToken != null) {
				reverse = true;
				
				switch (branchToken.type) {
					case Battle:
						// for each branch get the scenes, the first entry in that list is what this branchToken scene should be tied to
						ObjectMap<String, Integer> outcomeToScene = new ObjectMap<String, Integer>();
						for (OrderedMap.Entry<Object, Branch> next : branchOptions) {
							Scene nextScene = weld(scenes, battleScenes, endScenes, next, sceneMap);
							outcomeToScene.put(((Outcome) next.key).toString(), nextScene.getCode());
						}
						
						BattleScene newBattleScene = new BattleScene(sceneMap, saveService, battleCode, playerStance, enemyStance, disarm, climaxCounter, outcomeToScene);
						battleScenes.add(newBattleScene);
						sceneMap = addScene(scenes, newBattleScene, false);						
						break;
					case Check:
						CheckSceneToken checkBranchToken = ((CheckSceneToken)branchToken);
						if (checkBranchToken.getStat() != null || checkBranchToken.getPerk() != null) {
							OrderedMap<Integer, Scene> checkValueMap = new OrderedMap<Integer, Scene>();
							for (OrderedMap.Entry<Object, Branch> next : branchOptions) {
								Scene nextScene = weld(scenes, battleScenes, endScenes, next, sceneMap);
								checkValueMap.put(((Integer) next.key), nextScene);
							}
							if (checkBranchToken.getStat() != null) {
								sceneMap = addScene(scenes, new CheckScene(sceneMap, sceneCounter, assetManager, saveService, font, getDefaultBackground().build(), checkBranchToken.getStat(), checkValueMap, checkValueMap.get(0), character), true);						
							}
							else {
								sceneMap = addScene(scenes, new CheckScene(sceneMap, sceneCounter, assetManager, saveService, font, getDefaultBackground().build(), checkBranchToken.getPerk(), checkValueMap, checkValueMap.get(0), character), true);						
							}
						}
						else {
							OrderedMap<Boolean, Scene> checkValueMap = new OrderedMap<Boolean, Scene>();
							for (OrderedMap.Entry<Object, Branch> next : branchOptions) {
								Scene nextScene = weld(scenes, battleScenes, endScenes, next, sceneMap);
								checkValueMap.put(((Boolean) next.key), nextScene);
							}
							sceneMap = addScene(scenes, new CheckScene(sceneMap, sceneCounter, assetManager, saveService, font, getDefaultBackground().build(), checkBranchToken.getCheckType(), checkValueMap.get(true), checkValueMap.get(false), character), true);						
						}
						break;
					case Choice:
					case Gametype:
						Sound buttonSound = assetManager.get(AssetEnum.BUTTON_SOUND.getSound());
						for (OrderedMap.Entry<Object, Branch> next : branchOptions) {
							weld(scenes, battleScenes, endScenes, next, sceneMap);
						}
						Array<BranchChoice> choices = new Array<BranchChoice>();
						for (OrderedMap.Entry<Object, Branch> next : branchOptions) {
							Scene nextScene = next.value.getScenes().first();
							choices.add(new BranchChoice(new TextButton((String)next.key, skin), nextScene, next.value.require, buttonSound));							
						}
						AbstractChoiceScene choiceScene = branchToken.type == EndTokenType.Choice ? new ChoiceScene(sceneMap, sceneCounter, saveService, font, ((ChoiceSceneToken)branchToken).getToDisplay(), choices, assetManager.get(AssetEnum.STANCE_ARROW.getTexture()), character, getDefaultBackground().build())
						: new GameTypeScene(sceneMap, sceneCounter, saveService, choices, new BackgroundBuilder(assetManager.get(AssetEnum.GAME_TYPE_BACKGROUND.getTexture())).build());
						// need the choiceScene in order to create the buttons, so iterate through again
						sceneMap = addScene(scenes, choiceScene, true);						
						break;
					case EndGame:
					case EndEncounter:
						EndScene newEndScene = branchToken.type == EndTokenType.EndEncounter ? 
							new EndScene(sceneCounter, EndScene.Type.ENCOUNTER_OVER, saveService, assetManager, returnContext, getEndBackground(), new LogDisplay(sceneCodes, masterSceneMap, skin), results) :
							new EndScene(sceneCounter, EndScene.Type.GAME_OVER, saveService, assetManager, SaveManager.GameContext.GAME_OVER, getEndBackground(), new LogDisplay(sceneCodes, masterSceneMap, skin), results);
						endScenes.add(newEndScene);
						sceneMap = addScene(scenes, newEndScene, true);		
						break;
				}
			}
			else {
				for (OrderedMap.Entry<Object, Branch> next : branchOptions) {
					weld(scenes, battleScenes, endScenes, next, sceneMap);
				}
			}
			
			// catch if there's an unplugged branch without an end scene
			if (sceneMap.size == 0) {
				EndScene newEndScene;
				newEndScene = new EndScene(sceneCounter, EndScene.Type.ENCOUNTER_OVER, saveService, assetManager, returnContext, getEndBackground(), new LogDisplay(sceneCodes, masterSceneMap, skin), results);
				endScenes.add(newEndScene);
				sceneMap = addScene(scenes, newEndScene, true);		
			}
			
			String characterName = character.getCharacterName();
			String buttsize = character.getBootyLiciousness();
			String lipsize = character.getLipFullness();
			String debt = character.getCurrentDebt() > 0 ? "You currently owe " + character.getCurrentDebt() + " gold." : "";
			
			Array<Background> backgrounds = new Array<Background>();
			AssetEnum background = null;
			AssetEnum foreground = null;
			AnimationEnum animatedForeground = null;

			Texture dialogBoxTexture = assetManager.get(AssetEnum.BATTLE_HOVER.getTexture());
			
			if (sceneTokens.size > 0) {
				reverse = false;
				// iterate through and every time either background or foreground/animatedforeground change, create a new background
				for (SceneToken token: sceneTokens) {
					if (token instanceof ShopSceneToken || token instanceof CharacterCreationToken || token instanceof CharacterCustomizationToken || token instanceof SkillSelectionToken) continue;	
					// if all of the tokens are  the same, clone the last background
					if ((token.foreground == null || token.foreground == foreground) && (token.animatedForeground == null || token.animatedForeground == animatedForeground) && (token.background == null || token.background == background)) {
						if (backgrounds.size > 0) {
							backgrounds.add(backgrounds.get(backgrounds.size - 1).clone());
						}
						else {
							backgrounds.add(getDefaultBackground().setDialogBox(dialogBoxTexture).build());
						}
					}
					else {						
						BackgroundBuilder backgroundBuilder = (token.background != null ? new BackgroundBuilder(assetManager.get(token.background.getTexture()), token.background.isTinted()) : background != null ? new BackgroundBuilder(assetManager.get(background.getTexture()), background.isTinted()) : getDefaultBackground()).setDialogBox(dialogBoxTexture); 
						if (token.animatedForeground != null) {
							int x = token.animatedForeground == AnimationEnum.BUTTBANG ? 555 : 0;
							int y = token.animatedForeground == AnimationEnum.BUTTBANG ? 520 : 0;
							backgroundBuilder.setForeground(getAnimation(token.animatedForeground), x, y);
						}
						else if (token.foreground != null) {
							if (token.foreground == AssetEnum.SILHOUETTE) {
								backgroundBuilder.setForeground(assetManager.get(token.foreground.getTexture()), 1000, 0);
							}
							else {
								backgroundBuilder.setForeground(assetManager.get(token.foreground.getTexture()));
							}
						}
						else if (animatedForeground != null) {
							int x = animatedForeground == AnimationEnum.BUTTBANG ? 555 : 0;
							int y = animatedForeground == AnimationEnum.BUTTBANG ? 520 : 0;
							backgroundBuilder.setForeground(getAnimation(animatedForeground), x, y);
						}
						else if (foreground != null) {
							if (foreground == AssetEnum.SILHOUETTE) {
								backgroundBuilder.setForeground(assetManager.get(foreground.getTexture()), 1000, 0);
							}
							else {
								backgroundBuilder.setForeground(assetManager.get(foreground.getTexture()));
							}
						}
						backgrounds.add(backgroundBuilder.build());
					}
					background = token.background;
					foreground = token.foreground;
					animatedForeground = token.animatedForeground;
				}
				
				backgrounds.reverse();
				sceneTokens.reverse();
					
				// run through the tokens once and create a list of backgrounds using clone when it persists (need to check both background and animated background, clone if it doesn't, then reverse that list
				// probably need to make the variables foreground, background, and animatedbackground - think hoverbox is consistent for now
				// taking the branchToken scene and use it as the entrypoint, traversing the sceneTokens backwards and putting them into each other
				int ii = 0;
				for (SceneToken token: sceneTokens) {
					Scene newScene = null;
					if (token instanceof ShopSceneToken) {
						ShopCode shopCode = ((ShopSceneToken) token).shopCode;
						// this needs to get the proper background, probably from shopcode attributes
						Background bg = new BackgroundBuilder(assetManager.get(shopCode.getBackground()), shopCode.isTinted()).setForeground(assetManager.get(shopCode.getForeground()), shopCode.getX(), shopCode.getY()).build();
						newScene = new ShopScene(sceneMap, sceneCounter, saveService, assetManager, character, bg, shopCode, shops.get(shopCode.toString()));
					}
					else if (token instanceof CharacterCreationToken) {
						boolean storyMode = ((CharacterCreationToken) token).storyMode;
						newScene = new CharacterCreationScene(sceneMap, sceneCounter, saveService, new BackgroundBuilder(assetManager.get(AssetEnum.CLASS_SELECT_BACKGROUND.getTexture())).build(), assetManager, character, storyMode);
					}
					else if (token instanceof SkillSelectionToken) {
						newScene = new SkillSelectionScene(sceneMap, sceneCounter, saveService, new BackgroundBuilder(assetManager.get(AssetEnum.SKILL_SELECTION_BACKGROUND.getTexture())).build(), assetManager, character);
					}
					else if (token instanceof CharacterCustomizationToken) {
						newScene = new CharacterCustomizationScene(sceneMap, sceneCounter, saveService, font, new BackgroundBuilder(assetManager.get(AssetEnum.CHARACTER_CUSTOM_BACKGROUND.getTexture())).build(), assetManager, character);
					}
					else {
						String scriptLine = token.text.replace("<NAME>", characterName).replace("<BUTTSIZE>", buttsize).replace("<LIPSIZE>", lipsize).replace("<DEBT>", debt);
						// create the scene
						newScene = new TextScene(sceneMap, sceneCounter, assetManager, font, saveService, backgrounds.get(ii++), scriptLine, getMutations(token.mutations), character, new LogDisplay(sceneCodes, masterSceneMap, skin), token.music != null ? token.music : null, token.sound != null ? token.sound.getSound() : null);		
					}
					// add it to array
					scenes.add(newScene);
					// use it to make the map
					sceneMap = new OrderedMap<Integer, Scene>();
					masterSceneMap.put(sceneCounter, newScene);
					sceneMap.put(sceneCounter++, newScene);
				}
				
				scenes.reverse();
			}
			
			if (reverse) scenes.reverse();
			this.scenes.addAll(scenes);
			this.battleScenes.addAll(battleScenes);
			this.endScenes.addAll(endScenes);
		}
		
		private BackgroundBuilder getDefaultBackground() {
			return new BackgroundBuilder(assetManager.get(AssetEnum.DEFAULT_BACKGROUND.getTexture()), AssetEnum.DEFAULT_BACKGROUND.isTinted());
		}
		
		private Background getEndBackground() {
			return getDefaultBackground().setDialogBox(assetManager.get(AssetEnum.BATTLE_HOVER.getTexture()), 400, 100, 1200, 800).build();
		}
		
		private AnimatedActor getAnimation(AnimationEnum type) {
			AnimatedActor animation = animationCache.get(type);
			if (animation == null) {
				animation = type.getAnimation(assetManager);
				animationCache.put(type, animation);
			}			
			return animation;
		}
		
		public Array<Scene> getScenes() {
			upsertScenes();
			return scenes;
		}
		
		public Array<BattleScene> getBattleScenes() {
			upsertScenes();
			return battleScenes;
		}
		
		public Array<EndScene> getEndScenes() {
			upsertScenes();
			return endScenes;
		}
		
		public Scene getStartScene() {
			// returns the first scene or the current scene based on sceneCode
			upsertScenes();
			if (sceneCodes.size == 0) {
				saveService.saveDataValue(SaveEnum.MUSIC, AssetEnum.ENCOUNTER_MUSIC);
				return scenes.get(0);
			}
			return masterSceneMap.get(sceneCodes.get(sceneCodes.size - 1), scenes.get(0));
		}
		
		public Encounter getEncounter() {
			// this should accept some kind of object that has assetmanager and whatever else to actually build the scenes			
			return new Encounter(getScenes(), getEndScenes(), getBattleScenes(), getStartScene());
		}
		
	}
	
	public static class ChoiceCheckToken {
		public final ChoiceCheckType type;
		public final Stat statToCheck;
		public final Perk perkToCheck;
		public final int target;
		
		public ChoiceCheckToken(ChoiceCheckType type) {
			this(type, 0);
		}
		// this needs to be refactored so that Stat or Perk based ChoiceCheckTypes have their stat or perk built in
		public ChoiceCheckToken(ChoiceCheckType type, int target) {
			this(type, target, null, null);
		}
		public ChoiceCheckToken(ChoiceCheckType type, int target, Stat statToCheck) {
			this(type, target, statToCheck, null);
		}
		public ChoiceCheckToken(ChoiceCheckType type, int target, Perk perkToCheck) {
			this(type, target, null, perkToCheck);
		}
		public ChoiceCheckToken(ChoiceCheckType type, int target, Stat statToCheck, Perk perkToCheck) {
			this.type = type;
			this.target = target;
			this.statToCheck = statToCheck;
			this.perkToCheck = perkToCheck;
		}
		public boolean isValidChoice(PlayerCharacter character) {
			return type.isValidChoice(character, statToCheck, perkToCheck, target);
		}
	}
	
	public static class BranchChoice {
		public final TextButton button;
		public final Scene scene;
		public final ChoiceCheckToken require;
		public final Sound clickSound;
		public BranchChoice(TextButton button, Scene scene, ChoiceCheckToken require, Sound clickSound) {
			this.button = button;
			this.scene = scene;
			this.require = require;
			this.clickSound = clickSound;
		}
	}
	
	public Array<Mutation> getMutations(Array<MutateToken> tokens) {
		Array<Mutation> mutations = new Array<Mutation>();
		if (tokens == null) { return mutations; }
		for (MutateToken token: tokens) {
			mutations.add(token.getMutation(saveService));
		}
		return mutations;	
	}
	
	public class BranchToken {
		private final EndTokenType type;
		protected BranchToken(EndTokenType type) {
			this.type = type;
		}
		EndTokenType getType() { return type; }
	}
	
	// this should have playerstance, disarm, etc. - right now this is basically unused
	public class BattleSceneToken extends BranchToken {
		private final BattleCode battleCode;
		public BattleSceneToken(BattleCode battleCode) {
			super(EndTokenType.Battle);
			this.battleCode = battleCode;
		}
		public BattleCode getBattleCode() { return battleCode; }
		
	}
	
	public class GameTypeSceneToken extends BranchToken {
		public GameTypeSceneToken() {
			super(EndTokenType.Gametype);
		}
	}
	
	public class CheckSceneToken extends BranchToken {
		private final Stat stat;
		private final CheckType checkType;
		private final Perk perk;
		public CheckSceneToken(Stat stat) {
			this(stat, null, null);
		}
		public CheckSceneToken(CheckType checkType) {
			this(null, checkType, null);
		}
		public CheckSceneToken(Perk perk) {
			this(null, null, perk);
		}
		public CheckSceneToken(Stat stat, CheckType checkType, Perk perk) {
			super(EndTokenType.Check);
			this.stat = stat;
			this.checkType = checkType;
			this.perk = perk;
		}
		
		public Stat getStat() { return stat; }
		public CheckType getCheckType() { return checkType; }
		public Perk getPerk() { return perk; }
	}
	
	public class ChoiceSceneToken extends BranchToken {
		private final String toDisplay;
		private final ChoiceCheckType require;
		public ChoiceSceneToken(String toDisplay) {
			this(toDisplay, null);
		}
		public ChoiceSceneToken(String toDisplay, ChoiceCheckType require) {
			super(EndTokenType.Choice);
			this.toDisplay = toDisplay;
			this.require = require;
		}
		public ChoiceCheckType getRequire() { return require; }
		public String getToDisplay() { return toDisplay; }
		
	}
	
	public class EndSceneToken extends BranchToken {
		public EndSceneToken(EndTokenType endType) {
			super(endType);
		}
	}
	
	// as scenetokens arrays are retrieved, they're placed into a map of key to scene token array to prevent duplicates - and another map is used for the actual scenes so they aren't duplicated (scenes are individual at that point, not in an array, and that key is the new scenecode) - this is currently not implemented
	
	// this represents a text-like scene - it should be able to display text, show who is talking, display a new background, play an animation, play a sound, mutate the game state 
	// these will be serialized into the actual script and have their own key
	public static class SceneToken {
		// this should also have a fontEnum to determine what font the text is displayed in
		String text;
		String speaker;
		AssetEnum background;
		AssetEnum foreground;
		AnimationEnum animatedForeground;
		AssetEnum sound;
		AssetEnum music;
		Array<MutateToken> mutations;
		public void preprocess(AssetEnum startBackground, AssetEnum startForeground, AnimationEnum startAnimatedForeground) {
			//if foreground == null and token.foreground != null and animatedForeground != null -> foreground = token.foreground, animatedForeground = null
					
			if (background == null) background = startBackground;
			if (foreground == null) {
				foreground = startForeground;
			}
			else if (animatedForeground == null) {
				startAnimatedForeground = null;
			}
			if (animatedForeground == null) animatedForeground = startAnimatedForeground;
		}		
	}
	
	public static class ShopSceneToken extends SceneToken {
		ShopCode shopCode;
		public ShopSceneToken (ShopCode shopCode) {
			this.shopCode = shopCode;
		}
	}
	
	public static class CharacterCreationToken extends SceneToken {
		boolean storyMode;
		public CharacterCreationToken (boolean storyMode) {
			this.storyMode = storyMode;
		}
	}
	
	public static class CharacterCustomizationToken extends SceneToken {}
	
	public static class SkillSelectionToken extends SceneToken {}
	
	public static class MutateToken {
		SaveEnum saveType;
		ProfileEnum profileSaveType;
		Object value;
				
		public Mutation getMutation(SaveService saveService) {
			if (saveType != null) { return new Mutation(saveService, saveType, value); }
			else return new Mutation(saveService, profileSaveType, value);
		}
	}
}

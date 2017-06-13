package com.majalis.encounter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.OrderedMap;
import com.majalis.asset.AssetEnum;
import com.majalis.battle.BattleCode;
import com.majalis.battle.Battle.Outcome;
import com.majalis.character.EnemyCharacter;
import com.majalis.character.EnemyEnum;
import com.majalis.character.Perk;
import com.majalis.character.PlayerCharacter;
import com.majalis.character.Stance;
import com.majalis.character.AbstractCharacter.Stat;
import com.majalis.save.ProfileEnum;
import com.majalis.save.SaveEnum;
import com.majalis.save.SaveManager;
import com.majalis.save.SaveService;
import com.majalis.save.SaveManager.GameContext;
import com.majalis.save.SaveManager.GameMode;
import com.majalis.scenes.AbstractChoiceScene;
import com.majalis.scenes.BattleScene;
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
	private final int sceneCode;
	private final ObjectMap<String, Shop> shops;
	private final PlayerCharacter character;
	private final GameContext returnContext;
	private final GameMode mode;
	private final OrderedMap<Integer, Scene> masterSceneMap;
	// can probably be replaced with a call to scenes.size
	private int sceneCounter;
	
	protected EncounterBuilder(EncounterReader reader, AssetManager assetManager, SaveService saveService, BitmapFont font, int sceneCode, ObjectMap<String, Shop> shops, PlayerCharacter character, GameContext returnContext, GameMode mode) {
		this.reader = reader;
		this.assetManager = assetManager;
		this.saveService = saveService;
		this.font = font;
		this.sceneCode = sceneCode;
		this.shops = shops == null ? new ObjectMap<String, Shop>() : shops;
		this.character = character;
		this.returnContext = returnContext;
		this.mode = mode;
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
						new Branch("Mount him").textScene("ADVENTURER-TOPPED").encounterEnd(), 
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
								new Branch(false).textScene("ADVENTURER-END").encounterEnd()
							)
						)
					)
				).getEncounter();
			case BEASTMISTRESS:
				return new Branch().textScene("BEASTMISTRESS-INTRO").choiceScene(
					"Snake or Pussy?", 
					new Branch("Snake").textScene("BEASTMISTRESS-ENTRANCE").encounterEnd(),
					new Branch("Pussy").textScene("BEASTMISTRESS-PUSSY").battleScene(
						BattleCode.BEASTMISTRESS,
						new Branch(Outcome.VICTORY).textScene("BEASTMISTRESS-VICTORY").choiceScene(
							"Well?", 
							new Branch("Go Spelunking").textScene("BEASTMISTRESS-SPELUNKING").encounterEnd(), 
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
				return new Branch().textScene("BRIGAND-INTRO").checkScene(
					Stat.PERCEPTION, 
					new Branch(6).textScene("BRIGAND-SPOT").choiceScene(
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
							new Branch("Accept").require(ChoiceCheckType.LEWD).textScene("BRIGAND-ACCEPT").choiceScene(
								"Tell her to pull out?",
								new Branch("Say Nothing").textScene("BRIGAND-CATCH").encounterEnd(),
								new Branch("Ask her").textScene("BRIGAND-REQUEST").checkScene(
									Stat.CHARISMA,
									new Branch(4).textScene("BRIGAND-FACIAL").encounterEnd(),
									new Branch(0).textScene("BRIGAND-BADTASTE").encounterEnd()
								)
							),
							new Branch("Decline").textScene("BRIGAND-DECLINE").checkScene(
								Stat.CHARISMA,
								new Branch(5).textScene("BRIGAND-CONVINCE").encounterEnd(),
								new Branch(0).textScene("BRIGAND-FAIL").battleScene(
									BattleCode.BRIGAND, 
									battleBranches2
								)
							)
						)
					),
					new Branch(4).textScene("BRIGAND-STAB").battleScene(
						BattleCode.BRIGAND, 
						battleBranches2
					),
					new Branch(0).textScene("BRIGAND-BACKSTAB").battleScene(
						BattleCode.BRIGAND, Stance.STANDING_BOTTOM, Stance.STANDING,
						battleBranches2	
					)	
				).getEncounter();
			case CAMP_AND_EAT:
				return new Branch().textScene("FORCED-CAMP").encounterEnd().getEncounter();
			case CENTAUR:
				Branch[] centaurBattle = new Branch[]{new Branch(Outcome.VICTORY).textScene("CENTAUR-VICTORY").encounterEnd(), new Branch(Outcome.DEFEAT).textScene("CENTAUR-DEFEAT").gameEnd(), new Branch(Outcome.SATISFIED).textScene("CENTAUR-SATISFIED").encounterEnd()};
				Branch[] unicornBattle = new Branch[]{new Branch(Outcome.VICTORY).textScene("UNICORN-VICTORY").encounterEnd(), new Branch(Outcome.DEFEAT).textScene("UNICORN-DEFEAT").encounterEnd()};
				return new Branch().textScene("CENTAUR-INTRO").checkScene(
					CheckType.VIRGIN, 
					new Branch(true).textScene("UNICORN-ENTRANCE").battleScene(
						BattleCode.UNICORN,
						unicornBattle
					),
					new Branch(false).textScene("CENTAUR-ENTRANCE").checkScene(
						Perk.ANAL_LOVER,
						new Branch(3).textScene("CENTAUR-CATAMITE").battleScene(
							BattleCode.CENTAUR, Stance.DOGGY_BOTTOM, Stance.DOGGY,
							centaurBattle
						),
						new Branch(0).choiceScene(
							"Fight the centaur?",
							new Branch("Fight Her").battleScene(
								BattleCode.CENTAUR,
								centaurBattle
							),
							new Branch("Decline").encounterEnd(),
							new Branch("Ask For It").require(ChoiceCheckType.LEWD).textScene("CENTAUR-CATAMITE").battleScene(
								BattleCode.CENTAUR, Stance.DOGGY_BOTTOM, Stance.DOGGY,
								centaurBattle
							)
						)
					)
				).getEncounter();
			case COTTAGE_TRAINER:
				return new Branch().textScene("STORY-003").characterCreation(true).encounterEnd().getEncounter(); 		
			case COTTAGE_TRAINER_VISIT:
				return new Branch().textScene("STORY-004").encounterEnd().getEncounter();
			case CRIER_QUEST:
				return new Branch().checkScene(
					CheckType.CRIER_QUEST, 
					new Branch(true).textScene("CRIER-NEW2").encounterEnd(), 
					new Branch(false).textScene("CRIER-OLD2").encounterEnd()
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
			case ERROR:
				break;
			case FIRST_BATTLE_STORY:
				return new Branch().textScene("STORY-FIGHT-FIRST").battleScene(
					BattleCode.GOBLIN_STORY,
					new Branch(Outcome.VICTORY).textScene("STORY-FIGHT-GOBLIN-VICTORY").encounterEnd(),
					new Branch(Outcome.DEFEAT).textScene("STORY-FIGHT-GOBLIN-DEFEAT").gameEnd()
				).getEncounter();
			case FORT:
				break;
			case GADGETEER:
				Branch no = new Branch("No thanks").textScene("GADGETEER-NO").encounterEnd();
				Branch yes = new Branch().textScene("GADGETEER-SLAVE").gameEnd();
				Branch[] yesyesyes = new Branch[]{new Branch("yes").concat(yes), new Branch("yeS").concat(yes), new Branch("YES").concat(yes)};
				return new Branch().textScene("GADGETEER-INTRO").choiceScene(
					"Do you want to peruse her wares?", 
					new Branch("Peruse").shopScene(ShopCode.GADGETEER_SHOP).textScene("GADGETEER-POSTSHOP").checkScene(
						Perk.ANAL_LOVER,
						new Branch(3).textScene("GADGETEER-PEGGED").choiceScene("Become hers?", yesyesyes),
						new Branch(2).textScene("GADGETEER-PLUGS").encounterEnd(),
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
					), 
					no
				).getEncounter();
			case GOBLIN:
				Branch postVirginityCheck = new Branch().choiceScene(
					"Mouth, or ass?",
					new Branch("In the Mouth").textScene("GOBLIN-MOUTH").encounterEnd(),
					new Branch("Up The Ass").textScene("GOBLIN-ANAL").checkScene(
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
					)
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
					new Branch("Catch Her (5 AGI)").checkScene(
						Stat.AGILITY,
						new Branch(5).textScene("GOBLIN-CATCH").choiceScene(
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
						pantsCutDown
					),
					new Branch("Trip Her (4 AGI)").checkScene(
						Stat.AGILITY,
						new Branch(4).textScene("GOBLIN-TRIP").choiceScene(
							"What do you do?",
							new Branch("Attack").battleScene(
								BattleCode.GOBLIN, Stance.OFFENSIVE, Stance.PRONE,
								battleScenes
							),
							new Branch("Run").textScene("GOBLIN-FLEE").encounterEnd()
						),
						pantsCutDown
					),
					new Branch("Disarm Her (3 AGI)").checkScene(
						Stat.AGILITY,
						new Branch(3).textScene("GOBLIN-DISARM").choiceScene(
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
						pantsCutDown
					),
					new Branch("Avoid Her (2 AGI)").checkScene(
						Stat.AGILITY,
						new Branch(2).textScene("GOBLIN-DODGE").choiceScene(
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
						pantsCutDown
					)
				);
				
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
							new Branch(Outcome.DEFEAT).textScene("GOBLIN-MALE-DEFEAT").gameEnd()
						)
					)
				).getEncounter();
			case HARPY:
				Branch[] battleBranches = new Branch[]{new Branch(Outcome.VICTORY).textScene("HARPY-VICTORY").encounterEnd(), new Branch(Outcome.DEFEAT).textScene("HARPY-DEFEAT").gameEnd(), new Branch(Outcome.SATISFIED).textScene("HARPY-SATISFIED").encounterEnd()};
				return new Branch().textScene("HARPY-INTRO").checkScene(
					Stat.AGILITY,
					new Branch(6).textScene("HARPY-DODGE").battleScene(
						BattleCode.HARPY, Stance.BALANCED, Stance.PRONE,
						battleBranches
					),
					new Branch(4).textScene("HARPY-DUCK").battleScene(
						BattleCode.HARPY, Stance.KNEELING, Stance.BALANCED,
						battleBranches
					),
					new Branch(0).textScene("HARPY-HORK").battleScene(
						BattleCode.HARPY, Stance.FELLATIO_BOTTOM, Stance.FELLATIO,
						battleBranches
					) 
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
					new Branch("Rest at Inn (10 Gold)").require(ChoiceCheckType.GOLD_GREATER_THAN_10).textScene("INNKEEP-02").encounterEnd(),
					new Branch("Rest at Inn (Low Funds)").require(ChoiceCheckType.GOLD_LESS_THAN_10).checkScene(
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
			case OGRE:
				Branch passerby = new Branch().textScene("OGRE-PASSERBY").encounterEnd();
				Branch partingScene = new Branch().checkScene(
					Perk.GIANT_LOVER,
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
				return new Branch().textScene("STORY-OGRE").choiceScene(
					"Continue on?",
					new Branch("Press on").textScene("STORY-OGRE-DEFEAT").gameEnd(), 
					new Branch("Turn back").encounterEnd()
				).getEncounter();
			case OGRE_WARNING_STORY:
				return new Branch().textScene("OGRE-WARN").encounterEnd().getEncounter();
			case ORC:
				Branch leaveOrc = new Branch().textScene("ORC-LEAVE").encounterEnd();
				Branch oralScene = new Branch().textScene("ORC-ORAL").encounterEnd();
				Branch failedCharisma = new Branch(0).textScene("ORC-OFFER-FAIL").concat(oralScene);
				Branch battleVictory = new Branch().textScene("ORC-VICTORY").choiceScene(
					"Front, back, or decline?", 
					new Branch("Front (Requires: Catamite)").require(ChoiceCheckType.LEWD).textScene("ORC-ANAL").encounterEnd(),
					new Branch("Back").textScene("ORC-BOTTOM").encounterEnd(),
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
									new Branch("Anal (Requires: Catamite)").require(ChoiceCheckType.LEWD).textScene("ORC-ANAL").encounterEnd(),
									new Branch("Oral").textScene("ORC-OFFER-ORAL").concat(oralScene), 
									new Branch("Nasal").textScene("ORC-NASAL").encounterEnd(),
									new Branch("Facial (4 CHA)").checkScene(
										Stat.CHARISMA, 
										new Branch(4).textScene("ORC-FACIAL").encounterEnd(),
										failedCharisma 
									),
									new Branch("Nasal (6 CHA)").checkScene(
										Stat.CHARISMA, 
										new Branch(6).textScene("ORC-PENAL").encounterEnd(),
										failedCharisma 
									)
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
						new Branch("Go In").textScene("SLIME-ENTER").encounterEnd(),
						new Branch("Love Dart (Requires: Catamite)").require(ChoiceCheckType.LEWD).textScene("SLIME-LOVEDART").encounterEnd()
					),
					new Branch("Leave Her Be").encounterEnd()			
				).getEncounter();
			case SOUTH_PASS:
				return new Branch().textScene("SOUTH-PASS").encounterEnd().getEncounter();
			case STARVATION:
				Branch starveEnding = new Branch("STARVATION-CONTINUE").gameEnd();
				return new Branch().textScene("STARVATION").checkScene(
					CheckType.VIRGIN,
					new Branch(true).textScene("STARVATION-VIRGIN").concat(starveEnding),
					new Branch(false).concat(starveEnding)
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
				return new Branch().checkScene(
					CheckType.CRIER, 
					new Branch(true).textScene("CRIER-NEW").encounterEnd(), 
					new Branch(false).textScene("CRIER-OLD").encounterEnd()
				).getEncounter();
			case TOWN_STORY:
				Branch leaveTown = new Branch().textScene("STORY-007").encounterEnd();
				return new Branch().textScene("STORY-005").shopScene(ShopCode.FIRST_STORY).textScene("STORY-006A").checkScene(
					Stat.CHARISMA,
					new Branch(6).textScene("STORY-006B").concat(leaveTown),
					new Branch(0).textScene("STORY-006C").concat(leaveTown)
				).getEncounter();
			case WEAPON_SHOP:
				return new Branch().textScene("WEAPON-SHOP").shopScene(ShopCode.WEAPON_SHOP).encounterEnd().getEncounter();
			case WERESLUT:
				return new Branch().textScene("WEREWOLF-INTRO").battleScene(
			    	BattleCode.WERESLUT,
			    	// this has a reference to the first node in this branch, which gets welded with the current context node
			        new Branch(Outcome.VICTORY).textScene("WEREWOLF-VICTORY").encounterEnd(),
			        new Branch(Outcome.KNOT).textScene("WEREWOLF-KNOT").gameEnd(),
			        new Branch(Outcome.DEFEAT).textScene("WEREWOLF-DEFEAT").encounterEnd(),
			        new Branch(Outcome.SATISFIED).textScene("WEREWOLF-SATISFIED").encounterEnd()
			    ).getEncounter();
			case WEST_PASS:
				return new Branch().textScene("WEST-PASS").encounterEnd().getEncounter();
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
	
	protected enum ChoiceCheckType {
		LEWD,
		GOLD_GREATER_THAN_10,
		GOLD_LESS_THAN_10
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
		ChoiceCheckType require;
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
			require = type;
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
	
		private void preprocess(AssetEnum startBackground, AssetEnum startForeground, EnemyEnum startAnimatedForeground) {
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
								sceneMap = addScene(scenes, new CheckScene(sceneMap, sceneCounter, assetManager, saveService, font, new BackgroundBuilder(assetManager.get(AssetEnum.DEFAULT_BACKGROUND.getTexture())).build(), checkBranchToken.getStat(), checkValueMap, checkValueMap.get(0), character), true);						
							}
							else {
								sceneMap = addScene(scenes, new CheckScene(sceneMap, sceneCounter, assetManager, saveService, font, new BackgroundBuilder(assetManager.get(AssetEnum.DEFAULT_BACKGROUND.getTexture())).build(), checkBranchToken.getPerk(), checkValueMap, checkValueMap.get(0), character), true);						
							}
						}
						else {
							OrderedMap<Boolean, Scene> checkValueMap = new OrderedMap<Boolean, Scene>();
							for (OrderedMap.Entry<Object, Branch> next : branchOptions) {
								Scene nextScene = weld(scenes, battleScenes, endScenes, next, sceneMap);
								checkValueMap.put(((Boolean) next.key), nextScene);
							}
							sceneMap = addScene(scenes, new CheckScene(sceneMap, sceneCounter, assetManager, saveService, font, new BackgroundBuilder(assetManager.get(AssetEnum.DEFAULT_BACKGROUND.getTexture())).build(), checkBranchToken.getCheckType(), checkValueMap.get(true), checkValueMap.get(false), character), true);						
						}
						break;
					case Choice:
					case Gametype:
						Skin skin = assetManager.get(AssetEnum.UI_SKIN.getSkin());
						Sound buttonSound = assetManager.get(AssetEnum.BUTTON_SOUND.getSound());
						for (OrderedMap.Entry<Object, Branch> next : branchOptions) {
							weld(scenes, battleScenes, endScenes, next, sceneMap);
						}
						if (branchToken.type == EndTokenType.Choice) {
							Table table = new Table();
							ChoiceSceneToken choiceBranchToken = (ChoiceSceneToken)branchToken;
							ChoiceScene choiceScene = new ChoiceScene(sceneMap, sceneCounter, saveService, font, choiceBranchToken.getToDisplay(), table, new BackgroundBuilder(assetManager.get(AssetEnum.DEFAULT_BACKGROUND.getTexture())).build());
							// need the choiceScene in order to create the buttons, so iterate through again
							for (OrderedMap.Entry<Object, Branch> next : branchOptions) {
								Scene nextScene = next.value.getScenes().first();
								TextButton button = new TextButton((String)next.key, skin);
								// this needs the logic for checks as well
								button.addListener(getListener(choiceScene, nextScene, buttonSound, next.value.require, button));
								table.add(button).size(650, 150).row();
							}
							sceneMap = addScene(scenes, choiceScene, true);	
						}
						else {
							Array<TextButton> buttons = new Array<TextButton>();
							for (OrderedMap.Entry<Object, Branch> next : branchOptions) {
								TextButton button = new TextButton((String)next.key, skin);
								buttons.add(button);
							}
							GameTypeScene gameTypeScene = new GameTypeScene(sceneMap, sceneCounter, saveService, buttons, new BackgroundBuilder(assetManager.get(AssetEnum.GAME_TYPE_BACKGROUND.getTexture())).build());
							int ii = 0;
							for (OrderedMap.Entry<Object, Branch> next : branchOptions) {
								Scene nextScene = next.value.getScenes().first();
								buttons.get(ii).addListener(getListener(gameTypeScene, nextScene, buttonSound, next.value.require, buttons.get(ii)));
								ii++;
							}	
							sceneMap = addScene(scenes, gameTypeScene, true);	
						}
						break;
					case EndGame:
					case EndEncounter:
						EndScene newEndScene;
						if (branchToken.type == EndTokenType.EndEncounter) newEndScene = new EndScene(EndScene.Type.ENCOUNTER_OVER, saveService, returnContext);
						else newEndScene = new EndScene(EndScene.Type.GAME_OVER, saveService, SaveManager.GameContext.GAME_OVER);
						endScenes.add(newEndScene);
						sceneMap = addScene(scenes, newEndScene, false);		
						break;
				}
			}
			else {
				for (OrderedMap.Entry<Object, Branch> next : branchOptions) {
					weld(scenes, battleScenes, endScenes, next, sceneMap);
				}
			}
				
			String characterName = character.getCharacterName();
			String buttsize = character.getBootyLiciousness();
			String lipsize = character.getLipFullness();

			// run through the tokens once and create a list of backgrounds using clone when it persists (need to check both background and animated background, clone if it doesn't, then reverse that list
			// probably need to make the variables foreground, background, and animatedbackground - think hoverbox is consistent for now
			Array<Background> backgrounds = new Array<Background>();
			AssetEnum background = null;
			AssetEnum foreground = null;
			EnemyEnum animatedForeground = null;

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
							backgrounds.add(new BackgroundBuilder(assetManager.get(AssetEnum.DEFAULT_BACKGROUND.getTexture())).setDialogBox(dialogBoxTexture).build());
						}
					}
					else {
						BackgroundBuilder backgroundBuilder = new BackgroundBuilder(assetManager.get(token.background != null ? token.background.getTexture() : background != null ? background.getTexture() : AssetEnum.DEFAULT_BACKGROUND.getTexture())).setDialogBox(dialogBoxTexture); 
						if (token.animatedForeground != null) {
							int x = token.animatedForeground == EnemyEnum.BUTTBANG ? 555 : 0;
							int y = token.animatedForeground == EnemyEnum.BUTTBANG ? 520 : 0;
							backgroundBuilder.setForeground(EnemyCharacter.getAnimatedActor(token.animatedForeground), x, y);
						}
						else if (animatedForeground != null) {
							int x = animatedForeground == EnemyEnum.BUTTBANG ? 555 : 0;
							int y = animatedForeground == EnemyEnum.BUTTBANG ? 520 : 0;
							backgroundBuilder.setForeground(EnemyCharacter.getAnimatedActor(animatedForeground), x, y);
						}
						else if (token.foreground != null) {
							if (token.foreground == AssetEnum.SILHOUETTE) {
								backgroundBuilder.setForeground(assetManager.get(token.foreground.getTexture()), 1000, 0);
							}
							else {
								backgroundBuilder.setForeground(assetManager.get(token.foreground.getTexture()));
							}
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
					background = token.background != null ? token.background : background;
					foreground = token.foreground != null ? token.foreground : foreground;
					animatedForeground = token.animatedForeground != null ? token.animatedForeground : animatedForeground;
				}
				
				backgrounds.reverse();
				sceneTokens.reverse();
				
				// taking the branchToken scene and use it as the entrypoint, traversing the sceneTokens backwards and putting them into each other
				int ii = 0;
				for (SceneToken token: sceneTokens) {
					Scene newScene = null;
					if (token instanceof ShopSceneToken) {
						ShopCode shopCode = ((ShopSceneToken) token).shopCode;
						// this needs to get the proper background, probably from shopcode attributes
						newScene = new ShopScene(sceneMap, sceneCounter, saveService, assetManager, character, new BackgroundBuilder(assetManager.get(shopCode.getBackground())).setForeground(assetManager.get(shopCode.getForeground()), shopCode.getX(), shopCode.getY()).build(), shopCode, shops.get(shopCode.toString()));
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
						String scriptLine = token.text.replace("<NAME>", characterName).replace("<BUTTSIZE>", buttsize).replace("<LIPSIZE>", lipsize);
						// create the scene
						newScene = new TextScene(sceneMap, sceneCounter, assetManager, font, saveService, backgrounds.get(ii++), scriptLine, getMutations(token.mutations), character, token.music != null ? token.music.getMusic() : null, token.sound != null ? token.sound.getSound() : null);		
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
			if (sceneCode == -1) {
				saveService.saveDataValue(SaveEnum.MUSIC, AssetEnum.ENCOUNTER_MUSIC.getPath());
				return scenes.get(0);
			}
			return masterSceneMap.get(sceneCode);
		}
		
		public Encounter getEncounter() {
			return new Encounter(getScenes(), getEndScenes(), getBattleScenes(), getStartScene());
		}
		
		private ClickListener getListener(final AbstractChoiceScene currentScene, final Scene nextScene, final Sound buttonSound, final ChoiceCheckType type, final TextButton button) {
			return new ClickListener() {
		        @Override
		        public void clicked(InputEvent event, float x, float y) {
		        	if (type == null || isValidChoice(type)) {
		        		buttonSound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
			        	// set new Scene as active based on choice
			        	nextScene.setActive();
			        	currentScene.finish();
		        	}
		        	else {
			        	button.setColor(Color.GRAY);
		        	}
		        }
		    };
		}
		
		private boolean isValidChoice(ChoiceCheckType type) {
			switch (type) {
			case LEWD:
				return character.isLewd();
			case GOLD_GREATER_THAN_10:
				return character.getMoney() >= 10;
			case GOLD_LESS_THAN_10:
				return character.getMoney() < 10;
			default:
				return false;
			}
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
		EnemyEnum animatedForeground;
		AssetEnum sound;
		AssetEnum music;
		Array<MutateToken> mutations;
		public void preprocess(AssetEnum startBackground, AssetEnum startForeground, EnemyEnum startAnimatedForeground) {
			if (background == null) background = startBackground;
			if (foreground == null) foreground = startForeground;
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

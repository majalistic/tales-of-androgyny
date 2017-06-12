package com.majalis.encounter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.OrderedMap;
import com.majalis.asset.AnimatedActor;
import com.majalis.asset.AssetEnum;
import com.majalis.battle.BattleCode;
import com.majalis.battle.Battle.Outcome;
import com.majalis.character.EnemyEnum;
import com.majalis.character.Perk;
import com.majalis.character.PlayerCharacter;
import com.majalis.character.PlayerCharacter.QuestFlag;
import com.majalis.character.PlayerCharacter.QuestType;
import com.majalis.character.Techniques;
import com.majalis.encounter.Background.BackgroundBuilder;
import com.majalis.character.SexualExperience.SexualExperienceBuilder;
import com.majalis.character.Stance;
import com.majalis.character.AbstractCharacter.Stat;
import com.majalis.save.ProfileEnum;
import com.majalis.save.SaveEnum;
import com.majalis.save.SaveManager;
import com.majalis.save.SaveManager.GameContext;
import com.majalis.save.SaveManager.GameMode;
import com.majalis.save.SaveManager.JobClass;
import com.majalis.save.SaveService;
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
import com.majalis.scenes.ShopScene.Shop;
import com.majalis.scenes.ShopScene.ShopCode;
import com.majalis.scenes.SkillSelectionScene;
import com.majalis.scenes.TextScene;
import com.majalis.scenes.CheckScene.CheckType;
/*
 * Given a sceneCode, reads that encounter and constructs it from a script file.
 */
public class EncounterBuilder {
	private final Array<Scene> scenes;
	private final Array<EndScene> endScenes;
	private final Array<BattleScene> battleScenes; 
	private final EncounterReader reader;
	private final AssetManager assetManager;
	private final SaveService saveService;
	private final BitmapFont font;
	private final BitmapFont smallFont;
	private final int sceneCode;
	private final ObjectMap<String, Shop> shops;
	private final PlayerCharacter character;
	private final GameContext returnContext;
	// can probably be replaced with a call to scenes.size
	private int sceneCounter;
	
	protected EncounterBuilder(EncounterReader reader, AssetManager assetManager, SaveService saveService, BitmapFont font, BitmapFont smallFont, int sceneCode, ObjectMap<String, Shop> shops, PlayerCharacter character, GameContext returnContext) {
		scenes = new Array<Scene>();
		endScenes = new Array<EndScene>();
		battleScenes = new Array<BattleScene>();
		this.reader = reader;
		this.assetManager = assetManager;
		this.saveService = saveService;
		this.font = font;
		this.smallFont = smallFont;
		this.sceneCode = sceneCode;
		this.shops = shops == null ? new ObjectMap<String, Shop>() : shops;
		this.character = character;
		this.returnContext = returnContext;
		sceneCounter = 0;
	}
	/* different encounter "templates" */
	@SuppressWarnings("unchecked")
	protected Encounter getClassChoiceEncounter() {	
		Background background = getDefaultTextBackground();
		Background classSelectbackground = getClassSelectBackground();	
		Background silhouetteBackground = new BackgroundBuilder(assetManager.get(AssetEnum.BURNING_FORT_BG.getTexture())).setDialogBox(assetManager.get(AssetEnum.BATTLE_HOVER.getTexture())).setForeground(assetManager.get(AssetEnum.SILHOUETTE.getTexture()), 1000, 0).build();
		
		getTextScenes(
			getScript("INTRO"), font, background,
			getGameTypeScene(
				getArray(new String[]{"Create Character", "Story (Patrons)"}),		
				getTextScenes(
					getArray(new String[]{"You've selected to create your character!", "Please choose your class."}), font, background, getArray(new Mutation[]{new Mutation(saveService, SaveEnum.MODE, GameMode.SKIRMISH)}), AssetEnum.INITIAL_MUSIC.getMusic(), new Array<AssetDescriptor<Sound>>(),
					getCharacterCreationScene(
						smallFont, classSelectbackground.clone(), false,
						getSkillSelectionScene(
							new BackgroundBuilder(assetManager.get(AssetEnum.SKILL_SELECTION_BACKGROUND.getTexture())).build(), 
							getCharacterCustomizationScene(
								new BackgroundBuilder(assetManager.get(AssetEnum.CHARACTER_CUSTOM_BACKGROUND.getTexture())).build(), 
								getEndScene(EndScene.Type.ENCOUNTER_OVER)
							)
						)
					)
				),
				getTextScenes(
					getArray(new String[]{"You have entered story mode.", "A tale of androgyny has begun..."}), font, background, getArray(new Mutation[]{new Mutation(saveService, SaveEnum.MODE, GameMode.STORY)}),							
					getTextScenes(
						// needs to be female silhouette from behind BG
						getScript("STORY-000"), font, silhouetteBackground, new Array<Mutation>(), AssetEnum.WAVES.getMusic(), getArray(new AssetDescriptor[]{null, null, null, null, null, null, null, null, null, AssetEnum.SMUG_LAUGH.getSound(), null, null, null, null, null, null, null, null, AssetEnum.SMUG_LAUGH.getSound()}),
						getTextScenes(
							// needs to be hovel BG
							getScript("STORY-001"), font, background, new Array<Mutation>(), AssetEnum.HOVEL_MUSIC.getMusic(),
							getTextScenes(
								// needs to be bright-white BG
								getScript("STORY-002"), font, background, getArray(new Mutation[]{new Mutation(saveService, SaveEnum.CLASS, JobClass.ENCHANTRESS)}),
								getEndScene(EndScene.Type.ENCOUNTER_OVER)						
							)
						)
					)
				)
			)
		);
		
		
		return new Encounter(scenes, endScenes, new Array<BattleScene>(), getStartScene(scenes, sceneCode));
	}
	
	private Background getDefaultTextBackground() { return getDefaultTextBackground(assetManager.get(AssetEnum.DEFAULT_BACKGROUND.getTexture())); }
	
	private Background getDefaultTextBackground(Texture background) { return new BackgroundBuilder(background).setDialogBox(assetManager.get(AssetEnum.BATTLE_HOVER.getTexture())).build(); }
	
	private Background getClassSelectBackground() { return new BackgroundBuilder(assetManager.get(AssetEnum.CLASS_SELECT_BACKGROUND.getTexture())).build(); }
	
	protected Encounter getLevelUpEncounter(boolean storyMode) {
		
		if (storyMode) {
			getTextScenes(
				getArray(new String[]{"You have no skills to select!"}), font, getDefaultTextBackground(), 
				getEndScene(EndScene.Type.ENCOUNTER_OVER)
			);
		}
		else {
			getSkillSelectionScene(
				new BackgroundBuilder(assetManager.get(AssetEnum.SKILL_SELECTION_BACKGROUND.getTexture())).build(), getEndScene(EndScene.Type.ENCOUNTER_OVER)
			);
		}
		return new Encounter(scenes, endScenes, new Array<BattleScene>(), getStartScene(scenes, sceneCode));
	}
	
	protected Encounter getDefaultEncounter() {
		Background background = getDefaultTextBackground(assetManager.get(AssetEnum.STICK_BACKGROUND.getTexture()));
		getTextScenes(new String[]{"You encounter a stick!", "It's actually rather sexy looking.", "There is nothing left here to do."}, font, background, getEndScene(EndScene.Type.ENCOUNTER_OVER));
		return new Encounter(scenes, endScenes, new Array<BattleScene>(), getStartScene(scenes, sceneCode));
	}
	
	@SuppressWarnings("unchecked")
	protected Encounter getRandomEncounter(EncounterCode encounterCode) {
		Texture backgroundTexture = assetManager.get(AssetEnum.DEFAULT_BACKGROUND.getTexture());	
		Background background = getDefaultTextBackground();
		Mutation analReceive = new Mutation(saveService, SaveEnum.ANAL, new SexualExperienceBuilder().setAnalSex(1, 1, 0).build());
		Array<Outcome> normalOutcomes = new Array<Outcome>(new Outcome[]{Outcome.VICTORY, Outcome.DEFEAT, Outcome.SATISFIED});
		
		BattleCode battleCode;
		
		switch (encounterCode) {	
				

				
			case BEASTMISTRESS:
				battleCode = BattleCode.BEASTMISTRESS;
				Background backgroundWithBM = new BackgroundBuilder(assetManager.get(AssetEnum.DEFAULT_BACKGROUND.getTexture())).setDialogBox(assetManager.get(AssetEnum.BATTLE_HOVER.getTexture())).setForeground(assetManager.get(AssetEnum.BEASTMISTRESS.getTexture())).build();
				
				getTextScenes(
					getScript(encounterCode, "INTRO"), font, background, getArray(new Mutation[]{new Mutation(saveService, ProfileEnum.KNOWLEDGE, EnemyEnum.BEASTMISTRESS.toString())}), AssetEnum.WEREWOLF_MUSIC.getMusic(), 
					getTextScenes(
						getScript(encounterCode, 0), font, backgroundWithBM,
						getChoiceScene(
							"Snake or Pussy?", getArray(new String[]{"Snake", "Pussy"}),
							getTextScenes(
								getScript(encounterCode, 1), font, backgroundWithBM,
								getTextScenes(
									getScript(encounterCode, 2), font, backgroundWithBM, getArray(new Mutation[]{new Mutation(saveService, SaveEnum.ANAL, new SexualExperienceBuilder().setAnalSex(1, 1, 1).build())}),
									getEndScene(EndScene.Type.ENCOUNTER_OVER)
								)
							),
							getTextScenes(
								getScript(encounterCode, 3), font, backgroundWithBM, 
								getBattleScene(
									battleCode, normalOutcomes,
									getTextScenes(
										getScript(encounterCode, 4), font, backgroundWithBM, getArray(new Mutation[]{new Mutation(saveService, SaveEnum.EXPERIENCE, 5)}),
										getChoiceScene(
											"Well?", getArray(new String[]{"Go Spelunking", "Go Home"}),
											getTextScenes(
												getScript(encounterCode, 5), font, backgroundWithBM, getArray(new Mutation[]{new Mutation(saveService, SaveEnum.EXPERIENCE, 1)}),
												getEndScene(EndScene.Type.ENCOUNTER_OVER)
											),
											getTextScenes(
												getScript(encounterCode, 6), font, backgroundWithBM, 
												getEndScene(EndScene.Type.ENCOUNTER_OVER)
											)
										)
									),
									getTextScenes(
										getScript(encounterCode, 7), font, backgroundWithBM, 
										getTextScenes(
											getScript(encounterCode, 8), font, backgroundWithBM, getArray(new Mutation[]{new Mutation(saveService, SaveEnum.ANAL, new SexualExperienceBuilder().setAnalSex(1, 0, 0).build())}),
											getTextScenes(
												getScript(encounterCode, 9), font, backgroundWithBM, getArray(new Mutation[]{new Mutation(saveService, SaveEnum.ANAL, new SexualExperienceBuilder().setOralSex(1).build())}),
												getTextScenes(
													getScript(encounterCode, 10), font, backgroundWithBM, getArray(new Mutation[]{new Mutation(saveService, SaveEnum.ANAL, new SexualExperienceBuilder().setOralCreampie(1).build())}),
													getTextScenes(
														getScript(encounterCode, 11), font, backgroundWithBM, getArray(new Mutation[]{new Mutation(saveService, SaveEnum.ANAL, new SexualExperienceBuilder().setAnalSex(4, 3, 1).build())}),
														getEndScene(EndScene.Type.GAME_OVER)
													)
												)
											)
										)
									),
									getCheckScene(
										Stat.AGILITY, new IntArray(new int[]{4}),	
										getTextScenes(
											getScript(encounterCode, 12), font, backgroundWithBM, getArray(new Mutation[]{new Mutation(saveService, SaveEnum.EXPERIENCE, 3)}),
											getEndScene(EndScene.Type.ENCOUNTER_OVER)
										),
										getTextScenes(
											getScript(encounterCode, 13), font, backgroundWithBM, 
											getTextScenes(
												getScript(encounterCode, 14), font, backgroundWithBM, getArray(new Mutation[]{new Mutation(saveService, SaveEnum.ANAL, new SexualExperienceBuilder().setAnalSex(1, 1, 1).build())}),
												getTextScenes(
													getScript(encounterCode, 15), font, background,
													getEndScene(EndScene.Type.ENCOUNTER_OVER)
												)
											)
										)
									)
								)
							)
						)
					)
				);
				break;
			case GADGETEER:
				Background backgroundWithGadgeteer = new BackgroundBuilder(assetManager.get(AssetEnum.DEFAULT_BACKGROUND.getTexture())).setDialogBox(assetManager.get(AssetEnum.BATTLE_HOVER.getTexture())).setForeground(assetManager.get(AssetEnum.GADGETEER.getTexture())).build();
				Background shopGadgeteer = new BackgroundBuilder(assetManager.get(AssetEnum.DEFAULT_BACKGROUND.getTexture())).setForeground(assetManager.get(AssetEnum.GADGETEER.getTexture()), 900, 0).build();
				Mutation[] dryAnal = new Mutation[]{new Mutation(saveService, SaveEnum.ANAL, new SexualExperienceBuilder(1).setAnalEjaculations(1).build())};
				OrderedMap<Integer, Scene> noScene = getTextScenes(
					getScript("GADGETEER-NO"), font, backgroundWithGadgeteer,
					getEndScene(EndScene.Type.ENCOUNTER_OVER)
				);
				getTextScenes (
					getScript("GADGETEER-00"), font, backgroundWithGadgeteer, new Array<Mutation>(), AssetEnum.GADGETEER_MUSIC.getMusic(),
					getChoiceScene(
						"Do you want to peruse her wares?", getArray(new String[]{"Peruse", "No Thanks"}),
						getShopScene(
							ShopCode.GADGETEER_SHOP, shopGadgeteer, 
							getTextScenes(
								getScript("GADGETEER-01"), font, backgroundWithGadgeteer,
								getCheckScene(
									Perk.ANAL_LOVER, new IntArray(new int[]{3, 2, 1}),
									getTextScenes(
										getScript("GADGETEER-02"), font, backgroundWithGadgeteer, 
										getChoiceScene(
											"Become hers?", getArray(new String[]{"Yes", "Yes", "Yes"}),
											getTextScenes(
												getScript("GADGETEER-03"), font, backgroundWithGadgeteer, getArray(dryAnal),
												getEndScene(EndScene.Type.GAME_OVER)	
											),
											getTextScenes(
												getScript("GADGETEER-03"), font, backgroundWithGadgeteer, getArray(dryAnal),
												getEndScene(EndScene.Type.GAME_OVER)	
											),
											getTextScenes(
												getScript("GADGETEER-03"), font, backgroundWithGadgeteer, getArray(dryAnal),
												getEndScene(EndScene.Type.GAME_OVER)	
											)
										)
									),
									getTextScenes(
										getScript("GADGETEER-04"), font, backgroundWithGadgeteer, getArray(dryAnal),
										getEndScene(EndScene.Type.ENCOUNTER_OVER)
									),
									getTextScenes(
										getScript("GADGETEER-05"), font, backgroundWithGadgeteer,
										getChoiceScene(
											"Try the toys?", getArray(new String[]{"Yes", "No thanks"}),
											getTextScenes(
												getScript("GADGETEER-06"), font, backgroundWithGadgeteer, getArray(dryAnal),
												getEndScene(EndScene.Type.ENCOUNTER_OVER)
											),
											noScene
										)
									),
									getTextScenes(
										getScript("GADGETEER-07"), font, backgroundWithGadgeteer,
										getChoiceScene(
											"Try the toys?", getArray(new String[]{"Yes (Requires: Catamite)", "No thanks"}),
											getArray(new ChoiceCheckType[]{ChoiceCheckType.LEWD, null}),
											getTextScenes(
												getScript("GADGETEER-08"), font, backgroundWithGadgeteer, getArray(dryAnal),
												getEndScene(EndScene.Type.ENCOUNTER_OVER)
											),
											noScene
										)
									)
								)
							)
						),
						noScene
					)								
				);
				break;
			case TOWN_CRIER:
				getCheckScene(
					CheckType.CRIER,
					getTextScenes(
						getScript("CRIER-NEW"), font, background, getArray(new Mutation[]{new Mutation(saveService, SaveEnum.QUEST, new QuestFlag(QuestType.CRIER, 1))}),
						getEndScene(EndScene.Type.ENCOUNTER_OVER)
					),
					getTextScenes(
						getScript("CRIER-OLD"), font, background,
						getEndScene(EndScene.Type.ENCOUNTER_OVER)
					)
				);
				break;
			case CRIER_QUEST:
				getCheckScene(
					CheckType.CRIER_QUEST,
					getTextScenes(
						getScript("CRIER-NEW"), font, background, getArray(new Mutation[]{new Mutation(saveService, SaveEnum.QUEST, new QuestFlag(QuestType.CRIER, 2))}),
						getEndScene(EndScene.Type.ENCOUNTER_OVER)
					),
					getTextScenes(
						getScript("CRIER-OLD"), font, background,
						getEndScene(EndScene.Type.ENCOUNTER_OVER)
					)
				);
			case INN:
				Background innkeeper = new BackgroundBuilder(assetManager.get(AssetEnum.DEFAULT_BACKGROUND.getTexture())).setDialogBox(assetManager.get(AssetEnum.BATTLE_HOVER.getTexture())).setForeground(assetManager.get(AssetEnum.INNKEEPER.getTexture())).build();
				Background backgroundKeyhole = new BackgroundBuilder(assetManager.get(AssetEnum.DEFAULT_BACKGROUND.getTexture())).setDialogBox(assetManager.get(AssetEnum.BATTLE_HOVER.getTexture())).setForeground(assetManager.get(AssetEnum.KEYHOLE.getTexture())).build();
				Background backgroundKeyholeGO = new BackgroundBuilder(assetManager.get(AssetEnum.DEFAULT_BACKGROUND.getTexture())).setDialogBox(assetManager.get(AssetEnum.BATTLE_HOVER.getTexture())).setForeground(assetManager.get(AssetEnum.GAME_OVER_KEYHOLE.getTexture())).build();
				
				OrderedMap<Integer, Scene> afterScene = getTextScenes(
					getScript("INNKEEP-10"), font, backgroundKeyhole, getArray(new Mutation[]{new Mutation(saveService, SaveEnum.ANAL, new SexualExperienceBuilder().setAnalSex(5, 2, 3).build())}),
					 getTextScenes(
						getScript("INNKEEP-11"), font, background,
						getTextScenes(
							new Array<String>(new String[]{"."}), font, background, getArray(new Mutation[]{new Mutation(saveService, SaveEnum.FOOD, 15), new Mutation(saveService, SaveEnum.HEALTH, 100)}),
							getEndScene(EndScene.Type.ENCOUNTER_OVER)
						)
					)
				);
				
				getTextScenes(
					getScript("INNKEEP-01"), font, innkeeper,	
					getChoiceScene(
						"Stay the night?", getArray(new String[]{"Rest at Inn (10 Gold)", "Rest at Inn (Low Funds)", "Leave"}), getArray(new ChoiceCheckType[]{ChoiceCheckType.GOLD_GREATER_THAN_10, ChoiceCheckType.GOLD_LESS_THAN_10, null}),
						getTextScenes(
							getScript("INNKEEP-02"), font, innkeeper, getArray(new Mutation[]{new Mutation(saveService, SaveEnum.GOLD, -10), new Mutation(saveService, SaveEnum.FOOD, 15), new Mutation(saveService, SaveEnum.HEALTH, 100)}),
							getEndScene(EndScene.Type.ENCOUNTER_OVER)
						),
						getCheckScene(
							CheckType.INN_0,
							getTextScenes(
								getScript("INNKEEP-03"), font, innkeeper,
								getChoiceScene(
									"Take his offer?", getArray(new String[]{"Get under table", "Leave"}),
									getTextScenes(
										getScript("INNKEEP-04"), font, innkeeper, getArray(new Mutation[]{new Mutation(saveService, SaveEnum.QUEST, new QuestFlag(QuestType.INNKEEP, 1))}),
										getTextScenes(
											getScript("INNKEEP-05"), font, innkeeper, getArray(new Mutation[]{new Mutation(saveService, SaveEnum.ANAL, new SexualExperienceBuilder().setOralSex(1).build())}),
											getTextScenes(
												getScript("INNKEEP-06"), font, innkeeper, getArray(new Mutation[]{new Mutation(saveService, SaveEnum.ANAL, new SexualExperienceBuilder().setOralCreampie(1).build())}),
												getTextScenes(
													new Array<String>(new String[]{"."}), font, innkeeper, getArray(new Mutation[]{new Mutation(saveService, SaveEnum.FOOD, 15), new Mutation(saveService, SaveEnum.HEALTH, 100)}),
													getEndScene(EndScene.Type.ENCOUNTER_OVER)
												)
											)
										)
									),
									getEndScene(EndScene.Type.ENCOUNTER_OVER)
								)
							),
							getCheckScene(
								CheckType.INN_1,
								getTextScenes(
									getScript("INNKEEP-07"), font, innkeeper,
									getChoiceScene(
										"Take his offer?", getArray(new String[]{"Go to his room", "Leave"}),
										getTextScenes(
											getScript("INNKEEP-08"), font, innkeeper, getArray(new Mutation[]{new Mutation(saveService, SaveEnum.QUEST, new QuestFlag(QuestType.INNKEEP, 2))}),
											getCheckScene(
												CheckType.VIRGIN,
												getTextScenes(
													getScript("INNKEEP-09"), font, innkeeper,
													afterScene
												),
												afterScene
											)
										),
										getEndScene(EndScene.Type.ENCOUNTER_OVER)
									)
								),
								getCheckScene(
									CheckType.INN_2,
									getTextScenes(
										getScript("INNKEEP-12"), font, innkeeper,
										getChoiceScene(
											"Take his offer?", getArray(new String[]{"Join him", "Leave"}),
											getTextScenes(
												getScript("INNKEEP-13"), font, innkeeper, getArray(new Mutation[]{new Mutation(saveService, SaveEnum.QUEST, new QuestFlag(QuestType.INNKEEP, 3))}),
												getTextScenes(
													getScript("INNKEEP-14"), font, innkeeper, getArray(new Mutation[]{new Mutation(saveService, SaveEnum.ANAL, new SexualExperienceBuilder().setAnalSex(1, 0, 0).build())}),
													getTextScenes(
														getScript("INNKEEP-15"), font, innkeeper, getArray(new Mutation[]{new Mutation(saveService, SaveEnum.ANAL, new SexualExperienceBuilder().setAnalEjaculations(1).build())}),
														getTextScenes(
															new Array<String>(new String[]{"."}), font, innkeeper, getArray(new Mutation[]{new Mutation(saveService, SaveEnum.FOOD, 15), new Mutation(saveService, SaveEnum.HEALTH, 100)}),
															getEndScene(EndScene.Type.ENCOUNTER_OVER)
														)
													)
												)
											),
											getEndScene(EndScene.Type.ENCOUNTER_OVER)
										)
									),
									getCheckScene(
										CheckType.INN_3,
										getTextScenes(
											getScript("INNKEEP-16"), font, innkeeper,
											getChoiceScene(
												"Take his offer?", getArray(new String[]{"Marry him", "Leave"}),
												getTextScenes(
													getScript("INNKEEP-17"), font, backgroundKeyholeGO, getArray(new Mutation[]{new Mutation(saveService, SaveEnum.QUEST, new QuestFlag(QuestType.INNKEEP, 4))}),
													getEndScene(EndScene.Type.GAME_OVER)
												),
												getEndScene(EndScene.Type.ENCOUNTER_OVER)
											)
										),
										getEndScene(EndScene.Type.ENCOUNTER_OVER) // should never get here
									)
								)
							)
						),
						getEndScene(EndScene.Type.ENCOUNTER_OVER)
					)
				);
				break;
			case COTTAGE_TRAINER:
				background = new BackgroundBuilder(assetManager.get(AssetEnum.CABIN_BACKGROUND.getTexture())).setDialogBox(assetManager.get(AssetEnum.BATTLE_HOVER.getTexture())).build();
				Background trainerBackground = new BackgroundBuilder(assetManager.get(AssetEnum.CABIN_BACKGROUND.getTexture())).setDialogBox(assetManager.get(AssetEnum.BATTLE_HOVER.getTexture())).setForeground(assetManager.get(AssetEnum.TRAINER.getTexture())).build();
				getTextScenes(
					getScript("STORY-003"), font, background, new Array<Mutation>(), AssetEnum.TRAINER_MUSIC.getMusic(),
					getTextScenes(
						getScript("STORY-003A"), font, trainerBackground, 
						getCharacterCreationScene(
							smallFont, getClassSelectBackground(), true,
							getEndScene(EndScene.Type.ENCOUNTER_OVER)
						)
					)
				);
				break;
			case COTTAGE_TRAINER_VISIT:
				background = new BackgroundBuilder(assetManager.get(AssetEnum.CABIN_BACKGROUND.getTexture())).setDialogBox(assetManager.get(AssetEnum.BATTLE_HOVER.getTexture())).build();
				getTextScenes(
					getScript("STORY-004"), font, background, new Array<Mutation>(), AssetEnum.TRAINER_MUSIC.getMusic(),
					getEndScene(EndScene.Type.ENCOUNTER_OVER)						
				);
				break;
			case TOWN_STORY: 
				background = new BackgroundBuilder(assetManager.get(AssetEnum.TOWN_BG.getTexture())).setDialogBox(assetManager.get(AssetEnum.BATTLE_HOVER.getTexture())).build();
				Background backgroundWithShopkeep = new BackgroundBuilder(assetManager.get(AssetEnum.TOWN_BG.getTexture())).setDialogBox(assetManager.get(AssetEnum.BATTLE_HOVER.getTexture())).setForeground(assetManager.get(AssetEnum.SHOPKEEP.getTexture())).build();
				Background shopBackground = new BackgroundBuilder(assetManager.get(AssetEnum.TOWN_BG.getTexture())).setForeground(assetManager.get(AssetEnum.SHOPKEEP.getTexture())).build();
				getTextScenes(
					getScript("STORY-005"), font, background, new Array<Mutation>(),
					getTextScenes (					
						getScript("STORY-006"), font, backgroundWithShopkeep, new Array<Mutation>(), AssetEnum.SHOP_MUSIC.getMusic(), getArray(new AssetDescriptor[]{null, null, null, null, AssetEnum.SMUG_LAUGH.getSound()}),
						getShopScene(
							ShopCode.FIRST_STORY, shopBackground, 
							getTextScenes(					
								getScript("STORY-006A"), font, backgroundWithShopkeep,
								getCheckScene(Stat.CHARISMA, new IntArray(new int[]{6}),
									getTextScenes (
										getScript("STORY-006B"), font, backgroundWithShopkeep, new Array<Mutation>(), AssetEnum.SHOP_MUSIC.getMusic(), getArray(new AssetDescriptor[]{ AssetEnum.SMUG_LAUGH.getSound()}),
										getTextScenes (					
											getScript("STORY-007"), font, background, new Array<Mutation>(),
											getEndScene(EndScene.Type.ENCOUNTER_OVER)	
										)	
									),
									getTextScenes (
										getScript("STORY-006C"), font, backgroundWithShopkeep, new Array<Mutation>(),
										getTextScenes (					
											getScript("STORY-007"), font, background, new Array<Mutation>(),
											getEndScene(EndScene.Type.ENCOUNTER_OVER)	
										)	
									)	
								)	
							)
						)	
					)
				);
				break;
			case MERI_COTTAGE: 
				background = new BackgroundBuilder(assetManager.get(AssetEnum.CABIN_BACKGROUND.getTexture())).setDialogBox(assetManager.get(AssetEnum.BATTLE_HOVER.getTexture())).build();
				Background witchBackground = new BackgroundBuilder(assetManager.get(AssetEnum.CABIN_BACKGROUND.getTexture())).setDialogBox(assetManager.get(AssetEnum.BATTLE_HOVER.getTexture())).setForeground(assetManager.get(AssetEnum.MERI_SILHOUETTE.getTexture())).build();
				getTextScenes (
					getScript("STORY-WITCH-COTTAGE"), font, background, new Array<Mutation>(new Mutation[]{new Mutation(saveService, SaveEnum.SKILL, Techniques.COMBAT_FIRE)}), AssetEnum.TRAINER_MUSIC.getMusic(), 
					getTextScenes (
						getScript("STORY-WITCH-COTTAGE-MERI"), font, witchBackground, new Array<Mutation>(new Mutation[]{new Mutation(saveService, SaveEnum.SKILL, Techniques.COMBAT_FIRE)}), AssetEnum.WEREWOLF_MUSIC.getMusic(),
						getEndScene(EndScene.Type.ENCOUNTER_OVER)
					)
				);
				break;
			case MERI_COTTAGE_VISIT:
				background = new BackgroundBuilder(assetManager.get(AssetEnum.CABIN_BACKGROUND.getTexture())).setDialogBox(assetManager.get(AssetEnum.BATTLE_HOVER.getTexture())).build();
				getTextScenes(
					getScript("STORY-WITCH-COTTAGE-VISIT"), font, background, new Array<Mutation>(), AssetEnum.TRAINER_MUSIC.getMusic(), 
					getEndScene(EndScene.Type.ENCOUNTER_OVER)						
				);
				break;
			case FIRST_BATTLE_STORY:
				battleCode = BattleCode.GOBLIN_STORY;
				Background goblinBackground2 = new BackgroundBuilder(backgroundTexture).setDialogBox(assetManager.get(AssetEnum.BATTLE_HOVER.getTexture())).setForeground(assetManager.get(AssetEnum.GOBLIN.getTexture())).build();
				getTextScenes(
					getScript("STORY-FIGHT-FIRST"), font, background,
					getTextScenes( 
						getScript("STORY-FIGHT-GOBLIN"), font, goblinBackground2, 
						getBattleScene(
							battleCode, 
							getTextScenes(getScript("STORY-FIGHT-GOBLIN-VICTORY"), font, goblinBackground2, getArray(new Mutation[]{new Mutation(saveService, SaveEnum.EXPERIENCE, 2)}), 
								getTextScenes(getScript("STORY-FIGHT-GOBLIN-VICTORY2"), font, background,  
								getEndScene(EndScene.Type.ENCOUNTER_OVER))
							),
							getTextScenes(getScript("STORY-FIGHT-GOBLIN-DEFEAT"), font, background, getArray(new Mutation[]{analReceive}), AssetEnum.WEREWOLF_MUSIC.getMusic(), getEndScene(EndScene.Type.GAME_OVER))				
						)
					)
				);		
				break;
			case OGRE_WARNING_STORY:
				getTextScenes(
					getScript("OGRE-WARN"), font, background, new Array<Mutation>(), AssetEnum.TRAINER_MUSIC.getMusic(), 
					getEndScene(EndScene.Type.ENCOUNTER_OVER)						
				);
				break;
			case OGRE_STORY:
				Background ogreBackground = new BackgroundBuilder(backgroundTexture).setDialogBox(assetManager.get(AssetEnum.BATTLE_HOVER.getTexture())).setForeground(assetManager.get(AssetEnum.GAME_OGRE.getTexture())).build();
				getTextScenes(
					getScript("STORY-OGRE"), font, background, new Array<Mutation>(), AssetEnum.WEREWOLF_MUSIC.getMusic(), getArray(new AssetDescriptor[]{null, null, null, null, AssetEnum.OGRE_GROWL.getSound()}),
					getChoiceScene(
						"Continue on?", getArray(new String[]{"Press On", "Turn back"}), 
						getTextScenes(
							getScript("STORY-OGRE-DEFEAT"), font, background, new Array<Mutation>(), AssetEnum.HEAVY_MUSIC.getMusic(), getArray(new AssetDescriptor[]{null, null, null, AssetEnum.OGRE_GROWL.getSound(), null, null, null, null, null, AssetEnum.OGRE_GROWL.getSound(), null, null, AssetEnum.OGRE_GROWL.getSound()}),
							getTextScenes(
								getScript("STORY-OGRE-AFTER"), font, ogreBackground,
								getEndScene(EndScene.Type.GAME_OVER)	
							)
						),
						getEndScene(EndScene.Type.ENCOUNTER_OVER)		
					)
				);
				break;
			case ECCENTRIC_MERCHANT:
				getTextScenes(
					getScript("STORY-MERCHANT"), font, background,
					getEndScene(EndScene.Type.ENCOUNTER_OVER)	
				);
				break;
			case STORY_FEM:
				Background backgroundWithAdventurer2 = new BackgroundBuilder(assetManager.get(AssetEnum.DEFAULT_BACKGROUND.getTexture())).setDialogBox(assetManager.get(AssetEnum.BATTLE_HOVER.getTexture())).setForeground(assetManager.get(AssetEnum.ADVENTURER.getTexture())).build();
				getTextScenes(
					getScript("STORY-FEM"), font, backgroundWithAdventurer2, new Array<Mutation>(), AssetEnum.GADGETEER_MUSIC.getMusic(),
					getEndScene(EndScene.Type.ENCOUNTER_OVER)	
				);
				break;
			case STORY_SIGN:
				getTextScenes(
					getScript("CROSSROADS"), font, background,
					getEndScene(EndScene.Type.ENCOUNTER_OVER)	 
				);
				break;
			case WEST_PASS:
				getTextScenes(
					getScript("WEST-PASS"), font, background,
					getEndScene(EndScene.Type.ENCOUNTER_OVER)	 
				);
				break;
			case SOUTH_PASS:
				getTextScenes(
					getScript("SOUTH-PASS"), font, background,
					getEndScene(EndScene.Type.ENCOUNTER_OVER)	 
				);
				break;
			case SHOP:
				Background backgroundWithShopkeep2 = new BackgroundBuilder(assetManager.get(AssetEnum.TOWN_BG. getTexture())).setDialogBox(assetManager.get(AssetEnum.BATTLE_HOVER.getTexture())).setForeground(assetManager.get(AssetEnum.SHOPKEEP.getTexture())).build();
				getTextScenes (					
					getArray(new String[]{"You peruse the shop."}), font, backgroundWithShopkeep2, new Array<Mutation>(), AssetEnum.SHOP_MUSIC.getMusic(),
					getShopScene(
						ShopCode.SHOP, new BackgroundBuilder(assetManager.get(AssetEnum.TOWN_BG.getTexture())).setForeground(assetManager.get(AssetEnum.SHOPKEEP.getTexture())).build(), 
						getEndScene(EndScene.Type.ENCOUNTER_OVER)	
					)
				);
				break;
			case WEAPON_SHOP:
				Background backgroundWithBlacksmith = new BackgroundBuilder(assetManager.get(AssetEnum.TOWN_BG.getTexture())).setDialogBox(assetManager.get(AssetEnum.BATTLE_HOVER.getTexture())).setForeground(assetManager.get(AssetEnum.TRAINER.getTexture())).build();
				getTextScenes (					
					getArray(new String[]{"You peruse the shop."}), font, backgroundWithBlacksmith, new Array<Mutation>(), AssetEnum.SHOP_MUSIC.getMusic(),	
					getShopScene(
						ShopCode.WEAPON_SHOP, new BackgroundBuilder(assetManager.get(AssetEnum.TOWN_BG.getTexture())).setForeground(assetManager.get(AssetEnum.TRAINER.getTexture())).build(), 
						getEndScene(EndScene.Type.ENCOUNTER_OVER)	
					)
				);
				break;
			case CAMP_AND_EAT:
				getTextScenes (					
					getScript("FORCED_CAMP"), font, background, getArray(new Mutation[]{new Mutation(saveService, SaveEnum.HEALTH, 10)}), AssetEnum.SHOP_MUSIC.getMusic(), 
					getEndScene(EndScene.Type.ENCOUNTER_OVER)	
				);
				break;
			case STARVATION:
				Background buttBangedBackground = new BackgroundBuilder(assetManager.get(AssetEnum.DEFAULT_BACKGROUND.getTexture())).setDialogBox(assetManager.get(AssetEnum.BATTLE_HOVER.getTexture())).setForeground(new AnimatedActor("animation/SplurtGO.atlas", "animation/SplurtGO.json"), 555, 520).build();
				
				getTextScenes (					
					getScript("STARVATION"), font, background, new Array<Mutation>(), AssetEnum.WEREWOLF_MUSIC.getMusic(), 
					getTextScenes(getScript("STARVATION-REVEAL"), font, buttBangedBackground, 
						getCheckScene(
							CheckType.VIRGIN,
							getTextScenes(getScript("STARVATION-VIRGIN"), font, buttBangedBackground, getArray(new Mutation[]{analReceive}),
								getTextScenes(getScript("STARVATION-CONTINUE"), font, buttBangedBackground,	
								getEndScene(EndScene.Type.GAME_OVER))
							),
							getTextScenes(getScript("STARVATION-CONTINUE"), font, buttBangedBackground, getEndScene(EndScene.Type.GAME_OVER))
						)
					)
				);
				break;
			default:
				getTextScenes(
					getScript("TOWN"), font, new BackgroundBuilder(assetManager.get(AssetEnum.TRAP_BONUS.getTexture())).setDialogBox(assetManager.get(AssetEnum.BATTLE_HOVER.getTexture())).build(),
					getEndScene(EndScene.Type.ENCOUNTER_OVER)				
				);		
				break;
		}
		return new Encounter(scenes, endScenes, battleScenes, getStartScene(scenes, sceneCode));	
	}

	private Array<Mutation> getMutation(int damage) {
		Array<Mutation> mutations = new Array<Mutation>();
		mutations.add(new Mutation(saveService, SaveEnum.HEALTH, -damage));
		return mutations;
	}
	
	private OrderedMap<Integer, Scene> addScene(Scene scene) { return addScene(new Array<Scene>(new Scene[]{scene})); }
	// pass in one or multiple scenes that the next scene will branch into
	private OrderedMap<Integer, Scene> addScene(Array<Scene> scenes) {
		OrderedMap<Integer, Scene> sceneMap = new OrderedMap<Integer, Scene>();
		for (Scene scene : scenes) {
			this.scenes.add(scene);
			if (scene instanceof BattleScene) battleScenes.add((BattleScene)scene);
			if (scene instanceof EndScene) endScenes.add((EndScene)scene);
			sceneMap.put(sceneCounter++, scene);
		}
		return sceneMap;
	}
	
	/* Scene type getters - these should all wrap themselves in addScene - look for anywhere they aren't currently to confirm*/
	
	private OrderedMap<Integer, Scene> getTextScenes(String[] script, BitmapFont font, Background background, OrderedMap<Integer, Scene> sceneMap) { return getTextScenes(new Array<String>(true, script, 0, script.length), font, background, sceneMap); }	
	private OrderedMap<Integer, Scene> getTextScenes(Array<String> script, BitmapFont font, Background background, OrderedMap<Integer, Scene> sceneMap) { return getTextScenes(script, font, background, new Array<Mutation>(), sceneMap); }
	// pass in a list of script lines in chronological order, this will reverse their order and add them to the stack
	private OrderedMap<Integer, Scene> getTextScenes(Array<String> script, BitmapFont font, Background background, Array<Mutation> mutations, OrderedMap<Integer, Scene> sceneMap) { return getTextScenes(script, font, background, mutations, null, new Array<AssetDescriptor<Sound>>(), sceneMap); }
	private OrderedMap<Integer, Scene> getTextScenes(Array<String> script, BitmapFont font, Background background, Array<Mutation> mutations, AssetDescriptor<Music> music, OrderedMap<Integer, Scene> sceneMap) { return getTextScenes(script, font, background, mutations, music, new Array<AssetDescriptor<Sound>>(), sceneMap); }
	private OrderedMap<Integer, Scene> getTextScenes(Array<String> script, BitmapFont font, Background background, Array<Mutation> mutations, AssetDescriptor<Music> music, Array<AssetDescriptor<Sound>> sounds, OrderedMap<Integer, Scene> sceneMap) {
		mutations.reverse();
		script.reverse();
		sounds.reverse();
		
		int soundIndex = -(script.size - sounds.size);
		int ii = 1;
		String characterName = character.getCharacterName();
		String buttsize = character.getBootyLiciousness();
		String lipsize = character.getLipFullness();
		for (String scriptLine: script) {
			scriptLine = scriptLine.replace("<NAME>", characterName).replace("<BUTTSIZE>", buttsize).replace("<LIPSIZE>", lipsize);
			sceneMap = addScene(new TextScene(sceneMap, sceneCounter, assetManager, font, saveService, background.clone(), scriptLine, ii == script.size ? mutations : null, character, ii == script.size ? music : null, soundIndex >= 0 ? sounds.get(soundIndex) : null));
			soundIndex++;
			ii++;
		}	
		return sceneMap;
	}
	
	private OrderedMap<Integer, Scene> getChoiceScene(String choiceDialogue, Array<String> buttonLabels, @SuppressWarnings("unchecked") OrderedMap<Integer, Scene>... sceneMaps) {
		return getChoiceScene(choiceDialogue, buttonLabels, new Array<ChoiceCheckType>(), sceneMaps);
	}
	private OrderedMap<Integer, Scene> getChoiceScene(String choiceDialogue, Array<String> buttonLabels, Array<ChoiceCheckType> checks, @SuppressWarnings("unchecked") OrderedMap<Integer, Scene>... sceneMaps) {
		OrderedMap<Integer, Scene> sceneMap = aggregateMaps(sceneMaps);
		
		// use sceneMap to generate the table
		Table table = new Table();

		Skin skin = assetManager.get(AssetEnum.UI_SKIN.getSkin());
		Sound buttonSound = assetManager.get(AssetEnum.BUTTON_SOUND.getSound());
		Texture background = assetManager.get(AssetEnum.DEFAULT_BACKGROUND.getTexture());
		
		ChoiceScene choiceScene = new ChoiceScene(sceneMap, sceneCounter, saveService, font, choiceDialogue, table, new BackgroundBuilder(background).build());
		int ii = 0;
		for (String label  : buttonLabels) {
			TextButton button = new TextButton(label, skin);
			if (ii < checks.size && checks.get(ii) != null) {
				button.addListener(getListener(choiceScene, sceneMap.get(sceneMap.orderedKeys().get(ii)), buttonSound, checks.get(ii), button));
			}
			else {
				button.addListener(getListener(choiceScene, sceneMap.get(sceneMap.orderedKeys().get(ii)), buttonSound));
			}
			
			table.add(button).size(650, 150).row();
			ii++;
		}
				
		return addScene(choiceScene);	
	}
	
	private OrderedMap<Integer, Scene> getShopScene(ShopCode shopCode, Background background, OrderedMap<Integer, Scene> sceneMap) {
		return addScene(new ShopScene(sceneMap, sceneCounter, saveService, assetManager, character, background, shopCode, shops.get(shopCode.toString())));
	}
	
	private ClickListener getListener(final AbstractChoiceScene currentScene, final Scene nextScene, final Sound buttonSound) {
		return new ClickListener() {
	        @Override
	        public void clicked(InputEvent event, float x, float y) {
	        	buttonSound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
	        	// set new Scene as active based on choice
	        	nextScene.setActive();
	        	currentScene.finish();
	        }
	    };
	}
	
	protected enum ChoiceCheckType {
		LEWD,
		GOLD_GREATER_THAN_10,
		GOLD_LESS_THAN_10
	}
	
	private ClickListener getListener(final AbstractChoiceScene currentScene, final Scene nextScene, final Sound buttonSound, final ChoiceCheckType type, final TextButton button) {
		return new ClickListener() {
	        @Override
	        public void clicked(InputEvent event, float x, float y) {
	        	if (isValidChoice(type)) {
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
	
	private OrderedMap<Integer, Scene> getCheckScene(Perk perk, IntArray checkValues, @SuppressWarnings("unchecked") OrderedMap<Integer, Scene>... sceneMaps) {
		OrderedMap<Integer, Scene> sceneMap = aggregateMaps(sceneMaps);
		Texture background = assetManager.get(AssetEnum.DEFAULT_BACKGROUND.getTexture());
		OrderedMap<Integer, Scene> checkValueMap = new OrderedMap<Integer, Scene>();
		int ii = 0;
		for (; ii < checkValues.size; ii++) {
			checkValueMap.put(checkValues.get(ii), sceneMap.get(sceneMap.orderedKeys().get(ii)));
		}
		CheckScene checkScene = new CheckScene(sceneMap, sceneCounter, assetManager, saveService, font, new BackgroundBuilder(background).build(), perk, checkValueMap, sceneMap.get(sceneMap.orderedKeys().get(ii)), character);
		return addScene(checkScene);
	}
	
	// accepts a list of values, will map those values to scenes in the scenemap in order
	private OrderedMap<Integer, Scene> getCheckScene(Stat stat, IntArray checkValues, @SuppressWarnings("unchecked") OrderedMap<Integer, Scene>... sceneMaps) {
		OrderedMap<Integer, Scene> sceneMap = aggregateMaps(sceneMaps);
		Texture background = assetManager.get(AssetEnum.DEFAULT_BACKGROUND.getTexture());
		OrderedMap<Integer, Scene> checkValueMap = new OrderedMap<Integer, Scene>();
		int ii = 0;
		for (; ii < checkValues.size; ii++) {
			checkValueMap.put(checkValues.get(ii), sceneMap.get(sceneMap.orderedKeys().get(ii)));
		}
		CheckScene checkScene = new CheckScene(sceneMap, sceneCounter, assetManager, saveService, font, new BackgroundBuilder(background).build(), stat, checkValueMap, sceneMap.get(sceneMap.orderedKeys().get(ii)), character);
		return addScene(checkScene);
	}
	
	private OrderedMap<Integer, Scene> getCheckScene(CheckType checkType, @SuppressWarnings("unchecked") OrderedMap<Integer, Scene>... sceneMaps) {
		OrderedMap<Integer, Scene> sceneMap = aggregateMaps(sceneMaps);
		Texture background = assetManager.get(AssetEnum.DEFAULT_BACKGROUND.getTexture());
		CheckScene checkScene = new CheckScene(sceneMap, sceneCounter, assetManager, saveService, font, new BackgroundBuilder(background).build(), checkType, sceneMap.get(sceneMap.orderedKeys().get(0)), sceneMap.get(sceneMap.orderedKeys().get(1)), character);
		return addScene(checkScene);
	}
	
	private OrderedMap<Integer, Scene> getBattleScene(BattleCode battleCode, @SuppressWarnings("unchecked") OrderedMap<Integer, Scene>... sceneMaps) {
		return getBattleScene(battleCode, Stance.BALANCED, Stance.BALANCED, sceneMaps);
	}
	
	private OrderedMap<Integer, Scene> getBattleScene(BattleCode battleCode, Array<Outcome> outcomes, @SuppressWarnings("unchecked") OrderedMap<Integer, Scene>... sceneMaps) {
		return getBattleScene(battleCode, Stance.BALANCED, Stance.BALANCED, outcomes, sceneMaps);
	}
	
	private OrderedMap<Integer, Scene> getBattleScene(BattleCode battleCode, int climaxCounter, Array<Outcome> outcomes, @SuppressWarnings("unchecked") OrderedMap<Integer, Scene>... sceneMaps) {
		return getBattleScene(battleCode, Stance.BALANCED, Stance.BALANCED, false, climaxCounter, outcomes, sceneMaps);
	}
	
	private OrderedMap<Integer, Scene> getBattleScene(BattleCode battleCode, Stance playerStance, Stance enemyStance, @SuppressWarnings("unchecked") OrderedMap<Integer, Scene>... sceneMaps) {
		return getBattleScene(battleCode, playerStance, enemyStance, new Array<Outcome>(new Outcome[]{Outcome.VICTORY, Outcome.DEFEAT}), sceneMaps);
	}
	
	private OrderedMap<Integer, Scene> getBattleScene(BattleCode battleCode, Stance playerStance, Stance enemyStance, Array<Outcome> outcomes, @SuppressWarnings("unchecked") OrderedMap<Integer, Scene>... sceneMaps) {
		return getBattleScene(battleCode, playerStance, enemyStance, false, 0, outcomes, sceneMaps);
	}
	
	private OrderedMap<Integer, Scene> getBattleScene(BattleCode battleCode, Stance playerStance, Stance enemyStance, boolean disarm, int climaxCounter, Array<Outcome> outcomes, @SuppressWarnings("unchecked") OrderedMap<Integer, Scene>... sceneMaps) {
		OrderedMap<Integer, Scene> sceneMap = aggregateMaps(sceneMaps);
		ObjectMap<String, Integer> outcomeToScene = new ObjectMap<String, Integer>();
		for (int ii = 0; ii < outcomes.size; ii++) {
			outcomeToScene.put(outcomes.get(ii).toString(), sceneMap.get(sceneMap.orderedKeys().get(ii)).getCode());
		}
		
		return addScene(new BattleScene(aggregateMaps(sceneMaps), saveService, battleCode, playerStance, enemyStance, disarm, climaxCounter, outcomeToScene));
	}
	
	private OrderedMap<Integer, Scene> getGameTypeScene(Array<String> buttonLabels, @SuppressWarnings("unchecked") OrderedMap<Integer, Scene>... sceneMaps) {
		OrderedMap<Integer, Scene> sceneMap = aggregateMaps(sceneMaps);
		Skin skin = assetManager.get(AssetEnum.UI_SKIN.getSkin());
		Sound buttonSound = assetManager.get(AssetEnum.BUTTON_SOUND.getSound());
		Texture background = assetManager.get(AssetEnum.GAME_TYPE_BACKGROUND.getTexture());
		
		Array<TextButton> buttons = new Array<TextButton>();
		for (String label : buttonLabels) {
			buttons.add(new TextButton(label, skin));
		}
		
		GameTypeScene gameTypeScene = new GameTypeScene(sceneMap, sceneCounter, saveService, buttons, new BackgroundBuilder(background).build());
		int ii = 0;
		for (TextButton button : buttons) {
			button.addListener(getListener(gameTypeScene, sceneMap.get(sceneMap.orderedKeys().get(ii++)), buttonSound));
		}
				
		return addScene(gameTypeScene);
	}
	
	private OrderedMap<Integer, Scene> getCharacterCreationScene(BitmapFont font, Background background, boolean story, OrderedMap<Integer, Scene> sceneMap) {
		return addScene(new CharacterCreationScene(sceneMap, sceneCounter, saveService, background, assetManager, character, story));
	}
	
	private OrderedMap<Integer, Scene> getSkillSelectionScene(Background background, OrderedMap<Integer, Scene> sceneMap) {
		return addScene(new SkillSelectionScene(sceneMap, sceneCounter, saveService, background, assetManager, character));
	}
	
	private OrderedMap<Integer, Scene> getCharacterCustomizationScene(Background background, OrderedMap<Integer, Scene> sceneMap) {
		return addScene(new CharacterCustomizationScene(sceneMap, sceneCounter, saveService, font, background, assetManager, character));
	}
	
	private OrderedMap<Integer, Scene> getEndScene(EndScene.Type type) {
		return addScene(new EndScene(type, saveService, type == EndScene.Type.ENCOUNTER_OVER ? returnContext : SaveManager.GameContext.GAME_OVER));
	}
	
	private OrderedMap<Integer, Scene> aggregateMaps(@SuppressWarnings("unchecked") OrderedMap<Integer, Scene>... sceneMaps) {
		OrderedMap<Integer, Scene> aggregatedMap = new OrderedMap<Integer, Scene>();
		for (OrderedMap<Integer, Scene> map : sceneMaps) {
			aggregatedMap.putAll(map);
		}
		return aggregatedMap;	
	}
	
	private Array<String> getScript(EncounterCode encounterCode, String string) {
		return getScript("0"+ (encounterCode.getBattleCode() < 10 ? "0" + encounterCode.getBattleCode() : encounterCode.getBattleCode()) + "-" + string);
	}
	
	private Array<String> getScript(EncounterCode encounterCode, int scene) {
		return getScript("0"+ (encounterCode.getBattleCode() < 10 ? "0" + encounterCode.getBattleCode() : encounterCode.getBattleCode()) + "-" + ( scene >= 10 ? scene : "0" + scene));
	}
	private Array<String> getScript(String code) {
		return getArray(reader.loadScript(code));
	}
	
	private Array<ChoiceCheckType> getArray(ChoiceCheckType[] array) { return new Array<ChoiceCheckType>(array); }
	private Array<String> getArray(String[] array) { return new Array<String>(array); }
	private Array<Mutation> getArray(Mutation[] array) { return new Array<Mutation>(array); }
	private Array<AssetDescriptor<Sound>> getArray(AssetDescriptor<Sound>[] AssetDescriptors) {	return new Array<AssetDescriptor<Sound>>(AssetDescriptors); }
	
	private Scene getStartScene(Array<Scene> scenes, Integer sceneCode) {
		// default case	
		if (sceneCode == 0) {
			saveService.saveDataValue(SaveEnum.MUSIC, AssetEnum.ENCOUNTER_MUSIC.getPath());
			// returns the final scene and plays in reverse order
			return scenes.get(scenes.size - 1);
		}
		for (Scene objScene: scenes) {
			if (objScene.getCode() == sceneCode) {
				return objScene;
			}
		}
		return null;
	}
}


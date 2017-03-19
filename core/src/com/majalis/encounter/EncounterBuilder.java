package com.majalis.encounter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
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
import com.majalis.battle.BattleFactory.EnemyEnum;
import com.majalis.character.EnemyCharacter;
import com.majalis.character.Perk;
import com.majalis.character.PlayerCharacter;
import com.majalis.character.PlayerCharacter.QuestFlag;
import com.majalis.character.PlayerCharacter.QuestType;
import com.majalis.character.Techniques;
import com.majalis.encounter.Background.BackgroundBuilder;
import com.majalis.character.SexualExperience.SexualExperienceBuilder;
import com.majalis.character.AbstractCharacter.Stance;
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
	private int battleCode;
	private final ObjectMap<String, Shop> shops;
	private final PlayerCharacter character;
	private final GameContext returnContext;
	// can probably be replaced with a call to scenes.size
	private int sceneCounter;
	
	protected EncounterBuilder(EncounterReader reader, AssetManager assetManager, SaveService saveService, BitmapFont font, BitmapFont smallFont, int sceneCode, int battleCode, ObjectMap<String, Shop> shops, PlayerCharacter character, GameContext returnContext) {
		scenes = new Array<Scene>();
		endScenes = new Array<EndScene>();
		battleScenes = new Array<BattleScene>();
		this.reader = reader;
		this.assetManager = assetManager;
		this.saveService = saveService;
		this.font = font;
		this.smallFont = smallFont;
		this.sceneCode = sceneCode;
		this.battleCode = battleCode;
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
		Background silhouetteBackground = new BackgroundBuilder(assetManager.get(AssetEnum.BURNING_FORT_BG.getPath(), Texture.class)).setDialogBox(assetManager.get(AssetEnum.BATTLE_HOVER.getPath(), Texture.class)).setForeground(assetManager.get(AssetEnum.SILHOUETTE.getPath(), Texture.class), 1000, 0).build();
		
		getTextScenes(
			getScript("INTRO"), font, background,
			getGameTypeScene(
				getArray(new String[]{"Create Character", "Story (Patrons)"}),		
				getTextScenes(
					getArray(new String[]{"You've selected to create your character!", "Please choose your class."}), font, background, getArray(new Mutation[]{new Mutation(saveService, SaveEnum.MODE, GameMode.SKIRMISH)}), AssetEnum.INITIAL_MUSIC.getPath(), new Array<String>(),
					getCharacterCreationScene(
						smallFont, classSelectbackground.clone(), false,
						getSkillSelectionScene(
							new BackgroundBuilder(assetManager.get(AssetEnum.SKILL_SELECTION_BACKGROUND.getPath(), Texture.class)).build(), 
							getCharacterCustomizationScene(
								new BackgroundBuilder(assetManager.get(AssetEnum.CHARACTER_CUSTOM_BACKGROUND.getPath(), Texture.class)).build(), 
								getEndScene(EndScene.Type.ENCOUNTER_OVER)
							)
						)
					)
				),
				getTextScenes(
					getArray(new String[]{"You have entered story mode.", "A tale of androgyny has begun..."}), font, background, getArray(new Mutation[]{new Mutation(saveService, SaveEnum.MODE, GameMode.STORY)}),							
					getTextScenes(
						// needs to be female silhouette from behind BG
						getScript("STORY-000"), font, silhouetteBackground, new Array<Mutation>(), AssetEnum.WAVES.getPath(), getArray(new String[]{null, null, null, null, null, null, null, null, null, AssetEnum.SMUG_LAUGH.getPath(), null, null, null, null, null, null, null, null, AssetEnum.SMUG_LAUGH.getPath()}),
						getTextScenes(
							// needs to be hovel BG
							getScript("STORY-001"), font, background, new Array<Mutation>(), AssetEnum.HOVEL_MUSIC.getPath(), new Array<String>(),
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
	
	private Background getDefaultTextBackground() { return getDefaultTextBackground(assetManager.get(AssetEnum.DEFAULT_BACKGROUND.getPath(), Texture.class)); }
	
	private Background getDefaultTextBackground(Texture background) { return new BackgroundBuilder(background).setDialogBox(assetManager.get(AssetEnum.BATTLE_HOVER.getPath(), Texture.class)).build(); }
	
	private Background getClassSelectBackground() { return new BackgroundBuilder(assetManager.get(AssetEnum.CLASS_SELECT_BACKGROUND.getPath(), Texture.class)).build(); }
	
	protected Encounter getLevelUpEncounter(boolean storyMode) {
		
		if (storyMode) {
			getTextScenes(
				getArray(new String[]{"You have no skills to select!"}), font, getDefaultTextBackground(), 
				getEndScene(EndScene.Type.ENCOUNTER_OVER)
			);
		}
		else {
			getSkillSelectionScene(
				new BackgroundBuilder(assetManager.get(AssetEnum.SKILL_SELECTION_BACKGROUND.getPath(), Texture.class)).build(), getEndScene(EndScene.Type.ENCOUNTER_OVER)
			);
		}
		return new Encounter(scenes, endScenes, new Array<BattleScene>(), getStartScene(scenes, sceneCode));
	}
	
	protected Encounter getDefaultEncounter() {
		Background background = getDefaultTextBackground(assetManager.get(AssetEnum.STICK_BACKGROUND.getPath(), Texture.class));
		getTextScenes(new String[]{"You encounter a stick!", "It's actually rather sexy looking.", "There is nothing left here to do."}, font, background, getEndScene(EndScene.Type.ENCOUNTER_OVER));
		return new Encounter(scenes, endScenes, new Array<BattleScene>(), getStartScene(scenes, sceneCode));
	}
	
	@SuppressWarnings("unchecked")
	protected Encounter getRandomEncounter(EncounterCode encounterCode) {
		Texture backgroundTexture = assetManager.get(AssetEnum.DEFAULT_BACKGROUND.getPath(), Texture.class);	
		Background background = getDefaultTextBackground();
		Mutation analReceive = new Mutation(saveService, SaveEnum.ANAL, new SexualExperienceBuilder().setAnalSex(1, 1, 0).build());
		Mutation goblinVirginityToFalse = new Mutation(saveService, SaveEnum.GOBLIN_VIRGIN, false);
		Array<Outcome> normalOutcomes = new Array<Outcome>(new Outcome[]{Outcome.VICTORY, Outcome.DEFEAT, Outcome.SATISFIED});
		
		// if there isn't already a battlecode set, it's determined by the encounterCode; for now, that means dividing the various encounters up by modulus
		if (battleCode == -1) battleCode = encounterCode.getBattleCode();
		switch (encounterCode) {
			case WERESLUT:
				Background werebitchBackground = new BackgroundBuilder(backgroundTexture).setDialogBox(assetManager.get(AssetEnum.BATTLE_HOVER.getPath(), Texture.class)).setForeground(assetManager.get(AssetEnum.WEREBITCH.getPath(), Texture.class)).build();
				getTextScenes(
					getScript(encounterCode, 0), font, background, getArray(new Mutation[]{new Mutation(saveService, ProfileEnum.KNOWLEDGE, EnemyEnum.WERESLUT.toString())}), AssetEnum.WEREWOLF_MUSIC.getPath(), new Array<String>(),
					getTextScenes( 
						getScript(encounterCode, 1), font, werebitchBackground, 
						getBattleScene(
							battleCode, new Array<Outcome>(new Outcome[]{Outcome.VICTORY, Outcome.KNOT, Outcome.DEFEAT, Outcome.SATISFIED}), 
							getTextScenes(getArray(new String[]{"You defeated the werebitch!", "You receive 2 Experience."}), font, background, getArray(new Mutation[]{new Mutation(saveService, SaveEnum.EXPERIENCE, 2)}), getEndScene(EndScene.Type.ENCOUNTER_OVER)),
							getTextScenes(getScript(encounterCode, 2), font, werebitchBackground, getArray(new Mutation[]{analReceive}), AssetEnum.WEREWOLF_MUSIC.getPath(), new Array<String>(), getEndScene(EndScene.Type.GAME_OVER)),
							getTextScenes(getScript(encounterCode, 3), font, werebitchBackground, getArray(new Mutation[]{}), AssetEnum.WEREWOLF_MUSIC.getPath(), new Array<String>(), getEndScene(EndScene.Type.ENCOUNTER_OVER)),
							getTextScenes(getScript(encounterCode, 4), font, werebitchBackground, getArray(new Mutation[]{}), AssetEnum.WEREWOLF_MUSIC.getPath(), new Array<String>(), getEndScene(EndScene.Type.ENCOUNTER_OVER))
						)
					)
				);		
				break;
			case HARPY:
				final AnimatedActor enemy = EnemyCharacter.getAnimatedActor(EnemyEnum.HARPY);
				
				Background harpyBackground = new BackgroundBuilder(backgroundTexture).setDialogBox(assetManager.get(AssetEnum.BATTLE_HOVER.getPath(), Texture.class)).setForeground(enemy, 0, 0).build();
				Background harpyFellatioBackground = new BackgroundBuilder(backgroundTexture).setDialogBox(assetManager.get(AssetEnum.BATTLE_HOVER.getPath(), Texture.class)).setForeground(assetManager.get(AssetEnum.HARPY_FELLATIO.getPath(), Texture.class)).build();
				
				OrderedMap<Integer, Scene> winFight = getTextScenes(getArray(new String[]{"You defeated the harpy!", "You receive 1 Experience."}), font, background, getArray(new Mutation[]{new Mutation(saveService, SaveEnum.EXPERIENCE, 1)}), getEndScene(EndScene.Type.ENCOUNTER_OVER));
				OrderedMap<Integer, Scene> loseFight = 
					getTextScenes(
						getScript(encounterCode, 4), font, harpyBackground, 
						getTextScenes(
							getScript(encounterCode, "04-A"), font, harpyBackground, getArray(new Mutation[]{new Mutation(saveService, SaveEnum.ANAL, new SexualExperienceBuilder().setAnalSex(1, 0, 0).build())}),
							getTextScenes(
								getScript(encounterCode, "04-B"), font, harpyBackground, getArray(new Mutation[]{new Mutation(saveService, SaveEnum.ANAL, new SexualExperienceBuilder().setAnalSex(0, 0, 1).build())}),
								getTextScenes(
									getScript(encounterCode, "04-C"), font, harpyBackground, getArray(new Mutation[]{new Mutation(saveService, SaveEnum.ANAL, new SexualExperienceBuilder().setAnalSex(0, 1, 0).build())}),
									getTextScenes(
										getScript(encounterCode, "04-D"), font, harpyBackground, getArray(new Mutation[]{new Mutation(saveService, SaveEnum.ANAL, new SexualExperienceBuilder().setAnalSex(10, 10, 0).build())}),
										getEndScene(EndScene.Type.GAME_OVER)
									)
								)
							)
						)
					);
						
				OrderedMap<Integer, Scene> satisfiedFight = getTextScenes(getScript(encounterCode, 5), font, harpyBackground, getArray(new Mutation[]{}), getEndScene(EndScene.Type.ENCOUNTER_OVER))	;
				
				getTextScenes(
					getScript(encounterCode, 0), font, background, getArray(new Mutation[]{new Mutation(saveService, ProfileEnum.KNOWLEDGE, EnemyEnum.HARPY.toString())}),
					getCheckScene(
						Stat.AGILITY, new IntArray(new int[]{6, 4}),  
						getTextScenes(
							getScript(encounterCode, 1), font, background, 
							getBattleScene(
								battleCode, Stance.BALANCED, Stance.PRONE, normalOutcomes,
								winFight,
								loseFight,
								satisfiedFight
							)
						),
						getTextScenes(
							getScript(encounterCode, 2), font, background, 
							getBattleScene(
								battleCode, Stance.KNEELING, Stance.BALANCED, normalOutcomes,
								winFight,
								loseFight,
								satisfiedFight
							)
						),
						getTextScenes(getScript(encounterCode, 3), font, harpyFellatioBackground, 
							getBattleScene(
								battleCode, Stance.FELLATIO_BOTTOM, Stance.FELLATIO, normalOutcomes,				
								winFight,
								loseFight,
								satisfiedFight
							)
						)
					)
				);		
				break;
			case SLIME:
				Background slimeBackground= new BackgroundBuilder(backgroundTexture).setDialogBox(assetManager.get(AssetEnum.BATTLE_HOVER.getPath(), Texture.class)).setForeground(assetManager.get(AssetEnum.SLIME.getPath(), Texture.class)).build();
				Background slimeDoggyBackground = new BackgroundBuilder(backgroundTexture).setDialogBox(assetManager.get(AssetEnum.BATTLE_HOVER.getPath(), Texture.class)).setForeground(assetManager.get(AssetEnum.SLIME_DOGGY.getPath(), Texture.class)).build();
				getTextScenes(
					getScript(encounterCode, 0), font, slimeBackground, getArray(new Mutation[]{new Mutation(saveService, ProfileEnum.KNOWLEDGE, EnemyEnum.SLIME.toString())}),
					getChoiceScene(
						"What do you do with the slime?", getArray(new String[]{"Fight Her", "Smooch Her", "Leave Her Be"}),
						getBattleScene(
							battleCode,
							getTextScenes(
								getScript(encounterCode, 1), font, slimeBackground, 
								getChoiceScene(
									"Slay the slime?",
									getArray(new String[]{"Stab the core", "Spare her"}),
									getTextScenes(
										getScript(encounterCode, 2), font, slimeBackground,
										getCheckScene(
											Stat.AGILITY,
											new IntArray(new int[]{6}),
											getTextScenes(getScript(encounterCode, 3), font, slimeBackground,
												getTextScenes(getArray(new String[]{"You slew the slime!", "You receive 3 Experience."}), font, background, getArray(new Mutation[]{new Mutation(saveService, SaveEnum.EXPERIENCE, 3)}), getEndScene(EndScene.Type.ENCOUNTER_OVER))
											),
											getTextScenes(getScript(encounterCode, 4), font, slimeBackground, getEndScene(EndScene.Type.GAME_OVER))		
										)		
									),
									getTextScenes(getScript(encounterCode, 5), font, slimeBackground,
										getTextScenes(getArray(new String[]{"You spared the slime!", "You receive 3 Experience."}), font, background, getArray(new Mutation[]{new Mutation(saveService, SaveEnum.EXPERIENCE, 3)}), getEndScene(EndScene.Type.ENCOUNTER_OVER))
									)
								)
							),
							getTextScenes(
								getScript(encounterCode, 6),
								font, slimeBackground, 
								getChoiceScene(
									"What do you do?",
									getArray(new String[]{"Try to speak", "Run!"}),
									getTextScenes(
											getScript(encounterCode, 7), font, slimeBackground,
											getTextScenes(
												getArray(new String[]{"You are stuck for a long while.  You eat 10 food.", "You recover 15 health.", "You receive 2 Experience for the... experience."}), font, background, getArray(new Mutation[]{new Mutation(saveService, SaveEnum.FOOD, -10), new Mutation(saveService, SaveEnum.HEALTH, 15), new Mutation(saveService, SaveEnum.EXPERIENCE, 2)}),
												getEndScene(EndScene.Type.ENCOUNTER_OVER))
									),
									getCheckScene(
										Stat.AGILITY,
										new IntArray(new int[]{5}),
										getTextScenes(getScript(encounterCode, 8), font, background, getEndScene(EndScene.Type.ENCOUNTER_OVER)),
										getTextScenes(
											getScript(encounterCode, 9), font, background, 
											getTextScenes(
												getArray(new String[]{"You are stuck for a long while.  You eat 10 food.", "You recover 15 health.", "You receive 2 Experience for the... experience."}), font, background, getArray(new Mutation[]{new Mutation(saveService, SaveEnum.FOOD, -10), new Mutation(saveService, SaveEnum.HEALTH, 15), new Mutation(saveService, SaveEnum.EXPERIENCE, 2)}),
												getEndScene(EndScene.Type.ENCOUNTER_OVER)
											)
										)
									)
								)
							)								
						), 
						getTextScenes(
							getScript(encounterCode, 10), font, slimeBackground, 
							getChoiceScene(
								"Do you enter the slime, or...?",
								getArray(new String[]{"Go In", "Love Dart (Requires: Catamite)"}),
								getArray(new ChoiceCheckType[]{null, ChoiceCheckType.LEWD}),
								getTextScenes(getScript(encounterCode, 11), font, slimeBackground,
									getTextScenes(getArray(new String[]{"You banged the slime!", "You receive 2 Experience."}), font, background, getArray(new Mutation[]{new Mutation(saveService, SaveEnum.EXPERIENCE, 2)}), getEndScene(EndScene.Type.ENCOUNTER_OVER))
								),
								getTextScenes(getScript(encounterCode, 12), font, slimeBackground, 
									getTextScenes(getScript(encounterCode, 13), font, slimeDoggyBackground, getArray(new Mutation[]{analReceive}),
										getTextScenes(getArray(new String[]{"You got banged by the slime!", "You receive 3 Experience."}), font, background, getArray(new Mutation[]{new Mutation(saveService, SaveEnum.EXPERIENCE, 3)}), getEndScene(EndScene.Type.ENCOUNTER_OVER))
									)
								)
							)
						),
						getEndScene(EndScene.Type.ENCOUNTER_OVER)
					)
				);
				break;
			case BRIGAND:
				Background brigandBackground = new BackgroundBuilder(backgroundTexture).setDialogBox(assetManager.get(AssetEnum.BATTLE_HOVER.getPath(), Texture.class)).setForeground(assetManager.get(AssetEnum.BRIGAND_ORAL.getPath(), Texture.class)).build();
				
				OrderedMap<Integer, Scene> winFight2 = getTextScenes(getArray(new String[]{"You defeated the brigand!", "You receive 1 Experience."}), font, background, getArray(new Mutation[]{new Mutation(saveService, SaveEnum.EXPERIENCE, 1)}), getEndScene(EndScene.Type.ENCOUNTER_OVER));
				OrderedMap<Integer, Scene> loseFight2 = getTextScenes(getScript(encounterCode, 13), font, background, 
					getTextScenes(
						getScript(encounterCode, 14), font, brigandBackground, getArray(new Mutation[]{new Mutation(saveService, SaveEnum.ANAL, new SexualExperienceBuilder().setOralSex(1).build())}), 
							getTextScenes(
								getScript(encounterCode, 15), font, brigandBackground, getArray(new Mutation[]{new Mutation(saveService, SaveEnum.ANAL, new SexualExperienceBuilder().setOralCreampie(2).build())}), 
								getTextScenes(
									getArray(new String[]{"You rest, eating 5 food.", "You recover 10 health."}), font, background, getArray(new Mutation[]{new Mutation(saveService, SaveEnum.FOOD, -5), new Mutation(saveService, SaveEnum.HEALTH, 10)}),
									getEndScene(EndScene.Type.ENCOUNTER_OVER)
								)
							)
						)
					);
				OrderedMap<Integer, Scene> satisfiedFight2 = getTextScenes(getScript(encounterCode, 16), font, background, getArray(new Mutation[]{}), getEndScene(EndScene.Type.ENCOUNTER_OVER));
				
				getTextScenes(
					getScript(encounterCode, 0), font, background, getArray(new Mutation[]{new Mutation(saveService, ProfileEnum.KNOWLEDGE, EnemyEnum.BRIGAND.toString())}),
					getCheckScene(
						Stat.PERCEPTION, new IntArray(new int[]{6, 4}),
						getTextScenes(
							getScript(encounterCode, 1), font, background, 
							getChoiceScene(
								"How do you handle the brigand?", getArray(new String[]{"Charge", "Ready an Arrow", "Speak"}),
								getBattleScene(battleCode, Stance.OFFENSIVE, Stance.BALANCED, normalOutcomes,
									winFight2,
									loseFight2,
									satisfiedFight2
								),
								getBattleScene(
									battleCode, normalOutcomes,
									winFight2,
									loseFight2,
									satisfiedFight2
								),
								getTextScenes(
									getScript(encounterCode, 2), font, background,
									getChoiceScene(
										"Accept her offer?", getArray(new String[]{"Accept (Requires: Catamite)", "Decline"}), getArray(new ChoiceCheckType[]{ChoiceCheckType.LEWD, null}),
										getTextScenes(
											getScript(encounterCode, 3), font, background, getArray(new Mutation[]{analReceive}),
											getChoiceScene(
												"Tell her to pull out?", getArray(new String[]{"Say Nothing", "Ask her"}),
												getTextScenes(
													getScript(encounterCode, 4), font, background,
													getTextScenes(
														getArray(new String[]{"You are flooded with cum and Experience!", "You receive 3 Experience."}), font, background, getArray(new Mutation[]{new Mutation(saveService, SaveEnum.EXPERIENCE, 3)}), getEndScene(EndScene.Type.ENCOUNTER_OVER)
													)
												),
												getTextScenes(
													getScript(encounterCode, 5), font, background,
													getCheckScene(
														Stat.CHARISMA, new IntArray(new int[]{4}),
														getTextScenes(
															getScript(encounterCode, 6), font, background,
															getTextScenes(
																getArray(new String[]{"Your face is covered with cum... and Experience!", "You receive 2 Experience."}), font, background, getArray(new Mutation[]{new Mutation(saveService, SaveEnum.EXPERIENCE, 2)}), getEndScene(EndScene.Type.ENCOUNTER_OVER)
															)
														),
														getTextScenes(
															getScript(encounterCode, 7), font, background,
															getTextScenes(
																getArray(new String[]{"Your stomach is full of cum... and Experience!", "You receive 2 Experience."}), font, background, getArray(new Mutation[]{new Mutation(saveService, SaveEnum.EXPERIENCE, 2)}), getEndScene(EndScene.Type.ENCOUNTER_OVER)
															)
														)													
													)
												)
											)
										),
										getTextScenes(
											getScript(encounterCode, 8), font, background,
											getCheckScene(
												Stat.CHARISMA, new IntArray(new int[]{5}), 
												getTextScenes(
													getScript(encounterCode, 9), font, background,
													getEndScene(EndScene.Type.ENCOUNTER_OVER)
												),
												getTextScenes(
													getScript(encounterCode, 10), font, background,
													getBattleScene(
														battleCode, normalOutcomes,
														winFight2,
														loseFight2,
														satisfiedFight2
													)
												)		
											)
										)
									)
								)
							)
						),
						getTextScenes(
							getArray(new String[]{"Ouch!  You take 5 damage!"}), font, background, getMutation(5),
							getTextScenes(
								getScript(encounterCode, 11), font, background, 
								getBattleScene(
									battleCode, normalOutcomes,
									winFight2,
									loseFight2,
									satisfiedFight2
								)
							)
						),
						getTextScenes(
							getArray(new String[]{"Ouch!  You take 5 damage!"}), font, background, getMutation(5),
							getTextScenes(
								getScript(encounterCode, 12), font, background, getArray(new Mutation[]{new Mutation(saveService, SaveEnum.ANAL, new SexualExperienceBuilder(1).build())}),
								getBattleScene(
									battleCode, Stance.STANDING_BOTTOM, Stance.STANDING, normalOutcomes,
									winFight2,
									loseFight2,
									satisfiedFight2
								)
							)
						)
					)
				);	
				break;
			case DRYAD:
				background = new BackgroundBuilder(backgroundTexture).setDialogBox(assetManager.get(AssetEnum.BATTLE_HOVER.getPath(), Texture.class)).setForeground(assetManager.get(AssetEnum.DRYAD_BACKGROUND.getPath(), Texture.class)).build();
				getTextScenes(
					getScript(encounterCode, 0), font, background, new Array<Mutation>(), AssetEnum.SHOP_MUSIC.getPath(), new Array<String>(),
					getChoiceScene(
						"Do you offer her YOUR apple, or try to convince her to just hand it over?", getArray(new String[]{"Offer (Requires: Catamite)", "Plead with her"}), getArray(new ChoiceCheckType[]{ChoiceCheckType.LEWD, null}),												
						getTextScenes(
							getScript(encounterCode, 1), font, background, getArray(new Mutation[]{analReceive}),
							getTextScenes(
								getArray(new String[]{"So that happened.", "You take 5 damage from the splinters.", "You receive 10 food from the dryad.", "You receive 1 Experience."}), font, background, getArray(new Mutation[]{analReceive, new Mutation(saveService, SaveEnum.HEALTH, -5), new Mutation(saveService, SaveEnum.FOOD, 10), new Mutation(saveService, SaveEnum.EXPERIENCE, 1)}), 
								getEndScene(EndScene.Type.ENCOUNTER_OVER)
							)
						),
						getCheckScene(
							Stat.CHARISMA, new IntArray(new int[]{5}),
							getTextScenes(
								getScript(encounterCode, 2), font, background,
								getTextScenes(
									getArray(new String[]{"You receive 10 food from the dryad.", "You receive 1 Experience."}),
									font,
									background,
									getArray(new Mutation[]{new Mutation(saveService, SaveEnum.FOOD, 10), new Mutation(saveService, SaveEnum.EXPERIENCE, 1)}),
									getEndScene(EndScene.Type.ENCOUNTER_OVER)
								)
							),
							getTextScenes(
								getScript(encounterCode, 3), font, background,
								getEndScene(EndScene.Type.ENCOUNTER_OVER)
							)
						)			
					)
				);
				break;
			case CENTAUR:
				final AnimatedActor enemy2 = EnemyCharacter.getAnimatedActor(EnemyEnum.CENTAUR);
				final AnimatedActor enemy3 = EnemyCharacter.getAnimatedActor(EnemyEnum.UNICORN);
				Background centaurBackground = new BackgroundBuilder(backgroundTexture).setDialogBox(assetManager.get(AssetEnum.BATTLE_HOVER.getPath(), Texture.class)).setForeground(enemy2, 0, 0).build();
				Background unicornBackground = new BackgroundBuilder(backgroundTexture).setDialogBox(assetManager.get(AssetEnum.BATTLE_HOVER.getPath(), Texture.class)).setForeground(enemy3, 0, 0).build();
				OrderedMap<Integer, Scene> satisfy = 
						getTextScenes(getScript(encounterCode, 6), font, centaurBackground, getArray(new Mutation[]{new Mutation(saveService, SaveEnum.ANAL, new SexualExperienceBuilder().setHorse().build())}),
							getTextScenes(getScript(encounterCode, 7), font, centaurBackground, getArray(new Mutation[]{new Mutation(saveService, SaveEnum.ITEM, null)}), getEndScene(EndScene.Type.ENCOUNTER_OVER)));
						
				OrderedMap<Integer, Scene> catamite = getTextScenes(
					getScript(encounterCode, 3), font, centaurBackground, getArray(new Mutation[]{analReceive}),
					getBattleScene(
						battleCode, Stance.DOGGY_BOTTOM, Stance.DOGGY, normalOutcomes,
						getTextScenes(getArray(new String[]{"You defeated the centaur!", "You receive 2 Experience."}), font, background, getArray(new Mutation[]{new Mutation(saveService, SaveEnum.EXPERIENCE, 2)}), getEndScene(EndScene.Type.ENCOUNTER_OVER)),
						getTextScenes(getScript(encounterCode, 5), font, centaurBackground, getEndScene(EndScene.Type.GAME_OVER)),
						satisfy
					)
				);
				
				getTextScenes(
					getScript(encounterCode, 0), font, background, new Array<Mutation>(), AssetEnum.SHOP_MUSIC.getPath(), new Array<String>(),
					getCheckScene(
						CheckType.VIRGIN,
						getTextScenes(
							getScript(encounterCode, 1), font, unicornBackground, getArray(new Mutation[]{new Mutation(saveService, ProfileEnum.KNOWLEDGE, EnemyEnum.UNICORN.toString())}),
							getBattleScene(
								battleCode + 1000, 
								getTextScenes(getArray(new String[]{"You defeated the unicorn!", "You receive 3 Experience."}), font, background, getArray(new Mutation[]{new Mutation(saveService, SaveEnum.EXPERIENCE, 3)}), getEndScene(EndScene.Type.ENCOUNTER_OVER)),
								getTextScenes(
									getScript(encounterCode, 4), font, unicornBackground,
									getTextScenes(
										getScript(encounterCode, "04-A"), font, unicornBackground, getArray(new Mutation[]{new Mutation(saveService, SaveEnum.ANAL, new SexualExperienceBuilder().setAnalSex(1, 1, 1).setHorse().build())}),
										getTextScenes(
											getScript(encounterCode, "04-B"), font, unicornBackground, getArray(new Mutation[]{new Mutation(saveService, SaveEnum.ANAL, new SexualExperienceBuilder().setAnalSex(0, 1, 0).setHorse().build())}),
											getTextScenes(
												getArray(new String[]{"You rest, eating 5 food.", "You recover 10 health."}), font, background, getArray(new Mutation[]{new Mutation(saveService, SaveEnum.FOOD, -5), new Mutation(saveService, SaveEnum.HEALTH, 10)}),
												getEndScene(EndScene.Type.ENCOUNTER_OVER)
											)
										)
									)
								)
							)
						),
						getTextScenes(
							getScript(encounterCode, 2), font, centaurBackground, getArray(new Mutation[]{new Mutation(saveService, ProfileEnum.KNOWLEDGE, EnemyEnum.CENTAUR.toString())}),
							getCheckScene(
								Perk.ANAL_LOVER, new IntArray(new int[]{3}),
								catamite,
								getChoiceScene(
									"Fight the centaur?", getArray(new String[]{"Fight Her", "Decline", "Ask for It (Requires: Catamite)"}), getArray(new ChoiceCheckType[]{null, null, ChoiceCheckType.LEWD}),
									getBattleScene(
										battleCode, normalOutcomes,
										getTextScenes(getArray(new String[]{"You defeated the centaur!", "You are now welcome to sleep in their camp, and receive 10 food.", "You receive 2 Experience."}), font, background, getArray(new Mutation[]{new Mutation(saveService, SaveEnum.EXPERIENCE, 2), new Mutation(saveService, SaveEnum.FOOD, 10)}), getEndScene(EndScene.Type.ENCOUNTER_OVER)),
										getTextScenes(getScript(encounterCode, 5), font, centaurBackground, getEndScene(EndScene.Type.GAME_OVER)),
										satisfy
									),
									getEndScene(EndScene.Type.ENCOUNTER_OVER),
									catamite
								)
							)
						)
					)
				);		
				break;	
			case GOBLIN:
				Background goblinBackground = new BackgroundBuilder(backgroundTexture).setDialogBox(assetManager.get(AssetEnum.BATTLE_HOVER.getPath(), Texture.class)).setForeground(assetManager.get(AssetEnum.GOBLIN.getPath(), Texture.class)).build();
				Background buttBangedBackground2 = new BackgroundBuilder(assetManager.get(AssetEnum.DEFAULT_BACKGROUND.getPath(), Texture.class)).setDialogBox(assetManager.get(AssetEnum.BATTLE_HOVER.getPath(), Texture.class)).setForeground(new AnimatedActor("animation/SplurtGO.atlas", "animation/SplurtGO.json"), 555, 520).build();
				
				OrderedMap<Integer, Scene> fightOff = 
					getTextScenes(
						getScript(encounterCode, 37), font, background,
						getEndScene(EndScene.Type.ENCOUNTER_OVER)
				);		
				
				OrderedMap<Integer, Scene> postVirginityCheck = getChoiceScene(
					"Mouth, or ass?", getArray(new String[]{"In The Mouth", "Up The Ass"}),
					getTextScenes(
						getScript(encounterCode, "MOUTH-01"), font, goblinBackground, 
						getTextScenes(
							getScript(encounterCode, "MOUTH-02"), font, goblinBackground, getArray(new Mutation[]{goblinVirginityToFalse}),
							getTextScenes(
								getScript(encounterCode, "MOUTH-03"), font, goblinBackground, getArray(new Mutation[]{new Mutation(saveService, SaveEnum.ANAL, new SexualExperienceBuilder().setOralSex(1).build())}),
								getTextScenes(
									getScript(encounterCode, "MOUTH-04"), font, goblinBackground, getArray(new Mutation[]{new Mutation(saveService, SaveEnum.ANAL, new SexualExperienceBuilder().setOralCreampie(1).build())}),
									getEndScene(EndScene.Type.ENCOUNTER_OVER)
								)
							)
						)
					),	
					getTextScenes(
						getScript(encounterCode, "ASS-01"), font, goblinBackground, getArray(new Mutation[]{new Mutation(saveService, SaveEnum.ANAL, new SexualExperienceBuilder().setOralSex(1).build())}),
						getTextScenes(
							getScript(encounterCode, "ASS-02"), font, goblinBackground, getArray(new Mutation[]{goblinVirginityToFalse}),
							getTextScenes(
								getScript(encounterCode, "ASS-03"), font, goblinBackground,  getArray(new Mutation[]{new Mutation(saveService, SaveEnum.ANAL, new SexualExperienceBuilder().setAnalEjaculations(1).build())}),
								getTextScenes(
									getScript(encounterCode, "ASS-04"), font, goblinBackground,  getArray(new Mutation[]{new Mutation(saveService, SaveEnum.ANAL, new SexualExperienceBuilder().setAnalSex(0, 1, 0).build())}),
									getCheckScene(
										Stat.ENDURANCE, new IntArray(new int[]{3}),
										fightOff,
										getTextScenes(										
											getScript(encounterCode, 33), font, goblinBackground, getArray(new Mutation[]{new Mutation(saveService, SaveEnum.ANAL, new SexualExperienceBuilder().setAnalSex(1, 0, 0).build())}),
											getTextScenes(										
												getScript(encounterCode, "33-A"), font, goblinBackground, getArray(new Mutation[]{new Mutation(saveService, SaveEnum.ANAL, new SexualExperienceBuilder().setAnalSex(0, 1, 0).build())}),
												getCheckScene(
													Stat.ENDURANCE, new IntArray(new int[]{2}),
													fightOff,
													getTextScenes(
														getScript(encounterCode, 34), font, goblinBackground,  getArray(new Mutation[]{new Mutation(saveService, SaveEnum.ANAL, new SexualExperienceBuilder().setAnalSex(1, 0, 0).build())}),
														getTextScenes(
															getScript(encounterCode, "34-A"), font, goblinBackground, getArray(new Mutation[]{new Mutation(saveService, SaveEnum.ANAL, new SexualExperienceBuilder().setAnalSex(0, 1, 0).build())}),
															getCheckScene(
																Stat.ENDURANCE, new IntArray(new int[]{1}),
																fightOff,
																getTextScenes(
																	getScript(encounterCode, 35), font, goblinBackground, getArray(new Mutation[]{new Mutation(saveService, SaveEnum.ANAL, new SexualExperienceBuilder().setAnalSex(1, 0, 0).build())}),
																	getTextScenes(
																		getScript(encounterCode, "35-A"), font, goblinBackground, getArray(new Mutation[]{new Mutation(saveService, SaveEnum.ANAL, new SexualExperienceBuilder().setAnalSex(0, 1, 0).build())}),										
																		getTextScenes(
																			getScript(encounterCode, 36), font, buttBangedBackground2,	
																			getEndScene(EndScene.Type.ENCOUNTER_OVER)
																		)
																	)
																)
															)
														)
													)
												)
											)
										)
									)
								)
							)
						)	
					)
				);
				
				OrderedMap<Integer, Scene> defeatScene = getTextScenes(getScript(encounterCode, 28), font, goblinBackground,
					getCheckScene(
						CheckType.GOBLIN_VIRGIN,
						getTextScenes(getScript(encounterCode, 29), font, goblinBackground, postVirginityCheck),
						getTextScenes(getScript(encounterCode, 30), font, goblinBackground, postVirginityCheck)
					)						
				);		
				
				OrderedMap<Integer, Scene> battleScene = getBattleScene(battleCode, 
					getTextScenes(getArray(new String[]{"You defeated the goblin!", "You receive 1 Experience."}), font, background, getArray(new Mutation[]{new Mutation(saveService, SaveEnum.EXPERIENCE, 1)}), getEndScene(EndScene.Type.ENCOUNTER_OVER)),
					defeatScene
				);
				
				OrderedMap<Integer, Scene> battleSceneDisarm = getBattleScene(battleCode, Stance.BALANCED, Stance.BALANCED, true, 0, new Array<Outcome>(new Outcome[]{Outcome.VICTORY, Outcome.DEFEAT}),
						getTextScenes(getArray(new String[]{"You defeated the goblin!", "You receive 1 Experience."}), font, background, getArray(new Mutation[]{new Mutation(saveService, SaveEnum.EXPERIENCE, 1)}), getEndScene(EndScene.Type.ENCOUNTER_OVER)),
						defeatScene
					);
				
				OrderedMap<Integer, Scene> pantsCutDown = getTextScenes(
					getScript(encounterCode, 20), font, goblinBackground,
					getBattleScene(battleCode, Stance.DOGGY_BOTTOM, Stance.DOGGY,
						getTextScenes(getArray(new String[]{"You defeated the goblin!", "You receive 1 Experience."}), font, background, getArray(new Mutation[]{new Mutation(saveService, SaveEnum.EXPERIENCE, 1)}), getEndScene(EndScene.Type.ENCOUNTER_OVER)),
						defeatScene
					)
				);
				
				OrderedMap<Integer, Scene> cutPantsScene = getTextScenes(
					getScript(encounterCode, 9), font, goblinBackground, getArray(new Mutation[]{new Mutation(saveService, ProfileEnum.KNOWLEDGE, EnemyEnum.GOBLIN.toString())}),
					getChoiceScene(
						"Quick, what do you do?", getArray(new String[]{"Catch Her (5 AGI)", "Trip Her (4 AGI)", "Disarm Her (3 AGI)", "Avoid Her (2 AGI)"}),
						getCheckScene(
							Stat.AGILITY, new IntArray(new int[]{5}),
							getTextScenes(
								getScript(encounterCode, 10), font, goblinBackground,
								getChoiceScene(
									"What do you do with her?", getArray(new String[]{"Put Her Down", "Turn Her Over Your Knee"}),	
									getTextScenes(
										getScript(encounterCode, 11), font, goblinBackground, getArray(new Mutation[]{new Mutation(saveService, ProfileEnum.KNOWLEDGE, EnemyEnum.GOBLIN.toString())}),
										getChoiceScene(
											"Accept Her Offer?", getArray(new String[]{"Accept", "Decline"}),
											getTextScenes(
												getScript(encounterCode, 12), font, goblinBackground,
												getEndScene(EndScene.Type.ENCOUNTER_OVER)
											),
											getTextScenes(
												getScript(encounterCode, 13), font, goblinBackground,
												getEndScene(EndScene.Type.ENCOUNTER_OVER)
											)
										)
									),
									getTextScenes(
										getScript(encounterCode, 14), font, goblinBackground,
										getCheckScene(
											 Stat.STRENGTH, new IntArray(new int[]{5}),
											getTextScenes(
												getScript(encounterCode, 15), font, goblinBackground,
												getEndScene(EndScene.Type.ENCOUNTER_OVER)
											),
											getTextScenes(
												getScript(encounterCode, 16), font, goblinBackground, getMutation(5),
												getEndScene(EndScene.Type.ENCOUNTER_OVER)
											)
										)
									)	
								)														
							),
							pantsCutDown
						),
						getCheckScene(
							Stat.AGILITY, new IntArray(new int[]{4}),
							getTextScenes(
								getScript(encounterCode, 17), font, goblinBackground, getArray(new Mutation[]{new Mutation(saveService, ProfileEnum.KNOWLEDGE, EnemyEnum.GOBLIN.toString())}),
								getChoiceScene(
									"What do you do?", getArray(new String[]{"Attack", "Run"}),
									getBattleScene(battleCode, Stance.OFFENSIVE, Stance.PRONE,
										getTextScenes(getArray(new String[]{"You defeated the goblin!", "You receive 1 Experience."}), font, background, getArray(new Mutation[]{new Mutation(saveService, SaveEnum.EXPERIENCE, 1)}), getEndScene(EndScene.Type.ENCOUNTER_OVER)),
										defeatScene
									),
									getTextScenes(
										getArray(new String[]{"While she struggles to her feet, you flee."}), font, background, 
										getEndScene(EndScene.Type.ENCOUNTER_OVER)
									)
								)
							),
							pantsCutDown
						),
						getCheckScene(
							Stat.AGILITY, new IntArray(new int[]{3}), 
							getTextScenes(
								getScript(encounterCode, 18), font, goblinBackground, getArray(new Mutation[]{new Mutation(saveService, ProfileEnum.KNOWLEDGE, EnemyEnum.GOBLIN.toString())}),
								getChoiceScene(
									"What do you do?", getArray(new String[]{"Attack Her", "Block Her", "Let Her Go"}),  // probably should be battle, convo, end encounter
									getBattleScene(battleCode, Stance.OFFENSIVE, Stance.BALANCED,
										getTextScenes(getArray(new String[]{"You defeated the goblin!", "You receive 1 Experience."}), font, background, getArray(new Mutation[]{new Mutation(saveService, SaveEnum.EXPERIENCE, 1)}), getEndScene(EndScene.Type.ENCOUNTER_OVER)),
										defeatScene
									),
									battleSceneDisarm,
									getTextScenes(
										getArray(new String[]{"You let her flee, and proceed."}), font, background, 
										getEndScene(EndScene.Type.ENCOUNTER_OVER)
									)
								)
							),
							pantsCutDown
						),
						getCheckScene(
							Stat.AGILITY, new IntArray(new int[]{2}),
							getTextScenes(
								getScript(encounterCode, 19), font, goblinBackground, getArray(new Mutation[]{new Mutation(saveService, ProfileEnum.KNOWLEDGE, EnemyEnum.GOBLIN.toString())}),
								getChoiceScene(
									"What do you do?", getArray(new String[]{"Attack Her", "Block Her", "Let Her Go"}),  // probably should be battle, convo, end encounter
									getBattleScene(battleCode, Stance.OFFENSIVE, Stance.BALANCED,
										getTextScenes(getArray(new String[]{"You defeated the goblin!", "You receive 1 Experience."}), font, background, getArray(new Mutation[]{new Mutation(saveService, SaveEnum.EXPERIENCE, 1)}), getEndScene(EndScene.Type.ENCOUNTER_OVER)),
										defeatScene
									),
									battleScene,
									getTextScenes(
										getArray(new String[]{"You let her flee, and proceed."}), font, background, 
										getEndScene(EndScene.Type.ENCOUNTER_OVER)
									)
								)
							),
							pantsCutDown
						)
					)	
				);
				
				getTextScenes(
					getScript(encounterCode, 0), font, background, new Array<Mutation>(), AssetEnum.WEREWOLF_MUSIC.getPath(), new Array<String>(),
					getChoiceScene(
						"What path do you follow?", getArray(new String[]{"Pass By", "Enter the Small Path"}),
						getTextScenes(
							getScript(encounterCode, 1), font, background,
							getEndScene(EndScene.Type.ENCOUNTER_OVER)
						),
						getTextScenes(
							getScript(encounterCode, 2), font, background, getArray(new Mutation[]{new Mutation(saveService, ProfileEnum.KNOWLEDGE, EnemyEnum.GOBLIN.toString())}), null, getArray(new String[]{ null, null, null, null, AssetEnum.LOUD_LAUGH.getPath()}),
							getCheckScene(
								Stat.PERCEPTION, new IntArray(new int[]{7, 4}),
								getTextScenes(
									getScript(encounterCode, 3), font, goblinBackground,
									getCheckScene(
										Stat.AGILITY, new IntArray(new int[]{5, 3}),
										getTextScenes(
											getScript(encounterCode, 4), font, goblinBackground,
											getCheckScene(
												Stat.STRENGTH, new IntArray(new int[]{5}),
												getTextScenes(
													getScript(encounterCode, 5), font, goblinBackground,
													cutPantsScene
												),
												getTextScenes(
													getScript(encounterCode, 6), font, goblinBackground,
													cutPantsScene
												)
											)
										),
										getTextScenes(
											getScript(encounterCode, 7), font, background,
											cutPantsScene
										),
										getTextScenes(
											getScript(encounterCode, 8), font, background, getMutation(5),
											cutPantsScene
										)
									)
								),
								getTextScenes( // noticed
									getScript(encounterCode, 21), font, background,
									getCheckScene(
										Stat.AGILITY, new IntArray(new int[]{7, 5}),
										getTextScenes(
											getScript(encounterCode, 4), font, goblinBackground,
											getCheckScene(
												Stat.STRENGTH, new IntArray(new int[]{5}),
												getTextScenes(
													getScript(encounterCode, 5), font, goblinBackground,
													cutPantsScene
												),
												getTextScenes(
													getScript(encounterCode, 6), font, goblinBackground,
													cutPantsScene
												)
											)
										),
										getTextScenes(
											getScript(encounterCode, 7), font, background,
											cutPantsScene
										),
										getTextScenes(
											getScript(encounterCode, 8), font, background, getMutation(5),
											cutPantsScene
										)
									)
								),
								getTextScenes( // failed to notice
									getScript(encounterCode, 22), font, background,
									getCheckScene(
										Stat.AGILITY, new IntArray(new int[]{5}),
										getTextScenes(
											getScript(encounterCode, 23), font, background,
											getCheckScene(
												Stat.AGILITY, new IntArray(new int[]{7, 5}),
												getTextScenes(
													getScript(encounterCode, 4), font, goblinBackground,
													getCheckScene(
														Stat.STRENGTH, new IntArray(new int[]{5}),
														getTextScenes(
															getScript(encounterCode, 5), font, goblinBackground,
															cutPantsScene
														),
														getTextScenes(
															getScript(encounterCode, 6), font, goblinBackground,
															cutPantsScene
														)
													)
												),
												getTextScenes(
													getScript(encounterCode, 7), font, background,
													cutPantsScene
												),
												getTextScenes(
													getScript(encounterCode, 8), font, background, getMutation(5),
													cutPantsScene
												)
											)
										),
										getCheckScene(
											Stat.ENDURANCE, new IntArray(new int[]{7}),
											getTextScenes(
												getScript(encounterCode, 24), font, background,
												getEndScene(EndScene.Type.ENCOUNTER_OVER)
											),
											getTextScenes(
												getScript(encounterCode, 25), font, background, getArray(new Mutation[]{goblinVirginityToFalse}),
												getTextScenes(
													getScript(encounterCode, 26), font, buttBangedBackground2,
													getTextScenes(
														getScript(encounterCode, 27), font, background,
														getEndScene(EndScene.Type.ENCOUNTER_OVER)
													)
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
			case ORC:
				Texture orc = assetManager.get(AssetEnum.ORC.getPath(), Texture.class);
				Background backgroundWithOrc = new BackgroundBuilder(assetManager.get(AssetEnum.DEFAULT_BACKGROUND.getPath(), Texture.class)).setDialogBox(assetManager.get(AssetEnum.BATTLE_HOVER.getPath(), Texture.class)).setForeground(orc).build();
				Background backgroundWithOrcZoom = new BackgroundBuilder(assetManager.get(AssetEnum.DEFAULT_BACKGROUND.getPath(), Texture.class)).setDialogBox(assetManager.get(AssetEnum.BATTLE_HOVER.getPath(), Texture.class)).setForeground(new TextureRegion(orc, 500, 700, 700, 700)).build();
				Background backgroundWithOrcZoomUp = new BackgroundBuilder(assetManager.get(AssetEnum.DEFAULT_BACKGROUND.getPath(), Texture.class)).setDialogBox(assetManager.get(AssetEnum.BATTLE_HOVER.getPath(), Texture.class)).setForeground(new TextureRegion(orc, 500, 200, 700, 700)).build();
				Background backgroundWithOrcZoomDown = new BackgroundBuilder(assetManager.get(AssetEnum.DEFAULT_BACKGROUND.getPath(), Texture.class)).setDialogBox(assetManager.get(AssetEnum.BATTLE_HOVER.getPath(), Texture.class)).setForeground(new TextureRegion(orc, 500, 1270, 700, 700)).build();
				
				Background gapeBackground = new BackgroundBuilder(assetManager.get(AssetEnum.GAPE.getPath(), Texture.class)).setDialogBox(assetManager.get(AssetEnum.BATTLE_HOVER.getPath(), Texture.class)).build();
				
				OrderedMap<Integer, Scene> leaveOrc = 
					getTextScenes(
						getScript(encounterCode, 23), font, backgroundWithOrc, getArray(new Mutation[]{new Mutation(saveService, SaveEnum.QUEST, new QuestFlag(QuestType.ORC, 2))}),
						getTextScenes(
							getScript(encounterCode, "23A"), font, background,
							getTextScenes(
								getScript(encounterCode, 24), font, gapeBackground, 
								getEndScene(EndScene.Type.ENCOUNTER_OVER)
							)
						)
					);
				
				OrderedMap<Integer, Scene> battleVictory = 
					getTextScenes(
						getScript(encounterCode, 8), font, backgroundWithOrc, 
						getChoiceScene(
							"Front, back, or decline?", getArray(new String[]{"Front (Requires: Catamite)", "Back", "Decline"}), getArray(new ChoiceCheckType[]{ChoiceCheckType.LEWD, null, null}),
							getTextScenes(
								getScript(encounterCode, 9), font, backgroundWithOrc, getArray(new Mutation[]{new Mutation(saveService, SaveEnum.ANAL, new SexualExperienceBuilder().setAnalSex(1, 1, 1).build())}),
								getEndScene(EndScene.Type.ENCOUNTER_OVER)
							),
							getTextScenes(
								getScript(encounterCode, 10), font, backgroundWithOrc, 
								getEndScene(EndScene.Type.ENCOUNTER_OVER)
							),
							getTextScenes(
								getScript(encounterCode, 11), font, backgroundWithOrc, 
								getEndScene(EndScene.Type.ENCOUNTER_OVER)
							)
						)
					);
				
				OrderedMap<Integer, Scene> oralScene = 
					getTextScenes(
						getScript(encounterCode, 14), font, backgroundWithOrc, getArray(new Mutation[]{new Mutation(saveService, SaveEnum.ANAL, new SexualExperienceBuilder().setOralSex(1).setOralCreampie(1).build())}),
						getTextScenes(
							getScript(encounterCode, 15), font, backgroundWithOrc, 
							getEndScene(EndScene.Type.ENCOUNTER_OVER)
						)
					);
					
				OrderedMap<Integer, Scene> failCharismaScene = 
					getTextScenes(
						getScript(encounterCode, 18), font, backgroundWithOrc, 
						oralScene
					);
				
				getTextScenes(
					getScript(encounterCode, 0), font, background, getArray(new Mutation[]{new Mutation(saveService, ProfileEnum.KNOWLEDGE, EnemyEnum.ORC.toString())}), AssetEnum.WEREWOLF_MUSIC.getPath(), new Array<String>(),	// intro filler
					getCheckScene(
						CheckType.ORC_ENCOUNTERED,		
						getTextScenes(
							getScript(encounterCode, 1), font, background, getArray(new Mutation[]{new Mutation(saveService, SaveEnum.QUEST, new QuestFlag(QuestType.ORC, 1))}), // before you spot the orc
							getChoiceScene(
								"Do you speak up?", getArray(new String[]{"Speak up", "Remain silent"}),
								getTextScenes(
									getScript(encounterCode, 2), font, background, 
									getTextScenes(
										getScript(encounterCode, 3), font, backgroundWithOrcZoomUp, 
										getTextScenes(
											getScript(encounterCode, 4), font, backgroundWithOrcZoom, 
											getTextScenes(
												getScript(encounterCode, 5), font, backgroundWithOrcZoomDown, 
												getTextScenes(
													getScript(encounterCode, 6), font, backgroundWithOrc,
													getChoiceScene(
														"How do you respond?", getArray(new String[]{"Attack", "Remain still"}),
														getBattleScene(
															battleCode, 4, normalOutcomes,
															getTextScenes(
																getScript(encounterCode, 7), font, backgroundWithOrc, // different from if you beat her in the second encounter
																battleVictory
															),
															getTextScenes(
																getScript(encounterCode, 12), font, backgroundWithOrc, // options to finish off the orc
																getChoiceScene(
																	"What do you offer?", getArray(new String[]{"Anal (Requires: Catamite)", "Oral", "Nasal", "Facial (4 CHA)", "Penal (6 CHA)"}), getArray(new ChoiceCheckType[]{ChoiceCheckType.LEWD, null, null, null, null}),
																	getTextScenes(
																		getScript(encounterCode, 9), font, backgroundWithOrc, getArray(new Mutation[]{new Mutation(saveService, SaveEnum.ANAL, new SexualExperienceBuilder().setAnalSex(1, 1, 1).build())}),
																		getEndScene(EndScene.Type.ENCOUNTER_OVER)
																	),
																	getTextScenes(
																		getScript(encounterCode, 13), font, backgroundWithOrc, 
																		oralScene
																	),
																	getTextScenes(
																		getScript(encounterCode, 16), font, backgroundWithOrc, 
																		getEndScene(EndScene.Type.ENCOUNTER_OVER)
																	),
																	getCheckScene(
																		Stat.CHARISMA, new IntArray(new int[]{4}), 
																		getTextScenes(
																			getScript(encounterCode, 17), font, backgroundWithOrc, 
																			getEndScene(EndScene.Type.ENCOUNTER_OVER)
																		),
																		failCharismaScene
																	),
																	getCheckScene(
																		Stat.CHARISMA, new IntArray(new int[]{6}), 
																		getTextScenes(
																			getScript(encounterCode, 19), font, backgroundWithOrc, 
																			getEndScene(EndScene.Type.ENCOUNTER_OVER)
																		),
																		failCharismaScene
																	)
																)
															),
															getTextScenes(
																getScript(encounterCode, 20), font, backgroundWithOrc, // orc is satisfied
																getEndScene(EndScene.Type.ENCOUNTER_OVER)
															)
														),		
														getTextScenes(
															getScript(encounterCode, 21), font, backgroundWithOrc, // she puts it back in
															leaveOrc
														)
													)
												)
											)
										)
									)
								),	
								getTextScenes( // remain silent
									getScript(encounterCode, 22), font, backgroundWithOrc, 	// she goes about her business
									leaveOrc
								)
							)
						),
						getCheckScene(
							CheckType.ORC_COWARD,	
							getTextScenes(
								getScript(encounterCode, 25), font, backgroundWithOrc,
								getChoiceScene(
									"Accept her invitation?", getArray(new String[]{"Accept (Requires: Catamite)", "Decline"}), getArray(new ChoiceCheckType[]{ChoiceCheckType.LEWD, null}),
									getTextScenes(
										getScript(encounterCode, 26), font, backgroundWithOrc, getArray(new Mutation[]{new Mutation(saveService, SaveEnum.FOOD, 15), new Mutation(saveService, SaveEnum.ANAL, new SexualExperienceBuilder().setAnalSex(3, 3, 2).build())}),
										getEndScene(EndScene.Type.ENCOUNTER_OVER)
									),
									getTextScenes(
										getScript(encounterCode, 27), font, backgroundWithOrc, getArray(new Mutation[]{new Mutation(saveService, SaveEnum.FOOD, 5)}),
										getEndScene(EndScene.Type.ENCOUNTER_OVER)
									)
								)
							),
							getTextScenes(
								getScript(encounterCode, 28), font, backgroundWithOrc,
								getChoiceScene(
									"Well?", getArray(new String[]{"Yes (Requires: Catamite)", "No"}), getArray(new ChoiceCheckType[]{ChoiceCheckType.LEWD, null, null}),
									getTextScenes(
										getScript(encounterCode, 29), font, backgroundWithOrc, getArray(new Mutation[]{new Mutation(saveService, SaveEnum.QUEST, new QuestFlag(QuestType.ORC, 1)), new Mutation(saveService, SaveEnum.ANAL, new SexualExperienceBuilder().setAnalSex(1, 1, 1).build())}),
										getEndScene(EndScene.Type.ENCOUNTER_OVER)
									),
									getTextScenes(
										getScript(encounterCode, 30), font, backgroundWithOrc,
										getBattleScene(
											battleCode, normalOutcomes,
											getTextScenes(
												getScript(encounterCode, 31), font, backgroundWithOrc, getArray(new Mutation[]{new Mutation(saveService, SaveEnum.QUEST, new QuestFlag(QuestType.ORC, 1))}),
												battleVictory
											),
											getTextScenes(
												getScript(encounterCode, 32), font, backgroundWithOrc, 
												getChoiceScene(
													"Man or woman?", getArray(new String[]{"Man", "Woman"}),
													getTextScenes(
														getScript(encounterCode, 33), font, backgroundWithOrc,
														getEndScene(EndScene.Type.GAME_OVER)
													),
													getTextScenes(
														getScript(encounterCode, 34), font, backgroundWithOrc, getArray(new Mutation[]{new Mutation(saveService, SaveEnum.ANAL, new SexualExperienceBuilder().setAnalSex(1, 1, 5).build())}),
														getEndScene(EndScene.Type.GAME_OVER)
													)
												)
											),
											getTextScenes(
												getScript(encounterCode, 35), font, backgroundWithOrc, 
												getEndScene(EndScene.Type.GAME_OVER)
											)
										)
									)
								)	
							)
						) 
					)
				);
				break;
			case ADVENTURER:
				Background backgroundWithAdventurer = new BackgroundBuilder(assetManager.get(AssetEnum.DEFAULT_BACKGROUND.getPath(), Texture.class)).setDialogBox(assetManager.get(AssetEnum.BATTLE_HOVER.getPath(), Texture.class)).setForeground(assetManager.get(AssetEnum.ADVENTURER.getPath(), Texture.class)).build();
				getTextScenes (
					getScript(encounterCode, 0), font, backgroundWithAdventurer, new Array<Mutation>(), AssetEnum.GADGETEER_MUSIC.getPath(), new Array<String>(),
					getBattleScene(
						battleCode, normalOutcomes,
						getTextScenes(
							getScript(encounterCode, 0), font, backgroundWithAdventurer, 
							getEndScene(EndScene.Type.ENCOUNTER_OVER)
						),
						getTextScenes(
							getScript(encounterCode, 0), font, backgroundWithAdventurer, 
							getEndScene(EndScene.Type.ENCOUNTER_OVER)
						),
						getTextScenes(
							getScript(encounterCode, 0), font, backgroundWithAdventurer, 
							getEndScene(EndScene.Type.ENCOUNTER_OVER)
						)
					)
				);
				break;
			case GADGETEER:
				Background backgroundWithGadgeteer = new BackgroundBuilder(assetManager.get(AssetEnum.DEFAULT_BACKGROUND.getPath(), Texture.class)).setDialogBox(assetManager.get(AssetEnum.BATTLE_HOVER.getPath(), Texture.class)).setForeground(assetManager.get(AssetEnum.GADGETEER.getPath(), Texture.class)).build();
				Background shopGadgeteer = new BackgroundBuilder(assetManager.get(AssetEnum.DEFAULT_BACKGROUND.getPath(), Texture.class)).setForeground(assetManager.get(AssetEnum.GADGETEER.getPath(), Texture.class), 900, 0).build();
				Mutation[] dryAnal = new Mutation[]{new Mutation(saveService, SaveEnum.ANAL, new SexualExperienceBuilder(1).setAnalEjaculations(1).build())};
				OrderedMap<Integer, Scene> noScene = getTextScenes(
					getScript("GADGETEER-NO"), font, backgroundWithGadgeteer,
					getEndScene(EndScene.Type.ENCOUNTER_OVER)
				);
				getTextScenes (
					getScript("GADGETEER-00"), font, backgroundWithGadgeteer, new Array<Mutation>(), AssetEnum.GADGETEER_MUSIC.getPath(), new Array<String>(),
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
				Background innkeeper = new BackgroundBuilder(assetManager.get(AssetEnum.DEFAULT_BACKGROUND.getPath(), Texture.class)).setDialogBox(assetManager.get(AssetEnum.BATTLE_HOVER.getPath(), Texture.class)).setForeground(assetManager.get(AssetEnum.INNKEEPER.getPath(), Texture.class)).build();
				Background backgroundKeyhole = new BackgroundBuilder(assetManager.get(AssetEnum.DEFAULT_BACKGROUND.getPath(), Texture.class)).setDialogBox(assetManager.get(AssetEnum.BATTLE_HOVER.getPath(), Texture.class)).setForeground(assetManager.get(AssetEnum.KEYHOLE.getPath(), Texture.class)).build();
				Background backgroundKeyholeGO = new BackgroundBuilder(assetManager.get(AssetEnum.DEFAULT_BACKGROUND.getPath(), Texture.class)).setDialogBox(assetManager.get(AssetEnum.BATTLE_HOVER.getPath(), Texture.class)).setForeground(assetManager.get(AssetEnum.GAME_OVER_KEYHOLE.getPath(), Texture.class)).build();
				
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
				background = new BackgroundBuilder(assetManager.get(AssetEnum.CABIN_BACKGROUND.getPath(), Texture.class)).setDialogBox(assetManager.get(AssetEnum.BATTLE_HOVER.getPath(), Texture.class)).build();
				Background trainerBackground = new BackgroundBuilder(assetManager.get(AssetEnum.CABIN_BACKGROUND.getPath(), Texture.class)).setDialogBox(assetManager.get(AssetEnum.BATTLE_HOVER.getPath(), Texture.class)).setForeground(assetManager.get(AssetEnum.TRAINER.getPath(), Texture.class)).build();
				getTextScenes(
					getScript("STORY-003"), font, background, new Array<Mutation>(), AssetEnum.TRAINER_MUSIC.getPath(), new Array<String>(),
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
				background = new BackgroundBuilder(assetManager.get(AssetEnum.CABIN_BACKGROUND.getPath(), Texture.class)).setDialogBox(assetManager.get(AssetEnum.BATTLE_HOVER.getPath(), Texture.class)).build();
				getTextScenes(
					getScript("STORY-004"), font, background, new Array<Mutation>(), AssetEnum.TRAINER_MUSIC.getPath(), new Array<String>(),
					getEndScene(EndScene.Type.ENCOUNTER_OVER)						
				);
				break;
			case TOWN_STORY: 
				background = new BackgroundBuilder(assetManager.get(AssetEnum.TOWN_BG.getPath(), Texture.class)).setDialogBox(assetManager.get(AssetEnum.BATTLE_HOVER.getPath(), Texture.class)).build();
				Background backgroundWithShopkeep = new BackgroundBuilder(assetManager.get(AssetEnum.TOWN_BG.getPath(), Texture.class)).setDialogBox(assetManager.get(AssetEnum.BATTLE_HOVER.getPath(), Texture.class)).setForeground(assetManager.get(AssetEnum.SHOPKEEP.getPath(), Texture.class)).build();
				Background shopBackground = new BackgroundBuilder(assetManager.get(AssetEnum.TOWN_BG.getPath(), Texture.class)).setForeground(assetManager.get(AssetEnum.SHOPKEEP.getPath(), Texture.class)).build();
				getTextScenes(
					getScript("STORY-005"), font, background, new Array<Mutation>(),
					getTextScenes (					
						getScript("STORY-006"), font, backgroundWithShopkeep, new Array<Mutation>(), AssetEnum.SHOP_MUSIC.getPath(), getArray(new String[]{null, null, null, null, AssetEnum.SMUG_LAUGH.getPath()}),
						getShopScene(
							ShopCode.FIRST_STORY, shopBackground, 
							getTextScenes(					
								getScript("STORY-006A"), font, backgroundWithShopkeep,
								getCheckScene(Stat.CHARISMA, new IntArray(new int[]{6}),
									getTextScenes (
										getScript("STORY-006B"), font, backgroundWithShopkeep, new Array<Mutation>(), AssetEnum.SHOP_MUSIC.getPath(), getArray(new String[]{ AssetEnum.SMUG_LAUGH.getPath()}),
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
				background = new BackgroundBuilder(assetManager.get(AssetEnum.CABIN_BACKGROUND.getPath(), Texture.class)).setDialogBox(assetManager.get(AssetEnum.BATTLE_HOVER.getPath(), Texture.class)).build();
				Background witchBackground = new BackgroundBuilder(assetManager.get(AssetEnum.CABIN_BACKGROUND.getPath(), Texture.class)).setDialogBox(assetManager.get(AssetEnum.BATTLE_HOVER.getPath(), Texture.class)).setForeground(assetManager.get(AssetEnum.MERI_SILHOUETTE.getPath(), Texture.class)).build();
				getTextScenes (
					getScript("STORY-WITCH-COTTAGE"), font, background, new Array<Mutation>(new Mutation[]{new Mutation(saveService, SaveEnum.SKILL, Techniques.COMBAT_FIRE)}), AssetEnum.TRAINER_MUSIC.getPath(), new Array<String>(),
					getTextScenes (
						getScript("STORY-WITCH-COTTAGE-MERI"), font, witchBackground, new Array<Mutation>(new Mutation[]{new Mutation(saveService, SaveEnum.SKILL, Techniques.COMBAT_FIRE)}), AssetEnum.WEREWOLF_MUSIC.getPath(), new Array<String>(),
						getEndScene(EndScene.Type.ENCOUNTER_OVER)
					)
				);
				break;
			case MERI_COTTAGE_VISIT:
				background = new BackgroundBuilder(assetManager.get(AssetEnum.CABIN_BACKGROUND.getPath(), Texture.class)).setDialogBox(assetManager.get(AssetEnum.BATTLE_HOVER.getPath(), Texture.class)).build();
				getTextScenes(
					getScript("STORY-WITCH-COTTAGE-VISIT"), font, background, new Array<Mutation>(), AssetEnum.TRAINER_MUSIC.getPath(), new Array<String>(),
					getEndScene(EndScene.Type.ENCOUNTER_OVER)						
				);
				break;
			case FIRST_BATTLE_STORY:
				Background goblinBackground2 = new BackgroundBuilder(backgroundTexture).setDialogBox(assetManager.get(AssetEnum.BATTLE_HOVER.getPath(), Texture.class)).setForeground(assetManager.get(AssetEnum.GOBLIN.getPath(), Texture.class)).build();
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
							getTextScenes(getScript("STORY-FIGHT-GOBLIN-DEFEAT"), font, background, getArray(new Mutation[]{analReceive}), AssetEnum.WEREWOLF_MUSIC.getPath(), new Array<String>(), getEndScene(EndScene.Type.GAME_OVER))				
						)
					)
				);		
				break;
			case OGRE_WARNING_STORY:
				getTextScenes(
					getScript("OGRE-WARN"), font, background, new Array<Mutation>(), AssetEnum.TRAINER_MUSIC.getPath(), new Array<String>(),
					getEndScene(EndScene.Type.ENCOUNTER_OVER)						
				);
				break;
			case OGRE_STORY:
				Background ogreBackground = new BackgroundBuilder(backgroundTexture).setDialogBox(assetManager.get(AssetEnum.BATTLE_HOVER.getPath(), Texture.class)).setForeground(assetManager.get(AssetEnum.GAME_OGRE.getPath(), Texture.class)).build();
				getTextScenes(
					getScript("STORY-OGRE"), font, background, new Array<Mutation>(), AssetEnum.WEREWOLF_MUSIC.getPath(), new Array<String>(new String[]{null, null, null, null, AssetEnum.OGRE.getPath()}),
					getChoiceScene(
						"Continue on?", getArray(new String[]{"Press On", "Turn back"}), 
						getTextScenes(
							getScript("STORY-OGRE-DEFEAT"), font, background, new Array<Mutation>(), AssetEnum.HEAVY_MUSIC.getPath(), new Array<String>(new String[]{null, null, null, AssetEnum.OGRE.getPath(), null, null, null, null, null, AssetEnum.OGRE.getPath(), null, null, AssetEnum.OGRE.getPath()}),
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
				Background backgroundWithAdventurer2 = new BackgroundBuilder(assetManager.get(AssetEnum.DEFAULT_BACKGROUND.getPath(), Texture.class)).setDialogBox(assetManager.get(AssetEnum.BATTLE_HOVER.getPath(), Texture.class)).setForeground(assetManager.get(AssetEnum.ADVENTURER.getPath(), Texture.class)).build();
				getTextScenes(
					getScript("STORY-FEM"), font, backgroundWithAdventurer2,
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
				Background backgroundWithShopkeep2 = new BackgroundBuilder(assetManager.get(AssetEnum.TOWN_BG.getPath(), Texture.class)).setDialogBox(assetManager.get(AssetEnum.BATTLE_HOVER.getPath(), Texture.class)).setForeground(assetManager.get(AssetEnum.SHOPKEEP.getPath(), Texture.class)).build();
				getTextScenes (					
					getArray(new String[]{"You peruse the shop."}), font, backgroundWithShopkeep2, new Array<Mutation>(), AssetEnum.SHOP_MUSIC.getPath(), getArray(new String[]{}),	
					getShopScene(
						ShopCode.SHOP, new BackgroundBuilder(assetManager.get(AssetEnum.TOWN_BG.getPath(), Texture.class)).setForeground(assetManager.get(AssetEnum.SHOPKEEP.getPath(), Texture.class)).build(), 
						getEndScene(EndScene.Type.ENCOUNTER_OVER)	
					)
				);
				break;
			case WEAPON_SHOP:
				Background backgroundWithBlacksmith = new BackgroundBuilder(assetManager.get(AssetEnum.TOWN_BG.getPath(), Texture.class)).setDialogBox(assetManager.get(AssetEnum.BATTLE_HOVER.getPath(), Texture.class)).setForeground(assetManager.get(AssetEnum.TRAINER.getPath(), Texture.class)).build();
				getTextScenes (					
					getArray(new String[]{"You peruse the shop."}), font, backgroundWithBlacksmith, new Array<Mutation>(), AssetEnum.SHOP_MUSIC.getPath(), getArray(new String[]{}),	
					getShopScene(
						ShopCode.WEAPON_SHOP, new BackgroundBuilder(assetManager.get(AssetEnum.TOWN_BG.getPath(), Texture.class)).setForeground(assetManager.get(AssetEnum.TRAINER.getPath(), Texture.class)).build(), 
						getEndScene(EndScene.Type.ENCOUNTER_OVER)	
					)
				);
				break;
			case CAMP_AND_EAT:
				getTextScenes (					
					getScript("FORCED_CAMP"), font, background, getArray(new Mutation[]{new Mutation(saveService, SaveEnum.HEALTH, 10)}), AssetEnum.SHOP_MUSIC.getPath(), getArray(new String[]{}),	
					getEndScene(EndScene.Type.ENCOUNTER_OVER)	
				);
				break;
			case STARVATION:
				Background buttBangedBackground = new BackgroundBuilder(assetManager.get(AssetEnum.DEFAULT_BACKGROUND.getPath(), Texture.class)).setDialogBox(assetManager.get(AssetEnum.BATTLE_HOVER.getPath(), Texture.class)).setForeground(new AnimatedActor("animation/SplurtGO.atlas", "animation/SplurtGO.json"), 555, 520).build();
				
				getTextScenes (					
					getScript("STARVATION"), font, background, new Array<Mutation>(), AssetEnum.WEREWOLF_MUSIC.getPath(), new Array<String>(),
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
					getScript("TOWN"), font, new BackgroundBuilder(assetManager.get(AssetEnum.TRAP_BONUS.getPath(), Texture.class)).setDialogBox(assetManager.get(AssetEnum.BATTLE_HOVER.getPath(), Texture.class)).build(),
					getEndScene(EndScene.Type.ENCOUNTER_OVER)				
				);		
				break;
		}
		// reporting that the battle code has been consumed - this should be encounter code
		saveService.saveDataValue(SaveEnum.BATTLE_CODE, new BattleCode(-1, null, Stance.BALANCED, Stance.BALANCED, false, 0));
		return new Encounter(scenes, endScenes, battleScenes, getStartScene(scenes, sceneCode));	
	}

	private Array<Mutation> getMutation(int damage) {
		Array<Mutation> mutations = new Array<Mutation>();
		mutations.add(new Mutation(saveService, SaveEnum.HEALTH, -damage));
		return mutations;
	}
	
	private OrderedMap<Integer, Scene> addScene(Scene scene) { return addScene(new Array<Scene>(true, new Scene[]{scene}, 0, 1)); }
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
	private OrderedMap<Integer, Scene> getTextScenes(Array<String> script, BitmapFont font, Background background, Array<Mutation> mutations, OrderedMap<Integer, Scene> sceneMap) { return getTextScenes(script, font, background, mutations, null, new Array<String>(), sceneMap); }
	private OrderedMap<Integer, Scene> getTextScenes(Array<String> script, BitmapFont font, Background background, Array<Mutation> mutations, String music, Array<String> sounds, OrderedMap<Integer, Scene> sceneMap) {
		mutations.reverse();
		script.reverse();
		sounds.reverse();
		
		int soundIndex = -(script.size - sounds.size);
		int ii = 1;
		String characterName = character.getCharacterName();
		String buttsize = character.getBootyLiciousness();
		for (String scriptLine: script) {
			scriptLine = scriptLine.replace("<NAME>", characterName).replace("<BUTTSIZE>", buttsize);
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

		Skin skin = assetManager.get(AssetEnum.UI_SKIN.getPath(), Skin.class);
		Sound buttonSound = assetManager.get(AssetEnum.BUTTON_SOUND.getPath(), Sound.class);
		Texture background = assetManager.get(AssetEnum.DEFAULT_BACKGROUND.getPath(), Texture.class);
		
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
			
			table.add(button).size(650, 60).row();
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
	
	private enum ChoiceCheckType {
		LEWD,
		GOLD_GREATER_THAN_10,
		GOLD_LESS_THAN_10
	}
	
	private ClickListener getListener(final AbstractChoiceScene currentScene, final Scene nextScene, final Sound buttonSound, ChoiceCheckType type, final TextButton button) {
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
		Texture background = assetManager.get(AssetEnum.DEFAULT_BACKGROUND.getPath(), Texture.class);
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
		Texture background = assetManager.get(AssetEnum.DEFAULT_BACKGROUND.getPath(), Texture.class);
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
		Texture background = assetManager.get(AssetEnum.DEFAULT_BACKGROUND.getPath(), Texture.class);
		CheckScene checkScene = new CheckScene(sceneMap, sceneCounter, assetManager, saveService, font, new BackgroundBuilder(background).build(), checkType, sceneMap.get(sceneMap.orderedKeys().get(0)), sceneMap.get(sceneMap.orderedKeys().get(1)), character);
		return addScene(checkScene);
	}
	
	private OrderedMap<Integer, Scene> getBattleScene(int battleCode, @SuppressWarnings("unchecked") OrderedMap<Integer, Scene>... sceneMaps) {
		return getBattleScene(battleCode, Stance.BALANCED, Stance.BALANCED, sceneMaps);
	}
	
	private OrderedMap<Integer, Scene> getBattleScene(int battleCode, Array<Outcome> outcomes, @SuppressWarnings("unchecked") OrderedMap<Integer, Scene>... sceneMaps) {
		return getBattleScene(battleCode, Stance.BALANCED, Stance.BALANCED, outcomes, sceneMaps);
	}
	
	private OrderedMap<Integer, Scene> getBattleScene(int battleCode, int climaxCounter, Array<Outcome> outcomes, @SuppressWarnings("unchecked") OrderedMap<Integer, Scene>... sceneMaps) {
		return getBattleScene(battleCode, Stance.BALANCED, Stance.BALANCED, false, climaxCounter, outcomes, sceneMaps);
	}
	
	private OrderedMap<Integer, Scene> getBattleScene(int battleCode, Stance playerStance, Stance enemyStance, @SuppressWarnings("unchecked") OrderedMap<Integer, Scene>... sceneMaps) {
		return getBattleScene(battleCode, playerStance, enemyStance, new Array<Outcome>(new Outcome[]{Outcome.VICTORY, Outcome.DEFEAT}), sceneMaps);
	}
	
	private OrderedMap<Integer, Scene> getBattleScene(int battleCode, Stance playerStance, Stance enemyStance, Array<Outcome> outcomes, @SuppressWarnings("unchecked") OrderedMap<Integer, Scene>... sceneMaps) {
		return getBattleScene(battleCode, playerStance, enemyStance, false, 0, outcomes, sceneMaps);
	}
	
	private OrderedMap<Integer, Scene> getBattleScene(int battleCode, Stance playerStance, Stance enemyStance, boolean disarm, int climaxCounter, Array<Outcome> outcomes, @SuppressWarnings("unchecked") OrderedMap<Integer, Scene>... sceneMaps) {
		OrderedMap<Integer, Scene> sceneMap = aggregateMaps(sceneMaps);
		ObjectMap<String, Integer> outcomeToScene = new ObjectMap<String, Integer>();
		for (int ii = 0; ii < outcomes.size; ii++) {
			outcomeToScene.put(outcomes.get(ii).toString(), sceneMap.get(sceneMap.orderedKeys().get(ii)).getCode());
		}
		
		return addScene(new BattleScene(aggregateMaps(sceneMaps), saveService, battleCode, playerStance, enemyStance, disarm, climaxCounter, outcomeToScene));
	}
	
	private OrderedMap<Integer, Scene> getGameTypeScene(Array<String> buttonLabels, @SuppressWarnings("unchecked") OrderedMap<Integer, Scene>... sceneMaps) {
		OrderedMap<Integer, Scene> sceneMap = aggregateMaps(sceneMaps);
		Skin skin = assetManager.get(AssetEnum.UI_SKIN.getPath(), Skin.class);
		Sound buttonSound = assetManager.get(AssetEnum.BUTTON_SOUND.getPath(), Sound.class);
		Texture background = assetManager.get(AssetEnum.GAME_TYPE_BACKGROUND.getPath(), Texture.class);
		
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
		return getScript("00"+encounterCode.getBattleCode() + "-" + string);
	}
	
	private Array<String> getScript(EncounterCode encounterCode, int scene) {
		return getScript("00"+encounterCode.getBattleCode()+"-"+ ( scene >= 10 ? scene : "0" + scene));
	}
	private Array<String> getScript(String code) {
		return getArray(reader.loadScript(code));
	}
	
	private Array<ChoiceCheckType> getArray(ChoiceCheckType[] array) { return new Array<ChoiceCheckType>(true, array, 0, array.length); }
	private Array<String> getArray(String[] array) { return new Array<String>(true, array, 0, array.length); }
	private Array<Mutation> getArray(Mutation[] array) { return new Array<Mutation>(true, array, 0, array.length); }
	
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


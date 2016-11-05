package com.majalis.encounter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.OrderedMap;
import com.majalis.asset.AssetEnum;
import com.majalis.battle.BattleCode;
import com.majalis.battle.BattleFactory.EnemyEnum;
import com.majalis.character.PlayerCharacter;
import com.majalis.character.Techniques;
import com.majalis.character.AbstractCharacter.Stance;
import com.majalis.character.AbstractCharacter.Stat;
import com.majalis.save.ProfileEnum;
import com.majalis.save.SaveEnum;
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
import com.majalis.scenes.SkillSelectionScene;
import com.majalis.scenes.TextScene;
/*
 * Given a sceneCode, reads that encounter and constructs it from a script file.
 */
public class EncounterBuilder {
	private final Array<Scene> scenes;
	private final Array<EndScene> endScenes;
	private final Array<BattleScene> battleScenes; 
	private final EncounterReader reader;
	private final SaveService saveService;
	private final BitmapFont font;
	private final int sceneCode;
	private int battleCode;
	// can probably be replaced with a call to scenes.size
	private int sceneCounter;
	
	protected EncounterBuilder(EncounterReader reader, SaveService saveService, BitmapFont font, int sceneCode, int battleCode){
		scenes = new Array<Scene>();
		endScenes = new Array<EndScene>();
		battleScenes = new Array<BattleScene>();
		this.reader = reader;
		this.saveService = saveService;
		this.font = font;
		this.sceneCode = sceneCode;
		this.battleCode = battleCode;
		sceneCounter = 0;
	}
	/* different encounter "templates" */
	@SuppressWarnings("unchecked")
	protected Encounter getClassChoiceEncounter(AssetManager assetManager, PlayerCharacter playerCharacter){	
		Texture backgroundTexture = assetManager.get("DefaultBackground.jpg", Texture.class);
		Texture classSelectTexture = assetManager.get("ClassSelect.jpg", Texture.class); 
		Background background = new Background(backgroundTexture);
		Array<Mutation> classMutation = getArray(new Mutation[]{new Mutation(saveService, SaveEnum.CLASS, JobClass.ENCHANTRESS), new Mutation(saveService, SaveEnum.SKILL, Techniques.TAUNT), new Mutation(saveService, SaveEnum.SKILL, Techniques.SECOND_WIND), new Mutation(saveService, SaveEnum.SKILL, Techniques.COMBAT_FIRE)});
				
		getTextScenes(
			getScript("INTRO"), font, background,
			getGameTypeScene(
				assetManager, getArray(new String[]{"Create Character", "Story (Patrons)"}),		
				getTextScenes(
					new String[]{"You've selected to create your character!", "Please choose your class."}, font, background,
					getCharacterCreationScene(
						saveService, font, new Background(classSelectTexture), assetManager, playerCharacter,
						getSkillSelectionScene(
							saveService, font, new Background(classSelectTexture), assetManager, playerCharacter, 
							getCharacterCustomizationScene(
								saveService, font, new Background(classSelectTexture), assetManager, playerCharacter, 
								getEndScene(EndScene.Type.ENCOUNTER_OVER)
							)
						)
					)
				),
				getTextScenes(
					getArray(new String[]{"You have entered story mode.  You are now an Enchantress, alone in the world."}), font, background, classMutation,							
					getEndScene(EndScene.Type.ENCOUNTER_OVER)						
				)
			)
		);
		return new Encounter(scenes, endScenes, new Array<BattleScene>(), getStartScene(scenes, sceneCode));
	}
	
	protected Encounter getLevelUpEncounter(AssetManager assetManager, PlayerCharacter playerCharacter){
		Texture classSelectTexture = assetManager.get("ClassSelect.jpg", Texture.class);
		getSkillSelectionScene(
				saveService, font, new Background(classSelectTexture), assetManager, playerCharacter, getEndScene(EndScene.Type.ENCOUNTER_OVER)
		);
		return new Encounter(scenes, endScenes, new Array<BattleScene>(), getStartScene(scenes, sceneCode));
	}
	
	protected Encounter getDefaultEncounter(AssetManager assetManager){
		Texture backgroundTexture = assetManager.get("StickEncounter.jpg", Texture.class);
		Texture vignetteTexture = assetManager.get(AssetEnum.VIGNETTE.getPath(), Texture.class);
		Background background = new Background(backgroundTexture, vignetteTexture);
		
		getTextScenes(new String[]{"You encounter a stick!", "It's actually rather sexy looking.", "There is nothing left here to do."}, font, background, getEndScene(EndScene.Type.ENCOUNTER_OVER));
		return new Encounter(scenes, endScenes, new Array<BattleScene>(), getStartScene(scenes, sceneCode));
	}
	
	@SuppressWarnings("unchecked")
	protected Encounter getRandomEncounter(int encounterCode, AssetManager assetManager, PlayerCharacter character){
		Texture backgroundTexture = assetManager.get("DefaultBackground.jpg", Texture.class);	
		Background background = new Background(backgroundTexture);
		// if there isn't already a battlecode set, it's determined by the encounterCode; for now, that means dividing the various encounters up by modulus
		if (battleCode == -1) battleCode = encounterCode;
		switch (battleCode){
			// werebitch
			case 0:
				Background werebitchBackground = new Background(backgroundTexture, assetManager.get(AssetEnum.WEREBITCH.getPath(), Texture.class), 1280, 720, 450, 600);
				getTextScenes(
					getScript(battleCode, 0), font, background, getArray(new Mutation[]{new Mutation(saveService, ProfileEnum.KNOWLEDGE, EnemyEnum.WERESLUT.toString())}),
					getTextScenes( 
						getScript(battleCode, 1), font, werebitchBackground, 
						getBattleScene(
							saveService, battleCode, 
							getTextScenes(getArray(new String[]{"You defeated the werebitch!", "You receive 2 Experience."}), font, background, getArray(new Mutation[]{new Mutation(saveService, SaveEnum.EXPERIENCE, 2)}), getEndScene(EndScene.Type.ENCOUNTER_OVER)),
							getTextScenes(getScript(battleCode, 2), font, werebitchBackground, getEndScene(EndScene.Type.GAME_OVER))				
						)
					)
				);		
				break;
			// harpy
			case 1:
				Texture fellatioTexture = assetManager.get(AssetEnum.HARPY_FELLATIO.getPath(), Texture.class);
				int width = (int) (fellatioTexture.getWidth() / (fellatioTexture.getHeight() / 600f) );
				Background harpyFellatioBackground = new Background(backgroundTexture, fellatioTexture, 1280, 720, width, 600);
				getTextScenes(
					getScript(battleCode, 0), font, background, getArray(new Mutation[]{new Mutation(saveService, ProfileEnum.KNOWLEDGE, EnemyEnum.HARPY.toString())}),
					getCheckScene(
						assetManager, Stat.AGILITY, new IntArray(new int[]{6, 4, 0}), character,
						getTextScenes(
							getScript(battleCode, 1), font, background, 
							// need to create a getBattleScene method
							getBattleScene(
								saveService, battleCode, Stance.BALANCED, Stance.PRONE,
								getTextScenes(getArray(new String[]{"You defeated the harpy!", "You receive 1 Experience."}), font, background, getArray(new Mutation[]{new Mutation(saveService, SaveEnum.EXPERIENCE, 1)}), getEndScene(EndScene.Type.ENCOUNTER_OVER)),
								getTextScenes(getScript(battleCode, 4), font, background, getEndScene(EndScene.Type.GAME_OVER))					
							)
						),
						getTextScenes(
							getScript(battleCode, 2), font, background, 
							getBattleScene(
								saveService, battleCode, Stance.KNEELING, Stance.BALANCED,
								getTextScenes(getArray(new String[]{"You defeated the harpy!", "You receive 1 Experience."}), font, background, getArray(new Mutation[]{new Mutation(saveService, SaveEnum.EXPERIENCE, 1)}), getEndScene(EndScene.Type.ENCOUNTER_OVER)),
								getTextScenes(getScript(battleCode, 4), font, background, getEndScene(EndScene.Type.GAME_OVER))					
							)
						),
						getTextScenes(getScript(battleCode, 3), font, harpyFellatioBackground, 
							getBattleScene(
								saveService, battleCode, Stance.FELLATIO, Stance.FELLATIO,									
								getTextScenes(getArray(new String[]{"You defeated the harpy!", "You receive 1 Experience."}), font, background, getArray(new Mutation[]{new Mutation(saveService, SaveEnum.EXPERIENCE, 1)}), getEndScene(EndScene.Type.ENCOUNTER_OVER)),
								getTextScenes(getScript(battleCode, 4), font, background, getEndScene(EndScene.Type.GAME_OVER))					
							)
						)
					)
				);		
				break;
			// slime
			case 2:
				Background slimeBackground = new Background(backgroundTexture, assetManager.get(AssetEnum.SLIME.getPath(), Texture.class), 1280, 720, 450, 600);
				Background slimeDoggyBackground = new Background(backgroundTexture, assetManager.get(AssetEnum.SLIME_DOGGY.getPath(), Texture.class), 1280, 720, 450, 600);
				getTextScenes(
					getScript(battleCode, 0), font, slimeBackground, getArray(new Mutation[]{new Mutation(saveService, ProfileEnum.KNOWLEDGE, EnemyEnum.SLIME.toString())}),
					getChoiceScene(
						assetManager, "What do you do with the slime?", getArray(new String[]{"Fight Her", "Smooch Her", "Leave Her Be"}),
						getBattleScene(
							saveService, battleCode,
							getTextScenes(
								getScript(battleCode, 1), font, slimeBackground, 
								getChoiceScene(
									assetManager,
									"Slay the slime?",
									getArray(new String[]{"Stab the core", "Spare her"}),
									getTextScenes(
										getScript(battleCode, 2), font, slimeBackground,
										getCheckScene(
											assetManager,
											Stat.AGILITY,
											new IntArray(new int[]{6, 0}),
											character,
											getTextScenes(getScript(battleCode, 3), font, slimeBackground,
												getTextScenes(getArray(new String[]{"You slew the slime!", "You receive 3 Experience."}), font, background, getArray(new Mutation[]{new Mutation(saveService, SaveEnum.EXPERIENCE, 3)}), getEndScene(EndScene.Type.ENCOUNTER_OVER))
											),
											getTextScenes(getScript(battleCode, 4), font, slimeBackground, getEndScene(EndScene.Type.GAME_OVER))		
										)		
									),
									getTextScenes(getScript(battleCode, 5), font, slimeBackground,
										getTextScenes(getArray(new String[]{"You spared the slime!", "You receive 3 Experience."}), font, background, getArray(new Mutation[]{new Mutation(saveService, SaveEnum.EXPERIENCE, 3)}), getEndScene(EndScene.Type.ENCOUNTER_OVER))
									)
								)
							),
							getTextScenes(
								getScript(battleCode, 6),
								font, slimeBackground, 
								getChoiceScene(
									assetManager,
									"What do you do?",
									getArray(new String[]{"Try to speak", "Run!"}),
									getTextScenes(
											getScript(battleCode, 7), font, slimeBackground,
											getTextScenes(
												getArray(new String[]{"You are stuck for a long while.  You eat 10 food.", "You recover 15 health.", "You receive 2 Experience for the... experience."}), font, background, getArray(new Mutation[]{new Mutation(saveService, SaveEnum.FOOD, -10), new Mutation(saveService, SaveEnum.HEALTH, 15), new Mutation(saveService, SaveEnum.EXPERIENCE, 2)}),
												getEndScene(EndScene.Type.ENCOUNTER_OVER))
									),
									getCheckScene(
										assetManager,
										Stat.AGILITY,
										new IntArray(new int[]{5, 0}),
										character,
										getTextScenes(getScript(battleCode, 8), font, background, getEndScene(EndScene.Type.ENCOUNTER_OVER)),
										getTextScenes(
											getScript(battleCode, 9), font, background, 
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
							getScript(battleCode, 10), font, slimeBackground, 
							getChoiceScene(
								assetManager,
								"Do you enter the slime, or...?",
								getArray(new String[]{"Go In", "Love Dart (Requires: Catamite)"}),
								getArray(new PlayerCharacter[]{null, character}),
								getTextScenes(getScript(battleCode, 11), font, slimeBackground,
									getTextScenes(getArray(new String[]{"You banged the slime!", "You receive 2 Experience."}), font, background, getArray(new Mutation[]{new Mutation(saveService, SaveEnum.EXPERIENCE, 2)}), getEndScene(EndScene.Type.ENCOUNTER_OVER))
								),
								getTextScenes(getScript(battleCode, 12), font, slimeDoggyBackground,
									getTextScenes(getArray(new String[]{"You got banged by the slime!", "You receive 3 Experience."}), font, background, getArray(new Mutation[]{new Mutation(saveService, SaveEnum.EXPERIENCE, 3)}), getEndScene(EndScene.Type.ENCOUNTER_OVER))
								)
							)
						),
						getEndScene(EndScene.Type.ENCOUNTER_OVER)
					)
				);
				break;
			// brigand
			case 3:
				getTextScenes(
					getScript(battleCode, 0), font, background, getArray(new Mutation[]{new Mutation(saveService, ProfileEnum.KNOWLEDGE, EnemyEnum.BRIGAND.toString())}),
					getCheckScene(
						assetManager, Stat.PERCEPTION, new IntArray(new int[]{6, 4, 0}), character,
						getTextScenes(
							getScript(battleCode, 1), font, background, 
							getChoiceScene(
								assetManager, "How do you handle the brigand?", getArray(new String[]{"Charge", "Ready an Arrow", "Speak"}),
								getBattleScene(saveService, battleCode, Stance.OFFENSIVE, Stance.BALANCED,
									getTextScenes(getArray(new String[]{"You defeated the brigand!", "You receive 1 Experience."}), font, background, getArray(new Mutation[]{new Mutation(saveService, SaveEnum.EXPERIENCE, 1)}), getEndScene(EndScene.Type.ENCOUNTER_OVER)),
									getTextScenes(getScript(battleCode, 13), font, background, 
										getTextScenes(
											getArray(new String[]{"You rest, eating 5 food.", "You recover 10 health."}), font, background, getArray(new Mutation[]{new Mutation(saveService, SaveEnum.FOOD, -5), new Mutation(saveService, SaveEnum.HEALTH, 10)}),
											getEndScene(EndScene.Type.ENCOUNTER_OVER)
										)
									)
								),
								getBattleScene(
									saveService, battleCode,
									getTextScenes(getArray(new String[]{"You defeated the brigand!", "You receive 1 Experience."}), font, background, getArray(new Mutation[]{new Mutation(saveService, SaveEnum.EXPERIENCE, 1)}), getEndScene(EndScene.Type.ENCOUNTER_OVER)),
									getTextScenes(getScript(battleCode, 13), font, background, 
										getTextScenes(
											getArray(new String[]{"You rest, eating 5 food.", "You recover 10 health."}), font, background, getArray(new Mutation[]{new Mutation(saveService, SaveEnum.FOOD, -5), new Mutation(saveService, SaveEnum.HEALTH, 10)}),
											getEndScene(EndScene.Type.ENCOUNTER_OVER)
										)
									)
								),
								getTextScenes(
									getScript(battleCode, 2), font, background,
									getChoiceScene(
										assetManager, "Accept her offer?", getArray(new String[]{"Accept (Requires: Catamite)", "Decline"}), getArray(new PlayerCharacter[]{character, null}),
										getTextScenes(
											getScript(battleCode, 3), font, background,
											getChoiceScene(
												assetManager, "Tell her to pull out?", getArray(new String[]{"Say Nothing", "Ask her"}),
												getTextScenes(
													getScript(battleCode, 4), font, background,
													getTextScenes(
														getArray(new String[]{"You are flooded with cum and Experience!", "You receive 3 Experience."}), font, background, getArray(new Mutation[]{new Mutation(saveService, SaveEnum.EXPERIENCE, 3)}), getEndScene(EndScene.Type.ENCOUNTER_OVER)
													)
												),
												getTextScenes(
													getScript(battleCode, 5), font, background,
													getCheckScene(
														assetManager, Stat.CHARISMA, new IntArray(new int[]{4, 0}), character,
														getTextScenes(
															getScript(battleCode, 6), font, background,
															getTextScenes(
																getArray(new String[]{"Your face is covered with cum... and Experience!", "You receive 2 Experience."}), font, background, getArray(new Mutation[]{new Mutation(saveService, SaveEnum.EXPERIENCE, 2)}), getEndScene(EndScene.Type.ENCOUNTER_OVER)
															)
														),
														getTextScenes(
															getScript(battleCode, 7), font, background,
															getTextScenes(
																getArray(new String[]{"Your stomach is full of cum... and Experience!", "You receive 2 Experience."}), font, background, getArray(new Mutation[]{new Mutation(saveService, SaveEnum.EXPERIENCE, 2)}), getEndScene(EndScene.Type.ENCOUNTER_OVER)
															)
														)													
													)
												)
											)
										),
										getTextScenes(
											getScript(battleCode, 8), font, background,
											getCheckScene(
												assetManager, Stat.CHARISMA, new IntArray(new int[]{5, 0}), character,
												getTextScenes(
													getScript(battleCode, 9), font, background,
													getEndScene(EndScene.Type.ENCOUNTER_OVER)
												),
												getTextScenes(
													getScript(battleCode, 10), font, background,
													getBattleScene(
														saveService, battleCode,
														getTextScenes(getArray(new String[]{"You defeated the brigand!", "You receive 1 Experience."}), font, background, getArray(new Mutation[]{new Mutation(saveService, SaveEnum.EXPERIENCE, 1)}), getEndScene(EndScene.Type.ENCOUNTER_OVER)),
														getTextScenes(getScript(battleCode, 13), font, background, 
															getTextScenes(
																getArray(new String[]{"You rest, eating 5 food.", "You recover 10 health."}), font, background, getArray(new Mutation[]{new Mutation(saveService, SaveEnum.FOOD, -5), new Mutation(saveService, SaveEnum.HEALTH, 10)}),
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
						),
						getTextScenes(
							getArray(new String[]{"Ouch!  You take 5 damage!"}), font, background, getMutation(5),
							getTextScenes(
								getScript(battleCode, 11), font, background, 
								getBattleScene(
									saveService, battleCode,
									getTextScenes(getArray(new String[]{"You defeated the brigand!", "You receive 1 Experience."}), font, background, getArray(new Mutation[]{new Mutation(saveService, SaveEnum.EXPERIENCE, 1)}), getEndScene(EndScene.Type.ENCOUNTER_OVER)),
									getTextScenes(getScript(battleCode, 13), font, background, 
										getTextScenes(
											getArray(new String[]{"You rest, eating 5 food.", "You recover 10 health."}), font, background, getArray(new Mutation[]{new Mutation(saveService, SaveEnum.FOOD, -5), new Mutation(saveService, SaveEnum.HEALTH, 10)}),
											getEndScene(EndScene.Type.ENCOUNTER_OVER)
										)
									)
								)
							)
						),
						getTextScenes(
							getArray(new String[]{"Ouch!  You take 5 damage!"}), font, background, getMutation(5),
							getTextScenes(
								getScript(battleCode, 12), font, background, 
								getBattleScene(
									saveService, battleCode, Stance.DOGGY, Stance.DOGGY,
									getTextScenes(getArray(new String[]{"You defeated the brigand!", "You receive 1 Experience."}), font, background, getArray(new Mutation[]{new Mutation(saveService, SaveEnum.EXPERIENCE, 1)}), getEndScene(EndScene.Type.ENCOUNTER_OVER)),
									getTextScenes(getScript(battleCode, 13), font, background, 
										getTextScenes(
											getArray(new String[]{"You rest, eating 5 food.", "You recover 10 health."}), font, background, getArray(new Mutation[]{new Mutation(saveService, SaveEnum.FOOD, -5), new Mutation(saveService, SaveEnum.HEALTH, 10)}),
											getEndScene(EndScene.Type.ENCOUNTER_OVER)
										)
									)
								)
							)
						)
					)
				);	
				break;
			// dryad
			case 4:
				backgroundTexture = assetManager.get("DryadApple.jpg", Texture.class);
				Texture vignetteTexture = assetManager.get(AssetEnum.VIGNETTE.getPath(), Texture.class);
				background = new Background(backgroundTexture, vignetteTexture, 540, 720);
				getTextScenes(
					getScript(battleCode, 0), font, background, 
					getChoiceScene(
						assetManager, "Do you offer her YOUR apple, or try to convince her to just hand it over?", getArray(new String[]{"Offer(Requires: Catamite)", "Plead with her"}), getArray(new PlayerCharacter[]{character, null}),												
						getTextScenes(
							getScript(battleCode, 1), font, background,
							getTextScenes(
								getArray(new String[]{"You take 5 damage from the splinters.", "You receive 10 food from the dryad.", "You receive 1 Experience."}), font, background, getArray(new Mutation[]{new Mutation(saveService, SaveEnum.HEALTH, -5), new Mutation(saveService, SaveEnum.FOOD, 10), new Mutation(saveService, SaveEnum.EXPERIENCE, 1)}), 
								getEndScene(EndScene.Type.ENCOUNTER_OVER)
							)
						),
						getCheckScene(
							assetManager, Stat.CHARISMA, new IntArray(new int[]{5, 0}), character,
							getTextScenes(
								getScript(battleCode, 2), font, background,
								getTextScenes(
									getArray(new String[]{"You receive 10 food from the dryad.", "You receive 1 Experience."}),
									font,
									background,
									getArray(new Mutation[]{new Mutation(saveService, SaveEnum.FOOD, 10), new Mutation(saveService, SaveEnum.EXPERIENCE, 1)}),
									getEndScene(EndScene.Type.ENCOUNTER_OVER)
								)
							),
							getTextScenes(
								getScript(battleCode, 3), font, background,
								getEndScene(EndScene.Type.ENCOUNTER_OVER)
							)
						)			
					)
				);
				break;
			default:
				backgroundTexture = assetManager.get(AssetEnum.TRAP_BONUS.getPath(), Texture.class);
				getTextScenes(
					getScript("TOWN"), font, new Background(backgroundTexture, assetManager.get(AssetEnum.VIGNETTE.getPath(), Texture.class), (int) (backgroundTexture.getWidth() / (backgroundTexture.getHeight() / 720f) ), 720), 
					getEndScene(EndScene.Type.ENCOUNTER_OVER)				
				);		
				break;
		}
		// reporting that the battle code has been consumed - this should be encounter code
		saveService.saveDataValue(SaveEnum.BATTLE_CODE, new BattleCode(-1, -1, -1, Stance.BALANCED, Stance.BALANCED));
		return new Encounter(scenes, endScenes, battleScenes, getStartScene(scenes, sceneCode));	
	}
	
	private Array<Mutation> getMutation(int damage){
		Array<Mutation> mutations = new Array<Mutation>();
		mutations.add(new Mutation(saveService, SaveEnum.HEALTH, -damage));
		return mutations;
	}
	
	private OrderedMap<Integer, Scene> addScene(Scene scene){ return addScene(new Array<Scene>(true, new Scene[]{scene}, 0, 1)); }
	// pass in one or multiple scenes that the next scene will branch into
	private OrderedMap<Integer, Scene> addScene(Array<Scene> scenes){
		OrderedMap<Integer, Scene> sceneMap = new OrderedMap<Integer, Scene>();
		for (Scene scene : scenes){
			this.scenes.add(scene);
			if (scene instanceof BattleScene) battleScenes.add((BattleScene)scene);
			if (scene instanceof EndScene) endScenes.add((EndScene)scene);
			sceneMap.put(sceneCounter++, scene);
		}
		return sceneMap;
	}
	
	/* Scene type getters - these should all wrap themselves in addScene - look for anywhere they aren't currently to confirm*/
	
	private OrderedMap<Integer, Scene> getTextScenes(String[] script, BitmapFont font, Background background, OrderedMap<Integer, Scene> sceneMap){ return getTextScenes(new Array<String>(true, script, 0, script.length), font, background, sceneMap); }	
	private OrderedMap<Integer, Scene> getTextScenes(Array<String> script, BitmapFont font, Background background, OrderedMap<Integer, Scene> sceneMap){ return getTextScenes(script, font, background, new Array<Mutation>(), sceneMap); }
	// pass in a list of script lines in chronological order, this will reverse their order and add them to the stack
	private OrderedMap<Integer, Scene> getTextScenes(Array<String> script, BitmapFont font, Background background, Array<Mutation> mutations, OrderedMap<Integer, Scene> sceneMap){
		mutations.reverse();
		script.reverse();
		
		int mutationIndex = -(script.size - mutations.size);
		for (String scriptLine: script){
			if (mutationIndex >= 0){
				Mutation toApply = mutations.get(mutationIndex);
				sceneMap = addScene(new TextScene(sceneMap, sceneCounter, saveService, font, background.clone(), scriptLine, new Array<Mutation>(true, new Mutation[]{toApply}, 0, 1)));
			}
			else {
				sceneMap = addScene(new TextScene(sceneMap, sceneCounter, saveService, font, background.clone(), scriptLine, new Array<Mutation>()));
			}
			mutationIndex++;
		}	
		return sceneMap;
	}
	
	private OrderedMap<Integer, Scene> getChoiceScene(AssetManager assetManager, String choiceDialogue, Array<String> buttonLabels, Array<PlayerCharacter> checks, OrderedMap<Integer, Scene>... sceneMaps){
		OrderedMap<Integer, Scene> sceneMap = aggregateMaps(sceneMaps);
		
		// use sceneMap to generate the table
		Table table = new Table();

		Skin skin = assetManager.get("uiskin.json", Skin.class);
		Sound buttonSound = assetManager.get("sound.wav", Sound.class);
		Texture background = assetManager.get("DefaultBackground.jpg", Texture.class);
		
		ChoiceScene choiceScene = new ChoiceScene(sceneMap, sceneCounter, saveService, font, choiceDialogue, table, new Background(background));
		int ii = 0;
		for (String label  : buttonLabels){
			TextButton button = new TextButton(label, skin);
			if (ii < checks.size && checks.get(ii) != null){
				button.addListener(getListener(choiceScene, sceneMap.get(sceneMap.orderedKeys().get(ii)), buttonSound, checks.get(ii)));
			}
			else {
				button.addListener(getListener(choiceScene, sceneMap.get(sceneMap.orderedKeys().get(ii)), buttonSound));
			}
			
			table.add(button).width(360).height(40).row();
			ii++;
		}
				
		return addScene(choiceScene);
		
	}
	
	private OrderedMap<Integer, Scene> getChoiceScene(AssetManager assetManager, String choiceDialogue, Array<String> buttonLabels, OrderedMap<Integer, Scene>... sceneMaps){
		return getChoiceScene(assetManager, choiceDialogue, buttonLabels, new Array<PlayerCharacter>(), sceneMaps);
	}
	
	private ClickListener getListener(final AbstractChoiceScene currentScene, final Scene nextScene, final Sound buttonSound){
		return new ClickListener(){
	        @Override
	        public void clicked(InputEvent event, float x, float y) {
	        	buttonSound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
	        	// set new Scene as active based on choice
	        	nextScene.setActive();
	        	currentScene.finish();
	        }
	    };
	}
	
	private ClickListener getListener(final AbstractChoiceScene currentScene, final Scene nextScene, final Sound buttonSound, final PlayerCharacter character){
		return new ClickListener(){
	        @Override
	        public void clicked(InputEvent event, float x, float y) {
	        	if (!character.isLewd()){
	        		// this should actually disable the button, but not as part of an on-click event
	        	}
	        	else {
	        		buttonSound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
		        	// set new Scene as active based on choice
		        	nextScene.setActive();
		        	currentScene.finish();
	        	}
	        }
	    };
	}
	
	// accepts a list of values, will map those values to scenes in the scenemap in order
	private OrderedMap<Integer, Scene> getCheckScene(AssetManager assetManager, Stat stat, IntArray checkValues, PlayerCharacter character, OrderedMap<Integer, Scene>... sceneMaps){
		OrderedMap<Integer, Scene> sceneMap = aggregateMaps(sceneMaps);
		Texture background = assetManager.get("DefaultBackground.jpg", Texture.class);
		OrderedMap<Integer, Scene> checkValueMap = new OrderedMap<Integer, Scene>();
		for (int ii = 0; ii < checkValues.size; ii++){
			checkValueMap.put(checkValues.get(ii), sceneMap.get(sceneMap.orderedKeys().get(ii)));
		}
		CheckScene checkScene = new CheckScene(sceneMap, sceneCounter, saveService, font, new Background(background), stat, checkValueMap, character);
		return addScene(checkScene);
	}
	
	private OrderedMap<Integer, Scene> getBattleScene(SaveService saveService, int battleCode, OrderedMap<Integer, Scene>... sceneMaps){
		return getBattleScene(saveService, battleCode, Stance.BALANCED, Stance.BALANCED, sceneMaps);
	}
	
	private OrderedMap<Integer, Scene> getBattleScene(SaveService saveService, int battleCode, Stance playerStance, Stance enemyStance, OrderedMap<Integer, Scene>... sceneMaps){
		OrderedMap<Integer, Scene> sceneMap = aggregateMaps(sceneMaps);
		return addScene(new BattleScene(sceneMap, saveService, battleCode, playerStance, enemyStance));
	}
	
	private OrderedMap<Integer, Scene> getGameTypeScene(AssetManager assetManager, Array<String> buttonLabels, OrderedMap<Integer, Scene>... sceneMaps){
		OrderedMap<Integer, Scene> sceneMap = aggregateMaps(sceneMaps);
		Skin skin = assetManager.get("uiskin.json", Skin.class);
		Sound buttonSound = assetManager.get("sound.wav", Sound.class);
		Texture background = assetManager.get("GameTypeSelect.jpg", Texture.class);
		
		Array<TextButton> buttons = new Array<TextButton>();
		for (String label : buttonLabels){
			buttons.add(new TextButton(label, skin));
		}
		
		GameTypeScene gameTypeScene = new GameTypeScene(sceneMap, sceneCounter, saveService, buttons, new Background(background));
		int ii = 0;
		for (TextButton button : buttons){
			button.addListener(getListener(gameTypeScene, sceneMap.get(sceneMap.orderedKeys().get(ii++)), buttonSound));
		}
				
		return addScene(gameTypeScene);
	}
	
	private OrderedMap<Integer, Scene> getCharacterCreationScene(SaveService saveService, BitmapFont font, Background background, AssetManager assetManager, PlayerCharacter character, OrderedMap<Integer, Scene> sceneMap){
		return addScene(new CharacterCreationScene(sceneMap, sceneCounter, saveService, font, background, assetManager, character));
	}
	
	private OrderedMap<Integer, Scene> getSkillSelectionScene(SaveService saveService, BitmapFont font, Background background, AssetManager assetManager, PlayerCharacter character, OrderedMap<Integer, Scene> sceneMap){
		return addScene(new SkillSelectionScene(sceneMap, sceneCounter, saveService, font, background, assetManager, character));
	}
	
	private OrderedMap<Integer, Scene> getCharacterCustomizationScene(SaveService saveService, BitmapFont font, Background background, AssetManager assetManager, PlayerCharacter character, OrderedMap<Integer, Scene> sceneMap){
		return addScene(new CharacterCustomizationScene(sceneMap, sceneCounter, saveService, font, background, assetManager, character));
	}
	
	private OrderedMap<Integer, Scene> getEndScene(EndScene.Type type){
		return addScene(new EndScene(type));
	}
	
	private OrderedMap<Integer, Scene> aggregateMaps(OrderedMap<Integer, Scene>... sceneMaps){
		OrderedMap<Integer, Scene> aggregatedMap = new OrderedMap<Integer, Scene>();
		for (OrderedMap<Integer, Scene> map : sceneMaps){
			aggregatedMap.putAll(map);
		}
		return aggregatedMap;	
	}
	
	private Array<String> getScript(int battleCode, int scene){
		return getScript("00"+battleCode+"-"+ ( scene >= 10 ? scene : "0" + scene));
	}
	private Array<String> getScript(String code){
		return getArray(reader.loadScript(code));
	}
	
	private Array<String> getArray(String[] array){ return new Array<String>(true, array, 0, array.length); }
	private Array<Mutation> getArray(Mutation[] array){ return new Array<Mutation>(true, array, 0, array.length); }
	private Array<PlayerCharacter> getArray(PlayerCharacter[] array) { return new Array<PlayerCharacter>(true, array, 0, array.length);  }
	
	private Scene getStartScene(Array<Scene> scenes, Integer sceneCode){
		// default case	
		if (sceneCode == 0){
			// returns the final scene and plays in reverse order
			return scenes.get(scenes.size - 1);
		}
		for (Scene objScene: scenes){
			if (objScene.getCode() == sceneCode){
				return objScene;
			}
		}
		return null;
	}
}

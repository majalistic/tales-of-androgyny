package com.majalis.encounter;

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
import com.majalis.battle.BattleCode;
import com.majalis.character.PlayerCharacter;
import com.majalis.character.Techniques;
import com.majalis.character.AbstractCharacter.Stance;
import com.majalis.character.PlayerCharacter.Stat;
import com.majalis.save.SaveEnum;
import com.majalis.save.SaveManager.JobClass;
import com.majalis.save.SaveService;
import com.majalis.scenes.AbstractChoiceScene;
import com.majalis.scenes.BattleScene;
import com.majalis.scenes.CharacterCreationScene;
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
		Array<Mutation> classMutation = new Array<Mutation>(true, new Mutation[]{new Mutation(saveService, SaveEnum.CLASS, JobClass.ENCHANTRESS), new Mutation(saveService, SaveEnum.SKILL, Techniques.TAUNT), new Mutation(saveService, SaveEnum.SKILL, Techniques.SECOND_WIND), new Mutation(saveService, SaveEnum.SKILL, Techniques.COMBAT_FIRE)}, 0, 4);
				
		getTextScenes(
			getScript("INTRO"), font, background,
			addScene(
				getGameTypeScene(
					assetManager, new Array<String>(true, new String[]{"Create Character", "Default"}, 0, 2),
					aggregateMaps(			
						getTextScenes(
							new String[]{"You've selected to create your character!", "Please choose your class."}, font, background,
							addScene(
								getCharacterCreationScene(
									sceneCounter, saveService, font, new Background(classSelectTexture), assetManager, playerCharacter,
									addScene(
										getSkillSelectionScene(
											sceneCounter, saveService, font, new Background(classSelectTexture), assetManager, playerCharacter, 
											addScene(
												getEndScene(EndScene.Type.ENCOUNTER_OVER)
											)
										)	
									)
								)
							)
						),
						getTextScenes(
							new Array<String>(true, new String[]{"You have entered story mode.  You are now an Enchantress, alone in the world."}, 0, 1), font, background, classMutation,
							addScene(
								getEndScene(EndScene.Type.ENCOUNTER_OVER)
							)
						)
					)
				)
			)
		);
		return new Encounter(scenes, endScenes, new Array<BattleScene>(), getStartScene(scenes, sceneCode));
	}
	
	protected Encounter getDefaultEncounter(AssetManager assetManager){
		Texture backgroundTexture = assetManager.get("StickEncounter.jpg", Texture.class);
		Texture vignetteTexture = assetManager.get("BlackVignetteBottom.png", Texture.class);
		Background background = new Background(backgroundTexture, vignetteTexture);
		
		getTextScenes(new String[]{"You encounter a stick!", "It's actually rather sexy looking.", "There is nothing left here to do."}, font, background, addScene(getEndScene(EndScene.Type.ENCOUNTER_OVER)));
		return new Encounter(scenes, endScenes, new Array<BattleScene>(), getStartScene(scenes, sceneCode));
	}
	
	@SuppressWarnings("unchecked")
	protected Encounter getRandomEncounter(int encounterCode, AssetManager assetManager, PlayerCharacter character){
		Texture backgroundTexture = assetManager.get("DefaultBackground.jpg", Texture.class);	
		Background background = new Background(backgroundTexture);
		// if there isn't already a battlecode set, it's determined by the encounterCode; for now, that means dividing the various encounters up by modulus
		if (battleCode == -1) battleCode = encounterCode % 5;
		switch (battleCode){
			// werebitch
			case 0:
				Background werebitchBackground = new Background(backgroundTexture, assetManager.get("WerebitchBasicNoBG.png", Texture.class), 1280, 720, 450, 600);
				getTextScenes(
					getScript(battleCode, 0), 
					font, background,
					getTextScenes( 
						getScript(battleCode, 1), font, werebitchBackground, 
						addScene(
							getBattleScene(
								saveService, battleCode, 
								aggregateMaps(
									getTextScenes(new String[]{"You defeated the werebitch!", "You receive 2 XP."}, font, background, addScene(getEndScene(EndScene.Type.ENCOUNTER_OVER))),
									getTextScenes(getScript(battleCode, 2), font, werebitchBackground, addScene(getEndScene(EndScene.Type.GAME_OVER)))				
								)	
							)
						)
					)
				);		
				break;
			// harpy
			case 1:
				getTextScenes(
					getScript(battleCode, 0), font, background, 
					addScene(
						getCheckScene(
							assetManager,
							Stat.AGILITY, new IntArray(new int[]{6, 4, 0}), character,
							aggregateMaps(
								getTextScenes(
									getScript(battleCode, 1), font, background, 
									// need to create a getBattleScene method
									addScene(
										getBattleScene(
											saveService, battleCode, Stance.BALANCED, Stance.PRONE,
											aggregateMaps(
												getTextScenes(new String[]{"You defeated the harpy!", "You receive 1 XP."}, font, background, addScene(getEndScene(EndScene.Type.ENCOUNTER_OVER))),
												getTextScenes(getScript(battleCode, 4), font, background, addScene(getEndScene(EndScene.Type.GAME_OVER)))					
											)
										)
									)
								),
								getTextScenes(
									getScript(battleCode, 2), font, background, 
									addScene(
										getBattleScene(
											saveService, battleCode, Stance.KNEELING, Stance.BALANCED,
											aggregateMaps(
												getTextScenes(new String[]{"You defeated the harpy!", "You receive 1 XP."}, font, background, addScene(getEndScene(EndScene.Type.ENCOUNTER_OVER))),
												getTextScenes(getScript(battleCode, 4), font, background, addScene(getEndScene(EndScene.Type.GAME_OVER)))					
											)
										)
									)
								),
								getTextScenes(getScript(battleCode, 3), font, background, 
									addScene(
										getBattleScene(
											saveService, battleCode, Stance.FELLATIO, Stance.FELLATIO,
											aggregateMaps(
												getTextScenes(new String[]{"You defeated the harpy!", "You receive 1 XP."}, font, background, addScene(getEndScene(EndScene.Type.ENCOUNTER_OVER))),
												getTextScenes(getScript(battleCode, 4), font, background, addScene(getEndScene(EndScene.Type.GAME_OVER)))					
											)
										)
									)
								)
							)
						)
					)
				);		
				break;
			// slime
			case 2:
				Background slimeBackground = new Background(backgroundTexture, assetManager.get("HeartSlimeNoBG.png", Texture.class), 1280, 720, 450, 600);
				getTextScenes(
					getScript(battleCode, 0), font, slimeBackground, 
					addScene(
						getChoiceScene(
							assetManager, "What do you do with the slime?", new Array<String>(true, new String[]{"Fight Her", "Smooch Her", "Leave Her Be"}, 0, 3),
							aggregateMaps(
								addScene(
									getBattleScene(
										saveService, battleCode,
										aggregateMaps(
											getTextScenes(
												getScript(battleCode, 1), font, slimeBackground, 
												addScene(
													getChoiceScene(
														assetManager,
														"Slay the slime?",
														new Array<String>(true, new String[]{"Stab the core", "Spare her"}, 0, 2),
														aggregateMaps(
															getTextScenes(
																getScript(battleCode, 2), font, slimeBackground,
																addScene(
																	getCheckScene(
																		assetManager,
																		Stat.AGILITY,
																		new IntArray(new int[]{6, 0}),
																		character,
																		aggregateMaps(
																			getTextScenes(getScript(battleCode, 3), font, slimeBackground, addScene(getEndScene(EndScene.Type.ENCOUNTER_OVER))),
																			getTextScenes(getScript(battleCode, 4), font, slimeBackground, addScene(getEndScene(EndScene.Type.GAME_OVER)))		
																		)
																	)		
																)
															),
															getTextScenes(getScript(battleCode, 5), font, slimeBackground, addScene(getEndScene(EndScene.Type.ENCOUNTER_OVER)))
														)
													)
												)
											),
											getTextScenes(
												getScript(battleCode, 6),
												font, slimeBackground, 
												addScene(
													getChoiceScene(
														assetManager,
														"What do you do?",
														new Array<String>(true, new String[]{"Try to speak", "Run!"}, 0, 2),
														aggregateMaps(
															getTextScenes(getScript(battleCode, 7), font, slimeBackground, addScene(getEndScene(EndScene.Type.GAME_OVER))),
															addScene(
																getCheckScene(
																	assetManager,
																	Stat.AGILITY,
																	new IntArray(new int[]{5, 0}),
																	character,
																	aggregateMaps(	
																		getTextScenes(getScript(battleCode, 8), font, background, addScene(getEndScene(EndScene.Type.ENCOUNTER_OVER))),
																		getTextScenes(getScript(battleCode, 9), font, background, addScene(getEndScene(EndScene.Type.GAME_OVER)))
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
									getScript(battleCode, 10), font, slimeBackground, 
									addScene(
										getChoiceScene(
											assetManager,
											"Do you enter the slime, or...?",
											new Array<String>(true, new String[]{"Go In", "Love Dart"}, 0, 2),
											aggregateMaps(
												getTextScenes(getScript(battleCode, 11), font, slimeBackground, addScene(getEndScene(EndScene.Type.ENCOUNTER_OVER))),
												getTextScenes(getScript(battleCode, 12), font, slimeBackground, addScene(getEndScene(EndScene.Type.ENCOUNTER_OVER)))
											)	
										)
									)
								),
								addScene(getEndScene(EndScene.Type.ENCOUNTER_OVER))
							) 
						)
					)
				);
				break;
			// brigand
			case 3:
				getTextScenes(
					getScript(battleCode, 0), font, background, 
					addScene(
						getCheckScene(
							assetManager, Stat.PERCEPTION, new IntArray(new int[]{6, 4, 0}), character,
							aggregateMaps(
								getTextScenes(
									getScript(battleCode, 1), font, background, 
									// need to create a getBattleScene method
									addScene(
										getChoiceScene(
											assetManager,
											"Do you charge or ready an arrow?",
											new Array<String>(true, new String[]{"Charge", "Ready an Arrow"}, 0, 2),
											aggregateMaps(
												addScene(
													getBattleScene(saveService, battleCode, Stance.OFFENSIVE, Stance.BALANCED,
														aggregateMaps(
																getTextScenes(new String[]{"You defeated the brigand!", "You get 1 XP."}, font, background, addScene(getEndScene(EndScene.Type.ENCOUNTER_OVER))),
																getTextScenes(getScript(battleCode, 4), font, background, addScene(getEndScene(EndScene.Type.GAME_OVER)))					
														))
												),
												addScene(
													getBattleScene(
														saveService, battleCode, Stance.BALANCED, Stance.BALANCED,
														aggregateMaps(
																getTextScenes(new String[]{"You defeated the brigand!", "You get 1 XP."}, font, background, addScene(getEndScene(EndScene.Type.ENCOUNTER_OVER))),
																getTextScenes(getScript(battleCode, 4), font, background, addScene(getEndScene(EndScene.Type.GAME_OVER)))					
														)
													)
												)
											)
										)
									)
								),
								getTextScenes(
									new Array<String>(true, new String[]{"Ouch!  You take 5 damage!"}, 0, 1), font, background, getMutation(5),
									getTextScenes(
										getScript(battleCode, 2), font, background, 
										addScene(getBattleScene(
											saveService, battleCode, Stance.BALANCED, Stance.BALANCED,
												aggregateMaps(
													getTextScenes(new String[]{"You defeated the brigand!.", "You get 1 XP."}, font, background, addScene(getEndScene(EndScene.Type.ENCOUNTER_OVER))),
													getTextScenes(getScript(battleCode, 4), font, background, addScene(getEndScene(EndScene.Type.GAME_OVER)))					
												)
											)
										)
									)
								),
								getTextScenes(
									getScript(battleCode, 3), font, background, 
									addScene(
										getBattleScene(
											saveService, battleCode, Stance.DOGGY, Stance.DOGGY,
											aggregateMaps(
													getTextScenes(new String[]{"You defeated the brigand!.", "You get 1 XP."}, font, background, addScene(getEndScene(EndScene.Type.ENCOUNTER_OVER))),
													getTextScenes(getScript(battleCode, 4), font, background, addScene(getEndScene(EndScene.Type.GAME_OVER)))					
											)
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
				Texture vignetteTexture = assetManager.get("BlackVignetteBottom.png", Texture.class);
				background = new Background(backgroundTexture, vignetteTexture, 540, 720);
				Array<Mutation> mutations = new Array<Mutation>();
				mutations.add(new Mutation());
				getTextScenes(
					getScript(battleCode, 0), font, background, 
					addScene(
						getChoiceScene(
							assetManager, "Do you offer her something, or try to convince her?", new Array<String>(true, new String[]{"Offer her YOUR apple", "Plead with her"}, 0, 2),
							aggregateMaps(														
								getTextScenes(
									getScript(battleCode, 1), font, background,
									getTextScenes(
										new Array<String>(true, new String[]{"You receive 5 food from the dryad."}, 0, 1),
											font,
											background,
											new Array<Mutation>(true, new Mutation[]{new Mutation(saveService, SaveEnum.FOOD, 5)}, 0, 1),
											addScene(getEndScene(EndScene.Type.ENCOUNTER_OVER)
										)
									)
								),
								addScene(
									getCheckScene(
										assetManager, Stat.CHARISMA, new IntArray(new int[]{5, 0}), character,
										aggregateMaps(
											getTextScenes(
												getScript(battleCode, 2), font, background,
												getTextScenes(
													new Array<String>(true, new String[]{"You receive 10 food from the dryad."}, 0, 1),
													font,
													background,
													new Array<Mutation>(true, new Mutation[]{new Mutation(saveService, SaveEnum.FOOD, 10)}, 0, 1),
													addScene(getEndScene(EndScene.Type.ENCOUNTER_OVER))
												)
											),
											getTextScenes(
												getScript(battleCode, 3), font, background,
												addScene(getEndScene(EndScene.Type.ENCOUNTER_OVER))
											)
										)
									)
								)
							)					
						)
					)
				);
				break;
			default:
				getTextScenes(
					getScript(battleCode, 0), font, background, 
						addScene(
							getBattleScene(
							saveService, battleCode,
							aggregateMaps(
								getTextScenes(new String[]{"You won!  You get NOTHING.", "Sad :(", "What a pity.  Go away."}, font, background, addScene(getEndScene(EndScene.Type.ENCOUNTER_OVER))),
								getTextScenes(getScript(battleCode, 1), font, background, addScene(getEndScene(EndScene.Type.GAME_OVER)))					
							)
						)
					)
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
		
		int ii = 0;
		for (String scriptLine: script){
			Mutation toApply = new Mutation();
			if (mutations.size > ii){
				toApply = mutations.get(ii);
			}
			sceneMap = addScene(new TextScene(sceneMap, sceneCounter, saveService, font, background.clone(), scriptLine, new Array<Mutation>(true, new Mutation[]{toApply}, 0, 1)));
		}	
		return sceneMap;
	}
	
	private ChoiceScene getChoiceScene(AssetManager assetManager, String choiceDialogue, Array<String> buttonLabels, OrderedMap<Integer, Scene> sceneMap){
		// use sceneMap to generate the table
		Table table = new Table();

		Skin skin = assetManager.get("uiskin.json", Skin.class);
		Sound buttonSound = assetManager.get("sound.wav", Sound.class);
		Texture background = assetManager.get("DefaultBackground.jpg", Texture.class);
		
		ChoiceScene choiceScene = new ChoiceScene(sceneMap, sceneCounter, saveService, font, choiceDialogue, table, new Background(background));
		int ii = 0;
		for (String label  : buttonLabels){
			TextButton button = new TextButton(label, skin);
			button.addListener(getListener(choiceScene, sceneMap.get(sceneMap.orderedKeys().get(ii++)), buttonSound));
			table.add(button).row();
		}
				
		return choiceScene;
	}
	
	private ClickListener getListener(final AbstractChoiceScene currentScene, final Scene nextScene, final Sound buttonSound){
		return new ClickListener(){
	        @Override
	        public void clicked(InputEvent event, float x, float y) {
	        	buttonSound.play();
	        	// set new Scene as active based on choice
	        	nextScene.setActive();
	        	currentScene.finish();
	        }
	    };
	}
	
	// accepts a list of values, will map those values to scenes in the scenemap in order
	private CheckScene getCheckScene(AssetManager assetManager, Stat stat, IntArray checkValues, PlayerCharacter character, OrderedMap<Integer, Scene> sceneMap){
		Texture background = assetManager.get("DefaultBackground.jpg", Texture.class);
		OrderedMap<Integer, Scene> checkValueMap = new OrderedMap<Integer, Scene>();
		for (int ii = 0; ii < checkValues.size; ii++){
			checkValueMap.put(checkValues.get(ii), sceneMap.get(sceneMap.orderedKeys().get(ii)));
		}
		CheckScene checkScene = new CheckScene(sceneMap, sceneCounter, saveService, font, new Background(background), stat, checkValueMap, character);
		return checkScene;
	}
	
	private BattleScene getBattleScene(SaveService saveService, int battleCode, OrderedMap<Integer, Scene> sceneMap){
		return getBattleScene(saveService, battleCode, Stance.BALANCED, Stance.BALANCED, sceneMap);
	}
	
	private BattleScene getBattleScene(SaveService saveService, int battleCode, Stance playerStance, Stance enemyStance, OrderedMap<Integer, Scene> sceneMap){
		return new BattleScene(sceneMap, saveService, battleCode, playerStance, enemyStance);
	}
	
	private GameTypeScene getGameTypeScene(AssetManager assetManager, Array<String> buttonLabels, OrderedMap<Integer, Scene> sceneMap){
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
				
		return gameTypeScene;
	}
	
	private CharacterCreationScene getCharacterCreationScene(int sceneCode, SaveService saveService, BitmapFont font, Background background, AssetManager assetManager, PlayerCharacter character, OrderedMap<Integer, Scene> sceneMap){
		return new CharacterCreationScene(sceneMap, sceneCode, saveService, font, background, assetManager, character);
	}
	
	private SkillSelectionScene getSkillSelectionScene(int sceneCode, SaveService saveService, BitmapFont font, Background background, AssetManager assetManager, PlayerCharacter character, OrderedMap<Integer, Scene> sceneMap){
		return new SkillSelectionScene(sceneMap, sceneCode, saveService, font, background, assetManager, character);
	}
	
	private EndScene getEndScene(EndScene.Type type){
		return new EndScene(type);
	}
	// currently wrapped around any scene that needs to aggregate maps - instead, each of those can use this varargs syntax, then call this method with the array of maps to aggregate them
	private OrderedMap<Integer, Scene> aggregateMaps(OrderedMap<Integer, Scene>... sceneMaps){
		OrderedMap<Integer, Scene> aggregatedMap = new OrderedMap<Integer, Scene>();
		for (OrderedMap<Integer, Scene> map : sceneMaps){
			aggregatedMap.putAll(map);
		}
		return aggregatedMap;	
	}
	
	private String[] getScript(int battleCode, int scene){
		return getScript("00"+battleCode+"-"+ ( scene >= 10 ? scene : "0" + scene));
	}
	private String[] getScript(String code){
		return reader.loadScript(code);
	}
	
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

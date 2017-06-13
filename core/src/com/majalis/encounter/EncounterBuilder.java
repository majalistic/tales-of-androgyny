package com.majalis.encounter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
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
import com.majalis.asset.AnimatedActor;
import com.majalis.asset.AssetEnum;
import com.majalis.battle.BattleCode;
import com.majalis.battle.Battle.Outcome;
import com.majalis.character.PlayerCharacter;
import com.majalis.encounter.Background.BackgroundBuilder;
import com.majalis.character.SexualExperience.SexualExperienceBuilder;
import com.majalis.character.Stance;
import com.majalis.save.SaveEnum;
import com.majalis.save.SaveManager;
import com.majalis.save.SaveManager.GameContext;
import com.majalis.save.SaveService;
import com.majalis.scenes.AbstractChoiceScene;
import com.majalis.scenes.BattleScene;
import com.majalis.scenes.CheckScene;
import com.majalis.scenes.ChoiceScene;
import com.majalis.scenes.EndScene;
import com.majalis.scenes.Mutation;
import com.majalis.scenes.Scene;
import com.majalis.scenes.ShopScene;
import com.majalis.scenes.ShopScene.Shop;
import com.majalis.scenes.ShopScene.ShopCode;
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
	private final int sceneCode;
	private final ObjectMap<String, Shop> shops;
	private final PlayerCharacter character;
	private final GameContext returnContext;
	// can probably be replaced with a call to scenes.size
	private int sceneCounter;
	
	protected EncounterBuilder(EncounterReader reader, AssetManager assetManager, SaveService saveService, BitmapFont font, int sceneCode, ObjectMap<String, Shop> shops, PlayerCharacter character, GameContext returnContext) {
		scenes = new Array<Scene>();
		endScenes = new Array<EndScene>();
		battleScenes = new Array<BattleScene>();
		this.reader = reader;
		this.assetManager = assetManager;
		this.saveService = saveService;
		this.font = font;
		this.sceneCode = sceneCode;
		this.shops = shops == null ? new ObjectMap<String, Shop>() : shops;
		this.character = character;
		this.returnContext = returnContext;
		sceneCounter = 0;
	}
	/* different encounter "templates" */
	private Background getDefaultTextBackground() { return getDefaultTextBackground(assetManager.get(AssetEnum.DEFAULT_BACKGROUND.getTexture())); }
	
	private Background getDefaultTextBackground(Texture background) { return new BackgroundBuilder(background).setDialogBox(assetManager.get(AssetEnum.BATTLE_HOVER.getTexture())).build(); }
	
	@SuppressWarnings("unchecked")
	protected Encounter getRandomEncounter(EncounterCode encounterCode) {
		Texture backgroundTexture = assetManager.get(AssetEnum.DEFAULT_BACKGROUND.getTexture());	
		Background background = getDefaultTextBackground();
		Mutation analReceive = new Mutation(saveService, SaveEnum.ANAL, new SexualExperienceBuilder().setAnalSex(1, 1, 0).build());
		BattleCode battleCode;
		
		switch (encounterCode) {	
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
	
	private OrderedMap<Integer, Scene> getCheckScene(CheckType checkType, @SuppressWarnings("unchecked") OrderedMap<Integer, Scene>... sceneMaps) {
		OrderedMap<Integer, Scene> sceneMap = aggregateMaps(sceneMaps);
		Texture background = assetManager.get(AssetEnum.DEFAULT_BACKGROUND.getTexture());
		CheckScene checkScene = new CheckScene(sceneMap, sceneCounter, assetManager, saveService, font, new BackgroundBuilder(background).build(), checkType, sceneMap.get(sceneMap.orderedKeys().get(0)), sceneMap.get(sceneMap.orderedKeys().get(1)), character);
		return addScene(checkScene);
	}
	
	private OrderedMap<Integer, Scene> getBattleScene(BattleCode battleCode, @SuppressWarnings("unchecked") OrderedMap<Integer, Scene>... sceneMaps) {
		return getBattleScene(battleCode, Stance.BALANCED, Stance.BALANCED, sceneMaps);
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
	
	private Array<String> getScript(String code) {
		return getArray(reader.loadScript(code));
	}
	
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


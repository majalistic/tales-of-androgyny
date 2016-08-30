package com.majalis.encounter;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.OrderedMap;
import com.majalis.save.LoadService;
import com.majalis.save.SaveEnum;
import com.majalis.save.SaveManager;
import com.majalis.save.SaveService;
import com.majalis.scenes.BattleScene;
import com.majalis.scenes.ChoiceScene;
import com.majalis.scenes.EndScene;
import com.majalis.scenes.Mutation;
import com.majalis.scenes.Scene;
import com.majalis.scenes.TextScene;

/*
 * Retrieves encounters from internal files given an encounterId.  Need to create some kind of encounter builder helper class.
 */
public class EncounterFactory {
	
	private final AssetManager assetManager;
	private final SaveService saveService;
	private final LoadService loadService;
	private Array<Scene> scenes;
	private Array<EndScene> endScenes;
	private Array<BattleScene> battleScenes; 
	private int sceneCounter;
	
	public EncounterFactory(AssetManager assetManager, SaveManager saveManager){
		this.assetManager = assetManager;
		this.saveService = saveManager;
		this.loadService = saveManager;
	}
	
	public Encounter getEncounter(int encounterCode, BitmapFont font) {
		// temporarily stored in a static switch block until file retrieval for encounters is implemented
		Integer sceneCode = loadService.loadDataValue(SaveEnum.SCENE_CODE, Integer.class);
		switch (encounterCode){
			case 0: return getClassChoiceEncounter(font, sceneCode);
			case 1:	
			case 2: 
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
			case 8:
			case 9: return getRandomEncounter(font, sceneCode);
			default: return getDefaultEncounter(font, sceneCode);
		}
	}
	
	private Encounter getClassChoiceEncounter(BitmapFont font, Integer sceneCode){	
		Array<Scene> scenes = new Array<Scene>();
		Array<EndScene> endScenes = new Array<EndScene>();
		EndScene encounterEnd = new EndScene(new OrderedMap<Integer, Scene>(), EndScene.Type.ENCOUNTER_OVER);
		// Id 0
		endScenes.add(encounterEnd);
		scenes.add(encounterEnd);
		OrderedMap<Integer, Scene> sceneMap = new OrderedMap<Integer, Scene>();
		int ii = 1;
		for (SaveManager.JobClass jobClass: SaveManager.JobClass.values()){
			Scene newScene = new TextScene(getSceneMap(getSceneCodeList(0), getSceneList(encounterEnd)), ii, saveService, font, "You are now "+getJobClass(jobClass)+".", getMutationList(new Mutation(saveService, SaveEnum.CLASS, jobClass)));
			scenes.add(newScene);
			sceneMap.put(ii++, newScene);
		}
		ChoiceScene branch = new ChoiceScene(sceneMap, ii, saveService, assetManager, font);
		scenes.add(branch);
		Array<String> script = new Array<String>();
		script.addAll("Please choose your class.", "You're looking mighty fine.", "Welcome to the world of tRaPG!");
		sceneMap = getSceneMap(getSceneCodeList(ii++), getSceneList(branch));
		for (String scriptLine: script){
			Scene nextScene = new TextScene(sceneMap, ii, saveService, font, scriptLine, getMutationList(new Mutation()));
			scenes.add(nextScene);
			sceneMap = getSceneMap(getSceneCodeList(ii++), getSceneList(nextScene));
		}		
		return new Encounter(scenes, endScenes, new Array<BattleScene>(), getStartScene(scenes, sceneCode));
	}
	
	private Encounter getRandomEncounter(BitmapFont font, Integer sceneCode){
		scenes = new Array<Scene>();
		endScenes = new Array<EndScene>();
		battleScenes = new Array<BattleScene>();
			
		int battleCode = new IntArray(new int[]{0,1}).random();
		sceneCounter = 0;
		OrderedMap<Integer, Scene> sceneMap = addScene(
				getSceneList(
				new TextScene(addScene(new EndScene(new OrderedMap<Integer, Scene>(), EndScene.Type.ENCOUNTER_OVER)), sceneCounter, saveService, font, "You won! You get NOTHING.", getMutationList(new Mutation())),
				new TextScene(addScene(new EndScene(new OrderedMap<Integer, Scene>(), EndScene.Type.GAME_OVER)), sceneCounter, saveService, font, getDefeatText(battleCode), getMutationList(new Mutation()))
				)
		);
		
		sceneMap = addScene(new BattleScene(sceneMap, saveService, battleCode));				
		scenes.addAll(getTextScenes(new String[]{getIntroText(battleCode), "There is nothing left here to do.", "It's so random. :^)", "You encounter a random encounter!"}, sceneMap, font));	
		System.out.println(sceneCode);
		System.out.println(getStartScene(scenes, sceneCode));
		return new Encounter(scenes, endScenes, battleScenes, getStartScene(scenes, sceneCode));	
	}
	
	private OrderedMap<Integer, Scene> addScene(Scene scene){
		return addScene(getSceneList(scene));
	}
	
	// pass in multiple scenes that the next scene will branch into
	private OrderedMap<Integer, Scene> addScene(Array<Scene> scenes){
		IntArray sceneCodes = new IntArray();
		for (Scene scene : scenes){
			this.scenes.add(scene);
			if (scene instanceof BattleScene) battleScenes.add((BattleScene)scene);
			if (scene instanceof EndScene) endScenes.add((EndScene)scene);
			sceneCodes.add(sceneCounter++);
		}
		return getSceneMap(sceneCodes, scenes);
	}
	
	private Array<Scene> getTextScenes(String[] script, OrderedMap<Integer, Scene> sceneMap, BitmapFont font){
		return getTextScenes(new Array<String>(true, script, 0, script.length), sceneMap, font);
	}
	
	// pass in a list of script scenes that will follow one another in reverse order
	private Array<Scene> getTextScenes(Array<String> script, OrderedMap<Integer, Scene> sceneMap, BitmapFont font){
		Array<Scene> scenes = new Array<Scene>();
		for (String scriptLine: script){
			sceneMap = addScene(new TextScene(sceneMap, sceneCounter, saveService, font, scriptLine, getMutationList(new Mutation())));
		}	
		return scenes;
	}
	
	private String getDefeatText(int battleCode){
		switch(battleCode){
			case 0:
				return "You lost! You get knotty werewolf cock! (up the butt).";
			default:
				return "You lost! The harpy mounts you! (up the butt).";
		}
	}
	
	private String getIntroText(int battleCode){
		switch(battleCode){
			case 0:
				return "No wait lol there's a basic werebitch, RAWR.";
			default:
				return "Wait actually that harpy looks like she wants to drill you silly!";
		}
	}
	
	private String getJobClass(SaveManager.JobClass jobClass){
		if (jobClass == SaveManager.JobClass.ENCHANTRESS){
			return "an Enchantress";
		}
		return "a " + jobClass.getLabel();
	}
	
	private Encounter getDefaultEncounter(BitmapFont font, int sceneCode){
		Array<Scene> scenes = new Array<Scene>();
		Array<EndScene> endScenes = new Array<EndScene>();
		EndScene encounterEnd = new EndScene(new OrderedMap<Integer, Scene>(), EndScene.Type.ENCOUNTER_OVER);
		// Id 0
		endScenes.add(encounterEnd);
		scenes.add(encounterEnd);
		Array<String> script = new Array<String>();
		script.addAll("There is nothing left here to do.", "It's actually rather sexy looking.", "You encounter a stick!");
		Integer ii = 1;
		OrderedMap<Integer, Scene> sceneMap = getSceneMap(getSceneCodeList(ii++), getSceneList(encounterEnd));
		for (String scriptLine: script){
			Scene nextScene = new TextScene(sceneMap, ii, saveService, font, scriptLine, getMutationList(new Mutation()));
			scenes.add(nextScene);
			sceneMap = getSceneMap(getSceneCodeList(ii++), getSceneList(nextScene));
			
		}		
		return new Encounter(scenes, endScenes, new Array<BattleScene>(), getStartScene(scenes, sceneCode));
	}
	
	private IntArray getSceneCodeList(int... integers){
		return new IntArray(integers);
	}
	
	private Array<Scene> getSceneList(Scene... scenes){
		return new Array<Scene>(true, scenes, 0, scenes.length);
	}
	
	private OrderedMap<Integer, Scene> getSceneMap(IntArray integers, Array<Scene> scenes){
		OrderedMap<Integer, Scene> sceneMap = new OrderedMap<Integer, Scene>();
		for (int ii = 0; ii < integers.size; ii++){
			sceneMap.put(integers.get(ii), scenes.get(ii));
		}
		return sceneMap;
	}
	
	private Array<Mutation> getMutationList(Mutation... mutations){
		Array<Mutation> mutationArray = new Array<Mutation>();
		for (Mutation mutation : mutations){
			mutationArray.add(mutation);
		}
		return mutationArray;
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

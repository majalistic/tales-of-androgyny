package com.majalis.traprpg;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

/*
 * Retrieves encounters from internal files given an encounterId
 */
public class EncounterFactory {
	
	private final AssetManager assetManager;
	private final SaveService saveService;
	private final LoadService loadService;
	
	public EncounterFactory(AssetManager assetManager, SaveManager saveManager){
		this.assetManager = assetManager;
		this.saveService = saveManager;
		this.loadService = saveManager;
	}
	
	public Encounter getEncounter(int encounterCode, BitmapFont font) {
		// temporarily stored in a static switch block until file retrieval for encounters is implemented
		Integer sceneCode = loadService.loadDataValue("SceneCode", Integer.class);
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
		EndScene encounterEnd = new EndScene(new ObjectMap<Integer, Scene>(), EndScene.Type.ENCOUNTER_OVER);
		// Id 0
		endScenes.add(encounterEnd);
		scenes.add(encounterEnd);
		ObjectMap<Integer, Scene> sceneMap = new ObjectMap<Integer, Scene>();
		int ii = 1;
		for (SaveManager.JobClass jobClass: SaveManager.JobClass.values()){
			Scene newScene = new TextScene(getSceneMap(getSceneCodeList(0), getSceneList(encounterEnd)), ii, saveService, font, "You are now "+getJobClass(jobClass)+".", getMutationList(new Mutation(saveService, "Class", jobClass)));
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
		return new Encounter(scenes, endScenes, getStartScene(scenes, sceneCode));
	}
	
	private Encounter getRandomEncounter(BitmapFont font, Integer sceneCode){
		Array<Scene> scenes = new Array<Scene>();
		Array<EndScene> endScenes = new Array<EndScene>();
		EndScene encounterEnd = new EndScene(new ObjectMap<Integer, Scene>(), EndScene.Type.ENCOUNTER_OVER);
		// Id 0
		endScenes.add(encounterEnd);
		scenes.add(encounterEnd);
		Array<String> script = new Array<String>();
		script.addAll("There is nothing left here to do.", "It's so random. :^)", "You encounter a random encounter!");
		Integer ii = 1;
		ObjectMap<Integer, Scene> sceneMap = getSceneMap(getSceneCodeList(ii++), getSceneList(encounterEnd));
		for (String scriptLine: script){
			Scene nextScene = new TextScene(sceneMap, ii, saveService, font, scriptLine, getMutationList(new Mutation()));
			scenes.add(nextScene);
			sceneMap = getSceneMap(getSceneCodeList(ii++), getSceneList(nextScene));
			
		}		
		return new Encounter(scenes, endScenes, getStartScene(scenes, sceneCode));	
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
		EndScene encounterEnd = new EndScene(new ObjectMap<Integer, Scene>(), EndScene.Type.ENCOUNTER_OVER);
		// Id 0
		endScenes.add(encounterEnd);
		scenes.add(encounterEnd);
		Array<String> script = new Array<String>();
		script.addAll("There is nothing left here to do.", "It's actually rather sexy looking.", "You encounter a stick!");
		Integer ii = 1;
		ObjectMap<Integer, Scene> sceneMap = getSceneMap(getSceneCodeList(ii++), getSceneList(encounterEnd));
		for (String scriptLine: script){
			Scene nextScene = new TextScene(sceneMap, ii, saveService, font, scriptLine, getMutationList(new Mutation()));
			scenes.add(nextScene);
			sceneMap = getSceneMap(getSceneCodeList(ii++), getSceneList(nextScene));
			
		}		
		return new Encounter(scenes, endScenes, getStartScene(scenes, sceneCode));
	}
	
	private Array<Integer> getSceneCodeList(int... integers){
		Array<Integer> codes = new Array<Integer>();
		for (int ii : integers){
			codes.add(ii);
		}
		return codes;
	}
	
	private Array<Scene> getSceneList(Scene... scenes){
		Array<Scene> scenesArray = new Array<Scene>();
		for (Scene scene : scenes){
			scenesArray.add(scene);
		}
		return scenesArray;
	}
	
	private ObjectMap<Integer, Scene> getSceneMap(Array<Integer> integers, Array<Scene> scenes){
		ObjectMap<Integer, Scene> sceneMap = new ObjectMap<Integer, Scene>();
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
		if (sceneCode == 0){
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

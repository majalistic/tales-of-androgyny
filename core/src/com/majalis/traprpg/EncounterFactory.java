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
		switch (encounterCode){
			case 0:
			case 1:
			case 2: 
			default: return getClassChoiceEncounter(font);
		}
	}
	
	private Encounter getClassChoiceEncounter(BitmapFont font){
		Array<Scene> scenes = new Array<Scene>();
		Array<EndScene> endScenes = new Array<EndScene>();
		EndScene encounterEnd = new EndScene(new ObjectMap<Integer, Scene>(), EndScene.Type.ENCOUNTER_OVER);
		// Id 0
		endScenes.add(encounterEnd);
		scenes.add(encounterEnd);
		ObjectMap<Integer, Scene> classMap = new ObjectMap<Integer, Scene>();
		int ii = 1;
		for (GameWorldManager.ClassEnum jobClass: GameWorldManager.ClassEnum.values()){
			Scene newScene = new TextScene(getSceneMap(getSceneCodeList(0), getSceneList(encounterEnd)), font, "You are now a "+jobClass.getLabel().replace("Enchanter", "Enchantress")+".", getMutationList(new Mutation(saveService, "Class", jobClass)));
			scenes.add(newScene);
			classMap.put(ii++, newScene);
		}
		ChoiceScene branch = new ChoiceScene(classMap, assetManager, font);
		scenes.add(branch);
		scenes.add(new TextScene(getSceneMap(getSceneCodeList(ii), getSceneList(branch)), font, "Please choose your class.", getMutationList(new Mutation())));
		return new Encounter(scenes, endScenes);
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
	
}

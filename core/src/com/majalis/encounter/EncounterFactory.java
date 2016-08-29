package com.majalis.encounter;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.ObjectMap;
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
		EndScene encounterEnd = new EndScene(new ObjectMap<Integer, Scene>(), EndScene.Type.ENCOUNTER_OVER);
		// Id 0
		endScenes.add(encounterEnd);
		scenes.add(encounterEnd);
		ObjectMap<Integer, Scene> sceneMap = new ObjectMap<Integer, Scene>();
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
		int battleCode = new IntArray(new int[]{0,1}).random();
		Array<Scene> scenes = new Array<Scene>();
		Array<EndScene> endScenes = new Array<EndScene>();
		Array<BattleScene> battleScenes = new Array<BattleScene>();
		EndScene encounterEnd = new EndScene(new ObjectMap<Integer, Scene>(), EndScene.Type.ENCOUNTER_OVER);
		// Id 0
		endScenes.add(encounterEnd);
		scenes.add(encounterEnd);
		
		EndScene gameEnd = new EndScene(new ObjectMap<Integer, Scene>(), EndScene.Type.GAME_OVER);
		// Id 1
		endScenes.add(gameEnd);
		scenes.add(gameEnd);	
		ObjectMap<Integer, Scene> sceneMap = getSceneMap(getSceneCodeList(0), getSceneList(encounterEnd));
		
		Scene winScene = new TextScene(sceneMap, 2, saveService, font, "You won! You get NOTHING.", getMutationList(new Mutation()));
		scenes.add(winScene);
		
		sceneMap = getSceneMap(getSceneCodeList(1), getSceneList(gameEnd));
		
		Scene loseScene = new TextScene(sceneMap, 3, saveService, font, getDefeatText(battleCode), getMutationList(new Mutation()));
		scenes.add(loseScene);
		
		sceneMap = getSceneMap(getSceneCodeList(2, 3), getSceneList(winScene, loseScene));
				
		BattleScene battleScene = new BattleScene(sceneMap, saveService, battleCode, 2, 3);
		scenes.add(battleScene);
		battleScenes.add(battleScene);
		sceneMap = getSceneMap(getSceneCodeList(4), getSceneList(battleScene));	
		
		Scene moreScene = new TextScene(sceneMap, 5, saveService, font, getIntroText(battleCode), getMutationList(new Mutation()));
		scenes.add(moreScene);
		sceneMap = getSceneMap(getSceneCodeList(5), getSceneList(moreScene));	
		
		Integer ii = 6;
		Array<String> script = new Array<String>();
		script.addAll("There is nothing left here to do.", "It's so random. :^)", "You encounter a random encounter!");
		
		for (String scriptLine: script){
			Scene nextScene = new TextScene(sceneMap, ii, saveService, font, scriptLine, getMutationList(new Mutation()));
			scenes.add(nextScene);
			sceneMap = getSceneMap(getSceneCodeList(ii++), getSceneList(nextScene));
		}	
				
		return new Encounter(scenes, endScenes, battleScenes, getStartScene(scenes, sceneCode));	
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
		return new Encounter(scenes, endScenes, new Array<BattleScene>(), getStartScene(scenes, sceneCode));
	}
	
	private IntArray getSceneCodeList(int... integers){
		return new IntArray(integers);
	}
	
	private Array<Scene> getSceneList(Scene... scenes){
		return new Array<Scene>(true, scenes, 0, scenes.length);
	}
	
	private ObjectMap<Integer, Scene> getSceneMap(IntArray integers, Array<Scene> scenes){
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

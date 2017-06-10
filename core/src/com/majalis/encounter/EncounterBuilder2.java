package com.majalis.encounter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.OrderedMap;
import com.majalis.asset.AssetEnum;
import com.majalis.battle.BattleCode;
import com.majalis.battle.Battle.Outcome;
import com.majalis.character.EnemyCharacter;
import com.majalis.character.EnemyEnum;
import com.majalis.character.PlayerCharacter;
import com.majalis.character.Stance;
import com.majalis.save.ProfileEnum;
import com.majalis.save.SaveEnum;
import com.majalis.save.SaveManager;
import com.majalis.save.SaveService;
import com.majalis.save.SaveManager.GameContext;
import com.majalis.scenes.BattleScene;
import com.majalis.scenes.EndScene;
import com.majalis.scenes.Mutation;
import com.majalis.scenes.Scene;
import com.majalis.scenes.TextScene;
import com.majalis.scenes.ShopScene.Shop;
import com.majalis.encounter.Background.BackgroundBuilder;

public class EncounterBuilder2 {
	private final EncounterReader2 reader;
	private final AssetManager assetManager;
	private final SaveService saveService;
	private final BitmapFont font;
	private final BitmapFont smallFont;
	private final int sceneCode;
	private final ObjectMap<String, Shop> shops;
	private final PlayerCharacter character;
	private final GameContext returnContext;
	private final OrderedMap<Integer, Scene> masterSceneMap;
	// can probably be replaced with a call to scenes.size
	private int sceneCounter;
	
	protected EncounterBuilder2(EncounterReader2 reader, AssetManager assetManager, SaveService saveService, BitmapFont font, BitmapFont smallFont, int sceneCode, ObjectMap<String, Shop> shops, PlayerCharacter character, GameContext returnContext) {
		this.reader = reader;
		this.assetManager = assetManager;
		this.saveService = saveService;
		this.font = font;
		this.smallFont = smallFont;
		this.sceneCode = sceneCode;
		this.shops = shops == null ? new ObjectMap<String, Shop>() : shops;
		this.character = character;
		this.returnContext = returnContext;
		sceneCounter = 1;
		masterSceneMap = new OrderedMap<Integer, Scene>();
	}
	
	// this needs to be moved into EncounterCode
	protected Encounter getEncounter() {
		BattleCode battleCode = BattleCode.WERESLUT;
		
		// should probably be BattleBranch?
		return new Branch()
		    .textScene("WEREWOLF-INTRO").battleScene(
		    	battleCode,
		    	// this has a reference to the first node in this branch, which gets welded with the current context node
		        new Branch(Outcome.VICTORY).textScene("WEREWOLF-VICTORY").encounterEnd(),
		        new Branch(Outcome.KNOT).textScene("WEREWOLF-KNOT").gameEnd(),
		        new Branch(Outcome.DEFEAT).textScene("WEREWOLF-DEFEAT").encounterEnd(),
		        new Branch(Outcome.SATISFIED).textScene("WEREWOLF-SATISFIED").encounterEnd()
		    ).getEncounter();
	}

	enum EndTokenType {
		Choice,
		Check,
		Battle,
		EndEncounter,
		EndGame
	}
	public class Branch {
		
		Array<SceneToken> sceneTokens;
		ObjectMap<Object, Branch> branchOptions;
		Object key;
		BranchToken branchToken;
		BattleCode battleCode;
		Stance playerStance;
		Stance enemyStance;
		boolean disarm;
		int climaxCounter;
		
		boolean preprocessed;
		Array<Scene> scenes;
		Array<BattleScene> battleScenes;
		Array<EndScene> endScenes;
		
		public Branch () {
			init();
		}
		public Branch (Outcome type) {
			init();
			key = type;
		}
		
		private void init() {
			sceneTokens = new Array<SceneToken>();
			branchOptions = new ObjectMap<Object, Branch>();
		}
		
		public Branch textScene(String key) {
			// this needs to persist certain things like sound/music/background until they're changed - the tokens themselves should have a reference to that persistence
			// if field == null, populate current running value
			// need to be careful with this - should only populate background if animatedBackground is null and vice versa as well
			sceneTokens.addAll(reader.loadScript(key));
			return this;
		}
		
		public Branch battleScene(BattleCode battleCode, Branch ... branches) { return battleScene(battleCode, Stance.BALANCED, Stance.BALANCED, branches); }		
		public Branch battleScene(BattleCode battleCode, Stance playerStance, Stance enemyStance, Branch ... branches) {
			// for each of the branches, add them to the next map with their associated code
			branchToken = new BattleSceneToken(battleCode);
			for (Branch branch : branches) {
				// this needs to call a method to persist certain things on each of these branches
				// if field == null, populate current running value
				// need to be careful with this - should only populate background if animatedBackground is null and vice versa as well
				branchOptions.put(branch.getKey(), branch);
			}
			this.battleCode = battleCode;
			this.playerStance = playerStance;
			this.enemyStance = enemyStance;
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
			sceneTokens.get(0).preprocess(startBackground, startForeground, startAnimatedForeground);			
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
			
			if (branchToken != null) {
				switch (branchToken.type) {
					case Battle:
						// for each branch get the scenes, the first entry in that list is what this branchToken scene should be tied to
						ObjectMap<String, Integer> outcomeToScene = new ObjectMap<String, Integer>();
						for (ObjectMap.Entry<Object, Branch> next : branchOptions) {
							next.value.preprocess();
							// grab the first scene, add it to the map, use the map to generate the appropriate scene
							battleScenes.addAll(next.value.getBattleScenes());
							endScenes.addAll(next.value.getEndScenes());
							Array<Scene> nextScenes = next.value.getScenes();
							scenes.addAll(nextScenes);
							Scene nextScene = nextScenes.first();
							sceneMap.put(nextScene.getCode(), nextScene);
							outcomeToScene.put(((Outcome) next.key).toString(), nextScene.getCode());
						}
						
						BattleScene newBattleScene = new BattleScene(sceneMap, saveService, battleCode, playerStance, enemyStance, disarm, climaxCounter, outcomeToScene);
						scenes.add(newBattleScene);
						battleScenes.add(newBattleScene);
						sceneMap = new OrderedMap<Integer, Scene>();
						sceneMap.put(newBattleScene.getCode(), newBattleScene);
					case Check:
						break;
					case Choice:
						break;
					case EndGame:
					case EndEncounter:
						EndScene newEndScene;
						if (branchToken.type == EndTokenType.EndEncounter) newEndScene = new EndScene(EndScene.Type.ENCOUNTER_OVER, saveService, returnContext);
						else newEndScene = new EndScene(EndScene.Type.GAME_OVER, saveService, SaveManager.GameContext.GAME_OVER);
						endScenes.add(newEndScene);
						scenes.add(newEndScene);
						sceneMap = new OrderedMap<Integer, Scene>();
						sceneMap.put(newEndScene.getCode(), newEndScene);
						break;
					default:
						break;
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
			
			// iterate through and every time either background or foreground/animatedforeground change, create a new background
			for (SceneToken token: sceneTokens) {
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
					if (token.animatedForeground != null) backgroundBuilder.setForeground(EnemyCharacter.getAnimatedActor(token.animatedForeground), 0, 0);
					else if (animatedForeground != null) backgroundBuilder.setForeground(EnemyCharacter.getAnimatedActor(animatedForeground), 0, 0);
					else if (token.foreground != null) backgroundBuilder.setForeground(assetManager.get(token.foreground.getTexture()));
					else if (foreground != null) backgroundBuilder.setForeground(assetManager.get(foreground.getTexture()));
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
				String scriptLine = token.text.replace("<NAME>", characterName).replace("<BUTTSIZE>", buttsize).replace("<LIPSIZE>", lipsize);
				// create the scene
				Scene newScene = new TextScene(sceneMap, sceneCounter, assetManager, font, saveService, backgrounds.get(ii++), scriptLine, getMutations(token.mutations), character, token.music != null ? token.music.getMusic() : null, token.sound != null ? token.sound.getSound() : null);
				// add it to array
				scenes.add(newScene);
				// use it to make the map
				sceneMap = new OrderedMap<Integer, Scene>();
				masterSceneMap.put(sceneCounter, newScene);
				sceneMap.put(sceneCounter++, newScene);
			}
			
			scenes.reverse();
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
	
	// this should have playerstance, disarm, etc. - right now this is basically unused
	public class EndSceneToken extends BranchToken {
		public EndSceneToken(EndTokenType endType) {
			super(endType);
		}
	}
	
	// as scenetokens arrays are retrieved, they're placed into a map of key to scene token array to prevent duplicates - and another map is used for the actual scenes so they aren't duplicated (scenes are individual at that point, not in an array, and that key is the new scenecode) - this is currently not implemented
	
	// this represents a text-like scene - it should be able to display text, show who is talking, display a new background, play an animation, play a sound, mutate the game state 
	// these will be serialized into the actual script and have their own key
	public static class SceneToken {
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
			if (startAnimatedForeground == null) animatedForeground = startAnimatedForeground;
		}
		
		// this should also have a fontEnum to determine what font the text is displayed in
		
	}
	
	public static class MutateToken {
		SaveEnum saveType;
		ProfileEnum profileSaveType;
		Object value;
				
		public Mutation getMutation(SaveService saveService) {
			if (saveType != null) { return new Mutation(saveService, saveType, value); }
			else return new Mutation(saveService, profileSaveType, value);
		}
	}
	
	public static class EncounterReader2 {
		private final FileHandle file;   
		private final ObjectMap<String, SceneToken[]> scriptData;
		
	    public EncounterReader2(String path){
	        file = Gdx.files.internal(path);
	        scriptData = getScriptData();
	    }
	   
		private ObjectMap<String, SceneToken[]> getScriptData(){
			ObjectMap<String, SceneToken[]> data = new ObjectMap<String, SceneToken[]>();
	        if(file.exists()){
	        	data = convertToIntMap(new Json().fromJson(FullScript.class, file.readString()));
	        }
	        return data;
	    }
		
		private ObjectMap<String, SceneToken[]> convertToIntMap(FullScript data){
			ObjectMap<String, SceneToken[]> convertedData = new ObjectMap<String, SceneToken[]>();
			for (ScriptData datum: data.script){
				convertedData.put(datum.key, datum.scriptLines);
			}
			return convertedData;
		}
	  
	    public SceneToken[] loadScript(String key){
	    	return scriptData.get(key);
	    }
	   
	}
	
	private static class FullScript{
    	public ScriptData[] script;
    	// 0-arg constructor for JSON serialization: DO NOT USE
		private FullScript(){}

    }
    
    /* package for containing the data in a pretty format, an array of which deserializes to an IntMap */
    private static class ScriptData{
    	public String key;
    	public SceneToken[] scriptLines;
    	// 0-arg constructor for JSON serialization: DO NOT USE
		private ScriptData(){}
    }

	
	
}

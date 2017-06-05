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
		sceneCounter = 0;
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
		End
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
			return this;
		}
		
		public Branch gameEnd() {
			return this;
		}
		
		private void upsertScenes() {
			if (scenes != null) return;
			scenes = new Array<Scene>();
			battleScenes = new Array<BattleScene>();
			endScenes = new Array<EndScene>();
			OrderedMap<Integer, Scene> sceneMap = new OrderedMap<Integer, Scene>();
			
			switch (branchToken.type) {
				case Battle:
					// for each branch get the scenes, the first entry in that list is what this branchToken scene should be tied to
					ObjectMap<String, Integer> outcomeToScene = new ObjectMap<String, Integer>();
					for (ObjectMap.Entry<Object, Branch> next : branchOptions) {
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
					sceneMap = new OrderedMap<Integer, Scene>();
					sceneMap.put(newBattleScene.getCode(), newBattleScene);
				case Check:
					break;
				case Choice:
					break;
				case End:
					break;
				default:
					break;
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
			
			for (SceneToken token: sceneTokens) {
				// if all of the tokens are  the same, clone the last background
				if (token.foreground == foreground && token.animatedForeground == animatedForeground && token.background == background) {
					backgrounds.add(backgrounds.get(backgrounds.size - 1).clone());
				}
				else if (token.foreground != null) {
					backgrounds.add(new BackgroundBuilder(assetManager.get(token.background.getTexture())).setDialogBox(dialogBoxTexture).setForeground(assetManager.get(token.foreground.getTexture())).build());
				}
				else if (token.animatedForeground != null) {
					backgrounds.add(new BackgroundBuilder(assetManager.get(token.background.getTexture())).setDialogBox(dialogBoxTexture).setForeground(EnemyCharacter.getAnimatedActor(animatedForeground), 0, 0).build());	
				}
				else {
					backgrounds.add(new BackgroundBuilder(assetManager.get(token.background.getTexture())).setDialogBox(dialogBoxTexture).build());
				}
			}
			
			backgrounds.reverse();
			
			// taking the branchToken scene and use it as the entrypoint, traversing the sceneTokens backwards and putting them into each other
			int ii = 0;
			for (SceneToken token: sceneTokens) {
				String scriptLine = token.text.replace("<NAME>", characterName).replace("<BUTTSIZE>", buttsize).replace("<LIPSIZE>", lipsize);
				// create the scene
				Scene newScene = new TextScene(sceneMap, sceneCounter, assetManager, font, saveService, backgrounds.get(ii++), scriptLine, getMutations(token.mutations), character, token.music.getMusic(), token.sound.getSound());
				// add it to array
				
				// use it to make the map
				
			}
			
			scenes.reverse();
			
		}
		
		/*
		 * 
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
		
		private OrderedMap<Integer, Scene> aggregateMaps(@SuppressWarnings("unchecked") OrderedMap<Integer, Scene>... sceneMaps) {
		OrderedMap<Integer, Scene> aggregatedMap = new OrderedMap<Integer, Scene>();
			for (OrderedMap<Integer, Scene> map : sceneMaps) {
				aggregatedMap.putAll(map);
			}
			return aggregatedMap;	
		}
		
		
		private OrderedMap<Integer, Scene> getBattleScene(BattleCode battleCode, Stance playerStance, Stance enemyStance, boolean disarm, int climaxCounter, Array<Outcome> outcomes, @SuppressWarnings("unchecked") OrderedMap<Integer, Scene>... sceneMaps) {
			OrderedMap<Integer, Scene> sceneMap = aggregateMaps(sceneMaps);
			ObjectMap<String, Integer> outcomeToScene = new ObjectMap<String, Integer>();
			for (int ii = 0; ii < outcomes.size; ii++) {
				outcomeToScene.put(outcomes.get(ii).toString(), sceneMap.get(sceneMap.orderedKeys().get(ii)).getCode());
			}
			
			return addScene(new BattleScene(aggregateMaps(sceneMaps), saveService, battleCode, playerStance, enemyStance, disarm, climaxCounter, outcomeToScene));
		}*/
		
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
			return scenes.get(0);
		}
		
		public Encounter getEncounter() {
			return new Encounter(getScenes(), getEndScenes(), getBattleScenes(), getStartScene());
		}
		
	}
	
	
	public Array<Mutation> getMutations(Array<MutateToken> tokens) {
		Array<Mutation> mutations = new Array<Mutation>();
		for (MutateToken token: tokens) {
			mutations.add(token.getMutation());
		}
		return mutations;	
	}
	
	public class BranchToken {
		private final EndTokenType type;
		EndTokenType getType() { return type; }
		protected BranchToken(EndTokenType type) {
			this.type = type;
		}
	}
	
	public class BattleSceneToken extends BranchToken {
		private final BattleCode battleCode;
		public BattleSceneToken(BattleCode battleCode) {
			super(EndTokenType.Battle);
			this.battleCode = battleCode;
		}
		
	}
	// as scenetokens arrays are retrieved, they're placed into a map of key to scene token array to prevent duplicates - and another map is used for the actual scenes so they aren't duplicated (scenes are individual at that point, not in an array, and that key is the new scenecode)
	
	// this represents a text-like scene - it should be able to display text, show who is talking, display a new background, play an animation, play a sound, mutate the game state, 
	// these will be serialized into the actual script and have their own key
	public class SceneToken {
		String text;
		String speaker;
		AssetEnum background;
		AssetEnum foreground;
		EnemyEnum animatedForeground;
		AssetEnum sound;
		AssetEnum music;
		Array<MutateToken> mutations;
		
		// this should also have a fontEnum to determine what font the text is displayed in
		
	}
	
	public class MutateToken {
		SaveEnum saveType;
		ProfileEnum profileSaveType;
		Object value;
		
		public Mutation getMutation() {
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

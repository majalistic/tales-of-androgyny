package com.majalis.encounter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
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
import com.majalis.character.AbstractCharacter.Stat;
import com.majalis.save.ProfileEnum;
import com.majalis.save.SaveEnum;
import com.majalis.save.SaveManager;
import com.majalis.save.SaveService;
import com.majalis.save.SaveManager.GameContext;
import com.majalis.scenes.AbstractChoiceScene;
import com.majalis.scenes.BattleScene;
import com.majalis.scenes.CheckScene;
import com.majalis.scenes.ChoiceScene;
import com.majalis.scenes.EndScene;
import com.majalis.scenes.Mutation;
import com.majalis.scenes.Scene;
import com.majalis.scenes.TextScene;
import com.majalis.scenes.ShopScene.Shop;
import com.majalis.encounter.Background.BackgroundBuilder;
import com.majalis.encounter.EncounterBuilder.ChoiceCheckType;

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
	protected Encounter getEncounter(EncounterCode encounterCode) {
		switch (encounterCode) {
			case ADVENTURER:
				break;
			case BEASTMISTRESS:
				break;
			case BRIGAND:
				Branch[] battleBranches2 = new Branch[]{new Branch(Outcome.VICTORY).textScene("BRIGAND-VICTORY").encounterEnd(), new Branch(Outcome.DEFEAT).textScene("BRIGAND-DEFEAT").encounterEnd(), new Branch(Outcome.SATISFIED).textScene("BRIGAND-SATISFIED").encounterEnd()};
				return new Branch().textScene("BRIGAND-INTRO").checkScene(
					Stat.PERCEPTION, 
					new Branch(6).textScene("BRIGAND-SPOT").choiceScene(
						"How do you handle the brigand?",
						new Branch("Charge").battleScene(
							BattleCode.BRIGAND, Stance.OFFENSIVE, Stance.BALANCED,
							battleBranches2
						),
						new Branch("Ready an Arrow").battleScene(
							BattleCode.BRIGAND, 
							battleBranches2
						),
						new Branch("Speak").textScene("BRIGAND-HAIL").choiceScene(
							"Accept her offer?",
							new Branch("Accept").require(ChoiceCheckType.LEWD).textScene("BRIGAND-ACCEPT").choiceScene(
								"Tell her to pull out?",
								new Branch("Say Nothing").textScene("BRIGAND-CATCH").encounterEnd(),
								new Branch("Ask her").textScene("BRIGAND-REQUEST").checkScene(
									Stat.CHARISMA,
									new Branch(4).textScene("BRIGAND-FACIAL").encounterEnd(),
									new Branch(0).textScene("BRIGAND-BADTASTE").encounterEnd()
								)
							),
							new Branch("Decline").textScene("BRIGAND-DECLINE").checkScene(
								Stat.CHARISMA,
								new Branch(5).textScene("BRIGAND-CONVINCE").encounterEnd(),
								new Branch(0).textScene("BRIGAND-FAIL").battleScene(
									BattleCode.BRIGAND, 
									battleBranches2
								)
							)
						)
					),
					new Branch(4).textScene("BRIGAND-STAB").battleScene(
						BattleCode.BRIGAND, 
						battleBranches2
					),
					new Branch(0).textScene("BRIGAND-BACKSTAB").battleScene(
						BattleCode.BRIGAND, Stance.STANDING_BOTTOM, Stance.STANDING,
						battleBranches2	
					)	
				).getEncounter();
			case CAMP_AND_EAT:
				break;
			case CENTAUR:
				break;
			case COTTAGE_TRAINER:
				break;
			case COTTAGE_TRAINER_VISIT:
				break;
			case CRIER_QUEST:
				break;
			case DEFAULT:
				break;
			case DRYAD:
				return new Branch().textScene("DRYAD-INTRO").choiceScene(
					"Do you offer her YOUR apple, or try to convince her to just hand it over?",
					new Branch("Offer (Requires: Catamite)").require(ChoiceCheckType.LEWD).textScene("DRYAD-OFFER").encounterEnd(),
					new Branch("Plead with her").checkScene(
						Stat.CHARISMA,
						new Branch(5).textScene("DRYAD-CONVINCE"),
						new Branch(0).textScene("DRYAD-FAIL")
					)
			    ).getEncounter();
			case ECCENTRIC_MERCHANT:
				break;
			case ERROR:
				break;
			case FIRST_BATTLE_STORY:
				break;
			case FORT:
				break;
			case GADGETEER:
				break;
			case GOBLIN:
				break;
			case HARPY:
				Branch[] battleBranches = new Branch[]{new Branch(Outcome.VICTORY).textScene("HARPY-VICTORY").encounterEnd(), new Branch(Outcome.DEFEAT).textScene("HARPY-DEFEAT").gameEnd(), new Branch(Outcome.SATISFIED).textScene("HARPY-SATISFIED").encounterEnd()};
				return new Branch().textScene("HARPY-INTRO").checkScene(
					Stat.AGILITY,
					new Branch(6).textScene("HARPY-DODGE").battleScene(
						BattleCode.HARPY, Stance.BALANCED, Stance.PRONE,
						battleBranches
					),
					new Branch(4).textScene("HARPY-DUCK").battleScene(
						BattleCode.HARPY, Stance.KNEELING, Stance.BALANCED,
						battleBranches
					),
					new Branch(0).textScene("HARPY-HORK").battleScene(
						BattleCode.HARPY, Stance.FELLATIO_BOTTOM, Stance.FELLATIO,
						battleBranches
					) 
			    ).getEncounter();
			case INITIAL:
				break;
			case INN:
				break;
			case LEVEL_UP:
				break;
			case MERI_COTTAGE:
				break;
			case MERI_COTTAGE_VISIT:
				break;
			case OGRE:
				break;
			case OGRE_STORY:
				break;
			case OGRE_WARNING_STORY:
				break;
			case ORC:
				break;
			case SHOP:
				break;
			case SLIME:
				return new Branch().textScene("SLIME-INTRO").choiceScene(
					"What do you do with the slime?",
					new Branch("Fight Her").battleScene(
						BattleCode.SLIME,
						new Branch(Outcome.VICTORY).textScene("SLIME-VICTORY").choiceScene(
							"Slay the slime?",
							new Branch("Stab the core").textScene("SLIME-STAB").checkScene(
								Stat.AGILITY,
								new Branch(6).textScene("SLIME-SHATTER").encounterEnd(),
								new Branch(0).textScene("SLIME-FAIL").gameEnd()
							),
							new Branch("Spare her").textScene("SLIME-SPARE")						
						),
						new Branch(Outcome.DEFEAT).textScene("SLIME-DEFEAT").choiceScene(
							"What do you do?",
							new Branch("Try to speak").textScene("SLIME-MOUTH").encounterEnd(),
							new Branch("Run!").checkScene(
								Stat.AGILITY, 
								new Branch(5).textScene("SLIME-FLEE").encounterEnd(),
								new Branch(0).textScene("SLIME-FALL").encounterEnd()								
							)
						)
					),
					new Branch("Smooch Her").textScene("SLIME-APPROACH").choiceScene(
						"Do you enter the slime, or...?",
						new Branch("Go In").textScene("SLIME-ENTER").encounterEnd(),
						new Branch("Love Dart (Requires: Catamite)").require(ChoiceCheckType.LEWD).textScene("SLIME-LOVEDART").encounterEnd()
					),
					new Branch("Leave Her Be").encounterEnd()			
				).getEncounter();
			case SOUTH_PASS:
				break;
			case STARVATION:
				break;
			case STORY_FEM:
				break;
			case STORY_SIGN:
				break;
			case TOWN:
				break;
			case TOWN2:
				break;
			case TOWN_CRIER:
				break;
			case TOWN_STORY:
				break;
			case WEAPON_SHOP:
				break;
			case WERESLUT:
				return new Branch().textScene("WEREWOLF-INTRO").battleScene(
			    	BattleCode.WERESLUT,
			    	// this has a reference to the first node in this branch, which gets welded with the current context node
			        new Branch(Outcome.VICTORY).textScene("WEREWOLF-VICTORY").encounterEnd(),
			        new Branch(Outcome.KNOT).textScene("WEREWOLF-KNOT").gameEnd(),
			        new Branch(Outcome.DEFEAT).textScene("WEREWOLF-DEFEAT").encounterEnd(),
			        new Branch(Outcome.SATISFIED).textScene("WEREWOLF-SATISFIED").encounterEnd()
			    ).getEncounter();
			case WEST_PASS:
				break;
		}
		return null;
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
		OrderedMap<Object, Branch> branchOptions;
		Object key;
		BranchToken branchToken;
		BattleCode battleCode;
		Stance playerStance;
		Stance enemyStance;
		boolean disarm;
		int climaxCounter;
		ChoiceCheckType require;
		
		boolean preprocessed;
		Array<Scene> scenes;
		Array<BattleScene> battleScenes;
		Array<EndScene> endScenes;
		
		public Branch () {
			this((Object)null);
		}

		public Branch (int check) {
			this((Object)check);
		}
		
		public Branch (Outcome type) {
			this((Object)type);
		}
		
		public Branch(String key) {
			this((Object)key);
		}
		
		public Branch (Object key) {
			init();
			this.key = key;
		}

		private void init() {
			sceneTokens = new Array<SceneToken>();
			branchOptions = new OrderedMap<Object, Branch>();
		}
		
		public Branch textScene(String key) {
			sceneTokens.addAll(reader.loadScript(key));
			return this;
		}
		
		public Branch choiceScene(String toDisplay, Branch ... branches) {
			branchToken = new ChoiceSceneToken(toDisplay);
			for (Branch branch : branches) {
				branchOptions.put(branch.getKey(), branch);
			}
			return this;
		}
		
		public Branch checkScene(Stat toCheck, Branch ... branches) {
			branchToken = new CheckSceneToken(toCheck);
			for (Branch branch : branches) {
				branchOptions.put(branch.getKey(), branch);
			}
			return this;
		}
		
		public Branch battleScene(BattleCode battleCode, Branch ... branches) { return battleScene(battleCode, Stance.BALANCED, Stance.BALANCED, branches); }		
		public Branch battleScene(BattleCode battleCode, Stance playerStance, Stance enemyStance, Branch ... branches) {
			// for each of the branches, add them to the next map with their associated code
			branchToken = new BattleSceneToken(battleCode);
			for (Branch branch : branches) {
				branchOptions.put(branch.getKey(), branch);
			}
			this.battleCode = battleCode;
			this.playerStance = playerStance;
			this.enemyStance = enemyStance;
			return this;
		}
		
		public Branch require(ChoiceCheckType type) {
			require = type;
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
			preprocessed = true;
			for (SceneToken token : sceneTokens) {
				token.preprocess(startBackground, startForeground, startAnimatedForeground);
				startBackground = token.background;
				startForeground = token.foreground;
				startAnimatedForeground = token.animatedForeground;
			}
			
			for (OrderedMap.Entry<Object, Branch> next : branchOptions) {
				next.value.preprocess(startBackground, startForeground, startAnimatedForeground);				
			}
		}
		
		private Scene weld(Array<Scene> scenes, Array<BattleScene> battleScenes, Array<EndScene> endScenes, OrderedMap.Entry<Object, Branch> next, OrderedMap<Integer, Scene> sceneMap) {
			battleScenes.addAll(next.value.getBattleScenes());
			endScenes.addAll(next.value.getEndScenes());
			Array<Scene> nextScenes = next.value.getScenes();
			scenes.addAll(nextScenes);
			Scene nextScene = nextScenes.first();
			sceneMap.put(nextScene.getCode(), nextScene);
			return nextScene;
		}
		
		private OrderedMap<Integer, Scene> addScene(Array<Scene> scenes, Scene toAdd, boolean addToMasterMap) {
			OrderedMap<Integer, Scene> sceneMap = new OrderedMap<Integer, Scene>();
			scenes.add(toAdd);
			sceneMap = new OrderedMap<Integer, Scene>();
			if (addToMasterMap) masterSceneMap.put(toAdd.getCode(), toAdd);
			sceneMap.put(toAdd.getCode(), toAdd);
			sceneCounter++;
			return sceneMap;
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
						for (OrderedMap.Entry<Object, Branch> next : branchOptions) {
							Scene nextScene = weld(scenes, battleScenes, endScenes, next, sceneMap);
							outcomeToScene.put(((Outcome) next.key).toString(), nextScene.getCode());
						}
						
						BattleScene newBattleScene = new BattleScene(sceneMap, saveService, battleCode, playerStance, enemyStance, disarm, climaxCounter, outcomeToScene);
						battleScenes.add(newBattleScene);
						sceneMap = addScene(scenes, newBattleScene, false);						
						break;
					case Check:
						OrderedMap<Integer, Scene> checkValueMap = new OrderedMap<Integer, Scene>();
						for (OrderedMap.Entry<Object, Branch> next : branchOptions) {
							Scene nextScene = weld(scenes, battleScenes, endScenes, next, sceneMap);
							checkValueMap.put(((Integer) next.key), nextScene);
						}
						sceneMap = addScene(scenes, new CheckScene(sceneMap, sceneCounter, assetManager, saveService, font,  new BackgroundBuilder(assetManager.get(AssetEnum.DEFAULT_BACKGROUND.getTexture())).build(), ((CheckSceneToken)branchToken).getStat(), checkValueMap, checkValueMap.get(0), character), true);						
						break;
					case Choice:
						Table table = new Table();
						Skin skin = assetManager.get(AssetEnum.UI_SKIN.getSkin());
						Sound buttonSound = assetManager.get(AssetEnum.BUTTON_SOUND.getSound());
						ChoiceSceneToken choiceBranchToken = (ChoiceSceneToken)branchToken;
						for (OrderedMap.Entry<Object, Branch> next : branchOptions) {
							weld(scenes, battleScenes, endScenes, next, sceneMap);
						}
						
						ChoiceScene choiceScene = new ChoiceScene(sceneMap, sceneCounter, saveService, font, choiceBranchToken.getToDisplay(), table, new BackgroundBuilder(assetManager.get(AssetEnum.DEFAULT_BACKGROUND.getTexture())).build());
						// need the choiceScene in order to create the buttons, so iterate through again
						for (OrderedMap.Entry<Object, Branch> next : branchOptions) {
							Scene nextScene = next.value.getScenes().first();
							TextButton button = new TextButton((String)next.key, skin);
							// this needs the logic for checks as well
							button.addListener(getListener(choiceScene, nextScene, buttonSound, next.value.require, button));
							table.add(button).size(650, 150).row();
						}
						sceneMap = addScene(scenes, choiceScene, true);						
						break;
					case EndGame:
					case EndEncounter:
						EndScene newEndScene;
						if (branchToken.type == EndTokenType.EndEncounter) newEndScene = new EndScene(EndScene.Type.ENCOUNTER_OVER, saveService, returnContext);
						else newEndScene = new EndScene(EndScene.Type.GAME_OVER, saveService, SaveManager.GameContext.GAME_OVER);
						endScenes.add(newEndScene);
						sceneMap = addScene(scenes, newEndScene, false);		
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
		
		private ClickListener getListener(final AbstractChoiceScene currentScene, final Scene nextScene, final Sound buttonSound, final ChoiceCheckType type, final TextButton button) {
			return new ClickListener() {
		        @Override
		        public void clicked(InputEvent event, float x, float y) {
		        	if (type == null || isValidChoice(type)) {
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
	
	public class CheckSceneToken extends BranchToken {
		private final Stat stat;
		public CheckSceneToken(Stat stat) {
			super(EndTokenType.Check);
			this.stat = stat;
		}
		public Stat getStat() { return stat; }
		
	}
	
	public class ChoiceSceneToken extends BranchToken {
		private final String toDisplay;
		private final ChoiceCheckType require;
		public ChoiceSceneToken(String toDisplay) {
			this(toDisplay, null);
		}
		public ChoiceSceneToken(String toDisplay, ChoiceCheckType require) {
			super(EndTokenType.Choice);
			this.toDisplay = toDisplay;
			this.require = require;
		}
		public ChoiceCheckType getRequire() { return require; }
		public String getToDisplay() { return toDisplay; }
		
	}
	
	public class EndSceneToken extends BranchToken {
		public EndSceneToken(EndTokenType endType) {
			super(endType);
		}
	}
	
	// as scenetokens arrays are retrieved, they're placed into a map of key to scene token array to prevent duplicates - and another map is used for the actual scenes so they aren't duplicated (scenes are individual at that point, not in an array, and that key is the new scenecode) - this is currently not implemented
	
	// this represents a text-like scene - it should be able to display text, show who is talking, display a new background, play an animation, play a sound, mutate the game state 
	// these will be serialized into the actual script and have their own key
	public static class SceneToken {
		// this should also have a fontEnum to determine what font the text is displayed in
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
			if (animatedForeground == null) animatedForeground = startAnimatedForeground;
		}		
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

package com.majalis.encounter;
import com.badlogic.gdx.assets.AssetDescriptor;
/*
 * Class used for building an encounter from an encounter code and current state information.
 */
// this class currently has a lot of imports - part of its refactor will be to minimize integration points
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.OrderedMap;
import com.badlogic.gdx.utils.Scaling;
import com.majalis.asset.AnimatedActor;
import com.majalis.asset.AnimatedImage;
import com.majalis.asset.AnimationEnum;
import com.majalis.asset.AssetEnum;
import com.majalis.battle.BattleCode;
import com.majalis.battle.Battle.Outcome;
import com.majalis.character.Perk;
import com.majalis.character.PlayerCharacter;
import com.majalis.character.Stance;
import com.majalis.character.AbstractCharacter.Stat;
import com.majalis.save.MutationResult;
import com.majalis.save.ProfileEnum;
import com.majalis.save.SaveEnum;
import com.majalis.save.SaveService;
import com.majalis.save.SaveManager.GameOver;
import com.majalis.scenes.AbstractChoiceScene;
import com.majalis.scenes.BattleScene; // this should be refactored so that encounterbuilder receives a scenebuilder
import com.majalis.scenes.CharacterCreationScene;
import com.majalis.scenes.CharacterCustomizationScene;
import com.majalis.scenes.CheckScene;
import com.majalis.scenes.ChoiceScene;
import com.majalis.scenes.EndScene;
import com.majalis.scenes.GameTypeScene;
import com.majalis.scenes.Mutation;
import com.majalis.scenes.Scene;
import com.majalis.scenes.ShopScene;
import com.majalis.scenes.SkillSelectionScene;
import com.majalis.scenes.TextScene;
import com.majalis.scenes.CheckScene.CheckType;
import com.majalis.scenes.ShopScene.Shop;
import com.majalis.scenes.ShopScene.ShopCode;
import com.majalis.encounter.Background.BackgroundBuilder;

public class EncounterBuilder {
	private final EncounterReader reader;
	private final AssetManager assetManager;
	private final SaveService saveService;
	private final BitmapFont font;
	private final IntArray sceneCodes;
	private final ObjectMap<String, Shop> shops;
	private final PlayerCharacter character;
	private final OrderedMap<Integer, Scene> masterSceneMap;
	private final ObjectMap<AnimationEnum, AnimatedActor> animationCache;
	private final Array<MutationResult> results;
	private final Array<MutationResult> battleResults;
	private EncounterHUD hud;
	// can probably be replaced with a call to scenes.size
	private int sceneCounter;
	
	protected EncounterBuilder(EncounterReader reader, AssetManager assetManager, SaveService saveService, BitmapFont font, IntArray sceneCodes, ObjectMap<String, Shop> shops, PlayerCharacter character, Array<MutationResult> results, Array<MutationResult> battleResults) {
		this.reader = reader;
		this.assetManager = assetManager;
		this.saveService = saveService;
		this.font = font;
		this.sceneCodes = sceneCodes;
		this.shops = shops == null ? new ObjectMap<String, Shop>() : shops;
		this.character = character;
		this.results = results;
		this.battleResults = battleResults;
		this.animationCache = new ObjectMap<AnimationEnum, AnimatedActor>();
		sceneCounter = 1;
		masterSceneMap = new OrderedMap<Integer, Scene>();
	}
	
	protected Branch branch() { return branch((Object)null); }
	protected Branch branch(int check) { return branch((Object)check);}
	protected Branch branch(Outcome type) { return branch((Object)type); }
	protected Branch branch(String key) { return branch((Object)key); }
	protected Branch branch(Object key) { return new Branch(key); }
	private EncounterHUD getEncounterHUD() {
		if (hud == null) hud = new EncounterHUD(assetManager, character, masterSceneMap, sceneCodes, font);
		return hud;
	}
	
	enum EndTokenType {
		Choice,
		Check,
		Battle,
		Gametype,
		EndEncounter,
		EndGame 
	}
	
	public enum ChoiceCheckType {
		LEWD,
		GOLD_GREATER_THAN_X,
		GOLD_LESS_THAN_X,
		STAT_GREATER_THAN_X,
		STAT_LESS_THAN_X,
		PERK_GREATER_THAN_X,
		PERK_LESS_THAN_X,
		FREE_COCK, 
		HAS_GEM, 
		HAS_TRUDY;
		
		public boolean isValidChoice(PlayerCharacter character, Stat statToCheck, Perk perkToCheck, int target) {
			switch (this) {
				case LEWD:
					return character.isLewd();
				case GOLD_GREATER_THAN_X:
					return character.getMoney() >= target;
				case GOLD_LESS_THAN_X:
					return character.getMoney() < target;
				case PERK_GREATER_THAN_X:
					return character.getPerks().get(perkToCheck, 0) >= target;
				case PERK_LESS_THAN_X:
					return character.getPerks().get(perkToCheck, 0) < target;
				case STAT_GREATER_THAN_X:
					return character.getRawStat(statToCheck) >= target;
				case STAT_LESS_THAN_X:
					return character.getRawStat(statToCheck) < target;
				case FREE_COCK:
					return !character.isChastitied();
				case HAS_GEM:
					return character.hasGem();
				case HAS_TRUDY:
					return character.hasTrudy();
			}
			return false;
		}
	}
	
	public class Branch {
		private Array<SceneToken> sceneTokens;
		private OrderedMap<Object, Branch> branchOptions;
		private Object key;
		private BranchToken branchToken;
		private BattleCode battleCode;
		private Stance playerStance;
		private Stance enemyStance;
		private boolean disarm;
		private int climaxCounter;
		private int range;
		private ChoiceCheckToken require;
		private int concatCounter;
		
		private boolean preprocessed;
		private Array<Scene> scenes;
		
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
		
		public Branch characterCreation(boolean storyMode) {
			sceneTokens.add(new CharacterCreationToken(storyMode));
			return this;
		}
		
		public Branch characterCustomization() {
			sceneTokens.add(new CharacterCustomizationToken());
			return this;
		}

		public Branch skillSelection() {
			sceneTokens.add(new SkillSelectionToken());
			return this;
		}
		
		public Branch shopScene(ShopCode shopCode) {
			sceneTokens.add(new ShopSceneToken(shopCode));
			return this;
		}
		
		public Branch randomScene(Array<String> keys) {
			sceneTokens.addAll(reader.loadScript(keys.random()));
			return this;
		}
		
		public Branch concat(Branch branch) {
			return weldBranches(new Branch[]{branch});
		}
		
		public Branch choiceScene(String toDisplay, Branch ... branches) {
			branchToken = new ChoiceSceneToken(toDisplay);
			return weldBranches(branches);
		}
		
		public Branch gameTypeScene(Branch... branches) {
			branchToken = new GameTypeSceneToken();
			return weldBranches(branches);
		}
		
		public Branch checkScene(Stat toCheck, Branch ... branches) {
			branchToken = new CheckSceneToken(toCheck);
			return weldBranches(branches);
		}
		
		public Branch checkScene(Perk toCheck, Branch ... branches) {
			branchToken = new CheckSceneToken(toCheck);
			return weldBranches(branches);
		}
		
		public Branch checkScene(CheckType toCheck, Branch ... branches) {
			branchToken = new CheckSceneToken(toCheck);
			return weldBranches(branches);
		}
		
		public Branch weldBranches(Branch[] branches) {
			for (Branch branch : branches) {
				branchOptions.put(branch.getKey() != null ? branch.getKey() : "CONCAT-"+concatCounter++, branch);
			}
			return this;
		}
		
		public Branch battleScene(BattleCode battleCode, Branch ... branches) { return battleScene(battleCode, Stance.BALANCED, Stance.BALANCED, branches); }
		public Branch battleScene(BattleCode battleCode, int climaxCounter, Branch ... branches) { return battleScene(battleCode, Stance.BALANCED, Stance.BALANCED, false, climaxCounter, branches); }		
		public Branch battleScene(BattleCode battleCode, Stance playerStance, Stance enemyStance, Branch ... branches) { return battleScene(battleCode, playerStance, enemyStance, false, branches); }
		public Branch battleScene(BattleCode battleCode, Stance playerStance, Stance enemyStance, boolean disarm, Branch ... branches) { return battleScene(battleCode, playerStance, enemyStance, disarm, 0, branches); }
		public Branch battleScene(BattleCode battleCode, Stance playerStance, Stance enemyStance, boolean disarm, int climaxCounter, Branch ... branches) {
			// for each of the branches, add them to the next map with their associated code
			branchToken = new BattleSceneToken(battleCode);
			this.battleCode = battleCode;
			this.playerStance = playerStance;
			this.enemyStance = enemyStance;
			this.disarm = disarm;
			this.climaxCounter = climaxCounter;
			return weldBranches(branches);
		}
		
		public Branch setRange(int range) {
			this.range = range;
			return this;
		}
		
		public Branch require(ChoiceCheckType type) {
			require = new ChoiceCheckToken(type);
			return this;
		}
		
		public Branch require(ChoiceCheckType type, int target) {
			require = new ChoiceCheckToken(type, target);
			return this;
		}
		
		public Branch require(ChoiceCheckType type, Stat stat, int target) {
			require = new ChoiceCheckToken(type, target, stat);
			return this;
		}
		
		public Branch require(ChoiceCheckType type, Perk perk, int target) {
			require = new ChoiceCheckToken(type, target, perk);
			return this;
		}
		
		public Object getKey() {
			return key;
		}
		
		public Branch gameEnd() { return gameEnd(GameOver.DEFAULT); }
		public Branch gameEnd(GameOver gameOver) {
			branchToken = new EndSceneToken(EndTokenType.EndGame, gameOver);	
			return this;
		}
		
		private void preprocess() {
			preprocess(null, null, null);
		}
	
		private void preprocess(AssetEnum startBackground, AssetEnum startForeground, AnimationEnum startAnimatedForeground) {
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
		
		private Scene weld(Array<Scene> scenes, OrderedMap.Entry<Object, Branch> next, OrderedMap<Integer, Scene> sceneMap) {
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
					
			Skin skin = assetManager.get(AssetEnum.UI_SKIN.getSkin());
			
			// set shadows
		    Array<Scene> scenes = new Array<Scene>();
			OrderedMap<Integer, Scene> sceneMap = new OrderedMap<Integer, Scene>();
			
			boolean reverse = false;
			
			if (branchToken != null) {
				reverse = true;
				
				switch (branchToken.type) {
					case Battle:
						// for each branch get the scenes, the first entry in that list is what this branchToken scene should be tied to
						ObjectMap<String, Integer> outcomeToScene = new ObjectMap<String, Integer>();
						for (OrderedMap.Entry<Object, Branch> next : branchOptions) {
							Scene nextScene = weld(scenes, next, sceneMap);
							outcomeToScene.put(((Outcome) next.key).toString(), nextScene.getCode());
						}
						sceneMap = addScene(scenes, new BattleScene(sceneMap, saveService, battleCode, playerStance, enemyStance, disarm, climaxCounter, range, outcomeToScene, getEncounterHUD()), false);						
						break;
					case Check:
						CheckSceneToken checkBranchToken = ((CheckSceneToken)branchToken);
						if (checkBranchToken.getStat() != null || checkBranchToken.getPerk() != null) {
							OrderedMap<Integer, Scene> checkValueMap = new OrderedMap<Integer, Scene>();
							for (OrderedMap.Entry<Object, Branch> next : branchOptions) {
								Scene nextScene = weld(scenes, next, sceneMap);
								checkValueMap.put(((Integer) next.key), nextScene);
							}
							if (checkBranchToken.getStat() != null) {
								sceneMap = addScene(scenes, new CheckScene(sceneMap, sceneCounter, assetManager, saveService, font, getDefaultBackground().build(), checkBranchToken.getStat(), checkValueMap, checkValueMap.get(0), character, getEncounterHUD()), true);						
							}
							else {
								sceneMap = addScene(scenes, new CheckScene(sceneMap, sceneCounter, assetManager, saveService, font, getDefaultBackground().build(), checkBranchToken.getPerk(), checkValueMap, checkValueMap.get(0), character, getEncounterHUD()), true);						
							}
						}
						else {
							OrderedMap<Boolean, Scene> checkValueMap = new OrderedMap<Boolean, Scene>();
							for (OrderedMap.Entry<Object, Branch> next : branchOptions) {
								Scene nextScene = weld(scenes, next, sceneMap);
								checkValueMap.put(((Boolean) next.key), nextScene);
							}
							sceneMap = addScene(scenes, new CheckScene(sceneMap, sceneCounter, assetManager, saveService, font, getDefaultBackground().build(), checkBranchToken.getCheckType(), checkValueMap.get(true), checkValueMap.get(false), character, getEncounterHUD()), true);						
						}
						break;
					case Choice:
					case Gametype:
						Sound buttonSound = assetManager.get(AssetEnum.BUTTON_SOUND.getSound());
						for (OrderedMap.Entry<Object, Branch> next : branchOptions) {
							weld(scenes, next, sceneMap);
						}
						Array<BranchChoice> choices = new Array<BranchChoice>();
						for (OrderedMap.Entry<Object, Branch> next : branchOptions) {
							Scene nextScene = next.value.getScenes().first();
							choices.add(new BranchChoice(new TextButton((String)next.key, skin), nextScene, next.value.require, buttonSound));							
						}
						AbstractChoiceScene choiceScene = branchToken.type == EndTokenType.Choice ? new ChoiceScene(sceneMap, sceneCounter, saveService, font, ((ChoiceSceneToken)branchToken).getToDisplay(), choices, assetManager.get(AssetEnum.STANCE_ARROW.getTexture()), character, getDefaultBackground().build(), getEncounterHUD())
						: new GameTypeScene(sceneMap, sceneCounter, saveService, choices, new BackgroundBuilder(assetManager.get(AssetEnum.GAME_TYPE_BACKGROUND.getTexture())).build(), getEncounterHUD());
						// need the choiceScene in order to create the buttons, so iterate through again
						sceneMap = addScene(scenes, choiceScene, true);						
						break;
					case EndGame:
					case EndEncounter:
						EndScene newEndScene = branchToken.type == EndTokenType.EndEncounter ? 
							new EndScene(sceneCounter, EndScene.Type.ENCOUNTER_OVER, saveService, assetManager, getEndBackground(), results, battleResults, getEncounterHUD()) :
							new EndScene(sceneCounter, EndScene.Type.GAME_OVER, saveService, assetManager, getEndBackground(), results, battleResults, ((EndSceneToken)branchToken).getGameOver(), getEncounterHUD());
						sceneMap = addScene(scenes, newEndScene, true);		
						break;
				}
			}
			else {
				for (OrderedMap.Entry<Object, Branch> next : branchOptions) {
					weld(scenes, next, sceneMap);
				}
			}
			
			// catch if there's an unplugged branch without an end scene
			if (sceneMap.size == 0) {
				sceneMap = addScene(scenes, new EndScene(sceneCounter, EndScene.Type.ENCOUNTER_OVER, saveService, assetManager, getEndBackground(), results, battleResults, getEncounterHUD()), true);		
			}
			
			String characterName = character.getCharacterName();
			String buttsize = character.getBootyLiciousness();
			String lipsize = character.getLipFullness();
			String cockSize = character.getPhallusLabel().toLowerCase();
			String debt = character.getCurrentDebt() > 0 ? "You currently owe " + character.getCurrentDebt() + " gold." : "";
			
			Array<Background> backgrounds = new Array<Background>();
			AssetEnum background = null;
			AssetEnum foreground = null;
			AnimationEnum animatedForeground = null;

			Texture dialogBoxTexture = assetManager.get(AssetEnum.BATTLE_HOVER.getTexture());
			
			if (sceneTokens.size > 0) {
				reverse = false;
				// iterate through and every time either background or foreground/animatedforeground change, create a new background
				for (SceneToken token: sceneTokens) {
					if (token instanceof ShopSceneToken || token instanceof CharacterCreationToken || token instanceof CharacterCustomizationToken || token instanceof SkillSelectionToken) continue;	
					// if all of the tokens are  the same, clone the last background
					if ((token.foreground == null || token.foreground == foreground) && (token.animatedForeground == null || token.animatedForeground == animatedForeground) && (token.background == null || token.background == background)) {
						if (backgrounds.size > 0) {
							backgrounds.add(backgrounds.get(backgrounds.size - 1).clone());
						}
						else {
							backgrounds.add(getDefaultBackground().setDialogBox(dialogBoxTexture).build());
						}
					}
					else {						
						BackgroundBuilder backgroundBuilder = (token.background != null ? new BackgroundBuilder(assetManager.get(token.background.getTexture()), token.background.isTinted()) : background != null ? new BackgroundBuilder(assetManager.get(background.getTexture()), background.isTinted()) : getDefaultBackground()).setDialogBox(dialogBoxTexture); 
						if (token.animatedForeground != null) {
							int x = token.animatedForeground == AnimationEnum.BUTTBANG ? 555 : 0;
							int y = token.animatedForeground == AnimationEnum.BUTTBANG ? 520 : 0;
							backgroundBuilder.setForeground(getAnimation(token.animatedForeground), x, y);
						}
						else if (token.foreground != null) {
							if (token.foreground == AssetEnum.SILHOUETTE) {
								backgroundBuilder.setForeground(assetManager.get(token.foreground.getTexture()), 1000, 0);
							}
							else {
								backgroundBuilder.setForeground(assetManager.get(token.foreground.getTexture()));
							}
						}
						else if (animatedForeground != null) {
							int x = animatedForeground == AnimationEnum.BUTTBANG ? 555 : 0;
							int y = animatedForeground == AnimationEnum.BUTTBANG ? 520 : 0;
							backgroundBuilder.setForeground(getAnimation(animatedForeground), x, y);
						}
						else if (foreground != null) {
							if (foreground == AssetEnum.SILHOUETTE) {
								backgroundBuilder.setForeground(assetManager.get(foreground.getTexture()), 1000, 0);
							}
							else {
								backgroundBuilder.setForeground(assetManager.get(foreground.getTexture()));
							}
						}
						backgrounds.add(backgroundBuilder.build());
					}
					background = token.background;
					foreground = token.foreground;
					animatedForeground = token.animatedForeground;
				}
				
				backgrounds.reverse();
				sceneTokens.reverse();
					
				// run through the tokens once and create a list of backgrounds using clone when it persists (need to check both background and animated background, clone if it doesn't, then reverse that list
				// probably need to make the variables foreground, background, and animatedbackground - think hoverbox is consistent for now
				// taking the branchToken scene and use it as the entrypoint, traversing the sceneTokens backwards and putting them into each other
				int ii = 0;
				for (SceneToken token: sceneTokens) {
					Scene newScene = null;
					if (token instanceof ShopSceneToken) {
						ShopCode shopCode = ((ShopSceneToken) token).shopCode;
						// this needs to get the proper background, probably from shopcode attributes
						Background bg = new BackgroundBuilder(assetManager.get(shopCode.getBackground()), shopCode.isTinted()).setForeground(assetManager.get(shopCode.getForeground()), shopCode.getX(), shopCode.getY()).build();
						newScene = new ShopScene(sceneMap, sceneCounter, saveService, assetManager, character, bg, shopCode, shops.get(shopCode.toString()), getEncounterHUD());
					}
					else if (token instanceof CharacterCreationToken) {
						boolean storyMode = ((CharacterCreationToken) token).storyMode;
						newScene = new CharacterCreationScene(sceneMap, sceneCounter, saveService, new BackgroundBuilder(assetManager.get(AssetEnum.CLASS_SELECT_BACKGROUND.getTexture())).build(), assetManager, character, storyMode, getEncounterHUD());
					}
					else if (token instanceof SkillSelectionToken) {
						newScene = new SkillSelectionScene(sceneMap, sceneCounter, saveService, getCampBackground(), assetManager, character, getEncounterHUD());
					}
					else if (token instanceof CharacterCustomizationToken) {
						newScene = new CharacterCustomizationScene(sceneMap, sceneCounter, saveService, font, getCampBackground(), assetManager, character, getEncounterHUD());
					}
					else {
						String scriptLine = token.text.replace("<NAME>", characterName).replace("<BUTTSIZE>", buttsize).replace("<LIPSIZE>", lipsize).replace("<DEBT>", debt).replace("<COCKSIZE>", cockSize);
						// create the scene
						newScene = new TextScene(sceneMap, sceneCounter, assetManager, font, saveService, backgrounds.get(ii++), scriptLine, token.chatter != null ? token.chatter : "", token.chatterPerson != null ? token.chatterPerson : "", getMutations(token.mutations), character, token.music != null ? token.music : null, token.sound != null ? token.sound.getSound() : null, getEncounterHUD());		
					}
					// add it to array
					scenes.add(newScene);
					// use it to make the map
					sceneMap = new OrderedMap<Integer, Scene>();
					masterSceneMap.put(sceneCounter, newScene);
					sceneMap.put(sceneCounter++, newScene);
				}
				
				scenes.reverse();
			}
			
			if (reverse) scenes.reverse();
			this.scenes.addAll(scenes);
		}
		
		private Background getCampBackground() {
			Array<TextureRegion> frames = new Array<TextureRegion>();
			frames.add(new TextureRegion(assetManager.get(AssetEnum.CAMP_BG0.getTexture())));
			frames.add(new TextureRegion(assetManager.get(AssetEnum.CAMP_BG1.getTexture())));
			frames.add( new TextureRegion(assetManager.get(AssetEnum.CAMP_BG2.getTexture())));
			Animation animation = new Animation(.08f, frames);
			animation.setPlayMode(PlayMode.LOOP_PINGPONG);
			AnimatedImage animationActor = new AnimatedImage(animation, Scaling.fit, Align.right);
			return new BackgroundBuilder(animationActor).build();
		}
		
		private BackgroundBuilder getDefaultBackground() {
			return new BackgroundBuilder(assetManager.get(AssetEnum.DEFAULT_BACKGROUND.getTexture()), AssetEnum.DEFAULT_BACKGROUND.isTinted());
		}
		
		private Background getEndBackground() {
			return getDefaultBackground().setDialogBox(assetManager.get(AssetEnum.BATTLE_HOVER.getTexture()), 400, 100, 1200, 800).build();
		}
		
		private AnimatedActor getAnimation(AnimationEnum type) {
			AnimatedActor animation = animationCache.get(type);
			if (animation == null) {
				animation = type.getAnimation(assetManager);
				animationCache.put(type, animation);
			}			
			return animation;
		}
		
		public Array<Scene> getScenes() {
			upsertScenes();
			return scenes;
		}
		
		public Scene getStartScene() {
			// returns the first scene or the current scene based on sceneCode
			upsertScenes();
			if (sceneCodes.size == 0) {
				saveService.saveDataValue(SaveEnum.MUSIC, AssetEnum.ENCOUNTER_MUSIC);
				return scenes.get(0);
			}
			return masterSceneMap.get(sceneCodes.get(sceneCodes.size - 1), scenes.get(0));
		}
		
		private ObjectSet<Scene> getUniqueScenes(Array<Scene> scenes) { 
			ObjectSet<Scene> allUniqueScenes = new ObjectSet<Scene>();
			allUniqueScenes.addAll(scenes);
			return allUniqueScenes;
		}
		
		public Encounter getEncounter() {
			// this should accept some kind of object that has assetmanager and whatever else to actually build the scenes			
			return new Encounter(getUniqueScenes(getScenes()), getStartScene(), getEncounterHUD());
		}

		private Array<SceneToken> getAllSceneTokens() {
			Array<SceneToken> allSceneTokens = new Array<SceneToken>(sceneTokens);
			for (Branch branch : branchOptions.values()) {
				allSceneTokens.addAll(branch.getAllSceneTokens());
			}
			return allSceneTokens;
		}
		
		private Array<BranchToken> getAllBranchTokens() {
			Array<BranchToken> allBranchTokens = new Array<BranchToken>();
			allBranchTokens.add(branchToken);
			for (Branch branch : branchOptions.values()) {
				allBranchTokens.addAll(branch.getAllBranchTokens());
			}
			return allBranchTokens;
		}
		
		public Array<AssetDescriptor<?>> getRequirements() {
			Array<AssetDescriptor<?>> requirements = new Array<AssetDescriptor<?>>();
			ObjectSet<AssetEnum> alreadySeen = new ObjectSet<AssetEnum>();
			ObjectSet<SceneToken> alreadySeenScene = new ObjectSet<SceneToken>();
			for (SceneToken scene : getAllSceneTokens()) {
				if(!alreadySeenScene.contains(scene)) {
					alreadySeenScene.add(scene);
					requirements.addAll(scene.getRequirements(alreadySeen));
				}
			}		
			ObjectSet<BranchToken> alreadySeenBranch = new ObjectSet<BranchToken>();
			for (BranchToken branch : getAllBranchTokens()) {
				if(branch != null && !alreadySeenBranch.contains(branch)) {
					alreadySeenBranch.add(branch);
					requirements.addAll(branch.getRequirements(alreadySeen));
				}
			}	
			
			return requirements;
		}
	}
	
	public static class ChoiceCheckToken {
		public final ChoiceCheckType type;
		public final Stat statToCheck;
		public final Perk perkToCheck;
		public final int target;
		
		public ChoiceCheckToken(ChoiceCheckType type) {
			this(type, 0);
		}
		// this needs to be refactored so that Stat or Perk based ChoiceCheckTypes have their stat or perk built in
		public ChoiceCheckToken(ChoiceCheckType type, int target) {
			this(type, target, null, null);
		}
		public ChoiceCheckToken(ChoiceCheckType type, int target, Stat statToCheck) {
			this(type, target, statToCheck, null);
		}
		public ChoiceCheckToken(ChoiceCheckType type, int target, Perk perkToCheck) {
			this(type, target, null, perkToCheck);
		}
		public ChoiceCheckToken(ChoiceCheckType type, int target, Stat statToCheck, Perk perkToCheck) {
			this.type = type;
			this.target = target;
			this.statToCheck = statToCheck;
			this.perkToCheck = perkToCheck;
		}
		public boolean isValidChoice(PlayerCharacter character) {
			return type.isValidChoice(character, statToCheck, perkToCheck, target);
		}
	}
	
	public static class BranchChoice {
		public final TextButton button;
		public final Scene scene;
		public final ChoiceCheckToken require;
		public final Sound clickSound;
		public BranchChoice(TextButton button, Scene scene, ChoiceCheckToken require, Sound clickSound) {
			this.button = button;
			this.scene = scene;
			this.require = require;
			this.clickSound = clickSound;
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
		protected Array<AssetDescriptor<?>> getRequirements(ObjectSet<AssetEnum> alreadySeen) { return new Array<AssetDescriptor<?>>(); }
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
	
	public class GameTypeSceneToken extends BranchToken {
		public GameTypeSceneToken() {
			super(EndTokenType.Gametype);
		}
		@Override
		protected Array<AssetDescriptor<?>> getRequirements(ObjectSet<AssetEnum> alreadySeen) { 
			Array<AssetDescriptor<?>> requirements = new Array<AssetDescriptor<?>>();
			add(requirements, alreadySeen, AssetEnum.GAME_TYPE_BACKGROUND);
			return requirements; 
		}
	}
	
	public class CheckSceneToken extends BranchToken {
		private final Stat stat;
		private final CheckType checkType;
		private final Perk perk;
		public CheckSceneToken(Stat stat) {
			this(stat, null, null);
		}
		public CheckSceneToken(CheckType checkType) {
			this(null, checkType, null);
		}
		public CheckSceneToken(Perk perk) {
			this(null, null, perk);
		}
		public CheckSceneToken(Stat stat, CheckType checkType, Perk perk) {
			super(EndTokenType.Check);
			this.stat = stat;
			this.checkType = checkType;
			this.perk = perk;
		}
		
		public Stat getStat() { return stat; }
		public CheckType getCheckType() { return checkType; }
		public Perk getPerk() { return perk; }
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
		private final GameOver gameOver;
		public EndSceneToken(EndTokenType endType) {
			this(endType, GameOver.DEFAULT);
		}

		public EndSceneToken(EndTokenType endType, GameOver gameOver) {
			super(endType);
			this.gameOver = gameOver;
		}
		
		public GameOver getGameOver() { return gameOver; }
	}
	
	// as scenetokens arrays are retrieved, they're placed into a map of key to scene token array to prevent duplicates - and another map is used for the actual scenes so they aren't duplicated (scenes are individual at that point, not in an array, and that key is the new scenecode) - this is currently not implemented
	
	// this represents a text-like scene - it should be able to display text, show who is talking, display a new background, play an animation, play a sound, mutate the game state 
	// these will be serialized into the actual script and have their own key
	public static class SceneToken {
		// this should also have a fontEnum to determine what font the text is displayed in
		String text;
		String speaker;
		String chatter;
		String chatterPerson;
		AssetEnum background;
		AssetEnum foreground;
		AnimationEnum animatedForeground;
		AssetEnum sound;
		AssetEnum music;
		Array<MutateToken> mutations;
		public void preprocess(AssetEnum startBackground, AssetEnum startForeground, AnimationEnum startAnimatedForeground) {
			//if foreground == null and token.foreground != null and animatedForeground != null -> foreground = token.foreground, animatedForeground = null
					
			if (background == null) background = startBackground;
			if (foreground == null) {
				foreground = startForeground;
			}
			else if (animatedForeground == null) {
				startAnimatedForeground = null;
			}
			if (animatedForeground == null) animatedForeground = startAnimatedForeground;
		}
		protected Array<AssetDescriptor<?>> getRequirements(ObjectSet<AssetEnum> alreadySeen) {
			Array<AssetDescriptor<?>> requirements = new Array<AssetDescriptor<?>>();
			if (background != null && !alreadySeen.contains(background)) {
				alreadySeen.add(background);
				requirements.add(background.getTexture());
			}
			if (foreground != null && !alreadySeen.contains(foreground)) {
				alreadySeen.add(foreground);
				requirements.add(foreground.getTexture());
			}
			if (animatedForeground != null && !alreadySeen.contains(animatedForeground.getAnimationToken())) {
				alreadySeen.add(animatedForeground.getAnimationToken());
				requirements.add(animatedForeground.getAnimationToken().getAnimation());
			}
			if (sound != null && !alreadySeen.contains(sound)) {
				alreadySeen.add(sound);
				requirements.add(sound.getSound());
			}
			if (music != null && !alreadySeen.contains(music)) {
				alreadySeen.add(music);
				requirements.add(music.getMusic());
			}
			return requirements;
		}		
	}
	
	public static class ShopSceneToken extends SceneToken {
		ShopCode shopCode;
		public ShopSceneToken (ShopCode shopCode) {
			this.shopCode = shopCode;
		}
		@Override
		protected Array<AssetDescriptor<?>> getRequirements(ObjectSet<AssetEnum> alreadySeen) {
			Array<AssetDescriptor<?>> requirements = super.getRequirements(alreadySeen);
			add(requirements, alreadySeen, AssetEnum.EQUIP, true);
			add(requirements, alreadySeen, AssetEnum.BATTLE_TEXTBOX);
			add(requirements, alreadySeen, AssetEnum.TEXT_BOX);
			add(requirements, alreadySeen, AssetEnum.BATTLE_HOVER);
			requirements.add(shopCode.getForeground());
			requirements.add(shopCode.getBackground());
			return requirements;
		}
		
	}
	
	public static class CharacterCreationToken extends SceneToken {
		boolean storyMode;
		public CharacterCreationToken (boolean storyMode) {
			this.storyMode = storyMode;
		}
		@Override
		protected Array<AssetDescriptor<?>> getRequirements(ObjectSet<AssetEnum> alreadySeen) {
			Array<AssetDescriptor<?>> requirements = super.getRequirements(alreadySeen);
			add(requirements, alreadySeen, AssetEnum.WARRIOR);
			add(requirements, alreadySeen, AssetEnum.PALADIN);
			add(requirements, alreadySeen, AssetEnum.THIEF);
			add(requirements, alreadySeen, AssetEnum.RANGER);
			add(requirements, alreadySeen, AssetEnum.MAGE);
			add(requirements, alreadySeen, AssetEnum.ENCHANTRESS);
			add(requirements, alreadySeen, AssetEnum.STRENGTH);
			add(requirements, alreadySeen, AssetEnum.ENDURANCE);
			add(requirements, alreadySeen, AssetEnum.AGILITY);
			add(requirements, alreadySeen, AssetEnum.PERCEPTION);
			add(requirements, alreadySeen, AssetEnum.MAGIC);
			add(requirements, alreadySeen, AssetEnum.CHARISMA);
			add(requirements, alreadySeen, AssetEnum.PLUS);
			add(requirements, alreadySeen, AssetEnum.PLUS_DOWN);
			add(requirements, alreadySeen, AssetEnum.PLUS_HIGHLIGHT);
			add(requirements, alreadySeen, AssetEnum.MINUS);
			add(requirements, alreadySeen, AssetEnum.MINUS_DOWN);
			add(requirements, alreadySeen, AssetEnum.MINUS_HIGHLIGHT);
			add(requirements, alreadySeen, AssetEnum.CREATION_BUTTON_DOWN);
			add(requirements, alreadySeen, AssetEnum.CREATION_BUTTON_UP);
			add(requirements, alreadySeen, AssetEnum.CREATION_BUTTON_CHECKED);
			add(requirements, alreadySeen, AssetEnum.CREATION_BAUBLE_EMPTY);
			add(requirements, alreadySeen, AssetEnum.CREATION_BAUBLE_NEW);
			add(requirements, alreadySeen, AssetEnum.CREATION_BAUBLE_OLD);
			add(requirements, alreadySeen, AssetEnum.CREATION_BAUBLE_REMOVED);
			add(requirements, alreadySeen, AssetEnum.CLASS_SELECT_BACKGROUND);
			add(requirements, alreadySeen, AssetEnum.CLASS_SELECT_CHARACTER_BOX);
			add(requirements, alreadySeen, AssetEnum.CLASS_SELECT_LABEL);
			add(requirements, alreadySeen, AssetEnum.CLASS_SELECT_PANEL);
			add(requirements, alreadySeen, AssetEnum.CLASS_SELECT_FOREGROUND);
			add(requirements, alreadySeen, AssetEnum.CLASS_SELECT_LEAF_BORDER);
			add(requirements, alreadySeen, AssetEnum.CLASS_SELECT_HANGING_LEAVES);
			add(requirements, alreadySeen, AssetEnum.CLASS_SELECT_STAT_PANEL_FOLDOUT);
			add(requirements, alreadySeen, AssetEnum.CLASS_SELECT_STAT_BOX);
			add(requirements, alreadySeen, AssetEnum.CLASS_SELECT_TOOLTIP_BOX);
			add(requirements, alreadySeen, AssetEnum.CLASS_SELECT_TOOLTIP_SLIDEOUT);
			add(requirements, alreadySeen, AssetEnum.CLASS_SELECT_SUBTLE_BORDER);
			add(requirements, alreadySeen, AssetEnum.CLASS_SELECT_TOP_BORDER);
			add(requirements, alreadySeen, AssetEnum.GEM_CLINK, true);
			add(requirements, alreadySeen, AssetEnum.QUICK_PAGE_TURN, true);
			return requirements;
		}
	}
	
	public static class CharacterCustomizationToken extends SceneToken {
		@Override
		protected Array<AssetDescriptor<?>> getRequirements(ObjectSet<AssetEnum> alreadySeen) {
			Array<AssetDescriptor<?>> requirements = super.getRequirements(alreadySeen);
			add(requirements, alreadySeen, AssetEnum.CAMP_BG0);
			add(requirements, alreadySeen, AssetEnum.CAMP_BG1);
			add(requirements, alreadySeen, AssetEnum.CAMP_BG2);
			add(requirements, alreadySeen, AssetEnum.SKILL_CONSOLE_BOX);
			add(requirements, alreadySeen, AssetEnum.EMBELLISHED_BUTTON_UP);
			add(requirements, alreadySeen, AssetEnum.EMBELLISHED_BUTTON_DOWN);
			add(requirements, alreadySeen, AssetEnum.EMBELLISHED_BUTTON_HIGHLIGHT);
			return requirements;
		}
	}
	
	public static class SkillSelectionToken extends SceneToken {
		@Override
		protected Array<AssetDescriptor<?>> getRequirements(ObjectSet<AssetEnum> alreadySeen) {
			Array<AssetDescriptor<?>> requirements = super.getRequirements(alreadySeen);
			add(requirements, alreadySeen, AssetEnum.SKILL_BOX_0);
			add(requirements, alreadySeen, AssetEnum.SKILL_BOX_1);
			add(requirements, alreadySeen, AssetEnum.SKILL_BOX_2);
			add(requirements, alreadySeen, AssetEnum.BATTLE_HOVER);
			add(requirements, alreadySeen, AssetEnum.CAMP_BG0);
			add(requirements, alreadySeen, AssetEnum.CAMP_BG1);
			add(requirements, alreadySeen, AssetEnum.CAMP_BG2);
			add(requirements, alreadySeen, AssetEnum.SKILL_TITLE);
			add(requirements, alreadySeen, AssetEnum.MAGIC_TITLE);
			add(requirements, alreadySeen, AssetEnum.PERK_TITLE);
			add(requirements, alreadySeen, AssetEnum.ARROW_BUTTON_UP);
			add(requirements, alreadySeen, AssetEnum.ARROW_BUTTON_DOWN);
			add(requirements, alreadySeen, AssetEnum.ARROW_BUTTON_HIGHLIGHT);
			add(requirements, alreadySeen, AssetEnum.PLUS);
			add(requirements, alreadySeen, AssetEnum.PLUS_DOWN);
			add(requirements, alreadySeen, AssetEnum.PLUS_HIGHLIGHT);
			add(requirements, alreadySeen, AssetEnum.MINUS);
			add(requirements, alreadySeen, AssetEnum.MINUS_DOWN);
			add(requirements, alreadySeen, AssetEnum.MINUS_HIGHLIGHT);
			add(requirements, alreadySeen, AssetEnum.EMPTY_BAUBLE);
			add(requirements, alreadySeen, AssetEnum.FILLED_BAUBLE);
			add(requirements, alreadySeen, AssetEnum.ADDED_BAUBLE);
			add(requirements, alreadySeen, AssetEnum.STANCE_ARROW);
			add(requirements, alreadySeen, AssetEnum.SKILL_CONSOLE_BOX);
			add(requirements, alreadySeen, AssetEnum.GEM_CLINK, true);
			add(requirements, alreadySeen, AssetEnum.QUICK_PAGE_TURN, true);
			for (Stance stance : Stance.values()) {
				requirements.add(stance.getTexture());
			}
			return requirements;
		}	
	}
	
	private static void add(Array<AssetDescriptor<?>> requirements, ObjectSet<AssetEnum> alreadySeen, AssetEnum asset) { add(requirements, alreadySeen, asset, false); }	
	private static void add(Array<AssetDescriptor<?>> requirements, ObjectSet<AssetEnum> alreadySeen, AssetEnum asset, boolean sound) {
		if (!alreadySeen.contains(asset)) {
			requirements.add(!sound ? asset.getTexture() : asset.getSound());
			alreadySeen.add(asset);
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
}

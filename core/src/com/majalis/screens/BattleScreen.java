package com.majalis.screens;

import static com.majalis.asset.AssetEnum.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.majalis.asset.AssetEnum;
import com.majalis.battle.Battle;
import com.majalis.battle.Battle.Outcome;
import com.majalis.battle.BattleCode;
import com.majalis.save.SaveEnum;
import com.majalis.save.SaveManager;
import com.majalis.save.SaveService;
/*
 * Abstract screen class for handling generic screen logic and screen switching.
 */
public class BattleScreen extends AbstractScreen{

	public static final ObjectMap<String, Class<?>> resourceRequirements = new ObjectMap<String, Class<?>>();
	public static ObjectMap<String, Class<?>> requirementsToDispose = new ObjectMap<String, Class<?>>();
	static {
		resourceRequirements.put(BATTLE_SKIN.getPath(), Skin.class);
		resourceRequirements.put(BUTTON_SOUND.getPath(), Sound.class);
		resourceRequirements.put(UNPLUGGED_POP.getPath(), Sound.class);
		resourceRequirements.put(MOUTH_POP.getPath(), Sound.class);
		resourceRequirements.put(ATTACK_SOUND.getPath(), Sound.class);
		resourceRequirements.put(SWORD_SLASH_SOUND.getPath(), Sound.class);
		resourceRequirements.put(FIREBALL_SOUND.getPath(), Sound.class);
		resourceRequirements.put(INCANTATION.getPath(), Sound.class);
		resourceRequirements.put(HIT_SOUND.getPath(), Sound.class);
		resourceRequirements.put(THWAPPING.getPath(), Sound.class);
		resourceRequirements.put(BATTLE_MUSIC.getPath(), Music.class);
		
		AssetEnum[] assets = new AssetEnum[]{
			ANAL, BLITZ, BALANCED, DEFENSIVE, DOGGY, ERUPT, FELLATIO, FULL_NELSON, KNEELING, HANDY, COWGIRL,
			OFFENSIVE, PRONE, SUPINE, STANDING, AIRBORNE, CASTING, FACE_SITTING, SIXTY_NINE, KNOTTED, 
			SLASH,  BATTLE_HOVER, BATTLE_TEXTBOX, BATTLE_UI, CHARACTER_POTRAIT, HEALTH_ICON_0, STAMINA_ICON_0, BALANCE_ICON_0, MANA_ICON_0,
			HEALTH_ICON_1, STAMINA_ICON_1, BALANCE_ICON_1, MANA_ICON_1, HEALTH_ICON_2, STAMINA_ICON_2, BALANCE_ICON_2, MANA_ICON_2,
			HEALTH_ICON_3, STAMINA_ICON_3, BALANCE_ICON_3, MANA_ICON_3
		};
		for (AssetEnum asset: assets){
			resourceRequirements.put(asset.getPath(), Texture.class);
		}
			
		String[] textureArray = new String[]{
			 "arousal/Trap0.png", "arousal/Trap1.png", "arousal/Trap2.png", 
		};
		for (String path: textureArray){
			resourceRequirements.put(path, Texture.class);
		}
	}
	private final SaveService saveService;
	private final Battle battle;
	private final AssetManager assetManager;
	private final Music music;
	
	protected BattleScreen(ScreenFactory screenFactory, ScreenElements elements, SaveService saveService, Battle battle, AssetManager assetManager) {
		super(screenFactory, elements);
		this.saveService = saveService;
		this.battle = battle;
		this.assetManager = assetManager;
		this.music = assetManager.get(AssetEnum.BATTLE_MUSIC.getPath(), Music.class);
	}

	@Override
	public void buildStage() {
		addActor(battle);
		music.setVolume(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("musicVolume", 1) * .6f);
		music.setLooping(true);
		music.play();
	}

	@Override
	public void render(float delta) {
		super.render(delta);
		battle.battleLoop();
		if (battle.gameExit){
			showScreen(ScreenEnum.MAIN_MENU);
			music.stop();
		}
		else if (battle.isBattleOver()){	
			// begin the process of switching off the battle - the battle itself should stop functioning except display
			saveService.saveDataValue(SaveEnum.CONTEXT, SaveManager.GameContext.ENCOUNTER);
			// this should save the correct scene code based on the end of battle status
			Outcome battleOutcome = battle.getOutcome();
			if (battleOutcome == Outcome.VICTORY) {
				saveService.saveDataValue(SaveEnum.SCENE_CODE, battle.getVictoryScene());
			}
			else if (battleOutcome == Outcome.DEFEAT) {
				saveService.saveDataValue(SaveEnum.SCENE_CODE, battle.getDefeatScene());
			}
			saveService.saveDataValue(SaveEnum.ENEMY, null); // this may need to be removed if the enemy needs to persist until the end of the encounter; endScenes would have to perform this save or the encounter screen itself
			showScreen(ScreenEnum.ENCOUNTER);
			// this should play victory or defeat music
			music.stop();
		}
	}
	
	@Override
	public void dispose(){
		for(String path: requirementsToDispose.keys()){
			if (path.equals(BUTTON_SOUND.getPath())) continue;
			assetManager.unload(path);
		}
		requirementsToDispose = new ObjectMap<String, Class<?>>();
	}

	// this should simply return the battlecode's requirements, rather than use a switch
	public static ObjectMap<String, Class<?>> getRequirements(BattleCode battleCode) {
		ObjectMap<String, Class<?>> requirements = new ObjectMap<String, Class<?>>(BattleScreen.resourceRequirements);
		Array<String> textureArray = new Array<String>();
		switch (battleCode.battleCode){
			case 0:
				textureArray.addAll(WEREBITCH.getPath(), FOREST_BG.getPath(), "arousal/Monster0.png", "arousal/Monster1.png", "arousal/Monster2.png");
				break;
			case 1: 
			case 2004:
				textureArray.addAll(HARPY.getPath(),  HARPY_FELLATIO.getPath(), FOREST_BG.getPath(), "arousal/Monster0.png", "arousal/Monster1.png", "arousal/Monster2.png");
				break;
			case 2: 
				textureArray.addAll(SLIME.getPath(),  SLIME_DOGGY.getPath(), FOREST_BG.getPath(), "arousal/Monster0.png", "arousal/Monster1.png", "arousal/Monster2.png");
				break;
			case 3: 
				textureArray.addAll(BRIGAND.getPath(), BRIGAND_ORAL.getPath(), FOREST_BG.getPath(), "arousal/Human0.png", "arousal/Human1.png", "arousal/Human2.png");
				break;
			case 5: 
				textureArray.addAll(CENTAUR.getPath(), PLAINS_BG.getPath(), "arousal/Monster0.png", "arousal/Monster1.png", "arousal/Monster2.png");
				break;
			case 6: 
				textureArray.addAll(GOBLIN.getPath(),  GOBLIN_FACE_SIT.getPath(), ENCHANTED_FOREST_BG.getPath(), "arousal/Monster0.png", "arousal/Monster1.png", "arousal/Monster2.png");
			case 1005:
				textureArray.addAll(UNICORN.getPath(), PLAINS_BG.getPath(), "arousal/Monster0.png", "arousal/Monster1.png", "arousal/Monster2.png");
				break;
		}
		for (String path: textureArray){
			requirements.put(path, Texture.class);
		}
		requirementsToDispose = requirements;
		return requirements;
	}
	
}

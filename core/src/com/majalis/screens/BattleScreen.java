package com.majalis.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.majalis.asset.AssetEnum;
import com.majalis.battle.Battle;
import com.majalis.battle.BattleCode;
import com.majalis.battle.BattleFactory.EnemyEnum;
import com.majalis.character.EnemyCharacter;
import com.majalis.save.SaveEnum;
import com.majalis.save.SaveManager;
import com.majalis.save.SaveService;
/*
 * Abstract screen class for handling generic screen logic and screen switching.
 */
public class BattleScreen extends AbstractScreen{

	public static final ObjectMap<String, Class<?>> resourceRequirements = new ObjectMap<String, Class<?>>();
	static {
		resourceRequirements.put(AssetEnum.UI_SKIN.getPath(), Skin.class);
		resourceRequirements.put(AssetEnum.BUTTON_SOUND.getPath(), Sound.class);
		resourceRequirements.put(AssetEnum.UNPLUGGED_POP.getPath(), Sound.class);
		resourceRequirements.put(AssetEnum.ATTACK_SOUND.getPath(), Sound.class);
		resourceRequirements.put(AssetEnum.HIT_SOUND.getPath(), Sound.class);
		resourceRequirements.put(AssetEnum.THWAPPING.getPath(), Sound.class);
		resourceRequirements.put(AssetEnum.BATTLE_MUSIC.getPath(), Music.class);
		
		AssetEnum[] assets = new AssetEnum[]{
			AssetEnum.BATTLE_BG,  AssetEnum.STANCE_ARROW, AssetEnum.ANAL, AssetEnum.BLITZ,
			AssetEnum.BALANCED, AssetEnum.DEFENSIVE, AssetEnum.DOGGY, AssetEnum.ERUPT, AssetEnum.FELLATIO, AssetEnum.FULL_NELSON, AssetEnum.KNEELING, AssetEnum.HANDY, AssetEnum.COWGIRL,
			AssetEnum.OFFENSIVE, AssetEnum.PRONE, AssetEnum.SUPINE, AssetEnum.STANDING, AssetEnum.AIRBORNE, AssetEnum.CASTING, AssetEnum.KNOTTED, AssetEnum.SLASH, AssetEnum.BATTLE_HOVER
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
	private final Music music;
	
	protected BattleScreen(ScreenFactory screenFactory, ScreenElements elements, SaveService saveService, Battle battle, AssetManager assetManager) {
		super(screenFactory, elements);
		this.saveService = saveService;
		this.battle = battle;
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
		else if (battle.battleOver){			
			saveService.saveDataValue(SaveEnum.CONTEXT, SaveManager.GameContext.ENCOUNTER);
			if (battle.victory){
				saveService.saveDataValue(SaveEnum.SCENE_CODE, battle.getVictoryScene());
			}
			else {
				saveService.saveDataValue(SaveEnum.SCENE_CODE, battle.getDefeatScene());
			}
			showScreen(ScreenEnum.ENCOUNTER);
			music.stop();
		}
		else {
			draw();
		}
	}
	
	public void draw(){
		batch.begin();
		OrthographicCamera camera = (OrthographicCamera) getCamera();
        batch.setTransformMatrix(camera.view);
		batch.setProjectionMatrix(camera.combined);
		camera.update();
		batch.end();
	}
	// passthrough for battle.dispose
	@Override
	public void dispose(){
		battle.dispose();
	}

	public static ObjectMap<String, Class<?>> getRequirements(BattleCode battleCode) {
		ObjectMap<String, Class<?>> requirements = new ObjectMap<String, Class<?>>(BattleScreen.resourceRequirements);
		Array<String> textureArray = new Array<String>();
		switch (battleCode.battleCode){
			case 0:
				textureArray.addAll(AssetEnum.WEREBITCH.getPath(), "enemies/WereUI.png", "arousal/Monster0.png", "arousal/Monster1.png", "arousal/Monster2.png");
				break;
			case 1: 
				textureArray.addAll(AssetEnum.HARPY.getPath(),  AssetEnum.HARPY_FELLATIO.getPath(), "enemies/HarpyUI.png", "arousal/Monster0.png", "arousal/Monster1.png", "arousal/Monster2.png");
				break;
			case 2: 
				textureArray.addAll(AssetEnum.SLIME.getPath(),  AssetEnum.SLIME_DOGGY.getPath(), "enemies/SlimeUI.png", "arousal/Monster0.png", "arousal/Monster1.png", "arousal/Monster2.png");
				break;
			case 3: 
				textureArray.addAll(AssetEnum.BRIGAND.getPath(),  AssetEnum.SLIME_DOGGY.getPath(), "enemies/BrigandUI.png", "arousal/Human0.png", "arousal/Human1.png", "arousal/Human2.png");
				break;
		}
		for (String path: textureArray){
			requirements.put(path, Texture.class);
		}
		return requirements;
	}
	
}

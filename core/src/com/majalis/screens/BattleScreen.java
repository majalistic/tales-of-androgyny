package com.majalis.screens;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ObjectMap;
import com.majalis.battle.Battle;
import com.majalis.save.SaveEnum;
import com.majalis.save.SaveManager;
import com.majalis.save.SaveService;
/*
 * Abstract screen class for handling generic screen logic and screen switching.
 */
public class BattleScreen extends AbstractScreen{

	public static final ObjectMap<String, Class<?>> resourceRequirements = new ObjectMap<String, Class<?>>();
	static {
		resourceRequirements.put("uiskin.json", Skin.class);
		resourceRequirements.put("sound.wav", Sound.class);
		String[] textureArray = new String[]{
			"WerebitchChibi.png", "WerebitchBasic.jpg", "Harpy.jpg", "HeartSlime.jpg", "Brigand.jpg", 
			"Stances/Balanced.png", "Stances/Defensive.png", "Stances/Doggy.png", "Stances/Erupt.png", "Stances/Fellatio.png", "Stances/Kneeling.png", "Stances/Offensive.png", "Stances/Prone.png", "Stances/Supine.png", "Stances/Airborne.png", "Stances/Casting.png", "Stances/Knotted.png",
			"Arousal/Human0.png", "Arousal/Human1.png", "Arousal/Human2.png", "Arousal/Trap0.png", "Arousal/Trap1.png", "Arousal/Trap2.png", "Arousal/Monster0.png", "Arousal/Monster1.png", "Arousal/Monster2.png"
		};
		for (String path: textureArray){
			resourceRequirements.put(path, Texture.class);
		}
	}
	private final SaveService saveService;
	private final Battle battle;
	
	protected BattleScreen(ScreenFactory screenFactory, ScreenElements elements, SaveService saveService, Battle battle) {
		super(screenFactory, elements);
		this.saveService = saveService;
		this.battle = battle;
	}

	@Override
	public void buildStage() {
		addActor(battle);
	}

	@Override
	public void render(float delta) {
		super.render(delta);
		battle.battleLoop();
		if (battle.gameExit){
			showScreen(ScreenEnum.MAIN_MENU);
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
	
}

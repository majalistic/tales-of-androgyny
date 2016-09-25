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
		resourceRequirements.put("WerebitchChibi.png", Texture.class);
		resourceRequirements.put("WerebitchBasic.jpg", Texture.class);
		resourceRequirements.put("Harpy.jpg", Texture.class);
		resourceRequirements.put("HeartSlime.jpg", Texture.class);
		resourceRequirements.put("Brigand.jpg", Texture.class);
		resourceRequirements.put("Stances/Balanced.png", Texture.class);
		resourceRequirements.put("Stances/Defensive.png", Texture.class);
		resourceRequirements.put("Stances/Doggy.png", Texture.class);
		resourceRequirements.put("Stances/Erupt.png", Texture.class);
		resourceRequirements.put("Stances/Fellatio.png", Texture.class);
		resourceRequirements.put("Stances/Kneeling.png", Texture.class);
		resourceRequirements.put("Stances/Offensive.png", Texture.class);
		resourceRequirements.put("Stances/Prone.png", Texture.class);
		resourceRequirements.put("Stances/Supine.png", Texture.class);
		resourceRequirements.put("Stances/Airborne.png", Texture.class);
		resourceRequirements.put("Stances/Casting.png", Texture.class);
		resourceRequirements.put("Stances/Knotted.png", Texture.class);
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

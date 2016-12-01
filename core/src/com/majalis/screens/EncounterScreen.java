package com.majalis.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ObjectMap;
import com.majalis.asset.AssetEnum;
import com.majalis.encounter.Encounter;
import com.majalis.save.SaveEnum;
import com.majalis.save.SaveManager;
import com.majalis.save.SaveService;
/*
 *Screen for displaying Encounters.  UI that Handles player input while in an encounter.
 */
public class EncounterScreen extends AbstractScreen {

	private static final ObjectMap<String, Class<?>> resourceRequirements = new ObjectMap<String, Class<?>>();
	public static ObjectMap<String, Class<?>> requirementsToDispose = new ObjectMap<String, Class<?>>();
	static {
		resourceRequirements.put(AssetEnum.UI_SKIN.getPath(), Skin.class);
		resourceRequirements.put(AssetEnum.BUTTON_SOUND.getPath(), Sound.class);
		resourceRequirements.put(AssetEnum.DEFAULT_BACKGROUND.getPath(), Texture.class);
		resourceRequirements.put(AssetEnum.BATTLE_HOVER.getPath(), Texture.class);
		resourceRequirements.put(AssetEnum.ENCOUNTER_MUSIC.getPath(), Music.class);
		
	}
	private static AssetManager assetManager;
	private final SaveService saveService;
	private final Encounter encounter;
	private static Music music;
	
	protected EncounterScreen(ScreenFactory screenFactory, ScreenElements elements, AssetManager assetManager, SaveService saveService, String musicPath, Encounter encounter) {
		super(screenFactory, elements);
		EncounterScreen.assetManager = assetManager;
		this.saveService = saveService;
		this.encounter = encounter;
		setMusic(musicPath);
	}

	public static void play(String soundPath) {
		assetManager.get(soundPath, Sound.class).play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume", 1) * .6f);
	}
	
	public static void setMusic(String musicPath){
		if (EncounterScreen.music != null){
			EncounterScreen.music.stop();
		}
		EncounterScreen.music = assetManager.get(musicPath, Music.class);
		music.setVolume(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("musicVolume", 1) * .6f);
		music.setLooping(true);
		music.play();
	}
	
	@Override
	public void buildStage() {
		for (Actor actor: encounter.getActors()){
			this.addActor(actor);
		}        	
	}
	
	@Override
	public void render(float delta) {
		super.render(delta);
		encounter.gameLoop();
		if (encounter.battle){
			saveService.saveDataValue(SaveEnum.CONTEXT, SaveManager.GameContext.BATTLE);
			music.stop();
			showScreen(ScreenEnum.BATTLE);
		}
		if (encounter.encounterOver){
			saveService.saveDataValue(SaveEnum.CONTEXT, SaveManager.GameContext.WORLD_MAP);
			saveService.saveDataValue(SaveEnum.SCENE_CODE, 0);
			music.stop();
			showScreen(ScreenEnum.LOAD_GAME);
		}
		if (encounter.gameExit){
			music.stop();
			showScreen(ScreenEnum.MAIN_MENU);
		}
		else if (encounter.gameOver){
			music.stop();
			showScreen(ScreenEnum.GAME_OVER);
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

	@Override
	public void dispose() {
		for(String path: requirementsToDispose.keys()){
			if (path.equals(AssetEnum.BUTTON_SOUND.getPath())) continue;
			assetManager.unload(path);
		}
		requirementsToDispose = new ObjectMap<String, Class<?>>();
	}

	public static ObjectMap<String, Class<?>> getRequirements(int encounterCode) {
		ObjectMap<String, Class<?>> requirements = new ObjectMap<String, Class<?>>(EncounterScreen.resourceRequirements);

		switch (encounterCode){
			// level up
			case -3: 
				requirements.put(AssetEnum.CLASS_SELECT_BACKGROUND.getPath(), Texture.class);
				requirements.put(AssetEnum.STRENGTH.getPath(), Texture.class);
				requirements.put(AssetEnum.ENDURANCE.getPath(), Texture.class);
				requirements.put(AssetEnum.AGILITY.getPath(), Texture.class);
				requirements.put(AssetEnum.PERCEPTION.getPath(), Texture.class);
				requirements.put(AssetEnum.MAGIC.getPath(), Texture.class);
				requirements.put(AssetEnum.CHARISMA.getPath(), Texture.class);
				break;
			// class choice
			case -2: 
				requirements.put(AssetEnum.GAME_TYPE_BACKGROUND.getPath(), Texture.class);
				requirements.put(AssetEnum.CLASS_SELECT_BACKGROUND.getPath(), Texture.class);
				requirements.put(AssetEnum.STRENGTH.getPath(), Texture.class);
				requirements.put(AssetEnum.ENDURANCE.getPath(), Texture.class);
				requirements.put(AssetEnum.AGILITY.getPath(), Texture.class);
				requirements.put(AssetEnum.PERCEPTION.getPath(), Texture.class);
				requirements.put(AssetEnum.MAGIC.getPath(), Texture.class);
				requirements.put(AssetEnum.CHARISMA.getPath(), Texture.class);
				requirements.put(AssetEnum.SILHOUETTE.getPath(), Texture.class);
				requirements.put(AssetEnum.WAVES.getPath(), Music.class);
				requirements.put(AssetEnum.SMUG_LAUGH.getPath(), Sound.class);
				requirements.put(AssetEnum.HOVEL_MUSIC.getPath(), Music.class);	
				break;
			// default stick
			case -1: 
				requirements.put(AssetEnum.STICK_BACKGROUND.getPath(), Texture.class);
				break;
			// werebitch
			case 0:
				requirements.put(AssetEnum.WEREWOLF_MUSIC.getPath(), Music.class);
				requirements.put(AssetEnum.WEREBITCH.getPath(), Texture.class);
				break;
			// harpy
			case 2004:
				requirements.put(AssetEnum.HARPY.getPath(), Texture.class);
				requirements.put(AssetEnum.FOREST_BG.getPath(), Texture.class);
			case 1:
				requirements.put(AssetEnum.HARPY_FELLATIO.getPath(), Texture.class);
				break;
			// slime
			case 2:
				requirements.put(AssetEnum.SLIME.getPath(), Texture.class);
				requirements.put(AssetEnum.SLIME_DOGGY.getPath(), Texture.class);
				break;
			// brigand
			case 3:
				requirements.put(AssetEnum.BRIGAND_ORAL.getPath(), Texture.class);
				break;
			// dryad
			case 4:
				requirements.put(AssetEnum.SHOP_MUSIC.getPath(), Music.class);
				requirements.put(AssetEnum.DRYAD_BACKGROUND.getPath(), Texture.class);
				break;
				// initial trainer
			case 2001:
				// return trainer
			case 2002:
				requirements.put(AssetEnum.CABIN_BACKGROUND.getPath(), Texture.class);
				requirements.put(AssetEnum.TRAINER.getPath(), Texture.class);
				requirements.put(AssetEnum.TRAINER_MUSIC.getPath(), Music.class);		
				break;
			// initial shopkeep
			case 2003: 
				requirements.put(AssetEnum.TOWN_BG.getPath(), Texture.class);
				requirements.put(AssetEnum.SHOPKEEP.getPath(), Texture.class);
				requirements.put(AssetEnum.SMUG_LAUGH.getPath(), Sound.class);
				requirements.put(AssetEnum.SHOP_MUSIC.getPath(), Music.class);
				break;	
			// centaur
			case 5:
				requirements.put(AssetEnum.SHOP_MUSIC.getPath(), Music.class);
				requirements.put(AssetEnum.CENTAUR.getPath(), Texture.class);
				break;
			default:
				requirements.put(AssetEnum.TRAP_BONUS.getPath(), Texture.class);	
				break;
		}
		requirementsToDispose = requirements;
		return requirements;
	}

}

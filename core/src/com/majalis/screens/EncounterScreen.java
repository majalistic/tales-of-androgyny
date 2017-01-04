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
import com.majalis.encounter.EncounterCode;
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
	private final Encounter encounter;
	private static Music music;
	
	protected EncounterScreen(ScreenFactory screenFactory, ScreenElements elements, AssetManager assetManager, String musicPath, Encounter encounter) {
		super(screenFactory, elements);
		EncounterScreen.assetManager = assetManager;
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
		if (encounter.isSwitching()){
			music.stop();
			showScreen(ScreenEnum.LOAD_GAME);
		}
		else if (encounter.gameExit){
			music.stop();
			showScreen(ScreenEnum.MAIN_MENU);
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

	public static ObjectMap<String, Class<?>> getRequirements(EncounterCode encounterCode) {
		ObjectMap<String, Class<?>> requirements = new ObjectMap<String, Class<?>>(EncounterScreen.resourceRequirements);

		switch (encounterCode){
			case LEVEL_UP: 
				requirements.put(AssetEnum.CLASS_SELECT_BACKGROUND.getPath(), Texture.class);
				requirements.put(AssetEnum.STRENGTH.getPath(), Texture.class);
				requirements.put(AssetEnum.ENDURANCE.getPath(), Texture.class);
				requirements.put(AssetEnum.AGILITY.getPath(), Texture.class);
				requirements.put(AssetEnum.PERCEPTION.getPath(), Texture.class);
				requirements.put(AssetEnum.MAGIC.getPath(), Texture.class);
				requirements.put(AssetEnum.CHARISMA.getPath(), Texture.class);
				break;
			case INITIAL: 
				requirements.put(AssetEnum.GAME_TYPE_BACKGROUND.getPath(), Texture.class);
				requirements.put(AssetEnum.CLASS_SELECT_BACKGROUND.getPath(), Texture.class);
				requirements.put(AssetEnum.STRENGTH.getPath(), Texture.class);
				requirements.put(AssetEnum.ENDURANCE.getPath(), Texture.class);
				requirements.put(AssetEnum.AGILITY.getPath(), Texture.class);
				requirements.put(AssetEnum.PERCEPTION.getPath(), Texture.class);
				requirements.put(AssetEnum.MAGIC.getPath(), Texture.class);
				requirements.put(AssetEnum.CHARISMA.getPath(), Texture.class);
				requirements.put(AssetEnum.SILHOUETTE.getPath(), Texture.class);
				requirements.put(AssetEnum.BURNING_FORT_BG.getPath(), Texture.class);
				requirements.put(AssetEnum.WAVES.getPath(), Music.class);
				requirements.put(AssetEnum.SMUG_LAUGH.getPath(), Sound.class);
				requirements.put(AssetEnum.HOVEL_MUSIC.getPath(), Music.class);	
				break;
			case DEFAULT: 
				requirements.put(AssetEnum.STICK_BACKGROUND.getPath(), Texture.class);
				break;
			case WERESLUT:
				requirements.put(AssetEnum.WEREWOLF_MUSIC.getPath(), Music.class);
				requirements.put(AssetEnum.WEREBITCH.getPath(), Texture.class);
				break;
			case HARPY:
				requirements.put(AssetEnum.HARPY.getPath(), Texture.class);
				requirements.put(AssetEnum.HARPY_FELLATIO.getPath(), Texture.class);
				break;
			case SLIME:
				requirements.put(AssetEnum.SLIME.getPath(), Texture.class);
				requirements.put(AssetEnum.SLIME_DOGGY.getPath(), Texture.class);
				break;
			case BRIGAND:
				requirements.put(AssetEnum.BRIGAND_ORAL.getPath(), Texture.class);
				break;
			case DRYAD:
				requirements.put(AssetEnum.SHOP_MUSIC.getPath(), Music.class);
				requirements.put(AssetEnum.DRYAD_BACKGROUND.getPath(), Texture.class);
				break;
			case CENTAUR:
				requirements.put(AssetEnum.SHOP_MUSIC.getPath(), Music.class);
				requirements.put(AssetEnum.CENTAUR.getPath(), Texture.class);
				requirements.put(AssetEnum.UNICORN.getPath(), Texture.class);
				break;
			case FIRST_BATTLE_STORY:
				requirements.put(AssetEnum.FOREST_BG.getPath(), Texture.class);
			case GOBLIN:
				requirements.put(AssetEnum.GOBLIN.getPath(), Texture.class);
				requirements.put(AssetEnum.GAME_OVER_TUCKERED.getPath(), Texture.class);
				requirements.put(AssetEnum.LOUD_LAUGH.getPath(), Sound.class);
				requirements.put(AssetEnum.WEREWOLF_MUSIC.getPath(), Music.class);
				break;
			case COTTAGE_TRAINER:
				requirements.put(AssetEnum.CLASS_SELECT_BACKGROUND.getPath(), Texture.class);
				requirements.put(AssetEnum.STRENGTH.getPath(), Texture.class);
				requirements.put(AssetEnum.ENDURANCE.getPath(), Texture.class);
				requirements.put(AssetEnum.AGILITY.getPath(), Texture.class);
				requirements.put(AssetEnum.PERCEPTION.getPath(), Texture.class);
				requirements.put(AssetEnum.MAGIC.getPath(), Texture.class);
				requirements.put(AssetEnum.CHARISMA.getPath(), Texture.class);
			case COTTAGE_TRAINER_VISIT:
				requirements.put(AssetEnum.CABIN_BACKGROUND.getPath(), Texture.class);
				requirements.put(AssetEnum.TRAINER.getPath(), Texture.class);
				requirements.put(AssetEnum.TRAINER_MUSIC.getPath(), Music.class);		
				break;
			case TOWN_STORY: 
				requirements.putAll(TownScreen.resourceRequirements);
				requirements.put(AssetEnum.TOWN_BG.getPath(), Texture.class);
				requirements.put(AssetEnum.SHOPKEEP.getPath(), Texture.class);
				requirements.put(AssetEnum.SMUG_LAUGH.getPath(), Sound.class);
				requirements.put(AssetEnum.SHOP_MUSIC.getPath(), Music.class);
			case SHOP:
				requirements.putAll(TownScreen.resourceRequirements);
				break;	
			case OGRE_WARNING_STORY:
				requirements.put(AssetEnum.TRAINER_MUSIC.getPath(), Music.class);
				break;
			case OGRE_STORY:
				requirements.put(AssetEnum.GAME_OGRE.getPath(), Texture.class);
				requirements.put(AssetEnum.OGRE.getPath(), Sound.class);
				requirements.put(AssetEnum.WEREWOLF_MUSIC.getPath(), Music.class);
				requirements.put(AssetEnum.HEAVY_MUSIC.getPath(), Music.class);
				break;
			case MERI_COTTAGE:
				requirements.put(AssetEnum.MERI_SILHOUETTE.getPath(), Texture.class);
				requirements.put(AssetEnum.CABIN_BACKGROUND.getPath(), Texture.class);
				requirements.put(AssetEnum.TRAINER_MUSIC.getPath(), Music.class);
				requirements.put(AssetEnum.WEREWOLF_MUSIC.getPath(), Music.class);	
				break;
			case MERI_COTTAGE_VISIT:
				requirements.put(AssetEnum.CABIN_BACKGROUND.getPath(), Texture.class);
				requirements.put(AssetEnum.TRAINER_MUSIC.getPath(), Music.class);
				break;
			case CAMP_AND_EAT:
				requirements.put(AssetEnum.SHOP_MUSIC.getPath(), Music.class);
				break;
			case STARVATION:
				requirements.put(AssetEnum.GAME_OVER_TUCKERED.getPath(), Texture.class);
				requirements.put(AssetEnum.WEREWOLF_MUSIC.getPath(), Music.class);
			default:
				requirements.put(AssetEnum.TRAP_BONUS.getPath(), Texture.class);	
				break;
		}
		requirementsToDispose = requirements;
		return requirements;
	}

}

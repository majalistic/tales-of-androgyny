package com.majalis.screens;

import static com.majalis.asset.AssetEnum.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.majalis.asset.AssetEnum;
import com.majalis.encounter.Encounter;
import com.majalis.encounter.EncounterCode;

/*
 *Screen for displaying Encounters.  UI that Handles player input while in an encounter.
 */
public class EncounterScreen extends AbstractScreen {
	private static final Array<AssetDescriptor<?>> resourceRequirements = new Array<AssetDescriptor<?>>();
	public static Array<AssetDescriptor<?>> requirementsToDispose = new Array<AssetDescriptor<?>>();
	static {
		resourceRequirements.add(AssetEnum.UI_SKIN.getSkin());
		resourceRequirements.add(AssetEnum.BUTTON_SOUND.getSound());
		AssetEnum[] assets = new AssetEnum[] { DEFAULT_BACKGROUND, BATTLE_HOVER, PORTRAIT_NEUTRAL, PORTRAIT_AHEGAO,
				PORTRAIT_FELLATIO, PORTRAIT_MOUTHBOMB, PORTRAIT_GRIN, PORTRAIT_HIT, PORTRAIT_LOVE, PORTRAIT_LUST,
				PORTRAIT_SMILE, PORTRAIT_SURPRISE, PORTRAIT_GRIMACE, PORTRAIT_POUT, PORTRAIT_HAPPY, MARS_ICON_0,
				MARS_ICON_1, MARS_ICON_2, MARS_ICON_3, MARS_ICON_4, STUFFED_BELLY, FULL_BELLY, BIG_BELLY, FLAT_BELLY };
		for (AssetEnum asset : assets) {
			resourceRequirements.add(asset.getTexture());
		}

		resourceRequirements.add(AssetEnum.ENCOUNTER_MUSIC.getMusic());
	}
	private static AssetManager assetManager;
	private final Encounter encounter;
	private static Music music;

	protected EncounterScreen(ScreenFactory screenFactory, ScreenElements elements, AssetManager assetManager,
			AssetDescriptor<Music> musicPath, Encounter encounter) {
		super(screenFactory, elements);
		EncounterScreen.assetManager = assetManager;
		this.encounter = encounter;
		setMusic(musicPath);
	}

	public static void play(AssetDescriptor<Sound> sound) {
		assetManager.get(sound)
				.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume", 1) * .6f);
	}

	public static void setMusic(AssetDescriptor<Music> musicPath) {
		if (EncounterScreen.music != null) {
			EncounterScreen.music.stop();
		}
		EncounterScreen.music = assetManager.get(musicPath);
		music.setVolume(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("musicVolume", 1) * .6f);
		music.setLooping(true);
		music.play();
	}

	@Override
	public void buildStage() {
		for (Actor actor : encounter.getActors()) {
			this.addActor(actor);
		}
	}

	@Override
	public void render(float delta) {
		super.render(delta);
		encounter.gameLoop();
		if (encounter.isSwitching()) {
			music.stop();
			showScreen(ScreenEnum.CONTINUE);
		} else if (encounter.gameExit) {
			music.stop();
			showScreen(ScreenEnum.MAIN_MENU);
		} else {
			draw();
		}
	}

	public void draw() {
		batch.begin();
		OrthographicCamera camera = (OrthographicCamera) getCamera();
		batch.setTransformMatrix(camera.view);
		batch.setProjectionMatrix(camera.combined);
		camera.update();
		batch.end();
	}

	@Override
	public void dispose() {
		for (AssetDescriptor<?> path : requirementsToDispose) {
			if (path.fileName.equals(AssetEnum.BUTTON_SOUND.getSound().fileName))
				continue;
			assetManager.unload(path.fileName);
		}
		requirementsToDispose = new Array<AssetDescriptor<?>>();
	}

	public static Array<AssetDescriptor<?>> getRequirements(EncounterCode encounterCode) {
		Array<AssetDescriptor<?>> requirements = new Array<AssetDescriptor<?>>(EncounterScreen.resourceRequirements);

		// remove this switch, place these asset requirements in the encounter
		// code itself
		switch (encounterCode) {
		case LEVEL_UP:
			requirements.add(AssetEnum.CLASS_SELECT_BACKGROUND.getTexture());
			requirements.add(AssetEnum.STRENGTH.getTexture());
			requirements.add(AssetEnum.ENDURANCE.getTexture());
			requirements.add(AssetEnum.AGILITY.getTexture());
			requirements.add(AssetEnum.PERCEPTION.getTexture());
			requirements.add(AssetEnum.MAGIC.getTexture());
			requirements.add(AssetEnum.CHARISMA.getTexture());
			break;
		case INITIAL:
			requirements.add(AssetEnum.GAME_TYPE_BACKGROUND.getTexture());
			requirements.add(AssetEnum.CLASS_SELECT_BACKGROUND.getTexture());
			requirements.add(AssetEnum.STRENGTH.getTexture());
			requirements.add(AssetEnum.ENDURANCE.getTexture());
			requirements.add(AssetEnum.AGILITY.getTexture());
			requirements.add(AssetEnum.PERCEPTION.getTexture());
			requirements.add(AssetEnum.MAGIC.getTexture());
			requirements.add(AssetEnum.CHARISMA.getTexture());
			requirements.add(AssetEnum.SILHOUETTE.getTexture());
			requirements.add(AssetEnum.BURNING_FORT_BG.getTexture());
			requirements.add(AssetEnum.NORMAL_BOX.getTexture());
			requirements.add(AssetEnum.SKILL_SELECTION_BACKGROUND.getTexture());
			requirements.add(AssetEnum.CHARACTER_CUSTOM_BACKGROUND.getTexture());
			requirements.add(AssetEnum.SMUG_LAUGH.getSound());
			requirements.add(AssetEnum.WAVES.getMusic());
			requirements.add(AssetEnum.HOVEL_MUSIC.getMusic());
			requirements.add(AssetEnum.INITIAL_MUSIC.getMusic());
			break;
		case DEFAULT:
			requirements.add(AssetEnum.STICK_BACKGROUND.getTexture());
			break;
		case WERESLUT:
			requirements.add(AssetEnum.WEREWOLF_MUSIC.getMusic());
			requirements.add(AssetEnum.WEREBITCH.getTexture());
			break;
		case HARPY:
			requirements.add(AssetEnum.HARPY_FELLATIO_1.getTexture());
			break;
		case SLIME:
			requirements.add(AssetEnum.SLIME.getTexture());
			requirements.add(AssetEnum.SLIME_DOGGY.getTexture());
			break;
		case BRIGAND:
			requirements.add(AssetEnum.BRIGAND_ORAL.getTexture());
			break;
		case DRYAD:
			requirements.add(AssetEnum.SHOP_MUSIC.getMusic());
			requirements.add(AssetEnum.DRYAD_BACKGROUND.getTexture());
			break;
		case CENTAUR:
			requirements.add(AssetEnum.SHOP_MUSIC.getMusic());
			break;
		case GADGETEER:
			requirements.add(AssetEnum.GADGETEER.getTexture());
			requirements.add(AssetEnum.BATTLE_TEXTBOX.getTexture());
			requirements.add(AssetEnum.TEXT_BOX.getTexture());
			requirements.add(AssetEnum.EQUIP.getSound());
			requirements.add(AssetEnum.GADGETEER_MUSIC.getMusic());
			break;
		case ORC:
			requirements.add(AssetEnum.ORC.getTexture());
			requirements.add(AssetEnum.GAPE.getTexture());
			requirements.add(AssetEnum.WEREWOLF_MUSIC.getMusic());
			break;
		case ADVENTURER:
		case STORY_FEM:
			requirements.add(AssetEnum.ADVENTURER.getTexture());
			requirements.add(AssetEnum.GADGETEER_MUSIC.getMusic());
			break;
		case OGRE:
			requirements.add(AssetEnum.OGRE.getTexture());
			requirements.add(AssetEnum.OGRE_BANGED.getTexture());
			requirements.add(AssetEnum.WEREWOLF_MUSIC.getMusic());
			break;
		case INN:
			requirements.add(AssetEnum.INNKEEPER.getTexture());
			requirements.add(AssetEnum.KEYHOLE.getTexture());
			requirements.add(AssetEnum.GAME_OVER_KEYHOLE.getTexture());
			break;
		case FIRST_BATTLE_STORY:
			requirements.add(AssetEnum.FOREST_BG.getTexture());
		case GOBLIN:
			requirements.add(AssetEnum.GOBLIN.getTexture());
			requirements.add(AssetEnum.GOBLIN_MALE.getTexture());
			requirements.add(AssetEnum.GAME_OVER_TUCKERED.getTexture());
			requirements.add(AssetEnum.LOUD_LAUGH.getSound());
			requirements.add(AssetEnum.WEREWOLF_MUSIC.getMusic());
			break;
		case COTTAGE_TRAINER:
			requirements.add(AssetEnum.CLASS_SELECT_BACKGROUND.getTexture());
			requirements.add(AssetEnum.STRENGTH.getTexture());
			requirements.add(AssetEnum.ENDURANCE.getTexture());
			requirements.add(AssetEnum.AGILITY.getTexture());
			requirements.add(AssetEnum.PERCEPTION.getTexture());
			requirements.add(AssetEnum.MAGIC.getTexture());
			requirements.add(AssetEnum.CHARISMA.getTexture());
			requirements.add(AssetEnum.NORMAL_BOX.getTexture());
		case COTTAGE_TRAINER_VISIT:
			requirements.add(AssetEnum.CABIN_BACKGROUND.getTexture());
			requirements.add(AssetEnum.TRAINER.getTexture());
			requirements.add(AssetEnum.TRAINER_MUSIC.getMusic());
			break;
		case TOWN_STORY:
			requirements.add(AssetEnum.TOWN_BG.getTexture());
			requirements.add(AssetEnum.SMUG_LAUGH.getSound());
			requirements.add(AssetEnum.SHOP_MUSIC.getMusic());
		case SHOP:
		case WEAPON_SHOP:
			requirements.addAll(TownScreen.resourceRequirements);
			break;
		case OGRE_WARNING_STORY:
			requirements.add(AssetEnum.TRAINER_MUSIC.getMusic());
			break;
		case OGRE_STORY:
			requirements.add(AssetEnum.GAME_OGRE.getTexture());
			requirements.add(AssetEnum.OGRE_GROWL.getSound());
			requirements.add(AssetEnum.WEREWOLF_MUSIC.getMusic());
			requirements.add(AssetEnum.HEAVY_MUSIC.getMusic());
			break;
		case MERI_COTTAGE:
			requirements.add(AssetEnum.MERI_SILHOUETTE.getTexture());
			requirements.add(AssetEnum.CABIN_BACKGROUND.getTexture());
			requirements.add(AssetEnum.TRAINER_MUSIC.getMusic());
			requirements.add(AssetEnum.WEREWOLF_MUSIC.getMusic());
			break;
		case MERI_COTTAGE_VISIT:
			requirements.add(AssetEnum.CABIN_BACKGROUND.getTexture());
			requirements.add(AssetEnum.TRAINER_MUSIC.getMusic());
			break;
		case CAMP_AND_EAT:
			requirements.add(AssetEnum.SHOP_MUSIC.getMusic());
			break;
		case STARVATION:
			requirements.add(AssetEnum.GAME_OVER_TUCKERED.getTexture());
			requirements.add(AssetEnum.WEREWOLF_MUSIC.getMusic());
		default:
			requirements.add(AssetEnum.TRAP_BONUS.getTexture());
			break;
		}
		requirementsToDispose = requirements;
		return requirements;
	}

}

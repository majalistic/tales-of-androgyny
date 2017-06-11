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
		AssetEnum[] assets = new AssetEnum[] { NULL, DEFAULT_BACKGROUND, BATTLE_HOVER, PORTRAIT_NEUTRAL, PORTRAIT_AHEGAO,
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
		requirements.addAll(encounterCode.getRequirements());
		requirementsToDispose = requirements;
		return requirements;
	}

}

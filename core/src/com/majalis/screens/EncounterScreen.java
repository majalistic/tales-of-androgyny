package com.majalis.screens;

import static com.majalis.asset.AssetEnum.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.majalis.asset.AssetEnum;
import com.majalis.encounter.Encounter;
import com.majalis.encounter.EncounterCode;
import com.majalis.save.LoadService;
import com.majalis.save.SaveEnum;
/*
 *Screen for displaying Encounters.  UI that Handles player input while in an encounter.
 */
public class EncounterScreen extends AbstractScreen {
	private static final Array<AssetDescriptor<?>> resourceRequirements = new Array<AssetDescriptor<?>>();
	public static Array<AssetDescriptor<?>> requirementsToDispose = new Array<AssetDescriptor<?>>();
	static {
		resourceRequirements.add(AssetEnum.UI_SKIN.getSkin());
		resourceRequirements.add(AssetEnum.BUTTON_SOUND.getSound());
		AssetEnum[] assets = new AssetEnum[] { NULL, DEFAULT_BACKGROUND, BATTLE_HOVER, STANCE_ARROW, PORTRAIT_NEUTRAL, PORTRAIT_AHEGAO,
				PORTRAIT_FELLATIO, PORTRAIT_MOUTHBOMB, PORTRAIT_GRIN, PORTRAIT_HIT, PORTRAIT_LOVE, PORTRAIT_LUST,
				PORTRAIT_SMILE, PORTRAIT_SURPRISE, PORTRAIT_GRIMACE, PORTRAIT_POUT, PORTRAIT_HAPPY, MARS_ICON_0,
				MARS_ICON_1, MARS_ICON_2, MARS_ICON_3, MARS_ICON_4, STUFFED_BELLY, FULL_BELLY, BIG_BELLY, FLAT_BELLY,
				TRAP_BONUS, APPLE, EXP, GOLD, TIME, HEART};
		for (AssetEnum asset : assets) {
			resourceRequirements.add(asset.getTexture());
		}

		resourceRequirements.add(AssetEnum.ENCOUNTER_MUSIC.getMusic());
	}
	private final AssetManager assetManager;
	private final LoadService loadService;
	private final Encounter encounter;
	private TextButton saveButton;
	private AssetEnum musicPath;
	private Music music;

	protected EncounterScreen(ScreenFactory screenFactory, ScreenElements elements, AssetManager assetManager, AssetEnum musicPath, LoadService loadService, Encounter encounter) {
		super(screenFactory, elements);
		this.assetManager = assetManager;
		this.loadService = loadService;
		this.encounter = encounter;
		setMusic(musicPath);
	}

	public void setMusic(AssetEnum musicPath) {
		if (music != null && this.musicPath != musicPath) {
			music.stop();
		}
		
		this.musicPath = musicPath;
		music = assetManager.get(this.musicPath.getMusic());
		music.setVolume(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("musicVolume", 1) * .6f);
		music.setLooping(true);
		music.play();
	}

	@Override
	public void buildStage() {
		for (Actor actor : encounter.getActors()) {
			this.addActor(actor);
		}
		
		final Sound buttonSound = assetManager.get(AssetEnum.BUTTON_SOUND.getSound());
		saveButton = new TextButton("Save", assetManager.get(AssetEnum.UI_SKIN.getSkin()));
		saveButton.setPosition(1650, 100);
		saveButton.setWidth(150);
		saveButton.addListener(
				new ClickListener() {
					@Override
			        public void clicked(InputEvent event, float x, float y) {
						buttonSound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
						showScreen(ScreenEnum.SAVE);
			        }
				}
			);	
		
		this.addActor(saveButton);
		
	}

	@Override
	public void render(float delta) {
		super.render(delta);
		encounter.gameLoop();
		AssetEnum newMusic = loadService.loadDataValue(SaveEnum.MUSIC, AssetEnum.class);
		if (newMusic != null && newMusic != musicPath) {
			setMusic(newMusic);
		}
		if (encounter.showSave) {
			saveButton.addAction(Actions.show());
		}
		else {
			saveButton.addAction(Actions.hide());
		}
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

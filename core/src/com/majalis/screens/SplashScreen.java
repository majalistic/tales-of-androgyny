package com.majalis.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.majalis.asset.AssetEnum;
/*
 * Splash screen for initial load.
 */
public class SplashScreen extends AbstractScreen {

	private final AssetManager assetManager;
	private final int minTime;
	private final boolean fullLoad;
	private int clocktick;
	private Sound sound;
	private Skin skin;
	private ProgressBar progress;
	private Texture background;
	
	public SplashScreen(ScreenFactory factory, ScreenElements elements, AssetManager assetManager, int minTime, boolean fullLoad) {
		super(factory, elements);
		this.assetManager = assetManager;
		this.minTime = minTime;
		this.fullLoad = fullLoad;
		clocktick = 0;
	}

	@Override
	public void buildStage() {
		assetManager.load(AssetEnum.INTRO_SOUND.getSound());
		assetManager.load(AssetEnum.BATTLE_SKIN.getSkin());
		assetManager.load(AssetEnum.SPLASH_SCREEN.getTexture());
		assetManager.finishLoading();
		sound = assetManager.get(AssetEnum.INTRO_SOUND.getSound());
		skin = assetManager.get(AssetEnum.BATTLE_SKIN.getSkin());
		background = assetManager.get(AssetEnum.SPLASH_SCREEN.getTexture());
		
		if (fullLoad) {
			// need a better way to ensure all assets are loaded
			for (AssetEnum value : AssetEnum.values()) {
				assetManager.load(value.getAsset());
				assetManager.load(value.getAsset());
			}
		}
		else {
			// asynchronous
			Array<AssetDescriptor<?>> pathToType = MainMenuScreen.resourceRequirements;
			pathToType.addAll(ReplayScreen.resourceRequirements);
			pathToType.addAll(CreditsScreen.resourceRequirements);
			for (AssetDescriptor<?> path: pathToType) {
				if (!assetManager.isLoaded(path.fileName)) {
					assetManager.load(path);
				}
			}
			
			assetManager.load(AssetEnum.LOADING.getTexture());
			
		}
		
		progress = new ProgressBar(0, 1, .05f, false, skin);
		progress.setSize(280, 30);
		progress.setPosition(1600, 105);
		this.addActor(progress);
	}
	
	@Override
	public void render(float delta) {
		super.render(delta);
		OrthographicCamera camera = (OrthographicCamera) getCamera();
        batch.setTransformMatrix(camera.view);
		batch.setProjectionMatrix(camera.combined);
		camera.update();
		batch.begin();
		if (!assetManager.update() || clocktick++ < minTime) {
			batch.draw(background, 1500, 600, background.getWidth() / (background.getHeight() / 900f), 900);
			progress.setValue(assetManager.getProgress());
			font.setColor(Color.BLACK);
			font.draw(batch, "Loading: " + (int)(assetManager.getProgress() * 100) + "%", 2650, 600);
		}	
		else {
			showScreen(ScreenEnum.MAIN_MENU);
		}
		batch.end();
	}
	
	@Override
	public void show() {
		super.show();
		sound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.7f);
	    getRoot().getColor().a = 0;
	    getRoot().addAction(Actions.fadeIn(0.5f));
	}
}
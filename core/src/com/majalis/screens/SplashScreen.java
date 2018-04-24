package com.majalis.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
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
	private Texture background;
	
	public SplashScreen(ScreenFactory factory, ScreenElements elements, AssetManager assetManager, int minTime, boolean fullLoad) {
		super(factory, elements, null);
		this.assetManager = assetManager;
		this.minTime = minTime;
		this.fullLoad = fullLoad;
		clocktick = 0;
	}
	
	@Override
	protected void switchFade(ScreenEnum screenRequest, AbstractScreen currentScreen, AssetEnum oldMusicPath, Music oldMusic) { switchScreen(screenRequest, currentScreen, oldMusicPath, oldMusic); }
	
	@Override
	public void buildMenu() {}		
	
	@Override
	public void buildStage() {
		clearRed = .8f;
        clearGreen = .9f;
        clearBlue = .9f;
        clearAlpha = 1;
		assetManager.load(AssetEnum.INTRO_SOUND.getSound());
		assetManager.load(AssetEnum.BATTLE_SKIN.getSkin());
		assetManager.load(AssetEnum.SPLASH_SCREEN.getTexture());
		assetManager.load(AssetEnum.NULL.getTexture());
		assetManager.load(AssetEnum.MENU_BUTTON_UP.getTexture());
		assetManager.load(AssetEnum.MENU_BUTTON_DOWN.getTexture());
		assetManager.load(AssetEnum.MENU_BUTTON_HIGHLIGHT.getTexture());
		assetManager.load(AssetEnum.MUTE_BUTTON_UP.getTexture());
		assetManager.load(AssetEnum.MUTE_BUTTON_DOWN.getTexture());
		assetManager.load(AssetEnum.MUTE_BUTTON_HIGHLIGHT.getTexture());
		assetManager.load(AssetEnum.MUTE_BUTTON_ON.getTexture());
		assetManager.load(AssetEnum.NULL.getTexture());
		assetManager.finishLoading();
		sound = assetManager.get(AssetEnum.INTRO_SOUND.getSound());
		background = assetManager.get(AssetEnum.SPLASH_SCREEN.getTexture());
		
		if (fullLoad) {
			// need a better way to ensure all assets are loaded
			for (AssetEnum value : AssetEnum.values()) {
				assetManager.load(value.getAsset());
			}
			for (AssetDescriptor<?> requirement : MainMenuScreen.resourceRequirements) {
				assetManager.load(requirement);
			}
		}
		else {
			// asynchronous
			Array<AssetDescriptor<?>> pathToType = MainMenuScreen.resourceRequirements;
			pathToType.addAll(CreditsScreen.resourceRequirements);
			for (AssetDescriptor<?> path: pathToType) {
				if (!assetManager.isLoaded(path.fileName)) {
					assetManager.load(path);
				}
			}
			assetManager.load(AssetEnum.LOADING.getTexture());
		}
	}
	
	@Override
	public void render(float delta) {
		super.render(delta);
		OrthographicCamera camera = (OrthographicCamera) getCamera();
        batch.setTransformMatrix(camera.view);
		batch.setProjectionMatrix(camera.combined);
		camera.update();
		batch.begin();
		batch.draw(background, 1500, 600, background.getWidth() / (background.getHeight() / 900f), 900);
		font.setColor(Color.BLACK);
		if (assetManager.update(75) && clocktick++ > minTime) {
			showScreen(ScreenEnum.MAIN_MENU);
		}
		batch.end();
	}
	
	@Override
	public void show() {
		super.show();
		sound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.7f);
	}
}
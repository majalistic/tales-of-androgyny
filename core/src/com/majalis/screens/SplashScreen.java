package com.majalis.screens;

import static com.majalis.asset.AssetEnum.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
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
	private Image splash;
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
		
		for (AssetEnum asset : new AssetEnum[]{SPLASH_SCREEN, NULL, MENU_BUTTON_UP, MENU_BUTTON_DOWN, MENU_BUTTON_HIGHLIGHT, MUTE_BUTTON_UP, MUTE_BUTTON_DOWN, MUTE_BUTTON_HIGHLIGHT, MUTE_BUTTON_ON, NULL, 
				MAIN_MENU_STATIONARY, MAIN_MENU_BG2_LEFT, MAIN_MENU_BG2_RIGHT, MAIN_MENU_BG1, MAIN_MENU_MG4_LEFT, MAIN_MENU_MG4_RIGHT, MAIN_MENU_MG3_LEFT, MAIN_MENU_MG3_RIGHT, MAIN_MENU_MG2_LEFT, MAIN_MENU_MG2_RIGHT, MAIN_MENU_MG1_LEFT, MAIN_MENU_MG1_RIGHT, MAIN_MENU_FG}) {
			assetManager.load(asset.getTexture());
		}
		
		assetManager.finishLoading();
		sound = assetManager.get(AssetEnum.INTRO_SOUND.getSound());
		
		Image stationary = getImage(MAIN_MENU_STATIONARY);
		stationary.setPosition(0, 0);
		getImage(MAIN_MENU_BG2_LEFT);
		getImage(MAIN_MENU_BG2_RIGHT);
		getImage(MAIN_MENU_BG1);
		getImage(MAIN_MENU_MG4_LEFT);
		getImage(MAIN_MENU_MG4_RIGHT);
		getImage(MAIN_MENU_MG3_LEFT);
		getImage(MAIN_MENU_MG3_RIGHT);
		getImage(MAIN_MENU_MG2_LEFT);
		getImage(MAIN_MENU_MG2_RIGHT);
		getImage(MAIN_MENU_MG1_LEFT);
		getImage(MAIN_MENU_MG1_RIGHT);
		getImage(MAIN_MENU_FG);
		
		splash = getImage(SPLASH_SCREEN);
		splash.setPosition(500, 0);
		
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
	
	private Image getImage(AssetEnum asset) {
		Image temp = new Image (assetManager.get(asset.getTexture()));
		this.addActor(temp);
		return temp;
	}
	
	@Override
	public void render(float delta) {
		super.render(delta);
		if (assetManager.update(75) && clocktick++ > minTime) {
			splash.addAction(Actions.sequence(Actions.fadeOut(1), new Action() {
				@Override
				public boolean act(float delta) {
					showScreen(ScreenEnum.MAIN_MENU);
					return true;
				}
			}));		
		}
	}
	
	@Override
	public void show() {
		super.show();
		sound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.7f);
	}
}
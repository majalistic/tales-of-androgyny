package com.majalis.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ObjectMap;
import com.majalis.asset.AssetEnum;
/*
 * Splash screen for initial load.
 */
public class SplashScreen extends AbstractScreen {

	private final AssetManager assetManager;
	private final int minTime;
	private int clocktick;
	private Sound sound;
	private Skin skin;
	private ProgressBar progress;
	private Texture background;
	
	public SplashScreen(ScreenFactory factory, ScreenElements elements, AssetManager assetManager, int minTime) {
		super(factory, elements);
		this.assetManager = assetManager;
		this.minTime = minTime;
		clocktick = 0;
	}

	@Override
	public void buildStage() {
		assetManager.load(AssetEnum.INTRO_SOUND.getPath(), Sound.class);
		assetManager.load(AssetEnum.UI_SKIN.getPath(), Skin.class);
		assetManager.load(AssetEnum.SPLASH_SCREEN.getPath(), Texture.class);
		assetManager.finishLoading();
		sound = assetManager.get(AssetEnum.INTRO_SOUND.getPath(), Sound.class);
		skin = assetManager.get(AssetEnum.UI_SKIN.getPath(), Skin.class);
		background = assetManager.get(AssetEnum.SPLASH_SCREEN.getPath(), Texture.class);
		
		// asynchronous
		ObjectMap<String, Class<?>> pathToType = MainMenuScreen.resourceRequirements;
		for (String path: pathToType.keys()){
			if (!assetManager.isLoaded(path)){
				assetManager.load(path, pathToType.get(path));
			}
		}
		
		progress = new ProgressBar(0, 1, .05f, false, skin);
		progress.setWidth(280);
		progress.setHeight(30);
		progress.addAction(Actions.moveTo(980, 70));
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
		if (!assetManager.update() || clocktick++ < minTime){
			batch.draw(background, 900, 300, background.getWidth() / (background.getHeight() / 800f), 800);
			progress.setValue(assetManager.getProgress());
			font.draw(batch, "Loading: " + (int)(assetManager.getProgress() * 100) + "%", 1725, 400);
		}	
		else {
			showScreen(ScreenEnum.MAIN_MENU);
		}
		batch.end();
	}
	
	@Override
	public void show(){
		super.show();
		sound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.7f);
	    getRoot().getColor().a = 0;
	    getRoot().addAction(Actions.fadeIn(0.5f));
	}
}
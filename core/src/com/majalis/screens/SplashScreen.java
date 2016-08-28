package com.majalis.screens;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.ObjectMap;
/*
 * Splash screen for initial load.
 */
public class SplashScreen extends AbstractScreen {

	private final AssetManager assetManager;
	private final int minTime;
	private int clocktick;
	
	public SplashScreen(ScreenFactory factory, ScreenElements elements, AssetManager assetManager, int minTime) {
		super(factory, elements);
		this.assetManager = assetManager;
		this.minTime = minTime;
		clocktick = 0;
	}

	@Override
	public void buildStage() {
		// asynchronous
		ObjectMap<String, Class<?>> pathToType = MainMenuScreen.resourceRequirements;
		for (String path: pathToType.keys()){
			if (!assetManager.isLoaded(path)){
				assetManager.load(path, pathToType.get(path));
			}
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
		if (!assetManager.update() || clocktick < minTime){
			font.draw(batch, String.valueOf(clocktick++), 1675, 500);
			font.draw(batch, "Loading: " + (assetManager.getProgress() * 100) + "%", 1125, 750);
		}	
		else {
			showScreen(ScreenEnum.MAIN_MENU);
		}
		batch.end();
	}
	
	@Override
	public void show() {
		super.show();
		font.getData().setScale(4, 4);
	}	
}
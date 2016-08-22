package com.majalis.traprpg;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
/*
 * Splash screen for initial load.
 */
public class SplashScreen extends AbstractScreen {

	private final AssetManager assetManager;
	private int clocktick;
	
	public SplashScreen(ScreenFactory factory, AssetManager assetManager) {
		super(factory);
		this.assetManager = assetManager;
		clocktick = 0;
	}

	@Override
	public void buildStage() {
		// asynchronous
		assetManager.load("uiskin.json", Skin.class);
		assetManager.load("wereslut.png", Texture.class);
		assetManager.load("sound.wav", Sound.class);
	}
	
	@Override
	public void render(float delta) {
		super.render(delta);
		OrthographicCamera camera = (OrthographicCamera) getCamera();
        batch.setTransformMatrix(camera.view);
		batch.setProjectionMatrix(camera.combined);
		camera.update();
		batch.begin();
		if (!assetManager.update() || clocktick < 50){
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
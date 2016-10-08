package com.majalis.screens;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
/*
 * Screen that bridges between two screens when assets need to be loaded.
 */
public class LoadScreen extends AbstractScreen {

	private final AssetManager assetManager;
	private final ScreenEnum screenRequest;
	private final BitmapFont largeFont;
	private int clocktick;
	
	protected LoadScreen(ScreenFactory screenFactory, ScreenElements elements, AssetManager assetManager, ScreenEnum screenRequest) {
		super(screenFactory, elements);
		this.assetManager = assetManager;
		this.screenRequest = screenRequest;
		this.largeFont = fontFactory.getFont(72);
		clocktick = 0;
	}

	@Override
	public void buildStage() {
		// load synchronously the loadscreen asset if it isn't already loaded

	}

	@Override
	public void render(float delta){
		super.render(delta);
		OrthographicCamera camera = (OrthographicCamera) getCamera();
        batch.setTransformMatrix(camera.view);
		batch.setProjectionMatrix(camera.combined);
		camera.update();
		batch.begin();
		if (!assetManager.update() || clocktick < 25){
			font.draw(batch, String.valueOf(clocktick++), 1675, 500);
			largeFont.draw(batch, "Loading: " + (int)(assetManager.getProgress() * 100) + "%", 1125, 750);
		}	
		else {
			showScreen(screenRequest);
		}
		batch.end();
	}
}

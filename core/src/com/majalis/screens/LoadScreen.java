package com.majalis.screens;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
/*
 * Screen that bridges between two screens when assets need to be loaded.
 */
public class LoadScreen extends AbstractScreen {

	private final AssetManager assetManager;
	private final ScreenEnum screenRequest;
	private int clocktick;
	
	protected LoadScreen(ScreenFactory screenFactory, ScreenElements elements, AssetManager assetManager, ScreenEnum screenRequest) {
		super(screenFactory, elements);
		this.assetManager = assetManager;
		this.screenRequest = screenRequest;
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
			font.draw(batch, "Loading: " + (assetManager.getProgress() * 100) + "%", 1125, 750);
		}	
		else {
			showScreen(screenRequest);
		}
		batch.end();
	}
	
	@Override
	public void show() {
		super.show();
		font.getData().setScale(4, 4);
	}	
	
	
}

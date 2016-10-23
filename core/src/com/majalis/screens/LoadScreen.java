package com.majalis.screens;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.majalis.asset.AssetEnum;
/*
 * The replay encounters.  UI that handles player input to select and load and encounters to experience again.
 */
public class LoadScreen extends AbstractScreen {
	
	private final AssetManager assetManager;
	private final ScreenEnum screenRequest;
	private final BitmapFont largeFont;
	private ProgressBar progress;
	private int clocktick;
	private final Skin skin;
	
	public LoadScreen(ScreenFactory factory, ScreenElements elements, AssetManager assetManager, ScreenEnum screenRequest) {
		super(factory, elements);
		this.assetManager = assetManager;
		this.skin = assetManager.get(AssetEnum.UI_SKIN.getPath(), Skin.class);
		this.screenRequest = screenRequest;
		this.largeFont = fontFactory.getFont(72);
		clocktick = 0;
	}

	@Override
	public void render(float delta){
		super.render(delta);
		OrthographicCamera camera = (OrthographicCamera) getCamera();
        batch.setTransformMatrix(camera.view);
		batch.setProjectionMatrix(camera.combined);
		camera.update();
		batch.begin();
		
		if (!assetManager.update() || clocktick++ < 25){
			progress.setValue(assetManager.getProgress());
			largeFont.draw(batch, "Loading: " + (int)(assetManager.getProgress() * 100) + "%", 1125, 750);
		}	
		else {
			showScreen(screenRequest);
		}
		batch.end();
	}

	@Override
	public void buildStage() {
		progress = new ProgressBar(0, 1, .05f, false, skin);
		progress.setWidth(350);
		progress.addAction(Actions.moveTo(480, 400));
		this.addActor(progress);
	}
}
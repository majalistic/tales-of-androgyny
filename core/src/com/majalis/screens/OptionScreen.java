package com.majalis.screens;

import com.badlogic.gdx.graphics.OrthographicCamera;
/*
 * The options/configuration screen.  UI that handles player input to save Preferences to a player's file system.
 */
public class OptionScreen extends AbstractScreen {

	private int clocktick;
	
	public OptionScreen(ScreenFactory factory, ScreenElements elements) {
		super(factory, elements);
		clocktick = 0;
	}

	@Override
	public void render(float delta) {
		super.render(delta);
		OrthographicCamera camera = (OrthographicCamera) getCamera();
        batch.setTransformMatrix(camera.view);
		batch.setProjectionMatrix(camera.combined);
		camera.update();
		batch.begin();
		font.draw(batch, "Options", 1125, 750);
		font.draw(batch, String.valueOf(clocktick++), 1675, 500);
		batch.end();
		if (clocktick >= 50)
			showScreen(ScreenEnum.MAIN_MENU);
	}

	@Override
	public void buildStage() {
		// TODO Auto-generated method stub	
	}
	
	@Override
	public void show() {
		super.show();
		font.getData().setScale(4, 4);
	}	
}
package com.majalis.traprpg;

import com.badlogic.gdx.graphics.OrthographicCamera;
/*
 * The replay encounters.  UI that handles player input to select and load and encounters to experience again.
 */
public class ReplayScreen extends AbstractScreen {

	private int clocktick;
	
	public ReplayScreen(ScreenFactory factory, ScreenElements elements) {
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
		font.draw(batch, "Replay", 1125, 750);
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
package com.majalis.traprpg;

import com.badlogic.gdx.graphics.OrthographicCamera;
/*
 * Screen for displaying "Game Over" - can return the player to the main menu or offer them the ability to save their GO encounter.  May be loaded with different splashes / music at runtime.
 */
public class GameOverScreen extends AbstractScreen {

	private int clocktick;
	
	public GameOverScreen(ScreenFactory factory) {
		super(factory);
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
		font.draw(batch, "GAME OVER", 1125, 750);
		font.draw(batch, String.valueOf(clocktick++), 1675, 500);
		batch.end();
		if (clocktick >= 200)
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
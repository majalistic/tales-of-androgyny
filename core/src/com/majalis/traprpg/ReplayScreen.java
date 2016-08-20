package com.majalis.traprpg;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class ReplayScreen extends AbstractScreen {

	private int clocktick;
	
	public ReplayScreen(Game game, AbstractScreen parent, Object... params) {
		super(game, parent);
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
		if (clocktick >= 200)
			exit();
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
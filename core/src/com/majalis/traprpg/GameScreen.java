package com.majalis.traprpg;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;

public class GameScreen extends AbstractScreen {

	private boolean paused;
	private GameWorld world;
	
	public GameScreen(boolean loadGame) {
		world = new GameWorld(loadGame);
		paused = false;
	}
	
	@Override
	public void buildStage() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void render(float delta) {
		super.render(delta);
		world.gameLoop();
		if (paused!=world.paused){
			
		}
		paused = world.paused;
		if (world.gameExit){
			//game.saveManager.saveDataValue();
			ScreenManager.getInstance().showScreen(ScreenEnum.MAIN_MENU);
		}
		else if (world.gameOver){
			ScreenManager.getInstance().showScreen(ScreenEnum.GAME_OVER);
		}
		draw();
	}
	
	public void draw(){
		batch.begin();
		font.setUseIntegerPositions(false);
		if (world.displayHUD){
			font.draw(batch, "FPS: " + MathUtils.ceil(1/Gdx.graphics.getDeltaTime()), getCamera().position.x-200+(400), getCamera().position.y+220);
		}
		batch.end();
	}
	
	@Override
	public void show() {
		super.show();
	}

	@Override
	public void dispose() {
		super.dispose();
	}
}
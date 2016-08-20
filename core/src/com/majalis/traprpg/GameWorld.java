package com.majalis.traprpg;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;

public class GameWorld {
		
	public boolean displayHUD;
	public boolean paused;
	public boolean gameOver;
	public boolean gameExit;
	
	public GameWorld(boolean loadGame) {
		// initialize flags
		displayHUD = false;
		paused = false;
		gameOver = false;
		gameExit = false;
	}
	
	public void gameLoop(){
		if (Gdx.input.isKeyJustPressed(Keys.SPACE)){
			paused = !paused;
		}
		if (Gdx.input.isKeyJustPressed(Keys.TAB)){
			displayHUD = !displayHUD;
		}
		if (Gdx.input.isKeyJustPressed(Keys.SHIFT_LEFT)){
			gameOver = true;
		}
		if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)){
			gameExit = true;
		}
		if (!paused){
			gameIncrement();
		}
	}
	
	public void gameIncrement(){
		
	}
	
}

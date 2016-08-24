package com.majalis.traprpg;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;

/*
 * Contains state information about the world; may also contain state information about the player.
 */
public class GameWorld {
	public boolean displayHUD;
	public boolean encounterOver;
	public boolean gameOver;
	public boolean gameExit;
	
	
	public GameWorld(){
		displayHUD = true;
		encounterOver = false;
		gameOver = false;
		gameExit = false;
	}
	
	public void gameLoop(){
		if (Gdx.input.isKeyJustPressed(Keys.TAB)){
			displayHUD = !displayHUD;
		}
		if (Gdx.input.isKeyJustPressed(Keys.SHIFT_LEFT)){
			gameOver = true;
		}
		if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)){
			gameExit = true;
		}
	}
}

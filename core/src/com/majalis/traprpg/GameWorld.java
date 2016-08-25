package com.majalis.traprpg;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.utils.Array;

/*
 * Contains state information about the world; may also contain state information about the player.
 */
public class GameWorld {

	private Array<WorldNode> nodes;
	public boolean displayHUD;
	public boolean encounterOver;
	public boolean gameOver;
	public boolean gameExit;
	
	
	public GameWorld(Array<WorldNode> nodes){
		this.nodes = nodes;
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

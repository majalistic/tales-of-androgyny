package com.majalis.traprpg;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.utils.Array;

public class Encounter {
	private final Array<Scene> scenes;
	private Scene currentScene;
	public boolean gameExit;
	public boolean gameOver;
	public boolean displayHUD;
	
	public Encounter(Array<Scene> scenes){
		this.scenes = scenes;
		displayHUD = false;
		gameExit = false;
		gameOver = false;
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

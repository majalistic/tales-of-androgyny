package com.majalis.traprpg;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;

public class Encounter {
	private final Array<Scene> scenes;
	private final Array<EndScene> endScenes;
	public boolean displayHUD;
	public boolean encounterOver;
	public boolean gameOver;
	public boolean gameExit;
	
	
	public Encounter(Array<Scene> scenes, Array<EndScene> endScenes){
		this.scenes = scenes;
		this.endScenes = endScenes;
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
		for (EndScene objScene: endScenes){
			if (objScene.isActive()){
				switch(objScene.getType()){
					case ENCOUNTER_OVER: encounterOver = true; break;
					case GAME_OVER: gameOver = true; break;
					case GAME_EXIT: gameExit = true; break;
				} 
			}
		}
	}
	
	public Array<Actor> getActors(){
		Array<Actor> actors = new Array<Actor>();
		Scene lastActor = null;
		for (Actor actor: scenes){
			actors.add(actor);
			lastActor = (Scene) actor;
		}
		lastActor.setActive();
		return actors;
	}
}

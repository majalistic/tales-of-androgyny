package com.majalis.traprpg;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;

public class Encounter {
	private final Array<Scene> scenes;
	private final Array<EndScene> endScenes;
	private final Scene startScene;
	public boolean displayHUD;
	public boolean encounterOver;
	public boolean battle;
	public boolean gameOver;
	public boolean gameExit;
	
	
	public Encounter(Array<Scene> scenes, Array<EndScene> endScenes, Scene startScene){
		this.scenes = scenes;
		this.endScenes = endScenes;
		this.startScene = startScene;
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
		if (Gdx.input.isKeyJustPressed(Keys.ENTER)){
			for (Scene scene: scenes){
				if (scene.isActive()){
					scene.poke();
				}				
			}
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
		for (Actor actor: scenes){
			actors.add(actor);
		}
		startScene.setActive();
		return actors;
	}
}

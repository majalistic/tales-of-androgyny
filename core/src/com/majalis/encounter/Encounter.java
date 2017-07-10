package com.majalis.encounter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.majalis.scenes.BattleScene;
import com.majalis.scenes.EndScene;
import com.majalis.scenes.Scene;
/*
 * Represents the list of scenes, starts off the initial scene and listens for the end of an encounter
 */
public class Encounter {
	private final Array<Scene> scenes;
	private final Array<EndScene> endScenes;
	private final Array<BattleScene> battleScenes;
	private final Scene startScene;
	public boolean displayHUD;
	public boolean encounterOver;
	public boolean battle;
	public boolean gameOver;
	public boolean gameExit;
	
	public Encounter(Array<Scene> scenes, Array<EndScene> endScenes, Array<BattleScene> battleScenes, Scene startScene) {
		this.scenes = scenes;
		this.endScenes = endScenes;
		this.startScene = startScene;
		this.battleScenes = battleScenes;
		displayHUD = true;
		encounterOver = false;
		gameOver = false;
		gameExit = false;
	}
	
	public void gameLoop() {
		if (Gdx.input.isKeyJustPressed(Keys.TAB)) {
			displayHUD = !displayHUD;
		}
		if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
			gameExit = true;
		}
		
		for (BattleScene objScene : battleScenes) {
			if (objScene.isActive()) {
				battle = true;
			}
		}
		for (EndScene objScene: endScenes) {
			if (objScene.isActive()) {
				switch(objScene.getType()) {
					case ENCOUNTER_OVER: encounterOver = true; break;
					case GAME_OVER: gameOver = true; break;
				} 
			}
		}
	}
	
	public boolean isSwitching() {
		return battle || encounterOver || gameOver;
	}
	
	public Array<Actor> getActors() {
		Array<Actor> actors = new Array<Actor>();
		for (Actor actor: scenes) {
			actors.add(actor);
		}
		startScene.setActive();
		return actors;
	}
}

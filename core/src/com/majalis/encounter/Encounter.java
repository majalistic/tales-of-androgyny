package com.majalis.encounter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.majalis.scenes.Scene;
/*
 * Represents the list of scenes, starts off the initial scene and listens for the end of an encounter
 */
public class Encounter {
	private final Array<Scene> scenes;
	private final Scene startScene;
	public boolean encounterOver;
	public boolean battle;
	public boolean gameOver;
	public boolean gameExit;
	public boolean showSave;
	
	public Encounter(Array<Scene> scenes, Scene startScene) {
		this.scenes = scenes;
		this.startScene = startScene;
		encounterOver = false;
		gameOver = false;
		gameExit = false;
	}
	
	public void gameLoop() {
		if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
			gameExit = true;
		}
		// should probably be some "active" event that sets this rather than polling
		for (Scene objScene : scenes) {
			if (objScene.isActive()) {
				if (Gdx.input.isKeyJustPressed(Keys.TAB)) objScene.toggleBackground();
				showSave = objScene.showSave();
				battle = objScene.isBattle();
				encounterOver = objScene.encounterOver();
				gameOver = objScene.gameOver();
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

	public void poke() {
		for (Scene objScene : scenes) {
			if (objScene.isActive()) {
				objScene.poke();
			}
		}
	}
}

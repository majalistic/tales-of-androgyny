package com.majalis.encounter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ObjectSet;
import com.majalis.scenes.Scene;
/*
 * Represents the list of scenes, starts off the initial scene and listens for the end of an encounter
 */
public class Encounter {
	private final ObjectSet<Scene> scenes;
	private final Scene startScene;
	private final EncounterHUD hud;
	public boolean encounterOver;
	public boolean battle;
	public boolean gameOver;
	public boolean gameExit;
	
	public Encounter(ObjectSet<Scene> scenes, Scene startScene, EncounterHUD hud) {
		this.scenes = scenes;
		this.startScene = startScene;
		this.hud = hud;
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
				battle = objScene.isBattle();
				encounterOver = objScene.encounterOver();
				gameOver = objScene.gameOver();
			}
		}
	}
	
	public boolean isSwitching() {
		return battle || encounterOver || gameOver;
	}
	
	public Group getSceneGroup() {
		Group actors = new Group();
		for (Actor actor: scenes) {
			actors.addActor(actor);
		}
		actors.addActor(hud);
		startScene.setActive();
		return actors;
	}
	public void addSaveListener(ClickListener clickListener) { hud.addSaveListener(clickListener); }
}

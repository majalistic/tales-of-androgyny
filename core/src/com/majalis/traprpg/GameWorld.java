package com.majalis.traprpg;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;

/*
 * Contains state information about the world; may also contain state information about the player.
 */
public class GameWorld {

	private Array<WorldNode> nodes;
	public boolean displayHUD;
	public boolean gameOver;
	public boolean gameExit;
	public boolean encounterSelected;
	
	public GameWorld(Array<WorldNode> nodes){
		this.nodes = nodes;
		displayHUD = true;
		gameOver = false;
		gameExit = false;
		encounterSelected = false;
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
		
		for (WorldNode node: nodes){
			if (node.isSelected()){
				encounterSelected = true;
			}
		}
	}
	
	public Array<Actor> getActors(){
		Array<Actor> actors = new Array<Actor>();
		for (Actor actor: nodes){
			actors.add(actor);
		}
		return actors;
	}
}

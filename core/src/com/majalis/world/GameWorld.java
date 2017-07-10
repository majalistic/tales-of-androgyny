package com.majalis.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
/*
 * Contains state information about the world.  Is mostly obsolete - will need to be absorbed into WorldMapScreen and then broken out again to separate concerns.
 */
public class GameWorld {

	private Array<GameWorldNode> nodes;
	public boolean displayHUD;
	public boolean gameOver;
	public boolean gameExit;
	
	public GameWorld(Array<GameWorldNode> nodes) {
		this.nodes = nodes;
		displayHUD = true;
		gameOver = false;
		gameExit = false;
	}
	
	public void gameLoop(PolygonSpriteBatch batch, Vector3 position) {
		if (Gdx.input.isKeyJustPressed(Keys.TAB)) {
			displayHUD = !displayHUD;
		}
		if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
			gameExit = true;
		}
		batch.begin();
		for (GameWorldNode node: nodes) {
			// this should be refactored so that hover text is something the node returns that allows it to be rendered by the world map screen
			node.drawHover(batch, new Vector2(position.x + 1000, position.y + 30));			
		}
		batch.end();
	}
	
	public Array<Actor> getActors() {
		Array<Actor> actors = new Array<Actor>();
		for (GameWorldNode node : nodes) {
			for (Actor actor : node.getPaths()) {
				actors.add(actor);
			}
		}
		for (Actor actor : nodes) {
			actors.add(actor);
		}
		return actors;
	}
}

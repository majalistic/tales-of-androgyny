package com.majalis.traprpg;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Array;
/*
 * Represents a node on the world map.
 */
public class WorldNode extends Group {

	private final Array<WorldNode> connectedNodes;
	private final Encounter encounter;
	private final Encounter defaultEncounter;
	// for determining where to draw this node at
	private final Vector2 position;
	private boolean visited;
	private boolean active;
	private final int code;
	
	public WorldNode(Array<WorldNode> connectedNodes, Encounter encounter, Encounter defaultEncounter, Vector2 position, int code){
		this.connectedNodes = connectedNodes;
		this.encounter = encounter;
		this.defaultEncounter = defaultEncounter;
		this.position = position;
		this.code = code;
		visited = false;
		active = false;
		this.addAction(Actions.visible(true));
		this.addAction(Actions.show());
	}
	
	public boolean isAdjacent(WorldNode otherNode){
		return position.dst2(otherNode.getPosition()) < 70000;
	}
	
	public Vector2 getPosition(){
		return position;
	}
	
	public void connectTo(WorldNode otherNode){
		for (WorldNode connectedNode : connectedNodes){
			// if this node is already connected to this other node, skip
			if (otherNode == connectedNode){
				return;
			}
		}
		
		connectedNodes.add(otherNode);
		otherNode.getConnected(this);
	}
	
	public void getConnected(WorldNode otherNode){
		connectedNodes.add(otherNode);
	}
	
	public Encounter visit(){
		active = true;
		if (!visited){
			visited = true;
			return encounter;
		}
		else {
			return defaultEncounter;
		}
	}
	
	public Encounter moveTo(WorldNode targetNode){
		for (WorldNode node: connectedNodes){
			if (targetNode == node){
				active = false;
				return targetNode.visit();
			}
		}
		// if you can't visit the target node, there should probably be a boolean method to check for this
		return null;
	}

	public Array<WorldNode> getConnectedNodes() {
		return connectedNodes;
	}
	
	public int getCode(){
		return code;
	}
	
	@Override
    public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		
		BitmapFont font = new BitmapFont();
		font.setColor(0.5f,1,1,1);
		font.draw(batch, String.valueOf(code), (int)position.x, (int)position.y);		
		for (WorldNode otherNode: connectedNodes){
			Vector2 midPoint = new Vector2( (position.x+otherNode.getPosition().x)/2, (position.y+otherNode.getPosition().y)/2);
			font.draw(batch, "X", (int)midPoint.x, (int)midPoint.y);	
		}
		
		Pixmap shapeDrawing = new Pixmap(800, 800, Pixmap.Format.RGBA8888);
		shapeDrawing.setColor(Color.WHITE);
		shapeDrawing.fillCircle((int)position.x, (int)position.y, 800);
    }
}

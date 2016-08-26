package com.majalis.traprpg;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
/*
 * Represents a node on the world map.
 */
public class GameWorldNode extends Group {

	private final Array<GameWorldNode> connectedNodes;
	private final SaveService saveService;
	private final LoadService loadService;
	private final int nodeCode;
	private final int encounter;
	private final int defaultEncounter;
	// for determining where to draw this node at
	private final Vector2 position;
	private final boolean visited;
	private boolean selected;
	private boolean current;
	private boolean active;
	
	// all the nodes need are the encounter CODES, not the actual encounter
	public GameWorldNode(Array<GameWorldNode> connectedNodes, SaveService saveService, LoadService loadService, final int nodeCode, int encounter, int defaultEncounter, Vector2 position, boolean visited){
		this.connectedNodes = connectedNodes;
		this.saveService = saveService;
		this.loadService = loadService;
		this.encounter = encounter;
		this.defaultEncounter = defaultEncounter;
		this.position = position;
		this.nodeCode = nodeCode;
		this.visited = visited;
		selected = false;
		current = false;
		active = false;
		
		this.addAction(Actions.visible(true));
		this.addAction(Actions.show());
		this.setBounds(position.x-100, position.y-100, 200, 200);
	}
	
	
	public boolean isAdjacent(GameWorldNode otherNode){
		return position.dst2(otherNode.getPosition()) < 70000;
	}
	
	public Vector2 getPosition(){
		return position;
	}
	
	public void connectTo(GameWorldNode otherNode){
		for (GameWorldNode connectedNode : connectedNodes){
			// if this node is already connected to this other node, skip
			if (otherNode == connectedNode){
				return;
			}
		}
		
		connectedNodes.add(otherNode);
		otherNode.getConnected(this);
	}
	
	public void getConnected(GameWorldNode otherNode){
		connectedNodes.add(otherNode);
	}
	
	public void setAsCurrentNode(){
		current = true;
		for (GameWorldNode connectedNode : connectedNodes){
			connectedNode.setActive();
		}
	}
	
	private void setActive(){
		active = true;
		this.addListener(new ClickListener(){ 
			@Override
	        public void clicked(InputEvent event, float x, float y) {
				visit();
			}
		});
	}
	
	public void visit(){
		selected = true;
		if (!visited){
			saveService.saveDataValue("EncounterCode", encounter);
			Integer[] visitedCodes = loadService.loadDataValue("VisitedList", Object.class);
			Array<Integer> loadCodes = new Array<Integer>(true, visitedCodes, 0, visitedCodes.length);
			loadCodes.add(nodeCode);
			visitedCodes = loadCodes.toArray();
			saveService.saveDataValue("VisitedList", visitedCodes);
		}
		else {
			saveService.saveDataValue("EncounterCode", defaultEncounter);
		}
		saveService.saveDataValue("NodeCode", nodeCode);
	}

	public Array<GameWorldNode> getConnectedNodes() {
		return connectedNodes;
	}
	
	@Override
    public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		
		BitmapFont font = new BitmapFont();
		font.setColor(0.5f,1,1,1);
		font.draw(batch, (current ? "C" : "") + (active ? "A" : "" ) + (visited ? "V" : "") + String.valueOf(nodeCode), (int)position.x, (int)position.y);		
		for (GameWorldNode otherNode: connectedNodes){
			Vector2 midPoint = new Vector2( (position.x+otherNode.getPosition().x)/2, (position.y+otherNode.getPosition().y)/2);
			font.draw(batch, "X", (int)midPoint.x, (int)midPoint.y);	
		}
    }

	public boolean isSelected() {
		return selected;
	}
}

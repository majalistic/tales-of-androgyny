package com.majalis.world;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.majalis.save.SaveEnum;
import com.majalis.save.SaveManager;
import com.majalis.save.SaveService;
/*
 * Represents a node on the world map.
 */
public class GameWorldNode extends Group implements Comparable<GameWorldNode> {

	private final static int RADIUS = 25;
	private final Array<GameWorldNode> connectedNodes;
	private final SaveService saveService;
	private final OrthographicCamera camera;
	private final ShapeRenderer shapeRenderer;
	// temporary
	private final BitmapFont font;
	private final int nodeCode;
	private final int encounter;
	private final int defaultEncounter;
	// for determining where to draw this node at
	private final Vector2 position;
	private final boolean visited;
	private final Sound sound;
	private boolean selected;
	private boolean current;
	private boolean active;
	private boolean hover;
	
	// all the nodes need are the encounter CODES, not the actual encounter - should probably pass in some kind of object that contains the encounter generation logic, rather than an encounter and defaultEncounter code - at least, need a description of the encounter attached
	public GameWorldNode(Array<GameWorldNode> connectedNodes, SaveService saveService, OrthographicCamera camera, ShapeRenderer shapeRenderer, BitmapFont font, final int nodeCode, int encounter, int defaultEncounter, Vector2 position, boolean visited, Sound sound){
		this.connectedNodes = connectedNodes;
		this.saveService = saveService;
		this.camera = camera;
		this.shapeRenderer = shapeRenderer;
		this.font = font;
		this.encounter = encounter;
		this.defaultEncounter = defaultEncounter;
		this.position = position;
		this.nodeCode = nodeCode;
		this.visited = visited;
		this.sound = sound;
		selected = false;
		current = false;
		active = false;
		hover = false;
		
		this.addAction(Actions.visible(true));
		this.addAction(Actions.show());
		this.setBounds(position.x-RADIUS, position.y-RADIUS, RADIUS*2, RADIUS*2);
	}
	
	
	public boolean isAdjacent(GameWorldNode otherNode){
		return position.dst2(otherNode.getPosition()) < 67700;
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
				sound.play();
				visit();
			}
			@Override
	        public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
				hover = true;
			}
			@Override
	        public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
				hover = false;
			}
		});
	}
	
	public void visit(){
		selected = true;
		if (!visited){
			saveService.saveDataValue(SaveEnum.ENCOUNTER_CODE, encounter);
			saveService.saveDataValue(SaveEnum.VISITED_LIST, nodeCode);
		}
		else {
			saveService.saveDataValue(SaveEnum.ENCOUNTER_CODE, defaultEncounter);
		}
		saveService.saveDataValue(SaveEnum.CONTEXT, SaveManager.GameContext.ENCOUNTER);
		saveService.saveDataValue(SaveEnum.NODE_CODE, nodeCode);
		saveService.saveDataValue(SaveEnum.CAMERA_POS, position);
	}

	public Array<GameWorldNode> getConnectedNodes() {
		return connectedNodes;
	}
	
	@Override
    public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		
		batch.end();
		
		shapeRenderer.setProjectionMatrix(camera.combined);
		shapeRenderer.begin(ShapeType.Line);
		shapeRenderer.setColor(.258f, .652f, .4f, .5f);
		
		for (GameWorldNode otherNode: connectedNodes){
			// take my position vector and add a vector of length radius and inclination towards the center of the other node's vector
			Vector2 connection = new Vector2(otherNode.getPosition());
			connection.sub(position);
			connection.setLength(RADIUS);
			Vector2 onCircumference = new Vector2(position);
			onCircumference.add(connection);
			Vector2 onOtherCircumference = new Vector2(otherNode.getPosition());
			connection.rotate(180);
			onOtherCircumference.add(connection);			
			shapeRenderer.line(onCircumference.x, onCircumference.y,onOtherCircumference.x, onOtherCircumference.y);
		}
		shapeRenderer.end();
		
		// if isActive
		if (current){
			shapeRenderer.begin(ShapeType.Filled);
			shapeRenderer.setColor(.258f, .652f, .4f, .5f);
			shapeRenderer.circle(position.x, position.y, RADIUS);
			shapeRenderer.end();
		}
		else if (active){
			shapeRenderer.begin(ShapeType.Filled);
			shapeRenderer.setColor(.5f, .75f, .25f, 1);
			shapeRenderer.circle(position.x, position.y, RADIUS/2);
			shapeRenderer.end();
		}
		shapeRenderer.begin(ShapeType.Line);
		if (visited){	
			shapeRenderer.setColor(.2f, .2f, .2f, 1);
			shapeRenderer.circle(position.x, position.y, RADIUS);
			
		}
		else {
			shapeRenderer.setColor(.258f, .652f, .4f, 1);
			shapeRenderer.circle(position.x, position.y, RADIUS);
		}
		shapeRenderer.end();
		
		if (hover){
			// render hover box
			shapeRenderer.begin(ShapeType.Filled);
			shapeRenderer.setColor(.8f, .8f, .8f, 1);
			shapeRenderer.rect(position.x+35, (position.y-RADIUS)-25, 80, 100);
			shapeRenderer.setColor(.2f, .2f, .2f, 1);
			shapeRenderer.rect(position.x+40, (position.y-RADIUS)-20, 70, 90);
			shapeRenderer.end();
		}
			
		batch.begin();
		if (hover){
			// render hover text
			font.setColor(0.5f,1,1,1);
			font.getData().setScale(.9f);
			font.draw(batch, getHoverText(), (int)position.x + 50, (int)position.y + RADIUS + 15, 53, Align.center, true);	
		}		
    }

	private String getHoverText(){
		return visited ? "Nothing here" : getHoverText(encounter % 5); 
	}
	
	private String getHoverText(int encounter){
		switch (encounter){
			case 0: return "Wereslut";
			case 1: return "Harpy";
			case 2: return "Slime";
			case 3: return "Brigand";
			case 4: return "Dryad";
			default: return "Unknown";
		}
	}
	
	public boolean isSelected() {
		return selected;
	}

	public boolean isOverlapping(GameWorldNode otherNode) {
		return isOverlapping(otherNode.getPosition());
	}
	
	public boolean isOverlapping(Vector2 otherNode) {
		return Intersector.overlaps(new Circle(position, RADIUS+80), new Circle(otherNode, RADIUS));
	}
	
	@Override
	public int compareTo(GameWorldNode otherNode) {
		if (otherNode.getX() >= position.x){
			return 1;
		}
		else {
			return -1;
		}
	}
}

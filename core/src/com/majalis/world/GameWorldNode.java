package com.majalis.world;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
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
import com.badlogic.gdx.utils.ObjectSet;
import com.majalis.asset.AssetEnum;
import com.majalis.character.PlayerCharacter;
import com.majalis.save.SaveEnum;
import com.majalis.save.SaveManager;
import com.majalis.save.SaveService;
/*
 * Represents a node on the world map.
 */
public class GameWorldNode extends Group implements Comparable<GameWorldNode> {
	private final ObjectSet<GameWorldNode> connectedNodes;
	private final Array<Path> paths;
	private final SaveService saveService;
	// temporary
	private final BitmapFont font;
	private final int nodeCode;
	private final GameWorldNodeEncounter encounter;
	// for determining where to draw this node at
	private final Vector2 position;
	private final boolean visited;
	private final Sound sound;
	private final PlayerCharacter character;
	private boolean selected;
	private boolean current;
	private boolean active;
	private int visibility;
	private boolean hover;
	private Texture currentImage;
	private Texture activeImage;
	private Texture roadImage;
	private Texture hoverImage;
	private Texture arrowImage;
	private int arrowHeight;
	private int arrowShift;
	
	// all the nodes need are the encounter CODES, not the actual encounter - should probably pass in some kind of object that contains the encounter generation logic, rather than an encounter and defaultEncounter code - at least, need a description of the encounter attached
	public GameWorldNode(SaveService saveService, BitmapFont font, final int nodeCode, GameWorldNodeEncounter encounter, Vector2 position, boolean visited, Sound sound, PlayerCharacter character, AssetManager assetManager){
		this.connectedNodes = new ObjectSet<GameWorldNode>();
		paths = new Array<Path>();
		this.saveService = saveService;
		this.font = font;
		this.encounter = encounter;
		this.position = position;
		this.nodeCode = nodeCode;
		this.visited = visited;
		currentImage = assetManager.get(AssetEnum.CHARACTER_SPRITE.getPath(), Texture.class);
		int encounterCode = encounter.getCode();
		activeImage = 
			encounterCode == 1000 ? assetManager.get(AssetEnum.CASTLE.getPath(), Texture.class)
			: (encounterCode % 5 == 4 || encounterCode % 5 == 1 ? assetManager.get(AssetEnum.MOUNTAIN_ACTIVE.getPath(), Texture.class) : assetManager.get(AssetEnum.FOREST_ACTIVE.getPath(), Texture.class));
		hoverImage = assetManager.get(AssetEnum.WORLD_MAP_HOVER.getPath(), Texture.class);
		roadImage = assetManager.get(AssetEnum.ROAD.getPath(), Texture.class);
		arrowImage = assetManager.get(AssetEnum.ARROW.getPath(), Texture.class);
		this.sound = sound;
		this.character = character;
		selected = false;
		current = false;
		hover = false;
		active = false;
		
		this.addAction(Actions.visible(true));
		this.addAction(Actions.show());
		this.setBounds(position.x, position.y, activeImage.getWidth(), activeImage.getHeight());
		arrowHeight = 0;
		arrowShift = 1;
		visibility = -1;
	}
	
	
	public boolean isAdjacent(GameWorldNode otherNode){
		return position.dst2(otherNode.getPosition()) < 67700;
	}
	
	public Vector2 getPosition(){
		return position;
	}
	
	public void connectTo(GameWorldNode otherNode){
		if (connectedNodes.contains(otherNode)){
			return;
		}
		connectedNodes.add(otherNode);
		Vector2 centering = new Vector2(activeImage.getWidth()/2-10, activeImage.getHeight()/2);
		paths.add(new Path(roadImage, new Vector2(position).add(centering), new Vector2(otherNode.getPosition()).add(centering)));
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
		ObjectSet<GameWorldNode> visibleSet = new ObjectSet<GameWorldNode>();
		visibleSet.add(this);
		setNeighborsVisibility(getPerceptionLevel(character.getScoutingScore()), 1, visibleSet);
	}
	
	private void setActive(){
		active = true;
		this.addListener(new ClickListener(){ 
			@Override
	        public void clicked(InputEvent event, float x, float y) {
				sound.play(.5f);
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
	
	private void setVisibility(int visibility){
		this.visibility = visibility;
		if (!active && !current){
			this.addListener(new ClickListener(){ 
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
	}
	
	private void setNeighborsVisibility(int visibility, int diminishingFactor, ObjectSet<GameWorldNode> visibleSet){
		ObjectSet<GameWorldNode> nodesToSetVisible = new ObjectSet<GameWorldNode>(connectedNodes);
		while (visibility >= 0){
			for (GameWorldNode connectedNode : nodesToSetVisible){
				connectedNode.setVisibility(visibility);
				visibleSet.add(connectedNode);
			}
			ObjectSet<GameWorldNode> nextBatch = new ObjectSet<GameWorldNode>();
			for (GameWorldNode connectedNode : nodesToSetVisible){
				ObjectSet<GameWorldNode> newNeighbors = connectedNode.getNeighbors(visibleSet);
				nextBatch.addAll(newNeighbors);
			}
			nodesToSetVisible = nextBatch;
			visibility -= diminishingFactor;	
		}
		
	}
	
	private ObjectSet<GameWorldNode> getNeighbors(ObjectSet<GameWorldNode> visibleSet){
		ObjectSet<GameWorldNode> neighbors = new ObjectSet<GameWorldNode>();
		for (GameWorldNode node : connectedNodes){
			if (!visibleSet.contains(node)){
				neighbors.add(node);
			}
		}
		return neighbors;
		
	}
	
	
	public void visit(){
		selected = true;
		if (!visited){
			saveService.saveDataValue(SaveEnum.ENCOUNTER_CODE, encounter.getCode());
			saveService.saveDataValue(SaveEnum.VISITED_LIST, nodeCode);
		}
		else {
			saveService.saveDataValue(SaveEnum.ENCOUNTER_CODE, encounter.getDefaultCode());
		}
		saveService.saveDataValue(SaveEnum.FOOD, -4);
		saveService.saveDataValue(SaveEnum.CONTEXT, SaveManager.GameContext.ENCOUNTER);
		saveService.saveDataValue(SaveEnum.NODE_CODE, nodeCode);
		saveService.saveDataValue(SaveEnum.CAMERA_POS, position);
	}
	
	@Override
    public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
	
		batch.draw(activeImage, position.x, position.y);
		if (current){
			batch.draw(currentImage, position.x+20, position.y + 25);
		}
		if(active){
			batch.draw(arrowImage, position.x+25, position.y+45+arrowHeight/5);
			arrowHeight += arrowShift;
			if (arrowHeight > 100 || arrowHeight < 0) arrowShift = 0 - arrowShift;
		}
    }
	
	public void drawHover(Batch batch, Vector2 hoverPosition){
		if (hover){
			// render hover box
			batch.draw(hoverImage, hoverPosition.x, hoverPosition.y);
			// render hover text
			font.setColor(0f,0,0,1);
			font.draw(batch, getHoverText(), hoverPosition.x+50, hoverPosition.y+170, 150, Align.center, true);	
		}
	}

	private String getHoverText(){
		return encounter.getDescription(visibility, visited);
	}
	
	private int getPerceptionLevel(int perception) {
		if (perception >= 8){
			return 3;
		}
		if (perception >= 5){
			return 2;
		}
		else if (perception >= 2){
			return 1;
		}
		else {
			return 0;
		}
	}

	public Array<Path> getPaths(){
		return paths;
	}
	
	public boolean isSelected() {
		return selected;
	}

	public boolean isOverlapping(GameWorldNode otherNode) {
		return isOverlapping(otherNode.getPosition());
	}
	
	public boolean isOverlapping(Vector2 otherNode) {
		return Intersector.overlaps(new Circle(position, 105), new Circle(otherNode, 25));
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

package com.majalis.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectSet;
import com.majalis.asset.AssetEnum;
import com.majalis.character.PlayerCharacter;
import com.majalis.encounter.EncounterCode;
import com.majalis.save.SaveManager.GameContext;
/*
 * Represents a node on the world map.
 */
public class GameWorldNode extends Group implements Comparable<GameWorldNode> {
	private final ObjectSet<GameWorldNode> connectedNodes;
	private final Array<Path> paths;
	private final int nodeCode;
	private final GameWorldNodeEncounter encounter;
	// for determining where to draw this node at
	private final Vector2 position;
	private boolean visited;
	private final Sound sound;
	private final PlayerCharacter character;
	private boolean current;
	private boolean active;
	private int visibility;
	private boolean hover;
	private Texture activeImage;
	private Texture roadImage;
	// these should be replaced with an image that has a recurring action to move up and down indefinitely
	private Texture arrowImage;
	private int arrowHeight;
	private int arrowShift;
	
	// all the nodes need are the encounter CODES, not the actual encounter - should probably pass in some kind of object that contains the encounter generation logic, rather than an encounter and defaultEncounter code - at least, need a description of the encounter attached
	public GameWorldNode(final int nodeCode, GameWorldNodeEncounter encounter, Vector2 position, boolean visited, Sound sound, PlayerCharacter character, AssetManager assetManager) {
		this.connectedNodes = new ObjectSet<GameWorldNode>();
		paths = new Array<Path>();
		this.encounter = encounter;
		this.position = position;
		this.nodeCode = nodeCode;
		this.visited = visited;

		// this should be refactored - shouldn't need asset manager
		activeImage = assetManager.get(encounter.getCode().getTexture().getTexture());
		roadImage = assetManager.get(AssetEnum.ROAD.getTexture());
		arrowImage = assetManager.get(AssetEnum.ARROW.getTexture());
		this.sound = sound;
		this.character = character;
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
	
	public int getNodeCode() { return nodeCode; }
	public String getHoverText() { return current ? "" : encounter.getDescription(visibility, visited); }
	public Array<Path> getPaths() { return paths; }
	public EncounterCode getEncounterCode() { return visited ? encounter.getDefaultCode() : encounter.getCode(); }
	public GameContext getEncounterContext() { return visited ? encounter.getDefaultContext() : encounter.getContext(); }

	
	@Override
    public void draw(Batch batch, float parentAlpha) {
		if (hover && active) {
			Color cache = batch.getColor();
			batch.setColor(Color.GREEN);
			batch.draw(activeImage, position.x, position.y);
			batch.setColor(cache);
		}
		else {
			batch.draw(activeImage, position.x, position.y);
		}
		
		if(active) {
			Color cache = batch.getColor();
			batch.setColor(Color.WHITE);
			batch.draw(arrowImage, position.x+25, position.y+45+arrowHeight/5);
			batch.setColor(cache);
			arrowHeight += arrowShift;
			if (arrowHeight > 100 || arrowHeight < 0) arrowShift = 0 - arrowShift;
		}
		
		super.draw(batch, parentAlpha);
    }
	
	@Override
	public void setColor(Color color) {
		super.setColor(color);
		for (Actor actor : getChildren()) {
			actor.setColor(color);
		}
	}
	
	@Override
	public int compareTo(GameWorldNode otherNode) { return otherNode.getX() >= position.x ? 1 : -1; }	
	
	/*
	 * Code used for building out connections
	 */
	protected Vector2 getPosition() { return position; }
	protected boolean isOverlapping(GameWorldNode otherNode) { return isOverlapping(otherNode.getPosition()); }
	protected boolean isOverlapping(Vector2 otherNode) { return Intersector.overlaps(new Circle(position, 105), new Circle(otherNode, 25)); }
	protected boolean isAdjacent(GameWorldNode otherNode) { return position.dst2(otherNode.getPosition()) < 67700; }
	protected void connectTo(GameWorldNode otherNode) {
		if (connectedNodes.contains(otherNode)) {
			return;
		}
		connectedNodes.add(otherNode);
		Vector2 centering = new Vector2(activeImage.getWidth()/2-10, activeImage.getHeight()/2);
		paths.add(new Path(roadImage, new Vector2(position).add(centering), new Vector2(otherNode.getPosition()).add(centering)));
		otherNode.getConnected(this);
	}
	
	private void getConnected(GameWorldNode otherNode) { connectedNodes.add(otherNode); }
	
	/*
	 * All stateful code follows - setting active/current/visibility/etc.
	 */
	public boolean isCurrent() { return current; }
	public void setAsCurrentNode() {
		current = true;
		for (GameWorldNode connectedNode : connectedNodes) {
			connectedNode.setActive();
		}
		ObjectSet<GameWorldNode> visibleSet = new ObjectSet<GameWorldNode>();
		visibleSet.add(this);
		setNeighborsVisibility(character.getScoutingScore(), 1, visibleSet);
		visited = true;
	}	
	
	private void setActive() {
		active = true;
		this.addListener(new ClickListener() { 
			@Override
	        public void clicked(InputEvent event, float x, float y) {
				if (active) {
					fire(new ChangeListener.ChangeEvent());
					deactivate();
					sound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
				}
			}
			@Override public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) { hover = true; }
			@Override public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) { hover = false; }
		});
	}
	
	public void deactivate() {
		active = false;
		for (GameWorldNode connectedNode : connectedNodes) {
			connectedNode.setClickedAndAdjacentClicked();						
		}
	}
	
	// this will currently only deactivate nodes that are 2 away - when it becomes possible to click on a node 5 nodes away, this will be insufficie
	private void setClickedAndAdjacentClicked() {
		active = false;
		visibility = -1;
		current = false;
		for (GameWorldNode connectedNode : connectedNodes) {
			connectedNode.active = false;
			connectedNode.current = false;
			connectedNode.visibility = -1;
		}
	}

	private void setVisibility(int visibility) {
		this.visibility = visibility;
		if (!active && !current) {
			this.addListener(new ClickListener() { 
				@Override public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) { hover = true; }
				@Override public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) { hover = false; }
			});
		}
	}
	
	private void setNeighborsVisibility(int visibility, int diminishingFactor, ObjectSet<GameWorldNode> visibleSet) {
		ObjectSet<GameWorldNode> nodesToSetVisible = new ObjectSet<GameWorldNode>(connectedNodes);
		while (visibility >= 0) {
			for (GameWorldNode connectedNode : nodesToSetVisible) {
				connectedNode.setVisibility(visibility);
				visibleSet.add(connectedNode);
			}
			ObjectSet<GameWorldNode> nextBatch = new ObjectSet<GameWorldNode>();
			for (GameWorldNode connectedNode : nodesToSetVisible) {
				ObjectSet<GameWorldNode> newNeighbors = connectedNode.getNeighbors(visibleSet);
				nextBatch.addAll(newNeighbors);
			}
			nodesToSetVisible = nextBatch;
			visibility -= diminishingFactor;	
		}
		
	}
	
	private ObjectSet<GameWorldNode> getNeighbors(ObjectSet<GameWorldNode> visibleSet) {
		ObjectSet<GameWorldNode> neighbors = new ObjectSet<GameWorldNode>();
		for (GameWorldNode node : connectedNodes) {
			if (!visibleSet.contains(node)) {
				neighbors.add(node);
			}
		}
		return neighbors;
	}
}

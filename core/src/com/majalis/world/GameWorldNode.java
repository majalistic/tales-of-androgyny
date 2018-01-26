package com.majalis.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.Scaling;
import com.majalis.asset.AnimatedImage;
import com.majalis.asset.AssetEnum;
import com.majalis.character.PlayerCharacter;
import com.majalis.encounter.EncounterCode;
import com.majalis.save.SaveManager.GameContext;
/*
 * Represents a node on the world map.
 */
public class GameWorldNode extends Group implements Comparable<GameWorldNode> {
	private final ObjectSet<GameWorldNode> connectedNodes;
	private final ObjectMap<GameWorldNode, Path> pathMap;
	private final Array<Path> paths;
	private final int nodeCode;
	private final GameWorldNodeEncounter encounter;

	private final int x;
	private final int y;
	private final Sound sound;
	private final PlayerCharacter character;
	private boolean visited;
	private boolean current;
	private boolean active;
	private int visibility;
	private boolean hover;
	private Texture activeImage;
	private AnimatedImage activeAnimation;
	private Texture roadImage;
	// these should be replaced with an image that has a recurring action to move up and down indefinitely
	private Texture arrowImage;
	private int arrowHeight;
	private int arrowShift;
	
	public GameWorldNode(final int nodeCode, GameWorldNodeEncounter encounter, int x, int y, boolean visited, Sound sound, PlayerCharacter character, AssetManager assetManager) {
		this.connectedNodes = new ObjectSet<GameWorldNode>();
		paths = new Array<Path>();
		pathMap = new ObjectMap<GameWorldNode, Path>();
		this.encounter = encounter;
		this.x = x;
		this.y = y;
		
		this.nodeCode = nodeCode;
		this.visited = visited;

		// this should be refactored - shouldn't need asset manager
		activeImage = assetManager.get(encounter.getCode().getTexture().getTexture());
		if (encounter.getCode().getTexture() == AssetEnum.FOREST_ACTIVE || encounter.getCode().getTexture() == AssetEnum.ENCHANTED_FOREST || encounter.getCode().getTexture() == AssetEnum.FOREST_INACTIVE) {
			Array<TextureRegion> frames = new Array<TextureRegion>();
			int size = 64;
			for (int ii = 0; ii < 3; ii++) {
				frames.add(new TextureRegion(activeImage, ii * size, 0, size, size));
			}
			
			Animation animation = new Animation(.14f, frames);
			animation.setPlayMode(PlayMode.LOOP_PINGPONG);
			activeAnimation = new AnimatedImage(animation, Scaling.fit, Align.right);
			activeAnimation.setState(0);
			this.addActor(activeAnimation);
		}
		
		roadImage = assetManager.get(AssetEnum.ROAD.getTexture());
		arrowImage = assetManager.get(AssetEnum.ARROW.getTexture());
		this.sound = sound;
		this.character = character;
		current = false;
		hover = false;
		active = false;
		
		this.addAction(Actions.visible(true));
		this.addAction(Actions.show());
		Vector2 position = calculatePosition(x, y);
		this.setBounds(position.x, position.y, activeImage.getWidth(), activeImage.getHeight());
		arrowHeight = 0;
		arrowShift = 1;
		visibility = -1;
	}
	
	public Vector2 getHexPosition() { return new Vector2(x, y); }	
	public int getNodeCode() { return nodeCode; }
	public String getHoverText() { return current ? "" : encounter.getDescription(visibility, visited); }
	public Array<Path> getPaths() { return paths; }
	public EncounterCode getEncounterCode() { return visited ? encounter.getDefaultCode() : encounter.getCode(); }
	public GameContext getEncounterContext() { return visited ? encounter.getDefaultContext() : encounter.getContext(); }
	public boolean isConnected() { return connectedNodes.size > 0; }
	private void setPathHighlight() { 
		for (ObjectMap.Entry<GameWorldNode, Path> entry : pathMap.entries()) {
			if (entry.key.isCurrent()) {
				Path path = entry.value;
				Group g = path.getParent();
				g.removeActor(path);
				g.addActor(path);
				path.setColor(Color.GREEN);
			}
		}
	}
	
	private void setPathUnhighlight() { 
		for (ObjectMap.Entry<GameWorldNode, Path> entry : pathMap.entries()) {
			if (entry.key.isCurrent()) {
				Path path = entry.value;
				path.setColor(Color.WHITE);
			}
		}
	}
	
	@Override
    public void draw(Batch batch, float parentAlpha) {
		if (hover && active) {
			batch.setColor(Color.GREEN);
			setPathHighlight();
			if (activeAnimation == null) {
				batch.draw(activeImage, getX(), getY());
			}
			else {
				activeAnimation.setColor(Color.GREEN);
			}
		}
		else {	
			batch.setColor(getColor());
			setPathUnhighlight();
			if (activeAnimation == null) {
				batch.draw(activeImage, getX(), getY());
			}
			else {
				activeAnimation.setColor(current ? Color.PINK : Color.WHITE);
			}
		}
		
		if(active) {
			batch.setColor(Color.WHITE);
			batch.draw(arrowImage, getX() + 32 - arrowImage.getWidth() / 2, getY() + 50 + arrowHeight / 5);
			arrowHeight += arrowShift;
			if (arrowHeight > 100 || arrowHeight < 0) arrowShift = 0 - arrowShift;
		}		
		super.draw(batch, parentAlpha);
    }
	
	private Vector2 calculatePosition(int x, int y) {
		return GameWorldHelper.calculatePosition(x, y);
	}
	
	@Override
	public void setColor(Color color) {
		super.setColor(color);
		for (Actor actor : getChildren()) {
			actor.setColor(color);
		}
	}
	
	@Override
	public int compareTo(GameWorldNode otherNode) { return otherNode.getX() >= getX() ? 1 : -1; }	
	
	/*
	 * Code used for building out connections
	 */
	protected boolean isOverlapping(GameWorldNode otherNode) { return isOverlapping(otherNode.getHexPosition()); }
	public boolean isOverlapping(Vector2 otherNode) { 
		return getDistance(otherNode) <= 3;
	}
	
	protected boolean isAdjacent(GameWorldNode otherNode) {
		return isAdjacent(otherNode.getHexPosition()); 
	}
	
	public boolean isAdjacent(Vector2 possible) {
		return getDistance(possible) <= 6;
	}
	
	protected int getDistance(Vector2 otherNodePosition) { return GameWorldHelper.distance(x, y, (int)otherNodePosition.x, (int)otherNodePosition.y); }
	
	protected int getDistance(GameWorldNode otherNode) {
		return getDistance(otherNode.getHexPosition()); 		
	}
	
	protected void connectTo(GameWorldNode otherNode) {
		if (connectedNodes.contains(otherNode)) {
			return;
		}
		connectedNodes.add(otherNode);
		Path newPath = new Path(roadImage, getHexPosition(), otherNode.getHexPosition());
		pathMap.put(otherNode, newPath);
		paths.add(newPath);
		otherNode.getConnected(this, newPath);
	}
	
	private void getConnected(GameWorldNode otherNode, Path path) { connectedNodes.add(otherNode); pathMap.put(otherNode, path); }
	
	/*
	 * All stateful code follows - setting active/current/visibility/etc.
	 */
	public boolean isCurrent() { return current; }
	public void setAsCurrentNode() {
		current = true;
		if (activeAnimation != null) {
			activeAnimation.setColor(Color.PINK);
		}
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
		});
	}
	
	public void deactivate() {
		active = false;
		for (GameWorldNode connectedNode : connectedNodes) {
			connectedNode.setClickedAndAdjacentClicked();						
		}
	}
	
	// this will currently only deactivate nodes that are 2 away - when it becomes possible to click on a node 5 nodes away, this will be insufficient
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
		if (!current) {
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

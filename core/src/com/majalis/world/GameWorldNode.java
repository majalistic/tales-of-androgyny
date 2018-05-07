package com.majalis.world;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.majalis.asset.AnimationBuilder;
import com.majalis.asset.AssetEnum;
import com.majalis.character.PlayerCharacter;
import com.majalis.encounter.EncounterCode;
import com.majalis.save.SaveManager.GameContext;
import com.majalis.save.SaveManager.VisitInfo;
import com.majalis.save.SaveEnum;
import com.majalis.save.SaveService;
/*
 * Represents a node on the world map.
 */
public class GameWorldNode extends Group implements Comparable<GameWorldNode> {
	private final ObjectSet<GameWorldNode> connectedNodes;
	private final ObjectMap<GameWorldNode, Path> pathMap;
	private final Array<Path> paths;
	private final int nodeCode;
	private final GameWorldNodeEncounter encounter;
	private final int x, y;
	private final PlayerCharacter character;
	private final VisitInfo visitInfo;
	private final ClickListener fireListener;
	private final Image arrow;
	private boolean current;
	private boolean active;	// indicates adjacency to current node for arrow selector
	private Array<GameWorldNode> pathToCurrent;
	
	public GameWorldNode(final int nodeCode, GameWorldNodeEncounter encounter, int x, int y, VisitInfo visitInfo, PlayerCharacter character, AssetManager assetManager) {
		this.connectedNodes = new ObjectSet<GameWorldNode>();
		paths = new Array<Path>();
		pathMap = new ObjectMap<GameWorldNode, Path>();
		this.encounter = encounter;
		this.x = x;
		this.y = y;
		this.nodeCode = nodeCode;
		this.visitInfo = visitInfo;
		this.character = character;
		current = false;
		visitInfo.nodeCode = nodeCode;

		// this should be refactored - shouldn't need asset manager
		Texture activeImageTexture = assetManager.get(encounter.getCode().getTexture().getTexture());
		this.addActor(encounter.getCode().hasGenericTile() ? new AnimationBuilder(activeImageTexture, 3, 64, 64, .14f).setPlayMode(PlayMode.LOOP_PINGPONG).getActor() : new Image(activeImageTexture));
		
		arrow = initArrow(new Image(assetManager.get(AssetEnum.ARROW.getTexture())));
		
		this.addAction(Actions.show());
		Vector2 position = calculatePosition(x, y);
		this.setBounds(position.x, position.y, activeImageTexture.getWidth(), activeImageTexture.getHeight());
		fireListener = new ClickListener() { 
			@Override
	        public void clicked(InputEvent event, float x, float y) {
				if (!current) {
					fire(new ChangeListener.ChangeEvent());
					deactivate();
				}
			}
		};
		addListener(fireListener);
		addListener(new ClickListener() { 
			@Override public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) { setColor(Color.GREEN); setPathHighlight(); }
			@Override public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) { setColor(Color.WHITE);  setPathUnhighlight(); }
		});
		setColor(Color.WHITE);
	}

	private Image initArrow(Image arrow) {
		this.addActor(arrow);
		arrow.addAction(Actions.hide());
		arrow.setPosition(32 - arrow.getWidth() / 2, getY() + 50);
		arrow.addAction(Actions.forever(Actions.sequence(Actions.moveBy(0, 8, 2), Actions.moveBy(0, -8, 2))));
		return arrow;
	}
	
	public Vector2 getHexPosition() { return new Vector2(x, y); }	
	public int getNodeCode() { return nodeCode; }
	public Array<Path> getPaths() { return paths; }
	public String getHoverText() { return current || isHidden() ? "" : visitInfo.numberOfEncounters == 0 || (newEncounterReady() && encounter.hasRespawns()) ? getEncounterCode().getDescription(visitInfo.visibility) : getEncounterCode().getFullDescription(); }
	public EncounterCode getEncounterCode() { return visitInfo.numberOfEncounters == 0 ? encounter.getCode() : newEncounterReady() ? getRandomEncounter() : encounter.getDefaultCode(); }
	private EncounterCode getRandomEncounter() { return encounter.getRandomEncounterCode(visitInfo.randomVal % 5 + (visitInfo.randomVal * visitInfo.numberOfEncounters) % 13); }
	public GameContext getEncounterContext() { return getEncounterCode().getContext(); }
	public boolean isConnected() { return connectedNodes.size > 0; }
	
	// used for finding a minimum edge unweighted path from this node to a target node
	public Array<GameWorldNode> getPathTo(GameWorldNode node) {
		Array<GameWorldNode> foundPath = new Array<GameWorldNode>();
		ObjectSet<GameWorldNode> checkedNodes = new ObjectSet<GameWorldNode>(); 
		checkedNodes.add(this);
		ObjectSet<GameWorldNode> nodesToCheck = new ObjectSet<GameWorldNode>(connectedNodes);
		ObjectMap<GameWorldNode, GameWorldNode> nodeToNode = new ObjectMap<GameWorldNode, GameWorldNode>();
		while (nodesToCheck.size > 0) {
			for (GameWorldNode connectedNode : nodesToCheck) {
				if (connectedNode == node) {
					foundPath = convertMapToPath(nodeToNode, connectedNode);
					return foundPath;
				}
				checkedNodes.add(connectedNode);
			}
			ObjectSet<GameWorldNode> nextBatch = new ObjectSet<GameWorldNode>();
			for (GameWorldNode connectedNode : nodesToCheck) {
				ObjectSet<GameWorldNode> newNeighbors = connectedNode.getNeighbors(checkedNodes); // this needs to connect current nodes to new nodes, and also check to see if any of the newNeighbors are current
				for (GameWorldNode neighbor : newNeighbors) nodeToNode.put(neighbor, connectedNode);
				nextBatch.addAll(newNeighbors);
			}
			nodesToCheck = nextBatch;
		}
		return foundPath; // could not find
	}
	// used for finding a minimum edge path from this node to the current node with minimized absolute length
	public Array<GameWorldNode> getPathToCurrent() {
		if (pathToCurrent != null ) return pathToCurrent;
		ObjectSet<GameWorldNode> checkedNodes = new ObjectSet<GameWorldNode>(); 
		checkedNodes.add(this);
		ObjectSet<GameWorldNode> nodesToCheck = new ObjectSet<GameWorldNode>(connectedNodes);
		ObjectMap<GameWorldNode, GameWorldNode> nodeToNode = new ObjectMap<GameWorldNode, GameWorldNode>();
		ObjectMap<GameWorldNode, Integer> nodeToPathLength = new ObjectMap<GameWorldNode, Integer>();
		for (GameWorldNode connectedNode : connectedNodes) {
			nodeToPathLength.put(connectedNode, getDistance(connectedNode));
		}
		while (nodesToCheck.size > 0) {
			for (GameWorldNode connectedNode : nodesToCheck) {
				if (connectedNode.isCurrent()) {
					pathToCurrent = convertMapToPath(nodeToNode, connectedNode);
					return pathToCurrent;
				}
				checkedNodes.add(connectedNode);
			}
			ObjectSet<GameWorldNode> nextBatch = new ObjectSet<GameWorldNode>();
			for (GameWorldNode connectedNode : nodesToCheck) {
				ObjectSet<GameWorldNode> newNeighbors = connectedNode.getNeighbors(checkedNodes); // this needs to connect current nodes to new nodes, and also check to see if any of the newNeighbors are current
				for (GameWorldNode neighbor : newNeighbors) { // check if the node has already been used to construct a path, and see if this new path is shorter
					// determine the path length to the new node via the current one
					int pathLength = nodeToPathLength.get(connectedNode) + connectedNode.getDistance(neighbor);
					if (nodeToPathLength.get(neighbor, 1000) > pathLength) {
						nodeToPathLength.put(neighbor, pathLength);
						nodeToNode.put(neighbor, connectedNode);
					}
				}
				nextBatch.addAll(newNeighbors);
			}
			nodesToCheck = nextBatch;
		}
		
		return new Array<GameWorldNode>(); // could not find
	}
	
	private Array<GameWorldNode> convertMapToPath(ObjectMap<GameWorldNode, GameWorldNode> nodeToNode, GameWorldNode targetNode) {
		GameWorldNode next = targetNode;
		Array<GameWorldNode> path = new Array<GameWorldNode>();
		path.add(targetNode);
		while (nodeToNode.get(next) != null) {
			next = nodeToNode.get(next);
			path.add(next);
		}
		path.reverse();
		return path;
	}
	
	private void setPathHighlight() { 
		if (current) return;
		Array<GameWorldNode> pathToCurrent = getPathToCurrent();
		if (pathToCurrent.size > 0) {
			GameWorldNode algoNode = this;
			for (GameWorldNode node : pathToCurrent) {
				Path path1 = node.pathMap.get(algoNode);
				Path path2 = algoNode.pathMap.get(node);
				algoNode = node;
				path1.getParent().addActor(path1);
				path1.setColor(Color.GREEN);
				path2.getParent().addActor(path2);
				path2.setColor(Color.GREEN);
			}
		}
	}
	
	private void setPathUnhighlight() { 
		Array<GameWorldNode> pathToCurrent = getPathToCurrent();
		GameWorldNode algoNode = this;
		for (GameWorldNode node : pathToCurrent) {
			Path path = node.pathMap.get(algoNode);
			node.setPathAlpha(path, algoNode);
			algoNode = node;
		}
	}
	
	@Override
	public void setColor(Color color) {
		super.setColor(color);
		color = getAlpha(color);
		for(ObjectMap.Entry<GameWorldNode, Path> path : pathMap.entries()) {
			setPathAlpha(path.value, path.key);
		}
		for (Actor actor : getChildren()) {
			actor.setColor(color);
		}
		if (isHidden()) arrow.addAction(Actions.hide());
		else {
			arrow.setColor(Color.WHITE);
			if (active) arrow.addAction(Actions.show());
		}
	}
	
	@Override
	public int compareTo(GameWorldNode otherNode) { return otherNode.getX() >= getX() ? 1 : -1; }	
	
	/*
	 * Code used for building out connections
	 */
	protected boolean isOverlapping(GameWorldNode otherNode) { return isOverlapping(otherNode.getHexPosition()); }
	protected boolean isOverlapping(Vector2 otherNode) { return getDistance(otherNode) <= 3; }
	protected boolean isAdjacent(GameWorldNode otherNode) { return isAdjacent(otherNode.getHexPosition());  }
	protected boolean isAdjacent(Vector2 possible) { return getDistance(possible) <= 6; }
	protected int getDistance(Vector2 otherNodePosition) { return GameWorldHelper.distance(x, y, (int)otherNodePosition.x, (int)otherNodePosition.y); }
	protected int getDistance(GameWorldNode otherNode) { return getDistance(otherNode.getHexPosition()); }
	
	/*
	 * All stateful code follows - setting active/current/visibility/etc.
	 */
	public void setCurrent() { current = true; }
	public boolean isCurrent() { return current; }
	
	public void visit(SaveService saveService) { 
		if (visitInfo.numberOfEncounters == 0) {
			visitInfo.numberOfEncounters = 1;
			visitInfo.lastEncounterTime = character.getTime();
		}
		else {
			if (newEncounterReady()) {
				visitInfo.numberOfEncounters++; // change which encounter should be returned next time, need to make sure the encounter is retrieved first before calling visit on it, but that should be inherent
				visitInfo.lastEncounterTime = character.getTime();
			}
		}
		visitInfo.setVisibility(5);
		saveService.saveDataValue(SaveEnum.VISITED_LIST, visitInfo, false); // update the visited info in the gamestate for this node
		ObjectSet<GameWorldNode> visibleSet = new ObjectSet<GameWorldNode>();
		visibleSet.add(this);
		setNeighborsVisibility(saveService, character.getScoutingScore(), 1, visibleSet); // update the visibility info in each node 
		setColor(Color.WHITE);
	}
	
	protected void connectTo(GameWorldNode otherNode) {
		if (connectedNodes.contains(otherNode)) {
			return;
		}
		connectedNodes.add(otherNode);
		Path newPath = new Path(getHexPosition(), otherNode.getHexPosition());
		setPathAlpha(newPath, otherNode);
		pathMap.put(otherNode, newPath);
		paths.add(newPath);
		otherNode.getConnected(this, newPath);
	}

	public void setAsCurrentNode() {
		current = true;
		removeListener(fireListener);
		for (GameWorldNode connectedNode : connectedNodes) {
			connectedNode.setActive();
		}
	}	
	
	private void setPathAlpha(Path path, GameWorldNode otherNode) {	path.setColor(
		isHidden() ? getAlpha(Color.WHITE) :
		otherNode.isHidden() ? otherNode.getAlpha(Color.WHITE) :
		this.visitInfo.visibility <= otherNode.visitInfo.visibility || otherNode.isHidden() ? getAlpha(Color.WHITE) : otherNode.getAlpha(Color.WHITE)); 
	}
	private void getConnected(GameWorldNode otherNode, Path path) { connectedNodes.add(otherNode); pathMap.put(otherNode, path); }
	private Vector2 calculatePosition(int x, int y) { return GameWorldHelper.calculatePosition(x, y); }
	private Color getAlpha(Color color) { return new Color(color.r, color.g, color.b, isHidden() ? 0 : .4f + (visitInfo.visibility * 1.2f)); }
	private boolean isHidden() { return visitInfo.isHidden && visitInfo.visibility < 3; }
	private boolean newEncounterReady() { return visitInfo.lastEncounterTime + 18 + ((visitInfo.randomVal % 3) * 6) < character.getTime(); }
	
	private void setActive() { active = true; arrow.addAction(Actions.show()); }
	private void setInactive() {
		active = false;
		if (current) {
			current = false;
			addListener(fireListener);
		}
		
		for(ObjectMap.Entry<GameWorldNode, Path> path : pathMap.entries()) {
			setPathAlpha(path.value, path.key);
		}
		arrow.addAction(Actions.hide());
		pathToCurrent = null;
	}
	
	private void deactivate() {
		ObjectSet<GameWorldNode> checkedNodes = new ObjectSet<GameWorldNode>(); 
		checkedNodes.add(this);
		deactivateAll(checkedNodes);
	}
	
	private void deactivateAll(ObjectSet<GameWorldNode> checkedNodes) {
		setInactive();
		for (GameWorldNode node : connectedNodes) {
			if (!checkedNodes.contains(node)) {
				checkedNodes.add(node);
				node.deactivateAll(checkedNodes);
			}
		}
	}

	private void setVisibility(SaveService saveService, int visibility) {
		visitInfo.setVisibility(visibility); 
		saveService.saveDataValue(SaveEnum.VISITED_LIST, visitInfo, false); 
		if (isHidden()) {
			removeListener(fireListener);
		}
		else {
			addListener(fireListener);
		}
		setColor(Color.WHITE); 
	}
	
	private void setNeighborsVisibility(SaveService saveService, int visibility, int diminishingFactor, ObjectSet<GameWorldNode> visibleSet) {
		ObjectSet<GameWorldNode> nodesToSetVisible = new ObjectSet<GameWorldNode>(connectedNodes);
		while (visibility >= 0) {
			for (GameWorldNode connectedNode : nodesToSetVisible) {
				connectedNode.setVisibility(saveService, visibility);
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

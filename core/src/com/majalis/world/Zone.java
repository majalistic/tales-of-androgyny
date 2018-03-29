package com.majalis.world;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.majalis.character.PlayerCharacter;
import com.majalis.encounter.EncounterCode;
import com.majalis.save.LoadService;
import com.majalis.save.SaveEnum;
import com.majalis.save.SaveManager.VisitInfo;
import com.majalis.talesofandrogyny.Logging;
import com.majalis.talesofandrogyny.TalesOfAndrogyny;

public class Zone {

	private final AssetManager assetManager;
	private final RandomXS128 random;
	private final Array<GameWorldNode> nodes;
	private final Array<GameWorldNode> zoneNodes;
	private final Array<GameWorldNode> requiredNodes;
	private final IntMap<GameWorldNode> nodeMap;
	private final IntMap<VisitInfo> visitedInfo;
	private final PlayerCharacter character;
	private final int difficulty;
	private final int repeats;
	private final ObjectSet<EncounterCode> unspawnedEncounters;
	private GameWorldNode startNode;
	public int nodeCode;
	
	protected Zone(LoadService loadService, AssetManager assetManager, RandomXS128 random, Array<GameWorldNode> nodes, IntMap<GameWorldNode> nodeMap, ObjectSet<EncounterCode> unspawnedEncounters, int difficulty, int repeats) {
		this.assetManager = assetManager;
		this.random = random;
		this.difficulty = difficulty;
		this.repeats = repeats;
		visitedInfo = loadService.loadDataValue(SaveEnum.VISITED_LIST, IntMap.class);
		character = loadService.loadDataValue(SaveEnum.PLAYER, PlayerCharacter.class);

		this.nodes = nodes;
		this.nodeMap = nodeMap;
		this.unspawnedEncounters = unspawnedEncounters;
		requiredNodes = new Array<GameWorldNode>();
		zoneNodes = new Array<GameWorldNode>();
	}
	
	@SuppressWarnings("unchecked")
	protected Zone addStartNode(int nodeCode, EncounterCode initialEncounter, EncounterCode defaultEncounter, int x, int y) {
		startNode = getNode(nodeCode, initialEncounter, defaultEncounter, x, y, visitedInfo.get(nodeCode, getFreshVisitInfo()));
		addNode(startNode, nodeCode, nodes, zoneNodes);		
		return this;
	}
	protected Zone addStartNode(GameWorldNode node) { startNode = node; return this; }

	@SuppressWarnings("unchecked")
	protected Zone addEndNode(int nodeCode, EncounterCode initialEncounter, EncounterCode defaultEncounter, int x, int y) { addNode(getNode(nodeCode, initialEncounter, defaultEncounter, x, y, visitedInfo.get(nodeCode, getFreshVisitInfo())), nodeCode, nodes, requiredNodes, zoneNodes); return this; }

	protected Zone buildZone(int nodeCode) {
		Array<GameWorldNode> requiredNodesUnfulfilled = new Array<GameWorldNode>(requiredNodes);
		for (int ii = 0; ii < repeats; ii++) {
			for (GameWorldNode requiredNode : requiredNodes) {
				GameWorldNode closestNode = findClosestNode(requiredNode, zoneNodes);
				nodeCode = startToEndNodePath(requiredNodesUnfulfilled.contains(requiredNode, true) && closestNode != null ? closestNode : startNode, requiredNode, requiredNodesUnfulfilled, nodeCode);
			}
		}
		
		boolean failure = false;
		int attempts = 0;
		while(requiredNodesUnfulfilled.size > 0 && !failure && attempts < 10) {
			for (GameWorldNode unfulfilledNode : requiredNodesUnfulfilled) {
				GameWorldNode closest = findClosestNode(unfulfilledNode, zoneNodes);
				if (closest != null) {
					nodeCode = startToEndNodePath(closest, unfulfilledNode, requiredNodesUnfulfilled, nodeCode);
				}
				else { failure = true; }
			}
			for (GameWorldNode unfulfilledNode : requiredNodesUnfulfilled) {
				GameWorldNode closest = findClosestNode(unfulfilledNode, nodes);
				if (closest != null) {
					startToEndNodePath(closest, unfulfilledNode, requiredNodesUnfulfilled, nodeCode);
				}
				else { failure = true; }
			}
			for (GameWorldNode unfulfilledNode : requiredNodesUnfulfilled) { 
				GameWorldNode closestNode = null;
				for (GameWorldNode node : nodes) {
					if (!zoneNodes.contains(node, true) && node.getPathTo(unfulfilledNode).size != 0) {
						GameWorldNode closestInZone = findClosestNode(node, zoneNodes);
						if (closestNode == null || node.getDistance(closestInZone) < node.getDistance(closestNode)) {
							closestNode = closestInZone;
						}
					}
				}
				if (closestNode != null) {
					startToEndNodePath(closestNode, unfulfilledNode, requiredNodesUnfulfilled, nodeCode);
				}
			}
			attempts++;
		}
	
		// connect all nodes that consider themselves adjacent to nearby nodes - some nodes, like permanent nodes, might have a longer "reach" then others
		for (int ii = 0; ii < nodes.size - 1; ii++) {
			for (int jj = ii + 1; jj < nodes.size; jj++) {
				if (nodes.get(ii).isAdjacent(nodes.get(jj))) {
					nodes.get(ii).connectTo(nodes.get(jj));
				}
			}
		}
		
		if (TalesOfAndrogyny.testing) {
			String failures = "";
			for (GameWorldNode node : requiredNodesUnfulfilled) { failures += node.getEncounterCode().toString() + ", "; }
			if (!failures.equals("")) { Logging.logTime("Failed to generate following required nodes : " + failures); }
		}
		this.nodeCode = nodeCode;
		return this;
	}
	
	private GameWorldNode findClosestNode(GameWorldNode nodeToFindFriend, Array<GameWorldNode> nodesToSearch) {
		GameWorldNode closest = null;
		for (GameWorldNode node : nodesToSearch) {
			if (node == nodeToFindFriend || requiredNodes.contains(node, true)) continue;
			int distanceToNode = nodeToFindFriend.getDistance(node);
			if (closest == null || closest.getDistance(nodeToFindFriend) > distanceToNode) { closest = node; }
		}
		return closest;
	}
	
	@SuppressWarnings("unchecked")
	private int startToEndNodePath(GameWorldNode startNode, GameWorldNode requiredNode, Array<GameWorldNode> requiredNodesUnfulfilled, int nodeCode) {
		int minimumX = 10;
		int minimumXY = 200;
		int maxX = 130;
		int maximumXY = 380;
		boolean nodeNotReached = true;
		GameWorldNode currentNode = startNode;
		GameWorldNode closestNode = currentNode;
		for ( ; nodeNotReached; ) {
			Vector2 newNodePosition = null; // this should never happen
			Vector2 source = new Vector2(currentNode.getHexPosition());			
			int currentDistance = currentNode.getDistance(requiredNode);
			// create a set of possible coordinates
			Array<Vector2> possibleTowardsCoordinates = new Array<Vector2>();
			Array<Vector2> possibleAwayCoordinates = new Array<Vector2>();
			Vector2 possible = new Vector2(0, 0);
			for (int jj = Math.max((int)source.x - 11, minimumX); jj < Math.min((int)source.x + 11, maxX); jj++) {
				for (int kk = (int)source.y - 11; kk < source.y + 11; kk++) {
					int xy = jj + kk * 2;
					if (xy < minimumXY || xy >= maximumXY) continue;
					possible.x = jj;
					possible.y = kk;
					if (!currentNode.isOverlapping(possible) && !requiredNode.isOverlapping(possible) && currentNode.isAdjacent(possible)) {
						if (requiredNode.getDistance(possible) <= currentDistance) { possibleTowardsCoordinates.add(new Vector2(possible)); }
						else { possibleAwayCoordinates.add(new Vector2(possible)); }
					}
				}
			}
			
			boolean overlap = true;
			while (overlap && possibleTowardsCoordinates.size > 0) {
				// choose a random one
				newNodePosition = possibleTowardsCoordinates.get(Math.abs(random.nextInt() % possibleTowardsCoordinates.size));
				// check to see if it overlaps, remove it from the set of possible coordinates if it is invalid
				overlap = isOverlap(newNodePosition, nodes);
				if (overlap) {
					possibleTowardsCoordinates.removeValue(newNodePosition, true);
				}
			}
			if (possibleTowardsCoordinates.size == 0) {
				overlap = true;
				while (overlap && possibleAwayCoordinates.size > 0) {
					// choose a random one
					newNodePosition = possibleAwayCoordinates.get(Math.abs(random.nextInt() % possibleAwayCoordinates.size));
					// check to see if it overlaps, remove it from the set of possible coordinates if it is invalid
					overlap = isOverlap(newNodePosition, nodes);
					if (overlap) {
						possibleAwayCoordinates.removeValue(newNodePosition, true);
					}
				}
				if (possibleAwayCoordinates.size == 0) {
					if (currentNode == closestNode) {
						break;
					}
					currentNode = closestNode;
					continue;
				}
			}
			
			GameWorldNode newNode = getNode(
				nodeCode, 
				TalesOfAndrogyny.setEncounter.size == 0 ? EncounterCode.getEncounterCode(nodeCode - 1, difficulty, unspawnedEncounters) : TalesOfAndrogyny.setEncounter.get(nodeCode % TalesOfAndrogyny.setEncounter.size),
				EncounterCode.DEFAULT, TalesOfAndrogyny.randomEncounters ? EncounterCode.getDifficultySet(difficulty) : new Array<EncounterCode>(), (int)newNodePosition.x, (int)newNodePosition.y, visitedInfo.get(nodeCode, getFreshVisitInfo(true)));
			addNode(newNode, nodeCode, nodes, zoneNodes);
			
			// if we've reached the target node, we can terminate this run-through
			nodeNotReached = !requiredNode.isAdjacent(newNode);
			if (!nodeNotReached) {
				requiredNodesUnfulfilled.removeValue(requiredNode, true);
			}
			// save the node for the next iteration
			currentNode = newNode;	
			nodeCode++;
			if (closestNode.getDistance(requiredNode) > newNode.getDistance(requiredNode)) closestNode = newNode;
		}
		return nodeCode;
	}
	
	private boolean isOverlap(Vector2 newNodePosition, Array<GameWorldNode> nodes) {
		for (GameWorldNode node: nodes) {
			if (node.isOverlapping(newNodePosition)) {
				return true;
			}
		}
		return false;
	}
	
	protected Array<GameWorldNode> getEndNodes() { return requiredNodes; }
	
	private void addNode(GameWorldNode newNode, int nodeCode, @SuppressWarnings("unchecked") Array<GameWorldNode> ... nodes) {
		for (Array<GameWorldNode> nodeArray: nodes) {
			nodeArray.add(newNode);
		}
		nodeMap.put(nodeCode, newNode);
	}
	private VisitInfo getFreshVisitInfo() { return getFreshVisitInfo(false); }
	private VisitInfo getFreshVisitInfo(boolean canBeHidden) { return new VisitInfo(0, 0, (int) ((Math.random() * 1000) % 1000), 0, -1, canBeHidden && random.nextInt(10) == 0); }
	private GameWorldNode getNode(int nodeCode, EncounterCode initialEncounter, EncounterCode defaultEncounter, int x, int y, VisitInfo visitInfo) { return getNode(nodeCode, initialEncounter, defaultEncounter, new Array<EncounterCode>(), x, y, visitInfo); }
	private GameWorldNode getNode(int nodeCode, EncounterCode initialEncounter, EncounterCode defaultEncounter, Array<EncounterCode> raandomEncounters, int x, int y, VisitInfo visitInfo) {
		return new GameWorldNode(nodeCode, new GameWorldNodeEncounter(initialEncounter, defaultEncounter, raandomEncounters), x, y, visitInfo, character, assetManager);
	}
}

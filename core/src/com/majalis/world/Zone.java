package com.majalis.world;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.IntSet;
import com.majalis.character.PlayerCharacter;
import com.majalis.save.LoadService;
import com.majalis.save.SaveEnum;
import com.majalis.save.SaveService;

public class Zone {

	private final SaveService saveService;
	private final BitmapFont font;
	private final AssetManager assetManager;
	private final RandomXS128 random;
	private final Array<GameWorldNode> nodes;
	private final Array<GameWorldNode> requiredNodes;
	private final IntMap<GameWorldNode> nodeMap;
	private final IntSet visitedCodesSet;
	private final Sound sound;
	private final PlayerCharacter character;
	private GameWorldNode startNode;
	
	protected Zone(SaveService saveService, LoadService loadService, BitmapFont font, AssetManager assetManager, RandomXS128 random, Array<GameWorldNode> nodes, IntMap<GameWorldNode> nodeMap){
		this.saveService = saveService;
		this.font = font;
		this.assetManager = assetManager;
		this.random = random;
		visitedCodesSet = loadService.loadDataValue(SaveEnum.VISITED_LIST, IntSet.class);
		sound = assetManager.get("node_sound.wav", Sound.class);
		character = loadService.loadDataValue(SaveEnum.PLAYER, PlayerCharacter.class);

		this.nodes = nodes;
		this.nodeMap = nodeMap;
		requiredNodes = new Array<GameWorldNode>();
	}
	
	@SuppressWarnings("unchecked")
	protected Zone addStartNode(int nodeCode, int encounter, int defaultEncounter, Vector2 position){
		startNode = getNode(nodeCode, encounter, defaultEncounter, position, visitedCodesSet.contains(nodeCode));
		addNode(startNode, nodeCode, nodes);		
		return this;
	}
	
	protected Zone addStartNode(GameWorldNode node){
		startNode = node;
		return this;
	}
	
	@SuppressWarnings("unchecked")
	protected Zone addEndNode(int nodeCode, int encounter, int defaultEncounter, Vector2 position){
		requiredNodes.add(getNode(nodeCode, encounter, defaultEncounter, position, visitedCodesSet.contains(nodeCode)));
		addNode(requiredNodes.get(requiredNodes.size-1), nodeCode, nodes, requiredNodes);		
		return this;
	}
	
	@SuppressWarnings("unchecked")
	protected Zone buildZone(){
		for (int ii = 0; ii < 8; ii++){
			for (GameWorldNode requiredNode : requiredNodes){
				Boolean nodeNotReached = true;
				Vector2 currentNodePosition = startNode.getPosition();
				for (int nodeCode = nodes.size; nodeNotReached; nodeCode++){
					Vector2 newNodePosition;
					boolean overlap = true;
					int tries = 10;
					// try 10 times to plot a new point, then fail this run-through of the algorithm
					do {
						newNodePosition = new Vector2(currentNodePosition);
						Vector2 target = new Vector2(requiredNode.getPosition());
						// 260 is the squareroot of the actual distance modifier, 70000, which is the square of the distance
						// start with the last point, then add a vector to it with a randomly chosen angle of a fixed or minimally variant distance		
						newNodePosition = new Vector2(newNodePosition.add(target.sub(newNodePosition).setLength(random.nextInt()%60+200).rotate(random.nextInt()%90)));
						overlap = false;
						for (GameWorldNode node: nodes){
							if (node.isOverlapping(newNodePosition)){
								overlap = true;
							}
						}
					} while(overlap && tries-- > 1);
					if (tries == 0) {
						break;
					}
					// save the position for the next iteration
					currentNodePosition = newNodePosition;
					
					GameWorldNode newNode = getNode(nodeCode, (nodeCode-1)%5, -1, currentNodePosition, visitedCodesSet.contains(nodeCode) ? true : false);
					addNode(newNode, nodeCode, nodes);
					// if we've reached the target node, we can terminate this run-through
					nodeNotReached = !requiredNode.isAdjacent(newNode);
				}
			}
		}
		
		// connect all nodes that consider themselves adjacent to nearby nodes - some nodes, like permanent nodes, might have a longer "reach" then others
		for (int ii = 0; ii < nodes.size-1; ii++){
			for (int jj = ii + 1; jj < nodes.size; jj++){
				if (nodes.get(ii).isAdjacent(nodes.get(jj))){
					nodes.get(ii).connectTo(nodes.get(jj));
				}
			}
		}
		return this;
	}
	
	protected Array<GameWorldNode> getEndNodes(){
		return requiredNodes;
	}
	
	private void addNode(GameWorldNode newNode, int nodeCode, Array<GameWorldNode> ... nodes){
		for (Array<GameWorldNode> nodeArray: nodes){
			nodeArray.add(newNode);
		}
		nodeMap.put(nodeCode, newNode);
	}
	
	private GameWorldNode getNode(int nodeCode, int encounter, int defaultEncounter, Vector2 position, boolean visited){
		return new GameWorldNode(saveService, font, nodeCode, new GameWorldNodeEncounter(encounter, defaultEncounter), position, visited, sound, character, assetManager);
	}
	
}

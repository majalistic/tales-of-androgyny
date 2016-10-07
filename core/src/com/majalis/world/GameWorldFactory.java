package com.majalis.world;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.IntSet;
import com.majalis.character.PlayerCharacter;
import com.majalis.save.LoadService;
import com.majalis.save.SaveEnum;
import com.majalis.save.SaveManager;
import com.majalis.save.SaveService;
/*
 * Generates a world map or returns the world map.
 */
public class GameWorldFactory {

	private final SaveService saveService;
	private final LoadService loadService;
	private final AssetManager assetManager;
	private final BitmapFont font;
	private final RandomXS128 random;
	
	public GameWorldFactory(SaveManager saveManager, AssetManager assetManager, BitmapFont font, RandomXS128 random){
		this.saveService = saveManager;
		this.loadService = saveManager;
		this.assetManager = assetManager;
		this.font = font;
		this.random = random;
	}
	
	// should probably also receive any data that is being loaded - namely visited nodes and currently active node
	@SuppressWarnings("unchecked")
	public GameWorld getGameWorld(OrthographicCamera camera, int seed) {
		random.setSeed(seed);
		Array<GameWorldNode> nodes = new Array<GameWorldNode>();
		IntMap<GameWorldNode> nodeMap = new IntMap<GameWorldNode>();
		IntSet visitedCodesSet = loadService.loadDataValue(SaveEnum.VISITED_LIST, IntSet.class);
		Sound sound = assetManager.get("node_sound.wav", Sound.class);
		PlayerCharacter character = loadService.loadDataValue(SaveEnum.PLAYER, PlayerCharacter.class);
		
		// -1 = magic number to get the defaultEncounter
		addNode(getNode(1, 0, -1, new Vector2(500, 500), true, sound, character), nodeMap, 1, nodes);
		Array<GameWorldNode> requiredNodes = new Array<GameWorldNode>();
		// end node
		addNode(getNode(2, 1000, -1, new Vector2(1800, 1800), visitedCodesSet.contains(2) ? true : false, sound, character), nodeMap, 2, nodes, requiredNodes);
		// temporarily stop at 1000 to prevent hangs if endpoint isn't found - in the future this should set something that will smoothly guide towards the exit as the number of nodes increase
		for (int ii = 0; ii < 8; ii++){
			for (GameWorldNode requiredNode : requiredNodes){
				Boolean nodeNotReached = true;
				Vector2 currentNodePosition = new Vector2(500, 500);
				for (int nodeCode = nodes.size + 2; nodeNotReached; nodeCode++){
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
					
					GameWorldNode newNode = getNode(nodeCode, (nodeCode-1)%5, -1, currentNodePosition, visitedCodesSet.contains(nodeCode) ? true : false, sound, character);
					addNode(newNode, nodeMap, nodeCode, nodes);
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
		
		// load the current node - subtracts 1 because it currently assumes that the index in the array is one less than the node's code - need to make an IntMap between nodeCode and node
		nodeMap.get((Integer)loadService.loadDataValue(SaveEnum.NODE_CODE, Integer.class)).setAsCurrentNode();
		nodes.sort();
		return new GameWorld(nodes);
	}
	
	private void addNode(GameWorldNode newNode, IntMap<GameWorldNode> nodeMap, int nodeCode, Array<GameWorldNode> ... nodes){
		for (Array<GameWorldNode> nodeArray: nodes){
			nodeArray.add(newNode);
		}
		nodeMap.put(nodeCode, newNode);
	}
	
	private GameWorldNode getNode(int nodeCode, int encounter, int defaultEncounter, Vector2 position, boolean visited, Sound sound, PlayerCharacter character){
		return new GameWorldNode(saveService, font, nodeCode, new GameWorldNodeEncounter(encounter, defaultEncounter), position, visited, sound, character, assetManager);
	}
}

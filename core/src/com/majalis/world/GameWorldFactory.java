package com.majalis.world;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.ObjectSet;
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
	private final ShapeRenderer shapeRenderer;
	private final BitmapFont font;
	private final RandomXS128 random;
	private GameContext context;
	
	public GameWorldFactory(SaveManager saveManager, ShapeRenderer shapeRenderer, BitmapFont font, RandomXS128 random){
		this.saveService = saveManager;
		this.loadService = saveManager;
		this.shapeRenderer = shapeRenderer;
		this.font = font;
		this.random = random;
	}
	
	// this will need to be passed an integer seed which is randomly generated on worldgen, so that this method itself is deterministic
	// should probably also receive any data that is being loaded - namely visited nodes and currently active node
	@SuppressWarnings("unchecked")
	public GameWorld getGameWorld(OrthographicCamera camera, int seed) {
		random.setSeed(seed);
		Array<GameWorldNode> nodes = new Array<GameWorldNode>();
		IntMap<GameWorldNode> nodeMap = new IntMap<GameWorldNode>();
		ObjectSet<Integer> visitedCodesSet = loadService.loadDataValue(SaveEnum.VISITED_LIST, ObjectSet.class);
		// -1 = magic number to get the defaultEncounter
		addNode(new GameWorldNode(new Array<GameWorldNode>(), saveService, loadService, camera, shapeRenderer, font, 1, 0, -1, new Vector2(500, 500), visitedCodesSet.contains(1) ? true : false), nodeMap, 1, nodes);
		Array<GameWorldNode> requiredNodes = new Array<GameWorldNode>();
		// end node
		addNode(new GameWorldNode(new Array<GameWorldNode>(), saveService, loadService, camera, shapeRenderer, font, 2, 1, -1, new Vector2(1800, 1800), visitedCodesSet.contains(1) ? true : false), nodeMap, 2, nodes, requiredNodes);
		Vector2 currentNodePosition = new Vector2(500, 500);
		// temporarily stop at 1000 to prevent hangs if endpoint isn't found - in the future this should set something that will smoothly guide towards the exit as the number of nodes increase
		for (int nodeCode = 3; nodeCode <= 100 || (nodeCode <= 1000 && requiredNodes.size != 0); nodeCode++){
			// start with the last point, then add a vector to it with a randomly chosen angle of a fixed or minimally variant distance		
			Vector2 newNodePosition;
			boolean overlap = true;
			do {
				newNodePosition = new Vector2(currentNodePosition);
				if (requiredNodes.size != 0){
					Vector2 target = new Vector2(requiredNodes.get(0).getPosition());
					// 260 is the squareroot of the actual distance modifier, 70000, which is the square of the distance
					newNodePosition = new Vector2(newNodePosition.add(target.sub(newNodePosition).setLength(random.nextInt()%130+130)));
				}
				else {
					newNodePosition = new Vector2(newNodePosition.add(new Vector2(random.nextInt()%10000, random.nextInt()%10000).sub(newNodePosition).setLength(260)));
				}
				overlap = false;
				for (GameWorldNode node: nodes){
					if (node.isOverlapping(newNodePosition)){
						overlap = true;
					}
				}
			} while(overlap);
			
			currentNodePosition = newNodePosition;
			
			GameWorldNode newNode = (new GameWorldNode(new Array<GameWorldNode>(), saveService, loadService, camera, shapeRenderer, font, nodeCode, nodeCode-1, -1, currentNodePosition, visitedCodesSet.contains(nodeCode) ? true : false));
			addNode(newNode, nodeMap, nodeCode, nodes);
			// and continue until a list of points that must be connected are connected
			IntArray toRemoveFromRequired = new IntArray();
			int ii = 0;
			for (GameWorldNode requiredNode: requiredNodes){
				if (requiredNode.isAdjacent(newNode)){
					toRemoveFromRequired.add(ii);
				}
				ii++;
			}
			toRemoveFromRequired.reverse();
			for (int jj = 0; jj < toRemoveFromRequired.size; jj++){
				requiredNodes.removeIndex(toRemoveFromRequired.get(jj));
			}			
		}
		
		// remove all nodes such that none are overlapping
		IntArray toRemoveFromNodeList = new IntArray();
		for (int ii = 0; ii < nodes.size-1; ii++){
			for (int jj = ii + 1; jj < nodes.size; jj++){
				if (nodes.get(ii).isOverlapping(nodes.get(jj))){		
					if (!toRemoveFromNodeList.contains(jj)){
						toRemoveFromNodeList.add(jj);
					}
				}
			}
		}
		toRemoveFromNodeList.sort();
		toRemoveFromNodeList.reverse();
		for (int ii = 0; ii < toRemoveFromNodeList.size; ii++){
			nodes.removeIndex(toRemoveFromNodeList.get(ii));
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
		return new GameWorld(nodes);
	}
	
	public void addNode(GameWorldNode newNode, IntMap<GameWorldNode> nodeMap, int nodeCode, Array<GameWorldNode> ... nodes){
		for (Array<GameWorldNode> nodeArray: nodes){
			nodeArray.add(newNode);
		}
		nodeMap.put(nodeCode, newNode);
	}

	public void setContext(GameContext context) {
		this.context = context;
	}
	
	public GameContext getGameContext() {
		return context;
	}
	
	public enum ClassEnum {
		WARRIOR ("Warrior"),
		PALADIN ("Paladin"),
		THIEF ("Thief"),
		RANGER ("Ranger"),
		MAGE ("Mage"),
		ENCHANTRESS ("Enchanter");
		
		private final String label;

		ClassEnum(String label) {
		    this.label = label;
		 }
		public String getLabel(){return label;}
	}
	
	public enum GameContext {
		ENCOUNTER,
		WORLD_MAP
	}
}

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
	public GameWorld getGameWorld(OrthographicCamera camera, int seed) {
		random.setSeed(seed);
		Array<GameWorldNode> nodes = new Array<GameWorldNode>();
		IntMap<GameWorldNode> nodeMap = new IntMap<GameWorldNode>();
		ObjectSet<Integer> visitedCodesSet = loadService.loadDataValue(SaveEnum.VISITED_LIST, ObjectSet.class);
		
		
		for (int ii = 1; ii <= 100; ii++){
			// 100 = magic number to get the defaultEncounter for now
			GameWorldNode newNode = new GameWorldNode(new Array<GameWorldNode>(), saveService, loadService, camera, shapeRenderer, font, ii, ii-1, 100, new Vector2(random.nextInt()%1000, random.nextInt()%1000), visitedCodesSet.contains(ii) ? true : false);
			nodes.add(newNode);
			nodeMap.put(ii, newNode);
		}
		/*// DEFAULT WORLD GEN
		for (int ii = 1; ii <= 10; ii++){
			// 100 = magic number to get the defaultEncounter for now
			nodes.add(new GameWorldNode(new Array<GameWorldNode>(), saveService, loadService, camera, shapeRenderer, font, ii, ii-1, 100, new Vector2(ii * 85, 200 + (200 * Math.floorMod(ii, 3))-ii*10), visitedCodesSet.contains(ii) ? true : false));
		}
		*/
		// connect all nodes that consider themselves adjacent to nearby nodes - some nodes, like permanent nodes, might have a longer "reach" then others
		IntArray toRemove = new IntArray();
		for (int ii = 0; ii < nodes.size-1; ii++){
			for (int jj = ii + 1; jj < nodes.size; jj++){
				if (nodes.get(ii).isOverlapping(nodes.get(jj))){		
					if (!toRemove.contains(jj)){
						toRemove.add(ii);
						break;
					}
				}
			}
		}
		
		toRemove.reverse();
		for (int ii = 0; ii < toRemove.size; ii++){
			nodes.removeIndex(toRemove.get(ii));
		}
		
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

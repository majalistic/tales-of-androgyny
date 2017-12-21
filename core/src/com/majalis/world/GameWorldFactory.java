package com.majalis.world;

import static com.majalis.encounter.EncounterCode.*;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.IntSet;
import com.badlogic.gdx.utils.ObjectSet;
import com.majalis.asset.AssetEnum;
import com.majalis.character.PlayerCharacter;
import com.majalis.encounter.EncounterCode;
import com.majalis.save.LoadService;
import com.majalis.save.SaveEnum;
import com.majalis.save.SaveManager;
import com.majalis.save.SaveManager.GameMode;
import com.majalis.talesofandrogyny.Logging;
import com.majalis.talesofandrogyny.TalesOfAndrogyny;
/*
 * Generates a world map or returns the world map.
 */
public class GameWorldFactory {
	private final LoadService loadService;
	private final AssetManager assetManager;
	private final RandomXS128 random;
	private Sound sound;
	private PlayerCharacter character;
	private IntMap<GameWorldNode> nodeMap = new IntMap<GameWorldNode>();
	private Array<GameWorldNode> nodes = new Array<GameWorldNode>();
	
	public GameWorldFactory(SaveManager saveManager, AssetManager assetManager, RandomXS128 random) {
		this.loadService = saveManager;
		this.assetManager = assetManager;
		this.random = random;
	}
	
	@SuppressWarnings("unchecked")
	public Array<GameWorldNode> getGameWorld(int seed, GameMode gameMode, int currentNode) {
		random.setSeed(seed);
		nodeMap = new IntMap<GameWorldNode>();
		nodes = new Array<GameWorldNode>();
		// specifying the start and end points to a getZone method which will perform the below functions, creating an array of nodes.  Can make it a class so that it can be chained?  Piping in the end points as start points

		sound = assetManager.get(AssetEnum.CLICK_SOUND.getSound());
		character = loadService.loadDataValue(SaveEnum.PLAYER, PlayerCharacter.class);
		
		ObjectSet<EncounterCode> unspawnedEncounters = new ObjectSet<EncounterCode>(EncounterCode.getAllRandomEncounters());
		
		if (gameMode == GameMode.SKIRMISH) {
			new Zone(loadService, assetManager, random, nodes, nodeMap, unspawnedEncounters, 1,  1)
				.addStartNode(1, INITIAL, DEFAULT, 18, 89) 
				.addEndNode(10000, GADGETEER, GADGETEER,  18, 100)
				.buildZone();
			
			Zone zone = new Zone(loadService, assetManager, random, nodes, nodeMap, unspawnedEncounters, 1,  2)
					.addStartNode(nodes.get(0))
					.addEndNode(1000, TOWN, TOWN, 31, 94)
					.buildZone();
			
			Zone zone2 = new Zone(loadService, assetManager, random, nodes, nodeMap, unspawnedEncounters, 2,  3)
					.addStartNode(zone.getEndNodes().get(0))
					.addEndNode(1001, SPIDER, SPIDER, 53, 109)
					.buildZone();
			
			Zone zone3 = new Zone(loadService, assetManager, random, nodes, nodeMap, unspawnedEncounters, 2, 2)
					.addStartNode(zone2.getEndNodes().get(0))
					.addEndNode(1003, ANGEL, ANGEL, 83, 119)
					.addEndNode(1004, WITCH_COTTAGE, WITCH_COTTAGE, 83, 88)
					.buildZone();
			
			new Zone(loadService, assetManager, random, nodes, nodeMap, unspawnedEncounters, 3, 2)
					.addStartNode(zone3.getEndNodes().get(0))
					.addEndNode(1005, QUETZAL, QUETZAL, 119, 115)
					.addEndNode(1006, FORT, FORT, 119, 84)
					.buildZone();

		}
		else {
			int nodeCode = 1;
			IntSet visitedCodesSet = loadService.loadDataValue(SaveEnum.VISITED_LIST, IntSet.class);
			addNode(getNode(nodeCode, DEFAULT, DEFAULT, 12, 92, visitedCodesSet.contains(nodeCode++)), nodes);
			addNode(getNode(nodeCode, COTTAGE_TRAINER, COTTAGE_TRAINER_VISIT, 15, 91, visitedCodesSet.contains(nodeCode++)), nodes);
			addNode(getNode(nodeCode, TOWN_STORY, TOWN2, 19, 90, visitedCodesSet.contains(nodeCode++)), nodes);
			addNode(getNode(nodeCode, FIRST_BATTLE_STORY, DEFAULT, 23, 90, visitedCodesSet.contains(nodeCode++)), nodes);
			addNode(getNode(nodeCode, MERI_COTTAGE, MERI_COTTAGE_VISIT, 23, 86, visitedCodesSet.contains(nodeCode++)), nodes);
			addNode(getNode(nodeCode, ECCENTRIC_MERCHANT, DEFAULT, 28, 88, visitedCodesSet.contains(nodeCode++)), nodes);	
			addNode(getNode(nodeCode, STORY_FEM, DEFAULT, 28, 91, visitedCodesSet.contains(nodeCode++)), nodes);
			addNode(getNode(nodeCode, STORY_SIGN, DEFAULT, 32, 91, visitedCodesSet.contains(nodeCode++)), nodes);
			addNode(getNode(nodeCode, BRIGAND_STORY, DEFAULT, 31, 95, visitedCodesSet.contains(nodeCode++)), nodes);
			addNode(getNode(nodeCode, HARPY_STORY, DEFAULT, 37, 88, visitedCodesSet.contains(nodeCode++)), nodes);
			addNode(getNode(nodeCode, FOOD_CACHE, DEFAULT, 37, 93, visitedCodesSet.contains(nodeCode++)), nodes);
			
			addNode(getNode(nodeCode, FOOD_CACHE, DEFAULT, 20, 95, visitedCodesSet.contains(nodeCode++)), nodes);
			addNode(getNode(nodeCode, OGRE_WARNING_STORY, DEFAULT, 19, 99, visitedCodesSet.contains(nodeCode++)), nodes);	
			addNode(getNode(nodeCode, OGRE_STORY, DEFAULT, 19, 103, visitedCodesSet.contains(nodeCode++)), nodes);
			
			addNode(getNode(nodeCode, FOOD_CACHE, DEFAULT, 24, 102, visitedCodesSet.contains(nodeCode++)), nodes);
			addNode(getNode(nodeCode, FORT, FORT, 29, 102, visitedCodesSet.contains(nodeCode++)), nodes);
			
			/*new Zone(loadService, assetManager, random, nodes, nodeMap, 1, 1)
				.addStartNode(nodes.get(nodes.size-1))
				.addEndNode(1003, FORT, FORT, 9, 51)
				.buildZone();*/
			
			for (int ii = 0; ii < nodes.size-1; ii++) {
				for (int jj = ii + 1; jj < nodes.size; jj++) {
					if (nodes.get(ii).isAdjacent(nodes.get(jj))) {
						nodes.get(ii).connectTo(nodes.get(jj));
					}
				}
			}
		}
		
		nodeMap.get(currentNode).setAsCurrentNode();
		nodes.sort();
		
		if (TalesOfAndrogyny.testing) {
			ObjectSet<EncounterCode> encounterCodesUnfulfilled = new ObjectSet<EncounterCode>(EncounterCode.getAllRandomEncounters());
			for (GameWorldNode node : nodes) {
				encounterCodesUnfulfilled.remove(node.getEncounterCode());
			}
			String missingCodes = "";
			for (EncounterCode code : encounterCodesUnfulfilled) {
				missingCodes += code.toString() + ", ";
			}
			if (!missingCodes.equals("")) {
				Logging.logTime("Failed to generate following encounters : " + missingCodes);
			}
		}
		
		return nodes;
	}
	
	private void addNode(GameWorldNode newNode, Array<GameWorldNode> ... nodes) {
		for (Array<GameWorldNode> nodeArray: nodes) {
			nodeArray.add(newNode);
		}
		nodeMap.put(newNode.getNodeCode(), newNode);
	}
	
	private GameWorldNode getNode(int nodeCode, EncounterCode initialEncounter, EncounterCode defaultEncounter, int x, int y, boolean visited) {
		return new GameWorldNode(nodeCode, new GameWorldNodeEncounter(initialEncounter, defaultEncounter), x, y, visited, sound, character, assetManager);
	}
	// temporary until gameworld terrain gen is moved to this class
	public RandomXS128 getRandom() {
		return random;
	}
}

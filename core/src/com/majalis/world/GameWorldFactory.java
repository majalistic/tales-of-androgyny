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
		if (TalesOfAndrogyny.testing) testWorldGen();
	}
	
	/* Unit Test */
	private void testWorldGen() {
		Logging.logTime("Begin logging");
		for (int ii = 0; ii < 10000; ii++) {
			getGameWorld(ii, GameMode.SKIRMISH, 1);
			EncounterCode.resetState();
			Logging.logTime("Seed: " + ii);
		}
		Logging.flush();
	}
	/* End Unit Test */
	
	@SuppressWarnings("unchecked")
	public Array<GameWorldNode> getGameWorld(int seed, GameMode gameMode, int currentNode) {
		random.setSeed(seed);
		nodeMap = new IntMap<GameWorldNode>();
		nodes = new Array<GameWorldNode>();
		// specifying the start and end points to a getZone method which will perform the below functions, creating an array of nodes.  Can make it a class so that it can be chained?  Piping in the end points as start points

		sound = assetManager.get(AssetEnum.CLICK_SOUND.getSound());
		character = loadService.loadDataValue(SaveEnum.PLAYER, PlayerCharacter.class);
		
		if (gameMode == GameMode.SKIRMISH) {
			
			new Zone(loadService, assetManager, random, nodes, nodeMap, 1,  1)
				.addStartNode(1, INITIAL, DEFAULT, 3, 10)
				.addEndNode(10000, GADGETEER, DEFAULT, 3, 32)
				.buildZone();
			
			Zone zone = new Zone(loadService, assetManager, random, nodes, nodeMap, 1,  2)
					.addStartNode(nodes.get(0))
					.addEndNode(1000, TOWN, TOWN, 27, 22)
					//.addEndNode(5000, CRIER_QUEST, CRIER_QUEST, new Vector2(1300, 1300))
					.buildZone();
			
			Zone zone2 = new Zone(loadService, assetManager, random, nodes, nodeMap, 2,  3)
					.addStartNode(zone.getEndNodes().get(0))
					.addEndNode(1001, SPIDER, SPIDER, 66, 60)
					.buildZone();
			
			Zone zone3 = new Zone(loadService, assetManager, random, nodes, nodeMap, 2, 2)
					.addStartNode(zone2.getEndNodes().get(0))
					.addEndNode(1003, FORT, FORT, 120, 87)
					.addEndNode(1004, FORT, FORT, 120, 20)
					.buildZone();
			
			new Zone(loadService, assetManager, random, nodes, nodeMap, 3, 2)
					.addStartNode(zone3.getEndNodes().get(0))
					.addEndNode(1005, FORT, FORT, 170, 87)
					.addEndNode(1006, FORT, FORT, 170, 20)
					.buildZone();

		}
		else {
			int nodeCode = 1;
			IntSet visitedCodesSet = loadService.loadDataValue(SaveEnum.VISITED_LIST, IntSet.class);
			addNode(getNode(nodeCode, DEFAULT, DEFAULT, -8, 15, visitedCodesSet.contains(nodeCode++)), nodes);
			addNode(getNode(nodeCode, COTTAGE_TRAINER, COTTAGE_TRAINER_VISIT, -2, 12, visitedCodesSet.contains(nodeCode++)), nodes);
			addNode(getNode(nodeCode, TOWN_STORY, TOWN2, 5, 11, visitedCodesSet.contains(nodeCode++)), nodes);
			addNode(getNode(nodeCode, FIRST_BATTLE_STORY, DEFAULT, 13, 12, visitedCodesSet.contains(nodeCode++)), nodes);
			addNode(getNode(nodeCode, EncounterCode.MERI_COTTAGE, EncounterCode.MERI_COTTAGE_VISIT, 13, 4, visitedCodesSet.contains(nodeCode++)), nodes);
			addNode(getNode(nodeCode, EncounterCode.ECCENTRIC_MERCHANT, DEFAULT, 22, 10, visitedCodesSet.contains(nodeCode++)), nodes);	
			addNode(getNode(nodeCode, EncounterCode.STORY_FEM, DEFAULT, 21, 16, visitedCodesSet.contains(nodeCode++)), nodes);
			addNode(getNode(nodeCode, EncounterCode.STORY_SIGN, DEFAULT, 29, 16, visitedCodesSet.contains(nodeCode++)), nodes);
			addNode(getNode(nodeCode, EncounterCode.WEST_PASS, DEFAULT, 28, 25, visitedCodesSet.contains(nodeCode++)), nodes);
			addNode(getNode(nodeCode, EncounterCode.SOUTH_PASS, DEFAULT, 38, 12, visitedCodesSet.contains(nodeCode++)), nodes);
			
			addNode(getNode(nodeCode, EncounterCode.FOOD_CACHE, DEFAULT, 7, 21, visitedCodesSet.contains(nodeCode++)), nodes);
			addNode(getNode(nodeCode, EncounterCode.OGRE_WARNING_STORY, DEFAULT, 6, 28, visitedCodesSet.contains(nodeCode++)), nodes);	
			addNode(getNode(nodeCode, EncounterCode.OGRE_STORY, DEFAULT, 6, 34, visitedCodesSet.contains(nodeCode++)), nodes);
			
			addNode(getNode(nodeCode, EncounterCode.FOOD_CACHE, DEFAULT, 4, 42, visitedCodesSet.contains(nodeCode++)), nodes);
			addNode(getNode(nodeCode, EncounterCode.FORT, FORT, 5, 48, visitedCodesSet.contains(nodeCode++)), nodes);
			
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
}

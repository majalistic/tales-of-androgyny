package com.majalis.world;

import static com.majalis.encounter.EncounterCode.*;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.IntSet;
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
			Zone zone = new Zone(loadService, assetManager, random, nodes, nodeMap, 1,  1)
					.addStartNode(1, INITIAL, DEFAULT, 20, 10)
					.addEndNode(1000, TOWN, TOWN, 36, 18)
					//.addEndNode(5000, CRIER_QUEST, CRIER_QUEST, new Vector2(1300, 1300))
					.addEndNode(10000, GADGETEER, DEFAULT, 20, 22)
					.buildZone();
			
			Zone zone2 = new Zone(loadService, assetManager, random, nodes, nodeMap, 1,  3)
					.addStartNode(zone.getEndNodes().get(0))
					.addEndNode(1001, SPIDER, SPIDER, 72, 37)
					.addEndNode(1002, FORT, FORT, 48, 72)
					.buildZone();
			
			new Zone(loadService, assetManager, random, nodes, nodeMap, 2, 3)
					.addStartNode(zone2.getEndNodes().get(0))
					.addEndNode(1003, FORT, FORT, 120, 62)
					.addEndNode(1004, FORT, FORT, 120, 10)
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
			addNode(getNode(nodeCode, EncounterCode.ECCENTRIC_MERCHANT, DEFAULT, 22, 7, visitedCodesSet.contains(nodeCode++)), nodes);	
			addNode(getNode(nodeCode, EncounterCode.STORY_FEM, DEFAULT, 21, 13, visitedCodesSet.contains(nodeCode++)), nodes);
			addNode(getNode(nodeCode, EncounterCode.STORY_SIGN, DEFAULT, 31, 6, visitedCodesSet.contains(nodeCode++)), nodes);
			addNode(getNode(nodeCode, EncounterCode.WEST_PASS, DEFAULT, 32, 15, visitedCodesSet.contains(nodeCode++)), nodes);
			addNode(getNode(nodeCode, EncounterCode.SOUTH_PASS, DEFAULT, 41, -2, visitedCodesSet.contains(nodeCode++)), nodes);
			
			addNode(getNode(nodeCode, EncounterCode.OGRE_WARNING_STORY, DEFAULT, 7, 23, visitedCodesSet.contains(nodeCode++)), nodes);	
			addNode(getNode(nodeCode, EncounterCode.OGRE_STORY, DEFAULT, 6, 32, visitedCodesSet.contains(nodeCode++)), nodes);
			
			new Zone(loadService, assetManager, random, nodes, nodeMap, 1, 1)
				.addStartNode(nodes.get(nodes.size-1))
				.addEndNode(1003, FORT, FORT, 9, 51)
				.buildZone();
			
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
		return nodes;
	}
	
	private void addNode(GameWorldNode newNode, @SuppressWarnings("unchecked") Array<GameWorldNode> ... nodes) {
		for (Array<GameWorldNode> nodeArray: nodes) {
			nodeArray.add(newNode);
		}
		nodeMap.put(newNode.getNodeCode(), newNode);
	}
	
	private GameWorldNode getNode(int nodeCode, EncounterCode initialEncounter, EncounterCode defaultEncounter, int x, int y, boolean visited) {
		return new GameWorldNode(nodeCode, new GameWorldNodeEncounter(initialEncounter, defaultEncounter), x, y, visited, sound, character, assetManager);
	}
}

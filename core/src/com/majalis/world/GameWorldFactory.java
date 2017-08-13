package com.majalis.world;

import static com.majalis.encounter.EncounterCode.*;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Vector2;
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
	public Array<GameWorldNode> getGameWorld(int seed, GameMode gameMode) {
		random.setSeed(seed);

		nodeMap = new IntMap<GameWorldNode>();
		nodes = new Array<GameWorldNode>();
		// specifying the start and end points to a getZone method which will perform the below functions, creating an array of nodes.  Can make it a class so that it can be chained?  Piping in the end points as start points

		sound = assetManager.get(AssetEnum.CLICK_SOUND.getSound());
		character = loadService.loadDataValue(SaveEnum.PLAYER, PlayerCharacter.class);
		
		if (gameMode == GameMode.SKIRMISH) {
			Zone zone = new Zone(loadService, assetManager, random, nodes, nodeMap, 1,  3)
					.addStartNode(1, INITIAL, DEFAULT, new Vector2(500, 500))
					.addEndNode(1000, TOWN, TOWN, new Vector2(900, 900))
					//.addEndNode(5000, CRIER_QUEST, CRIER_QUEST, new Vector2(1300, 1300))
					.addEndNode(10000, GADGETEER, DEFAULT, new Vector2(500, 800))
					.buildZone();
			
			Zone zone2 = new Zone(loadService, assetManager, random, nodes, nodeMap, 1,  8)
					.addStartNode(zone.getEndNodes().get(0))
					.addEndNode(1001, SPIDER, SPIDER, new Vector2(1800, 1800))
					.addEndNode(1002, FORT, FORT, new Vector2(1200, 2400))
					.buildZone();
			
			new Zone(loadService, assetManager, random, nodes, nodeMap, 2, 8)
					.addStartNode(zone2.getEndNodes().get(0))
					.addEndNode(1003, FORT, FORT, new Vector2(3000, 3000))
					.addEndNode(1004, FORT, FORT, new Vector2(3000, 1700))
					.buildZone();
		}
		else {
			int nodeCode = 1;
			IntSet visitedCodesSet = loadService.loadDataValue(SaveEnum.VISITED_LIST, IntSet.class);
			addNode(getNode(nodeCode, DEFAULT, DEFAULT, new Vector2(-200, 300), visitedCodesSet.contains(nodeCode++)), nodes);
			addNode(getNode(nodeCode, COTTAGE_TRAINER, COTTAGE_TRAINER_VISIT, new Vector2(-50, 285), visitedCodesSet.contains(nodeCode++)), nodes);
			addNode(getNode(nodeCode, TOWN_STORY, TOWN2, new Vector2(145, 350), visitedCodesSet.contains(nodeCode++)), nodes);
			addNode(getNode(nodeCode, FIRST_BATTLE_STORY, DEFAULT, new Vector2(325, 480), visitedCodesSet.contains(nodeCode++)), nodes);
			addNode(getNode(nodeCode, EncounterCode.MERI_COTTAGE, EncounterCode.MERI_COTTAGE_VISIT, new Vector2(340, 260), visitedCodesSet.contains(nodeCode++)), nodes);
			addNode(getNode(nodeCode, EncounterCode.ECCENTRIC_MERCHANT, DEFAULT, new Vector2(555, 450), visitedCodesSet.contains(nodeCode++)), nodes);	
			addNode(getNode(nodeCode, EncounterCode.STORY_FEM, DEFAULT, new Vector2(545, 590), visitedCodesSet.contains(nodeCode++)), nodes);
			addNode(getNode(nodeCode, EncounterCode.STORY_SIGN, DEFAULT, new Vector2(790, 530), visitedCodesSet.contains(nodeCode++)), nodes);
			addNode(getNode(nodeCode, EncounterCode.WEST_PASS, DEFAULT, new Vector2(820, 770), visitedCodesSet.contains(nodeCode++)), nodes);
			addNode(getNode(nodeCode, EncounterCode.SOUTH_PASS, DEFAULT, new Vector2(1040, 460), visitedCodesSet.contains(nodeCode++)), nodes);
			
			addNode(getNode(nodeCode, EncounterCode.OGRE_WARNING_STORY, DEFAULT, new Vector2(180, 675), visitedCodesSet.contains(nodeCode++)), nodes);	
			addNode(getNode(nodeCode, EncounterCode.OGRE_STORY, DEFAULT, new Vector2(150, 875), visitedCodesSet.contains(nodeCode++)), nodes);
			
			new Zone(loadService, assetManager, random, nodes, nodeMap, 1, 2)
				.addStartNode(nodes.get(nodes.size-1))
				.addEndNode(1003, FORT, FORT, new Vector2(240, 1400))
				.buildZone();
			
			for (int ii = 0; ii < nodes.size-1; ii++) {
				for (int jj = ii + 1; jj < nodes.size; jj++) {
					if (nodes.get(ii).isAdjacent(nodes.get(jj))) {
						nodes.get(ii).connectTo(nodes.get(jj));
					}
				}
			}
		}
		
		nodeMap.get((Integer)loadService.loadDataValue(SaveEnum.NODE_CODE, Integer.class)).setAsCurrentNode();
		nodes.sort();
		return nodes;
	}
	
	private void addNode(GameWorldNode newNode, @SuppressWarnings("unchecked") Array<GameWorldNode> ... nodes) {
		for (Array<GameWorldNode> nodeArray: nodes) {
			nodeArray.add(newNode);
		}
		nodeMap.put(newNode.getNodeCode(), newNode);
	}
	
	private GameWorldNode getNode(int nodeCode, EncounterCode initialEncounter, EncounterCode defaultEncounter, Vector2 position, boolean visited) {
		return new GameWorldNode(nodeCode, new GameWorldNodeEncounter(initialEncounter, defaultEncounter), position, visited, sound, character, assetManager);
	}
}

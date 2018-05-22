package com.majalis.world;

import static com.majalis.encounter.EncounterCode.*;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.majalis.character.PlayerCharacter;
import com.majalis.encounter.EncounterCode;
import com.majalis.save.LoadService;
import com.majalis.save.SaveEnum;
import com.majalis.save.SaveManager;
import com.majalis.save.SaveManager.GameMode;
import com.majalis.save.SaveManager.VisitInfo;
import com.majalis.talesofandrogyny.Logging;
import com.majalis.talesofandrogyny.TalesOfAndrogyny;
/*
 * Generates a world map or returns the world map.
 */
public class GameWorldFactory {
	private final LoadService loadService;
	private final AssetManager assetManager;
	private final RandomXS128 random;
	private PlayerCharacter character;
	private IntMap<GameWorldNode> nodeMap = new IntMap<GameWorldNode>();
	private Array<GameWorldNode> nodes = new Array<GameWorldNode>();
	private int mapCode; // temporary cache of map code instead of parameter passing
	
	public GameWorldFactory(SaveManager saveManager, AssetManager assetManager, RandomXS128 random) {
		this.loadService = saveManager;
		this.assetManager = assetManager;
		this.random = random;
	}

	@SuppressWarnings("unchecked")
	public GameWorld getGameWorld(int seed, int mapCode, GameMode gameMode, int currentNode) {
		random.setSeed(seed);
		this.mapCode = mapCode;
		nodeMap = new IntMap<GameWorldNode>();
		nodes = new Array<GameWorldNode>();
		// specifying the start and end points to a getZone method which will perform the below functions, creating an array of nodes.  Can make it a class so that it can be chained?  Piping in the end points as start points
		character = loadService.loadDataValue(SaveEnum.PLAYER, PlayerCharacter.class);
		
		Zone zone4 = null;
		IntMap<VisitInfo> visitedInfo = loadService.loadDataValue(SaveEnum.VISITED_LIST, IntMap.class);
		if (mapCode == 0) {
			ObjectSet<EncounterCode> unspawnedEncounters = new ObjectSet<EncounterCode>(EncounterCode.getAllRandomEncounters());			
			GameWorldNode mermaid = null;
			GameWorldNode secondTown = null;
			GameWorldNode giantess = null;
			GameWorldNode mouthfiend = null;
			GameWorldNode mouthfiend2 = null;
			GameWorldNode leaveMap = null;
			
			if (gameMode == GameMode.SKIRMISH) {
				int nodeCode = 2;
				nodeCode = new Zone(loadService, assetManager, random, nodes, nodeMap, unspawnedEncounters, 1,  1, mapCode)
					.addStartNode(1, INITIAL, DEFAULT, 18, 89) 
					.addEndNode(10000, GADGETEER, GADGETEER,  18, 100)
					.buildZone(nodeCode).nodeCode;
				
				Zone zone = new Zone(loadService, assetManager, random, nodes, nodeMap, unspawnedEncounters, 1,  2, mapCode)
					.addStartNode(nodes.get(0))
					.addEndNode(1000, TOWN, TOWN, 31, 94)
					.buildZone(nodeCode);
				
				mermaid = addNode(getNode(2000, MERMAID, MERMAID, 50, 94, visitedInfo.get(2000, getFreshVisitInfo())), nodes);
				
				secondTown = addNode(getNode(1007, TOWN3, TOWN3, 45, 127, visitedInfo.get(1007, getFreshVisitInfo())), nodes);
				
				giantess = addNode(getNode(3000, DEFAULT, DEFAULT, 80, 142, visitedInfo.get(3000, getFreshVisitInfo())), nodes);
				
				leaveMap = addNode(getNode(29999, LEAVE_MAP, LEAVE_MAP, 80, 150, visitedInfo.get(29999, getFreshVisitInfo())), nodes);
				
				GameWorldNode offMap = addNode(getNode(29998, LEAVE_MAP, LEAVE_MAP, 80, 160, visitedInfo.get(29998, getFreshVisitInfo())), nodes);
				offMap.connectTo(leaveMap);
				
				Zone zone2 = new Zone(loadService, assetManager, random, nodes, nodeMap, unspawnedEncounters, 2, 1, mapCode)
					.addStartNode(zone.getEndNodes().get(0))
					.addEndNode(1001, SPIDER, SPIDER, 53, 109)
					.buildZone(zone.nodeCode);
				
				Zone zone3 = new Zone(loadService, assetManager, random, nodes, nodeMap, unspawnedEncounters, 2, 2, mapCode)
					.addStartNode(zone2.getEndNodes().get(0))
					.addEndNode(1003, ANGEL, ALTAR, 83, 119)
					.addEndNode(1004, WITCH_COTTAGE, WITCH_COTTAGE, 83, 88)
					.buildZone(zone2.nodeCode);
				
				zone4 = new Zone(loadService, assetManager, random, nodes, nodeMap, unspawnedEncounters, 3, 2, mapCode)
					.addStartNode(zone3.getEndNodes().get(0))
					.addEndNode(1005, QUETZAL, QUETZAL, 119, 115)
					.addEndNode(1006, FORT, FORT, 119, 84)
					.buildZone(zone3.nodeCode);
				
				mouthfiend = addNode(getNode(50000, MOUTH_FIEND, MOUTH_FIEND, 96, 49, visitedInfo.get(50000, getFreshVisitInfo())), nodes);
				mouthfiend2 = addNode(getNode(50001, MOUTH_FIEND_ESCAPE, MOUTH_FIEND_ESCAPE, 99, 49, visitedInfo.get(50001, getFreshVisitInfo())), nodes);
				mouthfiend.connectTo(mouthfiend2);	
			}
			else {
				int nodeCode = 1;
				
				addNode(getNode(nodeCode, DEFAULT, DEFAULT, 12, 92, visitedInfo.get(nodeCode++, getFreshVisitInfo())), nodes);
				addNode(getNode(nodeCode, COTTAGE_TRAINER, COTTAGE_TRAINER, 15, 91, visitedInfo.get(nodeCode++, getFreshVisitInfo())), nodes);
				addNode(getNode(nodeCode, TOWN_STORY, TOWN2, 19, 90, visitedInfo.get(nodeCode++, getFreshVisitInfo())), nodes);
				addNode(getNode(nodeCode, FIRST_BATTLE_STORY, DEFAULT, 23, 90, visitedInfo.get(nodeCode++, getFreshVisitInfo())), nodes);
				addNode(getNode(nodeCode, MERI_COTTAGE, MERI_COTTAGE, 23, 86,  visitedInfo.get(nodeCode++, getFreshVisitInfo())), nodes);
				addNode(getNode(nodeCode, ECCENTRIC_MERCHANT, DEFAULT, 28, 88,  visitedInfo.get(nodeCode++, getFreshVisitInfo())), nodes);	
				addNode(getNode(nodeCode, STORY_FEM, DEFAULT, 28, 91,  visitedInfo.get(nodeCode++, getFreshVisitInfo())), nodes);
				addNode(getNode(nodeCode, STORY_SIGN, DEFAULT, 32, 91,  visitedInfo.get(nodeCode++, getFreshVisitInfo())), nodes);
				addNode(getNode(nodeCode, BRIGAND_STORY, DEFAULT, 31, 95,  visitedInfo.get(nodeCode++, getFreshVisitInfo())), nodes);
				addNode(getNode(nodeCode, HARPY_STORY, DEFAULT, 37, 88,  visitedInfo.get(nodeCode++, getFreshVisitInfo())), nodes);
				addNode(getNode(nodeCode, ORC_STORY, DEFAULT, 37, 93,  visitedInfo.get(nodeCode++, getFreshVisitInfo())), nodes);
				addNode(getNode(nodeCode, WEREWOLF_STORY, DEFAULT, 42, 90,  visitedInfo.get(nodeCode++, getFreshVisitInfo())), nodes);
				addNode(getNode(nodeCode, END_OF_STORY, DEFAULT, 44, 92, visitedInfo.get(nodeCode++, getFreshVisitInfo())), nodes);
				
				addNode(getNode(nodeCode, FOOD_CACHE, DEFAULT, 20, 95,  visitedInfo.get(nodeCode++, getFreshVisitInfo())), nodes);
				addNode(getNode(nodeCode, OGRE_WARNING_STORY, DEFAULT, 19, 99,  visitedInfo.get(nodeCode++, getFreshVisitInfo())), nodes);	
				addNode(getNode(nodeCode, OGRE_STORY, DEFAULT, 19, 103,  visitedInfo.get(nodeCode++, getFreshVisitInfo())), nodes);
				
				addNode(getNode(nodeCode, ICE_CREAM, ICE_CREAM, 24, 102,  visitedInfo.get(nodeCode++, getFreshVisitInfo())), nodes);
				addNode(getNode(nodeCode, FORT, FORT, 29, 102,  visitedInfo.get(nodeCode++, getFreshVisitInfo())), nodes);
				
				for (int ii = 0; ii < nodes.size-1; ii++) {
					for (int jj = ii + 1; jj < nodes.size; jj++) {
						if (nodes.get(ii).isAdjacent(nodes.get(jj))) {
							nodes.get(ii).connectTo(nodes.get(jj));
						}
					}
				}
			}
			
			if (mermaid != null) {
				GameWorldNode closestBeforeRiver = null;
				GameWorldNode closestAfterRiver = null;
				for (GameWorldNode node : nodes) {
					if (node == mermaid || node == mouthfiend || node == mouthfiend2) continue;
					int distanceToNode = mermaid.getDistance(node);
					if (closestBeforeRiver == null || (beforeRiver(node) && closestBeforeRiver.getDistance(mermaid) > distanceToNode)) { closestBeforeRiver = node; }
					if (closestAfterRiver == null || (afterRiver(node) && closestAfterRiver.getDistance(mermaid) > distanceToNode)) { closestAfterRiver = node; }
				}
				if (closestBeforeRiver != null) mermaid.connectTo(closestBeforeRiver);	
				if (closestAfterRiver != null) mermaid.connectTo(closestAfterRiver);	
			}
			
			if (secondTown != null && !secondTown.isConnected()) {
				GameWorldNode closest = null;
				for (GameWorldNode node : nodes) {
					if (node == secondTown) continue;
					int distanceToNode = secondTown.getDistance(node);
					if (closest == null || closest.getDistance(secondTown) > distanceToNode) { closest = node; }
				}
				if (closest != null) secondTown.connectTo(closest);		
			}
			
			if (giantess != null && !giantess.isConnected()) {
				GameWorldNode closest = null;
				for (GameWorldNode node : nodes) {
					if (node == giantess || node == leaveMap) continue;
					int distanceToNode = giantess.getDistance(node);
					if (closest == null || closest.getDistance(giantess) > distanceToNode) { closest = node; }
				}
				if (closest != null) giantess.connectTo(closest);		
			}
			
			leaveMap.connectTo(giantess);
		}
		else { // second map
			int nodeCode = 30000;
			GameWorldNode offramp = addNode(getNode(nodeCode, DEFAULT, DEFAULT, 20, 92, visitedInfo.get(nodeCode++, getFreshVisitInfo())), nodes);
			GameWorldNode onramp = addNode(getNode(nodeCode, RETURN_MAP, RETURN_MAP, 15, 87, visitedInfo.get(nodeCode++, getFreshVisitInfo())), nodes);
			GameWorldNode offScreen = addNode(getNode(nodeCode, RETURN_MAP, RETURN_MAP, 15, 70, visitedInfo.get(nodeCode++, getFreshVisitInfo())), nodes);
			addNode(getNode(nodeCode, GOLD_CACHE, DEFAULT, 24, 95, visitedInfo.get(nodeCode++, getFreshVisitInfo())), nodes);
			addNode(getNode(nodeCode, FOOD_CACHE, DEFAULT, 21, 95, visitedInfo.get(nodeCode++, getFreshVisitInfo())), nodes);
			addNode(getNode(nodeCode, GIANTESS_FUTA, GIANTESS_FUTA, 27, 92, visitedInfo.get(nodeCode++, getFreshVisitInfo())), nodes);
			
			offramp.connectTo(onramp);
			onramp.connectTo(offScreen);
			for (int ii = 0; ii < nodes.size-1; ii++) {
				for (int jj = ii + 1; jj < nodes.size; jj++) {
					if (nodes.get(ii).isAdjacent(nodes.get(jj))) {
						nodes.get(ii).connectTo(nodes.get(jj));
					}
				}
			}			
		}
		
		if (nodeMap.get(currentNode) != null) {
			nodeMap.get(currentNode).setCurrent();
		}
		else {
			nodes.get(0).setCurrent();
		}
		
		GameWorldNode first = nodes.get(0);
		
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
			if (first.getPathTo(zone4.getEndNodes().get(0)).size == 0) {
				Logging.logTime("Failed to create a path from start to finish");
			}
		}
		return new GameWorld(nodes, mapCode, assetManager, random);
	}
	
	private VisitInfo getFreshVisitInfo() { return new VisitInfo(0, 0, (int) ((Math.random() * 1000) % 1000), 0, -1, mapCode); }
	
	private boolean beforeRiver(GameWorldNode node) { 
		float x = node.getHexPosition().x;
		float y = node.getHexPosition().y;
		return  x + y < 140;
	}
	
	private boolean afterRiver(GameWorldNode node) { return !beforeRiver(node); }
	
	private GameWorldNode addNode(GameWorldNode newNode, @SuppressWarnings("unchecked") Array<GameWorldNode> ... nodes) {
		for (Array<GameWorldNode> nodeArray: nodes) {
			nodeArray.add(newNode);
		}
		nodeMap.put(newNode.getNodeCode(), newNode);
		return newNode;
	}
	
	private GameWorldNode getNode(int nodeCode, EncounterCode initialEncounter, EncounterCode defaultEncounter, int x, int y, VisitInfo visitInfo) {
		return new GameWorldNode(nodeCode, new GameWorldNodeEncounter(initialEncounter, defaultEncounter), x, y, visitInfo, character, assetManager);
	}
}

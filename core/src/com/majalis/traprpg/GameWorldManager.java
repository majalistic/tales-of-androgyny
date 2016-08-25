package com.majalis.traprpg;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
/*
 * Generates a world map or returns the world map.
 */
public class GameWorldManager {

	private final EncounterFactory encounterFactory;
	private GameContext context;
	
	public GameWorldManager(EncounterFactory encounterFactory){
		this.encounterFactory = encounterFactory;
	}
	
	public GameWorld getGameWorld(BitmapFont font) {
		Array<WorldNode> nodes = new Array<WorldNode>();
		
		for (int ii = 1; ii <= 10; ii++){
			Encounter nodeEncounter = encounterFactory.getEncounter(1, font);
			// 100 = magic number to get the defaultEncounter for now
			Encounter defaultEncounter  = encounterFactory.getEncounter(100, font);
			nodes.add(new WorldNode(new Array<WorldNode>(), nodeEncounter, defaultEncounter, new Vector2(ii * 85, 200 + (200 * Math.floorMod(ii, 3))-ii*10), ii));
		}
		
		for (int ii = 0; ii < nodes.size-1; ii++){
			for (int jj = ii + 1; jj < nodes.size; jj++){
				if (nodes.get(ii).isAdjacent(nodes.get(jj))){
					nodes.get(ii).connectTo(nodes.get(jj));
				}
			}
		}
		
		// uncomment to see connections gneerated
		// printConnections(nodes);
		
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
	
	@SuppressWarnings("unused")
	private void printConnections(Array<WorldNode> nodes){
		for (WorldNode node: nodes){
			Array<Integer> connectedNodeCodes = new Array<Integer>();
			for (WorldNode connectedNode: node.getConnectedNodes()){
				connectedNodeCodes.add(connectedNode.getCode());
			}
			System.out.println(node.getCode() + " is connected to " + connectedNodeCodes);
		}
	}
}

package com.majalis.traprpg;

import com.badlogic.gdx.utils.Array;

public class GameWorldManager {

	private GameContext context;
	
	public GameWorldManager(){
	}
	
	public GameWorld getGameWorld() {
		Array<WorldNode> nodes = new Array<WorldNode>();
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

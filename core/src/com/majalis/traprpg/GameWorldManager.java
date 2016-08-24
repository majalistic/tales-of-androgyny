package com.majalis.traprpg;

public class GameWorldManager {

	private ClassEnum characterClass;
	private GameContext context;
	
	public GameWorldManager(){
	}
	
	public static String getGameWorld(String characterClass) {
		return characterClass;
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

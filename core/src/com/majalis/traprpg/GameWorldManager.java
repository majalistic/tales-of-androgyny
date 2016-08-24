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
	
	private enum ClassEnum {
		WARRIOR,
		PALADIN,
		THIEF,
		RANGER,
		MAGE,
		ENCHANTRESS
	}
	
	public enum GameContext {
		ENCOUNTER,
		WORLD_MAP
	}
}

package com.majalis.traprpg;

import com.majalis.traprpg.GameWorldManager.GameContext;

public class GameWorldManager {

	private ClassEnum characterClass;
	private GameContext context;
	
	public GameWorldManager(){
	}
	
	public static String getGameWorld(String characterClass) {
		return characterClass;
	}

	public void setContext(GameContext context2) {
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

package com.majalis.battle;
/*
 * Represents the info about a battle for saving and loading.
 */
public class BattleCode {

	public int battleCode;
	public int victoryScene;
	public int defeatScene;
	
	@SuppressWarnings("unused")
	private BattleCode(){}
	
	public BattleCode(int battleCode, int victoryScene, int defeatScene) {
		this.battleCode = battleCode;
		this.victoryScene = victoryScene;
		this.defeatScene = defeatScene;
	}
}

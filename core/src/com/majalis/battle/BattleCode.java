package com.majalis.battle;

import com.majalis.character.AbstractCharacter.Stance;

/*
 * Represents the info about a battle for saving and loading.
 */
public class BattleCode {

	public int battleCode;
	public int victoryScene;
	public int defeatScene;
	public Stance playerStance;
	public Stance enemyStance;
	
	@SuppressWarnings("unused")
	private BattleCode(){}
	
	public BattleCode(int battleCode, int victoryScene, int defeatScene, Stance playerStance, Stance enemyStance) {
		this.battleCode = battleCode;
		this.victoryScene = victoryScene;
		this.defeatScene = defeatScene;
		this.playerStance = playerStance;
		this.enemyStance = enemyStance;
	}
}

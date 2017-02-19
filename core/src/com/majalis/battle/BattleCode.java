package com.majalis.battle;

import com.badlogic.gdx.utils.ObjectMap;
import com.majalis.character.AbstractCharacter.Stance;

/*
 * Represents the info about a battle for saving and loading.
 */
public class BattleCode {

	public int battleCode;
	public ObjectMap<String, Integer> outcomes;
	public Stance playerStance;
	public Stance enemyStance;
	public boolean disarm;
	public int climaxCounter;
	
	@SuppressWarnings("unused")
	private BattleCode(){}
	
	public BattleCode(int battleCode, ObjectMap<String, Integer> outcomes, Stance playerStance, Stance enemyStance, boolean disarm, int climaxCounter) {
		this.battleCode = battleCode;
		this.outcomes = outcomes;
		this.playerStance = playerStance;
		this.enemyStance = enemyStance;
		this.disarm = disarm;
		this.climaxCounter = climaxCounter;
	}
}

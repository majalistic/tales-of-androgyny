package com.majalis.battle;

import com.majalis.character.AbstractCharacter.Stance;
/*
 * Represents the result of an attack after it has been filtered through an opposing action.
 */
public class Attack {

	private final int damage;
	private final Stance forceStance;
	public Attack(int damage, Stance forceStance){
		this.damage = damage;
		this.forceStance = forceStance;
	}
	
	public int getDamage(){
		return damage;
	}
	public Stance getForceStance(){
		return forceStance;
	}
}

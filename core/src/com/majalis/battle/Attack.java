package com.majalis.battle;

import com.majalis.character.AbstractCharacter.Stance;

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

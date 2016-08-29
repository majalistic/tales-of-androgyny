package com.majalis.character;

import com.majalis.character.AbstractCharacter.Stance;

/*
 * Represents an action taken by a character in battle.  Will likely need a builder helper.
 */
public class Technique {
	private final Techniques technique;
	private final int strength;
	public Technique(Techniques technique, int strength){
		this.technique = technique;
		this.strength = strength;
	}
	public int getDamage(){
		int damage = strength + technique.getPowerMod();
		if (damage < 0) damage = 0;
		return damage;
	}	
	public Stance getStance(){
		return technique.getStanceResult();
	}
	public String getTechniqueName(){
		return technique.toString();
	}
	
}

package com.majalis.character;
/*
 * Represents an action taken by a character in battle.  Will likely need a builder helper.
 */
public class Technique {

	private final int damage;
	public Technique(int strength){
		this.damage = strength + 1;
	}
	
	public int getDamage(){
		return damage;
	}	
}

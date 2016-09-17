package com.majalis.character;

import com.majalis.character.AbstractCharacter.Stance;
/*
 * Represents an action taken by a character in battle.  Will likely need a builder helper.
 */
public class Technique {
	private final Techniques technique;
	private final int strength;
	private final int block;
	private final Stance forceStance;
	private final boolean battleOver;
	
	public Technique(Techniques technique, int strength){
		this(technique, strength, 0);
	}
	public Technique(Techniques technique, int strength, int block){
		this.technique = technique;
		this.strength = strength;
		this.block = block;
		forceStance = setForceStance();
		
		battleOver = technique == Techniques.KNOT_BANG;
	}
	
	public int getDamage(){
		// can special case powerMod 100 = 0 here
		int damage = technique == Techniques.KNOT ? 4 : strength + technique.getPowerMod();
		if (damage < 0) damage = 0;
		return damage;
	}	
	
	public int getBlock(){
		return block + (technique == Techniques.GUARD ? 100 : 0);
	}
	
	private Stance setForceStance(){
		switch (technique){
			case POUNCE:
				return Stance.DOGGY;
			case ERUPT:
				return Stance.BALANCED;
			case KNOT:
				return Stance.KNOTTED;
			default:
				return null;
		}
	}
	
	public Stance getStance(){
		return technique.getStanceResult();
	}
	
	public String getTechniqueName(){
		return technique.toString();
	}

	// right now this is a pass-through for technique.getStaminaCost() - could be modified by player (status effect that increases stamina cost, for instance)
	public int getStaminaCost(){
		return technique.getStaminaCost();
	}
	
	public int getStabilityCost(){
		return technique.getStabilityCost();
	}
	
	public Stance getForceStance(){
		return forceStance;
	}	
	
	public boolean forceBattleOver(){
		return battleOver;
	}
}

package com.majalis.character;

import com.majalis.Technique.TechniquePrototype;
import com.majalis.character.AbstractCharacter.Stance;
/*
 * Represents an action taken by a character in battle.  Will likely need a builder helper.
 */
public class Technique {
	private final TechniquePrototype technique;
	private final int strength;
	private final int block;
	private final Stance forceStance;
	private final boolean battleOver;
	
	public Technique(TechniquePrototype techniquePrototype, int strength){
		this(techniquePrototype, strength, 0);
	}
	public Technique(TechniquePrototype techniquePrototype, int strength, int block){
		this.technique = techniquePrototype;
		this.strength = strength;
		this.block = block + technique.getGuardMod();
		forceStance = technique.getForceStance();
		battleOver = technique.causesBattleOver();
	}
	
	public int getDamage(){
		// can special case powerMod 100 = 0 here
		int damage = technique.doesSetDamage() ? 4 : technique.isDamaging() ? strength + technique.getPowerMod() : 0;
		if (damage < 0) damage = 0;
		return damage;
	}	
	
	public int getBlock(){
		return block;
	}

	public Stance getStance(){
		return technique.getResultingStance();
	}
	
	public String getTechniqueName(){
		return technique.getName();
	}

	// right now this is a pass-through for technique.getStaminaCost() - could be modified by player (status effect that increases stamina cost, for instance)
	public int getStaminaCost(){
		return technique.getStaminaCost();
	}
	
	public int getStabilityCost(){
		return technique.getStabilityCost();
	}
	
	public int getManaCost(){
		return technique.getManaCost();
	}
	
	public Stance getForceStance(){
		return forceStance;
	}	
	
	public boolean forceBattleOver(){
		return battleOver;
	}
}

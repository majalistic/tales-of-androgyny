package com.majalis.technique;

import com.majalis.character.AbstractCharacter.Stance;

public class NonAttackTechnique extends TechniquePrototype {

	public NonAttackTechnique(Stance resultingStance, String name, int staminaCost, int stabilityCost){
		this(resultingStance, name, staminaCost, stabilityCost, null);
	}
	
	public NonAttackTechnique(Stance resultingStance, String name, int staminaCost, int stabilityCost, Stance forceStance){
		this(resultingStance, name, staminaCost, stabilityCost, forceStance, false);
	}

	public NonAttackTechnique(Stance resultingStance, String name, int staminaCost, int stabilityCost, Stance forceStance, boolean battleOver) {
		this(resultingStance, name, staminaCost, stabilityCost, forceStance, battleOver, null);
	}

	public NonAttackTechnique(Stance resultingStance, String name, int staminaCost, int stabilityCost, Stance forceStance, String setDamage) {
		this(resultingStance, name, staminaCost, stabilityCost, forceStance, false, setDamage);
	}
	
	public NonAttackTechnique(Stance resultingStance, String name, int staminaCost, int stabilityCost, Stance forceStance, boolean battleOver, String setDamage) {
		this(resultingStance, name, staminaCost, stabilityCost, forceStance, battleOver, setDamage, null);
	}

	public NonAttackTechnique(Stance resultingStance, String name, int staminaCost, int stabilityCost, Stance forceStance, TechniqueHeight height) {
		this(resultingStance, name, staminaCost, stabilityCost, forceStance, false, null, height);
	}
	
	public NonAttackTechnique(Stance resultingStance, String name, int staminaCost, int stabilityCost, boolean taunt) {
		this(resultingStance, name, staminaCost, stabilityCost, null, false, null, null, taunt);
	}
	
	public NonAttackTechnique(Stance resultingStance, String name, int staminaCost, int stabilityCost, Stance forceStance, boolean battleOver, String setDamage, TechniqueHeight height){		
		this(resultingStance, name, staminaCost, stabilityCost, forceStance, battleOver, setDamage, height, false);
	}

	public NonAttackTechnique(Stance resultingStance, String name, int staminaCost, int stabilityCost, Stance forceStance, boolean battleOver, String setDamage, TechniqueHeight height, boolean taunt){
		super(resultingStance, name);
		this.staminaCost = staminaCost;
		this.stabilityCost = stabilityCost;
		this.forceStance = forceStance;
		this.causeBattleOver = battleOver;
		this.setDamage = setDamage != null;
		this.height = height;
		this.isTaunt = taunt;
	}	
}

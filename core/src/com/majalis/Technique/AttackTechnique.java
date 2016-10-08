package com.majalis.Technique;

import com.majalis.character.AbstractCharacter.Stance;

public class AttackTechnique extends TechniquePrototype {
	public AttackTechnique(Stance resultingStance, String name, int powerMod, int staminaCost, int stabilityCost){
		this(resultingStance, name, powerMod, staminaCost, stabilityCost, TechniqueHeight.MEDIUM);
	}
	public AttackTechnique(Stance resultingStance, String name, int powerMod, int staminaCost, int stabilityCost, TechniqueHeight height){
		super(resultingStance, name);
		this.doesDamage = true;
		this.powerMod = powerMod;
		this.staminaCost = staminaCost;
		this.stabilityCost = stabilityCost;
		this.height = height;
	}
}
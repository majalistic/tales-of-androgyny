package com.majalis.technique;

import com.majalis.character.Stance;
import com.majalis.technique.TechniquePrototype.TechniqueHeight;

public class NonAttackTechnique extends TechniqueBuilder {

	public NonAttackTechnique(Stance usableStance, Stance resultingStance, String name, int staminaCost, int stabilityCost) {
		this(usableStance, resultingStance, name, staminaCost, stabilityCost, null, TechniqueHeight.NONE);
	}

	public NonAttackTechnique(Stance usableStance, Stance resultingStance, String name, int staminaCost, int stabilityCost, Stance forceStance) {
		this(usableStance, resultingStance, name, staminaCost, stabilityCost, forceStance, null, TechniqueHeight.NONE);
	}
	
	public NonAttackTechnique(Stance usableStance, Stance resultingStance, String name, int staminaCost, int stabilityCost, TechniqueHeight height) {
		this(usableStance, resultingStance, name, staminaCost, stabilityCost, null, null, height);
	}
	
	public NonAttackTechnique(Stance usableStance, Stance resultingStance, String name, int staminaCost, int stabilityCost, Stance forceStance, String setDamage) {
		this(usableStance, resultingStance, name, staminaCost, stabilityCost, forceStance, setDamage, TechniqueHeight.NONE);
	}

	public NonAttackTechnique(Stance usableStance, Stance resultingStance, String name, int staminaCost, int stabilityCost, Stance forceStance, TechniqueHeight height) {
		this(usableStance, resultingStance, name, staminaCost, stabilityCost, forceStance, null, height);
	}

	public NonAttackTechnique(Stance usableStance, Stance resultingStance, String name, int staminaCost, int stabilityCost, Stance forceStance, String setDamage, TechniqueHeight height) {
		super(usableStance, resultingStance, name);
		this.staminaCost = staminaCost;
		this.stabilityCost = stabilityCost;
		setForceStance(forceStance);
		this.setDamage = setDamage != null;
		this.height = height;
	}	
}

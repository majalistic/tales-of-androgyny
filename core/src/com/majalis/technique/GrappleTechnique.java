package com.majalis.technique;

import com.majalis.character.AbstractCharacter.Stance;

public class GrappleTechnique extends TechniquePrototype{

	public GrappleTechnique(Stance usableStance, Stance resultingStance, String name, int staminaCost){
		this(usableStance, resultingStance, name, staminaCost, null);
	}
	
	public GrappleTechnique(Stance usableStance, Stance resultingStance, String name, int staminaCost, Stance forceStance){
		this(usableStance, resultingStance, name, staminaCost, forceStance, null);
	}

	public GrappleTechnique(Stance usableStance, Stance resultingStance, String name, int staminaCost, Stance forceStance, TechniqueHeight height) {
		super(usableStance, resultingStance, name);
		this.grapple = true;
		this.staminaCost = staminaCost;
		this.forceStance = forceStance;
		this.height = height;
	}
}

package com.majalis.Technique;

import com.majalis.character.AbstractCharacter.Stance;

public class GrappleTechnique extends TechniquePrototype{

	public GrappleTechnique(Stance resultingStance, String name, int staminaCost){
		this(resultingStance, name, staminaCost, null);
	}
	
	public GrappleTechnique(Stance resultingStance, String name, int staminaCost, Stance forceStance){
		this(resultingStance, name, staminaCost, forceStance, null);
	}

	public GrappleTechnique(Stance resultingStance, String name, int staminaCost, Stance forceStance, TechniqueHeight height) {
		super(resultingStance, name);
		this.grapple = true;
		this.staminaCost = staminaCost;
		this.forceStance = forceStance;
		this.height = height;
	}

}

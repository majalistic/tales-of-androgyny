package com.majalis.technique;

import com.majalis.character.Stance;
import com.majalis.technique.TechniquePrototype.TechniqueHeight;

public class GrappleTechnique extends TechniqueBuilder {

	private String description;
	public GrappleTechnique(Stance usableStance, Stance resultingStance, String name, int staminaCost) {
		this(usableStance, resultingStance, name, staminaCost, null);
	}
	
	public GrappleTechnique(Stance usableStance, Stance resultingStance, String name, int staminaCost, Stance forceStance) {
		this(usableStance, resultingStance, name, staminaCost, forceStance, TechniqueHeight.NONE);
	}

	public GrappleTechnique(Stance usableStance, Stance resultingStance, String name, int staminaCost, Stance forceStance, String description) {
		this(usableStance, resultingStance, name, staminaCost, forceStance, TechniqueHeight.NONE, description);
	}
	
	public GrappleTechnique(Stance usableStance, Stance resultingStance, String name, int staminaCost, Stance forceStance, TechniqueHeight height) {
		this(usableStance, resultingStance, name, staminaCost, forceStance, height, "");
	}
	
	public GrappleTechnique(Stance usableStance, Stance resultingStance, String name, int staminaCost, Stance forceStance, TechniqueHeight height, String description) {
		super(usableStance, resultingStance, name);
		this.grapple = true;
		this.staminaCost = staminaCost;
		this.forceStance = forceStance;
		this.height = height;
		this.description = description;
	}
	
	@Override public String getDescription() {
		return (description.equals("") ? "" : description + "\n") + super.getDescription();
	}
}

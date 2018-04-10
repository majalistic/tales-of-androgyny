package com.majalis.technique;

import com.majalis.character.GrappleType;
import com.majalis.character.Stance;
import com.majalis.technique.TechniquePrototype.TechniqueHeight;

public class GrappleTechnique extends TechniqueBuilder {

	private String description;
	public GrappleTechnique(Stance usableStance, Stance resultingStance, String name, int staminaCost) {
		this(usableStance, resultingStance, name, staminaCost, null, "");
	}
	
	public GrappleTechnique(Stance usableStance, Stance resultingStance, String name, int staminaCost,  GrappleType grapple) {
		this(usableStance, resultingStance, name, staminaCost, null, grapple);
	}
	
	public GrappleTechnique(Stance usableStance, Stance resultingStance, String name, int staminaCost, Stance forceStance) {
		this(usableStance, resultingStance, name, staminaCost, forceStance, TechniqueHeight.NONE);
	}
	
	public GrappleTechnique(Stance usableStance, Stance resultingStance, String name, int staminaCost, Stance forceStance, GrappleType grapple) {
		this(usableStance, resultingStance, name, staminaCost, forceStance, TechniqueHeight.NONE, grapple, "");
	}

	public GrappleTechnique(Stance usableStance, Stance resultingStance, String name, int staminaCost, String description) {
		this(usableStance, resultingStance, name, staminaCost, null, TechniqueHeight.NONE, description);
	}
	
	public GrappleTechnique(Stance usableStance, Stance resultingStance, String name, int staminaCost, Stance forceStance, String description) {
		this(usableStance, resultingStance, name, staminaCost, forceStance, TechniqueHeight.NONE, description);
	}
	
	public GrappleTechnique(Stance usableStance, Stance resultingStance, String name, int staminaCost, Stance forceStance, TechniqueHeight height) {
		this(usableStance, resultingStance, name, staminaCost, forceStance, height, "");
	}
	
	public GrappleTechnique(Stance usableStance, Stance resultingStance, String name, int staminaCost, Stance forceStance, TechniqueHeight height, String description) {
		this(usableStance, resultingStance, name, staminaCost, forceStance, height, GrappleType.HOLD, description);
	}
	
	public GrappleTechnique(Stance usableStance, Stance resultingStance, String name, int staminaCost, Stance forceStance, TechniqueHeight height, GrappleType grapple, String description) {
		super(usableStance, resultingStance, name);
		this.grapple = grapple;
		this.staminaCost = staminaCost;
		setForceStance(forceStance);
		this.height = height;
		this.description = description;
	}
	
	@Override public String getDescription() {
		return (description.equals("") ? "" : description + "\n") + super.getDescription();
	}
}

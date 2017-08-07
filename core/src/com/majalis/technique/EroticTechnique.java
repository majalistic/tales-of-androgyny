package com.majalis.technique;

import com.majalis.character.Stance;

public class EroticTechnique extends NonAttackTechnique {

	private final String description;
	public EroticTechnique(Stance usableStance, Stance resultingStance, String name, int staminaCost, int stabilityCost, String description) {
		this(usableStance, resultingStance, name, staminaCost, stabilityCost, description, false);
	}
	
	public EroticTechnique(Stance usableStance, Stance resultingStance, String name, int staminaCost, int stabilityCost, String description, boolean isTaunt) {
		super(usableStance, resultingStance, name, staminaCost, stabilityCost);
		this.description = description;
		this.isTaunt = isTaunt;
	}
	
	@Override public String getDescription() {
		return description + "\n" + super.getDescription();
	}
}

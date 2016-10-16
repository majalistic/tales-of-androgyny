package com.majalis.technique;

import com.majalis.character.AbstractCharacter.Stance;

public class EroticTechnique extends NonAttackTechnique {

	private final String description;
	public EroticTechnique(Stance usableStance, Stance resultingStance, String name, int staminaCost, int stabilityCost, String description) {
		super(usableStance, resultingStance, name, staminaCost, stabilityCost);
		this.description = description;
	}
	
	@Override public String getDescription() {
		return description + super.getDescription();
	}
	

}

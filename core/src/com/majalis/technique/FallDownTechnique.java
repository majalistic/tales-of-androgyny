package com.majalis.technique;

import com.majalis.character.AbstractCharacter.Stance;

public class FallDownTechnique extends TechniqueBuilder{

	public FallDownTechnique(Stance usableStance, Stance resultingStance, String name) {
		super(usableStance, resultingStance, name);
		selfTrip = true;
	}
}

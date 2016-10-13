package com.majalis.technique;

import com.majalis.character.AbstractCharacter.Stance;

public class FallDownTechnique extends TechniquePrototype{

	public FallDownTechnique(Stance resultingStance, String name) {
		super(resultingStance, name);
		selfTrip = true;
	}
}

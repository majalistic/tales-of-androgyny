package com.majalis.technique;

import com.majalis.character.AbstractCharacter.Stance;

public class ClimaxTechnique extends TechniquePrototype {

	public ClimaxTechnique(Stance resultingStance, String name, Stance forceStance) {
		super(resultingStance, name);
		this.isClimax = true;
		this.forceStance = forceStance;
	}
}
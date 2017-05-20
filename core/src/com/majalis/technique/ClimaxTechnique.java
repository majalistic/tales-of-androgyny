package com.majalis.technique;

import com.majalis.character.Stance;

public class ClimaxTechnique extends TechniqueBuilder {

	public ClimaxTechnique(Stance usableStance, Stance resultingStance, String name, Stance forceStance, ClimaxType type) {
		super(usableStance, resultingStance, name);
		this.climaxType = type;
		this.forceStance = forceStance;
	}
	
	public enum ClimaxType {
		ANAL,
		ORAL,
		FACIAL,
		BACKWASH, // cum on back
		ANAL_RECEPTIVE, // cum while penetrated
		ORAL_RECEPTIVE
	}
}
package com.majalis.technique;

import com.majalis.character.AbstractCharacter.Stance;

public class GuardTechnique extends NonAttackTechnique{

	public GuardTechnique(Stance usableStance, Stance resultingStance, String name, int staminaCost, int stabilityCost) {
		super(usableStance, resultingStance, name, staminaCost, stabilityCost);
		guardMod = 100;
	}
}

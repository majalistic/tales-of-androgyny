package com.majalis.Technique;

import com.majalis.character.AbstractCharacter.Stance;

public class GuardTechnique extends NonAttackTechnique{

	public GuardTechnique(Stance resultingStance, String name, int staminaCost, int stabilityCost) {
		super(resultingStance, name, staminaCost, stabilityCost);
		guardMod = 100;
	}
}

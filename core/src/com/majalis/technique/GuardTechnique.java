package com.majalis.technique;

import com.majalis.character.Stance;

public class GuardTechnique extends NonAttackTechnique{

	public GuardTechnique(Stance usableStance, Stance resultingStance, String name, int staminaCost, int stabilityCost, int power, boolean guard) {
		super(usableStance, resultingStance, name, staminaCost, stabilityCost);
		if (guard) {
			guardMod = power;
		}
		else {
			parryMod = power;
		}
	}
}

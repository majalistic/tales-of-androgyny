package com.majalis.technique;

import com.majalis.character.AbstractCharacter.Stance;

public class SpellTechnique extends TechniquePrototype {

	public SpellTechnique(Stance resultingStance, String name, int powerMod, int manaCost, boolean heal) {
		super(resultingStance, name);
		isSpell = true;
		blockable = false;
		this.powerMod = powerMod;
		this.manaCost = manaCost;
		doesDamage = !heal;
		doesHealing = heal;
	}
}

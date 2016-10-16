package com.majalis.technique;

import com.majalis.character.AbstractCharacter.Stance;
import com.majalis.character.StatusType;

public class SpellTechnique extends TechniquePrototype {

	public SpellTechnique(Stance usableStance, Stance resultingStance, String name, int powerMod, int manaCost, boolean heal) {
		this(usableStance, resultingStance, name, powerMod, manaCost, heal, null);
	}

	public SpellTechnique(Stance usableStance, Stance resultingStance, String name, int powerMod, int manaCost, boolean heal, StatusType buff) {
		super(usableStance, resultingStance, name);
		isSpell = true;
		blockable = false;
		this.powerMod = powerMod;
		this.manaCost = manaCost;
		doesDamage = !heal && buff == null;
		doesHealing = heal;
		this.buff = buff;
	}
}

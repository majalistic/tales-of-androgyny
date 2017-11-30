package com.majalis.technique;

import com.majalis.character.Stance;
import com.majalis.character.StatusType;

public class SpellTechnique extends TechniqueBuilder {

	public SpellTechnique(Stance usableStance, Stance resultingStance, String name, int powerMod, int manaCost, boolean heal) {
		this(usableStance, resultingStance, name, powerMod, manaCost, heal, null);
	}

	public SpellTechnique(Stance usableStance, Stance resultingStance, String name, int powerMod, int manaCost, boolean heal, StatusType selfEffect) {
		this(usableStance, resultingStance, name, powerMod, manaCost, heal, selfEffect, null);
	}
	
	public SpellTechnique(Stance usableStance, Stance resultingStance, String name, int powerMod, int manaCost, boolean heal, StatusType selfEffect, StatusType enemyEffect) {
		super(usableStance, resultingStance, name);
		isSpell = true;
		blockable = false;
		this.powerMod = powerMod;
		this.manaCost = manaCost;
		doesDamage = !heal && selfEffect == null && enemyEffect == null;
		doesHealing = heal;
		this.selfEffect = selfEffect;
		this.enemyEffect = enemyEffect;
	}
}

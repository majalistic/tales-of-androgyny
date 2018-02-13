package com.majalis.technique;

import com.majalis.character.Stance;
import com.majalis.character.StatusType;

public class SpellTechnique extends TechniqueBuilder {
	public SpellTechnique(Stance usableStance, Stance resultingStance, String name, int powerMod, int manaCost, SpellEffect effect) {
		this(usableStance, resultingStance, name, powerMod, manaCost, effect, null);
	}

	public SpellTechnique(Stance usableStance, Stance resultingStance, String name, int powerMod, int manaCost, SpellEffect effect, StatusType selfEffect) {
		this(usableStance, resultingStance, name, powerMod, manaCost, effect, selfEffect, null);
	}
	
	public SpellTechnique(Stance usableStance, Stance resultingStance, String name, int powerMod, int manaCost, SpellEffect effect, StatusType selfEffect, StatusType enemyEffect) {
		super(usableStance, resultingStance, name);
		blockable = false;
		this.powerMod = powerMod;
		this.manaCost = manaCost;
		this.spellEffect = effect;
		doesDamage = effect == SpellEffect.DAMAGE;
		doesHealing = effect == SpellEffect.HEALING;
		this.selfEffect = selfEffect;
		this.enemyEffect = enemyEffect;
	}
}

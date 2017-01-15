package com.majalis.technique;

import com.majalis.character.AbstractCharacter.Stance;
import com.majalis.technique.TechniquePrototype.TechniqueHeight;
import com.majalis.technique.Bonus.BonusCondition;
import com.majalis.technique.Bonus.BonusType;
public class AttackTechnique extends TechniqueBuilder {
	public AttackTechnique(Stance usableStance, Stance resultingStance, String name, int powerMod, int staminaCost, int stabilityCost){
		this(usableStance, resultingStance, name, powerMod, staminaCost, stabilityCost, TechniqueHeight.MEDIUM);
	}
	public AttackTechnique(Stance usableStance, Stance resultingStance, String name, int powerMod, int staminaCost, int stabilityCost, TechniqueHeight height){
		this(usableStance, resultingStance, name, powerMod, staminaCost, stabilityCost, true, height);
	}
	public AttackTechnique(Stance usableStance, Stance resultingStance, String name, int powerMod, int staminaCost, int stabilityCost, boolean isBlockable) {
		this(usableStance, resultingStance, name, powerMod, staminaCost, stabilityCost, isBlockable, TechniqueHeight.MEDIUM);
	}
	
	public AttackTechnique(Stance usableStance, Stance resultingStance, String name, int powerMod, int staminaCost, int stabilityCost, double knockdown) {
		this(usableStance, resultingStance, name, powerMod, staminaCost, stabilityCost, knockdown, true, TechniqueHeight.MEDIUM);
	}
	
	public AttackTechnique(Stance usableStance, Stance resultingStance, String name, int powerMod, int staminaCost, int stabilityCost, boolean isBlockable, TechniqueHeight height){
		this(usableStance, resultingStance, name, powerMod, staminaCost, stabilityCost, 1, isBlockable, height);
	}
	
	public AttackTechnique(Stance usableStance, Stance resultingStance, String name, int powerMod, int staminaCost, int stabilityCost, double knockdown, boolean isBlockable, TechniqueHeight height){
		this(usableStance, resultingStance, name, powerMod, staminaCost, stabilityCost, knockdown, 0, isBlockable, height);
	}
	
	public AttackTechnique(Stance usableStance, Stance resultingStance, String name, int powerMod, int staminaCost, int stabilityCost, double knockdown, int gutCheck, boolean isBlockable, TechniqueHeight height){
		this(usableStance, resultingStance, name, powerMod, staminaCost, stabilityCost, knockdown, 1, gutCheck, isBlockable, height);
	}
	
	public AttackTechnique(Stance usableStance, Stance resultingStance, String name, int powerMod, int staminaCost, int stabilityCost, double knockdown, int armorSunder, int gutCheck, boolean isBlockable, TechniqueHeight height){
		super(usableStance, resultingStance, name);
		this.doesDamage = true;
		this.blockable = true;
		this.powerMod = powerMod;
		this.staminaCost = staminaCost;
		this.stabilityCost = stabilityCost;
		this.knockdown = knockdown;
		this.armorSunder = armorSunder;
		this.gutCheck = gutCheck;
		this.blockable = isBlockable;
		this.height = height;
		bonuses.put(BonusCondition.SKILL_LEVEL, new Bonus(BonusCondition.SKILL_LEVEL, BonusType.POWER_MOD, 1));
	}
}
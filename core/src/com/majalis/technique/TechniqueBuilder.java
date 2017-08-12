package com.majalis.technique;

import com.majalis.character.StatusType;
import com.badlogic.gdx.utils.OrderedMap;
import com.majalis.character.GrappleType;
import com.majalis.character.Stance;
import com.majalis.technique.Bonus.BonusCondition;
import com.majalis.technique.Bonus.BonusType;
import com.majalis.technique.ClimaxTechnique.ClimaxType;
import com.majalis.technique.TechniquePrototype.TechniqueHeight;

public class TechniqueBuilder {
	
	protected Stance usableStance;
	protected Stance resultingStance;
	protected String name;
	protected boolean doesDamage;
	protected boolean doesHealing;
	protected boolean isSpell;
	protected boolean isTaunt;
	protected int powerMod;
	protected int staminaCost;
	protected int stabilityCost;
	protected int manaCost;
	protected double knockdown;
	protected int armorSunder;
	protected int gutCheck;
	protected int guardMod;
	protected int parryMod;
	protected Stance forceStance;
	protected TechniqueHeight height;
	protected boolean setDamage;
	protected boolean blockable;
	protected GrappleType grapple;
	protected ClimaxType climaxType;
	protected StatusType buff;
	protected OrderedMap<BonusCondition, Bonus> bonuses;
	
	public TechniqueBuilder(Stance usableStance, Stance resultingStance, String name) {
		this.usableStance = usableStance;
		this.resultingStance = resultingStance;
		this.name = name;
		doesDamage = false;
		doesHealing = false;
		powerMod = 0;
		staminaCost = 0;
		stabilityCost = 0;
		manaCost = 0;
		isSpell = false;
		isTaunt = false;
		forceStance = null;
		knockdown = 0;
		armorSunder = 0;
		gutCheck = 0;
		height = null;
		guardMod = 0;
		parryMod = 0;
		setDamage = false;
		blockable = false;
		grapple = GrappleType.NULL;
		buff = null;
		height = TechniqueHeight.NONE;
		bonuses = new OrderedMap<BonusCondition, Bonus>();
	}
	
	public TechniqueBuilder addBonus(BonusCondition condition, BonusType type) {
		return addBonus(condition, type, 1);
	}
	
	public TechniqueBuilder addBonus(BonusCondition condition, BonusType type, int amount) {
		Bonus bonus = bonuses.get(condition, new Bonus(condition, type, amount));
		bonus.getBonusMap().put(type,  amount);
		bonuses.put(condition, bonus);
		return this;
	}

	public TechniqueBuilder setStamDam(int stamDam) {
		gutCheck = stamDam;
		return this;
	}
	
	public TechniqueBuilder setSeduce() {
		isTaunt = true;
		return this;
	}
	
	
	public TechniquePrototype build() {
		String lightDescription = getDescription();
		return new TechniquePrototype(usableStance, resultingStance, name, doesDamage, doesHealing, powerMod, staminaCost, stabilityCost, manaCost, isSpell, isTaunt, forceStance, knockdown, armorSunder, gutCheck, height, guardMod, parryMod, setDamage, blockable, grapple, climaxType, buff, getStanceInfo() + lightDescription, lightDescription, getBonusInfo(), bonuses); 
	}	
	
	protected String getStanceInfo() { 
		StringBuilder builder = new StringBuilder();
		builder.append("Usable in " + usableStance.toString() + " stance.\n");
		builder.append("Results in " + resultingStance.toString() + " stance.\n");
		return builder.toString();
	}
	
	protected String getBonusInfo() {
		StringBuilder builder = new StringBuilder();
		for (OrderedMap.Entry<BonusCondition, Bonus> bonus : bonuses.entries()) {
			builder.append(bonus.key.getDescription() + "\n");
			builder.append(bonus.value.getDescription());
		}
		return builder.toString();
	}
	
	protected String getDescription() {
		StringBuilder builder = new StringBuilder();
		if (doesDamage) {
			builder.append("Deals" + (powerMod > 0 ? " +" + powerMod : powerMod < 0 ? " " + powerMod : "") + " damage, improved by " + (isSpell ? "Magic" : "Strength") + ".\n");
		}
		if (doesHealing) {
			builder.append("Heals user with a power of " + powerMod + ", improved by Magic.\n");
		}
		if (buff != null) {
			builder.append("Increases Strength dramatically, erodes - improved by Magic.\n");
		}
		if (isTaunt) {
			builder.append("Taunts, angering and/or arousing the\n enemy with a power of " + powerMod + ", improved by Charisma.\n");
		}
		if (blockable) {
			builder.append("Can be blocked.\n");
		}
		else if (doesDamage) {
			builder.append("CANNOT be blocked.\n");
		}
		if (doesDamage && (isSpell || setDamage)) {
			builder.append("Ignores armor.\n");
		}
		if (guardMod > 0) {
			builder.append("Blocks against enemy attacks\nwith " + guardMod + "% effectiveness.\n");
		}
		if (parryMod > 0) {
			builder.append("Parries enemy attacks\nwith " + guardMod + "% effectiveness.\n");
		}
		if (staminaCost > 0) {
			builder.append("Costs " + staminaCost + " stamina, reduced by Endurance.\n");
		}
		else if (staminaCost < 0) {
			builder.append("Recovers " + -staminaCost + " stamina, improved by Endurance.\n");
		}
		if (stabilityCost > 0) {
			builder.append("Causes " + stabilityCost + " instability, reduced by Agility.\n");			
		}
		if (manaCost > 0) {
			builder.append("Costs " + manaCost + " mana.\n");			
		}
		if (height != TechniqueHeight.NONE) {
			builder.append(height.toString() + "-height attack.\n");
		}
		if (forceStance != null) {
			builder.append("Forces enemy into " + forceStance.toString() + " stance.\n");
		}
		if (knockdown > 0) {
			builder.append("Causes " + (knockdown > 1.6 ? "heavy" : knockdown > 1.1 ? "medium" : "light") + " knockdown.\n");
		}
		if (armorSunder > 0) {
			builder.append("Causes " + (armorSunder > 1.6 ? "heavy" : armorSunder > 1.1 ? "medium" : "light") + " armor sundering.\n");
		}
		if (gutCheck > 0) {
			builder.append("Causes " + (armorSunder > 1.6 ? "heavy" : armorSunder > 1.1 ? "medium" : "light") + " enemy stamina destruction.\n");
		}
		
		return builder.toString();
	}
}

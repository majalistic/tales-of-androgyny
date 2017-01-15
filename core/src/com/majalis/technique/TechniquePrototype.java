package com.majalis.technique;

import com.badlogic.gdx.utils.ObjectMap;
import com.majalis.character.AbstractCharacter.Stance;
import com.majalis.character.StatusType;
import com.majalis.technique.ClimaxTechnique.ClimaxType;
import com.majalis.technique.Bonus;
import com.majalis.technique.Bonus.BonusCondition;

public class TechniquePrototype {
	private final Stance usableStance;
	private final Stance resultingStance;
	private final String name;
	private final boolean doesDamage;
	private final boolean doesHealing;
	private final int powerMod;
	private final int staminaCost;
	private final int stabilityCost;
	private final int manaCost;
	private final boolean isSpell;
	private final boolean isTaunt;
	private final Stance forceStance;
	private final double knockdown;
	private final int armorSunder;
	private final int gutCheck;
	private final TechniqueHeight height;
	private final int guardMod;
	private final int parryMod;
	private final boolean setDamage;
	private final boolean blockable;
	private final boolean grapple;
	private final ClimaxType climaxType;
	private final StatusType buff;
	private final String description;
	private final String lightDescription;
	private final ObjectMap<BonusCondition, Bonus> bonuses;
	
	protected TechniquePrototype( Stance usableStance, Stance resultingStance, String name, boolean doesDamage, boolean doesHealing, int powerMod, int staminaCost, int stabilityCost, int manaCost, boolean isSpell, boolean isTaunt, Stance forceStance, double knockdown, int armorSunder,
			int gutCheck, TechniqueHeight height, int guardMod, int parryMod, boolean setDamage, boolean blockable, boolean grapple, ClimaxType climaxType, StatusType buff, String description, String lightDescription, ObjectMap<BonusCondition, Bonus> bonuses) {
		this.usableStance = usableStance;
		this.resultingStance = resultingStance;
		this.name = name;
		this.doesDamage = doesDamage;
		this.doesHealing = doesHealing;
		this.powerMod = powerMod;
		this.staminaCost = staminaCost;
		this.stabilityCost = stabilityCost;
		this.manaCost = manaCost;
		this.isSpell = isSpell;
		this.isTaunt = isTaunt;
		this.forceStance = forceStance;
		this.knockdown = knockdown;
		this.armorSunder = armorSunder;
		this.gutCheck = gutCheck;
		this.height = height;
		this.guardMod = guardMod;
		this.parryMod = parryMod;
		this.setDamage = setDamage;
		this.blockable = blockable;
		this.grapple = grapple;
		this.climaxType = climaxType;
		this.buff = buff;
		this.description = description;
		this.lightDescription = lightDescription;
		this.bonuses = bonuses;
	}
	
	public Stance getUsableStance(){ return usableStance; }
	public Stance getResultingStance(){ return resultingStance; }
	public String getName(){ return name; }
	public boolean isDamaging(){ return doesDamage; }
	public boolean isHealing(){ return doesHealing; }
	public int getPowerMod(){ return powerMod; }
	public boolean isSpell(){ return isSpell; }
	public int getStaminaCost(){ return staminaCost; }
	public int getStabilityCost(){ return stabilityCost; }
	public Stance getForceStance(){ return forceStance; }
	public double getKnockdown(){ return knockdown; }
	public int getArmorSunder() { return armorSunder; }
	public int getGutCheck(){ return gutCheck; }
	public int getManaCost(){ return manaCost; }
	public TechniqueHeight getTechniqueHeight(){ return height; }
	public int getGuardMod(){ return guardMod; }
	public int getParryMod() {	return parryMod; }
	public boolean doesSetDamage(){ return setDamage; }
	public boolean isBlockable() { return blockable; }
	public boolean isGrapple() { return grapple; }
	public boolean isTaunt(){ return isTaunt; }
	public ClimaxType getClimaxType() { return climaxType; }
	public StatusType getBuff() { return buff; }
	public String getDescription() { return description; }
	public String getLightDescription() { return lightDescription; }
	public ObjectMap<BonusCondition, Bonus> getBonuses() { return bonuses; }
	
	public enum TechniqueHeight{
		HIGH,
		MEDIUM,
		LOW,
		NONE
	}
}

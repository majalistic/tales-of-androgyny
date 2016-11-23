package com.majalis.technique;

import com.majalis.character.AbstractCharacter.Stance;
import com.majalis.character.StatusType;
import com.majalis.technique.ClimaxTechnique.ClimaxType;

public class TechniquePrototype {
	protected final Stance usableStance;
	protected final Stance resultingStance;
	protected final String name;
	protected final boolean doesDamage;
	protected final boolean doesHealing;
	protected final int powerMod;
	protected final int staminaCost;
	protected final int stabilityCost;
	protected final int manaCost;
	protected final boolean isSpell;
	protected final boolean isTaunt;
	protected final Stance forceStance;
	protected final double knockdown;
	protected final int armorSunder;
	protected final int gutCheck;
	protected final TechniqueHeight height;
	protected final int guardMod;
	protected final boolean causeBattleOver;
	protected final boolean setDamage;
	protected final boolean blockable;
	protected final boolean grapple;
	protected final ClimaxType climaxType;
	protected final boolean selfTrip;
	protected final StatusType buff;
	protected final String description;
	
	protected TechniquePrototype( Stance usableStance, Stance resultingStance, String name, boolean doesDamage, boolean doesHealing, int powerMod, int staminaCost, int stabilityCost, int manaCost, boolean isSpell, boolean isTaunt, Stance forceStance, double knockdown, int armorSunder,
			int gutCheck, TechniqueHeight height, int guardMod, boolean causeBattleOver, boolean setDamage, boolean blockable, boolean grapple, ClimaxType climaxType, boolean selfTrip, StatusType buff, String description) {
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
		this.causeBattleOver = causeBattleOver;
		this.setDamage = setDamage;
		this.blockable = blockable;
		this.grapple = grapple;
		this.climaxType = climaxType;
		this.selfTrip = selfTrip;
		this.buff = buff;
		this.description = description;
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
	public boolean causesBattleOver(){ return causeBattleOver; }
	public boolean doesSetDamage(){ return setDamage; }
	public boolean isBlockable() { return blockable; }
	public boolean isGrapple() { return grapple; }
	public boolean isTaunt(){ return isTaunt; }
	public ClimaxType getClimaxType() { return climaxType; }
	public boolean causesTrip() { return selfTrip; }
	public StatusType getBuff() { return buff; }
	public String getDescription() { return description; }
	
	public enum TechniqueHeight{
		HIGH,
		MEDIUM,
		LOW,
		NONE
	}
}

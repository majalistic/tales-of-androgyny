package com.majalis.technique;

import com.majalis.character.AbstractCharacter.Stance;

public abstract class TechniquePrototype {
	protected Stance resultingStance;
	protected String name;
	protected boolean doesDamage;
	protected boolean doesHealing;
	protected int powerMod;
	protected int staminaCost;
	protected int stabilityCost;
	protected int manaCost;
	protected boolean isSpell;
	protected boolean isTaunt;
	protected boolean selfTrip;
	protected Stance forceStance;
	protected int knockdown;
	protected TechniqueHeight height;
	protected int guardMod;
	protected boolean causeBattleOver;
	protected boolean setDamage;
	protected boolean blockable;
	protected boolean grapple;
	protected ClimaxType climaxType;
	
	protected TechniquePrototype(Stance resultingStance, String name){
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
		selfTrip = false;
		forceStance = null;
		knockdown = 0;
		height = TechniqueHeight.MEDIUM;
		guardMod = 0;
		causeBattleOver = false;
		setDamage = false;
		blockable = false;
		grapple = false;
	}
	
	public Stance getResultingStance(){ return resultingStance; }
	public String getName(){ return name; }
	public boolean isDamaging(){ return doesDamage; }
	public boolean isHealing(){ return doesHealing; }
	public int getPowerMod(){ return powerMod; }
	public boolean isSpell(){ return isSpell; }
	public int getStaminaCost(){ return staminaCost; }
	public int getStabilityCost(){ return stabilityCost; }
	public boolean causesTrip() { return selfTrip; }
	public Stance getForceStance(){ return forceStance; }
	public int getKnockdown(){ return knockdown; }
	public int getManaCost(){ return manaCost; }
	public TechniqueHeight getTechniqueHeight(){ return height; }
	public int getGuardMod(){ return guardMod; }
	public boolean causesBattleOver(){ return causeBattleOver; }
	public boolean doesSetDamage(){ return setDamage; }
	public boolean isBlockable() { return blockable; }
	public boolean isGrapple() { return grapple; }
	public boolean isTaunt(){ return isTaunt; }
	public ClimaxType getClimaxType() { return climaxType; }
	
	public enum TechniqueHeight{
		HIGH,
		MEDIUM,
		LOW
	}
}

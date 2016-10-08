package com.majalis.Technique;

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
	protected boolean selfTrip;
	protected Stance forceStance;
	protected Stance knockdownResultingStance;
	protected TechniqueHeight height;
	protected int guardMod;
	protected boolean causeBattleOver;
	protected boolean setDamage;
	
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
		selfTrip = false;
		forceStance = null;
		knockdownResultingStance = null;
		height = TechniqueHeight.MEDIUM;
		guardMod = 0;
		causeBattleOver = false;
		setDamage = false;
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
	public Stance getKnockdownResultingStance(){ return knockdownResultingStance; }
	public int getManaCost(){ return manaCost; }
	public TechniqueHeight getTechniqueHeight(){ return height; }
	public int getGuardMod(){ return guardMod; }
	public boolean causesBattleOver(){ return causeBattleOver; }
	public boolean doesSetDamage(){ return setDamage; }
	
	public enum TechniqueHeight{
		HIGH,
		MEDIUM,
		LOW
	}


}

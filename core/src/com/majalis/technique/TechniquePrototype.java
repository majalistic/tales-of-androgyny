package com.majalis.technique;

import com.majalis.character.AbstractCharacter.Stance;
import com.majalis.character.StatusType;
import com.majalis.technique.ClimaxTechnique.ClimaxType;

public abstract class TechniquePrototype {
	protected Stance usableStance;
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
	protected Stance forceStance;
	protected double knockdown;
	protected int armorSunder;
	protected int gutCheck;
	protected TechniqueHeight height;
	protected int guardMod;
	protected boolean causeBattleOver;
	protected boolean setDamage;
	protected boolean blockable;
	protected boolean grapple;
	protected ClimaxType climaxType;
	protected boolean selfTrip;
	protected StatusType buff;
	
	protected TechniquePrototype(Stance usableStance, Stance resultingStance, String name){
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
		causeBattleOver = false;
		setDamage = false;
		blockable = false;
		grapple = false;
		selfTrip = false;
		buff = null;
		height = TechniqueHeight.NONE;
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
	
	public String getDescription(){
		StringBuilder builder = new StringBuilder();
		builder.append("Usable in " + usableStance.toString() + " stance.\n");
		builder.append("Results in " + resultingStance.toString() + " stance.\n");
		if (doesDamage){
			builder.append("Deals" + (powerMod > 0 ? " +" + powerMod : powerMod < 0 ? " " + powerMod : "") + " damage, improved by " + (isSpell ? "Magic" : "Strength") + ".\n");
		}
		if (doesHealing){
			builder.append("Heals user with a power of " + powerMod + ", improved by Magic.\n");
		}
		if (buff != null){
			builder.append("Increases Strength dramatically, erodes - improved by Magic.\n");
		}
		if (isTaunt){
			builder.append("Taunts, angering and/or arousing the\n enemy with a power of " + powerMod + ", improved by Charisma.\n");
		}
		if (blockable){
			builder.append("Can be blocked.\n");
		}
		else if (doesDamage){
			builder.append("CANNOT be blocked.\n");
		}
		if (guardMod > 0){
			builder.append("Blocks against enemy attacks\nwith " + guardMod + "% effectiveness.\n");
		}
		if (staminaCost > 0){
			builder.append("Costs " + staminaCost + " stamina, reduced by Endurance.\n");
		}
		else if (staminaCost < 0){
			builder.append("Recovers " + -staminaCost + " stamina, improved by Endurance.\n");
		}
		if (stabilityCost > 0){
			builder.append("Causes " + stabilityCost + " instability, reduced by Agility.\n");			
		}
		if (manaCost > 0){
			builder.append("Costs " + manaCost + " mana.\n");			
		}
		if (height != TechniqueHeight.NONE){
			builder.append(height.toString() + "-height attack.\n");
		}
		if (forceStance != null){
			builder.append("Forces enemy into " + forceStance.toString() + " stance.\n");
		}
		if (knockdown > 0){
			builder.append("Causes " + (knockdown > 1.6 ? "heavy" : knockdown > 1.1 ? "medium" : "light") + " knockdown.\n");
		}
		if (armorSunder > 0){
			builder.append("Causes " + (armorSunder > 1.6 ? "heavy" : armorSunder > 1.1 ? "medium" : "light") + " armor sundering.\n");
		}
		if (gutCheck > 0){
			builder.append("Causes " + (armorSunder > 1.6 ? "heavy" : armorSunder > 1.1 ? "medium" : "light") + " enemy stamina destruction.\n");
		}
		
		return builder.toString();
	}
	
	public enum TechniqueHeight{
		HIGH,
		MEDIUM,
		LOW,
		NONE
	}
}

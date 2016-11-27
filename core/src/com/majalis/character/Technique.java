package com.majalis.character;

import java.util.Comparator;

import com.majalis.character.AbstractCharacter.Stance;
import com.majalis.character.Attack.Status;
import com.majalis.technique.TechniquePrototype;
import com.majalis.technique.TechniquePrototype.TechniqueHeight;
/*
 * Represents an action taken by a character in battle.  Will likely need a builder helper.
 */
public class Technique {
	private final TechniquePrototype technique;
	private final int strength;
	private final int block;
	private final Stance forceStance;
	private final boolean battleOver;
	
	protected Technique(TechniquePrototype techniquePrototype, int strength){
		this(techniquePrototype, strength, 0);
	}
	protected Technique(TechniquePrototype techniquePrototype, int strength, int block){
		this.technique = techniquePrototype;
		this.strength = strength;
		this.block = block + technique.getGuardMod();
		forceStance = technique.getForceStance();
		battleOver = technique.causesBattleOver();
	}
	
	public String getTechniqueName(){
		return technique.getName();
	}
	
	public String getTechniqueDescription(){
		return technique.getLightDescription();
	}
	
	public Attack resolve(Technique otherTechnique){
		int rand = (int) Math.floor(Math.random() * 100);
		double blockMod = otherTechnique.isBlockable() ? (getBlock() > rand * 2 ? 0 : getBlock() > rand ? .5 : 1) : 1;
		
		boolean isSuccessful = 
				technique.getTechniqueHeight() == TechniqueHeight.NONE ||
				(technique.getTechniqueHeight() == TechniqueHeight.HIGH && otherTechnique.getStance().receivesHighAttacks) || 
				(technique.getTechniqueHeight() == TechniqueHeight.MEDIUM && otherTechnique.getStance().receivesMediumAttacks) || 
				(technique.getTechniqueHeight() == TechniqueHeight.LOW && otherTechnique.getStance().receivesLowAttacks) 
				;
		// this is temporarily to prevent struggling from failing to work properly on the same term an eruption or knot happens
		boolean failure = false;
		if (isSuccessful) {
			isSuccessful = otherTechnique.getForceStance() == null || otherTechnique.getForceStance() == Stance.KNOTTED || otherTechnique.getForceStance() == Stance.KNEELING;
			failure = !isSuccessful;
		}
		
		return new Attack(
			isSuccessful ? Status.SUCCESS : failure ? Status.FAILURE : Status.MISS, 
			technique.getName(), 
			(int)(getDamage() * blockMod), 
			((int) ((strength + technique.getPowerMod()) * technique.getKnockdown()))/2, 
			((int)(getDamage() * blockMod) * technique.getArmorSunder() ) /4, 
			technique.getGutCheck() * (strength + technique.getPowerMod()), 
			technique.isHealing() ? strength + technique.getPowerMod() : 0,
			technique.isTaunt() ? strength + technique.getPowerMod() : 0, 
			technique.isGrapple() ? strength + technique.getPowerMod() : 0,
			technique.getClimaxType(), getForceStance(),
			technique.isSpell(),
			new Buff(technique.getBuff(), strength + technique.getPowerMod()),
			technique.isDamaging() && !technique.doesSetDamage()
		);
	}
	
	private boolean isBlockable() {
		return technique.isBlockable();
	}
	
	protected boolean isHeal(){
		return technique.isHealing();
	}
	
	protected boolean isSpell(){
		return technique.isSpell();
	}
	
	protected boolean isFallDown(){
		return technique.causesTrip();
	}
	
	protected int getDamage(){
		// can special case powerMod 100 = 0 here
		int damage = technique.doesSetDamage() ? 4 : technique.isDamaging() && technique.getGutCheck() == 0 ? strength + technique.getPowerMod() : 0;
		if (damage < 0) damage = 0;
		return damage;
	}	
	
	protected int getBlock(){
		return block;
	}

	public Stance getStance(){
		return technique.getResultingStance();
	}

	// right now this is a pass-through for technique.getStaminaCost() - could be modified by player (status effect that increases stamina cost, for instance)
	protected int getStaminaCost(){
		return technique.getStaminaCost();
	}
	
	protected int getStabilityCost(){
		return technique.getStabilityCost();
	}
	
	protected int getManaCost(){
		return technique.getManaCost();
	}
	
	protected Stance getForceStance(){
		return forceStance;
	}	
	
	protected boolean forceBattleOver(){
		return battleOver;
	}
	
	public static class StaminaComparator implements Comparator<Technique> {
		public int compare(Technique a, Technique b) {
	        return Integer.compare(a.getStaminaCost(), b.getStaminaCost());
	    }
	}
	
	public static class StabilityComparator implements Comparator<Technique> {
		public int compare(Technique a, Technique b) {
	        return Integer.compare(a.getStabilityCost(), b.getStabilityCost());
	    }
	}
}

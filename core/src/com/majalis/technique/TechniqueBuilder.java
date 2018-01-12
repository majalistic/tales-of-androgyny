package com.majalis.technique;

import com.majalis.character.StatusType;
import com.badlogic.gdx.utils.OrderedMap;
import com.majalis.character.GrappleType;
import com.majalis.character.SexualExperience.SexualExperienceBuilder;
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
	protected SexualExperienceBuilder sex;
	protected SexualExperienceBuilder selfSex;
	protected int powerMod;
	protected int staminaCost;
	protected int stabilityCost;
	protected int manaCost;
	protected double knockdown;
	protected int armorSunder;
	protected int gutCheck;
	protected int guardMod;
	protected int parryMod;
	private Stance forceStance;
	protected TechniqueHeight height;
	protected boolean ignoresArmor;
	protected boolean setDamage;
	protected boolean blockable;
	protected int setBleed;
	protected GrappleType grapple;
	protected ClimaxType climaxType;
	protected StatusType selfEffect;
	protected StatusType enemyEffect;
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
		sex = new SexualExperienceBuilder();
		selfSex = new SexualExperienceBuilder();
		forceStance = null;
		knockdown = 0;
		armorSunder = 0;
		gutCheck = 0;
		height = null;
		guardMod = 0;
		parryMod = 0;
		ignoresArmor = false;
		setDamage = false;
		blockable = false;
		setBleed = 0;
		grapple = GrappleType.NULL;
		selfEffect = null;
		enemyEffect = null;
		height = TechniqueHeight.NONE;
		bonuses = new OrderedMap<BonusCondition, Bonus>();
		switch(resultingStance.getClimaxType()) {
			case ANAL: selfSex.setAnalSexTop(1); break;
			case ANAL_RECEPTIVE: if (usableStance.getClimaxType() == ClimaxType.ANAL_RECEPTIVE) selfSex.setAnal(1); else selfSex.setAnalSex(1); break;
			case BACKWASH: selfSex.setAssBottomTeasing(1); break;
			case FACIAL: 
			case ORAL: selfSex.setOralSexTop(1); break;
			case ORAL_RECEPTIVE: if (usableStance.getClimaxType() == ClimaxType.ORAL_RECEPTIVE) selfSex.setOral(1); else selfSex.setOralSex(1); break;
			default: break;
		}
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
	
	public TechniqueBuilder setIgnoreArmor() {
		ignoresArmor = true;
		return this;
	}
	
	public TechniqueBuilder setAutoDamage() {
		setDamage = true;
		return this;
	}
	
	public TechniqueBuilder setBleed(int setBleed) {
		this.setBleed = setBleed;
		return this;
	}
	
	public TechniqueBuilder addSelfSex(SexualExperienceBuilder addedBuilder) {
		selfSex.combine(addedBuilder);
		return this;
	}
	
	public TechniqueBuilder addSex(SexualExperienceBuilder addedBuilder) {
		sex.combine(addedBuilder);
		return this;
	}
	
	protected TechniqueBuilder setForceStance(Stance forceStance) {
		if (forceStance == null) return this;
		this.forceStance = forceStance;
		if (forceStance.isAnalReceptive()) {
			addSex(new SexualExperienceBuilder().setAnalSex(1));
		}
		else if (forceStance.isAnalPenetration()) {
			addSex(new SexualExperienceBuilder().setAnalSexTop(1));
		}
		else if (forceStance.isOralReceptive()) {
			addSex(new SexualExperienceBuilder().setOralSex(1));
		}
		else if (forceStance.isOralPenetration()) {
			addSex(new SexualExperienceBuilder().setOralSexTop(1));
		}
		return this;
	}
	
	public TechniquePrototype build() {
		String lightDescription = getDescription();
		return new TechniquePrototype(usableStance, resultingStance, name, doesDamage, doesHealing, powerMod, staminaCost, stabilityCost, manaCost, isSpell, sex, selfSex, forceStance, knockdown, armorSunder, gutCheck, height, guardMod, parryMod, ignoresArmor, setDamage, blockable, setBleed, grapple, climaxType, selfEffect, enemyEffect, getStanceInfo() + lightDescription, lightDescription, getBonusInfo(), bonuses); 
	}	
	
	protected String getStanceInfo() { 
		StringBuilder builder = new StringBuilder();
		builder.append("Usable in " + usableStance.getLabel() + " stance.\n");
		builder.append("Results in " + resultingStance.getLabel() + " stance.\n");
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
		if (selfEffect != null) {
			builder.append("Increases Strength dramatically, erodes - improved by Magic.\n");
		}
		if (enemyEffect != null) {
			builder.append("Decreases Strength dramatically, duration improved by Magic.\n");
		}
			
		if (sex.isTeasing()) {
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
			builder.append("Blocks against enemy attacks with a shield,\nblocking " + (guardMod == 1 ? "a quarter of the damage" : guardMod == 2 ? "half damage" : guardMod == 3 ? "three-quarters of the damage" : guardMod >= 4 ? "all damage" : "") + ".\n");
		}
		if (parryMod > 0) {
			builder.append("Parries enemy attacks with a weapon,\npreventing " + (guardMod == 1 ? "a quarter of the damage" : guardMod == 2 ? "half damage" : guardMod == 3 ? "three-quarters of the damage" : guardMod >= 4 ? "all damage" : "") + ".\n");
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
			builder.append("Forces enemy into " + forceStance.getLabel() + " stance.\n");
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

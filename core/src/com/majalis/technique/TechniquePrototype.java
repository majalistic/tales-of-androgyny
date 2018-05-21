package com.majalis.technique;

import com.badlogic.gdx.utils.ObjectMap;
import com.majalis.character.Attack.AttackHeight;
import com.majalis.character.GrappleType;
import com.majalis.character.SexualExperience.SexualExperienceBuilder;
import com.majalis.character.Stance;
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
	private final SpellEffect spellEffect;
		
	private final SexualExperienceBuilder sex;
	private final SexualExperienceBuilder selfSex;
	
	private final Stance forceStance;
	private final double knockdown;
	private final int armorSunder;
	private final int gutCheck;
	private final TechniqueHeight height;
	private final int guardMod;
	private final int parryMod;
	private final int evadeMod;
	private final boolean ignoresArmor;
	private final boolean setDamage;
	private final boolean blockable;
	private final boolean parryable;
	private final boolean evadeable;
	private final boolean causesBleed;
	private final int setBleed;
	private final GrappleType grapple;
	private final ClimaxType climaxType;
	private final StatusType selfEffect;
	private final StatusType enemyEffect;
	private final int range;
	private final int advance;
	private final String description;
	private final String lightDescription;
	private final String bonusInfo;
	private final ObjectMap<BonusCondition, Bonus> bonuses;
	
	protected TechniquePrototype( Stance usableStance, Stance resultingStance, String name, boolean doesDamage, boolean doesHealing, int powerMod, int staminaCost, int stabilityCost, int manaCost, SpellEffect spellEffect, SexualExperienceBuilder sex, SexualExperienceBuilder selfSex, Stance forceStance, double knockdown, int armorSunder,
			int gutCheck, TechniqueHeight height, int guardMod, int parryMod, int evadeMod, boolean ignoresArmor, boolean setDamage, boolean blockable, boolean parryable, boolean evadeable, boolean causesBleed, int setBleed, GrappleType grapple, ClimaxType climaxType, StatusType selfEffect, StatusType enemyEffect, int range, int advance, String description, String lightDescription, String bonusInfo, ObjectMap<BonusCondition, Bonus> bonuses) {
		this.usableStance = usableStance;
		this.resultingStance = resultingStance;
		this.name = name;
		this.doesDamage = doesDamage;
		this.doesHealing = doesHealing;
		this.powerMod = powerMod;
		this.staminaCost = staminaCost;
		this.stabilityCost = stabilityCost;
		this.manaCost = manaCost;
		this.spellEffect = spellEffect;
		this.sex = sex;
		this.selfSex = selfSex;
		this.forceStance = forceStance;
		this.knockdown = knockdown;
		this.armorSunder = armorSunder;
		this.gutCheck = gutCheck;
		this.height = height;
		this.guardMod = guardMod;
		this.parryMod = parryMod;
		this.evadeMod = evadeMod;
		this.ignoresArmor = ignoresArmor;
		this.setDamage = setDamage;
		this.blockable = blockable;
		this.parryable = parryable;
		this.evadeable = evadeable;
		this.causesBleed = causesBleed;
		this.setBleed = setBleed;
		this.grapple = grapple;
		this.climaxType = climaxType;
		this.selfEffect = selfEffect;
		this.enemyEffect = enemyEffect;
		this.range = range;
		this.advance = advance;
		this.description = description;
		this.lightDescription = lightDescription;
		this.bonusInfo = bonusInfo;
		this.bonuses = bonuses;
	}
	public Stance getUsableStance() { return usableStance; }
	public Stance getResultingStance() { return resultingStance; }
	public String getName() { return name; }
	public boolean isDamaging() { return doesDamage; }
	public boolean isHealing() { return doesHealing; }
	public int getPowerMod() { return powerMod; }
	public boolean isSpell() { return spellEffect != null; }
	public SpellEffect getSpellEffect() { return spellEffect; }
	public int getStaminaCost() { return staminaCost; }
	public int getStabilityCost() { return stabilityCost; }
	public Stance getForceStance() { return forceStance; }
	public double getKnockdown() { return knockdown; }
	public int getArmorSunder() { return armorSunder; }
	public int getGutCheck() { return gutCheck; }
	public int getManaCost() { return manaCost; }
	public TechniqueHeight getTechniqueHeight() { return height; }
	public int getGuardMod() { return guardMod; }
	public int getParryMod() { return parryMod; }
	public int getEvadeMod() { return evadeMod; }
	public boolean ignoresArmor() { return ignoresArmor; }
	public boolean doesSetDamage() { return setDamage; }
	public boolean isBlockable() { return blockable; }
	public boolean isParryable() { return parryable; }
	public boolean isEvadeable() { return evadeable; }
	public boolean causesBleed() { return causesBleed;}
	public int getSetBleed() { return setBleed; }
	public GrappleType getGrappleType() { return grapple; }
	public SexualExperienceBuilder getSex() { return sex; }
	public SexualExperienceBuilder getSelfSex() { return selfSex; }
	public ClimaxType getClimaxType() { return climaxType; }
	public StatusType getSelfEffect() { return selfEffect; }
	public StatusType getEnemyEffect() { return enemyEffect; }
	public int getRange() { return range; }
	public int getAdvance() { return advance; }
	public boolean isMelee() { return range < 2; }
	public String getDescription() { return description; }
	public String getLightDescription() { return lightDescription; }
	public String getBonusInfo() { return bonusInfo; }
	public ObjectMap<BonusCondition, Bonus> getBonuses() { return bonuses; }
	
	public enum TechniqueHeight{
		HIGH,
		MEDIUM,
		LOW,
		HEAD,
		ARM,
		FOOT,
		NONE;
		
		public AttackHeight getAttackHeight() { return AttackHeight.valueOf(this.toString()); }
		public boolean isHigh() { return this == HIGH || this == HEAD; }
		public boolean isMedium() { return this == MEDIUM || this == ARM; }
		public boolean isLow() { return this == LOW || this == FOOT; }
	}
}

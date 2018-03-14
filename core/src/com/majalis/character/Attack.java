package com.majalis.character;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import com.majalis.character.Stance;
import com.majalis.save.MutationResult;
import com.majalis.technique.ClimaxTechnique.ClimaxType;
import com.majalis.technique.SpellEffect;
import com.majalis.technique.Bonus;

/*
 * Represents the result of an attack after it has been filtered through an opposing action.
 */
public class Attack {

	private final Status status;
	private final String name;
	private final int rawDamage;
	private final int blockAmount;
	private final int parryAmount;
	private final int evadeAmount;
	private final int magicResistance;
	private final int force;
	private final int rawArmorBreak;
	private final int gutcheck;
	private final int healing;
	private final SexualExperience sex;
	private final SexualExperience selfSex;
	private final GrappleStatus grapple;
	private final int disarm;
	private final int trip;
	private final int bleeding;
	private final int plugRemove;
	private final ClimaxType climaxType;
	private final int advance;
	private final Stance forceStance;
	private final SpellEffect spellEffect;
	private final Buff selfEffect;
	private final Buff enemyEffect;
	private final boolean isAttack;
	private final AttackHeight height;
	private final boolean ignoresArmor;
	private final Array<Bonus> bonuses;
	private final Item useItem;
	// this should be refactored to be passed in
	private final AbstractCharacter user;
	
	private final Array<String> results;
	private final Array<String> dialog;
	private final Array<MutationResult> attackerResults;
	private final Array<MutationResult> defenderResults;
	
	public enum Status {
		SUCCESS,
		PARRIED, // this attack was parried
		BLOCKED,
		MISSED,
		FAILURE, 
		FIZZLE, 
		EVADED // this attack was evaded
	}
	
	// this should have all the info for an attack, including damage or effects that were blocked
	protected Attack(Status status, String name, int rawDamage, int blockAmount, int parryAmount, int evadeAmount, int magicResistance, int force, int rawArmorBreak, int gutcheck, int healing, SexualExperience sex, SexualExperience selfSex, GrappleStatus grapple, int disarm, int trip, int bleeding, int plugRemove, ClimaxType climaxType, int advance, Stance forceStance, SpellEffect spellEffect, Buff selfEffect, Buff enemyEffect, 
					boolean isAttack, AttackHeight height, boolean ignoresArmor, Array<Bonus> bonuses, Item useItem, AbstractCharacter user) {
		this.status = status;
		this.name = name;
		this.rawDamage = rawDamage;
		this.blockAmount = blockAmount;
		this.parryAmount = parryAmount;
		this.evadeAmount = evadeAmount;
		this.magicResistance = magicResistance;
		this.force = force;
		this.rawArmorBreak = rawArmorBreak;
		this.gutcheck = gutcheck;
		this.healing = healing;
		this.sex = sex;
		this.selfSex = selfSex;
		this.grapple = grapple;
		this.disarm = disarm;
		this.trip = trip;
		this.bleeding = bleeding;
		this.plugRemove = plugRemove;
		this.climaxType = climaxType;
		this.advance = advance;
		this.forceStance = forceStance;
		this.spellEffect = spellEffect;
		this.selfEffect = selfEffect;
		this.enemyEffect = enemyEffect;
		this.isAttack = isAttack;
		this.height = height;
		this.ignoresArmor = ignoresArmor;
		this.bonuses = bonuses;
		this.useItem = useItem;
		this.user = user;

		this.results = new Array<String>();
		this.attackerResults = new Array<MutationResult>();
		this.defenderResults = new Array<MutationResult>();
		this.dialog = new Array<String>();
	}
	
	private int getNegatedAmount(int amount) { return amount - ( magicResistance > 0 ? getMagicResistanceReduction(amount) : (getBlockReduction(amount) + getParryReduction(amount) + getDodgeReduction(amount))); }
	private IntArray getAmountQuartiles(int amount) {
		IntArray quartiles = new IntArray(new int[]{0, 0, 0, 0});
		int ii = 0;
		while (amount > 0) {
			quartiles.incr(ii, 1);
			ii = (ii + 1) % 4;
			amount--;
		}
		return quartiles;
	}
	private int getBlockReduction(int amount) {
		IntArray quartiles = getAmountQuartiles(amount);
		int negated = 0;
		for (int ii = evadeAmount + parryAmount; ii < evadeAmount + parryAmount + blockAmount && ii < 4; ii++) { negated += quartiles.get(ii); }
		return negated; 
	}
	private int getParryReduction(int amount) {
		IntArray quartiles = getAmountQuartiles(amount);
		int negated = 0;
		for (int ii = evadeAmount; ii < evadeAmount + parryAmount && ii < 4; ii++) { negated += quartiles.get(ii); }
		return negated; 
	}
	private int getDodgeReduction(int amount) {
		IntArray quartiles = getAmountQuartiles(amount);
		int negated = 0;
		for (int ii = 0; ii < evadeAmount && ii < 4; ii++) { negated += quartiles.get(ii); }
		return negated; 
	}
	private int getMagicResistanceReduction(int amount) {
		IntArray quartiles = getAmountQuartiles(amount);
		int negated = 0;
		for (int ii = 0; ii < magicResistance && ii < 4; ii++) { negated += quartiles.get(ii); }
		return negated; 
	}
	
	protected String getName() { return name; }
	public boolean isAttack() { return isAttack; }	
	public AttackHeight getAttackHeight() { return height; }	
	public int getDamage() { return getNegatedAmount(rawDamage); } 
	protected int getForce() { return getNegatedAmount(force); }
	public int getArmorSunder() { return getNegatedAmount(rawDamage * rawArmorBreak); } 
	public int getBleeding() { return getNegatedAmount (bleeding); } // 
	public int getShieldDamage() { return getBlockReduction(rawDamage); } // figure out how much damage the shield blocked
	public int getMagicDamageReduction() { return magicResistance > 0 ? getMagicResistanceReduction(rawDamage) : 0; }
	public int getBlockAmount() { return blockAmount; }
	public int getParryAmount() { return parryAmount; }
	public int getEvadeAmount() { return evadeAmount; }
	protected String getUser() { return user.label; }
	protected int getGutCheck() { return gutcheck; }
	protected boolean isHealing() { return healing > 0; }
	protected int getHealing() { return healing; }
	protected SexualExperience getSex() { return sex; }
	protected SexualExperience getSelfSex() { return selfSex; }
	protected GrappleStatus getGrapple() { return grapple; }
	protected int getDisarm() { return disarm; }
	protected int getTrip() { return trip; }
	protected int getAdvance() { return advance; }
	protected Stance getForceStance() { return forceStance; }
	public boolean isSuccessful() { return status == Status.SUCCESS || status == Status.BLOCKED || status == Status.PARRIED; }
	public Status getStatus() { return status; }
	protected void addMessage(String message) { results.add(message); }
	protected Array<String> getMessages() { return results; }
	protected void addDialog(String message) { dialog.add(message); }
	protected Array<String> getDialog() { return dialog; }
	protected void addAttackerResults(Array<MutationResult> results) { attackerResults.addAll(results); }
	protected Array<MutationResult> getAttackerResults() { return attackerResults; }
	protected void addDefenderResults(Array<MutationResult> results) { defenderResults.addAll(results); }
	protected Array<MutationResult> getDefenderResults() { return defenderResults; }
	public boolean isClimax() { return climaxType != null; }
	protected ClimaxType getClimaxType() { return climaxType; }
	public boolean isSpell() { return spellEffect != null; }
	public SpellEffect getSpellEffect() { return spellEffect; }
	public Buff getSelfEffect() { return selfEffect == null ? null : selfEffect.type == null ? null : selfEffect; }
	public Buff getEnemyEffect() { return enemyEffect == null ? null : enemyEffect.type == null ? null : enemyEffect; }
	public Array<Bonus> getBonuses() { return bonuses != null ? bonuses : new Array<Bonus>(); }
	public boolean ignoresArmor() { return ignoresArmor; }
	public Item getItem() { return useItem; }
	public int getClimaxVolume() { return user.getClimaxVolume(); }
	public int plugRemove() { return plugRemove; }	

	public String getDescription() {
		String description = "";
		if (!isSuccessful()) { description += "This skill is predicted to be ineffective."; }
		if (getDamage() > 0) { description += "Predicted damage: " + getDamage() + "\n"; }
		if (getForce() > 0) { description += "Predicted force: " + getForce() + "\n"; }
		if (getArmorSunder() > 0) { description += "Predicted armor damage: " + getArmorSunder() + "\n"; }
		if (getBleeding() > 0) { description += "Predicted bleed: " + getBleeding() + "\n"; }
		if (isHealing()) { description += "Predicted self-healing: " + getHealing() + "\n"; }
		return description;
	}

	public enum AttackHeight {
		NONE,
		LOW,
		MEDIUM,
		HIGH
	}
}

package com.majalis.character;
import com.badlogic.gdx.utils.Array;
import com.majalis.character.AbstractCharacter.Stance;
import com.majalis.technique.ClimaxTechnique.ClimaxType;
import com.majalis.technique.Bonus;

/*
 * Represents the result of an attack after it has been filtered through an opposing action.
 */
public class Attack {

	private final Status status;
	private final String name;
	private final int rawDamage;
	private final double blockMod;
	private final int force;
	private final int rawArmorBreak;
	private final int gutcheck;
	private final int healing;
	private final int lust;
	private final int grapple;
	private final int disarm;
	private final int trip;
	private final int bleeding;
	private final ClimaxType climaxType;
	private final Stance forceStance;
	private final boolean isSpell;
	private final Buff buff;
	private final boolean isAttack;
	private final boolean ignoresArmor;
	private final Array<Bonus> bonuses;
	private final Item useItem;
	// this should be refactored to be passed in
	private final AbstractCharacter user;
	
	private final Array<String> results;
	private final Array<String> dialog;
	
	public enum Status {
		SUCCESS,
		PARRIED, // this attack was parried
		PARRY,   // this attack parried another attack
		BLOCKED,
		MISSED,
		FAILURE, 
		FIZZLE, 
		EVADED // this attack was evaded
	}
	
	// this should have all the info for an attack, including damage or effects that were blocked
	protected Attack(Status status, String name, int rawDamage, double blockMod, int force, int rawArmorBreak, int gutcheck, int healing, int lust, int grapple, int disarm, int trip, int bleeding, ClimaxType climaxType, Stance forceStance, boolean isSpell, Buff buff, boolean isAttack, boolean ignoresArmor, Array<Bonus> bonuses, Item useItem, AbstractCharacter user) {
		this.status = status;
		this.name = name;
		this.rawDamage = rawDamage;
		this.blockMod = blockMod;
		this.force = force;
		this.rawArmorBreak = rawArmorBreak;
		this.gutcheck = gutcheck;
		this.healing = healing;
		this.lust = lust;
		this.grapple = grapple;
		this.disarm = disarm;
		this.trip = trip;
		this.bleeding = bleeding;
		this.climaxType = climaxType;
		this.forceStance = forceStance;
		this.isSpell = isSpell;
		this.buff = buff;
		this.isAttack = isAttack;
		this.ignoresArmor = ignoresArmor;
		this.bonuses = bonuses;
		this.useItem = useItem;
		this.user = user;

		this.results = new Array<String>();
		this.dialog = new Array<String>();
	}
	
	protected String getName() {
		return name;
	}
	
	public boolean isAttack() {
		return isAttack;
	}
	
	protected String getUser() {
		return user.label;
	}
	
	protected int getDamage() {
		return (int) (rawDamage * blockMod);
	}
	
	protected int getForce() {
		return force;
	}
	
	protected int getGutCheck() {
		return gutcheck;
	}
	
	protected boolean isHealing() {
		return healing > 0;
	}
	
	protected int getHealing() {
		return healing;
	}
	
	protected int getLust() {
		return lust;
	}
	
	protected int getGrapple() {
		return grapple;
	}
	
	protected int getDisarm() {
		return disarm;
	}
	
	protected int getTrip() {
		return trip;
	}
	
	protected Stance getForceStance() {
		return forceStance;
	}
	
	public boolean isSuccessful() {
		return status == Status.SUCCESS || status == Status.PARRY || status == Status.BLOCKED;
	}
	
	public Status getStatus() {
		return status;
	}

	protected void addMessage(String message) {
		results.add(message);
	}
	
	protected Array<String> getMessages() {
		return results;
	}
	
	protected void addDialog(String message) {
		dialog.add(message);
	}

	protected Array<String> getDialog() {
		return dialog;
	}
	
	protected boolean isClimax() {
		return climaxType != null;
	}
	
	protected ClimaxType getClimaxType() {
		return climaxType;
	}

	public int getArmorSunder() {
		return (int)(rawDamage * rawArmorBreak * blockMod) / 4;
	}

	public boolean isSpell() {
		return isSpell;
	}
	
	public Buff getBuff() {
		return buff.type == null ? null : buff;
	}
	
	public Array<Bonus> getBonuses() {
		return bonuses;
	}

	public boolean ignoresArmor() {
		return ignoresArmor;
	}
	
	public Item getItem() {
		return useItem;
	}
	
	public int getClimaxVolume() {
		return user.getClimaxVolume();
	}
	
	public int getBleeding() {
		return (int) (bleeding * blockMod);
	}
	
}

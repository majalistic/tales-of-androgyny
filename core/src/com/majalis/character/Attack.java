package com.majalis.character;
import com.badlogic.gdx.utils.Array;
import com.majalis.character.AbstractCharacter.Stance;
import com.majalis.technique.ClimaxTechnique.ClimaxType;

/*
 * Represents the result of an attack after it has been filtered through an opposing action.
 */
public class Attack {

	private final boolean success;
	private final String name;
	private final int damage;
	private final int force;
	private final int armorBreak;
	private final int gutcheck;
	private final int healing;
	private final int lust;
	private final int grapple;
	private final ClimaxType climaxType;
	private final Stance forceStance;
	private final Array<String> results;
	private final boolean isSpell;
	private String user;
	protected Attack(boolean success, String name, int damage, int force, int armorBreak, int gutcheck, int healing, int lust, int grapple, ClimaxType climaxType, Stance forceStance, boolean isSpell){
		this.success = success;
		this.name = name;
		this.damage = damage;
		this.force = force;
		this.armorBreak = armorBreak;
		this.gutcheck = gutcheck;
		this.healing = healing;
		this.lust = lust;
		this.grapple = grapple;
		this.climaxType = climaxType;
		this.forceStance = forceStance;
		this.isSpell = isSpell;
		this.results = new Array<String>();
	}
	
	protected String getName(){
		return name;
	}
	
	protected void setUser(String user){
		this.user = user;
	}
	
	protected String getUser(){
		return user;
	}
	
	protected int getDamage(){
		return damage;
	}
	
	protected int getForce(){
		return force;
	}
	
	protected int getGutCheck(){
		return gutcheck;
	}
	
	protected boolean isHealing(){
		return healing > 0;
	}
	
	protected int getHealing(){
		return healing;
	}
	
	protected int getLust(){
		return lust;
	}
	
	protected int getGrapple(){
		return grapple;
	}
	
	protected Stance getForceStance(){
		return forceStance;
	}
	protected boolean isSuccessful(){
		return success;
	}

	protected void addMessage(String message) {
		results.add(message);
	}
	
	protected Array<String> getMessages(){
		return results;
	}

	protected boolean isClimax() {
		return climaxType != null;
	}
	
	protected ClimaxType getClimaxType(){
		return climaxType;
	}

	public int getArmorSunder() {
		return armorBreak;
	}

	public boolean isSpell() {
		return isSpell;
	}
}

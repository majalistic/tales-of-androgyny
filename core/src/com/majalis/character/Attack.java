package com.majalis.character;
import com.badlogic.gdx.utils.Array;
import com.majalis.character.AbstractCharacter.Stance;
import com.majalis.technique.ClimaxType;

/*
 * Represents the result of an attack after it has been filtered through an opposing action.
 */
public class Attack {

	private final boolean success;
	private final String name;
	private final int damage;
	private final int force;
	private final int healing;
	private final int lust;
	private final int grapple;
	private final ClimaxType climaxType;
	private final Stance forceStance;
	private final Array<String> results;
	private String user;
	protected Attack(boolean success, String name, int damage, int force, int healing, int lust, int grapple, ClimaxType climaxType, Stance forceStance){
		this.success = success;
		this.name = name;
		this.damage = damage;
		this.force = force;
		this.healing = healing;
		this.lust = lust;
		this.grapple = grapple;
		this.climaxType = climaxType;
		this.forceStance = forceStance;
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
	
}

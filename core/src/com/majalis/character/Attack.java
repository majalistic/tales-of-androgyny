package com.majalis.character;
import com.badlogic.gdx.utils.Array;
import com.majalis.character.AbstractCharacter.Stance;

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
	private final boolean isClimax;
	private final Stance forceStance;
	private final Array<String> results;
	private String user;
	public Attack(boolean success, String name, int damage, int force, int healing, int lust, int grapple, boolean isClimax, Stance forceStance){
		this.success = success;
		this.name = name;
		this.damage = damage;
		this.force = force;
		this.healing = healing;
		this.lust = lust;
		this.grapple = grapple;
		this.isClimax = isClimax;
		this.forceStance = forceStance;
		this.results = new Array<String>();
	}
	
	public String getName(){
		return name;
	}
	
	public void setUser(String user){
		this.user = user;
	}
	
	public String getUser(){
		return user;
	}
	
	public int getDamage(){
		return damage;
	}
	
	public int getForce(){
		return force;
	}
	
	public boolean isHealing(){
		return healing > 0;
	}
	
	public int getHealing(){
		return healing;
	}
	
	public int getLust(){
		return lust;
	}
	
	public int getGrapple(){
		return grapple;
	}
	
	public Stance getForceStance(){
		return forceStance;
	}
	public boolean isSuccessful(){
		return success;
	}

	public void addMessage(String message) {
		results.add(message);
	}
	
	public Array<String> getMessages(){
		return results;
	}

	public boolean isClimax() {
		return isClimax;
	}
}

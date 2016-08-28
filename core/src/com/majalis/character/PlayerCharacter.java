package com.majalis.character;

import com.badlogic.gdx.utils.IntArray;

/*
 * Contains the current player character's statistics, including "party" statistics like food remaining
 */
public class PlayerCharacter extends Character{

	@SuppressWarnings("unused")
	private PlayerCharacter(){}
	
	public PlayerCharacter(boolean defaultValues){
		super(defaultValues);
		if (defaultValues){
			baseCharisma = 6;
			healthTiers = new IntArray(new int[]{10});	
			currentHealth = getMaxHealth();
		}
	}
	
	// advantage, range, and combat-lock(boolean) are shared properties between two creatures
	
	/* out of battle only statistics */
	public int money;
	public int food;
	
	public int dignity;
	public int integrity;
	public int analIntegrity;
	public int lustForDick;
	public Femininity femininity;
	public Bootyliciousness bootyliciousness;
	public LipFullness lipFullness;
	
	/* anatomy - contains current and permanent properties */
	// public Hole hole;  // bowels contents, tightness, number of copulations, number of creampies, etc. 
	// public Mouth mouth; 
	// public Wiener wiener;	
	
	public enum Femininity {
		MALE,
		UNMASCULINE,
		EFFIMINATE,
		FEMALE,
		BITCH
	}
	
	public enum Bootyliciousness {
		
	}
	
	public enum LipFullness {
		POUTY
	}
}

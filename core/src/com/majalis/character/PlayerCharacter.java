package com.majalis.character;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
/*
 * Contains the current player character's statistics, including "party" statistics like food remaining
 */
public class PlayerCharacter extends AbstractCharacter implements Json.Serializable{

	@SuppressWarnings("unused")
	private PlayerCharacter(){}
	
	public PlayerCharacter(boolean defaultValues){
		super(defaultValues);
		if (defaultValues){
			label = "You";
			secondPerson = true;
			baseCharisma = 6;
			healthTiers = new IntArray(new int[]{10, 10, 10, 10});	
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
	
	
	public Technique getTechnique(AbstractCharacter target){
		// default neutral attack
		return getTechnique(Keys.S);
	}
	
	public Technique getTechnique(int keyPressed) {
		switch(stance){
			case OFFENSIVE:
				switch (keyPressed){
					case Keys.A:
						return new Technique(Techniques.STRONG_ATTACK, getStrength());	
					case Keys.S:
						return new Technique(Techniques.TEMPO_ATTACK, getStrength());	
					case Keys.D:	
						return new Technique(Techniques.RESERVED_ATTACK, getStrength());
				}
			case BALANCED:
				switch (keyPressed){
					case Keys.A:
						return new Technique(Techniques.SPRING_ATTACK, getStrength());	
					case Keys.S:
						return new Technique(Techniques.NEUTRAL_ATTACK, getStrength());	
					case Keys.D:	
						return new Technique(Techniques.CAUTIOUS_ATTACK, getStrength());
				}
			case DEFENSIVE:
				switch (keyPressed){
					case Keys.A:
						return new Technique(Techniques.REVERSAL_ATTACK, getStrength());	
					case Keys.S:
						return new Technique(Techniques.CAREFUL_ATTACK, getStrength());	
					case Keys.D:	
						return new Technique(Techniques.GUARD, getStrength());
				}
		}
		return null;
	}
	
	@Override
	public void write(Json json) {
		super.write(json);
		writeFields(json, this.getClass().getDeclaredFields());
	}
	@Override
	public void read(Json json, JsonValue jsonData) {
		super.read(json, jsonData);
	}
	
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

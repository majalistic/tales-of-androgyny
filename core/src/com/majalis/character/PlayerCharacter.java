package com.majalis.character;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.utils.Array;
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
	public int exp;
	
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
	
	// this needs to consolidate logic with the getTechniques method
	public Array<String> getPossibleTechniques(){
		switch(stance){
			case OFFENSIVE:
				return new Array<String>(true, new String[]{"Strong (A)", "Tempo (S)", "Reserved (D)"}, 0, 3);
			case BALANCED:
				return new Array<String>(true, new String[]{"Spring (A)", "Neutral (S)", "Cautious (D)"}, 0, 3);
			case DEFENSIVE:
				return new Array<String>(true, new String[]{"Reversal (A)", "Careful (S)", "Guard (D)"}, 0, 3);
			case PRONE:
			case SUPINE:
				return new Array<String>(true, new String[]{"Kip Up (A)", "Stand Up (S)", "Knee Up (D)", "Rest (F)"}, 0, 4);
			case KNEELING:
				return new Array<String>(true, new String[]{"Stand Up (A)"}, 0, 1);
			case DOGGY:
				return new Array<String>(true, new String[]{"Take It In The (A)"}, 0, 1);
			case KNOTTED:
				return new Array<String>(true, new String[]{"Take the Knot in the (AWOOGA)"}, 0, 1);
		}
		return null;
	}
	
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
			case PRONE:
			case SUPINE:
				switch (keyPressed){
					case Keys.A:
						return new Technique(Techniques.KIP_UP, getStrength());	
					case Keys.S:
						return new Technique(Techniques.STAND_UP, getStrength());	
					case Keys.D:
						return new Technique(Techniques.KNEE_UP, getStrength());
					case Keys.F:	
						return new Technique(Techniques.REST, getStrength());
					}	
			case KNEELING:
				return new Technique(Techniques.STAND_UP, getStrength());
			case DOGGY:
				return new Technique(Techniques.RECEIVE, getStrength());
			case KNOTTED:
				return new Technique(Techniques.RECEIVE_KNOT, getStrength());
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

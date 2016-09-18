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
	public Array<Technique> getPossibleTechniques(){
		switch(stance){
			case OFFENSIVE:
				return getTechniques(Techniques.STRONG_ATTACK, Techniques.TEMPO_ATTACK, Techniques.RESERVED_ATTACK);
			case BALANCED:
				return getTechniques(Techniques.SPRING_ATTACK, Techniques.NEUTRAL_ATTACK, Techniques.CAUTIOUS_ATTACK);
			case DEFENSIVE:
				return getTechniques(Techniques.REVERSAL_ATTACK, Techniques.CAREFUL_ATTACK, Techniques.GUARD);
			case PRONE:
			case SUPINE:
				return getTechniques(Techniques.KIP_UP, Techniques.STAND_UP, Techniques.KNEE_UP, stance == Stance.PRONE ? Techniques.REST_FACE_DOWN : Techniques.REST);
			case KNEELING:
				return getTechniques(Techniques.STAND_UP);
			case DOGGY:
				return getTechniques(Techniques.RECEIVE);
			case KNOTTED:
				return getTechniques(Techniques.RECEIVE_KNOT);
		}
		return null;
	}
	
	public Technique getTechnique(AbstractCharacter target){
		// this should be re-architected - player characters likely won't use this method
		return null;
	}
	
	public Array<Technique> getTechniques(Techniques... possibilities) {
		Array<Technique> possibleTechniques = new Array<Technique>();
		for (Techniques technique : possibilities){
			possibleTechniques.add(new Technique(technique, getStrength()));
		}
		return possibleTechniques;
	}
	
	public void refresh(){
		setStaminaToMax();
		setStabilityToMax();
		stance = Stance.BALANCED;
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

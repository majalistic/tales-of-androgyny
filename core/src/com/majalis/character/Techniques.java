package com.majalis.character;

import com.badlogic.gdx.utils.Array;
import com.majalis.Technique.TechniquePrototype;
import com.majalis.Technique.TechniquePrototype.TechniqueHeight;
import com.majalis.Technique.AttackTechnique;
import com.majalis.Technique.ClimaxTechnique;
import com.majalis.Technique.FallDownTechnique;
import com.majalis.Technique.GrappleTechnique;
import com.majalis.Technique.GuardTechnique;
import com.majalis.Technique.NonAttackTechnique;
import com.majalis.Technique.SpellTechnique;
import com.majalis.character.AbstractCharacter.Stance;
/*
 * List of all techniques and their generic attributes
 */
public enum Techniques {
	/* Offensive Techniques */  
	STRONG_ATTACK 		(new AttackTechnique(Stance.OFFENSIVE, "Strong Attack", 3, 3, 8)),
	TEMPO_ATTACK  		(new AttackTechnique(Stance.OFFENSIVE, "Tempo Attack", 2, 2, 6)),
	SPRING_ATTACK  		(new AttackTechnique(Stance.OFFENSIVE, "Spring Attack", 1, 4, 6)),
	/* Balanced Techniques */
	RESERVED_ATTACK  	(new AttackTechnique(Stance.BALANCED, "Reserved Attack", 1, 4, 2)),
	REVERSAL_ATTACK  	(new AttackTechnique(Stance.BALANCED, "Reversal Attack", 0, 2, 4)),
	NEUTRAL_ATTACK  	(new AttackTechnique(Stance.BALANCED, "Neutral Attack", 0, 1, 1)),
	/* Defensive Techniques */
	CAUTIOUS_ATTACK  	(new AttackTechnique(Stance.DEFENSIVE, "Cautious Attack", -1, 0, 2)),
	CAREFUL_ATTACK  	(new AttackTechnique(Stance.DEFENSIVE, "Careful Attack", 0, 0, 2)),
	GUARD  				(new GuardTechnique	(Stance.DEFENSIVE, "Guard", -1, 0)),
	SECOND_WIND			(new NonAttackTechnique(Stance.DEFENSIVE, "Second Wind", -1, -4)),
	/* Techniques from Prone/Supine */
	KIP_UP				(new NonAttackTechnique(Stance.BALANCED, "Kip Up", -5, 5)),
	STAND_UP			(new NonAttackTechnique(Stance.BALANCED, "Stand Up", -1, 2)),
	KNEE_UP				(new NonAttackTechnique(Stance.BALANCED, "Knee Up", -5, 1)),
	REST				(new NonAttackTechnique(Stance.SUPINE, "Rest", -1, -1)),
	REST_FACE_DOWN		(new NonAttackTechnique(Stance.PRONE, "Rest", -1, -1)),
	/* Out of resources */
	FALL_DOWN			(new FallDownTechnique(Stance.SUPINE, "Fall Down")), // run out of stamina
	TRIP				(new FallDownTechnique(Stance.PRONE, "Trip")),		 // run out of stability
	FIZZLE				(new NonAttackTechnique(Stance.BALANCED, "Fizzle", 0, 0)),	 // run out of mana
	/* Positional */
	DUCK				(new NonAttackTechnique(Stance.KNEELING, "Duck", 0, 0)),	 
	FLY					(new NonAttackTechnique(Stance.AIRBORNE, "Fly", 0, 0)),	 
	/* Enemy attacks */
	SLIME_ATTACK 		(new AttackTechnique(Stance.BALANCED, "Slime Attack", 7, 0, 5)),
	SLIME_QUIVER 		(new NonAttackTechnique(Stance.DEFENSIVE, "Slime Quiver", 0, -1)),
	
	/* Enemy pouncing */
	DIVEBOMB 			(new GrappleTechnique  (Stance.FELLATIO, "Divebomb", 2, Stance.FELLATIO, TechniqueHeight.HIGH)),
	SAY_AHH 			(new GrappleTechnique  (Stance.FELLATIO, "Say 'Ahh'", 2, Stance.FELLATIO)),
	IRRUMATIO 			(new NonAttackTechnique(Stance.FELLATIO, "Irrumatio", 0, 0)), 
	POUNCE 				(new GrappleTechnique  (Stance.DOGGY, "Pounce", 2, Stance.DOGGY)), // Used to initiate doggy
	POUND 				(new NonAttackTechnique(Stance.DOGGY, "Pound", 0, 1)), // Used to doggystyle
	KNOT 				(new NonAttackTechnique(Stance.KNOTTED, "Knot", 0, 0, Stance.KNOTTED, "Set Damage")), // Used to knot by knotty weresluts and others
	KNOT_BANG 			(new NonAttackTechnique(Stance.KNOTTED, "Knot Bang", 0, 0, Stance.KNOTTED, true)), // Used to knot by knotty weresluts and others - could end the battle
	ERUPT 				(new ClimaxTechnique   (Stance.ERUPT, "Erupt", Stance.KNEELING)),
	
	RECEIVE 			(new NonAttackTechnique(Stance.DOGGY, "Receive", 0, 0)), 
	RECEIVE_KNOT 		(new NonAttackTechnique(Stance.KNOTTED, "Receive Knot", 0, 0)), 
	OPEN_WIDE 			(new NonAttackTechnique(Stance.FELLATIO, "Open Wide", 0, 0)), 
	STRUGGLE_ANAL		(new GrappleTechnique(Stance.DOGGY, "Struggle", 4)), 
	STRUGGLE_ORAL		(new GrappleTechnique(Stance.FELLATIO, "Struggle", 4)), 
	BREAK_FREE_ANAL		(new NonAttackTechnique(Stance.BALANCED, "Struggle", 0, 0, Stance.BALANCED)), // Break hold
	BREAK_FREE_ORAL		(new NonAttackTechnique(Stance.BALANCED, "Struggle", 0, 0, Stance.BALANCED)), // Break hold
	
	INCANTATION 		(new NonAttackTechnique(Stance.CASTING, "Incantation", 0, 1)), 
	
	/* Learnable Skills*/
	VAULT 				(new NonAttackTechnique(Stance.AIRBORNE, "Vault", 2, 4)), 
	JUMP_ATTACK 		(new AttackTechnique(Stance.BALANCED, "Jump Attack", 4, 4, 2)),
	RECKLESS_ATTACK 	(new AttackTechnique(Stance.OFFENSIVE, "Reckless Attack", 2, 3, 6)), // needs to be unguardable
	
	TAUNT 				(new NonAttackTechnique(Stance.DEFENSIVE, "Taunt", 0, 0, true)), 
	KNOCK_DOWN 			(new AttackTechnique(Stance.OFFENSIVE, "Knock Down", 1, 3, 6)), // needs to knock down
	HIT_THE_DECK		(new FallDownTechnique(Stance.PRONE, "Hit the Deck")), 
	PARRY  				(new GuardTechnique(Stance.DEFENSIVE, "Parry", -1, 0)),
	
	COMBAT_HEAL  		(new SpellTechnique(Stance.BALANCED, "Combat Heal", 10, 10, true)),
	COMBAT_FIRE  		(new SpellTechnique(Stance.BALANCED, "Combat Fire", 3, 5, false)),
	TITAN_STRENGTH  	(new SpellTechnique(Stance.BALANCED, "Titan Strength", 0, 2, false)),	
	;
	
	private final TechniquePrototype trait;
	private Techniques(TechniquePrototype trait){
		this.trait = trait;
	}
	
	public TechniquePrototype getTrait(){ return trait; }
	
	public static Array<Techniques> getLearnableSkills() {
		Techniques[] learnables = new Techniques[]{VAULT, RECKLESS_ATTACK, TAUNT, KNOCK_DOWN, SECOND_WIND, HIT_THE_DECK, PARRY};
		return new Array<Techniques>(true, learnables, 0, learnables.length);
	}
	public static Array<Techniques> getLearnableSpells() {
		return new Array<Techniques>(true, new Techniques[]{COMBAT_HEAL, COMBAT_FIRE, TITAN_STRENGTH}, 0, 2);
	}
	public boolean isSpell() {
		return false;
	}
}
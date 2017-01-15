package com.majalis.character;

import com.badlogic.gdx.utils.Array;
import com.majalis.character.AbstractCharacter.Stance;
import com.majalis.technique.AttackTechnique;
import com.majalis.technique.ClimaxTechnique;
import com.majalis.technique.ClimaxTechnique.ClimaxType;
import com.majalis.technique.Bonus.BonusCondition;
import com.majalis.technique.Bonus.BonusType;
import com.majalis.technique.EroticTechnique;
import com.majalis.technique.FallDownTechnique;
import com.majalis.technique.GrappleTechnique;
import com.majalis.technique.GuardTechnique;
import com.majalis.technique.NonAttackTechnique;
import com.majalis.technique.SpellTechnique;
import com.majalis.technique.TechniquePrototype;
import com.majalis.technique.TechniquePrototype.TechniqueHeight;
/*
 * List of all techniques and their generic attributes.
 */
public enum Techniques {
	/* Offensive Techniques */  
	POWER_ATTACK 		(new AttackTechnique(Stance.OFFENSIVE, Stance.OFFENSIVE, "Power Attack", 3, 3, 4, TechniqueHeight.LOW).addBonus(BonusCondition.ENEMY_ON_GROUND, BonusType.POWER_MOD, 4).build()),
	TEMPO_ATTACK  		(new AttackTechnique(Stance.OFFENSIVE, Stance.OFFENSIVE, "Tempo Attack", 2, 2, 3, TechniqueHeight.LOW).build()),
	SPRING_ATTACK  		(new AttackTechnique(Stance.BALANCED, Stance.OFFENSIVE, "Spring Attack", 1, 4, 3, TechniqueHeight.LOW).build()),

	/* Blitz Techniques */
	BLITZ_ATTACK  		(new AttackTechnique(Stance.OFFENSIVE, Stance.BLITZ, "Blitz Attack", 4, 4, 6, TechniqueHeight.LOW).build()),
	ALL_OUT_BLITZ		(new AttackTechnique(Stance.BLITZ, Stance.BLITZ, "All-Out Blitz", 6, 4, 8, 1.5).build()),
	HOLD_BACK			(new NonAttackTechnique(Stance.BLITZ, Stance.OFFENSIVE, "Hold Back", 0, 3).build()),
	
	/* Balanced Techniques */
	RESERVED_ATTACK  	(new AttackTechnique(Stance.OFFENSIVE, Stance.BALANCED, "Reserved Attack", 1, 4, 1, TechniqueHeight.LOW).build()),
	REVERSAL_ATTACK  	(new AttackTechnique(Stance.DEFENSIVE, Stance.BALANCED, "Reversal Attack", 0, 2, 2, TechniqueHeight.LOW).build()),
	NEUTRAL_ATTACK  	(new AttackTechnique(Stance.BALANCED, Stance.BALANCED, "Low Attack", 0, 1, 1, TechniqueHeight.LOW).build()),
	/* Defensive Techniques */
	CAREFUL_ATTACK  	(new AttackTechnique(Stance.DEFENSIVE, Stance.DEFENSIVE, "Careful Attack", 0, 0, 1).build()),
	BLOCK				(new GuardTechnique	(Stance.BALANCED, Stance.DEFENSIVE, "Block", 0, 0, 100, true).build()),
	GUARD  				(new GuardTechnique	(Stance.DEFENSIVE, Stance.DEFENSIVE, "Guard", -2, -2, 200, true).build()),
	SECOND_WIND			(new NonAttackTechnique(Stance.DEFENSIVE, Stance.DEFENSIVE, "Second Wind", -4, -1).build()),
	/* Techniques from Prone/Supine */
	KIP_UP				(new NonAttackTechnique(Stance.PRONE, Stance.BALANCED, "Kip Up", 5, -10).build()),
	STAND_UP			(new NonAttackTechnique(Stance.PRONE, Stance.BALANCED, "Stand Up", 3, -6).build()),
	KNEE_UP				(new NonAttackTechnique(Stance.PRONE, Stance.KNEELING, "Knee Up", 1, -10).build()),
	STAY_KNELT			(new NonAttackTechnique(Stance.KNEELING, Stance.KNEELING, "Stay Knelt", -1, 0).build()),
	REST				(new NonAttackTechnique(Stance.SUPINE, Stance.SUPINE, "Rest", -1, -1).build()),
	REST_FACE_DOWN		(new NonAttackTechnique(Stance.PRONE, Stance.PRONE, "Rest", -1, -1).build()),
	ROLL_OVER_UP		(new NonAttackTechnique(Stance.PRONE, Stance.SUPINE, "Roll Over", -1, -1).build()),
	ROLL_OVER_DOWN		(new NonAttackTechnique(Stance.SUPINE, Stance.PRONE, "Roll Over", -1, -1).build()),
	/* Positional */
	DUCK				(new NonAttackTechnique(Stance.BALANCED, Stance.KNEELING, "Duck", 0, 0).build()),	 
	FLY					(new NonAttackTechnique(Stance.BALANCED, Stance.AIRBORNE, "Fly", 0, 0).build()),	 
	/* Enemy attacks */
	SLIME_ATTACK 		(new AttackTechnique(Stance.BALANCED, Stance.BALANCED, "Slime Attack", 7, 0, 5).build()),
	SLIME_QUIVER 		(new NonAttackTechnique(Stance.BALANCED, Stance.DEFENSIVE, "Slime Quiver", -1, -1).build()),
	GUT_CHECK			(new AttackTechnique(Stance.OFFENSIVE, Stance.OFFENSIVE, "Gutcheck", 3, 3, 4, 0, 1, false, TechniqueHeight.MEDIUM).build()),
	
	/* Enemy pouncing */
	DIVEBOMB 			(new GrappleTechnique  (Stance.AIRBORNE, Stance.FELLATIO, "Divebomb", 2, Stance.FELLATIO, TechniqueHeight.HIGH).build()),
	SAY_AHH 			(new GrappleTechnique  (Stance.BALANCED, Stance.FELLATIO, "Say 'Ahh'", 2, Stance.FELLATIO).addBonus(BonusCondition.OUTMANEUVER, BonusType.PRIORITY).build()),
	FULL_NELSON			(new GrappleTechnique  (Stance.BALANCED, Stance.FULL_NELSON, "Full Nelson", 2, Stance.FULL_NELSON).build()), // Used to initiate full nelson
	POUNCE_DOGGY		(new GrappleTechnique  (Stance.BALANCED, Stance.DOGGY, "Pounce", 2, Stance.DOGGY).build()), // Used to initiate doggy
	POUNCE_ANAL			(new GrappleTechnique  (Stance.BALANCED, Stance.ANAL, "Pounce", 2, Stance.ANAL).build()), // Used to initiate missionary
	PENETRATE_STANDING	(new GrappleTechnique  (Stance.FULL_NELSON, Stance.STANDING, "Penetrate", 2, Stance.STANDING).build()), // Used to initiate standing anal
	FACE_SIT			(new GrappleTechnique  (Stance.BALANCED, Stance.FACE_SITTING, "Plop Down", 2, Stance.FACE_SITTING).build()), // Used to initiate face-sitting
	SITTING_ORAL		(new GrappleTechnique  (Stance.FACE_SITTING, Stance.SIXTY_NINE, "Say 'Ahh'", 2, Stance.SIXTY_NINE).build()), // Used to initiate face-sitting
	KNOT 				(new NonAttackTechnique(Stance.DOGGY, Stance.KNOTTED, "Knot", 0, 0, Stance.KNOTTED, "Set Damage").build()), // Used to knot by knotty weresluts and others
	
	IRRUMATIO 			(new NonAttackTechnique(Stance.FELLATIO, Stance.FELLATIO, "Irrumatio", 0, 0, Stance.FELLATIO).build()), 
	HOLD	 			(new NonAttackTechnique(Stance.FULL_NELSON, Stance.FULL_NELSON, "Hold", 0, 1).build()), // Used to hold
	POUND_DOGGY 		(new NonAttackTechnique(Stance.DOGGY, Stance.DOGGY, "Pound", 0, 0).build()), // Used to doggystyle
	POUND_ANAL 			(new NonAttackTechnique(Stance.ANAL, Stance.ANAL, "Pound", 0, 0).build()), // Used to missionary
	POUND_STANDING 		(new NonAttackTechnique(Stance.STANDING, Stance.STANDING, "Pound", 0, 0).build()), // Used to standing anal
	RIDE_FACE			(new NonAttackTechnique(Stance.FACE_SITTING, Stance.FACE_SITTING, "Ride", 0, 0).build()), // Used to faceride
	RECIPROCATE			(new NonAttackTechnique(Stance.SIXTY_NINE, Stance.SIXTY_NINE, "Reciprocate", 0, 0).build()), // Used to sixty nine
	KNOT_BANG 			(new NonAttackTechnique(Stance.KNOTTED, Stance.KNOTTED, "Knot Bang", 0, 0, Stance.KNOTTED).build()), // Used to knot by knotty weresluts and others - could end the battle
	
	ERUPT_ANAL 			(new ClimaxTechnique   (Stance.DOGGY, Stance.DOGGY, "Erupt", Stance.PRONE, ClimaxType.ANAL ).build()),
	ERUPT_ORAL 			(new ClimaxTechnique   (Stance.FELLATIO, Stance.FELLATIO, "Erupt", Stance.KNEELING, ClimaxType.ORAL ).build()),
	ERUPT_FACIAL		(new ClimaxTechnique   (Stance.HANDY, Stance.HANDY, "Facial", Stance.KNEELING, ClimaxType.FACIAL ).build()),
	ERUPT_COWGIRL		(new ClimaxTechnique   (Stance.COWGIRL, Stance.SUPINE, "Erupt", Stance.KNEELING, ClimaxType.ANAL ).build()),
	ERUPT_SIXTY_NINE	(new ClimaxTechnique   (Stance.SIXTY_NINE, Stance.KNEELING, "Erupt", Stance.SUPINE, ClimaxType.ORAL ).build()),
	
	BE_RIDDEN			(new GrappleTechnique(Stance.COWGIRL, Stance.COWGIRL, "Be Ridden", 0).build()),
	RECEIVE_HANDY		(new GrappleTechnique(Stance.HANDY, Stance.HANDY, "Receive Handy", 0).build()),
	PUSH_OFF			(new NonAttackTechnique(Stance.COWGIRL, Stance.BALANCED, "Push Off", 0, 0, Stance.BALANCED).build()), // Break hold
	
	SIT_ON_IT			(new GrappleTechnique(Stance.BALANCED, Stance.COWGIRL, "Sit on It", 1, Stance.COWGIRL, "Sit down on it - and yes, it's going right up there.\nDon't say I didn't warn you.").build()), 
	OPEN_WIDE 			(new GrappleTechnique(Stance.HANDY, Stance.FELLATIO, "Open Wide",  1, Stance.FELLATIO, "Open wide and swallow it down.").build()), 
	GRAB_IT				(new GrappleTechnique(Stance.KNEELING, Stance.HANDY, "Grab It", 1, Stance.HANDY, "Grab it.").addBonus(BonusCondition.OUTMANEUVER, BonusType.PRIORITY).build()), 
		
	RIDE_ON_IT			(new EroticTechnique(Stance.COWGIRL, Stance.COWGIRL, "Ride on It", -1, 0, "Ride up and down on it.").build()), 
	STAND_OFF_IT		(new GrappleTechnique(Stance.COWGIRL, Stance.BALANCED, "Stand up off It", 1, Stance.SUPINE, "Get up off it.").build()), 
	STROKE_IT			(new EroticTechnique(Stance.HANDY, Stance.HANDY, "Stroke It", -1, 0, "Stroke it up and down.").build()), 
	LET_GO				(new GrappleTechnique(Stance.HANDY, Stance.KNEELING, "Let It Go", 1, Stance.BALANCED, "Let go of it.").build()), 
	
	RECEIVE_DOGGY		(new EroticTechnique(Stance.DOGGY, Stance.DOGGY, "Receive", -1, 0, "Take it up the butt.").build()), 
	RECEIVE_ANAL		(new EroticTechnique(Stance.ANAL, Stance.ANAL, "Receive", -1, 0, "Take it up the butt.").build()), 
	RECEIVE_STANDING	(new EroticTechnique(Stance.STANDING, Stance.STANDING, "Receive", -1, 0, "Take it up the butt.").build()), 
	RECIPROCATE_FORCED	(new EroticTechnique(Stance.SIXTY_NINE, Stance.SIXTY_NINE, "Reciprocate", -1, 0, "Give head and take it.").build()), 
	SUBMIT				(new EroticTechnique(Stance.FULL_NELSON, Stance.FULL_NELSON, "Submit", -1, 0, "Don't try to struggle.\nShe's looking for an opening.\nLiterally.").build()), 
	GET_FACE_RIDDEN		(new EroticTechnique(Stance.FACE_SITTING, Stance.FACE_SITTING, "Endure", -1, 0, "Let her press her ass all over your face.").build()), 
	RECEIVE_KNOT		(new EroticTechnique(Stance.KNOTTED, Stance.KNOTTED, "Receive Knot", -1, 0, "Take that big knot up the butt.").build()), 
	SUCK_IT 			(new EroticTechnique(Stance.FELLATIO, Stance.FELLATIO, "Suck It", -1, 0, "Open wide and swallow it down.").build()), 
	STRUGGLE_DOGGY		(new GrappleTechnique(Stance.DOGGY, Stance.DOGGY, "Struggle", 4).build()),
	STRUGGLE_ANAL		(new GrappleTechnique(Stance.ANAL, Stance.ANAL, "Struggle", 4).build()),
	STRUGGLE_STANDING	(new GrappleTechnique(Stance.STANDING, Stance.STANDING, "Struggle", 4).build()),
	STRUGGLE_ORAL		(new GrappleTechnique(Stance.FELLATIO, Stance.FELLATIO, "Struggle", 4).build()), 
	STRUGGLE_FULL_NELSON(new GrappleTechnique(Stance.FULL_NELSON, Stance.FULL_NELSON, "Struggle", 4).build()), 
	STRUGGLE_FACE_SIT   (new GrappleTechnique(Stance.FACE_SITTING, Stance.FACE_SITTING, "Struggle", 4).build()), 
	STRUGGLE_SIXTY_NINE (new GrappleTechnique(Stance.SIXTY_NINE, Stance.SIXTY_NINE, "Struggle", 4).build()), 
	
	BREAK_FREE_FULL_NELSON 	(new NonAttackTechnique(Stance.FULL_NELSON, Stance.BALANCED, "Struggle", 0, 0, Stance.BALANCED).build()), // Break hold
	BREAK_FREE_FACE_SIT	(new NonAttackTechnique(Stance.FACE_SITTING, Stance.BALANCED, "Struggle", 0, 0, Stance.BALANCED).build()), // Break hold
	BREAK_FREE_ANAL		(new NonAttackTechnique(Stance.ANAL, Stance.BALANCED, "Struggle", 0, 0, Stance.BALANCED).build()), // Break hold
	BREAK_FREE_ORAL		(new NonAttackTechnique(Stance.FELLATIO, Stance.BALANCED, "Struggle", 0, 0, Stance.BALANCED).build()), // Break hold
	
	INCANTATION 		(new NonAttackTechnique(Stance.BALANCED, Stance.CASTING, "Incantation", 0, 1).build()), 
	
	/* Learnable Skills*/
	ARMOR_SUNDER		(new AttackTechnique(Stance.OFFENSIVE, Stance.OFFENSIVE, "Armor Sunder", 1, 7, 4, 0, 2, 0, true, TechniqueHeight.MEDIUM).build(), 3),
	CAUTIOUS_ATTACK  	(new AttackTechnique(Stance.BALANCED, Stance.DEFENSIVE, "Cautious Attack", -1, 0, 2).build(), 3),
	VAULT 				(new NonAttackTechnique(Stance.OFFENSIVE, Stance.AIRBORNE, "Vault", 2, 4).build()), 
	JUMP_ATTACK 		(new AttackTechnique(Stance.AIRBORNE, Stance.BALANCED, "Jump Attack", 4, 4, 2).build()),
	RECKLESS_ATTACK 	(new AttackTechnique(Stance.OFFENSIVE, Stance.OFFENSIVE, "Reckless Attack", 2, 3, 6, false).build(), 3), // unguardable
	
	TAUNT 				(new NonAttackTechnique(Stance.DEFENSIVE, Stance.DEFENSIVE, "Taunt", 0, 0, true).build(), 2), 
	KNOCK_DOWN 			(new AttackTechnique(Stance.OFFENSIVE, Stance.OFFENSIVE, "Knock Down", 1, 3, 6, 2).build(), 3), 
	HIT_THE_DECK		(new FallDownTechnique(Stance.BALANCED, Stance.PRONE, "Hit the Deck").build()), 
	PARRY  				(new GuardTechnique(Stance.DEFENSIVE, Stance.DEFENSIVE, "Parry", -1, 0, 0, false).addBonus(BonusCondition.SKILL_LEVEL, BonusType.PARRY, 25).addBonus(BonusCondition.OUTMANEUVER, BonusType.PARRY, 25).build(), 3),
	
	COMBAT_HEAL  		(new SpellTechnique(Stance.CASTING, Stance.BALANCED, "Combat Heal", 10, 10, true).build(), 3),
	COMBAT_FIRE  		(new SpellTechnique(Stance.CASTING, Stance.BALANCED, "Combat Fire", 3, 5, false).build(), 3),
	TITAN_STRENGTH  	(new SpellTechnique(Stance.CASTING, Stance.BALANCED, "Titan Strength", 0, 2, false, StatusType.STRENGTH_BUFF).build(), 3),
	;
	
	private final TechniquePrototype trait;
	private final int maxRank;
	private Techniques(TechniquePrototype trait){
		this(trait, 1);
	}
	private Techniques(TechniquePrototype trait, int maxRank){
		this.trait = trait;
		this.maxRank = maxRank;
	}
	
	public TechniquePrototype getTrait(){ return trait; }
	
	public int getMaxRank() { return maxRank; }
	
	public static Array<Techniques> getLearnableSkills() {
		Techniques[] learnables = new Techniques[]{ARMOR_SUNDER, CAUTIOUS_ATTACK, VAULT, RECKLESS_ATTACK, TAUNT, KNOCK_DOWN, SECOND_WIND, HIT_THE_DECK, PARRY};
		return new Array<Techniques>(true, learnables, 0, learnables.length);
	}
	public static Array<Techniques> getLearnableSpells() {
		// need to change this to actually include Titan Strength once it's implemented
		Techniques[] learnables = new Techniques[]{COMBAT_HEAL, COMBAT_FIRE, TITAN_STRENGTH};
		return new Array<Techniques>(true, learnables, 0, learnables.length);
	}
}
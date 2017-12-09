package com.majalis.character;

import com.badlogic.gdx.utils.Array;
import com.majalis.character.Stance;
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
	
	DO_NOTHING			(new NonAttackTechnique(Stance.NULL, Stance.NULL, "Do Nothing", -1, 0).build()),
	
	/* Offensive Techniques */  
	POWER_ATTACK 		(new AttackTechnique(Stance.OFFENSIVE, Stance.OFFENSIVE, "Power Attack", 3, 3, 4, TechniqueHeight.LOW).addBonus(BonusCondition.ENEMY_ON_GROUND, BonusType.POWER_MOD, 4).build()),
	TEMPO_ATTACK  		(new AttackTechnique(Stance.OFFENSIVE, Stance.OFFENSIVE, "Tempo Attack", 2, 2, 3, TechniqueHeight.LOW).build()),
	SPRING_ATTACK  		(new AttackTechnique(Stance.BALANCED, Stance.OFFENSIVE, "Spring Attack", 1, 4, 3, TechniqueHeight.LOW).addBonus(BonusCondition.OUTMANEUVER, BonusType.BLEEDING, 1).build()),

	/* Blitz Techniques */
	BLITZ_ATTACK  		(new AttackTechnique(Stance.OFFENSIVE, Stance.BLITZ, "Blitz Attack", 4, 4, 6, TechniqueHeight.LOW).addBonus(BonusCondition.OUTMANEUVER, BonusType.BLEEDING, 1).build()),
	ALL_OUT_BLITZ		(new AttackTechnique(Stance.BLITZ, Stance.BLITZ, "All-Out Blitz", 6, 4, 8, 1.5).build()),
	HOLD_BACK			(new NonAttackTechnique(Stance.BLITZ, Stance.OFFENSIVE, "Hold Back", 0, 3).build()),
	
	/* Balanced Techniques */
	RESERVED_ATTACK  	(new AttackTechnique(Stance.OFFENSIVE, Stance.BALANCED, "Reserved Attack", -2, 4, 1, TechniqueHeight.LOW).build()),
	REVERSAL_ATTACK  	(new AttackTechnique(Stance.DEFENSIVE, Stance.BALANCED, "Reversal Attack", -3, 2, 2, TechniqueHeight.LOW).build()),
	NEUTRAL_ATTACK  	(new AttackTechnique(Stance.BALANCED, Stance.BALANCED, "Low Attack", -3, 1, 1, TechniqueHeight.LOW).addBonus(BonusCondition.ENEMY_LOW_STABILITY, BonusType.TRIP, 60).addBonus(BonusCondition.OUTMANEUVER, BonusType.TRIP, 10).build()),
	USE_ITEM		  	(new NonAttackTechnique(Stance.BALANCED, Stance.ITEM, "Use Item", 0, 2).build()),
	ITEM_OR_CANCEL		(new NonAttackTechnique(Stance.ITEM, Stance.BALANCED, "Cancel", 0, 0).build()),
	
	/* Defensive Techniques */
	CAREFUL_ATTACK  	(new AttackTechnique(Stance.DEFENSIVE, Stance.DEFENSIVE, "Careful Attack", -2, 0, 1).build()),
	BLOCK				(new GuardTechnique	(Stance.BALANCED, Stance.DEFENSIVE, "Block", 0, 0, 3, true).build()),//.addBonus(BonusCondition.OUTMANEUVER, BonusType.GUARD_MOD, 25).build()),
	GUARD  				(new GuardTechnique	(Stance.DEFENSIVE, Stance.DEFENSIVE, "Guard", -2, -2, 4, true).build()),//.addBonus(BonusCondition.OUTMANEUVER, BonusType.GUARD_MOD, 25).build()),
	SECOND_WIND			(new NonAttackTechnique(Stance.DEFENSIVE, Stance.DEFENSIVE, "Second Wind", -4, -1).build()),
	
	/* Counter Techniques */ 			
	RIPOSTE  			(new GuardTechnique(Stance.COUNTER, Stance.BALANCED, "Riposte", -1, 3, 5, false).addBonus(BonusCondition.SKILL_LEVEL, BonusType.PARRY, 2).addBonus(BonusCondition.OUTMANEUVER, BonusType.PARRY, 2).addBonus(BonusCondition.SKILL_LEVEL, BonusType.DISARM, 50).addBonus(BonusCondition.SKILL_LEVEL, BonusType.COUNTER, 50).addBonus(BonusCondition.OUTMANEUVER, BonusType.COUNTER, 50).addBonus(BonusCondition.OUTMANEUVER, BonusType.DISARM, 50).build(), 1),
	EN_GARDE  			(new GuardTechnique(Stance.COUNTER, Stance.DEFENSIVE, "En Garde", -1, 0, 1, false).addBonus(BonusCondition.SKILL_LEVEL, BonusType.PARRY, 2).addBonus(BonusCondition.OUTMANEUVER, BonusType.PARRY, 1).build(), 1),
	
	/* Techniques from Prone/Supine */
	KIP_UP				(new NonAttackTechnique(Stance.PRONE, Stance.BALANCED, "Kip Up", 5, -15).build()),
	STAND_UP			(new NonAttackTechnique(Stance.PRONE, Stance.BALANCED, "Stand Up", 3, -6).build()),
	STAND_UP_HANDS		(new NonAttackTechnique(Stance.HANDS_AND_KNEES, Stance.BALANCED, "Stand Up", 1, -15).build()),
	KNEE_UP				(new NonAttackTechnique(Stance.PRONE, Stance.KNEELING, "Knee Up", 1, -15).build()),
	KNEE_UP_HANDS		(new NonAttackTechnique(Stance.HANDS_AND_KNEES, Stance.KNEELING, "Knee Up", 0, -15).build()),
	PUSH_UP				(new NonAttackTechnique(Stance.PRONE, Stance.HANDS_AND_KNEES, "Push Up", 0, -15).build()),
	STAY_KNELT			(new NonAttackTechnique(Stance.KNEELING, Stance.KNEELING, "Stay Knelt", -1, 0).build()),
	STAY				(new NonAttackTechnique(Stance.HANDS_AND_KNEES, Stance.HANDS_AND_KNEES, "Stay", -1, 0).build()),
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
	
	ACTIVATE		  	(new SpellTechnique(Stance.CASTING, Stance.BALANCED, "Activate", 0, 0, false, StatusType.ACTIVATE).build()),
	ANGELIC_GRACE  		(new SpellTechnique(Stance.CASTING, Stance.BALANCED, "Angelic Grace", 80, 0, true).build()),
	TRUMPET		  		(new SpellTechnique(Stance.CASTING, Stance.BALANCED, "Trumpet", -1, 0, false).build()),
	HEAL  		(new SpellTechnique(Stance.CASTING, Stance.BALANCED, "Heal", 27, 10, true).build()),
	
	GUT_CHECK			(new AttackTechnique(Stance.OFFENSIVE, Stance.OFFENSIVE, "Gutcheck", 3, 3, 4, 0, 1, false, TechniqueHeight.MEDIUM).build()),

	RIP					(new AttackTechnique(Stance.BALANCED, Stance.BALANCED, "Rip", -2, 3, 3, 0, 5, 0, false, TechniqueHeight.LOW).build()),
	SMASH				(new AttackTechnique(Stance.OFFENSIVE, Stance.BALANCED, "Smash", 4, 5, 5, 1.5).build()),
	LIFT_WEAPON			(new NonAttackTechnique(Stance.BALANCED, Stance.OFFENSIVE, "Raise Club", 1, 1).build()),
	SLAM				(new AttackTechnique(Stance.BALANCED, Stance.OFFENSIVE, "Slam", -2, 3, 3).build()),
	
	WRAP				(new GrappleTechnique(Stance.BALANCED, Stance.WRAPPED, "Wrap", 0, Stance.WRAPPED_BOTTOM, GrappleType.ADVANTAGE).build()), // when enemy is prone
	BITE				(new GrappleTechnique(Stance.WRAPPED, Stance.WRAPPED, "Bite", 4, GrappleType.SUBMIT).setIgnoreArmor().setBleed(1).build()),	
	SQUEEZE				(new GrappleTechnique(Stance.WRAPPED, Stance.WRAPPED, "Squeeze", 4, GrappleType.ADVANTAGE).addBonus(BonusCondition.STRENGTH_OVERPOWER, BonusType.GRAPPLE).build()),
	SQUEEZE_CRUSH		(new GrappleTechnique(Stance.WRAPPED, Stance.WRAPPED, "Crush", 4, GrappleType.SUBMIT).setAutoDamage().build()),
	SQUEEZE_RELEASE		(new GrappleTechnique(Stance.WRAPPED, Stance.PRONE, "Release", -1, Stance.PRONE, GrappleType.WIN).build()),		
	
	SQUEEZE_STRUGGLE		(new GrappleTechnique(Stance.WRAPPED_BOTTOM, Stance.WRAPPED_BOTTOM, "Struggle", 5, GrappleType.ADVANTAGE).build()),
	BREAK_FREE_SQUEEZE	(new GrappleTechnique(Stance.WRAPPED_BOTTOM, Stance.PRONE, "Struggle", 0, Stance.PRONE, GrappleType.BREAK).build()), // Break hold
	SQUEEZE_REST	(new GrappleTechnique(Stance.WRAPPED_BOTTOM, Stance.WRAPPED_BOTTOM, "Rest", -1, GrappleType.SUBMIT).build()),	
	
	/* Enemy pouncing */
	DIVEBOMB 			(new GrappleTechnique  (Stance.AIRBORNE, Stance.FELLATIO, "Divebomb", 2, Stance.FELLATIO_BOTTOM, TechniqueHeight.HIGH, GrappleType.WIN, "").build()),
	SAY_AHH 			(new GrappleTechnique  (Stance.BALANCED, Stance.FELLATIO, "Say 'Ahh'", 2, Stance.FELLATIO_BOTTOM, GrappleType.WIN).addBonus(BonusCondition.OUTMANEUVER, BonusType.PRIORITY).build()),
	FULL_NELSON			(new GrappleTechnique  (Stance.BALANCED, Stance.FULL_NELSON, "Full Nelson", 2, Stance.FULL_NELSON_BOTTOM, GrappleType.ADVANTAGE).build()), // Used to initiate full nelson
	POUNCE_DOGGY		(new GrappleTechnique  (Stance.BALANCED, Stance.DOGGY, "Pounce", 2, Stance.DOGGY_BOTTOM, GrappleType.WIN).build()), // Used to initiate doggy
	POUNCE_PRONE_BONE	(new GrappleTechnique  (Stance.BALANCED, Stance.PRONE_BONE, "Pounce", 2, Stance.PRONE_BONE_BOTTOM, GrappleType.WIN).build()), // Used to initiate prone-bone
	POUNCE_ANAL			(new GrappleTechnique  (Stance.BALANCED, Stance.ANAL, "Pounce", 2, Stance.ANAL_BOTTOM, GrappleType.WIN).build()), // Used to initiate missionary
	PENETRATE_STANDING	(new GrappleTechnique  (Stance.FULL_NELSON, Stance.STANDING, "Penetrate", 2, Stance.STANDING_BOTTOM, GrappleType.WIN).build()), // Used to initiate standing anal
	SEIZE				(new GrappleTechnique  (Stance.BALANCED, Stance.HOLDING, "Seize", 2, Stance.HELD, GrappleType.WIN).build()), // Used to initiate giant sex
	FACE_SIT			(new GrappleTechnique  (Stance.BALANCED, Stance.FACE_SITTING, "Plop Down", 2, Stance.FACE_SITTING_BOTTOM, GrappleType.ADVANTAGE).build()), // Used to initiate face-sitting
	SITTING_ORAL		(new GrappleTechnique  (Stance.FACE_SITTING, Stance.SIXTY_NINE, "Say 'Ahh'", 2, Stance.SIXTY_NINE_BOTTOM, GrappleType.WIN).build()), // Used to initiate sixty nine
	OUROBOROS			(new GrappleTechnique  (Stance.BALANCED, Stance.OUROBOROS, "Ouroboros", 2, Stance.OUROBOROS_BOTTOM, GrappleType.WIN).build()), // Used to initiate ouroboros
	MOUNT_FACE			(new GrappleTechnique  (Stance.BALANCED, Stance.FACEFUCK, "Mount Face", 2, Stance.FACEFUCK_BOTTOM, GrappleType.WIN).build()), // Used to initiate facefuck
	
	YOINK				(new GrappleTechnique  (Stance.NULL, Stance.NULL, "Yoink", 0, GrappleType.NULL).addBonus(BonusCondition.SKILL_LEVEL, BonusType.REMOVE_PLUG).build()), // Used to initiate doggy
	
	KNOT 				(new NonAttackTechnique(Stance.DOGGY, Stance.KNOTTED, "Knot", 0, 0, Stance.KNOTTED_BOTTOM, "Set Damage").build()), // Used to knot by knotty weresluts and others
	MOUTH_KNOT 				(new NonAttackTechnique(Stance.FELLATIO, Stance.MOUTH_KNOTTED, "Knot", 0, 0, Stance.MOUTH_KNOTTED_BOTTOM, "Set Damage").build()), // Used to knot by knotty weresluts and others
	
	OVIPOSITION			(new NonAttackTechnique(Stance.FULL_NELSON, Stance.OVIPOSITION, "Oviposition", 0, 0, Stance.OVIPOSITION_BOTTOM).build()), // Used to oviposition
	
	IRRUMATIO 			(new GrappleTechnique(Stance.FELLATIO, Stance.FELLATIO, "Irrumatio", 1).build()), 
	FORCE_DEEPTHROAT	(new GrappleTechnique(Stance.FELLATIO, Stance.FELLATIO, "Deepthroat", 1).build()), 
	GRIP	 			(new GrappleTechnique(Stance.FULL_NELSON, Stance.FULL_NELSON, "Grip", 3, GrappleType.ADVANTAGE).build()), // Used to grapple
	HOLD	 			(new GrappleTechnique(Stance.FULL_NELSON, Stance.FULL_NELSON, "Hold", -1).build()), // Used to hold
	POUND_DOGGY 		(new GrappleTechnique(Stance.DOGGY, Stance.DOGGY, "Pound", 2).build()), // Used to doggystyle
	CRUSH_ASS	 		(new GrappleTechnique(Stance.DOGGY, Stance.DOGGY, "Crush Ass", 2).build()), // Used to doggystyle
	POUND_PRONE_BONE	(new GrappleTechnique(Stance.PRONE_BONE, Stance.PRONE_BONE, "Prone Bone", 2).build()), // Used to prone bone
	ROUND_AND_ROUND		(new GrappleTechnique(Stance.OUROBOROS, Stance.OUROBOROS, "Round and Round", 2).build()), // Used to ouroboros
	FACEFUCK			(new GrappleTechnique(Stance.FACEFUCK, Stance.FACEFUCK, "Facefuck", 2).build()), // Used to ouroboros
	POUND_ANAL 			(new GrappleTechnique(Stance.ANAL, Stance.ANAL, "Pound", 2).build()), // Used to missionary
	POUND_STANDING 		(new GrappleTechnique(Stance.STANDING, Stance.STANDING, "Pound", 2).build()), // Used to standing anal
	OGRE_SMASH	 		(new GrappleTechnique(Stance.HOLDING, Stance.CRUSHING, "Ogre Smash", 2, Stance.PENETRATED).build()), // Used to bring down onto giant's cock first time
	CRUSH		 		(new GrappleTechnique(Stance.CRUSHING, Stance.CRUSHING, "Crush", 2, Stance.PENETRATED).build()), // Used to bring down onto giant's cock
	PULL_UP		 		(new GrappleTechnique(Stance.CRUSHING, Stance.CRUSHING, "Pull Up", 2, Stance.SPREAD).build()), // Used to bring down onto giant's cock
	
	RIDE_FACE			(new GrappleTechnique(Stance.FACE_SITTING, Stance.FACE_SITTING, "Ride", 0).build()), // Used to faceride
	RECIPROCATE			(new GrappleTechnique(Stance.SIXTY_NINE, Stance.SIXTY_NINE, "Reciprocate", 0).build()), // Used to sixty nine
	KNOT_BANG 			(new GrappleTechnique(Stance.KNOTTED, Stance.KNOTTED, "Knot Bang", 0, Stance.KNOTTED_BOTTOM).build()), // Used to knot by knotty weresluts and others - could end the battle
	MOUTH_KNOT_BANG 	(new GrappleTechnique(Stance.MOUTH_KNOTTED, Stance.MOUTH_KNOTTED, "Knot Bang", 0, Stance.MOUTH_KNOTTED_BOTTOM).build()), // Used to knot by knotty weresluts and others - could end the battle
	
	LAY_EGGS 			(new GrappleTechnique(Stance.OVIPOSITION, Stance.OVIPOSITION, "Lay Eggs", 0, Stance.OVIPOSITION_BOTTOM).build()), // Used to lay eggs - can end battle
	
	ERUPT_ANAL 			(new ClimaxTechnique   (Stance.DOGGY, Stance.DOGGY, "Erupt", Stance.PRONE, ClimaxType.ANAL).build()),
	ERUPT_ORAL 			(new ClimaxTechnique   (Stance.FELLATIO, Stance.FELLATIO, "Erupt", Stance.KNEELING, ClimaxType.ORAL).build()),
	ERUPT_FACIAL		(new ClimaxTechnique   (Stance.HANDY, Stance.HANDY, "Facial", Stance.KNEELING, ClimaxType.FACIAL).build()),
	ERUPT_COWGIRL		(new ClimaxTechnique   (Stance.COWGIRL, Stance.SUPINE, "Erupt", Stance.KNEELING, ClimaxType.ANAL).build()),
	ERUPT_SIXTY_NINE	(new ClimaxTechnique   (Stance.SIXTY_NINE, Stance.KNEELING, "Erupt", Stance.SUPINE, ClimaxType.ORAL).build()),
	
	RECEIVE_HANDY		(new GrappleTechnique(Stance.HANDY, Stance.HANDY, "Receive Handy", 0).build()),
	BE_RIDDEN			(new GrappleTechnique(Stance.COWGIRL, Stance.COWGIRL, "Be Ridden", 0).build()),
	BE_RIDDEN_REVERSE	(new GrappleTechnique(Stance.REVERSE_COWGIRL, Stance.REVERSE_COWGIRL, "Be Ridden", 0).build()),
	PUSH_OFF			(new GrappleTechnique(Stance.COWGIRL, Stance.BALANCED, "Push Off", 0, Stance.BALANCED).build()), // Break hold
	PUSH_OFF_ATTEMPT	(new GrappleTechnique(Stance.COWGIRL, Stance.COWGIRL, "Push Off", 4).build()),
	PUSH_OFF_REVERSE			(new GrappleTechnique(Stance.COWGIRL, Stance.BALANCED, "Push Off", 0, Stance.BALANCED).build()), // Break hold
	PUSH_OFF_ATTEMPT_REVERSE	(new GrappleTechnique(Stance.COWGIRL, Stance.COWGIRL, "Push Off", 4).build()),
	
	SIT_ON_IT			(new GrappleTechnique(Stance.BALANCED, Stance.COWGIRL_BOTTOM, "Sit on It", 1, Stance.COWGIRL, "Sit down on it - and yes, it's going right up there.\nDon't say I didn't warn you.").build()), 
	TURN_AND_SIT		(new GrappleTechnique(Stance.BALANCED, Stance.REVERSE_COWGIRL_BOTTOM, "Turn and Sit", 1, Stance.REVERSE_COWGIRL, "Turn around and sit on it - and yes, it's going right up there.\nBut at least you'll give 'em a show.").build()), 
	
	OPEN_WIDE 			(new GrappleTechnique(Stance.HANDY, Stance.FELLATIO_BOTTOM, "Open Wide",  1, Stance.FELLATIO, "Open wide and swallow it down.").build()), 
	GRAB_IT				(new GrappleTechnique(Stance.KNEELING, Stance.HANDY_BOTTOM, "Grab It", 1, Stance.HANDY, "Grab it.").addBonus(BonusCondition.OUTMANEUVER, BonusType.PRIORITY).build()), 
		
	RIDE_ON_IT			(new EroticTechnique(Stance.COWGIRL_BOTTOM, Stance.COWGIRL_BOTTOM, "Ride on It", -1, 0, "Keep it deep and stir it around inside.").build()), 
	BOUNCE_ON_IT		(new EroticTechnique(Stance.COWGIRL_BOTTOM, Stance.COWGIRL_BOTTOM, "Bounce on It", -1, 0, "Bounce up and down on it.").build()), 
	SQUEEZE_IT			(new EroticTechnique(Stance.COWGIRL_BOTTOM, Stance.COWGIRL_BOTTOM, "Squeeze It", -1, 0, "Squeeze it with your hole.").build()),
	
	RIDE_ON_IT_REVERSE	(new EroticTechnique(Stance.REVERSE_COWGIRL_BOTTOM, Stance.REVERSE_COWGIRL_BOTTOM, "Ride on It", -1, 0, "Keep it deep and stir it around inside.").build()), 
	BOUNCE_ON_IT_REVERSE	(new EroticTechnique(Stance.REVERSE_COWGIRL_BOTTOM, Stance.REVERSE_COWGIRL_BOTTOM, "Bounce on It", -1, 0, "Bounce up and down on it.").build()), 
	SQUEEZE_IT_REVERSE	(new EroticTechnique(Stance.REVERSE_COWGIRL_BOTTOM, Stance.REVERSE_COWGIRL_BOTTOM, "Squeeze It", -1, 0, "Squeeze it with your hole.").build()),
	
	STAND_OFF_IT		(new GrappleTechnique(Stance.COWGIRL_BOTTOM, Stance.BALANCED, "Stand up off It", 1, Stance.SUPINE, TechniqueHeight.NONE, GrappleType.BREAK, "Get up off it.").build()), 
	PULL_OUT			(new GrappleTechnique(Stance.DOGGY, Stance.BALANCED, "Pull Out", 1, Stance.PRONE, TechniqueHeight.NONE, GrappleType.BREAK, "Pull out.").build()), 
	PULL_OUT_ANAL		(new GrappleTechnique(Stance.ANAL, Stance.KNEELING, "Pull Out", 1, Stance.SUPINE, TechniqueHeight.NONE, GrappleType.BREAK, "Pull out.").build()), 
	PULL_OUT_ORAL		(new GrappleTechnique(Stance.FELLATIO, Stance.BALANCED, "Pull Out", 1, Stance.KNEELING, TechniqueHeight.NONE, GrappleType.BREAK, "Pull Out.").build()), 
	PULL_OUT_STANDING	(new GrappleTechnique(Stance.STANDING, Stance.BALANCED, "Pull Out", 1, Stance.BALANCED, TechniqueHeight.NONE, GrappleType.BREAK, "Pull Out.").build()), 
	
	STROKE_IT			(new EroticTechnique(Stance.HANDY_BOTTOM, Stance.HANDY_BOTTOM, "Stroke It", -1, 0, "Stroke it up and down.").build()), 
	LET_GO				(new GrappleTechnique(Stance.HANDY_BOTTOM, Stance.KNEELING, "Let It Go", 1, Stance.BALANCED, "Let go of it.").build()), 
	
	RECEIVE_DOGGY		(new EroticTechnique(Stance.DOGGY_BOTTOM, Stance.DOGGY_BOTTOM, "Receive", -1, 0, "Take it up the butt.").build()), 
	RECEIVE_PRONE_BONE	(new EroticTechnique(Stance.PRONE_BONE_BOTTOM, Stance.PRONE_BONE_BOTTOM, "Receive", -1, 0, "Take it up the butt.").build()), 
	SELF_SPANK			(new EroticTechnique(Stance.DOGGY_BOTTOM, Stance.DOGGY_BOTTOM, "Self Spank", -1, 0, "Spank your own ass as a taunt.", true).build()), 
	RECEIVE_ANAL		(new EroticTechnique(Stance.ANAL_BOTTOM, Stance.ANAL_BOTTOM, "Receive", -1, 0, "Take it face to face.").build()), 
	WRAP_LEGS			(new EroticTechnique(Stance.ANAL_BOTTOM, Stance.ANAL_BOTTOM, "Wrap Legs", -1, 0, "Wrap your legs around them.", true).build()), 
	POUT				(new EroticTechnique(Stance.ANAL_BOTTOM, Stance.ANAL_BOTTOM, "Pout", -1, 0, "Make a pathetic face.").build()), 
	RECEIVE_STANDING	(new EroticTechnique(Stance.STANDING_BOTTOM, Stance.STANDING_BOTTOM, "Receive", -1, 0, "Take it up the butt.").build()), 
	RECIPROCATE_FORCED	(new EroticTechnique(Stance.SIXTY_NINE_BOTTOM, Stance.SIXTY_NINE_BOTTOM, "Reciprocate", -1, 0, "Give head and take it.").build()), 
	UH_OH				(new EroticTechnique(Stance.HELD, Stance.PENETRATED, "Uh-Oh", 0, 0, "Oh no. Oh no no no. Wait... wait, wait wait wait wait wait wait wait wait wait wait wait wait wait wait wait wait wait wait wait wait wait wait wait wait wait wait wait wait wait--!!!").build()), 
	RECEIVE_COCK		(new EroticTechnique(Stance.SPREAD, Stance.PENETRATED, "Receive Cock", -1, 0, "That is a lot of dick.").build()), 
	HURK				(new EroticTechnique(Stance.PENETRATED, Stance.SPREAD, "Hurk!", -1, 0, "Hurk.").build()), 
	SUBMIT				(new EroticTechnique(Stance.FULL_NELSON_BOTTOM, Stance.FULL_NELSON_BOTTOM, "Submit", -1, 0, "Don't try to struggle.\nShe's looking for an opening.\nLiterally.").build()), 
	GET_FACE_RIDDEN		(new EroticTechnique(Stance.FACE_SITTING_BOTTOM, Stance.FACE_SITTING_BOTTOM, "Endure", -1, 0, "Let her press her ass all over your face.").build()), 
	RECEIVE_KNOT		(new EroticTechnique(Stance.KNOTTED_BOTTOM, Stance.KNOTTED_BOTTOM, "Receive Knot", -1, 0, "Take that big knot up the butt.").build()), 
	SUCK_KNOT			(new EroticTechnique(Stance.MOUTH_KNOTTED_BOTTOM, Stance.MOUTH_KNOTTED_BOTTOM, "Suck Knot", -1, 0, "Get your mouth around that big knot.").build()), 
	RECEIVE_EGGS		(new EroticTechnique(Stance.OVIPOSITION_BOTTOM, Stance.OVIPOSITION_BOTTOM, "Receive Eggs", -1, 0, "Get stuffed full of eggs.").build()), 
	SUCK_IT 			(new EroticTechnique(Stance.FELLATIO_BOTTOM, Stance.FELLATIO_BOTTOM, "Suck It", -1, 0, "Open wide and swallow it down.").build()), 
	DEEPTHROAT			(new EroticTechnique(Stance.FELLATIO_BOTTOM, Stance.FELLATIO_BOTTOM, "Deepthroat", -1, 0, "Take it down your throat.").build()), 
	RECEIVE_OUROBOROS	(new EroticTechnique(Stance.OUROBOROS_BOTTOM, Stance.OUROBOROS_BOTTOM, "Round and Round", -1, 0, "Open wide and swallow it down.").build()), 
	GET_FACEFUCKED		(new EroticTechnique(Stance.FACEFUCK_BOTTOM, Stance.FACEFUCK_BOTTOM, "Get Facefucked", -1, 0, "Open wide and swallow it down.").build()), 
	
	/* Ground Wrestling */
	WRESTLE_TO_GROUND	(new GrappleTechnique(Stance.BALANCED, Stance.GROUND_WRESTLE, "Wrestle", 0, Stance.GROUND_WRESTLE_FACE_DOWN, GrappleType.ADVANTAGE).build()), // when enemy is prone
	WRESTLE_TO_GROUND_UP(new GrappleTechnique(Stance.BALANCED, Stance.GROUND_WRESTLE, "Wrestle", 0, Stance.GROUND_WRESTLE_FACE_UP, GrappleType.ADVANTAGE).build()), // when enemy is supine
	
	PENETRATE_PRONE		(new GrappleTechnique(Stance.GROUND_WRESTLE, Stance.PRONE_BONE, "Penetrate", 0, Stance.PRONE_BONE_BOTTOM, GrappleType.WIN).build()),
	PENETRATE_MISSIONARY(new GrappleTechnique(Stance.GROUND_WRESTLE, Stance.ANAL, "Penetrate", 0, Stance.ANAL_BOTTOM, GrappleType.WIN).build()),
	PIN					(new GrappleTechnique(Stance.GROUND_WRESTLE, Stance.GROUND_WRESTLE, "Pin", 8, GrappleType.PIN).addBonus(BonusCondition.STRENGTH_OVERPOWER_STRONG, BonusType.GRAPPLE, 2).build()),
	GRAPPLE				(new GrappleTechnique(Stance.GROUND_WRESTLE, Stance.GROUND_WRESTLE, "Grapple", 4, GrappleType.ADVANTAGE).addBonus(BonusCondition.STRENGTH_OVERPOWER, BonusType.GRAPPLE).build()),
	HOLD_WRESTLE		(new GrappleTechnique(Stance.GROUND_WRESTLE, Stance.GROUND_WRESTLE, "Hold", 2, GrappleType.HOLD).addBonus(BonusCondition.STRENGTH_OVERPOWER, BonusType.GRAPPLE).build()),
	CHOKE				(new GrappleTechnique(Stance.GROUND_WRESTLE, Stance.GROUND_WRESTLE, "Choke", 2, GrappleType.SUBMIT).setStamDam(1).addBonus(BonusCondition.STRENGTH_OVERPOWER, BonusType.GRAPPLE).build()),	
	REST_WRESTLE		(new GrappleTechnique(Stance.GROUND_WRESTLE, Stance.GROUND_WRESTLE, "Rest", -1, GrappleType.SUBMIT).build()),	
	FLIP_PRONE 			(new GrappleTechnique(Stance.GROUND_WRESTLE, Stance.GROUND_WRESTLE, "Flip", 2, Stance.GROUND_WRESTLE_FACE_DOWN, GrappleType.HOLD).addBonus(BonusCondition.STRENGTH_OVERPOWER, BonusType.GRAPPLE).build()),
	FLIP_SUPINE			(new GrappleTechnique(Stance.GROUND_WRESTLE, Stance.GROUND_WRESTLE, "Flip", 2, Stance.GROUND_WRESTLE_FACE_UP, GrappleType.HOLD).addBonus(BonusCondition.STRENGTH_OVERPOWER, BonusType.GRAPPLE).build()),
	RELEASE_PRONE		(new GrappleTechnique(Stance.GROUND_WRESTLE, Stance.HANDS_AND_KNEES, "Release", -1, Stance.PRONE, GrappleType.WIN).build()),	
	RELEASE_SUPINE		(new GrappleTechnique(Stance.GROUND_WRESTLE, Stance.HANDS_AND_KNEES, "Release", -1, Stance.SUPINE, GrappleType.WIN).build()),	
	
	STRUGGLE_GROUND		(new GrappleTechnique(Stance.GROUND_WRESTLE_FACE_DOWN, Stance.GROUND_WRESTLE_FACE_DOWN, "Struggle", 5, GrappleType.ADVANTAGE).build()),
	BREAK_FREE_GROUND	(new GrappleTechnique(Stance.GROUND_WRESTLE_FACE_DOWN, Stance.PRONE, "Struggle", 0, Stance.PRONE, GrappleType.BREAK).build()), // Break hold
	GRIND				(new GrappleTechnique(Stance.GROUND_WRESTLE_FACE_DOWN, Stance.GROUND_WRESTLE_FACE_DOWN, "Grind", 0, GrappleType.SUBMIT).setSeduce().build()),
	REST_GROUND_DOWN	(new GrappleTechnique(Stance.GROUND_WRESTLE_FACE_DOWN, Stance.GROUND_WRESTLE_FACE_DOWN, "Rest", -1, GrappleType.SUBMIT).build()),	
	
	STRUGGLE_GROUND_UP	(new GrappleTechnique(Stance.GROUND_WRESTLE_FACE_UP, Stance.GROUND_WRESTLE_FACE_UP, "Grapple", 4, GrappleType.ADVANTAGE).build()),
	BREAK_FREE_GROUND_UP(new GrappleTechnique(Stance.GROUND_WRESTLE_FACE_UP, Stance.SUPINE, "Struggle", 0, Stance.PRONE, GrappleType.BREAK).build()), // Break hold
	REVERSAL			(new GrappleTechnique(Stance.GROUND_WRESTLE_FACE_UP, Stance.GROUND_WRESTLE_FACE_UP, "Reversal", 6, GrappleType.REVERSAL).addBonus(BonusCondition.OUTMANEUVER, BonusType.GRAPPLE).build()),
	FULL_REVERSAL		(new GrappleTechnique(Stance.GROUND_WRESTLE_FACE_UP, Stance.GROUND_WRESTLE, "Reversal", 8, Stance.GROUND_WRESTLE_FACE_DOWN, GrappleType.WIN).build()),
	REST_GROUND_UP		(new GrappleTechnique(Stance.GROUND_WRESTLE_FACE_UP, Stance.GROUND_WRESTLE_FACE_UP, "Rest", -1, GrappleType.SUBMIT).build()),
	
	/* Struggle Skills */
	STRUGGLE_DOGGY		(new GrappleTechnique(Stance.DOGGY_BOTTOM, Stance.DOGGY_BOTTOM, "Struggle", 4, GrappleType.ADVANTAGE).build()),
	STRUGGLE_PRONE_BONE	(new GrappleTechnique(Stance.PRONE_BONE_BOTTOM, Stance.PRONE_BONE_BOTTOM, "Struggle", 4, GrappleType.ADVANTAGE).build()),
	STRUGGLE_ANAL		(new GrappleTechnique(Stance.ANAL_BOTTOM, Stance.ANAL_BOTTOM, "Struggle", 4, GrappleType.ADVANTAGE).build()),
	STRUGGLE_STANDING	(new GrappleTechnique(Stance.STANDING_BOTTOM, Stance.STANDING_BOTTOM, "Struggle", 4, GrappleType.ADVANTAGE).build()),
	STRUGGLE_ORAL		(new GrappleTechnique(Stance.FELLATIO_BOTTOM, Stance.FELLATIO_BOTTOM, "Struggle", 4, GrappleType.ADVANTAGE).build()), 
	STRUGGLE_FULL_NELSON(new GrappleTechnique(Stance.FULL_NELSON_BOTTOM, Stance.FULL_NELSON_BOTTOM, "Struggle", 4, GrappleType.ADVANTAGE).build()), 
	STRUGGLE_FACE_SIT   (new GrappleTechnique(Stance.FACE_SITTING_BOTTOM, Stance.FACE_SITTING_BOTTOM, "Struggle", 4, GrappleType.ADVANTAGE).build()), 
	STRUGGLE_SIXTY_NINE (new GrappleTechnique(Stance.SIXTY_NINE_BOTTOM, Stance.SIXTY_NINE_BOTTOM, "Struggle", 4, GrappleType.ADVANTAGE).build()), 
	STRUGGLE_OUROBOROS (new GrappleTechnique(Stance.OUROBOROS_BOTTOM, Stance.OUROBOROS_BOTTOM, "Struggle", 4, GrappleType.ADVANTAGE).build()), 
	STRUGGLE_FACEFUCK  (new GrappleTechnique(Stance.FACEFUCK_BOTTOM, Stance.FACEFUCK_BOTTOM, "Struggle", 4, GrappleType.ADVANTAGE).build()), 
	
	/* Break Free Skills */
	BREAK_FREE_FULL_NELSON 	(new GrappleTechnique(Stance.FULL_NELSON_BOTTOM, Stance.BALANCED, "Struggle", 0, Stance.BALANCED, GrappleType.BREAK).build()), // Break hold
	BREAK_FREE_FACE_SIT	(new GrappleTechnique(Stance.FACE_SITTING_BOTTOM, Stance.BALANCED, "Struggle", 0, Stance.BALANCED, GrappleType.BREAK).build()), // Break hold
	BREAK_FREE_ANAL		(new GrappleTechnique(Stance.ANAL_BOTTOM, Stance.BALANCED, "Struggle", 0, Stance.BALANCED, GrappleType.BREAK).build()), // Break hold
	BREAK_FREE_ORAL		(new GrappleTechnique(Stance.FELLATIO_BOTTOM, Stance.BALANCED, "Struggle", 0, Stance.BALANCED, GrappleType.BREAK).build()), // Break hold
	
	INCANTATION 		(new NonAttackTechnique(Stance.BALANCED, Stance.CASTING, "Incantation", 0, 1).build()), 
	
	/* Learnable Skills */
	ARMOR_SUNDER		(new AttackTechnique(Stance.OFFENSIVE, Stance.OFFENSIVE, "Armor Crusher", 1, 7, 4, 0, 1, 0, true, TechniqueHeight.MEDIUM).addBonus(BonusCondition.SKILL_LEVEL, BonusType.ARMOR_SUNDER).build(), 3),
	CAUTIOUS_ATTACK  	(new AttackTechnique(Stance.BALANCED, Stance.DEFENSIVE, "Fade-Away", -1, 0, 2).addBonus(BonusCondition.OUTMANEUVER, BonusType.GUARD_MOD, 25).addBonus(BonusCondition.SKILL_LEVEL, BonusType.GUARD_MOD, 25).build(), 3),
	VAULT 				(new NonAttackTechnique(Stance.OFFENSIVE, Stance.AIRBORNE, "Vault", 2, 4).addBonus(BonusCondition.OUTMANEUVER, BonusType.EVASION, 25).build()), // needs to be changed to evasion mod 
	JUMP_ATTACK 		(new AttackTechnique(Stance.AIRBORNE, Stance.BALANCED, "Falling Crush", 4, 4, 2).addBonus(BonusCondition.ENEMY_ON_GROUND, BonusType.POWER_MOD, 4).addBonus(BonusCondition.ENEMY_ON_GROUND, BonusType.ARMOR_SUNDER, 4).build()),
	VAULT_OVER			(new NonAttackTechnique(Stance.AIRBORNE, Stance.BALANCED, "Vault Over", 1, 1).build()),
	RECKLESS_ATTACK 	(new AttackTechnique(Stance.OFFENSIVE, Stance.OFFENSIVE, "Assault", 2, 3, 6, false).addBonus(BonusCondition.STRENGTH_OVERPOWER, BonusType.KNOCKDOWN, 1).build(), 3), // unguardable
	KNOCK_DOWN 			(new AttackTechnique(Stance.OFFENSIVE, Stance.OFFENSIVE, "Overrun", 1, 3, 6, 1).addBonus(BonusCondition.SKILL_LEVEL, BonusType.KNOCKDOWN, 1).build(), 3), 
	SLIDE				(new NonAttackTechnique(Stance.BALANCED, Stance.KNEELING, "Slide", 1, 4, TechniqueHeight.LOW).addBonus(BonusCondition.ENEMY_LOW_STABILITY, BonusType.TRIP, 80).addBonus(BonusCondition.OUTMANEUVER, BonusType.TRIP, 20).addBonus(BonusCondition.OUTMANEUVER, BonusType.EVASION, 40).build(), 1), 
	TAUNT 				(new NonAttackTechnique(Stance.DEFENSIVE, Stance.DEFENSIVE, "Taunt", 0, 0, true).addBonus(BonusCondition.SKILL_LEVEL, BonusType.POWER_MOD, 1).build(), 2), 
	HIT_THE_DECK		(new FallDownTechnique(Stance.BALANCED, Stance.PRONE, "Hit the Deck").addBonus(BonusCondition.OUTMANEUVER, BonusType.EVASION, 50).build()), 
	FEINT_AND_STRIKE	(new AttackTechnique(Stance.OFFENSIVE, Stance.OFFENSIVE, "Feint Strike", -1, 3, 7).addBonus(BonusCondition.OUTMANEUVER, BonusType.POWER_MOD).addBonus(BonusCondition.OUTMANEUVER, BonusType.EVASION, 25).addBonus(BonusCondition.SKILL_LEVEL, BonusType.STABILTIY_COST, 1).build(), 3), 
	PARRY  				(new GuardTechnique(Stance.DEFENSIVE, Stance.COUNTER, "Parry", -1, 0, 0, false).addBonus(BonusCondition.SKILL_LEVEL, BonusType.PARRY, 1).addBonus(BonusCondition.OUTMANEUVER, BonusType.PARRY, 1).build(), 3),
	UPPERCUT			(new AttackTechnique(Stance.KNEELING, Stance.OFFENSIVE, "Uppercut", 1, 4, 2, true, TechniqueHeight.HIGH).addBonus(BonusCondition.SKILL_LEVEL, BonusType.POWER_MOD).build(), 3),
	COMBAT_HEAL  		(new SpellTechnique(Stance.CASTING, Stance.BALANCED, "Combat Heal", 7, 10, true).addBonus(BonusCondition.SKILL_LEVEL, BonusType.POWER_MOD, 3).build(), 3),
	COMBAT_FIRE  		(new SpellTechnique(Stance.CASTING, Stance.BALANCED, "Combat Fire", 3, 3, false).addBonus(BonusCondition.SKILL_LEVEL, BonusType.POWER_MOD, 2).build(), 3),
	MANA_LASER  		(new SpellTechnique(Stance.CASTING, Stance.BALANCED, "Mana Laser", 3, 10, false).addBonus(BonusCondition.SKILL_LEVEL, BonusType.POWER_MOD, 2).build(), 3),
	TITAN_STRENGTH  	(new SpellTechnique(Stance.CASTING, Stance.BALANCED, "Titan Strength", 0, 2, false, StatusType.STRENGTH_BUFF).build(), 3),
	WEAKENING_CURSE  	(new SpellTechnique(Stance.CASTING, Stance.BALANCED, "Weakening Curse", 8, 7, false, null, StatusType.STRENGTH_DEBUFF).build(), 3),
	
	FOCUS_ENERGY	  	(new SpellTechnique(Stance.CASTING, Stance.BALANCED, "Focus Energy", 4, -5, false, StatusType.ENDURANCE_BUFF).build(), 3)
	;
	
	private final TechniquePrototype trait;
	private final int maxRank;
	private Techniques(TechniquePrototype trait) {
		this(trait, 1);
	}
	private Techniques(TechniquePrototype trait, int maxRank) {
		this.trait = trait;
		this.maxRank = maxRank;
	}
	
	public TechniquePrototype getTrait() { return trait; }
	
	public int getMaxRank() { return maxRank; }
	
	public static Array<Techniques> getLearnableSkills() { return new Array<Techniques>(new Techniques[]{ARMOR_SUNDER, CAUTIOUS_ATTACK, VAULT, RECKLESS_ATTACK, KNOCK_DOWN, TAUNT, UPPERCUT, SECOND_WIND, FEINT_AND_STRIKE, HIT_THE_DECK, PARRY, SLIDE}); }
	public static Array<Techniques> getLearnableSpells() { return new Array<Techniques>(new Techniques[]{COMBAT_HEAL, COMBAT_FIRE, TITAN_STRENGTH}); }
	
	public Techniques getPluggedAlternate() { return YOINK; }
}
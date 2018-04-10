package com.majalis.character;

import com.majalis.character.SexualExperience.SexualExperienceBuilder;
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
import com.majalis.technique.SpellEffect;
import com.majalis.technique.SpellTechnique;
import com.majalis.technique.TechniquePrototype;
import com.majalis.technique.TechniquePrototype.TechniqueHeight;
/*
 * List of all techniques and their generic attributes.
 */
public enum Techniques {
	
	DO_NOTHING			(new NonAttackTechnique(Stance.NULL, Stance.NULL, "Do Nothing", -1, 0).build(), false),
	
	/* Offensive Techniques */  
	POWER_ATTACK 		(new AttackTechnique(Stance.OFFENSIVE, Stance.OFFENSIVE, "Power Attack", 3, 3, 4, TechniqueHeight.LOW).addBonus(BonusCondition.ENEMY_ON_GROUND, BonusType.POWER_MOD, 4).build()),
	TEMPO_ATTACK  		(new AttackTechnique(Stance.OFFENSIVE, Stance.OFFENSIVE, "Tempo Attack", 2, 2, 3, TechniqueHeight.MEDIUM).build()),
	SPRING_ATTACK  		(new AttackTechnique(Stance.BALANCED, Stance.OFFENSIVE, "Spring Attack", 1, 4, 3, TechniqueHeight.MEDIUM).addBonus(BonusCondition.OUTMANEUVER, BonusType.BLEEDING, 1).build(), 5),
	
	/* Blitz Techniques */
	BLITZ_ATTACK  		(new AttackTechnique(Stance.OFFENSIVE, Stance.BLITZ, "Blitz Attack", 4, 4, 5, TechniqueHeight.MEDIUM).addBonus(BonusCondition.OUTMANEUVER, BonusType.BLEEDING, 1).build(), false),
	ALL_OUT_BLITZ		(new AttackTechnique(Stance.BLITZ, Stance.BLITZ, "All-Out Blitz", 5, 4, 7, 1.5).build(), 3),
	HOLD_BACK			(new NonAttackTechnique(Stance.BLITZ, Stance.OFFENSIVE, "Hold Back", 0, 1).build()),
	
	/* Berserk Techniques */
	RAGE				(new AttackTechnique(Stance.BERSERK, Stance.BERSERK, "Rage", 7, 10, 1, TechniqueHeight.MEDIUM).build(), false),
	BERSERK				(new NonAttackTechnique(Stance.OFFENSIVE, Stance.BERSERK, "Berserk", 0, 0).build()),
	
	/* Haymaker Techniques */
	HAYMAKER			(new AttackTechnique(Stance.HAYMAKER, Stance.BALANCED, "Haymaker", 10, 5, 2, TechniqueHeight.HIGH).build(), false),
	REEL_BACK			(new NonAttackTechnique(Stance.OFFENSIVE, Stance.HAYMAKER, "Reel Back", 0, 2).build()),
	
	/* Balanced Techniques */
	RESERVED_ATTACK		(new GuardTechnique	(Stance.OFFENSIVE, Stance.BALANCED, "Combat Guard", 0, 0, 2, true).addBonus(BonusCondition.SKILL_LEVEL, BonusType.PARRY, 1).build()),	
	REVERSAL_ATTACK  	(new AttackTechnique(Stance.DEFENSIVE, Stance.BALANCED, "Reversal Attack", -2, 2, 2, TechniqueHeight.LOW).build()),
	NEUTRAL_ATTACK  	(new AttackTechnique(Stance.BALANCED, Stance.BALANCED, "Low Attack", -1, 0, 1, TechniqueHeight.LOW).addBonus(BonusCondition.ENEMY_LOW_STABILITY, BonusType.TRIP, 60).addBonus(BonusCondition.OUTMANEUVER, BonusType.TRIP, 10).build()),
	USE_ITEM		  	(new NonAttackTechnique(Stance.BALANCED, Stance.ITEM, "Use Item", 0, 0).build(), false),
	ITEM_OR_CANCEL		(new NonAttackTechnique(Stance.ITEM, Stance.BALANCED, "Cancel", 0, 0).setBlockable(false).setParryable(false).build(), false),
	KICK_OVER_FACE_UP	(new NonAttackTechnique(Stance.BALANCED, Stance.BALANCED, "Kick Over", 0, 3, Stance.SUPINE).build(), false),
	KICK_OVER_FACE_DOWN (new NonAttackTechnique(Stance.BALANCED, Stance.BALANCED, "Kick Over", 0, 3, Stance.PRONE).build(), false),
	
	/* Defensive Techniques */
	CAREFUL_ATTACK  	(new AttackTechnique(Stance.DEFENSIVE, Stance.DEFENSIVE, "Careful Attack", -2, 0, 1).build()),
	BLOCK				(new GuardTechnique	(Stance.BALANCED, Stance.DEFENSIVE, "Block", 0, -1, 3, true).addBonus(BonusCondition.SKILL_LEVEL, BonusType.PARRY, 1).build()),//.addBonus(BonusCondition.OUTMANEUVER, BonusType.GUARD_MOD, 25).build()),
	GUARD  				(new GuardTechnique	(Stance.DEFENSIVE, Stance.DEFENSIVE, "Guard", -2, -2, 4, true).addBonus(BonusCondition.SKILL_LEVEL, BonusType.PARRY, 2).build()),//.addBonus(BonusCondition.OUTMANEUVER, BonusType.GUARD_MOD, 25).build()),
	SECOND_WIND			(new NonAttackTechnique(Stance.DEFENSIVE, Stance.DEFENSIVE, "Second Wind", -4, -1).build()),
	SUDDEN_ADVANCE		(new NonAttackTechnique(Stance.DEFENSIVE, Stance.OFFENSIVE, "Sudden Advance", 1, 5).addBonus(BonusCondition.SKILL_LEVEL, BonusType.PARRY, 1).addBonus(BonusCondition.OUTMANEUVER, BonusType.PARRY, 1).build(), 3),	
	
	/* Focus Techniques */
	CENTER				(new NonAttackTechnique(Stance.DEFENSIVE, Stance.FOCUS, "Center", -6, -4).build()),	
	ASHI				(new NonAttackTechnique(Stance.FOCUS, Stance.OFFENSIVE, "Ashi", 0, 1).build()),
	
	/* Stonewall Techniques */
	ABSOLUTE_GUARD		(new GuardTechnique	(Stance.STONEWALL, Stance.STONEWALL, "Absolute Guard", 4, -1, 4, true).addBonus(BonusCondition.SKILL_LEVEL, BonusType.PARRY, 4).build()),
	STONEWALL			(new GuardTechnique	(Stance.DEFENSIVE, Stance.STONEWALL, "Stonewall", -2, -2, 4, true).addBonus(BonusCondition.SKILL_LEVEL, BonusType.PARRY, 2).build()),
	LOWER_GUARD			(new GuardTechnique	(Stance.STONEWALL, Stance.DEFENSIVE, "Lower Guard", -2, -2, 4, true).addBonus(BonusCondition.SKILL_LEVEL, BonusType.PARRY, 2).build()),
	
	/* Counter Techniques */ 			
	RIPOSTE  			(new GuardTechnique(Stance.COUNTER, Stance.BALANCED, "Riposte", -1, 3, 5, false).addBonus(BonusCondition.SKILL_LEVEL, BonusType.PARRY, 2).addBonus(BonusCondition.OUTMANEUVER, BonusType.PARRY, 2).addBonus(BonusCondition.SKILL_LEVEL, BonusType.DISARM, 50).addBonus(BonusCondition.SKILL_LEVEL, BonusType.COUNTER, 50).addBonus(BonusCondition.OUTMANEUVER, BonusType.COUNTER, 50).addBonus(BonusCondition.OUTMANEUVER, BonusType.DISARM, 50).build(), 1),
	EN_GARDE  			(new GuardTechnique(Stance.COUNTER, Stance.DEFENSIVE, "En Garde", -1, 0, 1, false).addBonus(BonusCondition.SKILL_LEVEL, BonusType.PARRY, 2).addBonus(BonusCondition.OUTMANEUVER, BonusType.PARRY, 1).build(), 1),
	PARRY  				(new GuardTechnique(Stance.DEFENSIVE, Stance.COUNTER, "Parry", -1, 0, 0, false).addBonus(BonusCondition.SKILL_LEVEL, BonusType.PARRY, 1).addBonus(BonusCondition.OUTMANEUVER, BonusType.PARRY, 1).build(), 3),	
	
	/* Misc Techniques */
	DRAW_ARROW 			(new NonAttackTechnique(Stance.BALANCED, Stance.DRAWN, "Draw Arrow", 0, 0).build(), false),		
	CANCEL				(new NonAttackTechnique(Stance.DRAWN, Stance.BALANCED, "Cancel", 0, 0).build(), false),
	FIRE				(new AttackTechnique(Stance.DRAWN, Stance.BALANCED, "Fire", 20, 3, 0, TechniqueHeight.MEDIUM).setBlockable(true).setParryable(false).setEvadeable(false).setIgnoreArmor().setRange(2).build(), false),
	
	/* Seduction Techniques */
	SLAP_ASS			(new NonAttackTechnique(Stance.SEDUCTION, Stance.SEDUCTION, "Slap Ass", 0, -1).addSelfSex(new SexualExperienceBuilder().setAssBottomTeasing(1)).addSex(new SexualExperienceBuilder().setAssTeasing(1)).build(), 3), // makes enemy want to fuck your ass, raises own lust
	GESTURE				(new NonAttackTechnique(Stance.SEDUCTION, Stance.SEDUCTION, "Gesture", 0, -1).addSex(new SexualExperienceBuilder().setAssTeasing(1)).build(), 3), // makes enemy want to fuck you
	PUCKER_LIPS			(new NonAttackTechnique(Stance.SEDUCTION, Stance.SEDUCTION, "Pucker Lips", 0, -1).addSelfSex(new SexualExperienceBuilder().setMouthBottomTeasing(1)).addSex(new SexualExperienceBuilder().setMouthTeasing(1)).build(), 3), // makes enemy want to fuck you
	RUB					(new NonAttackTechnique(Stance.SEDUCTION, Stance.SEDUCTION, "Rub", 0, -1).addSelfSex(new SexualExperienceBuilder().setAssTeasing(1)).addSex(new SexualExperienceBuilder().setAssBottomTeasing(1)).build(), 3), // raises own lust, makes enemy want to be fucked by you
	PRESENT				(new NonAttackTechnique(Stance.SEDUCTION, Stance.HANDS_AND_KNEES, "Present", 0, -1).addSelfSex(new SexualExperienceBuilder().setAssBottomTeasing(1)).addSex(new SexualExperienceBuilder().setAssTeasing(1)).build(), 3), // makes enemy want to fuck your ass, puts you in hands and knees
	SLAP_ASS_KNEES 		(new NonAttackTechnique(Stance.HANDS_AND_KNEES, Stance.HANDS_AND_KNEES, "Spank Ass", -1, -1).addSelfSex(new SexualExperienceBuilder().setAssBottomTeasing(1)).addSex(new SexualExperienceBuilder().setAssTeasing(1)).build(), 3), // makes enemy want to fuck your ass, raises own lust (by more if catamite/slut)
	
	/* Techniques from Prone/Supine */
	KIP_UP				(new NonAttackTechnique(Stance.PRONE, Stance.BALANCED, "Kip Up", 5, -15).build(), false),
	STAND_UP_KNEELING	(new NonAttackTechnique(Stance.PRONE, Stance.BALANCED, "Stand Up", 1, -15).build(), false),
	STAND_UP			(new NonAttackTechnique(Stance.PRONE, Stance.BALANCED, "Stand Up", 3, -6).build(), false),
	STAND_UP_HANDS		(new NonAttackTechnique(Stance.HANDS_AND_KNEES, Stance.BALANCED, "Stand Up", 1, -15).build(), false),
	KNEE_UP				(new NonAttackTechnique(Stance.PRONE, Stance.KNEELING, "Knee Up", 1, -15).build(), false),
	KNEE_UP_HANDS		(new NonAttackTechnique(Stance.HANDS_AND_KNEES, Stance.KNEELING, "Knee Up", -1, -15).build(), false),
	PUSH_UP				(new NonAttackTechnique(Stance.PRONE, Stance.HANDS_AND_KNEES, "Push Up", -1, -15).build(), false),
	STAY_KNELT			(new NonAttackTechnique(Stance.KNEELING, Stance.KNEELING, "Stay Knelt", -1, -1).build(), false),
	STAY				(new NonAttackTechnique(Stance.HANDS_AND_KNEES, Stance.HANDS_AND_KNEES, "Stay", -1, -1).build(), false),
	REST				(new NonAttackTechnique(Stance.SUPINE, Stance.SUPINE, "Rest", -1, -1).build(), false),
	REST_FACE_DOWN		(new NonAttackTechnique(Stance.PRONE, Stance.PRONE, "Rest", -1, -1).build(), false),
	ROLL_OVER_UP		(new NonAttackTechnique(Stance.PRONE, Stance.SUPINE, "Roll Over", -1, -1).build(), false),
	ROLL_OVER_DOWN		(new NonAttackTechnique(Stance.SUPINE, Stance.PRONE, "Roll Over", -1, -1).build(), false),
	
	/* Positional */
	DUCK				(new NonAttackTechnique(Stance.BALANCED, Stance.KNEELING, "Duck", 0, 0).build(), false),	 
	FLY					(new NonAttackTechnique(Stance.BALANCED, Stance.AIRBORNE, "Fly", 0, 0).build(), false),	 
	
	/* Enemy attacks */
	SLIME_ATTACK 		(new AttackTechnique(Stance.BALANCED, Stance.BALANCED, "Slime Attack", 7, 5, 0).build(), false),
	SLIME_QUIVER 		(new NonAttackTechnique(Stance.BALANCED, Stance.DEFENSIVE, "Slime Quiver", -1, -1).build(), false),
	
	ACTIVATE		  	(new SpellTechnique(Stance.CASTING, Stance.BALANCED, "Activate", 0, 0, SpellEffect.NONE, StatusType.ACTIVATE).build(), false),
	ANGELIC_GRACE  		(new SpellTechnique(Stance.CASTING, Stance.BALANCED, "Angelic Grace", 80, 0, SpellEffect.HEALING).build(), false),
	TRUMPET		  		(new SpellTechnique(Stance.CASTING, Stance.BALANCED, "Trumpet", -1, 0, SpellEffect.DAMAGE).build(), false),
	HEAL  				(new SpellTechnique(Stance.CASTING, Stance.BALANCED, "Heal", 27, 10, SpellEffect.HEALING).build(), false),
	
	GUT_CHECK			(new AttackTechnique(Stance.OFFENSIVE, Stance.OFFENSIVE, "Gutcheck", 3, 3, 4, 0, 1, false, TechniqueHeight.MEDIUM).build(), false),

	RIP					(new AttackTechnique(Stance.BALANCED, Stance.BALANCED, "Rip", -2, 3, 3, 0, 5, 0, false, TechniqueHeight.LOW).build(), false),
	SMASH				(new AttackTechnique(Stance.OFFENSIVE, Stance.BALANCED, "Smash", 4, 5, 5, 1.5).build(), false),
	LIFT_WEAPON			(new NonAttackTechnique(Stance.BALANCED, Stance.OFFENSIVE, "Raise Club", 1, 1).build(), false),
	SLAM				(new AttackTechnique(Stance.BALANCED, Stance.OFFENSIVE, "Slam", -2, 3, 3).build(), false),
	
	COCK_SLAM			(new AttackTechnique(Stance.BALANCED, Stance.BALANCED, "Drop Her Cock on You", 0, 0, 0, TechniqueHeight.NONE).setBlockable(false).setParryable(false).setEvadeable(false).build(), false),	
	
	WRAP				(new GrappleTechnique(Stance.BALANCED, Stance.WRAPPED, "Wrap", 0, Stance.WRAPPED_BOTTOM, GrappleType.ADVANTAGE).build(), false), // when enemy is prone
	BITE				(new GrappleTechnique(Stance.WRAPPED, Stance.WRAPPED, "Bite", 4, GrappleType.SUBMIT).setIgnoreArmor().setBleed(1).build(), false),	
	SQUEEZE				(new GrappleTechnique(Stance.WRAPPED, Stance.WRAPPED, "Squeeze", 4, GrappleType.ADVANTAGE).addBonus(BonusCondition.STRENGTH_OVERPOWER, BonusType.GRAPPLE).build(), false),
	SQUEEZE_CRUSH		(new GrappleTechnique(Stance.WRAPPED, Stance.WRAPPED, "Crush", 4, GrappleType.SUBMIT).setAutoDamage().build(), false),
	SQUEEZE_RELEASE		(new GrappleTechnique(Stance.WRAPPED, Stance.PRONE, "Release", -1, Stance.PRONE, GrappleType.WIN).build(), false),		
	
	SQUEEZE_STRUGGLE	(new GrappleTechnique(Stance.WRAPPED_BOTTOM, Stance.WRAPPED_BOTTOM, "Struggle", 5, GrappleType.ADVANTAGE).build(), false),
	BREAK_FREE_SQUEEZE	(new GrappleTechnique(Stance.WRAPPED_BOTTOM, Stance.PRONE, "Break Free", 0, Stance.PRONE, GrappleType.BREAK).build(), false), // Break hold
	SQUEEZE_REST		(new GrappleTechnique(Stance.WRAPPED_BOTTOM, Stance.WRAPPED_BOTTOM, "Rest", -1, GrappleType.SUBMIT).build(), false),	
	
	WATCH				(new NonAttackTechnique(Stance.NULL, Stance.NULL, "Watch", -1, -1).build(), false),
	LICK_LIPS			(new NonAttackTechnique(Stance.NULL, Stance.NULL, "Lick Lips", -1, -1).build(), false),
	PREPARE_OILS		(new NonAttackTechnique(Stance.NULL, Stance.NULL, "Prepare Oils", -1, -1).build(), false),
	LUBE_UP				(new NonAttackTechnique(Stance.NULL, Stance.NULL, "Lube Up", -1, -1).build(), false),
	HARDEN				(new NonAttackTechnique(Stance.NULL, Stance.NULL, "Harden", -5, 0).addSelfSex(new SexualExperienceBuilder().setAssTeasing(4)).build(), false), 
	SKEWER		 		(new GrappleTechnique(Stance.HOLDING, Stance.CRUSHING, "Skewer", 2, Stance.PENETRATED).build(), false), 
	SCREW		 		(new GrappleTechnique(Stance.CRUSHING, Stance.CRUSHING, "Screw", 2, Stance.PENETRATED).build(), false), 
	FERTILIZE 			(new ClimaxTechnique(Stance.CRUSHING, Stance.CRUSHING, "Fertilize", Stance.PENETRATED, ClimaxType.ANAL).build(), false),
	PLUG 				(new NonAttackTechnique(Stance.CRUSHING, Stance.CRUSHING, "Plug", 0, 0, Stance.SPREAD).build(), false),
	LICK_LIPS_HOLDING  	(new GrappleTechnique(Stance.HOLDING, Stance.HOLDING, "Lick Lips", -1, Stance.HELD, GrappleType.WIN).build(), false),
	
	/* Enemy pouncing */
	DIVEBOMB 			(new GrappleTechnique  (Stance.AIRBORNE, Stance.FELLATIO, "Divebomb", 2, Stance.FELLATIO_BOTTOM, TechniqueHeight.HIGH, GrappleType.WIN, "").build(), false),
	SAY_AHH 			(new GrappleTechnique  (Stance.BALANCED, Stance.FELLATIO, "Say 'Ahh'", 2, Stance.FELLATIO_BOTTOM, GrappleType.WIN).addBonus(BonusCondition.OUTMANEUVER, BonusType.PRIORITY).build(), false),
	FULL_NELSON			(new GrappleTechnique  (Stance.BALANCED, Stance.FULL_NELSON, "Full Nelson", 2, Stance.FULL_NELSON_BOTTOM, GrappleType.ADVANTAGE).build(), false), // Used to initiate full nelson
	POUNCE_DOGGY		(new GrappleTechnique  (Stance.BALANCED, Stance.DOGGY, "Pounce", 2, Stance.DOGGY_BOTTOM, GrappleType.WIN).build(), false), // Used to initiate doggy
	MATING				(new GrappleTechnique  (Stance.BALANCED, Stance.DOGGY, "Mating", 2, Stance.DOGGY_BOTTOM, GrappleType.WIN).build(), false), // Used to initiate doggy
	POUNCE_PRONE_BONE	(new GrappleTechnique  (Stance.BALANCED, Stance.PRONE_BONE, "Pounce", 2, Stance.PRONE_BONE_BOTTOM, GrappleType.WIN).build(), false), // Used to initiate prone-bone
	POUNCE_ANAL			(new GrappleTechnique  (Stance.BALANCED, Stance.ANAL, "Pounce", 2, Stance.ANAL_BOTTOM, GrappleType.WIN).build(), false), // Used to initiate missionary
	PENETRATE_STANDING	(new GrappleTechnique  (Stance.FULL_NELSON, Stance.STANDING, "Penetrate", 2, Stance.STANDING_BOTTOM, GrappleType.WIN).build(), false), // Used to initiate standing anal
	SEIZE				(new GrappleTechnique  (Stance.BALANCED, Stance.HOLDING, "Seize", 2, Stance.HELD, GrappleType.WIN).build(), false), // Used to initiate giant sex
	FACE_SIT			(new GrappleTechnique  (Stance.BALANCED, Stance.FACE_SITTING, "Plop Down", 2, Stance.FACE_SITTING_BOTTOM, GrappleType.ADVANTAGE).build(), false), // Used to initiate face-sitting
	SITTING_ORAL		(new GrappleTechnique  (Stance.FACE_SITTING, Stance.SIXTY_NINE, "Say 'Ahh'", 2, Stance.SIXTY_NINE_BOTTOM, GrappleType.WIN).build(), false), // Used to initiate sixty nine
	OUROBOROS			(new GrappleTechnique  (Stance.BALANCED, Stance.OUROBOROS, "Ouroboros", 2, Stance.OUROBOROS_BOTTOM, GrappleType.WIN).build(), false), // Used to initiate ouroboros
	MOUNT_FACE			(new GrappleTechnique  (Stance.BALANCED, Stance.FACEFUCK, "Mount Face", 2, Stance.FACEFUCK_BOTTOM, GrappleType.WIN).build(), false), // Used to initiate facefuck
	
	YOINK				(new GrappleTechnique  (Stance.NULL, Stance.NULL, "Yoink", 0, GrappleType.NULL).addBonus(BonusCondition.SKILL_LEVEL, BonusType.REMOVE_PLUG).build(), false), // Used to initiate doggy
	
	KNOT 				(new NonAttackTechnique(Stance.DOGGY, Stance.KNOTTED, "Knot", 0, 0, Stance.KNOTTED_BOTTOM, "Set Damage").build(), false), // Used to knot by knotty weresluts and others
	MOUTH_KNOT 				(new NonAttackTechnique(Stance.FELLATIO, Stance.MOUTH_KNOTTED, "Knot", 0, 0, Stance.MOUTH_KNOTTED_BOTTOM, "Set Damage").build(), false), // Used to knot by knotty weresluts and others
	
	OVIPOSITION			(new NonAttackTechnique(Stance.FULL_NELSON, Stance.OVIPOSITION, "Oviposition", 0, 0, Stance.OVIPOSITION_BOTTOM).build(), false), // Used to oviposition
	
	IRRUMATIO 			(new GrappleTechnique(Stance.FELLATIO, Stance.FELLATIO, "Irrumatio", 1).build()), 
	FORCE_DEEPTHROAT	(new GrappleTechnique(Stance.FELLATIO, Stance.FELLATIO, "Deepthroat", 1).addSex(new SexualExperienceBuilder().setMouthBottomTeasing(1)).addSelfSex(new SexualExperienceBuilder().setMouthTeasing(1)).build()),
	GRIP	 			(new GrappleTechnique(Stance.FULL_NELSON, Stance.FULL_NELSON, "Grip", 3, GrappleType.ADVANTAGE).build()), // Used to grapple
	HOLD	 			(new GrappleTechnique(Stance.FULL_NELSON, Stance.FULL_NELSON, "Hold", -1).build()), // Used to hold
	TAKEDOWN			(new GrappleTechnique(Stance.FULL_NELSON, Stance.GROUND_WRESTLE, "Takedown", 6, Stance.GROUND_WRESTLE_FACE_DOWN, GrappleType.HOLD).build()),
	POUND_DOGGY 		(new GrappleTechnique(Stance.DOGGY, Stance.DOGGY, "Pound", 2, "Pound that ass.").build()), // Used to doggystyle
	SPANK		 		(new GrappleTechnique(Stance.DOGGY, Stance.DOGGY, "Spank", 2, "Spank that ass while you pound it.").addSex(new SexualExperienceBuilder().setAssBottomTeasing(1)).addSelfSex(new SexualExperienceBuilder().setAssTeasing(1)).build()), 
	ASS_BLAST			(new GrappleTechnique(Stance.DOGGY, Stance.DOGGY, "Ass Blast", 4, "Give that ass a whole dick like a jackhammer.").addSex(new SexualExperienceBuilder().setAssBottomTeasing(1)).addSelfSex(new SexualExperienceBuilder().setAssTeasing(1)).build()), 
	CRUSH_ASS	 		(new GrappleTechnique(Stance.DOGGY, Stance.DOGGY, "Crush Ass", 4, "Give it to that ass deep and hard.").addSex(new SexualExperienceBuilder().setAssBottomTeasing(1)).build()),
	PROSTATE_GRIND 		(new GrappleTechnique(Stance.DOGGY, Stance.DOGGY, "Prostate Grind", 2, "Grind it inside against their prostate or whatever else.").addSex(new SexualExperienceBuilder().setAssBottomTeasing(3)).addSelfSex(new SexualExperienceBuilder().setAssTeasing(2)).build()), 
	POUND_PRONE_BONE	(new GrappleTechnique(Stance.PRONE_BONE, Stance.PRONE_BONE, "Prone Bone", 2, "Pound them on the ground.").build()), // Used to prone bone
	ROUND_AND_ROUND		(new GrappleTechnique(Stance.OUROBOROS, Stance.OUROBOROS, "Round and Round", 2).build()), // Used to ouroboros
	FACEFUCK			(new GrappleTechnique(Stance.FACEFUCK, Stance.FACEFUCK, "Facefuck", 2, "Fuck their face on the ground.").build()), 
	POUND_ANAL 			(new GrappleTechnique(Stance.ANAL, Stance.ANAL, "Pound", 2, "Fuck them lovingly.").build()), // Used to missionary
	POUND_STANDING 		(new GrappleTechnique(Stance.STANDING, Stance.STANDING, "Pound", 2, "Fuck them right up the ass standing.").build()), // Used to standing anal
	OGRE_SMASH	 		(new GrappleTechnique(Stance.HOLDING, Stance.CRUSHING, "Ogre Smash", 2, Stance.PENETRATED).build(), false), // Used to bring down onto giant's cock first time
	CRUSH		 		(new GrappleTechnique(Stance.CRUSHING, Stance.CRUSHING, "Crush", 2, Stance.PENETRATED).build(), false), // Used to bring down onto giant's cock
	PULL_UP		 		(new GrappleTechnique(Stance.CRUSHING, Stance.CRUSHING, "Pull Up", 2, Stance.SPREAD).build(), false), // Used to bring down onto giant's cock
	
	RIDE_FACE			(new GrappleTechnique(Stance.FACE_SITTING, Stance.FACE_SITTING, "Ride", 0).build()), // Used to faceride
	RECIPROCATE			(new GrappleTechnique(Stance.SIXTY_NINE, Stance.SIXTY_NINE, "Reciprocate", 0).build()), // Used to sixty nine
	KNOT_BANG 			(new GrappleTechnique(Stance.KNOTTED, Stance.KNOTTED, "Knot Bang", 0, Stance.KNOTTED_BOTTOM).build(), false), // Used to knot by knotty weresluts and others - could end the battle
	MOUTH_KNOT_BANG 	(new GrappleTechnique(Stance.MOUTH_KNOTTED, Stance.MOUTH_KNOTTED, "Knot Bang", 0, Stance.MOUTH_KNOTTED_BOTTOM).build(), false), // Used to knot by knotty weresluts and others - could end the battle
	
	LAY_EGGS 			(new GrappleTechnique(Stance.OVIPOSITION, Stance.OVIPOSITION, "Lay Eggs", 0, Stance.OVIPOSITION_BOTTOM).build(), false), // Used to lay eggs - can end battle
	
	BLOW_LOAD 			(new ClimaxTechnique(Stance.DOGGY, Stance.DOGGY, "Blow Load", Stance.PRONE, ClimaxType.ANAL).build(), false),
	BLOW_LOAD_ORAL		(new ClimaxTechnique(Stance.FELLATIO, Stance.FELLATIO, "Blow Load", Stance.KNEELING, ClimaxType.ORAL).build(), false),
	ERUPT_ANAL 			(new ClimaxTechnique(Stance.DOGGY, Stance.DOGGY, "Erupt", Stance.PRONE, ClimaxType.ANAL).build(), false),
	ERUPT_ORAL 			(new ClimaxTechnique(Stance.FELLATIO, Stance.FELLATIO, "Erupt", Stance.KNEELING, ClimaxType.ORAL).build(), false),
	ERUPT_FACIAL		(new ClimaxTechnique(Stance.HANDY, Stance.HANDY, "Facial", Stance.KNEELING, ClimaxType.FACIAL).build(), false),
	ERUPT_COWGIRL		(new ClimaxTechnique(Stance.COWGIRL, Stance.SUPINE, "Erupt", Stance.KNEELING, ClimaxType.ANAL).build(), false),
	ERUPT_SIXTY_NINE	(new ClimaxTechnique(Stance.SIXTY_NINE, Stance.KNEELING, "Erupt", Stance.SUPINE, ClimaxType.ORAL).build(), false),
	
	RECEIVE_HANDY		(new GrappleTechnique(Stance.HANDY, Stance.HANDY, "Receive Handy", 0).build()),
	BE_RIDDEN			(new GrappleTechnique(Stance.COWGIRL, Stance.COWGIRL, "Be Ridden", 0).build()),
	BE_RIDDEN_REVERSE	(new GrappleTechnique(Stance.REVERSE_COWGIRL, Stance.REVERSE_COWGIRL, "Be Ridden", 0).build()),
	PUSH_OFF			(new GrappleTechnique(Stance.COWGIRL, Stance.BALANCED, "Push Off", 0, Stance.BALANCED).build()), // Break hold
	PUSH_OFF_ATTEMPT	(new GrappleTechnique(Stance.COWGIRL, Stance.COWGIRL, "Push Off", 4).build()),
	PUSH_OFF_REVERSE	(new GrappleTechnique(Stance.COWGIRL, Stance.BALANCED, "Push Off", 0, Stance.BALANCED).build()), // Break hold
	PUSH_OFF_ATTEMPT_REVERSE	(new GrappleTechnique(Stance.COWGIRL, Stance.COWGIRL, "Push Off", 4).build()),
	
	SIT_ON_IT			(new GrappleTechnique(Stance.BALANCED, Stance.COWGIRL_BOTTOM, "Sit on It", 1, Stance.COWGIRL, "Sit down on it - and yes, it's going right up there.\nDon't say I didn't warn you.").build(), false), 
	TURN_AND_SIT		(new GrappleTechnique(Stance.BALANCED, Stance.REVERSE_COWGIRL_BOTTOM, "Turn and Sit", 1, Stance.REVERSE_COWGIRL, "Turn around and sit on it - and yes, it's going right up there.\nBut at least you'll give 'em a show.").build(), false), 
	
	OPEN_WIDE 			(new GrappleTechnique(Stance.HANDY_BOTTOM, Stance.FELLATIO_BOTTOM, "Open Wide",  1, Stance.FELLATIO, "Open wide and swallow it down.").build()), 
	GRAB_IT				(new GrappleTechnique(Stance.KNEELING, Stance.HANDY_BOTTOM, "Grab It", 1, Stance.HANDY, "Grab it.").addBonus(BonusCondition.OUTMANEUVER, BonusType.PRIORITY).build()), 
		
	RIDE_ON_IT			(new EroticTechnique(Stance.COWGIRL_BOTTOM, Stance.COWGIRL_BOTTOM, "Ride on It", -1, 0, "Keep it deep and stir it around inside.").build()), 
	BOUNCE_ON_IT		(new EroticTechnique(Stance.COWGIRL_BOTTOM, Stance.COWGIRL_BOTTOM, "Bounce on It", -1, 0, "Bounce up and down on it.").build()), 
	SQUEEZE_IT			(new EroticTechnique(Stance.COWGIRL_BOTTOM, Stance.COWGIRL_BOTTOM, "Squeeze It", -1, 0, "Squeeze it with your hole.").build()),
	
	RIDE_ON_IT_REVERSE	(new EroticTechnique(Stance.REVERSE_COWGIRL_BOTTOM, Stance.REVERSE_COWGIRL_BOTTOM, "Ride on It", -1, 0, "Keep it deep and stir it around inside.").build()), 
	BOUNCE_ON_IT_REVERSE	(new EroticTechnique(Stance.REVERSE_COWGIRL_BOTTOM, Stance.REVERSE_COWGIRL_BOTTOM, "Bounce on It", -1, 0, "Bounce up and down on it.").build()), 
	SQUEEZE_IT_REVERSE	(new EroticTechnique(Stance.REVERSE_COWGIRL_BOTTOM, Stance.REVERSE_COWGIRL_BOTTOM, "Squeeze It", -1, 0, "Squeeze it with your hole.").build()),
	
	STAND_OFF_IT		(new GrappleTechnique(Stance.COWGIRL_BOTTOM, Stance.BALANCED, "Stand up off It", 1, Stance.SUPINE, TechniqueHeight.NONE, GrappleType.BREAK, "Get up off it.").build()), 
	PULL_OUT			(new GrappleTechnique(Stance.DOGGY, Stance.BALANCED, "Pull Out", 1, Stance.PRONE, TechniqueHeight.NONE, GrappleType.BREAK, "Pull out.").build(), false), 
	PULL_OUT_ANAL		(new GrappleTechnique(Stance.ANAL, Stance.KNEELING, "Pull Out", 1, Stance.SUPINE, TechniqueHeight.NONE, GrappleType.BREAK, "Pull out.").build(), false), 
	PULL_OUT_ORAL		(new GrappleTechnique(Stance.FELLATIO, Stance.BALANCED, "Pull Out", 1, Stance.KNEELING, TechniqueHeight.NONE, GrappleType.BREAK, "Pull Out.").build(), false), 
	PULL_OUT_STANDING	(new GrappleTechnique(Stance.STANDING, Stance.BALANCED, "Pull Out", 1, Stance.BALANCED, TechniqueHeight.NONE, GrappleType.BREAK, "Pull Out.").build(), false), 
	
	STROKE_IT			(new EroticTechnique(Stance.HANDY_BOTTOM, Stance.HANDY_BOTTOM, "Stroke It", 2, 0, "Stroke it up and down.").build()), 
	TANDEM_STROKE		(new EroticTechnique(Stance.HANDY_BOTTOM, Stance.HANDY_BOTTOM, "Tandem Stroke", 2, 0, "Stroke it and your own at the same time, perv.").addSelfSex(new SexualExperienceBuilder().setMouthBottomTeasing(1)).build()), 
	FONDLE_BALLS		(new EroticTechnique(Stance.HANDY_BOTTOM, Stance.HANDY_BOTTOM, "Fondle Balls", 3, 0, "Fondle their balls while stroking.").addSex(new SexualExperienceBuilder().setMouthTeasing(1)).build()), 
	KISS_IT				(new EroticTechnique(Stance.HANDY_BOTTOM, Stance.HANDY_BOTTOM, "Kiss It", 4, 0, "Give it a nice kiss while you stroke it.").addSelfSex(new SexualExperienceBuilder().setMouthBottomTeasing(2)).addSex(new SexualExperienceBuilder().setMouthTeasing(2)).build()), 
	KISS_BALLS			(new EroticTechnique(Stance.HANDY_BOTTOM, Stance.HANDY_BOTTOM, "Kiss Balls", 4, 0, "Give their sack a nice smooch while you stroke it.").addSelfSex(new SexualExperienceBuilder().setMouthBottomTeasing(2)).addSex(new SexualExperienceBuilder().setMouthTeasing(2)).build()), 	
	SPIT_ON_IT			(new EroticTechnique(Stance.HANDY_BOTTOM, Stance.HANDY_BOTTOM, "Spit on It", 5, 0, "Give it a nice spitshine. For what, I wonder?").addSelfSex(new SexualExperienceBuilder().setMouthBottomTeasing(2)).addSex(new SexualExperienceBuilder().setMouthTeasing(2)).build()), 	
	OPEN_UP				(new EroticTechnique(Stance.HANDY_BOTTOM, Stance.HANDY_BOTTOM, "Open Up", 3, 0, "Tease them by opening your mouth, giving them a target for their cock or cum.").addSelfSex(new SexualExperienceBuilder().setMouthBottomTeasing(4)).addSex(new SexualExperienceBuilder().setMouthTeasing(2)).build()), 
	LET_GO				(new GrappleTechnique(Stance.HANDY_BOTTOM, Stance.KNEELING, "Let It Go", 1, Stance.BALANCED, "Let go of it.").build()), 
	
	RECEIVE_DOGGY		(new EroticTechnique(Stance.DOGGY_BOTTOM, Stance.DOGGY_BOTTOM, "Receive", -1, 0, "Take it up the butt on your knees.").build()), 
	PUSH_BACK_DOGGY		(new EroticTechnique(Stance.DOGGY_BOTTOM, Stance.DOGGY_BOTTOM, "Push Back", -1, 0, "Push your ass back at them. Maybe they'll pull your hair...").addSelfSex(new SexualExperienceBuilder().setAssBottomTeasing(1)).addSex(new SexualExperienceBuilder().setAssTeasing(1)).build()),
	SPREAD_DOGGY		(new EroticTechnique(Stance.DOGGY_BOTTOM, Stance.DOGGY_BOTTOM, "Spread Cheeks", -1, 0, "Reach back and spread your ass for them while they plow you. Signals you want them to cum inside.").addSelfSex(new SexualExperienceBuilder().setAssBottomTeasing(2)).addSex(new SexualExperienceBuilder().setAssTeasing(2)).build()),
	RECEIVE_PRONE_BONE	(new EroticTechnique(Stance.PRONE_BONE_BOTTOM, Stance.PRONE_BONE_BOTTOM, "Receive", -1, 0, "Take it up the butt face-down.").build()), 
	SELF_SPANK			(new EroticTechnique(Stance.DOGGY_BOTTOM, Stance.DOGGY_BOTTOM, "Self Spank", -1, 0, "Spank your own ass and ask for more.").addSelfSex(new SexualExperienceBuilder().setAssBottomTeasing(1)).addSex(new SexualExperienceBuilder().setAssTeasing(1)).build()), 
	RECEIVE_ANAL		(new EroticTechnique(Stance.ANAL_BOTTOM, Stance.ANAL_BOTTOM, "Receive", -1, 0, "Take it face to face.").build()), 
	STROKE				(new EroticTechnique(Stance.ANAL_BOTTOM, Stance.ANAL_BOTTOM, "Stroke", -1, 0, "Stroke it while you take it face to face.").addSelfSex(new SexualExperienceBuilder().setAssBottomTeasing(1)).build()), 
	STROKE_DOGGY		(new EroticTechnique(Stance.DOGGY_BOTTOM, Stance.DOGGY_BOTTOM, "Stroke", -1, 0, "Stroke it while you take it up the ass.").addSelfSex(new SexualExperienceBuilder().setAssBottomTeasing(1)).build()), 
	STROKE_STANDING		(new EroticTechnique(Stance.STANDING_BOTTOM, Stance.STANDING_BOTTOM, "Stroke", -1, 0, "Stroke it while you you take it up the butt.").addSelfSex(new SexualExperienceBuilder().setAssBottomTeasing(1)).build()), 
	WRAP_LEGS			(new EroticTechnique(Stance.ANAL_BOTTOM, Stance.ANAL_BOTTOM, "Wrap Legs", -1, 0, "Wrap your legs around them so they cum inside.").addSelfSex(new SexualExperienceBuilder().setAssBottomTeasing(1)).addSex(new SexualExperienceBuilder().setAssTeasing(3)).build()), 
	POUT				(new EroticTechnique(Stance.ANAL_BOTTOM, Stance.ANAL_BOTTOM, "Pout", -1, 0, "Make a pathetic face. Your poor butt.").build()), 
	RECEIVE_STANDING	(new EroticTechnique(Stance.STANDING_BOTTOM, Stance.STANDING_BOTTOM, "Receive", -1, 0, "Take it up the butt while standing.").build()), 
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
	SUCK_AND_STROKE		(new EroticTechnique(Stance.FELLATIO_BOTTOM, Stance.FELLATIO_BOTTOM, "Suck and Stroke", 1, 0, "Suck it while stroking it up and down like a pro.").addSex(new SexualExperienceBuilder().setMouthTeasing(1)).build()), 
	SUCK_AND_BEAT		(new EroticTechnique(Stance.FELLATIO_BOTTOM, Stance.FELLATIO_BOTTOM, "Suck and Beat", -1, 0, "Suck it while stroking yourself like a slut.").addSelfSex(new SexualExperienceBuilder().setMouthBottomTeasing(1)).build()), 
	DEEPTHROAT			(new EroticTechnique(Stance.FELLATIO_BOTTOM, Stance.FELLATIO_BOTTOM, "Deepthroat", 3, 0, "Take it down your throat.").addSelfSex(new SexualExperienceBuilder().setMouthBottomTeasing(1)).addSex(new SexualExperienceBuilder().setMouthTeasing(2)).build()), 
	LICK_BALLS			(new EroticTechnique(Stance.FELLATIO_BOTTOM, Stance.FELLATIO_BOTTOM, "Lick Balls", 3, 0, "Take it down your throat and licks their balls.").addSelfSex(new SexualExperienceBuilder().setMouthBottomTeasing(2)).addSex(new SexualExperienceBuilder().setMouthTeasing(3)).build()), 
	BLOW				(new EroticTechnique(Stance.FELLATIO_BOTTOM, Stance.FELLATIO_BOTTOM, "Blow", 0, 0, "Put the \"blow\" in blowjob.").addSelfSex(new SexualExperienceBuilder().setMouthBottomTeasing(1)).addSex(new SexualExperienceBuilder().setMouthTeasing(1)).build()), 
	RECEIVE_OUROBOROS	(new EroticTechnique(Stance.OUROBOROS_BOTTOM, Stance.OUROBOROS_BOTTOM, "Round and Round", -1, 0, "Open wide and swallow it down.").build()), 
	GET_FACEFUCKED		(new EroticTechnique(Stance.FACEFUCK_BOTTOM, Stance.FACEFUCK_BOTTOM, "Get Facefucked", -1, 0, "Open wide and swallow it down.").build()), 
	
	/* Ground Wrestling */
	WRESTLE_TO_GROUND	(new GrappleTechnique(Stance.BALANCED, Stance.GROUND_WRESTLE, "Wrestle", 0, Stance.GROUND_WRESTLE_FACE_DOWN, GrappleType.ADVANTAGE).build(), false), // when enemy is prone
	WRESTLE_TO_GROUND_UP(new GrappleTechnique(Stance.BALANCED, Stance.GROUND_WRESTLE, "Wrestle", 0, Stance.GROUND_WRESTLE_FACE_UP, GrappleType.ADVANTAGE).build(), false), // when enemy is supine
	
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
	BREAK_FREE_GROUND	(new GrappleTechnique(Stance.GROUND_WRESTLE_FACE_DOWN, Stance.PRONE, "Release", 0, Stance.PRONE, GrappleType.BREAK).build()), // Break hold
	GRIND				(new GrappleTechnique(Stance.GROUND_WRESTLE_FACE_DOWN, Stance.GROUND_WRESTLE_FACE_DOWN, "Grind", 0, GrappleType.SUBMIT).addSelfSex(new SexualExperienceBuilder().setAssBottomTeasing(1)).addSex(new SexualExperienceBuilder().setAssTeasing(1)).build()),
	REST_GROUND_DOWN	(new GrappleTechnique(Stance.GROUND_WRESTLE_FACE_DOWN, Stance.GROUND_WRESTLE_FACE_DOWN, "Rest", -1, GrappleType.SUBMIT).build()),	
	
	STRUGGLE_GROUND_UP	(new GrappleTechnique(Stance.GROUND_WRESTLE_FACE_UP, Stance.GROUND_WRESTLE_FACE_UP, "Grapple", 4, GrappleType.ADVANTAGE).build()),
	BREAK_FREE_GROUND_UP(new GrappleTechnique(Stance.GROUND_WRESTLE_FACE_UP, Stance.SUPINE, "Release", 0, Stance.PRONE, GrappleType.BREAK).build()), // Break hold
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
	BREAK_FREE_FULL_NELSON 	(new GrappleTechnique(Stance.FULL_NELSON_BOTTOM, Stance.BALANCED, "Break Free", 0, Stance.BALANCED, GrappleType.BREAK).build()), // Break hold
	BREAK_FREE_FACE_SIT	(new GrappleTechnique(Stance.FACE_SITTING_BOTTOM, Stance.BALANCED, "Break Free", 0, Stance.BALANCED, GrappleType.BREAK).build()), // Break hold
	BREAK_FREE_ANAL		(new GrappleTechnique(Stance.ANAL_BOTTOM, Stance.BALANCED, "Break Free", 0, Stance.BALANCED, GrappleType.BREAK).build()), // Break hold
	BREAK_FREE_ORAL		(new GrappleTechnique(Stance.FELLATIO_BOTTOM, Stance.BALANCED, "Break Free", 0, Stance.BALANCED, GrappleType.BREAK).build()), // Break hold
	
	INCANTATION 		(new NonAttackTechnique(Stance.BALANCED, Stance.CASTING, "Incantation", 0, 1).build(), false), 
	
	/* Learnable Skills */
	ARMOR_SUNDER		(new AttackTechnique(Stance.OFFENSIVE, Stance.OFFENSIVE, "Armor Crusher", 1, 7, 4, 0, 1, 0, true, TechniqueHeight.MEDIUM).addBonus(BonusCondition.SKILL_LEVEL, BonusType.ARMOR_SUNDER).build(), 3),
	CAUTIOUS_ATTACK  	(new AttackTechnique(Stance.BALANCED, Stance.DEFENSIVE, "Fade-Away", -1, 0, 2, TechniqueHeight.LOW).addBonus(BonusCondition.OUTMANEUVER, BonusType.GUARD_MOD, 1).addBonus(BonusCondition.SKILL_LEVEL, BonusType.GUARD_MOD, 1).build(), 3),
	VAULT 				(new NonAttackTechnique(Stance.OFFENSIVE, Stance.AIRBORNE, "Vault", 2, 4).addBonus(BonusCondition.OUTMANEUVER, BonusType.EVASION, 1).build()), 
	JUMP_ATTACK 		(new AttackTechnique(Stance.AIRBORNE, Stance.BALANCED, "Falling Crush", 4, 4, 2).addBonus(BonusCondition.ENEMY_ON_GROUND, BonusType.POWER_MOD, 4).addBonus(BonusCondition.ENEMY_ON_GROUND, BonusType.ARMOR_SUNDER, 4).build()),
	VAULT_OVER			(new NonAttackTechnique(Stance.AIRBORNE, Stance.BALANCED, "Vault Over", 1, 1).build()),
	RECKLESS_ATTACK 	(new AttackTechnique(Stance.OFFENSIVE, Stance.OFFENSIVE, "Assault", 2, 3, 5, false).addBonus(BonusCondition.STRENGTH_OVERPOWER, BonusType.KNOCKDOWN, 1).build(), 3), // unguardable
	KNOCK_DOWN 			(new AttackTechnique(Stance.OFFENSIVE, Stance.OFFENSIVE, "Overrun", -2, 3, 5, 1).addBonus(BonusCondition.SKILL_LEVEL, BonusType.KNOCKDOWN, 1).setCausesBleed(false).build(), 3), 
	SLIDE				(new NonAttackTechnique(Stance.BALANCED, Stance.KNEELING, "Slide", 1, 4, TechniqueHeight.LOW).addBonus(BonusCondition.ENEMY_LOW_STABILITY, BonusType.TRIP, 80).addBonus(BonusCondition.OUTMANEUVER, BonusType.TRIP, 20).addBonus(BonusCondition.OUTMANEUVER, BonusType.EVASION, 1).build(), 1), 
	TAUNT 				(new NonAttackTechnique(Stance.DEFENSIVE, Stance.SEDUCTION, "Seduce", 0, 0).addBonus(BonusCondition.SKILL_LEVEL, BonusType.POWER_MOD, 1).addSex(new SexualExperienceBuilder().setAssTeasing(1)).build(), 3), 
	HIT_THE_DECK		(new FallDownTechnique(Stance.BALANCED, Stance.PRONE, "Hit the Deck").addBonus(BonusCondition.OUTMANEUVER, BonusType.EVASION, 2).build()), 
	FEINT_AND_STRIKE	(new AttackTechnique(Stance.OFFENSIVE, Stance.OFFENSIVE, "Feint Strike", -1, 3, 6).addBonus(BonusCondition.OUTMANEUVER, BonusType.POWER_MOD).addBonus(BonusCondition.OUTMANEUVER, BonusType.EVASION, 1).addBonus(BonusCondition.SKILL_LEVEL, BonusType.STABILTIY_COST, 1).build(), 3), 
	UPPERCUT			(new AttackTechnique(Stance.KNEELING, Stance.OFFENSIVE, "Uppercut", 1, 4, 2, true, TechniqueHeight.HIGH).addBonus(BonusCondition.SKILL_LEVEL, BonusType.POWER_MOD).build(), 3),
	COMBAT_HEAL  		(new SpellTechnique(Stance.CASTING, Stance.BALANCED, "Combat Heal", 7, 10, SpellEffect.HEALING).addBonus(BonusCondition.SKILL_LEVEL, BonusType.POWER_MOD, 3).build(), 3),
	COMBAT_FIRE  		(new SpellTechnique(Stance.CASTING, Stance.BALANCED, "Combat Fire", 3, 3, SpellEffect.FIRE_DAMAGE).addBonus(BonusCondition.SKILL_LEVEL, BonusType.POWER_MOD, 2).build(), 3),
	TITAN_STRENGTH  	(new SpellTechnique(Stance.CASTING, Stance.BALANCED, "Titan Strength", 0, 2, SpellEffect.NONE, StatusType.STRENGTH_BUFF).build(), 3),
	WEAKENING_CURSE  	(new SpellTechnique(Stance.CASTING, Stance.BALANCED, "Weakening Curse", 8, 7, SpellEffect.NONE, null, StatusType.STRENGTH_DEBUFF).build(), 1),
	GRAVITY			  	(new SpellTechnique(Stance.CASTING, Stance.BALANCED, "Gravity", 8, 3, SpellEffect.NONE, StatusType.GRAVITY).build(), 1),
	REFORGE			  	(new SpellTechnique(Stance.CASTING, Stance.BALANCED, "Reforge", 4, 3, SpellEffect.ARMOR_REPAIR).build(), 1),
	OIL					(new SpellTechnique(Stance.CASTING, Stance.BALANCED, "Oil", 8, 3, SpellEffect.NONE, null, StatusType.OIL).build(), 1),
	PARALYZE			(new SpellTechnique(Stance.CASTING, Stance.BALANCED, "Paralyze", -2, 8, SpellEffect.NONE, null, StatusType.PARALYSIS).build(), 1),
	HYPNOSIS			(new SpellTechnique(Stance.CASTING, Stance.BALANCED, "Hypnosis", -2, 12, SpellEffect.NONE, null, StatusType.HYPNOSIS).build(), 1),
	FOCUS_ENERGY	  	(new SpellTechnique(Stance.CASTING, Stance.BALANCED, "Focus Energy", 4, -5, SpellEffect.NONE, StatusType.ENDURANCE_BUFF).build(), 3, false)
	;
	
	private final TechniquePrototype trait;
	private final int maxRank;
	private final boolean learnable;
	private Techniques(TechniquePrototype trait) { this(trait, true); }
	private Techniques(TechniquePrototype trait, boolean learnable) { this(trait, 1, learnable); }
	private Techniques(TechniquePrototype trait, int maxRank) { this(trait, maxRank, true); }
	private Techniques(TechniquePrototype trait, int maxRank, boolean learnable) { 
		this.trait = trait;
		this.maxRank = maxRank;
		this.learnable = learnable;
	}
	
	public String getFlavorText() { 
		switch (this) {
			case POWER_ATTACK: return "Powerful, confident strike downwards. Cripplingly effective when used on downed enemies.";
			case TEMPO_ATTACK: return "Follow-up strike used to continue the momentum of an attack. Keeps the opponent on the defensive."; 
			case SPRING_ATTACK: return "A sudden aggressive strike to initiate an attack. Be wary of your footing.";
			case BLITZ_ATTACK: return "An agile strike that sets up an incredibly aggressive attack. Be extremely sure of your footing before committing.";
			case ALL_OUT_BLITZ: return "Continues the assault, using momentum and well-placed footwork to seamlessly move from one strike to the next.";
			case HOLD_BACK: return "Ends the blitz assault, dropping the user into a less aggressive, but still offensive, stance.";
			case RESERVED_ATTACK: return "Ends the attack, returning to a more balanced stance that can respond to the enemy as needed.";
			case REVERSAL_ATTACK: return "A sudden strike from a defensive position, with versatile follow-up options.";
			case NEUTRAL_ATTACK: return "A normal downward strike, weak and obvious, but effective nonetheless.";
			case CAREFUL_ATTACK: return "A strike that doesn't drop the user's guard. Useful for keeping defensive options open.";
			case BLOCK: return "A balanced defensive maneuver that allows the user to transition to a more defensible position.";
			case GUARD: return "A focused defensive maneuver, using all available tools to reject an opponent's strike.";
			case SECOND_WIND: return "The user lowers their arms and relaxes their stance to take a breather, recovering their stamina.";
			case SUDDEN_ADVANCE: return "An unstable technique - the user quickly shifts from a defensive stance to an offensive one while actively parrying their opponent.";
			case RIPOSTE: return "A counter attack capable of parrying or even disarming the opponent.";
			case EN_GARDE: return "An active parry used to deflect an opponent's strike."; 
			case ARMOR_SUNDER: return "A strike that maximizes damage to an opponent's armor.";
			case CAUTIOUS_ATTACK: return "A skillful strike that guards the user while it is performed, and leaves them in a good position to defend against counterattacks.";
			case VAULT: return "A skill that launches the user into the air with good form.";
			case JUMP_ATTACK: return "A crushing blow from the air.";
			case VAULT_OVER: return "Using their momentum, the user vaults over their opponent's attacks.";
			case RECKLESS_ATTACK: return "An unpredictable, powerful attack that unbalances the user, but cannot be blocked.";
			case KNOCK_DOWN: return "A bullrush attack that intends to knock the opponent off their feet.";
			case SLIDE: return "A low tackle that may knock an opponent over while avoiding their attacks.";
			case TAUNT: return "An introductory seductive maneuver, teasing the erotic motions to follow.";
			case HIT_THE_DECK: return "An active evasion that throws the user to the ground.";
			case FEINT_AND_STRIKE: return "A feint and attack that unstables the user unless they are quite skilled.";
			case PARRY: return "An active parry that is used to deflect an opponent's strike.";
			case UPPERCUT: return "A strike coming from low and landing high.";
			case COMBAT_HEAL: return "An easy self-healing spell to cast in the heat of close combat.";
			case COMBAT_FIRE: return "An easy fire-summoning spell to cast in the heat of close combat.";
			case TITAN_STRENGTH: return "Summons the strength of a titan into the user's body.";
			case WEAKENING_CURSE: return "A spell that curses the opponent, rendering them feeble.";
			case GRAVITY: return "A spell that curses the opponent, making them substantially heavier.";
			case OIL: return "Slicks the target in oil, rendering them slippery and flammable.";
			case REFORGE: return "Magically repairs armor, as if rewinding time.";
			case HYPNOSIS: return "Hypnotizes they who are so cursed; weaker than full mind control, but still incredibly effective.";
			case FOCUS_ENERGY: return "A spell for centering the spirit, restoring mana and improving physical endurance for a time.";
			case SLAP_ASS: return "A playful slap of the rear to entice someone to give it a try.";
			case GESTURE: return "An obscene, suggestive gesture of the hands.";
			case PUCKER_LIPS: return "An invitation using the lips.";
			case RUB: return "Some self love designed to entice someone.";
			case PRESENT: return "A simple, but effective seduction technique: assuming the position.";
			case POUND_ANAL:
			case POUND_DOGGY: return "Straightforward butt-pounding thrusts.";
			case ASS_BLAST: return "A frenetic ass-fucking technique.";
			case CRUSH_ASS: return "A deep, bowel-excavating maneuver.";
			case PROSTATE_GRIND: return "An advanced skill, designed to cause the receiver to cum buckets.";
			case POUND_PRONE_BONE: return "Crushin' ass flat on the receiver's back.";
			case IRRUMATIO: return "Straighforward mouth fucking.";
			case FORCE_DEEPTHROAT: return "Deep mouth fucking.";
			case GRAB_IT: return "Used to grab the enemy by the... well, y'know.";
			case CENTER: return "Focus yourself, preparing to advance and strike.";
			case STONEWALL: return "Enter into a fully defensive stance, unable to strike, but difficult to harm.";
			case BERSERK: return "Enter a blind, thunderous rage, fury tempered, a blade with two edges.";
			case REEL_BACK: return "Prepare to throw a haymaker strike with frightening momentum.";
			case PARALYZE: return "A powerful curse that locks the cursed in place for as long as its duration."; 
			default: return "";
		}
	}
	
	public TechniquePrototype getTrait() { return trait; }
	
	public int getMaxRank() { return maxRank; }
	
	public Techniques getPluggedAlternate() { return YOINK; }
	public boolean isLearnable() { return learnable; }
}
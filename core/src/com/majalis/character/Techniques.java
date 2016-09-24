package com.majalis.character;

import com.badlogic.gdx.utils.Array;
import com.majalis.character.AbstractCharacter.Stance;
/*
 * List of all techniques and their generic attributes
 */
public enum Techniques {
	// may want to reorganize these by what stances they're used in, rather than what stances they result in
	/* Offensive Techniques */
	STRONG_ATTACK 	{ public Stance getStanceResult(){ return Stance.OFFENSIVE; } public int getPowerMod(){ return 3; } public int getStabilityCost(){ return 3; } public int getStaminaCost(){ return 8; } },
	TEMPO_ATTACK  	{ public Stance getStanceResult(){ return Stance.OFFENSIVE; } public int getPowerMod(){ return 2; } public int getStabilityCost(){ return 2; } public int getStaminaCost(){ return 6; } },
	SPRING_ATTACK 	{ public Stance getStanceResult(){ return Stance.OFFENSIVE; } public int getPowerMod(){ return 1; } public int getStabilityCost(){ return 4; } public int getStaminaCost(){ return 6; } },	
	/* Balanced Techniques */
	RESERVED_ATTACK { public Stance getStanceResult(){ return Stance.BALANCED;  } public int getPowerMod(){ return 1; } public int getStabilityCost(){ return 4; } public int getStaminaCost(){ return 2; } },
	REVERSAL_ATTACK { public Stance getStanceResult(){ return Stance.BALANCED;  } public int getPowerMod(){ return 0; } public int getStabilityCost(){ return 2; } public int getStaminaCost(){ return 4; } },
	NEUTRAL_ATTACK  { public Stance getStanceResult(){ return Stance.BALANCED;  } public int getPowerMod(){ return 0; } public int getStabilityCost(){ return 1; } public int getStaminaCost(){ return 1; } },
	/* Defensive Techniques */
	CAUTIOUS_ATTACK { public Stance getStanceResult(){ return Stance.DEFENSIVE; } public int getPowerMod(){ return -1; } public int getStabilityCost(){ return 0; } public int getStaminaCost(){ return 2; } }, 
	CAREFUL_ATTACK  { public Stance getStanceResult(){ return Stance.DEFENSIVE; } public int getPowerMod(){ return 0; }  public int getStabilityCost(){ return 0; } public int getStaminaCost(){ return 2; } }, 
	GUARD			{ public Stance getStanceResult(){ return Stance.DEFENSIVE; } public int getPowerMod(){ return -100; } public int getStabilityCost(){ return -1; } public int getStaminaCost(){ return 0; } },
	SECOND_WIND		{ public Stance getStanceResult(){ return Stance.DEFENSIVE; } public int getPowerMod(){ return -100; } public int getStabilityCost(){ return -1; } public int getStaminaCost(){ return -4; } },	
	
	/* Techniques from Prone/Supine */
	KIP_UP			{ public Stance getStanceResult(){ return Stance.BALANCED; } public int getPowerMod(){ return -100; } public int getStabilityCost(){ return -5; } public int getStaminaCost(){ return 5; } }, // stand automatically	
	STAND_UP		{ public Stance getStanceResult(){ return Stance.BALANCED; } public int getPowerMod(){ return -100; } public int getStabilityCost(){ return -1; } public int getStaminaCost(){ return 2; } }, // will fall again if you don't have enough stability
	KNEE_UP			{ public Stance getStanceResult(){ return Stance.KNEELING; } public int getPowerMod(){ return -100; } public int getStabilityCost(){ return -5; } public int getStaminaCost(){ return 1; } },
	REST			{ public Stance getStanceResult(){ return Stance.SUPINE; } 	 public int getPowerMod(){ return -100; } public int getStabilityCost(){ return -1; } public int getStaminaCost(){ return -1; } },
	REST_FACE_DOWN  { public Stance getStanceResult(){ return Stance.PRONE; } 	 public int getPowerMod(){ return -100; } public int getStabilityCost(){ return -1; } public int getStaminaCost(){ return -1; } },	
	
	/* Out of resources */
	FALL_DOWN		{ public Stance getStanceResult(){ return Stance.SUPINE; } 	 public int getPowerMod(){ return -100; } public int getStabilityCost(){ return -100; } public int getStaminaCost(){ return 0; } }, // run out of stamina
	TRIP			{ public Stance getStanceResult(){ return Stance.PRONE; } 	 public int getPowerMod(){ return -100; } public int getStabilityCost(){ return -100; } public int getStaminaCost(){ return 0; } }, // run out of stability
	FIZZLE			{ public Stance getStanceResult(){ return Stance.BALANCED; }  public int getPowerMod(){ return -100; } public int getStabilityCost(){ return 0; } public int getStaminaCost(){ return 0; } },
	
	/* Positional */
	DUCK			{ public Stance getStanceResult(){ return Stance.KNEELING; } public int getPowerMod(){ return -100; } public int getStabilityCost(){ return 0; } public int getStaminaCost(){ return 0; } }, // run out of stamina
	FLY				{ public Stance getStanceResult(){ return Stance.AIRBORNE; } public int getPowerMod(){ return -100; } public int getStabilityCost(){ return 0; } public int getStaminaCost(){ return 0; } }, // run out of stability

	/* Enemy pouncing */
	DIVEBOMB		{ public Stance getStanceResult(){ return Stance.FELLATIO; } public int getPowerMod(){ return -100; } public int getStabilityCost(){ return 0; } public int getStaminaCost(){ return 0; } },
	IRRUMATIO		{ public Stance getStanceResult(){ return Stance.FELLATIO; } public int getPowerMod(){ return -100; } public int getStabilityCost(){ return 0; } public int getStaminaCost(){ return 0; } },
	POUNCE			{ public Stance getStanceResult(){ return Stance.DOGGY; } 	 public int getPowerMod(){ return -100; } public int getStabilityCost(){ return 0; } public int getStaminaCost(){ return 2; } }, // Used to initiate doggy
	POUND			{ public Stance getStanceResult(){ return Stance.DOGGY; } 	 public int getPowerMod(){ return -100; } public int getStabilityCost(){ return 0; } public int getStaminaCost(){ return 1; } }, // Used to doggystyle
	KNOT 			{ public Stance getStanceResult(){ return Stance.KNOTTED; }	 public int getPowerMod(){ return -100; } public int getStabilityCost(){ return 0; } public int getStaminaCost(){ return 0; } }, // Used to knot by knotty weresluts and others
	KNOT_BANG 		{ public Stance getStanceResult(){ return Stance.KNOTTED; }  public int getPowerMod(){ return -100; } public int getStabilityCost(){ return 0; } public int getStaminaCost(){ return 0; } }, // Used to knot by knotty weresluts and others - could end the battle
	ERUPT			{ public Stance getStanceResult(){ return Stance.BALANCED; } public int getPowerMod(){ return -100; } public int getStabilityCost(){ return 0; } public int getStaminaCost(){ return 1; } }, // Used to creampie
	/* Receptive */
	RECEIVE			{ public Stance getStanceResult(){ return Stance.DOGGY; } 	 public int getPowerMod(){ return -100; } public int getStabilityCost(){ return 0; } public int getStaminaCost(){ return 1; } }, // Used to receive doggystyle 
	RECEIVE_KNOT	{ public Stance getStanceResult(){ return Stance.KNOTTED; }  public int getPowerMod(){ return -100; } public int getStabilityCost(){ return 0; } public int getStaminaCost(){ return 1; } }, // Used to receive the knot
	OPEN_WIDE		{ public Stance getStanceResult(){ return Stance.FELLATIO; }  public int getPowerMod(){ return -100; } public int getStabilityCost(){ return 0; } public int getStaminaCost(){ return 1; } },
	STRUGGLE		{ public Stance getStanceResult(){ return Stance.BALANCED; }  public int getPowerMod(){ return -100; } public int getStabilityCost(){ return 0; } public int getStaminaCost(){ return 1; } },
	
	/* Learnable Skills*/
	VAULT			{ public Stance getStanceResult(){ return Stance.AIRBORNE; }  public int getPowerMod(){ return -100; } public int getStabilityCost(){ return 2; } public int getStaminaCost(){ return 4; } },
	JUMP_ATTACK		{ public Stance getStanceResult(){ return Stance.BALANCED; }  public int getPowerMod(){ return 4; } 	public int getStabilityCost(){ return 4; } public int getStaminaCost(){ return 2; } },
	RECKLESS_ATTACK	{ public Stance getStanceResult(){ return Stance.OFFENSIVE; }  public int getPowerMod(){ return 2; } public int getStabilityCost(){ return 3; } public int getStaminaCost(){ return 6; } },
	TAUNT			{ public Stance getStanceResult(){ return Stance.DEFENSIVE;}  public int getPowerMod(){ return -100; } public int getStabilityCost(){ return 0; } public int getStaminaCost(){ return 0; } },
	KNOCK_DOWN		{ public Stance getStanceResult(){ return Stance.OFFENSIVE;}  public int getPowerMod(){ return 1; } public int getStabilityCost(){ return 3; } public int getStaminaCost(){ return 6; } },
	INCANTATION		{ public Stance getStanceResult(){ return Stance.CASTING; }   public int getPowerMod(){ return -100; } public int getStabilityCost(){ return 0; } public int getStaminaCost(){ return 1; } },
	COMBAT_HEAL		{ public Stance getStanceResult(){ return Stance.BALANCED; }  public int getPowerMod(){ return 10; } public int getStabilityCost(){ return 0; } public int getStaminaCost(){ return 1; } @Override public boolean isSpell(){return true;} @Override public int getManaCost(){ return 10; } },
	COMBAT_FIRE		{ public Stance getStanceResult(){ return Stance.BALANCED; }  public int getPowerMod(){ return 3; } public int getStabilityCost(){ return 0; } public int getStaminaCost(){ return 1; } @Override public boolean isSpell(){return true;} @Override public int getManaCost(){ return 5; } },
	TITAN_STRENGTH  { public Stance getStanceResult(){ return Stance.BALANCED; }  public int getPowerMod(){ return -100; } public int getStabilityCost(){ return 0; } public int getStaminaCost(){ return 1; } @Override public boolean isSpell(){return true;} @Override public int getManaCost(){ return 2; } }	
	;
	
	public abstract Stance getStanceResult();
	public abstract int getPowerMod();
	public abstract int getStabilityCost();  // Unbalancing moves carry a heavier stability cost
	public abstract int getStaminaCost(); 	 // More strenuous moves carry a heavier stamina cost.  
	public int getManaCost(){ return 0; }
	
	
	public String toString(){
		char[] chars = super.toString().replace("_", " ").toLowerCase().toCharArray();
		boolean found = false;
		for (int i = 0; i < chars.length; i++) {
			if (!found && Character.isLetter(chars[i])) {
				chars[i] = Character.toUpperCase(chars[i]);
				found = true;
		    } 
			else if (Character.isWhitespace(chars[i])) {
				found = false;
		    }
		}		
		return String.valueOf(chars);
	}
	public static Array<Techniques> getLearnableSkills() {
		return new Array<Techniques>(true, new Techniques[]{VAULT, RECKLESS_ATTACK, TAUNT, KNOCK_DOWN, SECOND_WIND}, 0, 5);
	}
	public static Array<Techniques> getLearnableSpells() {
		return new Array<Techniques>(true, new Techniques[]{COMBAT_HEAL, COMBAT_FIRE, TITAN_STRENGTH}, 0, 2);
	}
	public boolean isSpell() {
		return false;
	}
}
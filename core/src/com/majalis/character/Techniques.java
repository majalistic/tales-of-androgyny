package com.majalis.character;

import com.majalis.character.AbstractCharacter.Stance;

public enum Techniques {
	// may want to reorganize these by what stances they're used in, rather than what stances they result in
	/* Offensive Techniques */
	STRONG_ATTACK 	{ public Stance getStanceResult(){ return Stance.OFFENSIVE; } public int getPowerMod(){ return 3; } public int getStabilityCost(){ return 2; } public int getStaminaCost(){ return 6; } },
	TEMPO_ATTACK  	{ public Stance getStanceResult(){ return Stance.OFFENSIVE; } public int getPowerMod(){ return 2; } public int getStabilityCost(){ return 1; } public int getStaminaCost(){ return 4; } },
	SPRING_ATTACK 	{ public Stance getStanceResult(){ return Stance.OFFENSIVE; } public int getPowerMod(){ return 1; } public int getStabilityCost(){ return 2; } public int getStaminaCost(){ return 2; } },	
	/* Balanced Techniques */
	RESERVED_ATTACK { public Stance getStanceResult(){ return Stance.BALANCED;  } public int getPowerMod(){ return 1; } public int getStabilityCost(){ return 0; } public int getStaminaCost(){ return 2; } },
	REVERSAL_ATTACK { public Stance getStanceResult(){ return Stance.BALANCED;  } public int getPowerMod(){ return 0; } public int getStabilityCost(){ return 1; } public int getStaminaCost(){ return 2; } },
	NEUTRAL_ATTACK  { public Stance getStanceResult(){ return Stance.BALANCED;  } public int getPowerMod(){ return 0; } public int getStabilityCost(){ return 1; } public int getStaminaCost(){ return 2; } },
	/* Defensive Techniques */
	CAUTIOUS_ATTACK { public Stance getStanceResult(){ return Stance.DEFENSIVE; } public int getPowerMod(){ return -1; } public int getStabilityCost(){ return 0; } public int getStaminaCost(){ return 1; } }, 
	CAREFUL_ATTACK  { public Stance getStanceResult(){ return Stance.DEFENSIVE; } public int getPowerMod(){ return 0; }  public int getStabilityCost(){ return -1; } public int getStaminaCost(){ return 1; } }, 
	GUARD			{ public Stance getStanceResult(){ return Stance.DEFENSIVE; } public int getPowerMod(){ return -100; } public int getStabilityCost(){ return -1; } public int getStaminaCost(){ return 0; } },
	
	/* Techniques from Prone/Supine */
	KIP_UP			{ public Stance getStanceResult(){ return Stance.BALANCED; } public int getPowerMod(){ return -100; } public int getStabilityCost(){ return -5; } public int getStaminaCost(){ return 5; } }, // stand automatically	
	STAND_UP		{ public Stance getStanceResult(){ return Stance.BALANCED; } public int getPowerMod(){ return -100; } public int getStabilityCost(){ return -1; } public int getStaminaCost(){ return 2; } }, // will fall again if you don't have enough stability
	KNEE_UP			{ public Stance getStanceResult(){ return Stance.KNEELING; } public int getPowerMod(){ return -100; } public int getStabilityCost(){ return -5; } public int getStaminaCost(){ return 1; } },
	REST			{ public Stance getStanceResult(){ return Stance.SUPINE; } 	 public int getPowerMod(){ return -100; } public int getStabilityCost(){ return -1; } public int getStaminaCost(){ return -1; } },
	
	/* Out of resources */
	FALL_DOWN		{ public Stance getStanceResult(){ return Stance.SUPINE; } 	 public int getPowerMod(){ return -100; } public int getStabilityCost(){ return -100; } public int getStaminaCost(){ return 0; } }, // run out of stamina
	TRIP			{ public Stance getStanceResult(){ return Stance.PRONE; } 	 public int getPowerMod(){ return -100; } public int getStabilityCost(){ return -100; } public int getStaminaCost(){ return 0; } }, // run out of stability
	
	/* Enemy pouncing */
	
	POUNCE			{ public Stance getStanceResult(){ return Stance.DOGGY; } 	 public int getPowerMod(){ return -100; } public int getStabilityCost(){ return 0; } public int getStaminaCost(){ return 2; } }, // Used to initiate doggy
	POUND			{ public Stance getStanceResult(){ return Stance.DOGGY; } 	 public int getPowerMod(){ return -100; } public int getStabilityCost(){ return 0; } public int getStaminaCost(){ return 1; } }, // Used to doggystyle
	KNOT 			{ public Stance getStanceResult(){ return Stance.KNOTTED; }	 public int getPowerMod(){ return -100; } public int getStabilityCost(){ return 0; } public int getStaminaCost(){ return 1; } }, // Used to knot by knotty weresluts and others
	KNOT_BANG 		{ public Stance getStanceResult(){ return Stance.KNOTTED; }  public int getPowerMod(){ return -100; } public int getStabilityCost(){ return 0; } public int getStaminaCost(){ return 1; } }, // Used to knot by knotty weresluts and others - could end the battle
	ERUPT			{ public Stance getStanceResult(){ return Stance.BALANCED; } public int getPowerMod(){ return -100; } public int getStabilityCost(){ return 0; } public int getStaminaCost(){ return 1; } }, // Used to creampie
	RECEIVE			{ public Stance getStanceResult(){ return Stance.DOGGY; } 	 public int getPowerMod(){ return -100; } public int getStabilityCost(){ return 0; } public int getStaminaCost(){ return 1; } }, // Used to receive doggystyle 
	RECEIVE_KNOT	{ public Stance getStanceResult(){ return Stance.KNOTTED; }  public int getPowerMod(){ return -100; } public int getStabilityCost(){ return 0; } public int getStaminaCost(){ return 1; } }; // Used to receive the knot
	
	public abstract Stance getStanceResult();
	public abstract int getPowerMod();
	public abstract int getStabilityCost();  // Unbalancing moves carry a heavier stability cost
	public abstract int getStaminaCost(); 	 // More strenuous moves carry a heavier stamina cost.  
	
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
}
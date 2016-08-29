package com.majalis.character;

import com.majalis.character.AbstractCharacter.Stance;

public enum Techniques {
	/* Offensive Techniques */
	STRONG_ATTACK 	 { public Stance getStanceResult(){ return Stance.OFFENSIVE; } public int getPowerMod(){ return 3; } },
	TEMPO_ATTACK  	 { public Stance getStanceResult(){ return Stance.OFFENSIVE; } public int getPowerMod(){ return 2; } },
	SPRING_ATTACK 	 { public Stance getStanceResult(){ return Stance.OFFENSIVE; } public int getPowerMod(){ return 1; } },	
	/* Balanced Techniques */
	RESERVED_ATTACK  { public Stance getStanceResult(){ return Stance.BALANCED;  } public int getPowerMod(){ return 1; } },
	REVERSAL_ATTACK  { public Stance getStanceResult(){ return Stance.BALANCED;  } public int getPowerMod(){ return 0; } },
	NEUTRAL_ATTACK   { public Stance getStanceResult(){ return Stance.BALANCED;  } public int getPowerMod(){ return 0; } },
	/* Defensive Techniques */
	CAUTIOUS_ATTACK  { public Stance getStanceResult(){ return Stance.DEFENSIVE; } public int getPowerMod(){ return -1; } }, 
	CAREFUL_ATTACK   { public Stance getStanceResult(){ return Stance.DEFENSIVE; } public int getPowerMod(){ return 0; } }, 
	GUARD			 { public Stance getStanceResult(){ return Stance.DEFENSIVE; } public int getPowerMod(){ return -100; } };
	
	public abstract Stance getStanceResult();
	public abstract int getPowerMod();
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
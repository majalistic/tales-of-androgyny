package com.majalis.character;

public enum Perk {
	SKILLED, // increases number of skill points to allocate
	WELLROUNDED, // increases lowest stat by 1
	SURVEYOR, // increases effective perception for scouting
	EROTIC, // increases effective charisma for alluring enemies, including charisma checks of that sort
	CATAMITE; // unlocks more willing options for catching it

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

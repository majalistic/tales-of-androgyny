package com.majalis.character;

public enum Perk {
	SKILLED (5), // increases number of skill points to allocate
	WELLROUNDED (1), // increases lowest stat by 1
	SURVEYOR (3), // increases effective perception for scouting
	EROTIC (3), // increases effective charisma for alluring enemies, including charisma checks of that sort
	CATAMITE (1); // unlocks more willing options for catching it

	private final int maxRank;
	private Perk(){
		this(1);
	}
	private Perk(int maxRank){
		this.maxRank = maxRank;
	}
	
	public String getLabel(){
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

	public int getMaxRank() {
		return maxRank;
	}
}

package com.majalis.character;

public enum Perk {
	SKILLED ("Increases number of skill points to allocate by 2 per rank.", 5), 
	WELLROUNDED ("Increases current lowest stat by 1.", 1),
	SURVEYOR ("Increases effective perception for scouting purposes by 2 per rank.", 3),
	EROTIC ("Increases effective charisma for arousing others by 2 per rank.", 3), 
	CATAMITE ("Unlocks more options for being willingly receptive.", 1),
	WEAK_TO_ANAL ("Weak to butt stuff. Causes quick pew pews while penetrated.", false),
	ANAL_LOVER ("Loves butt stuff. Or, more accurately, getting butt stuffed.", 3, false),
	MOUTH_MANIAC ("Loves suckin' it.", 3, false),
	CREAMPIE_ADDICT ("Loves to get cream-filled like a creampuff.", 3, false),
	SEMEN_SWALLOWER ("Loves to drink jizz.", 3, false)
	;
	
	private final int maxRank;
	private final boolean positive;
	private final String description;
	private Perk(String description){
		this(description, 1, true);
	}
	private Perk(String description, boolean positive){
		this(description, 1, positive);
	}
	private Perk(String description, int maxRank){
		this(description, maxRank, true);
	}
	private Perk(String description, int maxRank, boolean positive){
		this.description = description;
		this.maxRank = maxRank;
		this.positive = positive;
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
	
	public boolean isPositive() { return positive; }
	
	public String getDescription(){ return description; }
	
}

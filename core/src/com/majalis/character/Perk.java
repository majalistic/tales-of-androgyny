package com.majalis.character;

public enum Perk {
	SKILLED ("Increases number of skill points to allocate by 2 per perk rank. (5 Ranks)", 5), 
	WELLROUNDED ("Increases current lowest stat by 1.", 1),
	SURVEYOR ("Increases effective perception for scouting purposes by 2 per rank.", 3),
	EROTIC ("Increases effective charisma for arousing others by 2 per rank.", 3), 
	CATAMITE ("Unlocks more options for being willingly receptive.", 1),
	STRONGER ("Increases strength by 1 per rank.", 3),
	HARDER ("Increases endurance by 1 per rank.", 3),
	FASTER ("Increases agility by 1 per rank.", 3),
	WEAK_TO_ANAL ("Weak to butt stuff. Causes quick pew pews while penetrated.", false),
	ANAL_LOVER ("Loves butt stuff. Or, more accurately, getting butt stuffed. Unlocks skills for anal.", 3, false),
	MOUTH_MANIAC ("Loves suckin' it. Unlocks skills for fellatio.", 3, false),
	CREAMPIE_ADDICT ("Loves to get cream-filled like a creampuff.", 3, false),
	SEMEN_SWALLOWER ("Loves to drink jizz.", 3, false),
	HORSE_LOVER ("Has a thing for equine phalluses. Be careful!", 1, false),
	GIANT_LOVER ("Likes 'em big. For a certain definition of \"'em\" Be careful!", 3, false), 
	LADY_OF_THE_NIGHT ("You know how to make money with your... talents.", 5, false)
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

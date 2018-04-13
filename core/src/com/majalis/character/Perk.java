package com.majalis.character;

public enum Perk {
	SKILLED ("Increases number of skill points to allocate by 2 per perk rank. (5 Ranks)", 5), 
	WELLROUNDED ("Increases current lowest stat by 1.", 1),
	SPECIALIST("Increases current greatest stat by 1.  Requires: Level 10", 1),
	SURVEYOR ("Increases effective perception for scouting purposes by 2 per rank.", 3),
	STEALTHY ("Increases effective agility for stealthing purposes by 2 per rank.", 3),
	EROTIC ("Increases effective charisma for arousing others by 2 per rank.", 3), 
	CATAMITE ("Unlocks more options for being willingly receptive.", 1),
	QUICKFOOTED ("Increases stability as if having higher agility by 2 per rank.", 3),
	COMBAT_FINESSE ("Increases weapon skill bonuses as if having higher agility by 2 per rank.", 3),
	SUNDERER ("Increases armor destruction on armor sundering attacks.", 3),
	VERSATILE ("Decreases stability cost of techniques that change stance from Balanced stance.", 3),
	CUM_DRINKER ("Restores a small amount of health when swallowing cum.", 3),
	EASY_TO_PLEASE ("When ejaculating, lust is decreased by a greater amount.", 3),
	FORAGER ("Outcomes from foraging are more likely to be good.", 3),
	STRONGER ("Increases strength by 1 per rank.", 3),
	HARDER ("Increases endurance by 1 per rank.", 3),
	FASTER ("Increases agility by 1 per rank.", 3),
	SMARTER ("Increases perception by 1 per rank.", 3),
	WITCHER ("Increases magic by 1 per rank.", 3),
	HOTTER ("Increases charisma by 1 per rank.", 3),
	BLOWJOB_EXPERT ("Skill at oral sex.", 10),
	PERFECT_BOTTOM ("Skill at anal sex.", 10),
	CRANK_MASTER ("Skill at handjobs", 10),	
	WEAK_TO_ANAL ("Weak to butt stuff. Causes quick pew pews while penetrated.", false),
	COCK_LOVER ("Loves cocks. What else is there to say?", 10, false),
	ANAL_ADDICT ("Loves butt stuff. Or, more accurately, getting butt stuffed. Unlocks skills for anal.", 3, false),
	MOUTH_MANIAC ("Loves suckin' it. Unlocks skills for fellatio.", 3, false),
	CREAMPIE_COLLECTOR ("Loves to get cream-filled like a creampuff.", 3, false),
	CUM_CONNOISSEUR ("Loves to drink jizz.", 3, false),
	EQUESTRIAN ("Has a thing for equine phalluses. Be careful!", 1, false),
	SIZE_QUEEN ("Likes 'em big. For a certain definition of \"'em\" Be careful!", 3, false), 
	LADY_OF_THE_NIGHT ("You know how to make money with your... talents.", 20, false),
	BEASTMASTER ("You've been... friendly with a few non-humanoid creatures.", 3, false),
	CUCKOO_FOR_CUCKOO ("You enjoy the company of feathered friends.", 3, false),
	BITCH ("You love being tied together with someone who's fucking your ass.  You like yippy dog sex, and there's no shame in that.  Well, some.", 3, false),
	POWER_BOTTOM ("You like to take it, but you like to be in control.", 10, false),
	TOP ("You like to give it.", 10)
	;
	
	private final int maxRank;
	private final boolean positive;
	private final String description;
	private Perk(String description) {
		this(description, 1, true);
	}
	private Perk(String description, boolean positive) {
		this(description, 1, positive);
	}
	private Perk(String description, int maxRank) {
		this(description, maxRank, true);
	}
	private Perk(String description, int maxRank, boolean positive) {
		this.description = description;
		this.maxRank = maxRank;
		this.positive = positive;
	}
	
	public String getLabel() {
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
	
	public String getDescription() { return description; }
	public boolean isLearnable() {
		return isPositive() && this != BLOWJOB_EXPERT && this != PERFECT_BOTTOM && this != CRANK_MASTER && this != TOP;
	}
	public int getRequiredLevel() { return this == SPECIALIST ? 10 : 0; }
	
}

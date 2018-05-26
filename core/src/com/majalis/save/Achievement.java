package com.majalis.save;

public enum Achievement {
	QUETZAL_GODDESS_DEFEATED ("Fallen Goddess", "Defeated the Quetzal Goddess of Mount Xiuh."),
	QUETZAL_GODDESS_VIRGIN ("Pure Hero", "Defeated the Quetzal Goddess of Mount Xiuh without losing your anal virginity."),
	BOUNTY_HUNTER("Bounty Hunter", "Completed a bounty for the Brothel Madame."),
	BITCH("Bitch", "Became a bitch."),
	HARPY_WIFE("Harpy Wife", "Became a harpy's \"wife\"."),
	QUEEN("Queen", "Became a queen."),
	SEAT_OF_POWER("Seat of Power", "Became the seat of power in Silajam."),
	HORSE_RIDER("Horse Rider", "Became an expert horse rider."),
	LOCKED_UP("Locked Up", "Became a certain merchant's little toy lockbox."),
	NINE_TENTHS("Nine-Tenths", "Got possessed and made into a bandit meat receptacle."),
	GOKKUN("Gokkun", "Got \"married\" to an eccentric noblelady and never looked at clam chowder the same way ever again."),
	NURSERY("Nursery", "Got dolled up and filled with spiders.")
	;
	private final String label;
	private final String description;
	private Achievement(String label, String description) { this.label = label; this.description = description; }
	public String getLabel() { return label; }	
	public String getDescription() { return description; }
}

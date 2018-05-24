package com.majalis.save;

public enum Achievement {
	QUETZAL_GODDESS_DEFEATED ("Fallen Goddess"),
	QUETZAL_GODDESS_VIRGIN ("Pure Hero"),
	BOUNTY_HUNTER("Bounty Hunter"),
	BITCH("Bitch"),
	HARPY_WIFE("Harpy Wife"),
	QUEEN("Queen"),
	SEAT_OF_POWER("Seat of Power"),
	HORSE_RIDER("Horse Rider"),
	LOCKED_UP("Locked Up"),
	NINE_TENTHS("Nine-Tenths"),
	GOKKUN("Gokkun"),
	NURSERY("Nursery")
	;
	private final String label;
	private Achievement(String label) { this.label = label; }
	public String getLabel() { return label; }	
}

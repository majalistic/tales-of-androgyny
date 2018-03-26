package com.majalis.save;

public enum Achievement {
	QUETZAL_GODDESS_DEFEATED ("Fallen Goddess"),
	QUETZAL_GODDESS_VIRGIN ("Pure Hero"),
	BOUNTY_HUNTER("Bounty Hunter"),;

	private final String label;
	private Achievement(String label) { this.label = label; }
	public String getLabel() { return label; }	
}

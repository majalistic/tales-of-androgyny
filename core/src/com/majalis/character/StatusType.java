package com.majalis.character;

public enum StatusType {
	STRENGTH_BUFF("Strength Up"),
	AGILITY_BUFF("Agility Up"),
	ENDURANCE_BUFF("Endurance Up"),
	BLEEDING ("Bleeding", false, false), 
	ACTIVATE ("Activate"), 
	STRENGTH_DEBUFF ("Strength Down", true, false), 
	GRAVITY ("Gravity", true, false),
	OIL("Oily", true, false), ;

	private final boolean doesDegrade;
	private final boolean isPositive;
	private final String label;	
	private StatusType(String label) { this(label, true, true); }
	private StatusType(String label, boolean doesDegrade, boolean isPositive) {
		this.label = label;
		this.doesDegrade = doesDegrade;
		this.isPositive = isPositive;
	}
	
	public String getLabel() { return label; }
	public boolean degrades() { return doesDegrade; }
	public boolean isPositive() { return isPositive; }
}

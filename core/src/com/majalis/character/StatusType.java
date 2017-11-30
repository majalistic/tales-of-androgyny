package com.majalis.character;

public enum StatusType {
	STRENGTH_BUFF,
	AGILITY_BUFF,
	ENDURANCE_BUFF,
	BLEEDING (false, false), 
	ACTIVATE, 
	STRENGTH_DEBUFF (true, false);

	private final boolean doesDegrade;
	private final boolean isPositive;
	
	StatusType() {
		doesDegrade = true;
		isPositive = true;
	}
	
	StatusType(boolean doesDegrade, boolean isPositive) {
		this.doesDegrade = doesDegrade;
		this.isPositive = isPositive;
	}
	
	public boolean degrades() {
		return doesDegrade;
	}

	public boolean isPositive() { return isPositive; }
}

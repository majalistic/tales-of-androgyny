package com.majalis.character;

public enum StatusType {
	STRENGTH_BUFF,
	AGILITY_BUFF,
	ENDURANCE_BUFF,
	BLEEDING (false);

	private final boolean doesDegrade;
	
	StatusType() {
		doesDegrade = true;
	}
	
	StatusType(boolean doesDegrade) {
		this.doesDegrade = doesDegrade;
	}
	
	public boolean degrades() {
		return doesDegrade;
	}
}

package com.majalis.character;

public enum StatusType {
	STRENGTH_BUFF,
	AGILITY_BUFF,
	ENDURANCE_BUFF;

	private final boolean doesDegrade;
	
	StatusType(){
		doesDegrade = true;
	}
	
	public boolean degrades() {
		return doesDegrade;
	}
}

package com.majalis.character;

public class Buff {

	public final StatusType type;
	public final int power;
	protected Buff (StatusType type, int power){
		this.type = type;
		this.power = power;
	}
}

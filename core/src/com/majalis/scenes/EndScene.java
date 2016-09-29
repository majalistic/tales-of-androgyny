package com.majalis.scenes;
/*
 * Represents a scene that concludes an encounter
 */
public class EndScene extends Scene{

	private Type type;
	public EndScene(Type type) {
		super(null, -1);
		this.type = type;
	}

	public Type getType(){
		return type;
	}
	
	@Override
	public void setActive() {
		isActive = true;
	}

	@Override
	public boolean isActive() {
		return isActive;
	}
	
	public enum Type {
		ENCOUNTER_OVER,
		GAME_OVER,
		GAME_EXIT
	}

}

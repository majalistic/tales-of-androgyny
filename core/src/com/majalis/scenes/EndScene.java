package com.majalis.scenes;

import com.badlogic.gdx.utils.ObjectMap;

public class EndScene extends Scene{

	private Type type;
	public EndScene(ObjectMap<Integer, Scene> sceneBranches, Type type) {
		super(sceneBranches);
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

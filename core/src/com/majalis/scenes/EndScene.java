package com.majalis.scenes;

import com.badlogic.gdx.utils.OrderedMap;
/*
 * Represents a scene that concludes an encounter
 */
public class EndScene extends Scene{

	private Type type;
	public EndScene(OrderedMap<Integer, Scene> sceneBranches, int sceneCode, Type type) {
		super(sceneBranches, sceneCode);
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

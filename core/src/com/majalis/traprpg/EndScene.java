package com.majalis.traprpg;

import com.badlogic.gdx.utils.ObjectMap;

public class EndScene extends Scene{

	private Type type;
	protected EndScene(ObjectMap<Integer, Scene> sceneBranches, Type type) {
		super(sceneBranches);
		this.type = type;
	}

	public Type getType(){
		return type;
	}
	
	@Override
	protected void setActive() {
		isActive = true;
	}

	@Override
	protected boolean isActive() {
		return isActive;
	}
	
	enum Type {
		ENCOUNTER_OVER,
		GAME_OVER,
		GAME_EXIT
	}

}

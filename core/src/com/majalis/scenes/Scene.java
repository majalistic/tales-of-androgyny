package com.majalis.scenes;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.OrderedMap;
/*
 * Controls the logic for a single "scene", whether it be a text and image splash, a battle entry-point, or a selection dialog
 */
public abstract class Scene extends Group {
	protected final OrderedMap<Integer, Scene> sceneBranches;
	protected final int sceneCode;
	protected boolean isActive;
	
	protected Scene(OrderedMap<Integer, Scene> sceneBranches, int sceneCode){
		this.sceneBranches = sceneBranches;
		this.sceneCode = sceneCode;
		this.addAction(Actions.hide());
	}
	
	public abstract void setActive();
	// implement the input logic that determines the next Scene for children
	public boolean isActive(){
		return isActive;
	}
	public int getCode(){
		return sceneCode;
	}
	public void poke(){}
}

package com.majalis.scenes;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.OrderedMap;
/*
 * Controls the logic for a single "scene", whether it be a text and image splash, a battle entry-point, or a selection dialog
 */
public abstract class Scene extends Group {
	protected final OrderedMap<Integer, Scene> sceneBranches;
	protected boolean isActive;
	
	protected Scene(OrderedMap<Integer, Scene> sceneBranches){
		this.sceneBranches = sceneBranches;
		this.addAction(Actions.hide());
	}
	
	public abstract void setActive();
	// implement the input logic that determines the next Scene for children
	public boolean isActive(){
		return isActive;
	}
	public int getCode(){
		return -1;
	}
	public void poke(){
		
	}
	
	@Override
    public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
    }
	// scenes of type battle will have access to the battlefactory on initialize, but will not have their battle generated (possibly) until it is lazy-loaded - otherwise they will have their battle dependency injected into them
}

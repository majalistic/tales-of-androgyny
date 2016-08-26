package com.majalis.traprpg;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.ObjectMap;
/*
 * Controls the logic for a single "scene", whether it be a text and image splash, a battle entry-point, or a selection dialog
 */
public abstract class Scene extends Group {
	protected final ObjectMap<Integer, Scene> sceneBranches;
	protected boolean isActive;
	
	protected Scene(ObjectMap<Integer, Scene> sceneBranches){
		this.sceneBranches = sceneBranches;
		this.addAction(Actions.hide());
	}
	
	protected abstract void setActive();
	// implement the input logic that determines the next Scene for children
	protected boolean isActive(){
		return isActive;
	}
	protected int getCode(){
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

package com.majalis.scenes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.OrderedMap;
/*
 * Controls the logic for a single "scene", whether it be a text and image splash, a battle entry-point, or a selection dialog
 */
public abstract class Scene extends Group {
	protected final OrderedMap<Integer, Scene> sceneBranches;
	protected final int sceneCode;
	protected boolean isActive;
	
	protected Scene(OrderedMap<Integer, Scene> sceneBranches, int sceneCode) {
		this.sceneBranches = sceneBranches;
		this.sceneCode = sceneCode;
		this.addAction(Actions.hide());
	}
	
	protected Label addLabel(String text, Skin skin, Color color, float x, float y) {
		Label newLabel = new Label(text, skin);
		newLabel.setColor(color);
		newLabel.setPosition(x, y);
		this.addActor(newLabel);
		return newLabel;
	}
	
	protected Image addImage(Texture texture, Color color, float x, float y, float width, float height) {
		Image newImage = new Image(texture);
		newImage.setBounds(x, y, width, height);
		this.addActor(newImage);
		newImage.setColor(color);
		return newImage;
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

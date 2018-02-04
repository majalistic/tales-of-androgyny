package com.majalis.scenes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
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
		return addLabel(text, skin, null, color, x, y);
	}
	
	protected Label addLabel(String text, Skin skin, BitmapFont font, Color color, float x, float y) {
		return addLabel(this, text, skin, font, color, x, y);
	}
	
	protected Label addLabel(Group group, String text, Skin skin, BitmapFont font, Color color, float x, float y) {
		Label newLabel = new Label(text, skin);
		newLabel.setColor(color);
		newLabel.setPosition(x, y);
		if (font != null) {
			Label.LabelStyle style = new Label.LabelStyle(newLabel.getStyle());
			style.font = font;
			newLabel.setStyle(style);
			newLabel.setWrap(true);
			newLabel.setAlignment(Align.top);
		}
		group.addActor(newLabel);
		return newLabel;
	}
	
	protected Image addImage(Texture texture, Color color, int x, int y) {
		return addImage(texture, color, x, y, texture.getWidth(), texture.getHeight());
	}
	
	protected Image addImage(Group group, Texture texture, Color color, float x, float y) {
		return addImage(group, texture, color, x, y, texture.getWidth(), texture.getHeight());
	}
	
	protected Image addImage(Texture texture, Color color, float x, float y, float width, float height) {
		return addImage(this, texture, color, x, y, width, height);
	}
	
	protected Image addImage(Group group, Texture texture, Color color, float x, float y, float width, float height) {
		Image newImage = new Image(texture);
		newImage.setBounds(x, y, width, height);
		group.addActor(newImage);
		if (color != null) {
			newImage.setColor(color);
		}
		return newImage;
	}
	
	protected Image addImage(Group group, TextureRegion texture, Color color, float x, float y, float width, float height) {
		Image newImage = new Image(texture);
		newImage.setBounds(x, y, width, height);
		group.addActor(newImage);
		if (color != null) {
			newImage.setColor(color);
		}
		return newImage;
	}
	
	public abstract void setActive();
	// implement the input logic that determines the next Scene for children
	public boolean isActive() { return isActive; }
	public int getCode() { return sceneCode; }
	public String getText() { return ""; }
	public boolean showSave() { return false; }
	public boolean isBattle() { return false; }
	public boolean encounterOver() { return false; }
	public boolean gameOver() { return false; }
	public void poke() {}
	public void toggleBackground() {}
}

package com.majalis.world;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class PathChunk extends Image {
	public PathChunk(Texture roadImage, Vector2 start, Vector2 finish) {
		super(roadImage);
		setHeight(54);
		setWidth(16);
		
		Vector2 position = GameWorldHelper.calculatePosition((int)start.x, (int)start.y);
		position.x += 32 - getWidth()/2; // offset to get chunk in the middle of the hex
		position.y += 32;
		setPosition(position.x, position.y);	
		setOrigin(getWidth() / 2, 0);
		this.addAction(Actions.rotateBy(start.y == finish.y ? (start.x < finish.x ? 300 : 120) : start.x == finish.x ? (start.y < finish.y ? 0 : 180) : (start.x < finish.x ? 240 : 60)));
	}
}

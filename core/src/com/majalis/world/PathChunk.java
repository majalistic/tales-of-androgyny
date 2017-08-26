package com.majalis.world;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class PathChunk extends Actor {

	private final Texture roadImage;
	private final Vector2 start;
	private final float length;
	private final float degrees;

	public PathChunk(Texture roadImage, Vector2 start, Vector2 finish) {
		this.roadImage = roadImage;
		this.start = start;
		length = start.dst(finish);
		degrees = (float) (
			Math.atan2(
				finish.y - start.y,
				finish.x - start.x
			) * 180.0d / Math.PI);
	}
	
    public void draw(Batch batch, float parentAlpha) {
		batch.draw(roadImage, start.x, start.y, 0, 0, 18, length, 1, 1, 270+degrees, 0, 0, (int)roadImage.getWidth() / 2, (int)roadImage.getHeight(), false, false);	
	}
}

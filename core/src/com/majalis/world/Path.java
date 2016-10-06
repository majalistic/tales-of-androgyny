package com.majalis.world;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Path extends Actor {

	private final Texture roadImage;
	private final Vector2 start;
	private final float length;
	private final float degrees;

	public Path(Texture roadImage, Vector2 start, Vector2 finish) {
		this.roadImage = roadImage;
		this.start = start;
		length = start.dst(finish);
		degrees = (float) (
			Math.atan2(
				finish.y - start.y,
				finish.x - start.x
			) * 180.0d / Math.PI);
	}
	
	@Override
    public void draw(Batch batch, float parentAlpha) {
		batch.draw(roadImage, start.x, start.y, roadImage.getWidth()/2, roadImage.getHeight()/2, roadImage.getWidth(), length, 1, 1, 270+degrees, 0, 0, (int)roadImage.getWidth(), (int)roadImage.getHeight(), false, false);	
	}
}

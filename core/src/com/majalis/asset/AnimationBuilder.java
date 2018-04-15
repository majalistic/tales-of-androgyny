package com.majalis.asset;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class AnimationBuilder {
	private final Animation animation;

	public AnimationBuilder(Texture texture, int numFrames, float width, float height) { this(texture, numFrames, width, height, .07f); }
	public AnimationBuilder(Texture texture, int numFrames, float width, float height, float frameDuration) {
		Array<TextureRegion> frames = new Array<TextureRegion>();
		for (int ii = 0; ii < numFrames; ii++) {
			frames.add(new TextureRegion(texture, (int) (ii * width), 0, (int) width, (int) height));
		}
		animation = new Animation(frameDuration, frames);		
	}
	
	public Animation build() { return animation; }
}

package com.majalis.encounter;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Background extends Actor{

	private final Texture texture;
	private final int width;
	private final int height;
	
	public Background(Texture texture){
		this(texture, 1280, 720);
	}
	public Background(Texture texture, int width, int height){
		this.texture = texture;
		this.width = width;
		this.height  = height;
	}
	
	@Override
    public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		batch.draw(texture, (1280-width)/2, (720-height)/2, width, height);
    }
	
	protected Background clone(){
		return new Background(texture, width, height);
	}	
}

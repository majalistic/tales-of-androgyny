package com.majalis.encounter;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Background extends Actor{

	private final Texture texture;
	private  Texture texture2;
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
	
	public Background(Texture texture, Texture texture2){
		this(texture, texture2, 1280, 720);
	}
	public Background(Texture texture, Texture texture2, int width, int height){
		this.texture = texture;
		this.texture2 = texture2;
		this.width = width;
		this.height  = height;
	}
	
	@Override
    public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		batch.draw(texture, (1280-width)/2, (720-height)/2, width, height);
		if (texture2 != null){
			batch.draw(texture2, 0, 0, 1280, 800);
		}	
    }
	
	protected Background clone(){
		return new Background(texture, texture2, width, height);
	}	
}

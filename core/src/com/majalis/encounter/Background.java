package com.majalis.encounter;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Background extends Actor{

	private final Texture texture;
	protected Background(Texture texture){
		this.texture = texture;
	}

	@Override
    public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		batch.draw(texture, 0, 0, 1280, 720);
    }
	
	protected Background clone(){
		return new Background(texture);
	}
	
}

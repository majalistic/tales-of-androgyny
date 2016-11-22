package com.majalis.encounter;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class Background extends Image{

	private final Texture texture;
	private  Texture texture2;
	private final int width;
	private final int height;
	private final int width2;
	private final int height2;
	
	public Background(Texture texture){
		this(texture, 1280, 720);
	}
	public Background(Texture texture, int width, int height){
		this(texture, null, width, height, 1280, 720);
	}
	
	public Background(Texture texture, Texture texture2){
		this(texture, texture2, 1280, 720);
	}
	public Background(Texture texture, Texture texture2, int width, int height){
		this(texture, texture2, width, height, 1280, 720);
	}
	// ignoring width2 for now
	public Background(Texture texture, Texture texture2, int width, int height, int width2, int height2) {
		this.texture = texture;
		this.texture2 = texture2;
		this.width = width;
		this.height  = height;
		this.width2 = texture2 == null ? width2 : (int) (texture2.getWidth() / (texture2.getHeight() / (height2 * 1.)) );
		this.height2 = height2;
	}
	
	@Override
    public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		batch.draw(texture, (1280-width)/2, (720-height)/2, width, height);
		if (texture2 != null){
			batch.draw(texture2, (1280-width2)/2, ((720-height2)/2) + (height2 == 720 ? 0 : 100), width2, height2);
		}	
    }
	
	protected Background clone(){
		return new Background(texture, texture2, width, height, width2, height2);
	}	
}

package com.majalis.world;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Cloud extends Actor{
	
	private final Texture cloud;
	private final int x;
	private final int y;
	private final int width;
	private final int height;
	private final Camera camera;
	public Cloud(Texture cloud, int x, int y, Camera camera){
		this.cloud = cloud;
		this.x = x;
		this.y = y;
		this.width = 800;
		this.height = 800;
		this.camera = camera;
		
	}
	@Override 
	public void draw(Batch batch, float alpha){
		batch.setTransformMatrix(camera.view);
		batch.setColor(1.0f, 1.0f, 1.0f, .3f);
		batch.draw(cloud, x, y, width, height);
	}
}

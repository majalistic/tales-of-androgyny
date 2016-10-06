package com.majalis.world;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;

public class Path extends Actor {
	private final Array<PathChunk> pathChunks;
	public Path(Texture roadImage, Vector2 start, Vector2 finish) {
		pathChunks = new Array<PathChunk>();
		pathChunks.add(new PathChunk(roadImage, start, finish));
	}
	
	@Override
    public void draw(Batch batch, float parentAlpha) {
		for (PathChunk chunk : pathChunks){
			chunk.draw(batch, parentAlpha);
		}	
	}
}

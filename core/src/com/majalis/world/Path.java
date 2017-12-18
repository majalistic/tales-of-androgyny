package com.majalis.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;

public class Path extends Actor {
	private final Array<PathChunk> pathChunks;
	public Path(Texture roadImage, Vector2 start, Vector2 finish) {
		pathChunks = new Array<PathChunk>();
		int distance = GameWorldHelper.distance((int)start.x, (int)start.y, (int)finish.x, (int)finish.y);
		while (distance > 0) {
			Vector2 currentStart = new Vector2(start.x, start.y);
			if (start.x + start.y == finish.x + finish.y) { // z is constant
				if (start.x < finish.x) { // downright
					start.x++;
					start.y--;
				}
				else { // upleft
					start.x--;
					start.y++;
				}
			}
			else if (start.y == finish.y) { // y is constant
				if (start.x < finish.x) start.x++; // upright
				else start.x--; // downleft
			}
			else if (start.x == finish.x) { // x is constant
				if (start.y < finish.y) start.y++; // up
				else start.y--; // down
			}
			else {
				int startZ = (int) (0 - (start.x + start.y));
				int finishZ = (int) (0 - (finish.x + finish.y));
				if (start.x > finish.x && startZ < finishZ) {
					start.x--;
				}
				else if (finish.y > start.y && startZ > finishZ) {
					start.y++;
				}
				else {
					start.x++;
					start.y--;
				}
			}
			pathChunks.add(new PathChunk(roadImage, currentStart, start));
			distance = GameWorldHelper.distance((int)start.x, (int)start.y, (int)finish.x, (int)finish.y);
		}
	}
	
	@Override
    public void setColor(Color color) {
		super.setColor(color);
		for (PathChunk chunk : pathChunks) {
			chunk.setColor(color);
		}	
	}
	
	@Override
    public void act(float delta) {
		super.act(delta);
		for (PathChunk chunk : pathChunks) {
			chunk.act(delta);
		}	
	}
	
	@Override
    public void draw(Batch batch, float parentAlpha) {
		for (PathChunk chunk : pathChunks) {
			chunk.draw(batch, parentAlpha);
		}	
	}
}

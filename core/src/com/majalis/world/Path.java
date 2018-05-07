package com.majalis.world;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public class Path extends Group {
	private final Array<Vector2> pathChunks;
	public Path(Vector2 start, Vector2 finish) {
		pathChunks = new Array<Vector2>();
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
			pathChunks.add(new Vector2(currentStart));
			distance = GameWorldHelper.distance((int)start.x, (int)start.y, (int)finish.x, (int)finish.y);
		}
	}
	
	public void setPathTextures(ObjectMap<Vector2, TextureRegion> pathTextureMap) {
		for (ObjectMap.Entry<Vector2, TextureRegion> pathTexture : pathTextureMap) {
			if (pathChunks.contains(pathTexture.key, true)) {
				Image newPathChunk = new Image(pathTexture.value);
				newPathChunk.setPosition(GameWorldHelper.getTrueX((int)pathTexture.key.x), GameWorldHelper.getTrueY((int)pathTexture.key.x, (int)pathTexture.key.y));
				this.addActor(newPathChunk);
			}			
		}
	}
	
	public Array<Vector2> getChunks() { return pathChunks; }	
	
	@Override
	public void setColor(Color color) {
		for (Actor child : getChildren()) {
			child.setColor(color);
		}
		super.setColor(color);
	}	
}

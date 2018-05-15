package com.majalis.world;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Scaling;
import com.majalis.asset.AnimatedImage;
import com.majalis.asset.AssetEnum;

public class GameWorld {

	private final Array<GameWorldNode> nodes;
	private final Array<Array<GroundType>> ground;
	private final Array<Doodad> doodads;
	private final Array<Shadow> shadows;
	private final Array<Image> reflections;
	private final Array<AnimatedImage> lilies;
	// this should not need an assetmanager - trees and doodads should not be actors in here
	protected GameWorld(Array<GameWorldNode> nodes, AssetManager assetManager, RandomXS128 random) {
		this.nodes = nodes;
		ground = new Array<Array<GroundType>>();
		doodads = new Array<Doodad>();
		shadows = new Array<Shadow>();		
		reflections = new Array<Image>();
		lilies = new Array<AnimatedImage>();
		
		Array<TextureRegion> forestTextures = new Array<TextureRegion>();
		Array<TextureRegion> forestShadowTextures = new Array<TextureRegion>();
		forestTextures.add(new TextureRegion(assetManager.get(AssetEnum.FOREST_1.getTexture())));
		forestTextures.add(new TextureRegion(assetManager.get(AssetEnum.FOREST_2.getTexture())));
		TextureRegion forestShadowTexture = new TextureRegion(assetManager.get(AssetEnum.FOREST_1.getTexture()));
		forestShadowTexture.flip(true, false);
		forestShadowTextures.add(forestShadowTexture);
		forestShadowTexture = new TextureRegion(assetManager.get(AssetEnum.FOREST_2.getTexture()));
		forestShadowTexture.flip(true, false);
		forestShadowTextures.add(forestShadowTexture);
		
		
		Texture treeClusterTextureSheet = assetManager.get(AssetEnum.TREE_CLUSTERS.getTexture());
		Array<TextureRegion> treeClusterTextures = new Array<TextureRegion>();
		Array<TextureRegion> treeClusterShadowTextures = new Array<TextureRegion>();
		int treeClusterRowSize = 4;
		int treeClusterWidth = 384;
		int treeClusterHeight = 256;
		for (int ii = 0; ii < treeClusterRowSize; ii++) {
			for (int jj = 0; jj < 2; jj++) {
				treeClusterTextures.add(new TextureRegion(treeClusterTextureSheet, ii * treeClusterWidth, jj * treeClusterHeight, treeClusterWidth, treeClusterHeight));
				TextureRegion shadowTexture = new TextureRegion(treeClusterTextureSheet, ii * treeClusterWidth, jj * treeClusterHeight, treeClusterWidth, treeClusterHeight);
				shadowTexture.flip(true, false);
				treeClusterShadowTextures.add(shadowTexture);
			}
		}
		
		Texture doodadTextureSheet = assetManager.get(AssetEnum.DOODADS.getTexture());
		Array<TextureRegion> treeTextures = new Array<TextureRegion>();
		Array<TextureRegion> treeShadowTextures = new Array<TextureRegion>();
		int treeRowSize = 7;
		int treeWidth = 192;
		int treeHeight = 256;
		for (int ii = 0; ii < treeRowSize; ii++) {
			for (int jj = 0; jj < 4; jj++) {
				treeTextures.add(new TextureRegion(doodadTextureSheet, ii * treeWidth, jj * treeHeight, treeWidth, treeHeight));
				TextureRegion shadowTexture = new TextureRegion(doodadTextureSheet, ii * treeWidth, jj * treeHeight, treeWidth, treeHeight);
				shadowTexture.flip(true, false);
				treeShadowTextures.add(shadowTexture);
			}
		}
		
		Array<TextureRegion> rockTextures = new Array<TextureRegion>();
		Array<TextureRegion> rockShadowTextures = new Array<TextureRegion>();
		int rockRowSize = 5;
		int rockWidth = 256;
		int rockHeight = 128;
		for (int ii = 0; ii < rockRowSize; ii++) {
			for (int jj = 0; jj < 4; jj++) {
				rockTextures.add(new TextureRegion(doodadTextureSheet, ii * rockWidth, treeHeight * 4 + jj * rockHeight, rockWidth, rockHeight));
				TextureRegion shadowTexture = new TextureRegion(doodadTextureSheet, ii * rockWidth, treeHeight * 4 + jj * rockHeight, rockWidth, rockHeight);
				shadowTexture.flip(true, false);
				rockShadowTextures.add(shadowTexture);
			}
		}
		
		Array<Animation> lilyAnimations = new Array<Animation>();
		int lilyArraySize = 15;
		int lilyWidth = 64;
		int lilyHeight = 64;
		for (int ii = 0; ii < lilyArraySize; ii++) {
			Array<TextureRegion> frames = new Array<TextureRegion>();
			frames.add(new TextureRegion(doodadTextureSheet, ii * lilyWidth, treeHeight * 4 + rockHeight * 4, lilyWidth, lilyHeight));
			frames.add(new TextureRegion(doodadTextureSheet, ii * lilyWidth, treeHeight * 4 + rockHeight * 4 + lilyHeight, lilyWidth, lilyHeight));
			Animation lilyAnimation = new Animation(.28f, frames);
			lilyAnimation.setPlayMode(PlayMode.LOOP);
			lilyAnimations.add(lilyAnimation);
		}		
		
		Array<Vector2> pathChunks = new Array<Vector2>();
		for (GameWorldNode node : nodes) {
			for (Path path : node.getPaths()) {
				pathChunks.addAll(path.getChunks());
			}
		}
		
		Texture pathSheet = assetManager.get(AssetEnum.ROAD_TILES.getTexture());
		ObjectMap<Vector2, TextureRegion> pathTextureMap = new ObjectMap<Vector2, TextureRegion>();
		for (Vector2 pathChunk : pathChunks) {
			int tileMask = 0;
			if (pathChunks.contains(new Vector2(pathChunk.x + 1, pathChunk.y), false)) tileMask += 1; // top right
			if (pathChunks.contains(new Vector2(pathChunk.x + 1, pathChunk.y - 1), false)) tileMask += 2; // bottom right
			if (pathChunks.contains(new Vector2(pathChunk.x, pathChunk.y - 1), false) && !(pathChunks.contains(new Vector2(pathChunk.x - 1, pathChunk.y), false) || pathChunks.contains(new Vector2(pathChunk.x + 1, pathChunk.y - 1), false))) tileMask += 4; // bottom
			if (pathChunks.contains(new Vector2(pathChunk.x - 1, pathChunk.y), false)) tileMask += 8; // bottom left
			if (pathChunks.contains(new Vector2(pathChunk.x - 1, pathChunk.y + 1), false)) tileMask += 16; // top left
			if (pathChunks.contains(new Vector2(pathChunk.x, pathChunk.y + 1), false) && !(pathChunks.contains(new Vector2(pathChunk.x + 1, pathChunk.y), false) || pathChunks.contains(new Vector2(pathChunk.x - 1, pathChunk.y + 1), false))) tileMask += 32; // top
			
			pathTextureMap.put(pathChunk, new TextureRegion(pathSheet, (tileMask % 32) * (GameWorldHelper.getTileWidth()) + 1, (tileMask > 31 ? 1 : 0) * (GameWorldHelper.getTileHeight()), GameWorldHelper.getTileWidth(), GameWorldHelper.getTileHeight()));
		}
		
		
		// this should grab all the textures, make all the path chunks, and just register them with the individual paths
		

		ObjectMap<Vector2, Image> pathChunkMap = new ObjectMap<Vector2, Image>();
		for (ObjectMap.Entry<Vector2, TextureRegion> pathTexture : pathTextureMap) {
			Image newPathChunk = new Image(pathTexture.value);
			newPathChunk.setPosition(GameWorldHelper.getTrueX((int)pathTexture.key.x), GameWorldHelper.getTrueY((int)pathTexture.key.x, (int)pathTexture.key.y));
			pathChunkMap.put(pathTexture.key, newPathChunk);
		}
				
		for (GameWorldNode node : nodes) {
			for (Path path : node.getPaths()) {
				path.setPathTextures(pathChunkMap);
			}
		}
				
		int maxX = 230;
		int maxY = 235;
		int tileWidth = GameWorldHelper.getTileWidth();
		int tileHeight = GameWorldHelper.getTileHeight();
		
		
		// first figure out what all of the tiles are - dirt, greenLeaf, redLeaf, moss, or water - create a model without drawing anything	
		for (int x = 0; x < maxX; x++) {
			Array<GroundType> layer = new Array<GroundType>();
			ground.add(layer);
			
			for (int y = 0; y < maxY; y++) {
				// redLeaf should be the default			
				// dirt should be randomly spread throughout redLeaf  
				// greenLeaf might also be randomly spread throughout redLeaf
				// bodies of water should be generated as a single central river that runs through the map for now, that randomly twists and turns and bulges at the turns
				// moss should be in patches adjacent to water
				
				int closest = worldCollide(x, y);
				
				GroundType toAdd;
				if (closest >= 2 && (isRiver(x, y) || isLake(x, y))) toAdd = GroundType.WATER;
				else if (closest <= 4 || (closest <= 5 && (Math.abs(random.nextInt() % 2) == 0)) || pathChunks.contains(new Vector2(x, y), false) || shoreline(x, y)) toAdd = GroundType.DIRT;
				else {
					toAdd = GroundType.valueOf("RED_LEAF_" + Math.abs(random.nextInt() % 6));					
				}
				
				layer.add(toAdd);
				
				if (toAdd == GroundType.WATER && random.nextInt() % 5 == 0) {
					AnimatedImage lily = new AnimatedImage(lilyAnimations.get(Math.abs(random.nextInt() % lilyArraySize)), Scaling.fit, Align.center);
					lily.setState(0);
					int trueX = getTrueX(x) - (int)lily.getWidth() / 2 + tileWidth / 2;
					int trueY = getTrueY(x, y) + tileHeight / 2;
					lily.setPosition(trueX, trueY);
					lilies.add(lily);
				}
				
				boolean treeAbundance = isAbundantTrees(x, y);				
				if (closest >= 3 && toAdd == GroundType.DIRT || toAdd == GroundType.RED_LEAF_0 || toAdd == GroundType.RED_LEAF_1) {
					int rando = random.nextInt();
					if (rando % (treeAbundance ? 2 : 5) == 0) {
						Array<TextureRegion> textures;
						Array<TextureRegion> shadowTextures;
						int arraySize;
						if (isSuperAbundantTrees(x, y) && rando % 7 == 0) {
							textures = forestTextures;
							shadowTextures = forestShadowTextures;
							arraySize = forestTextures.size;
						}
						else if (closest > 6 && (treeAbundance || rando % 7 == 0)) {
							textures = treeClusterTextures;
							shadowTextures = treeClusterShadowTextures;
							arraySize = treeClusterTextures.size;
						}
						else if (!(treeAbundance) && random.nextInt() % 6 == 0) {
							textures = rockTextures;
							shadowTextures = rockShadowTextures;
							arraySize = rockTextures.size;
						}
						else {
							textures = treeTextures;
							shadowTextures = treeShadowTextures;
							arraySize = treeTextures.size;
						}
						int chosen = Math.abs(random.nextInt() % arraySize);
						if (textures == treeTextures) { chosen = isDeadTree(chosen) && x + y < 150 ? (chosen % 7 * 4) : chosen; }
						Doodad doodad = new Doodad(textures.get(chosen));
						Shadow shadow = new Shadow(shadowTextures.get(chosen));
						Image reflection = new Image(shadowTextures.get(chosen));
						int trueX = getTrueX(x) - (int)doodad.getWidth() / 2 + tileWidth / 2;
						int trueY = getTrueY(x, y) + tileHeight / 2;
						doodad.setPosition(trueX , trueY);
						shadow.setPosition(trueX, trueY);
						reflection.setPosition(trueX, trueY);
						reflection.setOrigin(reflection.getWidth() / 2, 16);
						reflection.rotateBy(180);
						reflection.addAction(Actions.alpha(.6f));
						
						boolean doodadInserted = false;
						int ii = 0;
						for (Image compare : doodads) {
							if (doodad.getY() > compare.getY()) {
								doodadInserted = true;
								doodads.insert(ii, doodad);
								shadows.insert(ii, shadow);
								reflections.insert(ii, reflection);
								break;
							}
							ii++;
						}
						if (!doodadInserted) {
							doodads.add(doodad);
							shadows.add(shadow);
							reflections.add(reflection);
						}
					}
				}	
			}
		}			
		
		// iterate a second pass through and determine where rocks and trees and shadows should go
		/*for (int x = 0; x < ground.size; x++) {
			int layerSize = ground.get(x).size;
			for (int y = 0; y < ground.get(x).size; y++) {
				// place random rocks on tiles adjacent to water
				
				// place random trees on redLeaf/greenLeaf/dirt tiles that aren't adjacent to water
				// for each tree, create a shadow (should be mapped shadow textures placed at the same location as the tree)

			}
		}*/
	}
	
	private boolean isDeadTree(int val) { return val == 1 || val == 3 || val == 5 || val == 17 || val == 19 || val == 22 || val == 23; }
	public Array<GameWorldNode> getNodes() { return nodes; }
	public Array<Array<GroundType>> getGround() { return ground; }
	public Array<Doodad> getDoodads() { return doodads; }
	public Array<Shadow> getShadows() { return shadows; }
	public Array<Image> getReflections() { return reflections; }
	public Array<AnimatedImage> getLilies() { return lilies; }

	private int distance(int x, int y, int x2, int y2) { return GameWorldHelper.distance(x, y, x2, y2); }	
	private boolean shoreline(int x, int y) {
		int shoreLineDistance = 2;
		for (int ii = -shoreLineDistance; ii < shoreLineDistance; ii++) {
			//for (int jj = -shoreLineDistance + Math.abs(ii); jj <= shoreLineDistance - Math.abs(ii); jj++) {
			for (int jj = Math.max(-shoreLineDistance, -ii - shoreLineDistance); jj < Math.min(shoreLineDistance, -ii + shoreLineDistance); jj++) {
				if (isRiver(ii + x, jj + y)) return true;
				if (isLake(ii + x, jj + y)) return true;
			}
		}
		
		return false;
	}
	private boolean isRiver(int x, int y) { return downRightRiverWiggle(x, y, 140, 7, 50, 200) || upRightRiverWiggle(x, y, 50, 4, 140, 200); }//downRightRiver(x, y, 140, 7, 50, 10000) upRightRiver(x, y, 50, 9, 140, 10000) || upRightRiver(x, y, 48, 13, 160, 163); }
	
	private boolean upRightRiverWiggle(int x, int y, int start, int width, int lowerBound, int upperBound) { // should also have wiggle amount, maybe width variance
		// start at lower bound, and add or subtract a wiggle factor using modulus(?) to determine position
		for (; lowerBound <= upperBound; lowerBound++) {
			int offset = 0;
			switch (lowerBound % 10) {
				case 0: case 5: offset = 0; break;
				case 1: case 4: offset = 1; break;
				case 2: case 3: offset = 2; break;
				case 6: case 9: offset = -1;
				case 7: case 8: offset = -2;
			}
			if (upRightRiver(x, y, start + offset, width, lowerBound, lowerBound + 1)) { return true; }
		}
		return false;
	}
	
	private boolean downRightRiverWiggle(int x, int y, int start, int width, int lowerBound, int upperBound) { // should also have wiggle amount, maybe width variance
		// start at lower bound, and add or subtract a wiggle factor using modulus(?) to determine position
		for (; lowerBound <= upperBound; lowerBound++) {
			int offset = 0;
			switch (lowerBound % 14) {
				case 0: case 7: offset = 0; break;
				case 1: case 6: offset = 1; break;
				case 2: case 3: case 4: case 5: offset = 2; break;
				case 8: case 13: offset = -1;
				case 9: case 10: case 11: case 12: offset = -2;
			}
			if (downRightRiver(x, y, start + offset, width, lowerBound, lowerBound + 1)) { return true; }
		}
		return false;
	}
	
	private boolean downRightRiver(int x, int y, int start, int width, int lowerBound, int upperBound) { return x + y > start && x + y <= start + width && y > lowerBound && y <= upperBound; }
	private boolean upRightRiver(int x, int y, int start, int width, int lowerBound, int upperBound) { return y > start && y <= start + width && x + y > lowerBound && x + y <= upperBound; }
	private boolean isLake(int x, int y) { return lake(x, y, 13, 90, 5) || lake(x, y, 87, 55, 7) || lake(x, y, 80, 62, 5) || lake(x, y, 94, 55, 3); }		
	private boolean lake(int x, int y, int lakeX, int lakeY, int size) { return distance(x, y, lakeX, lakeY) < size; }
	
	private boolean isSuperAbundantTrees(int x, int y) { return x + y * 2 > 170 && x + y * 2 < 180; }
	private boolean isAbundantTrees(int x, int y) { return (x > 0 && x < 10) || isSuperAbundantTrees(x, y); }
	
	private int worldCollide(int x, int y) {
		int minDistance = 100;
		for (GameWorldNode node : nodes) {
			int distance = distance(x, y, (int)node.getHexPosition().x, (int)node.getHexPosition().y);
			if (distance < minDistance) minDistance = distance;
		}
		return minDistance;
	}
	
	private int getTrueX(int x) { return GameWorldHelper.getTrueX(x); }
	private int getTrueY(int x, int y) { return GameWorldHelper.getTrueY(x, y); }
	
	public class Doodad extends Image {
		private Doodad(TextureRegion textureRegion) {
			super(textureRegion);
		}
		
		@Override
		public Actor hit(float x, float y, boolean touchable) { return null; }		
	}
		
	public static class SkewAction extends TemporalAction {
		private Vector2 start, end;
		
		public SkewAction(Vector2 end, float duration) {
			super(duration);
			this.end = end;
		}
		
		@Override
		protected void begin () {
			start = new Vector2(((Shadow)target).getSkew());
		}
		
		@Override
		protected void update (float percent) {
			((Shadow)target).setSkew(start.x + ((end.x - start.x) * percent), start.y + ((end.y - start.y) * percent));
		}

		@Override
		public void reset () {
			super.reset();
			start = end = null;
		}
		
		public void setSkew (Vector2 skew) {
			this.end = skew;
		}
	}
	
	public class Shadow extends Actor {
		private final TextureRegion texture;
		private Affine2 affine = new Affine2();
		private Vector2 skew;
		
		private Shadow(TextureRegion textureRegion) {
			this.texture = textureRegion;
			skew = new Vector2();
		}

		public void setSkew(float shadowDirection, float shadowLength) {
			skew.x = shadowDirection;
			skew.y = shadowLength;			
		}
		
		public Vector2 getSkew() { return skew; } 

		@Override
	    public void draw(Batch batch, float parentAlpha) {
			Color color = getColor();
			batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
			affine.setToTrnRotScl(getX() + texture.getRegionWidth() + (getOriginX()), getY() + (getOriginY()*2)+3, 180, 1, 1);
	        affine.shear(skew.x, 0);  // this modifies the skew
			batch.draw(texture, texture.getRegionWidth(), texture.getRegionHeight() * skew.y, affine);
	    }
	}
	
}

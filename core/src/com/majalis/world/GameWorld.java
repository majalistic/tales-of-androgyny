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
				
				if (closest >= 2 && (river(x, y) || lake(x, y))) toAdd = GroundType.WATER;
				else if (closest <= 3 || shoreline(x, y)) toAdd = GroundType.DIRT;
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
					if (random.nextInt() % (treeAbundance ? 2 : 5) == 0) {
						Array<TextureRegion> textures;
						Array<TextureRegion> shadowTextures;
						int arraySize;
						if (!(treeAbundance) && random.nextInt() % 6 == 0) {
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
	public Array<GameWorldNode> getNodes() { return nodes; }
	public Array<Array<GroundType>> getGround() { return ground; }
	public Array<Doodad> getDoodads() { return doodads; }
	public Array<Shadow> getShadows() { return shadows; }
	public Array<Image> getReflections() { return reflections; }
	public Array<AnimatedImage> getLilies() { return lilies; }

	private int distance(int x, int y, int x2, int y2) { return GameWorldHelper.distance(x, y, x2, y2); }	
	private boolean shoreline(int x, int y) {
		for (int ii = x - 2; ii <= x + 2; ii++) {
			for (int jj = y - 2; jj <= y + 2; jj++) {
				if (river(ii, jj)) return true;
				if (lake(ii, jj)) return true;
			}
		}
		return false;
	}
	
	private boolean river(int x, int y) { return (x + y > 140 && x + y < 148 && y > 50) || (y > 50 && y < 60 && x + y > 140); }
	private boolean lake(int x, int y) { return distance(x, y, 13, 90) < 5 || distance(x, y, 87, 55) < 7 || distance(x, y, 80, 62) < 5 || distance(x, y, 94, 55) < 5; }	
	private boolean isAbundantTrees(int x, int y) { return (x + y > 147 && x + y < 150) || (x > 0 && x < 15) || (x + y * 2 > 180 && x + y * 2 < 195); }
	
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

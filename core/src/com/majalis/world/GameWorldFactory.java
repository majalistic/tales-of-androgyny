package com.majalis.world;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.majalis.save.LoadService;
import com.majalis.save.SaveEnum;
import com.majalis.save.SaveManager;
import com.majalis.save.SaveService;
import com.majalis.save.SaveManager.GameMode;
/*
 * Generates a world map or returns the world map.
 */
public class GameWorldFactory {

	private final SaveService saveService;
	private final LoadService loadService;
	private final AssetManager assetManager;
	private final BitmapFont font;
	private final RandomXS128 random;
	
	public GameWorldFactory(SaveManager saveManager, AssetManager assetManager,  FreeTypeFontGenerator fontGenerator, RandomXS128 random){
		this.saveService = saveManager;
		this.loadService = saveManager;
		this.assetManager = assetManager;
		FreeTypeFontParameter fontParameter = new FreeTypeFontParameter();
	    fontParameter.size = 24;
	    font = fontGenerator.generateFont(fontParameter);
		this.random = random;
	}
	
	public GameWorld getGameWorld(int seed, GameMode gameMode) {
		random.setSeed(seed);

		IntMap<GameWorldNode> nodeMap = new IntMap<GameWorldNode>();
		Array<GameWorldNode> nodes = new Array<GameWorldNode>();
		// specifying the start and end points to a getZone method which will perform the below functions, creating an array of nodes.  Can make it a class so that it can be chained?  Piping in the end points as start points

		if (gameMode == GameMode.SKIRMISH){
			Zone zone = new Zone(saveService, loadService, font, assetManager, random, nodes, nodeMap, 3)
					.addStartNode(1, 0, -1, new Vector2(500, 500))
					.addEndNode(1000, 1000, 1000, new Vector2(900, 900))
					.buildZone();
			
			Zone zone2 = new Zone(saveService, loadService, font, assetManager, random, nodes, nodeMap, 8)
					.addStartNode(zone.getEndNodes().get(0))
					.addEndNode(1001, 1001, 1001, new Vector2(1800, 1800))
					.addEndNode(1002, 1001, 1001, new Vector2(1200, 2400))
					.buildZone();
			
			new Zone(saveService, loadService, font, assetManager, random, nodes, nodeMap, 8)
					.addStartNode(zone2.getEndNodes().get(0))
					.addEndNode(1003, 1001, 1001, new Vector2(3000, 3000))
					.addEndNode(1004, 1001, 1001, new Vector2(3000, 1700))
					.buildZone();
		}
		else {
			new Zone(saveService, loadService, font, assetManager, random, nodes, nodeMap, 2)
					.addStartNode(1, 2000, 2000, new Vector2(500, 500))
					.addEndNode(1000, 1000, 1000, new Vector2(900, 900))
					.buildZone();
		}
		
		nodeMap.get((Integer)loadService.loadDataValue(SaveEnum.NODE_CODE, Integer.class)).setAsCurrentNode();
		nodes.sort();
		return new GameWorld(nodes);
	}
}

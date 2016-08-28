package com.majalis.world;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectSet;
import com.majalis.save.LoadService;
import com.majalis.save.SaveManager;
import com.majalis.save.SaveService;
/*
 * Generates a world map or returns the world map.
 */
public class GameWorldFactory {

	private final SaveService saveService;
	private final LoadService loadService;
	private final BitmapFont font;
	private GameContext context;
	
	public GameWorldFactory(SaveManager saveManager, BitmapFont font){
		this.saveService = saveManager;
		this.loadService = saveManager;
		this.font = font;
	}
	
	public GameWorld getGameWorld() {
		Array<GameWorldNode> nodes = new Array<GameWorldNode>();
		ObjectSet<Integer> visitedCodesSet = loadService.loadDataValue("VisitedList", ObjectSet.class);
		for (int ii = 1; ii <= 10; ii++){
			// 100 = magic number to get the defaultEncounter for now
			nodes.add(new GameWorldNode(new Array<GameWorldNode>(), saveService, loadService, font, ii, ii-1, 100, new Vector2(ii * 85, 200 + (200 * Math.floorMod(ii, 3))-ii*10), visitedCodesSet.contains(ii) ? true : false));
		}
		
		for (int ii = 0; ii < nodes.size-1; ii++){
			for (int jj = ii + 1; jj < nodes.size; jj++){
				if (nodes.get(ii).isAdjacent(nodes.get(jj))){
					nodes.get(ii).connectTo(nodes.get(jj));
				}
			}
		}
		
		nodes.get((Integer)loadService.loadDataValue("NodeCode", Integer.class) - 1).setAsCurrentNode();
		return new GameWorld(nodes);
	}

	public void setContext(GameContext context) {
		this.context = context;
	}
	
	public GameContext getGameContext() {
		return context;
	}
	
	public enum ClassEnum {
		WARRIOR ("Warrior"),
		PALADIN ("Paladin"),
		THIEF ("Thief"),
		RANGER ("Ranger"),
		MAGE ("Mage"),
		ENCHANTRESS ("Enchanter");
		
		private final String label;

		ClassEnum(String label) {
		    this.label = label;
		 }
		public String getLabel(){return label;}
	}
	
	public enum GameContext {
		ENCOUNTER,
		WORLD_MAP
	}
}

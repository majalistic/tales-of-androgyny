package com.majalis.world;

import static com.majalis.encounter.EncounterCode.*;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.majalis.asset.AssetEnum;
import com.majalis.character.PlayerCharacter;
import com.majalis.encounter.EncounterCode;
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
	private Sound sound;
	private PlayerCharacter character;
	private IntMap<GameWorldNode> nodeMap = new IntMap<GameWorldNode>();
	private Array<GameWorldNode> nodes = new Array<GameWorldNode>();
	
	public GameWorldFactory(SaveManager saveManager, AssetManager assetManager,  FreeTypeFontGenerator fontGenerator, RandomXS128 random){
		this.saveService = saveManager;
		this.loadService = saveManager;

		this.assetManager = assetManager;
		FreeTypeFontParameter fontParameter = new FreeTypeFontParameter();
	    fontParameter.size = 32;
	    font = fontGenerator.generateFont(fontParameter);
		this.random = random;
	}
	
	@SuppressWarnings("unchecked")
	public GameWorld getGameWorld(int seed, GameMode gameMode) {
		random.setSeed(seed);

		nodeMap = new IntMap<GameWorldNode>();
		nodes = new Array<GameWorldNode>();
		// specifying the start and end points to a getZone method which will perform the below functions, creating an array of nodes.  Can make it a class so that it can be chained?  Piping in the end points as start points

		sound = assetManager.get(AssetEnum.CLICK_SOUND.getPath(), Sound.class);
		character = loadService.loadDataValue(SaveEnum.PLAYER, PlayerCharacter.class);
		
		if (gameMode == GameMode.SKIRMISH){
			Zone zone = new Zone(saveService, loadService, font, assetManager, random, nodes, nodeMap, 3)
					.addStartNode(1, INITIAL, DEFAULT, new Vector2(500, 500))
					.addEndNode(1000, TOWN, TOWN, new Vector2(900, 900))
					.buildZone();
			
			Zone zone2 = new Zone(saveService, loadService, font, assetManager, random, nodes, nodeMap, 8)
					.addStartNode(zone.getEndNodes().get(0))
					.addEndNode(1001, FORT, FORT, new Vector2(1800, 1800))
					.addEndNode(1002, FORT, FORT, new Vector2(1200, 2400))
					.buildZone();
			
			new Zone(saveService, loadService, font, assetManager, random, nodes, nodeMap, 8)
					.addStartNode(zone2.getEndNodes().get(0))
					.addEndNode(1003, FORT, FORT, new Vector2(3000, 3000))
					.addEndNode(1004, FORT, FORT, new Vector2(3000, 1700))
					.buildZone();
		}
		else {
			addNode(getNode(1, DEFAULT, DEFAULT, new Vector2(500, 500), true), 1, nodes);
			addNode(getNode(2, COTTAGE_TRAINER, COTTAGE_TRAINER_VISIT, new Vector2(600, 600), false), 2, nodes);
			addNode(getNode(3, TOWN_STORY, TOWN2, new Vector2(700, 700), false), 3, nodes);
			addNode(getNode(4, FIRST_BATTLE_STORY, DEFAULT, new Vector2(800, 800), false), 4, nodes);
			
			new Zone(saveService, loadService, font, assetManager, random, nodes, nodeMap, 1)
					.addStartNode(nodes.get(nodes.size-1))
					.addEndNode(1000, TOWN, TOWN, new Vector2(1000, 1000))
					.buildZone();
		}
		
		nodeMap.get((Integer)loadService.loadDataValue(SaveEnum.NODE_CODE, Integer.class)).setAsCurrentNode();
		nodes.sort();
		return new GameWorld(nodes);
	}
	
	private void addNode(GameWorldNode newNode, int nodeCode, Array<GameWorldNode> ... nodes){
		for (Array<GameWorldNode> nodeArray: nodes){
			nodeArray.add(newNode);
		}
		nodeMap.put(nodeCode, newNode);
	}
	
	private GameWorldNode getNode(int nodeCode, EncounterCode initialEncounter, EncounterCode defaultEncounter, Vector2 position, boolean visited){
		return new GameWorldNode(saveService, font, nodeCode, new GameWorldNodeEncounter(initialEncounter, defaultEncounter), position, visited, sound, character, assetManager);
	}
}

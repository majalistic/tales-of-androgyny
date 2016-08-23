package com.majalis.traprpg;

import com.badlogic.gdx.assets.AssetManager;
/*
 * The screen that displays the world map.  UI that Handles player input while on the world map - will delegate to other screens depending on the gameWorld state.
 */
public class GameScreen extends AbstractScreen {

	private final AssetManager assetManager;
	private final SaveService saveService;
	private final GameWorld world;
	
	public GameScreen(ScreenFactory factory, AssetManager assetManager, SaveService saveService, GameWorld world) {
		super(factory);
		this.assetManager = assetManager;
		this.saveService = saveService;
		this.world = world;
	}
	
	@Override
	public void buildStage() {
		// asynchronous

	}
	
}
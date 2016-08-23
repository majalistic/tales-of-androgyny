package com.majalis.traprpg;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
/*
 * ScreenFactory implementation to generate and cache screens.
 */
public class ScreenFactoryImpl implements ScreenFactory {

	private final Game game;
	private final AssetManager assetManager;
	private final SaveService saveService;
	private final LoadService loadService;	
	private final GameWorldManager gameWorldManager;
	private final EncounterFactory encounterFactory;
	private int temp;
	
	public ScreenFactoryImpl(Game game, AssetManager assetManager, SaveManager saveManager, GameWorldManager gameWorldManager, EncounterFactory encounterFactory) {
		this.game = game;
		this.assetManager = assetManager;
		this.saveService = saveManager;
		this.loadService = saveManager;
		this.gameWorldManager = gameWorldManager;
		this.encounterFactory = encounterFactory;
		temp = 0;
	}

	@Override  
	public AbstractScreen getScreen(ScreenEnum screenRequest) {
		switch(screenRequest){
			case SPLASH: 	return new SplashScreen(this, assetManager); 
			case MAIN_MENU: return new MainMenuScreen(this, assetManager, saveService, loadService); 
			case NEW_GAME:	
			case ENCOUNTER:
			case LOAD_GAME: return getGameScreen();
			case GAME_OVER: return new GameOverScreen(this);
			case OPTIONS: 	return new OptionScreen(this);
			case REPLAY: 	return new ReplayScreen(this);
			case EXIT: 		return new ExitScreen(this);
			default: return null;
		}
	}
	
	private AbstractScreen getGameScreen(){
		GameWorldManager.GameContext context = loadService.loadDataValue("Context", GameWorldManager.GameContext.class);
		gameWorldManager.setContext(context);		
		switch (context){
			case ENCOUNTER: return new EncounterScreen(this, assetManager, saveService, encounterFactory.getEncounter(temp++), GameWorldManager.getGameWorld((String) loadService.loadDataValue("Class", String.class)));
			case WORLD_MAP: return new GameScreen(this, assetManager, saveService, null);
			default: return null;
		}
		
	}
	
	@Override
	public Game getGame() {
		return game;
	}
	
	private class ExitScreen extends AbstractScreen{
		protected ExitScreen(ScreenFactory factory) {
			super(factory);
		}
		@Override
		public void buildStage() {
			Gdx.app.exit();
		}
	}
}
package com.majalis.traprpg;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.utils.ObjectMap;
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
	private boolean loading;
	private int temp;
	
	public ScreenFactoryImpl(Game game, AssetManager assetManager, SaveManager saveManager, GameWorldManager gameWorldManager, EncounterFactory encounterFactory) {
		this.game = game;
		this.assetManager = assetManager;
		this.saveService = saveManager;
		this.loadService = saveManager;
		this.gameWorldManager = gameWorldManager;
		this.encounterFactory = encounterFactory;
		loading = true;
		temp = 0;		
	}

	@Override  
	public AbstractScreen getScreen(ScreenEnum screenRequest) {
		// this needs to be moved
		GameWorldManager.GameContext context = loadService.loadDataValue("Context", GameWorldManager.GameContext.class);
		gameWorldManager.setContext(context);	
		AbstractScreen tempScreen;
		switch(screenRequest){
			case SPLASH: 
				return new SplashScreen(this, assetManager, 100);
			case MAIN_MENU: 
				if (getAssetCheck(MainMenuScreen.resourceRequirements)){
					return new MainMenuScreen(this, assetManager, saveService, loadService); 
				}
				break;
			case NEW_GAME:	
			case ENCOUNTER: 
				tempScreen = getEncounter();
				if (tempScreen != null) return tempScreen;
				break; 
			case LOAD_GAME: 
				tempScreen = getGameScreen();
				if (tempScreen != null) return tempScreen;
				break;
			case GAME_OVER:				
				if (getAssetCheck(GameOverScreen.resourceRequirements)){
					return new GameOverScreen(this, assetManager);
				}
				break;
			case OPTIONS: 	return new OptionScreen(this);
			case REPLAY: 	return new ReplayScreen(this);
			case EXIT: 		return new ExitScreen(this);
		}
		loading = true;
		return new LoadScreen(this, assetManager, screenRequest);
	}

	private boolean getAssetCheck(ObjectMap<String, Class<?>> pathToType){
		// if the loading screen has just loaded the assets, don't perform the checks or increment the reference counts
		if (loading){
			loading = false;
			return true;
		}
		// if screens are being switched but no assets need to be loaded, don't call the loading screen
		boolean assetsLoaded = true;
		for (String path: pathToType.keys()){
			if (!assetManager.isLoaded(path)){
				assetsLoaded = false;
			}
			assetManager.load(path, pathToType.get(path));
		}
		return assetsLoaded;
	}
	
	private AbstractScreen getEncounter(){
		if (getAssetCheck(EncounterScreen.resourceRequirements)){
			return new EncounterScreen(this, assetManager, saveService, encounterFactory.getEncounter(temp++), GameWorldManager.getGameWorld((String) loadService.loadDataValue("Class", String.class)));
		}
		else {
			return null;
		}
		
	}
	
	private AbstractScreen getGameScreen(){
		switch (gameWorldManager.getGameContext()){
			case ENCOUNTER: return getEncounter();
			case WORLD_MAP: 
				if (getAssetCheck(GameScreen.resourceRequirements)){
					return new GameScreen(this, assetManager, saveService, null);
				}
				else return null;
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
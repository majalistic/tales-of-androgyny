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
	
	public ScreenFactoryImpl(Game game, AssetManager assetManager, SaveManager saveManager) {
		this.game = game;
		this.assetManager = assetManager;
		this.saveService = saveManager;
		this.loadService = saveManager;
	}

	@Override  
	public AbstractScreen getScreen(ScreenEnum screenRequest) {
		switch(screenRequest){
			case SPLASH: 	return new SplashScreen(this, assetManager); 
			case MAIN_MENU: return new MainMenuScreen(this, assetManager, saveService, loadService); 
			case GAME: 		return new GameScreen(this, assetManager, saveService, (String) loadService.loadDataValue("Class", String.class));
			case GAME_OVER: return new GameOverScreen(this);
			case OPTIONS: 	return new OptionScreen(this);
			case REPLAY: 	return new ReplayScreen(this);
			case EXIT: 		return new ExitScreen(this);
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
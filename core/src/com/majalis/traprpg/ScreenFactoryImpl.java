package com.majalis.traprpg;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;

public class ScreenFactoryImpl implements ScreenFactory {

	private final Game game;
	private final AssetManager assetManager;
	private final SaveManager saveManager;
	
	public ScreenFactoryImpl(Game game, AssetManager assetManager, SaveManager saveManager) {
		this.game = game;
		this.assetManager = assetManager;
		this.saveManager = saveManager;
	}

	@Override
	public AbstractScreen getScreen(ScreenEnum screenRequest, ScreenService service) {
		switch(screenRequest){
			case MAIN_MENU: return new MainMenuScreen(game, service, assetManager, saveManager); 
			case GAME: return new GameScreen(game, service, (String) saveManager.loadDataValue("Class", String.class));
			case GAME_OVER: return new GameOverScreen(game, service);
			case OPTIONS: return new OptionScreen(game, service);
			case REPLAY: return new ReplayScreen(game, service);
			case EXIT: return new ExitScreen(game, service);
			default: return null;
		}
	}
	
	private class ExitScreen extends AbstractScreen{

		protected ExitScreen(Game game, ScreenService service) {
			super(game, service);
		}

		@Override
		public void buildStage() {
			// TODO Auto-generated method stub
			Gdx.app.exit();
		}
		
	}

}

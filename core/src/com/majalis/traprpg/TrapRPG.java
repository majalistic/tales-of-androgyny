package com.majalis.traprpg;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;

public class TrapRPG extends Game {
	
	private class TempScreen extends AbstractScreen{
		protected TempScreen(Game game){
			super(game, null);
		}
		@Override
		protected void buildStage() {}
	}
	public void create() {
		// this will create a temporary screen and then switch to the actual main menu screen with all dependencies injected
        new TempScreen(this).switchScreen(ScreenEnum.MAIN_MENU, new AssetManager());
	}
}
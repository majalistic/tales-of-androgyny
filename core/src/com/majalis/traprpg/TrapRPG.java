package com.majalis.traprpg;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;

public class TrapRPG extends Game {
	
	public void create() {	
		init(new ScreenFactoryImpl(this, new AssetManager(), new SaveManager(false)));
	}
	
	public void init(ScreenFactory factory){
		// this is to define access to the factory methods in DependencyContainer
		ScreenService screenService = new ScreenService(factory);
		AbstractScreen screen = factory.getScreen(ScreenEnum.MAIN_MENU, screenService);
		screen.buildStage();
		setScreen(screen);
	}
	
}
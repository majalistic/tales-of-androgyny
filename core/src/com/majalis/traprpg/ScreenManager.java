package com.majalis.traprpg;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;

// Singleton class for screen management and delegation
public class ScreenManager {
	 
    private static ScreenManager instance;
 
    // Reference to game
    private Game game;
 
    private ScreenManager() {
        super();
    }
 
    public static ScreenManager getInstance() {
        if (instance == null) {
            instance = new ScreenManager();
        }
        return instance;
    }
 
    public ScreenManager initialize(Game game) {
        this.game = game;
        return this;
    }

    public void showScreen(ScreenEnum screenEnum, Object... params) {
 
        // Get current screen to dispose it
        Screen currentScreen = game.getScreen();
 
        // Show new screen
        AbstractScreen newScreen = screenEnum.getScreen(params);
        newScreen.buildStage();
        game.setScreen(newScreen);
 
        // Dispose previous screen
        if (currentScreen != null) {
            currentScreen.dispose();
        }
    }
}
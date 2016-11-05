package com.majalis.traprpg;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.RandomXS128;
import com.majalis.battle.BattleFactory;
import com.majalis.encounter.EncounterFactory;
import com.majalis.encounter.EncounterReader;
import com.majalis.save.SaveManager;
import com.majalis.screens.AbstractScreen;
import com.majalis.screens.ScreenEnum;
import com.majalis.screens.ScreenFactory;
import com.majalis.screens.ScreenFactoryImpl;
import com.majalis.world.GameWorldFactory;
/*
 * Package shared entry point for each platform.  Generates a ScreenFactory and service for dependency injection, and switches to the splash screen for loading.
 */
public class TrapRPG extends Game {
	public void create() {	
		
		Preferences prefs = Gdx.app.getPreferences("tales-of-androgyny-preferences");
		if (prefs.getBoolean("fullScreen", false)){
			Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
		}
		else {
			Gdx.graphics.setWindowedMode(prefs.getInteger("width", 1920), prefs.getInteger("height", 1080));	
		}
		
		SaveManager saveManager = new SaveManager(false, "data/save.json", "data/profile.json");
		EncounterReader encounterReader = new EncounterReader("script/encounters.json");
		AssetManager assetManager = new AssetManager();
        FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(Gdx.files.classpath("solstice.ttf"));
		RandomXS128 random = new RandomXS128();
		init(new ScreenFactoryImpl(this, assetManager, saveManager, new GameWorldFactory(saveManager, assetManager, fontGenerator, random), new EncounterFactory(encounterReader, assetManager, saveManager), new BattleFactory(saveManager, assetManager, fontGenerator), new SpriteBatch(), fontGenerator));
	}
	/*
	 * Takes a factory implementation and uses it to generate a screen and switch to it
	 */
	public void init(ScreenFactory factory){
		AbstractScreen screen = factory.getScreen(ScreenEnum.SPLASH);
		screen.buildStage();
		setScreen(screen);
	}
}
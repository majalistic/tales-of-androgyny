package com.majalis.traprpg;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
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
		SaveManager saveManager = new SaveManager(false, "data/save.json");
		EncounterReader encounterReader = new EncounterReader("script/encounters.json");
		AssetManager assetManager = new AssetManager();
		BitmapFont font = new BitmapFont(Gdx.files.internal("fonts/Solstice18/solstice.fnt"), Gdx.files.internal("fonts/Solstice18/solstice.png" ), false);
		ShapeRenderer shapeRenderer = new ShapeRenderer();
		RandomXS128 random = new RandomXS128();
		init(new ScreenFactoryImpl(this, assetManager, saveManager, new GameWorldFactory(saveManager, assetManager, shapeRenderer, font, random), new EncounterFactory(encounterReader, assetManager, saveManager), new BattleFactory(saveManager, assetManager, font), new SpriteBatch()));
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
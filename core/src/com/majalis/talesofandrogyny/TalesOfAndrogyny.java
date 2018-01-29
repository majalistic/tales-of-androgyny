package com.majalis.talesofandrogyny;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.majalis.asset.AnimatedActor;
import com.majalis.asset.AnimatedActorFactory;
import com.majalis.asset.AnimatedActorLoader;
import com.majalis.asset.SafeAssetManager;
import com.majalis.battle.BattleFactory;
import com.majalis.encounter.EncounterCode;
import com.majalis.encounter.EncounterFactory;
import com.majalis.encounter.EncounterReader;
import com.majalis.encounter.EncounterReaderImpl;
import com.majalis.save.SaveManager;
import com.majalis.screens.AbstractScreen;
import com.majalis.screens.ScreenEnum;
import com.majalis.screens.ScreenFactory;
import com.majalis.screens.ScreenFactoryImpl;
import com.majalis.world.GameWorldFactory;
import static com.majalis.encounter.EncounterCode.*;
/*
 * Package shared entry point for each platform.  Generates a ScreenFactory and service for dependency injection, and switches to the splash screen for loading.
 */
@SuppressWarnings("unused")
public class TalesOfAndrogyny extends Game {
	public static boolean patron = true;
	public static String getVersion() { return "Version: 0.1.27.5" + (patron ? " Patron-Only" : ""); }
	public static Array<EncounterCode> setEncounter = new Array<EncounterCode>(new EncounterCode[]{});
	public static boolean testing = false;
	public static int defaultScreenWidth = 1280;
	public static int defaultScreenHeight = 720;
	
	public void create() {	
		Preferences prefs = Gdx.app.getPreferences("tales-of-androgyny-preferences");
		if (prefs.getBoolean("fullScreen", false)) {
			Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
		}
		else {
			Gdx.graphics.setWindowedMode(prefs.getInteger("width", defaultScreenWidth), prefs.getInteger("height", defaultScreenHeight));
		}
		
		SaveManager saveManager = new SaveManager(false, ".toa-data/save.json", ".toa-data/profile.json");
		ObjectMap<String, EncounterReader> encounterReaders = new ObjectMap<String, EncounterReader>();
		for (EncounterCode encounter : EncounterCode.values()) {
			String path = encounter.getScriptPath();
			EncounterReader reader = encounterReaders.get(path);
			if (reader == null) reader = new EncounterReaderImpl(path);
			encounterReaders.put(path, reader);
		}
		AssetManager assetManager = new SafeAssetManager();
		FileHandleResolver resolver = assetManager.getFileHandleResolver();
		assetManager.setLoader(AnimatedActorFactory.class, new AnimatedActorLoader(resolver));
        FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("ui/ArgosGeorge.ttf"));
		RandomXS128 random = new RandomXS128();
		PolygonSpriteBatch batch = new PolygonSpriteBatch(2500) {
            final int MAX = 2500 * 5;
            @Override
            public void draw (Texture texture, float[] spriteVertices, int offset, int count) {
                while(count > MAX) {
                    super.draw(texture, spriteVertices, offset, MAX);
                    offset += MAX;
                    count -= MAX;
                }
                super.draw(texture, spriteVertices, offset, count);
            }
        };
		init(new ScreenFactoryImpl(this, assetManager, saveManager, new GameWorldFactory(saveManager, assetManager, random), new EncounterFactory(encounterReaders, assetManager, saveManager), new BattleFactory(saveManager, assetManager), batch, fontGenerator));
	}
	/*
	 * Takes a factory implementation and uses it to generate a screen and switch to it
	 */
	public void init(ScreenFactory factory) {
		AbstractScreen screen = factory.getScreen(ScreenEnum.SPLASH);
		screen.buildStage();
		setScreen(screen);
	}
	
}
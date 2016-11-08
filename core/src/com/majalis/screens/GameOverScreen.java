package com.majalis.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ObjectMap;
import com.majalis.asset.AssetEnum;
import com.majalis.encounter.Background;
/*
 * Screen for displaying "Game Over" - can return the player to the main menu or offer them the ability to save their GO encounter.  May be loaded with different splashes / music at runtime.
 */
public class GameOverScreen extends AbstractScreen {

	public static final ObjectMap<String, Class<?>> resourceRequirements = new ObjectMap<String, Class<?>>();
	static {
		resourceRequirements.put(AssetEnum.GAME_OVER_GAPE.getPath(), Texture.class);
		resourceRequirements.put(AssetEnum.GAME_OVER_TUCKERED.getPath(), Texture.class);
	}
	private final AssetManager assetManager;
	private final Texture backgroundImage;
	private int clocktick;
	
	public GameOverScreen(ScreenFactory factory, ScreenElements elements,  AssetManager assetManager) {
		super(factory, elements);
		this.assetManager = assetManager;
		this.backgroundImage = Math.random() > .5 ? assetManager.get(AssetEnum.GAME_OVER_GAPE.getPath(), Texture.class) : assetManager.get(AssetEnum.GAME_OVER_TUCKERED.getPath(), Texture.class);
		clocktick = 0;
	}

	@Override
	public void buildStage() {
		this.addActor(new Background(backgroundImage, 934, 720));
	}
	
	@Override
	public void render(float delta) {
		super.render(delta);
		OrthographicCamera camera = (OrthographicCamera) getCamera();
        batch.setTransformMatrix(camera.view);
		batch.setProjectionMatrix(camera.combined);
		camera.update();
		batch.begin();
		font.draw(batch, "Press Enter", 700, 400);
		font.draw(batch, String.valueOf(clocktick++), 1820, 400);
		batch.end();
		if (Gdx.input.isKeyJustPressed(Keys.ENTER) || clocktick >= 500)
			showScreen(ScreenEnum.MAIN_MENU);
	}
	
	@Override
	public void show() {
		super.show();
	}	
	
	@Override
	public void dispose() {
		for(String path: resourceRequirements.keys()){
			assetManager.unload(path);
		}
	}
}
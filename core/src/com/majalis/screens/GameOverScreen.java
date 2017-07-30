package com.majalis.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.majalis.asset.AnimatedActor;
import com.majalis.asset.AssetEnum;
/*
 * Screen for displaying "Game Over" - can return the player to the main menu or offer them the ability to save their GO encounter.  May be loaded with different splashes / music at runtime.
 */
public class GameOverScreen extends AbstractScreen {

	public static final Array<AssetDescriptor<?>> resourceRequirements = new Array<AssetDescriptor<?>>();
	static {
		resourceRequirements.add(AssetEnum.UI_SKIN.getSkin());
		resourceRequirements.add(AssetEnum.GAME_OVER_ANIMATION.getAnimation());
	}
	private final AssetManager assetManager;
	
	public GameOverScreen(ScreenFactory factory, ScreenElements elements,  AssetManager assetManager) {
		super(factory, elements);
		this.assetManager = assetManager;
		setClearColor(Color.SLATE.r, Color.SLATE.g, Color.SLATE.b, 1);
	}

	@Override
	public void buildStage() {
		AnimatedActor background = assetManager.get(AssetEnum.GAME_OVER_ANIMATION.getAnimation()).getInstance();
		background.setSkeletonPosition(555, 520);
		background.setSize(2000, 2000);
		background.addListener(new ClickListener() {
			@Override
	        public void clicked(InputEvent event, float x, float y) {
				showScreen(ScreenEnum.MAIN_MENU);
	        }
		});
		this.addActor(background);
		
		Skin skin = assetManager.get(AssetEnum.UI_SKIN.getSkin());
		Label gameOver = new Label("GAME OVER - Press Enter", skin);
		gameOver.setColor(Color.BLACK);
		gameOver.setPosition(100, 50);
		this.addActor(gameOver);		
	}
	
	@Override
	public void render(float delta) {
		super.render(delta);
		if (Gdx.input.isKeyJustPressed(Keys.ENTER) || Gdx.input.isKeyJustPressed(Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Keys.SPACE)) {
			showScreen(ScreenEnum.MAIN_MENU);
		}
	}
	
	@Override
	public void show() {
		super.show();
	}	
	
	@Override
	public void dispose() {
		for(AssetDescriptor<?> path: resourceRequirements) {
			assetManager.unload(path.fileName);
		}
	}
}
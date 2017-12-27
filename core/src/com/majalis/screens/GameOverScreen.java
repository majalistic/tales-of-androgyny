package com.majalis.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.majalis.asset.AnimatedActor;
import com.majalis.asset.AssetEnum;
import com.majalis.save.SaveService;
/*
 * Screen for displaying "Game Over" - can return the player to the main menu or offer them the ability to save their GO encounter.  May be loaded with different splashes / music at runtime.
 */
public class GameOverScreen extends AbstractScreen {

	public static final Array<AssetDescriptor<?>> resourceRequirements = new Array<AssetDescriptor<?>>();
	static {
		resourceRequirements.add(AssetEnum.UI_SKIN.getSkin());
		resourceRequirements.add(AssetEnum.GAME_OVER_ANIMATION.getAnimation());
		resourceRequirements.add(AssetEnum.CUM.getSound());
		resourceRequirements.add(AssetEnum.GAME_OVER_MUSIC.getMusic());
	}
	private final SaveService saveService;
	private final Sound sound;
	
	public GameOverScreen(ScreenFactory factory, ScreenElements elements, SaveService saveService) {
		super(factory, elements, AssetEnum.GAME_OVER_MUSIC);
		this.saveService = saveService;
		sound = assetManager.get(AssetEnum.CUM.getSound());
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
				leaveScreen();
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
			leaveScreen();
		}
	}
	
	private void leaveScreen(){
		saveService.newSave();
		showScreen(ScreenEnum.MAIN_MENU);
	}
	
	@Override
	public void show() {
		super.show();
		sound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.7f);
	}	
	
	@Override
	public void dispose() {
		for(AssetDescriptor<?> path: resourceRequirements) {
			if (path.type == Music.class) continue;
			assetManager.unload(path.fileName);
		}
	}
	
}
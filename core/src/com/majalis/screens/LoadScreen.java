package com.majalis.screens;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.majalis.asset.AssetEnum;
/*
 * Screen that displays while a new screen is loading.
 */
public class LoadScreen extends AbstractScreen {
	private final BitmapFont largeFont;
	private final Image loadingImage;
	private final ScreenEnum screenRequest;
	
	public LoadScreen(ScreenFactory factory, ScreenElements elements, ScreenEnum screenRequest) {
		super(factory, elements, null);
		this.loadingImage = new Image(assetManager.get(AssetEnum.LOADING.getTexture()));
		this.largeFont = fontFactory.getFont(72);
		this.screenRequest = screenRequest;
	}

	@Override
	public void buildStage() {
		LabelStyle style = new LabelStyle();
		style.font = largeFont;
		Label loading = new Label("Loading...", style);
		loading.setPosition(1500, 50);
		loading.addAction(Actions.forever(Actions.sequence(Actions.color(Color.GRAY, 1), Actions.color(Color.WHITE, 1))));
		this.addActor(loading);
		this.addActor(loadingImage);
		loadingImage.setPosition(1350, 50);
		loadingImage.setScale(.25f);
	}
	@Override
	protected void switchFade(ScreenEnum screenRequest, AbstractScreen currentScreen, AssetEnum oldMusicPath, Music oldMusic) { switchScreen(screenRequest, currentScreen, oldMusicPath, oldMusic); }
	
	@Override
	public void render(float delta) {
		super.render(delta);
		if (assetManager.update(100)) {
			showScreen(screenRequest);
		}		
	}
	
	@Override
	public void dispose() { super.dispose(); largeFont.dispose(); }
}
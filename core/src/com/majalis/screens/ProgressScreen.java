package com.majalis.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.majalis.asset.AssetEnum;

public class ProgressScreen extends AbstractScreen {


	public static final Array<AssetDescriptor<?>> resourceRequirements = new Array<AssetDescriptor<?>>();
	static {
		resourceRequirements.add(AssetEnum.UI_SKIN.getSkin());
		resourceRequirements.add(AssetEnum.BUTTON_SOUND.getSound());
		resourceRequirements.add(AssetEnum.MAIN_MENU_MUSIC.getMusic());
		resourceRequirements.add(AssetEnum.CAMP_BG0.getTexture());
		resourceRequirements.add(AssetEnum.CAMP_BG1.getTexture());
		resourceRequirements.add(AssetEnum.CAMP_BG2.getTexture());
		resourceRequirements.addAll(MainMenuScreen.resourceRequirements);
	}
	private final Skin skin;
	
	public ProgressScreen(ScreenFactory factory, ScreenElements elements) {
		super(factory, elements, null);
		this.addActor(getCampBackground());
		skin = assetManager.get(AssetEnum.UI_SKIN.getSkin());
		final Sound sound = assetManager.get(AssetEnum.BUTTON_SOUND.getSound());
		
		//addLabelActor("- Gameplay Options -", 860, 950);
		
		//addActorAndListen(makeup, 325, 825);
		
		final TextButton done = new TextButton("Done", skin);
		
		done.addListener(
			new ClickListener() {
				@Override
		        public void clicked(InputEvent event, float x, float y) {
					if (!debug) {
						sound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
						saveAndExit();	 
					}
		        }
			}
		);
		addActorAndListen(done, 1500, 100);
	}
	
	@Override
	public void buildStage() {}
	
	@Override
	public void render(float delta) {
		super.render(delta);
		if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
			saveAndExit();
		}
		if(Gdx.input.isKeyJustPressed(Keys.SHIFT_LEFT)) {
			debug = !debug;
		}
	}
	
	private void saveAndExit() { showScreen(ScreenEnum.MAIN_MENU); }
	
	private Label addLabelActor(String label, int x, int y) {
		Label newLabel = new Label(label, skin);
		newLabel.setColor(Color.WHITE);
		addActorAndListen(newLabel, x, y);
		return newLabel;
	}
	
	@Override
	public void show() {
		super.show();
	    getRoot().getColor().a = 0;
	    getRoot().addAction(Actions.fadeIn(0.5f));
	}
}

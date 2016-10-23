package com.majalis.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ObjectMap;
import com.majalis.asset.AssetEnum;
/*
 * The options/configuration screen.  UI that handles player input to save Preferences to a player's file system.
 */
public class OptionScreen extends AbstractScreen {
	
	public static final ObjectMap<String, Class<?>> resourceRequirements = new ObjectMap<String, Class<?>>();
	static {
		resourceRequirements.put(AssetEnum.UI_SKIN.getPath(), Skin.class);
		resourceRequirements.put(AssetEnum.BUTTON_SOUND.getPath(), Sound.class);
	}
	
	private final Preferences preferences;
	public OptionScreen(ScreenFactory factory, ScreenElements elements, AssetManager assetManager) {
		super(factory, elements);
		Skin skin = assetManager.get(AssetEnum.UI_SKIN.getPath(), Skin.class);
		final Slider slider = new Slider(0, 1, .1f, false, skin);
		preferences  = Gdx.app.getPreferences("trap-rpg-preferences");
		
		slider.setValue(preferences.getFloat("volume", 1));
		slider.addListener(new ChangeListener() {
	        @Override
	        public void changed(ChangeEvent event, Actor actor) {
	            final float val = slider.getValue();
	            preferences.putFloat("volume", val);
	        }
	    });
		slider.addAction(Actions.moveTo(500, 400));
		this.addActor(slider);
	}

	@Override
	public void render(float delta) {
		super.render(delta);
		OrthographicCamera camera = (OrthographicCamera) getCamera();
		batch.setTransformMatrix(camera.view);
		camera.update();
		batch.begin();
		font.setColor(Color.WHITE);
		font.draw(batch, String.valueOf((int)(preferences.getFloat("volume", 1) * 100)) +"%", 1200, 850);
		batch.end();
		
		if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)){
			preferences.flush();
			showScreen(ScreenEnum.MAIN_MENU);
		}
	}

	@Override
	public void buildStage() {
		// TODO Auto-generated method stub	
	}
}
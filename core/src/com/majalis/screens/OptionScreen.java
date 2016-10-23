package com.majalis.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
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
		resourceRequirements.put(AssetEnum.MAIN_MENU_MUSIC.getPath(), Music.class);
	}
	
	private final Preferences preferences;
	private final Music music;
	public OptionScreen(ScreenFactory factory, ScreenElements elements, AssetManager assetManager) {
		super(factory, elements);
		Skin skin = assetManager.get(AssetEnum.UI_SKIN.getPath(), Skin.class);
		this.music = assetManager.get(AssetEnum.MAIN_MENU_MUSIC.getPath(), Music.class);
		final Sound sound = assetManager.get(AssetEnum.BUTTON_SOUND.getPath(), Sound.class);
		final Slider slider = new Slider(0, 1, .1f, false, skin);
		preferences  = Gdx.app.getPreferences("trap-rpg-preferences");
		
		slider.setValue(preferences.getFloat("volume", 1));
		slider.addListener(new ChangeListener() {
	        @Override
	        public void changed(ChangeEvent event, Actor actor) {
	            final float val = slider.getValue();
	            preferences.putFloat("volume", val);
	            sound.play(val *.5f);
	        }
	    });
		slider.addAction(Actions.moveTo(500, 400));
		this.addActor(slider);
		
		final Slider musicSlider = new Slider(0, 1, .1f, false, skin);
		
		musicSlider.setValue(preferences.getFloat("musicVolume", 1));
		musicSlider.addListener(new ChangeListener() {
	        @Override
	        public void changed(ChangeEvent event, Actor actor) {
	            final float val = musicSlider.getValue();
	            preferences.putFloat("musicVolume", val);
	            music.setVolume(val);
	        }
	    });
		musicSlider.addAction(Actions.moveTo(500, 300));
		this.addActor(musicSlider);
		
		
		final TextButton done = new TextButton("Done", skin);
		
		done.setWidth(180);
		done.setHeight(40);
		done.addListener(
			new ClickListener(){
				@Override
		        public void clicked(InputEvent event, float x, float y) {
					sound.play(preferences.getFloat("volume") *.5f);
					saveAndExit();	   
		        }
			}
		);
		done.addAction(Actions.moveTo(done.getX() + 1015, done.getY() + 20));
		this.addActor(done);
	}
	
	private void saveAndExit(){
		preferences.flush();
		showScreen(ScreenEnum.MAIN_MENU);
	}

	@Override
	public void render(float delta) {
		super.render(delta);
		OrthographicCamera camera = (OrthographicCamera) getCamera();
		batch.setTransformMatrix(camera.view);
		camera.update();
		batch.begin();
		font.setColor(Color.WHITE);
		font.draw(batch, "Sound volume:", 1170, 865);
		font.draw(batch, "Music volume:", 1170, 760);
		font.draw(batch, String.valueOf((int)(preferences.getFloat("volume", 1) * 100)) +"%", 1200, 850);
		font.draw(batch, String.valueOf((int)(preferences.getFloat("musicVolume", 1) * 100)) +"%", 1200, 745);
		batch.end();
		
		if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)){
			saveAndExit();
		}
	}

	@Override
	public void buildStage() {
		music.play();
	}
	
	@Override
	public void show() {
		super.show();
	    getRoot().getColor().a = 0;
	    getRoot().addAction(Actions.fadeIn(0.5f));
	}
}
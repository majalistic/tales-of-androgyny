package com.majalis.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ObjectMap;
import com.majalis.asset.AssetEnum;

public class CreditsScreen extends AbstractScreen{

	public static final ObjectMap<String, Class<?>> resourceRequirements = new ObjectMap<String, Class<?>>();
	static {
		resourceRequirements.put("uiskin.json", Skin.class);
		resourceRequirements.put("sound.wav", Sound.class);
		resourceRequirements.put(AssetEnum.MAIN_MENU_MUSIC.getPath(), Music.class);
	}
	private final String credits;
	protected CreditsScreen(ScreenFactory screenFactory, ScreenElements elements, AssetManager assetManager) {
		super(screenFactory, elements);
		Skin skin = assetManager.get(AssetEnum.UI_SKIN.getPath(), Skin.class);
		final Sound sound = assetManager.get(AssetEnum.BUTTON_SOUND.getPath(), Sound.class);
		final TextButton done = new TextButton("Done", skin);
		
		done.setWidth(180);
		done.setHeight(40);
		done.addListener(
			new ClickListener(){
				@Override
		        public void clicked(InputEvent event, float x, float y) {
					sound.play(Gdx.app.getPreferences("trap-rpg-preferences").getFloat("volume") *.5f);
					exitScreen();	   
		        }
			}
		);
		done.addAction(Actions.moveTo(done.getX() + 1015, done.getY() + 20));
		this.addActor(done);
		
		credits = "\"Danse Macabre - Sad Part - no violin\", \"Kings of Tara\""
				+ "\nKevin MacLeod (incompetech.com)"
				+ "\nLicensed under Creative Commons: By Attribution 3.0"
				+ "\nhttp://creativecommons.org/licenses/by/3.0/";
	}

	@Override
	public void buildStage() {
		
	}
	
	@Override
	public void render(float delta) {
		super.render(delta);
		OrthographicCamera camera = (OrthographicCamera) getCamera();
		batch.setTransformMatrix(camera.view);
		camera.update();
		batch.begin();
		font.setColor(Color.WHITE);
		font.draw(batch, credits, 770, 1000);
		batch.end();
		
		if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)){
			exitScreen();
		}
	}
	
	private void exitScreen(){
		showScreen(ScreenEnum.MAIN_MENU);
	}

	
	@Override
	public void show() {
		super.show();
	    getRoot().getColor().a = 0;
	    getRoot().addAction(Actions.fadeIn(0.5f));
	}
	
}

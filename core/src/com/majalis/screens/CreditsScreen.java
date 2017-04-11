package com.majalis.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.majalis.asset.AssetEnum;
import com.majalis.encounter.Background.BackgroundBuilder;;

public class CreditsScreen extends AbstractScreen{
	public static final Array<AssetDescriptor<?>> resourceRequirements = new Array<AssetDescriptor<?>>();
	static {
		resourceRequirements.add(AssetEnum.UI_SKIN.getSkin());
		resourceRequirements.add(AssetEnum.BUTTON_SOUND.getSound());
		resourceRequirements.add(AssetEnum.MAIN_MENU_MUSIC.getMusic());
		resourceRequirements.add(AssetEnum.DEFAULT_BACKGROUND.getTexture());
	}
	
	private final String credits;
	protected CreditsScreen(ScreenFactory screenFactory, ScreenElements elements, AssetManager assetManager) {
		super(screenFactory, elements);
		this.addActor(new BackgroundBuilder(assetManager.get(AssetEnum.DEFAULT_BACKGROUND.getTexture())).build());
		Skin skin = assetManager.get(AssetEnum.UI_SKIN.getSkin());
		final Sound sound = assetManager.get(AssetEnum.BUTTON_SOUND.getSound());
		final TextButton done = new TextButton("Done", skin);
		
		done.addListener(
			new ClickListener() {
				@Override
		        public void clicked(InputEvent event, float x, float y) {
					sound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
					exitScreen();	   
		        }
			}
		);
		done.setPosition(1523, 120);
		this.addActor(done);
		
		credits = "\"Broken Reality\", \"Perspectives\", \"Floating Cities\", \"Kings of Tara\", \"Enchanted Valley\", \"Brittle Rille\", \"For Originz\", \"Phantom from Space\",\n\"Mechanolith,\", \"Fearless First\", \"Danger Storm\", \"Killers\""
				+ "\nKevin MacLeod (incompetech.com)"
				+ "\nLicensed under Creative Commons: By Attribution 3.0"
				+ "\nhttp://creativecommons.org/licenses/by/3.0/"
				+ "\n\nSuccubus-Tier patrons that helped make this happen:"
				+ "\nDarksideX, Ace, crufl, JennaTran, Joel Fields, Laersect"
				+ "\nMucho kudos to official contributor Dern for programming, debugging and moral support <3"
				+ "\n\nCopy-editing and play-testing by T3mp3st, Legion, Fattycakes, and anonymous others"
				;
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
		font.setColor(Color.BLACK);
		font.draw(batch, credits, 1100, 1300);
		batch.end();
		
		if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
			exitScreen();
		}
	}
	
	private void exitScreen() {
		showScreen(ScreenEnum.MAIN_MENU);
	}

	
	@Override
	public void show() {
		super.show();
	    getRoot().getColor().a = 0;
	    getRoot().addAction(Actions.fadeIn(0.5f));
	}
	
}

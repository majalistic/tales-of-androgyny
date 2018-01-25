package com.majalis.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.majalis.asset.AssetEnum;

public class HelpScreen extends AbstractScreen{
	public static final Array<AssetDescriptor<?>> resourceRequirements = new Array<AssetDescriptor<?>>();
	static {
		resourceRequirements.add(AssetEnum.UI_SKIN.getSkin());
		resourceRequirements.add(AssetEnum.BUTTON_SOUND.getSound());
		resourceRequirements.add(AssetEnum.MAIN_MENU_MUSIC.getMusic());
		resourceRequirements.add(AssetEnum.CAMP_BG0.getTexture());
		resourceRequirements.add(AssetEnum.CAMP_BG1.getTexture());
		resourceRequirements.add(AssetEnum.CAMP_BG2.getTexture());
	}
	
	private int display = 0;
	
	protected HelpScreen(ScreenFactory screenFactory, ScreenElements elements, AssetManager assetManager) {
		super(screenFactory, elements, null);
		this.addActor(getCampBackground());
		final Array<Label> slides = new Array<Label>();
		Skin skin = assetManager.get(AssetEnum.UI_SKIN.getSkin());
		final Sound sound = assetManager.get(AssetEnum.BUTTON_SOUND.getSound());

		slides.add(new Label("Your character and the enemy are not always at their best - various afflictions temporarily lower their attributes, making them less effective.", skin));
		slides.add(new Label("An attack that does 0 damage is not without its use, if it inflicts damage to the opponent's armor/shield or destabilizes them, or if it changes the character's stance to a more advantageous one.", skin));
		slides.add(new Label("Your stance is the primary determinant in what techniques you can use at a given time. Using a technique will often change your stance. Pay close attention to your stance, the enemy's stance, and what stance a given technique will leave you in if it's successful.", skin));
		
		for (Label actor : slides) {
			actor.setPosition(100, 900);
			actor.setWidth(850);
			actor.setWrap(true);
			actor.setAlignment(Align.topLeft);
		}
		
		final Group imageGroup = new Group();
		this.addActor(imageGroup);
		
		final Group uiGroup = new Group();
		this.addActor(uiGroup);
		
		imageGroup.addActor(slides.get(0));
		
		final TextButton back = new TextButton("Back", skin);
		
		back.addListener(
			new ClickListener() {
				@Override
		        public void clicked(InputEvent event, float x, float y) {
					sound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
					imageGroup.clear();
					display--;
					imageGroup.addActor(slides.get(display));
					if (display == 0) uiGroup.removeActor(back);
		        }
			}
		);
		
		back.setPosition(1553, 200);
		back.setWidth(100);
		
		final TextButton next = new TextButton("Next", skin);
		
		next.addListener(
			new ClickListener() {
				@Override
		        public void clicked(InputEvent event, float x, float y) {
					sound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
					if (display == 0) uiGroup.addActor(back);
					if (display < slides.size - 1) {
						imageGroup.clear();			
						display++;
						imageGroup.addActor(slides.get(display));
					}
					else {
						exitScreen();
					}
		        }
			}
		);
		
		next.setWidth(100);
		next.setPosition(1668, 200);
		this.addActor(next);
	
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
	}
	
	@Override
	public void buildStage() {
		
	}
	
	@Override
	public void render(float delta) {
		super.render(delta);
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

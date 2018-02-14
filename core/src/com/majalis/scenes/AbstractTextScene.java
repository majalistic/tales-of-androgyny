package com.majalis.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.OrderedMap;
import com.majalis.asset.AssetEnum;
import com.majalis.character.PlayerCharacter;
import com.majalis.encounter.Background;
import com.majalis.encounter.EncounterHUD;
import com.majalis.save.SaveEnum;
import com.majalis.save.SaveService;

public abstract class AbstractTextScene extends Scene {
	protected final SaveService saveService;
	protected final Label display;
	protected final Table statusResults;
	protected final Skin skin;
	protected final Background background;
	private final Label skipText;
	private boolean isAutoplay;
	
	protected AbstractTextScene(OrderedMap<Integer, Scene> sceneBranches, int sceneCode, AssetManager assetManager, BitmapFont font, PlayerCharacter character, SaveService saveService, Background background, EncounterHUD hud) {
		super(sceneBranches, sceneCode, hud);
		this.saveService = saveService;
		this.background = background;
		this.addActor(background);
		skin = assetManager.get(AssetEnum.UI_SKIN.getSkin());
		display = addLabel("", skin, font, Color.BLACK, 430, 330);
		display.setWidth(1125);
		statusResults = new Table();
		statusResults.align(Align.topRight);
		statusResults.setPosition(1700, 985);
		this.addActor(statusResults);
		skipText = addLabel("Press CTRL to skip", skin, font, Color.BLACK, 95, 180);
		skipText.setWidth(240);
	}

	protected void showSkipText() { skipText.addAction(Actions.show()); }
	protected void hideSkipText() { skipText.addAction(Actions.hide()); }
	protected void toggleSkipText() { if(skipText.isVisible()) hideSkipText(); else showSkipText(); }
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		if (Gdx.input.isKeyJustPressed(Keys.ENTER) || Gdx.input.isKeyJustPressed(Keys.SPACE) || Gdx.input.isKeyPressed(Keys.CONTROL_LEFT) || Gdx.input.isKeyPressed(Keys.CONTROL_RIGHT)) {
			nextScene();
		}
		if (isActive) {
			if (hud.isSkipHeld()) nextScene();
			else {
				boolean autoplay = Gdx.app.getPreferences("tales-of-androgyny-preferences").getBoolean("autoplay", false);
				if (isAutoplay != autoplay) {
					isAutoplay = autoplay;
					if (!isAutoplay) {
						this.clearActions();
					}
					else {
						int textSpeed = Gdx.app.getPreferences("tales-of-androgyny-preferences").getInteger("autoplaySpeed", 5);
						int baseDisplayTime = display.getText().length / 20 + 2;
						float speedFactor = 1.5f - (textSpeed * .1f); 
						
						this.addAction(Actions.sequence(Actions.delay(baseDisplayTime * speedFactor), new Action(){
							@Override
							public boolean act(float delta) {
								clearActions();
								nextScene();
								return true;
							}}));
					}
				}
			}
		}		
	}
	
	@Override
	public void activate() {
		isActive = true;	
		this.removeAction(Actions.hide());
		this.addAction(Actions.visible(true));
		this.addAction(Actions.show());
		background.addListener(new ClickListener() { 
			@Override
	        public void clicked(InputEvent event, float x, float y) {
				nextScene();
			}
		});
		saveService.saveDataValue(SaveEnum.SCENE_CODE, sceneCode);
	}

	protected abstract void nextScene();
}

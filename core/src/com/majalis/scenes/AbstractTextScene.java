package com.majalis.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.OrderedMap;
import com.majalis.asset.AssetEnum;
import com.majalis.character.PlayerCharacter;
import com.majalis.encounter.Background;
import com.majalis.save.SaveEnum;
import com.majalis.save.SaveService;

public abstract class AbstractTextScene extends Scene {
	protected final SaveService saveService;
	protected final Label display;
	protected final Table statusResults;
	protected final Image characterPortrait;
	protected final Image masculinityIcon;
	protected final Image fullnessIcon;
	protected final Skin skin;
	protected final Background background;
	
	protected AbstractTextScene(OrderedMap<Integer, Scene> sceneBranches, int sceneCode, AssetManager assetManager, BitmapFont font, PlayerCharacter character, SaveService saveService, Background background) {
		super(sceneBranches, sceneCode);
		this.saveService = saveService;
		this.background = background;
		this.addActor(background);
		skin = assetManager.get(AssetEnum.UI_SKIN.getSkin());
		display = addLabel("", skin, font, Color.BLACK, 430, 330);
		display.setWidth(1125);
		statusResults = new Table();//addLabel("", skin, font, new Color(216/256f, 149/256f, 34/256f, 1), 1010, 985); 
		statusResults.align(Align.topRight);
		statusResults.setPosition(1700, 985);
		this.addActor(statusResults);
		Label skipText = addLabel("Press CTRL to skip", skin, font, Color.GRAY, 105, 180);
		skipText.setWidth(240);
		Texture portrait = assetManager.get(character.getPortraitPath());
		characterPortrait = addImage(portrait, null, 105, 800, portrait.getWidth() / (portrait.getHeight() / 200f), 200);
		characterPortrait.addAction(Actions.hide());
		Texture icon = assetManager.get(character.getMasculinityPath());
		masculinityIcon = addImage(icon, null, 105, 705, icon.getWidth() / (icon.getHeight() / 100f), 100);
		masculinityIcon.addAction(Actions.hide());
		Texture fullness = assetManager.get(character.getCumInflationPath());
		fullnessIcon = addImage(icon, null, 42, 755, fullness.getWidth() / (fullness.getHeight() / 100f), 100);
		fullnessIcon.addAction(Actions.hide());
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		if (Gdx.input.isKeyJustPressed(Keys.ENTER) || Gdx.input.isKeyJustPressed(Keys.SPACE) || Gdx.input.isKeyPressed(Keys.CONTROL_LEFT) || Gdx.input.isKeyPressed(Keys.CONTROL_RIGHT)) {
			nextScene();
		}
	}
	
	@Override
	public void setActive() {
		isActive = true;	
		this.removeAction(Actions.hide());
		this.addAction(Actions.visible(true));
		this.addAction(Actions.show());
		Image toClick = background.getDialogBox() != null ? background.getDialogBox() : background.getBackground();
		toClick.addListener(new ClickListener() { 
			@Override
	        public void clicked(InputEvent event, float x, float y) {
				nextScene();
			}
		});
		saveService.saveDataValue(SaveEnum.SCENE_CODE, sceneCode);
	}
	protected abstract void nextScene();
}

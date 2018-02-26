package com.majalis.character;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class HealthBar extends DisplayWidget {
	private final AssetManager assetManager;
	private final AbstractCharacter character;
	private final ProgressBar bar;
	private final Image icon;
	private final Label label;
	private final Label diffValueDisplay;
	private int value;
	public HealthBar(AbstractCharacter character, AssetManager assetManager, Skin skin) {
		this.character = character;
		this.assetManager = assetManager;
		bar = new ProgressBar(0, 1, .01f, false, skin);
		bar.setWidth(350);
		bar.setValue(character.getHealthPercent());
		this.addActor(bar);
		
		icon = new Image(assetManager.get(character.getHealthDisplay()));
		icon.setPosition(3, 7.5f);
		this.addActor(icon);
		
		label = new Label(character.getCurrentHealth() + " / " + character.getMaxHealth(), skin);
		label.setColor(Color.BROWN);
		label.setPosition(75, 8);
		this.addActor(label);
		bar.setColor(character.getHealthColor());
		bar.getStyle().knobBefore.setMinWidth(0); // this affects ALL progress bars with this skin, so shouldn't be done like this - REMOVE
		
		diffValueDisplay = new Label("", skin);
		diffValueDisplay.setPosition(getX() + 350, getY() + 25);
		this.addActor(diffValueDisplay);
		value = character.getCurrentHealth();
	}
	
	// this should be changed to act
	@Override
	public void act(float delta) {
		float characterHealthPercent = character.getHealthPercent();
		if(Math.abs(bar.getValue() - characterHealthPercent) > .01) {
			if (bar.getValue() < characterHealthPercent) {
				bar.setValue(bar.getValue() + .01f);
			}
			else {
				bar.setValue(bar.getValue() - .01f);
			}
		}
		icon.setDrawable(new TextureRegionDrawable(new TextureRegion(assetManager.get(character.getHealthDisplay()))));
		label.setText(character.getCurrentHealth() + " / " + character.getMaxHealth());
		bar.setColor(character.getHealthColor());
		if (value != character.getCurrentHealth()) {
			setDiffLabel(diffValueDisplay, character.getCurrentHealth() - value);
			value = character.getCurrentHealth();
		}
		super.act(delta);
	}
}
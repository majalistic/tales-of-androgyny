package com.majalis.character;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class ManaBar extends Group {
	private final AssetManager assetManager;
	private final AbstractCharacter character;
	private final ProgressBar bar;
	private final Image icon;
	private final Label label;
	public ManaBar(AbstractCharacter character, AssetManager assetManager, Skin skin) {
		this.character = character;
		this.assetManager = assetManager;
		bar = new ProgressBar(0, 1, .01f, false, skin);
		bar.setWidth(350);
		bar.setValue(character.getManaPercent());
		this.addActor(bar);
		
		icon = new Image(assetManager.get(character.getManaDisplay()));
		icon.setPosition(3, 7.5f);
		this.addActor(icon);
		
		label = new Label(character.getCurrentMana() + " / " + character.getMaxMana(), skin);
		label.setColor(Color.BROWN);
		label.setPosition(75, 8);
		this.addActor(label);
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		float characterManaPercent = character.getManaPercent();
		if(Math.abs(bar.getValue() - characterManaPercent) > .01) {
			if (bar.getValue() < characterManaPercent) {
				bar.setValue(bar.getValue() + .01f);
			}
			else {
				bar.setValue(bar.getValue() - .01f);
			}
		}
		icon.setDrawable(new TextureRegionDrawable(new TextureRegion(assetManager.get(character.getManaDisplay()))));
		label.setText(character.getCurrentMana() + " / " + character.getMaxMana());
		super.draw(batch, parentAlpha);
	}
}
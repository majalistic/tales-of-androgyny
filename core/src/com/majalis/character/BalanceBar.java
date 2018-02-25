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

public class BalanceBar extends Group {
	private final AssetManager assetManager;
	private final AbstractCharacter character;
	private final ProgressBar bar;
	private final Image icon;
	private final Label label;
	public BalanceBar(AbstractCharacter character, AssetManager assetManager, Skin skin) {
		this.character = character;
		this.assetManager = assetManager;
		bar = new ProgressBar(0, 1, .01f, false, skin);
		bar.setWidth(350);
		bar.setValue(character.getBalancePercent());
		this.addActor(bar);
		
		icon = new Image(assetManager.get(character.getBalanceDisplay()));
		icon.setPosition(3, 7.5f);
		this.addActor(icon);
		
		label = new Label(character.getStability().toString(), skin);
		label.setColor(Color.BROWN);
		label.setPosition(75, 8);
		this.addActor(label);
		bar.setColor(character.getStabilityColor());
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		float characterBalancePercent = character.getBalancePercent();
		if(Math.abs(bar.getValue() - characterBalancePercent) > .01) {
			if (bar.getValue() < characterBalancePercent) {
				bar.setValue(bar.getValue() + .01f);
			}
			else {
				bar.setValue(bar.getValue() - .01f);
			}
		}
		icon.setDrawable(new TextureRegionDrawable(new TextureRegion(assetManager.get(character.getBalanceDisplay()))));
		label.setText(character.getStability().toString());
		bar.setColor(character.getStabilityColor());
		super.draw(batch, parentAlpha);
	}
}
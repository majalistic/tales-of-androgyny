package com.majalis.character;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.majalis.screens.BattleScreen;

public class MasculinityDisplay extends Group {
	private final Image display;
	private final PlayerCharacter character;
	private final AssetManager assetManager;
	private AssetDescriptor<Texture> texture;
	public MasculinityDisplay (PlayerCharacter character, AssetManager assetManager) {
		this.character = character;
		this.assetManager = assetManager;
		texture = character.getMasculinityPath();
		display = new Image(assetManager.get(texture));
		display.setScale(.15f);
		this.addActor(display);
		this.addAction(hide());
	}
	@Override
	public void act(float delta) {
		AssetDescriptor<Texture> newTexture = character.getMasculinityPath();
		if (newTexture != texture) {
			texture = newTexture;
			this.addAction(
				sequence(show(), fadeIn(1), new Action(){
				@Override
				public boolean act(float delta) {
					display.setDrawable(BattleScreen.getDrawable(assetManager.get(character.getMasculinityPath())));
					return true;
				}},
				fadeOut(1)
			));
		}
		super.act(delta);
	}
}
package com.majalis.scenes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.majalis.save.MutationResult;

public class MutationActor extends Label {

	private final Texture drawTexture;
	public MutationActor(MutationResult result, Texture texture, Skin skin) {
		this(result, texture, skin, false);
	}

	public MutationActor(MutationResult result, Texture texture, Skin skin, boolean miniDisplay) {
		super(miniDisplay && result.getType().canBeMinified() ? String.valueOf(result.getMod() > 0 ? "+" + result.getMod() : result.getMod()) : result.getText(), skin);
		if (result.getMod() == 0)  setColor(Color.TAN);
		else if (result.getMod() > 0) setColor(Color.FOREST);
		else setColor(Color.FIREBRICK);
		this.drawTexture = texture;
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		batch.draw(drawTexture, getX() + getWidth() + 10, getY(), drawTexture.getWidth() / (drawTexture.getHeight() / getHeight()), getHeight());	
	}
}

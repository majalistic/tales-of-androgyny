package com.majalis.scenes;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.majalis.save.MutationResult;

public class MutationActor extends Label {

	private final Texture drawTexture;
	public MutationActor(MutationResult result, Texture texture, Skin skin) {
		super(result.getText(), skin);
		this.drawTexture = texture;
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		batch.draw(drawTexture, getX() + getWidth() + 10, getY(), drawTexture.getWidth() / (drawTexture.getHeight() / getHeight()), getHeight());	
	}
}

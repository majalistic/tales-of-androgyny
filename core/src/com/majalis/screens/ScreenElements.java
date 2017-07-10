package com.majalis.screens;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.utils.viewport.FitViewport;
/*
 * Packaged screen elements for constructing screens.
 */
public class ScreenElements {

	private final FitViewport viewport;
	private final PolygonSpriteBatch batch;
	private final FreeTypeFontGenerator fontGenerator;	
	
	public ScreenElements(FitViewport viewport, PolygonSpriteBatch batch, FreeTypeFontGenerator fontGenerator) {
		this.viewport = viewport;
		this.batch = batch;
		this.fontGenerator = fontGenerator;
	}
	
	public FitViewport getViewport() {
		return viewport;
	}
	
	public PolygonSpriteBatch getBatch() {
		return batch;
	}
	
	public BitmapFont getFont(int size) {
		FreeTypeFontParameter param = new FreeTypeFontParameter();
		param.size = size;
		return fontGenerator.generateFont(param);
	}
}

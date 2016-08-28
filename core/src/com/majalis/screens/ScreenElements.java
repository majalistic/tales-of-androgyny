package com.majalis.screens;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class ScreenElements {

	private final FitViewport viewport;
	private final SpriteBatch batch;
	private final BitmapFont font;	
	
	public ScreenElements(FitViewport viewport, SpriteBatch batch, BitmapFont font){
		this.viewport = viewport;
		this.batch = batch;
		this.font = font;
	}
	
	public FitViewport getViewport(){
		return viewport;
	}
	
	public SpriteBatch getBatch(){
		return batch;
	}
	
	public BitmapFont getFont(){
		return font;
	}
}

package com.majalis.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
/*
 * Abstract class which all Screens inherit from; each screen has a a single master Stage.  Allows a Screen to switch to a different screen via an enum.
 */
public abstract class AbstractScreen extends Stage3D implements Screen {

	private final Game game;
	private final ScreenFactory screenFactory;

	protected final PolygonSpriteBatch batch;
	protected final BitmapFont font;
	protected final ScreenElements fontFactory;
	protected boolean debug = false;
	private float clearRed, clearGreen, clearBlue, clearAlpha;
	
    protected AbstractScreen(ScreenFactory screenFactory, ScreenElements elements) {
        super(elements.getViewport(), elements.getBatch());
        this.game = screenFactory.getGame();
        this.screenFactory = screenFactory;
        this.batch = elements.getBatch();
        this.fontFactory = elements;
        this.font = elements.getFont(32);
        clearRed = .9f;
        clearGreen = .8f;
        clearBlue = .6f;
        clearAlpha = 1;
    }
 
    // Subclasses must load actors in this method
    public abstract void buildStage();
    
    public void showScreen(ScreenEnum screenRequest) {
    	AbstractScreen newScreen = screenFactory.getScreen(screenRequest);
        // Get current screen to dispose it
        Screen currentScreen = game.getScreen();
    	
        // Dispose previous screen
        if (currentScreen != null) {
            currentScreen.dispose();
        }
    	// Show new screen
    	newScreen.buildStage();
        game.setScreen(newScreen);
    }  
    
    public void clear() {
        // Clear screen
        Gdx.gl.glClearColor(clearRed, clearGreen, clearBlue, clearAlpha);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }
    
    protected void setClearColor(float clearRed, float clearGreen, float clearBlue, float clearAlpha) {
    	this.clearRed = clearRed;
    	this.clearGreen = clearGreen;
    	this.clearBlue = clearBlue;
    	this.clearAlpha = clearAlpha;
    }
    
    protected void addActorAndListen(Actor actor, int x, int y) {
		this.addActor(actor);
		actor.setPosition(x, y);
		addDragListener(actor);
	}
	
	private void addDragListener(final Actor actor) {
		actor.addListener(new DragListener() {
			@Override
		    public void drag(InputEvent event, float x, float y, int pointer) {
				if (debug) {
			        actor.moveBy(x - actor.getWidth() / 2, y - actor.getHeight() / 2);
			        System.out.println(actor.getX() + ", " + actor.getY());
				}
		    }
		});
	}
    
    @Override
    public void render(float delta) {
    	clear();
    	// this calls actor.act() on all actors
    	super.act(delta);
        // this draws all actors
        super.draw();
    }
 
    @Override
    public void show() {
        Gdx.input.setInputProcessor(this);
        font.setUseIntegerPositions(false);
    }
 
    @Override
    public void resize(int width, int height) {
        getViewport().update(width, height, false);
    }
 
    @Override public void hide() {
    	font.dispose();
    }
    @Override public void pause() {}
    @Override public void resume() {}
    
}
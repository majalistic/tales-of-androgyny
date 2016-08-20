package com.majalis.traprpg;
import java.util.Stack;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;

public abstract class AbstractScreen extends Stage implements Screen {
	private Game game;
	private AbstractScreen parent;
	private static final int winWidth = 1280;
	private static final int winHeight = 720;
	private static final Stack<ScreenEnum> screenStack = new Stack<ScreenEnum>();
	protected SpriteBatch batch;
	protected BitmapFont font;
	
    protected AbstractScreen(Game game, AbstractScreen parent) {
        super( new FitViewport(winWidth, winHeight, new OrthographicCamera()) );
        this.game = game;
        this.parent = parent;
    }
 
    // Subclasses must load actors in this method
    protected abstract void buildStage();
 
    protected void generateScreen(ScreenEnum screenEnum, Object... params){
    	if (pushToStack(screenEnum)){
    		showScreen(screenEnum.getScreen(game, this, params));
    	}	
    }
    
    protected void switchScreen(ScreenEnum screenEnum, Object... params){
    	if (pushToStack(screenEnum)){
    		screenStack.pop();
    		showScreen(screenEnum.getScreen(game, parent, params));
    	}
    }
    
    private Boolean pushToStack(ScreenEnum screenEnum){
    	// can only push if enum is not already on the stack
    	Boolean successfulPush = screenStack.search(screenEnum) == -1;
    	if (successfulPush){
    		screenStack.push(screenEnum);
    	}
    	else {
    		System.err.println("Screen is already on the stack - violation of tree structure.  Stack: " + screenStack.toString());
    	}
    	return successfulPush;
    }
    
    private void showScreen(AbstractScreen newScreen, Object... params) {
    	
        // Get current screen to dispose it
        Screen currentScreen = game.getScreen();
    	
    	// Show new screen
    	newScreen.buildStage();
        game.setScreen(newScreen);
        
        // Dispose previous screen
        if (currentScreen != null) {
            currentScreen.dispose();
        }
    }
    
    protected void exit(){
    	screenStack.pop();
    	showScreen(parent);
    }
    
    @Override
    public void render(float delta) {
        // Clear screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
 
        // Calling to Stage methods
        super.act(delta);
        super.draw();
    }
 
    @Override
    public void show() {
        Gdx.input.setInputProcessor(this);
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.setUseIntegerPositions(false);
    }
 
    @Override
    public void resize(int width, int height) {
        getViewport().update(width, height, true);
    }
 
    @Override public void hide() {
    	batch.dispose();
    	font.dispose();
    }
    @Override public void pause() {}
    @Override public void resume() {}
    
}
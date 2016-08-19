package com.majalis.traprpg;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;

public abstract class AbstractScreen extends Stage implements Screen {
	
	private TrapRPG game;
	private static final int winWidth = 1280;
	private static final int winHeight = 720;
	protected SpriteBatch batch;
	protected BitmapFont font;
	
    protected AbstractScreen(TrapRPG game) {
        super( new FitViewport(winWidth, winHeight, new OrthographicCamera()) );
        this.game = game;
    }
 
    // Subclasses must load actors in this method
    protected abstract void buildStage();
 
    protected void showScreen(ScreenEnum screenEnum, Object... params) {
    	 
        // Get current screen to dispose it
        Screen currentScreen = game.getScreen();
 
        // Show new screen
        AbstractScreen newScreen = screenEnum.getScreen(game, params);
        newScreen.buildStage();
        game.setScreen(newScreen);
 
        // Dispose previous screen
        if (currentScreen != null) {
            currentScreen.dispose();
        }
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
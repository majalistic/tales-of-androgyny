package com.majalis.traprpg;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
public class MainMenuScreen extends AbstractScreen {

	private Skin skin = new Skin(Gdx.files.internal("uiskin.json"), new TextureAtlas(Gdx.files.internal("uiskin.atlas")));
	private Texture wereslutImage;
	private Sound buttonSound;
	private int clocktick = 0;

	public MainMenuScreen() {
		wereslutImage = TextureMap.getTexture("wereslut");
		buttonSound = Gdx.audio.newSound(Gdx.files.internal("sound.wav"));		
	}

	@Override
	public void buildStage() {
		Table table = new Table();
		
		TextButton  buttonPlay 		= new TextButton("Begin", skin),
					buttonContinue 	= new TextButton("Continue", skin),
					buttonOptions 	= new TextButton("Options", skin),
					buttonPervert 	= new TextButton("Pervert", skin),
					buttonExit 		= new TextButton("Exit", skin);
		
		buttonPlay.addListener(getListener(ScreenEnum.GAME, false));            
		buttonContinue.addListener(getListener(ScreenEnum.GAME, true));
		buttonOptions.addListener(getListener(ScreenEnum.OPTIONS));
		buttonPervert.addListener(getListener(ScreenEnum.REPLAY));
	    buttonExit.addListener(getListener(ScreenEnum.EXIT));
	    
        //The buttons are displayed in this order from top to bottom
        table.add(buttonPlay).row();
        table.add(buttonContinue).row();
        table.add(buttonOptions).row();
        table.add(buttonPervert).row();
        table.add(buttonExit).row();
        table.setFillParent(true);
        
        this.addActor(table);
	}
	
	private ClickListener getListener(final ScreenEnum screenSelection, final Object... params){
		return new ClickListener(){
	        @Override
	        public void clicked(InputEvent event, float x, float y) {
	        	buttonSound.play();
	        	ScreenManager.getInstance().showScreen(screenSelection, params);    
	        }
	    };
	}
	
	@Override
	public void render(float delta) {
		super.render(delta);
		OrthographicCamera camera = (OrthographicCamera) getCamera();
        batch.setTransformMatrix(camera.view);
        
		camera.update();
		
		batch.setProjectionMatrix(camera.combined);
		batch.begin(); 
		// need to make these relative to viewport
		batch.draw(wereslutImage, 1020, 600);
		font.draw(batch, "tRaPG - The Really Awesome Porn Game", 1200, 900);
		font.draw(batch, String.valueOf(clocktick), 1850, 400);
		clocktick++;
		batch.end();
	}

	public void resize(int width, int height) {
		this.getViewport().update(width, height, true);
        this.getCamera().update();
	}

	@Override
	public void dispose() {
		skin.dispose();
		buttonSound.dispose();
	}

}
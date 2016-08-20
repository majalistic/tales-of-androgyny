package com.majalis.traprpg;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
public class MainMenuScreen extends AbstractScreen {

	private final AssetManager assetManager;
	private Skin skin; 
	private Texture wereslutImage;
	private Sound buttonSound;
	private int clocktick = 0;
	
	
	public MainMenuScreen(Game game, AbstractScreen parent, Object... params) {
		super(game, parent);	
		assetManager = (AssetManager) params[0];
	}

	@Override
	public void buildStage() {
		skin = new Skin(Gdx.files.internal("uiskin.json"), new TextureAtlas(Gdx.files.internal("uiskin.atlas")));
		// synchronous
		assetManager.load("uiskin.atlas", TextureAtlas.class);

		// asynchronous
		assetManager.load("uiskin.json", Skin.class);
		assetManager.load("wereslut.png", Texture.class);
		assetManager.load("sound.wav", Sound.class);
		
		// forcing asynchronous loading to be synchronous
		assetManager.finishLoading();

		skin = assetManager.get("uiskin.json", Skin.class);
		wereslutImage = assetManager.get("wereslut.png", Texture.class);
		buttonSound = assetManager.get("sound.wav", Sound.class);
		
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
	    
	    Array<TextButton> buttons = new Array<TextButton>();
	    buttons.addAll(buttonPlay, buttonContinue, buttonOptions, buttonPervert, buttonExit);

	    for (TextButton button: buttons){
	    	table.add(button).row();
	    }
        table.setFillParent(true);
        
        this.addActor(table);
	}
	
	private ClickListener getListener(final ScreenEnum screenSelection, final Object... params){
		return new ClickListener(){
	        @Override
	        public void clicked(InputEvent event, float x, float y) {
	        	buttonSound.play();
	        	generateScreen(screenSelection, params);    
	        }
	    };
	}
	
	@Override
	public void render(float delta) {
		super.render(delta);
		assetManager.update();
		OrthographicCamera camera = (OrthographicCamera) getCamera();
        batch.setTransformMatrix(camera.view);
        
		camera.update();
		
		batch.setProjectionMatrix(camera.combined);
		batch.begin(); 
		// need to make these relative to viewport
		
		font.draw(batch, "tRaPG - The Really Awesome Porn Game", 1200, 900);
		// currently this texture is loaded synchronously - want to experiment with a loading screen, may need a VERY large texture to view it
		float progress = assetManager.getProgress();
		if (progress < 1)
			font.draw(batch, "Loading: " + (progress * 100) + "%", 1850, 600);
		else {
			batch.draw(wereslutImage, 1020, 600);
		}
		font.draw(batch, String.valueOf(clocktick++), 1850, 400);
		batch.end();
	}

	public void resize(int width, int height) {
		this.getViewport().update(width, height, true);
        this.getCamera().update();
	}

	@Override
	public void dispose() {
		// this should clear the loaded assets, but this works fine for now - don't call dispose, or the asset maanger will stop functioning!
		assetManager.clear();
	}

}
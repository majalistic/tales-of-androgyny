package com.majalis.traprpg;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
public class MainMenuScreen extends AbstractScreen {

	private final AssetManager assetManager;
	private final SaveManager saveManager;
	private Skin skin; 
	private Texture wereslutImage;
	private Sound buttonSound;
	private int clocktick = 0;

	public MainMenuScreen(Game game, ScreenService service, AssetManager assetManager, SaveManager saveManager) {
		super(game, service);
		this.assetManager = assetManager;
		this.saveManager = saveManager;
	}

	@Override
	public void buildStage() {

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
		
		Array<String> buttonLabels = new Array<String>();
		Array<ScreenEnum> optionList = new Array<ScreenEnum>();
		buttonLabels.addAll("Begin", "Continue", "Options", "Pervert", "Exit");
		optionList.addAll(ScreenEnum.GAME, ScreenEnum.GAME_LOAD, ScreenEnum.OPTIONS, ScreenEnum.REPLAY, ScreenEnum.EXIT);
		
		Array<TextButton> buttons = new Array<TextButton>();
		for (int ii = 0; ii < buttonLabels.size; ii++){
			buttons.add(new TextButton(buttonLabels.get(ii), skin));
			buttons.get(ii).addListener(getListener(optionList.get(ii)));
			table.add(buttons.get(ii)).row();
		}
	
        table.setFillParent(true);
        
        this.addActor(table);
	}
	
	private ClickListener getListener(final ScreenEnum screenSelection){
		return new ClickListener(){
	        @Override
	        public void clicked(InputEvent event, float x, float y) {
	        	buttonSound.play();
	        	if (screenSelection == ScreenEnum.GAME_LOAD){
	        		saveManager.setSaveType(SaveManager.SaveType.LOAD);
	        		showScreen(ScreenEnum.GAME); 
	        	}
	        	else {
	        		saveManager.setSaveType(SaveManager.SaveType.NEW);
	        		showScreen(screenSelection);    
	        	}
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
		batch.draw(wereslutImage, 1020, 600);
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
package com.majalis.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.majalis.encounter.Background;
import com.majalis.save.LoadService;
import com.majalis.save.SaveService;
/*
 * The main menu screen loaded initially.  UI that handles player input to switch to different screens.
 */
public class MainMenuScreen extends AbstractScreen {

	public static final ObjectMap<String, Class<?>> resourceRequirements = new ObjectMap<String, Class<?>>();
	static {
		resourceRequirements.put("uiskin.json", Skin.class);
		resourceRequirements.put("MainMenuScreen.jpg", Texture.class);
		resourceRequirements.put("sound.wav", Sound.class);
	}
	private final AssetManager assetManager;
	private final SaveService saveService;
	private final Skin skin; 
	private final Texture backgroundImage;
	private final Sound buttonSound;
	private int clocktick = 0;

	public MainMenuScreen(ScreenFactory factory, ScreenElements elements, AssetManager assetManager, SaveService saveService, LoadService loadService) {
		super(factory, elements);
		this.assetManager = assetManager;
		this.saveService = saveService;
		this.skin = assetManager.get("uiskin.json", Skin.class);
		this.backgroundImage = assetManager.get("MainMenuScreen.jpg", Texture.class);
		this.buttonSound = assetManager.get("sound.wav", Sound.class);
	}

	@Override
	public void buildStage() {		
		Table table = new Table();
		
		Array<String> buttonLabels = new Array<String>();
		Array<ScreenEnum> optionList = new Array<ScreenEnum>();
		buttonLabels.addAll("Begin", "Continue", "Options", "Pervert", "Exit");
		optionList.addAll(ScreenEnum.NEW_GAME, ScreenEnum.LOAD_GAME, ScreenEnum.OPTIONS, ScreenEnum.REPLAY, ScreenEnum.EXIT);
		
		Array<TextButton> buttons = new Array<TextButton>();
		for (int ii = 0; ii < buttonLabels.size; ii++){
			buttons.add(new TextButton(buttonLabels.get(ii), skin));
			buttons.get(ii).addListener(getListener(optionList.get(ii)));
			table.add(buttons.get(ii)).width(120).height(40).row();
		}
	
        table.setFillParent(true);
        
        this.addActor(new Background(backgroundImage));
        this.addActor(table);
        table.addAction(Actions.moveTo(330, 130));
	}
	
	@Override
	public void render(float delta) {super.render(delta);
		OrthographicCamera camera = (OrthographicCamera) getCamera();
        batch.setTransformMatrix(camera.view);
        
		camera.update();
		
		batch.setProjectionMatrix(camera.combined);
		batch.begin(); 
		// need to make these relative to viewport
		font.draw(batch, String.valueOf(clocktick++), 1850, 400);
		font.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 680, 1050);
		font.draw(batch, "Version: 0.1.07.0", 1800, 1050);
		batch.end();
	}

	public void resize(int width, int height) {
		this.getViewport().update(width, height, true);
        this.getCamera().update();
	}

	@Override
	public void dispose() {
		for(String path: resourceRequirements.keys()){
			assetManager.unload(path);
		}
	}
	
	private ClickListener getListener(final ScreenEnum screenSelection){
		return new ClickListener(){
	        @Override
	        public void clicked(InputEvent event, float x, float y) {
	        	buttonSound.play(.5f);
        		if (screenSelection == ScreenEnum.NEW_GAME){
        			// ONLY CALL THIS TO DESTROY OLD DATA AND REPLACE WITH A BRAND NEW SAVE
        			saveService.newSave();
        		}
	        	showScreen(screenSelection);    
	        }
	    };
	}

}
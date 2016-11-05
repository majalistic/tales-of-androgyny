package com.majalis.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.majalis.asset.AssetEnum;
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
		resourceRequirements.put(AssetEnum.STANCE_ARROW.getPath(), Texture.class);
		resourceRequirements.put(AssetEnum.MAIN_MENU_MUSIC.getPath(), Music.class);
		resourceRequirements.put("sound.wav", Sound.class);
	}
	private final AssetManager assetManager;
	private final SaveService saveService;
	private final Skin skin; 
	private final Texture backgroundImage;
	private final Texture arrowImage;
	private final Music music;
	private final Sound buttonSound;
	private final Array<TextButton> buttons;
	private int clocktick = 0;
	private int selection;

	public MainMenuScreen(ScreenFactory factory, ScreenElements elements, AssetManager assetManager, SaveService saveService, LoadService loadService) {
		super(factory, elements);
		this.assetManager = assetManager;
		this.saveService = saveService;
		this.skin = assetManager.get("uiskin.json", Skin.class);
		this.backgroundImage = assetManager.get("MainMenuScreen.jpg", Texture.class);
		this.arrowImage = assetManager.get(AssetEnum.STANCE_ARROW.getPath(), Texture.class);
		this.music = assetManager.get(AssetEnum.MAIN_MENU_MUSIC.getPath(), Music.class);
		this.buttonSound = assetManager.get("sound.wav", Sound.class);
		buttons = new Array<TextButton>();
		selection = 0;
	}

	@Override
	public void buildStage() {		
		Table table = new Table();
		
		Array<String> buttonLabels = new Array<String>();
		Array<ScreenEnum> optionList = new Array<ScreenEnum>();
		buttonLabels.addAll("Begin", "Continue", "Options", "Pervert", "Credits", "Exit");
		optionList.addAll(ScreenEnum.NEW_GAME, ScreenEnum.LOAD_GAME, ScreenEnum.OPTIONS, ScreenEnum.REPLAY, ScreenEnum.CREDITS, ScreenEnum.EXIT);
		
		for (int ii = 0; ii < buttonLabels.size; ii++){
			buttons.add(new TextButton(buttonLabels.get(ii), skin));
			buttons.get(ii).addListener(getListener(optionList.get(ii), ii));
			table.add(buttons.get(ii)).width(120).height(40).row();
		}
	
        table.setFillParent(true);
        
        this.addActor(new Background(backgroundImage));
        this.addActor(table);
        table.addAction(Actions.moveTo(330, 130));
        music.play();
        music.setVolume(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("musicVolume", 1));
        music.setLooping(true);
	}
	
	@Override
	public void render(float delta) {
		super.render(delta);
		OrthographicCamera camera = (OrthographicCamera) getCamera();
        batch.setTransformMatrix(camera.view);
        
        if(Gdx.input.isKeyJustPressed(Keys.UP)){
        	if (selection > 0) selection--;
        	else selection = buttons.size-1;
        }
        else if(Gdx.input.isKeyJustPressed(Keys.DOWN)){
        	if (selection < buttons.size- 1) selection++;
        	else selection = 0;
        }
        else if(Gdx.input.isKeyJustPressed(Keys.ENTER)){
        	InputEvent event1 = new InputEvent();
            event1.setType(InputEvent.Type.touchDown);
            buttons.get(selection).fire(event1);

            InputEvent event2 = new InputEvent();
            event2.setType(InputEvent.Type.touchUp);
            buttons.get(selection).fire(event2);
        }
		camera.update();
		
		batch.setProjectionMatrix(camera.combined);
		batch.begin(); 
		batch.draw(arrowImage, 1520, 925 - selection * 40, 30, 50);
		// need to make these relative to viewport
		font.draw(batch, String.valueOf(clocktick++), 1850, 400);
		font.draw(batch, "Version: 0.1.12.1", 1600, 1050);
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
	
	private ClickListener getListener(final ScreenEnum screenSelection, final int index){
		return new ClickListener(){
	        @Override
	        public void clicked(InputEvent event, float x, float y) {
	        	buttonSound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
        		if (screenSelection == ScreenEnum.NEW_GAME){
        			// ONLY CALL THIS TO DESTROY OLD DATA AND REPLACE WITH A BRAND NEW SAVE
        			saveService.newSave();
        		}
        		if (!(screenSelection == ScreenEnum.OPTIONS || screenSelection == ScreenEnum.CREDITS || screenSelection == ScreenEnum.REPLAY)){
        			music.stop();
        		}
	        	showScreen(screenSelection);    
	        }
	        @Override
	        public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
				selection = index;
	        }
	    };
	}
	
	@Override
	public void show() {
		super.show();
	    getRoot().getColor().a = 0;
	    getRoot().addAction(Actions.fadeIn(0.5f));
	}

}
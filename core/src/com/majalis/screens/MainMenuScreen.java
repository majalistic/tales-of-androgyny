package com.majalis.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetDescriptor;
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
import com.majalis.asset.AssetEnum;
import com.majalis.encounter.Background.BackgroundBuilder;
import com.majalis.save.LoadService;
import com.majalis.save.SaveService;
import com.majalis.talesofandrogyny.TalesOfAndrogyny;
/*
 * The main menu screen loaded initially.  UI that handles player input to switch to different screens.
 */
public class MainMenuScreen extends AbstractScreen {

	public static final Array<AssetDescriptor<?>> resourceRequirements = new Array<AssetDescriptor<?>>();
	static {
		resourceRequirements.add(AssetEnum.UI_SKIN.getSkin());
		resourceRequirements.add(AssetEnum.BUTTON_SOUND.getSound());
		resourceRequirements.add(AssetEnum.MAIN_MENU_MUSIC.getMusic());
		resourceRequirements.add(AssetEnum.STANCE_ARROW.getTexture());
		resourceRequirements.add(AssetEnum.MAIN_MENU_SCREEN.getTexture());
	}
	private final AssetManager assetManager;
	private final SaveService saveService;
	private final Skin skin; 
	private final Texture backgroundImage;
	private final Texture arrowImage;
	private final Music music;
	private final Sound buttonSound;
	private final Array<TextButton> buttons;
	private int selection;

	public MainMenuScreen(ScreenFactory factory, ScreenElements elements, AssetManager assetManager, SaveService saveService, LoadService loadService) {
		super(factory, elements);
		this.assetManager = assetManager;
		this.saveService = saveService;
		this.skin = assetManager.get(AssetEnum.UI_SKIN.getSkin());
		this.backgroundImage = assetManager.get(AssetEnum.MAIN_MENU_SCREEN.getTexture());
		this.arrowImage = assetManager.get(AssetEnum.STANCE_ARROW.getTexture());
		this.music = assetManager.get(AssetEnum.MAIN_MENU_MUSIC.getMusic());
		this.buttonSound = assetManager.get(AssetEnum.BUTTON_SOUND.getSound());
		buttons = new Array<TextButton>();
		selection = 0;
	}

	@Override
	public void buildStage() {		
		Table table = new Table();
		
		Array<String> buttonLabels = new Array<String>();
		Array<ScreenEnum> optionList = new Array<ScreenEnum>();
		buttonLabels.addAll("Begin", "Continue", "Load", "Options", "Pervert", "Credits", "Exit");
		optionList.addAll(ScreenEnum.NEW_GAME, ScreenEnum.CONTINUE, ScreenEnum.LOAD_GAME, ScreenEnum.OPTIONS, ScreenEnum.REPLAY, ScreenEnum.CREDITS, ScreenEnum.EXIT);
		
		for (int ii = 0; ii < buttonLabels.size; ii++) {
			buttons.add(new TextButton(buttonLabels.get(ii), skin));
			buttons.get(ii).addListener(getListener(optionList.get(ii), ii));
			table.add(buttons.get(ii)).size(180, 60).row();
		}
	
        table.setFillParent(true);
        
        this.addActor(new BackgroundBuilder(backgroundImage).build());
        this.addActor(table);
        table.setPosition(495, 195);
        music.play();
        music.setVolume(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("musicVolume", 1));
        music.setLooping(true);
	}
	
	@Override
	public void render(float delta) {
		super.render(delta);
		OrthographicCamera camera = (OrthographicCamera) getCamera();
        batch.setTransformMatrix(camera.view);
        
        if(Gdx.input.isKeyJustPressed(Keys.UP)) {
        	if (selection > 0) selection--;
        	else selection = buttons.size-1;
        }
        else if(Gdx.input.isKeyJustPressed(Keys.DOWN)) {
        	if (selection < buttons.size- 1) selection++;
        	else selection = 0;
        }
        else if(Gdx.input.isKeyJustPressed(Keys.ENTER)) {
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
		// need to make these actors
		batch.draw(arrowImage, 2280, 1428 - selection * 60, 30, 50);
		font.draw(batch, "Version: 0.1.20.3" + (TalesOfAndrogyny.patron ? " Patron-Only" : ""), 2450, 600);
		batch.end();
	}

	@Override
	public void dispose() {
		for(AssetDescriptor<?> path: resourceRequirements) {
			assetManager.unload(path.fileName);
		}
	}
	
	private ClickListener getListener(final ScreenEnum screenSelection, final int index) {
		return new ClickListener() {
	        @Override
	        public void clicked(InputEvent event, float x, float y) {
	        	buttonSound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
        		if (screenSelection == ScreenEnum.NEW_GAME) {
        			// ONLY CALL THIS TO DESTROY OLD DATA AND REPLACE WITH A BRAND NEW SAVE
        			saveService.newSave();
        		}
        		if (screenSelection == ScreenEnum.LOAD_GAME) {
        			saveService.newSave("data/save01.json");
        		}
        		if (!(screenSelection == ScreenEnum.OPTIONS || screenSelection == ScreenEnum.CREDITS || screenSelection == ScreenEnum.REPLAY)) {
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
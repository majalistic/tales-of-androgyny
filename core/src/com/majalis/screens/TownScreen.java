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
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.majalis.asset.AssetEnum;
import com.majalis.encounter.Background;
import com.majalis.encounter.Background.BackgroundBuilder;
import com.majalis.encounter.EncounterCode;
import com.majalis.save.SaveEnum;
import com.majalis.save.SaveManager;
import com.majalis.save.SaveService;

public class TownScreen extends AbstractScreen {

	public static final ObjectMap<String, Class<?>> resourceRequirements = new ObjectMap<String, Class<?>>();
	static {
		resourceRequirements.put(AssetEnum.UI_SKIN.getPath(), Skin.class);
		resourceRequirements.put(AssetEnum.TOWN_BG.getPath(), Texture.class);
		resourceRequirements.put(AssetEnum.SHOPKEEP.getPath(), Texture.class);
		resourceRequirements.put(AssetEnum.STANCE_ARROW.getPath(), Texture.class);
		resourceRequirements.put(AssetEnum.DEFAULT_BACKGROUND.getPath(), Texture.class);
		resourceRequirements.put(AssetEnum.BATTLE_HOVER.getPath(), Texture.class);
		resourceRequirements.put(AssetEnum.BATTLE_TEXTBOX.getPath(), Texture.class);
		resourceRequirements.put(AssetEnum.TEXT_BOX.getPath(), Texture.class);
		resourceRequirements.put(AssetEnum.SHOP_MUSIC.getPath(), Music.class);
		resourceRequirements.put(AssetEnum.BUTTON_SOUND.getPath(), Sound.class);
		resourceRequirements.put(AssetEnum.ENCOUNTER_MUSIC.getPath(), Music.class);
	}
	private final SaveService saveService;
	private final Skin skin;
	private final Background background;
	private final Image arrow;
	private final Sound buttonSound;
	private final Music music;
	private final Array<TextButton> buttons;
	private final Image shopkeep;
	private int selection;
	
	protected TownScreen(ScreenFactory screenFactory, ScreenElements elements, AssetManager assetManager, SaveService saveService) {
		super(screenFactory, elements);
		this.saveService = saveService;
		skin = assetManager.get(AssetEnum.UI_SKIN.getPath(), Skin.class);
		background = new BackgroundBuilder(assetManager.get(AssetEnum.TOWN_BG.getPath(), Texture.class)).build();
		arrow = new Image(assetManager.get(AssetEnum.STANCE_ARROW.getPath(), Texture.class));
		music = assetManager.get(AssetEnum.SHOP_MUSIC.getPath(), Music.class);
		buttonSound = assetManager.get(AssetEnum.BUTTON_SOUND.getPath(), Sound.class);
		Texture shopkeepTexture = assetManager.get(AssetEnum.SHOPKEEP.getPath(), Texture.class);
		shopkeep = new Image(shopkeepTexture);
		shopkeep.setSize(shopkeepTexture.getWidth() / (shopkeepTexture.getHeight() / 1050f), 1050);
		
		buttons = new Array<TextButton>();
		selection = 0;
	}

	@Override
	public void buildStage() {
		Table table = new Table();
		
		Array<String> buttonLabels = new Array<String>();
		buttonLabels.addAll("General Store", "Town Crier", "Inn", "Depart");
		
		for (int ii = 0; ii < buttonLabels.size; ii++){
			buttons.add(new TextButton(buttonLabels.get(ii), skin));
			buttons.get(ii).addListener(getListener(ii));
			table.add(buttons.get(ii)).size(300, 60).row();
		}
		
		buttons.get(0).addListener(new ClickListener(){
	        @Override
	        public void clicked(InputEvent event, float x, float y) {
	        	buttonSound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
	        	saveService.saveDataValue(SaveEnum.CONTEXT, SaveManager.GameContext.ENCOUNTER);
	        	saveService.saveDataValue(SaveEnum.RETURN_CONTEXT, SaveManager.GameContext.TOWN);
	        	saveService.saveDataValue(SaveEnum.ENCOUNTER_CODE, EncounterCode.SHOP);
	        	music.stop();
	        	showScreen(ScreenEnum.LOAD_GAME);    
	        }
	    });
		
		buttons.get(3).addListener(new ClickListener(){
	        @Override
	        public void clicked(InputEvent event, float x, float y) {
	        	buttonSound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
	        	saveService.saveDataValue(SaveEnum.CONTEXT, SaveManager.GameContext.WORLD_MAP);
	        	music.stop();
	        	showScreen(ScreenEnum.LOAD_GAME);    
	        }
	    });
	
        table.setFillParent(true);
        
        this.addActor(background);
        this.addActor(shopkeep);
        shopkeep.setPosition(300, 0);
        
        this.addActor(table);
        table.setPosition(300, 195);
        this.addActor(arrow);
        
        arrow.setSize(45, 75);
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
        arrow.setPosition(1065, 788 - selection * 60);
		camera.update();
		batch.setProjectionMatrix(camera.combined);
	}
	
	private ClickListener getListener(final int index){
		return new ClickListener(){
	        @Override
	        public void clicked(InputEvent event, float x, float y) {
	        }
	        @Override
	        public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
				selection = index;
	        }
	    };
	}

}

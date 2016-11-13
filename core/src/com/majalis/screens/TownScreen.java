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
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.majalis.asset.AssetEnum;
import com.majalis.encounter.Background;
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
		resourceRequirements.put(AssetEnum.ENCOUNTER_MUSIC.getPath(), Music.class);
		resourceRequirements.put(AssetEnum.BUTTON_SOUND.getPath(), Sound.class);
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
		background = new Background(assetManager.get(AssetEnum.TOWN_BG.getPath(), Texture.class));
		arrow = new Image(assetManager.get(AssetEnum.STANCE_ARROW.getPath(), Texture.class));
		music = assetManager.get(AssetEnum.ENCOUNTER_MUSIC.getPath(), Music.class);
		buttonSound = assetManager.get(AssetEnum.BUTTON_SOUND.getPath(), Sound.class);
		Texture shopkeepTexture = assetManager.get(AssetEnum.SHOPKEEP.getPath(), Texture.class);
		shopkeep = new Image(shopkeepTexture);
		shopkeep.setHeight(700);
		shopkeep.setWidth(shopkeepTexture.getWidth() / (shopkeepTexture.getHeight() / 700f) );
		
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
			table.add(buttons.get(ii)).width(200).height(40).row();
		}
		
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
        shopkeep.addAction(Actions.moveTo(200, 0));
        
        this.addActor(table);
        table.addAction(Actions.moveTo(200, 130));
        this.addActor(arrow);
        
        arrow.setWidth(30);
        arrow.setHeight(50);
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
        arrow.addAction(Actions.moveTo(710, 525 - selection * 40));
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

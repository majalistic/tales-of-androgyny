package com.majalis.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.majalis.asset.AssetEnum;
import com.majalis.encounter.Background;
import com.majalis.encounter.Background.BackgroundBuilder;
import com.majalis.encounter.EncounterCode;
import com.majalis.save.SaveEnum;
import com.majalis.save.SaveManager;
import com.majalis.save.SaveService;

public class TownScreen extends AbstractScreen {

	public static final Array<AssetDescriptor<?>> resourceRequirements = new Array<AssetDescriptor<?>>();
	static {
		resourceRequirements.add(AssetEnum.UI_SKIN.getSkin());
		resourceRequirements.add(AssetEnum.TOWN_BG.getTexture());
		resourceRequirements.add(AssetEnum.STANCE_ARROW.getTexture());
		resourceRequirements.add(AssetEnum.DEFAULT_BACKGROUND.getTexture());
		resourceRequirements.add(AssetEnum.BATTLE_HOVER.getTexture());
		resourceRequirements.add(AssetEnum.BATTLE_TEXTBOX.getTexture());
		resourceRequirements.add(AssetEnum.TEXT_BOX.getTexture());
		resourceRequirements.add(AssetEnum.SHOPKEEP.getTexture());
		resourceRequirements.add(AssetEnum.TRAINER.getTexture());
		resourceRequirements.add(AssetEnum.SHOP_MUSIC.getMusic());
		resourceRequirements.add(AssetEnum.BUTTON_SOUND.getSound());
		resourceRequirements.add(AssetEnum.EQUIP.getSound());
		resourceRequirements.add(AssetEnum.ENCOUNTER_MUSIC.getMusic());
		resourceRequirements.addAll(EncounterScreen.getRequirements(EncounterCode.TOWN));
	}
	private final SaveService saveService;
	private final Skin skin;
	private final Background background;
	private final Image arrow;
	private final Sound buttonSound;
	private final Music music;
	private final Array<TextButton> buttons;
	private int selection;
	
	protected TownScreen(ScreenFactory screenFactory, ScreenElements elements, AssetManager assetManager, SaveService saveService) {
		super(screenFactory, elements);
		this.saveService = saveService;
		skin = assetManager.get(AssetEnum.UI_SKIN.getSkin());
		background = new BackgroundBuilder(assetManager.get(AssetEnum.TOWN_BG.getTexture())).build();
		arrow = new Image(assetManager.get(AssetEnum.STANCE_ARROW.getTexture()));
		music = assetManager.get(AssetEnum.SHOP_MUSIC.getMusic());
		buttonSound = assetManager.get(AssetEnum.BUTTON_SOUND.getSound());
		
		buttons = new Array<TextButton>();
		selection = 0;
	}

	private ClickListener getListener(EncounterCode code) {
		return new ClickListener() {
	        @Override
	        public void clicked(InputEvent event, float x, float y) {
	        	buttonSound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
	        	saveService.saveDataValue(SaveEnum.CONTEXT, SaveManager.GameContext.ENCOUNTER);
	        	saveService.saveDataValue(SaveEnum.RETURN_CONTEXT, SaveManager.GameContext.TOWN);
	        	saveService.saveDataValue(SaveEnum.ENCOUNTER_CODE, code);
	        	music.stop();
	        	showScreen(ScreenEnum.CONTINUE);    
	        }
	    };
	}
	
	@Override
	public void buildStage() {
		Table table = new Table();
		table.align(Align.bottomLeft);
        table.setPosition(1200, 595);
		
		Array<String> buttonLabels = new Array<String>();
		buttonLabels.addAll("General Store", "Blacksmith", "Inn", "Town Crier", "Depart");
		
		for (int ii = 0; ii < buttonLabels.size; ii++) {
			buttons.add(new TextButton(buttonLabels.get(ii), skin));
			buttons.get(ii).addListener(getListener(ii));
			table.add(buttons.get(ii)).size(300, 60).row();
		}
		
		buttons.get(0).addListener(getListener(EncounterCode.SHOP));
		buttons.get(1).addListener(getListener(EncounterCode.WEAPON_SHOP));
		buttons.get(2).addListener(getListener(EncounterCode.INN));  		
		//buttons.get(3).addListener(getListener(EncounterCode.TOWN_CRIER)); 
		buttons.get(4).addListener(new ClickListener() {
	        @Override
	        public void clicked(InputEvent event, float x, float y) {
	        	buttonSound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
	        	saveService.saveDataValue(SaveEnum.CONTEXT, SaveManager.GameContext.WORLD_MAP);
	        	music.stop();
	        	showScreen(ScreenEnum.CONTINUE);    
	        }
	    });
		
        this.addActor(background);
        
        this.addActor(table);
        this.addActor(arrow);
        
        arrow.setSize(45, 75);
        setArrowPosition();
        arrow.setPosition(arrow.getX(), arrow.getY() + 60 * (buttons.size-1));
        
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
        	setArrowPosition();
        }
        else if(Gdx.input.isKeyJustPressed(Keys.DOWN)) {
        	if (selection < buttons.size- 1) selection++;
        	else selection = 0;
        	setArrowPosition();
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
	}
	
	private void setArrowPosition() {
		Vector2 buttonPosition = buttons.get(selection).localToStageCoordinates(new Vector2(0,0));
		arrow.setPosition(buttonPosition.x-43, buttonPosition.y-8);
	}
	
	private ClickListener getListener(final int index) {
		return new ClickListener() {
	        @Override
	        public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
				selection = index;
				setArrowPosition();
	        }
	    };
	}

}

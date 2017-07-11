package com.majalis.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.majalis.asset.AssetEnum;
import com.majalis.character.PlayerCharacter;
import com.majalis.encounter.Background;
import com.majalis.encounter.Background.BackgroundBuilder;
import com.majalis.save.SaveEnum;
import com.majalis.save.SaveManager;
import com.majalis.save.SaveService;

public class CampScreen extends AbstractScreen {

	public static final Array<AssetDescriptor<?>> resourceRequirements = new Array<AssetDescriptor<?>>();
	static {
		resourceRequirements.add(AssetEnum.UI_SKIN.getSkin());
		resourceRequirements.add(AssetEnum.PLAINS_BG.getTexture());
		resourceRequirements.add(AssetEnum.APPLE.getTexture());
		resourceRequirements.add(AssetEnum.WORLD_MAP_UI.getTexture());
		resourceRequirements.add(AssetEnum.STANCE_ARROW.getTexture());
		resourceRequirements.add(AssetEnum.DEFAULT_BACKGROUND.getTexture());
		resourceRequirements.add(AssetEnum.BATTLE_HOVER.getTexture());
		resourceRequirements.add(AssetEnum.BATTLE_TEXTBOX.getTexture());
		resourceRequirements.add(AssetEnum.TEXT_BOX.getTexture());
		resourceRequirements.add(AssetEnum.SHOP_MUSIC.getMusic());
		resourceRequirements.add(AssetEnum.BUTTON_SOUND.getSound());
	}
	private final SaveService saveService;
	private final PlayerCharacter character;
	private final Skin skin;
	private final Background background;
	private final Image arrow;
	private final Sound buttonSound;
	private final Music music;
	private final Array<TextButton> buttons;
	private final Texture characterUITexture;
	private final Texture food;
	private int selection;
	private int time;
	
	protected CampScreen(ScreenFactory screenFactory, ScreenElements elements, AssetManager assetManager, SaveService saveService, PlayerCharacter character, int time) {
		super(screenFactory, elements);
		this.saveService = saveService;
		this.character = character;
		skin = assetManager.get(AssetEnum.UI_SKIN.getSkin());
		background = new BackgroundBuilder(assetManager.get(AssetEnum.PLAINS_BG.getTexture())).build();
		this.time = time;
		
		food = assetManager.get(AssetEnum.APPLE.getTexture());
		
		characterUITexture = assetManager.get(AssetEnum.WORLD_MAP_UI.getTexture());
		
		background.setColor(getTimeColor(time));
		arrow = new Image(assetManager.get(AssetEnum.STANCE_ARROW.getTexture()));
		music = assetManager.get(AssetEnum.SHOP_MUSIC.getMusic());
		buttonSound = assetManager.get(AssetEnum.BUTTON_SOUND.getSound());
		
		buttons = new Array<TextButton>();
		selection = 0;
	}

	private Color getTimeColor(int time) { return TimeOfDay.getTime(time).getColor(); }
	private String getTime() { return TimeOfDay.getTime(time).getDisplay(); }
	
	private void passTime(int timePass) {
		time += timePass;
		saveService.saveDataValue(SaveEnum.TIME, timePass);
		saveService.saveDataValue(SaveEnum.HEALTH, 5 * timePass);
		background.setColor(getTimeColor(time));	
	}
	
	private void disableEat() {
		if (character.getFood() < 2) {
			TextButtonStyle style = new TextButtonStyle(buttons.get(2).getStyle());
			style.fontColor = Color.RED;
			buttons.get(2).setStyle(style);
			buttons.get(2).setTouchable(Touchable.disabled);
		}
	}
	
	@Override
	public void buildStage() {
		Table table = new Table();
		table.align(Align.bottomLeft);
        table.setPosition(1200, 595);
		
		Array<String> buttonLabels = new Array<String>();
		buttonLabels.addAll("Rest", "Sleep (morning)", "Sleep (night)");
		//buttonLabels.add("Chat");
		buttonLabels.add("Depart");
		
		for (int ii = 0; ii < buttonLabels.size; ii++) {
			buttons.add(new TextButton(buttonLabels.get(ii), skin));
			buttons.get(ii).addListener(getListener(ii));
			table.add(buttons.get(ii)).size(300, 60).row();
		}
		
		buttons.get(0).addListener(new ClickListener() { @Override public void clicked(InputEvent event, float x, float y) { passTime(1); } } );
		buttons.get(1).addListener(new ClickListener() { @Override public void clicked(InputEvent event, float x, float y) { passTime(TimeOfDay.timeTillNext(TimeOfDay.DAWN, time)); } } );
		buttons.get(2).addListener(new ClickListener() { @Override public void clicked(InputEvent event, float x, float y) { passTime(TimeOfDay.timeTillNext(TimeOfDay.DUSK, time)); } } );
		
		disableEat();
		buttons.get(3).addListener(new ClickListener() {
	        @Override
	        public void clicked(InputEvent event, float x, float y) {
	        	buttonSound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
	        	saveService.saveDataValue(SaveEnum.CONTEXT, SaveManager.GameContext.WORLD_MAP);
	        	music.stop();
	        	showScreen(ScreenEnum.CONTINUE);    
	        }
	    });
		
        this.addActor(background);
        
        Image characterUI = new Image(characterUITexture);
		this.addActor(characterUI);
		characterUI.setScale(1.1f);
        
        Image foodIcon = new Image(food);
		foodIcon.setSize(75, 75);
		this.addActor(foodIcon);
        
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
        
        drawText();
		camera.update();
		batch.setProjectionMatrix(camera.combined);
	}
	
	// this should be replaced with label actors
	private void drawText() {
		batch.begin();
		OrthographicCamera camera = (OrthographicCamera) getCamera();
		batch.setColor(1.0f, 1.0f, 1.0f, 1);
		font.draw(batch, String.valueOf(character.getCurrentHealth()), camera.position.x + 310.2f, camera.position.y + 139.7f);
		font.draw(batch, "Day: " + (time / 6 + 1), camera.position.x + 350, camera.position.y + 150);
		font.draw(batch, getTime(), camera.position.x + 370, camera.position.y + 125);
		font.draw(batch, "X " + character.getFood(), camera.position.x + 23, camera.position.y + 25);
		batch.end();
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

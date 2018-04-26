package com.majalis.screens;

import static com.majalis.asset.AssetEnum.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.majalis.asset.AssetEnum;
import com.majalis.save.LoadService;
import com.majalis.save.SaveEnum;
import com.majalis.save.SaveService;
import com.majalis.talesofandrogyny.TalesOfAndrogyny;
/*
 * The main menu screen loaded initially.  UI that handles player input to switch to different screens.
 */
public class MainMenuScreen extends AbstractScreen {

	private static final int INTRO = 7;
	private static final int mgScroll = 1920;
	private static final int fgScroll = 3000;
	
	public static final Array<AssetDescriptor<?>> resourceRequirements = new Array<AssetDescriptor<?>>();
	static {
		resourceRequirements.add(UI_SKIN.getSkin());
		resourceRequirements.add(BUTTON_SOUND.getSound());
		resourceRequirements.add(MAIN_MENU_MUSIC.getMusic());
		
		// need to refactor to get all stance textures
		AssetEnum[] assets = new AssetEnum[]{
			STANCE_ARROW, MAIN_MENU_FG, MAIN_MENU_DK, MAIN_MENU_MC, MAIN_MENU_MG1_LEFT, MAIN_MENU_MG2_LEFT, MAIN_MENU_MG3_LEFT, MAIN_MENU_MG4_LEFT, MAIN_MENU_BG1, MAIN_MENU_BG2_LEFT, MAIN_MENU_MG1_RIGHT, MAIN_MENU_MG2_RIGHT, MAIN_MENU_MG3_RIGHT, MAIN_MENU_MG4_RIGHT, MAIN_MENU_BG2_RIGHT, MAIN_MENU_STATIONARY, TOA, ALPHA
		};
		for (AssetEnum asset: assets) {
			resourceRequirements.add(asset.getTexture());
		}
	}
	private final SaveService saveService;
	private final Skin skin; 
	private final Texture arrowImage;
	private final Sound buttonSound;
	private final Array<TextButton> buttons;
	private int selection;
	private Image arrow;
	private Image arrow2;
	private boolean cutScenePlayed;
	// background images for parallaxing
	private Image bg;
	private Image mg5;
	private Image mg4;
	private Image mg3;
	private Image mg2;
	private Image mg1;
	private Image bgright;
	private Image mg4right;
	private Image mg3right;
	private Image mg2right;
	private Image mg1right;
	private Image mc;
	private Image dk;
	private Image fg;
	private Group uiGroup;
	
	public MainMenuScreen(ScreenFactory factory, ScreenElements elements, SaveService saveService, LoadService loadService) {
		super(factory, elements, AssetEnum.MAIN_MENU_MUSIC);
		this.saveService = saveService;
		this.skin = assetManager.get(AssetEnum.UI_SKIN.getSkin());
		this.arrowImage = assetManager.get(AssetEnum.STANCE_ARROW.getTexture());
		this.buttonSound = assetManager.get(AssetEnum.BUTTON_SOUND.getSound());
		buttons = new Array<TextButton>();
		selection = 0;
		cutScenePlayed = false;
	}
	
	@Override
	public void buildMenu() {}		
	
	@Override
	public void buildStage() {		
		Image stationary = getImage(MAIN_MENU_STATIONARY);
		stationary.setPosition(0, 0);
		bg = getImage(MAIN_MENU_BG2_LEFT);
		bgright = getImage(MAIN_MENU_BG2_RIGHT);
		mg5 = getImage(MAIN_MENU_BG1);
		mg4 = getImage(MAIN_MENU_MG4_LEFT);
		mg4right = getImage(MAIN_MENU_MG4_RIGHT);
		mg3 = getImage(MAIN_MENU_MG3_LEFT);
		mg3right = getImage(MAIN_MENU_MG3_RIGHT);
		mg2 = getImage(MAIN_MENU_MG2_LEFT);
		mg2right = getImage(MAIN_MENU_MG2_RIGHT);
		mg1 = getImage(MAIN_MENU_MG1_LEFT);
		mg1right = getImage(MAIN_MENU_MG1_RIGHT);
		mc = getImage(MAIN_MENU_MC);
		dk = getImage(MAIN_MENU_DK);
		fg = getImage(MAIN_MENU_FG);
		
		move(bg, 200);
		move(bgright, 200, 1920, 0, false);
		move(mg5, 0, 920);
		move(mg4, 1170);
		move(mg4right, 1170, 1920, 0, false);
		move(mg3, 1420);
		move(mg3right, 1420, 1920, 0, false);
		move(mg2, 1670);
		move(mg2right, 1670, 1920, 0, false);
		move(mg1, mgScroll);
		move(mg1right, mgScroll, 1920, 0, false);
		move(mc, mgScroll, 1775, -75, false);
		move(dk, fgScroll, 4000, 0, false);
		move(fg, fgScroll);
		
		// build UI
		uiGroup = new Group();
		Table table = new Table();
		
		Array<String> buttonLabels = new Array<String>();
		Array<ScreenEnum> optionList = new Array<ScreenEnum>();
		buttonLabels.addAll("Begin", "Continue", "Load", "Options", "Progress", "Pervert", "Help", "Credits", "Exit");
		optionList.addAll(ScreenEnum.NEW_GAME, ScreenEnum.CONTINUE, ScreenEnum.SAVE, ScreenEnum.OPTIONS, ScreenEnum.PROGRESS, ScreenEnum.REPLAY, ScreenEnum.HELP, ScreenEnum.CREDITS, ScreenEnum.EXIT);
		
		for (int ii = 0; ii < buttonLabels.size; ii++) {
			buttons.add(new TextButton(buttonLabels.get(ii), skin));
			buttons.get(ii).addListener(getListener(optionList.get(ii), ii));
			table.add(buttons.get(ii)).size(180, 60).row();
		}
	        
        uiGroup.addActor(table);
        table.setPosition(1455, 750);
        
        arrow = new Image(arrowImage);
        arrow.setColor(Color.BROWN);
        arrow.setHeight(60);
        arrow.setWidth(30);
        uiGroup.addActor(arrow);
        TextureRegion flipped = new TextureRegion(arrowImage);
        flipped.flip(true, false);
        arrow2 = new Image(flipped);
        arrow2.setColor(Color.BROWN);
        arrow2.setHeight(60);
        arrow2.setWidth(30);
        uiGroup.addActor(arrow2);
        this.addListener(new ClickListener() { @Override public void clicked(InputEvent event, float x, float y) { if (!cutScenePlayed) finishCutScene(); }});
        
        arrow.addAction(Actions.forever(Actions.sequence(Actions.moveBy(-8, 0, 2), Actions.moveBy(8, 0, 2))));
        arrow2.addAction(Actions.forever(Actions.sequence(Actions.moveBy(8, 0, 2), Actions.moveBy(-8, 0, 2))));
        
        Label version = new Label(TalesOfAndrogyny.getVersion(), skin);
        version.setPosition(1400, 0);
        uiGroup.addActor(version);
        
        Image toa = new Image(assetManager.get(AssetEnum.TOA.getTexture()));
        toa.setPosition(1200, 50);
        uiGroup.addActor(toa);
        
        Image alphaBuild = new Image(assetManager.get(AssetEnum.ALPHA.getTexture()));
        alphaBuild.setPosition(600, 10);
        uiGroup.addActor(alphaBuild);
       
        activate(0);
        this.addActor(uiGroup);
       
        uiGroup.addAction(Actions.alpha(0));
        uiGroup.addAction(Actions.sequence(Actions.delay(INTRO), Actions.fadeIn(1), new Action() {
			@Override
			public boolean act(float delta) {
				cutScenePlayed = true;
				return true;
			}}));
	}
	
	private Image getImage(AssetEnum asset) {
		Image temp = new Image (assetManager.get(asset.getTexture()));
		this.addActor(temp);
		return temp;
	}
	private void move(Image toMove, int distance) { move(toMove, distance, 0, 0); }
	private void move(Image toMove, int distance, int startX) { move(toMove, distance, startX, 0); }
	private void move(Image toMove, int distance, int startX, int startY) { move(toMove, distance, startX, startY, true); }
	private void move(Image toMove, int distance, int startX, int startY, boolean start) { 
		toMove.setPosition(startX, startY);
		toMove.addAction(Actions.moveBy(-distance + (start ? -startX : 0), 0, INTRO));
	}
	
	private void finishCutScene() {
		bg.clearActions();
		bgright.clearActions();
		mg5.clearActions();
		mg4right.clearActions();
		mg3right.clearActions();
		mg2right.clearActions();
		mg1right.clearActions();
		mg4.clearActions();
		mg3.clearActions();
		mg2.clearActions();
		mg1.clearActions();
		mc.clearActions();
		dk.clearActions();
		fg.clearActions();
		bgright.setPosition(-200 + 1920, 0);
		mg4right.setPosition(-1170 + 1920, 0);
		mg3right.setPosition(-1420 + 1920, 0);
		mg2right.setPosition(-1670 + 1920, 0);
		mg1right.setPosition(-mgScroll + 1920, 0);
		bg.setPosition(-200, 0);
		mg5.setPosition(0, 0);
		mg4.setPosition(-1170, 0);
		mg3.setPosition(-1420, 0);
		mg2.setPosition(-1670, 0);
		mg1.setPosition(-mgScroll, 0);
		mc.setPosition(1775 - mgScroll, -75);
		dk.setPosition(4000 - fgScroll, 0);
		fg.setPosition(-fgScroll, 0);
		uiGroup.addAction(Actions.alpha(1));
		cutScenePlayed = true;
	}
	
	@Override
	public void render(float delta) {
		super.render(delta);
		OrthographicCamera camera = (OrthographicCamera) getCamera();
        batch.setTransformMatrix(camera.view);
        
        if(Gdx.input.isKeyJustPressed(Keys.UP)) {
        	if (selection > 0) setSelection(selection - 1);
        	else setSelection(buttons.size - 1);
        }
        else if(Gdx.input.isKeyJustPressed(Keys.DOWN)) {
        	if (selection < buttons.size- 1) setSelection(selection + 1);
        	else setSelection(0);
        }
        else if(Gdx.input.isKeyJustPressed(Keys.ENTER) || Gdx.input.isKeyJustPressed(Keys.SPACE)) {
        	if (cutScenePlayed) {
        		InputEvent event1 = new InputEvent();
                event1.setType(InputEvent.Type.touchDown);
                buttons.get(selection).fire(event1);

                InputEvent event2 = new InputEvent();
                event2.setType(InputEvent.Type.touchUp);
                buttons.get(selection).fire(event2);
        	}
        	else {
        		finishCutScene();
        	}
        }
	}

	private void setSelection(int newSelection) {
		if (newSelection == this.selection) return;
		deactivate(selection);
		activate(newSelection);
	}
	
	private void deactivate(int toDeactivate) {
		TextButton button = buttons.get(toDeactivate);
		button.setColor(Color.WHITE);
	}
	
	private void activate(int activate) {
		TextButton button = buttons.get(activate);
		button.setColor(Color.YELLOW);
		this.selection = activate;
		arrow.setPosition(1545, 960 - selection * 60);
		arrow2.setPosition(1335, 960 - selection * 60);
	}
	
	@Override
	public void dispose() {
		for(AssetDescriptor<?> path: resourceRequirements) {
			if (path.fileName.equals(AssetEnum.BUTTON_SOUND.getSound().fileName) || path.type == Music.class) continue;
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
        			saveService.saveDataValue(SaveEnum.ENCOUNTER_END, null);
        		}
	        	showScreen(screenSelection);    
	        }
	        @Override
	        public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
				setSelection(index);
	        }
	    };
	}
}
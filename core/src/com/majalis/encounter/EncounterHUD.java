package com.majalis.encounter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.OrderedMap;
import com.majalis.asset.AnimatedActor;
import com.majalis.asset.AssetEnum;
import com.majalis.character.HealthBar;
import com.majalis.character.LevelBar;
import com.majalis.character.PlayerCharacter;
import com.majalis.scenes.Scene;
import com.majalis.screens.TimeOfDay;
import com.majalis.screens.WorldMapScreen.FoodDisplay;

public class EncounterHUD extends Group {

	private final PlayerCharacter character;
	private final AssetManager assetManager;
	private final Group characterGroup;
	private final Group logGroup;
	private final LogDisplay logDisplay;
	private final TextButton showLog;
	private final TextButton copyLog;
	private final ScrollPane pane;
	private final Label skipText;
	private final TextButton hideButton;
	private final TextButton saveButton;
	private final TextButton skipButton;
	private final TextButton autoplayButton;
	private final Label dateLabel;
	private final Image characterPortrait;
	private final Image masculinityIcon;
	private final Skin skin;
	private boolean skipHeld;
	private boolean buttonsHidden;
	
	protected EncounterHUD(AssetManager assetManager, PlayerCharacter character, OrderedMap<Integer, Scene> masterSceneMap, IntArray sceneCodes, BitmapFont font) {
		this.character = character;
		this.assetManager = assetManager;
		skin = assetManager.get(AssetEnum.UI_SKIN.getSkin());
	
		logGroup = new Group();
		logDisplay = new LogDisplay(sceneCodes, masterSceneMap, skin);
		ScrollPaneStyle paneStyle = new ScrollPaneStyle();
		paneStyle.background = new TextureRegionDrawable(new TextureRegion(assetManager.get(AssetEnum.BASIC_BOX.getTexture())));
		pane = new ScrollPane(logDisplay, paneStyle);
		logDisplay.setAlignment(Align.topLeft);
		logDisplay.setWrap(true);
		logDisplay.setColor(Color.DARK_GRAY);
		pane.setScrollingDisabled(true, false);
		pane.setOverscroll(false, false);
		pane.setSize(1300, 950);
		pane.setPosition(325, 1000, Align.topLeft);
		showLog = new TextButton("Show Log", skin);
		copyLog = new TextButton("Copy Log", skin);
		this.addActor(copyLog);
		pane.addAction(Actions.hide());
		
		copyLog.addListener(new ClickListener() { 
			@Override
	        public void clicked(InputEvent event, float x, float y) {
				logDisplay.displayLog();
				Gdx.app.getClipboard().setContents(logDisplay.getText().toString());
			}
		});
		this.addActor(showLog);
		showLog.setBounds(425, 1000, 165, 50);
		copyLog.setBounds(590, 1000, 165, 50);
		
		this.addActor(logGroup);
		logGroup.addActor(pane);
		logGroup.addActor(showLog);
		skipText = addLabel("Press CTRL to skip", skin, font, Color.BLACK, 95, 180);
		skipText.setWidth(240);		
		hideButton = new TextButton("Hide", skin);
		saveButton = new TextButton("Save", skin);
		skipButton = new TextButton("Skip", skin);
		autoplayButton = new TextButton("Auto", skin);
		dateLabel = new DateLabel(character, skin);
		if (Gdx.app.getPreferences("tales-of-androgyny-preferences").getBoolean("autoplay", false)) autoplayButton.setColor(Color.YELLOW);

		dateLabel.setPosition(1650, 325);
		this.addActor(dateLabel);
		
		hideButton.setPosition(1650, 25);
		hideButton.setWidth(150);	
		this.addActor(hideButton);
		
		saveButton.setPosition(1650, 100);
		saveButton.setWidth(150);	
		this.addActor(saveButton);
		
		skipButton.setPosition(1650, 175);
		skipButton.setWidth(150);
		skipButton.addListener(
			new ClickListener() {
				@Override
		        public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
					skipHeld = false;	
					super.touchUp(event, x, y, pointer, button);
		        }
				
				@Override
		        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
					skipHeld = true;
					return super.touchDown(event, x, y, pointer, button);				
		        }
			}
		);	
		
		this.addActor(skipButton);
		
		autoplayButton.setPosition(1650, 250);
		autoplayButton.setWidth(150);
		autoplayButton.addListener(
			new ClickListener() {
				@Override
		        public void clicked(InputEvent event, float x, float y) {
					Gdx.app.getPreferences("tales-of-androgyny-preferences").putBoolean("autoplay", !Gdx.app.getPreferences("tales-of-androgyny-preferences").getBoolean("autoplay", false));
					if (Gdx.app.getPreferences("tales-of-androgyny-preferences").getBoolean("autoplay", false)) autoplayButton.setColor(Color.YELLOW);
					else autoplayButton.setColor(Color.WHITE);
		        }
			}
		);	
		this.addActor(autoplayButton);
	
		characterGroup = new Group();
		this.addActor(characterGroup);
		HealthBar healthBar = new HealthBar(character, assetManager, assetManager.get(AssetEnum.BATTLE_SKIN.getSkin()));
		healthBar.setPosition(25, 950);		
		characterGroup.addActor(healthBar);
		
		LevelBar levelBar = new LevelBar(character, assetManager, skin);
		levelBar.setPosition(-175, 825);
		characterGroup.addActor(levelBar);
		
		FoodDisplay foodDisplay = new FoodDisplay(character, assetManager, skin);
		foodDisplay.setPosition(15, 850);
		characterGroup.addActor(foodDisplay);
		
		Texture portrait = assetManager.get(character.getPortraitPath());
		characterPortrait = addImage(characterGroup, portrait, 105, 750, portrait.getWidth() / (portrait.getHeight() / 200f), 200);
		Texture icon = assetManager.get(character.getMasculinityPath());
		masculinityIcon = addImage(characterGroup, icon, 105, 655, icon.getWidth() / (icon.getHeight() / 100f), 100);
		((AnimatedActor) addActor(characterGroup, character.getBelly(assetManager))).setSkeletonPosition(68, 753);
	}	
	
	protected Label addLabel(String text, Skin skin, BitmapFont font, Color color, float x, float y) {
		Label newLabel = new Label(text, skin);
		newLabel.setColor(color);
		newLabel.setPosition(x, y);
		if (font != null) {
			Label.LabelStyle style = new Label.LabelStyle(newLabel.getStyle());
			style.font = font;
			newLabel.setStyle(style);
			newLabel.setWrap(true);
			newLabel.setAlignment(Align.top);
		}
		this.addActor(newLabel);
		return newLabel;
	}
	
	private Image addImage(Group group, Texture texture, float x, float y, float width, float height) {
		Image newImage = new Image(texture);
		newImage.setBounds(x, y, width, height);
		group.addActor(newImage);
		return newImage;
	}
	
	private Actor addActor(Group group, Actor actor) { group.addActor(actor); return actor; }
	
	public boolean isSkipHeld() { return skipHeld; }
	
	public void showButtons() { 
		if (character.isLoaded()) {
			characterGroup.addAction(Actions.show());
			characterPortrait.setDrawable(new TextureRegionDrawable(new TextureRegion(assetManager.get(character.getPortraitPath()))));
			masculinityIcon.setDrawable(new TextureRegionDrawable(new TextureRegion(assetManager.get(character.getMasculinityPath()))));
		}
		else {
			characterGroup.addAction(Actions.hide());
		}
		skipText.addAction(Actions.show());
		saveButton.addAction(Actions.show());
		skipButton.addAction(Actions.show());
		autoplayButton.addAction(Actions.show());
		hideButton.addAction(Actions.show());
		dateLabel.addAction(Actions.show());
		showLog.addAction(Actions.show());
		copyLog.addAction(Actions.show());
		buttonsHidden = false;
	}
	
	public void hideButtons() {
		characterGroup.addAction(Actions.hide());
		skipText.addAction(Actions.hide());
		saveButton.addAction(Actions.hide());
		skipButton.addAction(Actions.hide());
		autoplayButton.addAction(Actions.hide());
		hideButton.addAction(Actions.hide());
		dateLabel.addAction(Actions.hide());
		showLog.addAction(Actions.hide());
		copyLog.addAction(Actions.hide());
		buttonsHidden = true;
	}
	
	public TextButton getHideButton() { return hideButton; }
	public boolean buttonsVisible() { return saveButton.isVisible(); }
	public void toggleButtons() { if (buttonsHidden) showButtons(); else hideButtons(); }
	public void addSaveListener(ClickListener clickListener) { saveButton.addListener(clickListener); }
	public void showLog() { showLog.addAction(Actions.show()); }
	public Group getLog() { return logGroup; }
	public boolean displayingLog() { return pane.isVisible(); }
	public void toggleLog() { 
		logDisplay.displayLog(); 
		showLog.clearActions();
		showLog.addAction(Actions.show());
		if (displayingLog()) {
			pane.addAction(Actions.hide());
			showLog.setText("Show Log");
		}
		else {
			pane.addAction(Actions.show());
			showLog.setText("Hide Log");	
		}
	}

	private static class DateLabel extends Label {
		private final PlayerCharacter character;
		private DateLabel(PlayerCharacter character, Skin skin) {
			super("Day: " + (character.getTime() / 6 + 1) + "\n" + TimeOfDay.getTime(character.getTime()).getDisplay(), skin);
			this.character = character;
		}
		@Override
		public void draw(Batch batch, float parentAlpha) {
			setText("Day: " + (character.getTime() / 6 + 1) + "\n" + TimeOfDay.getTime(character.getTime()).getDisplay());
			setColor(TimeOfDay.getTime(character.getTime()).getColor());
			super.draw(batch, parentAlpha);
		}
	}
}

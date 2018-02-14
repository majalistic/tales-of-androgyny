package com.majalis.encounter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
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
import com.majalis.asset.AssetEnum;
import com.majalis.character.PlayerCharacter;
import com.majalis.scenes.Scene;
import com.majalis.screens.TimeOfDay;

public class EncounterHUD extends Group {

	private final Group logGroup;
	private final LogDisplay logDisplay;
	private final Label showLog;
	private final ScrollPane pane;
	private final TextButton saveButton;
	private final TextButton skipButton;
	private final TextButton autoplayButton;
	private final Label dateLabel;
	private final Skin skin;
	private boolean skipHeld;
	private boolean buttonsHidden;
	
	protected EncounterHUD(AssetManager assetManager, PlayerCharacter character, OrderedMap<Integer, Scene> masterSceneMap, IntArray sceneCodes) {
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
		showLog = new Label("Show Log", skin);
		pane.addAction(Actions.hide());
		
		this.addActor(showLog);
		
		showLog.setColor(Color.BLACK);
		showLog.setPosition(325, 1000);
		
		this.addActor(logGroup);
		logGroup.addActor(pane);
		logGroup.addActor(showLog);
		
		saveButton = new TextButton("Save", skin);
		skipButton = new TextButton("Skip", skin);
		autoplayButton = new TextButton("Auto", skin);
		dateLabel = new DateLabel(character);
		if (Gdx.app.getPreferences("tales-of-androgyny-preferences").getBoolean("autoplay", false)) autoplayButton.setColor(Color.YELLOW);

		dateLabel.setPosition(1650, 325);
		this.addActor(dateLabel);
		
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
	}	
	
	public boolean isSkipHeld() { return skipHeld; }
	
	public void showButtons() { 
		saveButton.addAction(Actions.show());
		skipButton.addAction(Actions.show());
		autoplayButton.addAction(Actions.show());
		dateLabel.addAction(Actions.show());
		buttonsHidden = false;
	}
	
	public void hideButtons() {
		saveButton.addAction(Actions.hide());
		skipButton.addAction(Actions.hide());
		autoplayButton.addAction(Actions.hide());
		dateLabel.addAction(Actions.hide());
		buttonsHidden = true;
	}
	
	public void toggleButtons() { if (buttonsHidden) showButtons(); else hideButtons(); }
	
	private class DateLabel extends Label {
		private final PlayerCharacter character;
		private DateLabel(PlayerCharacter character) {
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
	public void addSaveListener(ClickListener clickListener) { saveButton.addListener(clickListener); }
	public Group getLog() { return logGroup; }

	public boolean displayingLog() {
		return pane.isVisible();
	}
	
	public void toggleLog() { 
		logDisplay.displayLog(); 
		if (displayingLog()) {
			pane.addAction(Actions.hide());
			showLog.setText("Show Log");
		}
		else {
			pane.addAction(Actions.show());
			showLog.setText("Hide Log");	
		}
	}
}

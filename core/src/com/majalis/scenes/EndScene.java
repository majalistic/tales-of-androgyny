package com.majalis.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.majalis.asset.AssetEnum;
import com.majalis.encounter.Background;
import com.majalis.encounter.LogDisplay;
import com.majalis.save.MutationResult;
import com.majalis.save.SaveEnum;
import com.majalis.save.SaveManager;
import com.majalis.save.SaveService;

/*
 * Represents a scene that concludes an encounter.  Currently not displayed; may eventually be displayed.  Should fire an event that the encounter has ended
 */
public class EndScene extends Scene {

	private final Type type;
	private final SaveService saveService;
	private final SaveManager.GameContext context;
	private final Background background;
	private final Array<MutationResult> results;
	private final AssetManager assetManager;
	private final Table statusResults;
	private final Label showLog;
	private final LogDisplay log;
	private final ScrollPane pane;
	private final Skin skin;
	private final Group group;
	private boolean finished;
	public EndScene(int sceneCode, Type type, SaveService saveService, AssetManager assetManager, SaveManager.GameContext context, final Background background, LogDisplay log, Array<MutationResult> results) {
		super(null, sceneCode);
		this.type = type;
		this.saveService = saveService;
		this.assetManager = assetManager;
		this.context = context;
		this.background = background;
		this.results = results;
		this.addActor(background);
		pane = new ScrollPane(log);
		this.log = log;
		log.setAlignment(Align.topLeft);
		log.setWrap(true);
		pane.setScrollingDisabled(true, false);
		pane.setOverscroll(false, false);
		pane.setBounds(325, 350, 1300, 650);
		log.setColor(Color.BLACK);
		statusResults = new Table();
		skin = assetManager.get(AssetEnum.UI_SKIN.getSkin());
		showLog = new Label("Show Log", skin);
		
		this.addActor(showLog);
		
		showLog.setColor(Color.BLACK);
		showLog.setPosition(325, 1000);
		showLog.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				toggleLogDisplay();
			}
		});
		pane.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				toggleLogDisplay();
			}
		});
		group = new Group();
		finished = false;
	}
	
	private void toggleLogDisplay() {
		log.displayLog();
		if (showLog.getText().toString().equals("Show Log")) {
			showLog.setText("Hide Log");
			this.addActor(pane);		
			background.toggleDialogBox(background.getDialogBox(), false);
			this.removeActor(group);
		}
		else {
			showLog.setText("Show Log");
			this.addActor(group);
			background.toggleDialogBox(background.getDialogBox(), true);
			this.removeActor(pane);
		}
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		if (Gdx.input.isKeyJustPressed(Keys.ENTER) || Gdx.input.isKeyJustPressed(Keys.SPACE)) {
			finish();
		}
	}

	public Type getType() {
		return type;
	}
	
	@Override
	public void setActive() {
		if (results.size == 0 || type == Type.GAME_OVER) {
			finish();
			return;
		}
		isActive = true;
		saveService.saveDataValue(SaveEnum.SCENE_CODE, sceneCode);
		this.removeAction(Actions.hide());
		this.addAction(Actions.visible(true));
		this.addAction(Actions.show());
		
		background.addListener(new ClickListener() { 
			@Override
	        public void clicked(InputEvent event, float x, float y) {
				finish();
			}
		});
		// need to display "Results" at the top of the table, move table into the appropriate box, and a "click to continue" box or something
		statusResults.align(Align.topLeft);
		Label newLabel = new Label("Results: ", skin);
		newLabel.setColor(Color.BLACK);
		statusResults.add(newLabel).fillY().align(Align.left).row();
		Array<MutationResult> compactedResults = MutationResult.collapse(results); 
		for (MutationResult result : compactedResults) {
			statusResults.add(new MutationActor(result, assetManager.get(result.getTexture()), skin, true)).fillY().padLeft(50).align(Align.left).row();
		}
		group.addActor(statusResults);
		Label clickToContinue = new Label("Click to continue... ", skin);
		clickToContinue.setColor(Color.BLACK);
		group.addActor(clickToContinue);
		clickToContinue.setPosition(1200, 175);
		statusResults.setPosition(600, 750);
		this.addActor(group);	
		group.addListener(new ClickListener() { 
			@Override
	        public void clicked(InputEvent event, float x, float y) {
				finish();
			}
		});
	}
	
	public boolean isFinished() {
		return finished;
	}
	
	private void finish() {
		saveService.saveDataValue(SaveEnum.CONTEXT, context);
		if (type == Type.ENCOUNTER_OVER || type == Type.GAME_OVER) {
			saveService.saveDataValue(SaveEnum.RETURN_CONTEXT, null);
			saveService.saveDataValue(SaveEnum.ENCOUNTER_END, null);
		}
		finished = true;
	}

	@Override
	public boolean isActive() {
		return isActive;
	}
	
	public enum Type {
		ENCOUNTER_OVER,
		GAME_OVER
	}
}

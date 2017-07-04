package com.majalis.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.OrderedMap;
import com.majalis.encounter.Background;
import com.majalis.encounter.EncounterBuilder.BranchChoice;
import com.majalis.encounter.EncounterBuilder.ChoiceCheckType;
import com.majalis.save.SaveService;
import com.majalis.talesofandrogyny.TalesOfAndrogyny;
/*
 * Represents a choice displayed to the user in the course of an encounter.
 */
public class GameTypeScene extends AbstractChoiceScene {
	private final Array<TextButton> buttons;
	private int selection;
	public GameTypeScene(OrderedMap<Integer, Scene> sceneBranches, int sceneCode, SaveService saveService, Array<BranchChoice> choices, Background background) {
		super(sceneBranches, sceneCode, saveService);
		this.buttons = new Array<TextButton>();
		this.addActor(background);
        // may need to add the background as an actor
		int ii = 0;
        for (BranchChoice choice : choices) {
        	TextButton button = choice.button;
        	this.addActor(button);
        	this.buttons.add(button);
        	button.setSize(345, 90);
        	button.addListener(getListener(ii++, choice.scene, choice.clickSound, choice.require));
        } 
        buttons.get(0).setPosition(1515, 380);
        buttons.get(1).setPosition(90, 380);	
        	
        selection = 1;
        selected(0);
        if(!TalesOfAndrogyny.patron) { // this should be refactored to just be another choicechecktype that checks for TalesOfAndrogyny.patron
        	buttons.get(1).setTouchable(Touchable.disabled);
        	buttons.get(1).setColor(Color.GRAY);
        	this.buttons.removeIndex(1);
        }
	}
	
	private ClickListener getListener(final int index, final Scene nextScene, final Sound buttonSound, final ChoiceCheckType type) {
		return new ClickListener() {
	        @Override
	        public void clicked(InputEvent event, float x, float y) {
        		buttonSound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
	        	// set new Scene as active based on choice
	        	nextScene.setActive();
	        	finish();
	        }
	        @Override
	        public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
				selected(index);
	        }
	    };
	}

	private void selected(int selection) {
		TextButton button = buttons.get(this.selection);
	 	button.setColor(Color.WHITE);
	 	button = buttons.get(selection);
	 	button.setColor(Color.FOREST);		
	 	this.selection = selection;
	}
	
	@Override
    public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		 if(Gdx.input.isKeyJustPressed(Keys.LEFT)) {
	        	if (selection > 0) selected(selection - 1);
	        	else selected(buttons.size - 1);
	        }
	        else if(Gdx.input.isKeyJustPressed(Keys.RIGHT)) {
	        	if (selection < buttons.size- 1) selected(selection + 1);
	        	else selected(0);
	        }
	        else if(Gdx.input.isKeyJustPressed(Keys.ENTER)) {
	        	InputEvent event1 = new InputEvent();
	            event1.setType(InputEvent.Type.touchDown);
	            buttons.get(selection).fire(event1);

	            InputEvent event2 = new InputEvent();
	            event2.setType(InputEvent.Type.touchUp);
	            buttons.get(selection).fire(event2);
	        }
    }
	
	public int getCode() {
		return sceneCode;
	}
	
	public void finish() {
		isActive = false;
    	addAction(Actions.hide());
	}
}

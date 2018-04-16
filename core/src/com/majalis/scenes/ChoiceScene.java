package com.majalis.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.OrderedMap;
import com.majalis.character.PlayerCharacter;
import com.majalis.encounter.Background;
import com.majalis.encounter.EncounterHUD;
import com.majalis.encounter.EncounterBuilder.BranchChoice;
import com.majalis.save.SaveService;
import com.majalis.screens.TimeOfDay;
/*
 * Represents a choice displayed to the user in the course of an encounter.
 */
public class ChoiceScene extends AbstractChoiceScene {

	private final BitmapFont font;
	private final String choiceDialogue;
	private final Array<TextButton> buttons;
	private final Array<BranchChoice> choices;
	private final Texture arrowImage;
	private final PlayerCharacter character;
	private final Background background;
	private final int maxLength;
	private int selection;
	// this should receive a map of integers to choice buttons 
	public ChoiceScene(OrderedMap<Integer, Scene> sceneBranches, int sceneCode, SaveService saveService, BitmapFont font, String choiceDialogue, Array<BranchChoice> choices, Texture arrowImage, PlayerCharacter character, Background background, EncounterHUD hud) {
		super(sceneBranches, sceneCode, saveService, hud);
		this.font = font;
		this.choices = choices;
		this.buttons = new Array<TextButton>();
		this.arrowImage = arrowImage;
		this.character = character;
		this.addActor(background);
		Table table = new Table();
		int ii = 0;
		int maxLengthTemp = 0;
		for (BranchChoice choice: choices) {
			int length = 50 + choice.button.getText().length() * 20;
			if (length > maxLengthTemp) maxLengthTemp = length;
		}
		maxLength = maxLengthTemp;
		
		for (BranchChoice choice: choices) {
			TextButton button = choice.button;
			buttons.add(button);
			button.addListener(getListener(ii++, choice.scene, choice.clickSound));
			table.add(button).size(maxLength, 125).row();	
		}	
        table.setPosition(960, 900);
        table.align(Align.top);
        // may need to add the background as an actor
        this.addActor(table);
        this.choiceDialogue = choiceDialogue;
        this.background = background;
		selection = 0;
	}

	@Override
	public void activate() {
		super.activate();
		for (BranchChoice choice : choices) {
			if (choice.require != null && !choice.require.isValidChoice(character)) {
				choice.button.setTouchable(Touchable.disabled);
				choice.button.setColor(Color.GRAY);
			}
		}
		background.setColor(TimeOfDay.getTime(character.getTime()).getColor());
	}
	
	private ClickListener getListener(final int index, final Scene nextScene, final Sound buttonSound) {
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
				selection = index;
	        }
	    };
	}
	
	@Override
    public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		font.setColor(0.5f,0.4f,0,1);
		font.draw(batch, choiceDialogue, 600, 1025, 620, Align.center, true);
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
		boolean hasTouchable = false;
		for (TextButton button : buttons) {
			if (button.isTouchable()) {
				hasTouchable = true;
				break;
			}
		}
		while (!buttons.get(selection).isTouchable() && hasTouchable) {
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
	        else {
	        	if (selection < buttons.size- 1) selection++;
	        	else selection = 0;
	        }
		}
		batch.draw(arrowImage, 840 - maxLength / 2, 775 - selection * 125, 100, 125);
    }
	@Override
	public String getText() {
		return "Choice: " + choiceDialogue + "\n" + getChoicesLabel();
	}
	private String getChoicesLabel() {
		String temp = "";
		for (BranchChoice choice : choices) {
			temp += "> " + choice.button.getText() + "\n";
		}
		return temp.trim();
	}
}

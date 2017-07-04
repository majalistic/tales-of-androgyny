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
import com.majalis.encounter.EncounterBuilder.BranchChoice;
import com.majalis.encounter.EncounterBuilder.ChoiceCheckType;
import com.majalis.save.SaveService;
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
	private int selection;
	// this should receive a map of integers to choice buttons 
	public ChoiceScene(OrderedMap<Integer, Scene> sceneBranches, int sceneCode, SaveService saveService, BitmapFont font, String choiceDialogue, Array<BranchChoice> choices, Texture arrowImage, PlayerCharacter character, Background background) {
		super(sceneBranches, sceneCode, saveService);
		this.font = font;
		this.choices = choices;
		this.buttons = new Array<TextButton>();
		this.arrowImage = arrowImage;
		this.character = character;
		this.addActor(background);
		Table table = new Table();
		int ii = 0;
		for (BranchChoice choice: choices) {
			TextButton button = choice.button;
			buttons.add(button);
			button.addListener(getListener(ii++, choice.scene, choice.clickSound, choice.require));
			table.add(button).size(665, 150).row();	
		}	
        table.setPosition(960, 775);
        table.align(Align.top);
        // may need to add the background as an actor
        this.addActor(table);
        this.choiceDialogue = choiceDialogue;
		selection = 0;
	}

	@Override
	public void setActive() {
		super.setActive();
		for (BranchChoice choice : choices) {
			if (!isValidChoice(choice.require)) {
				choice.button.setTouchable(Touchable.disabled);
				choice.button.setColor(Color.GRAY);
				//buttons.removeValue(choice.button, true);
			}
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
				selection = index;
	        }
	    };
	}
	
	private boolean isValidChoice(ChoiceCheckType type) {
		if (type == null) return true;
		switch (type) {
			case LEWD:
				return character.isLewd();
			case GOLD_GREATER_THAN_25:
				return character.getMoney() >= 25;
			case GOLD_GREATER_THAN_10:
				return character.getMoney() >= 10;
			case GOLD_LESS_THAN_10:
				return character.getMoney() < 10;
			default:
				return false;
		}
	}
	
	@Override
    public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		font.setColor(0.5f,0.4f,0,1);
		font.draw(batch, choiceDialogue, 600, 900, 620, Align.center, true);
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
		while (!buttons.get(selection).isTouchable()) {
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
		batch.draw(arrowImage, 525, 625 - selection * 150, 100, 150);
    }
	
	public int getCode(){
		return sceneCode;
	}
}

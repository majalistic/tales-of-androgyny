package com.majalis.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.OrderedMap;
import com.majalis.encounter.Background;
import com.majalis.save.SaveService;
/*
 * Represents a choice displayed to the user in the course of an encounter.
 */
public class ChoiceScene extends AbstractChoiceScene {

	private final BitmapFont font;
	private final String choiceDialogue;
	private final Array<TextButton> buttons;
	private final Texture arrowImage;
	private int selection;
	// this should receive a map of integers to choice buttons 
	public ChoiceScene(OrderedMap<Integer, Scene> sceneBranches, int sceneCode, SaveService saveService, BitmapFont font, String choiceDialogue, Array<TextButton> buttons, Texture arrowImage, Background background) {
		super(sceneBranches, sceneCode, saveService);
		this.font = font;
		this.buttons = buttons;
		this.arrowImage = arrowImage;
		this.addActor(background);
		Table table = new Table();
		int ii = 0;
		for (TextButton button: buttons) {
			button.addListener(getListener(ii++));
			table.add(button).size(665, 150).row();	
		}	
        table.setPosition(960, 775);
        table.align(Align.top);
        // may need to add the background as an actor
        this.addActor(table);
        this.choiceDialogue = choiceDialogue;
		selection = 0;
	}

	private ClickListener getListener(final int index) {
		return new ClickListener() {
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
		batch.draw(arrowImage, 525, 625 - selection * 150, 100, 150);
    }
	
	public int getCode(){
		return sceneCode;
	}
}

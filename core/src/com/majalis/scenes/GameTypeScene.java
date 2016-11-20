package com.majalis.scenes;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.OrderedMap;
import com.majalis.asset.AssetEnum;
import com.majalis.encounter.Background;
import com.majalis.save.SaveService;
/*
 * Represents a choice displayed to the user in the course of an encounter.
 */
public class GameTypeScene extends AbstractChoiceScene {

	public static final ObjectMap<String, Class<?>> resourceRequirements = new ObjectMap<String, Class<?>>();
	static {
		resourceRequirements.put(AssetEnum.UI_SKIN.getPath(), Skin.class);
		resourceRequirements.put(AssetEnum.BUTTON_SOUND.getPath(), Sound.class);
		resourceRequirements.put(AssetEnum.GAME_TYPE_BACKGROUND.getPath(), Texture.class);
	}
	// this should receive a map of integers to choice buttons 
	public GameTypeScene(OrderedMap<Integer, Scene> sceneBranches, int sceneCode, SaveService saveService, Array<TextButton> buttons, Background background) {
		super(sceneBranches, sceneCode, saveService);
		this.addActor(background);
        // may need to add the background as an actor
        for (TextButton button : buttons){
        	this.addActor(button);
        } 
        for(TextButton button : buttons){
        	button.setHeight(36);
        	button.setWidth(184);
        }
        buttons.get(0).addAction(Actions.moveTo(1030, 240));
        buttons.get(1).addAction(Actions.moveTo(78, 240));	
        buttons.get(1).setTouchable(Touchable.disabled);
	}

	@Override
    public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
    }
	
	public int getCode(){
		return sceneCode;
	}
	
	public void finish(){
		isActive = false;
    	addAction(Actions.hide());
	}
}

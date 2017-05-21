package com.majalis.scenes;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.OrderedMap;
import com.majalis.asset.AssetEnum;
import com.majalis.encounter.Background;
import com.majalis.save.SaveService;
import com.majalis.talesofandrogyny.TalesOfAndrogyny;
/*
 * Represents a choice displayed to the user in the course of an encounter.
 */
public class GameTypeScene extends AbstractChoiceScene {

	public static final Array<AssetDescriptor<?>> resourceRequirements = new Array<AssetDescriptor<?>>();
	static {
		resourceRequirements.add(AssetEnum.UI_SKIN.getSkin());
		resourceRequirements.add(AssetEnum.BUTTON_SOUND.getSound());
		resourceRequirements.add(AssetEnum.GAME_TYPE_BACKGROUND.getTexture());
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
        	button.setSize(345, 90);
        }
        buttons.get(0).setPosition(1515, 380);
        buttons.get(1).setPosition(90, 380);	
        if(!TalesOfAndrogyny.patron)
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

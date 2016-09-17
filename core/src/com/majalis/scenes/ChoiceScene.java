package com.majalis.scenes;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.OrderedMap;
import com.majalis.save.SaveEnum;
import com.majalis.save.SaveService;
/*
 * Represents a choice displayed to the user in the course of an encounter.
 */
public class ChoiceScene extends Scene {

	public static final ObjectMap<String, Class<?>> resourceRequirements = new ObjectMap<String, Class<?>>();
	static {
		resourceRequirements.put("uiskin.json", Skin.class);
		resourceRequirements.put("sound.wav", Sound.class);
	}
	
	private final int sceneCode;
	private final SaveService saveService;
	private final BitmapFont font;
	private final String choiceDialogue;
	// this should receive a map of integers to choice buttons 
	public ChoiceScene(OrderedMap<Integer, Scene> sceneBranches, int sceneCode, SaveService saveService, BitmapFont font, String choiceDialogue, Table table) {
		super(sceneBranches);
		this.sceneCode = sceneCode;
		this.saveService = saveService;
		this.font = font;
		
        table.setFillParent(true);
        table.addAction(Actions.moveTo(640, 400));
        this.addActor(table);
        
        this.choiceDialogue = choiceDialogue;
		
	}

	@Override
    public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		font.setColor(0.5f,0.4f,0,1);
		font.draw(batch, choiceDialogue, 600, 600);
    }
	
	public int getCode(){
		return sceneCode;
	}
	
	@Override
	public void setActive() {
		isActive = true;	
		this.removeAction(Actions.hide());
		this.addAction(Actions.visible(true));
		this.addAction(Actions.show());
		saveService.saveDataValue(SaveEnum.SCENE_CODE, sceneCode);
	}
	
	public void finish(){
		isActive = false;
    	addAction(Actions.hide());
	}
}

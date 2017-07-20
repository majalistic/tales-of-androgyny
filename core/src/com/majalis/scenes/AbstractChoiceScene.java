package com.majalis.scenes;

import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.OrderedMap;
import com.majalis.save.SaveEnum;
import com.majalis.save.SaveService;

public abstract class AbstractChoiceScene extends Scene {
	private final SaveService saveService;
		
	protected AbstractChoiceScene(OrderedMap<Integer, Scene> sceneBranches, int sceneCode, SaveService saveService) {
		super(sceneBranches, sceneCode);
		this.saveService = saveService;
	}

	@Override
	public void setActive() {
		isActive = true;	
		this.removeAction(Actions.hide());
		this.addAction(Actions.visible(true));
		this.addAction(Actions.show());
		saveService.saveDataValue(SaveEnum.SCENE_CODE, sceneCode);
	}
	
	public void finish() {
		isActive = false;
    	addAction(Actions.hide());
	}
}

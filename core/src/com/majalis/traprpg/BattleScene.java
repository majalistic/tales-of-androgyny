package com.majalis.traprpg;

import com.badlogic.gdx.utils.ObjectMap;

public class BattleScene extends Scene {

	private final SaveService saveService;
	private final int battleCode;
	private final int victoryScene;
	private final int defeatScene;
	
	protected BattleScene(ObjectMap<Integer, Scene> sceneBranches, SaveService saveService, int battleCode, int victoryScene, int defeatScene) {
		super(sceneBranches);
		this.saveService = saveService;
		this.battleCode = battleCode;
		this.victoryScene = victoryScene;
		this.defeatScene = defeatScene;
		
	}

	@Override
	protected void setActive() {
		isActive = true;
		saveService.saveDataValue("BattleCode", new BattleCode(battleCode, victoryScene, defeatScene));
	}
}

package com.majalis.scenes;

import com.badlogic.gdx.utils.ObjectMap;
import com.majalis.battle.BattleCode;
import com.majalis.save.SaveEnum;
import com.majalis.save.SaveService;

public class BattleScene extends Scene {

	private final SaveService saveService;
	private final int battleCode;
	private final int victoryScene;
	private final int defeatScene;
	
	public BattleScene(ObjectMap<Integer, Scene> sceneBranches, SaveService saveService, int battleCode, int victoryScene, int defeatScene) {
		super(sceneBranches);
		this.saveService = saveService;
		this.battleCode = battleCode;
		this.victoryScene = victoryScene;
		this.defeatScene = defeatScene;
		
	}

	@Override
	public void setActive() {
		isActive = true;
		saveService.saveDataValue(SaveEnum.BATTLE_CODE, new BattleCode(battleCode, victoryScene, defeatScene));
	}
}

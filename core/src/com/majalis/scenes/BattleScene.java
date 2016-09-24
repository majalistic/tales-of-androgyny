package com.majalis.scenes;

import com.badlogic.gdx.utils.OrderedMap;
import com.majalis.battle.BattleCode;
import com.majalis.character.AbstractCharacter.Stance;
import com.majalis.save.SaveEnum;
import com.majalis.save.SaveService;
/*
 * Represents a scene that needs to cut to a battle.
 */
public class BattleScene extends Scene {

	private final SaveService saveService;
	private final int battleCode;
	private final int victoryScene;
	private final int defeatScene;
	private final Stance playerStance;
	private final Stance enemyStance;
	
	public BattleScene(OrderedMap<Integer, Scene> sceneBranches, int sceneCode, SaveService saveService, int battleCode) {
		this(sceneBranches, sceneCode, saveService, battleCode, Stance.BALANCED, Stance.BALANCED);
	}

	public BattleScene(OrderedMap<Integer, Scene> sceneBranches, int sceneCode, SaveService saveService, int battleCode, Stance playerStance, Stance enemyStance) {
		super(sceneBranches, sceneCode);
		this.saveService = saveService;
		this.battleCode = battleCode;
		this.victoryScene = sceneBranches.get(sceneBranches.orderedKeys().get(0)).getCode();
		this.defeatScene = sceneBranches.get(sceneBranches.orderedKeys().get(1)).getCode();
		this.playerStance = playerStance;
		this.enemyStance = enemyStance;
	}
	
	@Override
	public void setActive() {
		isActive = true;
		saveService.saveDataValue(SaveEnum.BATTLE_CODE, new BattleCode(battleCode, victoryScene, defeatScene, playerStance, enemyStance));
	}
}

package com.majalis.scenes;

import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.OrderedMap;
import com.majalis.battle.BattleCode;
import com.majalis.character.AbstractCharacter.Stance;
import com.majalis.save.SaveEnum;
import com.majalis.save.SaveManager;
import com.majalis.save.SaveService;
/*
 * Represents a scene that needs to cut to a battle.
 */
public class BattleScene extends Scene {

	private final SaveService saveService;
	private final int battleCode;
	private final ObjectMap<String, Integer> outcomes;
	private final Stance playerStance;
	private final Stance enemyStance;
	private final boolean disarm;
	private final int climaxCounter;
	
	public BattleScene(OrderedMap<Integer, Scene> sceneBranches, SaveService saveService, int battleCode, Stance playerStance, Stance enemyStance, boolean disarm, int climaxCounter, ObjectMap<String, Integer> outcomes) {
		super(sceneBranches, -1);
		this.saveService = saveService;
		this.battleCode = battleCode;
		this.playerStance = playerStance;
		this.enemyStance = enemyStance;
		this.outcomes = outcomes;
		this.disarm = disarm;
		this.climaxCounter = climaxCounter;
	}
	
	
	@Override
	public void setActive() {
		isActive = true;
		saveService.saveDataValue(SaveEnum.BATTLE_CODE, new BattleCode(battleCode, outcomes, playerStance, enemyStance, disarm, climaxCounter));
		saveService.saveDataValue(SaveEnum.CONTEXT, SaveManager.GameContext.BATTLE);
	}
}

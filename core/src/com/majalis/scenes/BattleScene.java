package com.majalis.scenes;

import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.OrderedMap;
import com.majalis.battle.BattleAttributes;
import com.majalis.battle.BattleCode;
import com.majalis.character.Stance;
import com.majalis.encounter.EncounterHUD;
import com.majalis.save.SaveEnum;
import com.majalis.save.SaveManager;
import com.majalis.save.SaveService;
/*
 * Represents a scene that needs to cut to a battle.
 */
public class BattleScene extends Scene {

	private final SaveService saveService;
	private final BattleCode battleCode;
	private final ObjectMap<String, Integer> outcomes;
	private final Stance playerStance;
	private final Stance enemyStance;
	private final boolean disarm;
	private final int climaxCounter;
	
	public BattleScene(OrderedMap<Integer, Scene> sceneBranches, SaveService saveService, BattleCode battleCode, Stance playerStance, Stance enemyStance, boolean disarm, int climaxCounter, ObjectMap<String, Integer> outcomes, EncounterHUD hud) {
		super(sceneBranches, -1, hud);
		this.saveService = saveService;
		this.battleCode = battleCode;
		this.playerStance = playerStance;
		this.enemyStance = enemyStance;
		this.outcomes = outcomes;
		this.disarm = disarm;
		this.climaxCounter = climaxCounter;
	}
	
	@Override
	public void activate() {
		isActive = true;
		saveService.saveDataValue(SaveEnum.BATTLE_CODE, new BattleAttributes(battleCode, outcomes, playerStance, enemyStance, disarm, climaxCounter));
		saveService.saveDataValue(SaveEnum.CONTEXT, SaveManager.GameContext.BATTLE);
	}
	
	@Override
	public boolean isBattle() { return true; }
}

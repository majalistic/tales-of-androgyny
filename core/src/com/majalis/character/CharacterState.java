package com.majalis.character;

import com.badlogic.gdx.utils.ObjectMap;
import com.majalis.character.AbstractCharacter.Stance;
import com.majalis.character.AbstractCharacter.Stat;
import com.majalis.character.Item.Weapon;

public class CharacterState {

	private final ObjectMap<Stat, Integer> stats;
	private final ObjectMap<Stat, Integer> rawStats;
	private final Weapon weapon;
	private final boolean lowBalance;
	private final int currentMana;
	private final AbstractCharacter target;
	
	// will likely need stats with and without stepdowns, or will need to implement stepdown here as well
	public CharacterState(ObjectMap<Stat, Integer> stats, ObjectMap<Stat, Integer> rawStats, Weapon weapon, boolean lowBalance, int currentMana, AbstractCharacter target) {
		this.stats = stats;
		this.rawStats = rawStats;
		this.weapon = weapon;
		this.lowBalance = lowBalance;
		this.currentMana = currentMana;
		this.target = target;
	}

	public int getStat(Stat stat) {
		return stats.get(stat);
	}
	
	public int getRawStat(Stat stat) {
		return rawStats.get(stat);
	}
	
	public ObjectMap<Stat, Integer> getStats() {
		return stats;
	}
	
	public Weapon getWeapon() {
		return weapon;
	}
	
	public boolean lowBalance() {
		return lowBalance;
	}
	
	public int getMana() {
		return currentMana;
	}
	
	/* //for character effects that would give + guardMod
	public int getGuardMod() {
		// TODO Auto-generated method stub
		return 0;
	}*/

	public boolean getEnemyLowStability() {
		if (target != null) return target.lowStability();
		return false;
	}

	public boolean isEnemyOnGround() {
		if (target != null) return target.getStance() == Stance.SUPINE || target.getStance() == Stance.PRONE;
		return false;
	}
	
	
}

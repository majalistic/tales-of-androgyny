package com.majalis.character;

import com.badlogic.gdx.utils.ObjectMap;
import com.majalis.character.Stance;
import com.majalis.character.AbstractCharacter.PhallusType;
import com.majalis.character.AbstractCharacter.Stat;
import com.majalis.character.Item.Weapon;

public class CharacterState {

	private final ObjectMap<Stat, Integer> stats;
	private final ObjectMap<Stat, Integer> rawStats;
	private final Weapon weapon;
	private final Weapon rangedWeapon;
	private final Armor shield;
	private final boolean lowBalance;
	private final int currentMana;
	private final boolean enemyLowStability;
	private final boolean enemyOnGround;
	private final boolean isCorporeal;
	private final PhallusType phallusType;
	private final GrappleStatus grappleStatus;
	private final AbstractCharacter user;
	
	// will likely need stats with and without stepdowns, or will need to implement stepdown here as well
	protected CharacterState(ObjectMap<Stat, Integer> stats, ObjectMap<Stat, Integer> rawStats, Weapon weapon, Weapon rangedWeapon, Armor shield, boolean lowBalance, int currentMana, boolean isCorporeal, PhallusType phallusType, AbstractCharacter user, AbstractCharacter target) {
		this.stats = stats;
		this.rawStats = rawStats;
		this.weapon = weapon;
		this.rangedWeapon = rangedWeapon;
		this.shield = shield;
		this.lowBalance = lowBalance;
		this.currentMana = currentMana;
		if (target != null) {
			enemyLowStability = target.lowStability();
			enemyOnGround = target.getStance() == Stance.SUPINE || target.getStance() == Stance.PRONE;
		}
		else {
			enemyLowStability = false;
			enemyOnGround = false;
		}
		this.grappleStatus = user.getGrappleStatus();
		this.isCorporeal = isCorporeal;
		this.phallusType = phallusType;
		this.user = user;
	}

	protected int getStat(Stat stat) { return stats.get(stat); }
	
	protected int getRawStat(Stat stat) { return rawStats.get(stat); }
	
	protected ObjectMap<Stat, Integer> getStats() { return stats; }
	
	protected Weapon getWeapon() { return weapon; }
	
	protected Weapon getRangedWeapon() { return rangedWeapon; }
	
	protected int getShieldScore() { return shield != null ? shield.getShockAbsorption() : 0; }
	
	protected boolean lowBalance() { return lowBalance; }
	
	protected int getMana() { return currentMana; }

	protected boolean getEnemyLowStability() { return enemyLowStability; }

	protected boolean isEnemyOnGround() { return enemyOnGround; }

	protected GrappleStatus getGrappleStatus() { return grappleStatus; }
	
	protected PhallusType getPhallusType() { return phallusType; }
	
	protected AbstractCharacter getCharacter() { return user; }

	protected boolean isCorporeal() { return isCorporeal; }

	protected int getLewdCharisma() { return user.getLewdCharisma(); }

	public ObjectMap<Perk, Integer> getPerks() { return user.getPerks(); }

	public int getRange() { return user.getRange();	}

	public int getGrappleMod() { return user.winsGrapples() ? 15 : 0; }
}

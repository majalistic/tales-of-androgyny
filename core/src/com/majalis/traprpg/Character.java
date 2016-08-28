package com.majalis.traprpg;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Array;
import com.majalis.traprpg.PlayerCharacter.Stance;
/*
 * Abstract character class, both enemies and player characters extend this class
 */
public abstract class Character extends Group {

	public int level;
	public int baseStrength;
	public int baseVtality;
	public int baseAgility;
	public int basePerception;
	public int baseMagic;
	public int baseCharisma;
	public int baseLuck; // 0 for most classes, can go negative
	
	public int baseEvade;
	public int baseBlock;
	public int baseParry;
	public int baseCounter;
	
	public Array<Integer> healthTiers; // total these to receive maxHealth, maybe cache it when this changes
	public Array<Integer> staminaTiers; // total these to receive maxStamina, maybe cache it when this changes
	public Array<Integer> manaTiers; // total these to receive maxMana, maybe cache it when this changes
	
	/* morphic stats */
	public int currentHealth;
	public int currentStamina;
	public int currentMana; // mana might be replaced with spell slots that get refreshed
	
	public int stability;
	public int focus;
	public int fortune;
	
	// public Weapon weapon;
	// public Shield shield;
	// public Armor armor;
	// public Gauntlet gauntlet;
	// public Sabaton sabaton;
	// public Accessory firstAccessory;
	// public Accessory secondAccessory;
	
	public Stance stance;
	// public ObjectMap<StatusTypes, Status>; // status effects will be represented by a map of Enum to Status object
	
}

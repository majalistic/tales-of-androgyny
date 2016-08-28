package com.majalis.traprpg;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.IntArray;
/*
 * Abstract character class, both enemies and player characters extend this class
 */
public abstract class Character extends Group {
	// some of these ints will be enumerators or objects in time
	/* rigid stats */	
	public int level;
	public int baseStrength;
	public int baseVitality;
	public int baseAgility;
	public int basePerception;
	public int baseMagic;
	public int baseCharisma;
	public int baseLuck; // 0 for most classes, can go negative
	
	public int baseEvade;
	public int baseBlock;
	public int baseParry;
	public int baseCounter;
	
	public IntArray healthTiers; // total these to receive maxHealth, maybe cache it when this changes
	public IntArray staminaTiers; // total these to receive maxStamina, maybe cache it when this changes
	public IntArray manaTiers; // total these to receive maxMana, maybe cache it when this changes
	
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
	
	protected Character(){}
	public Character(boolean defaultValues){
		if (defaultValues){
			level = 1;
			baseStrength = baseVitality = baseAgility = basePerception = baseMagic = baseCharisma = 3;
			baseLuck = 0;
			baseEvade = 0;
			baseBlock = 0;
			baseParry = 0;
			baseCounter = 0;
			healthTiers = new IntArray(new int[]{5});
			staminaTiers = new IntArray(new int[]{5});
			manaTiers = new IntArray(new int[]{0});
			currentHealth = getMaxHealth();
			currentStamina = getMaxStamina();
			currentMana = getMaxMana();
			stability = focus = fortune = 5;
			stance = Stance.BALANCED;			
		}
	}

	protected int getMaxHealth() { return getMax(healthTiers); }
	protected int getMaxStamina() { return getMax(staminaTiers); }
	protected int getMaxMana() { return getMax(manaTiers); }
	protected int getMax(IntArray tiers){
		int max = 0;
		for (int ii = 0; ii < tiers.size; ii++){
			max += tiers.get(ii);
		}
		return max;
	}

	public enum Stance {
		BALANCED,
		DEFENSIVE,
		OFFENSIVE
	}
}

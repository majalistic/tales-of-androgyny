package com.majalis.character;

import com.majalis.battle.Attack;
import com.majalis.battle.BattleFactory.EnemyEnum;
import com.majalis.save.SaveManager.JobClass;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.IntArray;
/*
 * Abstract character class, both enemies and player characters extend this class
 */
public abstract class AbstractCharacter extends Actor {
	
	// some of these ints will be enumerators or objects in time
	/* permanent stats */
	public String label;
	public boolean secondPerson;
	
	/* rigid stats */
	public JobClass jobClass;
	public EnemyEnum enemyType;
	public int level;
	public int baseStrength;
	public int baseEndurance;
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
	/* Constructors */
	protected AbstractCharacter(){}
	public AbstractCharacter(boolean defaultValues){
		if (defaultValues){
			secondPerson = false;
			level = 1;
			baseStrength = baseEndurance = baseAgility = basePerception = baseMagic = baseCharisma = 3;
			baseLuck = 0;
			baseEvade = 0;
			baseBlock = 0;
			baseParry = 0;
			baseCounter = 0;
			healthTiers = getDefaultHealthTiers();
			staminaTiers = getDefaultStaminaTiers();
			manaTiers = getDefaultManaTiers();
			currentHealth = getMaxHealth();
			currentStamina = getMaxStamina();
			currentMana = getMaxMana();
			stability = focus = fortune = 10;
			stance = Stance.BALANCED;			
		}
	}
	
	protected abstract Technique getTechnique(AbstractCharacter target);
	
	protected IntArray getDefaultHealthTiers(){ return new IntArray(new int[]{10, 10}); }
	protected IntArray getDefaultStaminaTiers(){ return new IntArray(new int[]{5, 5}); }
	protected IntArray getDefaultManaTiers(){ return new IntArray(new int[]{0}); }
	
	protected int getMaxHealth() { return getMax(healthTiers); }
	protected int getMaxStamina() { return getMax(staminaTiers); }
	protected int getMaxMana() { return getMax(manaTiers); }
	protected int getMaxStability() { return getAgility() * 3; }
	protected int getMax(IntArray tiers){
		int max = 0;
		for (int ii = 0; ii < tiers.size; ii++){
			max += tiers.get(ii);
		}
		return max;
	}
	protected int getStaminaRegen() { return getEndurance()/2 + 1; }
	
	protected int getStabilityRegen() { return getAgility()/2; }
	
	public String getLabel (){ return label; }
	
	public Boolean getSecondPerson(){ return secondPerson; }
	
	public int getCurrentHealth(){ return currentHealth; }
	
	public int getCurrentStamina(){ return currentStamina; }
	
	public int getCurrentMana(){ return currentMana; }
	
	protected void setStaminaToMax() { currentStamina = getMaxStamina(); }
	
	protected void modStamina(int staminaMod){ this.currentStamina += staminaMod; if (currentStamina > getMaxStamina()) currentStamina = getMaxStamina(); }
	
	public int getStability(){ return stability; }
	
	protected void setStabilityToMax(){ stability = 10; }
	
	protected void modStability(int stabilityMod){ this.stability += stabilityMod; if (stability > getMaxStability()) stability = getMaxStability(); }
	
	protected void setManaToMax() { currentMana = getMaxMana(); }
	
	protected void modMana(int manaMod){ this.currentMana += manaMod; if (currentMana > getMaxMana()) currentMana = getMaxMana(); }
	
	public Stance getStance(){ return stance; }
	
	public void setStance(Stance stance){ this.stance = stance; }
	
	protected int getStrength(){ return baseStrength; }
	
	protected int getEndurance(){ return baseEndurance; }
	
	protected int getAgility() { return baseAgility; }

	protected int getPerception() { return basePerception; }

	protected int getMagic() { return baseMagic; }

	protected int getCharisma() { return baseCharisma; }
	
	public String doAttack(String technique, AbstractCharacter secondCharacter, Attack attack){
		return label + (secondPerson ? " use " : " uses ") + technique + " against " + secondCharacter.receiveAttack(attack);
	}
	
	public String receiveAttack(Attack attack){
		String result = (secondPerson ? label.toLowerCase() : label) + ". ";

		int damage = attack.getDamage();
		damage -= getEndurance();
		if (damage > 0){	
			currentHealth -= damage;
			result += "The blow strikes for " + damage + " damage! ";
		}
		
		Stance forcedStance = attack.getForceStance();
		if (forcedStance != null){
			result += "\n" + label + (secondPerson ? " are " : " is ") + "forced into " + stance.toString() + " stance!";
		}
		
		return result+"\n";
	}
	
	public Technique extractCosts(Technique technique){
		modStamina(-technique.getStaminaCost());
		modStamina(getStaminaRegen());
		modStability(-technique.getStabilityCost());
		modStability(getStabilityRegen());
		modMana(-technique.getManaCost());
			
		if (stance != Stance.PRONE && stance != Stance.SUPINE && stance != Stance.DOGGY){
			if (stability <= 0){
				technique = new Technique(Techniques.TRIP, 0);
			}
			else if (currentStamina <= 0){
				technique = new Technique(Techniques.FALL_DOWN, 0);
			}
			else if (currentMana < 0){
				technique = new Technique(Techniques.FIZZLE, 0);
				currentMana = 0;
			}
		}
		
		return technique;
	}

	public enum Stance {
		BALANCED,
		DEFENSIVE,
		OFFENSIVE,
		PRONE,
		SUPINE,
		KNEELING,
		AIRBORNE, 
		DOGGY, 
		KNOTTED, 
		FELLATIO, 
		CASTING
	}	
}

package com.majalis.character;

import com.majalis.battle.BattleFactory.EnemyEnum;
import com.majalis.save.SaveManager.JobClass;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
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
	
	public int baseDefense;
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
	
	public int lust; 
	public int struggle;
	public int battleOver;
	
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
			baseDefense = 2;
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
			struggle = 0;
		}
	}
	
	protected abstract Technique getTechnique(AbstractCharacter target);
	
	protected IntArray getDefaultHealthTiers(){ return new IntArray(new int[]{10, 10, 10, 10}); }
	protected IntArray getDefaultStaminaTiers(){ return new IntArray(new int[]{5, 5, 5, 5}); }
	protected IntArray getDefaultManaTiers(){ return new IntArray(new int[]{0}); }
	
	protected int getMaxHealth() { return getMax(healthTiers); }
	protected int getMaxStamina() { return getMax(staminaTiers); }
	protected int getMaxMana() { return getMax(manaTiers); }
	protected int getMaxStability() { return getAgility() * 3 + 6; }
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
	
	protected void setHealthToMax() { currentHealth = getMaxHealth(); }
	
	public void modHealth(int healthMod){ this.currentHealth += healthMod; if (currentHealth > getMaxHealth()) currentHealth = getMaxHealth(); }
	
	protected void setStaminaToMax() { currentStamina = getMaxStamina(); }
	
	protected void modStamina(int staminaMod){ this.currentStamina += staminaMod; if (currentStamina > getMaxStamina()) currentStamina = getMaxStamina(); }
	
	public int getStability(){ return stability; }
	
	protected void setStabilityToMax(){ stability = 10; }
	
	protected void setStabilityToMin(){ stability = -5; }
	
	protected void modStability(int stabilityMod){ this.stability += stabilityMod; if (stability > getMaxStability()) stability = getMaxStability(); }
	
	protected void setManaToMax() { currentMana = getMaxMana(); }
	
	protected void modMana(int manaMod){ this.currentMana += manaMod; if (currentMana > getMaxMana()) currentMana = getMaxMana(); }
	
	public Stance getStance(){ return stance; }
	
	public void setStance(Stance stance){ this.stance = stance; }
	
	protected int getStrength(){ return baseStrength - (getHealthDegradation() + getStaminaDegradation()); }
	
	protected int getEndurance(){ return baseEndurance - getHealthDegradation(); }
	
	protected int getAgility() { return baseAgility - (getHealthDegradation() + getStaminaDegradation()); }

	protected int getPerception() { return basePerception; }

	protected int getMagic() { return baseMagic; }

	protected int getCharisma() { return baseCharisma; }
	
	protected int getDefense(){ return baseDefense; }
	
	protected int getHealthDegradation(){ return getDegradation(healthTiers, currentHealth); }
	protected int getStaminaDegradation(){ return getDegradation(staminaTiers, currentStamina); }
	
	protected int getDegradation(IntArray tiers, int currentValue){
		int numTiers = tiers.size;
		int value = currentValue;
		for (int tier : tiers.items){
			value -= tier;
			numTiers--;
			if (value <= 0) return numTiers;
		}
		return numTiers;
	}
	
	public void modLust(int lustMod) { lust += lustMod; }
	
	public Technique extractCosts(Technique technique){
		modStamina(-technique.getStaminaCost());
		modStamina(getStaminaRegen());
		modStability(-technique.getStabilityCost());
		modStability(getStabilityRegen());
		modMana(-technique.getManaCost());
		
		if (stance != Stance.PRONE && stance != Stance.SUPINE && stance != Stance.DOGGY && stance != Stance.FELLATIO && stance != Stance.KNOTTED){
			if (stability <= 0){
				technique = new Technique(Techniques.TRIP.getTrait(), 0);
				setStabilityToMin();
			}
			else if (currentStamina <= 0){
				technique = new Technique(Techniques.FALL_DOWN.getTrait(), 0);
			}
			else if (currentMana < 0){
				technique = new Technique(Techniques.FIZZLE.getTrait(), 0);
				currentMana = 0;
			}
		}
		
		if (technique.getTechniqueName().equals("Hit The Deck")){
			setStabilityToMin();
		}
		
		stance = technique.getStance();
		
		return technique;
	}
	
	public Attack doAttack(Attack resolvedAttack) {
		resolvedAttack.setUser(label);
		if (!resolvedAttack.isSuccessful()){
			if (enemyType == EnemyEnum.HARPY && stance == Stance.FELLATIO && resolvedAttack.getForceStance() == Stance.FELLATIO){
				resolvedAttack.addMessage("The harpy missed! She crashes to the ground!");
				stance = Stance.PRONE;
			}
			return resolvedAttack;
		}
		if (resolvedAttack.isHealing()){
			modHealth(resolvedAttack.getHealing());
			
			resolvedAttack.addMessage("You heal for " + resolvedAttack.getHealing()+"!");
		}
		if (resolvedAttack.getForceStance() == Stance.DOGGY){
			resolvedAttack.addMessage("You are being anally violated!");
			resolvedAttack.addMessage("Your hole is stretched by her fat dick!");
			resolvedAttack.addMessage("Your hole feels like it's on fire!");
			resolvedAttack.addMessage("Her cock glides smoothly through your irritated anal mucosa!");
			resolvedAttack.addMessage("Her rhythmic thrusting in and out of your asshole is emasculating!");
			resolvedAttack.addMessage("You are red-faced and embarassed because of her butt-stuffing!");
			resolvedAttack.addMessage("Your cock is ignored!");
		}		
		else if(resolvedAttack.getForceStance() == Stance.FELLATIO){
			if (enemyType == EnemyEnum.HARPY){
				resolvedAttack.addMessage("She tastes horrible! Harpies are highly unhygenic!");
				resolvedAttack.addMessage("You learned Anatomy (Harpy)!");
				resolvedAttack.addMessage("You learned Behavior (Harpy)!");
				resolvedAttack.addMessage("There is a phallus in your mouth!");
				resolvedAttack.addMessage("It blew past your lips!");
				resolvedAttack.addMessage("The harpy is holding your head in place with");
				resolvedAttack.addMessage("her talons and balancing herself with her wings!");
				resolvedAttack.addMessage("She flaps violently while humping your face!  Her cock tastes awful!");
			}
			else {
				resolvedAttack.addMessage("She stuffs her cock into your face!");
			}
		}
		else if (resolvedAttack.getForceStance() == Stance.KNOTTED){
			if (battleOver == 0){
				resolvedAttack.addMessage("Her powerful hips try to force something big inside!");
				resolvedAttack.addMessage("You struggle... but can't escape!");
				resolvedAttack.addMessage("Her grapefruit-sized knot slips into your rectum!  You take 4 damage!");
				resolvedAttack.addMessage("You learned about Anatomy(Wereslut)! You are being bred!");
				resolvedAttack.addMessage("Your anus is permanently stretched!");
			}
			else if (battleOver < 3){
				resolvedAttack.addMessage("Her tremendous knot is still lodged in your rectum!");
				resolvedAttack.addMessage("You can't dislodge it; it's too large!");
				resolvedAttack.addMessage("You're drooling!");
				resolvedAttack.addMessage("Her fat thing is plugging your shithole!");					
			}
			else {
				resolvedAttack.addMessage("The battle is over, but your ordeal has just begun!");
				resolvedAttack.addMessage("You are about to be bred like a bitch!");
				resolvedAttack.addMessage("She's going to ejaculate her runny dog cum in your bowels!");	
			}
			battleOver++;
		}
		// all climax logic should go here
		if (resolvedAttack.isClimax()){
			lust -= 14;
			if (stance == Stance.FELLATIO){
				if (enemyType == EnemyEnum.HARPY){
					resolvedAttack.addMessage("A harpy semen bomb explodes in your mouth!  It tastes awful!");
					resolvedAttack.addMessage("You are going to vomit!");
					resolvedAttack.addMessage("You spew up harpy cum!  The harpy preens her feathers.");
				}
				else {
					resolvedAttack.addMessage("Her cock erupts in your mouth!");
					resolvedAttack.addMessage("You swallow all of her semen!");
				}
			}
			else {
				resolvedAttack.addMessage("The " + getLabel() + " spews hot, thick semen into your bowels!");
			}
		}
		return resolvedAttack;
	}
	
	public Array<String> receiveAttack(Attack attack){
		
		if (!attack.isSuccessful()){
			return attack.getMessages();
		}
		
		Array<String> result = attack.getMessages();

		attack.addMessage(attack.getuser() + " used " + attack.getName() +  " on " + (secondPerson ? label.toLowerCase() : label) + "!");
		
		struggle += attack.getGrapple();
		if (attack.isClimax()){
			struggle = 0;
		}
		if (attack.getForceStance() == Stance.BALANCED){
			attack.addMessage("You broke free!");
			if (stance == Stance.FELLATIO){
				attack.addMessage("It slips out of your mouth and you get to your feet!");
			}
			else {
				attack.addMessage("It pops out of your ass and you get to your feet!");
			}
		}
		
		int damage = attack.getDamage();
		damage -= getDefense();
		if (damage > 0){	
			currentHealth -= damage;
			result.add("The blow strikes for " + damage + " damage!");
		}
		
		Stance forcedStance = attack.getForceStance();
		if (forcedStance != null){
			result.add(label + (secondPerson ? " are " : " is ") + "forced into " + forcedStance.toString() + " stance!");
			stance = forcedStance;
		}
		
		String lustIncrease = increaseLust();
		if (lustIncrease != null) result.add(lustIncrease);
		
		if (attack.getLust() > 0){
			lustIncrease = increaseLust(attack.getLust());
			if (lustIncrease != null) result.add(lustIncrease);
			result.add(label + (secondPerson ? " are taunted " : " is taunted ") + "! " + (secondPerson ? " Your " : " Their ") + "lust raises by" + attack.getLust());
		}
		return result;
	}

	protected abstract String increaseLust();
	protected abstract String increaseLust(int lustIncrease);
	
	public String getStanceTransform(Technique firstTechnique) {
		return label + " adopt" + (secondPerson ? "" : "s") + " a(n) " + firstTechnique.getStance().toString() + " stance! ";
 	}

	public enum Stance {
		BALANCED,
		DEFENSIVE,
		OFFENSIVE,
		PRONE (false),
		SUPINE (false),
		KNEELING (false),
		AIRBORNE, 
		DOGGY, 
		KNOTTED, 
		FELLATIO, 
		CASTING,
		ERUPT
		;
		
		public final boolean receivesHighAttacks;
		private Stance(){
			this(true);
		}
		private Stance(boolean receivesHigh){
			receivesHighAttacks = receivesHigh;
		}
		
	}
}

package com.majalis.character;

import com.majalis.asset.AssetEnum;
import com.majalis.battle.BattleFactory.EnemyEnum;
import com.majalis.save.SaveManager.JobClass;
import com.majalis.technique.ClimaxType;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
/*
 * Abstract character class, both enemies and player characters extend this class
 */
public abstract class AbstractCharacter extends Actor {
	
	// some of these ints will be enumerators or objects in time
	/* permanent stats */
	protected String label;
	protected boolean secondPerson;
	
	/* rigid stats */
	protected JobClass jobClass;
	protected EnemyEnum enemyType;
	protected int level;
	protected int experience;
	protected int baseStrength;
	protected int baseEndurance;
	protected int baseAgility;
	protected int basePerception;
	protected int baseMagic;
	protected int baseCharisma;
	protected int baseLuck; // 0 for most classes, can go negative
	
	protected int baseDefense;
	protected int baseEvade;
	protected int baseBlock;
	protected int baseParry;
	protected int baseCounter;
	
	protected IntArray healthTiers; // total these to receive maxHealth, maybe cache it when this changes
	protected IntArray staminaTiers; // total these to receive maxStamina, maybe cache it when this changes
	protected IntArray manaTiers; // total these to receive maxMana, maybe cache it when this changes
	
	/* morphic stats */
	protected int currentHealth;
	protected int currentStamina;
	protected int currentMana; // mana might be replaced with spell slots that get refreshed
	
	protected int stability;
	protected int focus;
	protected int fortune;
	
	protected int lust; 
	protected int struggle;
	protected int battleOver;
	
	// public Weapon weapon;
	// public Shield shield;
	// public Armor armor;
	// public Gauntlet gauntlet;
	// public Sabaton sabaton;
	// public Accessory firstAccessory;
	// public Accessory secondAccessory;
	
	// public Hole hole;  // bowels contents, tightness, number of copulations, number of creampies, etc. 
	// public Mouth mouth; 
	protected PhallusType phallus;	
	
	protected int buttful;
	protected int mouthful;
	
	protected Stance stance;
	// public ObjectMap<StatusTypes, Status>; // status effects will be represented by a map of Enum to Status object
	/* Constructors */
	protected AbstractCharacter(){}
	protected AbstractCharacter(boolean defaultValues){
		if (defaultValues){
			secondPerson = false;
			level = 1;
			experience = 0;
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
			phallus = PhallusType.NORMAL;
		}
	}
	
	protected abstract Technique getTechnique(AbstractCharacter target);
	
	protected IntArray getDefaultHealthTiers(){ return new IntArray(new int[]{10, 10, 10, 10}); }
	protected IntArray getDefaultStaminaTiers(){ return new IntArray(new int[]{5, 5, 5, 5}); }
	protected IntArray getDefaultManaTiers(){ return new IntArray(new int[]{0}); }
	
	protected int getMaxHealth() { return getMax(healthTiers); }
	protected int getMaxStamina() { return getMax(staminaTiers); }
	protected int getMaxMana() { return getMax(manaTiers); }
	protected int getMaxStability() { return getAgility() * 3 + 9; }
	protected int getMax(IntArray tiers){
		int max = 0;
		for (int ii = 0; ii < tiers.size; ii++){
			max += tiers.get(ii);
		}
		return max;
	}
	
	public Stance getStance(){ return stance; }
	
	public void setStance(Stance stance){ this.stance = stance; }
	
	public int getCurrentHealth(){ return currentHealth; }
	
	public int getCurrentStamina(){ return currentStamina; }
	
	public int getCurrentMana(){ return currentMana; }
	
	public int getStability(){ return stability; }
	
	public int getBattleOver(){ return battleOver; }
	
	public int getLust(){ return lust; }
	
	public void modHealth(int healthMod){ this.currentHealth += healthMod; if (currentHealth > getMaxHealth()) currentHealth = getMaxHealth(); }
	
	protected int getStaminaRegen() { return getEndurance()/2 + 1; }
	
	protected int getStabilityRegen() { return getAgility()/2; }
	
	protected String getLabel (){ return label; }
	
	protected Boolean getSecondPerson(){ return secondPerson; }
	
	protected void setHealthToMax() { currentHealth = getMaxHealth(); }
	
	protected void setStaminaToMax() { currentStamina = getMaxStamina(); }
	
	protected void modStamina(int staminaMod){ this.currentStamina += staminaMod; if (currentStamina > getMaxStamina()) currentStamina = getMaxStamina(); }
	
	protected void setStabilityToMax(){ stability = getMaxStability(); }
	
	protected void setStabilityToMin(){ stability = -5; }
	
	protected void modStability(int stabilityMod){ this.stability += stabilityMod; if (stability > getMaxStability()) stability = getMaxStability(); }
	
	protected void setManaToMax() { currentMana = getMaxMana(); }
	
	protected void modMana(int manaMod){ this.currentMana += manaMod; if (currentMana > getMaxMana()) currentMana = getMaxMana(); }
	
	protected int getStrength(){ return stepDown(baseStrength - (getHealthDegradation() + getStaminaDegradation())); }
	
	private int stepDown(int value){ if (value < 4) return value; else return 6 + (value - 6)/2; } 
	
	protected int getEndurance(){ return baseEndurance - getHealthDegradation(); }
	
	protected int getAgility() { return baseAgility - (getHealthDegradation() + getStaminaDegradation()); }

	protected int getPerception() { return basePerception; }

	protected int getMagic() { return baseMagic; }

	protected int getCharisma() { return baseCharisma; }
	
	protected int getDefense(){ return baseDefense; }
	protected int getTraction(){ return 2; }
	
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
	
	protected int getStaminaMod(Technique technique){
		int staminaMod = technique.getStaminaCost();
		if (staminaMod >= 0){
			staminaMod -= getStaminaRegen();
			if (staminaMod < 0) staminaMod = 0;
		}
		else {
			staminaMod -= getStaminaRegen();
		}
		return staminaMod;
	}
	
	public Technique extractCosts(Technique technique){
		int staminaMod = getStaminaMod(technique); 
		modStamina(-staminaMod);
		modStability(-technique.getStabilityCost());
		modStability(getStabilityRegen());
		modMana(-technique.getManaCost());
		
		// this should be moved to technique failure
		if (technique.isSpell() && currentMana < 0){
			technique = new Technique(Techniques.FIZZLE.getTrait(), 0);
			currentMana = 0;
		}
		
		if (technique.isFallDown()){
			setStabilityToMin();
		}
		
		stance = technique.getStance();
		
		return technique;
	}
	
	protected boolean alreadyIncapacitated(){
		return stance == Stance.PRONE || stance == Stance.SUPINE || stance == Stance.FELLATIO || stance == Stance.DOGGY || stance == Stance.KNOTTED;
	}
	
	public Attack doAttack(Attack resolvedAttack) {
		resolvedAttack.setUser(label);
		if (!resolvedAttack.isSuccessful()){
			resolvedAttack.addMessage(resolvedAttack.getUser() + " used " + resolvedAttack.getName() + " but missed! ");
			if (enemyType == EnemyEnum.HARPY && stance == Stance.FELLATIO && resolvedAttack.getForceStance() == Stance.FELLATIO){
				resolvedAttack.addMessage("She crashes to the ground!");
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
			stance = Stance.ERUPT;
		}
		return resolvedAttack;
	}
	
	public Array<String> receiveAttack(Attack attack){
		Array<String> result = attack.getMessages();
		
		boolean knockedDown = false;
		
		if (attack.isSuccessful()){
			result.add(attack.getUser() + " used " + attack.getName() +  " on " + (secondPerson ? label.toLowerCase() : label) + "!");
			
			struggle += attack.getGrapple();
			if (attack.isClimax()){
				struggle = 0;
			}
			if (attack.getForceStance() == Stance.BALANCED){
				result.add("You broke free!");
				if (stance == Stance.FELLATIO){
					result.add("It slips out of your mouth and you get to your feet!");
				}
				else {
					result.add("It pops out of your ass and you get to your feet!");
				}
			}
			
			int damage = attack.getDamage();
			damage -= getDefense();
			if (damage > 0){	
				currentHealth -= damage;
				result.add("The blow strikes for " + damage + " damage!");
			}
			
			int knockdown = attack.getForce();
			knockdown -= getTraction();
			if (knockdown > 0){
				if (!alreadyIncapacitated()){
					stability -= knockdown;
					result.add("It's a solid blow! It reduces balance by " + knockdown + "!");
					if (stability <= 0){
						result.add(label + (secondPerson ? " are " : " is ") + "knocked to the ground!");
						setStabilityToMin();
						stance = Stance.SUPINE;
						knockedDown = true;
					}
				}
			}
			
			Stance forcedStance = attack.getForceStance();
			if (forcedStance != null){
				result.add(label + (secondPerson ? " are " : " is ") + "forced into " + forcedStance.toString() + " stance!");
				stance = forcedStance;
				if (forcedStance == Stance.PRONE || forcedStance == Stance.SUPINE){
					setStabilityToMin();
				}
				else if (forcedStance == Stance.KNEELING && stability > getMaxStability() / 2){
					stability = getMaxStability() / 2;
				}
			}
			
			String lustIncrease = increaseLust();
			if (lustIncrease != null) result.add(lustIncrease);
			
			if (attack.getLust() > 0){
				lustIncrease = increaseLust(attack.getLust());
				if (lustIncrease != null) result.add(lustIncrease);
				result.add(label + (secondPerson ? " are taunted " : " is taunted ") + "! " + (secondPerson ? " Your " : " Their ") + "lust raises by" + attack.getLust());
			}	
			
			if (attack.getClimaxType() == ClimaxType.ANAL){
				buttful = 3;
			}
			else if (attack.getClimaxType() == ClimaxType.ORAL){
				mouthful = 1;
			}
			
			if (buttful > 0) result.add(getLeakMessage());
			if (mouthful > 0) result.add(getDroolMessage());
			
		}
		if (!alreadyIncapacitated() && !knockedDown){
			// you tripped
			if (stability <= 0){
				stance = Stance.PRONE;
				result.add(label + (secondPerson ? " lose your" : " loses their") + " footing and " + (secondPerson ? "trip" : "trips") + "!");
				setStabilityToMin();
			}
			// you blacked out
			else if (currentStamina <= 0){
				result.add(label + " runs out of breath and " + (secondPerson ? "collapse" : "collapses") + "!");
				stance = Stance.SUPINE;
			}
		}
		
		return result;
	}

	private String getLeakMessage(){
		String message = "";
		if (buttful > 1){
			message = "You drool cum from your hole!";
		}
		else {
			message = "The last of the cum runs out of your hole!";
		}
		buttful--;
		return message;
	}
	private String getDroolMessage(){
		String message = "";
		if (mouthful > 1){
			message = "You try to spit up all of their cum!";
		}
		else {
			message = "You spit all of their cum out onto the ground!";
		}
		mouthful--;
		return message;
	}
	
	protected abstract String increaseLust();
	protected abstract String increaseLust(int lustIncrease);
	
	public String getStanceTransform(Technique firstTechnique) {
		return label + " adopt" + (secondPerson ? "" : "s") + " a(n) " + firstTechnique.getStance().toString() + " stance! ";
 	}
	
	protected enum PhallusType {
		SMALL("Trap"),
		NORMAL("Human"),
		MONSTER("Monster");
		private final String label;

		PhallusType(String label) {
		    this.label = label;
		}
	}
	
	public String getLustImagePath(){
		int lustLevel = lust > 7 ? 2 : lust > 3 ? 1 : 0;
		return "arousal/" + phallus.label + lustLevel + ".png";
	}
	
	protected boolean outOfStamina(Technique technique){
		return getStaminaMod(technique) >= currentStamina;
	}
	
	protected boolean outOfStability(Technique technique){
		return technique.getStabilityCost() - getStabilityRegen() >= stability;
	}
	
	
	public boolean outOfStaminaOrStability(Technique technique) {
		 return outOfStamina(technique) || outOfStability(technique);
	}

	public boolean lowStaminaOrStability(Technique technique) {
		return getStaminaMod(technique) >= currentStamina - 5 || technique.getStabilityCost() - getStabilityRegen() >= stability - 5;
	
	}
	
	public enum Stance {
		BALANCED (AssetEnum.BALANCED.getPath()),
		DEFENSIVE (AssetEnum.DEFENSIVE.getPath()),
		OFFENSIVE (AssetEnum.OFFENSIVE.getPath()),
		BLITZ (AssetEnum.BLITZ.getPath()),
		STONEWALL (AssetEnum.BALANCED.getPath()),
		PRONE ((AssetEnum.PRONE.getPath()), false, false, true),
		SUPINE ((AssetEnum.SUPINE.getPath()), false, false, true),
		KNEELING ((AssetEnum.KNEELING.getPath()), false, true, true),
		AIRBORNE ((AssetEnum.AIRBORNE.getPath()), true, false, false), 
		DOGGY (AssetEnum.DOGGY.getPath()), 
		KNOTTED (AssetEnum.KNOTTED.getPath()), 
		FELLATIO (AssetEnum.FELLATIO.getPath()), 
		CASTING (AssetEnum.CASTING.getPath()),
		ERUPT (AssetEnum.ERUPT.getPath())
		;
		
		private final String texturePath;
		public final boolean receivesHighAttacks;
		public final boolean receivesMediumAttacks;
		public final boolean receivesLowAttacks;
		private Stance(String texturePath){
			this(texturePath, true, true, true);
		}
		private Stance(String texturePath, boolean receivesHigh, boolean receivesMedium, boolean receivesLow){
			this.texturePath = texturePath;
			receivesHighAttacks = receivesHigh;
			receivesMediumAttacks = receivesMedium;
			receivesLowAttacks = receivesLow;
		}
		public String getPath() { return texturePath; }
		
	}
}

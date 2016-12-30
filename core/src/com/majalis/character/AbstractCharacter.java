package com.majalis.character;

import com.majalis.asset.AssetEnum;
import com.majalis.battle.BattleFactory.EnemyEnum;
import com.majalis.character.Attack.Status;
import com.majalis.character.Item.Weapon;
import com.majalis.character.PlayerCharacter.Bootyliciousness;
import com.majalis.save.SaveManager.JobClass;
import com.majalis.technique.ClimaxTechnique.ClimaxType;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.ObjectMap;
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
	
	protected Weapon weapon;
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
	
	protected Bootyliciousness bootyliciousness;
	
	protected Stance stance;
	private Stance oldStance;
	public ObjectMap<String, Integer> statuses; // status effects will be represented by a map of Enum to Status object

	/* Constructors */
	protected AbstractCharacter(){}
	protected AbstractCharacter(boolean defaultValues){
		if (defaultValues){
			secondPerson = false;
			level = 1;
			experience = 0;
			baseStrength = baseEndurance = baseAgility = basePerception = baseMagic = baseCharisma = 3;
			baseDefense = 4;
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
			statuses = new ObjectMap<String, Integer>();
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
		
	public float getHealthPercent(){ return currentHealth / (getMaxHealth() * 1.0f); }
	
	public float getStaminaPercent(){ return currentStamina / (getMaxStamina() * 1.0f); }
	
	public float getBalancePercent() { return stability / (getMaxStability() * 1.0f); }
	
	public float getManaPercent(){ return currentMana / (getMaxMana() * 1.0f); }
	
	public String getHealthDisplay(){ 
		switch (getHealthDegradation()){
			case 0: return AssetEnum.HEALTH_ICON_0.getPath();
			case 1: return AssetEnum.HEALTH_ICON_1.getPath();
			case 2: return AssetEnum.HEALTH_ICON_2.getPath();
			case 3: return AssetEnum.HEALTH_ICON_3.getPath();
			case 4: return AssetEnum.HEALTH_ICON_3.getPath();
		}
		return null;
	}
	
	public String getStaminaDisplay(){ 
		switch (getStaminaDegradation()){
			case 0: return AssetEnum.STAMINA_ICON_0.getPath();
			case 1: return AssetEnum.STAMINA_ICON_1.getPath();
			case 2: return AssetEnum.STAMINA_ICON_2.getPath();
			case 3: return AssetEnum.STAMINA_ICON_3.getPath();
			case 4: return AssetEnum.STAMINA_ICON_3.getPath();
		}
		return null;
	}
	
	public String getBalanceDisplay(){ 
		return stability > 10 ?  AssetEnum.BALANCE_ICON_0.getPath() : stability > 5 ? AssetEnum.BALANCE_ICON_1.getPath() : AssetEnum.BALANCE_ICON_2.getPath();
	}
	
	public String getManaDisplay(){ 
		switch (4 - (int)(getManaPercent() * 100)/ 25){
			case 0: return AssetEnum.MANA_ICON_0.getPath();
			case 1: return AssetEnum.MANA_ICON_1.getPath();
			case 2: return AssetEnum.MANA_ICON_2.getPath();
			case 3: return AssetEnum.MANA_ICON_3.getPath();
			case 4: return AssetEnum.MANA_ICON_3.getPath();
		}
		return null;
	}
	
	public int getStability(){ return stability; }
	
	public int getBattleOver(){ return battleOver; }
	
	public int getLust(){ return lust; }
	
	public void modHealth(int healthMod){ this.currentHealth += healthMod; if (currentHealth > getMaxHealth()) currentHealth = getMaxHealth(); }
	
	protected int getStaminaRegen() { return getEndurance()/2; }
	
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
	
	protected int getStrength(){ return stepDown(getRawStrength()); }
	// no step down
	protected int getRawStrength() { return Math.max((baseStrength + getStrengthBuff()) - (getHealthDegradation() + getStaminaDegradation())/2, 0); }
	
	private int getStrengthBuff(){ return statuses.get(StatusType.STRENGTH_BUFF.toString(), 0); }
	private int getEnduranceBuff(){ return statuses.get(StatusType.ENDURANCE_BUFF.toString(), 0); }
	private int getAgilityBuff(){ return statuses.get(StatusType.AGILITY_BUFF.toString(), 0); }
	
	protected int stepDown(int value){ if (value < 3) return value; else if (value < 7) return 3 + (value - 3)/2; else return 5 + (value - 7)/3; } 
	
	protected int getEndurance(){ return Math.max((baseEndurance + getEnduranceBuff()) - (getHealthDegradation()), 0); }
	
	protected int getAgility() { return Math.max((baseAgility + getAgilityBuff()) - (getHealthDegradation() + getStaminaDegradation()), 0); }

	protected int getPerception() { return Math.max(basePerception, 0); }

	protected int getMagic() { return Math.max(baseMagic, 0); }

	protected int getCharisma() { return Math.max(baseCharisma, 0); }
	
	protected int getDefense(){ return Math.max(baseDefense, 0); }
	protected int getTraction(){ return 2; }
	
	public int getHealthDegradation(){ return getDegradation(healthTiers, currentHealth); }
	public int getStaminaDegradation(){ return getDegradation(staminaTiers, currentStamina); }
	
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
	// right now this and "doAttack" handle once-per-turn character activities
	public Technique extractCosts(Technique technique){
		int staminaMod = getStaminaMod(technique); 
		modStamina(-staminaMod);
		modStability(-technique.getStabilityCost());
		modStability(getStabilityRegen());
		modMana(-technique.getManaCost());
		
		Array<String> toRemove = new Array<String>();
		// statuses degrade with time in a general way currently
		for(String key: statuses.keys()){
			StatusType type = StatusType.valueOf(key);
			if (!type.degrades()) continue;
			int value = statuses.get(key) - 1;
			statuses.put(key, value);
			if (value <= 0){
				toRemove.add(key);
			}
		}
		for(String key: toRemove){
			statuses.remove(key);
		}
		
		// this should be moved to technique failure
		if (technique.isSpell() && currentMana < 0){
			technique = new Technique(Techniques.FIZZLE.getTrait(), 0);
			currentMana = 0;
		}
		
		if (technique.isFallDown()){
			setStabilityToMin();
		}
		
		oldStance = stance;
		stance = technique.getStance();
		
		
		return technique;
	}
	
	protected boolean alreadyIncapacitated(){
		return stance == Stance.PRONE || stance == Stance.SUPINE || stance == Stance.FELLATIO || stance == Stance.DOGGY || stance == Stance.ANAL || stance == Stance.KNOTTED || stance == Stance.COWGIRL || stance == Stance.STANDING;
	}
	
	public Attack doAttack(Attack resolvedAttack) {
		resolvedAttack.setUser(label);
		if (!resolvedAttack.isSuccessful()){
			resolvedAttack.addMessage(resolvedAttack.getUser() + " used " + resolvedAttack.getName() + (resolvedAttack.getStatus() == Status.MISS ? " but missed!" : "! FAILURE!"));
			
			if (resolvedAttack.getStatus() == Status.MISS && enemyType == EnemyEnum.HARPY && stance == Stance.FELLATIO && resolvedAttack.getForceStance() == Stance.FELLATIO){
				resolvedAttack.addMessage("She crashes to the ground!");
				stance = Stance.PRONE;
			}
			else if(resolvedAttack.getForceStance() != null){
				stance = oldStance;
			}
			return resolvedAttack;
		}
		if (resolvedAttack.isHealing()){
			modHealth(resolvedAttack.getHealing());
			
			resolvedAttack.addMessage("You heal for " + resolvedAttack.getHealing()+"!");
		}
		Buff buff = resolvedAttack.getBuff();
		if (buff != null){
			statuses.put(buff.type.toString(), buff.power);
		}
		if (resolvedAttack.getForceStance() == Stance.DOGGY || resolvedAttack.getForceStance() == Stance.ANAL || resolvedAttack.getForceStance() == Stance.STANDING){
			resolvedAttack.addMessage("You are being anally violated!");
			resolvedAttack.addMessage("Your hole is stretched by her fat dick!");
			resolvedAttack.addMessage("Your hole feels like it's on fire!");
			resolvedAttack.addMessage("Her cock glides smoothly through your irritated anal mucosa!");
			resolvedAttack.addMessage("Her rhythmic thrusting in and out of your asshole is emasculating!");
			resolvedAttack.addMessage("You are red-faced and embarassed because of her butt-stuffing!");
			resolvedAttack.addMessage("Your cock is ignored!");
		}		
		else if(resolvedAttack.getForceStance() == Stance.FELLATIO || resolvedAttack.getForceStance() == Stance.SIXTY_NINE){
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
				resolvedAttack.addMessage("You suck on her cock!");
				resolvedAttack.addMessage("She licks her lips!");
			}
			if (resolvedAttack.getForceStance() == Stance.SIXTY_NINE){
				resolvedAttack.addMessage("She deepthroats your cock!");
			}
		}
		else if (resolvedAttack.getForceStance() == Stance.FACE_SITTING){
			resolvedAttack.addMessage("She rides your face!");
			resolvedAttack.addMessage("You receive a faceful of ass!");
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
			if (oldStance == Stance.FELLATIO){
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
			else if (oldStance == Stance.COWGIRL){
				resolvedAttack.addMessage("The " + getLabel() + " blasts off in your intestines while you bounce\non their cumming cock! You got butt-bombed!");
			}
			else if (oldStance == Stance.ANAL){
				resolvedAttack.addMessage("The " + getLabel() + "'s lovemaking reaches a climax!");
				resolvedAttack.addMessage("They don't pull out! It twitches and throbs in your rectum!");
				resolvedAttack.addMessage("They cum up your ass! Your stomach receives it!");				
			}
			else if (oldStance == Stance.HANDY){
				resolvedAttack.addMessage("Their cock jerks in your hand! They're gonna spew!");
				resolvedAttack.addMessage("Their eyes roll into the back of their head! Here it comes!");
				resolvedAttack.addMessage("It's too late to dodge! They blast a rope of cum on your face!");
				resolvedAttack.addMessage("Rope after rope lands all over face!");
				resolvedAttack.addMessage("They spewed cum all over your face!");
				resolvedAttack.addMessage("You look like a glazed donut! Hilarious!");
				resolvedAttack.addMessage("You've been bukkaked!");
			}
			else if (oldStance == Stance.STANDING || oldStance == Stance.DOGGY){
				resolvedAttack.addMessage("The " + getLabel() + " spews hot, thick semen into your bowels!");
				resolvedAttack.addMessage("You are anally inseminated!");
				resolvedAttack.addMessage("You're going to be farting cum for days!");
			}
			else if (oldStance == Stance.SIXTY_NINE){
				
			}
			stance = Stance.ERUPT;
		}
		oldStance = stance;
		return resolvedAttack;
	}
	
	public Array<String> receiveAttack(Attack attack){
		Array<String> result = attack.getMessages();
		
		boolean knockedDown = false;
		
		if (attack.isSuccessful()){
			if (attack.getForceStance() == Stance.DOGGY && bootyliciousness != null)
				result.add("They slap their hips against your " + bootyliciousness.toString().toLowerCase() + " booty!");
			
			if (!attack.isHealing() && attack.getBuff() == null){
				result.add(attack.getUser() + " used " + attack.getName() +  " on " + (secondPerson ? label.toLowerCase() : label) + "!");
			}
			
			struggle += attack.getGrapple();
			if (attack.isClimax()){
				struggle = 0;
			}
			if (attack.getForceStance() == Stance.BALANCED){
				result.add(attack.getUser() + " broke free!");
				if (stance == Stance.FELLATIO){
					result.add("It slips out of your mouth and you get to your feet!");
				}
				else if (stance == Stance.HANDY){
					
				}
				else {
					result.add("It pops out of your ass and you get to your feet!");
				}
			}
			
			int damage = attack.getDamage();
			if (!attack.isSpell()){
				damage -= getDefense();
			}
			
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
			
			int armorSunder = attack.getArmorSunder();
			if (armorSunder > 0){
				// this shouldn't lower baseDefense, instead sundering armor
				if (baseDefense > 0){
					result.add("It's an armor shattering blow! It reduces armor by " + (armorSunder > baseDefense ? baseDefense : armorSunder) + "!");
					baseDefense -= armorSunder;
					if (baseDefense < 0) baseDefense = 0;
					
				}
			}
			
			int gutcheck = attack.getGutCheck();
			if (gutcheck > 0){
				if (!alreadyIncapacitated()){
					currentStamina -= gutcheck;
					result.add("It's a blow to the stomach! It reduces stamina by " + gutcheck + "!");
					if (currentStamina <= 0){
						result.add(label + (secondPerson ? " fall " : " falls ") + " to the ground!");
						setStabilityToMin();
						stance = Stance.PRONE;
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
	
	public boolean outOfStamina(Technique technique){
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
	
	protected boolean isErect(){
		return lust > 7;
	}
	
	private enum StanceType{
		ANAL,
		ORAL,
		HANDJOB,
		FACESIT,
		INCAPACITATED,
		NORMAL
	}
	
	public enum Stance {
		BALANCED (AssetEnum.BALANCED.getPath()),
		DEFENSIVE (AssetEnum.DEFENSIVE.getPath()),
		OFFENSIVE (AssetEnum.OFFENSIVE.getPath()),
		BLITZ (AssetEnum.BLITZ.getPath()),
		STONEWALL (AssetEnum.BALANCED.getPath()),
		PRONE (StanceType.INCAPACITATED, AssetEnum.PRONE.getPath(), false, false, true),
		SUPINE (StanceType.INCAPACITATED, AssetEnum.SUPINE.getPath(), false, false, true),
		KNEELING (AssetEnum.KNEELING.getPath(), false, true, true),
		AIRBORNE (AssetEnum.AIRBORNE.getPath(), true, false, false), 
		FULL_NELSON (AssetEnum.FULL_NELSON.getPath()), 
		DOGGY (StanceType.ANAL, AssetEnum.DOGGY.getPath()), 
		ANAL (StanceType.ANAL, AssetEnum.ANAL.getPath()), 
		STANDING (StanceType.ANAL, AssetEnum.STANDING.getPath()),
		HANDY (StanceType.HANDJOB, AssetEnum.HANDY.getPath()),
		COWGIRL (StanceType.ANAL, AssetEnum.COWGIRL.getPath()),
		KNOTTED (StanceType.ANAL, AssetEnum.KNOTTED.getPath()), 
		FELLATIO (StanceType.ORAL, AssetEnum.FELLATIO.getPath()), 
		FACE_SITTING(StanceType.FACESIT, AssetEnum.FACE_SITTING.getPath()),
		SIXTY_NINE(StanceType.ORAL, AssetEnum.SIXTY_NINE.getPath()),
		CASTING (AssetEnum.CASTING.getPath()),
		ERUPT (AssetEnum.ERUPT.getPath())
		;
		// need to create: boolean anal, boolean oral, boolean method erotic, boolean incapacitated
		private final String texturePath;
		private final StanceType type;
		public final boolean receivesHighAttacks;
		public final boolean receivesMediumAttacks;
		public final boolean receivesLowAttacks;
		
		private Stance(String texturePath) {
			this(StanceType.NORMAL, texturePath, true, true, true);
		}
		
		private Stance(StanceType type, String texturePath) {
			this(type, texturePath, true, true, true);
		}
		
		private Stance(String texturePath, boolean receivesHigh, boolean receivesMedium, boolean receivesLow) {
			this(StanceType.NORMAL, texturePath, receivesHigh, receivesMedium, receivesLow);
		}
		
		private Stance(StanceType type, String texturePath, boolean receivesHigh, boolean receivesMedium, boolean receivesLow) {
			this.type = type;
			this.texturePath = texturePath;
			receivesHighAttacks = receivesHigh;
			receivesMediumAttacks = receivesMedium;
			receivesLowAttacks = receivesLow;
		}
		public String getPath() { return texturePath; }
		
		public boolean isErotic() {
			return type == StanceType.ANAL || type == StanceType.ORAL || type == StanceType.HANDJOB || type == StanceType.FACESIT;
		}
		
		public boolean isIncapacitating() {
			return type == StanceType.INCAPACITATED;
		}
		
		public boolean isAnal() {
			return type == StanceType.ANAL;
		}
		public boolean isOral() {
			return type == StanceType.ORAL;
		}
		
		public boolean isIncapacitatingOrErotic(){
			return isErotic() || isIncapacitating(); 
		}
		
	}
	
	public enum Stat {
		STRENGTH(AssetEnum.STRENGTH.getPath(), "Strength determines raw attack power, which affects damage,\nhow much attacks unbalance an enemies, and contests\nof strength, such as wrestling, struggling,\nor weapon locks."),
		ENDURANCE(AssetEnum.ENDURANCE.getPath(), "Endurance determines stamina and resilience, which affects\nyour ability to keep up an assault without getting tired,\nyour ability to shrug off low damage attacks,\nand wear heavier armor without being tired."),
		AGILITY(AssetEnum.AGILITY.getPath(), "Agility determines balance and skill, affecting your ability\nto keep a sure footing even while doing acrobatic\nmaneuvers, getting unblockable attacks against\nenemies, and evading enemy attacks."),
		PERCEPTION(AssetEnum.PERCEPTION.getPath(), "Perception determines your ability to see what attacks an\nenemy may use next and prepare accordingly, as well as\nyour base scouting ability, which determines what\ninformation you can see about upcoming areas."),
		MAGIC(AssetEnum.MAGIC.getPath(), "Magic determines your magical capabilities, such as how\npowerful magic spells are, and how many of them\nyou can cast before becoming magically exhausted."),
		CHARISMA(AssetEnum.CHARISMA.getPath(), "Charisma determines your ability to influence an enemy,\ngetting them to calm down and listen to reason,\nenraging them, or seducing them.");

		private final String path;
		private final String description;
		private Stat(String path, String description){
			this.path = path;
			this.description = description;
		}
		public String getPath() {
			return path;
		}
		public String getDescription() {
			return description;
		}
	}
}

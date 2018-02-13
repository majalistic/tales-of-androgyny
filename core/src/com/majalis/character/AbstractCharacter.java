package com.majalis.character;

import com.majalis.asset.AssetEnum;
import com.majalis.character.Attack.AttackHeight;
import com.majalis.character.Attack.Status;
import com.majalis.character.Item.Accessory;
import com.majalis.character.Item.AccessoryType;
import com.majalis.character.Item.ChastityCage;
import com.majalis.character.Item.EffectType;
import com.majalis.character.Item.ItemEffect;
import com.majalis.character.Item.Misc;
import com.majalis.character.Item.MiscType;
import com.majalis.character.Item.Plug;
import com.majalis.character.Item.Weapon;
import com.majalis.character.PlayerCharacter.Bootyliciousness;
import com.majalis.save.MutationResult;
import com.majalis.save.MutationResult.MutationType;
import com.majalis.save.SaveManager.JobClass;
import com.majalis.technique.ClimaxTechnique.ClimaxType;
import com.majalis.technique.SpellEffect;
import com.majalis.technique.Bonus;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.OrderedMap;
/*
 * Abstract character class, both enemies and player characters extend this class
 */
public abstract class AbstractCharacter extends Actor {
	
	// some of these ints will be enumerators or objects in time
	/* permanent stats */
	protected String label;
	protected PronounSet pronouns;
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
	protected int heartbeat;
	
	protected IntArray healthTiers; // total these to receive maxHealth, maybe cache it when this changes
	protected IntArray staminaTiers; // total these to receive maxStamina, maybe cache it when this changes
	protected IntArray manaTiers; // total these to receive maxMana, maybe cache it when this changes
	
	protected ObjectMap<String, Integer> perks;
	
	/* morphic stats */
	protected int currentHealth;
	protected int currentStamina;
	protected int currentMana; // mana might be replaced with spell slots that get refreshed
	
	protected Stability stability;
	protected int focus;
	protected int fortune;
	
	protected Arousal arousal;
	protected int lust; // legacy
	//protected int lust; 
	protected int knotInflate;
	
	protected Weapon weapon;
	public Armor shield;
	protected Armor armor;
	protected Armor legwear;
	protected Armor underwear;
	protected Armor headgear;
	protected Armor armwear;
	protected Armor footwear;

	public Accessory firstAccessory;
	// public Accessory secondAccessory;
	
	protected Plug plug;
	protected ChastityCage cage;
	
	protected Weapon disarmedWeapon;
	
	// public Hole hole;  // bowels contents, tightness, number of copulations, number of creampies, etc. 
	// public Mouth mouth; 
	protected PhallusType phallus;	
	
	protected int buttful;
	protected int mouthful;
	
	protected Bootyliciousness bootyliciousness;
	
	protected Stance stance;
	protected Stance oldStance;
	protected GrappleStatus grappleStatus;
	protected ObjectMap<String, Integer> statuses; // status effects will be represented by a map of Enum to Status object
	
	protected Array<Item> inventory;
	protected int food;

	/* Constructors */
	protected AbstractCharacter() { }
	protected AbstractCharacter(boolean defaultValues) {
		if (defaultValues) {
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
			stability = Stability.Surefooted;
			focus = fortune = 10;
			stance = Stance.BALANCED;
			phallus = PhallusType.NORMAL;
			perks = new ObjectMap<String, Integer>();
			statuses = new ObjectMap<String, Integer>();
			grappleStatus = GrappleStatus.NULL;
		}
	}
	
	protected abstract Technique getTechnique(AbstractCharacter target);
	
	protected IntArray getDefaultHealthTiers() { return new IntArray(new int[]{10, 10, 10, 10}); }
	protected IntArray getDefaultStaminaTiers() { return new IntArray(new int[]{5, 5, 5, 5}); }
	protected IntArray getDefaultManaTiers() { return new IntArray(new int[]{0}); }
	
	public ObjectMap<String, Integer> getStatuses() { return statuses; }
	
	public int getMaxHealth() { return getMax(healthTiers); }
	public int getMaxStamina() { return getMax(staminaTiers); }
	public int getMaxMana() { return getMax(manaTiers); }
	public int getMaxStability() { return getAgility() * 3 + 9; }
	protected int getMax(IntArray tiers) {
		int max = 0;
		for (int ii = 0; ii < tiers.size; ii++) {
			max += tiers.get(ii);
		}
		return max;
	}
	
	public Stance getStance() { return stance; }
	
	public void setStance(Stance stance) { this.stance = stance; }
	
	public GrappleStatus getGrappleStatus() { return grappleStatus; } // should be passed into character state
	public void setGrappleStatus(GrappleStatus status) { grappleStatus = status; } // for battle factory
	
	public int getCurrentHealth() { return currentHealth; }
	
	public int getCurrentStamina() { return currentStamina; }
	
	public int getCurrentMana() { return currentMana; }
		
	public float getHealthPercent() { return currentHealth / (getMaxHealth() * 1.0f); }
	
	public float getStaminaPercent() { return currentStamina / (getMaxStamina() * 1.0f); }
	
	public float getBalancePercent() { return stability.getPercent(); }
	
	public float getManaPercent() { return currentMana / (getMaxMana() * 1.0f); }
	
	public AssetDescriptor<Texture> getHealthDisplay() { 
		switch (getHealthDegradation()) {
			case 0: return AssetEnum.HEALTH_ICON_0.getTexture();
			case 1: return AssetEnum.HEALTH_ICON_1.getTexture();
			case 2: return AssetEnum.HEALTH_ICON_2.getTexture();
			case 3: return AssetEnum.HEALTH_ICON_3.getTexture();
			case 4: return AssetEnum.HEALTH_ICON_3.getTexture();
			case 5: return AssetEnum.HEALTH_ICON_3.getTexture();
			default: return AssetEnum.HEALTH_ICON_3.getTexture();
		}
	}
	
	public AssetDescriptor<Texture> getStaminaDisplay() { 
		switch (getStaminaDegradation()) {
			case 0: return AssetEnum.STAMINA_ICON_0.getTexture();
			case 1: return AssetEnum.STAMINA_ICON_1.getTexture();
			case 2: return AssetEnum.STAMINA_ICON_2.getTexture();
			case 3: return AssetEnum.STAMINA_ICON_3.getTexture();
			case 4: return AssetEnum.STAMINA_ICON_3.getTexture();
			default: return AssetEnum.STAMINA_ICON_3.getTexture();
		}
	}
	
	public AssetDescriptor<Texture> getBalanceDisplay() { 
		return stability.getDisplay();
	}
	
	public AssetDescriptor<Texture> getManaDisplay() {  
		switch (4 - (int)(getManaPercent() * 100)/ 25) {
			case 0: return AssetEnum.MANA_ICON_0.getTexture();
			case 1: return AssetEnum.MANA_ICON_1.getTexture();
			case 2: return AssetEnum.MANA_ICON_2.getTexture();
			case 3: return AssetEnum.MANA_ICON_3.getTexture();
			default: return AssetEnum.MANA_ICON_3.getTexture();
		}
	}
	
	public Stability getStability() { return stability; }
	
	public Array<MutationResult> modHealth(int healthMod) { return modHealth(healthMod, ""); }
	
	public Array<MutationResult> modHealth(int healthMod, String cause) { 
		int healthChange = this.currentHealth;
		this.currentHealth += healthMod; 
		if (currentHealth > getMaxHealth()) {
			currentHealth = getMaxHealth();  
		}
			
		healthChange = this.currentHealth - healthChange;
		// if need to track overkill arises, can do so here - marking an overkill var with the amount of overkill
		if (this.currentHealth < 0) this.currentHealth = 0; 
		return healthChange == 0 ? new Array<MutationResult>() : new Array<MutationResult>(new MutationResult[]{new MutationResult(healthChange > 0 ? "Gained " + healthChange + " health"  + (cause.isEmpty() ? "!" : " " + cause + "!") : "You take " + -healthChange + " damage" + (cause.isEmpty() ? "!" : " " + cause + "!"), healthChange, MutationType.HEALTH)}); 
	}
	
	protected int getStaminaRegen() { return Math.max(getEndurance() / (isGravitied() ? 4 : 2), 0); }
	
	protected int getStabilityRegen() { return getAgility() / 2 + perks.get(Perk.QUICKFOOTED.toString(), 0); }
	
	public String getLabel() { return label; }
	
	protected Boolean getSecondPerson() { return secondPerson; }
	
	protected void setHealthToMax() { currentHealth = getMaxHealth(); }
	
	protected void setStaminaToMax() { currentStamina = getMaxStamina(); }
	
	protected void modStamina(int staminaMod) { this.currentStamina += staminaMod; if (currentStamina > getMaxStamina()) currentStamina = getMaxStamina(); }
	
	protected void setStabilityToMax() { stability = Stability.Surefooted; }
	
	protected void setStabilityToMin() { stability = Stability.Dazed; }
	
	protected void modStability(int stabilityMod) { stability = stability.shift(stabilityMod); if (stance.isIncapacitating() && !stability.isDown()) stability = Stability.Down; }
	
	protected void setManaToMax() { currentMana = getMaxMana(); }
	
	protected void modMana(int manaMod) { this.currentMana += manaMod; if (currentMana > getMaxMana()) currentMana = getMaxMana(); if (currentMana < 0) currentMana = 0; }

	protected int getStrength() { return Math.max(((baseStrength + itemBonus(Stat.STRENGTH) + getStrengthBuff()) - (getHealthDegradation() / 2 + getStaminaDegradation() / 2 + getLustDegradation() / 2))/(strengthDebuffed() ? 2 : 1), 0); }
	
	protected int getStrengthBuff() { return statuses.get(StatusType.STRENGTH_BUFF.toString(), 0); }
	protected int getEnduranceBuff() { return statuses.get(StatusType.ENDURANCE_BUFF.toString(), 0); }
	protected int getAgilityBuff() { return statuses.get(StatusType.AGILITY_BUFF.toString(), 0); }
	
	protected boolean strengthDebuffed() { return statuses.get(StatusType.STRENGTH_DEBUFF.toString(), 0) > 0; }
	protected boolean isGravitied() { return statuses.get(StatusType.STRENGTH_DEBUFF.toString(), 0) > 0; }
	
	protected int stepDown(int value) { if (value < 3) return value; else if (value < 7) return 3 + (value - 3)/2; else return 5 + (value - 7)/3; } 
	
	protected int getEndurance() { return Math.max((baseEndurance + itemBonus(Stat.ENDURANCE) + getEnduranceBuff()) - (getHealthDegradation()), 0); }
	
	protected int getAgility() { return Math.max((baseAgility + itemBonus(Stat.AGILITY) + getAgilityBuff()) - (getHealthDegradation() + getStaminaDegradation() + getCumInflation()), 0); }

	protected int getPerception() { return Math.max(basePerception + itemBonus(Stat.PERCEPTION), 0); }

	protected int getMagic() { return Math.max(baseMagic + itemBonus(Stat.MAGIC), 0); }

	protected int getCharisma() { return Math.max(baseCharisma + itemBonus(Stat.CHARISMA), 0); }
	
	public int getLewdCharisma() { return getCharisma() + perks.get(Perk.EROTIC.toString(), 0) * 2; }	
	
	protected int itemBonus(Stat stat) { return firstAccessory != null && firstAccessory.equals(new Accessory(AccessoryType.STATBOOSTER, stat)) ? 1 : 0; }
	
	protected int getBaseDefense() { return Math.max(baseDefense, 0); }
	protected int getTraction() { return 2; }
	
	// temporary for battle coherence
	public int getArmorScore() { return armor != null && armor.getDurability() > 0 ? armor.getShockAbsorption(): 0; }
	public int getLegwearScore() { 	return legwear != null ? legwear.getShockAbsorption() : 0; }
	public int getUnderwearScore() { 	return underwear != null ? underwear.getShockAbsorption() : 0; }
	
	public int getHealthDegradation() { return getDegradation(healthTiers, currentHealth); }
	public int getStaminaDegradation() { return getDegradation(staminaTiers, currentStamina); }
	public int getLustDegradation() { return arousal.getLust() >= 100 ? 4 : arousal.getLust() >= 75 ? 3 : arousal.getLust() >= 50 ? 2 : arousal.getLust() >= 25 ? 1 : 0; }
	public int getCumInflation() { return buttful >= 20 || mouthful >= 20 ? 2 : buttful >=10 || mouthful >= 10 || fullOfEggs() ? 1 : 0; } 
	
	public String getStatusBlurb() {
		String blurb = strengthDebuffed() ? "Weakening Curse\n" : "";
		switch(getHealthDegradation()) {
			case 3: blurb += "Injured (-3)\n"; break;
			case 2: blurb += "Wounded (-2)\n"; break;
			case 1: blurb += "Hurt (-1)\n"; break;
			default:
		}
		switch(getStaminaDegradation()) {
			case 3: blurb += "Breathless (-3)\n"; break;
			case 2: blurb += "Gasping (-2)\n"; break;
			case 1: blurb += "Winded (-1)\n"; break;
			default:
		}
		switch(getLustDegradation()) {
			case 4: blurb += "Cumdrunk (-2)\n"; break;
			case 2: blurb += "Lusty (-1)\n"; break;
			default:
		}
		
		return blurb;
	}
	public Armor getArmor() { return armor; }
	public Armor getLegwear() { return legwear; }
	public Armor getUnderwear() { return underwear; }
	public Armor getShield() { return shield; }
	public Armor getHeadgear() { return headgear; }
	public Armor getArmwear() { return armwear; }
	public Armor getFootwear() { return footwear; }
	public Accessory getFirstAccessory() { return firstAccessory; }
	
	public String getArmorStatus() { return getAnyArmorStatus(armor); }
	public String getLegwearStatus() { return getAnyArmorStatus(legwear); }
	public String getUnderwearStatus() { return getAnyArmorStatus(underwear); }
	public String getShieldStatus() { return getAnyArmorStatus(shield); }
	private String getAnyArmorStatus(Armor armor) { return armor == null ? "" : armor.getName() + "\nCurrent damage absorption provided: " + armor.getShockAbsorption() + "\nCurrent durability: " + armor.getDurability() + "\n\n" + armor.getDescription(); }
	
	protected int getDegradation(IntArray tiers, int currentValue) {
		int numTiers = tiers.size;
		int value = currentValue;
		for (int tier : tiers.items) {
			value -= tier;
			numTiers--;
			if (value <= 0) return numTiers;
		}
		return numTiers;
	}
	
	// this method can be removed, as the CharacterState could dictate what modifiers are applied to the stamina cost of a technique
	protected int getStaminaMod(Technique technique) {
		int staminaMod = technique.getStaminaCost() * (isGravitied() ? 2 : 1);
		if (staminaMod >= 0) {
			staminaMod -= getStaminaRegen();
			if (staminaMod < 0) staminaMod = 0;
		}
		else {
			staminaMod -= getStaminaRegen();
		}
		return staminaMod;
	}
	
	// right now this and "doAttack" handle once-per-turn character activities
	public void extractCosts(Technique technique) {
		oldStance = stance;
		stance = !technique.getStance().isNull() ? technique.getStance() : stance;
		if (oldStance != Stance.PRONE && oldStance != Stance.SUPINE && (stance == Stance.PRONE || stance == Stance.SUPINE)) {
			setStabilityToMin();
		}
		
		int staminaMod = getStaminaMod(technique); 
		modStamina(-staminaMod);
		modStability(getStabilityRegen() - technique.getStabilityCost());
		modMana(-technique.getManaCost());
		
		Array<String> toRemove = new Array<String>();
		// statuses degrade with time in a general way currently
		for(String key: statuses.keys()) {
			StatusType type = StatusType.valueOf(key);
			if (!type.degrades()) continue;
			int value = statuses.get(key) - 1;
			statuses.put(key, value);
			if (value <= 0) {
				toRemove.add(key);
			}
		}
		for(String key: toRemove) {
			statuses.remove(key);
		}
	}
	
	private int getBloodLossDamage() {
		return Math.max(0, (statuses.get(StatusType.BLEEDING.toString(), 0) - getEndurance()) / 3);
	}
	
	protected CharacterState getCurrentState(AbstractCharacter target) {		
		return new CharacterState(getStats(), getRawStats(), weapon, shield, stability.lowBalance(), currentMana, enemyType == null ? true : enemyType.isCorporeal(), enemyType == null ? PhallusType.SMALL : enemyType.getPhallusType(), this, target);
	}
	
	protected boolean alreadyIncapacitated() {
		return stance.isIncapacitatingOrErotic();
	}
	
	protected boolean wasIncapacitated() {
		return oldStance != null ? oldStance.isIncapacitatingOrErotic() : false;
	}
	
	protected boolean hasGrappleAdvantage() {
		return grappleStatus.isAdvantage();
	}
	
	private String repairArmor(int power) {
		String result = "";

		if (armor != null) {
			armor.modDurability(power);
			result += armor.getName() + " durability improved by " + power + "!";
		}
		if (legwear != null) {
			legwear.modDurability(power);
			result += legwear.getName() + " durability improved by " + power + "!";
		}
		
		return result;
	}
	
	public Attack doAttack(Attack resolvedAttack) {
		heartbeat++;
		int bleedDamage = getBloodLossDamage();
		if (bleedDamage > 0) {
			resolvedAttack.addAttackerResults(modHealth(-getBloodLossDamage()));
			resolvedAttack.addMessage(label + (secondPerson ? " bleed" : " bleeds") + " out for " + getBloodLossDamage() + " damage!");
		}
		
		if (!resolvedAttack.isSuccessful()) {
			resolvedAttack.addMessage(resolvedAttack.getUser() + " used " + resolvedAttack.getName() + (resolvedAttack.getStatus() == Status.MISSED ? " but missed!" : (resolvedAttack.getStatus() == Status.EVADED ? " but was evaded!" : resolvedAttack.getStatus() == Status.FIZZLE ? " but the spell fizzled!" : "! FAILURE!")));
			
			if ((resolvedAttack.getStatus() == Status.MISSED || resolvedAttack.getStatus() == Status.EVADED) && enemyType == EnemyEnum.HARPY && stance == Stance.FELLATIO && resolvedAttack.getForceStance() == Stance.FELLATIO_BOTTOM) {
				resolvedAttack.addMessage(properCase(pronouns.getNominative()) + " crashes to the ground!");
				stance = Stance.PRONE;
			}
			else if(resolvedAttack.getForceStance() != null) {
				stance = oldStance;
			}	
			return resolvedAttack;
		}
		
		if (resolvedAttack.getItem() != null) {
			UseItemEffect effect = consumeItem(resolvedAttack.getItem(), true);
			resolvedAttack.addMessage(effect.resultDisplay);
			resolvedAttack.addAttackerResults(effect.results);
			resolvedAttack.addAttackerResults(new Array<MutationResult>(new MutationResult[]{new MutationResult("You used a " + resolvedAttack.getItem().getName() + ".")}));
		}
		else if (!resolvedAttack.isAttack() && !resolvedAttack.isClimax() && resolvedAttack.getSex().isEmpty()) {
			resolvedAttack.addMessage(resolvedAttack.getUser() + " used " + resolvedAttack.getName() + "!");
		}
		
		if (resolvedAttack.isSpell() && resolvedAttack.getSpellEffect() == SpellEffect.ARMOR_REPAIR) {
			resolvedAttack.addMessage(repairArmor(getMagic()));
		}
		
		if (resolvedAttack.isHealing()) {
			resolvedAttack.addAttackerResults(modHealth(resolvedAttack.getHealing()));
			resolvedAttack.addMessage(resolvedAttack.getUser() + " heal" + (secondPerson ? "" : "s" ) + " for " + resolvedAttack.getHealing()+"!");
		}
		Buff buff = resolvedAttack.getSelfEffect();
		if (buff != null) {
			statuses.put(buff.type.toString(), buff.power);
		}
		if (enemyType != null) {
			if (resolvedAttack.getForceStance() == Stance.DOGGY_BOTTOM || resolvedAttack.getForceStance() == Stance.ANAL_BOTTOM || resolvedAttack.getForceStance() == Stance.STANDING_BOTTOM) {
				if (enemyType == EnemyEnum.OGRE) {
					resolvedAttack.addMessage(properCase(pronouns.getPossessive()) + " tremendous, fat cock visibly bulges out your stomach!");		
					resolvedAttack.addMessage("You are being anally violated by an ogre!");
				}
				else {
					resolvedAttack.addMessage("You are being anally violated!");
					resolvedAttack.addMessage("Your hole is stretched by " + pronouns.getPossessive() + " fat dick!");
					resolvedAttack.addMessage("Your hole feels like it's on fire!");
					resolvedAttack.addMessage(properCase(pronouns.getPossessive()) + " cock glides smoothly through your irritated anal mucosa!");
					resolvedAttack.addMessage(properCase(pronouns.getPossessive()) + " rhythmic thrusting in and out of your asshole is emasculating!");
					resolvedAttack.addMessage("You are red-faced and embarassed because of " + pronouns.getPossessive() + " butt-stuffing!");
					resolvedAttack.addMessage("Your cock is ignored!");
				}
			}		
			else if(resolvedAttack.getForceStance() == Stance.FELLATIO_BOTTOM || resolvedAttack.getForceStance() == Stance.SIXTY_NINE_BOTTOM) {
				if (enemyType == EnemyEnum.HARPY) {
					resolvedAttack.addMessage(properCase(pronouns.getNominative()) + " tastes awful!");
					resolvedAttack.addMessage("You learned Anatomy (Harpy)!");
					resolvedAttack.addMessage("It blew past your lips!");
					resolvedAttack.addMessage("The harpy is holding your head in place with " + pronouns.getPossessive() + " talons and balancing herself with her wings!");
					resolvedAttack.addMessage(properCase(pronouns.getNominative()) + " flaps violently while humping your face!  Her cock tastes awful!");
				}
				else {
					resolvedAttack.addMessage(properCase(pronouns.getNominative()) + " stuffs her cock into your face!");
					resolvedAttack.addMessage("You suck on " + pronouns.getPossessive() + " cock!");
					resolvedAttack.addMessage(properCase(pronouns.getNominative()) + " licks " + (pronouns.getPossessive()) + " lips!");
				}
				if (resolvedAttack.getForceStance() == Stance.SIXTY_NINE_BOTTOM) {
					resolvedAttack.addMessage(properCase(pronouns.getNominative()) + " deepthroats your cock!");
					resolvedAttack.addMessage(properCase(pronouns.getNominative()) + " pistons " + pronouns.getPossessive() + " own cock in and out of your mouth!");
				}
			}
			else if (resolvedAttack.getForceStance() == Stance.FACE_SITTING_BOTTOM) {
				resolvedAttack.addMessage(properCase(pronouns.getNominative()) + " rides your face!");
				resolvedAttack.addMessage("You receive a faceful of ass!");
			}
			else if (resolvedAttack.getForceStance() == Stance.KNOTTED_BOTTOM) {
				if (knotInflate == 0) {
					resolvedAttack.addMessage(properCase(pronouns.getPossessive()) + " powerful hips try to force something big inside!");
					resolvedAttack.addMessage("You struggle... but can't escape!");
					resolvedAttack.addMessage(properCase(pronouns.getPossessive()) + " grapefruit-sized knot slips into your rectum!  You take 4 damage!");
					resolvedAttack.addMessage("You learned about Anatomy(Wereslut)! You are being bred!");
					resolvedAttack.addMessage("Your anus is permanently stretched!");
				}
				else if (knotInflate < 3) {
					resolvedAttack.addMessage(properCase(pronouns.getPossessive()) + " tremendous knot is still lodged in your rectum!");
					resolvedAttack.addMessage("You can't dislodge it; it's too large!");
					resolvedAttack.addMessage("You're drooling!");
					resolvedAttack.addMessage(properCase(pronouns.getPossessive()) + " fat thing is plugging your shithole!");					
				}
				else {
					resolvedAttack.addMessage("The battle is over, but your ordeal has just begun!");
					resolvedAttack.addMessage("You are about to be bred like a bitch!");
					resolvedAttack.addMessage(properCase(pronouns.getNominative()) + "'s going to ejaculate her runny dog cum in your bowels!");	
				}
				knotInflate++;
			}
			else if (resolvedAttack.getForceStance() == Stance.MOUTH_KNOTTED_BOTTOM) {
				if (knotInflate == 0) {
					resolvedAttack.addMessage(properCase(pronouns.getPossessive()) + " powerful hips try to force something big inside!");
					resolvedAttack.addMessage("You struggle... but can't escape!");
					resolvedAttack.addMessage(properCase(pronouns.getPossessive()) + " melon-sized knot forces your jaw open! You take 4 damage!");
					resolvedAttack.addMessage("You learned about Anatomy(Wereslut)! You are being bred!");
				}
				else if (knotInflate < 3) {
					resolvedAttack.addMessage(properCase(pronouns.getPossessive()) + " tremendous knot is still stuck behind your teeth!");
					resolvedAttack.addMessage("You can't dislodge it; it's too large!");
					resolvedAttack.addMessage("You're drooling!");					
				}
				else {
					resolvedAttack.addMessage("The battle is over, but your ordeal has just begun!");
					resolvedAttack.addMessage("You are about to swallow doggy cum!");
				}
				knotInflate++;
			}
			else if (resolvedAttack.getForceStance() == Stance.OVIPOSITION_BOTTOM) {
				if (knotInflate == 0) {
					
				}
				else if (knotInflate < 3) {
									
				}
				else {
					resolvedAttack.addMessage("The battle is over, but your ordeal has just begun!");
					resolvedAttack.addMessage("You are full of " + pronouns.getPossessive() + " eggs!");
				}
				knotInflate++;
			}
		}
		
		// all climax logic should go here
		if (resolvedAttack.isClimax()) {
			resolvedAttack.addMessage(climax());
		}
		
		for (Bonus bonus : resolvedAttack.getBonuses()) {
			String bonusDescription = bonus.getDescription(label);
			if (bonusDescription != null) {
				resolvedAttack.addMessage(bonusDescription);
			}
		}
		
		return resolvedAttack;
	}
	private Armor getArmorHit(AttackHeight height) {
		return height != AttackHeight.LOW ? armor : legwear != null && legwear.getShockAbsorption() > 0 ? legwear : underwear;
	}
	
	private int getShockAbsorption(Armor armor) {
		return armor != null ? armor.getShockAbsorption() : 0;
	}
	
	public static class AttackResult {
		private final Array<String> messages;
		private final Array<String> dialog;
		private final Array<MutationResult> attackerResults;
		private final Array<MutationResult> defenderResults;
		
		protected AttackResult(Array<String> messages, Array<String> dialog, Array<MutationResult> attackerResults, Array<MutationResult> defenderResults) {
			this.messages = messages;
			this.dialog = dialog;
			this.attackerResults = attackerResults;
			this.defenderResults = defenderResults;
		}
		
		public Array<String> getMessages() { return messages; }
		public Array<String> getDialog() { return dialog; }
		public Array<MutationResult> getAttackerResults() { return attackerResults; }
		public Array<MutationResult> getDefenderResults() { return defenderResults; }
	}
	
	// return an array of array of strings and mutation results packaged together, save the mutation results into the battle results but that doesn't work either because doAttack can also cause mutations
	public AttackResult receiveAttack(Attack attack) {
		Array<String> result = attack.getMessages();
		boolean knockedDown = false;
		grappleStatus = attack.getGrapple();
		
		if (attack.isSuccessful()) {
			if (attack.getForceStance() == Stance.DOGGY_BOTTOM && bootyliciousness != null)
				result.add("They slap their hips against your " + bootyliciousness.toString().toLowerCase() + " booty!");
			
			if (attack.isAttack() || attack.isClimax() || !attack.getSex().isEmpty()) {
				result.add(attack.getUser() + " used " + attack.getName() +  " on " + (secondPerson ? label.toLowerCase() : label) + "!");
			}
			
			if (attack.getForceStance() == Stance.BALANCED) {
				result.add(attack.getUser() + " broke free!");
				if (stance == Stance.FELLATIO_BOTTOM) {
					result.add("It slips out of your mouth and you get to your feet!");
				}
				else if (stance == Stance.SIXTY_NINE_BOTTOM) {
					result.add("You spit out their cock and push them off!");
				}
				else if (stance == Stance.HANDY_BOTTOM) {
					
				}
				else if (stance.isAnalReceptive()) {
					result.add("It pops out of your ass and you get to your feet!");
				}
			}
			
			Buff buff = attack.getEnemyEffect();
			if (buff != null) {
				statuses.put(buff.type.toString(), buff.power);
			}

			int shieldDamage = attack.getShieldDamage();
			
			if (attack.getStatus() == Status.BLOCKED) {
				double blockMod = attack.getBlockMod();
				if (shield != null && shield.getDurability() > 0) {
					result.add((blockMod < .1 ? "The blow strikes off the shield!" : blockMod < .3 ? "The blow is mostly blocked by the shield!" : blockMod < .6 ? "The blow is half-blocked by the shield!" : "The blow is barely blocked by the shield!") + "\nIt deals " + shieldDamage + " damage to it!");
					shield.modDurability(-shieldDamage);
					if (shield.getDurability() == 0) result.add("The shield is broken!");
				}
			}
			
			if (attack.getStatus() == Status.PARRIED) {
				double blockMod = attack.getBlockMod();
				result.add((blockMod < .1 ? "The blow is parried away!" : blockMod < .3 ? "The blow is mostly deflected by a parry!" : blockMod < .6 ? "The blow is half-deflected by a parry!" : "The blow is barely blocked by a parry!"));
			}
			

			int damage = attack.getDamage();
			Armor hitArmor = getArmorHit(attack.getAttackHeight());
			if (!attack.ignoresArmor()) {
				damage -= getBaseDefense() + getShockAbsorption(hitArmor);
			}
			
			if (damage > 0) {	
				attack.addDefenderResults(modHealth(-damage));				
				result.add("The blow strikes for " + damage + " damage!");
				if (!(attack.ignoresArmor() || ((hitArmor == null || hitArmor.getDurability() == 0)))) {
					result.add("The blow strikes off the armor!");
				}
			}
			
			if (attack.ignoresArmor() || ((hitArmor == null || hitArmor.getDurability() == 0))) {
				int bleed = attack.getBleeding();
				if (bleed > 0 && canBleed()) {
					result.add("It opens wounds! +" + bleed + " blood loss!");
					statuses.put(StatusType.BLEEDING.toString(), statuses.get(StatusType.BLEEDING.toString(), 0) + bleed);
				}
			}
			
			int plugRemove = attack.plugRemove();
			if (plugRemove > 0) {
				if (legwear != null && legwear.getShockAbsorption() > 0 && legwear.coversAnus()) {
					result.add("They pull down your " + legwear.getName() + "!");
					setLegwear(legwear, false);
				}
				else if (underwear != null && underwear.getShockAbsorption() > 0 && underwear.coversAnus()) {
					result.add("They pull down your " + underwear.getName() + "!");
					setUnderwear(underwear, false);
				}
				else {
					result.add("They pull out your " + plug.getName() + "!");
					setPlug(plug, false); // unequip your plug
				}
			}
			
			int knockdown = attack.getForce();
			knockdown -= getTraction();
			
			if (knockdown > 0) {
				if (!alreadyIncapacitated() && !wasIncapacitated()) {
					modStability(-knockdown);
					result.add("It's a solid blow! It reduces balance by " + knockdown + "!");
					if (stability.isDown()) {
						if (enemyType == EnemyEnum.OGRE) {
							result.add(label + (secondPerson ? " are " : " is ") + "knocked to their knees!");
							stability = Stability.Teetering;
							stance = Stance.KNEELING;
						}
						else {
							result.add(label + (secondPerson ? " are " : " is ") + "knocked to the ground!");
							setStabilityToMin();
							stance = Stance.SUPINE;
							
						}
						knockedDown = true;
					}
				}
			}
			
			int trip = attack.getTrip();
			if (trip >= 100) {
				if (!alreadyIncapacitated()) {
					setStabilityToMin();
					stance = Stance.PRONE;
					result.add(label + (secondPerson ? " are " : " is ") + "tripped and "+ (secondPerson ? "fall" : "falls") +" prone!");
					knockedDown = true;
				}
			}
			
			int armorSunder = attack.getArmorSunder();
			if (armorSunder > 0) {
				if (hitArmor != null && hitArmor.getDurability() > 0) {
					result.add("It's an armor shattering blow! It reduces " + hitArmor.getName() + " durability by " + (armorSunder > hitArmor.getDurability() ? hitArmor.getDurability() : armorSunder) + "!");
					hitArmor.modDurability(-armorSunder);
					if (hitArmor.getDurability() == 0) result.add("The " + hitArmor.getName() + " is broken!");
				}
			}
			
			int gutcheck = attack.getGutCheck();
			if (gutcheck > 0) {
				if (!alreadyIncapacitated()) {
					currentStamina -= gutcheck;
					result.add("It's winds " + (secondPerson ? "you" : "them") + "! It reduces stamina by " + gutcheck + "!");
					if (currentStamina <= 0 && grappleStatus == GrappleStatus.NULL) {
						result.add(label + (secondPerson ? " fall " : " falls ") + "to the ground!");
						setStabilityToMin();
						stance = Stance.PRONE;
						knockedDown = true;
					}
				}
			}
			
			int disarm = attack.getDisarm();
			if (disarm > 0) {
				if (disarm >= 100) {
					if (disarm()) {
						result.add((secondPerson ? "You are " : label + " is ") + "disarmed!");
					}
				}
			}
			
			Stance forcedStance = attack.getForceStance();
			if (forcedStance != null) {
				if (stance != forcedStance) { 
					result.add(label + (secondPerson ? " are " : " is ") + "forced into " + forcedStance.getLabel() + " stance!");
					stance = forcedStance;
					if (forcedStance == Stance.PRONE || forcedStance == Stance.SUPINE) {
						setStabilityToMin();
					}
					else if (forcedStance == Stance.KNEELING && stability.isGood()) {
						stability = Stability.Unstable;
					}
				}
			}
			SexualExperience sex = attack.getSex();
			SexualExperience selfSex = attack.getSelfSex();

			if ((enemyType != EnemyEnum.GOLEM || arousal.isErect()) && enemyType != EnemyEnum.QUETZAL) {
				if (!sex.isEmpty() || !selfSex.isEmpty()) {
					int formerLust = arousal.getLust();
					String lustIncrease = increaseLust(sex, selfSex);
					if (lustIncrease != null) result.add(lustIncrease);
					int lustChange = arousal.getLust() - formerLust;
					if (sex.isTeasing()) result.add(label + (secondPerson ? " are seduced" : " is seduced") + "! " + (lustChange > 0 ? ((secondPerson ? " Your " : " Their ") + "lust raises by " + lustChange + "!") : (secondPerson ? " You " : " They ") + "cum!"));
				}	
			}
			else if (enemyType == EnemyEnum.QUETZAL) {
				arousal.increaseArousal(selfSex, perks);
			}
			
			String internalShotText = null;
			if (attack.getClimaxType() == ClimaxType.ANAL) {
				Array<MutationResult> temp = fillButt(attack.getClimaxVolume());
				attack.addDefenderResults(temp);		
				if (temp.size > 0) internalShotText = temp.first().getText();
			}
			else if (attack.getClimaxType() == ClimaxType.ORAL) {
				Array<MutationResult> temp = fillMouth(1);
				attack.addDefenderResults(temp);	
				if (temp.size > 0) internalShotText = temp.first().getText();
			}
			if (internalShotText != null) result.add(internalShotText);
			
			if (buttful > 0 && !stance.isAnalReceptive()) result.add(getLeakMessage());
			if (mouthful > 0 && !stance.isOralReceptive()) result.add(getDroolMessage());
		}
		if (!alreadyIncapacitated() && !knockedDown) {
			if (enemyType == EnemyEnum.OGRE) {
				if (stability.isDown()) {
					stance = Stance.KNEELING;
					result.add(label + (secondPerson ? " lose your" : " loses their") + " footing and " + (secondPerson ? "trip" : "trips") + "!");
					stability = Stability.Teetering;
				}
				// you blacked out
				else if (currentStamina <= 0) {
					result.add(label + (secondPerson ? " run " : " runs ") + "out of breath and " + (secondPerson ? "collapse" : "collapses") + "!");
					stance = Stance.KNEELING;
					stability = Stability.Teetering;
				}
			}
			else {
				// you tripped
				if (stability.isDown() && grappleStatus == GrappleStatus.NULL) {
					stance = Stance.PRONE;
					result.add(label + (secondPerson ? " lose your" : " loses their") + " footing and " + (secondPerson ? "trip" : "trips") + "!");
					setStabilityToMin();
				}
				// you blacked out
				else if (currentStamina <= 0 && grappleStatus == GrappleStatus.NULL) {
					result.add(label + (secondPerson ? " run " : " runs ") + "out of breath and " + (secondPerson ? "collapse" : "collapses") + "!");
					stance = Stance.SUPINE;
					setStabilityToMin();
				}
			}
		}
		
		return new AttackResult(result, new Array<String>(), attack.getAttackerResults(), attack.getDefenderResults());
	}
	
	protected String increaseLust(SexualExperience ... sexes) {
		String spurt = "";
		arousal.increaseArousal(sexes, perks);
		if (arousal.isClimax() && stance.isEroticReceptive()) {
			spurt = climax();
		}
		return !spurt.isEmpty() ? spurt : null;
	}
	
	protected abstract String climax();
	protected boolean canBleed() { return true; }
	
	protected Array<MutationResult> getResult(String text) { return new Array<MutationResult>(new MutationResult[]{new MutationResult(text)}); }
	protected Array<MutationResult> getResult(String text, int mod, MutationType type) { return new Array<MutationResult>(new MutationResult[]{new MutationResult(text, mod, type)}); }
	
	public class UseItemEffect {
		private final String resultDisplay;
		private final Array<MutationResult> results;
		private UseItemEffect(String resultDisplay, Array<MutationResult> results) {
			this.resultDisplay = resultDisplay;
			this.results = results;
		}
		
		public String getResult() { return resultDisplay; }
		
	}
	
	public UseItemEffect consumeItem(Item item) { return consumeItem(item, false); }
	public UseItemEffect consumeItem(Item item, boolean combatUse) {
		ItemEffect effect = item.getUseEffect();
		Array<MutationResult> results = new Array<MutationResult>();
		if (effect == null) { return new UseItemEffect("Item cannot be used.", results); }
		String result = "";
		switch (effect.getType()) {
			case HEALING:
				int currentHealth = getCurrentHealth();
				results.addAll(modHealth(effect.getMagnitude()));
				result = "You used " + item.getName() + " and restored " + String.valueOf(getCurrentHealth() - currentHealth) + " health!";
				break;
			case MANA:
				int currentMana = getCurrentMana();
				modMana(effect.getMagnitude());
				result = "You used " + item.getName() + " and restored " + String.valueOf(getCurrentHealth() - currentMana) + " mana!";
				break;
			// this should perform buff stacking if need be - but these item buffs should be permanent until consumed
			case BONUS_STRENGTH:
				result = "You used " + item.getName() + " and temporarily increased Strength by " + effect.getMagnitude() + "!";
				statuses.put(StatusType.STRENGTH_BUFF.toString(), effect.getMagnitude());
				break;
			case BONUS_ENDURANCE:
				result = "You used " + item.getName() + " and temporarily increased Endurance by " + effect.getMagnitude() + "!";
				statuses.put(StatusType.ENDURANCE_BUFF.toString(), effect.getMagnitude());
				break;
			case BONUS_AGILITY:
				result = "You used " + item.getName() + " and temporarily increased Agility by " + effect.getMagnitude() + "!";
				statuses.put(StatusType.AGILITY_BUFF.toString(), effect.getMagnitude());
				break;
			case MEAT:
				result = "You ate the " + item.getName() + "! Hunger decreased by 5.";
				results.addAll(modFood(effect.getMagnitude()));
				break;
			case SPIDER:
				result = "You ate the " + item.getName() + "?! WHY?! Hunger decreased by 5, uhhhhhhh?";
				results.addAll(modFood(effect.getMagnitude()));
				break;
			case SLIME:
				result = "You ate the " + item.getName() + ", temporarily increasing defense by " + effect.getMagnitude() + ".";
				baseDefense += effect.getMagnitude();
			case BANDAGE:
				int currentBleed = statuses.get(StatusType.BLEEDING.toString(), 0);
				if (currentBleed != 0) {
					result = "You applied the " + item.getName() + " to staunch bleeding by " + Math.max(currentBleed, effect.getMagnitude()) + "!";
					statuses.put(StatusType.BLEEDING.toString(), Math.max(currentBleed - effect.getMagnitude(), 0));
				}
				break;
			case MAGIC:
			case KNOCKDOWN:
			case ARMOR_SUNDER:
				result = combatUse ? "You used " + (effect.getType() == EffectType.MAGIC ? "the frozen flame" : effect.getType() == EffectType.KNOCKDOWN ? "the wind scroll" : "the acid scroll") + "!" : "Nothing happened!"; 
				break;
			default:
				result = "Nothing happened!";
			
		}	
		if (effect.getType() != EffectType.GEM && (combatUse || (effect.getType() != EffectType.MAGIC && effect.getType() != EffectType.KNOCKDOWN && effect.getType() != EffectType.ARMOR_SUNDER))) {
			inventory.removeValue(item, true);
		}
		
		return new UseItemEffect(result, results);
	}
	
	public boolean disarm() {
		if (weapon != null && weapon.isDisarmable()) {
			disarmedWeapon = weapon;
			weapon = null;
			return true;
		}
		return false;
		
	}
	protected Array<MutationResult> fillMouth(int mouthful) {
		this.mouthful += mouthful;
		return new Array<MutationResult>();
	}
	protected Array<MutationResult> fillButt(int buttful) {
		this.buttful += buttful;
		return new Array<MutationResult>();
	}
	
	protected void drainMouth() {
		mouthful = 0;
	}
	
	protected void drainButt() {
		buttful--;
	}

	protected boolean fullOfEggs() { return false; }
	
	public AssetDescriptor<Texture> getCumInflationPath() {
		if (buttful >= 20) {
			return AssetEnum.STUFFED_BELLY.getTexture();
		}
		else if (buttful >= 10 || fullOfEggs()) {
			return AssetEnum.FULL_BELLY.getTexture();
		}
		else if (buttful >= 5 || mouthful >= 10) {
			return AssetEnum.BIG_BELLY.getTexture();
		}
		else {
			return AssetEnum.FLAT_BELLY.getTexture(); 
		}
	}
	
	protected abstract String getLeakMessage();
	protected abstract String getDroolMessage();
	
	protected OrderedMap<Stat, Integer> getStats() {
		OrderedMap<Stat, Integer> stats = new OrderedMap<Stat, Integer>();
		for (Stat stat: Stat.values()) {
			stats.put(stat, getStat(stat));
		}
		return stats;
	}
	
	protected ObjectMap<Stat, Integer> getRawStats() {
		ObjectMap<Stat, Integer> stats = new ObjectMap<Stat, Integer>();
		for (Stat stat: Stat.values()) {
			stats.put(stat, getRawStat(stat));
		}
		return stats;
	}
	
	protected int getStat(Stat stat) {
		return stepDown(getRawStat(stat));
	}
	

	public int getRawStat(Stat stat) {
		switch(stat) {
			case STRENGTH: return getStrength();
			case ENDURANCE: return getEndurance();
			case AGILITY: return getAgility();
			case PERCEPTION: return getPerception();
			case MAGIC: return getMagic();
			case CHARISMA: return getCharisma();
			default: return -1;
		}
	}
	
	public int getBaseStat(Stat stat) {
		switch(stat) {
			case STRENGTH: return getBaseStrength();
			case ENDURANCE: return getBaseEndurance();
			case AGILITY: return getBaseAgility();
			case PERCEPTION: return getBasePerception();
			case MAGIC: return getBaseMagic();
			case CHARISMA: return getBaseCharisma();
			default: return -1;
		}
	}
	
	private int getBaseCharisma() {
		return baseCharisma;
	}

	private int getBaseMagic() {
		return baseMagic;
	}

	private int getBasePerception() {
		return basePerception;
	}

	private int getBaseAgility() {
		return baseAgility;
	}

	private int getBaseEndurance() {
		return baseEndurance;
	}
	
	private int getBaseStrength() {
		return baseStrength;
	}
	
	public Weapon getWeapon() {
		return weapon;
	}
	
	public String getStanceTransform(Technique firstTechnique) {
		Stance newStance = firstTechnique.getStance();
		if (newStance.isNull() || (oldStance != null && oldStance == newStance)) {
			return "";
		}
		String stanceTransform = newStance.getLabel();
		String vowels = "aeiou";
		String article = vowels.indexOf(Character.toLowerCase(stanceTransform.charAt(0))) != -1 ? "an" : "a";
		return label + " adopt" + (secondPerson ? "" : "s") + " " + article + " " + stanceTransform + " stance! ";
 	}
	
	public AssetDescriptor<Texture> getLustImagePath() {
		if (isChastitied()) return phallus.getPhallusState(3);
		int lustLevel = arousal.getPhallusLevel();
		return phallus.getPhallusState(lustLevel);
	}
	
	public boolean outOfStamina(Technique technique) {
		return getStaminaMod(technique) >= currentStamina;
	}
	
	private Stability checkStability(int stabilityModifier) {
		Stability currentStability = stability;
		modStability(stabilityModifier);
		Stability resultingStability = stability;
		stability = currentStability;
		return resultingStability;
	}
	
	protected boolean outOfStability(Technique technique) {
		return checkStability(getStabilityRegen() - technique.getStabilityCost()).isDown();
	}
	
	public boolean outOfStaminaOrStability(Technique technique) {
		 return outOfStamina(technique) || outOfStability(technique);
	}

	public boolean lowStaminaOrStability(Technique technique) {
		return getStaminaMod(technique) >= currentStamina - 5 || checkStability(getStabilityRegen() - technique.getStabilityCost()).lowBalance();	
	}
	
	protected boolean lowStability() {
		return stability.lowBalance();
	}
	
	protected boolean isErect() {
		return arousal.isErect() && !isChastitied() && phallus != PhallusType.NONE;
	}
	
	public int getCurrentLust() { return arousal.getLust(); }
	
	public String getDefeatMessage() {
		return label + (secondPerson ? " are " : " is ") + "defeated!";
	}
	
	public Array<MutationResult> modFood(Integer foodMod) {
		int foodChange = food;
		food += foodMod; 
		Array<MutationResult> result = new Array<MutationResult>();
		Array<MutationResult> starve = new Array<MutationResult>();
		if (food < 0) {
			starve.addAll(modHealth(5 * food, "from starvation"));
			food = 0; 
		}

		foodChange = food - foodChange;
		
		if (foodChange != 0) {
			result.add(new MutationResult(foodChange > 0 ? "+" + foodChange + " fullness!" : "Hunger increases by " + -foodChange + "!", foodChange, MutationType.FOOD));
		}
		result.addAll(starve);
		return result;
	}
	
	protected int getClimaxVolume() {
		return 3;
	}
	
	public int getBleed() {
		return statuses.get(StatusType.BLEEDING.toString(), 0);
	}
	
	protected String properCase(String sample) {
		return sample.substring(0, 1).toUpperCase() + sample.substring(1);
	}
	
	public String setArmor(Item armor, boolean newItem) {
		if (armor == null) {
			this.armor = null;
			return "You unequipped your armor.";
		}
		if (newItem) inventory.add(armor);
		
		Armor equipArmor = (Armor) armor;
		boolean alreadyEquipped = equipArmor.equals(this.armor); 
		this.armor = alreadyEquipped ? null : equipArmor;
		return "You " + (alreadyEquipped ? "unequipped" : "equipped") + " the " + armor.getName() + ".";
	}
	
	public String setLegwear(Item armor, boolean newItem) {
		if (armor == null) {
			this.legwear = null;
			return "You unequipped your legwear.";
		}
		if (newItem) inventory.add(armor);
		Armor equipArmor = (Armor) armor;
		boolean alreadyEquipped = equipArmor.equals(this.legwear); 
		this.legwear = alreadyEquipped ? null : equipArmor;
		return "You " + (alreadyEquipped ? "unequipped" : "equipped") + " the " + armor.getName() + ".";
	}
	
	public String setUnderwear(Item armor, boolean newItem) {
		if (armor == null) {
			this.underwear = null;
			return "You unequipped your underwear.";
		}
		if (newItem) inventory.add(armor);
		Armor equipArmor = (Armor) armor;
		boolean alreadyEquipped = equipArmor.equals(this.underwear); 
		this.underwear = alreadyEquipped ? null : equipArmor;
		return "You " + (alreadyEquipped ? "unequipped" : "equipped") + " the " + armor.getName() + ".";
	}

	public String setShield(Item armor, boolean newItem) {
		if (armor == null) {
			this.shield = null;
			return "You unequipped your shield.";
		}
		if (newItem) inventory.add(armor);
		Armor equipShield = (Armor) armor;
		boolean alreadyEquipped = equipShield.equals(this.shield); 
		this.shield = alreadyEquipped ? null : equipShield;
		return "You " + (alreadyEquipped ? "unequipped" : "equipped") + " the " + armor.getName() + ".";
	}
	
	public String setArmwear(Item armor, boolean newItem) {
		if (armor == null) {
			this.armwear = null;
			return "You unequipped your armwear.";
		}
		if (newItem) inventory.add(armor);
		Armor equipArmwear = (Armor) armor;
		boolean alreadyEquipped = equipArmwear.equals(this.armwear); 
		this.armwear = alreadyEquipped ? null : equipArmwear;
		return "You " + (alreadyEquipped ? "unequipped" : "equipped") + " the " + armor.getName() + ".";
	}
	
	public String setFootwear(Item armor, boolean newItem) {
		if (armor == null) {
			this.footwear = null;
			return "You unequipped your armwear.";
		}
		if (newItem) inventory.add(armor);
		Armor equipFootwear = (Armor) armor;
		boolean alreadyEquipped = equipFootwear.equals(this.footwear); 
		this.footwear = alreadyEquipped ? null : equipFootwear;
		return "You " + (alreadyEquipped ? "unequipped" : "equipped") + " the " + armor.getName() + ".";
	}
	
	public String setHeadgear(Item armor, boolean newItem) {
		if (armor == null) {
			this.headgear = null;
			return "You unequipped your headwear.";
		}
		if (newItem) inventory.add(armor);
		Armor equipHeadgear = (Armor) armor;
		boolean alreadyEquipped = equipHeadgear.equals(this.headgear); 
		this.headgear = alreadyEquipped ? null : equipHeadgear;
		return "You " + (alreadyEquipped ? "unequipped" : "equipped") + " the " + armor.getName() + ".";
	}
	
	public String setAccessory(Item accessory, boolean newItem) {
		if (newItem) inventory.add(accessory);
		Accessory equipAccessory = (Accessory) accessory;
		boolean alreadyEquipped = equipAccessory.equals(this.firstAccessory); 
		this.firstAccessory = alreadyEquipped ? null : equipAccessory;
		return "You " + (alreadyEquipped ? "unequipped" : "equipped") + " the " + accessory.getName() + ".";
	}
	
	// should return Plugged property != null
	public boolean isPlugged() {
		return plug != null;
	}
	
	public String setPlug(Item plug, boolean newItem) {
		if (newItem) inventory.add(plug);
		Plug equipPlug = (Plug) plug;
		boolean alreadyEquipped = equipPlug.equals(this.plug); 
		this.plug = alreadyEquipped ? null : equipPlug;
		return "You " + (alreadyEquipped ? "unequipped" : "equipped") + " the " + plug.getName() + ".";
	}
	
	// should return Chastity property != null
	public boolean isChastitied() {
		return cage != null;
	}
	
	private boolean hasKey() { return inventory.contains(new Misc(MiscType.KEY), false); }
	
	// possibly rethink this - maybe equipped items shouldn't be "in" inventory?
	public String setCage(Item cage, boolean newItem) {
		if (newItem) inventory.add(cage);
		if (this.cage != null && !hasKey()) return "You cannot remove your chastity cage without a key!";
		ChastityCage equipCage = (ChastityCage) cage;
		boolean alreadyEquipped = equipCage.equals(this.cage); 
		this.cage = alreadyEquipped ? null : equipCage;
		return "You " + (alreadyEquipped ? "unequipped" : "equipped") + " the " + cage.getName() + ".";
	}
	
	public String getPhallusLabel() {
		return phallus.getLabel();
	}
	
	
	public enum PhallusType {
		CUTE(AssetEnum.SMALL_DONG_0, AssetEnum.SMALL_DONG_1, AssetEnum.SMALL_DONG_2, AssetEnum.SMALL_DONG_CHASTITY),
		TINY(AssetEnum.SMALL_DONG_0, AssetEnum.SMALL_DONG_1, AssetEnum.SMALL_DONG_2, AssetEnum.SMALL_DONG_CHASTITY),
		SMALL(AssetEnum.SMALL_DONG_0, AssetEnum.SMALL_DONG_1, AssetEnum.SMALL_DONG_2, AssetEnum.SMALL_DONG_CHASTITY),
		NORMAL(AssetEnum.LARGE_DONG_0, AssetEnum.LARGE_DONG_1, AssetEnum.LARGE_DONG_2),
		MONSTER(AssetEnum.MONSTER_DONG_0, AssetEnum.MONSTER_DONG_1, AssetEnum.MONSTER_DONG_2), 
		DOG(AssetEnum.MONSTER_DONG_0, AssetEnum.MONSTER_DONG_1, AssetEnum.MONSTER_DONG_2), 
		HORSE(AssetEnum.MONSTER_DONG_0, AssetEnum.MONSTER_DONG_1, AssetEnum.MONSTER_DONG_2), 
		BIRD(AssetEnum.MONSTER_DONG_0, AssetEnum.MONSTER_DONG_1, AssetEnum.MONSTER_DONG_2), 
		GIANT(AssetEnum.MONSTER_DONG_0, AssetEnum.MONSTER_DONG_1, AssetEnum.MONSTER_DONG_2), 
		NONE(AssetEnum.NULL, AssetEnum.NULL, AssetEnum.NULL);
		private final Array<AssetEnum> phallusStates;

		PhallusType(AssetEnum... phallusStates ) {
		    this.phallusStates = new Array<AssetEnum>(phallusStates);
		}
		
		public String getLabel() {
			return this == CUTE ? "Cute" : this == TINY ? "Tiny" : this == SMALL ? "Small" : "";
		}
		
		public String getDescription() {
			return this == CUTE ? "An adorable penis." : this == TINY ? "A very small penis." : this == SMALL ? "Average." : "";
		}
		
		private AssetDescriptor<Texture> getPhallusState(int stateIndex) {
			return phallusStates.get(stateIndex).getTexture();
		}
	}
	
	public enum Stat { // these descriptions should have the \n removed and replaced with setWrap/setWidth on the display
		STRENGTH(AssetEnum.STRENGTH, "Strength determines raw attack power, which affects damage, how much attacks unbalance an enemies, and contests of strength, such as wrestling, struggling, or weapon locks."),
		ENDURANCE(AssetEnum.ENDURANCE, "Endurance determines stamina and resilience, which affects your ability to keep up an assault without getting tired, your ability to shrug off low damage attacks, and wear heavier armor without becoming exhausted."),
		AGILITY(AssetEnum.AGILITY, "Agility determines balance and skill, affecting your ability to keep a sure footing even while doing acrobatic maneuvers, getting unblockable attacks against enemies, and evading enemy attacks."),
		PERCEPTION(AssetEnum.PERCEPTION, "Perception determines your ability to see what attacks an enemy may use next and prepare accordingly, as well as your base scouting ability, which determines what information you can see about upcoming areas."),
		MAGIC(AssetEnum.MAGIC, "Magic determines your magical capabilities, such as how powerful magic spells are, and how many of them you can cast before becoming magically exhausted."),
		CHARISMA(AssetEnum.CHARISMA, "Charisma determines your ability to influence an enemy, getting them to calm down and listen to reason, enraging them, or seducing them.");

		private final AssetEnum asset;
		private final String description;
		private Stat(AssetEnum asset, String description) {
			this.asset = asset;
			this.description = description;
		}
		public AssetDescriptor<Texture> getAsset() {
			return asset.getTexture();
		}
		public String getLabel() {
			char[] chars = super.toString().replace("_", " ").toLowerCase().toCharArray();
			boolean found = false;
			for (int i = 0; i < chars.length; i++) {
				if (!found && Character.isLetter(chars[i])) {
					chars[i] = Character.toUpperCase(chars[i]);
					found = true;
			    } 
				else if (Character.isWhitespace(chars[i])) {
					found = false;
			    }
			}		
			return String.valueOf(chars);
		}
		public String getDescription() {
			return description;
		}
	}

	public enum Stability {
		Disoriented (AssetEnum.BALANCE_ICON_2, 0),
		Dazed (AssetEnum.BALANCE_ICON_2, 0),
		Down (AssetEnum.BALANCE_ICON_2, 0),
		Teetering (AssetEnum.BALANCE_ICON_2, .1f),
		Weakfooted (AssetEnum.BALANCE_ICON_1, .25f),
		Unstable (AssetEnum.BALANCE_ICON_1, .50f),
		Stable (AssetEnum.BALANCE_ICON_0, .75f),
		Surefooted (AssetEnum.BALANCE_ICON_0, 1);

		private final AssetEnum texture;
		private final float percent;
		private Stability(AssetEnum texture, float percent) {
			this.texture = texture;
			this.percent = percent;
		}
		
		public AssetDescriptor<Texture> getDisplay() {
			return texture.getTexture();
		}
		public float getPercent() {
			return percent;
		}
		public boolean isDown() {
			return this.ordinal() < 3;
		}
		public boolean lowBalance() {
			return this.ordinal() < 4;
		}
		public boolean isGood() {
			return this.ordinal() > 5;
		}
		
		public Stability shift(int stabilityMod) {
			int shift = 0;
			if (stabilityMod > 10) {
				shift = 4;
			}
			if (stabilityMod > 5) {
				shift = 2;
			}
			else if (stabilityMod > 0) {
				shift = 1;
			}
			else if (stabilityMod < -20) {
				shift = -6;
			}
			else if (stabilityMod < -10) {
				shift = -3;
			}
			else if (stabilityMod < -5) {
				shift = -2;
			}
			else if (stabilityMod < -1) {
				shift = -1;
			}
			int ordinal = this.ordinal() + shift;
			Stability [] values = Stability.values();
			return values.length < ordinal + 1 ? values[values.length - 1] : ordinal < 0 ? values[0] : values[ordinal];			
		}
	}
	
	protected enum PronounSet {
		MALE ("he", "him", "his", "himself"),
		FEMALE ("she", "her", "her", "herself"),
		SECOND_PERSON ("you", "you", "your", "yourself")
		;
		private final String nominative, objective, possessive, reflexive;
		private PronounSet(String nominative, String objective, String possessive, String reflexive) {
			this.nominative = nominative;
			this.objective = objective;
			this.possessive = possessive;
			this.reflexive = reflexive;
		}
		public String getNominative() { return nominative; }
		public String getObjective() { return objective; }
		public String getPossessive() { return possessive; }
		public String getReflexive() { return reflexive; }
		
	}

	public ObjectMap<Perk, Integer> getPerks() {
		ObjectMap<Perk, Integer> tempPerks = new ObjectMap<Perk, Integer>();
		for (String key : perks.keys()) {
			tempPerks.put(Perk.valueOf(key), perks.get(key));
		}
		return tempPerks;
	}
}

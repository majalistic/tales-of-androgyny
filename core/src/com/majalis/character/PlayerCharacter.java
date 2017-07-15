package com.majalis.character;

import static com.majalis.character.Techniques.*;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.majalis.asset.AssetEnum;
import com.majalis.character.Item.Potion;
import com.majalis.character.Item.Weapon;
import com.majalis.character.Item.WeaponType;
import com.majalis.save.SaveManager.JobClass;

/*
 * Contains the current player character's statistics, including "party" statistics like food remaining
 */
public class PlayerCharacter extends AbstractCharacter {

	private final static ObjectMap<Stat, Array<String>> statNameMap = new ObjectMap<Stat, Array<String>>();
	static {
		statNameMap.put(Stat.STRENGTH, new Array<String>(new String[]{"Crippled", "Feeble", "Weak", "Soft", "Able", "Strong", "Mighty", "Powerful", "Hulking", "Heroic", "Godlike", "Godlike", "Godlike"}));
		statNameMap.put(Stat.ENDURANCE, new Array<String>(new String[]{"Feeble", "Infirm", "Fragile", "Frail", "Sturdy", "Durable", "Tough", "Stalwart", "Titanic", "Unstoppable", "Juggernaut", "Juggernaut", "Juggernaut"}));
		statNameMap.put(Stat.AGILITY, new Array<String>(new String[]{"Sluggish", "Clumsy", "Inept", "Slow", "Swift", "Quick", "Skillful", "Nimble", "Adept", "Preternatural", "Supernatural", "Supernatural", "Supernatural"}));
		statNameMap.put(Stat.PERCEPTION, new Array<String>( new String[]{"Senseless", "Oblivious", "Dim-witted", "Slow-minded", "Alert", "Perceptive", "Observant", "Sharp", "Astute", "Eagle-eyed", "Omniscient", "Omniscient", "Omniscient"}));
		statNameMap.put(Stat.MAGIC, new Array<String>(new String[]{"Unaware", "Mundane", "Aware", "Aligned", "Enchanted", "Mystical", "Otherwordly", "Arcane", "Mythical", "Omnipotent", "Demiurge", "Demiurge", "Demiurge"}));
		statNameMap.put(Stat.CHARISMA, new Array<String>(new String[]{"Inhuman", "Horrible", "Uncouth", "Unpleasant", "Plain", "Likeable", "Charismatic", "Charming", "Magnetic", "Lovable", "Worshipable", "Worshipable", "Worshipable"}));
	}
	
	protected String name;
	
	protected ObjectMap<String, Integer> skills;
	protected ObjectMap<String, Integer> perks;
	protected int skillPoints;
	protected int magicPoints;
	protected int perkPoints;
	
	// advantage, range, and combat-lock(boolean) are shared properties between two creatures
	
	/* out of battle only statistics */
	private int time;
	private int money;
	private int debt;

	protected Femininity femininity;
	protected LipFullness lipFullness;

	private int receivedAnal;
	private int receivedOral;	
	private int analCreampie;
	private int oralCreampie;
	private int cameFromAnal;
	private int cameFromOral;
	private boolean justCame;
	
	private String currentPortrait;
	
	private boolean goblinVirgin;
	private boolean a2m;
	private boolean a2mcheevo;
	private boolean wrapLegs;
	
	private boolean loaded;
	private ObjectMap<String, Integer> questFlags;

	private int scout;
	
	@SuppressWarnings("unused")
	private PlayerCharacter() {}
	
	public PlayerCharacter(boolean defaultValues) {
		super(defaultValues);
		if (defaultValues) {
			label = "You";
			secondPerson = true;
			pronouns = PronounSet.SECOND_PERSON;
			healthTiers = new IntArray(new int[]{15, 15, 15, 15});
			currentHealth = getMaxHealth();	
			setStaminaToMax();
			setStabilityToMax();
			setManaToMax();
			food = 60;
			setGoblinVirginity(true);
			a2m = false;
			a2mcheevo = false;
			phallus = PhallusType.SMALL;	
			// this needs to be refactored - need "current defense" and for refresh method to set to max
			baseDefense = 6;
			money = 40;
			debt = 0;
			inventory = new Array<Item>();
			for (int ii = 10; ii <= 20; ii += 10) {
				inventory.add(new Potion(ii));
				inventory.add(new Potion(ii));
			}
			setCharacterName("Hiro");
			questFlags = new ObjectMap<String, Integer>();
			time = 0;
		}
		
		skills = new ObjectMap<String, Integer>();
		for (Techniques basicTechnique: getBaseTechniques()) {
			skills.put(basicTechnique.toString(), 1);
		}
		
		perks = new ObjectMap<String, Integer>();
		justCame = false;
		loaded = false;
		setCurrentPortrait(AssetEnum.PORTRAIT_SMILE); // his smile and optimism, not yet gone
	}
	
	private static ObjectSet<Techniques> getBaseTechniques() {
		ObjectSet<Techniques> baseTechniques = new ObjectSet<Techniques>();
		baseTechniques.addAll(
			DO_NOTHING, POWER_ATTACK, TEMPO_ATTACK, RESERVED_ATTACK, DUCK, SPRING_ATTACK, NEUTRAL_ATTACK, REVERSAL_ATTACK, CAREFUL_ATTACK, BLOCK, GUARD,
			KIP_UP, STAND_UP, STAY_KNELT, KNEE_UP, REST_FACE_DOWN, REST, JUMP_ATTACK, VAULT_OVER,
			RECEIVE_ANAL, RECEIVE_DOGGY, RECEIVE_STANDING, STRUGGLE_ORAL, STRUGGLE_DOGGY, STRUGGLE_ANAL, STRUGGLE_STANDING, RECEIVE_KNOT, SUCK_IT, BREAK_FREE_ANAL, BREAK_FREE_ORAL,
			SUBMIT, STRUGGLE_FULL_NELSON, BREAK_FREE_FULL_NELSON, STRUGGLE_PRONE_BONE, STRUGGLE_DOGGY,
			OPEN_WIDE, GRAB_IT, STROKE_IT, LET_GO, USE_ITEM, ITEM_OR_CANCEL,
			RECIPROCATE_FORCED, GET_FACE_RIDDEN, STRUGGLE_FACE_SIT, STRUGGLE_SIXTY_NINE, BREAK_FREE_FACE_SIT, ROLL_OVER_UP, ROLL_OVER_DOWN, RIPOSTE, EN_GARDE, POUNCE_DOGGY, POUND_DOGGY, POUNCE_ANAL, POUND_ANAL, ERUPT_ANAL, PULL_OUT, PULL_OUT_ORAL, PULL_OUT_ANAL, PULL_OUT_STANDING, RECEIVE_COCK, HURK, UH_OH,
			FORCE_DEEPTHROAT, CRUSH_ASS, BOUNCE_ON_IT, SQUEEZE_IT, BE_RIDDEN, PUSH_OFF, SELF_SPANK, POUT, DEEPTHROAT, WRAP_LEGS, PUSH_OFF_ATTEMPT, RIDE_ON_IT_REVERSE, BOUNCE_ON_IT_REVERSE, SQUEEZE_IT_REVERSE, RECEIVE_PRONE_BONE, BE_RIDDEN_REVERSE, PUSH_OFF_REVERSE, PUSH_OFF_ATTEMPT_REVERSE,
			OUROBOROS, ROUND_AND_ROUND, RECEIVE_OUROBOROS, STRUGGLE_OUROBOROS, MOUNT_FACE, FACEFUCK, GET_FACEFUCKED, STRUGGLE_FACEFUCK, RECEIVE_EGGS, ERUPT_ORAL
		);
		return baseTechniques;
	}
	
	public static ObjectMap<Stat, Array<String>> getStatMap() {
		return statNameMap;
	}
	
	private void setCurrentPortrait(AssetEnum portrait) {
		currentPortrait = portrait.getTexture().fileName;
	}
	
	public void addToInventory(Item item) {
		inventory.add(item);
	}
	
	public Array<Item> getInventory() { return inventory; }
	
	public JobClass getJobClass() { return jobClass; }
	
	public void setJobClass(JobClass jobClass) {
		for (Stat stat: Stat.values()) {
			setStat(stat, jobClass.getBaseStat(stat));
		}
		this.jobClass = jobClass;
		skillPoints = 2; 
		perkPoints = 2; 
		magicPoints = 0;
		food = 60; 
		skills.remove(COMBAT_HEAL.toString());
		skills.remove(INCANTATION.toString());
		skills.remove(BLITZ_ATTACK.toString());
		skills.remove(ALL_OUT_BLITZ.toString());
		skills.remove(HOLD_BACK.toString());
		perks.remove(Perk.WEAK_TO_ANAL.toString());
		weapon = null;
		// warrior will need to get bonus stance options, Ranger will need to start with a bow
		switch (jobClass) { 
			case WARRIOR: skillPoints = 3; skills.put(BLITZ_ATTACK.toString(), 1); skills.put(ALL_OUT_BLITZ.toString(), 1); skills.put(HOLD_BACK.toString(), 1); perks.put(Perk.WEAK_TO_ANAL.toString(), 1); break;
			case PALADIN: addSkill(COMBAT_HEAL, 1); break;
			case THIEF: skillPoints = 5; food += 40; break;
			case MAGE: magicPoints = 2; break;
			case RANGER: weapon = new Weapon(WeaponType.Bow); break;
			case ENCHANTRESS: magicPoints = 1; perkPoints = 3; break;
			default:
		}
	}
	
	// this needs to consolidate logic with the getTechniques method
	public Array<Technique> getPossibleTechniques(AbstractCharacter target) {
		Array<Technique> possibles = getPossibleKnownTechniques(target);
		if (possibles.size == 0) possibles.addAll(getTechniques(target, DO_NOTHING));
		return possibles;
	}
	
	private Array<Technique> getPossibleKnownTechniques(AbstractCharacter target) {
		Array<Technique> possibles;
		switch(stance) {
			case BLITZ:
				return getTechniques(target, ALL_OUT_BLITZ, HOLD_BACK);
			case COUNTER:
				return getTechniques(target, RIPOSTE, EN_GARDE);
			case OFFENSIVE:
				possibles = getTechniques(target, BLITZ_ATTACK, POWER_ATTACK, ARMOR_SUNDER, RECKLESS_ATTACK, KNOCK_DOWN, VAULT, FEINT_AND_STRIKE, TEMPO_ATTACK, RESERVED_ATTACK);
				if (target.getStance() == Stance.SUPINE && target.isErect() && target.enemyType.canBeRidden()) {
					possibles.addAll(getTechniques(target, SIT_ON_IT, TURN_AND_SIT));
				}
				if (target.stance == Stance.PRONE && isErect() && target.enemyType.isPounceable()) {
					possibles.addAll(getTechniques(target, POUNCE_DOGGY));
				}
				else if (target.stance == Stance.SUPINE && isErect() && target.enemyType.isPounceable()) {
					possibles.addAll(getTechniques(target, POUNCE_ANAL, MOUNT_FACE));
				}
				else if (target.stance == Stance.KNEELING && isErect() && target.enemyType.isPounceable()) {
					possibles.addAll(getTechniques(target, IRRUMATIO));
				}
				return possibles;
			case BALANCED:
				possibles = getTechniques(target, SPRING_ATTACK, NEUTRAL_ATTACK, CAUTIOUS_ATTACK, BLOCK, INCANTATION, SLIDE, DUCK, HIT_THE_DECK);
				if (hasItemsToUse()) {
					possibles.addAll(getTechniques(target, USE_ITEM));
				}
				if (target.getStance() == Stance.SUPINE && target.isErect() && target.enemyType != EnemyEnum.SLIME && target.enemyType != EnemyEnum.CENTAUR && target.enemyType != EnemyEnum.UNICORN) {
					possibles.addAll(getTechniques(target, SIT_ON_IT, TURN_AND_SIT));
				}
				return possibles;
			case DEFENSIVE:
				return getTechniques(target, REVERSAL_ATTACK, CAREFUL_ATTACK, GUARD, TAUNT, SECOND_WIND, PARRY, INCANTATION, DUCK);
			case PRONE:
			case SUPINE:
				return getTechniques(target, KIP_UP, STAND_UP, KNEE_UP, stance == Stance.PRONE ? REST_FACE_DOWN : REST, stance == Stance.PRONE ? ROLL_OVER_UP : ROLL_OVER_DOWN);
			case KNEELING:
				possibles = getTechniques(target, UPPERCUT, STAND_UP, STAY_KNELT);
				if (target.isErect() && target.enemyType != EnemyEnum.SLIME && target.enemyType.isPounceable()) {
					possibles.addAll(getTechniques(target, GRAB_IT));
				}
				return possibles;
			case AIRBORNE:
				return getTechniques(target, JUMP_ATTACK, VAULT_OVER);
			case FULL_NELSON_BOTTOM:
				if (struggle <= 0) {
					return getTechniques(target, SUBMIT, BREAK_FREE_FULL_NELSON);
				}
				return getTechniques(target, SUBMIT, STRUGGLE_FULL_NELSON);
			case DOGGY_BOTTOM:
				if (struggle <= 0) {
					possibles = getTechniques(target, RECEIVE_DOGGY, BREAK_FREE_ANAL);
				}
				else {
					possibles = getTechniques(target, RECEIVE_DOGGY, STRUGGLE_DOGGY);
				}
				if (perks.get(Perk.CATAMITE.toString(), 0) > 0 || perks.get(Perk.ANAL_LOVER.toString(), 0) > 1) {
					possibles.addAll(getTechniques(target, SELF_SPANK));
				}
				return possibles;
			case PRONE_BONE_BOTTOM:
				if (struggle <= 0) {
					possibles = getTechniques(target, RECEIVE_PRONE_BONE, BREAK_FREE_ANAL);
				}
				else {
					possibles = getTechniques(target, RECEIVE_PRONE_BONE, STRUGGLE_PRONE_BONE);
				}
				return possibles;
			case ANAL_BOTTOM:
				if (wrapLegs) {
					return getTechniques(target, RECEIVE_ANAL);
				}
				if (struggle <= 0) {
					possibles = getTechniques(target, RECEIVE_ANAL, POUT, BREAK_FREE_ANAL);
				}
				else {
					possibles = getTechniques(target, RECEIVE_ANAL, POUT, STRUGGLE_ANAL);
				}
				if (!wrapLegs && (perks.get(Perk.CATAMITE.toString(), 0) > 0 || perks.get(Perk.ANAL_LOVER.toString(), 0) > 1)) {
					possibles.addAll(getTechniques(target, WRAP_LEGS));
				}
				return possibles;
			case HANDY_BOTTOM:
				return getTechniques(target, STROKE_IT, LET_GO, OPEN_WIDE);
			case STANDING_BOTTOM:
				if (struggle <= 0) {
					return getTechniques(target, RECEIVE_STANDING, BREAK_FREE_ANAL);
				}
				return getTechniques(target, RECEIVE_STANDING, STRUGGLE_STANDING);
			case COWGIRL_BOTTOM:
				return getTechniques(target, RIDE_ON_IT, BOUNCE_ON_IT, SQUEEZE_IT, STAND_OFF_IT);
			case REVERSE_COWGIRL_BOTTOM:
				return getTechniques(target, RIDE_ON_IT_REVERSE, BOUNCE_ON_IT_REVERSE, SQUEEZE_IT_REVERSE, STAND_OFF_IT);
			case KNOTTED_BOTTOM:
				return getTechniques(target, RECEIVE_KNOT);
			case OVIPOSITION_BOTTOM:
				return getTechniques(target, RECEIVE_EGGS);
			case FELLATIO_BOTTOM:
				if (struggle <= 0) {
					possibles = getTechniques(target, SUCK_IT, BREAK_FREE_ORAL);
				}
				else {
					possibles = getTechniques(target, SUCK_IT, STRUGGLE_ORAL);
				}
				if (perks.get(Perk.MOUTH_MANIAC.toString(), 0) > 1) {
					possibles.addAll(getTechniques(target, DEEPTHROAT));
				}
				return possibles;
			case FACEFUCK_BOTTOM:
				if (struggle <= 0) {
					possibles = getTechniques(target, GET_FACEFUCKED, BREAK_FREE_ORAL);
				}
				else {
					possibles = getTechniques(target, GET_FACEFUCKED, STRUGGLE_FACEFUCK);
				}
				return possibles;
			case OUROBOROS_BOTTOM:
				if (struggle <= 0) {
					possibles = getTechniques(target, RECEIVE_OUROBOROS, BREAK_FREE_ORAL);
				}
				else {
					possibles = getTechniques(target, RECEIVE_OUROBOROS, STRUGGLE_OUROBOROS);
				}
				return possibles;
			case FACE_SITTING_BOTTOM:
				if (struggle <= 0) {
					return getTechniques(target, GET_FACE_RIDDEN, BREAK_FREE_FACE_SIT);
				}
				return getTechniques(target, GET_FACE_RIDDEN, STRUGGLE_FACE_SIT);
			case SIXTY_NINE_BOTTOM:
				if (struggle <= 0) {
					return getTechniques(target, RECIPROCATE_FORCED, BREAK_FREE_ORAL);
				}
				return getTechniques(target, RECIPROCATE_FORCED, STRUGGLE_SIXTY_NINE);
			case HELD:
				return getTechniques(target, UH_OH);
			case SPREAD:
				return getTechniques(target, RECEIVE_COCK);
			case PENETRATED:
				return getTechniques(target, HURK);
			case CASTING:
				return getTechniques(target, COMBAT_FIRE, COMBAT_HEAL, TITAN_STRENGTH);
			case ITEM:
				possibles = getConsumableItems(target);
				possibles.addAll(getTechniques(target, ITEM_OR_CANCEL));
				return possibles;
			case FELLATIO:
				if (lust > 12) {
					return getTechniques(target, ERUPT_ORAL);
				}
				else {
					return getTechniques(target, IRRUMATIO, PULL_OUT_ORAL);
				}	
			case FACEFUCK:
				if (lust > 12) {
					return getTechniques(target, ERUPT_ORAL);
				}
				else {
					return getTechniques(target, FACEFUCK, PULL_OUT_ORAL);
				}	
			case OUROBOROS:
				if (lust > 12) {
					return getTechniques(target, ERUPT_ORAL);
				}
				else {
					return getTechniques(target, ROUND_AND_ROUND, PULL_OUT_ORAL);
				}	
			case PRONE_BONE:
			case DOGGY:
			case ANAL:
			case STANDING:
				if (lust > 12) {
					return getTechniques(target, ERUPT_ANAL);
				}
				else {
					if (stance == Stance.ANAL) {
						return getTechniques(target, POUND_ANAL, PULL_OUT_ANAL);
					}
					else if (stance == Stance.DOGGY) {
						return getTechniques(target, POUND_DOGGY, CRUSH_ASS, PULL_OUT);
					}
					else if (stance == Stance.PRONE_BONE) {
						return getTechniques(target, POUND_PRONE_BONE, PULL_OUT);
					}
					else {
						return getTechniques(target, POUND_STANDING, PULL_OUT_STANDING);
					}		
				}
			case COWGIRL:
				if (lust > 12) {
					struggle = -1;
					return getTechniques(target, ERUPT_COWGIRL);
				}
				if (struggle <= 0) {
					return getTechniques(target, BE_RIDDEN, PUSH_OFF);
				}
				else {
					return getTechniques(target, BE_RIDDEN, PUSH_OFF_ATTEMPT);
				}
			case REVERSE_COWGIRL:
				if (lust > 12) {
					struggle = -1;
					return getTechniques(target, ERUPT_COWGIRL);
				}
				if (struggle <= 0) {
					return getTechniques(target, BE_RIDDEN_REVERSE, PUSH_OFF_REVERSE);
				}
				else {
					return getTechniques(target, BE_RIDDEN_REVERSE, PUSH_OFF_ATTEMPT_REVERSE);
				}
			case ERUPT:
				stance = Stance.BALANCED;
				possibles = getPossibleTechniques(target);
				stance = Stance.ERUPT;
				return possibles;
			default: return null;
		}
	}
	
	private boolean hasItemsToUse() {
		for (Item item : inventory) {
			if (item.isConsumable()) {
				return true;
			}
		}
		return false;
	}

	private Array<Technique> getConsumableItems(AbstractCharacter target) {
		Array<Technique> consumableItems = new Array<Technique>();
		for (Item item : inventory) {
			if (item.isConsumable()) {
				consumableItems.add(new Technique(ITEM_OR_CANCEL.getTrait(), getCurrentState(target), 1, item));
			}
		}
		return consumableItems;
	}
	
	public Technique getTechnique(AbstractCharacter target) {
		// this should be re-architected - player characters likely won't use this method
		return null;
	}
	
	private Array<Technique> getTechniques(AbstractCharacter target, Techniques... possibilities) {
		Array<Technique> possibleTechniques = new Array<Technique>();
		
		for (Techniques technique : possibilities) {
			int skillLevel = skills.get(technique.toString(), 0);
			if (skillLevel > 0) {
				// this should pass the players stats and other relevant info to the technique, rather than passing some generic "force" value - also passing the weapon separately so that the technique can determine if it's relevant or not - basically, this class should create a "current state" object
				// this may need to filter out techniques which are not possible because of current state evalutations?
				possibleTechniques.add(new Technique(technique.getTrait(), getCurrentState(target), skillLevel));
			}	
		}
		return possibleTechniques;
	}
	
	@Override
	public Attack doAttack(Attack resolvedAttack) {
		if (resolvedAttack.isSuccessful() && resolvedAttack.getName().contains("Struggle") && struggle == 0) {
			setStruggleToMin();
		}
		
		if (resolvedAttack.getGrapple() > 0) {			
			struggle(resolvedAttack.getGrapple());
			resolvedAttack.addMessage("You struggle to break free!");
			if (struggle <= 3) {
				resolvedAttack.addMessage("You feel their grasp slipping away!");
			}
			else if (struggle <= 0) {
				resolvedAttack.addMessage("You are almost free!");
			}
		}
		
		if (resolvedAttack.getLust() > 0) {
			currentPortrait = AssetEnum.PORTRAIT_GRIN.getTexture().fileName;
			// taunt increases self lust too
			lust += 2;
		}
		
		if (wrapLegs) {
			resolvedAttack.addMessage("Your legs are wrapped around them!");
		}
		
		if (resolvedAttack.isSuccessful() && resolvedAttack.getName().equals("Wrap Legs")) {
			wrapLegs = true;
		}
		
		
		return super.doAttack(resolvedAttack);
	}
	
	@Override
	public Array<Array<String>> receiveAttack(Attack resolvedAttack) {
		super.receiveAttack(resolvedAttack);
		
		String result;
		if (resolvedAttack.isClimax()) {
			if (oldStance.isAnalReceptive()) {
				setCurrentPortrait(perks.get(Perk.ANAL_LOVER.toString(), 0) > 1 ? AssetEnum.PORTRAIT_AHEGAO : AssetEnum.PORTRAIT_POUT);
			}
			else if (oldStance.isOralReceptive()) {
				setCurrentPortrait(AssetEnum.PORTRAIT_MOUTHBOMB);
			}
				
		}
		if (stance.isAnalReceptive()) {
			setCurrentPortrait(perks.get(Perk.ANAL_LOVER.toString(), 0) > 1 ? AssetEnum.PORTRAIT_LUST : AssetEnum.PORTRAIT_HIT);
		}
		else if (stance.isOralReceptive()) {
			setCurrentPortrait(resolvedAttack.isClimax() ? AssetEnum.PORTRAIT_MOUTHBOMB : AssetEnum.PORTRAIT_FELLATIO);
		}
		
		if (stance == Stance.HELD) {
			setCurrentPortrait(perks.get(Perk.GIANT_LOVER.toString(), 0) > 1 ? AssetEnum.PORTRAIT_LOVE : AssetEnum.PORTRAIT_SURPRISE);
		}
		
		if (oldStance.isAnalReceptive() && !stance.isAnalReceptive()) {
			wrapLegs = false;
		}
		
		if (stance == Stance.OVIPOSITION_BOTTOM) {
			receiveEggs();
		}
		
		if (!oldStance.isAnalReceptive() && stance.isAnalReceptive()) {
			result = receiveAnal(); 
			
			if (stance != Stance.PENETRATED) {
				setCurrentPortrait(perks.get(Perk.ANAL_LOVER.toString(), 0) > 1 ? AssetEnum.PORTRAIT_LOVE : AssetEnum.PORTRAIT_SURPRISE);
			}
			
			else {
				setCurrentPortrait(perks.get(Perk.ANAL_LOVER.toString(), 0) > 1 ? AssetEnum.PORTRAIT_LOVE : AssetEnum.PORTRAIT_AHEGAO);
			}
			
			
			if (result != null) { resolvedAttack.addMessage(result); } 
			if (resolvedAttack.getUser().equals("Goblin")) {
				setGoblinVirginity(false);
			}
			a2m = true;
		}
		else if (!oldStance.isOralReceptive() && stance.isOralReceptive()) {
			result = receiveOral();
			if (result != null) { resolvedAttack.addMessage(result); } 
			if(a2m) {
				resolvedAttack.addMessage("Bleugh! That was in your ass!");
				// gross portrait
				if (!a2mcheevo) {
					a2mcheevo = true;
					resolvedAttack.addMessage("Achievement unlocked: Ass to Mouth.");
				}
				a2m = false;
			}
		}
		else if (stance == Stance.SIXTY_NINE) {
			resolvedAttack.addMessage("She shoves her cock down your throat while swallowing yours!");
		}
		Array<Array<String>> results =  new Array<Array<String>>();
		results.add(resolvedAttack.getMessages());
		results.add(resolvedAttack.getDialog());
		return results;	
	}
	
	@Override
	protected String getLeakMessage() {
		String message = "";
		
		if (buttful >= 20) {
			message = "Your belly looks pregnant, full of baby batter! It drools out of your well-used hole! Your movements are sluggish! -2 Agility.";
		}
		else if (buttful >= 10) {
			message = "Your gut is stuffed with semen!  It drools out!  You're too queasy to move quickly! -1 Agility.";
		}
		else if (buttful >= 5) {
			message = "Cum runs out of your full ass!";
		}
		else if (buttful > 1) {
			message = "You drool cum from your hole!";
		}
		else if (buttful == 1) {
			message = " The last of the cum runs out of your hole!";
		}
		drainButt();
		return message;
	}
	
	@Override
	protected String getDroolMessage() {
		String message = "";
		if (mouthful > 10) {
			message = "You vomit their tremendous load onto the ground!";
		}
		else if (mouthful > 5) {
			message = "You spew their massive load onto the ground!";
		}
		else {
			message = "You spit all of their cum out onto the ground!";
		}
		drainMouth();
		return message;
	}
	
	public void refresh() {
		setStaminaToMax();
		setStabilityToMax();
		setManaToMax();
		stance = Stance.BALANCED;
		a2m = false;
		buttful = Math.max(0, buttful - 10);
		mouthful = 0;
		baseDefense = 6;
		if (disarmedWeapon != null) {
			weapon = disarmedWeapon;
			disarmedWeapon = null;
		}

		setCurrentPortrait(getNeutralFace());
		int currentBleed = statuses.get(StatusType.BLEEDING.toString(), 0);
		if (currentBleed != 0) {
			statuses.put(StatusType.BLEEDING.toString(), Math.max(currentBleed - getEndurance() * 2, 0));
		}
		wrapLegs = false;
		struggle = -1;
		scout = 0;
	}

	public enum Femininity {
		MALE,
		UNMASCULINE,
		EFFIMINATE,
		FEMALE,
		BITCH
	}
	
	public enum Bootyliciousness {
		Bubble ("A tight, round, firm, pleasing tush."),
		Round ("A soft, round rear with give to it."),
		Fat ("Dear diary, the ass was fat. A big booty.") // dear diary, the ass was fat
		;
		private final String description;
		private Bootyliciousness(String description) {
			this.description = description;
		}
		
		public String getDescription() {
			return description;
		}
	}
	
	public enum LipFullness {
		Thin ("Svelte lips."),
		Pouty ("Soft, feminine lips."),
		Full ("Full, luscious lips."),
		Beestung ("Prominent, kissable lips.")
		;
		private final String description;
		private LipFullness(String description) {
			this.description = description;
		}
		
		public String getDescription() {
			return description;
		}
	}

	public void setStat(Stat stat, int amount) {
		switch(stat) {
			case STRENGTH: baseStrength = amount; break;
			case ENDURANCE: baseEndurance = amount; break;
			case AGILITY: baseAgility = amount; break;
			case PERCEPTION: basePerception = amount; break;
			case MAGIC: baseMagic = amount; manaTiers = new IntArray(new int[]{baseMagic > 1 ? baseMagic * 3 + 1 : 0}); setManaToMax(); break;
			case CHARISMA: baseCharisma = amount; break;
			default: 
		}
	}
 
	public void setCurrentHealth(Integer newHealth) {
		currentHealth = newHealth;	
	}

	// need to add the list of default skills, the actual variable, and some way to access it for skill selection purposes (filter)
	public void addSkill(Techniques newTech, int rank) {
		// if it's a spell, add incantation
		if (newTech.getTrait().isSpell()) {
			skills.put(Techniques.INCANTATION.toString(), 1);
		}
		skills.put(newTech.toString(), rank);	
	}
	
	// need to add the list of default skills, the actual variable, and some way to access it for skill selection purposes (filter)
	public void addPerk(Perk newPerk, int rank) {
		perks.put(newPerk.toString(), rank);	
	}

	public ObjectMap<Techniques, Integer> getSkills() {
		ObjectMap<Techniques, Integer> tempSkills = new ObjectMap<Techniques, Integer>();
		for (String key : skills.keys()) {
			tempSkills.put(Techniques.valueOf(key), skills.get(key));
		}
		return tempSkills;
	}
	
	public ObjectMap<Perk, Integer> getPerks() {
		ObjectMap<Perk, Integer> tempPerks = new ObjectMap<Perk, Integer>();
		for (String key : perks.keys()) {
			tempPerks.put(Perk.valueOf(key), perks.get(key));
		}
		return tempPerks;
	}
	
	public void setSkills(ObjectMap<Techniques, Integer> skills) {
		this.skills.clear();
		for (Techniques key : skills.keys()) {
			addSkill(key, skills.get(key));
		}
	}

	private void increaseLowestStat() {
		int min = getStat(Stat.STRENGTH);
		Stat minStat = Stat.STRENGTH;
		for (Stat stat : Stat.values()) {
			if (getStat(stat) < min) {
				min = getStat(stat);
				minStat = stat;
			}
		}
		setStat(minStat, getStat(minStat) + 1);
	}
	
	public void setPerks(ObjectMap<Perk, Integer> perks) {
		if (perks.get(Perk.WELLROUNDED, 0) > 0 && !(this.perks.get(Perk.WELLROUNDED.toString(), 0) > 0) ) {
			increaseLowestStat();
		}
		if (perks.get(Perk.STRONGER, 0) > this.perks.get(Perk.STRONGER.toString(), 0)) {
			int strIncrease = perks.get(Perk.STRONGER, 0) -  this.perks.get(Perk.STRONGER.toString(), 0);
			baseStrength += strIncrease;
		}
		if (perks.get(Perk.HARDER, 0) > this.perks.get(Perk.HARDER.toString(), 0)) {
			int endIncrease = perks.get(Perk.HARDER, 0) -  this.perks.get(Perk.HARDER.toString(), 0);
			baseEndurance += endIncrease;
		}
		if (perks.get(Perk.FASTER, 0) > this.perks.get(Perk.FASTER.toString(), 0)) {
			int agiIncrease = perks.get(Perk.FASTER, 0) -  this.perks.get(Perk.FASTER.toString(), 0);
			baseAgility += agiIncrease;
		}
		
		if (perks.get(Perk.CATAMITE, 0) > 0 && !(this.perks.get(Perk.CATAMITE.toString(), 0) > 0)) {
			addSkill(SIT_ON_IT, 1);
			addSkill(RIDE_ON_IT, 1);
			addSkill(STAND_OFF_IT, 1);
			addSkill(TURN_AND_SIT, 1);
			addSkill(RIDE_ON_IT_REVERSE, 1);
			addSkill(BOUNCE_ON_IT_REVERSE, 1);
		}
		this.perks.clear();
		for (Perk key : perks.keys()) {
			addPerk(key, perks.get(key));
		}
	}
	
	public int getScoutingScore() {
		return getTrueScoutingScore(scout * 3 + getPerception() + (perks.get(Perk.SURVEYOR.toString(), 0) > 0 ? perks.get(Perk.SURVEYOR.toString()) * 2 : 0));
	}
	
	private int getTrueScoutingScore(int rawScoutingScore) {
		int level = 3;
		for (int ii : new int[]{8, 5, 2}) {
			if (rawScoutingScore < ii) level--;
		}
		return level;
	}
	
	public int getLewdCharisma() {
		return getCharisma() + (perks.get(Perk.EROTIC.toString(), 0) > 0 ? perks.get(Perk.EROTIC.toString()) * 2 : 0);
	}	
	
	public boolean isLewd() {
		return perks.get(Perk.CATAMITE.toString(), 0) > 0;
	}
	
	@Override
	protected String increaseLust() {
		if (stance.isEroticPenetration() || stance.isOralReceptive()) {
			return increaseLust(1);
		}
		else if (stance.isAnalReceptive()) {
			if (perks.get(Perk.WEAK_TO_ANAL.toString(), 0) > 0) return increaseLust(2);
		}
		return null;
	}
	
	@Override
	protected String increaseLust(int lustIncrease) {
		String spurt = "";
		lust += lustIncrease;
		if (lust > 10 && stance.isEroticReceptive()) {
			spurt = climax();
		}
		return !spurt.isEmpty() ? spurt : null;
	}
	
	@Override
	protected String climax() {
		String spurt = "";
		String result = null;
		boolean weakToAnal = perks.get(Perk.WEAK_TO_ANAL.toString(), 0) > 0;
		lust = 0;
		switch (stance) {
			case KNOTTED_BOTTOM:
			case DOGGY_BOTTOM: 
			case ANAL_BOTTOM:
				spurt = "Awoo! Semen spurts out your untouched cock as your hole is violated!\n"
					+	"You feel it with your ass, like a girl! Your face is red with shame!\n"
					+	"Got a little too comfortable, eh?\n";
				cumFromAnal();
				result = incrementPerk(cameFromAnal, Perk.ANAL_LOVER, weakToAnal ? 3 : 5, weakToAnal ? 2 : 3, 1);
				break;
			case COWGIRL_BOTTOM:
				spurt = "Awoo! Semen spurts out your untouched cock as your hole is violated!\n"
						+	"You feel it with your ass, like a girl! Your face is red with shame!\n"
						+	"It spews all over them!\n";
				cumFromAnal();
				result = incrementPerk(cameFromAnal, Perk.ANAL_LOVER, weakToAnal ? 3 : 5, weakToAnal ? 2 : 3, 1);
				break;
			case FELLATIO_BOTTOM:
				spurt = "You spew while sucking!\n"
					+	"Your worthless cum spurts into the dirt!\n"
					+	"They don't even notice!\n";
				cumFromOral();
				result = incrementPerk(cameFromOral, Perk.MOUTH_MANIAC, 5, 3, 1);
				break;
			case FACE_SITTING_BOTTOM:
				spurt = "You spew while she rides your face!\n"
						+	"Your worthless cum spurts into the dirt!\n"
						+	"They don't even notice!\n";
				break;
			case SIXTY_NINE_BOTTOM:
				spurt = "You spew into her mouth!\n"
						+	"Her cock twitches in yours!\n"
						+	"You're about to retun the favor!\n";
				cumFromOral();
				result = incrementPerk(cameFromOral, Perk.MOUTH_MANIAC, 5, 3, 1);
				break;
			case ANAL:
			case DOGGY:
			case FELLATIO:
				stance = Stance.ERUPT;
				break;
			default: spurt = "You spew your semen onto the ground!\n"; 
		}
		if (result != null) spurt += result + "\n";
		spurt += "You're now flaccid!\n";
		return spurt;
	}

	public void modExperience(Integer exp) { experience += exp; }
	
	public int getExperience() { return experience; }

	public Integer getFood() { return food; }

	public void setBootyliciousness(Bootyliciousness buttSize) { bootyliciousness = buttSize; }

	public void setLipFullness(LipFullness lipFullness) { this.lipFullness = lipFullness;	}
	
	public int getSkillPoints() { return skillPoints; }

	public int getMagicPoints() { return magicPoints; }

	public int getPerkPoints() { return perkPoints; }

	public void setSkillPoints(int skillPoints) { this.skillPoints = skillPoints; }

	public void setMagicPoints(int magicPoints) { this.magicPoints = magicPoints; }

	public void setPerkPoints(int perkPoints) { this.perkPoints = perkPoints; }

	public int getLevel() {
		return level;
	}

	public int getStoredLevels() {
		return experience / 5;
	}

	public void levelUp() {
		experience -= 5;
		level++;
		skillPoints += 2;
		perkPoints++;
		if (hasMagic()) {
			magicPoints++;
		}
	}

	public boolean hasMagic() {
		return jobClass == JobClass.ENCHANTRESS || jobClass == JobClass.MAGE || jobClass == JobClass.PALADIN;
	}

	public boolean needsLevelUp() {
		return skillPoints > 0 || magicPoints > 0 || perkPoints > 0 || getStoredLevels() > 0;
	}
	
	public boolean isVirgin() {
		return receivedAnal == 0;
	}
	
	public boolean isOralVirgin() {
		return receivedOral == 0;
	}
	
	public boolean tastedCumAnally() {
		return analCreampie != 0;
	}
	
	public boolean tastedCumOrally() {
		return oralCreampie != 0;
	}
	
	private int getMasculinityLevel() {
		return (int) Math.round(receivedOral / 5. + receivedAnal / 3. + oralCreampie / 3. + analCreampie + cameFromOral + cameFromAnal);		
	}
	
	public AssetDescriptor<Texture> getMasculinityPath() {
		int masculinityLevel = getMasculinityLevel();
		return masculinityLevel < 1 ? AssetEnum.MARS_ICON_0.getTexture() : masculinityLevel < 3 ? AssetEnum.MARS_ICON_1.getTexture() : masculinityLevel < 8 ? AssetEnum.MARS_ICON_2.getTexture() : masculinityLevel < 15 ? AssetEnum.MARS_ICON_3.getTexture() : AssetEnum.MARS_ICON_4.getTexture();
	}
	
	public boolean isVirgin(EnemyEnum enemyEnum) {
		switch (enemyEnum) {
			case GOBLIN: return goblinVirgin;
			default:
		}
		return false;
	}
	
	private String receiveAnal() {
		String result = receivedAnal == 0 ? "You are no longer a virgin! " : "";
		receivedAnal++;
		boolean weakToAnal = perks.get(Perk.WEAK_TO_ANAL.toString(), 0) > 0;
		return result + incrementPerk(receivedAnal, Perk.ANAL_LOVER, weakToAnal ? 5 : 10, weakToAnal ? 3 : 6, weakToAnal ? 1 : 3);
	}
	
	private String receiveOral() {
		receivedOral++;
		return incrementPerk(receivedOral, Perk.MOUTH_MANIAC, 10, 6, 3);
	}
	
	@Override
	protected String fillButt(int buttful) {
		super.fillButt(buttful);
		analCreampie++;
		return incrementPerk(analCreampie, Perk.CREAMPIE_ADDICT, 10, 6, 3);
	}
	
	protected String receiveEggs() {
		this.buttful += 5;
		return null;
	}
	
	@Override
	protected String fillMouth(int mouthful) {
		super.fillMouth(mouthful);
		oralCreampie++;
		return incrementPerk(oralCreampie, Perk.SEMEN_SWALLOWER, 10, 6, 3);
	}
	
	private String incrementPerk(int currentValueOfStat, Perk perkToIncrement, int ... valuesToCheck) {
		int currentValueOfPerk = perks.get(perkToIncrement.toString(), 0);
		int valueToApply = valuesToCheck.length;
		for (int valueToCheck : valuesToCheck) {
			if (valueToApply <= currentValueOfPerk) break;
			if (currentValueOfStat >= valueToCheck) {
				perks.put(perkToIncrement.toString(), valueToApply);
				return "You gained " + perkToIncrement.getLabel() + " (Rank " + valueToApply + ")!";
			}
			valueToApply--;
		}
		return "";
	}
	
	public void setGoblinVirginity(boolean virginity) {
		goblinVirgin = virginity;
		if (!goblinVirgin) {
			receiveSex(new SexualExperience.SexualExperienceBuilder(1).build());
		}
	}
	
	public String receiveItem(Item item) {
		if (item.instantUse()) {
			consumeItem(item);
		}
		else {
			inventory.add(item);
		}
		return "You have received a(n) " + item.getName() + "!";
	}
	
	public boolean buyItem(Item item, int cost) {
		if (cost > money) {
			return false;
		}
		money -= cost;
		receiveItem(item);
		if (item instanceof Weapon) {
			weapon = (Weapon) item;
		}
		return true;
	}

	public Integer getMoney() {
		return money;
	}

	// this should obviously only accept a Weapon parameter
	public String setWeapon(Item item) {
		weapon = (Weapon) item;
		return "You equipped the " + item.getName() + ".";
	}
	
	public void setBaseDefense(int defense) {
		baseDefense = defense;
	}

	public String receiveSex(SexualExperience sex) {
		Array<String> result = new Array<String>();
		String temp;
		for (int ii = 0; ii < sex.getAnalSex(); ii++) {
			temp = receiveAnal();
			setCurrentPortrait(perks.get(Perk.ANAL_LOVER.toString(), 0) > 1 ? AssetEnum.PORTRAIT_LUST : AssetEnum.PORTRAIT_HIT);
			if (!temp.isEmpty()) {
				result.add(temp);
			}
		}
		for (int ii = 0; ii < sex.getCreampies(); ii++) {
			temp = fillButt(5);
			if (!temp.isEmpty()) {
				result.add(temp);
			}
		}
		for (int ii = 0; ii < sex.getAnalEjaculations(); ii++) {
			cumFromAnal();
			setCurrentPortrait(AssetEnum.PORTRAIT_AHEGAO);
		}
		for (int ii = 0; ii < sex.getOralSex(); ii++) {
			temp = receiveOral();
			setCurrentPortrait(AssetEnum.PORTRAIT_FELLATIO);
			if (!temp.isEmpty()) {
				result.add(temp);
			}
		}
		for (int ii = 0; ii < sex.getOralCreampies(); ii++) {
			temp = fillMouth(5);
			setCurrentPortrait(AssetEnum.PORTRAIT_MOUTHBOMB);
			if (!temp.isEmpty()) {
				result.add(temp);
			}
		}
		if (sex.getOralCreampies() > 0) {
			temp = modFood(1);
			result.add("You swallow enough to sate your hunger!" + (temp.isEmpty() ? "" : " " + temp)); 
		}
		for (int ii = 0; ii < sex.getFellatioEjaculations(); ii++) {
			cumFromOral();
		}
		
		if (sex.isCentaurSex() && perks.get(Perk.HORSE_LOVER.toString(), 0) == 0) {
			result.add("You gained " + Perk.HORSE_LOVER.getLabel() + " (Rank " + 1 + ")!");
			perks.put(Perk.HORSE_LOVER.toString(), 1);
		}
		if (sex.isOgreSex() && perks.get(Perk.GIANT_LOVER.toString(), 0) != 3) {
			result.add("You gained " + Perk.GIANT_LOVER.getLabel() + " (Rank " + (perks.get(Perk.GIANT_LOVER.toString(), 0) + 1) + ")!");
			perks.put(Perk.GIANT_LOVER.toString(), ((int)perks.get(Perk.GIANT_LOVER.toString(), 0)) + 1);
		}
		
		if (sex.isBeast() && perks.get(Perk.BEASTMASTER.toString(), 0) != 3) {
			result.add("You gained " + Perk.BEASTMASTER.getLabel() + " (Rank " + (perks.get(Perk.BEASTMASTER.toString(), 0) + 1) + ")!");
			perks.put(Perk.BEASTMASTER.toString(), ((int)perks.get(Perk.BEASTMASTER.toString(), 0)) + 1);
		}
		
		if (sex.isProstitution() && perks.get(Perk.LADY_OF_THE_NIGHT.toString(), 0) != 5) {
			result.add("You gained " + Perk.LADY_OF_THE_NIGHT.getLabel() + " (Rank " + (perks.get(Perk.LADY_OF_THE_NIGHT.toString(), 0) + 1) + ")!");
			perks.put(Perk.LADY_OF_THE_NIGHT.toString(), ((int)perks.get(Perk.LADY_OF_THE_NIGHT.toString(), 0)) + 1);
		}
				
		return joinWithLines(result);
	}
	
	private String joinWithLines(Array<String> toJoin) {
		String result = "";
		for (String s: toJoin) {
			result += s + "\n";
		}
		return result.trim();
	}
	
	private void cumFromAnal() {
		cameFromAnal++;
		justCame = true;
	}
	
	private void cumFromOral() {
		cameFromOral++;
		justCame = true;
	}

	public String getBootyLiciousness() {
		return bootyliciousness != null ? bootyliciousness.toString() : Bootyliciousness.Bubble.toString();
	}
	
	public String getLipFullness() {
		return lipFullness != null ? lipFullness.toString() : LipFullness.Thin.toString(); 
	}

	public AssetDescriptor<Texture> popPortraitPath() {
		if (justCame) {
			justCame = false;
			return AssetEnum.PORTRAIT_AHEGAO.getTexture();
		}
		AssetDescriptor<Texture> currentDisplay = new AssetDescriptor<Texture>(currentPortrait, Texture.class);
		currentPortrait = getNeutralFace().getTexture().fileName;
		return currentDisplay;
	}
	
	public AssetDescriptor<Texture> getPortraitPath() {
		if (justCame) {
			justCame = false;
			return AssetEnum.PORTRAIT_AHEGAO.getTexture();
		}
		return new AssetDescriptor<Texture>(currentPortrait, Texture.class);
	}

	private AssetEnum getNeutralFace() {
		switch (getHealthDegradation()) {
			case 0: return AssetEnum.PORTRAIT_SMILE;
			case 1: return AssetEnum.PORTRAIT_HAPPY;
			default: return AssetEnum.PORTRAIT_NEUTRAL;		
		}
	}

	public boolean isLoaded() {
		return loaded;
	}

	public void load() {
		loaded = true;
	}

	public String getStatBonusDisplay() {
		String display = "";
		int strBuff = getStrengthBuff();
		if (strBuff > 0) {
			display += "Strength buff: +" + strBuff + "\n";
		}
		int agiBuff = getAgilityBuff();
		if (agiBuff > 0) {
			display += "Agility buff: +" + agiBuff + "\n";
		}
		int endBuff = getEnduranceBuff();
		if (endBuff > 0) {
			display += "Endurance buff: +" + endBuff + "\n";
		}
		return display;
	}
		
	public String getStatPenaltyDisplay() {
		String display = "";
		int healthDegradation = getHealthDegradation();
		if (healthDegradation > 0) {
			display += "Low health: -" + healthDegradation + " STR, END, AGI\n";
		}
		
		int staminaDegradation = getStaminaDegradation();
		if (staminaDegradation > 0) {
			display += "Low stamina: -" + staminaDegradation + " STR, AGI\n";
		}
		int cumFilled = getCumInflation();
		if (cumFilled > 0) {
			display += "Too full of cum: -" + cumFilled + " AGI\n";
		}
		
		return display;
	}

	public String getStatTextDisplay() {
		String display = "Current Stats:\n";
		for (ObjectMap.Entry<Stat, Integer> statEntry : getStats()) {
			display += statEntry.key + " - " + statEntry.value + "\n";
		}
		return display;		
	}

	public void setCharacterName(String s) {
		name = s;
	}
	
	public String getCharacterName() {
		return name;
	}
	
	public int getQuestStatus(QuestType type) {
		return questFlags.get(type.toString(), 0);
	}	
	
	public void setQuestStatus(QuestType type, int status) {
		questFlags.put(type.toString(), status);
	}
	
	public enum QuestType {
		ORC, CRIER, INNKEEP, TRUDY, GOBLIN, OGRE, SPIDER, BROTHEL
	}
	
	public static class QuestFlag {
		public final QuestType type;
		public final int value;
		public QuestFlag() { type = null; value = 0; }
		
		public QuestFlag(QuestType type, int value) {
			this.type = type;
			this.value = value;
		}		
	}

	public String modMoney(Integer gold) {
		int loss = money > gold ? -gold : money; 
		money += gold;
		if (money < 0) {
			money = 0;
		}
		
		return gold  > 0 ? "Gained " + gold + " gold!" : "-" + loss + " gold!";
	}
	
	public String modDebt(Integer gold) {
		int loss = debt > gold ? -gold : debt; 
		debt += gold;
		if (debt < 0) {
			debt = 0;
		}
		
		return gold > 0 ? "You have incurred " + gold + " gold worth of debt." : gold < 0 ? "You've been relieved of " + loss + " gold worth of debt!" : "";
	}
	
	public int getBattlePerception() {
		return getPerception();
	}

	public int getCurrentDebt() {
		return debt;
	}

	public String debtTick(int ticks) {
		if (debt > 0) {
			return modDebt(10 * ticks);
		}
		return "";
	}

	public String timePass(Integer timePassed) {
		int currentDay = time / 6;
		time += timePassed;
		String result = debtTick((time / 6) - currentDay);
		return "Some time passes.\n" + modFood(-getMetabolicRate() * timePassed) + (result.isEmpty() ? "" : "\n" + result);
	}
	
	public int getMetabolicRate() { return hasHungerCharm() ? 3 : 4; }

	private boolean hasHungerCharm() {
		for (Item item : inventory) {
			if (item.getName().equals("Hunger Charm")) return true;
		}
		return false;
	}
	
	public String cureBleed(Integer bleedCure) {
		int currentBleed = statuses.get(StatusType.BLEEDING.toString(), 0);
		int temp = currentBleed;
		currentBleed -= bleedCure;
		if (currentBleed < 0) currentBleed = 0;
		temp = temp - currentBleed;
		statuses.put(StatusType.BLEEDING.toString(), currentBleed);
		return temp > 0 ? "Cured " + temp + " bleed point" + (temp > 1 ? "s." : ".") : "";
	}

	public Integer getTime() { return time; }

	public String increaseScout(int increase) {
		scout += increase;
		return "You scouted the surrounding areas.";
	}
	
	public String resetScout() {
		scout = 0;
		return null;
	}
}

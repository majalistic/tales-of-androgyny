package com.majalis.character;

import static com.majalis.character.Techniques.*;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.BooleanArray;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.majalis.asset.AssetEnum;
import com.majalis.character.Armor.ArmorType;
import com.majalis.character.Arousal.ArousalType;
import com.majalis.character.Item.Accessory;
import com.majalis.character.Item.AccessoryType;
import com.majalis.character.Item.ChastityCage;
import com.majalis.character.Item.EffectType;
import com.majalis.character.Item.Plug;
import com.majalis.character.Item.Potion;
import com.majalis.character.Item.Weapon;
import com.majalis.character.Item.WeaponType;
import com.majalis.save.MutationResult;
import com.majalis.save.MutationResult.MutationType;
import com.majalis.save.SaveManager.GameOver;
import com.majalis.save.SaveManager.JobClass;
import com.majalis.scenes.ShopScene.ShopCode;
import com.majalis.screens.TimeOfDay;
import com.majalis.technique.ClimaxTechnique.ClimaxType;

/*
 * Contains the current player character's statistics, including "party" statistics like food remaining
 */
public class PlayerCharacter extends AbstractCharacter {
	// this should be moved to the Stat enum itself
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
	protected int skillPoints;
	protected int magicPoints;
	protected int perkPoints;
	
	// advantage, range, and combat-lock(boolean) are shared properties between two creatures
	
	private BooleanArray luckStreak;
	
	/* out of battle only statistics */
	private int time;
	private int money;
	private int debt;
	private int lastShopRestock;
	
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
	
	private GameOver gameOver;
	
	@SuppressWarnings("unused")
	private PlayerCharacter() { if(arousal == null) arousal = new Arousal(ArousalType.PLAYER); }
	
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
			baseDefense = 0;
			money = 40;
			debt = 0;
			inventory = new Array<Item>();
			initInventory();
			setCharacterName("Hiro");
			questFlags = new ObjectMap<String, Integer>();
			time = 0;
		}
		
		skills = new ObjectMap<String, Integer>();
		for (Techniques basicTechnique: getBaseTechniques()) {
			skills.put(basicTechnique.toString(), 1);
		}
	
		justCame = false;
		loaded = false;
		setCurrentPortrait(AssetEnum.PORTRAIT_SMILE); // his smile and optimism, not yet gone
		luckStreak = new BooleanArray(new boolean[]{true, true, true, false, false, false});
		arousal = new Arousal(ArousalType.PLAYER);
	}
	
	private void initInventory() {
		inventory.clear();
		for (int ii = 10; ii <= 20; ii += 10) {
			inventory.add(new Potion(ii));
			inventory.add(new Potion(ii));
		}
	}
	
	private static ObjectSet<Techniques> getBaseTechniques() {
		ObjectSet<Techniques> baseTechniques = new ObjectSet<Techniques>();
		baseTechniques.addAll(
			DO_NOTHING, POWER_ATTACK, TEMPO_ATTACK, RESERVED_ATTACK, DUCK, SPRING_ATTACK, NEUTRAL_ATTACK, REVERSAL_ATTACK, CAREFUL_ATTACK, BLOCK, GUARD,
			PUSH_UP, KNEE_UP_HANDS, STAY, STAND_UP_HANDS, STAND_UP_KNEELING, KIP_UP, STAND_UP, STAY_KNELT, KNEE_UP, REST_FACE_DOWN, REST, JUMP_ATTACK, VAULT_OVER,
			RECEIVE_ANAL, RECEIVE_DOGGY, RECEIVE_STANDING, STRUGGLE_ORAL, STRUGGLE_DOGGY, STRUGGLE_ANAL, STRUGGLE_STANDING, RECEIVE_KNOT, SUCK_KNOT, SUCK_IT, BREAK_FREE_ANAL, BREAK_FREE_ORAL,
			SUBMIT, STRUGGLE_FULL_NELSON, BREAK_FREE_FULL_NELSON, STRUGGLE_PRONE_BONE, STRUGGLE_DOGGY,
			OPEN_WIDE, GRAB_IT, STROKE_IT, LET_GO, USE_ITEM, ITEM_OR_CANCEL,
			RECIPROCATE_FORCED, GET_FACE_RIDDEN, STRUGGLE_FACE_SIT, STRUGGLE_SIXTY_NINE, BREAK_FREE_FACE_SIT, ROLL_OVER_UP, ROLL_OVER_DOWN, RIPOSTE, EN_GARDE, POUNCE_DOGGY, POUND_DOGGY, POUNCE_ANAL, POUND_ANAL, POUNCE_PRONE_BONE, POUND_PRONE_BONE, ERUPT_ANAL, PULL_OUT, PULL_OUT_ORAL, PULL_OUT_ANAL, PULL_OUT_STANDING, RECEIVE_COCK, HURK, UH_OH,
			FORCE_DEEPTHROAT, CRUSH_ASS, BOUNCE_ON_IT, SQUEEZE_IT, BE_RIDDEN, PUSH_OFF, SELF_SPANK, POUT, DEEPTHROAT, WRAP_LEGS, PUSH_OFF_ATTEMPT, RIDE_ON_IT_REVERSE, BOUNCE_ON_IT_REVERSE, SQUEEZE_IT_REVERSE, RECEIVE_PRONE_BONE, BE_RIDDEN_REVERSE, PUSH_OFF_REVERSE, PUSH_OFF_ATTEMPT_REVERSE,
			OUROBOROS, ROUND_AND_ROUND, RECEIVE_OUROBOROS, STRUGGLE_OUROBOROS, MOUNT_FACE, FACEFUCK, GET_FACEFUCKED, STRUGGLE_FACEFUCK, RECEIVE_EGGS, ERUPT_ORAL,
			WRESTLE_TO_GROUND, WRESTLE_TO_GROUND_UP, PENETRATE_PRONE, PENETRATE_MISSIONARY, PIN, GRAPPLE, HOLD_WRESTLE, CHOKE, REST_WRESTLE, FLIP_PRONE, FLIP_SUPINE, RELEASE_PRONE, RELEASE_SUPINE, STRUGGLE_GROUND, BREAK_FREE_GROUND, GRIND, REST_GROUND_DOWN, STRUGGLE_GROUND_UP, BREAK_FREE_GROUND_UP, REVERSAL,
			FULL_REVERSAL, REST_GROUND_UP, SQUEEZE_STRUGGLE, BREAK_FREE_SQUEEZE, SQUEEZE_REST
		);
		return baseTechniques;
	}
	
	public static ObjectMap<Stat, Array<String>> getStatMap() {
		return statNameMap;
	}
	
	public void setCurrentPortrait(AssetEnum portrait) {
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
		initInventory();
		
		setShield(null, false);
		setArmor(null, false);
		setLegwear(null, false);
		setUnderwear(null, false);	
		
		skills.remove(COMBAT_HEAL.toString());
		skills.remove(INCANTATION.toString());
		skills.remove(BLITZ_ATTACK.toString());
		skills.remove(ALL_OUT_BLITZ.toString());
		skills.remove(HOLD_BACK.toString());
		perks.remove(Perk.WEAK_TO_ANAL.toString());
		cage = null;
		weapon = null;
		switch (jobClass) { 
			case WARRIOR: 
				skillPoints = 3; 
				skills.put(BLITZ_ATTACK.toString(), 1); 
				skills.put(ALL_OUT_BLITZ.toString(), 1); 
				skills.put(HOLD_BACK.toString(), 1); 
				perks.put(Perk.WEAK_TO_ANAL.toString(), 1);
				break;
			case PALADIN: 
				addSkill(COMBAT_HEAL, 1); 
				setCage(new ChastityCage(), true); 
				setArmor(new Armor(ArmorType.BREASTPLATE), true);
				setLegwear(new Armor(ArmorType.BATTLE_SKIRT), true);
				setUnderwear(new Armor(ArmorType.UNDERWEAR), true);
				break;
			case THIEF: 
				skillPoints = 5; 
				food += 40;
				break;
			case MAGE: 
				setUnderwear(new Armor(ArmorType.UNDERWEAR), true);
				magicPoints = 2; 
				break;
			case RANGER: 
				Weapon bow = new Weapon(WeaponType.Bow); 
				inventory.add(bow);
				weapon = bow;
				break;
			case ENCHANTRESS: 
				magicPoints = 1; 
				perkPoints = 3; 
				break;
			default:
		}
		if (jobClass != JobClass.PALADIN && jobClass != JobClass.MAGE) {
			setArmor(new Armor(ArmorType.CLOTH_TOP), true);
			setLegwear(new Armor(ArmorType.SKIRT), true);
			setUnderwear(new Armor(ArmorType.UNDERWEAR), true);
		}
		
		setShield(new Armor(ArmorType.SHIELD), true);
	}

	// this needs to consolidate logic with the getTechniques method
	public Array<Technique> getPossibleTechniques(AbstractCharacter target) {
		Techniques[] temp = getPossibleKnownTechniques(target).toArray(Techniques.class);
		Array<Technique> possibles = getTechniques(target, temp);
		if (possibles.size == 0) possibles = getTechniques(target, DO_NOTHING);
		return possibles;
	}
	
	private Array<Techniques> getPossibleKnownTechniques(AbstractCharacter target) {
		Array<Techniques> possibles = new Array<Techniques>();
		switch(stance) {
			case BLITZ:
				return getTechniques(ALL_OUT_BLITZ, HOLD_BACK);
			case COUNTER:
				return getTechniques(RIPOSTE, EN_GARDE);
			case OFFENSIVE:
				possibles = getTechniques(BLITZ_ATTACK, POWER_ATTACK, ARMOR_SUNDER, RECKLESS_ATTACK, KNOCK_DOWN, VAULT, FEINT_AND_STRIKE, TEMPO_ATTACK, RESERVED_ATTACK);
				if (target.getStance() == Stance.SUPINE && target.isErect() && target.enemyType.canBeRidden()) {
					possibles.addAll(getTechniques(SIT_ON_IT, TURN_AND_SIT));
				}
				if (target.stance == Stance.HANDS_AND_KNEES && isErect() && target.enemyType.isPounceable()) {
					possibles.addAll(getTechniques(POUNCE_DOGGY));
				}
				else if (target.stance == Stance.PRONE && isErect() && target.enemyType.isPounceable() && target.enemyType.canWrestle()) {
					possibles.addAll(getTechniques(WRESTLE_TO_GROUND));
				}
				else if (target.stance == Stance.SUPINE && isErect() && target.enemyType.isPounceable()) {
					if (target.enemyType.canWrestle()) {
						possibles.addAll(getTechniques(WRESTLE_TO_GROUND_UP, MOUNT_FACE));
					}
					else {
						possibles.addAll(getTechniques(MOUNT_FACE));
					}
				}
				else if (target.stance == Stance.KNEELING && isErect() && target.enemyType.isPounceable()) {
					possibles.addAll(getTechniques(IRRUMATIO));
				}
				return possibles;
			case BALANCED:
				possibles = getTechniques(SPRING_ATTACK, NEUTRAL_ATTACK, CAUTIOUS_ATTACK, BLOCK, INCANTATION, SLIDE, DUCK, HIT_THE_DECK);
				if (hasItemsToUse()) {
					possibles.addAll(getTechniques(USE_ITEM));
				}
				if (target.getStance() == Stance.SUPINE && target.isErect() && target.enemyType.canBeRidden()) {
					possibles.addAll(getTechniques(SIT_ON_IT, TURN_AND_SIT));
				}
				if (target.stance == Stance.HANDS_AND_KNEES && isErect() && target.enemyType.isPounceable()) {
					possibles.addAll(getTechniques(POUNCE_DOGGY));
				}
				else if (target.stance == Stance.PRONE && isErect() && target.enemyType.isPounceable() && target.enemyType.canWrestle()) {
					possibles.addAll(getTechniques(WRESTLE_TO_GROUND));
				}
				else if (target.stance == Stance.SUPINE && isErect() && target.enemyType.isPounceable()) {
					if (target.enemyType.canWrestle()) {
						possibles.addAll(getTechniques(WRESTLE_TO_GROUND_UP, MOUNT_FACE));
					}
					else {
						possibles.addAll(getTechniques(MOUNT_FACE));
					}
				}
				return possibles;
			case DEFENSIVE:
				return getTechniques(REVERSAL_ATTACK, CAREFUL_ATTACK, GUARD, TAUNT, SECOND_WIND, PARRY, INCANTATION, DUCK);
			case SEDUCTION:
				return getTechniques(TAUNT, BLOCK);
			case PRONE:
				possibles = getTechniques(REST_FACE_DOWN, ROLL_OVER_UP);
				if (currentStamina >= 0) {
					possibles.addAll(getTechniques(PUSH_UP));
				}
				if (currentStamina >= 2) {
					possibles.addAll(getTechniques(KNEE_UP));
				}
				if (currentStamina >= 4 && stability.compareTo(Stability.Dazed) >= 0) {
					possibles.addAll(getTechniques(STAND_UP));
				}
				if (currentStamina >= 6) {
					possibles.addAll(getTechniques(KIP_UP));
				}
				return possibles;
			case SUPINE:
				possibles = getTechniques(REST, ROLL_OVER_DOWN);
				if (currentStamina >= 0) {
					possibles.addAll(getTechniques(PUSH_UP));
				}
				if (currentStamina >= 2) {
					possibles.addAll(getTechniques(KNEE_UP));
				}
				if (currentStamina >= 4 && stability.compareTo(Stability.Dazed) >= 0) {
					possibles.addAll(getTechniques(STAND_UP));
				}
				if (currentStamina >= 6) {
					possibles.addAll(getTechniques(KIP_UP));
				}
				return possibles;
			case HANDS_AND_KNEES:
				possibles = getTechniques(STAY);
				if (currentStamina >= 0) {
					possibles.addAll(getTechniques(KNEE_UP_HANDS));
				}
				if (currentStamina >= 2) {
					possibles.addAll(getTechniques(STAND_UP_HANDS));
				}
				return possibles;
			case KNEELING:
				possibles = getTechniques(UPPERCUT, STAY_KNELT);
				if (target.isErect() && target.enemyType != EnemyEnum.SLIME && target.enemyType.isPounceable()) {
					possibles.addAll(getTechniques(GRAB_IT));
				}
				if (currentStamina >= 2) {
					possibles.addAll(getTechniques(STAND_UP_KNEELING));
				}
				return possibles;
			case AIRBORNE:
				return getTechniques(JUMP_ATTACK, VAULT_OVER);
			case FULL_NELSON_BOTTOM:
				if (currentStamina <= 0) {
					return getTechniques(SUBMIT);
				}
				else if (hasGrappleAdvantage()) {
					return getTechniques(SUBMIT, BREAK_FREE_FULL_NELSON);
				}
				return getTechniques(SUBMIT, STRUGGLE_FULL_NELSON);
			case DOGGY_BOTTOM:
				if (hasGrappleAdvantage()) {
					possibles = getTechniques(RECEIVE_DOGGY, BREAK_FREE_ANAL);
				}
				else {
					possibles = getTechniques(RECEIVE_DOGGY, STRUGGLE_DOGGY);
				}
				if (perks.get(Perk.CATAMITE.toString(), 0) > 0 || perks.get(Perk.ANAL_ADDICT.toString(), 0) > 1) {
					possibles.addAll(getTechniques(SELF_SPANK));
				}
				return possibles;
			case PRONE_BONE_BOTTOM:
				if (hasGrappleAdvantage()) {
					possibles = getTechniques(RECEIVE_PRONE_BONE, BREAK_FREE_ANAL);
				}
				else {
					possibles = getTechniques(RECEIVE_PRONE_BONE, STRUGGLE_PRONE_BONE);
				}
				return possibles;
			case ANAL_BOTTOM:
				if (wrapLegs) {
					return getTechniques(RECEIVE_ANAL);
				}
				if (hasGrappleAdvantage()) {
					possibles = getTechniques(RECEIVE_ANAL, POUT, BREAK_FREE_ANAL);
				}
				else {
					possibles = getTechniques(RECEIVE_ANAL, POUT, STRUGGLE_ANAL);
				}
				if (!wrapLegs && (perks.get(Perk.CATAMITE.toString(), 0) > 0 || perks.get(Perk.ANAL_ADDICT.toString(), 0) > 1)) {
					possibles.addAll(getTechniques(WRAP_LEGS));
				}
				return possibles;
			case HANDY_BOTTOM:
				return getTechniques(STROKE_IT, LET_GO, OPEN_WIDE);
			case STANDING_BOTTOM:
				if (hasGrappleAdvantage()) {
					return getTechniques(RECEIVE_STANDING, BREAK_FREE_ANAL);
				}
				return getTechniques(RECEIVE_STANDING, STRUGGLE_STANDING);
			case COWGIRL_BOTTOM:
				return getTechniques(RIDE_ON_IT, BOUNCE_ON_IT, SQUEEZE_IT, STAND_OFF_IT);
			case REVERSE_COWGIRL_BOTTOM:
				return getTechniques(RIDE_ON_IT_REVERSE, BOUNCE_ON_IT_REVERSE, SQUEEZE_IT_REVERSE, STAND_OFF_IT);
			case KNOTTED_BOTTOM:
				return getTechniques(RECEIVE_KNOT);
			case MOUTH_KNOTTED_BOTTOM:
				return getTechniques(SUCK_KNOT);
			case OVIPOSITION_BOTTOM:
				return getTechniques(RECEIVE_EGGS);
			case FELLATIO_BOTTOM:
				if (hasGrappleAdvantage()) {
					possibles = getTechniques(SUCK_IT, BREAK_FREE_ORAL);
				}
				else {
					possibles = getTechniques(SUCK_IT, STRUGGLE_ORAL);
				}
				if (perks.get(Perk.MOUTH_MANIAC.toString(), 0) > 1) {
					possibles.addAll(getTechniques(DEEPTHROAT));
				}
				return possibles;
			case FACEFUCK_BOTTOM:
				if (hasGrappleAdvantage()) {
					possibles = getTechniques(GET_FACEFUCKED, BREAK_FREE_ORAL);
				}
				else {
					possibles = getTechniques(GET_FACEFUCKED, STRUGGLE_FACEFUCK);
				}
				return possibles;
			case OUROBOROS_BOTTOM:
				if (hasGrappleAdvantage()) {
					possibles = getTechniques(RECEIVE_OUROBOROS, BREAK_FREE_ORAL);
				}
				else {
					possibles = getTechniques(RECEIVE_OUROBOROS, STRUGGLE_OUROBOROS);
				}
				return possibles;
			case FACE_SITTING_BOTTOM:
				if (hasGrappleAdvantage()) {
					return getTechniques(GET_FACE_RIDDEN, BREAK_FREE_FACE_SIT);
				}
				return getTechniques(GET_FACE_RIDDEN, STRUGGLE_FACE_SIT);
			case SIXTY_NINE_BOTTOM:
				if (hasGrappleAdvantage()) {
					return getTechniques(RECIPROCATE_FORCED, BREAK_FREE_ORAL);
				}
				return getTechniques(RECIPROCATE_FORCED, STRUGGLE_SIXTY_NINE);
			case HELD:
				return getTechniques(UH_OH);
			case SPREAD:
				return getTechniques(RECEIVE_COCK);
			case PENETRATED:
				return getTechniques(HURK);
			case CASTING:
				return getTechniques(COMBAT_FIRE, COMBAT_HEAL, HEAL, TITAN_STRENGTH, WEAKENING_CURSE, FOCUS_ENERGY);
			case ITEM:
				possibles.addAll(getTechniques(ITEM_OR_CANCEL));
				return possibles;
			case FELLATIO:
				if (arousal.isClimax()) {
					return getTechniques(ERUPT_ORAL);
				}
				else {
					return getTechniques(IRRUMATIO, PULL_OUT_ORAL);
				}	
			case FACEFUCK:
				if (arousal.isClimax()) {
					return getTechniques(ERUPT_ORAL);
				}
				else {
					return getTechniques(FACEFUCK, PULL_OUT_ORAL);
				}	
			case OUROBOROS:
				if (arousal.isClimax()) {
					return getTechniques(ERUPT_ORAL);
				}
				else {
					return getTechniques(ROUND_AND_ROUND, PULL_OUT_ORAL);
				}	
			case GROUND_WRESTLE:
				if (currentStamina <= 0 || grappleStatus == GrappleStatus.HELD) {
					return getTechniques(REST_WRESTLE);
				}
				possibles.addAll(getTechniques(GRAPPLE, HOLD_WRESTLE, REST_WRESTLE));
				if (hasGrappleAdvantage()) {
					possibles.addAll(getTechniques(PIN));
				}
				if (target.getStance() == Stance.GROUND_WRESTLE_FACE_UP) {
					if (grappleStatus == GrappleStatus.HOLD && isErect()) {
						possibles.addAll(getTechniques(PENETRATE_MISSIONARY));
					}
					else if (hasGrappleAdvantage()) {
						possibles.addAll(getTechniques(FLIP_PRONE, RELEASE_SUPINE));
					}
				}
				else if (target.getStance() == Stance.GROUND_WRESTLE_FACE_DOWN) {
					if (grappleStatus == GrappleStatus.HOLD && isErect()) {
						possibles.addAll(getTechniques(PENETRATE_PRONE));
					}
					else if (hasGrappleAdvantage()) {
						possibles.addAll(getTechniques(FLIP_SUPINE, RELEASE_PRONE));
					}
				}
				return possibles;
			case GROUND_WRESTLE_FACE_DOWN:
				if (currentStamina <= 0 || grappleStatus == GrappleStatus.HELD) {
					return getTechniques(REST_GROUND_DOWN);
				}
				possibles.addAll(getTechniques(REST_GROUND_DOWN, GRIND));
				if (hasGrappleAdvantage()) {
					possibles.addAll(getTechniques(BREAK_FREE_GROUND));
				}
				else {
					possibles.addAll(getTechniques(STRUGGLE_GROUND));
				}
				return possibles;
			case GROUND_WRESTLE_FACE_UP:
				if (currentStamina <= 0 || grappleStatus == GrappleStatus.HELD) {
					return getTechniques(REST_GROUND_UP);
				}
				possibles.addAll(getTechniques(REST_GROUND_UP));
				if (hasGrappleAdvantage()) {
					possibles.addAll(getTechniques(BREAK_FREE_GROUND_UP, FULL_REVERSAL));
				}
				else {
					if (grappleStatus.isDisadvantage()) {
						possibles.addAll(getTechniques(REVERSAL));
					}
					possibles.addAll(getTechniques(STRUGGLE_GROUND_UP));
				}
				return possibles;
			case WRAPPED_BOTTOM:
				if (currentStamina <= 0) {
					return getTechniques(SQUEEZE_REST);
				}
				possibles.addAll(getTechniques(SQUEEZE_REST));
				if (hasGrappleAdvantage()) {
					possibles.addAll(getTechniques(BREAK_FREE_SQUEEZE));
				}
				else {
					possibles.addAll(getTechniques(SQUEEZE_STRUGGLE));
				}
				return possibles;
			case PRONE_BONE:
			case DOGGY:
			case ANAL:
			case STANDING:
				if (arousal.isClimax()) {
					return getTechniques(ERUPT_ANAL);
				}
				else {
					if (stance == Stance.ANAL) {
						return getTechniques(POUND_ANAL, PULL_OUT_ANAL);
					}
					else if (stance == Stance.DOGGY) {
						return getTechniques(POUND_DOGGY, CRUSH_ASS, PULL_OUT);
					}
					else if (stance == Stance.PRONE_BONE) {
						return getTechniques(POUND_PRONE_BONE, PULL_OUT);
					}
					else {
						return getTechniques(POUND_STANDING, PULL_OUT_STANDING);
					}		
				}
			case COWGIRL:
				if (arousal.isClimax()) {
					return getTechniques(ERUPT_COWGIRL);
				}
				if (hasGrappleAdvantage()) {
					return getTechniques(BE_RIDDEN, PUSH_OFF);
				}
				else {
					return getTechniques(BE_RIDDEN, PUSH_OFF_ATTEMPT);
				}
			case REVERSE_COWGIRL:
				if (arousal.isClimax()) {
					return getTechniques(ERUPT_COWGIRL);
				}
				if (hasGrappleAdvantage()) {
					return getTechniques(BE_RIDDEN_REVERSE, PUSH_OFF_REVERSE);
				}
				else {
					return getTechniques(BE_RIDDEN_REVERSE, PUSH_OFF_ATTEMPT_REVERSE);
				}
			case ERUPT:
				stance = Stance.BALANCED;
				possibles = getPossibleKnownTechniques(target);
				stance = Stance.ERUPT;
				return possibles;
			default: return possibles;
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
	
	private Array<Techniques> getTechniques(Techniques... possibilities) {
		return new Array<Techniques>(possibilities);
	}
	
	private Array<Technique> getTechniques(AbstractCharacter target, Techniques... possibilities) {
		Array<Technique> possibleTechniques = new Array<Technique>();
		
		for (Techniques technique : possibilities) {
			int skillLevel = skills.get(technique.toString(), 0);
			if (skillLevel > 0 || getBaseTechniques().contains(technique)) {
				// this should pass the players stats and other relevant info to the technique, rather than passing some generic "force" value - also passing the weapon separately so that the technique can determine if it's relevant or not - basically, this class should create a "current state" object
				// this may need to filter out techniques which are not possible because of current state evalutations?
				if (technique == Techniques.ITEM_OR_CANCEL) {
					possibleTechniques.addAll(getConsumableItems(target));
				}
				possibleTechniques.add(new Technique(technique.getTrait(), getCurrentState(target), skillLevel));
			}	
		}
		return possibleTechniques;
	}
	
	@Override
	public Attack doAttack(Attack resolvedAttack) {		
		if (resolvedAttack.getLust() > 0) {
			currentPortrait = AssetEnum.PORTRAIT_GRIN.getTexture().fileName;
			// taunt increases self lust too
			arousal.increaseArousal(2, perks, ClimaxType.NULL); // this will depend on the type of taunt used
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
				setCurrentPortrait(perks.get(Perk.ANAL_ADDICT.toString(), 0) > 1 ? AssetEnum.PORTRAIT_AHEGAO : AssetEnum.PORTRAIT_POUT);
			}
			else if (oldStance.isOralReceptive()) {
				setCurrentPortrait(AssetEnum.PORTRAIT_MOUTHBOMB);
			}
				
		}
		if (stance.isAnalReceptive()) {
			setCurrentPortrait(perks.get(Perk.ANAL_ADDICT.toString(), 0) > 1 ? AssetEnum.PORTRAIT_LUST : AssetEnum.PORTRAIT_HIT);
		}
		else if (stance.isOralReceptive()) {
			setCurrentPortrait(resolvedAttack.isClimax() ? AssetEnum.PORTRAIT_MOUTHBOMB : AssetEnum.PORTRAIT_FELLATIO);
		}
		
		if (stance == Stance.HELD) {
			setCurrentPortrait(perks.get(Perk.SIZE_QUEEN.toString(), 0) > 1 ? AssetEnum.PORTRAIT_LOVE : AssetEnum.PORTRAIT_SURPRISE);
		}
		
		if (oldStance.isAnalReceptive() && !stance.isAnalReceptive()) {
			wrapLegs = false;
		}
		
		if (stance == Stance.OVIPOSITION_BOTTOM) {
			receiveEggs();
		}
		
		if (!oldStance.isAnalReceptive() && stance.isAnalReceptive()) {
			Array<MutationResult> temp = receiveAnal(); 
			
			if (temp.size > 0) result = temp.first().getText();
			else result = "";
			
			if (stance != Stance.PENETRATED) {
				setCurrentPortrait(perks.get(Perk.ANAL_ADDICT.toString(), 0) > 1 ? AssetEnum.PORTRAIT_LOVE : AssetEnum.PORTRAIT_SURPRISE);
			}
			
			else {
				setCurrentPortrait(perks.get(Perk.ANAL_ADDICT.toString(), 0) > 1 ? AssetEnum.PORTRAIT_LOVE : AssetEnum.PORTRAIT_AHEGAO);
			}
			
			
			if (result != null) { resolvedAttack.addMessage(result); } 
			if (resolvedAttack.getUser().equals("Goblin")) {
				setGoblinVirginity(false);
			}
			a2m = true;
		}
		else if (!oldStance.isOralReceptive() && stance.isOralReceptive()) {
			Array<MutationResult> temp = receiveOral(); 
			
			if (temp.size > 0) result = temp.first().getText();
			else result = "";
			
			if (!result.equals("")) { resolvedAttack.addMessage(result); } 
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
		grappleStatus = GrappleStatus.NULL;
		a2m = false;
		buttful = Math.max(0, buttful - 10);
		mouthful = 0;
		if (armor != null) armor.refresh();	
		if (legwear != null) legwear.refresh();	
		if (underwear != null) underwear.refresh();	
		if (shield != null) shield.refresh();
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
		scout = 0;
		heartbeat = 0;
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
			case MAGIC: baseMagic = amount; manaTiers = new IntArray(new int[]{baseMagic > 1 ? baseMagic * 3 + 4 : 0}); setManaToMax(); break;
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
			if (!hasMagic()) {
				manaTiers = new IntArray(new int[]{baseMagic > 1 ? baseMagic * 3 + 4 : 0});
			}
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
		int min = getBaseStat(Stat.STRENGTH);
		Stat minStat = Stat.STRENGTH;
		for (Stat stat : Stat.values()) {
			if (getBaseStat(stat) < min) {
				min = getBaseStat(stat);
				minStat = stat;
			}
		}
		setStat(minStat, getBaseStat(minStat) + 1);
	}
	
	public void setPerks(ObjectMap<Perk, Integer> perks) {
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
		if (perks.get(Perk.SMARTER, 0) > this.perks.get(Perk.SMARTER.toString(), 0)) {
			int perIncrease = perks.get(Perk.SMARTER, 0) -  this.perks.get(Perk.SMARTER.toString(), 0);
			basePerception += perIncrease;
		}
		if (perks.get(Perk.WITCHER, 0) > this.perks.get(Perk.WITCHER.toString(), 0)) {
			int magIncrease = perks.get(Perk.WITCHER, 0) -  this.perks.get(Perk.WITCHER.toString(), 0);
			baseMagic += magIncrease;
		}
		if (perks.get(Perk.HOTTER, 0) > this.perks.get(Perk.HOTTER.toString(), 0)) {
			int chaIncrease = perks.get(Perk.HOTTER, 0) -  this.perks.get(Perk.HOTTER.toString(), 0);
			baseCharisma += chaIncrease;
		}
		if (perks.get(Perk.WELLROUNDED, 0) > 0 && !(this.perks.get(Perk.WELLROUNDED.toString(), 0) > 0) ) {
			increaseLowestStat();
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
		int level = 5;
		for (int ii : new int[]{14, 11, 8, 5, 2}) {
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
		if (stance.isErotic()) {
			return increaseLust(1);
		}
		return null;
	}
	
	@Override
	protected String increaseLust(int lustIncrease) {
		String spurt = "";
		arousal.increaseArousal(lustIncrease, perks, stance.getClimaxType(), stance.isErotic());
		if (arousal.isClimax() && stance.isEroticReceptive()) {
			spurt = climax();
		}
		return !spurt.isEmpty() ? spurt : null;
	}
	
	@Override
	protected String climax() {
		String spurt = "";
		String result = null;
		boolean weakToAnal = perks.get(Perk.WEAK_TO_ANAL.toString(), 0) > 0;
		
		switch (stance) {
			case PENETRATED:
			case KNOTTED_BOTTOM:
			case DOGGY_BOTTOM: 
			case ANAL_BOTTOM:
				spurt = "Awoo! Semen spurts out your untouched cock as your hole is violated!\n"
					+	"You feel it with your ass, like a girl! Your face is red with shame!\n"
					+	"Got a little too comfortable, eh?\n";
				cumFromAnal();
				result = incrementPerk(cameFromAnal, Perk.ANAL_ADDICT, weakToAnal ? 3 : 5, weakToAnal ? 2 : 3, 1);
				break;
			case COWGIRL_BOTTOM:
				spurt = "Awoo! Semen spurts out your untouched cock as your hole is violated!\n"
						+	"You feel it with your ass, like a girl! Your face is red with shame!\n"
						+	"It spews all over them!\n";
				cumFromAnal();
				result = incrementPerk(cameFromAnal, Perk.ANAL_ADDICT, weakToAnal ? 3 : 5, weakToAnal ? 2 : 3, 1);
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
				arousal.climax(ClimaxType.BACKWASH);
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
				stance = Stance.ERUPT;
				arousal.climax(ClimaxType.ANAL);
				break;
			case FELLATIO:
				stance = Stance.ERUPT;
				arousal.climax(ClimaxType.ORAL);
				break;
			default: 
				spurt = "You spew your semen onto the ground!\n"; 
				arousal.climax(ClimaxType.BACKWASH);
		}
		if (result != null) spurt += result + "\n";
		spurt += "You're now flaccid!\n";
		return spurt;
	}

	public Array<MutationResult> modExperience(Integer exp) { 
		int levels = getStoredLevels();
		experience += exp; 
		levels = getStoredLevels() - levels;
		
		Array<MutationResult> results = getResult(exp > 0 ? "You gain " + exp + " experience!" : "You lose " + -exp + " experience!", exp, MutationType.EXP);
		if (levels > 0) results.add(new MutationResult("You gain " + levels + " level" + (levels > 1 ? "s" : "") + "!"));
		return results;
	}
	
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
		return getMaxMana() > 0;
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
		return masculinityLevel < 1 ? AssetEnum.MARS_ICON_0.getTexture() : masculinityLevel < 6 ? AssetEnum.MARS_ICON_1.getTexture() : masculinityLevel < 16 ? AssetEnum.MARS_ICON_2.getTexture() : masculinityLevel < 30 ? AssetEnum.MARS_ICON_3.getTexture() : AssetEnum.MARS_ICON_4.getTexture();
	}
	
	public boolean isVirgin(EnemyEnum enemyEnum) {
		switch (enemyEnum) {
			case GOBLIN: return goblinVirgin;
			default:
		}
		return false;
	}
	
	private Array<MutationResult> receiveAnal() {
		String result = receivedAnal == 0 ? "You are no longer a virgin!\n" : "";
		result += isPlugged() ? plug.getName() + " removed! " : "";
		plug = null;
		receivedAnal++;
		boolean weakToAnal = perks.get(Perk.WEAK_TO_ANAL.toString(), 0) > 0;
		result += incrementPerk(receivedAnal, Perk.ANAL_ADDICT, weakToAnal ? 5 : 10, weakToAnal ? 3 : 6, weakToAnal ? 1 : 3);
		return result.equals("") ? new Array<MutationResult>() : getResult(result);
	}
	
	private Array<MutationResult> receiveOral() {
		receivedOral++;
		String result = incrementPerk(receivedOral, Perk.MOUTH_MANIAC, 10, 6, 3);
		return result.equals("") ? new Array<MutationResult>() : getResult(result);
	}
	
	@Override
	protected Array<MutationResult> fillButt(int buttful) {
		super.fillButt(buttful);
		analCreampie++;
		String result = incrementPerk(analCreampie, Perk.CREAMPIE_COLLECTOR, 10, 6, 3);
		return result.equals("") ? new Array<MutationResult>() : getResult(result);
	}
	
	protected String receiveEggs() {
		this.buttful += 5;
		return null;
	}
	
	@Override
	protected Array<MutationResult> fillMouth(int mouthful) {
		super.fillMouth(mouthful);
		oralCreampie++;
		String result = incrementPerk(oralCreampie, Perk.CUM_CONNOISSEUR, 10, 6, 3);
		return result.equals("") ? new Array<MutationResult>() : getResult(result);
	}
	
	private String incrementPerk(int currentValueOfStat, Perk perkToIncrement, int ... valuesToCheck) {
		String result = "";
		int currentValueOfPerk = perks.get(perkToIncrement.toString(), 0);
		int valueToApply = valuesToCheck.length;
		for (int valueToCheck : valuesToCheck) {
			if (valueToApply <= currentValueOfPerk) break;
			if (currentValueOfStat >= valueToCheck) {
				perks.put(perkToIncrement.toString(), valueToApply);
				result += "You gained " + perkToIncrement.getLabel() + " (Rank " + valueToApply + ")!";
			}
			valueToApply--;
		}
		if (perkToIncrement != Perk.COCK_LOVER) {
			String cockLoverGain = incrementPerk(getMasculinityLevel(), Perk.COCK_LOVER, 30, 27, 24, 21, 18, 15, 12, 9, 6, 3);
			result += (!result.equals("") && !cockLoverGain.equals("") ? "\n" : "") + cockLoverGain;
		}
		return result;
	}
	
	public void setGoblinVirginity(boolean virginity) {
		goblinVirgin = virginity;
		if (!goblinVirgin) {
			receiveSex(new SexualExperience.SexualExperienceBuilder(1).build());
		}
	}
	
	public Array<MutationResult> receiveItem(Item item) {
		if (item.instantUse()) {
			consumeItem(item);
		}
		else if (item instanceof ChastityCage) {
			setCage(item, true);
		}
		else {
			inventory.add(item);
		}
		return getResult("You have received a(n) " + item.getName() + "!");
	}
	
	private Array<MutationResult> getResult(String text) {
		return new Array<MutationResult>(new MutationResult[]{new MutationResult(text)});
	}
	
	private Array<MutationResult> getResult(String text, int mod, MutationType type) {
		return new Array<MutationResult>(new MutationResult[]{new MutationResult(text, mod, type)});
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
		Weapon equipWeapon = (Weapon) item;
		boolean alreadyEquipped = equipWeapon.equals(this.weapon);
		this.weapon = alreadyEquipped ? null : equipWeapon;
		return "You " + (alreadyEquipped ? "unequipped" : "equipped") + " the " + equipWeapon.getName() + ".";
	}
	
	public void setBaseDefense(int defense) {
		baseDefense = defense;
	}

	public Array<MutationResult> receiveSex(SexualExperience sex) {
		Array<MutationResult> result = new Array<MutationResult>();
		for (int ii = 0; ii < sex.getAnalSex(); ii++) {
			result.addAll(receiveAnal());
			setCurrentPortrait(perks.get(Perk.ANAL_ADDICT.toString(), 0) > 1 ? AssetEnum.PORTRAIT_LUST : AssetEnum.PORTRAIT_HIT);
		}
		for (int ii = 0; ii < sex.getCreampies(); ii++) {
			result.addAll(fillButt(5));
		}
		for (int ii = 0; ii < sex.getAnalEjaculations(); ii++) {
			cumFromAnal();
			setCurrentPortrait(AssetEnum.PORTRAIT_AHEGAO);
		}
		for (int ii = 0; ii < sex.getOralSex(); ii++) {
			result.addAll(receiveOral());
			setCurrentPortrait(AssetEnum.PORTRAIT_FELLATIO);
		}
		for (int ii = 0; ii < sex.getOralCreampies(); ii++) {
			result.addAll(fillMouth(5));
			setCurrentPortrait(AssetEnum.PORTRAIT_MOUTHBOMB);
		}
		if (sex.getOralCreampies() > 0) {
			result.add(new MutationResult("You swallow enough to sate your hunger!"));
			result.addAll(modFood(1));
		}
		for (int ii = 0; ii < sex.getFellatioEjaculations(); ii++) {
			cumFromOral();
		}
		
		if (sex.isCentaurSex() && perks.get(Perk.EQUESTRIAN.toString(), 0) == 0) {
			result.add(new MutationResult("You gained " + Perk.EQUESTRIAN.getLabel() + " (Rank " + 1 + ")!"));
			perks.put(Perk.EQUESTRIAN.toString(), 1);
		}
		if (sex.isOgreSex() && perks.get(Perk.SIZE_QUEEN.toString(), 0) != 3) {
			result.add(new MutationResult("You gained " + Perk.SIZE_QUEEN.getLabel() + " (Rank " + (perks.get(Perk.SIZE_QUEEN.toString(), 0) + 1) + ")!"));
			perks.put(Perk.SIZE_QUEEN.toString(), ((int)perks.get(Perk.SIZE_QUEEN.toString(), 0)) + 1);
		}
		
		if (sex.isBeast() && perks.get(Perk.BEASTMASTER.toString(), 0) != 3) {
			result.add(new MutationResult("You gained " + Perk.BEASTMASTER.getLabel() + " (Rank " + (perks.get(Perk.BEASTMASTER.toString(), 0) + 1) + ")!"));
			perks.put(Perk.BEASTMASTER.toString(), ((int)perks.get(Perk.BEASTMASTER.toString(), 0)) + 1);
		}
		
		if (sex.isProstitution() && perks.get(Perk.LADY_OF_THE_NIGHT.toString(), 0) != 20) {
			result.add(new MutationResult("You gained " + Perk.LADY_OF_THE_NIGHT.getLabel() + " (Rank " + (perks.get(Perk.LADY_OF_THE_NIGHT.toString(), 0) + 1) + ")!"));
			perks.put(Perk.LADY_OF_THE_NIGHT.toString(), ((int)perks.get(Perk.LADY_OF_THE_NIGHT.toString(), 0)) + 1);
			
			if (sex.getHandy() > 0 && perks.get(Perk.CRANK_MASTER.toString(), 0) != 10) {
				result.add(new MutationResult("You gained " + Perk.CRANK_MASTER.getLabel() + " (Rank " + (perks.get(Perk.CRANK_MASTER.toString(), 0) + 1) + ")!"));
				perks.put(Perk.CRANK_MASTER.toString(), ((int)perks.get(Perk.CRANK_MASTER.toString(), 0)) + 1);
				
			}
			if (sex.getOralSex() > 0 && perks.get(Perk.BLOWJOB_EXPERT.toString(), 0) != 10) {
				result.add(new MutationResult("You gained " + Perk.BLOWJOB_EXPERT.getLabel() + " (Rank " + (perks.get(Perk.BLOWJOB_EXPERT.toString(), 0) + 1) + ")!"));
				perks.put(Perk.BLOWJOB_EXPERT.toString(), ((int)perks.get(Perk.BLOWJOB_EXPERT.toString(), 0)) + 1);
				
			}
			if (sex.getAnalSex() > 0 && perks.get(Perk.PERFECT_BOTTOM.toString(), 0) != 10) {
				result.add(new MutationResult("You gained " + Perk.PERFECT_BOTTOM.getLabel() + " (Rank " + (perks.get(Perk.PERFECT_BOTTOM.toString(), 0) + 1) + ")!"));
				perks.put(Perk.PERFECT_BOTTOM.toString(), ((int)perks.get(Perk.PERFECT_BOTTOM.toString(), 0)) + 1);
				
			}
		}
		
		if (sex.isBird() && perks.get(Perk.CUCKOO_FOR_CUCKOO.toString(), 0) != 3) {
			result.add(new MutationResult("You gained " + Perk.CUCKOO_FOR_CUCKOO.getLabel() + " (Rank " + (perks.get(Perk.CUCKOO_FOR_CUCKOO.toString(), 0) + 1) + ")!"));
			perks.put(Perk.CUCKOO_FOR_CUCKOO.toString(), ((int)perks.get(Perk.CUCKOO_FOR_CUCKOO.toString(), 0)) + 1);
		}
		
		if (sex.isKnot() && perks.get(Perk.BITCH.toString(), 0) != 3) {
			result.add(new MutationResult("You gained " + Perk.BITCH.getLabel() + " (Rank " + (perks.get(Perk.BITCH.toString(), 0) + 1) + ")!"));
			perks.put(Perk.BITCH.toString(), ((int)perks.get(Perk.BITCH.toString(), 0)) + 1);
		}
		// should be moved into its own method
		this.buttful += sex.getBellyful();
				
		return result;
	}
	
	private void cumFromAnal() {
		cameFromAnal++;
		justCame = true;
		arousal.climax(ClimaxType.ANAL_RECEPTIVE);
	}
	
	private void cumFromOral() {
		cameFromOral++;
		justCame = true;
		arousal.climax(ClimaxType.ORAL_RECEPTIVE);
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
		ORC, CRIER, QUETZAL, INNKEEP, TRUDY, GOBLIN, OGRE, SPIDER, BROTHEL, ELF, DEBT, GADGETEER, MADAME, WITCH, MOUTH_FIEND;
		
		public String getQuestDescription(int currentValue) {
			switch (this) {
				case BROTHEL:
					return currentValue > 0 ? "You've agreed to be a prostitute in the brothel." : "";
				case CRIER:
					return currentValue > 0 ? ("You've heard about a quest to slay the lord of Xiuh mountain." + (currentValue == 2 ? " You've met a strange man who claims to have useful information." : currentValue == 3 ? " A strange man told you about a generic brothel patron." : currentValue == 4 ? " A strange man told you about the witch that lives in the forest." : "")) : "";
				case DEBT:
					return currentValue == 2 ? "You've encountered the debt collectors." : currentValue == 1 ? "You've been warned by the debt collectors." : "";
				case ELF:
					switch (currentValue) {
						case 2: return "You've encountered Kylira, the elf.";
						case 3: return "You've shared a meal with Kylira, the elf.";
						case 4: return "You've advised Kylira the elf to become a prostitute.";
						case 5: return "You've made Kylira the elf your travelling companion.";
						case 6: return "You've advised Kylira the elf to be a travelling healer.";
						case 7: return "You've advised Kylira the elf to depart for the Elven lands.";
						case 8: return "You've seen Kylira the elf working as a prostitute.";
						case 9: return "You've gotten close to Kylira the elf as a companion.";
						case 10: return "You've gotten very close to Kylira the elf.";
						case 11: return "You've gotten very close to Kylira the elf, and he's taught you healing magick.";
					}
				case GADGETEER:
					return currentValue == 2 ? "You've been teased by the eccentric merchant." : currentValue == 1 ? "You've encountered the eccentric merchant." : "";
				case GOBLIN:
					return currentValue == 1 ? "You've met Selkie the fem goblin." : "";
				case INNKEEP:
					return currentValue == 4 ? "You've married the innkeep." : currentValue == 3 ? "You've been innkeep's bitch for a day's lodging." : currentValue == 2 ? "You've caught the innkeep's fuck for a day's lodging." : currentValue == 1 ? "You've sucked the innkeep off for a day's lodging." : "";
				case MADAME:
					return currentValue == 1 ? "You've introduced yourself to the Brothel madame." : "";	
				case MOUTH_FIEND:
					return currentValue == 1 ? "The Brothel Madame has warned you not to be rough with the girls." : currentValue == 2 ? "The Brothel Madame has banned and threatened you." : "";
				case OGRE:
					return currentValue == 1 ? "You've seen an ogre." : "";		
				case ORC:
					return currentValue == 2 ? "You've encountered an orc and behaved cowardly." : currentValue == 1 ? "You've encountered Urka the orc and behaved honorably." : "";	
				case QUETZAL:
					return currentValue == 4 ? "You've been rewarded for defeating the Quetzal Goddess, the great lord of Mount Xiuh." : currentValue == 3 ? "You've defeated the Quetzal Goddess, the great lord of Mount Xiuh." : currentValue == 2 ? "You've seen the great lord of Mount Xiuh - she's a giant naga!" : currentValue == 1 ? "You've heard of the great lord of Mount Xiuh." : "";
				case SPIDER:
					return currentValue == 1 ? "You've survived the spider-infested ruins." : "";	
				case TRUDY:
					switch (currentValue) {
						case 1: return "You've met Trudy, another adventurer.";
						case 2: return "You foiled Trudy's betrayal, and he was buggered by a beast in your place.";
						case 3: return "You were tricked by Trudy and buggered by a beast.";
						case 4: return "You've battled with Trudy.";
						case 5: return "You've made Trudy your travelling companion.";
						case 6: return "You've gotten close to Trudy as a travelling companion."; 
						case 7: return "You've gotten very close to Trudy your travelling companion, and he's taught you some of his skills.";
					}	
				case WITCH:
					return currentValue == 2 ? "You've received the goddess' blessing from the witch of the forest." : currentValue == 1 ? "You've met the witch of the forest." : "";
			}
			return ""; 			
		}
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

	public Array<MutationResult> modMoney(Integer gold) {
		int goldChange = money;
		money += gold;
		if (money < 0) {
			money = 0;
		}
		
		goldChange = money - goldChange;
		return goldChange == 0 ? new Array<MutationResult>() : getResult(goldChange > 0 ? "Gained " + gold + " gold!" : goldChange + " gold!", goldChange, MutationType.GOLD);
	}
	
	public Array<MutationResult> modDebt(Integer gold) {
		int loss = debt > gold ? -gold : debt; 
		debt += gold;
		if (debt < 0) {
			debt = 0;
		}
		
		return gold == 0 ? new Array<MutationResult>() : getResult(gold > 0 ? "You have incurred " + gold + " gold worth of debt." : "You've been relieved of " + loss + " gold worth of debt!");
	}
	
	public int getBattlePerception() {
		return getPerception();
	}

	public int getCurrentDebt() {
		return debt;
	}

	public Array<MutationResult> debtTick(int ticks) {
		if (debt > 0) {
			return modDebt(10 * ticks);
		}
		return new Array<MutationResult>();
	}

	public Array<MutationResult> timePass(Integer timePassed) {
		int currentDay = time / 6;
		time += timePassed;
		Array<MutationResult> result = getResult(timePassed >= 12 ? timePassed / 6 + " days pass." : timePassed >= 6 ? "A day passes." : timePassed >= 3 ? "Much time passes." : timePassed == 2 ? "Some time passes." : "A short time passes.", timePassed, MutationType.TIME);
		result.addAll(modFood(-getMetabolicRate() * timePassed));
		result.addAll(debtTick((time / 6) - currentDay));
		return result;
	}
	
	public int getMetabolicRate() { return hasHungerCharm() ? 1 : 2; }

	private boolean hasHungerCharm() {
		return firstAccessory != null && firstAccessory.equals(new Accessory(AccessoryType.HUNGER_CHARM));
	}
	
	public boolean hasGem() {
		return inventory.contains(new Potion(1, EffectType.GEM), false);
	}
	
	public Array<MutationResult> cureBleed(Integer bleedCure) {
		int currentBleed = statuses.get(StatusType.BLEEDING.toString(), 0);
		int temp = currentBleed;
		currentBleed -= bleedCure;
		if (currentBleed < 0) currentBleed = 0;
		temp = temp - currentBleed;
		statuses.put(StatusType.BLEEDING.toString(), currentBleed);
		return temp > 0 ? getResult("Cured " + temp + " bleed point" + (temp > 1 ? "s." : ".")) : new Array<MutationResult>();
	}

	public Integer getTime() { return time; }

	public Array<MutationResult> increaseScout(int increase) {
		scout += increase;
		return getResult("You scouted the surrounding areas.  Scout level now " + (getScoutingScore() > 4 ? "maximum!" : getScoutingScore() + "."));
	}
	
	public String resetScout() {
		scout = 0;
		return null;
	}

	public boolean isLucky() {
		boolean gotLucky = luckStreak.random();
		if (luckStreak.size > 100) {
			int lucky = 0;
			for (boolean val : luckStreak.items) {
				if (val) lucky++;
			}
			int unlucky = luckStreak.size - lucky;
			luckStreak.clear();
			for (int ii = 0; ii < lucky; ii+= 10) {
				luckStreak.add(true);
			}
			for (int ii = 0; ii < unlucky; ii+= 10) {
				luckStreak.add(false);
			}			
		}
		luckStreak.add(!gotLucky);
		return gotLucky;		
	}

	public boolean isDayTime() {
		return TimeOfDay.getTime(time).isDay();
	}

	public int needShopRestock(ShopCode shopCode) {
		int needsRestock = 0;
		for (; lastShopRestock + 60 <= time; lastShopRestock += 60) {
			needsRestock++;
		}
		return needsRestock;
	}

	public String equipItem(Item item) {
		Armor armor = item instanceof Armor ? (Armor) item : null;
		return 
			item instanceof Weapon ? setWeapon(item) : 
			armor != null ? (armor.isArmwear() ? setArmwear(item, false) : armor.isHeadgear() ? setHeadgear(item, false) : armor.isShield() ? setShield(item, false) : armor.coversTop() ? setArmor(item, false) : armor.coversBottom() ? setLegwear(item, false) : setUnderwear(item, false)) : 
			item instanceof Accessory ? setAccessory(item, false) :
			item instanceof ChastityCage ? setCage(item, false) :
			setPlug(item, false);
	}

	public Plug getPlug() {
		return plug;
	}
	
	@Override
	public String setPlug(Item plug, boolean newItem) {
		if (receivedAnal == 0) receivedAnal++;
		return super.setPlug(plug, newItem);
	}
	
	public ChastityCage getCage() {
		return cage;
	}

	public boolean isEquipped(Item item) {
		return item.equals(weapon) || item.equals(armor) || item.equals(shield) || item.equals(legwear) || item.equals(underwear) || item.equals(plug) || item.equals(cage) || item.equals(headgear) || item.equals(armwear) || item.equals(firstAccessory);
	}

	public int getAnalReceptionCount() {
		return receivedAnal;
	}
	public int getOralReceptionCount() {
		return receivedOral;
	}

	public void setGameOver(GameOver gameOver) { this.gameOver = gameOver; }

	public GameOver getGameOver() { return gameOver; }
}

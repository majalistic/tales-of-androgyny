package com.majalis.character;

import static com.majalis.character.Techniques.*;

import com.badlogic.gdx.Gdx;
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
import com.majalis.character.Item.Misc;
import com.majalis.character.Item.MiscType;
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
	protected String name;
	
	protected ObjectMap<String, Integer> skills;
	protected int skillPoints;
	protected int magicPoints;
	protected int perkPoints;
	
	protected int bonusPoints;
	
	// advantage, range, and combat-lock(boolean) are shared properties between two creatures
	
	private BooleanArray luckStreak;
	
	/* out of battle only statistics */
	private int time;
	private int money;
	private int debt;
	private int debtCooldown;
	private int lastShopRestock;
	private Dignity dignity;
	private int willpower;
	
	private Femininity femininity;
	private LipFullness lipFullness;

	private int receivedAnal;
	private int receivedOral;	
	private int analCreampie;
	private int oralCreampie;
	private int cameFromAnal;
	private int cameFromOral;
	private boolean justCame;
	private int eggtick;
	
	private String currentPortrait;
	
	private boolean goblinVirgin;
	private boolean a2m;
	private boolean a2mcheevo;
	
	private boolean loaded;
	private ObjectMap<String, Integer> questFlags;
	private Array<String> eventLog;
	
	private int scout;
	
	private GameOver gameOver;
	
	private boolean degradationTutorial;
	private boolean grappleTutorial;
	private boolean knockdownTutorial;
	private boolean stanceTutorial;
	private boolean kyliraHeal;
	
	private ObjectMap<String, Boolean> bonuses;
	
	@SuppressWarnings("unused")
	private PlayerCharacter() { if(arousal == null) arousal = new Arousal(ArousalType.PLAYER); eventLog = new Array<String>(); dignity = new Dignity(); femininity = Femininity.MALE; kyliraHeal = true; bonuses = new ObjectMap<String, Boolean>(); }
	
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
			eventLog = new Array<String>();
			time = 0;
			dignity = new Dignity();
			willpower = 5;
			femininity = Femininity.MALE;
			kyliraHeal = true;
			bonuses = new ObjectMap<String, Boolean>();
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
			DO_NOTHING, DRAW_ARROW, FIRE, CANCEL, POWER_ATTACK, TEMPO_ATTACK, RESERVED_ATTACK, DUCK, SPRING_ATTACK, NEUTRAL_ATTACK, REVERSAL_ATTACK, CAREFUL_ATTACK, BLOCK, GUARD, KICK_OVER_FACE_UP, KICK_OVER_FACE_DOWN, ABSOLUTE_GUARD, LOWER_GUARD, RAGE, HAYMAKER, ASHI,
			PUSH_UP, KNEE_UP_HANDS, STAY, STAND_UP_HANDS, STAND_UP_KNEELING, KIP_UP, STAND_UP, STAY_KNELT, KNEE_UP, REST_FACE_DOWN, REST, JUMP_ATTACK, VAULT_OVER,
			RECEIVE_ANAL, RECEIVE_DOGGY, RECEIVE_STANDING, STRUGGLE_ORAL, STRUGGLE_DOGGY, STRUGGLE_ANAL, STRUGGLE_STANDING, RECEIVE_KNOT, SUCK_KNOT, SUCK_IT, SUCK_AND_STROKE, SUCK_AND_BEAT, BLOW, BREAK_FREE_ANAL, BREAK_FREE_ORAL,
			SUBMIT, STRUGGLE_FULL_NELSON, BREAK_FREE_FULL_NELSON, STRUGGLE_PRONE_BONE, STRUGGLE_DOGGY,
			OPEN_WIDE, GRAB_IT, STROKE_IT, TANDEM_STROKE, FONDLE_BALLS, KISS_IT, KISS_BALLS, SPIT_ON_IT, OPEN_UP, LET_GO, USE_ITEM, ITEM_OR_CANCEL, 
			RECIPROCATE_FORCED, GET_FACE_RIDDEN, STRUGGLE_FACE_SIT, STRUGGLE_SIXTY_NINE, BREAK_FREE_FACE_SIT, ROLL_OVER_UP, ROLL_OVER_DOWN, RIPOSTE, EN_GARDE, POUNCE_DOGGY, POUND_DOGGY, POUNCE_ANAL, POUND_ANAL, POUNCE_PRONE_BONE, POUND_PRONE_BONE, ERUPT_ANAL, PULL_OUT, PULL_OUT_ORAL, PULL_OUT_ANAL, PULL_OUT_STANDING, RECEIVE_COCK, HURK, UH_OH,
			FORCE_DEEPTHROAT, CRUSH_ASS, ASS_BLAST, BOUNCE_ON_IT, SQUEEZE_IT, BE_RIDDEN, PUSH_OFF, SELF_SPANK, POUT, DEEPTHROAT, LICK_BALLS, WRAP_LEGS, PUSH_OFF_ATTEMPT, SIT_ON_IT, TURN_AND_SIT, RIDE_ON_IT, RIDE_ON_IT_REVERSE, BOUNCE_ON_IT_REVERSE, SQUEEZE_IT_REVERSE, STAND_OFF_IT, RECEIVE_PRONE_BONE, BE_RIDDEN_REVERSE, PUSH_OFF_REVERSE, PUSH_OFF_ATTEMPT_REVERSE,
			OUROBOROS, ROUND_AND_ROUND, RECEIVE_OUROBOROS, STRUGGLE_OUROBOROS, MOUNT_FACE, FACEFUCK, GET_FACEFUCKED, STRUGGLE_FACEFUCK, RECEIVE_EGGS, ERUPT_ORAL,
			WRESTLE_TO_GROUND, WRESTLE_TO_GROUND_UP, PENETRATE_PRONE, PENETRATE_MISSIONARY, PIN, GRAPPLE, HOLD_WRESTLE, CHOKE, REST_WRESTLE, FLIP_PRONE, FLIP_SUPINE, RELEASE_PRONE, RELEASE_SUPINE, STRUGGLE_GROUND, BREAK_FREE_GROUND, GRIND, REST_GROUND_DOWN, STRUGGLE_GROUND_UP, BREAK_FREE_GROUND_UP, REVERSAL,
			FULL_REVERSAL, REST_GROUND_UP, SQUEEZE_STRUGGLE, BREAK_FREE_SQUEEZE, SQUEEZE_REST,
			SLAP_ASS, GESTURE, RUB, PRESENT, SLAP_ASS_KNEES, STROKE, STROKE_DOGGY, STROKE_STANDING, SAY_AHH, IRRUMATIO, FULL_NELSON, HOLD, TAKEDOWN, GRIP, PENETRATE_STANDING, POUND_STANDING, PULL_OUT_STANDING
		);
		return baseTechniques;
	}
	
	private AssetEnum portraitFeminization(AssetEnum portrait) { return femininity.isFeminine() && Gdx.app.getPreferences("tales-of-androgyny-preferences").getBoolean("makeup", true) ? AssetEnum.valueOf(portrait.toString().replace("_FEMME", "") + "_FEMME") : portrait; }
	public void setCurrentPortrait(AssetEnum portrait) { currentPortrait = portraitFeminization(portrait).getTexture().fileName; }
	public void addToInventory(Item item) { inventory.add(item); }
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
		if (bonuses.get("Bonus Food", false)) food += 20;
		if (bonuses.get("Bonus Skill Points", false)) skillPoints += 2;
		if (bonuses.get("Bonus Perk Points", false)) perkPoints += 2;
		if (bonuses.get("Bonus Soul Crystals", false)) magicPoints += 2;
		if (bonuses.get("Bonus Gold", false)) money += 20;
	}

	// this needs to consolidate logic with the getTechniques method
	public Array<Technique> getPossibleTechniques(AbstractCharacter target) {
		Techniques[] temp = getPossibleKnownTechniques(target).toArray(Techniques.class);
		Array<Technique> possibles = getLoadedTechniques(target, temp);
		if (possibles.size == 0) possibles = getLoadedTechniques(target, DO_NOTHING);
		return possibles;
	}
	
	
	private Array<Techniques> getPossibleKnownTechniques(AbstractCharacter target) {	
		return getDefaultTechniqueOptions(target);
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
	
	private Array<Technique> getLoadedTechniques(AbstractCharacter target, Techniques... possibilities) {
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
		if (resolvedAttack.getSex().isTeasing()) { currentPortrait = portraitFeminization(AssetEnum.PORTRAIT_GRIN).getTexture().fileName; }
		if (wrapLegs) {	resolvedAttack.addMessage("Your legs are wrapped around them!"); }
		if (resolvedAttack.isSuccessful() && resolvedAttack.getName().equals("Wrap Legs")) { wrapLegs = true; }		
		return super.doAttack(resolvedAttack);
	}
	
	@Override
	public AttackResult receiveAttack(Attack resolvedAttack) {
		super.receiveAttack(resolvedAttack);
		
		String result;
		if (resolvedAttack.isClimax()) {
			if (oldStance.isAnalReceptive()) { setCurrentPortrait(perks.get(Perk.ANAL_ADDICT.toString(), 0) > 1 ? AssetEnum.PORTRAIT_AHEGAO : AssetEnum.PORTRAIT_POUT); }
			else if (oldStance.isOralReceptive()) { setCurrentPortrait(AssetEnum.PORTRAIT_MOUTHBOMB); }				
		}
		if (stance.isAnalReceptive()) { setCurrentPortrait(perks.get(Perk.ANAL_ADDICT.toString(), 0) > 1 ? AssetEnum.PORTRAIT_LUST : AssetEnum.PORTRAIT_HIT); }
		else if (stance.isOralReceptive()) { setCurrentPortrait(resolvedAttack.isClimax() ? AssetEnum.PORTRAIT_MOUTHBOMB : AssetEnum.PORTRAIT_FELLATIO); }
		
		if (stance == Stance.HELD) { setCurrentPortrait(perks.get(Perk.SIZE_QUEEN.toString(), 0) > 1 ? AssetEnum.PORTRAIT_LOVE : AssetEnum.PORTRAIT_SURPRISE); }
		if (oldStance.isAnalReceptive() && !stance.isAnalReceptive()) { wrapLegs = false; }
		if (stance == Stance.OVIPOSITION_BOTTOM) { receiveEggs(); }
		if (!oldStance.isAnalReceptive() && stance.isAnalReceptive()) {
			Array<MutationResult> temp = receiveAnal(getPhallusType(resolvedAttack.getSex())); 
			
			if (temp.size > 0) result = temp.first().getText();
			else result = "";
			
			if (stance != Stance.PENETRATED) { setCurrentPortrait(perks.get(Perk.ANAL_ADDICT.toString(), 0) > 1 ? AssetEnum.PORTRAIT_LOVE : AssetEnum.PORTRAIT_SURPRISE); }
			else { setCurrentPortrait(perks.get(Perk.ANAL_ADDICT.toString(), 0) > 1 ? AssetEnum.PORTRAIT_LOVE : AssetEnum.PORTRAIT_AHEGAO); }
			
			if (result != null) { resolvedAttack.addMessage(result); } 
			if (resolvedAttack.getUser().equals("Goblin")) { setGoblinVirginity(false); }
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
		else if (stance == Stance.SIXTY_NINE) { resolvedAttack.addMessage("She shoves her cock down your throat while swallowing yours!"); }
		return new AttackResult(resolvedAttack.getMessages(), resolvedAttack.getDialog(), resolvedAttack.getAttackerResults(), resolvedAttack.getDefenderResults());	
	}
	
	@Override
	protected String getLeakMessage() {
		String message = "";
		
		if (ass.getFullnessAmount() >= 20) { message = "Your belly looks pregnant, full of baby batter! It drools out of your well-used hole! Your movements are sluggish! -2 Agility."; }
		else if (ass.getFullnessAmount() >= 10) { message = "Your gut is stuffed with semen!  It drools out!  You're too queasy to move quickly! -1 Agility."; }
		else if (ass.getFullnessAmount() >= 5) { message = "Cum runs out of your full ass!"; }
		else if (ass.getFullnessAmount() > 1) { message = "You drool cum from your hole!"; }
		else if (ass.getFullnessAmount() == 1) { message = " The last of the cum runs out of your hole!"; }
		drainButt();
		return message;
	}
	
	@Override
	protected String getDroolMessage() {
		String message = "";
		if (mouthful > 10) { message = "You vomit their tremendous load onto the ground!"; }
		else if (mouthful > 5) { message = "You spew their massive load onto the ground!"; }
		else { message = "You spit all of their cum out onto the ground!"; }
		drainMouth();
		return message;
	}
	
	public void refresh() {
		setStaminaToMax();
		setStabilityToMax();
		setManaToMax();
		setStance(Stance.BALANCED);
		grappleStatus = GrappleStatus.NULL;
		a2m = false;
		ass.fillButtWithCum(-10);
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
			statuses.put(StatusType.BLEEDING.toString(), Math.max(currentBleed - getEndurance(), 0));
		}
		wrapLegs = false;
		scout = 0;
		heartbeat = 0;
	}

	public enum Femininity {
		MALE,
		UNMASCULINE,
		EFFEMINATE,
		FEMALE,
		BITCH;

		public boolean isFeminine() { return this == FEMALE || this == BITCH; }
		public String getLabel() {
			switch(this) {
				case BITCH: return "Bitch";
				case EFFEMINATE: return "Effeminate";
				case FEMALE: return "Female";
				case MALE: return "Male";
				case UNMASCULINE: return "Unmasculine";
				default: return "";
			}
		}
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
 
	// need to add the list of default skills, the actual variable, and some way to access it for skill selection purposes (filter)
	public void addSkill(Techniques newTech, int rank) {
		// if it's a spell, add incantation
		if (newTech.getTrait().isSpell()) {
			skills.put(Techniques.INCANTATION.toString(), 1);
			if (!hasMagic()) { manaTiers = new IntArray(new int[]{baseMagic > 1 ? baseMagic * 3 + 4 : 0}); }
		}
		skills.put(newTech.toString(), rank);	
	}
	
	public void addPerk(Perk newPerk, int rank) { perks.put(newPerk.toString(), rank); }

	public ObjectMap<Techniques, Integer> getSkills() {
		ObjectMap<Techniques, Integer> tempSkills = new ObjectMap<Techniques, Integer>();
		for (String key : skills.keys()) {
			tempSkills.put(Techniques.valueOf(key), skills.get(key));
		}
		return tempSkills;
	}
	
	public void setSkills(ObjectMap<Techniques, Integer> skills) {
		this.skills.clear();
		for (Techniques key : skills.keys()) {
			addSkill(key, skills.get(key));
		}
	}

	private void increaseHighestStat() {
		int max = getBaseStat(Stat.STRENGTH);
		Stat maxStat = Stat.STRENGTH;
		for (Stat stat : Stat.values()) {
			if (getBaseStat(stat) > max) {
				max = getBaseStat(stat);
				maxStat = stat;
			}
		}
		setStat(maxStat, getBaseStat(maxStat) + 1);
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
		if (perks.get(Perk.WELLROUNDED, 0) > 0 && !(this.perks.get(Perk.WELLROUNDED.toString(), 0) > 0) ) { increaseLowestStat(); }
		if (perks.get(Perk.SPECIALIST, 0) > 0 && !(this.perks.get(Perk.SPECIALIST.toString(), 0) > 0) ) { increaseHighestStat(); }
		
		this.perks.clear();
		for (Perk key : perks.keys()) { addPerk(key, perks.get(key)); }
	}
	
	public int getScoutingScore() { return getTrueScoutingScore(scout * 3 + getPerception() + (perks.get(Perk.SURVEYOR.toString(), 0) > 0 ? perks.get(Perk.SURVEYOR.toString()) * 2 : 0)); }
	
	private int getTrueScoutingScore(int rawScoutingScore) {
		int level = 5;
		for (int ii : new int[]{14, 11, 8, 5, 2}) {
			if (rawScoutingScore < ii) level--;
		}
		return level;
	}
	
	@Override
	protected String climax() {
		String spurt = "";
		String result = null;
		boolean weakToAnal = perks.get(Perk.WEAK_TO_ANAL.toString(), 0) > 0;
		currentStamina -= 10;
		
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
				arousal.climax(ClimaxType.BACKWASH, perks);
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
				setStance(Stance.ERUPT);
				arousal.climax(ClimaxType.ANAL, perks);
				break;
			case FELLATIO:
				setStance(Stance.ERUPT);
				arousal.climax(ClimaxType.ORAL, perks);
				break;
			default: 
				spurt = "You spew your semen onto the ground!\n"; 
				arousal.climax(ClimaxType.BACKWASH, perks);
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
	public int getLevel() { return level; }
	public Integer getFood() { return food; }
	public void setBootyliciousness(Bootyliciousness buttSize) { bootyliciousness = buttSize; }
	public void setLipFullness(LipFullness lipFullness) { this.lipFullness = lipFullness;	}
	public int getSkillPoints() { return skillPoints; }
	public int getMagicPoints() { return magicPoints; }
	public int getPerkPoints() { return perkPoints; }
	public void setSkillPoints(int skillPoints) { this.skillPoints = skillPoints; }
	public void setMagicPoints(int magicPoints) { this.magicPoints = magicPoints; }
	public void setPerkPoints(int perkPoints) { this.perkPoints = perkPoints; }
	public Array<MutationResult> modSkillPoints(int skillPoints) { this.skillPoints += skillPoints; return getResult("You gain " + skillPoints + " skill!", skillPoints, MutationType.SKILL_POINTS); }
	public Array<MutationResult> modMagicPoints(int magicPoints) { this.magicPoints += magicPoints; return getResult("You gain " + magicPoints + " soul crystal" + (magicPoints > 1 ? "s" : "") + "!", magicPoints, MutationType.CRYSTAL); }
	public Array<MutationResult> modPerkPoints(int perkPoints) { this.perkPoints += perkPoints; return getResult("You gain " + perkPoints + " perk point" + (perkPoints > 1 ? "s" : "") + "!", perkPoints, MutationType.PERK_POINTS); }

	public float getPercentToLevel() {
		int currentExperience = experience;
		int storedLevels = 0;
		int nextLevel = 0;
		while (currentExperience >= 0) {
			int currentLevel = level + storedLevels;
			int experienceForLevel = (1 + (currentLevel / 10)) * 5;
			nextLevel = experienceForLevel;
			if (currentExperience > experienceForLevel) storedLevels++;
			currentExperience -= experienceForLevel;
		}
		return (nextLevel + currentExperience) * 1.0f / nextLevel;
	}
	
	public int getStoredLevels() {
		int currentExperience = experience;
		int storedLevels = 0;
		while (currentExperience > 0) {
			int currentLevel = level + storedLevels;
			int experienceForLevel = (1 + (currentLevel / 10)) * 5;
			if (currentExperience > experienceForLevel) storedLevels++;
			currentExperience -= experienceForLevel;
		}
		return storedLevels;
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

	public boolean hasMagic() { return getMaxMana() > 0; }

	public boolean needsLevelUp() { return skillPoints > 0 || magicPoints > 0 || perkPoints > 0 || getStoredLevels() > 0; }
	
	public boolean isVirgin() { return receivedAnal == 0; }
	
	public boolean isOralVirgin() { return receivedOral == 0; }
	
	public boolean tastedCumAnally() { return analCreampie != 0; }
	
	public boolean tastedCumOrally() { return oralCreampie != 0; }
	
	private int getMasculinityLevel() { return (int) Math.round(receivedOral / 5. + receivedAnal / 3. + oralCreampie / 3. + analCreampie + cameFromOral + cameFromAnal); }
	
	public AssetDescriptor<Texture> getMasculinityPath() {
		int masculinityLevel = getMasculinityLevel();
		return masculinityLevel < 1 ? AssetEnum.MARS_ICON_0.getTexture() : masculinityLevel < 6 ? AssetEnum.MARS_ICON_1.getTexture() : masculinityLevel < 32 ? AssetEnum.MARS_ICON_2.getTexture() : masculinityLevel < 60 ? AssetEnum.MARS_ICON_3.getTexture() : AssetEnum.MARS_ICON_4.getTexture();
	}
	
	public boolean isVirgin(EnemyEnum enemyEnum) {
		switch (enemyEnum) {
			case GOBLIN: return goblinVirgin;
			default:
		}
		return false;
	}
	
	private String getPioneer(PhallusType phallusType) {
		switch (phallusType) {
			case BIRD: return "Harpy";
			case DOG: return "Werewolf";
			case GIANT: return "Ogre";
			case HORSE: return "Centaur";
			default: return "";
		}
	}
	
	private String getTimeDescription() { return TimeOfDay.getTime(time).getDisplay() + " of day " + (time / 6 + 1); }
	private Array<MutationResult> receiveAnal(PhallusType phallusType) {
		boolean virgin = receivedAnal == 0;
		String result = virgin ? "You are no longer an anal virgin!\n" : "";
		if (virgin) {
			String pioneer = getPioneer(phallusType);
			eventLog.add("You lost your anal virginity" + (pioneer.equals("") ? "" : " to a " + pioneer) + " on the " + getTimeDescription() + "!");
		}
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
	public boolean fullOfEggs() { 
		return 
			(questFlags.get(QuestType.SPIDER.toString(), 0) >= 2 && questFlags.get(QuestType.SPIDER.toString(), 0) < 6) ||	
			(questFlags.get(QuestType.MERMAID.toString(), 0) >= 3 && questFlags.get(QuestType.MERMAID.toString(), 0) < 7) ||
			(questFlags.get(QuestType.GOBLIN.toString(), 0) >= 3 && questFlags.get(QuestType.GOBLIN.toString(), 0) < 9)
			; 
	}
	
	@Override
	protected Array<MutationResult> fillButt(int buttful) {
		super.fillButt(buttful);
		analCreampie++;
		modDignity(-5);
		String result = incrementPerk(analCreampie, Perk.CREAMPIE_COLLECTOR, 10, 6, 3);
		return result.equals("") ? new Array<MutationResult>() : getResult(result);
	}
	
	protected String receiveEggs() {
		ass.fillButtWithEggs(5);
		modDignity(-10);
		return null;
	}
	
	protected void flushEggs() { ass.emptyEggs(); }
	
	@Override
	protected Array<MutationResult> fillMouth(int mouthful) {
		super.fillMouth(mouthful);
		Array<MutationResult> results = new Array<MutationResult>();
		oralCreampie++;
		modDignity(-3);
		String result = incrementPerk(oralCreampie, Perk.CUM_CONNOISSEUR, 10, 6, 3);
		if (mouthful >= 5) {
			results.add(new MutationResult("You swallow enough to sate your hunger!"));
			results.addAll(modFood(mouthful / 5));
		}
		if (perks.get(Perk.CUM_DRINKER.toString(), 0) > 0) results.addAll(modHealth(perks.get(Perk.CUM_DRINKER.toString(), 0) * 5));
		
		if (!result.equals("")) results.addAll(getResult(result));

		return results;
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
			String cockLoverGain = incrementPerk(getMasculinityLevel(), Perk.COCK_LOVER, 60, 54, 48, 42, 36, 30, 24, 18, 12, 6);
			result += (!result.equals("") && !cockLoverGain.equals("") ? "\n" : "") + cockLoverGain;
		}
		else {
			int cockLoverRank = perks.get(Perk.COCK_LOVER.toString(), 0);
			femininity = cockLoverRank >= 10 ? Femininity.BITCH : cockLoverRank >= 8 ? Femininity.FEMALE : cockLoverRank >= 6 ? Femininity.EFFEMINATE : cockLoverRank >= 3 ? Femininity.UNMASCULINE : Femininity.MALE;
		}
		return result;
	}
	
	public void setGoblinVirginity(boolean virginity) {
		goblinVirgin = virginity;
		if (!goblinVirgin) {
			modDignity(-50);
			receiveSex(new SexualExperience.SexualExperienceBuilder().setAnalSex(1).build());
		}
	}
	
	public Array<MutationResult> receiveItem(Item item) {
		if (item.instantUse()) { consumeItem(item); }
		else if (item instanceof ChastityCage) { setCage(item, true); }
		else { inventory.add(item); }
		return getResult("You have received a(n) " + item.getName() + "!");
	}
	
	public boolean buyItem(Item item, int cost) {
		if (cost > money) { return false; }
		money -= cost;
		receiveItem(item);
		if (item instanceof Weapon) { weapon = (Weapon) item; }
		return true;
	}
	

	public boolean sellItem(Item item) {
		if (item.isConsumable() || item.isEquippable() && !item.equals(new ChastityCage())) {
			money += item.getValue() / 4;
			return true;
		}
		return false;
	}

	public Integer getMoney() { return money; }

	// this should obviously only accept a Weapon parameter
	public String setWeapon(Item item) {	
		Weapon equipWeapon = (Weapon) item;
		boolean alreadyEquipped = equipWeapon.equals(this.weapon);
		this.weapon = alreadyEquipped ? null : equipWeapon;
		return "You " + (alreadyEquipped ? "unequipped" : "equipped") + " the " + equipWeapon.getName() + ".";
	}
	
	public String setRangedWeapon(Item item) {	
		Weapon equipWeapon = (Weapon) item;
		boolean alreadyEquipped = equipWeapon.equals(this.rangedWeapon);
		this.rangedWeapon = alreadyEquipped ? null : equipWeapon;
		return "You " + (alreadyEquipped ? "unequipped" : "equipped") + " the " + equipWeapon.getName() + ".";
	}
	
	public String unequipWeapon() {
		Weapon temp = this.weapon;
		this.weapon = null;
		return temp != null ? "You unequipped the " + temp.getName() + "." : "";
	}
	
	public String unequipRangedWeapon() {
		Weapon temp = this.rangedWeapon;
		this.rangedWeapon = null;
		return temp != null ? "You unequipped the " + temp.getName() + "." : "";
	}
	
	public String unequipShield() {
		Armor temp = this.shield;
		this.shield = null;
		return temp != null ? "You unequipped the " + temp.getName() + "." : "";
	}
	
	public String unequipArmor() {
		Armor temp = this.armor;
		this.armor = null;
		return temp != null ? "You unequipped the " + temp.getName() + "." : "";
	}
	
	public String unequipLegwear() {
		Armor temp = this.legwear;
		this.legwear = null;
		return temp != null ? "You unequipped the " + temp.getName() + "." : "";
	}
	
	public String unequipUnderwear() {
		Armor temp = this.underwear;
		this.underwear = null;
		return temp != null ? "You unequipped the " + temp.getName() + "." : "";
	}
	
	public String unequipHeadgear() {
		Armor temp = this.headgear;
		this.headgear = null;
		return temp != null ? "You unequipped the " + temp.getName() + "." : "";
	}
	
	public String unequipArmwear() {
		Armor temp = this.armwear;
		this.armwear = null;
		return temp != null ? "You unequipped the " + temp.getName() + "." : "";
	}
	
	public String unequipFootwear() {
		Armor temp = this.footwear;
		this.footwear = null;
		return temp != null ? "You unequipped the " + temp.getName() + "." : "";
	}
	
	public String unequipAccessory() {
		Accessory temp = this.firstAccessory;
		this.firstAccessory = null;
		return temp != null ? "You unequipped the " + temp.getName() + "." : "";
	}
	
	public String unequipPlug() {
		Plug temp = this.plug;
		this.plug = null;
		return temp != null ? "You unequipped the " + temp.getName() + "." : "";
	}
	
	public String unequipCage() {
		ChastityCage temp = this.cage;
		this.cage = null;
		return temp != null ? "You unequipped the " + temp.getName() + "." : "";
	}
	
	
	private PhallusType getPhallusType(SexualExperience sex) { return sex.isBird() ? PhallusType.BIRD : sex.isCentaurSex() ? PhallusType.HORSE : sex.isKnot() ? PhallusType.DOG : sex.isOgreSex() ? PhallusType.GIANT : PhallusType.MONSTER; }
	
	private void getPregnant(PhallusType phallusType) {
		switch(phallusType) {
			case BIRD:
				questFlags.put(QuestType.GOBLIN.toString(), 3);
				eggtick = 0;
				modDignity(-50);
				break;
			case DOG:
				questFlags.put(QuestType.GOBLIN.toString(), 4);
				eggtick = 0;
				modDignity(-50);
				break;
			case HORSE:
				questFlags.put(QuestType.GOBLIN.toString(), 5);
				eggtick = 0;
				modDignity(-50);
				break;
			default:
				break;	
		}
	}
	
	// this needs to properly increase arousal
	public Array<MutationResult> receiveSex(SexualExperience sex) {
		Array<MutationResult> result = new Array<MutationResult>();
		increaseLust(sex);
		
		ass.receiveSex(sex);
		
		for (int ii = 0; ii < sex.getAnalSex(); ii++) {
			result.addAll(receiveAnal(getPhallusType(sex)));
			setCurrentPortrait(perks.get(Perk.ANAL_ADDICT.toString(), 0) > 1 ? AssetEnum.PORTRAIT_LUST : AssetEnum.PORTRAIT_HIT);
		}
		for (int ii = 0; ii < sex.getCreampies(); ii++) { result.addAll(fillButt(5)); }
		
		if (sex.getCreampies() > 0 && questFlags.get(QuestType.GOBLIN.toString(), 0) == 2 && !fullOfEggs()) { getPregnant(getPhallusType(sex)); }
		
		for (int ii = 0; ii < sex.getAnalEjaculations(); ii++) {
			cumFromAnal();
			modDignity(-20);
			setCurrentPortrait(AssetEnum.PORTRAIT_AHEGAO);
		}
		
		for (int ii = 0; ii < sex.getAnalSexTop(); ii++) {
			if (perks.get(Perk.TOP.toString(), 0) != 10) {
				result.add(new MutationResult("You gained " + Perk.TOP.getLabel() + " (Rank " + (perks.get(Perk.TOP.toString(), 0) + 1) + ")!"));
				perks.put(Perk.TOP.toString(), ((int)perks.get(Perk.TOP.toString(), 0)) + 1);	
			}
		}
		
		for (int ii = 0; ii < sex.getOralSexTop(); ii++) {
			if (perks.get(Perk.TOP.toString(), 0) != 10) {
				result.add(new MutationResult("You gained " + Perk.TOP.getLabel() + " (Rank " + (perks.get(Perk.TOP.toString(), 0) + 1) + ")!"));
				perks.put(Perk.TOP.toString(), ((int)perks.get(Perk.TOP.toString(), 0)) + 1);
			}
		}
		
		for (int ii = 0; ii < sex.getEjaculationInMouth(); ii++) {
			cumInMouth();
			setCurrentPortrait(AssetEnum.PORTRAIT_AHEGAO);
		}
		
		for (int ii = 0; ii < sex.getEjaculationInButt(); ii++) {
			cumInButt();
			setCurrentPortrait(AssetEnum.PORTRAIT_AHEGAO);
		}
		
		for (int ii = 0; ii < sex.getOralSex(); ii++) {
			result.addAll(receiveOral());
			setCurrentPortrait(AssetEnum.PORTRAIT_FELLATIO);
		}

		if (sex.getOralCreampies() > 0) {
			setCurrentPortrait(AssetEnum.PORTRAIT_MOUTHBOMB);
			result.addAll(fillMouth(5 * sex.getOralCreampies()));
		}
		for (int ii = 0; ii < sex.getFellatioEjaculations(); ii++) {
			cumFromOral();
			modDignity(-10);
		} 
		
		if (sex.isCentaurSex() && sex.getAnalSex() > 0 && perks.get(Perk.EQUESTRIAN.toString(), 0) != 3) {
			result.add(new MutationResult("You gained " + Perk.EQUESTRIAN.getLabel() + " (Rank " + (perks.get(Perk.EQUESTRIAN.toString(), 0) + 1) + ")!"));
			perks.put(Perk.EQUESTRIAN.toString(), ((int)perks.get(Perk.EQUESTRIAN.toString(), 0)) + 1);
			modDignity(-10);
			questFlags.put(QuestType.CENTAUR_ANATOMY.toString(), 1);
		}
		if (sex.isOgreSex() && sex.getAnalSex() > 0 && perks.get(Perk.SIZE_QUEEN.toString(), 0) != 3) {
			result.add(new MutationResult("You gained " + Perk.SIZE_QUEEN.getLabel() + " (Rank " + (perks.get(Perk.SIZE_QUEEN.toString(), 0) + 1) + ")!"));
			perks.put(Perk.SIZE_QUEEN.toString(), ((int)perks.get(Perk.SIZE_QUEEN.toString(), 0)) + 1);
			modDignity(-10);
			questFlags.put(QuestType.GIANT_ANATOMY.toString(), 1);
		}
		
		if (sex.isBeast() && perks.get(Perk.BEASTMASTER.toString(), 0) != 3) {
			result.add(new MutationResult("You gained " + Perk.BEASTMASTER.getLabel() + " (Rank " + (perks.get(Perk.BEASTMASTER.toString(), 0) + 1) + ")!"));
			perks.put(Perk.BEASTMASTER.toString(), ((int)perks.get(Perk.BEASTMASTER.toString(), 0)) + 1);
			modDignity(-20);
			questFlags.put(QuestType.CAT_ANATOMY.toString(), 1);
		}
		
		if (sex.isProstitution() && perks.get(Perk.LADY_OF_THE_NIGHT.toString(), 0) != 20) {
			result.add(new MutationResult("You gained " + Perk.LADY_OF_THE_NIGHT.getLabel() + " (Rank " + (perks.get(Perk.LADY_OF_THE_NIGHT.toString(), 0) + 1) + ")!"));
			perks.put(Perk.LADY_OF_THE_NIGHT.toString(), ((int)perks.get(Perk.LADY_OF_THE_NIGHT.toString(), 0)) + 1);
			
			modDignity(-10);
			
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
		
		if (sex.isBird() && sex.getAnalSex() > 0 && perks.get(Perk.CUCKOO_FOR_CUCKOO.toString(), 0) != 3) {
			result.add(new MutationResult("You gained " + Perk.CUCKOO_FOR_CUCKOO.getLabel() + " (Rank " + (perks.get(Perk.CUCKOO_FOR_CUCKOO.toString(), 0) + 1) + ")!"));
			perks.put(Perk.CUCKOO_FOR_CUCKOO.toString(), ((int)perks.get(Perk.CUCKOO_FOR_CUCKOO.toString(), 0)) + 1);
			modDignity(-5);
			questFlags.put(QuestType.HARPY_ANATOMY.toString(), 1);
		}
		
		if (sex.isKnot() && sex.getAnalSex() > 0 && perks.get(Perk.BITCH.toString(), 0) != 3) {
			result.add(new MutationResult("You gained " + Perk.BITCH.getLabel() + " (Rank " + (perks.get(Perk.BITCH.toString(), 0) + 1) + ")!"));
			perks.put(Perk.BITCH.toString(), ((int)perks.get(Perk.BITCH.toString(), 0)) + 1);
			modDignity(-10);
			questFlags.put(QuestType.WEREWOLF_ANATOMY.toString(), 1);
		}
		// should be moved into its own method
		ass.fillButtWithCum(sex.getBellyful());
				
		return result;
	}
	
	private void cumFromAnal() {
		cameFromAnal++;
		justCame = true;
		arousal.climax(ClimaxType.ANAL_RECEPTIVE, perks);
	}
	
	private void cumFromOral() {
		cameFromOral++;
		justCame = true;
		arousal.climax(ClimaxType.ORAL_RECEPTIVE, perks);
	}
	
	private void cumInButt() {
		justCame = true;
		arousal.climax(ClimaxType.ANAL, perks);
	}
	
	private void cumInMouth() {
		justCame = true;
		arousal.climax(ClimaxType.ORAL, perks);
	}

	public String getBootyLiciousness() { return bootyliciousness != null ? bootyliciousness.toString() : Bootyliciousness.Bubble.toString(); }
	public String getLipFullness() { return lipFullness != null ? lipFullness.toString() : LipFullness.Thin.toString();  }

	public AssetDescriptor<Texture> popPortraitPath() {
		if (justCame) {
			justCame = false;
			return portraitFeminization(AssetEnum.PORTRAIT_AHEGAO).getTexture();
		}
		AssetDescriptor<Texture> currentDisplay = new AssetDescriptor<Texture>(currentPortrait, Texture.class);
		currentPortrait = getNeutralFace().getTexture().fileName;
		return currentDisplay;
	}
	
	public AssetDescriptor<Texture> getPortraitPath() {
		if (justCame) {
			justCame = false;
			return portraitFeminization(AssetEnum.PORTRAIT_AHEGAO).getTexture();
		}
		return new AssetDescriptor<Texture>(currentPortrait, Texture.class);
	}

	private AssetEnum getNeutralFace() {
		switch (getHealthDegradation()) {
			case 0: return portraitFeminization(AssetEnum.PORTRAIT_SMILE);
			case 1: return portraitFeminization(AssetEnum.PORTRAIT_HAPPY);
			default: return portraitFeminization(AssetEnum.PORTRAIT_NEUTRAL);		
		}
	}

	public boolean isLoaded() { return loaded; }

	public void load() { loaded = true; }

	public String getStatBonusDisplay() {
		String display = "";
		int strBuff = getStrengthBuff();
		if (strBuff > 0) { display += "Strength buff: +" + strBuff + "\n"; }
		int agiBuff = getAgilityBuff();
		if (agiBuff > 0) { display += "Agility buff: +" + agiBuff + "\n"; }
		int endBuff = getEnduranceBuff();
		if (endBuff > 0) { display += "Endurance buff: +" + endBuff + "\n"; }
		return display;
	}
		
	public String getStatPenaltyDisplay() {
		String display = "";
		int healthDegradation = getHealthDegradation();
		if (healthDegradation > 0) { display += "Low health: -" + (healthDegradation / 2 > 0 ? healthDegradation / 2 + " STR, -" : "") + healthDegradation + " END, AGI\n"; }
		
		int staminaDegradation = getStaminaDegradation();
		if (staminaDegradation > 0) { display += "Low stamina: -" + (staminaDegradation / 2 > 0 ? staminaDegradation / 2 + " STR, -" : "") + staminaDegradation + " AGI\n"; }
		
		int lustDegradation = getLustDegradation() / 2;
		if (lustDegradation > 0) { display += "High lust: -" + lustDegradation + " STR\n"; }
		
		int cumFilled = getCumInflation();
		if (cumFilled > 0) { display += "Too full of cum: -" + cumFilled + " AGI\n"; }
		
		return display;
	}

	public String getStatTextDisplay() {
		String display = "Current Stats:\n";
		for (ObjectMap.Entry<Stat, Integer> statEntry : getStats()) {
			display += statEntry.key + " - " + statEntry.value + "\n";
		}
		return display;		
	}

	public void setCharacterName(String s) { name = s; }
	
	public String getCharacterName() { return name; }
	
	public int getQuestStatus(QuestType type) { return questFlags.get(type.toString(), 0); }	
	
	public void setQuestStatus(QuestType type, int status) {
		if (type == QuestType.MERMAID && status == 3) { 
			eggtick = 0; 
			receiveEggs();
			if (questFlags.get(QuestType.GOBLIN.toString(), 0) == 2) questFlags.put(QuestType.GOBLIN.toString(), 1);
			eventLog.add("You were impregnated by a mermaid on the " + getTimeDescription() + "!");
		}
		if (type == QuestType.SPIDER && status == 2) { 
			eggtick = 0; 
			receiveEggs();
			if (questFlags.get(QuestType.GOBLIN.toString(), 0) == 2) questFlags.put(QuestType.GOBLIN.toString(), 1); 
			eventLog.add("You were impregnated by a spider on the " + getTimeDescription() + "!");
		}
		if (type == QuestType.GOBLIN && status == 2) {
			receiveEggs();
		}
		if (type == QuestType.MERMAID && status == 7) { 
			eggtick = 0; 
			flushEggs();
			eventLog.add("You gave birth to mermaid offspring on the " + getTimeDescription() + "!");
		}
		if (type == QuestType.SPIDER && status == 6) { 
			eggtick = 0; 
			flushEggs();
			eventLog.add("You gave birth to spider offspring on the " + getTimeDescription() + "!");
		}
		if (type == QuestType.GOBLIN && (status == 9 || status == 10 || status == 11)) { 
			eggtick = 0; 
			flushEggs();
			eventLog.add("You gave birth to goblin's offspring on the " + getTimeDescription() + "!");
		}
		questFlags.put(type.toString(), status);
	}

	public Array<MutationResult> modMoney(Integer gold) {
		int goldChange = money;
		money += gold;
		if (money < 0) { money = 0; }		
		goldChange = money - goldChange;
		return goldChange == 0 ? new Array<MutationResult>() : getResult(goldChange > 0 ? "Gained " + gold + " gold!" : goldChange + " gold!", goldChange, MutationType.GOLD);
	}
	
	public void modDebtCooldown(int cooldown) {
		debtCooldown += cooldown;
		if (debtCooldown < 0) { debtCooldown = 0; }
	}
	
	public Array<MutationResult> modDebt(Integer gold) {
		int loss = debt > gold ? -gold : debt; 
		debt += gold;
		if (debt < 0) { debt = 0; }
		
		if (gold < 0 && debtCooldown < 18) { modDebtCooldown(18 - debtCooldown); }
		
		return gold == 0 ? new Array<MutationResult>() : getResult(gold > 0 ? "You have incurred " + gold + " gold worth of debt." : "You've been relieved of " + loss + " gold worth of debt!");
	}
	
	public int getBattlePerception() { return getPerception(); }

	public int getCurrentDebt() { return debt; }

	public Array<MutationResult> debtTick(int ticks) {
		if (debt > 0) {
			float newDebt = debt;
			for (int ii = 0; ii < ticks; ii++) {
				newDebt *= 1.1f;
			}			
			return modDebt((int)Math.ceil(newDebt) - debt);
		}
		return new Array<MutationResult>();
	}

	public Array<MutationResult> eggTick(int timePassed) {
		int questLevel = questFlags.get(QuestType.MERMAID.toString(), 0);
		if (questLevel >= 3 && questLevel < 7) {
			eggtick += timePassed;
			if (questLevel == 3) {
				if (eggtick >= 12) {
					questFlags.put(QuestType.MERMAID.toString(), 4);
					return getResult("Your belly womb is beginning to swell! You are fully pregnant!");
				}
				return getResult("Your belly is swollen with eggs!");
			}
			if (questLevel == 4) {
				if (eggtick >= 24) {
					questFlags.put(QuestType.MERMAID.toString(), 5);
					return getResult("The fish eggs begin to hatch in your gut! You need to get to open water, momma!");
				}
				return getResult("Your belly is distended by a clutch of eggs!");
			}	
			if (questLevel == 5) {
				if (eggtick >= 48) {
					questFlags.put(QuestType.MERMAID.toString(), 6);
					return getResult("The fish are about to come out!");
				}
				return getResult("Your belly is sloshing with fish and fish eggs!");
			}	
		}	
		questLevel = questFlags.get(QuestType.SPIDER.toString(), 0);
		if (questLevel >= 2 && questLevel < 6) {
			eggtick += timePassed;
			if (questLevel == 2) {
				if (eggtick >= 12) {
					questFlags.put(QuestType.SPIDER.toString(), 3);
					return getResult("Your spider nursery belly is starting to stir!");
				}
				return getResult("Your belly is swollen with eggs!");
			}
			if (questLevel == 3) {
				if (eggtick >= 24) {
					questFlags.put(QuestType.SPIDER.toString(), 4);
					return getResult("The spider eggs are nearly ready to hatch!");
				}
				return getResult("Your belly is distended by a clutch of eggs!");
			}		
			if (eggtick >= 36) {
				questFlags.put(QuestType.SPIDER.toString(), 5);
				return getResult("The spider eggs are hatching!");
			}
		}	
		questLevel = questFlags.get(QuestType.GOBLIN.toString(), 0);
		if (questLevel >= 2 && questLevel < 6) {
			eggtick += timePassed;
			if (questLevel == 2) {
				if (eggtick >= 18) {
					questFlags.put(QuestType.GOBLIN.toString(), 1);
					return getResult("The goblin eggs have flushed out!");
				}
				return getResult("You've got goblin eggs stuck in your bowels!");
			}
			if (questLevel > 2 && questLevel < 6) {
				if (eggtick >= 18) {
					questFlags.put(QuestType.GOBLIN.toString(), questLevel + 3);
					return getResult("You go into labor with the goblin's baby!");
				}
				return getResult("You're pregnant with the goblin's baby!");
			}
		}	
		return new Array<MutationResult>();
	}
	
	public Array<MutationResult> timePass(Integer timePassed) {
		int currentDay = time / 6;
		time += timePassed;
		Array<MutationResult> result = getResult(timePassed >= 12 ? timePassed / 6 + " days pass." : timePassed >= 6 ? "A day passes." : timePassed >= 3 ? "Much time passes." : timePassed == 2 ? "Some time passes." : "A short time passes.", timePassed, MutationType.TIME);
		result.addAll(eggTick(timePassed));
		ass.tick(timePassed);
		modDebtCooldown(-timePassed);
		result.addAll(modHealth(getHealthRegen())); 
		result.addAll(modFood(-getMetabolicRate() * timePassed));
		result.addAll(debtTick((time / 6) - currentDay));
		return result;
	}
	
	public int getMetabolicRate() { return (hasHungerCharm() ? 1 : 2) + (hasTrudy() ? 1 : 0) + (hasKylira() ? 1 : 0); }

	private boolean hasHungerCharm() { return firstAccessory != null && firstAccessory.equals(new Accessory(AccessoryType.HUNGER_CHARM)); }
	public boolean hasGem() { return inventory.contains(new Potion(1, EffectType.GEM), false); }	
	public boolean hasIceCream() { return inventory.contains(new Misc(MiscType.ICE_CREAM), false); }
	
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
			for (int ii = 0; ii < lucky; ii+= 10 + perks.get(Perk.FORAGER.toString(), 0) * 5) {
				luckStreak.add(true);
			}
			for (int ii = 0; ii < unlucky; ii+= 10) {
				luckStreak.add(false);
			}			
		}
		luckStreak.add(!gotLucky);
		if (!gotLucky) {
			for (int ii = 0; ii < perks.get(Perk.FORAGER.toString(), 0); ii++) {
				luckStreak.add(true);
			}
		}
		return gotLucky;		
	}

	public boolean isDayTime() { return TimeOfDay.getTime(time).isDay(); }

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
			item instanceof Weapon ? (((Weapon) item).isMelee() ? setWeapon(item) : setRangedWeapon(item)) : 
			armor != null ? 
				(armor.isFootwear() ? setFootwear(item, false) : armor.isArmwear() ? setArmwear(item, false) : armor.isHeadgear() ? setHeadgear(item, false) : armor.isShield() ? setShield(item, false) : armor.coversTop() ? setArmor(item, false) : armor.coversBottom() ? setLegwear(item, false) : setUnderwear(item, false)) : 
			item instanceof Accessory ? setAccessory(item, false) :
			item instanceof ChastityCage ? setCage(item, false) :
			setPlug(item, false);
	}

	public Plug getPlug() { return plug; }
	
	@Override
	public String setPlug(Item plug, boolean newItem) {
		if (receivedAnal == 0) receivedAnal++;
		return super.setPlug(plug, newItem);
	}
	
	public ChastityCage getCage() { return cage; }

	public boolean isEquipped(Item item) {
		return item.equals(weapon) || item.equals(rangedWeapon) || item.equals(armor) || item.equals(shield) || item.equals(legwear) || item.equals(underwear) || item.equals(plug) || item.equals(cage) || item.equals(headgear) || item.equals(armwear) || item.equals(footwear) || item.equals(firstAccessory);
	}

	public int getAnalReceptionCount() { return receivedAnal; }
	public int getOralReceptionCount() { return receivedOral; }

	public void setGameOver(GameOver gameOver) { this.gameOver = gameOver; }

	public GameOver getGameOver() { return gameOver; }

	public boolean hasStance(Stance stance) { return stance != Stance.BLITZ || jobClass == JobClass.WARRIOR; }

	public void setPhallusType(PhallusType penisType) { phallus = penisType; }

	public boolean debtDue() {
		return debtCooldown <= 0 && (getCurrentDebt() >= 150 || (getCurrentDebt() >= 100 && getQuestStatus(QuestType.DEBT) < 1));
	}

	public Array<String> getEventLog() { return eventLog; }
	
	public DignityTier getDignity() { return dignity.tier; }
	public Array<MutationResult> modWillpower(int mod) { willpower += mod; if (willpower <0) willpower = 0; return getResult("Used " + -mod + " willpower!"); }
	public int getWillpower() { return willpower; }
	
	public void modDignity(int delta) { dignity.modDignity(delta); }
	
	public static class Dignity {
		private DignityTier tier;
		private int dignity;
		
		private Dignity() {
			tier = DignityTier.FULL;
			dignity = 100;
		}
		
		public void modDignity(int delta) {
			dignity += delta;
			if (dignity >= 100) {
				tier = tier.increase();
				dignity -= 100;
			}
			else if (dignity < 0) {
				tier = tier.decrease();
				dignity += 100;
			}
		}
	}
	
	public enum DignityTier {
		FULL,
		SHAMED,
		HUMILIATED,
		DISGRACED,
		NONE;
		
		public DignityTier decrease() { return this.ordinal() < values().length - 1 ? values()[this.ordinal() + 1] : this; }
		public DignityTier increase() { return this.ordinal() > 0 ? values()[this.ordinal() - 1] : this; }
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
		public boolean isHigh() { return this.ordinal() < HUMILIATED.ordinal(); }
		public boolean isAny() { return this.ordinal() < NONE.ordinal(); }
	}
	
	public enum QuestType {
		ORC, CRIER, QUETZAL, INNKEEP, TRUDY, CENTAUR, GOBLIN, OGRE, SPIDER, BROTHEL, ELF, DEBT, GADGETEER, MADAME, WITCH, MOUTH_FIEND, MERMAID, TRAINER, MERI, HUMAN_TOWN, MONSTER_TOWN, WARLOCK, GIANTESS,
		HARPY_ANATOMY, WEREWOLF_ANATOMY, GIANT_ANATOMY, CENTAUR_ANATOMY, CAT_ANATOMY, HARPY_INTELLIGENCE, WEREWOLF_SCENT
		;
		
		public String getQuestDescription(int currentValue) {
			switch (this) {
				case HARPY_ANATOMY: return currentValue == 1 ? "You know what harpy cock tastes like." : "";
				case WEREWOLF_ANATOMY: return currentValue == 1 ? "You know how the knot on a werewolf's cock works firsthand." : "";
				case GIANT_ANATOMY: return currentValue == 1 ? "You know that giants are exactly as big as advertised." : "";
				case CENTAUR_ANATOMY: return currentValue == 1 ? "You know that centaurs are horse-like below the... belt." : "";
				case CAT_ANATOMY: return currentValue == 1 ? "You know that direcat penises have spines that can scratch any itch." : "";
				case HARPY_INTELLIGENCE: return currentValue == 1 ? "You know that harpies are easily fooled." : "";
				case WEREWOLF_SCENT: return currentValue == 1 ? "You know the scent of a werewolf." : "";
				
				case HUMAN_TOWN:
					return currentValue == 1 ? "You've been to the town of Silajam." : "";					
				case MONSTER_TOWN:
					return currentValue == 1 ? "You've been to the town of Monsters." : "";		
				case TRAINER:
					return currentValue == 2 ? "You've learned the secret of the ice cream." : currentValue == 1 ? "You've been trained as an Enchantress by the legendary trainer!" : "";
				case MERI:
					return currentValue == 1 ? "You've learned from the witch." : "";					
				case BROTHEL:
					return currentValue > 0 ? "You've agreed to be a prostitute in the brothel." : "";
				case CENTAUR:
					return currentValue == 1 ? "You've met the centaur and have earned your place around their campfire." : "";
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
				case GIANTESS:
					return currentValue == 1 ? "You've seen the giant woman of the hidden valley." : "";
				case GOBLIN:
					switch (currentValue) {
						case 1: return "You've met Selkie the fem goblin.";
						case 2: return "You've been pseudo-impregnated by Selkie the fem goblin.";
						case 3: return "Selkie's goblin eggs have been impregnated by a harpy!";
						case 4: return "Selkie's goblin eggs have been impregnated by a werewolf!";
						case 5: return "Selkie's goblin eggs have been impregnated by a centaur!";
						case 6: return "Selkie's harpy-fertilized baby is ready to be born!";
						case 7: return "Selkie's werewolf-fertilized baby is ready to be born!";
						case 8: return "Selkie's centaur-fertilized baby is ready to be born!";
						case 9: return "You gave birth to Selkie's harpy-fertilized baby!";
						case 10: return "You gave birth to Selkie's werewolf-fertilized baby!";
						case 11: return "You gave birth to Selkie's centaur-fertilized baby!";
					}
				case INNKEEP:
					return currentValue == 4 ? "You've married the innkeep." : currentValue == 3 ? "You've been innkeep's bitch for a day's lodging." : currentValue == 2 ? "You've caught the innkeep's fuck for a day's lodging." : currentValue == 1 ? "You've sucked the innkeep off for a day's lodging." : "";
				case MADAME:
					return currentValue == 1 ? "You've introduced yourself to the Brothel madame." : "";	
				case MERMAID: 
					switch (currentValue) {
						case 1: return "You've encountered a mermaid and are on friendly terms, allowing safe passage through her waters.";
						case 2: return "You've encountered the mermaid, and know that if you enter her waters again, she'll attack on sight.";
						case 3: return "The mermaid has laid her eggs inside of you. You're absolutely full of them, and the crushing pressure in your abdomen is your reminder.";
						case 4: return "The mermaid's eggs have been gestating in your belly for days - your aching, cramping bowels feel like labor pains.  You are fully pregnant.";
						case 5: return "The mermaid's eggs have begun hatching - if you don't get to a body of water soon to birth them, you're worried you'll explode.  You're gonna be a momma!";
						case 6: return "The mermaid's eggs are hatching!";
						case 7: return "You've hatced the mermaid's eggs. Yeesh.";
					}
					break;
				case MOUTH_FIEND:
					return currentValue == 1 ? "The Brothel Madame has warned you not to be rough with the girls." : currentValue == 2 ? "The Brothel Madame has banned and threatened you." : "";
				case OGRE:
					return currentValue == 1 ? "You've seen an ogre." : "";		
				case ORC:
					return currentValue == 2 ? "You've encountered an orc and behaved cowardly." : currentValue == 1 ? "You've encountered Urka the orc and behaved honorably." : "";	
				case QUETZAL:
					return currentValue == 4 ? "You've been rewarded for defeating the Quetzal Goddess, the great lord of Mount Xiuh." : currentValue == 3 ? "You've defeated the Quetzal Goddess, the great lord of Mount Xiuh." : currentValue == 2 ? "You've seen the great lord of Mount Xiuh - she's a giant naga!" : currentValue == 1 ? "You've heard of the great lord of Mount Xiuh." : "";
				case SPIDER:
					switch (currentValue) {
						case 1: return "You've survived the spider-infested ruins.";
						case 2: return "The spider laid eggs in you!";
						case 3: return "The spiders eggs inside of you are beginning to stir!";
						case 4: return "The spider eggs inside of you are nearly ready to hatch!";
						case 5: return "The spider's eggs are hatching!";
						case 6: return "You've birthed the spider's brood!";
					}	
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
					break;
				case WARLOCK:
					switch (currentValue) {
						case 1: return "You've seen the strange manor in the ghost town.";
						case 2: return "You've visited the strange manor in the ghost town.";
					}	
					break;
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

	public boolean hasSeenDegradationTutorial() { boolean temp = degradationTutorial; degradationTutorial = true; return temp; }
	public boolean hasSeenGrappleTutorial() { boolean temp = grappleTutorial; grappleTutorial = true; return temp; }
	public boolean hasSeenKnockdownTutorial() { boolean temp = knockdownTutorial; knockdownTutorial = true; return temp; }
	public boolean hasSeenStanceTutorial() { boolean temp = stanceTutorial; stanceTutorial = true; return temp; }
	public Femininity getFemininity() { return femininity; }
	public boolean hasTrudy() { return getTrudyLevel() > 0; }
	public boolean hasKylira() { return getKyliraLevel() > 0;  }
	public boolean hasKyliraHeal() { boolean temp = kyliraHeal; if (hasKylira()) kyliraHeal = false; return temp && hasKylira(); }
	public int getKyliraLevel() { return questFlags.get(QuestType.ELF.toString(), 0) - 8; }
	public int getTrudyLevel() { return questFlags.get(QuestType.TRUDY.toString(), 0) - 4; }
	public String getKyliraAffection() { return getCompanionAffection(getKyliraLevel()); }
	public String getTrudyAffection() { return getCompanionAffection(getTrudyLevel()); }
	private String getCompanionAffection(int level) {
		switch (level) {
			case 1: return "Companion";
			case 2: return "Friend";
			case 3: return "Lover";
			default: return "";
		}
	}
	private String unequipItem(Item item) {
		if (item.equals(weapon)) return unequipWeapon();
		if (item.equals(shield)) return unequipShield();
		if (item.equals(armor)) return unequipArmor();
		if (item.equals(legwear)) return unequipLegwear();
		if (item.equals(underwear)) return unequipUnderwear();
		if (item.equals(headgear)) return unequipHeadgear();
		if (item.equals(armwear)) return unequipArmwear();
		if (item.equals(footwear)) return unequipFootwear();
		if (item.equals(firstAccessory)) return unequipAccessory();
		if (item.equals(plug)) return unequipPlug();
		if (item.equals(cage)) return unequipCage();
		return "";
	}
	
	public String discardItem(Item item) {
		String result = "";
		if (isEquipped(item)) {
			if (item.equals(cage) && !hasKey()) { return "Cannot drop chastity cage without key."; }
			result += unequipItem(item);
		}
		inventory.removeValue(item, true);
		result += " " + "You throw away the " + item.getName() + "."; 
		return result;
	}

	public void addBonuses(ObjectMap<String, Boolean> bonuses) { this.bonuses = bonuses; }

	public int getBonusStats() { return bonuses.get("Bonus Stat Points", false) ? 1 : 0; }

	public int getBonusPoints() { return bonusPoints; }

	public void setBonusPoints(int bonusPoints) { this.bonusPoints = bonusPoints; }

}

package com.majalis.character;

import static com.majalis.character.Techniques.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.majalis.asset.AssetEnum;
import com.majalis.battle.BattleFactory.EnemyEnum;
import com.majalis.character.Item.ItemEffect;
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
		statNameMap.put(Stat.STRENGTH, new Array<String>(true, new String[]{"Crippled", "Feeble", "Weak", "Soft", "Able", "Strong", "Mighty", "Powerful", "Hulking", "Heroic", "Godlike"}, 0, 11));
		statNameMap.put(Stat.ENDURANCE, new Array<String>(true, new String[]{"Feeble", "Infirm", "Fragile", "Frail", "Sturdy", "Durable", "Tough", "Stalwart", "Titanic", "Unstoppable", "Juggernaut"}, 0, 11));
		statNameMap.put(Stat.AGILITY, new Array<String>(true, new String[]{"Sluggish", "Clumsy", "Inept", "Slow", "Swift", "Quick", "Skillful", "Nimble", "Adept", "Preternatural", "Supernatural"}, 0, 11));
		statNameMap.put(Stat.PERCEPTION, new Array<String>(true, new String[]{"Senseless", "Oblivious", "Dim-witted", "Slow-minded", "Alert", "Perceptive", "Observant", "Sharp", "Astute", "Eagle-eyed", "Omniscient"}, 0, 11));
		statNameMap.put(Stat.MAGIC, new Array<String>(true, new String[]{"Unaware", "Mundane", "Aware", "Aligned", "Enchanted", "Mystical", "Otherwordly", "Arcane", "Mythical", "Omnipotent", "Demiurge"}, 0, 11));
		statNameMap.put(Stat.CHARISMA, new Array<String>(true, new String[]{"Inhuman", "Horrible", "Uncouth", "Unpleasant", "Plain", "Likeable", "Charismatic", "Charming", "Magnetic", "Lovable", "Worshipable"}, 0, 11));
	}
	
	protected String name;
	
	protected ObjectMap<String, Integer> skills;
	protected ObjectMap<String, Integer> perks;
	protected int skillPoints;
	protected int magicPoints;
	protected int perkPoints;
	
	private Array<Item> inventory;
	
	// advantage, range, and combat-lock(boolean) are shared properties between two creatures
	
	/* out of battle only statistics */
	protected int money;
	protected int food;

	protected Femininity femininity;
	protected LipFullness lipFullness;

	private int receivedAnal;
	private int receivedOral;	
	private int analCreampie;
	private int oralCreampie;
	private int cameFromAnal;
	private int cameFromOral;
	
	private boolean goblinVirgin;
	private boolean a2m;
	private boolean a2mcheevo;
	
	@SuppressWarnings("unused")
	private PlayerCharacter() {}
	
	public PlayerCharacter(boolean defaultValues) {
		super(defaultValues);
		if (defaultValues) {
			label = "You";
			secondPerson = true;
			healthTiers = new IntArray(new int[]{15, 15, 15, 15});
			currentHealth = getMaxHealth();	
			setStaminaToMax();
			setStabilityToMax();
			setManaToMax();
			food = 40;
			setGoblinVirginity(true);
			a2m = false;
			a2mcheevo = false;
			phallus = PhallusType.SMALL;	
			// this needs to be refactored - need "current defense" and for refresh method to set to max
			baseDefense = 6;
			money = 40;
			inventory = new Array<Item>();
			for (int ii = 5; ii <= 20; ii += 5) {
				inventory.add(new Potion(ii));
			}
		}
		
		skills = new ObjectMap<String, Integer>();
		for (Techniques basicTechnique: getBaseTechniques()) {
			skills.put(basicTechnique.toString(), 1);
		}
		
		perks = new ObjectMap<String, Integer>();
	}

	private static ObjectSet<Techniques> getBaseTechniques() {
		ObjectSet<Techniques> baseTechniques = new ObjectSet<Techniques>();
		baseTechniques.addAll(POWER_ATTACK, TEMPO_ATTACK, RESERVED_ATTACK, DUCK, SPRING_ATTACK, NEUTRAL_ATTACK, REVERSAL_ATTACK, CAREFUL_ATTACK, BLOCK, GUARD,
			KIP_UP, STAND_UP, STAY_KNELT, KNEE_UP, REST_FACE_DOWN, REST, JUMP_ATTACK, VAULT_OVER,
			RECEIVE_ANAL, RECEIVE_DOGGY, RECEIVE_STANDING, STRUGGLE_ORAL, STRUGGLE_DOGGY, STRUGGLE_ANAL, STRUGGLE_STANDING, RECEIVE_KNOT, SUCK_IT, BREAK_FREE_ANAL, BREAK_FREE_ORAL,
			SUBMIT, STRUGGLE_FULL_NELSON, BREAK_FREE_FULL_NELSON,
			OPEN_WIDE, GRAB_IT, STROKE_IT, LET_GO,
			RECIPROCATE_FORCED, GET_FACE_RIDDEN, STRUGGLE_FACE_SIT, STRUGGLE_SIXTY_NINE, BREAK_FREE_FACE_SIT, ROLL_OVER_UP, ROLL_OVER_DOWN
		);
		return baseTechniques;
	}
	
	public static ObjectMap<Stat, Array<String>> getStatMap() {
		return statNameMap;
	}
	
	public void addToInventory(Item item) {
		inventory.add(item);
	}

	public String consumeItem(Item item) {
		ItemEffect effect = item.getUseEffect();
		if (effect == null) { return "Item cannot be used."; }
		String result = "";
		switch (effect.getType()) {
			case HEALING:
				int currentHealth = getCurrentHealth();
				modHealth(effect.getMagnitude());
				result = "You used " + item.getName() + " and restored " + String.valueOf(getCurrentHealth() - currentHealth) + " health!";
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
				result = "You ate the " + item.getName() + "! Food stores increased by 5.";
				modFood(effect.getMagnitude());
				break;
			default:
				break;
			
		}	
		inventory.removeValue(item, true);
		return result;
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
		food = 40; 
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
			case THIEF: skillPoints = 5; food = 80; break;
			case MAGE: magicPoints = 2; break;
			case RANGER: weapon = new Weapon(WeaponType.Bow); break;
			case ENCHANTRESS: magicPoints = 1; perkPoints = 3; break;
			default:
		}
	}
	
	// this needs to consolidate logic with the getTechniques method
	public Array<Technique> getPossibleTechniques(AbstractCharacter target) {
		Array<Technique> possibles;
		switch(stance) {
			case BLITZ:
				return getTechniques(target, ALL_OUT_BLITZ, HOLD_BACK);
			case OFFENSIVE:
				possibles = getTechniques(target, BLITZ_ATTACK, POWER_ATTACK, ARMOR_SUNDER, RECKLESS_ATTACK, KNOCK_DOWN, VAULT, FEINT_AND_STRIKE, TEMPO_ATTACK, RESERVED_ATTACK);
				if (target.getStance() == Stance.SUPINE && target.isErect() && target.enemyType != EnemyEnum.SLIME && target.enemyType != EnemyEnum.CENTAUR && target.enemyType != EnemyEnum.UNICORN) {
					possibles.addAll(getTechniques(target, SIT_ON_IT));
				}
				return possibles;
			case BALANCED:
				possibles = getTechniques(target, SPRING_ATTACK, NEUTRAL_ATTACK, CAUTIOUS_ATTACK, BLOCK, INCANTATION, SLIDE, DUCK, HIT_THE_DECK);
				if (target.getStance() == Stance.SUPINE && target.isErect() && target.enemyType != EnemyEnum.SLIME && target.enemyType != EnemyEnum.CENTAUR && target.enemyType != EnemyEnum.UNICORN) {
					possibles.addAll(getTechniques(target, SIT_ON_IT));
				}
				return possibles;
			case DEFENSIVE:
				return getTechniques(target, REVERSAL_ATTACK, CAREFUL_ATTACK, GUARD, TAUNT, SECOND_WIND, INCANTATION, DUCK, HIT_THE_DECK, PARRY);
			case PRONE:
			case SUPINE:
				return getTechniques(target, KIP_UP, STAND_UP, KNEE_UP, stance == Stance.PRONE ? REST_FACE_DOWN : REST, stance == Stance.PRONE ? ROLL_OVER_UP : ROLL_OVER_DOWN);
			case KNEELING:
				possibles = getTechniques(target, UPPERCUT, STAND_UP, STAY_KNELT);
				if (target.isErect() && target.enemyType != EnemyEnum.SLIME) {
					possibles.addAll(getTechniques(target, GRAB_IT));
				}
				return possibles;
			case AIRBORNE:
				return getTechniques(target, JUMP_ATTACK, VAULT_OVER);
			case FULL_NELSON:
				if (struggle <= 0) {
					return getTechniques(target, SUBMIT, BREAK_FREE_FULL_NELSON);
				}
				return getTechniques(target, SUBMIT, STRUGGLE_FULL_NELSON);
			case DOGGY:
				if (struggle <= 0) {
					return getTechniques(target, RECEIVE_DOGGY, BREAK_FREE_ANAL);
				}
				return getTechniques(target, RECEIVE_DOGGY, STRUGGLE_DOGGY);
			case ANAL:
				if (struggle <= 0) {
					return getTechniques(target, RECEIVE_ANAL, BREAK_FREE_ANAL);
				}
				return getTechniques(target, RECEIVE_ANAL, STRUGGLE_ANAL);
			case HANDY:
				return getTechniques(target, STROKE_IT, LET_GO, OPEN_WIDE);
			case STANDING:
				if (struggle <= 0) {
					return getTechniques(target, RECEIVE_STANDING, BREAK_FREE_ANAL);
				}
				return getTechniques(target, RECEIVE_STANDING, STRUGGLE_STANDING);
			case COWGIRL:
				return getTechniques(target, RIDE_ON_IT, STAND_OFF_IT);
			case KNOTTED:
				return getTechniques(target, RECEIVE_KNOT);
			case FELLATIO:
				if (struggle <= 0) {
					return getTechniques(target, SUCK_IT, BREAK_FREE_ORAL);
				}
				return getTechniques(target, SUCK_IT, STRUGGLE_ORAL);
			case FACE_SITTING:
				if (struggle <= 0) {
					return getTechniques(target, GET_FACE_RIDDEN, STRUGGLE_FACE_SIT);
				}
				return getTechniques(target, GET_FACE_RIDDEN, BREAK_FREE_FACE_SIT);
			case SIXTY_NINE:
				if (struggle <= 0) {
					return getTechniques(target, RECIPROCATE_FORCED, STRUGGLE_SIXTY_NINE);
				}
				return getTechniques(target, RECIPROCATE_FORCED, BREAK_FREE_ORAL);
			case CASTING:
				return getTechniques(target, COMBAT_FIRE, COMBAT_HEAL, TITAN_STRENGTH);
			default: return null;
		}
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
		if (resolvedAttack.getGrapple() > 0) {
			struggle -= resolvedAttack.getGrapple();
			resolvedAttack.addMessage("You struggle to break free!");
			if (struggle <= 3) {
				resolvedAttack.addMessage("You feel their grasp slipping away!");
			}
			else if (struggle <= 0) {
				struggle = 0;
				resolvedAttack.addMessage("You are almost free!");
			}
		}	
		return super.doAttack(resolvedAttack);
	}
	
	@Override
	public Array<String> receiveAttack(Attack resolvedAttack) {
		super.receiveAttack(resolvedAttack);
		
		String result;
		if (!oldStance.isAnal() && stance.isAnal()) {
			result = receiveAnal(); 
			if (result != null) { resolvedAttack.addMessage(result); } 
			if (resolvedAttack.getUser().equals("Goblin")) {
				setGoblinVirginity(false);
			}
			a2m = true;
		}
		else if (!oldStance.isOral() && stance.isOral()) {
			result = receiveOral();
			if (result != null) { resolvedAttack.addMessage(result); } 
			if(a2m) {
				resolvedAttack.addMessage("Bleugh! That was in your ass!");
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
		return resolvedAttack.getMessages();		
	}
	
	public void refresh() {
		setStaminaToMax();
		setStabilityToMax();
		setManaToMax();
		stance = Stance.BALANCED;
		a2m = false;
		buttful = 0;
		mouthful = 0;
		baseDefense = 6;
		weapon = disarmedWeapon;
		disarmedWeapon = null;
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
		if(perks.get(Perk.WELLROUNDED, 0) > 0 && !(this.perks.get(Perk.WELLROUNDED.toString(), 0) > 0) ) {
			increaseLowestStat();
		}
		if(perks.get(Perk.CATAMITE, 0) > 0 && !(this.perks.get(Perk.CATAMITE.toString(), 0) > 0)) {
			addSkill(SIT_ON_IT, 1);
			addSkill(RIDE_ON_IT, 1);
			addSkill(STAND_OFF_IT, 1);
		}
		this.perks.clear();
		for (Perk key : perks.keys()) {
			addPerk(key, perks.get(key));
		}
	}
	
	public int getScoutingScore() {
		return getPerception() + (perks.get(Perk.SURVEYOR.toString(), 0) > 0 ? perks.get(Perk.SURVEYOR.toString()) * 2 : 0);
	}
	
	public int getLewdCharisma() {
		return getCharisma() + (perks.get(Perk.EROTIC.toString(), 0) > 0 ? perks.get(Perk.EROTIC.toString()) * 2 : 0);
	}	
	
	public boolean isLewd() {
		return perks.get(Perk.CATAMITE.toString(), 0) > 0;
	}
	@Override
	protected String increaseLust() {
		switch (stance) {
			case DOGGY:
			case KNOTTED:
			case ANAL:
			case STANDING:
			case COWGIRL:
				if (perks.get(Perk.WEAK_TO_ANAL.toString(), 0) > 0) return increaseLust(2);
			case FELLATIO:
			case SIXTY_NINE:
				return increaseLust(1);
			default: return null;
		}
	}
	
	@Override
	protected String increaseLust(int lustIncrease) {
		String spurt = "";
		String result = null;
		lust += lustIncrease;
		if (lust > 10) {
			lust = 0;
			switch (stance) {
				case KNOTTED:
				case DOGGY: 
				case ANAL:
					spurt = "Awoo! Semen spurts out your untouched cock as your hole is violated!\n"
						+	"You feel it with your ass, like a girl! Your face is red with shame!\n"
						+	"Got a little too comfortable, eh?\n";
					cameFromAnal++;
					result = incrementPerk(cameFromAnal, Perk.ANAL_LOVER, 5, 3, 1);
					break;
				case COWGIRL:
					spurt = "Awoo! Semen spurts out your untouched cock as your hole is violated!\n"
							+	"You feel it with your ass, like a girl! Your face is red with shame!\n"
							+	"It spews all over them!\n";
					cameFromAnal++;
					result = incrementPerk(cameFromAnal, Perk.ANAL_LOVER, 5, 3, 1);
					break;
				case FELLATIO:
					spurt = "You spew while sucking!\n"
						+	"Your worthless cum spurts into the dirt!\n"
						+	"They don't even notice!\n";
					cameFromOral++;
					result = incrementPerk(cameFromOral, Perk.MOUTH_MANIAC, 5, 3, 1);
					break;
				case FACE_SITTING:
					spurt = "You spew while she rides your face!\n"
							+	"Your worthless cum spurts into the dirt!\n"
							+	"They don't even notice!\n";
					break;
				case SIXTY_NINE:
					spurt = "You spew into her mouth!\n"
							+	"Her cock twitches in yours!\n"
							+	"You're about to retun the favor!\n";
					cameFromOral++;
					result = incrementPerk(cameFromOral, Perk.MOUTH_MANIAC, 5, 3, 1);
					break;
				default: spurt = "You spew your semen onto the ground!\n"; 
			}
			if (result != null) spurt += result + "\n";
			spurt += "You're now flaccid!\n";
		}
		return !spurt.isEmpty() ? spurt : null;
	}

	public void modExperience(Integer exp) { experience += exp; }
	
	public int getExperience() { return experience; }

	public void modFood(Integer foodChange) { food += foodChange; if (food < 0) food = 0; }

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
		return experience / 10;
	}

	public void levelUp() {
		experience -= 10;
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
	
	public String getMasculinityPath() {
		int masculinityLevel = getMasculinityLevel();
		return masculinityLevel < 1 ? AssetEnum.MARS_ICON_0.getPath() : masculinityLevel < 3 ? AssetEnum.MARS_ICON_1.getPath() : masculinityLevel < 8 ? AssetEnum.MARS_ICON_2.getPath() : masculinityLevel < 15 ? AssetEnum.MARS_ICON_3.getPath() : AssetEnum.MARS_ICON_4.getPath();
	}
	
	public boolean isVirgin(EnemyEnum enemyEnum) {
		switch (enemyEnum) {
			case GOBLIN: return goblinVirgin;
			default:
		}
		return false;
	}
	
	private String receiveAnal() {
		receivedAnal++;
		return incrementPerk(receivedAnal, Perk.ANAL_LOVER, 10, 6, 3);
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
		return null;
	}
	
	public void setGoblinVirginity(boolean virginity) {
		goblinVirgin = virginity;
		if (!goblinVirgin) {
			receivedAnal++;
		}
	}
	
	public boolean buyItem(Item item, int cost) {
		if (cost > money) {
			return false;
		}
		money -= cost;
		inventory.add(item);
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
		String result = "";
		String temp;
		for (int ii = 0; ii < sex.getAnalSex(); ii++) {
			temp = receiveAnal();
			if (temp != null) {
				result += temp + "\n";
			}
		}
		for (int ii = 0; ii < sex.getCreampies(); ii++) {
			temp = fillButt(3);
			if (temp != null) {
				result += temp + "\n";
			}
		}
		for (int ii = 0; ii < sex.getAnalEjaculations(); ii++) {
			cameFromAnal++;
		}
		for (int ii = 0; ii < sex.getOralSex(); ii++) {
			temp = receiveOral();
			if (temp != null) {
				result += temp + "\n";
			}
		}
		for (int ii = 0; ii < sex.getOralCreampies(); ii++) {
			temp = fillMouth(1);
			if (temp != null) {
				result += temp + "\n";
			}
		}
		for (int ii = 0; ii < sex.getFellatioEjaculations(); ii++) {
			cameFromOral++;
		}
		
		return result;
	}

	public String getBootyLiciousness() {
		return bootyliciousness != null ? bootyliciousness.toString() : Bootyliciousness.Bubble.toString();
	}
	
	public String getLipFullness() {
		return lipFullness != null ? lipFullness.toString() : LipFullness.Thin.toString(); 
	}
}

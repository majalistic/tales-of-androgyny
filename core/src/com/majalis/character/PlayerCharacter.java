package com.majalis.character;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.majalis.battle.BattleFactory.EnemyEnum;
import com.majalis.save.SaveManager.JobClass;

/*
 * Contains the current player character's statistics, including "party" statistics like food remaining
 */
public class PlayerCharacter extends AbstractCharacter {

	private final static ObjectMap<Stat, Array<String>> statNameMap = new ObjectMap<Stat, Array<String>>();
	static {
		statNameMap.put(Stat.STRENGTH, new Array<String>(true, new String[]{"Crippled", "Feeble", "Weak", "Soft", "Able", "Strong", "Mighty", "Powerful", "Hulking", "Heroic", "Godlike"}, 0, 11));
		statNameMap.put(Stat.ENDURANCE, new Array<String>(true, new String[]{"Feeble", "Infirm", "Fragile", "Frail", "Sturdy", "Durable", "Tough", "Stalwart", "Titanic", "Unstoppable", "Juggernaut"}, 0, 11));
		statNameMap.put(Stat.AGILITY, new Array<String>(true, new String[]{"Sluggish", "Clumsy", "Inept", "Deft", "Swift", "Quick", "Skillful", "Nimble", "Adept", "Preternatural", "Supernatural"}, 0, 11));
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
	
	// advantage, range, and combat-lock(boolean) are shared properties between two creatures
	
	/* out of battle only statistics */
	protected int money;
	protected int food;
	
	protected int dignity;
	protected int integrity;
	protected int analIntegrity;
	protected int lustForDick;
	protected Femininity femininity;
	protected LipFullness lipFullness;
	
	/* anatomy - contains current and permanent properties */
	private boolean a2m;
	private boolean a2mcheevo;
	
	@SuppressWarnings("unused")
	private PlayerCharacter(){}
	
	public PlayerCharacter(boolean defaultValues){
		super(defaultValues);
		if (defaultValues){
			label = "You";
			secondPerson = true;
			healthTiers = new IntArray(new int[]{15, 15, 15, 15});
			currentHealth = getMaxHealth();	
			setStaminaToMax();
			setStabilityToMax();
			setManaToMax();
			food = 40;
			a2m = false;
			a2mcheevo = false;
			battleOver = 0;
			phallus = PhallusType.SMALL;			
		}
		
		skills = new ObjectMap<String, Integer>();
		for (Techniques basicTechnique: getBaseTechniques()){
			skills.put(basicTechnique.toString(), 1);
		}
		
		perks = new ObjectMap<String, Integer>();
	}
	
	private static ObjectSet<Techniques> getBaseTechniques(){
		ObjectSet<Techniques> baseTechniques = new ObjectSet<Techniques>();
		baseTechniques.addAll(Techniques.POWER_ATTACK, Techniques.TEMPO_ATTACK, Techniques.RESERVED_ATTACK, Techniques.DUCK, Techniques.SPRING_ATTACK, Techniques.NEUTRAL_ATTACK, Techniques.REVERSAL_ATTACK, Techniques.CAREFUL_ATTACK, Techniques.BLOCK, Techniques.GUARD,
		Techniques.KIP_UP, Techniques.STAND_UP, Techniques.STAY_KNELT, Techniques.KNEE_UP, Techniques.REST_FACE_DOWN, Techniques.REST, Techniques.JUMP_ATTACK, 
		Techniques.RECEIVE_ANAL, Techniques.RECEIVE_DOGGY, Techniques.RECEIVE_STANDING, Techniques.STRUGGLE_ORAL, Techniques.STRUGGLE_DOGGY, Techniques.STRUGGLE_ANAL, Techniques.STRUGGLE_STANDING, Techniques.RECEIVE_KNOT, Techniques.SUCK_IT, Techniques.BREAK_FREE_ANAL, Techniques.BREAK_FREE_ORAL,
		Techniques.SUBMIT, Techniques.STRUGLE_FULL_NELSON, Techniques.BREAK_FREE_FULL_NELSON,
		Techniques.OPEN_WIDE, Techniques.GRAB_IT, Techniques.STROKE_IT, Techniques.LET_GO
		);
		return baseTechniques;
	}
	
	public static ObjectMap<Stat, Array<String>> getStatMap(){
		return statNameMap;
	}
	
	public JobClass getJobClass() { return jobClass; }
	
	public void setJobClass(JobClass jobClass){
		for (Stat stat: Stat.values()){
			setStat(stat, jobClass.getBaseStat(stat));
		}
		this.jobClass = jobClass;
		skillPoints = 2; 
		perkPoints = 2; 
		magicPoints = 0;
		food = 40; 
		skills.remove(Techniques.COMBAT_HEAL.toString());
		skills.remove(Techniques.INCANTATION.toString());
		skills.remove(Techniques.BLITZ_ATTACK.toString());
		skills.remove(Techniques.ALL_OUT_BLITZ.toString());
		skills.remove(Techniques.HOLD_BACK.toString());
		perks.remove(Perk.WEAK_TO_ANAL.toString());
		// warrior will need to get bonus stance options, Ranger will need to start with a bow
		switch (jobClass){ 
			case WARRIOR: skillPoints = 3; skills.put(Techniques.BLITZ_ATTACK.toString(), 1); skills.put(Techniques.ALL_OUT_BLITZ.toString(), 1); skills.put(Techniques.HOLD_BACK.toString(), 1); perks.put(Perk.WEAK_TO_ANAL.toString(), 1); break;
			case PALADIN: addSkill(Techniques.COMBAT_HEAL, 1); break;
			case THIEF: skillPoints = 5; food = 80; break;
			case MAGE: magicPoints = 2; break;
			case ENCHANTRESS: magicPoints = 1; perkPoints = 3; break;
			default:
		}
	}
	
	// this needs to consolidate logic with the getTechniques method
	public Array<Technique> getPossibleTechniques(AbstractCharacter target){
		Array<Technique> possibles;
		switch(stance){
			case BLITZ:
				return getTechniques(Techniques.ALL_OUT_BLITZ, Techniques.HOLD_BACK);
			case OFFENSIVE:
				possibles = getTechniques(Techniques.BLITZ_ATTACK, Techniques.POWER_ATTACK, Techniques.ARMOR_SUNDER, Techniques.RECKLESS_ATTACK, Techniques.KNOCK_DOWN, Techniques.VAULT, Techniques.TEMPO_ATTACK, Techniques.RESERVED_ATTACK);
				if (target.getStance() == Stance.SUPINE && target.isErect() && target.enemyType != EnemyEnum.SLIME){
					possibles.addAll(getTechniques(Techniques.SIT_ON_IT));
				}
				return possibles;
			case BALANCED:
				possibles = getTechniques(Techniques.SPRING_ATTACK, Techniques.NEUTRAL_ATTACK, Techniques.CAUTIOUS_ATTACK, Techniques.BLOCK, Techniques.INCANTATION, Techniques.DUCK, Techniques.HIT_THE_DECK);;
				if (target.getStance() == Stance.SUPINE && target.isErect() && target.enemyType != EnemyEnum.SLIME){
					possibles.addAll(getTechniques(Techniques.SIT_ON_IT));
				}
				return possibles;
			case DEFENSIVE:
				return getTechniques(Techniques.REVERSAL_ATTACK, Techniques.CAREFUL_ATTACK, Techniques.GUARD, Techniques.TAUNT, Techniques.SECOND_WIND, Techniques.INCANTATION, Techniques.DUCK, Techniques.HIT_THE_DECK, Techniques.PARRY);
			case PRONE:
			case SUPINE:
				return getTechniques(Techniques.KIP_UP, Techniques.STAND_UP, Techniques.KNEE_UP, stance == Stance.PRONE ? Techniques.REST_FACE_DOWN : Techniques.REST);
			case KNEELING:
				possibles = getTechniques(Techniques.STAND_UP, Techniques.STAY_KNELT);
				if (target.isErect() && target.enemyType != EnemyEnum.SLIME){
					possibles.addAll(getTechniques(Techniques.GRAB_IT));
				}
				return possibles;
			case AIRBORNE:
				return getTechniques(Techniques.JUMP_ATTACK);
			case FULL_NELSON:
				if (struggle <= 0){
					return getTechniques(Techniques.SUBMIT, Techniques.BREAK_FREE_FULL_NELSON);
				}
				return getTechniques(Techniques.SUBMIT, Techniques.STRUGLE_FULL_NELSON);
			case DOGGY:
				if (struggle <= 0){
					return getTechniques(Techniques.RECEIVE_DOGGY, Techniques.BREAK_FREE_ANAL);
				}
				return getTechniques(Techniques.RECEIVE_DOGGY, Techniques.STRUGGLE_DOGGY);
			case ANAL:
				if (struggle <= 0){
					return getTechniques(Techniques.RECEIVE_ANAL, Techniques.BREAK_FREE_ANAL);
				}
				return getTechniques(Techniques.RECEIVE_ANAL, Techniques.STRUGGLE_ANAL);
			case HANDY:
				
				return getTechniques(Techniques.STROKE_IT, Techniques.LET_GO, Techniques.OPEN_WIDE);
			case STANDING:
				if (struggle <= 0){
					return getTechniques(Techniques.RECEIVE_STANDING, Techniques.BREAK_FREE_ANAL);
				}
				return getTechniques(Techniques.RECEIVE_STANDING, Techniques.STRUGGLE_STANDING);
			case COWGIRL:
				return getTechniques(Techniques.RIDE_ON_IT, Techniques.STAND_OFF_IT);
			case KNOTTED:
				return getTechniques(Techniques.RECEIVE_KNOT);
			case FELLATIO:
				if (struggle <= 0){
					return getTechniques(Techniques.SUCK_IT, Techniques.BREAK_FREE_ORAL);
				}
				return getTechniques(Techniques.SUCK_IT, Techniques.STRUGGLE_ORAL);
			case CASTING:
				return getTechniques(Techniques.COMBAT_FIRE, Techniques.COMBAT_HEAL, Techniques.TITAN_STRENGTH);
			default: return null;
		}
	}
	
	
	public Technique getTechnique(AbstractCharacter target){
		// this should be re-architected - player characters likely won't use this method
		return null;
	}
	
	private Array<Technique> getTechniques(Techniques... possibilities) {
		Array<Technique> possibleTechniques = new Array<Technique>();
		
		for (Techniques technique : possibilities){
			if (skills.containsKey(technique.toString())){
				int power = technique.getTrait().isSpell() ? (technique.getTrait().getBuff() != null ? stepDown(getMagic()) : getMagic()  ): technique.getTrait().isTaunt() ? getCharisma() : getStrength();
				power += skills.get(technique.toString()) - 1;
				possibleTechniques.add(new Technique(technique.getTrait(), power));
			}	
		}
		
		return possibleTechniques;
	}
	
	@Override
	public Attack doAttack(Attack resolvedAttack) {
		if (resolvedAttack.getGrapple() > 0){
			struggle -= resolvedAttack.getGrapple();
			resolvedAttack.addMessage("You struggle to break free!");
			if (struggle <= 3){
				resolvedAttack.addMessage("You feel their grasp slipping away!");
			}
			else if (struggle <= 0){
				struggle = 0;
				resolvedAttack.addMessage("You are almost free!");
			}
		}	
		if (resolvedAttack.getForceStance() == Stance.DOGGY){
			a2m = true;
		}
		else if (resolvedAttack.getForceStance() == Stance.FELLATIO && a2m){
			resolvedAttack.addMessage("Bleugh! That was in your ass!");
			if (!a2mcheevo){
				a2mcheevo = true;
				resolvedAttack.addMessage("Achievement unlocked: Ass to Mouth.");
			}
		}
		return super.doAttack(resolvedAttack);
	}
	
	public void refresh(){
		setStaminaToMax();
		setStabilityToMax();
		setManaToMax();
		stance = Stance.BALANCED;
		a2m = false;
		battleOver = 0;
		buttful = 0;
		mouthful = 0;
		baseDefense = 5;
	}

	public enum Femininity {
		MALE,
		UNMASCULINE,
		EFFIMINATE,
		FEMALE,
		BITCH
	}
	
	public enum Bootyliciousness {
		Bubble,
		Round,
		Fat // dear diary, the ass was fat
	}
	
	public enum LipFullness {
		POUTY
	}

	public int getStat(Stat stat) {
		switch(stat){
			case STRENGTH: return getStrength();
			case ENDURANCE: return getEndurance();
			case AGILITY: return getAgility();
			case PERCEPTION: return getPerception();
			case MAGIC: return getMagic();
			case CHARISMA: return getCharisma();
			default: return -1;
		}
	}
	
	public int getBaseStat(Stat stat){
		switch(stat){
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

	public void setStat(Stat stat, int amount){
		switch(stat){
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
		if (newTech.getTrait().isSpell()){
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
		for (String key : skills.keys()){
			tempSkills.put(Techniques.valueOf(key), skills.get(key));
		}
		return tempSkills;
	}
	
	public ObjectMap<Perk, Integer> getPerks() {
		ObjectMap<Perk, Integer> tempPerks = new ObjectMap<Perk, Integer>();
		for (String key : perks.keys()){
			tempPerks.put(Perk.valueOf(key), perks.get(key));
		}
		return tempPerks;
	}
	
	public void setSkills(ObjectMap<Techniques, Integer> skills) {
		this.skills.clear();
		for (Techniques key : skills.keys()){
			addSkill(key, skills.get(key));
		}
	}

	private void increaseLowestStat(){
		int min = getBaseStrength();
		Stat minStat = Stat.STRENGTH;
		for (Stat stat : Stat.values()){
			if (getStat(stat) < min){
				min = getStat(stat);
				minStat = stat;
			}
		}
		setStat(minStat, getStat(minStat) + 1);
	}
	
	public void setPerks(ObjectMap<Perk, Integer> perks) {
		if(perks.containsKey(Perk.WELLROUNDED) && !this.perks.containsKey(Perk.WELLROUNDED.toString())){
			increaseLowestStat();
		}
		if(perks.containsKey(Perk.CATAMITE) && !this.perks.containsKey(Perk.CATAMITE.toString())){
			addSkill(Techniques.SIT_ON_IT, 1);
			addSkill(Techniques.RIDE_ON_IT, 1);
			addSkill(Techniques.STAND_OFF_IT, 1);
		}
		this.perks.clear();
		for (Perk key : perks.keys()){
			addPerk(key, perks.get(key));
		}
	}
	
	public int getScoutingScore() {
		return getPerception() + (perks.containsKey(Perk.SURVEYOR.toString()) ? perks.get(Perk.SURVEYOR.toString()) * 2 : 0);
	}
	
	public int getLewdCharisma() {
		return getCharisma() + (perks.containsKey(Perk.EROTIC.toString()) ? perks.get(Perk.EROTIC.toString()) * 2 : 0);
	}	
	
	public boolean isLewd() {
		return perks.containsKey(Perk.CATAMITE.toString());
	}
	@Override
	protected String increaseLust(){
		switch (stance){
			case DOGGY:
			case KNOTTED:
			case ANAL:
			case COWGIRL:
				if (perks.containsKey(Perk.WEAK_TO_ANAL.toString())) return increaseLust(2);
			case FELLATIO:
				return increaseLust(1);
			default: return null;
		}
	}
	
	@Override
	protected String increaseLust(int lustIncrease) {
		String spurt = "";
		lust += lustIncrease;
		if (lust > 10){
			lust = 0;
			switch (stance){
				case KNOTTED:
				case DOGGY: 
				case ANAL:
					spurt = "Awoo! Semen spurts out your untouched cock as your hole is violated!\n"
						+	"You feel it with your ass, like a girl! Your face is red with shame!\n"
						+	"Got a little too comfortable, eh?\n";
				break;
				case COWGIRL:
					spurt = "Awoo! Semen spurts out your untouched cock as your hole is violated!\n"
							+	"You feel it with your ass, like a girl! Your face is red with shame!\n"
							+	"It spews all over them!\n";
				break;
				case FELLATIO:
					spurt = "You spew while sucking!\n"
						+	"Your worthless cum spurts into the dirt!\n"
						+	"They don't even notice!\n";
				break;
				default: spurt = "You spew your semen onto the ground!\n"; 
			}
			spurt += "You're now flaccid!\n";
		}
		return !spurt.isEmpty() ? spurt : null;
	}

	public void modExperience(Integer exp) { experience += exp; }
	
	public int getExperience(){ return experience; }

	public void modFood(Integer foodChange) { food += foodChange; if (food < 0) food = 0; }

	public Integer getFood() { return food; }

	public void setBootyliciousness(Bootyliciousness buttSize) { bootyliciousness = buttSize; }

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
		if (hasMagic()){
			magicPoints++;
		}
	}

	public boolean hasMagic() {
		return jobClass == JobClass.ENCHANTRESS || jobClass == JobClass.MAGE || jobClass == JobClass.PALADIN;
	}

	public boolean needsLevelUp() {
		return skillPoints > 0 || magicPoints > 0 || perkPoints > 0 || getStoredLevels() > 0;
	}
}

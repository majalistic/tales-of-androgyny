package com.majalis.character;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;
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
	
	public String name;
	
	public ObjectSet<Techniques> skills;
	public ObjectSet<Perk> perks;
	public int skillPoints;
	public int magicPoints;
	public int perkPoints;
	
	// advantage, range, and combat-lock(boolean) are shared properties between two creatures
	
	/* out of battle only statistics */
	public int money;
	public int food;
	public int exp;
	
	public int dignity;
	public int integrity;
	public int analIntegrity;
	public int lustForDick;
	public Femininity femininity;
	public Bootyliciousness bootyliciousness;
	public LipFullness lipFullness;
	
	/* anatomy - contains current and permanent properties */
	public boolean a2m;
	public boolean a2mcheevo;
	
	@SuppressWarnings("unused")
	private PlayerCharacter(){}
	
	public PlayerCharacter(boolean defaultValues){
		super(defaultValues);
		if (defaultValues){
			label = "You";
			secondPerson = true;
			currentHealth = getMaxHealth();	
			setStaminaToMax();
			setStabilityToMax();
			setManaToMax();
			food = 40;
			a2m = false;
			a2mcheevo = false;
			battleOver = 0;
		}
		
		skills = new ObjectSet<Techniques>();
		skills.addAll(Techniques.STRONG_ATTACK, Techniques.TEMPO_ATTACK, Techniques.RESERVED_ATTACK, Techniques.DUCK, Techniques.SPRING_ATTACK, Techniques.NEUTRAL_ATTACK, Techniques.REVERSAL_ATTACK, Techniques.CAREFUL_ATTACK, Techniques.BLOCK, Techniques.GUARD, Techniques.KIP_UP, Techniques.STAND_UP,
				Techniques.KNEE_UP, Techniques.REST_FACE_DOWN, Techniques.REST, Techniques.JUMP_ATTACK, Techniques.RECEIVE, Techniques.STRUGGLE_ORAL, Techniques.STRUGGLE_ANAL, Techniques.RECEIVE_KNOT, Techniques.OPEN_WIDE, Techniques.BREAK_FREE_ANAL, Techniques.BREAK_FREE_ORAL);
		perks = new ObjectSet<Perk>();
	}
	// stop-gap method to deal with idiosyncracies of ObjectSet deserialization - map breaks as a result of deserialization
	public void init(){
		ObjectSet<Techniques> tempSkills = new ObjectSet<Techniques>();
		for (Techniques technique : skills){
			tempSkills.add(technique);
		}
		skills = tempSkills;
		ObjectSet<Perk> tempPerks = new ObjectSet<Perk>();
		for (Perk perk : perks){
			tempPerks.add(perk);
		}
		perks = tempPerks;
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
		skills.remove(Techniques.COMBAT_HEAL);
		skills.remove(Techniques.INCANTATION);
		// warrior will need to get bonus stance options, Ranger will need to start with a bow
		switch (jobClass){ 
			
			case WARRIOR: skillPoints = 3; break;
			case PALADIN: addSkill(Techniques.COMBAT_HEAL); break;
			case THIEF: skillPoints = 5; food = 80; break;
			case MAGE: magicPoints = 2; break;
			case ENCHANTRESS: magicPoints = 1; perkPoints = 3; break;
			default:
		}
	}
	
	// this needs to consolidate logic with the getTechniques method
	public Array<Technique> getPossibleTechniques(){
		switch(stance){
			case OFFENSIVE:
				return getTechniques(Techniques.STRONG_ATTACK, Techniques.RECKLESS_ATTACK, Techniques.KNOCK_DOWN, Techniques.VAULT, Techniques.TEMPO_ATTACK, Techniques.RESERVED_ATTACK, Techniques.DUCK, Techniques.HIT_THE_DECK);
			case BALANCED:
				return getTechniques(Techniques.SPRING_ATTACK, Techniques.NEUTRAL_ATTACK, Techniques.CAUTIOUS_ATTACK, Techniques.BLOCK, Techniques.INCANTATION, Techniques.DUCK, Techniques.HIT_THE_DECK);
			case DEFENSIVE:
				return getTechniques(Techniques.REVERSAL_ATTACK, Techniques.CAREFUL_ATTACK, Techniques.GUARD, Techniques.TAUNT, Techniques.SECOND_WIND, Techniques.INCANTATION, Techniques.DUCK, Techniques.HIT_THE_DECK, Techniques.PARRY);
			case PRONE:
			case SUPINE:
				return getTechniques(Techniques.KIP_UP, Techniques.STAND_UP, Techniques.KNEE_UP, stance == Stance.PRONE ? Techniques.REST_FACE_DOWN : Techniques.REST);
			case KNEELING:
				return getTechniques(Techniques.STAND_UP);
			case AIRBORNE:
				return getTechniques(Techniques.JUMP_ATTACK);
			case DOGGY:
				if (struggle <= 0){
					return getTechniques(Techniques.RECEIVE, Techniques.BREAK_FREE_ANAL);
				}
				return getTechniques(Techniques.RECEIVE, Techniques.STRUGGLE_ANAL);
			case KNOTTED:
				return getTechniques(Techniques.RECEIVE_KNOT);
			case FELLATIO:
				if (struggle <= 0){
					return getTechniques(Techniques.OPEN_WIDE, Techniques.BREAK_FREE_ORAL);
				}
				return getTechniques(Techniques.OPEN_WIDE, Techniques.STRUGGLE_ORAL);
			case CASTING:
				return getTechniques(Techniques.COMBAT_FIRE, Techniques.COMBAT_HEAL, Techniques.TITAN_STRENGTH);
			default: return null;
		}
	}
	
	
	public Technique getTechnique(AbstractCharacter target){
		// this should be re-architected - player characters likely won't use this method
		return null;
	}
	
	public Array<Technique> getTechniques(Techniques... possibilities) {
		Array<Technique> possibleTechniques = new Array<Technique>();
		
		for (Techniques technique : possibilities){
			if (skills.contains(technique)){
				possibleTechniques.add(new Technique(technique.getTrait(), technique.getTrait().isSpell() ? getMagic() : technique.getTrait().isTaunt() ? getCharisma() : getStrength()));
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
	
	public enum Stat {
		STRENGTH,
		ENDURANCE,
		AGILITY,
		PERCEPTION,
		MAGIC,
		CHARISMA
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
	public void addSkill(Techniques newTech) {
		// if it's a spell, add incantation
		if (newTech.getTrait().isSpell()){
			skills.add(Techniques.INCANTATION);
		}
		skills.add(newTech);	
	}
	
	// need to add the list of default skills, the actual variable, and some way to access it for skill selection purposes (filter)
	public void addPerk(Perk newPerk) {
		perks.add(newPerk);	
	}

	public ObjectSet<Techniques> getSkills() {
		return skills;
	}

	public ObjectSet<Perk> getPerks() {
		return perks;
	}
	
	public int getScoutingScore() {
		return getPerception() + (perks.contains(Perk.SURVEYOR) ? 2 : 0);
	}
	
	public int getLewdCharisma() {
		return getCharisma() + (perks.contains(Perk.EROTIC) ? 2 : 0);
	}	
	
	public boolean isLewd() {
		return perks.contains(Perk.CATAMITE);
	}
	@Override
	protected String increaseLust(){
		switch (stance){
			case DOGGY:
			case KNOTTED:
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
					spurt = "Awoo! Semen spurts out your untouched cock as your hole is violated!\n"
						+	"You feel it with your ass, like a girl! Your face is red with shame!\n"
						+	"Got a little too comfortable, eh?\n";
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

	public boolean outOfStaminaOrStability(Technique technique) {
		 return getStaminaMod(technique) >= currentStamina || technique.getStabilityCost() - getStabilityRegen() >= stability;
	}

	public boolean lowStaminaOrStability(Technique technique) {
		return getStaminaMod(technique) >= currentStamina - 5 || technique.getStabilityCost() - getStabilityRegen() >= stability - 5;
	}
	
	
	
}

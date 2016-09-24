package com.majalis.character;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.ObjectMap;
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
	// public Hole hole;  // bowels contents, tightness, number of copulations, number of creampies, etc. 
	// public Mouth mouth; 
	// public Wiener wiener;	
	
	@SuppressWarnings("unused")
	private PlayerCharacter(){}
	
	public PlayerCharacter(boolean defaultValues){
		super(defaultValues);
		if (defaultValues){
			label = "You";
			secondPerson = true;
			baseCharisma = 6;
			currentHealth = getMaxHealth();
		}
	}
	@Override
	protected IntArray getDefaultHealthTiers(){ return new IntArray(new int[]{10, 10, 10, 10}); }
	
	public static ObjectMap<Stat, Array<String>> getStatMap(){
		return statNameMap;
	}
	
	public void setJobClass(JobClass jobClass){
		for (Stat stat: Stat.values()){
			setStat(stat, jobClass.getBaseStat(stat));
		}
		this.jobClass = jobClass;
	}
	
	// this needs to consolidate logic with the getTechniques method
	public Array<Technique> getPossibleTechniques(){
		switch(stance){
			case OFFENSIVE:
				return getTechniques(Techniques.STRONG_ATTACK, Techniques.TEMPO_ATTACK, Techniques.RESERVED_ATTACK, Techniques.DUCK);
			case BALANCED:
				return getTechniques(Techniques.SPRING_ATTACK, Techniques.NEUTRAL_ATTACK, Techniques.CAUTIOUS_ATTACK, Techniques.DUCK);
			case DEFENSIVE:
				return getTechniques(Techniques.REVERSAL_ATTACK, Techniques.CAREFUL_ATTACK, Techniques.GUARD, Techniques.SECOND_WIND, Techniques.DUCK);
			case PRONE:
			case SUPINE:
				return getTechniques(Techniques.KIP_UP, Techniques.STAND_UP, Techniques.KNEE_UP, stance == Stance.PRONE ? Techniques.REST_FACE_DOWN : Techniques.REST);
			case KNEELING:
				return getTechniques(Techniques.STAND_UP);
			case DOGGY:
				return getTechniques(Techniques.RECEIVE, Techniques.STRUGGLE);
			case KNOTTED:
				return getTechniques(Techniques.RECEIVE_KNOT);
			case FELLATIO:
				return getTechniques(Techniques.OPEN_WIDE, Techniques.STRUGGLE);
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
			possibleTechniques.add(new Technique(technique, getStrength()));
		}
		return possibleTechniques;
	}
	
	public void refresh(){
		setStaminaToMax();
		setStabilityToMax();
		stance = Stance.BALANCED;
	}

	public enum Femininity {
		MALE,
		UNMASCULINE,
		EFFIMINATE,
		FEMALE,
		BITCH
	}
	
	public enum Bootyliciousness {
		
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
			case MAGIC: baseMagic = amount; break;
			case CHARISMA: baseCharisma = amount; break;
			default: 
		}
	}

	public void setCurrentHealth(Integer newHealth) {
		currentHealth = newHealth;	
	}
}

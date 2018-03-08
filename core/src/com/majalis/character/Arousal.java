package com.majalis.character;

import com.badlogic.gdx.utils.ObjectMap;
import com.majalis.technique.ClimaxTechnique.ClimaxType;

public class Arousal {
	private final ArousalType type;
	private ArousalLevel arousalLevel;
	private int lust;
	private int bottomLust;
	private int arousal;	
	private int numberOfClimaxes;
	
	@SuppressWarnings("unused")
	private Arousal() { this(ArousalType.DEFAULT); }
	
	protected Arousal(ArousalType type) {
		this.type = type;
		arousalLevel = ArousalLevel.FLACCID;
	}
	
	public enum ArousalType {
		DEFAULT,
		PLAYER,
		GOBLIN,
		OGRE,
		WEREWOLF, // EDGING is knot
		QUETZAL,
		GOLEM,
		SEXLESS,
	}
	
	public enum ArousalLevel {
		FLACCID,
		SEMI_ERECT,
		ERECT,
		FULLY_AROUSED,
		EDGING,
		CLIMAX; // handjobs take longer to get to climax
		
		public ArousalLevel increase() { return this.ordinal() + 1 >= ArousalLevel.values().length ? this : ArousalLevel.values()[this.ordinal() + 1]; }
	}
	
	protected int getPhallusLevel() { return Math.min(arousalLevel.ordinal(), 2); }
	protected boolean isErect() { return arousalLevel.ordinal() > 1; }
	protected boolean isFullyAroused() { return arousalLevel.ordinal() > 2; }
	protected boolean isEdging() { return arousalLevel == ArousalLevel.EDGING; }
	protected boolean isClimax() { return arousalLevel == ArousalLevel.CLIMAX; }
	protected boolean isSuperEdging() { return arousalLevel == ArousalLevel.EDGING && arousal > 3; }

	// need to also know whether you're being aroused by creampie or not
	// this accepts a raw increase amount that's the "size" of the arousal increase, which is then modified by current lust and ArousalLevel - may also need additional information like type of Arousal (anal stimulation, oral stimulation, bottom, top, etc.)
	protected void increaseArousal(SexualExperience sex, ObjectMap<String, Integer> perks) {
		increaseArousal(new SexualExperience[]{sex}, perks);
	}
	
	public void increaseArousal(SexualExperience[] sexes, ObjectMap<String, Integer> perks) {
		if (type == ArousalType.SEXLESS) return;
		int climaxArousalAmount = 0;
		int arousalAmount = 0;
		int base = getBase();
		int analBottomMod = getAnalBottomMod(perks, base);
		int oralBottomMod = getOralBottomMod(perks, base);
		int topMod = getTopMod(perks, base);
		for (SexualExperience sex : sexes) {
			// currently does not count creampies or ejaculations
			climaxArousalAmount += sex.getAnalSex() * analBottomMod; // this should be doubled for penetration?
			climaxArousalAmount += sex.getAnal() * analBottomMod;
			climaxArousalAmount += sex.getCreampies() * analBottomMod;
			climaxArousalAmount += sex.getOralSex() * oralBottomMod;
			climaxArousalAmount += sex.getOral() * oralBottomMod;
			climaxArousalAmount += sex.getOralCreampies() * oralBottomMod;
			climaxArousalAmount += sex.getAnalSexTop() * topMod;
			climaxArousalAmount += sex.getOralSexTop() * topMod;
			climaxArousalAmount *= !sex.isCentaurSex() ? 1 : perks.get(Perk.EQUESTRIAN.toString(), 0) * .5 + 1;
			climaxArousalAmount *= !sex.isOgreSex() ? 1 : perks.get(Perk.SIZE_QUEEN.toString(), 0) * .5 + 1;
			climaxArousalAmount *= !sex.isBird() ? 1 : perks.get(Perk.CUCKOO_FOR_CUCKOO.toString(), 0) * .5 + 1;
			climaxArousalAmount *= !sex.isKnot() ? 1 : perks.get(Perk.BITCH.toString(), 0) * .5 + 1;				
			arousalAmount += sex.getAssBottomTeasing() * analBottomMod;
			arousalAmount += sex.getMouthBottomTeasing() * oralBottomMod;
			arousalAmount += sex.getAssTeasing() * topMod;
			arousalAmount += sex.getMouthTeasing() * topMod;
			arousalAmount *= !sex.isCentaurSex() ? 1 : perks.get(Perk.EQUESTRIAN.toString(), 0) * .5 + 1;
			arousalAmount *= !sex.isOgreSex() ? 1 : perks.get(Perk.SIZE_QUEEN.toString(), 0) * .5 + 1;
			arousalAmount *= !sex.isBird() ? 1 : perks.get(Perk.CUCKOO_FOR_CUCKOO.toString(), 0) * .5 + 1;
			arousalAmount *= !sex.isKnot() ? 1 : perks.get(Perk.BITCH.toString(), 0) * .5 + 1;				
					
			bottomLust += sex.getAssBottomTeasing() * analBottomMod;
			
			arousal += climaxArousalAmount;
			if (!isFullyAroused() || sex.isSex()) arousal += arousalAmount; 
		}
			
		modLust(arousalAmount + climaxArousalAmount);	
		
		if (arousal > (type == ArousalType.OGRE ? (isErect() ? 16 : 100) : type == ArousalType.PLAYER ? getLustArousalMod() : 16)) { 
			if (type != ArousalType.QUETZAL || !isEdging() || arousal > 32)
			increaseArousalLevel();
		}
	}
	
	public boolean isBottomReady() { return bottomLust >= 50; }
	
	private int getLustArousalMod() { return lust == 400 ? 12 : lust >= 300 ? 16 : lust >= 200 ? 20 : lust >= 100 ? 24 : 28; }
	
	protected void climax(ClimaxType climaxType, ObjectMap<String, Integer> perks) { // should reset ArousalLevel, arousal, and some portion of lust, unless there's some functional difference like for goblins - this may be called externally for encounter climaxes
		numberOfClimaxes++;
		arousalLevel = type == ArousalType.GOBLIN && numberOfClimaxes % 5 != 0 ? ArousalLevel.EDGING : type == ArousalType.QUETZAL ? ArousalLevel.EDGING : ArousalLevel.FLACCID;
		if (type != ArousalType.QUETZAL) arousal = 0;
		int base = getBase();
		int analBottomMod = getAnalBottomMod(perks, base);
		int oralBottomMod = getOralBottomMod(perks, base);
		int topMod = getTopMod(perks, base);
		if (climaxType == ClimaxType.ANAL_RECEPTIVE) bottomLust = 0;
		modLust((-25 + perks.get(Perk.EASY_TO_PLEASE.toString(), 0) * -5) * (climaxType == ClimaxType.ANAL_RECEPTIVE ? analBottomMod / (1 + perks.get(Perk.WEAK_TO_ANAL.toString(), 0)) : climaxType == ClimaxType.ORAL_RECEPTIVE ? oralBottomMod : topMod));
	} 
	
	private int getBase() { return type == ArousalType.PLAYER ? 2 : 4; }
	private int getAnalBottomMod(ObjectMap<String, Integer> perks, int base) { return (base + perks.get(Perk.ANAL_ADDICT.toString(), 0) + perks.get(Perk.COCK_LOVER.toString(), 0) / 3) * (1 + perks.get(Perk.WEAK_TO_ANAL.toString(), 0)); }
	private int getOralBottomMod(ObjectMap<String, Integer> perks, int base) { return base + perks.get(Perk.MOUTH_MANIAC.toString(), 0) + perks.get(Perk.COCK_LOVER.toString(), 0) / 3; }
	private int getTopMod(ObjectMap<String, Integer> perks, int base) { return base + perks.get(Perk.TOP.toString(), 0); }
	
	private void modLust(int mod) {
		lust += mod;
		lust = Math.max(Math.min(lust, 400), 0);
	}
	
	private void increaseArousalLevel() {
		arousalLevel = arousalLevel.increase();
		arousal = 0;
	}
	protected void setArousalLevel(ArousalLevel newArousalLevel) { arousalLevel = newArousalLevel; }

	protected int getLust() { return lust / 4; }

	// need a way to tell 
	public String getCurrentState() {
		switch(arousalLevel) {
			case CLIMAX: return "Edging";
			case EDGING:  return "Edging";
			case ERECT: return "Erect";
			case FLACCID: return "Flaccid";
			case FULLY_AROUSED: return "FullyAroused";
			case SEMI_ERECT: return "SemiErect";
			default: return "";			
		}
	}	
}

package com.majalis.character;

import com.badlogic.gdx.utils.ObjectMap;
import com.majalis.technique.ClimaxTechnique.ClimaxType;

public class Arousal {
	private final ArousalType type;
	private ArousalLevel arousalLevel;
	private int lust;
	private int arousal;	
	
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
		SEXLESS
	}
	
	public enum ArousalLevel {
		FLACCID,
		SEMI_ERECT,
		ERECT,
		EDGING,
		CLIMAX; // handjobs take longer to get to climax
		
		public ArousalLevel increase() { return this.ordinal() + 1 >= ArousalLevel.values().length ? this : ArousalLevel.values()[this.ordinal() + 1]; }
	}
	
	protected int getPhallusLevel() { return Math.min(arousalLevel.ordinal(), 2); }
	protected boolean isErect() { return arousalLevel.ordinal() > 1; }
	protected boolean isClimax() { return arousalLevel == ArousalLevel.CLIMAX; }
	protected boolean isEdging() { return arousalLevel == ArousalLevel.EDGING; }
	protected boolean isSuperEdging() { return arousalLevel == ArousalLevel.EDGING && arousal > 3; }
	
	protected void increaseArousal(int increaseAmount, ObjectMap<String, Integer> perks, ClimaxType climaxType) {
		increaseArousal(increaseAmount, perks, climaxType, false);
	}
	
	private int getTypeArousalMod(ObjectMap<String, Integer> perks, ClimaxType climaxType) {
		int base = type == ArousalType.PLAYER ? 2 : 4;
		switch (climaxType) {
			case ANAL_RECEPTIVE:
				return (base + perks.get(Perk.ANAL_ADDICT.toString(), 0) + perks.get(Perk.COCK_LOVER.toString(), 0) / 3) * (1 + perks.get(Perk.WEAK_TO_ANAL.toString(), 0));
			case ORAL_RECEPTIVE:
				return base + perks.get(Perk.MOUTH_MANIAC.toString(), 0) + perks.get(Perk.COCK_LOVER.toString(), 0) / 3;
			case NULL:
			case ORAL:
			case ANAL:
			case BACKWASH:
			case FACIAL:
			default:
				return base;
		}
	}
	
	// need to also know whether you're being aroused by creampie or not
	// this accepts a raw increase amount that's the "size" of the arousal increase, which is then modified by current lust and ArousalLevel - may also need additional information like type of Arousal (anal stimulation, oral stimulation, bottom, top, etc.)
	protected void increaseArousal(int increaseAmount, ObjectMap<String, Integer> perks, ClimaxType climaxType, boolean causesClimax) {
		if (type == ArousalType.SEXLESS) return;
		int modArousalAmount = increaseAmount * getTypeArousalMod(perks, climaxType);		
		// multiple increaseAmount by some value, baseline 4, reduced or increased by perks and the type
		if (causesClimax || !isErect()) arousal += modArousalAmount; 
			
		modLust(modArousalAmount);	
		
		if (arousal > (type == ArousalType.OGRE ? (isErect() ? 16 : 100) : type == ArousalType.PLAYER ? getLustArousalMod() : 12)) { // && isUpgradeReady(typeOfArousal), which will check if the current ArousalLevel can have be upgraded by the type of arousal
			if (type != ArousalType.QUETZAL || !isEdging() || arousal > 32)
			increaseArousalLevel();
		}
	}
	
	private int getLustArousalMod() { return lust == 400 ? 12 : lust >= 300 ? 16 : lust >= 200 ? 20 : lust >= 100 ? 24 : 28; }
	
	protected void climax(ClimaxType climaxType) { // should reset ArousalLevel, arousal, and some portion of lust, unless there's some functional difference like for goblins - this may be called externally for encounter climaxes
		arousalLevel = type == ArousalType.GOBLIN ? ArousalLevel.EDGING : type == ArousalType.QUETZAL ? ArousalLevel.EDGING : ArousalLevel.FLACCID;
		if (type != ArousalType.QUETZAL) arousal = 0;
		modLust(-100);		
	} 
	
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
}

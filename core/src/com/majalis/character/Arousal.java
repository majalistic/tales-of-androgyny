package com.majalis.character;

import com.majalis.technique.ClimaxTechnique.ClimaxType;

public class Arousal {
	private final ArousalType type;
	private ArousalLevel arousalLevel;
	
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
	
	//private int lust;
	private int arousal;	
	
	protected int getPhallusLevel() { return Math.min(arousalLevel.ordinal(), 2); }
	protected boolean isErect() { return arousalLevel.ordinal() > 1; }
	protected boolean isClimax() { return arousalLevel == ArousalLevel.CLIMAX; }
	protected boolean isEdging() { return arousalLevel == ArousalLevel.EDGING; }
	protected boolean isSuperEdging() { return arousalLevel == ArousalLevel.EDGING && arousal > 3; }
	
	protected void increaseArousal(int increaseAmount) {
		increaseArousal(increaseAmount, false);
	}
	// this accepts a raw increase amount that's the "size" of the arousal increase, which is then modified by current lust and ArousalLevel - may also need additional information like type of Arousal (anal stimulation, oral stimulation, bottom, top, etc.)
	protected void increaseArousal(int increaseAmount, boolean causesClimax) {
		if (type == ArousalType.SEXLESS) return;
		if (causesClimax || !isErect()) arousal += increaseAmount; // isArousabale(typeOfArousal), which will check if the current ArousalLevel can have its arousal increased by the type of arousal
		if (arousal > (type == ArousalType.OGRE ? (isErect() ? 4 : 25) : 3)) { // && isUpgradeReady(typeOfArousal), which will check if the current ArousalLevel can have be upgraded by the type of arousal
			if (type != ArousalType.QUETZAL || !isEdging() || arousal > 8)
			increaseArousalLevel();
		}
	}
	
	protected void climax(ClimaxType climaxType) { // should reset ArousalLevel, arousal, and some portion of lust, unless there's some functional difference like for goblins - this may be called externally for encounter climaxes
		arousalLevel = type == ArousalType.GOBLIN ? ArousalLevel.EDGING : type == ArousalType.QUETZAL ? ArousalLevel.EDGING : ArousalLevel.FLACCID;
		if (type != ArousalType.QUETZAL) arousal = 0;
		//lust = 0;
	} 
	
	private void increaseArousalLevel() {
		arousalLevel = arousalLevel.increase(); // if it's now climax, call climax?
		arousal = 0;
	}
	protected void setArousalLevel(ArousalLevel newArousalLevel) { arousalLevel = newArousalLevel; }
	
}

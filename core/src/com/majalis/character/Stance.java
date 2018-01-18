package com.majalis.character;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.graphics.Texture;
import com.majalis.asset.AssetEnum;
import com.majalis.technique.ClimaxTechnique.ClimaxType;

public enum Stance {
	BALANCED (AssetEnum.BALANCED),
	DEFENSIVE (AssetEnum.DEFENSIVE),
	OFFENSIVE (AssetEnum.OFFENSIVE),
	BLITZ (AssetEnum.BLITZ),
	COUNTER (AssetEnum.COUNTER),
	STONEWALL (AssetEnum.BALANCED),
	PRONE (StanceType.INCAPACITATED, AssetEnum.PRONE, false, false, true),
	SUPINE (StanceType.INCAPACITATED, AssetEnum.SUPINE, false, false, true),
	HANDS_AND_KNEES (StanceType.INCAPACITATED, AssetEnum.HANDS_AND_KNEES, false, false, true), // need a new asset
	KNEELING (AssetEnum.KNEELING, false, true, true),
	AIRBORNE (AssetEnum.AIRBORNE, true, false, false), 
	CASTING (AssetEnum.CASTING),
	ITEM (AssetEnum.ITEM), 
	SEDUCTION(AssetEnum.SEDUCTION),
	
	GROUND_WRESTLE (AssetEnum.GROUND_WRESTLE),
	GROUND_WRESTLE_FACE_DOWN (AssetEnum.PRONE),
	GROUND_WRESTLE_FACE_UP (AssetEnum.SUPINE),
	WRAPPED(AssetEnum.WRAPPED),
	FULL_NELSON (AssetEnum.FULL_NELSON), 
	DOGGY (StanceType.ANAL, AssetEnum.DOGGY), 
	PRONE_BONE (StanceType.ANAL, AssetEnum.PRONEBONE),
	ANAL (StanceType.ANAL, AssetEnum.ANAL), 
	STANDING (StanceType.ANAL, AssetEnum.STANDING),
	HANDY (StanceType.HANDJOB, AssetEnum.HANDY),
	COWGIRL (StanceType.ANAL, AssetEnum.COWGIRL),
	REVERSE_COWGIRL (StanceType.ANAL, AssetEnum.REVERSE_COWGIRL),
	KNOTTED (StanceType.ANAL, AssetEnum.KNOTTED), 
	FELLATIO (StanceType.ORAL, AssetEnum.FELLATIO), 
	MOUTH_KNOTTED(StanceType.ORAL, AssetEnum.FELLATIO),
	FACEFUCK (StanceType.ORAL, AssetEnum.FACEFUCK),
	OUROBOROS (StanceType.ORAL, AssetEnum.OUROBOROS),
	OVIPOSITION (StanceType.ANAL, AssetEnum.KNOTTED),
	
	FACE_SITTING(StanceType.FACESIT, AssetEnum.FACE_SITTING),
	SIXTY_NINE(StanceType.ORAL, AssetEnum.SIXTY_NINE),
	HOLDING(AssetEnum.FULL_NELSON),
	CRUSHING(StanceType.ANAL, AssetEnum.FULL_NELSON),
	
	WRAPPED_BOTTOM(AssetEnum.WRAPPED),
	FULL_NELSON_BOTTOM (AssetEnum.FULL_NELSON), 
	DOGGY_BOTTOM (StanceType.ANAL_BOTTOM, AssetEnum.DOGGY), 
	PRONE_BONE_BOTTOM (StanceType.ANAL_BOTTOM, AssetEnum.PRONEBONE),
	ANAL_BOTTOM (StanceType.ANAL_BOTTOM, AssetEnum.ANAL), 
	STANDING_BOTTOM (StanceType.ANAL_BOTTOM, AssetEnum.STANDING),
	HANDY_BOTTOM (StanceType.HANDJOB_BOTTOM, AssetEnum.HANDY),
	COWGIRL_BOTTOM (StanceType.ANAL_BOTTOM, AssetEnum.COWGIRL),
	REVERSE_COWGIRL_BOTTOM (StanceType.ANAL_BOTTOM, AssetEnum.REVERSE_COWGIRL),
	KNOTTED_BOTTOM (StanceType.ANAL_BOTTOM, AssetEnum.KNOTTED), 
	FELLATIO_BOTTOM (StanceType.ORAL_BOTTOM, AssetEnum.FELLATIO), 
	MOUTH_KNOTTED_BOTTOM(StanceType.ORAL_BOTTOM, AssetEnum.FELLATIO),
	FACEFUCK_BOTTOM (StanceType.ORAL_BOTTOM, AssetEnum.FACEFUCK),
	OUROBOROS_BOTTOM (StanceType.ORAL_BOTTOM, AssetEnum.OUROBOROS),
	OVIPOSITION_BOTTOM (StanceType.ANAL_BOTTOM, AssetEnum.KNOTTED),
	
	FACE_SITTING_BOTTOM (StanceType.FACESIT_BOTTOM, AssetEnum.FACE_SITTING),
	SIXTY_NINE_BOTTOM (StanceType.ORAL_BOTTOM, AssetEnum.SIXTY_NINE),
	HELD(AssetEnum.SPREAD),
	SPREAD(StanceType.ANAL_BOTTOM, AssetEnum.SPREAD),
	PENETRATED(StanceType.ANAL_BOTTOM, AssetEnum.PENETRATED),
	
	ERUPT (AssetEnum.ERUPT), 
	NULL (AssetEnum.NULL_STANCE), 
	;
	// need to create: boolean anal, boolean oral, boolean method erotic, boolean incapacitated
	private final AssetEnum asset;
	private final StanceType type;
	private final boolean receivesHighAttacks;
	private final boolean receivesMediumAttacks;
	private final boolean receivesLowAttacks;
	
	private Stance(AssetEnum asset) {
		this(StanceType.NORMAL, asset, true, true, true);
	}
	
	private Stance(StanceType type, AssetEnum asset) {
		this(type, asset, true, true, true);
	}
	
	private Stance(AssetEnum asset, boolean receivesHigh, boolean receivesMedium, boolean receivesLow) {
		this(StanceType.NORMAL, asset, receivesHigh, receivesMedium, receivesLow);
	}
	
	private Stance(StanceType type, AssetEnum asset, boolean receivesHigh, boolean receivesMedium, boolean receivesLow) {
		this.type = type;
		this.asset = asset;
		receivesHighAttacks = receivesHigh;
		receivesMediumAttacks = receivesMedium;
		receivesLowAttacks = receivesLow;
	}
		
	public String getLabel() {
		switch (this) {
			case ANAL: return "Missionary";
			case ANAL_BOTTOM: return "Missionary (Receptive)";
			case COWGIRL_BOTTOM: return "Riding Cowgirl";
			case DOGGY: return "Doggy Style";
			case DOGGY_BOTTOM: return "Doggy Style (Receptive)";
			case FACEFUCK_BOTTOM: return "Facefucked";
			case FACE_SITTING_BOTTOM: return "Face Sat On";
			case FELLATIO_BOTTOM: return "Fellatio (Receptive)";
			case FULL_NELSON_BOTTOM: return "Full Nelsoned";
			case HANDY_BOTTOM: return "Giving Handy";
			case KNOTTED: return "Knotting";
			case KNOTTED_BOTTOM: return "Knotted";
			case OUROBOROS_BOTTOM: return "Upside Down";
			case OVIPOSITION: return "Ovipositing";
			case OVIPOSITION_BOTTOM: return "Oviposited";
			case PRONE_BONE_BOTTOM: return "Prone Bone (Receptive)";
			case REVERSE_COWGIRL_BOTTOM: return "Riding Reverse Cowgirl";
			case SIXTY_NINE_BOTTOM: return "Sixty Nine (Bottom)";
			case STANDING: return "Standing Anal";
			case STANDING_BOTTOM: return "Standing Anal (Receptive)";
			case GROUND_WRESTLE_FACE_UP:
			case GROUND_WRESTLE_FACE_DOWN:
			case GROUND_WRESTLE: return "Wrestling";
			default: break;
		}
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
	
	public AssetDescriptor<Texture> getTexture() { return asset.getTexture(); }
	public boolean isErotic() { return isEroticReceptive() || isEroticPenetration(); }	
	public boolean isEroticReceptive() { return type == StanceType.ANAL_BOTTOM || type == StanceType.ORAL_BOTTOM || type == StanceType.HANDJOB_BOTTOM || type == StanceType.FACESIT_BOTTOM; }
	public boolean isEroticPenetration() { return type == StanceType.ANAL || type == StanceType.ORAL || type == StanceType.HANDJOB || type == StanceType.FACESIT; }	
	public boolean isIncapacitating() { return type == StanceType.INCAPACITATED; }
	public boolean isAnalReceptive() { return type == StanceType.ANAL_BOTTOM; }
	public boolean isOralReceptive() { 	return type == StanceType.ORAL_BOTTOM; }	
	public boolean isAnalPenetration() { return type == StanceType.ANAL; } 
	public boolean isOralPenetration() { return type == StanceType.ORAL; }	
	public boolean isIncapacitatingOrErotic() { return isErotic() || isIncapacitating(); }	
	public boolean receivesHighAttacks() { return receivesHighAttacks; }
	public boolean receivesMediumAttacks() { return receivesMediumAttacks; }
	public boolean receivesLowAttacks() { return receivesLowAttacks; }
	public boolean hasLearnableSkills() { return this == BALANCED || this == OFFENSIVE || this == DEFENSIVE || this == KNEELING; }
	public ClimaxType getClimaxType() { return isAnalReceptive() ? ClimaxType.ANAL_RECEPTIVE : isOralReceptive() ? ClimaxType.ORAL_RECEPTIVE : isAnalPenetration() ? ClimaxType.ANAL : isOralPenetration() ? ClimaxType.ORAL : this == Stance.HANDY ? ClimaxType.FACIAL : isErotic() ? ClimaxType.BACKWASH : ClimaxType.NULL; }
	
	private enum StanceType {
		ANAL,
		ANAL_BOTTOM,
		ORAL,
		ORAL_BOTTOM,
		HANDJOB,
		HANDJOB_BOTTOM,
		FACESIT,
		FACESIT_BOTTOM,
		INCAPACITATED,
		NORMAL
	}

	public boolean isNull() {
		return this == NULL;
	}	
}
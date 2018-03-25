package com.majalis.asset;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.utils.ObjectMap;

public enum AnimationEnum {
	BUTTBANG(AssetEnum.GAME_OVER_ANIMATION),
	HARPY(AssetEnum.HARPY_ANIMATION),
	BRIGAND(AssetEnum.BRIGAND_ANIMATION),
	CENTAUR(AssetEnum.CENTAUR_ANIMATION),
	UNICORN(AssetEnum.CENTAUR_ANIMATION), 
	ORC(AssetEnum.ORC_ANIMATION),
	WEREWOLF(AssetEnum.WEREWOLF_ANIMATION),
	ORC_PRONE_BONE(AssetEnum.ORC_PRONE_BONE_ANIMATION),
	GOBLIN(AssetEnum.GOBLIN_ANIMATION),
	GOBLIN_MALE(AssetEnum.GOBLIN_ANIMATION),
	TRUDY(AssetEnum.TRUDY_SPRITE_ANIMATION),
	NULL(AssetEnum.NULL_ANIMATION)	
	;
	private static final ObjectMap<AssetEnum, AnimatedActorFactory> factoryMap = new ObjectMap<AssetEnum, AnimatedActorFactory>();
	private final AssetEnum animationToken;
	private AnimationEnum(AssetEnum animationToken) {
		this.animationToken = animationToken;
	}
	
	public AssetEnum getAnimationToken() { return animationToken; }
	
	public AnimatedActor getAnimation(AssetManager assetManager) {
		AnimatedActorFactory factory = factoryMap.get(animationToken);
		if (factory == null) {
			factory = assetManager.get(animationToken.getAnimation());
			factoryMap.put(animationToken, factory);
		}			
		AnimatedActor animation = factory.getInstance();
		if (this == NULL || this == BUTTBANG) return animation;
		if (this == HARPY) {
			animation.setSkeletonPosition(900, 550);
		}
		else if (this == BRIGAND) {
			animation.setSkeletonPosition(900, 450);
		}
		else if (this == GOBLIN || this == GOBLIN_MALE) {
			animation.setSkeletonPosition(1000, 350);
		}
		else if (this == ORC_PRONE_BONE) {
			animation.setSkeletonPosition(985, 310);
		}
		else {
			animation.setSkeletonPosition(1000, 550);
		}
		
		if (this == CENTAUR) {
			animation.setSkeletonSkin("BrownCentaur");
		}
		else if (this == UNICORN) {
			animation.setSkeletonSkin("WhiteUnicorn");
		}
		else if (this == GOBLIN) {
			animation.setSkeletonSkin("Femme");
		}
		else if (this == GOBLIN_MALE) {
			animation.setSkeletonSkin("Homme");
		}
		if (this == ORC_PRONE_BONE) {
			animation.setAnimation(0, "SlowMed", true);
		}
		else {
			animation.setAnimation(0, "Idle Erect", true);
		}
		return animation;
	}
}
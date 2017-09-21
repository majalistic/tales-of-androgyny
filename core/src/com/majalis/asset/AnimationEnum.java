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
	;
	private static final ObjectMap<AssetEnum, AnimatedActorFactory> factoryMap = new ObjectMap<AssetEnum, AnimatedActorFactory>();
	private final AssetEnum animationToken;
	private AnimationEnum(AssetEnum animationToken) {
		this.animationToken = animationToken;
	}
	public AnimatedActor getAnimation(AssetManager assetManager) {
		AnimatedActorFactory factory = factoryMap.get(animationToken);
		if (factory == null) {
			factory = assetManager.get(animationToken.getAnimation());
			factoryMap.put(animationToken, factory);
		}			
		AnimatedActor animation = factory.getInstance();
		if (this == BUTTBANG) return animation;
		if (this == AnimationEnum.HARPY) {
			animation.setSkeletonPosition(900, 550);
		}
		else if (this == AnimationEnum.BRIGAND) {
			animation.setSkeletonPosition(900, 450);
		}
		else {
			animation.setSkeletonPosition(1000, 550);
		}
		
		if (this == AnimationEnum.CENTAUR) {
			animation.setSkeletonSkin("BrownCentaur");
		}
		else if (this == AnimationEnum.UNICORN) {
			animation.setSkeletonSkin("WhiteUnicorn");
		}
		animation.setAnimation(0, "Idle Erect", true);
		return animation;
	}
}
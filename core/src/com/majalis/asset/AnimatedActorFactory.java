package com.majalis.asset;

import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.SkeletonMeshRenderer;

public class AnimatedActorFactory {
	
	private final SkeletonMeshRenderer renderer;
	private final SkeletonData skeletonData;
	private final float timeScale;
	private final boolean enemy;

	public AnimatedActorFactory(SkeletonMeshRenderer renderer, SkeletonData skeletonData, float timeScale, boolean enemy) {
		this.renderer = renderer;
		this.skeletonData = skeletonData;
		this.timeScale = timeScale;
		this.enemy = enemy;
	}
	
	public AnimatedActor getInstance() {
		return new AnimatedActor(renderer, skeletonData, timeScale, enemy);
	}
}

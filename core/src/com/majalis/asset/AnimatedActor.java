package com.majalis.asset;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.SkeletonMeshRenderer;

public class AnimatedActor extends Actor {
	private transient SkeletonMeshRenderer renderer;
	private transient AnimationState state;
	private transient Skeleton skeleton;
	
	public AnimatedActor(SkeletonMeshRenderer renderer, SkeletonData skeletonData, float timeScale, boolean enemy) {
		this.renderer = renderer;
		
		skeleton = new Skeleton(skeletonData); // Skeleton holds skeleton state (bone positions, slot attachments, etc).
		
		AnimationStateData stateData = new AnimationStateData(skeletonData); // Defines mixing (crossfading) between animations.

		state = new AnimationState(stateData); // Holds the animation state for a skeleton (current animation, time, etc).
		state.setTimeScale(timeScale); 
		if (!enemy) {
			try {
				state.setAnimation(0, "Splurt", false);
				state.addAnimation(0, "Idle", true, 5f);
			}
			catch(Exception ex){}
		}
	}
	
	public void setAnimation(int track, String animationName, boolean loop) { state.setAnimation(track, animationName, loop); }
	public void addAnimation(int track, String animationName, boolean loop, float delay) { state.addAnimation(track, animationName, loop, delay); }
	public void setSkeletonPosition(float x, float y) { skeleton.setPosition(x, y); }
	public void setSkeletonSkin(String skin) { skeleton.setSkin(skin); }
	
	@Override
    public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		state.update(Gdx.graphics.getDeltaTime());
		state.apply(skeleton);
		skeleton.updateWorldTransform();
		int blendSrc = batch.getBlendSrcFunc();
		int blendDst = batch.getBlendDstFunc();
		renderer.draw((PolygonSpriteBatch)batch, skeleton);
		
		batch.setBlendFunction(blendSrc, blendDst);
	}
	
	@Override
	public void setPosition(float x, float y) {
		float deltaX = x - getX();
		float deltaY = y - getY();
		setSkeletonPosition(skeleton.getX() + deltaX, skeleton.getY() + deltaY);
		super.setPosition(x, y);
	}	
}
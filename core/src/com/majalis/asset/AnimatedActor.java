package com.majalis.asset;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.SkeletonJson;
import com.esotericsoftware.spine.SkeletonMeshRenderer;

public class AnimatedActor extends Actor {
	private TextureAtlas atlas;
	private SkeletonMeshRenderer renderer;
	private AnimationState state;
	private Skeleton skeleton;
	
	public AnimatedActor(String atlasPath, String jsonPath) {
		renderer = new SkeletonMeshRenderer();
		renderer.setPremultipliedAlpha(true);
		atlas = new TextureAtlas(Gdx.files.internal(atlasPath));
		SkeletonJson json = new SkeletonJson(atlas); // This loads skeleton JSON data, which is stateless.
		json.setScale(1f);
		SkeletonData skeletonData = json.readSkeletonData(Gdx.files.internal(jsonPath));
		
		skeleton = new Skeleton(skeletonData); // Skeleton holds skeleton state (bone positions, slot attachments, etc).
				
		AnimationStateData stateData = new AnimationStateData(skeletonData); // Defines mixing (crossfading) between animations.

		state = new AnimationState(stateData); // Holds the animation state for a skeleton (current animation, time, etc).
		state.setTimeScale(1f); 

		// Queue animations on tracks 0 and 1.
		state.setAnimation(0, "Splurt", false);
		state.addAnimation(0, "Idle", true, 5f);
	}
	
	@Override
    public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		state.update(Gdx.graphics.getDeltaTime());
		state.apply(skeleton);
		skeleton.updateWorldTransform();
		renderer.draw((PolygonSpriteBatch)batch, skeleton);
	}
	
	public void setSkeletonPosition(float x, float y) {
		skeleton.setPosition(x, y);
	}
}
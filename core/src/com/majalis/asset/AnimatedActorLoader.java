package com.majalis.asset;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.SynchronousAssetLoader;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.SkeletonJson;
import com.esotericsoftware.spine.SkeletonMeshRenderer;

public class AnimatedActorLoader extends SynchronousAssetLoader<AnimatedActorFactory, AnimatedActorLoader.AnimatedActorParameter> {

	public AnimatedActorLoader(FileHandleResolver resolver) {
		super(resolver);
	}
	
	@Override
	public AnimatedActorFactory load(AssetManager assetManager, String fileName, FileHandle file, AnimatedActorParameter parameter) {
		return getActor(fileName, file, parameter);
	}
	
	private AnimatedActorFactory getActor(String fileName, FileHandle file, AnimatedActorParameter parameter) {
		TextureAtlas atlas = new TextureAtlas(file);
		SkeletonMeshRenderer renderer = new SkeletonMeshRenderer();
		renderer.setPremultipliedAlpha(true);
		
		if (parameter == null) parameter = new AnimatedActorParameter();
		
		SkeletonJson json = new SkeletonJson(atlas); // This loads skeleton JSON data, which is stateless.
		json.setScale(parameter.scale);
		String jsonPath = fileName.replace(".atlas", ".json");
		SkeletonData skeletonData = json.readSkeletonData(resolve(jsonPath));
		return new AnimatedActorFactory(renderer, skeletonData, parameter.timeScale, parameter.enemy);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, AnimatedActorParameter parameter) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static class AnimatedActorParameter extends AssetLoaderParameters<AnimatedActorFactory> {
		private final float scale;
		private final float timeScale;
		private final boolean enemy;
		
		public AnimatedActorParameter() {
			this(1, 1, false);
		}
		
		public AnimatedActorParameter(float scale, float timeScale, boolean enemy) {
			this.scale = scale;
			this.timeScale = timeScale;
			this.enemy = enemy;
		}
		
	}


}

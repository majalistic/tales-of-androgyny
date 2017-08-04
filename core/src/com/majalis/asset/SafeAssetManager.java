package com.majalis.asset;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
/*
 * Loads assets synchronously if there's ever a failure to retrieve an asset.
 */
public class SafeAssetManager extends AssetManager {
	@Override
	public synchronized <T> T get (String fileName, Class<T> type) {
		T temp;
		try {
			temp = super.get(fileName, type);
		}
		catch (Exception ex) {
			load(fileName, type);
			finishLoading();
			temp = super.get(fileName, type);
		}
		return temp;
	}
	@SuppressWarnings("unchecked")
	@Override
	public synchronized <T> T get (AssetDescriptor<T> assetDescriptor) {
		T temp;
		String fileName = assetDescriptor.fileName;
		Class<T> type = assetDescriptor.type;
		try {
			temp = super.get(fileName, type);
		}
		catch (Exception ex) {
			load(fileName, type, assetDescriptor.params);
			finishLoading();
			temp = super.get(fileName, type);
		}
		return temp;
	}
}

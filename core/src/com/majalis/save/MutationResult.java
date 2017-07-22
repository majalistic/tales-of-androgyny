package com.majalis.save;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.graphics.Texture;
import com.majalis.asset.AssetEnum;

public class MutationResult {

	private final String text;
	private final int mod;
	private final MutationType type;
	public MutationResult(String text) {
		this(text, 0, MutationType.NONE);
	}
	
	public MutationResult(String text, int mod, MutationType type) {
		this.text = text;
		this.mod = mod;
		this.type = type;
	}
	
	public enum MutationType {
		HEALTH (AssetEnum.HEART),
		FOOD (AssetEnum.APPLE),
		EXP (AssetEnum.EXP),
		TIME (AssetEnum.TIME),
		GOLD (AssetEnum.GOLD),
		NONE (AssetEnum.NULL)
		;
		private final AssetDescriptor<Texture> texture;
		private MutationType(AssetEnum asset) {
			this.texture = asset.getTexture();
		}
		private AssetDescriptor<Texture> getTexture() { return texture; }
		
	}
	
	public String getText() {
		return text;
	}
	
	public int getMod() {
		return mod;
	}
	
	public MutationType getType() {
		return type;
	}

	public AssetDescriptor<Texture> getTexture() {
		return type.getTexture();
	}
}

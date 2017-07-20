package com.majalis.save;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.graphics.Texture;
import com.majalis.asset.AssetEnum;

public class MutationResult {

	private final String text;
	private final String mod;
	private final MutationType type;
	public MutationResult(String text) {
		this(text, "", MutationType.NONE);
	}
	
	public MutationResult(String text, String mod, MutationType type) {
		this.text = text;
		this.mod = mod;
		this.type = type;
	}
	
	public enum MutationType {
		HEALTH (AssetEnum.HEALTH_ICON_0),
		FOOD (AssetEnum.APPLE),
		EXP (AssetEnum.EXP),
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
	
	public String getMod() {
		return mod;
	}
	
	public MutationType getType() {
		return type;
	}

	public AssetDescriptor<Texture> getTexture() {
		return type.getTexture();
	}
}

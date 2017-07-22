package com.majalis.save;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.OrderedMap;
import com.majalis.asset.AssetEnum;

public class MutationResult {

	private final String text;
	private final int mod;
	private final MutationType type;
	
	@SuppressWarnings("unused")
	private MutationResult() { text = ""; mod = 0; type = MutationType.NONE; }
	
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
		public boolean canBeMinified() { return this != NONE; }
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
	// collapsed an array of results of different types to contain no more than 1 result per type, aside from the "NONE" type which remains unflattened
	public static Array<MutationResult> collapse(Array<MutationResult> results) {
		if (results.size == 0) return results;
		Array<MutationResult> collapsedResults = new Array<MutationResult>();
		OrderedMap<MutationType, Array<MutationResult>> resultMap = new OrderedMap<MutationType, Array<MutationResult>>();
		for (MutationResult result : results){
			MutationType type = result.getType();
			if (type == MutationType.NONE) { collapsedResults.add(result); continue; }
			Array<MutationResult> list = resultMap.get(type, new Array<MutationResult>());
			list.add(result);
			resultMap.put(type, list);
		}
		for (OrderedMap.Entry<MutationType, Array<MutationResult>> entry : resultMap) {
			int mod = 0;
			for (MutationResult result : entry.value) {
				mod += result.getMod();
			}
			collapsedResults.add(new MutationResult(entry.value.first().getText(), mod, entry.key));			
		}
		return collapsedResults;
	}
}

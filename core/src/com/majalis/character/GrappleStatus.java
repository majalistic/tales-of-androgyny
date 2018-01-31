package com.majalis.character;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;
import com.majalis.asset.AssetEnum;

public enum GrappleStatus {
	NULL (AssetEnum.NULL, AssetEnum.NULL),
	HOLD (AssetEnum.GRAPPLE_HOLD, AssetEnum.GRAPPLE_HOLD_INACTIVE),
	DOMINANT(AssetEnum.GRAPPLE_DOMINANT, AssetEnum.GRAPPLE_DOMINANT_INACTIVE),
	ADVANTAGE(AssetEnum.GRAPPLE_ADVANTAGE, AssetEnum.GRAPPLE_ADVANTAGE_INACTIVE),
	SCRAMBLE(AssetEnum.GRAPPLE_SCRAMBLE, AssetEnum.GRAPPLE_SCRAMBLE_INACTIVE),
	DISADVANTAGE(AssetEnum.GRAPPLE_DISADVANTAGE, AssetEnum.GRAPPLE_DISADVANTAGE_INACTIVE),
	SUBMISSION(AssetEnum.GRAPPLE_SUBMISSION, AssetEnum.GRAPPLE_SUBMISSION_INACTIVE),
	HELD(AssetEnum.GRAPPLE_HELD, AssetEnum.GRAPPLE_HELD_INACTIVE)
	;
	private final AssetEnum activeTexture;
	private final AssetEnum inactiveTexture;
	private GrappleStatus(AssetEnum activeTexture, AssetEnum inactiveTexture) {
		this.activeTexture = activeTexture;
		this.inactiveTexture = inactiveTexture;
	}
	protected GrappleStatus inverse() {
		return this == NULL ? NULL : GrappleStatus.values()[((GrappleStatus.values().length - 1) - this.ordinal()) + 1];
	}
	
	protected boolean isAdvantage() {
		return this != NULL && this.ordinal() < 4;
	}
		
	protected boolean isDisadvantage() {
		return this.ordinal() > 4;
	}
	
	protected GrappleStatus modifyGrappleStatus(GrappleType grapple) {
		switch (grapple) {
			case ADVANTAGE:
				return this == NULL ? SCRAMBLE : GrappleStatus.values()[Math.max(1, this.ordinal() - 1)];
			case PIN:
				return HOLD;
			case REVERSAL:
				return this == NULL ? SCRAMBLE : GrappleStatus.values()[Math.max(1, this.ordinal() - 3)];
			case BREAK:
				return NULL;
			case HOLD:
			case SUBMIT:
			default:
				return this == NULL ? SCRAMBLE : this;
		}
	}
	public Texture getActiveTexture(AssetManager assetManager) { return assetManager.get(activeTexture.getTexture()); }
	public Texture getInactiveTexture(AssetManager assetManager) { return assetManager.get(inactiveTexture.getTexture()); }
	public static Array<GrappleStatus> reverseValues() { 
		Array<GrappleStatus> values = new Array<GrappleStatus>(GrappleStatus.values());
		values.reverse();
		return values;
	}
}

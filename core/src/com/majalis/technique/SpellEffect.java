package com.majalis.technique;

public enum SpellEffect {
	DAMAGE, FIRE_DAMAGE, HEALING, NONE, ARMOR_REPAIR;

	public boolean isDamaging() { return this == DAMAGE || this == FIRE_DAMAGE; }
}

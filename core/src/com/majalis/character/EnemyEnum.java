package com.majalis.character;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.graphics.Texture;
import com.majalis.asset.AssetEnum;

public enum EnemyEnum {
	WERESLUT ("Wereslut", AssetEnum.WEREBITCH.getTexture()),
	HARPY ("Harpy", null, "animation/Harpy"),
	SLIME ("Slime", AssetEnum.SLIME.getTexture()),
	BRIGAND ("Brigand", AssetEnum.BRIGAND.getTexture(), "animation/skeleton"),
	CENTAUR ("Centaur", null, "animation/Centaur"),
	UNICORN ("Unicorn", null, "animation/Centaur"),
	GOBLIN ("Goblin", AssetEnum.GOBLIN.getTexture()), 
	ORC ("Orc", AssetEnum.ORC.getTexture()), 
	ADVENTURER ("Adventurer", AssetEnum.ADVENTURER.getTexture()),
	GOBLIN_MALE ("Goblin (Male)", AssetEnum.GOBLIN_MALE.getTexture()),
	OGRE ("Ogre", AssetEnum.OGRE.getTexture())
	;
	private final String text;
	private final AssetDescriptor<Texture> path;
	private final String animationPath;
    private EnemyEnum(final String text, final AssetDescriptor<Texture> path) { this(text, path, ""); }
    private EnemyEnum(final String text, final AssetDescriptor<Texture> path, final String animationPath) { this.text = text; this.path = path; this.animationPath = animationPath; }
    @Override
    public String toString() { return text; }	
    public AssetDescriptor<Texture> getTexture() { return path; }
    public String getAnimationPath() { return animationPath; }
	public boolean canProneBone() {
		return this == BRIGAND || this == GOBLIN || this == ORC || this == ADVENTURER || this == GOBLIN_MALE;
	}
}
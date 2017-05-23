package com.majalis.character;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.ObjectMap;
import com.majalis.asset.AssetEnum;
import com.majalis.character.AbstractCharacter.PhallusType;
import com.majalis.character.AbstractCharacter.PronounSet;
import com.majalis.character.Item.WeaponType;

public enum EnemyEnum {
	WERESLUT (new EnemyTemplate(WeaponType.Claw).setStrength(5).setAgility(5), "Wereslut", AssetEnum.WEREBITCH.getTexture()),
	HARPY (new EnemyTemplate(WeaponType.Talon).setStrength(4), "Harpy", null, "animation/Harpy"),
	SLIME (new EnemyTemplate(null).setStrength(2).setEndurance(4).setAgility(4), "Slime", AssetEnum.SLIME.getTexture()),
	BRIGAND (new EnemyTemplate(WeaponType.Gladius).setAgility(4), "Brigand", AssetEnum.BRIGAND.getTexture(), "animation/skeleton"),
	CENTAUR (new EnemyTemplate(WeaponType.Bow).setEndurance(4).setAgility(4).setPerception(5), "Centaur", null, "animation/Centaur"),
	UNICORN (new EnemyTemplate(WeaponType.Bow).setEndurance(4).setAgility(4).setPerception(5), "Unicorn", null, "animation/Centaur"),
	GOBLIN (new EnemyTemplate(WeaponType.Dagger).setStrength(4).setEndurance(4).setAgility(5), "Goblin", AssetEnum.GOBLIN.getTexture()), 
	GOBLIN_MALE (new EnemyTemplate(WeaponType.Dagger).setStrength(4).setEndurance(4).setAgility(5), "Goblin (Male)", AssetEnum.GOBLIN_MALE.getTexture()),
	ORC (new EnemyTemplate(WeaponType.Flail, 7, 5, 4, 3, 3, 3).setDefense(6).addHealth(10), "Orc", AssetEnum.ORC.getTexture()), 
	ADVENTURER (new EnemyTemplate(WeaponType.Axe, 4, 4, 4, 3, 4, 6).setDefense(6).addHealth(10).setMana(20), "Adventurer", AssetEnum.ADVENTURER.getTexture()),
	OGRE (new EnemyTemplate(WeaponType.Club, 8, 8, 4, 3, 3, 3).addHealth(20), "Ogre", AssetEnum.OGRE.getTexture()),
	BEASTMISTRESS (new EnemyTemplate(WeaponType.Claw).setStrength(6).setAgility(6).addHealth(10), "Beast Mistress", AssetEnum.BEASTMISTRESS.getTexture()), 
	;
	private final String text;
	private final AssetDescriptor<Texture> path;
	private final String animationPath;
	private final EnemyTemplate template;
	private EnemyEnum(EnemyTemplate template, final String text, final AssetDescriptor<Texture> path) { this(template, text, path, ""); }
    private EnemyEnum(EnemyTemplate template, final String text, final AssetDescriptor<Texture> path, final String animationPath) { this.template = template; this.text = text; this.path = path; this.animationPath = animationPath; }
    @Override
    public String toString() { return text; }	
    public AssetDescriptor<Texture> getTexture() { return path; }
    public String getPath() { return path != null ? path.fileName : null; }
    public String getBGPath() { return this == OGRE ? AssetEnum.FOREST_UP_BG.getPath() : this == CENTAUR || this == UNICORN ? AssetEnum.PLAINS_BG.getPath() : this == GOBLIN || this == GOBLIN_MALE ? AssetEnum.ENCHANTED_FOREST_BG.getPath() : AssetEnum.FOREST_BG.getPath(); } 
    public ObjectMap<String, Array<String>> getImagePaths() { 
    	ObjectMap<String, Array<String>> textureImagePaths = new ObjectMap<String, Array<String>>();
    	if (this == HARPY) { textureImagePaths.put(Stance.FELLATIO.toString(), new Array<String>(new String[]{AssetEnum.HARPY_FELLATIO_0.getPath(), AssetEnum.HARPY_FELLATIO_1.getPath(), AssetEnum.HARPY_FELLATIO_2.getPath(), AssetEnum.HARPY_FELLATIO_3.getPath()})); }
    	else if (this == SLIME) { textureImagePaths.put(Stance.DOGGY.toString(),new Array<String>(new String[]{AssetEnum.SLIME_DOGGY.getPath()})); }
    	else if (this == ORC) { textureImagePaths.put(Stance.PRONE_BONE.toString(),new Array<String>(new String[]{AssetEnum.ORC_PRONE_BONE.getPath()})); }
    	else if (this == GOBLIN) {
    		textureImagePaths.put(Stance.FACE_SITTING.toString(), new Array<String>(new String[]{AssetEnum.GOBLIN_FACE_SIT.getPath()}));
    		textureImagePaths.put(Stance.SIXTY_NINE.toString(), new Array<String>(new String[]{AssetEnum.GOBLIN_FACE_SIT.getPath()}));
    	}
    	return textureImagePaths; 
    }
    public PhallusType getPhallusType() { return this == BRIGAND || this == BEASTMISTRESS ? PhallusType.NORMAL : this == ADVENTURER ? PhallusType.SMALL : PhallusType.MONSTER; }
    public PronounSet getPronounSet() { return this == ADVENTURER || this == OGRE || this == GOBLIN_MALE ? PronounSet.MALE : PronounSet.FEMALE; }
    
    public String getAnimationPath() { return animationPath; }
	public boolean canProneBone() {
		return this == BRIGAND || this == GOBLIN || this == ORC || this == ADVENTURER || this == GOBLIN_MALE;
	}
	public int getStartingLust() { return this == UNICORN ? 20 : 0; }
	
	public int getStrength() { return template.getStrength(); }
	public int getEndurance() { return template.getEndurance(); }
	public int getAgility() { return template.getAgility(); }
	public int getPerception() { return template.getPerception(); }
	public int getMagic() { return template.getMagic(); }
	public int getCharisma() { return template.getCharisma(); }
	public int getDefense() { return template.getDefense(); }
	public IntArray getHealthTiers() { return template.getHealthTiers(); }
	public IntArray getManaTiers() { return template.getManaTiers(); }
	public WeaponType getWeaponType() { return template.getWeaponType(); }
	public boolean willFaceSit() { return this != CENTAUR && this != UNICORN; } 
	public boolean willArmorSunder() { return this == BRIGAND || this == ORC || this == ADVENTURER; }
	public boolean willParry() { return this == BRIGAND || this == ADVENTURER; }
	public boolean canBeRidden() { return this != SLIME && this != CENTAUR && this != UNICORN && this != BEASTMISTRESS; }
	
	private static class EnemyTemplate {
		private int strength;
		private int endurance;
		private int agility;
		private int perception;
		private int magic;
		private int charisma;
		private int defense;
		private WeaponType weaponType;
		private IntArray healthTiers;
		private IntArray manaTiers;
		
		private EnemyTemplate(WeaponType weaponType) {
			this(weaponType, 3, 3, 3, 3, 3, 3);
		}
		
		private EnemyTemplate(WeaponType weaponType, int strength, int endurance, int agility, int perception, int magic, int charisma) {
			this.weaponType = weaponType;
			this.strength = strength;
			this.endurance = endurance;
			this.agility = agility;
			this.perception = perception;
			this.magic = magic;
			this.charisma = charisma;
			defense = 4;
			healthTiers = new IntArray(new int[]{10, 10, 10, 10});
			manaTiers = new IntArray(new int[]{0});
		}
		
		private int getStrength() { return strength; }
		private int getEndurance() { return endurance; }
		private int getAgility() { return agility; }
		private int getPerception() { return perception; }
		private int getMagic() { return magic; }
		private int getCharisma() { return charisma; }
		private int getDefense() { return defense; }
		private IntArray getHealthTiers() { return healthTiers; }
		private IntArray getManaTiers() { return manaTiers; }
		
		private EnemyTemplate setStrength(int strength) { this.strength = strength; return this; }
		private EnemyTemplate setEndurance(int endurance) { this.endurance = endurance; return this; }
		private EnemyTemplate setAgility(int agility) { this.agility = agility; return this; }
		private EnemyTemplate setPerception(int perception) { this.perception = perception;  return this; }
		@SuppressWarnings("unused") private EnemyTemplate setMagic(int magic) { this.magic = magic;  return this; }
		@SuppressWarnings("unused") private EnemyTemplate setCharisma(int charisma) { this.charisma = charisma;  return this; }
		private EnemyTemplate setDefense(int defense) { this.defense = defense; return this; }
		private EnemyTemplate addHealth(int health) { this.healthTiers.add(health); return this; }
		private EnemyTemplate setMana(int mana) { this.manaTiers = new IntArray(new int[]{mana}); return this; }
		
		private WeaponType getWeaponType() { return weaponType; }
	}
}
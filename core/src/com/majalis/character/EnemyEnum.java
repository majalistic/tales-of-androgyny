package com.majalis.character;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.ObjectMap;
import com.majalis.asset.AnimatedActor;
import com.majalis.asset.AnimationEnum;
import com.majalis.asset.AssetEnum;
import com.majalis.character.AbstractCharacter.PhallusType;
import com.majalis.character.AbstractCharacter.PronounSet;
import com.majalis.character.Item.WeaponType;
@SuppressWarnings("unchecked")
public enum EnemyEnum {
	
	WERESLUT (new EnemyTemplate(WeaponType.Claw).setStrength(5).setAgility(5), "Wereslut", AssetEnum.WEREBITCH.getTexture()),
	HARPY (new EnemyTemplate(WeaponType.Talon).setStrength(4), "Harpy", null, AnimationEnum.HARPY),
	SLIME (new EnemyTemplate(null).setStrength(2).setEndurance(4).setAgility(4), "Slime", AssetEnum.SLIME.getTexture()),
	BRIGAND (new EnemyTemplate(WeaponType.Gladius).setAgility(4), "Brigand", null, AnimationEnum.BRIGAND),
	CENTAUR (new EnemyTemplate(WeaponType.Bow).setEndurance(4).setAgility(4).setPerception(5), "Centaur", null, AnimationEnum.CENTAUR),
	UNICORN (new EnemyTemplate(WeaponType.Bow).setEndurance(4).setAgility(4).setPerception(5), "Unicorn", null, AnimationEnum.UNICORN),
	GOBLIN (new EnemyTemplate(WeaponType.Dagger).setStrength(4).setEndurance(4).setAgility(5), "Goblin", AssetEnum.GOBLIN.getTexture()), 
	GOBLIN_MALE (new EnemyTemplate(WeaponType.Dagger).setStrength(4).setEndurance(4).setAgility(5), "Goblin (Male)", AssetEnum.GOBLIN_MALE.getTexture()),
	ORC (new EnemyTemplate(WeaponType.Flail, 7, 6, 4, 3, 3, 3).setDefense(6).addHealth(10), "Orc", AssetEnum.ORC.getTexture()), 
	ADVENTURER (new EnemyTemplate(WeaponType.Axe, 4, 4, 4, 3, 4, 6).setDefense(6).addHealth(10).setMana(20), "Adventurer", AssetEnum.ADVENTURER.getTexture()),
	OGRE (new EnemyTemplate(WeaponType.Club, 8, 8, 4, 3, 3, 3).addHealth(20), "Ogre", AssetEnum.OGRE.getTexture()),
	BEASTMISTRESS (new EnemyTemplate(WeaponType.Claw).setStrength(6).setAgility(8).setEndurance(5).addHealth(10), "Beast Mistress", AssetEnum.BEASTMISTRESS.getTexture()),
	SPIDER (new EnemyTemplate(WeaponType.Claw).setStrength(6).setAgility(5).setEndurance(5).setHealth(new IntArray(new int[]{20, 20, 20, 20})), "Arachne", AssetEnum.SPIDER.getTexture()), 
	GOLEM (new EnemyTemplate(null, 7, 7, 4, 3, 3, 3).setHealth(new IntArray(new int[]{60})), "Golem", AssetEnum.GOLEM.getTexture(), AssetEnum.GOLEM_FUTA.getTexture())
	;
	private final String text;
	private final Array<AssetDescriptor<Texture>> texturePaths;
	private final AnimationEnum animation;
	private final EnemyTemplate template;
	private EnemyEnum(EnemyTemplate template, final String text, AssetDescriptor<Texture> ... path) { this(template, text, new Array<AssetDescriptor<Texture>>(path), null); }
    private EnemyEnum(EnemyTemplate template, final String text, final AnimationEnum animation) { this(template, text, new Array<AssetDescriptor<Texture>>(), animation); }
    private EnemyEnum(EnemyTemplate template, final String text, final Array<AssetDescriptor<Texture>> paths, final AnimationEnum animation) { this.template = template; this.text = text; this.texturePaths = paths; this.animation = animation; }
    
    @Override
    public String toString() { return text; }	
    public Array<AssetDescriptor<Texture>> getTextures() { return texturePaths; }
    public Array<String> getPaths() { 
    	Array<String> paths = new Array<String>(); 
    	if(texturePaths != null) 
    		for(AssetDescriptor<Texture> texture : texturePaths) 
    			paths.add(texture.fileName); 
    	return paths; 
    }
    public Array<Texture> getTextures(AssetManager assetManager) {
    	Array<Texture> textures = new Array<Texture>();
		for(AssetDescriptor<Texture> desc : texturePaths) {
			textures.add(assetManager.get(desc)); 
		}
		return textures;
	}
    public String getBGPath() { return this == OGRE ? AssetEnum.FOREST_UP_BG.getPath() : this == CENTAUR || this == UNICORN ? AssetEnum.PLAINS_BG.getPath() : this == GOBLIN || this == GOBLIN_MALE ? AssetEnum.ENCHANTED_FOREST_BG.getPath() : AssetEnum.FOREST_BG.getPath(); } 
    public ObjectMap<String, Array<String>> getImagePaths() { 
    	ObjectMap<String, Array<String>> textureImagePaths = new ObjectMap<String, Array<String>>();
    	if (this == HARPY) { textureImagePaths.put(Stance.FELLATIO.toString(), new Array<String>(new String[]{AssetEnum.HARPY_FELLATIO_0.getPath(), AssetEnum.HARPY_FELLATIO_1.getPath(), AssetEnum.HARPY_FELLATIO_2.getPath(), AssetEnum.HARPY_FELLATIO_3.getPath()})); }
    	else if (this == SLIME) { textureImagePaths.put(Stance.DOGGY.toString(),new Array<String>(new String[]{AssetEnum.SLIME_DOGGY.getPath()})); }
    	else if (this == BRIGAND) { 
    		textureImagePaths.put(Stance.FELLATIO.toString(),new Array<String>(new String[]{AssetEnum.BRIGAND_ORAL.getPath()}));
    		textureImagePaths.put(Stance.FACEFUCK.toString(),new Array<String>(new String[]{AssetEnum.BRIGAND_ORAL.getPath()}));
    		textureImagePaths.put(Stance.ANAL.toString(),new Array<String>(new String[]{AssetEnum.BRIGAND_MISSIONARY.getPath()}));
    	}
    	else if (this == ORC) { textureImagePaths.put(Stance.PRONE_BONE.toString(),new Array<String>(new String[]{AssetEnum.ORC_PRONE_BONE.getPath()})); }
    	else if (this == GOBLIN) {
    		textureImagePaths.put(Stance.FACE_SITTING.toString(), new Array<String>(new String[]{AssetEnum.GOBLIN_FACE_SIT.getPath()}));
    		textureImagePaths.put(Stance.SIXTY_NINE.toString(), new Array<String>(new String[]{AssetEnum.GOBLIN_FACE_SIT.getPath()}));
    		textureImagePaths.put(Stance.PRONE_BONE.toString(), new Array<String>(new String[]{AssetEnum.GOBLIN_ANAL.getPath()}));
    		textureImagePaths.put(Stance.DOGGY.toString(), new Array<String>(new String[]{AssetEnum.GOBLIN_ANAL.getPath()}));
    	}
    	else if (this == GOBLIN_MALE) {
    		textureImagePaths.put(Stance.FACE_SITTING.toString(), new Array<String>(new String[]{AssetEnum.GOBLIN_FACE_SIT_MALE.getPath()}));
    		textureImagePaths.put(Stance.SIXTY_NINE.toString(), new Array<String>(new String[]{AssetEnum.GOBLIN_FACE_SIT_MALE.getPath()}));
    		textureImagePaths.put(Stance.PRONE_BONE.toString(), new Array<String>(new String[]{AssetEnum.GOBLIN_ANAL_MALE.getPath()}));
    		textureImagePaths.put(Stance.DOGGY.toString(), new Array<String>(new String[]{AssetEnum.GOBLIN_ANAL_MALE.getPath()}));
    	}
    	else if (this == CENTAUR) {
    		textureImagePaths.put(Stance.DOGGY.toString(), new Array<String>(new String[]{AssetEnum.CENTAUR_ANAL.getPath(), AssetEnum.CENTAUR_ANAL_XRAY.getPath()})); 
    	}
    	return textureImagePaths; 
    }
    public PhallusType getPhallusType() { return this == BRIGAND || this == BEASTMISTRESS ? PhallusType.NORMAL : this == ADVENTURER ? PhallusType.SMALL : PhallusType.MONSTER; }
    public PronounSet getPronounSet() { return this == ADVENTURER || this == OGRE || this == GOBLIN_MALE ? PronounSet.MALE : PronounSet.FEMALE; }
    
    public AnimationEnum getAnimation() { return animation; }
	public boolean canProneBone() {
		return this == BRIGAND || this == GOBLIN || this == ORC || this == ADVENTURER || this == GOBLIN_MALE;
	}
	public boolean canBleed() { return this != SLIME; }
	
	public int getStartingLust() { return this == UNICORN ? 20 : 0; }
	
	// all of this should likely be replaced with a getEnemy method that simply returns the new enemyCharacter
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
	public boolean willPounce() { return this != EnemyEnum.UNICORN && this != EnemyEnum.BEASTMISTRESS; }
	public boolean isPounceable() { return this != EnemyEnum.OGRE && this != EnemyEnum.BEASTMISTRESS && this != EnemyEnum.UNICORN; }
	public boolean prefersProneBone() { return this == ORC || this == GOBLIN; }
	public boolean prefersMissionary() { return this == BRIGAND || this == ADVENTURER; }
	public boolean canWrestle() { return this != HARPY && this != CENTAUR && this != UNICORN && this != OGRE && this != BEASTMISTRESS && this != SPIDER; }
	
	public AnimatedActor getPrimaryAnimation(AssetManager assetManager) {
		AnimatedActor animation = null; 
		if (this.animation != null) {
			animation = this.animation.getAnimation(assetManager);			
		}
		return animation;
	}
	
	public Array<AnimatedActor> getAnimations(AssetManager assetManager) {
		Array<AnimatedActor> animations = new Array<AnimatedActor>();
		AnimatedActor primary = getPrimaryAnimation(assetManager);
		if (primary != null) animations.add(primary);
		 
		if (this == EnemyEnum.HARPY) {
			animations.add(assetManager.get(AssetEnum.HARPY_ATTACK_ANIMATION.getAnimation()).getInstance());	
			animations.get(1).setSkeletonPosition(800, 450);
			animations.get(1).setAnimation(0, "attack", false);
			animations.add(assetManager.get(AssetEnum.FEATHERS_ANIMATION.getAnimation()).getInstance());	
			animations.get(2).setSkeletonPosition(900, 500);
			animations.get(2).setAnimation(0, "featherpoof1", true);
			animations.add(assetManager.get(AssetEnum.FEATHERS2_ANIMATION.getAnimation()).getInstance());	
			animations.get(3).setSkeletonPosition(900, 500);
			animations.get(3).setAnimation(0, "Featherpoof2", true);	
		}
		else if (this == EnemyEnum.BRIGAND) {
			animations.add(assetManager.get(AssetEnum.ANAL_ANIMATION.getAnimation()).getInstance());	
			animations.get(1).setSkeletonPosition(775, 505);
			animations.get(1).setAnimation(0, "IFOS100N", true);
		}
		return animations;
	}
	
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
		private EnemyTemplate setHealth(IntArray healthTiers) { this.healthTiers = healthTiers; return this; }
		private EnemyTemplate addHealth(int health) { this.healthTiers.add(health); return this; }
		private EnemyTemplate setMana(int mana) { this.manaTiers = new IntArray(new int[]{mana}); return this; }
		
		private WeaponType getWeaponType() { return weaponType; }
	}	
}
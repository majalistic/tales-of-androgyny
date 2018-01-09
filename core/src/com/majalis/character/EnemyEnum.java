package com.majalis.character;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.ObjectMap;
import com.majalis.asset.AnimatedActor;
import com.majalis.asset.AnimatedActorFactory;
import com.majalis.asset.AnimationEnum;
import com.majalis.asset.AssetEnum;
import com.majalis.character.AbstractCharacter.PhallusType;
import com.majalis.character.AbstractCharacter.PronounSet;
import com.majalis.character.Armor.ArmorType;
import com.majalis.character.Arousal.ArousalLevel;
import com.majalis.character.Arousal.ArousalType;
import com.majalis.character.Item.WeaponType;
@SuppressWarnings("unchecked")
public enum EnemyEnum {
	WERESLUT (new EnemyTemplate(WeaponType.Claw).setStrength(5).setAgility(5), "Wereslut", AssetEnum.WEREBITCH.getTexture()),
	HARPY (new EnemyTemplate(WeaponType.Talon).setStrength(4), "Harpy", null, AnimationEnum.HARPY),
	SLIME (new EnemyTemplate(null).setArmor(null).setLegwear(null).setUnderwear(null).setDefense(3).setStrength(2).setEndurance(4).setAgility(4), "Slime", AssetEnum.SLIME.getTexture()),
	BRIGAND (new EnemyTemplate(WeaponType.Gladius).setAgility(4).setShield(ArmorType.SHIELD), "Brigand", null, AnimationEnum.BRIGAND),
	CENTAUR (new EnemyTemplate(WeaponType.Bow).setEndurance(4).setAgility(4).setPerception(5).setShield(ArmorType.SHIELD), "Centaur", null, AnimationEnum.CENTAUR),
	UNICORN (new EnemyTemplate(WeaponType.Bow).setEndurance(4).setAgility(4).setPerception(5).setShield(ArmorType.SHIELD), "Unicorn", null, AnimationEnum.UNICORN),
	GOBLIN (new EnemyTemplate(WeaponType.Dagger).setStrength(4).setEndurance(4).setAgility(5), "Goblin", null, AnimationEnum.GOBLIN), 
	GOBLIN_MALE (new EnemyTemplate(WeaponType.Dagger).setStrength(4).setEndurance(4).setAgility(5), "Goblin (Male)", null, AnimationEnum.GOBLIN_MALE),
	ORC (new EnemyTemplate(WeaponType.Chain, 6, 5, 4, 3, 3, 3).setArmor(ArmorType.MEDIUM_ENEMY_ARMOR).setLegwear(ArmorType.MEDIUM_ENEMY_LEGWEAR).addHealth(10).setShield(ArmorType.SHIELD), "Orc", null, AnimationEnum.ORC), 
	ADVENTURER (new EnemyTemplate(WeaponType.Axe, 4, 4, 4, 3, 4, 6).setArmor(ArmorType.MEDIUM_ENEMY_ARMOR).setLegwear(ArmorType.MEDIUM_ENEMY_LEGWEAR).addHealth(10).setMana(26).setShield(ArmorType.SHIELD), "Adventurer", AssetEnum.ADVENTURER.getTexture()),
	OGRE (new EnemyTemplate(WeaponType.Greatclub, 8, 6, 4, 3, 3, 3).setArmor(null).setLegwear(null).setHealth(new IntArray(new int[]{20, 20, 20})), "Ogre", AssetEnum.OGRE.getTexture()),
	BEASTMISTRESS (new EnemyTemplate(WeaponType.Claw).setStrength(6).setAgility(8).setEndurance(5).addHealth(10), "Beast Mistress", AssetEnum.BEASTMISTRESS.getTexture()),
	SPIDER (new EnemyTemplate(WeaponType.Claw).setStrength(6).setAgility(5).setEndurance(5).setHealth(new IntArray(new int[]{20, 20, 20, 20})), "Arachne", AssetEnum.SPIDER.getTexture()), 
	GOLEM (new EnemyTemplate(null, 6, 8, 4, 3, 3, 3).setArmor(ArmorType.MEDIUM_ENEMY_ARMOR).setLegwear(ArmorType.MEDIUM_ENEMY_LEGWEAR).setHealth(new IntArray(new int[]{60})).setMana(12), "Golem", AssetEnum.GOLEM.getTexture(), AssetEnum.GOLEM_FUTA.getTexture()),
	GHOST (new EnemyTemplate(null, 0, 0, 0, 8, 8, 8).setArmor(null).setLegwear(null).setUnderwear(null).setHealth(new IntArray(new int[]{15})).setMana(30), "Ghost", AssetEnum.GHOST_SPOOKY.getTexture(), AssetEnum.GHOST_SPOOKY_BLOODLESS.getTexture()),
	BUNNY (new EnemyTemplate(WeaponType.Sickle, 6, 6, 9, 5, 1, 8).setHealth(new IntArray(new int[]{20, 20, 20, 20})).setMana(30).setShield(ArmorType.SHIELD), "Puca", AssetEnum.BUNNY_CREAM.getTexture(), AssetEnum.BUNNY_VANILLA.getTexture(), AssetEnum.BUNNY_CARAMEL.getTexture(), AssetEnum.BUNNY_CHOCOLATE.getTexture(), AssetEnum.BUNNY_DARK_CHOCOLATE.getTexture()), 
	ANGEL (new EnemyTemplate(WeaponType.Trumpet).setHealth(new IntArray(new int[]{20, 20, 20, 20})).setMana(30).setEndurance(10).setAgility(10).setArmor(null).setLegwear(null).setStrength(5).setAgility(5), "Angel", AssetEnum.ANGEL.getTexture()), 
	NAGA (new EnemyTemplate(WeaponType.Flail).setHealth(new IntArray(new int[]{20, 20, 20, 20})).setArmor(null).setLegwear(null).setUnderwear(null).setStrength(6).setAgility(6).setEndurance(6).setShield(ArmorType.SHIELD), "Naga", AssetEnum.NAGA.getTexture()), 
	QUETZAL (new EnemyTemplate(WeaponType.Claw).setHealth(new IntArray(new int[]{30, 30, 30, 30, 30})).setArmor(null).setLegwear(null).setUnderwear(ArmorType.UNDERWEAR).setStrength(10).setAgility(10).setEndurance(10), "Quetzal Goddess", AssetEnum.QUETZAL.getTexture()), 
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
    	if (texturePaths != null) {
    		for(AssetDescriptor<Texture> desc : texturePaths) {
    			textures.add(assetManager.get(desc)); 
    		}
    	}
		return textures;
	}
    public String getBGPath() { return this == OGRE ? AssetEnum.FOREST_UP_BG.getPath() : this == NAGA || this == SPIDER ? AssetEnum.CAVE_BG.getPath() : this == CENTAUR || this == UNICORN ? AssetEnum.PLAINS_BG.getPath() : this == ANGEL || this == QUETZAL ? AssetEnum.CELESTIAL_BG.getPath() : this == GOBLIN || this == GOBLIN_MALE ? AssetEnum.ENCHANTED_FOREST_BG.getPath() : AssetEnum.FOREST_BG.getPath(); } 
    // there should be another method that accepts an assetManager and returns the actual maps
    public ObjectMap<String, Array<String>> getImagePaths() { 
    	ObjectMap<String, Array<String>> textureImagePaths = new ObjectMap<String, Array<String>>();
    	if (this == WERESLUT) {
    		textureImagePaths.put(Stance.DOGGY.toString(), new Array<String>(new String[]{AssetEnum.WEREBITCH_ANAL.getPath()}));
    		textureImagePaths.put(Stance.KNOTTED.toString(), new Array<String>(new String[]{AssetEnum.WEREBITCH_KNOT.getPath()}));
    	}
    	else if (this == HARPY) { 
    		textureImagePaths.put(Stance.FELLATIO.toString(), new Array<String>(new String[]{AssetEnum.HARPY_FELLATIO_0.getPath(), AssetEnum.HARPY_FELLATIO_1.getPath(), AssetEnum.HARPY_FELLATIO_2.getPath(), AssetEnum.HARPY_FELLATIO_3.getPath()})); 
    		textureImagePaths.put(Stance.DOGGY.toString(), new Array<String>(new String[]{AssetEnum.HARPY_ANAL.getPath()}));
    	}
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
    		textureImagePaths.put(Stance.FELLATIO.toString(), new Array<String>(new String[]{AssetEnum.CENTAUR_ORAL.getPath()})); 
    	}
    	else if (this == UNICORN) {
    		textureImagePaths.put(Stance.DOGGY.toString(), new Array<String>(new String[]{AssetEnum.UNICORN_ANAL.getPath(), AssetEnum.UNICORN_ANAL_XRAY.getPath()})); 
    	}
    	else if (this == ADVENTURER) {
    		textureImagePaths.put(Stance.COWGIRL_BOTTOM.toString(), new Array<String>(new String[]{AssetEnum.ADVENTURER_ANAL.getPath()})); 
    	}
    	return textureImagePaths; 
    }
    public PhallusType getPhallusType() { return this == BRIGAND || this == BEASTMISTRESS || this == GHOST || this == BUNNY ? PhallusType.NORMAL : this == ANGEL ? PhallusType.NONE : this == ADVENTURER ? PhallusType.SMALL : PhallusType.MONSTER; }
    public PronounSet getPronounSet() { return this == ADVENTURER || this == OGRE || this == GOBLIN_MALE ? PronounSet.MALE : PronounSet.FEMALE; }
    
    public AnimationEnum getAnimation() { return animation; }
	protected boolean hasHitAnimation() { return this == EnemyEnum.HARPY || this == EnemyEnum.CENTAUR || this == EnemyEnum.UNICORN; }
    
	protected Arousal getArousal() { return new Arousal(this == QUETZAL ? ArousalType.QUETZAL : this == GOBLIN || this == GOBLIN_MALE ? ArousalType.GOBLIN : this == OGRE ? ArousalType.OGRE : this == GOLEM ? ArousalType.SEXLESS : ArousalType.DEFAULT); }
	protected ArousalLevel getStartingArousal() { return this == UNICORN ? ArousalLevel.ERECT : ArousalLevel.FLACCID; }
	
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
	public ArmorType getArmorType() { return template.getArmorType(); }
	public ArmorType getLegwearType() { return template.getLegwearType(); }
	public ArmorType getUnderwearType() { return template.getUnderwearType(); }
	public ArmorType getShieldType() { return template.getShieldType(); }
	public boolean canBleed() { return this != SLIME && this != GOLEM && this != GHOST; }
	public boolean willFaceSit() { return this != CENTAUR && this != UNICORN && this != GHOST && this != OGRE && this != SPIDER && this != NAGA && this != QUETZAL;} 
	public boolean willArmorSunder() { return this == BRIGAND || this == ORC || this == ADVENTURER; }
	public boolean willParry() { return this == BRIGAND || this == ADVENTURER; }
	public boolean canBeRidden() { return this != SLIME && this != CENTAUR && this != UNICORN && this != BEASTMISTRESS && this != GHOST && this != ANGEL && this != NAGA && this != QUETZAL; }
	public boolean willPounce() { return this != UNICORN && this != BEASTMISTRESS && this != ANGEL && this != NAGA && this != GHOST && this != QUETZAL; }
	public boolean isPounceable() { return this != OGRE && this != BEASTMISTRESS && this != UNICORN && this != GHOST && this != ANGEL && this != NAGA && this != QUETZAL; }
	public boolean canProneBone() { return this == BRIGAND || this == GOBLIN || this == ORC || this == ADVENTURER || this == GOBLIN_MALE; }
	public boolean prefersProneBone() { return this == ORC || this == GOBLIN; }
	public boolean prefersMissionary() { return this == BRIGAND || this == ADVENTURER; }
	public boolean canWrestle() { return this != ANGEL && this != SLIME && this != HARPY && this != CENTAUR && this != UNICORN && this != OGRE && this != BEASTMISTRESS && this != SPIDER && this != GHOST && this != NAGA && this != QUETZAL; }
	public boolean isCorporeal() { return this != GHOST; }
	public boolean usesDefensiveTechniques() { return this != QUETZAL; }
	
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
		private ArmorType armorType;
		private ArmorType legwearType;
		private ArmorType underwearType;
		private ArmorType shieldType;
		private IntArray healthTiers;
		private IntArray manaTiers;
		
		private EnemyTemplate(WeaponType weaponType) {
			this(weaponType, 3, 3, 3, 3, 3, 3);
		}

		private EnemyTemplate(WeaponType weaponType, int strength, int endurance, int agility, int perception, int magic, int charisma) {
			this.weaponType = weaponType;
			this.armorType = ArmorType.LIGHT_ENEMY_ARMOR;
			this.legwearType = ArmorType.LIGHT_ENEMY_LEGWEAR;
			this.underwearType = ArmorType.UNDERWEAR;
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
		
		private EnemyTemplate setArmor(ArmorType type) { this.armorType = type; return this; }
		private EnemyTemplate setLegwear(ArmorType type) { this.legwearType = type; return this; }		
		private EnemyTemplate setUnderwear(ArmorType type) { this.underwearType = type; return this; }	
		public EnemyTemplate setShield(ArmorType shield) { this.shieldType = shield; return this; }
		
		private WeaponType getWeaponType() { return weaponType; }
		private ArmorType getArmorType() { return armorType; }
		private ArmorType getLegwearType() { return legwearType; }
		private ArmorType getUnderwearType() { return underwearType; }
		private ArmorType getShieldType() { return shieldType; }
	}

	public String getDescription() {
		switch (this) {
			case ADVENTURER: return "A young spellsword who, despite his diminuitive size, wields a large battle axe. Known for his haughty sense of superiority... and his peculiar bedroom habits.";
			case BEASTMISTRESS: return "The drow are a secretive culture, so little is known about them as a group, but the outliers that venture to the surface world are a diverse bunch of ruthless scoundrels.\n\nThe huntresses, or beastmistresses, are known to be particularly far-reaching, riding atop powerful, bear-like felines in search of... prey.";
			case BRIGAND: return "Humans are widespread surface dwellers with strange ideas about what goes into where. They're little liked by most of other sapient races, and a few of the non-sapient ones, too. On the bright side, they do make excellent cheese.\n\nBrigands wander the roads in pursuit of the simple pleasures in life - booty, mostly.";  
			case BUNNY: return "The puca are famed for their wiles and the good, and bad, fortune that they bring.\n\nAs far as debt collectors go, she seems pleasant enough - but woe befall those who are consistently late with their coin.";
			case CENTAUR: return "The centaurs are famed for their archery skills, their dominance for the plains - for some clans, their brutality, and for others, their temperance. Centaur society has very strict codes of conduct, and centaur cocks are the size of... well, the phrase is \"hung like a horse\", innit?\n\nHer archery skills are impressive, and they're not the only thing. Hobbies: making mares wink, making twinks mares.";
			case UNICORN: return "The centaurs are famed for their archery skills, their dominance for the plains - for some clans, their brutality, and for others, their temperance. Centaur society has very strict codes of conduct, and centaur cocks are the size of... well, the phrase is \"hung like a horse\", innit?\n\nA rare breed of centaur is that of the unicorn, who is particularly attracted to virgins.  Virgins should be cautioned, however, that despite this attraction, their behaviors indicate they have little interest in preserving said virginity.";
			case GHOST: return "Spirits of the dead can sometimes linger.\n\nAn ancient royalty of some long-dead kingdom - brought low by sorrow that her marriage of convenience was a loveless one.  Though her husband begrudgingly tried, he could not endure her love-making, and left her heart-broken, in much the same way that she left his ass broken.";
			case GOBLIN: return "Goblins are gobliny little buggers, aren't they?  Mischevious, energetic and often cruel, goblins have voracious appetites for food and... fun.  Try to keep them in your field of vision.\n\nFemale goblins are capable of a sort of pseudo-impregnation, and are thus better left to their own devices.";
			case GOBLIN_MALE: return "Goblins are gobliny little buggers, aren't they?  Mischevious, energetic and often cruel, goblins have voracious appetites for food and... fun.  Try to keep them in your field of vision.\n\nMale goblins often fear female goblins, and will let them get first pick of playthings.";
			case GOLEM: return "Stone brought to life is a sure sign that a mage is extremely powerful, and also likely old and bored.\n\nIf they look like this one, add \"lonely\" to that list.  Does a magic conduit really need ass for days?";
			case HARPY: return "The harpies are somewhat dimwitted beastmen, although there are rumors that they're more intelligent than their simple grasp of Common would let on.  They're aerial predators, devouring smaller gamefowl and land-bound critters.\n\nIf you've ever accidentally tripped and sat on a feather duster - congratulations, you know what this harpy's bedroom habits are like.";
			case OGRE: return "Ogres are massive, brutishly strong goblinoids - although some argue they're more closely related to humans. Ogrish kingdoms used to be common, but now it's more common to see singular ogres sleeping in caves and clearings, living simple lives of sleep and slaughter.\n\nSome ogres have a sexual predilection towards smaller humanoids - these humanoids should best avoid ogres entirely unless they want to find out what an ogre-sized plunger does to a smallfolk sized-asshole.";
			case ORC: return "Orcs are notoriously strong for goblinoids or humanoids, however you choose to classify them. The orcs of the region respect bravery and shun cowardice.\n\nCounter-intuitively, Urka prefers meek suitors to strong ones, although she won't kick a brave, strong, but soft boy out of bed for eating crackers.";
			case SLIME: return "Slimes are, well, made out of sentient slime. The core visible within them must remain intact for them to maintain their sentience, however.\n\nTea is a peaceful creature who tends to take the form of a busty, full-figured woman. Avoid her if you have a mean streak, unless you want to meet a most ignominious end.";
			case SPIDER: return "Little is known about the spider woman of the ruins, other than that she is singularly dangerous and malevolent. Her thousands of young are an army their own - enter the ruins at your own peril.";
			case WERESLUT: return "It's not certain if the werewolves are wolves who have become men, men who have become wolves, or simply a beastman-like creature that could appear to be either, but their beast-like strength and agility and sharp claws makes them extremely dangerous.\n\nAppearing like a village librarian, this werewolf has a penchant for mating with human boys... with disastrous results.";
			case ANGEL: return "A divine creature serving an ancient, long forgotten goddess.  It's not certain what domains her goddess has or had as her charge, but it is known exactly how long her trumpet is.";
			case NAGA: return "The naga is a particularly vicious creature. It will just as soon crush an opponent as it will devour them or defile them - depending on its mood.\n\nEven the simple naga is an incredibly dangerous foe - powerful, cunning, silent, tenacious. Underestimate their sadism at your own peril.";
			case QUETZAL: return "The naga is a particularly vicious creature. It will just as soon crush an opponent as it will devour them or defile them - depending on its mood.\n\nOne of the ancient gods, supposedly, is an antedeluvian Naga, of an extinct \"Quetzal\" variety. She is feared far and wide, despite rarely if ever descending from the summit of mount Xiuh. There are rumors that she requires virgin sacrifices - always male, with a preference for a soft face.  It is unknown what becomes of them.";
		}
		return "";
	}
	
	public Array<AssetDescriptor<AnimatedActorFactory>> getAnimationRequirements() {
		Array<AssetDescriptor<AnimatedActorFactory>> temp = new Array<AssetDescriptor<AnimatedActorFactory>>();
		switch (this) {
			case ADVENTURER:
				break;
			case BEASTMISTRESS:
				break;
			case BRIGAND:
				temp.add(AssetEnum.BRIGAND_ANIMATION.getAnimation());
				temp.add(AssetEnum.ANAL_ANIMATION.getAnimation());
				break;
			case CENTAUR:
				temp.add(AssetEnum.CENTAUR_ANIMATION.getAnimation());
				break;
			case GOBLIN:
			case GOBLIN_MALE:
				temp.add(AssetEnum.GOBLIN_ANIMATION.getAnimation());
				break;
			case HARPY:
				temp.add(AssetEnum.HARPY_ANIMATION.getAnimation());
				temp.add(AssetEnum.HARPY_ATTACK_ANIMATION.getAnimation());
				temp.add(AssetEnum.FEATHERS_ANIMATION.getAnimation());
				temp.add(AssetEnum.FEATHERS2_ANIMATION.getAnimation());
				break;
			case OGRE:
				break;
			case ORC:
				temp.add(AssetEnum.ORC_ANIMATION.getAnimation());
				break;
			case SLIME:
				break;
			case SPIDER:
				break;
			case UNICORN:
				temp.add(AssetEnum.CENTAUR_ANIMATION.getAnimation());
				break;
			case WERESLUT:
				break;
			default:
				break;
			
		}
		return temp;
	}
	
	private ObjectMap<Stance, Array<Texture>> getTextureMap(AssetManager assetManager) {
		ObjectMap<Stance, Array<Texture>> textures = new ObjectMap<Stance, Array<Texture>>();
		for (ObjectMap.Entry<String, Array<String>> entry : getImagePaths()) {
			Array<Texture> stanceTextures = new Array<Texture>();
			for (String path : entry.value) {
				stanceTextures.add(assetManager.get(path, Texture.class));
			}
			textures.put(Stance.valueOf(entry.key), stanceTextures);
		}
		return textures;
	}
	
	public EnemyCharacter getInstance(AssetManager assetManager, Stance stance, boolean storyMode) {
		return new EnemyCharacter(getTextures(assetManager), getTextureMap(assetManager), getAnimations(assetManager), this, stance, storyMode);
	}
}
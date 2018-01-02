package com.majalis.encounter;

import static com.majalis.asset.AssetEnum.*;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.majalis.asset.AssetEnum;
import com.majalis.screens.TownScreen;
/*
 * Represents all the metadata for an Encounter and its representation on the world map.
 */
public enum EncounterCode {
	DEFAULT, 
	ERROR, 
	INITIAL, 
	
	WERESLUT, 
	HARPY, 
	SLIME,
	BRIGAND,
	DRYAD, 
	CENTAUR, 
	GOBLIN (ENCHANTED_FOREST),
	GADGETEER (FOREST_INACTIVE), 
	ORC (FOREST_INACTIVE),
	ADVENTURER (FOREST_INACTIVE),
	TRUDY_COMPANION, 
	OGRE, 
	BEASTMISTRESS,
	SPIDER (CASTLE),
	ELF,
	ELF_COMPANION, 
	GOLEM,
	GHOST, 
	BUNNY,
	ANGEL (CASTLE),
	NAGA,
	
	FORT (CASTLE), 
	TOWN (AssetEnum.TOWN),
	TOWN2 (AssetEnum.TOWN),
	BANK,
	BROTHEL,
	TOWN_CRIER,
	WITCH_COTTAGE (AssetEnum.COTTAGE),
	QUETZAL (MOUNTAIN_ACTIVE), 
	INN,
	SHOP, 
	WEAPON_SHOP,
	STARVATION,
	CAMP_AND_EAT, 
	LEVEL_UP, 
	FORAGE,
	
	/* Mini Encounters */
	FOOD_CACHE,
	GOLD_CACHE,
	ICE_CREAM,
	HUNGER_CHARM,
	DAMAGE_TRAP,
	ANAL_TRAP,
	
	/* Story Mode */
	
	COTTAGE_TRAINER (AssetEnum.COTTAGE),
	COTTAGE_TRAINER_VISIT (AssetEnum.COTTAGE),
	TOWN_STORY (AssetEnum.TOWN),
	FIRST_BATTLE_STORY, 
	MERI_COTTAGE (AssetEnum.COTTAGE),
	MERI_COTTAGE_VISIT (AssetEnum.COTTAGE), 
	OGRE_WARNING_STORY (FOREST_INACTIVE),
	OGRE_STORY, 
	HARPY_STORY (MOUNTAIN_ACTIVE),
	BRIGAND_STORY (MOUNTAIN_ACTIVE),
	ECCENTRIC_MERCHANT,
	STORY_FEM (FOREST_INACTIVE), 
	STORY_SIGN (FOREST_INACTIVE), 
	;
	
	private final AssetEnum texture;	
	
	private EncounterCode() {
		this(FOREST_ACTIVE);
	}
	
	private EncounterCode(AssetEnum texture) {
		this.texture = texture;
	}

	public AssetEnum getTexture() { return texture; }
	
	public String getDescription(int visibility) {
		switch(visibility) {
			case -1: return ""; // special case for display no info
			case 0:
				return "You are unsure of what awaits you!";
			case 1:
				switch (this) {
					case WERESLUT: return "Wereslut";
					case HARPY: return "Harpy";
					case SLIME: return "Slime";
					case BRIGAND: return "Brigand";
					case DRYAD: return "Dryad";
					case CENTAUR: return "Centaur";
					case GOBLIN: return "Goblin";
					case ORC: return "Orc";
					case OGRE: return "Ogre";
					case BEASTMISTRESS: return "Drow";
					case ANGEL: return "Ruins";
					case SPIDER: return "Ruins";
					case ADVENTURER: 
					case ELF: return "Adventurer";
					case GHOST: return "Unknown";
					case GOLEM: return "Statue";
					case GADGETEER: return "Merchant";
					case NAGA: return "Cave";
					case TOWN: return "Small Settlement";
					case TOWN2:
					case TOWN_STORY:
						return "Town of Nadir";	
					case FORT:
						return "Fort";
					case QUETZAL: return "Mountain";
					case COTTAGE_TRAINER: return "Cottage on the Outskirts";
					case FIRST_BATTLE_STORY: return "Forest Clearing";
					case MERI_COTTAGE: return "Witch's Cottage";
					case WITCH_COTTAGE: return "Cottage";
					case ECCENTRIC_MERCHANT: return "Merchant Path";
					case OGRE_WARNING_STORY: return "Lean-to in the Forest";
					case OGRE_STORY: return "Forest Pass";
					case HARPY_STORY: return "South Pass";
					case STORY_FEM: return "Unwalked Path";
					case STORY_SIGN: return "Crossroads";
					case BRIGAND_STORY: return "West Pass";
					case FOOD_CACHE: 
					case GOLD_CACHE:
					case ICE_CREAM:
					case HUNGER_CHARM: 
					case DAMAGE_TRAP: 
					case ANAL_TRAP: return "Unknown";
					default: return "Unknown - No Info for encounter #" + this + " and perception level = " + visibility;
				}
			case 2:
				switch (this) {
					case NAGA: return "Cave - Unknown Danger!";
					default:
				}
			case 3:
			case 4:
			case 5:
			case 6:
			default:
				switch (this) {
					case WERESLUT: return "Wereslut - Hostile!";
					case HARPY: return "Harpy - Hostile!";
					case SLIME: return "Slime - Neutral";
					case BRIGAND: return "Brigand - Hostile!";
					case DRYAD: return "Dryad - Peaceful";
					case CENTAUR: return "Centaur - Neutral";
					case GOBLIN: return "Goblin - Hostile!";
					case ORC: return "Orc - Neutral";
					case ADVENTURER: return "Adventurer - Neutral";
					case ELF: return "Elf - Peaceful";
					case GHOST: return "Strange Presence";
					case GOLEM: return "Golem";
					case OGRE: return "Ogre - Danger!";
					case BEASTMISTRESS: return "Drow Beastmistress - Hostile!";
					case ANGEL: return "Ruins - Calm";
					case SPIDER: return "Ruins - Danger!";
					case GADGETEER: return "Suspicious Merchant";
					case NAGA: return "Cave - Naga Within!";
					case TOWN: return "Town of Silajam";
					case TOWN2:
					case TOWN_STORY: return "Town of Nadir";
					case FORT:
						return "Fort";
					case QUETZAL: return "Mount Xiuh";
					case COTTAGE_TRAINER: return "Cottage-on-the-Outskirts";
					case FIRST_BATTLE_STORY: return "Forest Clearing - signs of hostile creature";
					case MERI_COTTAGE: return "Witch's Cottage";
					case WITCH_COTTAGE: return "Witch's Cottage";
					case ECCENTRIC_MERCHANT: return "Merchant Path";
					case OGRE_WARNING_STORY: return "Lean-to in the Forest";
					case OGRE_STORY: return "Forest Pass";
					case HARPY_STORY: return "South Pass (Harpies)";
					case STORY_FEM: return "Unwalked Path";
					case STORY_SIGN: return "Crossroads";
					case BRIGAND_STORY: return "West Pass (Brigands)";
					case FOOD_CACHE: return "Cache!";
					case GOLD_CACHE: return "Cache!";
					case ICE_CREAM: return "Cache!";
					case HUNGER_CHARM: return "Cache!";
					case DAMAGE_TRAP: return "Trap!";
					case ANAL_TRAP: return "Trap!";
					default: return "Unknown - No Info for encounter #" + this  + " and perception level = " + visibility;
				}
		}
	}
	
	public String getFullDescription() {
		switch(this) {
			case TOWN: return "Town of Silajam (visited)";
			case TOWN2: return "Town of Nadir (visited)";
			case COTTAGE_TRAINER_VISIT: return "Cottage-on-the-Outskirts (visited)";
			case WITCH_COTTAGE: return "Witch's Cottage (visited)";
			case QUETZAL: return "Xiuh Mountain (visited)";
			case MERI_COTTAGE_VISIT: return "Witch's Cottage (visited)";
			case GADGETEER: return "Strange Gadgeteer";
			case FORT: return "Fort";
			default: return "Nothing here.";
		}
	}

	public Array<AssetDescriptor<?>> getRequirements() {
		switch (this) {
			case ADVENTURER:
			case STORY_FEM:
			case TRUDY_COMPANION:
				return new Array<AssetDescriptor<?>>(new AssetDescriptor[]{  
					AssetEnum.ADVENTURER.getTexture(),
					AssetEnum.ADVENTURER_ANAL.getTexture(),
					AssetEnum.CUM.getSound(),
					AssetEnum.GADGETEER_MUSIC.getMusic(),
					AssetEnum.STICK_BACKGROUND.getTexture(),
					AssetEnum.CUM.getSound(),
					AssetEnum.GAME_OVER_ANIMATION.getAnimation()
				});
			case ANGEL:
				return new Array<AssetDescriptor<?>>(new AssetDescriptor[]{
						AssetEnum.ANGEL.getTexture(),
						AssetEnum.CUM.getSound(),
						AssetEnum.CUM_BUBBLING.getSound(),
						AssetEnum.ANGEL_MUSIC.getMusic()
					});
			case BEASTMISTRESS:
				return new Array<AssetDescriptor<?>>(new AssetDescriptor[]{  
					AssetEnum.BEASTMISTRESS.getTexture(),
					AssetEnum.CUM.getSound(),
					AssetEnum.WEREWOLF_MUSIC.getMusic()
				});
			case BRIGAND:
				return new Array<AssetDescriptor<?>>(new AssetDescriptor[]{
					AssetEnum.BRIGAND_ORAL.getTexture(),
					AssetEnum.BRIGAND_MISSIONARY.getTexture(),
					AssetEnum.CUM.getSound(),
					AssetEnum.CUM_BUBBLING.getSound(),
					AssetEnum.BRIGAND_ANIMATION.getAnimation()
				});
			case BRIGAND_STORY:
				return new Array<AssetDescriptor<?>>(new AssetDescriptor[]{
					AssetEnum.BRIGAND_ORAL.getTexture(),
					AssetEnum.BRIGAND_MISSIONARY.getTexture(),
					AssetEnum.CUM.getSound(),
					AssetEnum.CUM_BUBBLING.getSound(),
					AssetEnum.BRIGAND_ANIMATION.getAnimation()
				});
			case BROTHEL:
				Array<AssetDescriptor<?>> reqs2 = new Array<AssetDescriptor<?>>(new AssetDescriptor[]{  
					AssetEnum.SMUG_LAUGH.getSound(),
					AssetEnum.CUM.getSound(),
					AssetEnum.ELF.getTexture(),
					AssetEnum.BROTHEL_MADAME.getTexture(),
				});
				reqs2.addAll(TownScreen.resourceRequirements);
				return reqs2;
			case BUNNY:
				return new Array<AssetDescriptor<?>>(new AssetDescriptor[]{
						AssetEnum.BUNNY_CREAM.getTexture(),
						AssetEnum.BUNNY_VANILLA.getTexture(),
						AssetEnum.BUNNY_CARAMEL.getTexture(),
						AssetEnum.BUNNY_CHOCOLATE.getTexture(),
						AssetEnum.BUNNY_DARK_CHOCOLATE.getTexture(),
						AssetEnum.BUNNY_CREAM_ANAL.getTexture(),
						AssetEnum.BUNNY_VANILLA_ANAL.getTexture(),
						AssetEnum.BUNNY_CARAMEL_ANAL.getTexture(),
						AssetEnum.BUNNY_CHOCOLATE_ANAL.getTexture(),
						AssetEnum.BUNNY_DARK_CHOCOLATE_ANAL.getTexture(),
						AssetEnum.CUM.getSound(),
						AssetEnum.CUM_BUBBLING.getSound(),
						AssetEnum.SWORD_SLASH_SOUND.getSound(),
						AssetEnum.GADGETEER_MUSIC.getMusic()
					});
			case CENTAUR:
				return new Array<AssetDescriptor<?>>(new AssetDescriptor[]{
					AssetEnum.UNICORN_ANAL.getTexture(), 
					AssetEnum.UNICORN_ANAL_XRAY.getTexture(),
					AssetEnum.CENTAUR_ANAL.getTexture(),
					AssetEnum.CENTAUR_ANAL_XRAY.getTexture(),
					AssetEnum.CUM.getSound(),
					AssetEnum.SHOP_MUSIC.getMusic(),
					AssetEnum.CENTAUR_ANIMATION.getAnimation()
				});
			case COTTAGE_TRAINER:
				return new Array<AssetDescriptor<?>>(new AssetDescriptor[]{  
					AssetEnum.CLASS_SELECT_BACKGROUND.getTexture(),
					AssetEnum.STRENGTH.getTexture(),
					AssetEnum.ENDURANCE.getTexture(),
					AssetEnum.AGILITY.getTexture(),
					AssetEnum.PERCEPTION.getTexture(),
					AssetEnum.MAGIC.getTexture(),
					AssetEnum.CHARISMA.getTexture(),
					AssetEnum.ENCHANTRESS.getTexture(),
					AssetEnum.CREATION_BUTTON_UP.getTexture(),
					AssetEnum.CREATION_BUTTON_DOWN.getTexture(),
					AssetEnum.CREATION_BUTTON_CHECKED.getTexture(),	
					AssetEnum.CREATION_BAUBLE_EMPTY.getTexture(),
					AssetEnum.CREATION_BAUBLE_NEW.getTexture(),
					AssetEnum.CREATION_BAUBLE_OLD.getTexture(),
					AssetEnum.CREATION_BAUBLE_REMOVED.getTexture(),
					AssetEnum.NORMAL_BOX.getTexture(),
					AssetEnum.CABIN_BACKGROUND.getTexture(),
					AssetEnum.TRAINER.getTexture(),
					AssetEnum.TRAINER_MUSIC.getMusic()
				});
			case COTTAGE_TRAINER_VISIT:
				return new Array<AssetDescriptor<?>>(new AssetDescriptor[]{  
					AssetEnum.CABIN_BACKGROUND.getTexture(),
					AssetEnum.TRAINER.getTexture(),
					AssetEnum.TRAINER_MUSIC.getMusic()
				});
			case DEFAULT:
				return new Array<AssetDescriptor<?>>(new AssetDescriptor[]{AssetEnum.STICK_BACKGROUND.getTexture()});
			case DRYAD:
				return new Array<AssetDescriptor<?>>(new AssetDescriptor[]{  
					AssetEnum.SHOP_MUSIC.getMusic(),
					AssetEnum.DRYAD_BACKGROUND.getTexture()
				});
			case ELF:
			case ELF_COMPANION:
				return new Array<AssetDescriptor<?>>(new AssetDescriptor[]{  
					AssetEnum.ELF.getTexture(),
					AssetEnum.ELF_TURTLE.getTexture(),
					AssetEnum.ELF_AND_TURTLE.getTexture(),
					AssetEnum.STICK_BACKGROUND.getTexture(),
					AssetEnum.GADGETEER_MUSIC.getMusic()
				});
			case FIRST_BATTLE_STORY:
				return new Array<AssetDescriptor<?>>(new AssetDescriptor[]{
					AssetEnum.FOREST_BG.getTexture(),
					AssetEnum.GOBLIN.getTexture(),
					AssetEnum.GOBLIN_ANAL.getTexture(),
					AssetEnum.LOUD_LAUGH.getSound(),
					AssetEnum.WEREWOLF_MUSIC.getMusic()
					
				});
			case GADGETEER:
				return new Array<AssetDescriptor<?>>(new AssetDescriptor[]{  
					AssetEnum.GADGETEER.getTexture(),
					AssetEnum.BATTLE_TEXTBOX.getTexture(),
					AssetEnum.TEXT_BOX.getTexture(),
					AssetEnum.EQUIP.getSound(),
					AssetEnum.GADGETEER_MUSIC.getMusic()
				});
			case GHOST:
				return new Array<AssetDescriptor<?>>(new AssetDescriptor[]{  
					AssetEnum.GHOST.getTexture(),
					AssetEnum.GHOST_SPOOKY.getTexture(),
					AssetEnum.GHOST_SPOOKY_BLOODLESS.getTexture(),
					AssetEnum.FIREBALL_SOUND.getSound(),
					AssetEnum.CUM.getSound(),
					AssetEnum.CUM_BUBBLING.getSound(),
					AssetEnum.SPOOKY_MUSIC.getMusic(),
					AssetEnum.HORROR_MUSIC.getMusic()
				});
			case GOBLIN:
				return new Array<AssetDescriptor<?>>(new AssetDescriptor[]{  
					AssetEnum.GOBLIN.getTexture(),
					AssetEnum.GOBLIN_MALE.getTexture(),
					AssetEnum.GOBLIN_ANAL.getTexture(),
					AssetEnum.GOBLIN_ANAL_MALE.getTexture(),
					AssetEnum.LOUD_LAUGH.getSound(),
					AssetEnum.CUM.getSound(),
					AssetEnum.CUM_BUBBLING.getSound(),
					AssetEnum.WEREWOLF_MUSIC.getMusic(),
					AssetEnum.CARNIVAL_MUSIC.getMusic(),
					AssetEnum.GAME_OVER_ANIMATION.getAnimation()
				});
			case GOLEM:
				return new Array<AssetDescriptor<?>>(new AssetDescriptor[]{  
					AssetEnum.GOLEM.getTexture(),
					AssetEnum.GOLEM_CLOSEUP.getTexture(),
					AssetEnum.GOLEM_FUTA.getTexture(),
					AssetEnum.GOLEM_DULL.getTexture(),
					AssetEnum.INCANTATION.getSound(),
					AssetEnum.FIREBALL_SOUND.getSound(),
					AssetEnum.INCANTATION.getSound(),
					AssetEnum.GOLEM_SHUTDOWN.getSound(),
					AssetEnum.GOLEM_ONE.getSound(),
					AssetEnum.CUM.getSound(),
					AssetEnum.CUM_BUBBLING.getSound(),
					AssetEnum.ETHEREAL_MUSIC.getMusic()
					
				});
			case HARPY:
				return new Array<AssetDescriptor<?>>(new AssetDescriptor[]{ 
					AssetEnum.HARPY_FELLATIO_1.getTexture(),
					AssetEnum.HARPY_ANAL.getTexture(),
					AssetEnum.CUM.getSound(),
					AssetEnum.CUM_BUBBLING.getSound(),
					AssetEnum.HARPY_ANIMATION.getAnimation()
				});
			case HARPY_STORY:
				return new Array<AssetDescriptor<?>>(new AssetDescriptor[]{ 
					AssetEnum.HARPY_FELLATIO_0.getTexture(),
					AssetEnum.HARPY_FELLATIO_1.getTexture(),
					AssetEnum.HARPY_FELLATIO_2.getTexture(),
					AssetEnum.HARPY_FELLATIO_3.getTexture(),
					AssetEnum.HARPY_ANAL.getTexture(),
					AssetEnum.CUM.getSound(),
					AssetEnum.CUM_BUBBLING.getSound(),
					AssetEnum.HARPY_ANIMATION.getAnimation()
				});
			case INITIAL:
				return new Array<AssetDescriptor<?>>(new AssetDescriptor[]{  
					AssetEnum.GAME_TYPE_BACKGROUND.getTexture(),
					AssetEnum.CLASS_SELECT_BACKGROUND.getTexture(),
					AssetEnum.SILHOUETTE.getTexture(),
					AssetEnum.BURNING_FORT_BG.getTexture(),
					AssetEnum.STRENGTH.getTexture(),
					AssetEnum.ENDURANCE.getTexture(),
					AssetEnum.AGILITY.getTexture(),
					AssetEnum.PERCEPTION.getTexture(),
					AssetEnum.MAGIC.getTexture(),
					AssetEnum.CHARISMA.getTexture(),
					AssetEnum.NORMAL_BOX.getTexture(),
					AssetEnum.SKILL_SELECTION_BACKGROUND.getTexture(),
					AssetEnum.CHARACTER_CUSTOM_BACKGROUND.getTexture(),
					AssetEnum.CREATION_BUTTON_UP.getTexture(),
					AssetEnum.CREATION_BUTTON_DOWN.getTexture(),
					AssetEnum.CREATION_BUTTON_CHECKED.getTexture(),	
					AssetEnum.CREATION_BAUBLE_EMPTY.getTexture(),
					AssetEnum.CREATION_BAUBLE_NEW.getTexture(),
					AssetEnum.CREATION_BAUBLE_OLD.getTexture(),
					AssetEnum.CREATION_BAUBLE_REMOVED.getTexture(),
					AssetEnum.WARRIOR.getTexture(),
					AssetEnum.PALADIN.getTexture(),
					AssetEnum.THIEF.getTexture(),
					AssetEnum.RANGER.getTexture(),
					AssetEnum.MAGE.getTexture(),
					AssetEnum.ENCHANTRESS.getTexture(),
					AssetEnum.SMUG_LAUGH.getSound(),
					AssetEnum.WAVES.getMusic(),
					AssetEnum.HOVEL_MUSIC.getMusic()
				});
			case INN:
				return new Array<AssetDescriptor<?>>(new AssetDescriptor[]{  
					AssetEnum.INNKEEPER.getTexture(),
					AssetEnum.KEYHOLE.getTexture(),
					AssetEnum.GAME_OVER_KEYHOLE.getTexture()
				});
			case LEVEL_UP:
				return new Array<AssetDescriptor<?>>(new AssetDescriptor[]{   
					AssetEnum.CLASS_SELECT_BACKGROUND.getTexture(),
					AssetEnum.STRENGTH.getTexture(),
					AssetEnum.ENDURANCE.getTexture(),
					AssetEnum.AGILITY.getTexture(),
					AssetEnum.PERCEPTION.getTexture(),
					AssetEnum.MAGIC.getTexture(),
					AssetEnum.CHARISMA.getTexture()
				});
			case MERI_COTTAGE:
				return new Array<AssetDescriptor<?>>(new AssetDescriptor[]{  
					AssetEnum.MERI_SILHOUETTE.getTexture(),
					AssetEnum.CABIN_BACKGROUND.getTexture(),
					AssetEnum.TRAINER_MUSIC.getMusic(),
					AssetEnum.WEREWOLF_MUSIC.getMusic()
				});
			case MERI_COTTAGE_VISIT:
				return new Array<AssetDescriptor<?>>(new AssetDescriptor[]{  
					AssetEnum.CABIN_BACKGROUND.getTexture(),
					AssetEnum.TRAINER_MUSIC.getMusic()
				});
			case NAGA:
				return new Array<AssetDescriptor<?>>(new AssetDescriptor[]{
						AssetEnum.NAGA.getTexture(),
						AssetEnum.CUM.getSound(),
						AssetEnum.CUM_BUBBLING.getSound(),
						AssetEnum.WEREWOLF_MUSIC.getMusic()
					});
			case OGRE:
				return new Array<AssetDescriptor<?>>(new AssetDescriptor[]{  
					AssetEnum.OGRE.getTexture(),
					AssetEnum.OGRE_BANGED.getTexture(),
					AssetEnum.CUM.getSound(),
					AssetEnum.CUM_BUBBLING.getSound(),
					AssetEnum.WEREWOLF_MUSIC.getMusic()
				});
			case OGRE_WARNING_STORY:
				return new Array<AssetDescriptor<?>>(new AssetDescriptor[]{AssetEnum.TRAINER_MUSIC.getMusic()});
			case OGRE_STORY:
				return new Array<AssetDescriptor<?>>(new AssetDescriptor[]{  
					AssetEnum.GAME_OGRE.getTexture(),
					AssetEnum.OGRE_GROWL.getSound(),
					AssetEnum.CUM.getSound(),
					AssetEnum.CUM_BUBBLING.getSound(),
					AssetEnum.WEREWOLF_MUSIC.getMusic(),
					AssetEnum.HEAVY_MUSIC.getMusic()
				});
			case ORC:
				return new Array<AssetDescriptor<?>>(new AssetDescriptor[]{  
					AssetEnum.ORC_ANIMATION.getAnimation(),
					AssetEnum.ORC_ZOOM_UP.getTexture(),
					AssetEnum.ORC_ZOOM.getTexture(),
					AssetEnum.ORC_ZOOM_DOWN.getTexture(),
					AssetEnum.ORC_PRONE_BONE.getTexture(),
					AssetEnum.GAPE.getTexture(),
					AssetEnum.CUM.getSound(),
					AssetEnum.WEREWOLF_MUSIC.getMusic()
				});
			case QUETZAL:
				return new Array<AssetDescriptor<?>>(new AssetDescriptor[]{  
						AssetEnum.QUETZAL_HERO.getTexture(),
						AssetEnum.QUETZAL_HERO_ANAL.getTexture(),
						AssetEnum.CUM.getSound(),
						AssetEnum.CUM_BUBBLING.getSound(),
						AssetEnum.HEAVY_MUSIC.getMusic()
				});
			case SLIME:
				return new Array<AssetDescriptor<?>>(new AssetDescriptor[]{  
						AssetEnum.SLIME.getTexture(),
						AssetEnum.SLIME_DOGGY.getTexture(),
				});
			case SPIDER:
				return new Array<AssetDescriptor<?>>(new AssetDescriptor[]{  
					AssetEnum.SPIDER.getTexture(),
					AssetEnum.TINY_SPIDER.getTexture(),
					AssetEnum.TINY_SPIDER_LOW.getTexture(),
					AssetEnum.WEREWOLF_MUSIC.getMusic()
				});
			case TOWN_STORY:
				Array<AssetDescriptor<?>> reqs = new Array<AssetDescriptor<?>>(new AssetDescriptor[]{  
					AssetEnum.TOWN_BG.getTexture(),
					AssetEnum.SMUG_LAUGH.getSound(),
					AssetEnum.SHOP_MUSIC.getMusic()
				});
				reqs.addAll(TownScreen.resourceRequirements);
				return reqs;	
			case WERESLUT:
				return new Array<AssetDescriptor<?>>(new AssetDescriptor[]{  
					AssetEnum.WEREWOLF_MUSIC.getMusic(),
					AssetEnum.WEREWOLF_GROWL.getSound(),
					AssetEnum.CUM.getSound(),
					AssetEnum.CUM_BUBBLING.getSound(),
					AssetEnum.WEREBITCH.getTexture(),
					AssetEnum.WEREBITCH_ANAL.getTexture(),
					AssetEnum.WEREBITCH_KNOT.getTexture(),
					AssetEnum.WEREBITCH_KNOT_CUM.getTexture()
				});
			case WITCH_COTTAGE:
				return new Array<AssetDescriptor<?>>(new AssetDescriptor[]{  
					AssetEnum.MERI_SILHOUETTE.getTexture(),
					AssetEnum.CABIN_BACKGROUND.getTexture(),
					AssetEnum.TEXT_BOX.getTexture(),
					AssetEnum.BATTLE_TEXTBOX.getTexture(),
					AssetEnum.EQUIP.getSound(),
					AssetEnum.TRAINER_MUSIC.getMusic(),
					AssetEnum.WEREWOLF_MUSIC.getMusic()
				});
			
			case SHOP:
			case WEAPON_SHOP:
			case BANK:
			case TOWN_CRIER:
				return TownScreen.resourceRequirements;
			case CAMP_AND_EAT:
				return new Array<AssetDescriptor<?>>(new AssetDescriptor[]{AssetEnum.SHOP_MUSIC.getMusic()});
			case STARVATION:
				return new Array<AssetDescriptor<?>>(new AssetDescriptor[]{  
					AssetEnum.WEREWOLF_MUSIC.getMusic(),
					AssetEnum.GAME_OVER_ANIMATION.getAnimation()
				});
			default:
				return new Array<AssetDescriptor<?>>(new AssetDescriptor[]{AssetEnum.TRAP_BONUS.getTexture()});
		}
	}
	// for random gen
	public static IntMap<Array<EncounterCode>> encounterMap;
	private static boolean iceCreamReady;
	private static boolean hungerCharmReady;
	static {
		encounterMap = new IntMap<Array<EncounterCode>>();
		encounterMap.put(1, new Array<EncounterCode>(new EncounterCode[]{WERESLUT, HARPY, SLIME, BRIGAND, DRYAD, CENTAUR, GOBLIN, ORC, FOOD_CACHE, GOLD_CACHE, DAMAGE_TRAP, ANAL_TRAP, HUNGER_CHARM}));
		encounterMap.put(2, new Array<EncounterCode>(new EncounterCode[]{WERESLUT, HARPY, BRIGAND, DRYAD, CENTAUR, GOBLIN, ORC, ADVENTURER, ELF, OGRE, HUNGER_CHARM, ICE_CREAM, BEASTMISTRESS, GOLEM, GHOST, GOLD_CACHE, FOOD_CACHE, DAMAGE_TRAP, ANAL_TRAP}));
		encounterMap.put(3, new Array<EncounterCode>(new EncounterCode[]{DRYAD, OGRE, HUNGER_CHARM, ICE_CREAM, BEASTMISTRESS, GOLEM, GHOST, NAGA, FOOD_CACHE, DAMAGE_TRAP, ANAL_TRAP}));
		
		iceCreamReady = true;
		hungerCharmReady = true;
		
	}
	
	// FOR TESTING PURPOSES ONLY
	public static void resetState() { 
		iceCreamReady = true;
		hungerCharmReady = true;
	}
	
	public static ObjectSet<EncounterCode> getAllRandomEncounters() {
		ObjectSet<EncounterCode> temp = new ObjectSet<EncounterCode>();
		for (Array<EncounterCode> mapFlat : encounterMap.values()) {
			temp.addAll(mapFlat);
		}
		return temp;
	}
	
	public static EncounterCode getEncounterCode(int rawCode, int difficulty, ObjectSet<EncounterCode> unspawnedEncounters) {
		for (int difficultyTier = difficulty; difficultyTier >= 1; difficultyTier--) {
			for (EncounterCode encounter : encounterMap.get(difficultyTier)) {
				if (unspawnedEncounters.contains(encounter)) {
					unspawnedEncounters.remove(encounter);
					return encounter;
				}
			}
		}
		
		Array<EncounterCode> encounterArray = encounterMap.get(difficulty);
		EncounterCode newEncounter = encounterArray.get(rawCode % encounterArray.size);
		if (newEncounter == ICE_CREAM) {
			if (iceCreamReady) {
				iceCreamReady = false;
			}
			else {
				newEncounter = FOOD_CACHE;
			}
		}
		else if (newEncounter == HUNGER_CHARM) {
			if (hungerCharmReady) {
				hungerCharmReady = false;
			}
			else {
				newEncounter = FOOD_CACHE;
			}
		}
		for (EncounterCode encounter : encounterArray) {
			if (unspawnedEncounters.contains(encounter)) {
				newEncounter = encounter;
			}
		}
		unspawnedEncounters.remove(newEncounter);
		return newEncounter;
	}

	public EncounterBounty getMiniEncounter() {
		switch (this) {
			case FOOD_CACHE:
			case GOLD_CACHE:
			case ICE_CREAM:
			case HUNGER_CHARM: 
			case DAMAGE_TRAP: 
			case ANAL_TRAP: return new EncounterBounty(this);
			default: return null;
		}
	}
	
	public String getScriptPath() {
		return "script/encounters.json"; //"script/" + (this.isStory() ? "encounters" : this.toString()) + ".json"; 
	}
	/*
	private boolean isStory() {
		return this == COTTAGE_TRAINER || this == COTTAGE_TRAINER_VISIT || this == TOWN_STORY || this == FIRST_BATTLE_STORY || this == MERI_COTTAGE || this == MERI_COTTAGE_VISIT || this == OGRE_WARNING_STORY || this == OGRE_STORY || this == ECCENTRIC_MERCHANT
				|| this == STORY_FEM || this == STORY_SIGN || this == SOUTH_PASS || this == WEST_PASS || this == DEFAULT || this == ERROR || this == INITIAL || this == FORT || this == TOWN || this == TOWN2 || this == BANK || this == BROTHEL || this == TOWN_CRIER
				|| this == CRIER_QUEST || this == INN || this == SHOP || this == WEAPON_SHOP || this == STARVATION || this == CAMP_AND_EAT || this == LEVEL_UP || this == FORAGE;
	}*/
}

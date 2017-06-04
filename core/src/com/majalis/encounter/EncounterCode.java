package com.majalis.encounter;

import static com.majalis.asset.AssetEnum.*;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.utils.Array;
import com.majalis.asset.AssetEnum;
import com.majalis.screens.TownScreen;
/*
 * Represents all the metadata for an Encounter and its representation on the world map.
 */
public enum EncounterCode {
	DEFAULT, 
	ERROR, 
	INITIAL, 
	WERESLUT (0), 
	HARPY (MOUNTAIN_ACTIVE, 1), 
	SLIME (2),
	BRIGAND (3),
	DRYAD (MOUNTAIN_ACTIVE, 4), 
	CENTAUR (5), 
	GOBLIN (ENCHANTED_FOREST, 6),
	GADGETEER (FOREST_INACTIVE), 
	ORC (FOREST_INACTIVE, 7),
	ADVENTURER (FOREST_INACTIVE, 8),
	OGRE (9), 
	BEASTMISTRESS (10),
	FORT (CASTLE), 
	TOWN (AssetEnum.TOWN),
	TOWN2 (AssetEnum.TOWN),
	TOWN_CRIER,
	CRIER_QUEST (MOUNTAIN_ACTIVE), 
	INN,
	SHOP, 
	WEAPON_SHOP,
	STARVATION,
	CAMP_AND_EAT, 
	LEVEL_UP, 
	
	/* Story Mode */
	
	COTTAGE_TRAINER (AssetEnum.COTTAGE),
	COTTAGE_TRAINER_VISIT (AssetEnum.COTTAGE),
	TOWN_STORY (AssetEnum.TOWN),
	FIRST_BATTLE_STORY (6), 
	MERI_COTTAGE (AssetEnum.COTTAGE),
	MERI_COTTAGE_VISIT (AssetEnum.COTTAGE), 
	OGRE_WARNING_STORY (FOREST_INACTIVE),
	OGRE_STORY, 
	ECCENTRIC_MERCHANT,
	STORY_FEM (FOREST_INACTIVE), 
	STORY_SIGN (FOREST_INACTIVE), 
	SOUTH_PASS (MOUNTAIN_ACTIVE), 
	WEST_PASS (MOUNTAIN_ACTIVE), 
	;
	
	private final AssetEnum texture;	
	private final int battleCode;	
	private EncounterCode() {
		this(FOREST_ACTIVE);
	}
	
	private EncounterCode(AssetEnum texture) {
		this(texture, -1);
	}
	
	private EncounterCode(int battleCode) {
		this(FOREST_ACTIVE, battleCode);
	}
	
	private EncounterCode(AssetEnum texture, int battleCode) {
		this.texture = texture;
		this.battleCode = battleCode;
	}
	
	public AssetEnum getTexture() { return texture; }
	public int getBattleCode() { return battleCode; }
	
	public String getDescription(int visibility) {
		switch(visibility) {
		
		
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
					case ADVENTURER: return "Adventurer";
					case GADGETEER: return "Merchant";
					case TOWN: return "Small Settlement";
					case TOWN2:
					case TOWN_STORY:
						return "Town of Nadir";	
					case FORT:
						return "Fort";
					case CRIER_QUEST: return "Mountain";
					case COTTAGE_TRAINER: return "Cottage-on-the-Outskirts";
					case FIRST_BATTLE_STORY: return "Forest Clearing";
					case MERI_COTTAGE: return "Witch's Cottage";
					case ECCENTRIC_MERCHANT: return "Merchant Path";
					case OGRE_WARNING_STORY: return "Lean-to in the Forest";
					case OGRE_STORY: return "Forest Pass";
					case SOUTH_PASS: return "South Pass";
					case STORY_FEM: return "Unwalked Path";
					case STORY_SIGN: return "Crossroads";
					case WEST_PASS: return "West Pass";
					default: return "Unknown - No Info for encounter #" + this + " and perception level = " + visibility;
			}
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
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
					case OGRE: return "Ogre - Danger!";
					case BEASTMISTRESS: return "Drow Beastmistress - Hostile!";
					case GADGETEER: return "Suspicious Merchant";
					case TOWN: return "Town of Silajam";
					case TOWN2:
					case TOWN_STORY: return "Town of Nadir";
					case FORT:
						return "Fort";
					case CRIER_QUEST: return "Mount Wip";
					case COTTAGE_TRAINER: return "Cottage-on-the-Outskirts";
					case FIRST_BATTLE_STORY: return "Forest Clearing - signs of hostile creature";
					case MERI_COTTAGE: return "Witch's Cottage";
					case ECCENTRIC_MERCHANT: return "Merchant Path";
					case OGRE_WARNING_STORY: return "Lean-to in the Forest";
					case OGRE_STORY: return "Forest Pass";
					case SOUTH_PASS: return "South Pass";
					case STORY_FEM: return "Unwalked Path";
					case STORY_SIGN: return "Crossroads";
					case WEST_PASS: return "West Pass";
					default: return "Unknown - No Info for encounter #" + this  + " and perception level = " + visibility;
				}
			default: return "Perception level error.";
		}
	}
	
	public String getFullDescription() {
		switch(this) {
			case TOWN: return "Town of Silajam (visited)";
			case TOWN2: return "Town of Nadir (visited)";
			case COTTAGE_TRAINER_VISIT: return "Cottage-on-the-Outskirts (visited)";
			case MERI_COTTAGE_VISIT: return "Witch's Cottage (visited)";
			case GADGETEER: return "Strange Gadgeteer";
			case FORT: return "Fort";
			default: return "Nothing here.";
		}
	}

	public Array<AssetDescriptor<?>> getRequirements() {
		switch (this) {
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
			case INITIAL:
				return new Array<AssetDescriptor<?>>(new AssetDescriptor[]{  
					AssetEnum.GAME_TYPE_BACKGROUND.getTexture(),
					AssetEnum.CLASS_SELECT_BACKGROUND.getTexture(),
					AssetEnum.STRENGTH.getTexture(),
					AssetEnum.ENDURANCE.getTexture(),
					AssetEnum.AGILITY.getTexture(),
					AssetEnum.PERCEPTION.getTexture(),
					AssetEnum.MAGIC.getTexture(),
					AssetEnum.CHARISMA.getTexture(),
					AssetEnum.SILHOUETTE.getTexture(),
					AssetEnum.BURNING_FORT_BG.getTexture(),
					AssetEnum.NORMAL_BOX.getTexture(),
					AssetEnum.SKILL_SELECTION_BACKGROUND.getTexture(),
					AssetEnum.CHARACTER_CUSTOM_BACKGROUND.getTexture(),
					AssetEnum.SMUG_LAUGH.getSound(),
					AssetEnum.WAVES.getMusic(),
					AssetEnum.HOVEL_MUSIC.getMusic(),
					AssetEnum.INITIAL_MUSIC.getMusic()
				});
			case DEFAULT:
				return new Array<AssetDescriptor<?>>(new AssetDescriptor[]{AssetEnum.STICK_BACKGROUND.getTexture()});
			case WERESLUT:
				return new Array<AssetDescriptor<?>>(new AssetDescriptor[]{  
					AssetEnum.WEREWOLF_MUSIC.getMusic(),
					AssetEnum.WEREBITCH.getTexture()
				});
			case HARPY:
				return new Array<AssetDescriptor<?>>(new AssetDescriptor[]{ AssetEnum.HARPY_FELLATIO_1.getTexture()});
			case SLIME:
				return new Array<AssetDescriptor<?>>(new AssetDescriptor[]{  
					AssetEnum.SLIME.getTexture(),
					AssetEnum.SLIME_DOGGY.getTexture()
				});
			case BRIGAND:
				return new Array<AssetDescriptor<?>>(new AssetDescriptor[]{AssetEnum.BRIGAND_ORAL.getTexture()});
			case DRYAD:
				return new Array<AssetDescriptor<?>>(new AssetDescriptor[]{  
				AssetEnum.SHOP_MUSIC.getMusic(),
				AssetEnum.DRYAD_BACKGROUND.getTexture()
				});
			case CENTAUR:
				return new Array<AssetDescriptor<?>>(new AssetDescriptor[]{AssetEnum.SHOP_MUSIC.getMusic()});
			case GADGETEER:
				return new Array<AssetDescriptor<?>>(new AssetDescriptor[]{  
					AssetEnum.GADGETEER.getTexture(),
					AssetEnum.BATTLE_TEXTBOX.getTexture(),
					AssetEnum.TEXT_BOX.getTexture(),
					AssetEnum.EQUIP.getSound(),
					AssetEnum.GADGETEER_MUSIC.getMusic()
				});
			case ORC:
				return new Array<AssetDescriptor<?>>(new AssetDescriptor[]{  
					AssetEnum.ORC.getTexture(),
					AssetEnum.ORC_PRONE_BONE.getTexture(),
					AssetEnum.GAPE.getTexture(),
					AssetEnum.WEREWOLF_MUSIC.getMusic()
				});
			case ADVENTURER:
			case STORY_FEM:
				return new Array<AssetDescriptor<?>>(new AssetDescriptor[]{  
					AssetEnum.ADVENTURER.getTexture(),
					AssetEnum.GADGETEER_MUSIC.getMusic()
				});
			case OGRE:
				return new Array<AssetDescriptor<?>>(new AssetDescriptor[]{  
					AssetEnum.OGRE.getTexture(),
					AssetEnum.OGRE_BANGED.getTexture(),
					AssetEnum.WEREWOLF_MUSIC.getMusic()
				});
			case BEASTMISTRESS:
				return new Array<AssetDescriptor<?>>(new AssetDescriptor[]{  
					AssetEnum.BEASTMISTRESS.getTexture(),
					AssetEnum.WEREWOLF_MUSIC.getMusic()
				});
			case INN:
				return new Array<AssetDescriptor<?>>(new AssetDescriptor[]{  
					AssetEnum.INNKEEPER.getTexture(),
					AssetEnum.KEYHOLE.getTexture(),
					AssetEnum.GAME_OVER_KEYHOLE.getTexture()
				});
			case FIRST_BATTLE_STORY:
				return new Array<AssetDescriptor<?>>(new AssetDescriptor[]{
					AssetEnum.FOREST_BG.getTexture(),
					AssetEnum.GOBLIN.getTexture(),
					AssetEnum.GAME_OVER_TUCKERED.getTexture(),
					AssetEnum.LOUD_LAUGH.getSound(),
					AssetEnum.WEREWOLF_MUSIC.getMusic()
					
				});
			case GOBLIN:
				return new Array<AssetDescriptor<?>>(new AssetDescriptor[]{  
					AssetEnum.GOBLIN.getTexture(),
					AssetEnum.GOBLIN_MALE.getTexture(),
					AssetEnum.GAME_OVER_TUCKERED.getTexture(),
					AssetEnum.LOUD_LAUGH.getSound(),
					AssetEnum.WEREWOLF_MUSIC.getMusic(),
					AssetEnum.CARNIVAL_MUSIC.getMusic()
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
			case TOWN_STORY:
				Array<AssetDescriptor<?>> reqs = new Array<AssetDescriptor<?>>(new AssetDescriptor[]{  
					AssetEnum.TOWN_BG.getTexture(),
					AssetEnum.SMUG_LAUGH.getSound(),
					AssetEnum.SHOP_MUSIC.getMusic()
				});
				reqs.addAll(TownScreen.resourceRequirements);
				return reqs;				
			case SHOP:
			case WEAPON_SHOP:
				return TownScreen.resourceRequirements;
			case OGRE_WARNING_STORY:
				return new Array<AssetDescriptor<?>>(new AssetDescriptor[]{AssetEnum.TRAINER_MUSIC.getMusic()});
			case OGRE_STORY:
				return new Array<AssetDescriptor<?>>(new AssetDescriptor[]{  
					AssetEnum.GAME_OGRE.getTexture(),
					AssetEnum.OGRE_GROWL.getSound(),
					AssetEnum.WEREWOLF_MUSIC.getMusic(),
					AssetEnum.HEAVY_MUSIC.getMusic()
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
			case CAMP_AND_EAT:
				return new Array<AssetDescriptor<?>>(new AssetDescriptor[]{AssetEnum.SHOP_MUSIC.getMusic()});
			case STARVATION:
				return new Array<AssetDescriptor<?>>(new AssetDescriptor[]{  
					AssetEnum.GAME_OVER_TUCKERED.getTexture(),
					AssetEnum.WEREWOLF_MUSIC.getMusic()
				});
			default:
				return new Array<AssetDescriptor<?>>(new AssetDescriptor[]{AssetEnum.TRAP_BONUS.getTexture()});
		}
	}
	// for random gen
	public static Array<EncounterCode> encounterArray;
	static {
		encounterArray = new Array<EncounterCode>();
		encounterArray.addAll(WERESLUT, HARPY, SLIME, BRIGAND, DRYAD, CENTAUR, GOBLIN, ORC, ADVENTURER, OGRE, BEASTMISTRESS);
	}
	
	public static EncounterCode getEncounterCode(int code) {
		if (code < encounterArray.size) {
			return encounterArray.get(code);
		}
		// Troja?  Need a way to get to here; currently no file input is read for the code param
		return ERROR;
	}
}

package com.majalis.encounter;

import static com.majalis.asset.AssetEnum.*;

import com.badlogic.gdx.utils.Array;
import com.majalis.asset.AssetEnum;
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
	FORT (CASTLE), 
	TOWN (AssetEnum.TOWN),
	TOWN2 (AssetEnum.TOWN),
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
	GADGETEER (FOREST_INACTIVE)
	;
	
	private final String texturePath;	
	private final int battleCode;	
	private EncounterCode(){
		this(FOREST_ACTIVE);
	}
	
	private EncounterCode(AssetEnum texture){
		this(texture, -1);
	}
	
	private EncounterCode(int battleCode){
		this(FOREST_ACTIVE, battleCode);
	}
	
	private EncounterCode(AssetEnum texture, int battleCode){
		this.texturePath = texture.getPath();
		this.battleCode = battleCode;
	}
	
	public String getTexturePath(){	return texturePath; }
	public int getBattleCode(){ return battleCode; }
	
	// for random gen
	public static Array<EncounterCode> encounterArray;
	static {
		encounterArray = new Array<EncounterCode>();
		encounterArray.addAll(WERESLUT, HARPY, SLIME, BRIGAND, DRYAD, CENTAUR, GOBLIN);
	}
	
	public static EncounterCode getEncounterCode(int code){
		if (code < encounterArray.size){
			return encounterArray.get(code);
		}
		// Troja?  Need a way to get to here; currently no file input is read for the code param
		return ERROR;
	}
}

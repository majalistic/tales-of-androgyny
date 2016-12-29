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
	FORT (CASTLE), 
	TOWN (AssetEnum.TOWN),
	TOWN2 (AssetEnum.TOWN),
	TOWN_STORY (AssetEnum.TOWN),
	COTTAGE_TRAINER,
	COTTAGE_TRAINER_VISIT,
	FIRST_BATTLE_STORY, 
	LEVEL_UP, 
	SHOP, 
	STARVATION,
	CAMP_AND_EAT, 
	GOBLIN (ENCHANTED_FOREST, 6)
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

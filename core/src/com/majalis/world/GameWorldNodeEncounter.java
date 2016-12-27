package com.majalis.world;

import com.majalis.encounter.EncounterCode;
import com.majalis.save.SaveManager;

/*
 * Represents the stub information for an encounter to be retrieved when that encounter is created.  This will eventually need to be refactored to bundle together two EncounterCodes and do little else, or removed entirely.
 */
public class GameWorldNodeEncounter {

	private final EncounterCode encounterCode;
	private final EncounterCode defaultEncounterCode;
	
	public GameWorldNodeEncounter(EncounterCode initialEncounter, EncounterCode defaultEncounter){
		this.encounterCode = initialEncounter;
		this.defaultEncounterCode = defaultEncounter;
	}
	
	public EncounterCode getCode(){
		return encounterCode;
	}
	
	public EncounterCode getDefaultCode() {
		return defaultEncounterCode;
	}
	
	public SaveManager.GameContext getContext(){
		return encounterCode == EncounterCode.TOWN || encounterCode == EncounterCode.TOWN2 ? SaveManager.GameContext.TOWN : SaveManager.GameContext.ENCOUNTER;
	}
	
	public SaveManager.GameContext getDefaultContext(){
		return defaultEncounterCode == EncounterCode.TOWN || defaultEncounterCode == EncounterCode.TOWN2 ? SaveManager.GameContext.TOWN : SaveManager.GameContext.ENCOUNTER;
	}
	
	public String getDescription(int visibility, boolean visited){
		if (visited){
			return getDefaultDescription(visibility);
		}
		switch(visibility){
			case 0:
				return "You are unsure of what awaits you!";
			case 1:
				switch (encounterCode){
					case WERESLUT: return "Wereslut";
					case HARPY: return "Harpy";
					case SLIME: return "Slime";
					case BRIGAND: return "Brigand";
					case DRYAD: return "Dryad";
					case CENTAUR: return "Centaur";
					case TOWN: return "Small Settlement";
					case TOWN2:
					case TOWN_STORY:
						return "Town of Nadir";	
					case COTTAGE_TRAINER: return "Cottage-on-the-Outskirts";
					case FIRST_BATTLE_STORY: return "Forest Clearing";
					default: return "Unknown - No Info for encounter #" + encounterCode + " and perception level = " + visibility;
			}
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
				switch (encounterCode){
					case WERESLUT: return "Wereslut - Hostile!";
					case HARPY: return "Harpy - Hostile!";
					case SLIME: return "Slime - Neutral";
					case BRIGAND: return "Brigand - Hostile!";
					case DRYAD: return "Dryad - Peaceful";
					case CENTAUR: return "Centaur - Neutral";
					case TOWN: return "Town of Silajam";
					case TOWN2:
					case TOWN_STORY: return "Town of Nadir";
					case COTTAGE_TRAINER: return "Cottage-on-the-Outskirts";
					case FIRST_BATTLE_STORY: return "Forest Clearing - signs of harpies";
					default: return "Unknown - No Info for encounter #" + encounterCode  + " and perception level = " + visibility;
				}
			default: return "Perception level error.";
		}
	}
	
	private String getDefaultDescription(int visibility){
		switch (defaultEncounterCode){
			case TOWN: return "Town of Silajam (visited)";
			case TOWN2: return "Town of Nadir (visited)";
			case COTTAGE_TRAINER: return "Cottage-on-the-Outskirts (visited)";
			default: return "Nothing here.";
		}
	}
}

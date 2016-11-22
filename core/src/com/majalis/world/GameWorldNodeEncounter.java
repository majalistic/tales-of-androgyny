package com.majalis.world;

import com.majalis.save.SaveManager;

/*
 * Represents the stub information for an encounter to be retrieved when that encounter is created.
 */
public class GameWorldNodeEncounter {

	private final int encounterCode;
	private final int defaultEncounterCode;
	
	public GameWorldNodeEncounter(int encounterCode, int defaultEncounterCode){
		this.encounterCode = encounterCode;
		this.defaultEncounterCode = defaultEncounterCode;
	}
	
	public int getCode(){
		return encounterCode;
	}
	
	public int getDefaultCode() {
		return defaultEncounterCode;
	}
	
	public SaveManager.GameContext getContext(){
		return encounterCode == 1000 || encounterCode == 2000 ? SaveManager.GameContext.TOWN : SaveManager.GameContext.ENCOUNTER;
	}
	
	public SaveManager.GameContext getDefaultContext(){
		return defaultEncounterCode == 1000 || defaultEncounterCode == 2000 ? SaveManager.GameContext.TOWN : SaveManager.GameContext.ENCOUNTER;
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
					case 0: return "Wereslut";
					case 1: return "Harpy";
					case 2: return "Slime";
					case 3: return "Brigand";
					case 4: return "Dryad";
					case 1000: return "Small Settlement";
					case 2000: return "Town of Nadir";
					default: return "Unknown - No Info for encounter #" + encounterCode + " and perception level = 1";
			}
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
				switch (encounterCode){
					case 0: return "Wereslut - Hostile!";
					case 1: return "Harpy - Hostile!";
					case 2: return "Slime - Neutral";
					case 3: return "Brigand - Hostile!";
					case 4: return "Dryad - Peaceful";
					case 1000: return "Town of Silajam";
					case 2000: return "Town of Nadir";
					default: return "Unknown - No Info for encounter #" + encounterCode  + " and perception level = 2";
				}
			default: return "Perception level error.";
		}
	}
	
	private String getDefaultDescription(int visibility){
		switch (defaultEncounterCode){
			case 1000: return "Town of Silajam (visited)";
			default: return "Nothing here.";
		}
	}
}

package com.majalis.world;

import com.majalis.encounter.EncounterCode;
import com.majalis.save.SaveManager;

/*
 * Represents the stub information for an encounter to be retrieved when that encounter is created.  This will eventually need to be refactored to bundle together two EncounterCodes and do little else, or removed entirely.
 */
public class GameWorldNodeEncounter {

	private final EncounterCode encounterCode;
	private final EncounterCode defaultEncounterCode;
	
	public GameWorldNodeEncounter(EncounterCode initialEncounter, EncounterCode defaultEncounter) {
		this.encounterCode = initialEncounter;
		this.defaultEncounterCode = defaultEncounter;
	}
	
	public EncounterCode getCode() {
		return encounterCode;
	}
	
	public EncounterCode getDefaultCode() {
		return defaultEncounterCode;
	}
	
	public SaveManager.GameContext getContext() {
		return encounterCode == EncounterCode.TOWN || encounterCode == EncounterCode.TOWN2 ? SaveManager.GameContext.TOWN : SaveManager.GameContext.ENCOUNTER;
	}
	
	public SaveManager.GameContext getDefaultContext() {
		return defaultEncounterCode == EncounterCode.TOWN || defaultEncounterCode == EncounterCode.TOWN2 ? SaveManager.GameContext.TOWN : SaveManager.GameContext.ENCOUNTER;
	}
	
	public String getDescription(int visibility, boolean visited) {
		if (visited) {
			return defaultEncounterCode.getFullDescription();
		}
		return encounterCode.getDescription(visibility);
	}
}

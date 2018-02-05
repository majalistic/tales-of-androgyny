package com.majalis.world;

import com.badlogic.gdx.utils.Array;
import com.majalis.encounter.EncounterCode;

/*
 * Represents the stub information for an encounter to be retrieved when that encounter is created.  This will eventually need to be refactored to bundle together two EncounterCodes and do little else, or removed entirely.
 */
public class GameWorldNodeEncounter {

	private final EncounterCode encounterCode;
	private final EncounterCode defaultEncounterCode;
	private final Array<EncounterCode> randomEncounterCodes;
	
	public GameWorldNodeEncounter(EncounterCode initialEncounter, EncounterCode defaultEncounter) { this(initialEncounter, defaultEncounter, new Array<EncounterCode>()); }	
	public GameWorldNodeEncounter(EncounterCode initialEncounter, EncounterCode defaultEncounter, Array<EncounterCode> randomEncounterCodes) {
		this.encounterCode = initialEncounter;
		this.defaultEncounterCode = defaultEncounter;
		this.randomEncounterCodes = randomEncounterCodes;
	}
	
	public EncounterCode getCode() { return encounterCode; }	
	public EncounterCode getDefaultCode() { return defaultEncounterCode; }
	public EncounterCode getRandomEncounterCode(int seedValue) { return !hasRespawns() ? defaultEncounterCode : randomEncounterCodes.get(seedValue % randomEncounterCodes.size); }
	public boolean hasRespawns() { return randomEncounterCodes.size != 0; }
}

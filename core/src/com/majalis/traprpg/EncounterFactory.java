package com.majalis.traprpg;
/*
 * Retrieves encounters from internal files given an encounterId
 */
public class EncounterFactory {

	public Encounter getEncounter(int encounterCode) {
		// temporarily stored in a static switch block until file retrieval for encounters is implemented
		switch (encounterCode){
			case 0: 
			case 1:
			case 2:
			default:
		}
		return new Encounter(null);
	}
	
}

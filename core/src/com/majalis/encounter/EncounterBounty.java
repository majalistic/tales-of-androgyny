package com.majalis.encounter;

import com.majalis.character.Item.Misc;
import com.majalis.character.Item.MiscType;
import com.majalis.character.SexualExperience.SexualExperienceBuilder;
import com.majalis.save.SaveEnum;
import com.majalis.save.SaveService;
import com.majalis.scenes.Mutation;

public class EncounterBounty {
	private final EncounterCode type;
	
	protected EncounterBounty(EncounterCode type) {
		this.type = type;
	}
	
	public String getDescription(int scoutingScore) {
		switch (type) {
			case FOOD_CACHE: return "Found 10 food!";
			case GOLD_CACHE: return "Found 10 gold!";
			case ICE_CREAM: return "Ice Cream!";
			case HUNGER_CHARM: return "Found some kind of charm!";
			case DAMAGE_TRAP: return "Agh! A pitfall! You take 10 damage.";
			case ANAL_TRAP: return "Some kind of tentacle forced its way up your butt!";
			default: return "";
		}
		
	}

	public void execute(int scoutingScore, SaveService saveService) {
		switch (type) {
			case FOOD_CACHE: new Mutation(saveService, SaveEnum.FOOD, 10).mutate();
			case GOLD_CACHE: new Mutation(saveService, SaveEnum.GOLD, 10).mutate();
			case ICE_CREAM:  new Mutation(saveService, SaveEnum.ITEM, new Misc(MiscType.ICE_CREAM)).mutate();
			case HUNGER_CHARM: new Mutation(saveService, SaveEnum.ITEM, new Misc(MiscType.HUNGER_CHARM)).mutate();
			case DAMAGE_TRAP: new Mutation(saveService, SaveEnum.HEALTH, -10).mutate();
			case ANAL_TRAP: new Mutation(saveService, SaveEnum.ANAL, new SexualExperienceBuilder().setAnalSex(1, 0, 0).build()).mutate();
			default: return;
		}
	}
}

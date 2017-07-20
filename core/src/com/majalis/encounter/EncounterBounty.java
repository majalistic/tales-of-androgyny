package com.majalis.encounter;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Array;
import com.majalis.asset.AssetEnum;
import com.majalis.character.Item.Misc;
import com.majalis.character.Item.MiscType;
import com.majalis.character.SexualExperience.SexualExperienceBuilder;
import com.majalis.save.MutationResult;
import com.majalis.save.SaveEnum;
import com.majalis.save.SaveService;
import com.majalis.scenes.Mutation;

public class EncounterBounty {
	private final EncounterCode type;
	
	protected EncounterBounty(EncounterCode type) {
		this.type = type;
	}

	public EncounterBountyResult execute(int scoutingScore, SaveService saveService) {
		switch (type) {
			case FOOD_CACHE: return new EncounterBountyResult("You discovered a cache with food rations in it!" + getResults(new Mutation(saveService, SaveEnum.FOOD, scoutingScore < 3 ? 10 : 15).mutate()), AssetEnum.EQUIP.getSound());
			case GOLD_CACHE: return new EncounterBountyResult("You discovered a treasure cache!" + getResults(new Mutation(saveService, SaveEnum.GOLD, scoutingScore < 3 ? 10 : 15).mutate()), AssetEnum.EQUIP.getSound());
			case ICE_CREAM:  return new EncounterBountyResult("Ice cream! " +getResults( new Mutation(saveService, SaveEnum.ITEM, new Misc(MiscType.ICE_CREAM)).mutate()), AssetEnum.EQUIP.getSound());
			case HUNGER_CHARM: return scoutingScore < 3 ? new EncounterBountyResult("You find nothing of use in the cache. Are you missing something?", null) : new EncounterBountyResult(getResults(new Mutation(saveService, SaveEnum.ITEM, new Misc(MiscType.HUNGER_CHARM)).mutate()), AssetEnum.EQUIP.getSound());
			case DAMAGE_TRAP: return new EncounterBountyResult(scoutingScore < 3 ? "Agh! A pitfall! " + getResults(new Mutation(saveService, SaveEnum.HEALTH, -10).mutate()) : "You discovered a pitfall, but avoided it.",  scoutingScore < 3 ? AssetEnum.SWORD_SLASH_SOUND.getSound() : null);
			case ANAL_TRAP: return new EncounterBountyResult(scoutingScore < 3 ?  "Some kind of tentacle forced its way up your butt! " + getResults(new Mutation(saveService, SaveEnum.ANAL, new SexualExperienceBuilder().setAnalSex(1, 0, 0).build()).mutate()) : "You avoided a rogue tentacle. Naughty!", scoutingScore < 3 ? AssetEnum.THWAPPING.getSound() : null);
			default: return null;
		}
	}
	
	private String getResults(Array<MutationResult> results) {
		String result = "";
		for (MutationResult mr : results) {
			result += "\n" + mr.getText();
		}
		return result;
	}
	
	
	public static class EncounterBountyResult {
		private final String displayText;
		private final AssetDescriptor<Sound> soundToPlay;
		
		private EncounterBountyResult(String displayText, AssetDescriptor<Sound> soundToPlay) {
			this.displayText = displayText;
			this.soundToPlay = soundToPlay;
		}
		
		public String displayText() { return displayText; }
		public AssetDescriptor<Sound> soundToPlay() { return soundToPlay; }
	}
}

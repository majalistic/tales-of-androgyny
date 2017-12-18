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
			case FOOD_CACHE: return new EncounterBountyResult("You discovered a cache with food rations in it!", new Mutation(saveService, SaveEnum.FOOD, scoutingScore < 3 ? 10 : 15).mutate(), AssetEnum.EQUIP.getSound());
			case GOLD_CACHE: return new EncounterBountyResult("You discovered a treasure cache!", new Mutation(saveService, SaveEnum.GOLD, scoutingScore < 3 ? 10 : 15).mutate(), AssetEnum.EQUIP.getSound());
			case ICE_CREAM:  return new EncounterBountyResult("Ice cream!", new Mutation(saveService, SaveEnum.ITEM, new Misc(MiscType.ICE_CREAM)).mutate(), AssetEnum.EQUIP.getSound());
			case HUNGER_CHARM: return scoutingScore < 3 ? new EncounterBountyResult("You find nothing of use in the cache. Are you missing something?", null, null) : new EncounterBountyResult("", new Mutation(saveService, SaveEnum.ITEM, new Misc(MiscType.HUNGER_CHARM)).mutate(), AssetEnum.EQUIP.getSound());
			case DAMAGE_TRAP: return scoutingScore < 3 ? new EncounterBountyResult( "Agh! A pitfall! ", new Mutation(saveService, SaveEnum.HEALTH, -10).mutate(), AssetEnum.SWORD_SLASH_SOUND.getSound()) : new EncounterBountyResult("You discovered a pitfall, but avoided it.", null, null);
			case ANAL_TRAP: return scoutingScore < 3 ? new EncounterBountyResult("Some kind of tentacle forced its way up your butt!\n", new Mutation(saveService, SaveEnum.ANAL, new SexualExperienceBuilder().setAnalSex(1, 0, 0).build()).mutate(), AssetEnum.THWAPPING.getSound()) : new EncounterBountyResult("You avoided a rogue tentacle. Naughty!", null, null);
			default: return null;
		}
	}
	
	public static class EncounterBountyResult {
		private final String displayText;
		private final Array<MutationResult> result;
		private final AssetDescriptor<Sound> soundToPlay;
		
		private EncounterBountyResult(String displayText, Array<MutationResult> result, AssetDescriptor<Sound> soundToPlay) {
			this.displayText = displayText;
			this.result = result != null ? result : new Array<MutationResult>();
			this.soundToPlay = soundToPlay;
		}
		
		public String displayText() { return displayText; }
		public AssetDescriptor<Sound> soundToPlay() { return soundToPlay; }
		public Array<MutationResult> getResults() { return result; }
	}
}

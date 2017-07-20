package com.majalis.scenes;

import com.badlogic.gdx.utils.Array;
import com.majalis.save.MutationResult;
import com.majalis.save.ProfileEnum;
import com.majalis.save.SaveEnum;
import com.majalis.save.SaveService;
/*
 * Component of TextScenes that allows them to mutate the gamestate.  Mutations have a save attribute to mutate and know whether to overwrite or read and modify.  Will likely need a MutationBuilder with a fluent interface
 */
public class Mutation {

	private final SaveService saveService;
	private SaveEnum path;
	private ProfileEnum pathProfile;
	private final Object value;
		
	public Mutation(SaveService saveService, SaveEnum path, Object value) {
		this.saveService = saveService;
		this.path = path;
		this.value = value;
	}
	
	public Mutation(SaveService saveService, ProfileEnum pathProfile, Object value) {
		this.saveService = saveService;
		this.pathProfile = pathProfile;
		this.value = value;
	}

	public  Array<MutationResult> mutate() {
		if (saveService == null) {
			return null;
		}
		if (path != null) {
			return saveService.saveDataValue(path, value, false);
		}
		else {
			saveService.saveDataValue(pathProfile, value, true);
			return null;
		}
	}
	
}

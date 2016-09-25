package com.majalis.scenes;

import com.majalis.save.SaveEnum;
import com.majalis.save.SaveService;
/*
 * Component of TextScenes that allows them to mutate the gamestate.  Mutations have a save attribute to mutate and know whether to overwrite or read and modify.  Will likely need a MutationBuilder with a fluent interface
 */
public class Mutation {

	private final SaveService saveService;
	private final SaveEnum path;
	private final Object value;

	public Mutation(){
		this(null, null, null);
	}
		
	public Mutation(SaveService saveService, SaveEnum path, Object value){
		this.saveService = saveService;
		this.path = path;
		this.value = value;
	}
	
	public void mutate() {
		if (saveService == null){
			return;
		}
		saveService.saveDataValue(path, value);
	}
}

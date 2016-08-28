package com.majalis.scenes;

import com.majalis.save.LoadService;
import com.majalis.save.SaveEnum;
import com.majalis.save.SaveService;

/*
 * Component of TextScenes that allows them to mutate the gamestate.  Mutations have a save attribute to mutate and know whether to overwrite or read and modify.  Will likely need a MutationBuilder with a fluent interface
 */
public class Mutation {

	private final SaveService saveService;
	private final LoadService loadService;
	private final boolean overwrite;
	private final SaveEnum path;
	private final Class<?> type;
	private final Object value;

	public Mutation(){
		this(null, null, null);
	}
	
	public Mutation(SaveService saveService, SaveEnum path, Object value){
		this(saveService, null, path, value, null, true);
	}
		
	public Mutation(SaveService saveService, LoadService loadService, SaveEnum path, Object value, Class<?> type, boolean overwrite){
		this.saveService = saveService;
		this.loadService = loadService;
		this.overwrite = overwrite;
		this.path = path;
		this.type = type;
		this.value = value;
	}
	
	public void mutate() {
		if (saveService == null){
			return;
		}
		if (overwrite){
			saveService.saveDataValue(path, value);
		}
		else {
			mutate(type);
		}
	}
	
	private <T> void mutate(Class<?> type){
		// currently performs string concatenation on all non-overrides
		saveService.saveDataValue(path, loadService.loadDataValue(path, type).toString() + value.toString());
	}

	
	
}

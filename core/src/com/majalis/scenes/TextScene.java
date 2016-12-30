package com.majalis.scenes;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.OrderedMap;
import com.majalis.encounter.Background;
import com.majalis.save.SaveEnum;
import com.majalis.save.SaveService;
import com.majalis.screens.EncounterScreen;

public class TextScene extends AbstractTextScene  {
	
	private final String toDisplay;
	private final Array<Mutation> mutations;
	private final Background background;
	private String music;
	private String sound;
	
	public TextScene(OrderedMap<Integer, Scene> sceneBranches, int sceneCode, SaveService saveService, BitmapFont font, Background background, String toDisplay, Array<Mutation> mutations) {
		super(sceneBranches, sceneCode, saveService, font, background);
		this.toDisplay = toDisplay;
		this.mutations = mutations;
		this.background = background;
	}
	
	public void setMusic(String music){
		this.music = music;
	}
	
	public void setSound(String sound){
		this.sound = sound;
	}

	@Override
	protected String getDisplay(){
		return toDisplay;
	}
	
	@Override
	public void setActive() {
		for (Mutation mutator: mutations){
			mutator.mutate();
		}
		super.setActive();
		if (music != null) {
			EncounterScreen.setMusic(music);
			saveService.saveDataValue(SaveEnum.MUSIC, music);
		};
		if (sound != null){
			EncounterScreen.play(sound);
		}
		background.initEnemy();
	}
	// this type of TextScene will be one that always pipes from one scene to the next with no branch - there will be another TextScene that actually has branching logic
	@Override
	protected void nextScene(){
		sceneBranches.get(sceneBranches.orderedKeys().get(0)).setActive();
		isActive = false;
		addAction(Actions.hide());
	}
}

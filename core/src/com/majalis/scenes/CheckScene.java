package com.majalis.scenes;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.OrderedMap;
import com.majalis.character.PlayerCharacter;
import com.majalis.character.AbstractCharacter.Stat;
import com.majalis.encounter.Background;
import com.majalis.save.SaveService;

public class CheckScene extends AbstractTextScene {

	private final PlayerCharacter character;
	private final Stat statToCheck;
	private final OrderedMap<Integer, Scene> checkValues;
	private String toDisplay;
	private Scene nextScene;
	public CheckScene(OrderedMap<Integer, Scene> sceneBranches, int sceneCode, SaveService saveService, BitmapFont font, Background background, Stat stat, OrderedMap<Integer, Scene> checkValues, PlayerCharacter character) {
		super(sceneBranches, sceneCode, saveService, font, background);
		this.statToCheck = stat;
		this.checkValues = checkValues;
		this.character = character;
		toDisplay = "";
	}

	@Override
	public void setActive() {
		super.setActive();
		int amount = statToCheck == Stat.CHARISMA ? character.getLewdCharisma() : character.getStat(statToCheck);
		toDisplay += "Your " + statToCheck.toString() + " score: " + amount + "\n\n";
		for (Integer threshold : checkValues.keys()){
			if (threshold == 0){
				nextScene = checkValues.get(threshold);
			}
			else {
				toDisplay += statToCheck.toString() + " check (" + threshold + "): ";
				if (amount >= threshold){
					nextScene = checkValues.get(threshold);
					toDisplay += "PASSED!\n";
					break;
				}
				else {
					toDisplay += "FAILURE!\n";
				}
			}
		}
	}

	@Override
	protected String getDisplay(){
		return toDisplay;
	}
	
	
	@Override
	protected void nextScene() {
		nextScene.setActive();
		isActive = false;
		addAction(Actions.hide());	
	}
}

package com.majalis.character;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.color;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.forever;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.majalis.asset.AssetEnum;

public class LevelBar extends Group {
	private final ProgressBar levelBar;
	private final Label levelLabel;
	private final Label storedLevelLabel;
	private final PlayerCharacter character;
	public LevelBar(PlayerCharacter character, AssetManager assetManager, Skin skin) {
		this.character = character;
		levelBar = new ProgressBar(0, 1, .01f, false, assetManager.get(AssetEnum.LEVEL_UP_SKIN.getSkin()));			
		levelBar.setValue(character.getPercentToLevel());
		levelBar.setBounds(200, 165, 352, 65);
		this.addActor(levelBar);
		levelLabel = new Label("" + character.getLevel(), skin);		
		levelLabel.setPosition(291, 208);
		levelLabel.setColor(Color.LIGHT_GRAY);
		this.addActor(levelLabel);
		storedLevelLabel = new Label(character.getStoredLevels() > 0 ? "LEVEL UP" + (character.getStoredLevels() > 1 ? " X " + character.getStoredLevels() : "") + "!" : "", skin);		
		storedLevelLabel.setPosition(345, 215);
		storedLevelLabel.setColor(Color.GOLD);
		storedLevelLabel.addAction(forever(sequence(getColorSequence(Color.PINK, Color.BLUE, Color.GREEN, Color.TAN, Color.GOLD))));
		this.addActor(storedLevelLabel);
	}
	@Override
	public void draw(Batch batch, float parentAlpha) {
		if(Math.abs(levelBar.getValue() - character.getPercentToLevel()) > .01) {
			levelBar.setValue(levelBar.getValue() + .01f);
			if (Math.abs(levelBar.getValue() - levelBar.getMaxValue()) < .01) levelBar.setValue(levelBar.getMinValue());
		}
		
		levelLabel.setText("" + character.getLevel());
		storedLevelLabel.setText(character.getStoredLevels() > 0 ? "LEVEL UP" + (character.getStoredLevels() > 1 ? " X " + character.getStoredLevels() : "") + "!" : "");
		super.draw(batch, parentAlpha);
	}	
	
	private static Action[] getColorSequence(Color ... colors) {
		Array<Action> colorActions = new Array<Action>();
		for (Color color : colors) {
			colorActions.add(color(color, .25f));
		}
		return colorActions.toArray(Action.class);
	}
}
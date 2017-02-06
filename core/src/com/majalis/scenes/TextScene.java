package com.majalis.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.OrderedMap;
import com.majalis.character.PlayerCharacter;
import com.majalis.encounter.Background;
import com.majalis.save.SaveEnum;
import com.majalis.save.SaveService;
import com.majalis.screens.EncounterScreen;

public class TextScene extends AbstractTextScene  {
	
	private final Array<Mutation> mutations;
	private final Background background;
	private final AssetManager assetManager;
	private final PlayerCharacter character;
	private final String music;
	private final String sound;
	
	public TextScene(OrderedMap<Integer, Scene> sceneBranches, int sceneCode, AssetManager assetManager, BitmapFont font, SaveService saveService, Background background, String toDisplay, Array<Mutation> mutations, PlayerCharacter character, String music, String sound) {
		super(sceneBranches, sceneCode, assetManager, font, character, saveService, background);
		this.assetManager = assetManager;
		this.character = character;
		display.setText(toDisplay);
		this.mutations = mutations != null ? mutations : new Array<Mutation>();
		this.background = background;
		this.music = music;
		this.sound = sound;
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		if (Gdx.input.isKeyJustPressed(Keys.TAB)) {
			background.toggleDialogBox(display);
		}
	}
	
	@Override
	public void setActive() {
		super.setActive();
		if (music != null) {
			EncounterScreen.setMusic(music);
			saveService.saveDataValue(SaveEnum.MUSIC, music);
		};
		if (sound != null) {
			EncounterScreen.play(sound);
		}
		background.initEnemy();
		String mutationResults = "";
		for (Mutation mutator: mutations) {
			String result = mutator.mutate();
			if (result != null) mutationResults += " ["+result+"]\n";
		}
		statusResults.setText(mutationResults);
		if (character.isLoaded()) {
			characterPortrait.addAction(Actions.show());
			characterPortrait.setDrawable(new TextureRegionDrawable(new TextureRegion(assetManager.get(character.getPortraitPath(), Texture.class))));
			masculinityIcon.addAction(Actions.show());
			masculinityIcon.setDrawable(new TextureRegionDrawable(new TextureRegion(assetManager.get(character.getMasculinityPath(), Texture.class))));
		}
	}
	// this type of TextScene will be one that always pipes from one scene to the next with no branch - there will be another TextScene that actually has branching logic
	@Override
	protected void nextScene() {
		sceneBranches.get(sceneBranches.orderedKeys().get(0)).setActive();
		isActive = false;
		addAction(Actions.hide());
	}
}

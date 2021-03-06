package com.majalis.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.OrderedMap;
import com.majalis.asset.AssetEnum;
import com.majalis.character.PlayerCharacter;
import com.majalis.encounter.Background;
import com.majalis.encounter.EncounterHUD;
import com.majalis.save.MutationResult;
import com.majalis.save.SaveEnum;
import com.majalis.save.SaveService;
import com.majalis.screens.TimeOfDay;

public class TextScene extends AbstractTextScene  {
	
	private final Array<Mutation> mutations;
	private final Background background;
	private final AssetManager assetManager;
	private final PlayerCharacter character;
	private final AssetEnum music;
	private final AssetDescriptor<Sound> sound;
	
	public TextScene(OrderedMap<Integer, Scene> sceneBranches, int sceneCode, AssetManager assetManager, BitmapFont font, SaveService saveService, final Background background, String toDisplay, String chatterText, String chatterPerson, Array<Mutation> mutations, PlayerCharacter character, AssetEnum music, AssetDescriptor<Sound> sound, EncounterHUD hud) {
		super(sceneBranches, sceneCode, assetManager, font, saveService, background, hud);
		this.assetManager = assetManager;
		this.character = character;
		display.setText(toDisplay);
		this.mutations = mutations != null ? mutations : new Array<Mutation>();
		this.background = background;
		this.music = music;
		this.sound = sound;
		if ((chatterPerson.equals("Trudy" ) && character.hasTrudy()) || chatterPerson.equals("Kylira") && character.hasKylira()) {
			Label chatter = addLabel(chatterPerson + ": \"" + chatterText + "\"", skin, font, Color.TAN, 0, 550);
			chatter.setWidth(600);
		}
		
		hud.getLog().addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (isActive()) toggleLogDisplay();
			}
		});
	}	
	
	private void toggleLogDisplay() {
		if (!hud.displayingLog()) {
			background.toggleDialogBox(display, false); // once toggleDialogBox is killed, all of this exists on the hud, and it can handle toggling the log as well gracefully
			hud.hideButtons();
			this.addAction(Actions.touchable(Touchable.disabled));
		}
		else {
			background.toggleDialogBox(display, true);
			hud.showButtons();		
			this.addAction(Actions.touchable(Touchable.enabled));
		}
		hud.toggleLog();
	}
	
	@Override
	public String getText() { return display.getText().toString(); }

	@Override
	public void toggleBackground() {
		if (!hud.displayingLog()) {
			background.toggleDialogBox(display);
			hud.toggleButtons();
			if (hud.buttonsVisible()) {
				hud.getHideButton().clearActions();
				hud.getHideButton().setText("Show");
			}
			else {
				hud.getHideButton().setText("Hide");
			}
		}
	}

	// careful!  This calls mutate() on all the mutations, which does not flush to the save file! Causing a flush to the save file in this method will cause mutations to replay every time this scene is loaded - super.setActive() flushes the save when it saves the scenecode and must be called first
	@Override
	public void activate() {
		super.activate();
		if (music != null) {
			saveService.saveDataValue(SaveEnum.MUSIC, music, false);
		};
		if (sound != null) {
			assetManager.get(sound).play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume", 1) * .5f);
		}
		background.initAnimation();
		Array<MutationResult> results = new Array<MutationResult>();
		for (Mutation mutator: mutations) {
			Array<MutationResult> result = mutator.mutate();
			if (result != null) results.addAll(result);
		}
		
		// save the results to the result array in the save file
		saveService.saveDataValue(SaveEnum.RESULT, results, false);		
		
		for (MutationResult result : results) {
			statusResults.add(new MutationActor(result, assetManager.get(result.getTexture()), skin)).fillY().align(Align.right).row();
		}
		
		showSave();
		
		hud.getHideButton().clearListeners();
		hud.getHideButton().addListener(
			new ClickListener() {
				@Override
		        public void clicked(InputEvent event, float x, float y) {
					toggleBackground();
				}
			}
		);	
		
		background.setColor(TimeOfDay.getTime(character.getTime()).getColor());
		if (display.getText().toString().equals("")) nextScene();
	}
	
	@Override
	public void showSave() { 
		hud.showButtons(); 
		if (hud.displayingLog()) hud.toggleLog();
	}
	
	@Override
	protected void nextScene() {
		clearActions();
		sceneBranches.get(sceneBranches.orderedKeys().get(0)).setActive();
		isActive = false;
		addAction(Actions.hide());
	}
}

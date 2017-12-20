package com.majalis.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.majalis.asset.AssetEnum;
import com.majalis.character.PlayerCharacter;
import com.majalis.save.SaveEnum;
import com.majalis.save.SaveManager;
import com.majalis.save.SaveService;
/*
 * Screen for displaying, saving, and loading save files.
 */
public class SaveScreen extends AbstractScreen {

	public static final Array<AssetDescriptor<?>> resourceRequirements = new Array<AssetDescriptor<?>>();
	static {
		resourceRequirements.add(AssetEnum.UI_SKIN.getSkin());
	}
	private final AssetManager assetManager;
	private final SaveService saveService;
	
	public SaveScreen(ScreenFactory factory, ScreenElements elements, AssetManager assetManager, SaveService saveService) {
		super(factory, elements);
		this.assetManager = assetManager;
		this.saveService = saveService;
		setClearColor(Color.SLATE.r, Color.SLATE.g, Color.SLATE.b, 1);
	}

	@Override
	public void buildStage() {
		Skin skin = assetManager.get(AssetEnum.UI_SKIN.getSkin());
		Table saveTable = new Table();
		saveTable.setPosition(100, 1050);
		saveTable.align(Align.topLeft);
		this.addActor(saveTable);

		final TextButton backButton = new TextButton ("Back", skin);
		backButton.setPosition(1500, 50);
		this.addActor(backButton);
		backButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				showScreen(ScreenEnum.LOAD_GAME);
			}
		});
		
		final TextButton quickLoadButton = new TextButton ("Quick Load", skin);
		quickLoadButton.setPosition(400, 50);
		this.addActor(quickLoadButton);
		quickLoadButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
        		saveService.newSave(".toa-data/quicksave.json");
				showScreen(ScreenEnum.LOAD_GAME);
			}
		});
		
		// for each save slot				
		for (int ii = 0; ii < 10; ii++) {
			// load the data
			final int num = ii;
			final String path = ".toa-data/save" + ii + ".json";
			SaveManager saveManager = new SaveManager(false, ".toa-data/save" + ii + ".json", ".toa-data/profile.json");
			PlayerCharacter character = saveManager.loadDataValue(SaveEnum.PLAYER, PlayerCharacter.class);
			// create an actor based on the data that will display stats with a button with clicklistener (and associated enter-> click functionality) that will save the current game to that file(overwrite), load that file, a button with a clicklistener that will delete that file
			final Label saveValue = new Label("Save File " + (ii + 1) + " - " + (character.getJobClass() == null ? "No Save File" : "\"" + character.getCharacterName() + "\" - " + character.getJobClass().getLabel() + " - Level: " + character.getLevel()), skin);			
			saveValue.setColor(character.getJobClass() == null ? Color.BLACK : Color.BLUE);
			// add the actor to the list
			final TextButton saveButton = new TextButton ("Save", skin);
			
			final TextButton loadButton = new TextButton ("Load", skin);
			if (character.getJobClass() != null) {
				loadButton.addListener(new ClickListener() {
					@Override
					public void clicked(InputEvent event, float x, float y) {
						saveService.newSave(path);
						showScreen(ScreenEnum.LOAD_GAME);
					}
				});
			}
			else {
				loadButton.setColor(Color.GRAY);
			}
			
			saveButton.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					saveService.manualSave(path);
					SaveManager saveManager = new SaveManager(false, path, ".toa-data/profile.json");
					PlayerCharacter characterTemp = saveManager.loadDataValue(SaveEnum.PLAYER, PlayerCharacter.class);
					saveValue.setText("Save File " + (num + 1) + " - " + (characterTemp.getJobClass() == null ? "No Save File" : "\"" + characterTemp.getCharacterName() + "\" - " + characterTemp.getJobClass().getLabel() + " - Level: " + characterTemp.getLevel()));
					saveValue.setColor(Color.BLUE);
					loadButton.setColor(Color.WHITE);
					loadButton.addListener(new ClickListener() {
						@Override
						public void clicked(InputEvent event, float x, float y) {
							saveService.newSave(path);
							showScreen(ScreenEnum.LOAD_GAME);
						}
					});
					
				}
			});
			
			final TextButton deleteButton = new TextButton ("Delete", skin);
			deleteButton.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					SaveManager saveManager = new SaveManager(false, path, ".toa-data/profile.json");
					saveManager.newSave();
					saveValue.setText("Save File " + (num + 1) + " - No Save File");
					saveValue.setColor(Color.BLACK);
					loadButton.setColor(Color.GRAY);
					loadButton.clearListeners();
				}
			});
			
			saveTable.add(saveValue).align(Align.left).width(800);
			saveTable.add(saveButton);	
			saveTable.add(loadButton);				
			saveTable.add(deleteButton).row();	
		}
	}
	
	
	@Override
	public void render(float delta) {
		super.render(delta);
		if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
			showScreen(ScreenEnum.LOAD_GAME);
		}
	}
	
	@Override
	public void dispose() {
		for(AssetDescriptor<?> path: resourceRequirements) {
			assetManager.unload(path.fileName);
		}
	}
}
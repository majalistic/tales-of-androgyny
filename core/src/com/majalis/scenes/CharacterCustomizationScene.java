package com.majalis.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.OrderedMap;
import com.majalis.asset.AssetEnum;
import com.majalis.character.PlayerCharacter;
import com.majalis.encounter.Background;
import com.majalis.save.SaveEnum;
import com.majalis.save.SaveService;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class CharacterCustomizationScene extends Scene {

	private final SaveService saveService;
	private final Skin skin;
	private final Sound buttonSound;
	private final PlayerCharacter character;
	// customize name, face, body, skin, hair, length, facial markings
	public CharacterCustomizationScene(OrderedMap<Integer, Scene> sceneBranches, int sceneCode, final SaveService saveService, BitmapFont font, Background background, AssetManager assetManager, PlayerCharacter character) {
		super(sceneBranches, sceneCode);
		this.saveService = saveService;
		this.addActor(background);
		this.character = character;
		skin = assetManager.get(AssetEnum.UI_SKIN.getPath(), Skin.class);
		buttonSound = assetManager.get(AssetEnum.BUTTON_SOUND.getPath(), Sound.class);
	}
	
	@Override
	public void setActive() {
		isActive = true;
		this.removeAction(Actions.hide());
		this.addAction(Actions.visible(true));
		this.addAction(Actions.show());
		this.setBounds(0, 0, 2000, 2000);
		saveService.saveDataValue(SaveEnum.SCENE_CODE, sceneCode);

		final TextButton done = new TextButton("Done", skin);
		
		done.addListener(
			new ClickListener() {
				@Override
		        public void clicked(InputEvent event, float x, float y) {
					buttonSound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
					nextScene();		   
		        }
			}
		);
		done.setPosition(1522, 30);
		addActor(done);

		final Label description = addLabel("", skin, Color.BLACK, 1000, 800);
		final Label console = addLabel("", skin, Color.GOLD, 1000, 400);
		
		final Table table = new Table();
		
		Label buttSizeLabel = addLabel("Bubble", skin, Color.SALMON, 500, 660);
		for (final PlayerCharacter.Bootyliciousness buttSize : PlayerCharacter.Bootyliciousness.values()) {
			final TextButton button = new TextButton(buttSize.toString(), skin);
			button.addListener(new ClickListener() {
				@Override
		        public void clicked(InputEvent event, float x, float y) {
					buttonSound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
					console.setText("You now have a " + buttSize.toString().toLowerCase() + " booty.");
					character.setBootyliciousness(buttSize);
					saveService.saveDataValue(SaveEnum.PLAYER, character);
					buttSizeLabel.setText(buttSize.toString());
		        }
				@Override
		        public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
					description.setText(buttSize.getDescription());
				}
				@Override
		        public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
					description.setText("");
				}
			});
			table.add(button).size(180, 40).row();
		}
		addLabel("Butt Size:", skin, Color.BLACK, 130, 700);
		table.setPosition(400, 675);
		addActor(table);
		
		final Table lipTable = new Table();
		
		Label lipSizeLabel = addLabel("Thin", skin, Color.SALMON, 500, 480);
		for (final PlayerCharacter.LipFullness lipFullness : PlayerCharacter.LipFullness.values()) {
			final TextButton button = new TextButton(lipFullness.toString(), skin);
			button.addListener(new ClickListener() {
				@Override
		        public void clicked(InputEvent event, float x, float y) {
					buttonSound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
					console.setText("You now have " + lipFullness.toString().toLowerCase() + " lips.");
					character.setLipFullness(lipFullness);
					saveService.saveDataValue(SaveEnum.PLAYER, character);
					lipSizeLabel.setText(lipFullness.toString());
		        }
				@Override
		        public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
					description.setText(lipFullness.getDescription());
				}
				@Override
		        public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
					description.setText("");
				}
			});
			lipTable.add(button).size(180, 40).row();
		}
		addLabel("Lip Fullness:", skin, Color.BLACK, 130, 520);
		lipTable.setPosition(400, 475);
		addActor(lipTable);
		
		addLabel("Name:", skin, Color.BLACK, 130, 820);
		final TextField nameField = new TextField("Hiro", skin);
		nameField.setPosition(233, 800);
		addActor(nameField);
		this.addListener(new InputListener() {
	        @Override
	        public boolean keyUp(InputEvent event, int keycode) {
	        	if (!nameField.getText().equals(character.getName())) {
	        		character.setName(nameField.getText());
	        		saveService.saveDataValue(SaveEnum.PLAYER, character);
	        	}
	        	return false;
	        }
	    });
	}
	
	private void nextScene() {
		sceneBranches.get(sceneBranches.orderedKeys().get(0)).setActive();
		isActive = false;
		addAction(Actions.hide());
	}
}

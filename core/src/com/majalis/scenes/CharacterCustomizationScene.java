package com.majalis.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.OrderedMap;
import com.majalis.asset.AssetEnum;
import com.majalis.character.AbstractCharacter;
import com.majalis.character.AbstractCharacter.PhallusType;
import com.majalis.character.PlayerCharacter;
import com.majalis.encounter.Background;
import com.majalis.encounter.EncounterHUD;
import com.majalis.save.SaveEnum;
import com.majalis.save.SaveService;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class CharacterCustomizationScene extends Scene {

	private final SaveService saveService;
	private final Skin skin;
	private final Sound buttonSound;
	private final PlayerCharacter character;
	private final AssetManager assetManager;
	// customize name, face, body, skin, hair, length, facial markings
	public CharacterCustomizationScene(OrderedMap<Integer, Scene> sceneBranches, int sceneCode, final SaveService saveService, BitmapFont font, Background background, AssetManager assetManager, PlayerCharacter character, EncounterHUD hud) {
		super(sceneBranches, sceneCode, hud);
		this.saveService = saveService;
		this.addActor(background);
		this.character = character;
		this.assetManager = assetManager;
		skin = assetManager.get(AssetEnum.UI_SKIN.getSkin());
		buttonSound = assetManager.get(AssetEnum.BUTTON_SOUND.getSound());
	}
	
	@Override
	public void activate() {
		isActive = true;
		this.addAction(Actions.show());
		this.setBounds(0, 0, 2000, 2000);
		saveService.saveDataValue(SaveEnum.SCENE_CODE, sceneCode);

		Image temp = addImage(assetManager.get(AssetEnum.SKILL_CONSOLE_BOX.getTexture()), Color.WHITE, 940 + 420, 0, 560, 1080); 
		temp.addAction(Actions.alpha(.9f));
		
		int consoleX = 1665;
		int consoleY = 975;
		int consoleWidth = 470;

		Table descriptionTable = new Table();
		final Label description = new Label("", skin);
		description.setWrap(true);
		description.setColor(Color.BLACK);
		descriptionTable.add(description).width(consoleWidth);
		descriptionTable.align(Align.top);
		this.addActor(descriptionTable);
		descriptionTable.setPosition(consoleX,  consoleY);
		
		final Label consoleName = new Label("", skin);
		consoleName.setColor(Color.FIREBRICK);
		consoleName.setPosition(1670, 1050);
		consoleName.setAlignment(Align.top);
		this.addActor(consoleName);
		
		Table consoleTable = new Table();
		consoleTable.setPosition(consoleX + 15, consoleY - 610);
		final Label console = new Label("", skin);
		console.setWrap(true);
		console.setColor(Color.BLACK);
		consoleTable.add(console).width(consoleWidth - 60);
		consoleTable.align(Align.top);
		this.addActor(consoleTable);	
		
		final Table table = new Table();
		table.align(Align.topLeft);
		table.setPosition(100, 1000);
		addActor(table);
		
		table.add(addLabel("Name:", skin, Color.GOLD, 0, 0)).row();
		final TextField nameField = new TextField("Hiro", skin);
		table.add(nameField).row();
		this.addListener(new InputListener() {
	        @Override
	        public boolean keyUp(InputEvent event, int keycode) {
	        	if (!nameField.getText().equals(character.getName())) {
	        		character.setCharacterName(nameField.getText());
	        		saveService.saveDataValue(SaveEnum.PLAYER, character);
	        	}
	        	return false;
	        }
	    });
		
		table.add(addLabel("Butt Size:", skin, Color.GOLD, 0, 0)).row();
		final Label buttSizeLabel = new Label("Bubble", skin);
		buttSizeLabel.setColor(Color.SALMON);
		for (final PlayerCharacter.Bootyliciousness buttSize : PlayerCharacter.Bootyliciousness.values()) {
			final TextButton button = getButton(buttSize.toString());
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
			table.add(button).size(180, 60);
		}
		table.add().row();
		table.add().width(180);
		table.add(buttSizeLabel).row();
		
		table.add(addLabel("Lip Fullness:", skin, Color.GOLD, 0, 0)).row();
		final Label lipSizeLabel = new Label("Thin", skin);
		lipSizeLabel.setColor(Color.SALMON);		
		for (final PlayerCharacter.LipFullness lipFullness : PlayerCharacter.LipFullness.values()) {
			final TextButton button = getButton(lipFullness.toString());
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
			table.add(button).size(180, 60);
		}
		table.add().row();
		table.add().width(180);
		table.add(lipSizeLabel).row();;
		
		table.add(addLabel("Penis Size:", skin, Color.GOLD, 0, 0)).row();
		final Label penisSizeLabel = new Label("Small", skin);
		penisSizeLabel.setColor(Color.SALMON);		
		for (final AbstractCharacter.PhallusType penisType : new PhallusType[]{PhallusType.CUTE, PhallusType.TINY, PhallusType.SMALL}) {
			final TextButton button = getButton(penisType.getLabel());
			button.addListener(new ClickListener() {
				@Override
		        public void clicked(InputEvent event, float x, float y) {
					buttonSound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
					console.setText("You now have " + penisType.getLabel().toLowerCase() + " penis.");
					character.setPhallusType(penisType);
					saveService.saveDataValue(SaveEnum.PLAYER, character);
					penisSizeLabel.setText(penisType.getLabel());
		        }
				@Override
		        public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
					description.setText(penisType.getDescription());
				}
				@Override
		        public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
					description.setText("");
				}
			});
			table.add(button).size(180, 60);
		}
		table.add().row();
		table.add().width(180);
		table.add(penisSizeLabel);
		
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
		
		final Table navigationButtons = new Table();
		navigationButtons.setPosition(1675, 75);
		navigationButtons.add().size(125, 75);
		navigationButtons.add().size(125, 75);
		navigationButtons.add().size(125, 75).row();
		navigationButtons.add().size(50);
		navigationButtons.add(done).width(200);
		this.addActor(navigationButtons);
	}
	
	private TextButton getButton(String label) {
		TextButtonStyle buttonStyle = new TextButtonStyle();
		buttonStyle.up = new TextureRegionDrawable(new TextureRegion(assetManager.get(AssetEnum.EMBELLISHED_BUTTON_UP.getTexture())));
		buttonStyle.down = new TextureRegionDrawable(new TextureRegion(assetManager.get(AssetEnum.EMBELLISHED_BUTTON_DOWN.getTexture())));
		buttonStyle.over = new TextureRegionDrawable(new TextureRegion(assetManager.get(AssetEnum.EMBELLISHED_BUTTON_HIGHLIGHT.getTexture())));	
		buttonStyle.font = skin.getFont("default-font");
		buttonStyle.fontColor = Color.BLACK;
		TextButton button = new TextButton(label, buttonStyle);
		return button;
	}
	
	private void nextScene() {
		sceneBranches.get(sceneBranches.orderedKeys().get(0)).setActive();
		isActive = false;
		addAction(Actions.hide());
	}
}

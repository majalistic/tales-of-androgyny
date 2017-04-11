package com.majalis.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.majalis.asset.AssetEnum;
import com.majalis.character.PlayerCharacter;
import com.majalis.character.AbstractCharacter.Stat;
import com.majalis.character.Item;
import com.majalis.character.Perk;
import com.majalis.encounter.Background.BackgroundBuilder;
import com.majalis.save.SaveEnum;
import com.majalis.save.SaveManager.GameContext;
import com.majalis.save.SaveService;
/*
 * The options/configuration screen.  UI that handles player input to save Preferences to a player's file system.
 */
public class CharacterScreen extends AbstractScreen {

	public static final Array<AssetDescriptor<?>> resourceRequirements = new Array<AssetDescriptor<?>>();
	static {
		resourceRequirements.add(AssetEnum.UI_SKIN.getSkin());
		resourceRequirements.add(AssetEnum.CLICK_SOUND.getSound());
		resourceRequirements.add(AssetEnum.MOUNTAIN_ACTIVE.getTexture()); 
		resourceRequirements.add(AssetEnum.FOREST_ACTIVE.getTexture());
		resourceRequirements.add(AssetEnum.FOREST_INACTIVE.getTexture());
		resourceRequirements.add(AssetEnum.CASTLE.getTexture());
		resourceRequirements.add(AssetEnum.APPLE.getTexture());
		resourceRequirements.add(AssetEnum.MEAT.getTexture());
		resourceRequirements.add(AssetEnum.GRASS0.getTexture());
		resourceRequirements.add(AssetEnum.GRASS1.getTexture());
		resourceRequirements.add(AssetEnum.GRASS2.getTexture());
		resourceRequirements.add(AssetEnum.CLOUD.getTexture());
		resourceRequirements.add(AssetEnum.ROAD.getTexture());
		resourceRequirements.add(AssetEnum.WORLD_MAP_UI.getTexture());
		resourceRequirements.add(AssetEnum.WORLD_MAP_HOVER.getTexture());
		resourceRequirements.add(AssetEnum.ARROW.getTexture());
		resourceRequirements.add(AssetEnum.CHARACTER_SCREEN.getTexture());
		resourceRequirements.add(AssetEnum.STRENGTH.getTexture());
		resourceRequirements.add(AssetEnum.ENDURANCE.getTexture());
		resourceRequirements.add(AssetEnum.AGILITY.getTexture());
		resourceRequirements.add(AssetEnum.PERCEPTION.getTexture());
		resourceRequirements.add(AssetEnum.MAGIC.getTexture());
		resourceRequirements.add(AssetEnum.CHARISMA.getTexture());
		resourceRequirements.add(AssetEnum.WORLD_MAP_MUSIC.getMusic());
	}
	
	public CharacterScreen(ScreenFactory factory, ScreenElements elements, AssetManager assetManager, final SaveService saveService, final PlayerCharacter character) {
		super(factory, elements);
		this.addActor(new BackgroundBuilder(assetManager.get(AssetEnum.CHARACTER_SCREEN.getTexture())).build()); 
		
		Skin skin = assetManager.get(AssetEnum.UI_SKIN.getSkin());
		final Sound buttonSound = assetManager.get(AssetEnum.CLICK_SOUND.getSound()); 
		final TextButton done = new TextButton("Done", skin);
		
		done.setSize(180, 60);
		done.addListener(
			new ClickListener() {
				@Override
		        public void clicked(InputEvent event, float x, float y) {
					buttonSound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
					saveService.saveDataValue(SaveEnum.CONTEXT, GameContext.WORLD_MAP);
					showScreen(ScreenEnum.CONTINUE);		   
		        }
			}
		);
		done.setPosition(1700, 30);
		this.addActor(done);

		final Table overview = new Table();
		overview.align(Align.top);
		overview.setPosition(200, 1040);
		this.addActor(overview);
		overview.add(getLabel("Name: ", skin, Color.BLACK)).align(Align.left);
		overview.add(getLabel(character.getCharacterName() != null ? character.getCharacterName() : "Hiro", skin, Color.DARK_GRAY)).align(Align.left).row();
		overview.add(getLabel("Class: ", skin, Color.BLACK)).align(Align.left);
		
		overview.add(getLabel(character.getJobClass().getLabel(), skin, Color.FIREBRICK)).align(Align.left).row();
		
		int storedLevels = character.getStoredLevels();
		overview.add(getLabel("Level: ", skin, Color.BLACK)).align(Align.left);
		overview.add(getLabel(String.valueOf(character.getLevel()), skin, Color.GOLD)).align(Align.left).row();
		overview.add(getLabel("Experience: ", skin, Color.BLACK)).align(Align.left);
		overview.add(getLabel(String.valueOf(character.getExperience()), skin, Color.GOLD)).align(Align.left).row();
		if (storedLevels > 0) {
			overview.add(getLabel("Available Levels: " + storedLevels, skin, Color.GOLD)).align(Align.left).row();
		}
		
		overview.add(getLabel("Booty: ", skin, Color.BLACK)).align(Align.left);
		overview.add(getLabel(character.getBootyLiciousness(), skin, Color.PINK)).align(Align.left).row();
		overview.add(getLabel("Lips: ", skin, Color.BLACK)).align(Align.left);
		overview.add(getLabel(character.getLipFullness(), skin, Color.PINK)).align(Align.left).row();
		
		final Table statTable = new Table();

		for (final Stat stat: Stat.values()) {
			Image statImage = new Image((Texture) assetManager.get(stat.getAsset()));
			Label statLabel = new Label("", skin);
			
			int amount = character.getBaseStat(stat);
			setFontColor(statLabel, amount);
			setStatText(stat, character, statLabel);
			statTable.add(statImage).size(statImage.getWidth() / (statImage.getHeight() / 35), 35).align(Align.left).padRight(20);
			statTable.add(statLabel).align(Align.left).row();
		}
		statTable.setPosition(250, 750);
		statTable.align(Align.top);
		this.addActor(statTable);
		
		if (character.needsLevelUp()) {
			final boolean levelup = character.getStoredLevels() > 0;
			final TextButton levelUp = new TextButton(levelup ? "Level Up!" : "Learn Skills", skin);
			
			levelUp.setSize(270, 60); 
			TextButtonStyle style = new TextButtonStyle(levelUp.getStyle());
			style.fontColor = levelup ? Color.OLIVE : Color.GOLDENROD;
			levelUp.setStyle(style);
			levelUp.addListener(
				new ClickListener() {
					@Override
			        public void clicked(InputEvent event, float x, float y) {
						buttonSound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
						if (levelup) character.levelUp();
						saveService.saveDataValue(SaveEnum.PLAYER, character);
						saveService.saveDataValue(SaveEnum.CONTEXT, GameContext.LEVEL);
						showScreen(ScreenEnum.CONTINUE);
			        }
				}
			);
			levelUp.setPosition(1400, 30);
			this.addActor(levelUp);
		}
		
		final Table inventoryTable = new Table();
		final Label inventoryText = new Label("", skin);
		inventoryTable.add(inventoryText).row();
		inventoryTable.add(getLabel("Inventory", skin, Color.BLACK)).row();
		inventoryTable.setPosition(780, 500);
		inventoryTable.align(Align.top);
		this.addActor(inventoryTable);
		final Table weaponTable = new Table();
		final Label weaponTableText = new Label("", skin);
		weaponTable.add(weaponTableText).row();
		weaponTable.add(getLabel("Weapons", skin, Color.BLACK)).row();
		weaponTable.setPosition(260, 500);
		weaponTable.align(Align.top);
		this.addActor(weaponTable);
		
		Table equipmentTable = new Table();
		equipmentTable.align(Align.topLeft);
		final Label weaponText = getLabel(character.getWeapon() != null ? "Weapon: " + character.getWeapon().getName() : "Weapon: Unarmed", skin, Color.BLACK);
		equipmentTable.setPosition(600, 1040);
		this.addActor(equipmentTable);
		equipmentTable.add(weaponText).align(Align.left).row();
		equipmentTable.add(getLabel("Shield: ", skin, Color.DARK_GRAY)).align(Align.left).row();
		equipmentTable.add(getLabel("Armor: ", skin, Color.DARK_GRAY)).align(Align.left).row();
		equipmentTable.add(getLabel("Headgear: ", skin, Color.DARK_GRAY)).align(Align.left).row();
		equipmentTable.add(getLabel("Legwear: ", skin, Color.DARK_GRAY)).align(Align.left).row();
		equipmentTable.add(getLabel("Armwear: ", skin, Color.DARK_GRAY)).align(Align.left).row();
		
		Table perkTable = new Table();
		perkTable.align(Align.topLeft);
		perkTable.setPosition(1000, 1040);
		this.addActor(perkTable);
		perkTable.add(getLabel("Perks: ", skin, Color.FOREST)).align(Align.left).row();
		for (ObjectMap.Entry<Perk, Integer> perk : character.getPerks().entries()) {
			perkTable.add(getLabel(perk.key.getLabel() + " (" + perk.value.toString() + ")", skin, Color.BLACK)).align(Align.left).row();
		}
		
		for (final Item item : character.getInventory()) {
			final TextButton itemButton = new TextButton(item.getName(), skin);
			if (item.isConsumable()) {
				itemButton.addListener(
					new ClickListener() {
						@Override
				        public void clicked(InputEvent event, float x, float y) {
							buttonSound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
							String result = character.consumeItem(item);
							inventoryText.setText(result);
							saveService.saveDataValue(SaveEnum.PLAYER, character);
							inventoryTable.removeActor(itemButton);
				        }
					}
				);
				inventoryTable.add(itemButton).size(450, 40).row();
			}
			else if (item.isEquippable()) {
				itemButton.addListener(
					new ClickListener() {
						@Override
				        public void clicked(InputEvent event, float x, float y) {
							buttonSound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
							String result = character.setWeapon(item);
							weaponTableText.setText(result);
							weaponText.setText("Weapon: " + character.getWeapon().getName());
							saveService.saveDataValue(SaveEnum.PLAYER, character);
				        }
					}
				);
				weaponTable.add(itemButton).size(500, 40).row();
			}
		}	
	}
	
	private Label getLabel(String label, Skin skin, Color color) {
		Label newLabel = new Label(label, skin);
		newLabel.setColor(color);
		return newLabel;
	}
	
	private void setFontColor(Label font, int amount) {
		Color toApply = Color.WHITE;
		switch (amount) {
			case 0: toApply = Color.BLACK; break;
			case 1: toApply = Color.DARK_GRAY; break;
			case 2: toApply = Color.GRAY; break;
			case 3: toApply = Color.NAVY; break;
			case 4: toApply = Color.ROYAL; break;
			case 5: toApply = Color.OLIVE; break;	
			case 6: toApply = Color.FOREST; break;
			case 7: toApply = Color.LIME; break;	
			case 8: toApply = Color.GOLDENROD; break;
			case 9: toApply = Color.GOLD; break;
		}
		font.setColor(toApply);
	}
	
	private void setStatText(Stat stat, PlayerCharacter character, Label label) {
		int amount = character.getBaseStat(stat);
		label.setText(amount + " - " + PlayerCharacter.getStatMap().get(stat).get(amount));
	}
	
	@Override
	public void buildStage() {
	}
	
	@Override
	public void render(float delta) {
		super.render(delta);
		if (Gdx.input.isKeyJustPressed(Keys.ENTER) || Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
			showScreen(ScreenEnum.CONTINUE);
		}
	}
}
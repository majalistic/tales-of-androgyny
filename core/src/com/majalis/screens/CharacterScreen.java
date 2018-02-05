package com.majalis.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
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
import com.majalis.character.StatusType;
import com.majalis.character.AbstractCharacter.Stat;
import com.majalis.character.Perk;
import com.majalis.encounter.Background.BackgroundBuilder;
import com.majalis.save.SaveEnum;
import com.majalis.save.SaveManager.GameContext;
import com.majalis.save.SaveService;
/*
 * Screen that displays the current save file's statistics.
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
		resourceRequirements.add(AssetEnum.WARRIOR.getTexture());
		resourceRequirements.add(AssetEnum.PALADIN.getTexture());
		resourceRequirements.add(AssetEnum.THIEF.getTexture());
		resourceRequirements.add(AssetEnum.RANGER.getTexture());
		resourceRequirements.add(AssetEnum.MAGE.getTexture());
		resourceRequirements.add(AssetEnum.ENCHANTRESS.getTexture());
		resourceRequirements.add(AssetEnum.WORLD_MAP_MUSIC.getMusic());
		resourceRequirements.addAll(WorldMapScreen.resourceRequirements);
	}
	
	private final Sound buttonSound;
	private final Skin skin;
	
	public CharacterScreen(ScreenFactory factory, ScreenElements elements, final SaveService saveService, final PlayerCharacter character) {
		super(factory, elements, null);
		this.addActor(new BackgroundBuilder(assetManager.get(AssetEnum.CHARACTER_SCREEN.getTexture())).build()); 
		
		skin = assetManager.get(AssetEnum.UI_SKIN.getSkin());
		buttonSound = assetManager.get(AssetEnum.CLICK_SOUND.getSound()); 
		
		Image characterImage = new Image(assetManager.get(character.getJobClass().getTexture()));
		characterImage.setPosition(1250, 0);
		this.addActor(characterImage);
		
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

		final TextButton inventory = new TextButton("Inventory", skin);
		inventory.setSize(250, 60);
		inventory.addListener(
			new ClickListener() {
				@Override
		        public void clicked(InputEvent event, float x, float y) {
					buttonSound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
					showScreen(ScreenEnum.INVENTORY);		   
		        }
			}
		);
		inventory.setPosition(1450, 30);
		this.addActor(inventory);
		
		final Table overview = new Table();
		overview.align(Align.topLeft);
		overview.setPosition(100, 1040);
		this.addActor(overview);
		overview.add(getLabel("Name: ", skin, Color.BLACK)).align(Align.left);
		overview.add(getLabel(character.getCharacterName() != null ? character.getCharacterName() : "Hiro", skin, Color.DARK_GRAY)).align(Align.left).row();
		overview.add(getLabel("Class: ", skin, Color.BLACK)).align(Align.left);
		
		overview.add(getLabel(character.getJobClass().getLabel(), skin, Color.FIREBRICK)).align(Align.left).row();
		
		int storedLevels = character.getStoredLevels();
		overview.add(getLabel("Level: ", skin, Color.BLACK)).align(Align.left);
		overview.add(getLabel(String.valueOf(character.getLevel()), skin, Color.GOLD)).align(Align.left).row();
		overview.add(getLabel("Experience: ", skin, Color.BLACK)).align(Align.left);
		overview.add(getLabel(String.valueOf(character.getExperience()), skin, Color.FOREST)).align(Align.left).row();
		if (storedLevels > 0) {
			overview.add(getLabel("Available Levels: " + storedLevels, skin, Color.GOLD)).align(Align.left).row();
		}
		overview.add(getLabel("Health: ", skin, Color.BLACK)).align(Align.left);
		overview.add(getLabel(String.valueOf(character.getCurrentHealth() + "/" + character.getMaxHealth()), skin, Color.GREEN)).align(Align.left).row();
		overview.add(getLabel("Lust: ", skin, Color.BLACK)).align(Align.left);
		overview.add(getLabel(String.valueOf(character.getCurrentLust() + "%"), skin, Color.SALMON)).align(Align.left).row();
		
		overview.add(getLabel("Gold: ", skin, Color.BLACK)).align(Align.left);
		overview.add(getLabel(String.valueOf(character.getMoney()), skin, Color.GOLD)).align(Align.left).row();
		overview.add(getLabel("Debt: ", skin, Color.BLACK)).align(Align.left);
		overview.add(getLabel(String.valueOf(character.getCurrentDebt()), skin, Color.FIREBRICK)).align(Align.left).row();
		
		
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
		statTable.setPosition(500, 1040);
		statTable.align(Align.topLeft);
		this.addActor(statTable);
		
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
					while(character.getStoredLevels() > 0) character.levelUp();
					saveService.saveDataValue(SaveEnum.PLAYER, character);
					saveService.saveDataValue(SaveEnum.CONTEXT, GameContext.LEVEL);
					showScreen(ScreenEnum.CONTINUE);
		        }
			}
		);
		levelUp.setPosition(1180, 30);
		this.addActor(levelUp);
		
		final Table sexTable = new Table();
		Label virgin = new Label("Anal Virgin? " + (character.isVirgin() ? "For now!" : "Nope!"), skin);
		virgin.setColor(Color.PINK);
		sexTable.add(virgin).row();
		Label analSex = new Label("Assfucked: " + (character.getAnalReceptionCount() > 1 ? character.getAnalReceptionCount() + " times." : character.getAnalReceptionCount() == 1 ? character.getAnalReceptionCount() + " time." : "Never!"), skin);
		analSex.setColor(Color.PINK);
		sexTable.add(analSex).row();
		
		Label oralSex = new Label("Mouthfucked: " + (character.getOralReceptionCount() > 1 ? character.getOralReceptionCount() + " times." : character.getOralReceptionCount() == 1 ? character.getOralReceptionCount() + " time." : "Never!"), skin);
		oralSex.setColor(Color.PINK);
		sexTable.add(oralSex).row();
		
		sexTable.setPosition(950, 1040);
		sexTable.align(Align.topLeft);
		this.addActor(sexTable);;
		
		final Table statusTable = new Table();
		Label headerStatus = new Label("Status Effects:", skin);
		headerStatus.setColor(Color.BLACK);
		statusTable.add(headerStatus).row();
		for (final ObjectMap.Entry<String, Integer> statusEffect: character.getStatuses()) {
			if (statusEffect.value == 0) continue;
			Label statusLabel = new Label(StatusType.valueOf(statusEffect.key).getLabel() + ": " + statusEffect.value, skin);
			if (StatusType.valueOf(statusEffect.key).isPositive()) {
				statusLabel.setColor(Color.FOREST);		
			}
			else {
				statusLabel.setColor(Color.FIREBRICK);		
			}
			statusTable.add(statusLabel).row();
				
		}
		statusTable.setPosition(1125, 600);
		statusTable.align(Align.topLeft);
		this.addActor(statusTable);
		
		final Label perkDescription = getLabel("", skin, Color.BLACK);
		perkDescription.setWidth(600);
		perkDescription.setAlignment(Align.center);
		perkDescription.setWrap(true);
		perkDescription.setPosition(200, 75);
		this.addActor(perkDescription);
		
		Table perkTable = new Table();
		perkTable.align(Align.topLeft);
		perkTable.setPosition(100, 600);
		this.addActor(perkTable);
		perkTable.add(getLabel("Perks: ", skin, Color.BLACK)).align(Align.left).row();
		int perkColumn = 0;
		for (final ObjectMap.Entry<Perk, Integer> perk : character.getPerks().entries()) {
			final Perk perkPlatonic = perk.key; 
			Integer perkValue = perk.value;
			if (perkValue > 0) {
				perkTable.add(getLabel(perk.key.getLabel() + " (" + perkValue.toString() + ")", skin, perk.key.isPositive() ? Color.FOREST : Color.FIREBRICK, new ClickListener(){
					@Override
			        public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
						perkDescription.setText(perkPlatonic.getDescription());
					}
					public void exit(InputEvent event, float x, float y, int pointer, Actor fromActor) {
						perkDescription.setText("");
					}
				})).align(Align.left).width(315);
				if (perkColumn == 2) perkTable.row();
				perkColumn++;
				perkColumn %= 3;
			}	
		}
	}
	
	private Label getLabel(String label, Skin skin, Color color) {
		return getLabel(label, skin, color, null);
	}
	
	private Label getLabel(String label, Skin skin, final Color color, ClickListener listener) {
		final Label newLabel = new Label(label, skin);
		newLabel.setColor(color);
		if (listener != null) {
			newLabel.addListener(listener);
			newLabel.addListener(new ClickListener(){
				@Override
		        public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
					newLabel.setColor(Color.GOLD);
				}
				public void exit(InputEvent event, float x, float y, int pointer, Actor fromActor) {
					newLabel.setColor(color);
				}
			});
		}
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
		Array<String> values = PlayerCharacter.getStatMap().get(stat);
		label.setText(amount + " - " + (amount < values.size ? values.get(amount) : "Godlike"));
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
	
	@Override
	public void dispose() {
		for(AssetDescriptor<?> path: resourceRequirements) {
			if (path.fileName.equals(AssetEnum.BUTTON_SOUND.getSound().fileName) || path.type == Music.class) continue;
			assetManager.unload(path.fileName);
		}
	}
	
}
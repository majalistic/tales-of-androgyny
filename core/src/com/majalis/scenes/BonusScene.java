package com.majalis.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.OrderedMap;
import com.majalis.asset.AssetEnum;
import com.majalis.character.PlayerCharacter;
import com.majalis.encounter.Background;
import com.majalis.encounter.EncounterHUD;
import com.majalis.save.SaveEnum;
import com.majalis.save.SaveService;

public class BonusScene extends Scene {

	private final SaveService saveService;
	private final Skin skin;
	private final Sound buttonSound;
	private final PlayerCharacter character;
	private final AssetManager assetManager;
	private int bonusPoints;
	private ObjectMap<String, Boolean> bonuses;
	
	public BonusScene(OrderedMap<Integer, Scene> sceneBranches, int sceneCode, final SaveService saveService, BitmapFont font, Background background, AssetManager assetManager, PlayerCharacter character, EncounterHUD hud) {
		super(sceneBranches, sceneCode, hud);
		this.saveService = saveService;
		this.addActor(background);
		this.character = character;
		this.assetManager = assetManager;
		skin = assetManager.get(AssetEnum.UI_SKIN.getSkin());
		buttonSound = assetManager.get(AssetEnum.BUTTON_SOUND.getSound());
		bonuses = new ObjectMap<String, Boolean>();
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
		
		bonusPoints = 1;
		final Label bonusLabel = new Label("Bonus Points: " + bonusPoints, skin);
		bonusLabel.setColor(Color.GOLD);
		for (final String s : new String[]{"Bonus Stat Points", "Bonus Skill Points", "Bonus Soul Crystals", "Bonus Perk Points", "Bonus Gold", "Bonus Food"}) { // this should be the vals of an enum
			TextButton button = getButton(s);
			table.add(button).size(300, 75).row();
			button.addListener(new ClickListener() {
				@Override
				public void clicked (InputEvent event, float x, float y) {
					if (!button.isChecked()) { 
						button.setColor(Color.WHITE);
						bonusPoints++;
						bonuses.put(s, false); 
					}
					else {
						if (bonusPoints > 0) {
							bonusPoints--;
							button.setColor(Color.YELLOW);
							bonuses.put(s, true);
						}
						else {
							button.setChecked(false); 
						}
					}
					bonusLabel.setText("Bonus Points: " + bonusPoints);
				}
			});
		}
		
		table.row();
		table.add(bonusLabel);
		
		final TextButton done = new TextButton("Done", skin);
		done.addListener(
			new ClickListener() {
				@Override
		        public void clicked(InputEvent event, float x, float y) {
					// this should have all of the logic for applying the bonuses
					character.addBonuses(bonuses);
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
		if (bonusPoints <= 0) nextScene();
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

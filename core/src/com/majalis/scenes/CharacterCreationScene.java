package com.majalis.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.OrderedMap;
import com.majalis.asset.AssetEnum;
import com.majalis.character.PlayerCharacter;
import com.majalis.character.AbstractCharacter.Stat;
import com.majalis.encounter.Background;
import com.majalis.save.SaveEnum;
import com.majalis.save.SaveManager;
import com.majalis.save.SaveService;
import com.majalis.save.SaveManager.JobClass;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class CharacterCreationScene extends Scene {

	private final SaveService saveService;
	private final BitmapFont font;
	private final PlayerCharacter character;
	private String classMessage;
	private String statMessage;
	private String statDescription;
	private int statPoints;
	private ObjectMap<Stat, Integer> statMap;
	private Group statGroup;
	private TextButton enchanterButton;
	
	// needs a done button, as well as other interface elements
	public CharacterCreationScene(OrderedMap<Integer, Scene> sceneBranches, int sceneCode, final SaveService saveService, BitmapFont font, Background background, AssetManager assetManager, final PlayerCharacter character, boolean story) {
		super(sceneBranches, sceneCode);
		this.saveService = saveService;
		this.font = font;
		this.character = character;
		this.addActor(background);

		statPoints = 3;
		statMap = resetObjectMap();
		
		Skin skin = assetManager.get(AssetEnum.UI_SKIN.getPath(), Skin.class);
		final Sound buttonSound = assetManager.get(AssetEnum.BUTTON_SOUND.getPath(), Sound.class);
		
		classMessage = "";
		statMessage = "";
		statDescription = "";
		
		final TextButton done = new TextButton("Done", skin);
		
		done.addListener(
			new ClickListener(){
				@Override
		        public void clicked(InputEvent event, float x, float y) {
					buttonSound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
					nextScene();		   
		        }
			}
		);
		done.setPosition(1522, 30);

		final Table statTable = new Table();
		
		statGroup = new Group();
		
		int offset = 0;
		for (final Stat stat: Stat.values()){
			Image statImage = new Image(assetManager.get(stat.getPath(), Texture.class));
			statImage.setSize(statImage.getWidth() / (statImage.getHeight() / 35), 35);
			statImage.setPosition(825, 650 - offset);
			statImage.addListener(new ClickListener(){
				@Override
		        public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
					statDescription = stat.getDescription();
				}
				@Override
		        public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
					statDescription = "";
				}
			});

			statGroup.addActor(statImage);
			offset += 75;
			
			TextButton buttonUp = new TextButton("+", skin);
			TextButton buttonDown = new TextButton("-", skin);
			buttonUp.addListener(new ClickListener(){
				@Override
		        public void clicked(InputEvent event, float x, float y) {
					buttonSound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
					
					int currentStatAllocation = statMap.get(stat);
					if (statPoints > 0 && (currentStatAllocation < 1 || (currentStatAllocation < 2 && noStatsAtMax()))){
						character.setStat(stat, character.getBaseStat(stat)+1);
						saveService.saveDataValue(SaveEnum.PLAYER, character);
						statPoints--;
						statMap.put(stat, currentStatAllocation+1);
						if (statPoints <= 0){
							addActor(done);
						}
						statMessage = "";
					}
					else {
						if (statPoints <= 0){
							statMessage = "You are out of stat points to allocate!";
						}
						else if (currentStatAllocation < 2){
							statMessage = "Only one stat may be\ntwo points above its base score!";
						}
						else {
							statMessage = "Your " + stat.toString() + " is at maximum!\nIt cannot be raised any more.";
						}
					}
		        }
			});
			buttonDown.addListener(new ClickListener(){
				@Override
		        public void clicked(InputEvent event, float x, float y) {
					buttonSound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
					int currentStatAllocation = statMap.get(stat);
					if (currentStatAllocation > 0 || (currentStatAllocation > -1 && statsAtNegative() < 2)){
						character.setStat(stat, character.getBaseStat(stat)-1);
						saveService.saveDataValue(SaveEnum.PLAYER, character);
						if (statPoints == 0){
							removeActor(done);
						}
						statPoints++;	
						statMap.put(stat, currentStatAllocation - 1);
						statMessage = "";
					}
					else {
						if (currentStatAllocation <= -1){
							statMessage = "Your " + stat.toString() + " is at minimum!\nIt cannot be lowered.";
						}
						else {
							statMessage = "You can only lower two stats below their base scores.";
						}
					}
		        }
			});
			statTable.add(buttonDown).size(45, 75);
			statTable.add(buttonUp).size(45, 75).row();
		}
		statTable.setPosition(773, 473);
		
		statGroup.addAction(Actions.hide());
		this.addActor(statGroup);
		
		Table table = new Table();
		this.addActor(table);	
		
		for (final JobClass jobClass: JobClass.values()) {
			TextButton button = new TextButton(jobClass.getLabel(), skin);
			if (story && jobClass != JobClass.ENCHANTRESS) {
				button.setTouchable(Touchable.disabled);
				TextButtonStyle style = new TextButtonStyle(button.getStyle());
				style.fontColor = Color.RED;
				style.up = style.disabled;
				button.setStyle(style);
			}
			else {
				button.addListener(new ClickListener() {
					@Override
			        public void clicked(InputEvent event, float x, float y) {
						buttonSound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
						classMessage = "You are now " + getJobClass(jobClass) + ".\n"
										+ getClassFeatures(jobClass);
						statGroup.removeAction(Actions.hide());
						statGroup.addAction(Actions.visible(true));
						statGroup.addAction(Actions.show());
						saveService.saveDataValue(SaveEnum.CLASS, jobClass);
						if (statPoints == 0){
							removeActor(done);
						}
						statPoints = 3;
						statMap = resetObjectMap();
						addActor(statTable);
			        }
				});
			}
			table.add(button).size(220, 60).row();
			if (story && jobClass == JobClass.ENCHANTRESS) {
				enchanterButton = button;
			}
		}
		table.setPosition(488, 488);
	}

	private ObjectMap<Stat, Integer> resetObjectMap() {
		ObjectMap<Stat, Integer> tempMap = new ObjectMap<Stat, Integer>();
		for (final Stat stat: Stat.values()){
			tempMap.put(stat, 0);
		}
		return tempMap;
	}
	
	private boolean noStatsAtMax() {
		for (Integer value: statMap.values()){
			if (value >= 2){
				return false;
			}
		}
		return true;
	}
	
	private int statsAtNegative() {
		int count = 0;
		for (Integer value: statMap.values()){
			if (value < 0){
				count++;
			}
		}
		return count;
	}
	
	private String getJobClass(SaveManager.JobClass jobClass) { return jobClass == SaveManager.JobClass.ENCHANTRESS ? "an Enchantress" : "a " + jobClass.getLabel(); }
	private String getClassFeatures(SaveManager.JobClass jobClass) {
		switch (jobClass){
			case WARRIOR: return "+1 Skill point.\nUnlocked \"Blitz\" Stance.\nGained perk \"Weak to Anal\".";
			case PALADIN: return "Combat Heal learned.";
			case THIEF:   return "+3 Skill points.\n+40 food.";
			case RANGER:  return "Received bow.";
			case MAGE:    return "+1 Magic point.\n";
			case ENCHANTRESS: return "+1 Perk point.\n";
			default: return "";
		}
	}
	
	@Override
    public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		font.setColor(0.4f,0.4f,0.4f,1);
		int base = 800;
		font.draw(batch, classMessage, base-450, 565 * 1.5f);
		if (statDescription.equals("")){
			font.draw(batch, statMessage, base - 50, 900);
		}
		else {
			font.draw(batch, statDescription, base - 50, 900);
		}
		
		int offset = 0;
		if (!classMessage.equals("")) {
			for (Stat stat: PlayerCharacter.Stat.values()) {
				font.setColor(0.6f, 0.2f, 0.1f, 1);
				int amount = character.getBaseStat(stat);
				setFontColor(font, amount);
				font.draw(batch, String.valueOf(amount), base+200, 675 - offset);
				font.draw(batch, "("+String.valueOf(statMap.get(stat))+")", base+215, 675 - offset);
				font.draw(batch, "- " + PlayerCharacter.getStatMap().get(stat).get(amount), base+260, 675 - offset);
				offset += 75;
			}
			font.draw(batch, "Stat points: " + statPoints, base + 100, 150);
		}
    }
	
	private void setFontColor(BitmapFont font, int amount){
		float red = amount / 10.0f;
		float green = .3f;
		float blue = (1 - (amount/10))/2;
		font.setColor(red, green, blue, 1);
	}
	
	@Override
	public void setActive() {
		isActive = true;
		this.removeAction(Actions.hide());
		this.addAction(Actions.visible(true));
		this.addAction(Actions.show());
		this.setBounds(0, 0, 2000, 2000);
		
		if (enchanterButton != null){
			InputEvent event1 = new InputEvent();
	        event1.setType(InputEvent.Type.touchDown);
	        enchanterButton.fire(event1);
	        InputEvent event2 = new InputEvent();
	        event2.setType(InputEvent.Type.touchUp);
	        enchanterButton.fire(event2);
		}
		
		saveService.saveDataValue(SaveEnum.SCENE_CODE, sceneCode);
	}
	
	private void nextScene() {
		sceneBranches.get(sceneBranches.orderedKeys().get(0)).setActive();
		isActive = false;
		addAction(Actions.hide());
	}
}

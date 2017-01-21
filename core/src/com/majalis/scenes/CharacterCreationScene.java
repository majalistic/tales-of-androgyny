package com.majalis.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.utils.Align;
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
	private ObjectMap<Stat, Integer> statMap;
	private TextButton enchanterButton;
	private int statPoints;
	
	public CharacterCreationScene(OrderedMap<Integer, Scene> sceneBranches, int sceneCode, final SaveService saveService, Background background, AssetManager assetManager, final PlayerCharacter character, final boolean story) {
		super(sceneBranches, sceneCode);
		this.saveService = saveService;
		this.addActor(background);

		statPoints = story ? 1 : 3;
		statMap = resetObjectMap();
		
		final Group statGroup = new Group();
		
		final Texture boxTexture = assetManager.get(AssetEnum.NORMAL_BOX.getPath(), Texture.class);
		final Image classSelectBox = new Image(boxTexture);
		classSelectBox.setBounds(80, 250, 300, 470);
		final Image statSelectBox = new Image(boxTexture);
		statSelectBox.setBounds(500, 100, 750, 570);
		final Image classDescriptionBox = new Image(boxTexture);
		classDescriptionBox.setBounds(-10, 700, 480, 190);
		final Image statDescriptionBox = new Image(boxTexture);
		statDescriptionBox.setBounds(425, 700, 990, 190);
		final Image statPointsBox = new Image(boxTexture);
		statPointsBox.setBounds(760, 40, 240, 50);
		
		this.addActor(classSelectBox);
		this.addActor(statSelectBox);
		this.addActor(classDescriptionBox);
		this.addActor(statDescriptionBox);
		this.addActor(statPointsBox);
		
		Skin skin = assetManager.get(AssetEnum.UI_SKIN.getPath(), Skin.class);
		final Sound buttonSound = assetManager.get(AssetEnum.BUTTON_SOUND.getPath(), Sound.class);
		
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

		final Label statPointDisplay = new Label("Stat points: " + statPoints, skin);
		statPointDisplay.setColor(Color.GRAY);
		statPointDisplay.setPosition(800, 50);
		
		int classBase = 230;
		
		final Label classMessage = new Label("", skin);
		classMessage.setColor(Color.BLACK);
		classMessage.setPosition(classBase, 800);
		classMessage.setAlignment(Align.center);
		final Label statMessage = new Label("", skin);
		statMessage.setColor(Color.RED);
		final Label statDescription = new Label("", skin);
		statDescription.setColor(Color.BLACK);
		statMessage.setPosition(525, 800);
		statDescription.setPosition(525, 800);
		this.addActor(classMessage);
		this.addActor(statMessage);
		this.addActor(statDescription);
		this.addActor(statPointDisplay);
		
		final Table statTable = new Table();
		
		final ObjectMap<Stat, Label> statToLabel = new ObjectMap<Stat, Label>();
		int offset = 0;
		int base = 700;
		for (final Stat stat: Stat.values()){
			Image statImage = new Image(assetManager.get(stat.getPath(), Texture.class));
			Label statLabel = new Label("", skin);
			statToLabel.put(stat, statLabel);
			int amount = character.getBaseStat(stat);
			setFontColor(statLabel, amount);
			setStatText(stat, character, statLabel);
		
			statLabel.setPosition(base + 175, 585 - offset);			
			statImage.setSize(statImage.getWidth() / (statImage.getHeight() / 35), 35);
			statImage.setPosition(base + 25, 567 - offset);
			statImage.addListener(new ClickListener(){
				@Override
		        public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
					statDescription.setText(stat.getDescription());
					statDescription.addAction(Actions.show());
					statMessage.addAction(Actions.hide());
				}
				@Override
		        public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
					statDescription.addAction(Actions.hide());
					statMessage.addAction(Actions.show());
				}
			});
			statGroup.addActor(statLabel);
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
						statPointDisplay.setText("Stat points: " + statPoints);
						statMap.put(stat, currentStatAllocation+1);
						if (statPoints <= 0){
							addActor(done);
						}
						setFontColor(statLabel, character.getBaseStat(stat));
						setStatText(stat, character, statLabel);
						statMessage.addAction(Actions.hide());
						statMessage.setText("");
					}
					else {
						if (statPoints <= 0){
							statMessage.setText("You are out of stat points to allocate!");
							
						}
						else if (currentStatAllocation < 2){
							statMessage.setText("Only one stat may be\ntwo points above its base score!");
						}
						else {
							statMessage.setText("Your " + stat.toString() + " is at maximum!\nIt cannot be raised any more.");
							
						}
						statMessage.addAction(Actions.show());
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
						statPointDisplay.setText("Stat points: " + statPoints);
						statMap.put(stat, currentStatAllocation - 1);
						setFontColor(statLabel, character.getBaseStat(stat));
						setStatText(stat, character, statLabel);
						statMessage.addAction(Actions.hide());
						statMessage.setText("");
					}
					else {
						if (currentStatAllocation <= (story ? 0 : -1)){
							statMessage.setText("Your " + stat.toString() + " is at minimum!\nIt cannot be lowered.");
						}
						else {
							statMessage.setText("You can only lower two stats below their base scores.");
						}
						statMessage.addAction(Actions.show());
					}
		        }
			});
			statTable.add(buttonDown).size(45, 75);
			statTable.add(buttonUp).size(45, 75).row();
		}
		statTable.setPosition(673, 393);
		
		statGroup.addAction(Actions.hide());
		this.addActor(statGroup);
		
		Table table = new Table();

		table.setPosition(classBase, 488);
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
						if (!story){
							buttonSound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
						}
						classMessage.setText("You are now " + getJobClass(jobClass) + ".\n"
										+ getClassFeatures(jobClass));
						statGroup.removeAction(Actions.hide());
						statGroup.addAction(Actions.visible(true));
						statGroup.addAction(Actions.show());
						saveService.saveDataValue(SaveEnum.CLASS, jobClass);
						if (statPoints == 0){
							removeActor(done);
						}
						statPoints = story ? 1 : 3;
						statPointDisplay.setText("Stat points: " + statPoints);
						statMap = resetObjectMap();
						for (Stat stat: Stat.values()) {
							Label statLabel = statToLabel.get(stat);
							setFontColor(statLabel, character.getBaseStat(stat));
							setStatText(stat, character, statLabel);
						}
						addActor(statTable);
			        }
				});
			}
			table.add(button).size(220, 60).row();
			if (story && jobClass == JobClass.ENCHANTRESS) {
				enchanterButton = button;
			}
		}
		
		if (story) {
			character.setBaseDefense(2);
		}
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
	
	private void setFontColor(Label font, int amount){
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
		label.setText(amount + " ("+String.valueOf(statMap.get(stat))+")" + " - " + PlayerCharacter.getStatMap().get(stat).get(amount));
	}
	
	@Override
	public void setActive() {
		isActive = true;
		this.removeAction(Actions.hide());
		this.addAction(Actions.visible(true));
		this.addAction(Actions.show());
		this.setBounds(0, 0, 2000, 2000);
		
		saveService.saveDataValue(SaveEnum.SCENE_CODE, sceneCode);
	}
	
	@Override
	public void act(float delta) {
		super.act(delta);
		if (enchanterButton != null){
			InputEvent event1 = new InputEvent();
	        event1.setType(InputEvent.Type.touchDown);
	        enchanterButton.fire(event1);
	        InputEvent event2 = new InputEvent();
	        event2.setType(InputEvent.Type.touchUp);
	        enchanterButton.fire(event2);
	        enchanterButton = null;
		}
	}
	
	private void nextScene() {
		sceneBranches.get(sceneBranches.orderedKeys().get(0)).setActive();
		isActive = false;
		addAction(Actions.hide());
	}
}

package com.majalis.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.OrderedMap;
import com.majalis.asset.AssetEnum;
import com.majalis.character.Perk;
import com.majalis.character.PlayerCharacter;
import com.majalis.character.Techniques;
import com.majalis.encounter.Background;
import com.majalis.save.SaveEnum;
import com.majalis.save.SaveService;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class SkillSelectionScene extends Scene {

	private final SaveService saveService;
	private final BitmapFont font;
	private final Skin skin;
	private final Sound buttonSound;
	private final PlayerCharacter character;
	private String console;
	private int skillPoints;
	private int magicPoints;
	private int perkPoints;
	private ObjectMap<Techniques, Integer> cachedSkills;
	private ObjectMap<Perk, Integer> cachedPerks;
	private ObjectMap<Techniques, Integer> skills;
	private ObjectMap<Perk, Integer> perks;
	private ObjectMap<Techniques, TextButton> techniquesToButtons;
	private String skillDisplay;
	
	public SkillSelectionScene(OrderedMap<Integer, Scene> sceneBranches, int sceneCode, final SaveService saveService, BitmapFont font, Background background, AssetManager assetManager, PlayerCharacter character) {
		super(sceneBranches, sceneCode);
		this.saveService = saveService;
		this.font = font;
		this.character = character;
		this.addActor(background);
		skin = assetManager.get(AssetEnum.UI_SKIN.getPath(), Skin.class);
		buttonSound = assetManager.get(AssetEnum.BUTTON_SOUND.getPath(), Sound.class);
		console = "";
		skillDisplay = "";
		techniquesToButtons = new ObjectMap<Techniques, TextButton>();
	}
	
	@Override
    public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		font.setColor(0.5f,0.4f,0,1);
		font.draw(batch, "Skill Selection", 145, 900);
		font.setColor(0.4f,0.4f,0.4f,1);
		int base = 975;
		if ( !skillDisplay.equals("") ){
			font.draw(batch, skillDisplay, base-900, 870);
		}
		font.draw(batch, console, base, 650);
		font.draw(batch, "Skill Points: " + skillPoints, base, 780);
		font.draw(batch, "Magic Points: " + magicPoints, base, 735);
		font.draw(batch, "Perk Points: " + perkPoints, base, 690);
    }
	
	@Override
	public void setActive() {
		isActive = true;
		this.removeAction(Actions.hide());
		this.addAction(Actions.visible(true));
		this.addAction(Actions.show());
		this.setBounds(0, 0, 2000, 2000);
		saveService.saveDataValue(SaveEnum.SCENE_CODE, sceneCode);
		
		this.skillPoints = character.getSkillPoints();
		this.magicPoints = character.getMagicPoints();
		this.perkPoints = character.getPerkPoints();
		this.cachedSkills = new ObjectMap<Techniques, Integer>(character.getSkills());
		this.cachedPerks = new ObjectMap<Perk, Integer>(character.getPerks());
		skills = new ObjectMap<Techniques, Integer>(cachedSkills);
		perks = new ObjectMap<Perk, Integer>(cachedPerks);
		
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
		done.setPosition(1523, 30);
		addActor(done);	
		
		final Table table = new Table();
		
		for (final Techniques technique: Techniques.getLearnableSkills()){
			Integer level = skills.get(technique, 0);
			final TextButton button = new TextButton(technique.getTrait().getName() + (level > 0 ? " (" + level + ")" : ""), skin);
			techniquesToButtons.put(technique, button);
			button.addListener(new ClickListener(){
				@Override
		        public void clicked(InputEvent event, float x, float y) {
					buttonSound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
					if (skillPoints > 0){
						Integer level = skills.get(technique);
						if (level == null) level = 0;
						if (level < technique.getMaxRank()){
							if (level + 1 <= skillPoints){
								skillPoints -= level + 1;
								skills.put(technique, ++level);						
								console = "You have learned " + technique.getTrait().getName() + " Rank " + level +".";
								button.setText(technique.getTrait().getName() + " (" + level + ")");
							}
							else {
								console = "You do not have enough skill points!";
							}
						}
						else {
							console = "You cannot improve on that skill any further!";
						}	
					}
					else {
						console = "You have no skill points!";
					}
		        }
				@Override
		        public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
					skillDisplay = technique.getTrait().getDescription();
				}
				@Override
		        public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
					skillDisplay = "";
				}
			});
			final TextButton minusButton = new TextButton("-", skin);
			minusButton.addListener(new ClickListener(){
				@Override
		        public void clicked(InputEvent event, float x, float y) {
					buttonSound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
					
					Integer level = skills.get(technique, 0);
					if (level == 0){
						console = "You do not yet possess that skill!";
					}
					else {
						Integer cachedLevel = cachedSkills.get(technique, 0);
						if (--level >= cachedLevel){
							skillPoints += level + 1;
							skills.put(technique, level);						
							console = "You have reduced " + technique.getTrait().getName() + " to Rank " + level +".";
							button.setText(technique.getTrait().getName() + (level == 0 ? "" : " (" + level + ")" ));
						}
						else {
							console = "You cannot reduce " + technique.getTrait().getName() + " below Rank " + cachedLevel +".";
						}
					}
		        }
			});
			
			table.add(button).size(350, 60);
			table.add(minusButton).size(45, 60).row();
		}
		table.setPosition(217, 300);
		addActor(table);
		
		final Table perkTable = new Table();
		for (final Perk perk: Perk.values()){
			if (!perk.isPositive()){ continue; }
			Integer level = perks.get(perk, 0);
			final TextButton button = new TextButton(perk.getLabel() + (level > 0 ? " (" + level + ")" : ""), skin);
			button.addListener(new ClickListener(){
				@Override
		        public void clicked(InputEvent event, float x, float y) {
					buttonSound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
					if (perkPoints > 0){
						Integer level = perks.get(perk, 0);
						if (level < perk.getMaxRank()){
							if (level + 1 <= perkPoints){
								perkPoints -= level + 1;
								if (perk == Perk.SKILLED) {
									skillPoints += 2;
								}
								
								perks.put(perk, ++level);						
								console = "You gained the " + perk.getLabel() + " Rank " + level +".";
								button.setText(perk.getLabel() + (level == 0 ? "" : " (" + level + ")" ));
							}
							else {
								console = "You do not have enough perk points!";
							}	
						}
						else {
							console = "You cannot improve on that perk any further!";
						}		
					}
					else {
						console = "You have no perk points!";
					}
		        }
				@Override
		        public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
					skillDisplay = perk.getDescription();
				}
				@Override
		        public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
					skillDisplay = "";
				}
			});
			final TextButton minusButton = new TextButton("-", skin);
			minusButton.addListener(new ClickListener(){
				@Override
		        public void clicked(InputEvent event, float x, float y) {
					buttonSound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
					
					Integer level = perks.get(perk, 0);
					if (level == 0){
						console = "You do not yet possess that perk!";
					}
					else {
						Integer cachedLevel = cachedPerks.get(perk, 0);
						if (--level >= cachedLevel){
							perkPoints += level + 1;
							perks.put(perk, level);
							console = "You have reduced " + perk.getLabel() + " to Rank " + level +".";
							
							if (perk == Perk.SKILLED) {
								skillPoints -= 2;
								handleNegativeSkillPoints();
							}
							
							button.setText(perk.getLabel() + (level == 0 ? "" : " (" + level + ")" ));
						}
						else {
							console = "You cannot reduce " + perk.getLabel() + " below Rank " + cachedLevel +".";
						}
					}
		        }
			});
			perkTable.add(button).size(350, 60);
			perkTable.add(minusButton).size(45, 60).row();
		}
		perkTable.setPosition(1087, 300);
		addActor(perkTable);
		
		if (character.hasMagic()){
			final Table magicTable = new Table();
			
			for (final Techniques technique: Techniques.getLearnableSpells()){
				Integer level = skills.get(technique, 0);
				final TextButton button = new TextButton(technique.getTrait().getName() + (level > 0 ? " (" + level + ")" : ""), skin);
				button.addListener(new ClickListener(){
					@Override
			        public void clicked(InputEvent event, float x, float y) {
						buttonSound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
						if (magicPoints > 0){
							Integer level = skills.get(technique, 0);
							if (level < technique.getMaxRank()){
								if (level + 1 <= magicPoints){
									magicPoints -= level + 1;
									skills.put(technique, ++level);						
									console = "You have learned " + technique.getTrait().getName() + " Rank " + level +".";
									button.setText(technique.getTrait().getName() + " (" + level + ")");
								}
								else {
									console = "You do not have enough magic points!";
								}
							}
							else {
								console = "You cannot improve on that spell any further!";
							}	
						}
						else {
							console = "You have no magic points!";
						}
			        }
					@Override
			        public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
						skillDisplay = technique.getTrait().getDescription();
					}
					@Override
			        public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
						skillDisplay = "";
					}
				});
				final TextButton minusButton = new TextButton("-", skin);
				minusButton.addListener(new ClickListener(){
					@Override
			        public void clicked(InputEvent event, float x, float y) {
						buttonSound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
						
						Integer level = skills.get(technique, 0);
						if (level == 0){
							console = "You do not yet possess that spell!";
						}
						else {
							Integer cachedLevel = cachedSkills.get(technique, 0);
							if (--level >= cachedLevel){
								magicPoints += level + 1;
								skills.put(technique, level);						
								console = "You have reduced " + technique.getTrait().getName() + " to Rank " + level +".";
								button.setText(technique.getTrait().getName() + (level == 0 ? "" : " (" + level + ")" ));
							}
							else {
								console = "You cannot reduce " + technique.getTrait().getName() + " below Rank " + cachedLevel +".";
							}
						}
			        }
				});
				magicTable.add(button).size(350, 60);
				magicTable.add(minusButton).size(45, 60).row();
			}
			magicTable.setPosition(653, 300);
			this.addActor(magicTable);
		}
	}
	
	private void handleNegativeSkillPoints() {
		while (skillPoints < 0){
			for (Techniques technique : skills.keys()){
				Integer cachedLevel = cachedSkills.get(technique, 0);
				Integer currentLevel = skills.get(technique, 0);
				if (currentLevel > cachedLevel){
					skillPoints += currentLevel;
					skills.put(technique, --currentLevel);
					console += "\nReduced " + technique.getTrait().getName() + " to Rank " + currentLevel +".";
					techniquesToButtons.get(technique).setText(technique.getTrait().getName() + (currentLevel == 0 ? "" : " (" + currentLevel + ")" ));
					break;
				}
			}
		}
		
	}
	
	private void nextScene(){
		character.setSkillPoints(skillPoints);
		character.setMagicPoints(magicPoints);
		character.setPerkPoints(perkPoints);
		character.setSkills(skills);
		character.setPerks(perks);
		saveService.saveDataValue(SaveEnum.PLAYER, character);
	
		sceneBranches.get(sceneBranches.orderedKeys().get(0)).setActive();
		isActive = false;
		addAction(Actions.hide());
	}
}

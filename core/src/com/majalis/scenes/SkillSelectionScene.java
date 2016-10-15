package com.majalis.scenes;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.OrderedMap;
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
	
	// needs a done button, as well as other interface elements
	public SkillSelectionScene(OrderedMap<Integer, Scene> sceneBranches, int sceneCode, final SaveService saveService, BitmapFont font, Background background, AssetManager assetManager, PlayerCharacter character) {
		super(sceneBranches, sceneCode);
		this.saveService = saveService;
		this.font = font;
		this.character = character;
		this.addActor(background);
		skin = assetManager.get("uiskin.json", Skin.class);
		buttonSound = assetManager.get("sound.wav", Sound.class);
		console = "";
	}
	
	@Override
    public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		font.setColor(0.5f,0.4f,0,1);
		font.draw(batch, "Skill Selection", 145, 600);
		font.setColor(0.4f,0.4f,0.4f,1);
		int base = 500;
		font.draw(batch, console, base, 550);
		font.draw(batch, "Skill Points: " + skillPoints, base, 520);
		font.draw(batch, "Magic Points: " + magicPoints, base, 490);
		font.draw(batch, "Perk Points: " + perkPoints, base, 460);
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
		done.setWidth(180); 
		done.setHeight(40);
		done.addListener(
			new ClickListener(){
				@Override
		        public void clicked(InputEvent event, float x, float y) {
					buttonSound.play(.5f);
					nextScene();		   
		        }
			}
		);
		done.addAction(Actions.moveTo(done.getX() + 1015, done.getY() + 20));
		addActor(done);	
		
		final Table table = new Table();
		
		for (final Techniques technique: Techniques.getLearnableSkills()){
			Integer level = skills.get(technique);
			if (level == null) level = 0;
			final TextButton button = new TextButton(technique.getTrait().getName() + (level > 0 ? " (" + level + ")" : ""), skin);
			button.addListener(new ClickListener(){
				@Override
		        public void clicked(InputEvent event, float x, float y) {
					buttonSound.play(.5f);
					if (skillPoints > 0){
						Integer level = skills.get(technique);
						if (level == null) level = 0;
						if (level < technique.getMaxRank()){
							skillPoints--;
							skills.put(technique, ++level);						
							console = "You have learned " + technique.getTrait().getName() + " Rank " + level +".";
							button.setText(technique.getTrait().getName() + " (" + level + ")");
						}
						else {
							console = "You cannot improve on that skill any further!";
						}
						
					}
					else {
						console = "You have no skill points!";
					}
		        }
			});
			table.add(button).width(220).height(40).row();
		}
		table.addAction(Actions.moveTo(table.getX() + 145, table.getY() + 200));
		addActor(table);
		
		final Table perkTable = new Table();
		for (final Perk perk: Perk.values()){
			Integer level = perks.get(perk);
			if (level == null) level = 0;
			final TextButton button = new TextButton(perk.getLabel() + (level > 0 ? " (" + level + ")" : ""), skin);
			button.addListener(new ClickListener(){
				@Override
		        public void clicked(InputEvent event, float x, float y) {
					buttonSound.play(.5f);
					if (perkPoints > 0){
						Integer level = perks.get(perk);
						if (level == null) level = 0;
						if (level < perk.getMaxRank()){
							perkPoints--;
							if (perk == Perk.SKILLED) {
								skillPoints += 2;
							}
							
							perks.put(perk, ++level);						
							console = "You gained the " + perk.getLabel() + " Rank " + level +".";
							button.setText(perk.getLabel() + " (" + level + ")");	
						}
						else {
							console = "You cannot improve on that perk any further!";
						}		
					}
					else {
						console = "You have no perk points!";
					}
		        }
			});
			perkTable.add(button).width(220).height(40).row();
		}
		perkTable.addAction(Actions.moveTo(perkTable.getX() + 725, perkTable.getY() + 200));
		addActor(perkTable);
		
		if (character.hasMagic()){
			final Table magicTable = new Table();
			
			for (final Techniques technique: Techniques.getLearnableSpells()){
				Integer level = skills.get(technique);
				if (level == null) level = 0;
				final TextButton button = new TextButton(technique.getTrait().getName() + (level > 0 ? " (" + level + ")" : ""), skin);
				button.addListener(new ClickListener(){
					@Override
			        public void clicked(InputEvent event, float x, float y) {
						buttonSound.play(.5f);
						if (magicPoints > 0){
							Integer level = skills.get(technique);
							if (level == null) level = 0;
							if (level < technique.getMaxRank()){
								magicPoints--;
								skills.put(technique, ++level);						
								console = "You have learned " + technique.getTrait().getName() + " Rank " + level +".";
								button.setText(technique.getTrait().getName() + " (" + level + ")");
							}
							else {
								console = "You cannot improve on that spell any further!";
							}	
						}
						else {
							console = "You have no magic points!";
						}
			        }
				});
				magicTable.add(button).width(140).width(220).height(40).row();
			}
			magicTable.addAction(Actions.moveTo(magicTable.getX() + 435, magicTable.getY() + 200));
			this.addActor(magicTable);
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

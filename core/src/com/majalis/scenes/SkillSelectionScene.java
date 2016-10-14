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
import com.badlogic.gdx.utils.OrderedMap;
import com.majalis.character.Perk;
import com.majalis.character.PlayerCharacter;
import com.majalis.character.Techniques;
import com.majalis.character.PlayerCharacter.Stat;
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
	
	// needs a done button, as well as other interface elements
	public SkillSelectionScene(OrderedMap<Integer, Scene> sceneBranches, int sceneCode, final SaveService saveService, BitmapFont font, Background background, AssetManager assetManager, PlayerCharacter character) {
		super(sceneBranches, sceneCode);
		this.saveService = saveService;
		this.font = font;
		this.addActor(background);
		this.character = character;
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
		font.draw(batch, "Skill Points: " + character.getSkillPoints(), base, 520);
		font.draw(batch, "Magic Points: " + character.getMagicPoints(), base, 490);
		font.draw(batch, "Perk Points: " + character.getPerkPoints(), base, 460);
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
		done.setWidth(180); //Sets positional stuff for "done" button)
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
		
		
		final Table table = new Table();
		
		for (final Techniques technique: Techniques.getLearnableSkills()){
			if (character.getSkills().contains(technique)) continue;
			final TextButton button = new TextButton(technique.getTrait().getName(), skin);
			button.addListener(new ClickListener(){
				@Override
		        public void clicked(InputEvent event, float x, float y) {
					buttonSound.play(.5f);
					console = "You have learned " + technique.getTrait().getName() + ".";
					table.removeActor(button);
					
					character.decrementSkillPoints();
					if (character.getSkillPoints() <= 0){
						removeActor(table);
						if (character.getMagicPoints() <= 0 && character.getPerkPoints() <= 0){
							addActor(done);
						}
					}
					saveService.saveDataValue(SaveEnum.SKILL, technique);
					saveService.saveDataValue(SaveEnum.PLAYER, character);
		        }
			});
			table.add(button).width(220).height(40).row();
		}
		table.addAction(Actions.moveTo(table.getX() + 145, table.getY() + 200));
		if (character.getSkillPoints() > 0){
			this.addActor(table);
		}
		
		final Table perkTable = new Table();
		for (final Perk perk: Perk.values()){
			final TextButton button = new TextButton(perk.toString(), skin);
			button.addListener(new ClickListener(){
				@Override
		        public void clicked(InputEvent event, float x, float y) {
					buttonSound.play(.5f);
					console = "You gained the " + perk.toString() + " perk!";
					perkTable.removeActor(button);
					
					character.decrementPerkPoints();
					if (perk == Perk.SKILLED) {
						if (character.getSkillPoints() == 0){
							addActor(table);
						}
						character.modSkillPoints(2);
					}
					else if (perk == Perk.WELLROUNDED) increaseLowestStat(); 
					if (character.getPerkPoints() <= 0){
						removeActor(perkTable);
						if (character.getMagicPoints() <= 0 && character.getSkillPoints() <= 0){
							addActor(done);
						}
					}
					saveService.saveDataValue(SaveEnum.PERK, perk);
					saveService.saveDataValue(SaveEnum.PLAYER, character);
		        }
			});
			perkTable.add(button).width(220).height(40).row();
		}
		perkTable.addAction(Actions.moveTo(perkTable.getX() + 725, perkTable.getY() + 200));
		if (character.getPerkPoints() > 0){
			this.addActor(perkTable);
		}
		
		
		final Table magicTable = new Table();
		
		for (final Techniques technique: Techniques.getLearnableSpells()){
			if (character.getSkills().contains(technique)) continue;
			final TextButton button = new TextButton(technique.getTrait().getName(), skin);
			button.addListener(new ClickListener(){
				@Override
		        public void clicked(InputEvent event, float x, float y) {
					buttonSound.play(.5f);
					console = "You have learned " + technique.getTrait().getName() + ".";
					magicTable.removeActor(button);
					
					character.decrementMagicPoints();
					if (character.getMagicPoints() <= 0){
						removeActor(magicTable);
					}
					if (character.getSkillPoints() <= 0 && character.getPerkPoints() <= 0){
						addActor(done);
					}
					saveService.saveDataValue(SaveEnum.SKILL, technique);
					saveService.saveDataValue(SaveEnum.PLAYER, character);
		        }
			});
			magicTable.add(button).width(140).width(220).height(40).row();
		}
		magicTable.addAction(Actions.moveTo(magicTable.getX() + 435, magicTable.getY() + 200));
		if (character.getMagicPoints() > 0){
			this.addActor(magicTable);
		}
		
		if(character.getSkillPoints() <= 0 && character.getPerkPoints() <= 0 && character.getMagicPoints() <= 0){
			addActor(done);
		}
		
	}
	
	private  void increaseLowestStat(){
		Stat lowest = Stat.values()[0];
		int min = character.getStat(lowest);
		for (Stat stat: Stat.values()){
			if (character.getStat(stat) < min){
				min = character.getStat(stat);
				lowest = stat;
			}
		}
		character.setStat(lowest, character.getStat(lowest) + 1);		
	}
	
	private void nextScene(){
		sceneBranches.get(sceneBranches.orderedKeys().get(0)).setActive();
		isActive = false;
		addAction(Actions.hide());
	}
}

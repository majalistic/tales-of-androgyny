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
		font.draw(batch, "Skill Selection", 600, 600);
		font.setColor(0.4f,0.4f,0.4f,1);
		int base = 500;
		font.draw(batch, console, base, 550);
		font.draw(batch, "Skill Points: " + character.skillPoints, base, 520);
		font.draw(batch, "Magic Points: " + character.magicPoints, base, 490);
		font.draw(batch, "Perk Points: " + character.perkPoints, base, 460);
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
			new ClickListener(){
				@Override
		        public void clicked(InputEvent event, float x, float y) {
					buttonSound.play();
					nextScene();		   
		        }
			}
		);
		done.addAction(Actions.moveTo(done.getX() + 1100, done.getY() + 20));
		
		
		final Table table = new Table();
		
		for (final Techniques technique: Techniques.getLearnableSkills()){
			if (character.getSkills().contains(technique)) continue;
			final TextButton button = new TextButton(technique.toString(), skin);
			button.addListener(new ClickListener(){
				@Override
		        public void clicked(InputEvent event, float x, float y) {
					buttonSound.play();
					console = "You have learned " + technique.toString() + ".";
					table.removeActor(button);
					
					character.skillPoints--;
					if (character.skillPoints <= 0){
						removeActor(table);
						if (character.magicPoints <= 0 && character.perkPoints <= 0){
							addActor(done);
						}
					}
					saveService.saveDataValue(SaveEnum.SKILL, technique);
					saveService.saveDataValue(SaveEnum.PLAYER, character);
		        }
			});
			table.add(button).width(140).row();
		}
		table.addAction(Actions.moveTo(table.getX() + 325, table.getY() + 400));
		if (character.skillPoints > 0){
			this.addActor(table);
		}
		
		final Table perkTable = new Table();
		for (final Perk perk: Perk.values()){
			final TextButton button = new TextButton(perk.toString(), skin);
			button.addListener(new ClickListener(){
				@Override
		        public void clicked(InputEvent event, float x, float y) {
					buttonSound.play();
					console = "You gained the " + perk.toString() + " perk!";
					perkTable.removeActor(button);
					
					character.perkPoints--;
					if (perk == Perk.SKILLED) {
						if (character.skillPoints == 0){
							addActor(table);
						}
						character.skillPoints += 2;
					}
					else if (perk == Perk.WELLROUNDED) increaseLowestStat(); 
					if (character.perkPoints <= 0){
						removeActor(perkTable);
						if (character.magicPoints <= 0 && character.skillPoints <= 0){
							addActor(done);
						}
					}
					saveService.saveDataValue(SaveEnum.PERK, perk);
					saveService.saveDataValue(SaveEnum.PLAYER, character);
		        }
			});
			perkTable.add(button).width(140).row();
		}
		perkTable.addAction(Actions.moveTo(perkTable.getX() + 725, perkTable.getY() + 400));
		if (character.perkPoints > 0){
			this.addActor(perkTable);
		}
		
		
		final Table magicTable = new Table();
		
		for (final Techniques technique: Techniques.getLearnableSpells()){
			if (character.getSkills().contains(technique)) continue;
			final TextButton button = new TextButton(technique.toString(), skin);
			button.addListener(new ClickListener(){
				@Override
		        public void clicked(InputEvent event, float x, float y) {
					buttonSound.play();
					console = "You have learned " + technique.toString() + ".";
					magicTable.removeActor(button);
					
					character.magicPoints--;
					if (character.magicPoints <= 0){
						removeActor(magicTable);
					}
					if (character.skillPoints <= 0 && character.perkPoints <= 0){
						addActor(done);
					}
					saveService.saveDataValue(SaveEnum.SKILL, technique);
					saveService.saveDataValue(SaveEnum.PLAYER, character);
		        }
			});
			magicTable.add(button).width(140).row();
		}
		magicTable.addAction(Actions.moveTo(magicTable.getX() + 525, magicTable.getY() + 400));
		if (character.magicPoints > 0){
			this.addActor(magicTable);
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

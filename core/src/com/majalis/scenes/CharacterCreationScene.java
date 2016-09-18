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
import com.majalis.character.PlayerCharacter;
import com.majalis.character.PlayerCharacter.Stat;
import com.majalis.save.SaveEnum;
import com.majalis.save.SaveManager;
import com.majalis.save.SaveService;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class CharacterCreationScene extends Scene {

	private final SaveService saveService;
	private final BitmapFont font;
	private final Sound buttonSound;
	private final PlayerCharacter character;
	private String console;
	
	// needs a done button, as well as other interface elements
	public CharacterCreationScene(OrderedMap<Integer, Scene> sceneBranches, int sceneCode, final SaveService saveService, BitmapFont font, AssetManager assetManager, PlayerCharacter character) {
		super(sceneBranches, sceneCode);
		this.saveService = saveService;
		this.font = font;
		this.character = character;
		
		Skin skin = assetManager.get("uiskin.json", Skin.class);
		buttonSound = assetManager.get("sound.wav", Sound.class);
		
		console = "";
		saveService.saveDataValue(SaveEnum.CLASS, SaveManager.JobClass.ENCHANTRESS);
		
		Table table = new Table();
		
		for (final SaveManager.JobClass jobClass: SaveManager.JobClass.values()){
			TextButton button = new TextButton(jobClass.getLabel(), skin);
			button.addListener(new ClickListener(){
				@Override
		        public void clicked(InputEvent event, float x, float y) {
					buttonSound.play();
					console = "You are now " + getJobClass(jobClass) + ".";
					saveService.saveDataValue(SaveEnum.CLASS, jobClass);
		        }
			});
			table.add(button).row();
		}
		table.addAction(Actions.moveTo(table.getX() + 450, table.getY() + 400));
		this.addActor(table);
		
		TextButton done = new TextButton("Done", skin);
		
		done.addListener(
			new ClickListener(){
				@Override
		        public void clicked(InputEvent event, float x, float y) {
					buttonSound.play();
					nextScene();		   
		        }
			}
		);
		done.addAction(Actions.moveTo(done.getX() + 1100, done.getY() + 50));
		this.addActor(done);
	}

	private String getJobClass(SaveManager.JobClass jobClass){ return jobClass == SaveManager.JobClass.ENCHANTRESS ? "an Enchantress" : "a " + jobClass.getLabel(); }
	
	@Override
	public void poke(){
		buttonSound.play();
		nextScene();
	}
	
	@Override
    public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		font.setColor(0.5f,0.4f,0,1);
		font.draw(batch, "Character Creation", 600, 600);
		font.setColor(0.4f,0.4f,0.4f,1);
		font.draw(batch, console, 600, 550);
		int offset = 0;
		for (Stat stat: PlayerCharacter.Stat.values()){
			font.setColor(0.6f,0.2f,0.1f,1);
			font.draw(batch, stat.toString(), 650, 500 - offset);
			font.draw(batch, ": ", 750, 500 - offset);
			int amount = character.getStat(stat);
			setFontColor(font, amount);
			font.draw(batch, String.valueOf(amount), 770, 500 - offset);
			font.draw(batch, "- " + PlayerCharacter.statNameMap.get(stat).get(amount), 785, 500 - offset);
			offset += 50;
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
		saveService.saveDataValue(SaveEnum.SCENE_CODE, sceneCode);
	}
	
	private void nextScene(){
		sceneBranches.get(sceneBranches.orderedKeys().get(0)).setActive();
		isActive = false;
		addAction(Actions.hide());
	}
}

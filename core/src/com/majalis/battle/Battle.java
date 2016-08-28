package com.majalis.battle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.majalis.character.EnemyCharacter;
import com.majalis.character.PlayerCharacter;
import com.majalis.save.SaveService;

public class Battle extends Group{

	private final PlayerCharacter character;
	private final EnemyCharacter enemy;
	private final SaveService saveService;
	private final BitmapFont font;
	private final int victoryScene;
	private final int defeatScene;
	public boolean battleOver;
	public boolean victory;
	
	public Battle(SaveService saveService, BitmapFont font, PlayerCharacter character, EnemyCharacter enemy,  int victoryScene, int defeatScene) {
		this.saveService = saveService;
		this.font = font;
		this.character = character;
		this.enemy = enemy;
		this.victoryScene = victoryScene;
		this.defeatScene = defeatScene;
		battleOver = false;
		//this.addActor(character);
		this.addActor(enemy);
	}

	public void battleLoop() {
		// temporary debug commands
		if (Gdx.input.isKeyJustPressed(Keys.ENTER)){
			victory = true;
			battleOver = true;
		}
		else if (Gdx.input.isKeyJustPressed(Keys.SPACE)){
			victory = false;
			battleOver = true;
		}
		else if (Gdx.input.isKeyJustPressed(Keys.A)){
			enemy.currentHealth--;
		}
		else if (Gdx.input.isKeyJustPressed(Keys.S)){
			character.currentHealth--;
			saveService.saveDataValue("Player", character);
		}
		
		if (character.currentHealth <= 0){
			victory = false;
			battleOver = true;
		}
		if (enemy.currentHealth <= 0){
			victory = true;
			battleOver = true;
		}
	}
	
	@Override
    public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		font.draw(batch, "Your health: " + String.valueOf(character.currentHealth), 500, 200);
		font.draw(batch, "Enemy health: " + String.valueOf(enemy.currentHealth), 700, 200);
    }
	
	public int getVictoryScene(){
		return victoryScene;
	}
	
	public int getDefeatScene(){
		return defeatScene;
	}
}

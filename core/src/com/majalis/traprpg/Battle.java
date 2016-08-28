package com.majalis.traprpg;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Group;

public class Battle extends Group{

	private final PlayerCharacter character;
	private final EnemyCharacter enemy;
	private final int victoryScene;
	private final int defeatScene;
	public boolean battleOver;
	public boolean victory;
	
	public Battle(PlayerCharacter character, EnemyCharacter enemy, int victoryScene, int defeatScene) {
		this.character = character;
		this.enemy = enemy;
		this.victoryScene = victoryScene;
		this.defeatScene = defeatScene;
		battleOver = false;
		this.addActor(character);
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
		if (character.currentHealth < 0){
			victory = false;
			battleOver = true;
		}
		if (enemy.currentHealth < 0){
			victory = true;
			battleOver = true;
		}
	}
	
	@Override
    public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
    }
	
	public int getVictoryScene(){
		return victoryScene;
	}
	
	public int getDefeatScene(){
		return defeatScene;
	}
}

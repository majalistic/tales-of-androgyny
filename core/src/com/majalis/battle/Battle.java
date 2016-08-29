package com.majalis.battle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.majalis.character.AbstractCharacter;
import com.majalis.character.EnemyCharacter;
import com.majalis.character.PlayerCharacter;
import com.majalis.character.Technique;
import com.majalis.save.SaveEnum;
import com.majalis.save.SaveService;

public class Battle extends Group{

	private final PlayerCharacter character;
	private final EnemyCharacter enemy;
	private final SaveService saveService;
	private final BitmapFont font;
	private final int victoryScene;
	private final int defeatScene;
	private String console;
	public boolean battleOver;
	public boolean victory;
	
	public Battle(SaveService saveService, BitmapFont font, PlayerCharacter character, EnemyCharacter enemy,  int victoryScene, int defeatScene) {
		this.saveService = saveService;
		this.font = font;
		this.character = character;
		this.enemy = enemy;
		this.victoryScene = victoryScene;
		this.defeatScene = defeatScene;
		console = "";
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
		// attack with balanced attack
		else if (Gdx.input.isKeyJustPressed(Keys.A)){
			// handle synchronous attacks
			
			// possibly construct a separate class for this
			resolveTechniques(character, character.getTechnique(enemy), enemy, enemy.getTechnique(character));
			
			saveService.saveDataValue(SaveEnum.PLAYER, character);
			saveService.saveDataValue(SaveEnum.ENEMY, enemy);
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
	
	private void resolveTechniques(AbstractCharacter firstCharacter, Technique firstTechnique, AbstractCharacter secondCharacter, Technique secondTechnique) {
		
		Attack attackForFirst = new Attack(secondTechnique.getDamage());
		Attack attackForSecond = new Attack(firstTechnique.getDamage());
				
		console = "";
		console += getResultString(firstCharacter, secondCharacter, attackForSecond);
		console += getResultString(secondCharacter, firstCharacter, attackForFirst);		
	}

	private String getResultString(AbstractCharacter firstCharacter, AbstractCharacter secondCharacter, Attack attackForSecond){
		return firstCharacter.label + (firstCharacter.secondPerson ? " hit " : " hits ") + secondCharacter.label + " for " + secondCharacter.receiveAttack(attackForSecond) + " damage! ";
	}
	
	@Override
    public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		font.draw(batch, "Your health: " + String.valueOf(character.currentHealth), 500, 200);
		font.draw(batch, "Enemy health: " + String.valueOf(enemy.currentHealth), 700, 200);
		font.draw(batch, console, 450, 100);
    }
	
	public int getVictoryScene(){
		return victoryScene;
	}
	
	public int getDefeatScene(){
		return defeatScene;
	}
}

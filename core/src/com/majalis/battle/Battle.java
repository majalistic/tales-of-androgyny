package com.majalis.battle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.majalis.character.AbstractCharacter;
import com.majalis.character.AbstractCharacter.Stance;
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
	public boolean gameExit;
	
	public Battle(SaveService saveService, BitmapFont font, PlayerCharacter character, EnemyCharacter enemy,  int victoryScene, int defeatScene) {
		this.saveService = saveService;
		this.font = font;
		this.character = character;
		this.enemy = enemy;
		this.victoryScene = victoryScene;
		this.defeatScene = defeatScene;
		console = "";
		battleOver = false;
		gameExit = false;
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
		else if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)){
			gameExit = true;
		}
		// attack with balanced attack
		else {
			int keyPress = getKeyPress();
			if (keyPress != -1){				
				// handle synchronous attacks
					
				// possibly construct a separate class for this
				resolveTechniques(character, character.getTechnique(keyPress), enemy, enemy.getTechnique(character));
				
				saveService.saveDataValue(SaveEnum.PLAYER, character);
				saveService.saveDataValue(SaveEnum.ENEMY, enemy);
			}
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
	
	private int getKeyPress() {
		int[] possibleKeys = new int[]{Keys.A, Keys.S, Keys.D};
		for (int possibleKey : possibleKeys){
			if (Gdx.input.isKeyJustPressed(possibleKey)){
				return possibleKey;
			}
		}
		return -1;
	}

	private void resolveTechniques(AbstractCharacter firstCharacter, Technique firstTechnique, AbstractCharacter secondCharacter, Technique secondTechnique) {
		Attack attackForFirst = new Attack(secondTechnique.getDamage());
		Attack attackForSecond = new Attack(firstTechnique.getDamage());
				
		console = "";
		Stance firstStance = firstTechnique.getStance();
		Stance secondStance = secondTechnique.getStance();
		console += getStanceString(firstCharacter, firstStance);
		console += getStanceString(secondCharacter, secondStance);
		
		firstCharacter.stance = firstStance;
		secondCharacter.stance = secondStance;
		
		console += "\n";
		
		console += getResultString(firstCharacter, secondCharacter, firstTechnique, attackForSecond);
		console += getResultString(secondCharacter, firstCharacter, secondTechnique, attackForFirst);		
	}

	private String getStanceString(AbstractCharacter character, Stance stance) {
		return character.label + (character.secondPerson ? " adopt " : " adopts ") + " a(n) " + stance.toString() + " stance!\n";
	}
	
	private String getResultString(AbstractCharacter firstCharacter, AbstractCharacter secondCharacter, Technique technique, Attack attackForSecond){
		return firstCharacter.label + (firstCharacter.secondPerson ? " use " : " uses ") + technique.getTechniqueName() + " against " + (secondCharacter.secondPerson ? secondCharacter.label.toLowerCase() : secondCharacter.label) + " for " + secondCharacter.receiveAttack(attackForSecond) + " damage!\n";
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

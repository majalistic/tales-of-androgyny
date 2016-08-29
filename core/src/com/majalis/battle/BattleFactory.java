package com.majalis.battle;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.majalis.character.EnemyCharacter;
import com.majalis.character.PlayerCharacter;
import com.majalis.save.LoadService;
import com.majalis.save.SaveEnum;
import com.majalis.save.SaveManager;
import com.majalis.save.SaveService;

public class BattleFactory {

	private final SaveService saveService;
	private final LoadService loadService;
	private final AssetManager assetManager;
	private final BitmapFont font;
	public BattleFactory(SaveManager saveManager, AssetManager assetManager, BitmapFont font){
		this.saveService = saveManager;
		this.loadService = saveManager;
		this.assetManager = assetManager;
		this.font = font;
	}
	
	public Battle getBattle(BattleCode battleCode, PlayerCharacter playerCharacter) {
		EnemyCharacter enemy = loadService.loadDataValue(SaveEnum.ENEMY, EnemyCharacter.class);
		if (enemy == null || enemy.currentHealth <= 0){
			boolean werewolf = Math.floor(Math.random()*10) % 2 == 0;
			enemy = new EnemyCharacter(assetManager.get(werewolf ? "wereslut.png" : "harpy.jpg", Texture.class), werewolf);
		}
		else {
			boolean werewolf = Math.floor(Math.random()*10) % 2 == 0;
			enemy.setTexture(assetManager.get(werewolf ? "wereslut.png" : "harpy.jpg", Texture.class));
			enemy.setOwnPosition(werewolf);
		}
		switch(battleCode.battleCode){	
			default: 
				return new Battle( saveService, font, playerCharacter, enemy, battleCode.victoryScene, battleCode.defeatScene);
		}
	}

}

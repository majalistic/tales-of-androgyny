package com.majalis.traprpg;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

public class BattleFactory {

	private final SaveService saveService;
	private final AssetManager assetManager;
	private final BitmapFont font;
	public BattleFactory(SaveService saveService, AssetManager assetManager, BitmapFont font){
		this.saveService = saveService;
		this.assetManager = assetManager;
		this.font = font;
	}
	
	public Battle getBattle(BattleCode battleCode, PlayerCharacter playerCharacter) {
		switch(battleCode.battleCode){
			
			default: 
				boolean werewolf = Math.floor(Math.random()*10) % 2 == 0;
				return new Battle( saveService, font, playerCharacter, new EnemyCharacter(assetManager.get(werewolf ? "wereslut.png" : "harpy.jpg", Texture.class), werewolf), battleCode.victoryScene, battleCode.defeatScene);
		}
	}

}

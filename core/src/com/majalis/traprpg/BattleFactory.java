package com.majalis.traprpg;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;

public class BattleFactory {

	private final AssetManager assetManager;
	public BattleFactory(AssetManager assetManager){
		this.assetManager = assetManager;
	}
	
	public Battle getBattle(BattleCode battleCode) {
		switch(battleCode.battleCode){
			
			default: 
				boolean werewolf = Math.floor(Math.random()*10) % 2 == 0;
				return new Battle( new PlayerCharacter(), new EnemyCharacter(assetManager.get(werewolf ? "wereslut.png" : "harpy.jpg", Texture.class), werewolf), battleCode.victoryScene, battleCode.defeatScene);
		}
	}

}

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
			default: return new Battle(battleCode.victoryScene, battleCode.defeatScene, assetManager.get("wereslut.png", Texture.class));
		}
	}

}

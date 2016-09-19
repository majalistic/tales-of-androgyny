package com.majalis.battle;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.majalis.character.EnemyCharacter;
import com.majalis.character.PlayerCharacter;
import com.majalis.save.LoadService;
import com.majalis.save.SaveEnum;
import com.majalis.save.SaveManager;
import com.majalis.save.SaveService;
/*
 * Controls the construction of a battle either from a saved state or net new.
 */
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
		// need a new Enemy
		if (enemy == null || enemy.getCurrentHealth() <= 0){
			enemy = getEnemy(battleCode.battleCode);
		}
		// loading old enemy
		else {
			enemy.init(getTexture(enemy.enemyType));
		}
		switch(battleCode.battleCode){	
			default: 
				return new Battle( saveService, assetManager, font, playerCharacter, getEnemy(battleCode.battleCode), battleCode.victoryScene, battleCode.defeatScene);
		}
	}
	
	private Texture getTexture(EnemyEnum type){
		switch(type){
			case WERESLUT:
				return assetManager.get("wereslut.png", Texture.class);
			case HARPY:
				return assetManager.get("harpy.jpg", Texture.class);
			default:
				return assetManager.get("wereslut.png", Texture.class);
		}
	}
	
	private EnemyCharacter getEnemy(int battleCode){
		switch(battleCode){
			case 0: return new EnemyCharacter(getTexture(EnemyEnum.WERESLUT), EnemyEnum.WERESLUT);
			case 1: return new EnemyCharacter(getTexture(EnemyEnum.HARPY), EnemyEnum.HARPY);
			case 2: return new EnemyCharacter(getTexture(EnemyEnum.SLIME), EnemyEnum.SLIME);
			case 3: return new EnemyCharacter(getTexture(EnemyEnum.BRIGAND), EnemyEnum.BRIGAND);
			default: return null;
		}
	}

	public enum EnemyEnum {
		WERESLUT ("Wereslut"){ public Vector2 getPosition(){ return new Vector2(600, 400); }},
		HARPY ("Harpy"){ public Vector2 getPosition(){ return new Vector2(150, -40); }},
		SLIME ("Slime"){ public Vector2 getPosition(){ return new Vector2(150, -40); }},
		BRIGAND ("Brigand"){ public Vector2 getPosition(){ return new Vector2(150, -40); }};
		
		private final String text;
	    private EnemyEnum(final String text) { this.text = text; }
	    @Override
	    public String toString() { return text; }		
		public abstract Vector2 getPosition();
	}
}

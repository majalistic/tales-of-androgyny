package com.majalis.battle;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.majalis.asset.AssetEnum;
import com.majalis.character.EnemyCharacter;
import com.majalis.character.PlayerCharacter;
import com.majalis.encounter.Background;
import com.majalis.character.AbstractCharacter.Stance;
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
	public BattleFactory(SaveManager saveManager, AssetManager assetManager, FreeTypeFontGenerator fontGenerator){
		this.saveService = saveManager;
		this.loadService = saveManager;
		this.assetManager = assetManager;
		FreeTypeFontParameter fontParameter = new FreeTypeFontParameter();
	    fontParameter.size = 18;
	    font = fontGenerator.generateFont(fontParameter);
	}
	
	public Battle getBattle(BattleCode battleCode, PlayerCharacter playerCharacter) {
		EnemyCharacter enemy = loadService.loadDataValue(SaveEnum.ENEMY, EnemyCharacter.class);
		// need a new Enemy
		if (enemy == null || enemy.getCurrentHealth() <= 0){
			enemy = getEnemy(battleCode.battleCode);
			enemy.setStance(battleCode.enemyStance);
			if (enemy.getStance() == Stance.DOGGY || enemy.getStance() == Stance.FELLATIO){
				enemy.setLust(10);
			}
			playerCharacter.setStance(battleCode.playerStance);			
		}
		// loading old enemy
		else {
			enemy.init(assetManager.get(enemy.getImagePath(), Texture.class));
		}
		switch(battleCode.battleCode){	
			default: 
				return new Battle(saveService, assetManager, font, playerCharacter, enemy, battleCode.victoryScene, battleCode.defeatScene, new Background(assetManager.get(AssetEnum.BATTLE_BG.getPath(), Texture.class)), new Background(assetManager.get(enemy.getBGPath(), Texture.class)));
		}
	}
	
	private Texture getTexture(EnemyEnum type){
		switch(type){
			case WERESLUT:
				return assetManager.get(AssetEnum.WEREBITCH.getPath(), Texture.class);
			case HARPY:
				return assetManager.get(AssetEnum.HARPY.getPath(), Texture.class);
			case SLIME:
				return assetManager.get(AssetEnum.SLIME.getPath(), Texture.class);
			case BRIGAND:
				return assetManager.get(AssetEnum.BRIGAND.getPath(), Texture.class);
			default:
				return assetManager.get("WerebitchChibi.png", Texture.class);
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
		WERESLUT ("Wereslut"),
		HARPY ("Harpy"),
		SLIME ("Slime"),
		BRIGAND ("Brigand");
		
		private final String text;
	    private EnemyEnum(final String text) { this.text = text; }
	    @Override
	    public String toString() { return text; }		
	}
}

package com.majalis.battle;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.majalis.asset.AssetEnum;
import com.majalis.character.EnemyCharacter;
import com.majalis.character.GrappleStatus;
import com.majalis.character.PlayerCharacter;
import com.majalis.encounter.Background.BackgroundBuilder;
import com.majalis.character.Stance;
import com.majalis.character.Arousal.ArousalLevel;
import com.majalis.save.LoadService;
import com.majalis.save.MutationResult;
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
	public BattleFactory(SaveManager saveManager, AssetManager assetManager) {
		this.saveService = saveManager;
		this.loadService = saveManager;
		this.assetManager = assetManager;
	}
	
	@SuppressWarnings("unchecked")
	public Battle getBattle(BattleAttributes battleAttributes, PlayerCharacter playerCharacter) {
		EnemyCharacter enemy = loadService.loadDataValue(SaveEnum.ENEMY, EnemyCharacter.class);
		// need a new Enemy
		if (enemy == null) {
			enemy = battleAttributes.getBattleCode().getEnemy(assetManager, battleAttributes.getEnemyStance());
			if (enemy.getStance().isEroticPenetration() ) {
				enemy.setArousal(ArousalLevel.ERECT);
				enemy.setGrappleStatus(GrappleStatus.ADVANTAGE);
				playerCharacter.setGrappleStatus(GrappleStatus.DISADVANTAGE);
			}
			if (battleAttributes.getDisarm()) {
				enemy.disarm();
			}
			if (battleAttributes.getClimaxCounter() > 0) {
				enemy.setArousal(ArousalLevel.ERECT);
				enemy.setClimaxCounter(battleAttributes.getClimaxCounter());
			}
			playerCharacter.setStance(battleAttributes.getPlayerStance());	
		}
		// loading old enemy
		else {
			ObjectMap<Stance, Array<Texture>> textures = new ObjectMap<Stance,  Array<Texture>>();
			for (ObjectMap.Entry<String, Array<String>> entry : enemy.getTextureImagePaths().entries()) {
				Array<Texture> textureList = new Array<Texture>();
				for (String s: entry.value) {
					textureList.add((Texture)assetManager.get(s));
				}
				textures.put(Stance.valueOf(entry.key), textureList);
			}
			enemy.init(enemy.getTextures(assetManager), textures, enemy.getAnimations(assetManager));
		}
		Array<String> console = (Array<String>) loadService.loadDataValue(SaveEnum.CONSOLE, Array.class);
		return new Battle(
			saveService, assetManager, playerCharacter, enemy, battleAttributes.getOutcomes(), 
			new BackgroundBuilder((Texture)assetManager.get(enemy.getBGPath())).build(), new BackgroundBuilder(assetManager.get(AssetEnum.BATTLE_UI.getTexture())).build(),
			console.size > 0 ? console.get(0) : "", console.size > 1 ? console.get(1) : "", (Array<MutationResult>) loadService.loadDataValue(SaveEnum.BATTLE_RESULT, Array.class), battleAttributes.getMusic()
		);
	}
}

package com.majalis.battle;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.majalis.asset.AssetEnum;
import com.majalis.character.EnemyCharacter;
import com.majalis.character.EnemyEnum;
import com.majalis.character.PlayerCharacter;
import com.majalis.encounter.Background.BackgroundBuilder;
import com.majalis.character.Stance;
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
	public BattleFactory(SaveManager saveManager, AssetManager assetManager) {
		this.saveService = saveManager;
		this.loadService = saveManager;
		this.assetManager = assetManager;
	}
	
	public Battle getBattle(BattleAttributes battleAttributes, PlayerCharacter playerCharacter) {
		EnemyCharacter enemy = loadService.loadDataValue(SaveEnum.ENEMY, EnemyCharacter.class);
		// need a new Enemy
		if (enemy == null) {
			enemy = getEnemy(battleAttributes.getBattleCode(), battleAttributes.getEnemyStance());
			if (enemy.getStance().isEroticPenetration() ) {
				enemy.setLust(10);
			}
			if (battleAttributes.getDisarm()) {
				enemy.disarm();
			}
			if (battleAttributes.getClimaxCounter() > 0) {
				enemy.setLust(10);
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
		@SuppressWarnings("unchecked")
		Array<String> console = (Array<String>) loadService.loadDataValue(SaveEnum.CONSOLE, Array.class);
		return new Battle(
			saveService, assetManager, playerCharacter, enemy, battleAttributes.getOutcomes(), 
			new BackgroundBuilder((Texture)assetManager.get(enemy.getBGPath())).build(), new BackgroundBuilder(assetManager.get(AssetEnum.BATTLE_UI.getTexture())).build(),
			console.size > 0 ? console.get(0) : "", console.size > 1 ? console.get(1) : "", battleAttributes.getMusic()
		);
	}
	
	private Array<Texture> getTextures(EnemyEnum type) {
		return type.getTextures(assetManager);
	}
	
	private ObjectMap<Stance, Array<Texture>> getTextureMap(EnemyEnum type) {
		ObjectMap<Stance, Array<Texture>> textures = new ObjectMap<Stance, Array<Texture>>();
		
		if(type == EnemyEnum.HARPY) {
			textures.put(Stance.FELLATIO, new Array<Texture>(new Texture[]{assetManager.get(AssetEnum.HARPY_FELLATIO_0.getTexture()), assetManager.get(AssetEnum.HARPY_FELLATIO_1.getTexture()), assetManager.get(AssetEnum.HARPY_FELLATIO_2.getTexture()), assetManager.get(AssetEnum.HARPY_FELLATIO_3.getTexture())}));
		}
		else if (type == EnemyEnum.SLIME) {
			textures.put(Stance.DOGGY, new Array<Texture>(new Texture[]{assetManager.get(AssetEnum.SLIME_DOGGY.getTexture())}));
		}
		else if (type == EnemyEnum.BRIGAND) {
			textures.put(Stance.FELLATIO, new Array<Texture>(new Texture[]{assetManager.get(AssetEnum.BRIGAND_ORAL.getTexture())}));
			textures.put(Stance.FACEFUCK, new Array<Texture>(new Texture[]{assetManager.get(AssetEnum.BRIGAND_ORAL.getTexture())}));
			textures.put(Stance.ANAL, new Array<Texture>(new Texture[]{assetManager.get(AssetEnum.BRIGAND_MISSIONARY.getTexture())}));
		}
		else if (type == EnemyEnum.CENTAUR) {
			textures.put(Stance.DOGGY, new Array<Texture>(new Texture[]{assetManager.get(AssetEnum.CENTAUR_ANAL.getTexture()), assetManager.get(AssetEnum.CENTAUR_ANAL_XRAY.getTexture())}));
		}
		else if (type == EnemyEnum.ORC) {
			textures.put(Stance.PRONE_BONE, new Array<Texture>(new Texture[]{assetManager.get(AssetEnum.ORC_PRONE_BONE.getTexture())}));
		}
		else if (type == EnemyEnum.GOBLIN) {
			Texture anal = assetManager.get(AssetEnum.GOBLIN_ANAL.getTexture());
			Texture faceSit = assetManager.get(AssetEnum.GOBLIN_FACE_SIT.getTexture());
			textures.put(Stance.PRONE_BONE, new Array<Texture>(new Texture[]{anal}));
			textures.put(Stance.DOGGY, new Array<Texture>(new Texture[]{anal}));
			textures.put(Stance.FACE_SITTING, new Array<Texture>(new Texture[]{faceSit}));
			textures.put(Stance.SIXTY_NINE, new Array<Texture>(new Texture[]{faceSit}));
		}
		else if (type == EnemyEnum.GOBLIN_MALE) {
			Texture anal = assetManager.get(AssetEnum.GOBLIN_ANAL_MALE.getTexture());
			Texture faceSit = assetManager.get(AssetEnum.GOBLIN_FACE_SIT_MALE.getTexture());
			textures.put(Stance.PRONE_BONE, new Array<Texture>(new Texture[]{anal}));
			textures.put(Stance.DOGGY, new Array<Texture>(new Texture[]{anal}));
			textures.put(Stance.FACE_SITTING, new Array<Texture>(new Texture[]{faceSit}));
			textures.put(Stance.SIXTY_NINE, new Array<Texture>(new Texture[]{faceSit}));
		}
		
		return textures;
	}
	
	protected EnemyCharacter getEnemy(BattleCode battleCode, Stance stance) {
		switch(battleCode) {
			case WERESLUT: return new EnemyCharacter(getTextures(EnemyEnum.WERESLUT), getTextureMap(EnemyEnum.WERESLUT), EnemyEnum.WERESLUT.getAnimations(assetManager), EnemyEnum.WERESLUT, stance);
			case HARPY: 
			case HARPY_STORY: return new EnemyCharacter(null, getTextureMap(EnemyEnum.HARPY), EnemyEnum.HARPY.getAnimations(assetManager), EnemyEnum.HARPY, stance);
			case SLIME: return new EnemyCharacter(getTextures(EnemyEnum.SLIME), getTextureMap(EnemyEnum.SLIME), EnemyEnum.SLIME.getAnimations(assetManager), EnemyEnum.SLIME, stance);
			case BRIGAND: return new EnemyCharacter(null, getTextureMap(EnemyEnum.BRIGAND), EnemyEnum.BRIGAND.getAnimations(assetManager), EnemyEnum.BRIGAND, stance);
			case CENTAUR: return new EnemyCharacter(null, getTextureMap(EnemyEnum.CENTAUR), EnemyEnum.CENTAUR.getAnimations(assetManager), EnemyEnum.CENTAUR, stance);
			case UNICORN: return new EnemyCharacter(null, getTextureMap(EnemyEnum.UNICORN), EnemyEnum.UNICORN.getAnimations(assetManager), EnemyEnum.UNICORN, stance);
			case GOBLIN: 
			case GOBLIN_STORY: return new EnemyCharacter(getTextures(EnemyEnum.GOBLIN), getTextureMap(EnemyEnum.GOBLIN), EnemyEnum.GOBLIN.getAnimations(assetManager), EnemyEnum.GOBLIN, stance);
			case GOBLIN_MALE: return new EnemyCharacter(getTextures(EnemyEnum.GOBLIN_MALE), getTextureMap(EnemyEnum.GOBLIN_MALE), EnemyEnum.GOBLIN_MALE.getAnimations(assetManager), EnemyEnum.GOBLIN_MALE, stance);
			case ORC: return new EnemyCharacter(getTextures(EnemyEnum.ORC), getTextureMap(EnemyEnum.ORC), EnemyEnum.ORC.getAnimations(assetManager), EnemyEnum.ORC, stance);
			case ADVENTURER: return new EnemyCharacter(getTextures(EnemyEnum.ADVENTURER), getTextureMap(EnemyEnum.ADVENTURER), EnemyEnum.ADVENTURER.getAnimations(assetManager), EnemyEnum.ADVENTURER, stance);
			case OGRE: return new EnemyCharacter(getTextures(EnemyEnum.OGRE), getTextureMap(EnemyEnum.OGRE), EnemyEnum.OGRE.getAnimations(assetManager), EnemyEnum.OGRE, stance);
			case BEASTMISTRESS: return new EnemyCharacter(getTextures(EnemyEnum.BEASTMISTRESS), getTextureMap(EnemyEnum.BEASTMISTRESS), EnemyEnum.BEASTMISTRESS.getAnimations(assetManager), EnemyEnum.BEASTMISTRESS, stance);
			case SPIDER: return new EnemyCharacter(getTextures(EnemyEnum.SPIDER), getTextureMap(EnemyEnum.SPIDER), EnemyEnum.SPIDER.getAnimations(assetManager), EnemyEnum.SPIDER, stance);
			case GOLEM: return new EnemyCharacter(getTextures(EnemyEnum.GOLEM), getTextureMap(EnemyEnum.GOLEM), EnemyEnum.GOLEM.getAnimations(assetManager), EnemyEnum.GOLEM, stance);
			case GHOST: return new EnemyCharacter(getTextures(EnemyEnum.GHOST), getTextureMap(EnemyEnum.GHOST), EnemyEnum.GHOST.getAnimations(assetManager), EnemyEnum.GHOST, stance);
			case BUNNY: return new EnemyCharacter(getTextures(EnemyEnum.BUNNY), getTextureMap(EnemyEnum.BUNNY), EnemyEnum.BUNNY.getAnimations(assetManager), EnemyEnum.BUNNY, stance);
			default: return null;
		}
	}
}

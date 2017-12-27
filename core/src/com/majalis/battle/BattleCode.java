package com.majalis.battle;

import static com.majalis.asset.AssetEnum.*;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.utils.Array;
import com.majalis.asset.AssetEnum;
import com.majalis.character.EnemyCharacter;
import com.majalis.character.EnemyEnum;
import com.majalis.character.Stance;

public enum BattleCode {
	// this should only require the type and battle background/music - all other requirements should come generically from the type
	WERESLUT (EnemyEnum.WERESLUT, WEREBITCH, FOREST_BG, MONSTER_DONG_0, MONSTER_DONG_1, MONSTER_DONG_2), 
	HARPY (EnemyEnum.HARPY, HARPY_FELLATIO_0, HARPY_FELLATIO_1, HARPY_FELLATIO_2, HARPY_FELLATIO_3, HARPY_ANAL, FOREST_BG, MONSTER_DONG_0, MONSTER_DONG_1, MONSTER_DONG_2), 
	HARPY_STORY (EnemyEnum.HARPY, HARPY_FELLATIO_0, HARPY_FELLATIO_1, HARPY_FELLATIO_2, HARPY_FELLATIO_3, HARPY_ANAL, FOREST_BG, MONSTER_DONG_0, MONSTER_DONG_1, MONSTER_DONG_2), 
	SLIME (EnemyEnum.SLIME, AssetEnum.SLIME, SLIME_DOGGY, FOREST_BG, MONSTER_DONG_0, MONSTER_DONG_1, MONSTER_DONG_2), 
	BRIGAND (EnemyEnum.BRIGAND, BRIGAND_ORAL, BRIGAND_MISSIONARY, FOREST_BG, LARGE_DONG_0, LARGE_DONG_1, LARGE_DONG_2),
	BRIGAND_STORY (EnemyEnum.BRIGAND, BRIGAND_ORAL, BRIGAND_MISSIONARY, FOREST_BG, LARGE_DONG_0, LARGE_DONG_1, LARGE_DONG_2), 
	CENTAUR (EnemyEnum.CENTAUR, CENTAUR_ORAL, CENTAUR_ANAL, CENTAUR_ANAL_XRAY, PLAINS_BG, MONSTER_DONG_0, MONSTER_DONG_1, MONSTER_DONG_2), 
	UNICORN (EnemyEnum.UNICORN, PLAINS_BG, UNICORN_ANAL, UNICORN_ANAL_XRAY, MONSTER_DONG_0, MONSTER_DONG_1, MONSTER_DONG_2), 
	GOBLIN (AssetEnum.CARNIVAL_MUSIC, EnemyEnum.GOBLIN, AssetEnum.GOBLIN, GOBLIN_ANAL, GOBLIN_FACE_SIT, ENCHANTED_FOREST_BG, MONSTER_DONG_0, MONSTER_DONG_1, MONSTER_DONG_2), 
	GOBLIN_STORY (AssetEnum.CARNIVAL_MUSIC, EnemyEnum.GOBLIN, AssetEnum.GOBLIN, GOBLIN_ANAL, GOBLIN_FACE_SIT, ENCHANTED_FOREST_BG, MONSTER_DONG_0, MONSTER_DONG_1, MONSTER_DONG_2), 
	GOBLIN_MALE (AssetEnum.CARNIVAL_MUSIC, EnemyEnum.GOBLIN_MALE, AssetEnum.GOBLIN_MALE, GOBLIN_ANAL_MALE, GOBLIN_FACE_SIT_MALE, ENCHANTED_FOREST_BG, MONSTER_DONG_0, MONSTER_DONG_1, MONSTER_DONG_2), 
	ORC (AssetEnum.BOSS_MUSIC, EnemyEnum.ORC, AssetEnum.ORC, ORC_PRONE_BONE, FOREST_BG, MONSTER_DONG_0, MONSTER_DONG_1, MONSTER_DONG_2), 
	ADVENTURER (AssetEnum.BOSS_MUSIC, EnemyEnum.ADVENTURER, AssetEnum.ADVENTURER, AssetEnum.ADVENTURER_ANAL, FOREST_BG, SMALL_DONG_0, SMALL_DONG_1, SMALL_DONG_2), 
	OGRE (AssetEnum.HEAVY_MUSIC, EnemyEnum.OGRE, AssetEnum.OGRE, FOREST_UP_BG, MONSTER_DONG_0, MONSTER_DONG_1, MONSTER_DONG_2), 
	OGRE_STORY (AssetEnum.HEAVY_MUSIC, EnemyEnum.OGRE, AssetEnum.OGRE, FOREST_UP_BG, MONSTER_DONG_0, MONSTER_DONG_1, MONSTER_DONG_2), 	
	BEASTMISTRESS (AssetEnum.BOSS_MUSIC, EnemyEnum.BEASTMISTRESS, AssetEnum.BEASTMISTRESS, FOREST_BG, LARGE_DONG_0, LARGE_DONG_1, LARGE_DONG_2), 
	SPIDER (AssetEnum.HEAVY_MUSIC, EnemyEnum.SPIDER, AssetEnum.SPIDER, CAVE_BG, MONSTER_DONG_0, MONSTER_DONG_1, MONSTER_DONG_2),
	GOLEM (AssetEnum.ETHEREAL_MUSIC, EnemyEnum.GOLEM, AssetEnum.GOLEM, AssetEnum.GOLEM_FUTA, FOREST_BG, MONSTER_DONG_0, MONSTER_DONG_1, MONSTER_DONG_2), 
	GHOST (AssetEnum.HORROR_MUSIC, EnemyEnum.GHOST, AssetEnum.GHOST_SPOOKY, AssetEnum.GHOST_SPOOKY_BLOODLESS, FOREST_BG, LARGE_DONG_0, LARGE_DONG_1, LARGE_DONG_2), 
	BUNNY(AssetEnum.BOSS_MUSIC, EnemyEnum.BUNNY, BUNNY_CREAM, BUNNY_VANILLA, BUNNY_CARAMEL, BUNNY_CHOCOLATE, BUNNY_DARK_CHOCOLATE, FOREST_BG, LARGE_DONG_0, LARGE_DONG_1, LARGE_DONG_2), 
	ANGEL(AssetEnum.ANGEL_MUSIC, EnemyEnum.ANGEL, AssetEnum.ANGEL, CELESTIAL_BG, NULL),
	NAGA(AssetEnum.HEAVY_MUSIC, EnemyEnum.NAGA, AssetEnum.NAGA, CAVE_BG, MONSTER_DONG_0, MONSTER_DONG_1, MONSTER_DONG_2), 
	QUETZAL(AssetEnum.HEAVY_MUSIC, EnemyEnum.QUETZAL, AssetEnum.QUETZAL, CELESTIAL_BG, MONSTER_DONG_0, MONSTER_DONG_1, MONSTER_DONG_2)
	;
	
	private final AssetEnum music;
	private final Array<AssetDescriptor<?>> requirements;
	private final EnemyEnum enemy;
	private BattleCode(EnemyEnum enemy, AssetEnum ... textures) {
		this(AssetEnum.BATTLE_MUSIC, enemy, textures);
	}
	
	private BattleCode(AssetEnum music,  EnemyEnum enemy, AssetEnum ... textures) {
		this.enemy = enemy;
		this.music = music;
		requirements = new Array<AssetDescriptor<?>>();
		requirements.add(music.getMusic());
		for (AssetEnum texture: textures) {
			requirements.add(texture.getTexture());
		}
	}

	public AssetEnum getMusic() { return music; }

	public Array<AssetDescriptor<?>> getRequirements() {
		Array<AssetDescriptor<?>> copiedRequirements = new Array<AssetDescriptor<?>>(requirements);
		copiedRequirements.addAll(enemy.getAnimationRequirements());
		return copiedRequirements;
	}

	private boolean isStoryMode() {
		return this == GOBLIN_STORY || this == HARPY_STORY || this == BRIGAND_STORY || this == OGRE_STORY;
	}
	
	public EnemyCharacter getEnemy(AssetManager assetManager, Stance stance) {
		return enemy.getInstance(assetManager, stance, isStoryMode());
	}
}
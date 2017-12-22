package com.majalis.battle;

import static com.majalis.asset.AssetEnum.*;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
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
	UNICORN (EnemyEnum.UNICORN, PLAINS_BG, MONSTER_DONG_0, MONSTER_DONG_1, MONSTER_DONG_2), 
	GOBLIN (EnemyEnum.GOBLIN, AssetEnum.CARNIVAL_MUSIC.getMusic(), AssetEnum.GOBLIN, GOBLIN_ANAL, GOBLIN_FACE_SIT, ENCHANTED_FOREST_BG, MONSTER_DONG_0, MONSTER_DONG_1, MONSTER_DONG_2), 
	GOBLIN_STORY (EnemyEnum.GOBLIN, AssetEnum.CARNIVAL_MUSIC.getMusic(), AssetEnum.GOBLIN, GOBLIN_ANAL, GOBLIN_FACE_SIT, ENCHANTED_FOREST_BG, MONSTER_DONG_0, MONSTER_DONG_1, MONSTER_DONG_2), 
	GOBLIN_MALE (EnemyEnum.GOBLIN_MALE, AssetEnum.CARNIVAL_MUSIC.getMusic(), AssetEnum.GOBLIN_MALE, GOBLIN_ANAL_MALE, GOBLIN_FACE_SIT_MALE, ENCHANTED_FOREST_BG, MONSTER_DONG_0, MONSTER_DONG_1, MONSTER_DONG_2), 
	ORC (EnemyEnum.ORC, AssetEnum.BOSS_MUSIC.getMusic(), AssetEnum.ORC, ORC_PRONE_BONE, FOREST_BG, MONSTER_DONG_0, MONSTER_DONG_1, MONSTER_DONG_2), 
	ADVENTURER (EnemyEnum.ADVENTURER, AssetEnum.BOSS_MUSIC.getMusic(), AssetEnum.ADVENTURER, AssetEnum.ADVENTURER_ANAL, FOREST_BG, SMALL_DONG_0, SMALL_DONG_1, SMALL_DONG_2), 
	OGRE (EnemyEnum.OGRE, AssetEnum.HEAVY_MUSIC.getMusic(), AssetEnum.OGRE, FOREST_UP_BG, MONSTER_DONG_0, MONSTER_DONG_1, MONSTER_DONG_2), 
	OGRE_STORY (EnemyEnum.OGRE, AssetEnum.HEAVY_MUSIC.getMusic(), AssetEnum.OGRE, FOREST_UP_BG, MONSTER_DONG_0, MONSTER_DONG_1, MONSTER_DONG_2), 	
	BEASTMISTRESS (EnemyEnum.BEASTMISTRESS, AssetEnum.BOSS_MUSIC.getMusic(), AssetEnum.BEASTMISTRESS, FOREST_BG, LARGE_DONG_0, LARGE_DONG_1, LARGE_DONG_2), 
	SPIDER (EnemyEnum.SPIDER, AssetEnum.HEAVY_MUSIC.getMusic(), AssetEnum.SPIDER, CAVE_BG, MONSTER_DONG_0, MONSTER_DONG_1, MONSTER_DONG_2),
	GOLEM (EnemyEnum.GOLEM, AssetEnum.ETHEREAL_MUSIC.getMusic(), AssetEnum.GOLEM, AssetEnum.GOLEM_FUTA, FOREST_BG, MONSTER_DONG_0, MONSTER_DONG_1, MONSTER_DONG_2), 
	GHOST (EnemyEnum.GHOST, AssetEnum.HORROR_MUSIC.getMusic(), AssetEnum.GHOST_SPOOKY, AssetEnum.GHOST_SPOOKY_BLOODLESS, FOREST_BG, LARGE_DONG_0, LARGE_DONG_1, LARGE_DONG_2), 
	BUNNY(EnemyEnum.BUNNY, AssetEnum.BOSS_MUSIC.getMusic(), BUNNY_CREAM, BUNNY_VANILLA, BUNNY_CARAMEL, BUNNY_CHOCOLATE, BUNNY_DARK_CHOCOLATE, FOREST_BG, LARGE_DONG_0, LARGE_DONG_1, LARGE_DONG_2), 
	ANGEL(EnemyEnum.ANGEL, AssetEnum.ANGEL_MUSIC.getMusic(), AssetEnum.ANGEL, CELESTIAL_BG, NULL),
	NAGA(EnemyEnum.NAGA, AssetEnum.HEAVY_MUSIC.getMusic(), AssetEnum.NAGA, CAVE_BG, MONSTER_DONG_0, MONSTER_DONG_1, MONSTER_DONG_2), 
	QUETZAL(EnemyEnum.QUETZAL, AssetEnum.HEAVY_MUSIC.getMusic(), AssetEnum.QUETZAL, CELESTIAL_BG, MONSTER_DONG_0, MONSTER_DONG_1, MONSTER_DONG_2)
	;
	
	private final AssetDescriptor<Music> music;
	private final Array<AssetDescriptor<?>> requirements;
	private final EnemyEnum enemy;
	private BattleCode(EnemyEnum enemy, AssetEnum ... textures) {
		this(enemy, AssetEnum.BATTLE_MUSIC.getMusic(), textures);
	}
	
	private BattleCode(EnemyEnum enemy, AssetDescriptor<Music> music, AssetEnum ... textures) {
		this.enemy = enemy;
		this.music = music;
		requirements = new Array<AssetDescriptor<?>>();
		requirements.add(music);
		for (AssetEnum texture: textures) {
			requirements.add(texture.getTexture());
		}
	}

	public AssetDescriptor<Music> getMusic() {
		return music;
	}

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
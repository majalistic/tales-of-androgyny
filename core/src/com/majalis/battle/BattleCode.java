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
	WERESLUT (EnemyEnum.WERESLUT, WEREBITCH_ANAL, WEREBITCH_KNOT, FOREST_BG), 
	HARPY (EnemyEnum.HARPY, HARPY_FELLATIO_0, HARPY_FELLATIO_1, HARPY_FELLATIO_2, HARPY_FELLATIO_3, HARPY_ANAL, FOREST_BG), 
	HARPY_STORY (EnemyEnum.HARPY, HARPY_FELLATIO_0, HARPY_FELLATIO_1, HARPY_FELLATIO_2, HARPY_FELLATIO_3, HARPY_ANAL, FOREST_BG), 
	SLIME (EnemyEnum.SLIME, AssetEnum.SLIME, SLIME_DOGGY, FOREST_BG), 
	BRIGAND (EnemyEnum.BRIGAND, BRIGAND_ORAL, BRIGAND_MISSIONARY, FOREST_BG),
	BRIGAND_STORY (EnemyEnum.BRIGAND, BRIGAND_ORAL, BRIGAND_MISSIONARY, FOREST_BG), 
	CENTAUR (EnemyEnum.CENTAUR, CENTAUR_ORAL, CENTAUR_ANAL, CENTAUR_ANAL_XRAY, PLAINS_BG), 
	UNICORN (EnemyEnum.UNICORN, PLAINS_BG, UNICORN_ANAL, UNICORN_ANAL_XRAY), 
	GOBLIN (AssetEnum.CARNIVAL_MUSIC, EnemyEnum.GOBLIN, GOBLIN_ANAL, GOBLIN_FACE_SIT, ENCHANTED_FOREST_BG), 
	GOBLIN_STORY (AssetEnum.CARNIVAL_MUSIC, EnemyEnum.GOBLIN, GOBLIN_ANAL, GOBLIN_FACE_SIT, ENCHANTED_FOREST_BG), 
	GOBLIN_MALE (AssetEnum.CARNIVAL_MUSIC, EnemyEnum.GOBLIN_MALE, GOBLIN_ANAL_MALE, GOBLIN_FACE_SIT_MALE, ENCHANTED_FOREST_BG), 
	ORC (AssetEnum.BOSS_MUSIC, EnemyEnum.ORC, FOREST_BG), 
	ORC_STORY (AssetEnum.BOSS_MUSIC, EnemyEnum.ORC, FOREST_BG), 
	ADVENTURER (AssetEnum.BOSS_MUSIC, EnemyEnum.ADVENTURER, AssetEnum.ADVENTURER_ANAL, FOREST_BG), 
	OGRE (AssetEnum.HEAVY_MUSIC, EnemyEnum.OGRE, AssetEnum.OGRE, FOREST_UP_BG), 
	OGRE_STORY (AssetEnum.HEAVY_MUSIC, EnemyEnum.OGRE, AssetEnum.OGRE, FOREST_UP_BG), 	
	BEASTMISTRESS (AssetEnum.BOSS_MUSIC, EnemyEnum.BEASTMISTRESS, FOREST_BG), 
	SPIDER (AssetEnum.HEAVY_MUSIC, EnemyEnum.SPIDER, AssetEnum.SPIDER, CAVE_BG),
	GOLEM (AssetEnum.ETHEREAL_MUSIC, EnemyEnum.GOLEM, AssetEnum.GOLEM, AssetEnum.GOLEM_FUTA, FOREST_BG), 
	GHOST (AssetEnum.HORROR_MUSIC, EnemyEnum.GHOST, AssetEnum.GHOST_SPOOKY, AssetEnum.GHOST_SPOOKY_BLOODLESS, FOREST_BG), 
	BUNNY(AssetEnum.BOSS_MUSIC, EnemyEnum.BUNNY, BUNNY_CREAM, BUNNY_VANILLA, BUNNY_CARAMEL, BUNNY_CHOCOLATE, BUNNY_DARK_CHOCOLATE, FOREST_BG), 
	ANGEL(AssetEnum.ANGEL_MUSIC, EnemyEnum.ANGEL, AssetEnum.ANGEL, CELESTIAL_BG, NULL),
	NAGA(AssetEnum.HEAVY_MUSIC, EnemyEnum.NAGA, AssetEnum.NAGA, CAVE_BG, NAGA_ANAL, NAGA_ANAL_CUM), 
	QUETZAL(AssetEnum.BOSS_MUSIC, EnemyEnum.QUETZAL, AssetEnum.QUETZAL, QUETZAL_BG),
	MERMAID(AssetEnum.HEAVY_MUSIC, EnemyEnum.MERMAID, AssetEnum.MERMAID, WATERFALL_BG),
	WARLOCK(AssetEnum.HORROR_MUSIC, EnemyEnum.WARLOCK, AssetEnum.WARLOCK, AssetEnum.WARLOCK_BG),
	GIANTESS(AssetEnum.ANGEL_MUSIC, EnemyEnum.GIANTESS, AssetEnum.GIANTESS_FUTA)
	;
	
	private final AssetEnum music;
	private final Array<AssetDescriptor<?>> requirements;
	private final EnemyEnum enemy;
	private BattleCode(EnemyEnum enemy, AssetEnum ... textures) {
		this(AssetEnum.BATTLE_MUSIC, enemy, textures);
	}
	
	private BattleCode(AssetEnum music, EnemyEnum enemy, AssetEnum ... textures) {
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
		return this == GOBLIN_STORY || this == HARPY_STORY || this == BRIGAND_STORY || this == OGRE_STORY || this == ORC_STORY;
	}
	
	public EnemyCharacter getEnemy(AssetManager assetManager, Stance stance) {
		return enemy.getInstance(assetManager, stance, isStoryMode());
	}
}
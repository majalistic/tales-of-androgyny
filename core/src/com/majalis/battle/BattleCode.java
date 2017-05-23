package com.majalis.battle;

import static com.majalis.asset.AssetEnum.*;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.utils.Array;
import com.majalis.asset.AssetEnum;

public enum BattleCode {
	WERESLUT (WEREBITCH, FOREST_BG, MONSTER_DONG_0, MONSTER_DONG_1, MONSTER_DONG_2), 
	HARPY (HARPY_FELLATIO_0, HARPY_FELLATIO_1, HARPY_FELLATIO_2, HARPY_FELLATIO_3, FOREST_BG, MONSTER_DONG_0, MONSTER_DONG_1, MONSTER_DONG_2), 
	HARPY_STORY (HARPY_FELLATIO_0, HARPY_FELLATIO_1, HARPY_FELLATIO_2, HARPY_FELLATIO_3, FOREST_BG, MONSTER_DONG_0, MONSTER_DONG_1, MONSTER_DONG_2), 
	SLIME (AssetEnum.SLIME, SLIME_DOGGY, FOREST_BG, MONSTER_DONG_0, MONSTER_DONG_1, MONSTER_DONG_2), 
	BRIGAND (AssetEnum.BRIGAND, BRIGAND_ORAL, FOREST_BG, LARGE_DONG_0, LARGE_DONG_1, LARGE_DONG_2),
	CENTAUR (PLAINS_BG, MONSTER_DONG_0, MONSTER_DONG_1, MONSTER_DONG_2), 
	UNICORN (PLAINS_BG, MONSTER_DONG_0, MONSTER_DONG_1, MONSTER_DONG_2), 
	GOBLIN (AssetEnum.GOBLIN, GOBLIN_FACE_SIT, ENCHANTED_FOREST_BG, MONSTER_DONG_0, MONSTER_DONG_1, MONSTER_DONG_2), 
	GOBLIN_STORY (AssetEnum.GOBLIN, GOBLIN_FACE_SIT, ENCHANTED_FOREST_BG, MONSTER_DONG_0, MONSTER_DONG_1, MONSTER_DONG_2), 
	GOBLIN_MALE (AssetEnum.GOBLIN_MALE, ENCHANTED_FOREST_BG, MONSTER_DONG_0, MONSTER_DONG_1, MONSTER_DONG_2), 
	ORC (AssetEnum.BOSS_MUSIC.getMusic(), AssetEnum.ORC, ORC_PRONE_BONE, FOREST_BG, MONSTER_DONG_0, MONSTER_DONG_1, MONSTER_DONG_2), 
	ADVENTURER (AssetEnum.BOSS_MUSIC.getMusic(), AssetEnum.ADVENTURER, FOREST_BG, SMALL_DONG_0, SMALL_DONG_1, SMALL_DONG_2), 
	OGRE (AssetEnum.HEAVY_MUSIC.getMusic(), AssetEnum.OGRE, FOREST_UP_BG, MONSTER_DONG_0, MONSTER_DONG_1, MONSTER_DONG_2), 
	BEASTMISTRESS (AssetEnum.BOSS_MUSIC.getMusic(), AssetEnum.BEASTMISTRESS, FOREST_BG, LARGE_DONG_0, LARGE_DONG_1, LARGE_DONG_2)
	;
	private final AssetDescriptor<Music> music;
	private final Array<AssetDescriptor<?>> requirements;
	private BattleCode(AssetEnum ... textures) {
		this(AssetEnum.BATTLE_MUSIC.getMusic(), textures);
	}
	
	private BattleCode(AssetDescriptor<Music> music, AssetEnum ... textures) {
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
		return requirements;
	}
}
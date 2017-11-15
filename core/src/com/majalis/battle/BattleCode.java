package com.majalis.battle;

import static com.majalis.asset.AssetEnum.*;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.utils.Array;
import com.majalis.asset.AnimatedActorFactory;
import com.majalis.asset.AssetEnum;

public enum BattleCode {
	WERESLUT (WEREBITCH, FOREST_BG, MONSTER_DONG_0, MONSTER_DONG_1, MONSTER_DONG_2), 
	HARPY (HARPY_FELLATIO_0, HARPY_FELLATIO_1, HARPY_FELLATIO_2, HARPY_FELLATIO_3, FOREST_BG, MONSTER_DONG_0, MONSTER_DONG_1, MONSTER_DONG_2), 
	HARPY_STORY (HARPY_FELLATIO_0, HARPY_FELLATIO_1, HARPY_FELLATIO_2, HARPY_FELLATIO_3, FOREST_BG, MONSTER_DONG_0, MONSTER_DONG_1, MONSTER_DONG_2), 
	SLIME (AssetEnum.SLIME, SLIME_DOGGY, FOREST_BG, MONSTER_DONG_0, MONSTER_DONG_1, MONSTER_DONG_2), 
	BRIGAND (BRIGAND_ORAL, BRIGAND_MISSIONARY, FOREST_BG, LARGE_DONG_0, LARGE_DONG_1, LARGE_DONG_2),
	CENTAUR (CENTAUR_ORAL, CENTAUR_ANAL, CENTAUR_ANAL_XRAY, PLAINS_BG, MONSTER_DONG_0, MONSTER_DONG_1, MONSTER_DONG_2), 
	UNICORN (PLAINS_BG, MONSTER_DONG_0, MONSTER_DONG_1, MONSTER_DONG_2), 
	GOBLIN (AssetEnum.CARNIVAL_MUSIC.getMusic(), AssetEnum.GOBLIN, GOBLIN_ANAL, GOBLIN_FACE_SIT, ENCHANTED_FOREST_BG, MONSTER_DONG_0, MONSTER_DONG_1, MONSTER_DONG_2), 
	GOBLIN_STORY (AssetEnum.CARNIVAL_MUSIC.getMusic(), AssetEnum.GOBLIN, GOBLIN_ANAL, GOBLIN_FACE_SIT, ENCHANTED_FOREST_BG, MONSTER_DONG_0, MONSTER_DONG_1, MONSTER_DONG_2), 
	GOBLIN_MALE (AssetEnum.CARNIVAL_MUSIC.getMusic(), AssetEnum.GOBLIN_MALE, GOBLIN_ANAL_MALE, GOBLIN_FACE_SIT_MALE, ENCHANTED_FOREST_BG, MONSTER_DONG_0, MONSTER_DONG_1, MONSTER_DONG_2), 
	ORC (AssetEnum.BOSS_MUSIC.getMusic(), AssetEnum.ORC, ORC_PRONE_BONE, FOREST_BG, MONSTER_DONG_0, MONSTER_DONG_1, MONSTER_DONG_2), 
	ADVENTURER (AssetEnum.BOSS_MUSIC.getMusic(), AssetEnum.ADVENTURER, AssetEnum.ADVENTURER_ANAL, FOREST_BG, SMALL_DONG_0, SMALL_DONG_1, SMALL_DONG_2), 
	OGRE (AssetEnum.HEAVY_MUSIC.getMusic(), AssetEnum.OGRE, FOREST_UP_BG, MONSTER_DONG_0, MONSTER_DONG_1, MONSTER_DONG_2), 
	BEASTMISTRESS (AssetEnum.BOSS_MUSIC.getMusic(), AssetEnum.BEASTMISTRESS, FOREST_BG, LARGE_DONG_0, LARGE_DONG_1, LARGE_DONG_2), 
	SPIDER (AssetEnum.HEAVY_MUSIC.getMusic(), AssetEnum.SPIDER, FOREST_BG, MONSTER_DONG_0, MONSTER_DONG_1, MONSTER_DONG_2),
	GOLEM (AssetEnum.ETHEREAL_MUSIC.getMusic(), AssetEnum.GOLEM, AssetEnum.GOLEM_FUTA, FOREST_BG, MONSTER_DONG_0, MONSTER_DONG_1, MONSTER_DONG_2), 
	GHOST (AssetEnum.HORROR_MUSIC.getMusic(), AssetEnum.GHOST_SPOOKY, AssetEnum.GHOST_SPOOKY_BLOODLESS, FOREST_BG, LARGE_DONG_0, LARGE_DONG_1, LARGE_DONG_2), 
	BUNNY(AssetEnum.BOSS_MUSIC.getMusic(), BUNNY_CREAM, BUNNY_VANILLA, BUNNY_CARAMEL, BUNNY_CHOCOLATE, BUNNY_DARK_CHOCOLATE, FOREST_BG, LARGE_DONG_0, LARGE_DONG_1, LARGE_DONG_2), 
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
	
	private Array<AssetDescriptor<AnimatedActorFactory>> getAnimationRequirements() {
		Array<AssetDescriptor<AnimatedActorFactory>> temp = new Array<AssetDescriptor<AnimatedActorFactory>>();
		switch (this) {
			case ADVENTURER:
				break;
			case BEASTMISTRESS:
				break;
			case BRIGAND:
				temp.add(AssetEnum.BRIGAND_ANIMATION.getAnimation());
				temp.add(AssetEnum.ANAL_ANIMATION.getAnimation());
				break;
			case CENTAUR:
				temp.add(AssetEnum.CENTAUR_ANIMATION.getAnimation());
				break;
			case GOBLIN:
				break;
			case GOBLIN_MALE:
				break;
			case GOBLIN_STORY:
				break;
			case HARPY:
				temp.add(AssetEnum.HARPY_ANIMATION.getAnimation());
				temp.add(AssetEnum.HARPY_ATTACK_ANIMATION.getAnimation());
				temp.add(AssetEnum.FEATHERS_ANIMATION.getAnimation());
				temp.add(AssetEnum.FEATHERS2_ANIMATION.getAnimation());
				break;
			case HARPY_STORY:
				break;
			case OGRE:
				break;
			case ORC:
				temp.add(AssetEnum.ORC_ANIMATION.getAnimation());
				break;
			case SLIME:
				break;
			case SPIDER:
				break;
			case UNICORN:
				temp.add(AssetEnum.CENTAUR_ANIMATION.getAnimation());
				break;
			case WERESLUT:
				break;
			default:
				break;
			
		}
		return temp;
	}

	public AssetDescriptor<Music> getMusic() {
		return music;
	}

	public Array<AssetDescriptor<?>> getRequirements() {
		Array<AssetDescriptor<?>> copiedRequirements = new Array<AssetDescriptor<?>>(requirements);
		copiedRequirements.addAll(getAnimationRequirements());
		return copiedRequirements;
	}
}
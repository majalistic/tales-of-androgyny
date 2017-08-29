package com.majalis.asset;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

@SuppressWarnings("unchecked")
public enum AssetEnum {
	UI_SKIN("uiskin.json", Skin.class),
	BATTLE_SKIN("battle/battleui.json", Skin.class),
	
	NULL("Null.png"),
	SPLASH_SCREEN("Splash.png"),
	LOADING("Loading.png"),

	MAIN_MENU_FG("title/foreground1.png"),
	MAIN_MENU_DK("title/DemonKing.png"),
	MAIN_MENU_MC("title/Mc.png"),
	MAIN_MENU_MG1("title/middleground1.png"),
	MAIN_MENU_MG2("title/middleground2.png"),
	MAIN_MENU_MG3("title/middleground3.png"),
	MAIN_MENU_MG4("title/middleground4.png"),
	MAIN_MENU_BG1("title/background1.png"),
	MAIN_MENU_BG2("title/background2.png"),
	MAIN_MENU_STATIONARY("title/stationary1.png"),
	TOA("title/ToA.png"),
	ALPHA("title/AlphaBuild.png"),
	
	DEFAULT_BACKGROUND("backgrounds/DefaultBackground.jpg"),
	GAME_TYPE_BACKGROUND("backgrounds/GameTypeSelect.jpg"),
	CLASS_SELECT_BACKGROUND("backgrounds/ClassSelect.jpg"),
	SKILL_SELECTION_BACKGROUND("backgrounds/SkillSelect.png"),
	CHARACTER_CUSTOM_BACKGROUND("backgrounds/CharCustom.png"),
	DRYAD_BACKGROUND("DryadApple.jpg"),
	STICK_BACKGROUND("backgrounds/StickEncounter.jpg"),
	CABIN_BACKGROUND("backgrounds/WoodsCottage.jpg"),
	BURNING_FORT_BG("backgrounds/BurningFort.jpg"),
	TOWN_BG("backgrounds/TownBG.jpg"),
	
	WORLD_MAP_UI("worldmap/CharacterInfo.png"),
	WORLD_MAP_HOVER("worldmap/HoverBox.png"),
	WORLD_MAP_BG("worldmap/MapBackground.jpg"),
	ARROW("worldmap/Arrow.png"),
	MOUNTAIN_ACTIVE("worldmap/MountainNode0.png"),
	FOREST_ACTIVE("worldmap/ForestNode1.png"),
	FOREST_INACTIVE("worldmap/ForestNode0.png"),
	ENCHANTED_FOREST("worldmap/ForestNode0.png"),
	GRASS0("worldmap/BaseGrass0.png"),
	GRASS1("worldmap/BaseGrass1.png"),
	GRASS2("worldmap/BaseGrass2.png"),
	CLOUD("worldmap/Cloud.png"),
	ROAD("worldmap/Road.png"),
	CASTLE("worldmap/Castle.png"),
	COTTAGE("worldmap/CottageNode0.png"),
	TOWN("worldmap/TownNode0.png"),
	CHARACTER_ANIMATION("worldmap/TinySprite.png"),
	
	APPLE("icons/Apple.png"),
	MEAT("icons/Meat.png"),
	HEART("icons/Heart.png"),
	GOLD("icons/Gold.png"),
	TIME("icons/Time.png"),
	EXP("icons/Exp.png"),
	
	CHARACTER_SCREEN("backgrounds/CharacterScreen.jpg"),
	
	WEREBITCH("enemies/WerebitchBasic.png"),
	HARPY_FELLATIO_0("enemies/HarpyBJ1.png"),
	HARPY_FELLATIO_1("enemies/HarpyBJ2.png"),
	HARPY_FELLATIO_2("enemies/HarpyBJ3.png"),
	HARPY_FELLATIO_3("enemies/HarpyBJ4.png"),
	BRIGAND_ORAL("enemies/BrigandOral.jpg"),
	BRIGAND_MISSIONARY("enemies/BrigandMissionary.jpg"),
	SLIME("enemies/HeartSlime.png"), 
	SLIME_DOGGY("enemies/HeartSlimeLoveDart.png"), 
	CENTAUR_ANAL("enemies/CentaurAnal.jpg"), 
	UNICORN_ANAL("enemies/UnicornAnal.jpg"), 
	CENTAUR_ANAL_XRAY("enemies/CentaurAnalXRay.jpg"), 
	UNICORN_ANAL_XRAY("enemies/UnicornAnalXRay.jpg"), 
	GOBLIN("enemies/GoblinFuta.png"),
	GOBLIN_MALE("enemies/GoblinMale.png"),
	GOBLIN_FACE_SIT("enemies/GoblinFaceSit.png"),
	GOBLIN_FACE_SIT_MALE("enemies/GoblinFaceSit.png"),
	GOBLIN_ANAL("enemies/GoblinAnal.jpg"),
	GOBLIN_ANAL_MALE("enemies/GoblinAnalMale.jpg"),
	ORC("enemies/OrcFuta.png"),
	ORC_ZOOM_UP("enemies/OrcZoom0.png"),
	ORC_ZOOM("enemies/OrcZoom1.png"),
	ORC_ZOOM_DOWN("enemies/OrcZoom2.png"),
	ORC_PRONE_BONE("enemies/OrcFutaMount.jpg"),
	OGRE("enemies/Ogre.png"),
	OGRE_BANGED("enemies/OgrePost.jpg"),
	SPIDER("enemies/Spider.png"),
	TINY_SPIDER("enemies/SpiderTinyHigh.png"),
	TINY_SPIDER_LOW("enemies/SpiderTinyLow.png"),
	ADVENTURER("enemies/Adventurer.png"), 
	BEASTMISTRESS("enemies/BeastMaster.png"),
	GOLEM("enemies/Golem.png"),
	GOLEM_DULL("enemies/GolemDull.png"),
	GOLEM_FUTA("enemies/GolemFuta.png"),
	GOLEM_CLOSEUP("enemies/GolemClose.png"),
	TRAP_BONUS("enemies/TrapBonus.jpg"), 
	
	ARMOR("battle/Armor.png"),
	BLEED("battle/Blood.png"),
	HEALTH_ICON_0("battle/Heart0.png"),
	STAMINA_ICON_0("battle/Stam0.png"),
	BALANCE_ICON_0("battle/Scale0.png"),
	MANA_ICON_0("battle/Eye0.png"),
	HEALTH_ICON_1("battle/Heart1.png"),
	STAMINA_ICON_1("battle/Stam1.png"),
	BALANCE_ICON_1("battle/Scale1.png"),
	MANA_ICON_1("battle/Eye1.png"),
	HEALTH_ICON_2("battle/Heart2.png"),
	STAMINA_ICON_2("battle/Stam2.png"),
	BALANCE_ICON_2("battle/Scale2.png"),
	MANA_ICON_2("battle/Eye2.png"),
	HEALTH_ICON_3("battle/Heart3.png"),
	STAMINA_ICON_3("battle/Stam3.png"),
	BALANCE_ICON_3("battle/Scale3.png"),
	MANA_ICON_3("battle/Eye3.png"),
	
	MARS_ICON_0("battle/MaleSymbol00.png"),
	MARS_ICON_1("battle/MaleSymbol01.png"),
	MARS_ICON_2("battle/MaleSymbol02.png"),
	MARS_ICON_3("battle/MaleSymbol03.png"),
	MARS_ICON_4("battle/MaleSymbol04.png"),
	
	SMALL_DONG_0("arousal/Small0.png"),
	SMALL_DONG_1("arousal/Small1.png"),
	SMALL_DONG_2("arousal/Small2.png"),
	
	LARGE_DONG_0("arousal/Human0.png"),
	LARGE_DONG_1("arousal/Human1.png"),
	LARGE_DONG_2("arousal/Human2.png"),
	
	MONSTER_DONG_0("arousal/Monster0.png"),
	MONSTER_DONG_1("arousal/Monster1.png"),
	MONSTER_DONG_2("arousal/Monster2.png"),
	
	STUFFED_BELLY("bellies/Stuffed.png"), 
	FULL_BELLY("bellies/Full.png"), 
	BIG_BELLY("bellies/Half.png"), 
	FLAT_BELLY("bellies/Empty.png"), 
	
	PORTRAIT_NEUTRAL("portraits/Neutral.png"),
	PORTRAIT_AHEGAO("portraits/Ahegao.png"),
	PORTRAIT_FELLATIO("portraits/BJ.png"),
	PORTRAIT_MOUTHBOMB("portraits/BJFin.png"),
	PORTRAIT_GRIN("portraits/DumbGrin.png"),
	PORTRAIT_LOVE("portraits/LoveCrazy.png"),
	PORTRAIT_LUST("portraits/Lusty.png"),
	PORTRAIT_SURPRISE("portraits/Surprise.png"),
	PORTRAIT_GRIMACE("portraits/NerveGrimace.png"),
	PORTRAIT_HIT("portraits/Hit.png"),
	PORTRAIT_POUT("portraits/Pout.png"),
	PORTRAIT_HAPPY("portraits/SlightHappy.png"),
	PORTRAIT_SMILE("portraits/Smile.png"),
	FOREST_BG("battle/ForestBG.jpg"),
	FOREST_UP_BG("battle/ForestUp.jpg"),
	PLAINS_BG("battle/AbandonedField.jpg"),
	ENCHANTED_FOREST_BG("battle/MushroomForest.jpg"),
	BATTLE_UI("battle/Treeframe.png"),
	BATTLE_HOVER("battle/SkillHover.png"),
	BATTLE_TEXTBOX("battle/TextBox.png"),
	NORMAL_BOX("BattleHover.png"),
	TEXT_BOX("battle/NameBox.png"),
	
	ELF("characters/Elf.png"),
	ELF_TURTLE("characters/Turtle.png"),
	ELF_AND_TURTLE("characters/ElfTurtle.png"),
	SHOPKEEP("characters/AppleKeep.png"),
	INNKEEPER("characters/Innkeeper.png"),
	TRAINER("characters/HeavyTrainer1.png"),
	MERI_SILHOUETTE("characters/Witch.png"),
	SILHOUETTE("characters/Silhouette.png"),
	GADGETEER("characters/Gadgeteer.png"),
	
	AIRBORNE("stances/Airborne.png"),
	ANAL("stances/Anal.png"),
	BALANCED("stances/Balanced.png"),
	BLITZ("stances/Blitz.png"),
	CASTING("stances/Casting.png"),
	COUNTER("stances/Counter.png"),
	COWGIRL("stances/Cowgirl.png"),
	DEFENSIVE("stances/Defensive.png"),
	DOGGY("stances/Doggy.png"),
	ERUPT("stances/Erupt.png"),
	FACE_SITTING("stances/Facesitting.png"),
	FELLATIO("stances/Fellatio.png"),
	FACEFUCK("stances/Facefuck.png"),
	GROUND_WRESTLE("stances/GroundWrestle.png"),
	HANDS_AND_KNEES("stances/HandsAndKnees.png"),
	OUROBOROS("stances/Ouroboros.png"),
	FULL_NELSON("stances/FullNelson.png"),
	HANDY("stances/Handy.png"),
	ITEM("stances/Item.png"),
	KNEELING("stances/Kneeling.png"),
	KNOTTED("stances/Knotted.png"),
	NULL_STANCE("stances/Null.png"),
	OFFENSIVE("stances/Offensive.png"),
	PRONE("stances/Prone.png"),
	PRONEBONE("stances/ProneBone.png"),
	REVERSE_COWGIRL("stances/ReverseCowgirl.png"),
	SIXTY_NINE("stances/SixyNine.png"),
	SPREAD("stances/SpreadEagle.png"),
	PENETRATED("stances/SpreadEaglePen.png"),
	STANDING("stances/Standing.png"),
	SUPINE("stances/Supine.png"),
	
	STANCE_ARROW("stances/ChevronArrow.png"),
	
	STRENGTH("stats/Strength.png"),
	ENDURANCE("stats/Endurance.png"),
	AGILITY("stats/Agility.png"),
	PERCEPTION("stats/Perception.png"),
	MAGIC("stats/Magic.png"),
	CHARISMA("stats/Charisma.png"), 
	
	SLASH("animation/ClawAttack.png"),
	
	GAPE("GapedButtBG.jpg"),
	GAME_OVER_GAPE("GameOverButt.jpg"),
	KEYHOLE("Keyhole.jpg"),
	GAME_OVER_KEYHOLE("GameOverKeyhole.jpg"),
	GAME_OGRE("enemies/OgrePost.jpg"),
	// sounds
	BUTTON_SOUND("sounds/sound.wav", Sound.class),
	CLICK_SOUND("sounds/node_sound.wav", Sound.class),
	INTRO_SOUND("sounds/IntroSound.wav", Sound.class),
	EQUIP("sounds/Equip.wav", Sound.class),
	LOUD_LAUGH("sounds/LoudLaugh.wav", Sound.class),
	ATTACK_SOUND("sounds/AttackSound.wav", Sound.class),
	HIT_SOUND("sounds/HitSound.wav", Sound.class),
	PARRY_SOUND("sounds/Parry.wav", Sound.class),
	BLOCK_SOUND("sounds/Block.wav", Sound.class),
	SWORD_SLASH_SOUND("sounds/SwordSlash.wav", Sound.class),
	FIREBALL_SOUND("sounds/Fireball.wav", Sound.class),
	INCANTATION("sounds/Incantation.wav", Sound.class),
	SMUG_LAUGH("sounds/FemaleSmugLaugh.wav", Sound.class),
	OGRE_GROWL("sounds/Ogre.wav", Sound.class),
	GOLEM_SHUTDOWN("sounds/GolemShutdown.wav", Sound.class),
	GOLEM_ONE("sounds/GolemOne.wav", Sound.class),

	THWAPPING("sounds/Thwapping.wav", Sound.class),
	UNPLUGGED_POP("sounds/UnpluggedPop.wav", Sound.class),
	MOUTH_POP("sounds/MouthPop.wav", Sound.class),
	CUM("sounds/Cum.wav", Sound.class),
	CUM_BUBBLING("sounds/CumBubbling.wav", Sound.class),
	// music
	MAIN_MENU_MUSIC("music/MainMenuMusic.mp3", Music.class),
	ENCOUNTER_MUSIC("music/EncounterMusic.mp3", Music.class),
	INITIAL_MUSIC("music/KingsofTara.mp3", Music.class),
	SHOP_MUSIC("music/ShopkeepMusic.mp3", Music.class),
	TRAINER_MUSIC("music/TrainerMusic.mp3", Music.class),
	HOVEL_MUSIC("music/HovelMusic.mp3", Music.class),
	WEREWOLF_MUSIC("music/WerewolfMusic.mp3", Music.class),
	CARNIVAL_MUSIC("music/CarnivalMusic.mp3", Music.class),
	ETHEREAL_MUSIC("music/EtherealMusic.mp3", Music.class),
	HEAVY_MUSIC("music/Mechanolith.mp3", Music.class),
	WORLD_MAP_MUSIC("music/WorldMapMusic.mp3", Music.class),
	BATTLE_MUSIC("music/BattleMusic.mp3", Music.class),
	GADGETEER_MUSIC("music/GadgeteerMusic.mp3", Music.class),
	BOSS_MUSIC("music/BossMusic.mp3", Music.class),
	WAVES("music/Waves.wav", Music.class), 
	
	GAME_OVER_ANIMATION("animation/SplurtGO.atlas", AnimatedActorFactory.class),
	HARPY_ANIMATION("animation/Harpy.atlas", AnimatedActorFactory.class, new AnimatedActorLoader.AnimatedActorParameter(.75f, 1, true)),
	HARPY_ATTACK_ANIMATION("animation/Attack Still.atlas", AnimatedActorFactory.class, new AnimatedActorLoader.AnimatedActorParameter(.75f, 1, true)),
	FEATHERS_ANIMATION("animation/Feathers.atlas", AnimatedActorFactory.class, new AnimatedActorLoader.AnimatedActorParameter(.75f, 1, true)),
	FEATHERS2_ANIMATION("animation/Feathers2.atlas", AnimatedActorFactory.class, new AnimatedActorLoader.AnimatedActorParameter(.75f, 1, true)),
	BRIGAND_ANIMATION("animation/Brigand.atlas", AnimatedActorFactory.class, new AnimatedActorLoader.AnimatedActorParameter(.60f, .75f, true)),
	ANAL_ANIMATION("animation/skeleton.atlas", AnimatedActorFactory.class, new AnimatedActorLoader.AnimatedActorParameter(.475f, 1, true)),
	CENTAUR_ANIMATION("animation/Centaur.atlas", AnimatedActorFactory.class, new AnimatedActorLoader.AnimatedActorParameter(.60f, 1.8f, true)), 
	;
	
	private final AssetDescriptor<?> assetDescriptor;
	
	private AssetEnum(String path) {
	    this(path, Texture.class);
	}
	
	@SuppressWarnings({ "rawtypes" })
	AssetEnum(String path, Class<?> assetType) {
	    this.assetDescriptor = new AssetDescriptor(path, assetType);
	}
	
	@SuppressWarnings({ "rawtypes" })
	AssetEnum(String path, Class<?> assetType, AssetLoaderParameters<?> params) {
	    this.assetDescriptor = new AssetDescriptor(path, assetType, params);
	}
	
	public AssetDescriptor<?> getAsset() {
		return assetDescriptor;
	}
		
	public AssetDescriptor<Skin> getSkin() {
		return (AssetDescriptor<Skin>) assetDescriptor;
	}
	
	public AssetDescriptor<Texture> getTexture() {
		return (AssetDescriptor<Texture>) assetDescriptor;
	}
	
	public AssetDescriptor<Music> getMusic() {
		return (AssetDescriptor<Music>) assetDescriptor;
	}
	
	public AssetDescriptor<Sound> getSound() {
		return (AssetDescriptor<Sound>) assetDescriptor;
	}
	
	public AssetDescriptor<AnimatedActorFactory> getAnimation() {
		return (AssetDescriptor<AnimatedActorFactory>) assetDescriptor;
	}
	
	public String getPath() {
		return assetDescriptor.fileName;
	}

	public boolean isTinted() {
		return this == TOWN_BG || this == PLAINS_BG;
	}
}

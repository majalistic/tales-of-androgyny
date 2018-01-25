package com.majalis.asset;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

@SuppressWarnings("unchecked")
public enum AssetEnum {
	UI_SKIN("ui/uiskin.json", Skin.class),
	BATTLE_SKIN("ui/battleui.json", Skin.class),
	
	NULL("Null.png"),
	SPLASH_SCREEN("Splash.png"),
	LOADING("Loading.png"),

	MAIN_MENU_FG("title/foreground1.png"),
	MAIN_MENU_DK("title/DemonKing.png"),
	MAIN_MENU_MC("title/Mc.png"),
	MAIN_MENU_MG1_LEFT("title/middleground1left.png"),
	MAIN_MENU_MG2_LEFT("title/middleground2left.png"),
	MAIN_MENU_MG3_LEFT("title/middleground3left.png"),
	MAIN_MENU_MG4_LEFT("title/middleground4left.png"),
	MAIN_MENU_MG1_RIGHT("title/middleground1right.png"),
	MAIN_MENU_MG2_RIGHT("title/middleground2right.png"),
	MAIN_MENU_MG3_RIGHT("title/middleground3right.png"),
	MAIN_MENU_MG4_RIGHT("title/middleground4right.png"),
	MAIN_MENU_BG1("title/background1.png"),
	MAIN_MENU_BG2_LEFT("title/background2left.png"),
	MAIN_MENU_BG2_RIGHT("title/background2right.png"),
	MAIN_MENU_STATIONARY("title/stationary1.png"),
	TOA("title/ToA.png"),
	ALPHA("title/AlphaBuild.png"),
	
	DEFAULT_BACKGROUND("battle/ForestBG.jpg"),
	GAME_TYPE_BACKGROUND("backgrounds/GameTypeSelect.jpg"),
	SKILL_SELECTION_BACKGROUND("backgrounds/SkillSelect.jpg"),
	CAMP_BG0("backgrounds/CampBG0.jpg"),
	CAMP_BG1("backgrounds/CampBG1.jpg"),
	CAMP_BG2("backgrounds/CampBG2.jpg"),
	
	CLASS_SELECT_BACKGROUND("backgrounds/ClassSelect/Bg.png"),
	CLASS_SELECT_CHARACTER_BOX("backgrounds/ClassSelect/CharacterBox.png"),
	CLASS_SELECT_LABEL("backgrounds/ClassSelect/ClassSelectLabel.png"),
	CLASS_SELECT_PANEL("backgrounds/ClassSelect/ClassSelectPanel.png"),
	CLASS_SELECT_FOREGROUND("backgrounds/ClassSelect/ForegroundGround.png"),
	CLASS_SELECT_LEAF_BORDER("backgrounds/ClassSelect/ForwardLeafBorder.png"),
	CLASS_SELECT_HANGING_LEAVES("backgrounds/ClassSelect/HangingLeaves.png"),
	CLASS_SELECT_STAT_PANEL_FOLDOUT("backgrounds/ClassSelect/StatPanelFoldout.png"),
	CLASS_SELECT_STAT_BOX("backgrounds/ClassSelect/StatsBox.png"),
	CLASS_SELECT_TOOLTIP_BOX("backgrounds/ClassSelect/StatsTooltipBox.png"),
	CLASS_SELECT_TOOLTIP_SLIDEOUT("backgrounds/ClassSelect/StatsTooltipSlideout.png"),
	CLASS_SELECT_SUBTLE_BORDER("backgrounds/ClassSelect/SubtleBorder3.png"),
	CLASS_SELECT_TOP_BORDER("backgrounds/ClassSelect/TopBorderBack.png"),
	
	CHARACTER_CUSTOM_BACKGROUND("backgrounds/CharCustom.png"),
	DRYAD_BACKGROUND("characters/DryadApple.jpg"),
	STICK_BACKGROUND("backgrounds/StickEncounter.jpg"),
	CABIN_BACKGROUND("backgrounds/WoodsCottage.jpg"),
	BURNING_FORT_BG("backgrounds/BurningFort.jpg"),
	TOWN_BG("backgrounds/TownBG.jpg"),

	SKILL_TITLE("backgrounds/SkillSelect/SkillsTitle.png"),
	MAGIC_TITLE("backgrounds/SkillSelect/MagicTitle.png"),
	PERK_TITLE("backgrounds/SkillSelect/PerkTitle.png"),
	SKILL_BOX_0("backgrounds/SkillSelect/Boxback.png"),
	SKILL_BOX_1("backgrounds/SkillSelect/Boxback1.png"),
	SKILL_BOX_2("backgrounds/SkillSelect/Boxback2.png"),
	SKILL_CONSOLE_BOX("backgrounds/SkillSelect/OverpanelBg.png"),
	
	WARRIOR("characters/protagonist/Warrior.png"),
	PALADIN("characters/protagonist/Paladin.png"),
	THIEF("characters/protagonist/Thief.png"),
	RANGER("characters/protagonist/Ranger.png"),
	MAGE("characters/protagonist/Mage.png"),
	ENCHANTRESS("characters/protagonist/Enchantress.png"),
	
	CREATION_BUTTON_UP("creation/ButtonUp.png"),
	CREATION_BUTTON_DOWN("creation/ButtonDown.png"),
	CREATION_BUTTON_CHECKED("creation/ButtonChecked.png"),
	CREATION_BAUBLE_EMPTY("creation/EmptyBauble.png"),
	CREATION_BAUBLE_NEW("creation/GoldBauble.png"),
	CREATION_BAUBLE_OLD("creation/GreenBauble.png"),
	CREATION_BAUBLE_REMOVED("creation/RemovedBauble.png"),
	
	BUTTON_UP("buttons/basic.png"),
	BUTTON_DOWN("buttons/basicd.png"),
	BUTTON_HIGHLIGHT("buttons/basich.png"),
	
	EMBELLISHED_BUTTON_UP("buttons/embellished.png"),
	EMBELLISHED_BUTTON_DOWN("buttons/embellishedd.png"),
	EMBELLISHED_BUTTON_HIGHLIGHT("buttons/embellishedh.png"),
	
	ORNATE_BUTTON_UP("buttons/ornate.png"),
	ORNATE_BUTTON_DOWN("buttons/ornated.png"),
	ORNATE_BUTTON_HIGHLIGHT("buttons/ornateh.png"),
	
	ARROW_BUTTON_UP("buttons/ScrollButtonL.png"),
	ARROW_BUTTON_DOWN("buttons/ScrollButtonLD.png"),
	ARROW_BUTTON_HIGHLIGHT("buttons/ScrollButtonLH.png"),
	
	PLUS("backgrounds/SkillSelect/Plus.png"),
	PLUS_DOWN("backgrounds/SkillSelect/PlusDown.png"),
	PLUS_HIGHLIGHT("backgrounds/SkillSelect/PlusHighlight.png"),
	MINUS("backgrounds/SkillSelect/Minus.png"),
	MINUS_DOWN("backgrounds/SkillSelect/MinusDown.png"),
	MINUS_HIGHLIGHT("backgrounds/SkillSelect/MinusHighlight.png"),
	EMPTY_BAUBLE("backgrounds/SkillSelect/EmptyBauble.png"),
	FILLED_BAUBLE("backgrounds/SkillSelect/FilledBauble.png"),
	ADDED_BAUBLE("backgrounds/SkillSelect/AddedBauble.png"),
	
	GROUND_SHEET("worldmap/GroundSheet.png"),
	DOODADS("worldmap/Doodads.png"),
	
	WORLD_MAP_UI("worldmap/CharacterInfo.png"),
	WORLD_MAP_HOVER("worldmap/HoverBox.png"),
	WORLD_MAP_BG("worldmap/MapBackground.jpg"),
	ARROW("worldmap/Arrow.png"),
	MOUNTAIN_ACTIVE("worldmap/MountainNode0.png"),
	FOREST_ACTIVE("worldmap/NodeAnimation.png"),
	FOREST_INACTIVE("worldmap/NodeAnimation.png"),
	ENCHANTED_FOREST("worldmap/NodeAnimation.png"),
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
	
	SEARCHING("icons/Searching.png"),
	
	CHARACTER_SCREEN("backgrounds/CharacterScreen.jpg"),
	
	WEREBITCH("enemies/Werebitch.png"),
	WEREBITCH_ANAL("enemies/WerebitchAnal.jpg"),
	WEREBITCH_KNOT("enemies/WerebitchKnot.jpg"),
	WEREBITCH_KNOT_CUM("enemies/WerebitchKnotCum.jpg"),
	HARPY_FELLATIO_0("enemies/HarpyBJ1.png"),
	HARPY_FELLATIO_1("enemies/HarpyBJ2.png"),
	HARPY_FELLATIO_2("enemies/HarpyBJ3.png"),
	HARPY_FELLATIO_3("enemies/HarpyBJ4.png"),
	HARPY_ANAL("enemies/HarpyAnal.jpg"),
	BRIGAND_ORAL("enemies/BrigandOral.jpg"),
	BRIGAND_MISSIONARY("enemies/BrigandMissionary.jpg"),
	SLIME("enemies/HeartSlime.png"), 
	SLIME_DOGGY("enemies/HeartSlimeLoveDart.png"), 
	CENTAUR_ORAL("enemies/CentaurOral.jpg"), 
	CENTAUR_ANAL("enemies/CentaurAnal.jpg"), 
	UNICORN_ANAL("enemies/UnicornAnal.jpg"), 
	CENTAUR_ANAL_XRAY("enemies/CentaurAnalXRay.jpg"), 
	UNICORN_ANAL_XRAY("enemies/UnicornAnalXRay.jpg"), 
	GOBLIN_FACE_SIT("enemies/GoblinFaceSit.png"),
	GOBLIN_FACE_SIT_MALE("enemies/GoblinFaceSitMale.png"),
	GOBLIN_ANAL("enemies/GoblinAnal.jpg"),
	GOBLIN_ANAL_MALE("enemies/GoblinAnalMale.jpg"),
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
	ADVENTURER_ANAL("enemies/AdventurerAnal.jpg"), 
	BEASTMISTRESS("enemies/BeastMaster.png"),
	GOLEM("enemies/Golem.png"),
	GOLEM_DULL("enemies/GolemDull.png"),
	GOLEM_FUTA("enemies/GolemFuta.png"),
	GOLEM_CLOSEUP("enemies/GolemClose.png"),
	GHOST("enemies/Ghost.png"),
	GHOST_SPOOKY("enemies/GhostSpooky.png"),
	GHOST_SPOOKY_BLOODLESS("enemies/GhostSpookyNoBlood.png"),
	BUNNY_CREAM("enemies/bunny/Cream.png"),
	BUNNY_VANILLA("enemies/bunny/Vanilla.png"),
	BUNNY_CARAMEL("enemies/bunny/Caramel.png"),
	BUNNY_CHOCOLATE("enemies/bunny/Chocolate.png"),
	BUNNY_DARK_CHOCOLATE("enemies/bunny/DarkChocolate.png"),
	BUNNY_CREAM_ANAL("enemies/bunny/CreamAnal.jpg"),
	BUNNY_VANILLA_ANAL("enemies/bunny/VanillaAnal.jpg"),
	BUNNY_CARAMEL_ANAL("enemies/bunny/CaramelAnal.jpg"),
	BUNNY_CHOCOLATE_ANAL("enemies/bunny/ChocolateAnal.jpg"),
	BUNNY_DARK_CHOCOLATE_ANAL("enemies/bunny/DarkChocolateAnal.jpg"),
	ANGEL("enemies/Angel.png"),
	NAGA("enemies/Naga.png"),
	QUETZAL("enemies/Quetzal.png"),
	QUETZAL_HERO("enemies/QuetzalHero.jpg"),
	QUETZAL_HERO_ANAL("enemies/QuetzalHeroAnal.jpg"),
	MOUTH_FIEND("enemies/MouthFiend.png"),
	MOUTH_FIEND_CLOTHED("enemies/MouthFiendClothed.png"),
	MOUTH_FIEND_CLOTHED_TRANSPARENT("enemies/MouthFiendClothedTransparent.png"),
	MOUTH_FIEND_ORAL("enemies/MouthFiendOral.jpg"),
	TRAP_BONUS("enemies/TrapBonus.jpg"), 

	ARMOR_DOLL("battle/ArmorDoll.png"),
	ARMOR_0("battle/Armor0.png"),
	ARMOR_1("battle/Armor1.png"),
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
	SMALL_DONG_CHASTITY("arousal/SmallChastity.png"),
	
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
	CAVE_BG("battle/Cave.jpg"),
	CELESTIAL_BG("battle/Celestial.jpg"),
	ENCHANTED_FOREST_BG("battle/MushroomForest.jpg"),
	BATTLE_UI("battle/Treeframe.png"),
	BATTLE_HOVER("battle/SkillHover.png"),
	BATTLE_TEXTBOX("battle/TextBox.png"),
	TEXT_BOX("battle/NameBox.png"),
	
	ELF("characters/Elf.png"),
	ELF_TURTLE("characters/Turtle.png"),
	ELF_AND_TURTLE("characters/ElfTurtle.png"),
	SHOPKEEP("characters/AppleKeep.png"),
	INNKEEPER("characters/Innkeeper.png"),
	TRAINER("characters/HeavyTrainer1.png"),
	BROTHEL_MADAME("characters/Madame.png"),
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
	WRAPPED("stances/Wrapped.png"),
	SEDUCTION("stances/Seduction.png"),	
	
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
	CLICK_SOUND("sounds/NodeSound.wav", Sound.class),
	GEM_CLINK("sounds/GemClink.wav", Sound.class),
	QUICK_PAGE_TURN("sounds/QuickPageTurn.wav", Sound.class),
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
	FEMALE_GROAN("sounds/FemaleGroan.wav", Sound.class),
	OGRE_GROWL("sounds/Ogre.wav", Sound.class),
	GOLEM_SHUTDOWN("sounds/GolemShutdown.wav", Sound.class),
	GOLEM_ONE("sounds/GolemOne.wav", Sound.class),
	WEREWOLF_GROWL("sounds/WerewolfGrowl.wav", Sound.class),
	FIEND_LAUGH("sounds/FiendLaugh.wav", Sound.class),
	FIEND_LONG_LAUGH("sounds/FiendLongLaugh.wav", Sound.class),
	FIEND_SIGH("sounds/FiendSigh.wav", Sound.class),
	FIEND_KISS("sounds/FiendKiss.wav", Sound.class),
	FIEND_AHH("sounds/FiendAhh.wav", Sound.class),
	HORSE_CLOP("sounds/HorseClop.wav", Sound.class),
	HORSE_NEIGH("sounds/HorseNeigh.wav", Sound.class),
	BIRD_SCREECH("sounds/BirdScreech.wav", Sound.class),	
	
	THWAPPING("sounds/Thwapping.wav", Sound.class),
	UNPLUGGED_POP("sounds/UnpluggedPop.wav", Sound.class),
	MOUTH_POP("sounds/MouthPop.wav", Sound.class),
	SWALLOW("sounds/Swallow.wav", Sound.class),
	BURP("sounds/Burp.wav", Sound.class),
	CUM("sounds/Cum.wav", Sound.class),
	CUM_BUBBLING("sounds/CumBubbling.wav", Sound.class),
	CUMFART("sounds/Cumfart.wav", Sound.class),
	// music
	MAIN_MENU_MUSIC("music/MainMenuMusic.mp3", Music.class),
	ENCOUNTER_MUSIC("music/EncounterMusic.mp3", Music.class),
	SHOP_MUSIC("music/ShopkeepMusic.mp3", Music.class),
	TRAINER_MUSIC("music/TrainerMusic.mp3", Music.class),
	HOVEL_MUSIC("music/HovelMusic.mp3", Music.class),
	WEREWOLF_MUSIC("music/WerewolfMusic.mp3", Music.class),
	CARNIVAL_MUSIC("music/CarnivalMusic.mp3", Music.class),
	ETHEREAL_MUSIC("music/EtherealMusic.mp3", Music.class),
	ANGEL_MUSIC("music/AngelMusic.mp3", Music.class),
	HEAVY_MUSIC("music/Mechanolith.mp3", Music.class),
	WORLD_MAP_MUSIC("music/WorldMapMusic.mp3", Music.class),
	BATTLE_MUSIC("music/BattleMusic.mp3", Music.class),
	GADGETEER_MUSIC("music/GadgeteerMusic.mp3", Music.class),
	SPOOKY_MUSIC("music/SpookyMusic.mp3", Music.class),
	HORROR_MUSIC("music/HorrorMusic.mp3", Music.class),
	BOSS_MUSIC("music/BossMusic.mp3", Music.class),
	GAME_OVER_MUSIC("music/GameOverMusic.mp3", Music.class),
	WAVES("music/Waves.wav", Music.class), 
	
	GAME_OVER_ANIMATION("animation/SplurtGO.atlas", AnimatedActorFactory.class),
	HARPY_ANIMATION("animation/Harpy.atlas", AnimatedActorFactory.class, new AnimatedActorLoader.AnimatedActorParameter(.75f, 1, true)),
	HARPY_ATTACK_ANIMATION("animation/Attack Still.atlas", AnimatedActorFactory.class, new AnimatedActorLoader.AnimatedActorParameter(.75f, 1, true)),
	FEATHERS_ANIMATION("animation/Feathers.atlas", AnimatedActorFactory.class, new AnimatedActorLoader.AnimatedActorParameter(.75f, 1, true)),
	FEATHERS2_ANIMATION("animation/Feathers2.atlas", AnimatedActorFactory.class, new AnimatedActorLoader.AnimatedActorParameter(.75f, 1, true)),
	BRIGAND_ANIMATION("animation/Brigand.atlas", AnimatedActorFactory.class, new AnimatedActorLoader.AnimatedActorParameter(.60f, .75f, true)),
	ANAL_ANIMATION("animation/skeleton.atlas", AnimatedActorFactory.class, new AnimatedActorLoader.AnimatedActorParameter(.475f, 1, true)),
	CENTAUR_ANIMATION("animation/Centaur.atlas", AnimatedActorFactory.class, new AnimatedActorLoader.AnimatedActorParameter(.60f, 1.8f, true)), 
	ORC_ANIMATION("animation/Orc.atlas", AnimatedActorFactory.class, new AnimatedActorLoader.AnimatedActorParameter(.60f, 1f, true)), 
	GOBLIN_ANIMATION("animation/Goblin.atlas", AnimatedActorFactory.class, new AnimatedActorLoader.AnimatedActorParameter(.40f, 1f, true)), 
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

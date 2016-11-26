package com.majalis.asset;

public enum AssetEnum {
	UI_SKIN("uiskin.json"),
	SPLASH_SCREEN("Splash.png"),
	
	MAIN_MENU_SCREEN("MainMenuScreen.jpg"),
	
	DEFAULT_BACKGROUND("DefaultBackground.jpg"),
	GAME_TYPE_BACKGROUND("GameTypeSelect.jpg"),
	CLASS_SELECT_BACKGROUND("ClassSelect.jpg"),
	DRYAD_BACKGROUND("DryadApple.jpg"),
	STICK_BACKGROUND("StickEncounter.jpg"),
	
	WORLD_MAP_UI("worldmap/CharacterInfo.png"),
	WORLD_MAP_HOVER("worldmap/HoverBox.png"),
	ARROW("worldmap/Arrow.png"),
	MOUNTAIN_ACTIVE("worldmap/MountainNode0.png"),
	FOREST_ACTIVE("worldmap/ForestNode1.png"),
	FOREST_INACTIVE("worldmap/ForestNode0.png"),
	GRASS0("worldmap/BaseGrass0.png"),
	GRASS1("worldmap/BaseGrass1.png"),
	GRASS2("worldmap/BaseGrass2.png"),
	CLOUD("worldmap/Cloud.png"),
	APPLE("worldmap/Apple.png"),
	MEAT("worldmap/Meat.png"),
	ROAD("worldmap/Road.png"),
	CASTLE("worldmap/Castle.png"),
	TOWN("worldmap/TownNode0.png"),
	CHARACTER_ANIMATION("worldmap/TinySprite.png"),
	
	CHARACTER_SCREEN("ClassSelect.jpg"),
	
	WEREBITCH("enemies/WerebitchBasic.png"),
	HARPY("enemies/Harpy.png"),
	HARPY_FELLATIO("enemies/HarpyFellatio.png"),
	BRIGAND("enemies/Brigand.png"),
	BRIGAND_ORAL("enemies/BrigandOral.jpg"),
	SLIME("enemies/HeartSlime.png"), 
	SLIME_DOGGY("enemies/HeartSlimeLoveDart.png"), 
	TRAP_BONUS("enemies/TrapBonus.jpg"), 
	
	HEALTH_ICON_0("battle/Heart0.png"),
	STAMINA_ICON_0("battle/Stam0.png"),
	BALANCE_ICON_0("battle/Scale0.png"),
	MANA_ICON_0("battle/Scale0.png"),
	HEALTH_ICON_1("battle/Heart1.png"),
	STAMINA_ICON_1("battle/Stam1.png"),
	BALANCE_ICON_1("battle/Scale1.png"),
	MANA_ICON_1("battle/Scale1.png"),
	HEALTH_ICON_2("battle/Heart2.png"),
	STAMINA_ICON_2("battle/Stam2.png"),
	BALANCE_ICON_2("battle/Scale2.png"),
	MANA_ICON_2("battle/Scale2.png"),
	
	BATTLE_SKIN("battle/battleui.json"),
	CHARACTER_POTRAIT("battle/Neutral.png"),
	FOREST_BG("battle/ForestBG.jpg"),
	PLAINS_BG("battle/AbandonedField.jpg"),
	BATTLE_UI("battle/Treeframe.png"),
	BATTLE_HOVER("battle/SkillHover.png"),
	BATTLE_TEXTBOX("battle/TextBox.png"),
	
	SHOPKEEP("characters/AppleKeep.png"),
	
	AIRBORNE("stances/Airborne.png"),
	ANAL("stances/Anal.png"),
	BALANCED("stances/Balanced.png"),
	BLITZ("stances/Blitz.png"),
	CASTING("stances/Casting.png"),
	COWGIRL("stances/Cowgirl.png"),
	DEFENSIVE("stances/Defensive.png"),
	DOGGY("stances/Doggy.png"),
	ERUPT("stances/Erupt.png"),
	FELLATIO("stances/Fellatio.png"),
	FULL_NELSON("stances/FullNelson.png"),
	HANDY("stances/Handy.png"),
	KNEELING("stances/Kneeling.png"),
	KNOTTED("stances/Knotted.png"),
	OFFENSIVE("stances/Offensive.png"),
	PRONE("stances/Prone.png"),
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
	
	GAME_OVER_GAPE("GameOverButt.jpg"),
	GAME_OVER_TUCKERED("GameOverTuckered.jpg"),
	
	TOWN_BG("town/TownBG.jpg"),
	
	// sounds
	BUTTON_SOUND("sound.wav"),
	CLICK_SOUND("node_sound.wav"),
	INTRO_SOUND("sounds/IntroSound.wav"),
	UNPLUGGED_POP("sounds/UnpluggedPop.wav"),
	ATTACK_SOUND("sounds/AttackSound.wav"),
	HIT_SOUND("sounds/HitSound.wav"),
	THWAPPING("sounds/Thwapping.wav"),
	SMUG_LAUGH("sounds/FemaleSmugLaugh.wav"),
	// music
	MAIN_MENU_MUSIC("music/MainMenuMusic.mp3"),
	ENCOUNTER_MUSIC("music/EncounterMusic.mp3"),
	WORLD_MAP_MUSIC("music/WorldMapMusic.mp3"),
	BATTLE_MUSIC("music/BattleMusic.mp3"),
	WAVES("music/Waves.wav")
	;
	
	private final String path;

	AssetEnum(String path) {
	    this.path = path;
	 }
	public String getPath(){return path;}
}

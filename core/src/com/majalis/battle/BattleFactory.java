package com.majalis.battle;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.majalis.asset.AssetEnum;
import com.majalis.character.EnemyCharacter;
import com.majalis.character.PlayerCharacter;
import com.majalis.encounter.Background.BackgroundBuilder;
import com.majalis.character.AbstractCharacter.Stance;
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
	private final BitmapFont font;
	public BattleFactory(SaveManager saveManager, AssetManager assetManager, FreeTypeFontGenerator fontGenerator) {
		this.saveService = saveManager;
		this.loadService = saveManager;
		this.assetManager = assetManager;
		FreeTypeFontParameter fontParameter = new FreeTypeFontParameter();
	    fontParameter.size = 24;
	    font = fontGenerator.generateFont(fontParameter);
	}
	
	public Battle getBattle(BattleCode battleCode, PlayerCharacter playerCharacter) {
		EnemyCharacter enemy = loadService.loadDataValue(SaveEnum.ENEMY, EnemyCharacter.class);
		// need a new Enemy
		if (enemy == null) {
			enemy = getEnemy(battleCode.battleCode);
			enemy.setStance(battleCode.enemyStance);
			if (enemy.getStance().isEroticPenetration() ) {
				enemy.setLust(10);
			}
			if (battleCode.disarm) {
				enemy.disarm();
			}
			if (battleCode.climaxCounter > 0) {
				enemy.setLust(10);
			}
			enemy.setClimaxCounter(battleCode.climaxCounter);
			playerCharacter.setStance(battleCode.playerStance);			
		}
		// loading old enemy
		else {
			ObjectMap<Stance, Texture> textures = new ObjectMap<Stance, Texture>();
			for (String key : enemy.getTextureImagePaths().keys()) {
				textures.put(Stance.valueOf(key), assetManager.get(enemy.getTextureImagePaths().get(key), Texture.class)) ;
			}
			enemy.init(enemy.getImagePath() == null ? null : assetManager.get(enemy.getImagePath(), Texture.class), textures);
		}
		@SuppressWarnings("unchecked")
		Array<String> console = (Array<String>) loadService.loadDataValue(SaveEnum.CONSOLE, Array.class);
		return new Battle(
			saveService, assetManager, font, playerCharacter, enemy, battleCode.outcomes, 
			new BackgroundBuilder(assetManager.get(enemy.getBGPath(), Texture.class)).build(), new BackgroundBuilder(assetManager.get(AssetEnum.BATTLE_UI.getPath(), Texture.class)).build(), console.size > 0 ? console.get(0) : "", console.size > 1 ? console.get(1) : "", battleCode.battleCode == 7 || battleCode.battleCode == 8 ? AssetEnum.BOSS_MUSIC.getPath() : battleCode.battleCode == 9 ? AssetEnum.HEAVY_MUSIC.getPath() : AssetEnum.BATTLE_MUSIC.getPath()
		);
	}
	
	private Texture getTexture(EnemyEnum type) {
		return assetManager.get(type.getPath(), Texture.class);
	}
	
	private ObjectMap<Stance, Texture> getTextures(EnemyEnum type) {
		ObjectMap<Stance, Texture> textures = new ObjectMap<Stance, Texture>();
		
		if (type == EnemyEnum.SLIME) {
			textures.put(Stance.DOGGY, assetManager.get(AssetEnum.SLIME_DOGGY.getPath(), Texture.class));
		}
		else if(type == EnemyEnum.HARPY) {
			textures.put(Stance.FELLATIO, assetManager.get(AssetEnum.HARPY_FELLATIO.getPath(), Texture.class));
		}
		else if (type == EnemyEnum.GOBLIN) {
			textures.put(Stance.FACE_SITTING, assetManager.get(AssetEnum.GOBLIN_FACE_SIT.getPath(), Texture.class));
			textures.put(Stance.SIXTY_NINE, assetManager.get(AssetEnum.GOBLIN_FACE_SIT.getPath(), Texture.class));
		}
		
		return textures;
	}
	
	private EnemyCharacter getEnemy(int battleCode) {
		switch(battleCode) {
			case 0: return new EnemyCharacter(getTexture(EnemyEnum.WERESLUT), getTextures(EnemyEnum.WERESLUT), EnemyEnum.WERESLUT);
			case 1: 
			case 2004: return new EnemyCharacter(null, getTextures(EnemyEnum.HARPY), EnemyEnum.HARPY);
			case 2: return new EnemyCharacter(getTexture(EnemyEnum.SLIME), getTextures(EnemyEnum.SLIME), EnemyEnum.SLIME);
			case 3: return new EnemyCharacter(getTexture(EnemyEnum.BRIGAND), getTextures(EnemyEnum.BRIGAND), EnemyEnum.BRIGAND);
			case 5: return new EnemyCharacter(null, getTextures(EnemyEnum.CENTAUR), EnemyEnum.CENTAUR);
			case 1005: return new EnemyCharacter(null, getTextures(EnemyEnum.UNICORN), EnemyEnum.UNICORN);
			case 6: return new EnemyCharacter(getTexture(EnemyEnum.GOBLIN), getTextures(EnemyEnum.GOBLIN), EnemyEnum.GOBLIN);
			case 1006: return new EnemyCharacter(getTexture(EnemyEnum.GOBLIN_MALE), getTextures(EnemyEnum.GOBLIN_MALE), EnemyEnum.GOBLIN_MALE);
			case 7: return new EnemyCharacter(getTexture(EnemyEnum.ORC), getTextures(EnemyEnum.ORC), EnemyEnum.ORC);
			case 8: return new EnemyCharacter(getTexture(EnemyEnum.ADVENTURER), getTextures(EnemyEnum.ADVENTURER), EnemyEnum.ADVENTURER);
			case 9: return new EnemyCharacter(getTexture(EnemyEnum.OGRE), getTextures(EnemyEnum.OGRE), EnemyEnum.OGRE);
			default: return null;
		}
	}

	public enum EnemyEnum {
		WERESLUT ("Wereslut", AssetEnum.WEREBITCH.getPath()),
		HARPY ("Harpy", null, "animation/Harpy"),
		SLIME ("Slime", AssetEnum.SLIME.getPath()),
		BRIGAND ("Brigand", AssetEnum.BRIGAND.getPath(), "animation/skeleton"),
		CENTAUR ("Centaur", null, "animation/Centaur"),
		UNICORN ("Unicorn", null, "animation/Centaur"),
		GOBLIN ("Goblin", AssetEnum.GOBLIN.getPath()), 
		ORC ("Orc", AssetEnum.ORC.getPath()), 
		ADVENTURER ("Adventurer", AssetEnum.ADVENTURER.getPath()),
		GOBLIN_MALE ("Goblin (Male)", AssetEnum.GOBLIN_MALE.getPath()),
		OGRE ("Ogre", AssetEnum.OGRE.getPath())
		;
		private final String text;
		private final String path;
		private final String animationPath;
	    private EnemyEnum(final String text, final String path) { this(text, path, ""); }
	    private EnemyEnum(final String text, final String path, final String animationPath) { this.text = text; this.path = path; this.animationPath = animationPath; }
	    @Override
	    public String toString() { return text; }	
	    public String getPath() { return path; }
	    public String getAnimationPath() { return animationPath; }
	}
}

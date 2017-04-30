package com.majalis.battle;

import com.badlogic.gdx.assets.AssetDescriptor;
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
	
	public Battle getBattle(BattleAttributes battleAttributes, PlayerCharacter playerCharacter) {
		EnemyCharacter enemy = loadService.loadDataValue(SaveEnum.ENEMY, EnemyCharacter.class);
		// need a new Enemy
		if (enemy == null) {
			enemy = getEnemy(battleAttributes.getBattleCode());
			enemy.setStance(battleAttributes.getEnemyStance());
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
					textureList.add(assetManager.get(s));
				}
				textures.put(Stance.valueOf(entry.key), textureList);
			}
			enemy.init(enemy.getImagePath() == null ? null : assetManager.get(enemy.getImagePath()), textures);
		}
		@SuppressWarnings("unchecked")
		Array<String> console = (Array<String>) loadService.loadDataValue(SaveEnum.CONSOLE, Array.class);
		return new Battle(
			saveService, assetManager, font, playerCharacter, enemy, battleAttributes.getOutcomes(), 
			new BackgroundBuilder(assetManager.get(enemy.getBGPath())).build(), new BackgroundBuilder(assetManager.get(AssetEnum.BATTLE_UI.getTexture())).build(),
			console.size > 0 ? console.get(0) : "", console.size > 1 ? console.get(1) : "", battleAttributes.getMusic()
		);
	}
	
	private Texture getTexture(EnemyEnum type) {
		return assetManager.get(type.getTexture());
	}
	
	private ObjectMap<Stance, Array<Texture>> getTextures(EnemyEnum type) {
		ObjectMap<Stance, Array<Texture>> textures = new ObjectMap<Stance, Array<Texture>>();
		
		if (type == EnemyEnum.SLIME) {
			textures.put(Stance.DOGGY, new Array<Texture>(new Texture[]{assetManager.get(AssetEnum.SLIME_DOGGY.getTexture())}));
		}
		else if(type == EnemyEnum.HARPY) {
			textures.put(Stance.FELLATIO, new Array<Texture>(new Texture[]{assetManager.get(AssetEnum.HARPY_FELLATIO_0.getTexture()), assetManager.get(AssetEnum.HARPY_FELLATIO_1.getTexture()), assetManager.get(AssetEnum.HARPY_FELLATIO_2.getTexture()), assetManager.get(AssetEnum.HARPY_FELLATIO_3.getTexture())}));
		}
		else if (type == EnemyEnum.GOBLIN) {
			textures.put(Stance.FACE_SITTING, new Array<Texture>(new Texture[]{assetManager.get(AssetEnum.GOBLIN_FACE_SIT.getTexture())}));
			textures.put(Stance.SIXTY_NINE, new Array<Texture>(new Texture[]{assetManager.get(AssetEnum.GOBLIN_FACE_SIT.getTexture())}));
		}
		
		return textures;
	}
	
	public enum EnemyEnum {
		WERESLUT ("Wereslut", AssetEnum.WEREBITCH.getTexture()),
		HARPY ("Harpy", null, "animation/Harpy"),
		SLIME ("Slime", AssetEnum.SLIME.getTexture()),
		BRIGAND ("Brigand", AssetEnum.BRIGAND.getTexture(), "animation/skeleton"),
		CENTAUR ("Centaur", null, "animation/Centaur"),
		UNICORN ("Unicorn", null, "animation/Centaur"),
		GOBLIN ("Goblin", AssetEnum.GOBLIN.getTexture()), 
		ORC ("Orc", AssetEnum.ORC.getTexture()), 
		ADVENTURER ("Adventurer", AssetEnum.ADVENTURER.getTexture()),
		GOBLIN_MALE ("Goblin (Male)", AssetEnum.GOBLIN_MALE.getTexture()),
		OGRE ("Ogre", AssetEnum.OGRE.getTexture())
		;
		private final String text;
		private final AssetDescriptor<Texture> path;
		private final String animationPath;
	    private EnemyEnum(final String text, final AssetDescriptor<Texture> path) { this(text, path, ""); }
	    private EnemyEnum(final String text, final AssetDescriptor<Texture> path, final String animationPath) { this.text = text; this.path = path; this.animationPath = animationPath; }
	    @Override
	    public String toString() { return text; }	
	    public AssetDescriptor<Texture> getTexture() { return path; }
	    public String getAnimationPath() { return animationPath; }
	}
	
	protected EnemyCharacter getEnemy(BattleCode battleCode) {
		switch(battleCode) {
			case WERESLUT: return new EnemyCharacter(getTexture(EnemyEnum.WERESLUT), getTextures(EnemyEnum.WERESLUT), EnemyEnum.WERESLUT);
			case HARPY: 
			case HARPY_STORY: return new EnemyCharacter(null, getTextures(EnemyEnum.HARPY), EnemyEnum.HARPY);
			case SLIME: return new EnemyCharacter(getTexture(EnemyEnum.SLIME), getTextures(EnemyEnum.SLIME), EnemyEnum.SLIME);
			case BRIGAND: return new EnemyCharacter(getTexture(EnemyEnum.BRIGAND), getTextures(EnemyEnum.BRIGAND), EnemyEnum.BRIGAND);
			case CENTAUR: return new EnemyCharacter(null, getTextures(EnemyEnum.CENTAUR), EnemyEnum.CENTAUR);
			case UNICORN: return new EnemyCharacter(null, getTextures(EnemyEnum.UNICORN), EnemyEnum.UNICORN);
			case GOBLIN: 
			case GOBLIN_STORY: return new EnemyCharacter(getTexture(EnemyEnum.GOBLIN), getTextures(EnemyEnum.GOBLIN), EnemyEnum.GOBLIN);
			case GOBLIN_MALE: return new EnemyCharacter(getTexture(EnemyEnum.GOBLIN_MALE), getTextures(EnemyEnum.GOBLIN_MALE), EnemyEnum.GOBLIN_MALE);
			case ORC: return new EnemyCharacter(getTexture(EnemyEnum.ORC), getTextures(EnemyEnum.ORC), EnemyEnum.ORC);
			case ADVENTURER: return new EnemyCharacter(getTexture(EnemyEnum.ADVENTURER), getTextures(EnemyEnum.ADVENTURER), EnemyEnum.ADVENTURER);
			case OGRE: return new EnemyCharacter(getTexture(EnemyEnum.OGRE), getTextures(EnemyEnum.OGRE), EnemyEnum.OGRE);
			default: return null;
		}
	}
}

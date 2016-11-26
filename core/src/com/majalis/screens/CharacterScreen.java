package com.majalis.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ObjectMap;
import com.majalis.asset.AssetEnum;
import com.majalis.character.PlayerCharacter;
import com.majalis.character.AbstractCharacter.Stat;
import com.majalis.encounter.Background.BackgroundBuilder;
import com.majalis.save.SaveEnum;
import com.majalis.save.SaveManager.GameContext;
import com.majalis.save.SaveService;
/*
 * The options/configuration screen.  UI that handles player input to save Preferences to a player's file system.
 */
public class CharacterScreen extends AbstractScreen {

	public static final ObjectMap<String, Class<?>> resourceRequirements = new ObjectMap<String, Class<?>>();
	static {
		resourceRequirements.put(AssetEnum.UI_SKIN.getPath(), Skin.class);
		resourceRequirements.put(AssetEnum.CLICK_SOUND.getPath(), Sound.class);
		resourceRequirements.put(AssetEnum.MOUNTAIN_ACTIVE.getPath(), Texture.class);
		resourceRequirements.put(AssetEnum.FOREST_ACTIVE.getPath(), Texture.class);
		resourceRequirements.put(AssetEnum.FOREST_INACTIVE.getPath(), Texture.class);
		resourceRequirements.put(AssetEnum.CASTLE.getPath(), Texture.class);
		resourceRequirements.put(AssetEnum.APPLE.getPath(), Texture.class);
		resourceRequirements.put(AssetEnum.MEAT.getPath(), Texture.class);
		resourceRequirements.put(AssetEnum.GRASS0.getPath(), Texture.class);
		resourceRequirements.put(AssetEnum.GRASS1.getPath(), Texture.class);
		resourceRequirements.put(AssetEnum.GRASS2.getPath(), Texture.class);
		resourceRequirements.put(AssetEnum.CLOUD.getPath(), Texture.class);
		resourceRequirements.put(AssetEnum.ROAD.getPath(), Texture.class);
		resourceRequirements.put(AssetEnum.WORLD_MAP_UI.getPath(), Texture.class);
		resourceRequirements.put(AssetEnum.WORLD_MAP_HOVER.getPath(), Texture.class);
		resourceRequirements.put(AssetEnum.ARROW.getPath(), Texture.class);
		resourceRequirements.put(AssetEnum.CHARACTER_SCREEN.getPath(), Texture.class);
		resourceRequirements.put(AssetEnum.STRENGTH.getPath(), Texture.class);
		resourceRequirements.put(AssetEnum.ENDURANCE.getPath(), Texture.class);
		resourceRequirements.put(AssetEnum.AGILITY.getPath(), Texture.class);
		resourceRequirements.put(AssetEnum.PERCEPTION.getPath(), Texture.class);
		resourceRequirements.put(AssetEnum.MAGIC.getPath(), Texture.class);
		resourceRequirements.put(AssetEnum.CHARISMA.getPath(), Texture.class);
		resourceRequirements.put(AssetEnum.WORLD_MAP_MUSIC.getPath(), Music.class);
	}
	
	private final PlayerCharacter character;
	private ObjectMap<Stat, Texture> statTextureMap;
	
	public CharacterScreen(ScreenFactory factory, ScreenElements elements, AssetManager assetManager, final SaveService saveService, final PlayerCharacter character) {
		super(factory, elements);
		this.character = character;
		this.addActor(new BackgroundBuilder(assetManager.get(AssetEnum.CHARACTER_SCREEN.getPath(), Texture.class)).build()); 
		
		statTextureMap = new ObjectMap<Stat, Texture>();
		for (final Stat stat: Stat.values()){
			statTextureMap.put(stat, assetManager.get(stat.getPath(), Texture.class));
		}
		
		Skin skin = assetManager.get(AssetEnum.UI_SKIN.getPath(), Skin.class);
		final Sound buttonSound = assetManager.get(AssetEnum.CLICK_SOUND.getPath(), Sound.class); 
		final TextButton done = new TextButton("Done", skin);
		
		done.setSize(180, 40);
		done.addListener(
			new ClickListener(){
				@Override
		        public void clicked(InputEvent event, float x, float y) {
					buttonSound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
					saveService.saveDataValue(SaveEnum.CONTEXT, GameContext.WORLD_MAP);
					showScreen(ScreenEnum.LOAD_GAME);		   
		        }
			}
		);
		done.setPosition(1523, 30);
		this.addActor(done);
		
		if (character.needsLevelUp()){
			final boolean levelup = character.getStoredLevels() > 0;
			final TextButton levelUp = new TextButton(levelup ? "Level Up!" : "Learn Skills", skin);
			
			levelUp.setSize(270, 40); 
			TextButtonStyle style = new TextButtonStyle(levelUp.getStyle());
			style.fontColor = levelup ? Color.OLIVE : Color.GOLDENROD;
			levelUp.setStyle(style);
			levelUp.addListener(
				new ClickListener(){
					@Override
			        public void clicked(InputEvent event, float x, float y) {
						buttonSound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
						if (levelup) character.levelUp();
						saveService.saveDataValue(SaveEnum.PLAYER, character);
						saveService.saveDataValue(SaveEnum.CONTEXT, GameContext.LEVEL);
						showScreen(ScreenEnum.LOAD_GAME);
			        }
				}
			);
			levelUp.setPosition(1200, 30);
			this.addActor(levelUp);
		}
	}
	
	@Override
	public void buildStage() {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void render(float delta) {
		super.render(delta);
		OrthographicCamera camera = (OrthographicCamera) getCamera();
        batch.setTransformMatrix(camera.view);
		batch.setProjectionMatrix(camera.combined);
		camera.update();
		batch.begin();
		font.setColor(0.4f,0.4f,0.4f,1);
		int baseX = 1125;
		int baseY = 1050;
		int offset = 0;
		for (Stat stat: PlayerCharacter.Stat.values()){
			font.setColor(0.6f, 0.2f, 0.1f, 1);
			Texture statTexture = statTextureMap.get(stat);
			batch.draw(statTexture, baseX + 22, baseY - (offset + 30), statTexture.getWidth() / (statTexture.getHeight() / 52), 52);
			font.draw(batch, ": ", baseX + 270, baseY - offset);
			int amount = character.getBaseStat(stat);
			setFontColor(font, amount);
			font.draw(batch, String.valueOf(amount), baseX + 300, baseY - offset);
			font.draw(batch, "- " + PlayerCharacter.getStatMap().get(stat).get(amount), baseX+322, baseY - offset);
			offset += 75;
		}
		int storedLevels = character.getStoredLevels();
		font.draw(batch, "Level: " + character.getLevel() + "\nExperience: " + character.getExperience() + (storedLevels > 0 ? "\nAvailable Levels: " + storedLevels : ""), 1200, 1200);
		batch.end();
		if (Gdx.input.isKeyJustPressed(Keys.ENTER)){
			showScreen(ScreenEnum.LOAD_GAME);
		}			
	}
	
	private void setFontColor(BitmapFont font, int amount){
		float red = amount / 10.0f;
		float green = .3f;
		float blue = (1 - (amount/10))/2;
		font.setColor(red, green, blue, 1);
	}
}
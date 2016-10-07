package com.majalis.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ObjectMap;
import com.majalis.asset.AssetEnum;
import com.majalis.character.PlayerCharacter;
import com.majalis.character.PlayerCharacter.Stat;
import com.majalis.encounter.Background;
/*
 * The options/configuration screen.  UI that handles player input to save Preferences to a player's file system.
 */
public class CharacterScreen extends AbstractScreen {

	public static final ObjectMap<String, Class<?>> resourceRequirements = new ObjectMap<String, Class<?>>();
	static {
		resourceRequirements.put("uiskin.json", Skin.class);
		resourceRequirements.put("node_sound.wav", Sound.class);
		resourceRequirements.put("TinySprite0.png", Texture.class);
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
	}
	
	private final PlayerCharacter character;

	public CharacterScreen(ScreenFactory factory, ScreenElements elements, AssetManager assetManager, PlayerCharacter character) {
		super(factory, elements);
		this.character = character;
		this.addActor(new Background((Texture)assetManager.get("ClassSelect.jpg", Texture.class))); 
		Skin skin = assetManager.get("uiskin.json", Skin.class);
		final Sound buttonSound = assetManager.get("node_sound.wav", Sound.class); 
		final TextButton done = new TextButton("Done", skin);
		
		done.setWidth(180); //Sets positional stuff for "done" button)
		done.setHeight(40);
		done.addListener(
			new ClickListener(){
				@Override
		        public void clicked(InputEvent event, float x, float y) {
					buttonSound.play(.5f);
					showScreen(ScreenEnum.LOAD_GAME);		   
		        }
			}
		);
		done.addAction(Actions.moveTo(done.getX() + 1015, done.getY() + 20));
		this.addActor(done);
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
		int baseX = 800;
		int baseY = 700;
		int offset = 0;
		for (Stat stat: PlayerCharacter.Stat.values()){
			font.setColor(0.6f,0.2f,0.1f,1);
			font.draw(batch, stat.toString(), baseX+50, baseY - offset);
			font.draw(batch, ": ", baseX+180, baseY - offset);
			int amount = character.getStat(stat);
			setFontColor(font, amount);
			font.draw(batch, String.valueOf(amount), baseX+200, baseY - offset);
			font.draw(batch, "- " + PlayerCharacter.getStatMap().get(stat).get(amount), baseX+240, baseY - offset);
			offset += 50;
		}
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
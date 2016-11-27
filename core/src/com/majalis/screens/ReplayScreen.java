package com.majalis.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ObjectMap;
import com.majalis.asset.AssetEnum;
import com.majalis.battle.BattleFactory.EnemyEnum;
import com.majalis.encounter.Background.BackgroundBuilder;
/*
 * The replay encounters.  UI that handles player input to select and load and encounters to experience again.
 */
public class ReplayScreen extends AbstractScreen {
	
	public static final ObjectMap<String, Class<?>> resourceRequirements = new ObjectMap<String, Class<?>>();
	static {
		resourceRequirements.put(AssetEnum.UI_SKIN.getPath(), Skin.class);
		resourceRequirements.put(AssetEnum.BUTTON_SOUND.getPath(), Sound.class);
		resourceRequirements.put(AssetEnum.BUTTON_SOUND.getPath(), Sound.class);
		resourceRequirements.put(AssetEnum.WEREBITCH.getPath(), Texture.class);
		resourceRequirements.put(AssetEnum.HARPY.getPath(), Texture.class);
		resourceRequirements.put(AssetEnum.BRIGAND.getPath(), Texture.class);
		resourceRequirements.put(AssetEnum.SLIME.getPath(), Texture.class);
		resourceRequirements.put(AssetEnum.CENTAUR.getPath(), Texture.class);
		resourceRequirements.put(AssetEnum.MAIN_MENU_MUSIC.getPath(), Music.class);
		resourceRequirements.put(AssetEnum.DEFAULT_BACKGROUND.getPath(), Texture.class);
	}
	private final AssetManager assetManager;
	private final ObjectMap<String, Integer> enemyKnowledge;
	private final Skin skin;
	private final Sound sound;
	private Texture enemyTexture;
	private String nothingToDisplay;
	
	public ReplayScreen(ScreenFactory factory, ScreenElements elements, AssetManager assetManager, ObjectMap<String, Integer> enemyKnowledge) {
		super(factory, elements);
		this.addActor(new BackgroundBuilder(assetManager.get(AssetEnum.DEFAULT_BACKGROUND.getPath(), Texture.class)).build());
		this.assetManager = assetManager;
		this.enemyKnowledge = enemyKnowledge;
		this.skin = assetManager.get(AssetEnum.UI_SKIN.getPath(), Skin.class);
		this.sound = assetManager.get(AssetEnum.BUTTON_SOUND.getPath(), Sound.class);
		nothingToDisplay = "No knowledge to display yet.";
	}

	@Override
	public void render(float delta) {
		super.render(delta);
		OrthographicCamera camera = (OrthographicCamera) getCamera();
        batch.setTransformMatrix(camera.view);
		camera.update();
		batch.begin();
		if (enemyTexture != null){
			batch.draw(enemyTexture, 1450, 485, (enemyTexture.getWidth() / (enemyTexture.getHeight() / 1155.f)), 1155);
		}
		if(!nothingToDisplay.equals("")){
			font.draw(batch, nothingToDisplay, 1170, 880);
		}
		batch.end();

		if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)){
			showScreen(ScreenEnum.MAIN_MENU);
		}
	}

	@Override
	public void buildStage() {
		Table table = new Table();
		
		for (final EnemyEnum type : EnemyEnum.values()){
			if (!enemyKnowledge.containsKey(type.toString())) continue;
			nothingToDisplay = "";
			TextButton button = new TextButton(type.toString(), skin);
			button.addListener(
				new ClickListener(){
					@Override
			        public void clicked(InputEvent event, float x, float y) {
						sound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
						enemyTexture = assetManager.get(type.getPath(), Texture.class);
			        }
				}
			);
			table.add(button).size(180, 60).row();
		}
        table.setFillParent(true);        
        this.addActor(table);
        table.setPosition(495, 195);
        
		final TextButton done = new TextButton("Done", skin);
		
		done.addListener(
			new ClickListener(){
				@Override
		        public void clicked(InputEvent event, float x, float y) {
					sound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
					showScreen(ScreenEnum.MAIN_MENU);		   
		        }
			}
		);
		done.setPosition(1500, 100);
		this.addActor(done);
	}
	
	@Override
	public void show() {
		super.show();
	    getRoot().getColor().a = 0;
	    getRoot().addAction(Actions.fadeIn(0.5f));
	}
}
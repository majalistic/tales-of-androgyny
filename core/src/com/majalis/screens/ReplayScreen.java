package com.majalis.screens;

import static com.majalis.asset.AssetEnum.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.majalis.asset.AssetEnum;
import com.majalis.character.EnemyEnum;
import com.majalis.character.EnemyCharacter;
import com.majalis.character.Stance;
import com.majalis.encounter.Background.BackgroundBuilder;
/*
 * The replay encounters.  UI that handles player input to select and load and encounters to experience again.
 */
public class ReplayScreen extends AbstractScreen {
	
	public static final Array<AssetDescriptor<?>> resourceRequirements = new Array<AssetDescriptor<?>>();
	// this should load the requisite enemy textures/animations depending on knowledge
	static {
		resourceRequirements.add(AssetEnum.UI_SKIN.getSkin());
		resourceRequirements.add(AssetEnum.BUTTON_SOUND.getSound());

		for (final EnemyEnum type : EnemyEnum.values()) {
			if (type.getTextures() != null) {
				for (AssetDescriptor<Texture> textures : type.getTextures()) {
					resourceRequirements.add(textures);
				}
			}
		}		
		resourceRequirements.add(AssetEnum.MAIN_MENU_MUSIC.getMusic());
		resourceRequirements.add(AssetEnum.DEFAULT_BACKGROUND.getTexture());

		Array<AssetEnum> animationReqs = new Array<AssetEnum>(new AssetEnum[]{
			HARPY_ANIMATION, HARPY_ATTACK_ANIMATION, FEATHERS_ANIMATION, FEATHERS2_ANIMATION, BRIGAND_ANIMATION, ANAL_ANIMATION, CENTAUR_ANIMATION			
		});
		for (AssetEnum asset: animationReqs) {
			resourceRequirements.add(asset.getAnimation());
		}
		resourceRequirements.addAll(MainMenuScreen.resourceRequirements);
	}
	private final AssetManager assetManager;
	private final ObjectMap<String, Integer> enemyKnowledge;
	private final Skin skin;
	private final Sound sound;
	private EnemyCharacter currentCharacter;
	private String nothingToDisplay;
	
	public ReplayScreen(ScreenFactory factory, ScreenElements elements, AssetManager assetManager, ObjectMap<String, Integer> enemyKnowledge) {
		super(factory, elements);
		this.addActor(new BackgroundBuilder(assetManager.get(AssetEnum.DEFAULT_BACKGROUND.getTexture())).build());
		this.assetManager = assetManager;
		this.enemyKnowledge = enemyKnowledge;
		this.skin = assetManager.get(AssetEnum.UI_SKIN.getSkin());
		this.sound = assetManager.get(AssetEnum.BUTTON_SOUND.getSound());
		nothingToDisplay = "No knowledge to display yet.";
	}

	@Override
	public void render(float delta) {
		super.render(delta);
		OrthographicCamera camera = (OrthographicCamera) getCamera();
        batch.setTransformMatrix(camera.view);
		camera.update();
		batch.begin();
		if(!nothingToDisplay.equals("")) {
			font.setColor(Color.BLACK);
			font.draw(batch, nothingToDisplay, 1170, 880);
		}
		batch.end();

		if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
			showScreen(ScreenEnum.MAIN_MENU);
		}
	}

	@Override
	public void buildStage() {
		Table table = new Table();
		
		for (final EnemyEnum type : EnemyEnum.values()) {
			if (!enemyKnowledge.containsKey(type.toString())) continue;
			nothingToDisplay = "";
			TextButton button = new TextButton(type.toString(), skin);
			ObjectMap<Stance, Array<Texture>> textures = new ObjectMap<Stance, Array<Texture>>();			
			Array<Texture> possibleTextures = type.getTextures(assetManager);
			Texture enemyTexture = null;
			if (possibleTextures.size > 0) {
				enemyTexture = possibleTextures.get(0);		
			}
			textures.put(Stance.BALANCED, new Array<Texture>(new Texture[]{enemyTexture}));
			final EnemyCharacter enemy = new EnemyCharacter(possibleTextures, textures, type.getAnimations(assetManager), type);
			this.addActor(enemy);
			enemy.addAction(Actions.hide());
			enemy.setPosition(700, 0);
			button.addListener(
				new ClickListener() {
					@Override
			        public void clicked(InputEvent event, float x, float y) {
						sound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
						if (currentCharacter != null)
							currentCharacter.addAction(Actions.hide());
						currentCharacter = enemy;
						enemy.addAction(Actions.show());
			        }
				}
			);
			table.add(button).size(265, 60).row();
		}
        table.setFillParent(true);        
        this.addActor(table);
        table.setPosition(495, 100);
        
		final TextButton done = new TextButton("Done", skin);
		
		done.addListener(
			new ClickListener() {
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
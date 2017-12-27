package com.majalis.screens;

import static com.majalis.asset.AssetEnum.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.majalis.asset.AssetEnum;
import com.majalis.character.EnemyEnum;
import com.majalis.character.EnemyCharacter;
import com.majalis.character.Stance;
import com.majalis.encounter.Background.BackgroundBuilder;
/*
 * Screen the displays encountered characters and their respective CG art.
 */
public class ReplayScreen extends AbstractScreen {
	
	public static final Array<AssetDescriptor<?>> resourceRequirements = new Array<AssetDescriptor<?>>();
	// this should load the requisite enemy textures/animations depending on knowledge
	static {
		resourceRequirements.add(AssetEnum.UI_SKIN.getSkin());
		resourceRequirements.add(AssetEnum.BUTTON_SOUND.getSound());

		for (final EnemyEnum type : EnemyEnum.values()) {
			if (type.getTextures() != null) {
				resourceRequirements.addAll(type.getTextures());
			}
			for (ObjectMap.Entry<String, Array<String>> entry : type.getImagePaths()) {
				for (String path : entry.value) {
					resourceRequirements.add(new AssetDescriptor<Texture>(path, Texture.class));
				}
			}
		}		
		resourceRequirements.add(AssetEnum.MAIN_MENU_MUSIC.getMusic());
		resourceRequirements.add(AssetEnum.DEFAULT_BACKGROUND.getTexture());
		resourceRequirements.add(AssetEnum.NULL.getTexture());
		resourceRequirements.add(AssetEnum.OGRE_BANGED.getTexture());
		resourceRequirements.add(AssetEnum.BUNNY_CARAMEL_ANAL.getTexture());
		resourceRequirements.add(AssetEnum.BUNNY_VANILLA_ANAL.getTexture());
		resourceRequirements.add(AssetEnum.BUNNY_CHOCOLATE_ANAL.getTexture());
		resourceRequirements.add(AssetEnum.BUNNY_DARK_CHOCOLATE_ANAL.getTexture());
		resourceRequirements.add(AssetEnum.BUNNY_CREAM_ANAL.getTexture());
		
		Array<AssetEnum> animationReqs = new Array<AssetEnum>(new AssetEnum[]{
			HARPY_ANIMATION, HARPY_ATTACK_ANIMATION, FEATHERS_ANIMATION, FEATHERS2_ANIMATION, BRIGAND_ANIMATION, ANAL_ANIMATION, CENTAUR_ANIMATION, ORC_ANIMATION			
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
	private final Image cg;
	private EnemyCharacter currentCharacter;
	private String nothingToDisplay;
	
	public ReplayScreen(ScreenFactory factory, ScreenElements elements, AssetManager assetManager, ObjectMap<String, Integer> enemyKnowledge) {
		super(factory, elements, null);
		this.addActor(new BackgroundBuilder(assetManager.get(AssetEnum.DEFAULT_BACKGROUND.getTexture())).build());
		this.assetManager = assetManager;
		this.enemyKnowledge = enemyKnowledge;
		this.skin = assetManager.get(AssetEnum.UI_SKIN.getSkin());
		this.sound = assetManager.get(AssetEnum.BUTTON_SOUND.getSound());
		this.cg = new Image(assetManager.get(AssetEnum.NULL.getTexture()));
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
		
		final Label displayText = new Label("", skin);
		displayText.setPosition(150, 900);
		displayText.setWidth(400);
		displayText.setWrap(true);
		displayText.setColor(Color.BLACK);
		displayText.setAlignment(Align.top);
		this.addActor(displayText);
		
		boolean left = true;
		
		final Table cgTable = new Table();
		cgTable.setFillParent(true);     
		cgTable.align(Align.top);
        this.addActor(cgTable);
        cgTable.setPosition(500, -750);
	
		for (final EnemyEnum type : EnemyEnum.values()) {
			if (!enemyKnowledge.containsKey(type.toString())) continue;
			nothingToDisplay = "";
			TextButton button = new TextButton(type.toString(), skin);
			ObjectMap<Stance, Array<Texture>> textures = new ObjectMap<Stance, Array<Texture>>();			
			Array<Texture> possibleTextures = type.getTextures(assetManager);
			textures.put(Stance.BALANCED, possibleTextures);
			final EnemyCharacter enemy = new EnemyCharacter(possibleTextures, textures, type.getAnimations(assetManager), type);
			this.addActor(enemy);
			enemy.addAction(Actions.hide());
			enemy.setPosition(type == EnemyEnum.CENTAUR || type == EnemyEnum.UNICORN ? 0 : type == EnemyEnum.HARPY ? -250 : -100, type == EnemyEnum.ORC ? -50 : 0);
			button.addListener(
				new ClickListener() {
					@Override
			        public void clicked(InputEvent event, float x, float y) {
						sound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
						if (currentCharacter != null)
							currentCharacter.addAction(Actions.hide());
						currentCharacter = enemy;
						enemy.addAction(Actions.show());
						displayText.setText(type.getDescription());
						fillCGTable(cgTable, type, enemy);
			        }
				}
			);
			table.add(button).size(265, 60).padRight(20);
			if (!left) table.row();
			left = !left;
		}
        table.setFillParent(true);     
        table.align(Align.top);
        this.addActor(table);
        table.setPosition(550, -100);
        
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
		this.addActor(cg);
	}
	
	private void fillCGTable(final Table table, final EnemyEnum type, final EnemyCharacter enemy) {
		table.clear();
		cg.clear();
		cg.addAction(Actions.hide());
		cg.addListener(
			new ClickListener() {
				@Override
		        public void clicked(InputEvent event, float x, float y) {
					cg.addAction(Actions.hide());
					enemy.addAction(Actions.show());
		        }
			}
		);
	
		TextButton button = new TextButton("Normal", skin);
		button.addListener(
			new ClickListener() {
				@Override
		        public void clicked(InputEvent event, float x, float y) {
					sound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
					cg.addAction(Actions.hide());
					enemy.addAction(Actions.show());
		        }
			}
		);
		table.add(button);
		
		// for replay button - need to create an encounter that autosaves to a different slot
		switch(type) {
			case ADVENTURER:
				attachListener(new TextButton("Cowgirl", skin), AssetEnum.ADVENTURER_ANAL, cg, enemy, table);
				break;
			case ANGEL:
				break;
			case BEASTMISTRESS:
				break;
			case BRIGAND:
				attachListener(new TextButton("Missionary", skin), AssetEnum.BRIGAND_MISSIONARY, cg, enemy, table);
				attachListener(new TextButton("Irrumatio", skin), AssetEnum.BRIGAND_ORAL, cg, enemy, table);
				break;
			case BUNNY:
				AssetEnum bunnyAnal = null;
				String bunnyType = Gdx.app.getPreferences("tales-of-androgyny-preferences").getString("bunny", "CREAM");
				if (bunnyType.equals("CREAM")) bunnyAnal = AssetEnum.BUNNY_CREAM_ANAL;
				if (bunnyType.equals("VANILLA")) bunnyAnal = AssetEnum.BUNNY_VANILLA_ANAL;
				if (bunnyType.equals("CARAMEL")) bunnyAnal = AssetEnum.BUNNY_CARAMEL_ANAL;
				if (bunnyType.equals("CHOCOLATE")) bunnyAnal = AssetEnum.BUNNY_CHOCOLATE_ANAL;
				if (bunnyType.equals("DARK-CHOCOLATE")) bunnyAnal = AssetEnum.BUNNY_DARK_CHOCOLATE_ANAL;
				attachListener(new TextButton("Anal", skin), bunnyAnal, cg, enemy, table);
				break;
			case CENTAUR:
				attachListener(new TextButton("Anal", skin), AssetEnum.CENTAUR_ANAL, cg, enemy, table);
				attachListener(new TextButton("Anal (X-ray)", skin), AssetEnum.CENTAUR_ANAL_XRAY, cg, enemy, table);
				table.row();
				attachListener(new TextButton("Oral", skin), AssetEnum.CENTAUR_ORAL, cg, enemy, table);
				break;
			case GHOST:
				break;
			case GOBLIN:
				attachListener(new TextButton("Doggy", skin), AssetEnum.GOBLIN_ANAL, cg, enemy, table);
				attachListener(new TextButton("Facesit", skin), AssetEnum.GOBLIN_FACE_SIT, cg, enemy, table);
				break;
			case GOBLIN_MALE:
				attachListener(new TextButton("Anal", skin), AssetEnum.GOBLIN_ANAL_MALE, cg, enemy, table);
				attachListener(new TextButton("Facesit", skin), AssetEnum.GOBLIN_FACE_SIT_MALE, cg, enemy, table);
				break;
			case GOLEM:
				break;
			case HARPY:
				attachListener(new TextButton("Doggy", skin), AssetEnum.HARPY_ANAL, cg, enemy, table);
				attachListener(new TextButton("Fellatio 1", skin), AssetEnum.HARPY_FELLATIO_0, cg, enemy, table);
				table.row();
				attachListener(new TextButton("Fellatio 2", skin), AssetEnum.HARPY_FELLATIO_1, cg, enemy, table);
				attachListener(new TextButton("Fellatio 3", skin), AssetEnum.HARPY_FELLATIO_2, cg, enemy, table);
				attachListener(new TextButton("Fellatio 4", skin), AssetEnum.HARPY_FELLATIO_3, cg, enemy, table);
				break;
			case OGRE:
				attachListener(new TextButton("Game Over", skin), AssetEnum.OGRE_BANGED, cg, enemy, table);
				break;
			case ORC:
				attachListener(new TextButton("Anal", skin), AssetEnum.ORC_PRONE_BONE, cg, enemy, table);
				break;
			case SLIME:
				attachListener(new TextButton("Love Dart", skin), AssetEnum.SLIME_DOGGY, cg, enemy, table);
				break;
			case SPIDER:
				break;
			case UNICORN:
				attachListener(new TextButton("Anal", skin), AssetEnum.UNICORN_ANAL, cg, enemy, table);
				attachListener(new TextButton("Anal (X-ray)", skin), AssetEnum.UNICORN_ANAL_XRAY, cg, enemy, table);
				break;
			case WERESLUT:
				break;
			default:
				break;
			
		}
	}
	
	private void attachListener(final TextButton button, final AssetEnum asset, final Image image, final EnemyCharacter toHide, final Table toAdd) {
		button.addListener(new ClickListener() {
			@Override
	        public void clicked(InputEvent event, float x, float y) {
				sound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
				Texture texture = assetManager.get(asset.getTexture());
				image.setDrawable(new TextureRegionDrawable(new TextureRegion(texture)));
				image.addAction(Actions.show());
				image.setPosition(0, 0);
				image.setWidth((int) (texture.getWidth() / (texture.getHeight() / 1080.)));
				image.setHeight(1080);
				toHide.addAction(Actions.hide());
	        }
		});
		toAdd.add(button);
	}
	
	@Override
	public void show() {
		super.show();
	    getRoot().getColor().a = 0;
	    getRoot().addAction(Actions.fadeIn(0.5f));
	}
	
	@Override
	public void dispose() {
		for(AssetDescriptor<?> path: resourceRequirements) {
			if (path.fileName.equals(AssetEnum.BUTTON_SOUND.getSound().fileName) || path.type == Music.class) continue;
			assetManager.unload(path.fileName);
		}
	}
}
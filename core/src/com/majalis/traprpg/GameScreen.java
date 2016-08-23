package com.majalis.traprpg;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
/*
 * The screen that displays the world map.  UI that Handles player input while on the world map - will delegate to other screens depending on the gameWorld state.
 */
public class GameScreen extends AbstractScreen {

	private final AssetManager assetManager;
	private final GameWorld world;
	private final SaveService saveService;
	private String classSelection;
	private Skin skin;
	private Sound buttonSound;
	
	// GameWorld should be passed in along with the saving (but not loading) interface
	public GameScreen(ScreenFactory factory, AssetManager assetManager, SaveService saveService, String classSelection) {
		super(factory);
		this.assetManager = assetManager;
		this.saveService = saveService;
		this.classSelection = classSelection;
		world = new GameWorld(false);
		buttonSound = Gdx.audio.newSound(Gdx.files.internal("sound.wav"));			
	}
	
	@Override
	public void buildStage() {
		// asynchronous
		assetManager.load("uiskin.json", Skin.class);
		assetManager.load("sound.wav", Sound.class);
		
		// forcing asynchronous loading to be synchronous
		assetManager.finishLoading();

		skin = assetManager.get("uiskin.json", Skin.class);
		buttonSound = assetManager.get("sound.wav", Sound.class);
		Table table = new Table();
		Array<String> classes = new Array<String>();
		classes.addAll("Warrior", "Paladin", "Thief", "Ranger", "Mage", "Enchanter");
		for (String jobClass: classes){
			TextButton button = new TextButton(jobClass, skin);
			button.addListener(getListener(jobClass));
			table.add(button).row();
		}
        table.setFillParent(true);
        this.addActor(table);	
	}
	
	@Override
	public void render(float delta) {
		super.render(delta);
		world.gameLoop();
		if (world.gameExit){
			showScreen(ScreenEnum.MAIN_MENU);
		}
		else if (world.gameOver){
			showScreen(ScreenEnum.GAME_OVER);
		}
		else {
			draw();
		}
	}
	
	public void draw(){
		batch.begin();
		OrthographicCamera camera = (OrthographicCamera) getCamera();
        batch.setTransformMatrix(camera.view);
		batch.setProjectionMatrix(camera.combined);
		camera.update();
		// need to make these relative to viewport
		font.draw(batch, "Choose a class:", 1230, 900);
		font.draw(batch, classSelection, 1260, 850);
		
		if (world.displayHUD){
			font.draw(batch, "FPS: " + MathUtils.ceil(1/Gdx.graphics.getDeltaTime()), camera.position.x-200+(400), camera.position.y+220);
		}
		batch.end();
	}
	
	private ClickListener getListener(final String selection){
		return new ClickListener(){
	        @Override
	        public void clicked(InputEvent event, float x, float y) {
	        	buttonSound.play();
	        	classSelection = selection;
	        	saveService.saveDataValue("Class", selection);
	        }
	    };
	}
}
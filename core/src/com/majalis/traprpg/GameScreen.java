package com.majalis.traprpg;

import com.badlogic.gdx.Game;
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

public class GameScreen extends AbstractScreen {

	private final AssetManager assetManager;
	private final GameWorld world;
	private String classSelection;
	private boolean paused;
	private Skin skin;
	private Sound buttonSound;
	
	//SaveManager should be passed in, extracting GameWorld from it, or possibly GameWorld should be passed in along with the saving (but not loading) interface
	public GameScreen(Game game, ScreenService service, AssetManager assetManager, String classSelection) {
		super(game, service);
		this.assetManager = assetManager;
		this.classSelection = classSelection;
		world = new GameWorld(false);
		paused = false;
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
		classes.addAll("Warrior", "Paladin", "Rogue", "Ranger", "Mage", "Enchanter");
		for (String jobClass: classes){
			TextButton button = new TextButton(jobClass, skin);
			button.addListener(getListener(jobClass));
			table.add(button).row();
		}
        table.setFillParent(true);
        this.addActor(table);	
	}
	
	
	private ClickListener getListener(final String selection){
		return new ClickListener(){
	        @Override
	        public void clicked(InputEvent event, float x, float y) {
	        	buttonSound.play();
	        	classSelection = selection;
	        	save(selection);
	        }
	    };
	}

	private void save(String data){
		SaveManager save = new SaveManager(false);
    	save.saveDataValue("Class", data);
	}
	
	@Override
	public void render(float delta) {
		super.render(delta);
		world.gameLoop();
		if (paused!=world.paused){
			
		}
		paused = world.paused;
		if (world.gameExit){
			showScreen(ScreenEnum.MAIN_MENU);
		}
		else if (world.gameOver){
			showScreen(ScreenEnum.GAME_OVER);
		}
		draw();
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
	
	@Override
	public void show() {
		super.show();
	}
}
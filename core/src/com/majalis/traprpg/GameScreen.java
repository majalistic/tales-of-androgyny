package com.majalis.traprpg;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ObjectMap;
/*
 * The screen that displays the world map.  UI that Handles player input while on the world map - will delegate to other screens depending on the gameWorld state.
 */
public class GameScreen extends AbstractScreen {

	private final AssetManager assetManager;
	private final SaveService saveService;
	private final GameWorld world;
	private Skin skin;
	private Sound buttonSound;
	public static final ObjectMap<String, Class<?>> resourceRequirements = new ObjectMap<String, Class<?>>();
	static {
		resourceRequirements.put("uiskin.json", Skin.class);
		resourceRequirements.put("sound.wav", Sound.class);
	}
	public GameScreen(ScreenFactory factory, AssetManager assetManager, SaveService saveService, GameWorld world) {
		super(factory);
		this.assetManager = assetManager;
		this.saveService = saveService;
		this.world = world;
	}

	@Override
	public void buildStage() {
		assetManager.load("uiskin.json", Skin.class);
		assetManager.load("sound.wav", Sound.class);
		
		// forcing asynchronous loading to be synchronous
		assetManager.finishLoading();

		skin = assetManager.get("uiskin.json", Skin.class);
		buttonSound = assetManager.get("sound.wav", Sound.class);
		Table table = new Table();
		TextButton button = new TextButton("New Encounter", skin);
		button.addListener(new ClickListener(){
	        @Override
	        public void clicked(InputEvent event, float x, float y) {
	        	buttonSound.play();
	        	saveService.saveDataValue("Context", GameWorldManager.GameContext.ENCOUNTER);
	        	showScreen(ScreenEnum.ENCOUNTER);
	        }
	    });
		table.add(button).row();
        table.setFillParent(true);
        this.addActor(table);	
	}
	
	@Override
	public void render(float delta) {
		super.render(delta);
		draw();
	}
	
	public void draw(){
		batch.begin();
		OrthographicCamera camera = (OrthographicCamera) getCamera();
        batch.setTransformMatrix(camera.view);
		batch.setProjectionMatrix(camera.combined);
		camera.update();
		batch.end();
	}
	
	@Override
	public void dispose() {
		for(String path: resourceRequirements.keys()){
			assetManager.unload(path);
		}
	}
	
}
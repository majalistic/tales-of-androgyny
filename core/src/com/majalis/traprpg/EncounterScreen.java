package com.majalis.traprpg;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

/*
 *Screen for displaying Encounters.  UI that Handles player input while in an encounter.
 */
public class EncounterScreen extends AbstractScreen {

	private final AssetManager assetManager;
	private final Encounter encounter;
	private final SaveService saveService;
	private Skin skin;
	private Sound buttonSound;
	private String classSelection;
	// these are required for all encounters, possibly - requirements for an individual encounter must be parsed by the EncounterFactory
	public static final ObjectMap<String, Class<?>> resourceRequirements = new ObjectMap<String, Class<?>>();
	static {
		resourceRequirements.put("uiskin.json", Skin.class);
		resourceRequirements.put("sound.wav", Sound.class);
	}
	protected EncounterScreen(ScreenFactory screenFactory, AssetManager assetManager, SaveService saveService, Encounter encounter, String classSelection) {
		super(screenFactory);
		this.assetManager = assetManager;
		this.encounter = encounter;
		this.saveService = saveService;
		this.classSelection = classSelection;
	}

	@Override
	public void buildStage() {
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
		encounter.gameLoop();
		if (encounter.gameExit){
			showScreen(ScreenEnum.MAIN_MENU);
		}
		else if (encounter.gameOver){
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
		
		if (encounter.displayHUD){
			font.draw(batch, "FPS: " + MathUtils.ceil(1/Gdx.graphics.getDeltaTime()), camera.position.x-200+(400), camera.position.y+220);
		}
		batch.end();
	}

	@Override
	public void dispose() {
		for(String path: resourceRequirements.keys()){
			assetManager.unload(path);
			
		}
	}
	
	private ClickListener getListener(final String selection){
		return new ClickListener(){
	        @Override
	        public void clicked(InputEvent event, float x, float y) {
	        	buttonSound.play();
	        	classSelection = selection;
	        	saveService.saveDataValue("Context", GameWorldManager.GameContext.WORLD_MAP);
	        	saveService.saveDataValue("Class", selection);
	        	showScreen(ScreenEnum.LOAD_GAME);
	        }
	    };
	}
}

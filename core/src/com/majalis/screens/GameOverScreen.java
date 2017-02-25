package com.majalis.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ObjectMap;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.SkeletonJson;
import com.esotericsoftware.spine.SkeletonMeshRenderer;
/*
 * Screen for displaying "Game Over" - can return the player to the main menu or offer them the ability to save their GO encounter.  May be loaded with different splashes / music at runtime.
 */
public class GameOverScreen extends AbstractScreen {

	public static final ObjectMap<String, Class<?>> resourceRequirements = new ObjectMap<String, Class<?>>();
	static {}
	private final AssetManager assetManager;
	private TextureAtlas atlas;
	private SkeletonMeshRenderer renderer;
	private AnimationState state;
	private Skeleton skeleton;
	
	public GameOverScreen(ScreenFactory factory, ScreenElements elements,  AssetManager assetManager) {
		super(factory, elements);
		this.assetManager = assetManager;
	}

	@Override
	public void buildStage() {
		Actor click = new Actor();
		click.setBounds(0, 0, 2000, 2000);
		click.addListener(new ClickListener(){
			@Override
	        public void clicked(InputEvent event, float x, float y) {
				showScreen(ScreenEnum.MAIN_MENU);
	        }
		});
		this.addActor(click);
		
		renderer = new SkeletonMeshRenderer();
		renderer.setPremultipliedAlpha(true);
		atlas = new TextureAtlas(Gdx.files.internal("animation/SplurtGO.atlas"));
		SkeletonJson json = new SkeletonJson(atlas); // This loads skeleton JSON data, which is stateless.
		json.setScale(1f);
		SkeletonData skeletonData = json.readSkeletonData(Gdx.files.internal("animation/SplurtGO.json"));
		
		skeleton = new Skeleton(skeletonData); // Skeleton holds skeleton state (bone positions, slot attachments, etc).
		skeleton.setPosition(1520, 1080);
		
		AnimationStateData stateData = new AnimationStateData(skeletonData); // Defines mixing (crossfading) between animations.

		state = new AnimationState(stateData); // Holds the animation state for a skeleton (current animation, time, etc).
		state.setTimeScale(1f); 

		// Queue animations on tracks 0 and 1.
		state.setAnimation(0, "Splurt", false);
		state.addAnimation(0, "Idle", true, 5f);
	}
	
	@Override
	public void render(float delta) {
		super.render(delta);
		OrthographicCamera camera = (OrthographicCamera) getCamera();
        batch.setTransformMatrix(camera.view);
		batch.setProjectionMatrix(camera.combined);
		camera.update();
		batch.begin();
		Gdx.gl.glClearColor(.75f, .5f, .5f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		state.update(Gdx.graphics.getDeltaTime());
		state.apply(skeleton);
		skeleton.updateWorldTransform();
		renderer.draw((PolygonSpriteBatch)batch, skeleton);
		font.setColor(Color.BLACK);
		font.draw(batch, "GAME OVER - Press Enter", 975, 600);
		batch.end();
		if (Gdx.input.isKeyJustPressed(Keys.ENTER) || Gdx.input.isKeyJustPressed(Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Keys.SPACE))
			showScreen(ScreenEnum.MAIN_MENU);
	}
	
	@Override
	public void show() {
		super.show();
	}	
	
	@Override
	public void dispose() {
		for(String path: resourceRequirements.keys()){
			assetManager.unload(path);
		}
	}
}
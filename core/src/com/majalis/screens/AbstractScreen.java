package com.majalis.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import com.majalis.asset.AnimatedImage;
import com.majalis.asset.AssetEnum;
import com.majalis.encounter.Background;
import com.majalis.encounter.Background.BackgroundBuilder;
/*
 * Abstract class which all Screens inherit from; each screen has a a single master Stage.  Allows a Screen to switch to a different screen via an enum.
 */
public abstract class AbstractScreen extends Stage3D implements Screen {

	private final Game game;
	private final ScreenFactory screenFactory;

	protected final PolygonSpriteBatch batch;
	protected final BitmapFont font;
	protected final AssetManager assetManager;
	protected final ScreenElements fontFactory;
	protected boolean debug = false;
	protected float clearRed, clearGreen, clearBlue, clearAlpha;
	private AssetEnum musicPath;
	private Music music;
    private boolean switchingScreen;
	
    protected AbstractScreen(ScreenFactory screenFactory, ScreenElements elements, AssetEnum musicPath) {
        super(elements.getViewport(), elements.getBatch());
        this.game = screenFactory.getGame();
        this.screenFactory = screenFactory;
        this.batch = elements.getBatch();
        this.assetManager = elements.getAssetManager();
        this.fontFactory = elements;
        this.font = elements.getFont(32);
        this.musicPath = musicPath;
        clearAlpha = 1;
    }
 
    // Subclasses must load actors in this method
    public abstract void buildStage();
    protected void switchFade(ScreenEnum screenRequest, AbstractScreen currentScreen, AssetEnum oldMusicPath, Music oldMusic) { 
    	this.addAction(Actions.sequence(Actions.fadeOut(.2f), Actions.hide(), new Action() {
			@Override
			public boolean act(float delta) {
				switchScreen(screenRequest, currentScreen, oldMusicPath, oldMusic);
				return true;
			}
		}));     	
    }
    
    public void showScreen(ScreenEnum screenRequest) {
    	if (switchingScreen) return;
    	switchingScreen = true;
        // Get current screen to dispose it
        AbstractScreen currentScreen = (AbstractScreen) game.getScreen();
    	
        AssetEnum oldMusicPath = currentScreen.getMusicPath();
        Music oldMusic = currentScreen.getMusic();
        switchFade(screenRequest, currentScreen, oldMusicPath, oldMusic);
    }  
    
    protected void buildMenu() {
    	Group menuGroup = new Group();
    	addActor(menuGroup);
    	Image mainMenuHighlight = new Image(assetManager.get(AssetEnum.NULL.getTexture()));
        mainMenuHighlight.setBounds(0, 1050, 2000, 100);
        menuGroup.addActor(mainMenuHighlight);   
        
        ButtonStyle buttonStyle = new ButtonStyle(new TextureRegionDrawable(new TextureRegion(assetManager.get(AssetEnum.MENU_BUTTON_UP.getTexture()))),  new TextureRegionDrawable(new TextureRegion(assetManager.get(AssetEnum.MENU_BUTTON_DOWN.getTexture()))),  new TextureRegionDrawable(new TextureRegion(assetManager.get(AssetEnum.MENU_BUTTON_HIGHLIGHT.getTexture()))));
        buttonStyle.over = new TextureRegionDrawable(new TextureRegion(assetManager.get(AssetEnum.MENU_BUTTON_HIGHLIGHT.getTexture())));		
        
        int mainMenuX = 600;
        int muteButtonX = 1000;
        
        Button mainMenuButton = new Button(buttonStyle);
        mainMenuButton.setPosition(mainMenuX, 1100);
        menuGroup.addActor(mainMenuButton);
        ClickListener currentListener = mainMenuButton.getClickListener();
        
        
        Button muteButton = new Button(buttonStyle);
        muteButton.setPosition(muteButtonX, 1100);
        menuGroup.addActor(muteButton);
        ClickListener currentListener2 = muteButton.getClickListener();
        
        mainMenuButton.addListener(new ClickListener() { 
        	@Override
        	public void clicked(InputEvent event, float x, float y) {
            	showScreen(ScreenEnum.MAIN_MENU);
            	currentListener.clicked(event, x, y);
            }
        	@Override
 	        public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
        		muteButton.addAction(Actions.moveTo(muteButtonX, 915, .5f));
        		mainMenuButton.addAction(Actions.moveTo(mainMenuX, 915, .5f));
        		currentListener.enter(event, x, y, pointer, fromActor);
 			}
 			@Override
 	        public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
 				muteButton.addAction(Actions.moveTo(muteButtonX, 1100, .5f));
 				mainMenuButton.addAction(Actions.moveTo(mainMenuX, 1100, .5f));
 				currentListener.exit(event, x, y, pointer, toActor);
 			}
        });
        
        muteButton.addListener(new ClickListener() { 
        	@Override
        	public void clicked(InputEvent event, float x, float y) {
        		Preferences prefs = Gdx.app.getPreferences("tales-of-androgyny-preferences");
        		if (prefs.getBoolean("isMuted", false)) {
        			prefs.putFloat("volume", prefs.getFloat("cachedVolume", 1));
            		prefs.putFloat("musicVolume", prefs.getFloat("cachedMusicVolume", 1));
            		prefs.putBoolean("isMuted", false);
            		prefs.flush();
                	if (music != null && music.isPlaying()) {
                		music.setVolume(prefs.getFloat("musicVolume", 1) * .6f);
                	}
        		}
        		else {
        			prefs.putFloat("cachedVolume", prefs.getFloat("volume", 1));
            		prefs.putFloat("cachedMusicVolume", prefs.getFloat("musicVolume", 1));
        			prefs.putFloat("volume", 0);
            		prefs.putFloat("musicVolume", 0);
            		prefs.putBoolean("isMuted", true);
            		prefs.flush();
                	if (music != null && music.isPlaying()) {
                		music.setVolume(0);
                	}
        		}
        		
        		currentListener2.clicked(event, x, y);
            }
        	@Override
 	        public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
        		muteButton.addAction(Actions.moveTo(muteButtonX, 915, .5f));
        		mainMenuButton.addAction(Actions.moveTo(mainMenuX, 915, .5f));
        		currentListener2.enter(event, x, y, pointer, fromActor);
 			}
 			@Override
 	        public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
 				muteButton.addAction(Actions.moveTo(muteButtonX, 1100, .5f));
 				mainMenuButton.addAction(Actions.moveTo(mainMenuX, 1100, .5f));
 				currentListener2.exit(event, x, y, pointer, toActor);
 			}
        });      
        
        mainMenuHighlight.addAction(Actions.sequence(Actions.delay(.5f), new Action() {@Override public boolean act(float delta) { 
        	mainMenuHighlight.addListener(new ClickListener() { 
        	@Override
 	        public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
        		mainMenuButton.addAction(Actions.moveTo(mainMenuX, 915, .5f));
        		muteButton.addAction(Actions.moveTo(muteButtonX, 915, .5f));
 			}
 			@Override
 	        public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
 				mainMenuButton.addAction(Actions.moveTo(mainMenuX, 1100, .5f));
 				muteButton.addAction(Actions.moveTo(muteButtonX, 1100, .5f));
 			}
        }); return true; }}));   
    }
    
    protected void switchScreen(ScreenEnum screenRequest, AbstractScreen currentScreen, AssetEnum oldMusicPath, Music oldMusic) {
		AbstractScreen newScreen = screenFactory.getScreen(screenRequest);
		// Show new screen
    	newScreen.buildStage();
    	newScreen.buildMenu();    	          
    	switchMusic(oldMusicPath, oldMusic, newScreen.getMusicPath(), newScreen);
		game.setScreen(newScreen);	
		for (Actor actor : getActors()) { actor.clear(); }
    	// Dispose previous screen
    	if (currentScreen != null) {
            currentScreen.dispose();
        }
    }

	private Music getMusic() { return music; }
    private AssetEnum getMusicPath() { return musicPath; }
    
    protected void setVolume(float volume) {
    	if (music != null) music.setVolume(volume);
    }
    
    protected void switchMusic(AssetEnum newMusicPath) {
    	switchMusic(musicPath, music, newMusicPath, this);
    }
    
    private void switchMusic(AssetEnum oldMusicPath, Music oldMusic, AssetEnum newMusicPath, AbstractScreen screenToSet) {
    	if (newMusicPath == null || newMusicPath == oldMusicPath) {
    		screenToSet.musicPath = oldMusicPath;
    		screenToSet.music = oldMusic;
    		return;
    	}
    	if (oldMusic != null) {
    		oldMusic.stop();
    		//assetManager.unload(oldMusicPath.getMusic().fileName);
    	}
    	
    	screenToSet.musicPath = newMusicPath;
    	screenToSet.music = assetManager.get(newMusicPath.getMusic());
    	screenToSet.music.setVolume(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("musicVolume", 1) * .6f);
    	screenToSet.music.setLooping(true);
    	screenToSet.music.play();
    }
    
    public void clear() {
        // Clear screen
        Gdx.gl.glClearColor(clearRed, clearGreen, clearBlue, clearAlpha);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }
    
    protected void setClearColor(float clearRed, float clearGreen, float clearBlue, float clearAlpha) {
    	this.clearRed = clearRed;
    	this.clearGreen = clearGreen;
    	this.clearBlue = clearBlue;
    	this.clearAlpha = clearAlpha;
    }
    
    protected void addActorAndListen(Actor actor, int x, int y) {
		this.addActor(actor);
		actor.setPosition(x, y);
		addDragListener(actor);
	}
	
    protected Background getCampBackground() {
		Array<TextureRegion> frames = new Array<TextureRegion>();
		frames.add(new TextureRegion(assetManager.get(AssetEnum.CAMP_BG0.getTexture())));
		frames.add(new TextureRegion(assetManager.get(AssetEnum.CAMP_BG1.getTexture())));
		frames.add( new TextureRegion(assetManager.get(AssetEnum.CAMP_BG2.getTexture())));
		Animation animation = new Animation(.08f, frames);
		animation.setPlayMode(PlayMode.LOOP_PINGPONG);
		AnimatedImage animationActor = new AnimatedImage(animation, Scaling.fit, Align.right);
		return new BackgroundBuilder(animationActor).build();
	}
    
	private void addDragListener(final Actor actor) {
		actor.addListener(new DragListener() {
			@Override
		    public void drag(InputEvent event, float x, float y, int pointer) {
				if (debug) {
			        actor.moveBy(x - actor.getWidth() / 2, y - actor.getHeight() / 2);
			        System.out.println(actor.getX() + ", " + actor.getY());
				}
		    }
		});
	}
    
    @Override
    public void render(float delta) {
    	clear();
    	// this calls actor.act() on all actors
    	super.act(delta);
        // this draws all actors
        super.draw();
    }
 
    @Override
    public void show() {
        Gdx.input.setInputProcessor(this);
        font.setUseIntegerPositions(false);
    }
 
    @Override
    public void resize(int width, int height) {
        getViewport().update(width, height, false);
    }
 
    @Override public void hide() {
    	font.dispose();
    }
    @Override public void pause() {}
    @Override public void resume() {}
    
}
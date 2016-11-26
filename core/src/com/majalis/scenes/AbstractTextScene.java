package com.majalis.scenes;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.OrderedMap;
import com.majalis.asset.AssetEnum;
import com.majalis.encounter.Background;
import com.majalis.save.SaveEnum;
import com.majalis.save.SaveService;

public abstract class AbstractTextScene extends Scene {

	public static final ObjectMap<String, Class<?>> resourceRequirements = new ObjectMap<String, Class<?>>();
	static {
		resourceRequirements.put(AssetEnum.UI_SKIN.getPath(), Skin.class);
		resourceRequirements.put(AssetEnum.BUTTON_SOUND.getPath(), Sound.class);
		resourceRequirements.put(AssetEnum.GAME_TYPE_BACKGROUND.getPath(), Texture.class);
	}
	protected final SaveService saveService;
	private final BitmapFont font;
		
	protected AbstractTextScene(OrderedMap<Integer, Scene> sceneBranches, int sceneCode, SaveService saveService, BitmapFont font, Background background) {
		super(sceneBranches, sceneCode);
		this.saveService = saveService;
		this.font = font;
		this.addActor(background);
	}

	@Override
    public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		font.setColor(0, 0, 0, 1);
		font.draw(batch, getDisplay(), 430, 322, 1125, Align.center, true);
		font.draw(batch, "Press CTRL to skip", 105, 180, 240, Align.center, true);
    }
	
	protected abstract String getDisplay();
	
	@Override
	public void poke(){
		nextScene();
	}
	
	@Override
	public void setActive() {
		isActive = true;	
		this.removeAction(Actions.hide());
		this.addAction(Actions.visible(true));
		this.addAction(Actions.show());
		this.setBounds(0, 0, 2000, 2000);
		this.addListener(new ClickListener(){ 
			@Override
	        public void clicked(InputEvent event, float x, float y) {
				nextScene();
			}
		});
		saveService.saveDataValue(SaveEnum.SCENE_CODE, sceneCode);
	}
	protected abstract void nextScene();
}

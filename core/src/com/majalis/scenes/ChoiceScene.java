package com.majalis.scenes;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ObjectMap;
import com.majalis.save.SaveEnum;
import com.majalis.save.SaveManager;
import com.majalis.save.SaveService;

public class ChoiceScene extends Scene {

	public static final ObjectMap<String, Class<?>> resourceRequirements = new ObjectMap<String, Class<?>>();
	static {
		resourceRequirements.put("uiskin.json", Skin.class);
		resourceRequirements.put("sound.wav", Sound.class);
	}
	
	private final int sceneCode;
	private final SaveService saveService;
	private final BitmapFont font;
	private final Skin skin;
	private final Sound buttonSound;
	
	public ChoiceScene(ObjectMap<Integer, Scene> sceneBranches, int sceneCode, SaveService saveService, AssetManager assetManager, BitmapFont font) {
		super(sceneBranches);
		this.sceneCode = sceneCode;
		this.saveService = saveService;
		this.font = font;
		this.skin = assetManager.get("uiskin.json", Skin.class);
		this.buttonSound = assetManager.get("sound.wav", Sound.class);
		
		Table table = new Table();
		for (SaveManager.JobClass jobClass: SaveManager.JobClass.values()){
			TextButton button = new TextButton(jobClass.getLabel(), skin);
			button.addListener(getListener(jobClass));
			table.add(button).row();
		}
        table.setFillParent(true);
        table.addAction(Actions.moveTo(640, 400));
        this.addActor(table);
		
	}

	@Override
    public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		font.draw(batch, "Choose a class:", 600, 600);
    }
	
	public int getCode(){
		return sceneCode;
	}
	
	@Override
	public void setActive() {
		isActive = true;	
		this.removeAction(Actions.hide());
		this.addAction(Actions.visible(true));
		this.addAction(Actions.show());
		saveService.saveDataValue(SaveEnum.SCENE_CODE, sceneCode);
	}
	
	private ClickListener getListener(final SaveManager.JobClass selection){
		return new ClickListener(){
	        @Override
	        public void clicked(InputEvent event, float x, float y) {
	        	buttonSound.play();
	        	// set new Scene as active based on choice - this scene will save the appropriate class
	        	switch(selection){
	        		case WARRIOR:
	        			sceneBranches.get(1).setActive();
	        			break;
	        		case PALADIN:
	        			sceneBranches.get(2).setActive();
	        			break;
	        		case THIEF:
	        			sceneBranches.get(3).setActive();
	        			break;
	        		case RANGER:
	        			sceneBranches.get(4).setActive();
	        			break;
	        		case MAGE:
	        			sceneBranches.get(5).setActive();
	        			break;
	        		case ENCHANTRESS:
	        			sceneBranches.get(6).setActive();
	        			break;
	        		default: ;
	        	}
	        	isActive = false;
	        	addAction(Actions.hide());
	        }
	    };
	}
	
	
}

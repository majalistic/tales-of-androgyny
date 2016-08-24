package com.majalis.traprpg;

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

public class ChoiceScene extends Scene {

	public static final ObjectMap<String, Class<?>> resourceRequirements = new ObjectMap<String, Class<?>>();
	static {
		resourceRequirements.put("uiskin.json", Skin.class);
		resourceRequirements.put("sound.wav", Sound.class);
	}
	
	private final BitmapFont font;
	private final Skin skin;
	private final Sound buttonSound;
	
	protected ChoiceScene(ObjectMap<Integer, Scene> sceneBranches, AssetManager assetManager, BitmapFont font) {
		super(sceneBranches);
		this.font = font;
		this.skin = assetManager.get("uiskin.json", Skin.class);
		this.buttonSound = assetManager.get("sound.wav", Sound.class);
		
		Table table = new Table();
		for (GameWorldManager.ClassEnum jobClass: GameWorldManager.ClassEnum.values()){
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
	
	@Override
	protected void setActive() {
		isActive = true;	
		this.removeAction(Actions.hide());
		this.addAction(Actions.visible(true));
		this.addAction(Actions.show());
	}
	
	private ClickListener getListener(final GameWorldManager.ClassEnum selection){
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

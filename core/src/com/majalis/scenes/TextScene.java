package com.majalis.scenes;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.OrderedMap;
import com.majalis.encounter.Background;
import com.majalis.save.SaveEnum;
import com.majalis.save.SaveService;

public class TextScene extends Scene  {

	private final SaveService saveService;
	private final BitmapFont font;
	private final String toDisplay;
	private final Array<Mutation> mutations;

	public TextScene(OrderedMap<Integer, Scene> sceneBranches, int sceneCode, SaveService saveService, BitmapFont font, Background background, String toDisplay, Array<Mutation> mutations) {
		super(sceneBranches, sceneCode);
		this.saveService = saveService;
		this.font = font;
		this.toDisplay = toDisplay;
		this.mutations = mutations;
		this.addActor(background);
	}
	
	// this type of TextScene will be one that always pipes from one scene to the next with no branch - there will be another TextScene that actually has branching logic
	@Override
	public void poke(){
		nextScene();
	}
	
	@Override
    public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		font.setColor(0.2f,0.3f,0.8f,1);
		font.draw(batch, toDisplay, 600, 400);
    }
	
	// need to save the gamestate before applying the mutation to prevent repeated mutations
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
		for (Mutation mutator: mutations){
			mutator.mutate();
		}
		saveService.saveDataValue(SaveEnum.SCENE_CODE, sceneCode);
	}
	
	private void nextScene(){
		sceneBranches.get(sceneBranches.orderedKeys().get(0)).setActive();
		isActive = false;
		addAction(Actions.hide());
	}
}

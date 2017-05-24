package com.majalis.scenes;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.OrderedMap;
import com.majalis.asset.AssetEnum;
import com.majalis.encounter.Background;
import com.majalis.save.SaveService;
/*
 * Represents a choice displayed to the user in the course of an encounter.
 */
public class ChoiceScene extends AbstractChoiceScene {

	public static final Array<AssetDescriptor<?>> resourceRequirements = new Array<AssetDescriptor<?>>();
	static {
		resourceRequirements.add(AssetEnum.UI_SKIN.getSkin());
		resourceRequirements.add(AssetEnum.BUTTON_SOUND.getSound());
		resourceRequirements.add(AssetEnum.DEFAULT_BACKGROUND.getTexture());
	}
	
	private final BitmapFont font;
	private final String choiceDialogue;
	// this should receive a map of integers to choice buttons 
	public ChoiceScene(OrderedMap<Integer, Scene> sceneBranches, int sceneCode, SaveService saveService, BitmapFont font, String choiceDialogue, Table table, Background background) {
		super(sceneBranches, sceneCode, saveService);
		this.font = font;
		this.addActor(background);
		
        table.setPosition(960, 825);
        table.align(Align.top);
        // may need to add the background as an actor
        this.addActor(table);
        this.choiceDialogue = choiceDialogue;
		
	}

	@Override
    public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		font.setColor(0.5f,0.4f,0,1);
		font.draw(batch, choiceDialogue, 600, 900, 620, Align.center, true);
    }
	
	public int getCode(){
		return sceneCode;
	}
}

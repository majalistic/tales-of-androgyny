package com.majalis.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.majalis.asset.AssetEnum;
import com.majalis.character.PlayerCharacter;
import com.majalis.character.PlayerCharacter.QuestType;
/*
 * Screen for displaying the quest log.
 */
public class QuestScreen extends AbstractScreen {

	public static final Array<AssetDescriptor<?>> resourceRequirements = new Array<AssetDescriptor<?>>();
	static {
		resourceRequirements.add(AssetEnum.UI_SKIN.getSkin());
	}
	private final PlayerCharacter character;
	
	public QuestScreen(ScreenFactory factory, ScreenElements elements, PlayerCharacter character) {
		super(factory, elements, null);
		this.character = character;
		setClearColor(Color.FOREST.r, Color.FOREST.g, Color.FOREST.b, 1);
	}

	@Override
	public void buildStage() {
		Skin skin = assetManager.get(AssetEnum.UI_SKIN.getSkin());
		Table questTable = new Table();
		questTable.setPosition(100, 1050);
		questTable.align(Align.topLeft);
		this.addActor(questTable);

		final TextButton backButton = new TextButton ("Back", skin);
		backButton.setPosition(1500, 50);
		this.addActor(backButton);
		backButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				showScreen(ScreenEnum.LOAD_GAME);
			}
		});
		
		boolean nothingToDisplay = true;
		
		// for each save slot				
		for (QuestType questType : QuestType.values()) {
			int questValue = character.getQuestStatus(questType);
			if (questValue == 0 || questType.getQuestDescription(questValue).equals("")) continue;
			nothingToDisplay = false;
			final Label questDisplay = new Label(questType.getQuestDescription(questValue), skin);			
			questDisplay.setColor(Color.BLACK);
			questTable.add(questDisplay).align(Align.left).width(800).row();
		}
		questTable.row();
		questTable.row();
		Label eventLog = new Label("Event log:", skin);
		eventLog.setColor(Color.BLACK);
		questTable.add(eventLog).align(Align.left).width(800).row();
		
		for (String event : character.getEventLog()) {
			nothingToDisplay = false;
			final Label eventDisplay = new Label(event, skin);			
			eventDisplay.setColor(Color.BLACK);
			questTable.add(eventDisplay).align(Align.left).padLeft(50).width(800).row();
		}
		
		if (nothingToDisplay) questTable.add(new Label("No quest records to display yet.", skin)).align(Align.left).width(800);		
	}

	@Override
	public void render(float delta) {
		super.render(delta);
		if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
			showScreen(ScreenEnum.LOAD_GAME);
		}
	}
}
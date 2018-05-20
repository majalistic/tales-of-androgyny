package com.majalis.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.majalis.asset.AssetEnum;
import com.majalis.character.PlayerCharacter.QuestFlag;
import com.majalis.character.PlayerCharacter.QuestType;
import com.majalis.encounter.Background;
import com.majalis.encounter.Background.BackgroundBuilder;
import com.majalis.encounter.EncounterBuilder.Branch;
import com.majalis.encounter.EncounterCode;
import com.majalis.save.MutationResult;
import com.majalis.save.SaveEnum;
import com.majalis.save.SaveManager;
import com.majalis.save.SaveService;

public class TownScreen extends AbstractScreen {

	private static final Array<AssetDescriptor<?>> resourceRequirements = new Array<AssetDescriptor<?>>();
	private static Array<AssetDescriptor<?>> requirementsToDispose = new Array<AssetDescriptor<?>>();
	static {
		resourceRequirements.add(AssetEnum.UI_SKIN.getSkin());
		resourceRequirements.add(AssetEnum.TOWN_BG.getTexture());
		resourceRequirements.add(AssetEnum.STANCE_ARROW.getTexture());
		resourceRequirements.add(AssetEnum.DEFAULT_BACKGROUND.getTexture());
		resourceRequirements.add(AssetEnum.BATTLE_HOVER.getTexture());
		resourceRequirements.add(AssetEnum.BATTLE_TEXTBOX.getTexture());
		resourceRequirements.add(AssetEnum.TEXT_BOX.getTexture());
		resourceRequirements.add(AssetEnum.SHOPKEEP.getTexture());
		resourceRequirements.add(AssetEnum.TRAINER.getTexture());
		resourceRequirements.add(AssetEnum.SHOP_MUSIC.getMusic());
		resourceRequirements.add(AssetEnum.BUTTON_SOUND.getSound());
		resourceRequirements.add(AssetEnum.EQUIP.getSound());
		resourceRequirements.add(AssetEnum.ENCOUNTER_MUSIC.getMusic());
	}
	private final SaveService saveService;
	private final Skin skin;
	private final Background background;
	private final Image arrow;
	private final Sound buttonSound;
	private final Array<TextButton> buttons;
	private final TownCode townCode;
	private final Label console;
	private int time;
	private int selection;
	
	protected TownScreen(ScreenFactory screenFactory, ScreenElements elements, SaveService saveService, int time, TownCode townCode) {
		super(screenFactory, elements, AssetEnum.SHOP_MUSIC);
		this.saveService = saveService;
		this.townCode = townCode;
		this.time = time;
		skin = assetManager.get(AssetEnum.UI_SKIN.getSkin());
		background = new BackgroundBuilder(assetManager.get(AssetEnum.TOWN_BG.getTexture()), true).build();
		background.setColor(getTimeColor(time));
		arrow = new Image(assetManager.get(AssetEnum.STANCE_ARROW.getTexture()));
		buttonSound = assetManager.get(AssetEnum.BUTTON_SOUND.getSound());
		
		buttons = new Array<TextButton>();
		selection = 0;
		console = new Label("", skin);
	}
	
	@Override
	public void buildStage() {
		Table table = new Table();
		table.align(Align.topLeft);
        table.setPosition(1200, 860);
		if (townCode != TownCode.TOWN_MONSTER) {
			buttons.add(getButton("General Store", skin, table, getListener(EncounterCode.SHOP)));
			buttons.add(getButton("Blacksmith", skin, table, getListener(EncounterCode.WEAPON_SHOP)));
			buttons.add(getButton("Inn", skin, table, getListener(EncounterCode.INN)));
			buttons.add(getButton("Bank", skin, table, getListener(EncounterCode.BANK)));
			buttons.add(getButton("Brothel", skin, table, getListener(EncounterCode.BROTHEL)));
			if (townCode != TownCode.TOWN_STORY) {
				buttons.add(getButton("Carriages", skin, table, getListener(EncounterCode.CARRIAGE)));
				buttons.add(getButton("Town Square", skin, table, getListener(EncounterCode.TOWN_CRIER)));
			}
		}
		else {
			buttons.add(getButton("Inn", skin, table, getListener(EncounterCode.INN_MONSTER)));
			buttons.add(getButton("General Store", skin, table, getListener(EncounterCode.SHOP_MONSTER)));
			buttons.add(getButton("Carriages", skin, table, getListener(EncounterCode.CARRIAGE_MONSTER)));
			buttons.add(getButton("Tavern", skin, table, getListener(EncounterCode.TAVERN)));
			buttons.add(getButton("Manor", skin, table, getListener(EncounterCode.WARLOCK)));
		}
		buttons.add(getButton("Rest", skin, table, new ClickListener() {
	        @Override
	        public void clicked(InputEvent event, float x, float y) {
	        	passTime(1);
	        }
	    }));
		buttons.add(getButton("Depart", skin, table,new ClickListener() {
	        @Override
	        public void clicked(InputEvent event, float x, float y) {
	        	buttonSound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
	        	saveService.saveDataValue(SaveEnum.CONTEXT, SaveManager.GameContext.WORLD_MAP);
	        	showScreen(ScreenEnum.CONTINUE);    
	        }
	    }));
		
		int ii = 0;
		for (TextButton button : buttons) {
			button.addListener(getListener(ii++));
		}
		
        this.addActor(background);
        this.addActor(table);
        this.addActor(arrow);
        
        arrow.setSize(45, 75);
        arrow.addAction(Actions.sequence(Actions.delay(.01f), new Action(){
			@Override
			public boolean act(float delta) {
				setArrowPosition();
				return true;
			} }));
        
        this.addActor(console);
		console.setPosition(900, 150);
		console.setAlignment(Align.top);
		saveService.saveDataValue(SaveEnum.QUEST, new QuestFlag(townCode == TownCode.TOWN_MONSTER ? QuestType.MONSTER_TOWN : QuestType.HUMAN_TOWN, 1));
	}
	
	private void passTime(int timePass) {
		buttonSound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
		time += timePass;
		Array<MutationResult> temp = saveService.saveDataValue(SaveEnum.HEALTH, 10 * timePass);
		Array<MutationResult> results = saveService.saveDataValue(SaveEnum.TIME, timePass);
		results.addAll(temp);
		console.setText(getResults(results));
		background.setColor(getTimeColor(time));
	}
	
	private String getResults(Array<MutationResult> results) {
		String result = "";
		for (MutationResult mr : results) {
			result += mr.getText() + "\n";
		}
		return result.trim();
	}
	
	@Override
	public void render(float delta) {
		super.render(delta);
		OrthographicCamera camera = (OrthographicCamera) getCamera();
        batch.setTransformMatrix(camera.view);
        
        if(Gdx.input.isKeyJustPressed(Keys.UP)) {
        	if (selection > 0) selection--;
        	else selection = buttons.size-1;
        	setArrowPosition();
        }
        else if(Gdx.input.isKeyJustPressed(Keys.DOWN)) {
        	if (selection < buttons.size- 1) selection++;
        	else selection = 0;
        	setArrowPosition();
        }
        else if(Gdx.input.isKeyJustPressed(Keys.ENTER)) {
        	InputEvent event1 = new InputEvent();
            event1.setType(InputEvent.Type.touchDown);
            buttons.get(selection).fire(event1);

            InputEvent event2 = new InputEvent();
            event2.setType(InputEvent.Type.touchUp);
            buttons.get(selection).fire(event2);
        }
        else if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
			showScreen(ScreenEnum.MAIN_MENU);
		}
        
		camera.update();
		batch.setProjectionMatrix(camera.combined);
	}
	
	private void setArrowPosition() {
		Vector2 buttonPosition = buttons.get(selection).localToStageCoordinates(new Vector2(0,0));
		arrow.setPosition(buttonPosition.x-43, buttonPosition.y-8);
	}
	
	private Color getTimeColor(int time) { return TimeOfDay.getTime(time).getColor(); }
	
	private TextButton getButton(String label, Skin skin, Table table, ClickListener listener) {
		TextButton newButton = new TextButton(label, skin);
		newButton.addListener(listener);
		table.add(newButton).size(300, 60).row();
		return newButton;
	}
	
	private ClickListener getListener(final EncounterCode code) {
		return new ClickListener() {
	        @Override
	        public void clicked(InputEvent event, float x, float y) {
	        	buttonSound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
	        	saveService.saveDataValue(SaveEnum.CONTEXT, SaveManager.GameContext.ENCOUNTER);
	        	saveService.saveDataValue(SaveEnum.RETURN_CONTEXT, SaveManager.GameContext.TOWN);
	        	saveService.saveDataValue(SaveEnum.ENCOUNTER_CODE, code);
	        	showScreen(ScreenEnum.CONTINUE);    
	        }
	    };
	}
	
	private ClickListener getListener(final int index) {
		return new ClickListener() {
	        @Override
	        public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
				selection = index;
				setArrowPosition();
	        }
	    };
	}

	@Override
	public void dispose() {
		for (AssetDescriptor<?> path : requirementsToDispose) {
			if (path.fileName.equals(AssetEnum.BUTTON_SOUND.getSound().fileName) || path.type == Music.class)
				continue;
			assetManager.unload(path.fileName);
		}
		requirementsToDispose = new Array<AssetDescriptor<?>>();
		super.dispose();
	}
	
	public static Array<AssetDescriptor<?>> getRequirements(Branch encounter) {
		Array<AssetDescriptor<?>> requirements = new Array<AssetDescriptor<?>>(resourceRequirements);
		requirements.addAll(encounter.getRequirements());
		requirementsToDispose = requirements;
		return requirements;
	}
	
	public enum TownCode {
		TOWN,
		TOWN_STORY,
		TOWN_MONSTER
	}
}

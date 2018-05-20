package com.majalis.screens;

import static com.majalis.asset.AssetEnum.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
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
import com.majalis.encounter.Background;
import com.majalis.encounter.Background.BackgroundBuilder;
import com.majalis.encounter.EncounterBuilder.Branch;
import com.majalis.encounter.EncounterCode;
import com.majalis.save.MutationResult;
import com.majalis.save.SaveEnum;
import com.majalis.save.SaveManager;
import com.majalis.save.SaveService;

public class CampScreen extends AbstractScreen {
	private static final Array<AssetDescriptor<?>> resourceRequirements = new Array<AssetDescriptor<?>>();
	private static Array<AssetDescriptor<?>> requirementsToDispose = new Array<AssetDescriptor<?>>();

	static {
		resourceRequirements.add(AssetEnum.UI_SKIN.getSkin());
		resourceRequirements.add(AssetEnum.SHOP_MUSIC.getMusic());
		resourceRequirements.add(AssetEnum.BUTTON_SOUND.getSound());
		
		AssetEnum[] assets = new AssetEnum[]{
			PORTRAIT_NEUTRAL, PORTRAIT_AHEGAO, PORTRAIT_FELLATIO, PORTRAIT_MOUTHBOMB, PORTRAIT_GRIN, PORTRAIT_HIT, PORTRAIT_LOVE, PORTRAIT_LUST, PORTRAIT_SMILE, PORTRAIT_SURPRISE, PORTRAIT_GRIMACE, PORTRAIT_POUT, PORTRAIT_HAPPY, 
			PORTRAIT_NEUTRAL_FEMME, PORTRAIT_AHEGAO_FEMME, PORTRAIT_FELLATIO_FEMME, PORTRAIT_MOUTHBOMB_FEMME, PORTRAIT_GRIN_FEMME, PORTRAIT_HIT_FEMME, PORTRAIT_LOVE_FEMME, PORTRAIT_LUST_FEMME, PORTRAIT_SMILE_FEMME, PORTRAIT_SURPRISE_FEMME, PORTRAIT_GRIMACE_FEMME, PORTRAIT_POUT_FEMME, PORTRAIT_HAPPY_FEMME,
			PLAINS_BG, APPLE, WORLD_MAP_UI, STANCE_ARROW
		};
		for (AssetEnum asset: assets) {
			resourceRequirements.add(asset.getTexture());
		}
	}
	private final SaveService saveService;
	private final PlayerCharacter character;
	private final Skin skin;
	private final Background background;
	private final Image arrow;
	private final Sound buttonSound;
	private final Array<TextButton> buttons;
	private final Texture characterUITexture;
	private final Texture food;
	private final Label console;
	private int selection;
	private int time;
	private TextButton forageButton;
	private TextButton departButton;
	
	protected CampScreen(ScreenFactory screenFactory, ScreenElements elements, SaveService saveService, PlayerCharacter character, int time) {
		super(screenFactory, elements, AssetEnum.SHOP_MUSIC);
		this.saveService = saveService;
		this.character = character;
		skin = assetManager.get(AssetEnum.UI_SKIN.getSkin());
		background = new BackgroundBuilder(assetManager.get(AssetEnum.PLAINS_BG.getTexture()), true).build();
		this.time = time;
		
		food = assetManager.get(AssetEnum.APPLE.getTexture());
		
		characterUITexture = assetManager.get(AssetEnum.WORLD_MAP_UI.getTexture());
		
		background.setColor(getTimeColor(time));
		arrow = new Image(assetManager.get(AssetEnum.STANCE_ARROW.getTexture()));
		buttonSound = assetManager.get(AssetEnum.BUTTON_SOUND.getSound());
		
		buttons = new Array<TextButton>();
		selection = 0;
		console = new Label("", skin);
	}

	private Color getTimeColor(int time) { return TimeOfDay.getTime(time).getColor(); }
	private String getTime() { return TimeOfDay.getTime(time).getDisplay(); }
	
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
	
	private void goToEncounter(EncounterCode encounter) {
		buttonSound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
		saveService.saveDataValue(SaveEnum.ENCOUNTER_CODE, encounter);
		saveService.saveDataValue(SaveEnum.CONTEXT, SaveManager.GameContext.ENCOUNTER);
		saveService.saveDataValue(SaveEnum.RETURN_CONTEXT, SaveManager.GameContext.CAMP);
    	showScreen(ScreenEnum.CONTINUE);    
	}
	
	private void checkForage() {
		if (character.getCurrentHealth() > 0) {
			forageButton.setTouchable(Touchable.enabled);
			forageButton.setColor(Color.WHITE);
			departButton.setTouchable(Touchable.enabled);
			departButton.setColor(Color.WHITE);
		}
		else {
			if (character.getFood() == 0) {
				goToEncounter(EncounterCode.STARVATION);
			}
			else {
				forageButton.setTouchable(Touchable.disabled);
				forageButton.setColor(Color.GRAY);
				departButton.setTouchable(Touchable.disabled);
				departButton.setColor(Color.GRAY);
			}
		}
	}
	
	@Override
	public void buildStage() {
		Table table = new Table();
		table.align(Align.bottomLeft);
        table.setPosition(1200, 595);
		
		Array<String> buttonLabels = new Array<String>();
		buttonLabels.addAll("Rest", "Sleep (morning)", "Sleep (night)", "Forage");
		
		boolean elf = character.getQuestStatus(QuestType.ELF) == 5 || character.getQuestStatus(QuestType.ELF) == 9 || character.getQuestStatus(QuestType.ELF) == 10 || character.getQuestStatus(QuestType.ELF) == 11;
		boolean trudy = character.getQuestStatus(QuestType.TRUDY) == 5 || character.getQuestStatus(QuestType.TRUDY) == 6 || character.getQuestStatus(QuestType.TRUDY) == 7;
		boolean lewd = character.getLustDegradation() >= 2;
		if (elf) buttonLabels.add("Chat (Kylira)");
		if (trudy) buttonLabels.add("Chat (Trudy)");
		if (lewd) buttonLabels.add("Masturbate");
		buttonLabels.add("Depart");
		
		for (int ii = 0; ii < buttonLabels.size; ii++) {
			buttons.add(new TextButton(buttonLabels.get(ii), skin));
			buttons.get(ii).addListener(getListener(ii));
			table.add(buttons.get(ii)).size(300, 60).row();
		}
		
		buttons.get(0).addListener(new ClickListener() { @Override public void clicked(InputEvent event, float x, float y) { passTime(1); } } );
		buttons.get(1).addListener(new ClickListener() { @Override public void clicked(InputEvent event, float x, float y) { passTime(TimeOfDay.timeTillNext(TimeOfDay.DAWN, time)); } } );
		buttons.get(2).addListener(new ClickListener() { @Override public void clicked(InputEvent event, float x, float y) { passTime(TimeOfDay.timeTillNext(TimeOfDay.DUSK, time)); } } );
		forageButton = buttons.get(3);
		forageButton.addListener(new ClickListener() { @Override public void clicked(InputEvent event, float x, float y) { goToEncounter(EncounterCode.FORAGE); }	} );
		int nextButtonIndex = 4;
		if (elf) {
			buttons.get(nextButtonIndex++).addListener(new ClickListener() { @Override public void clicked(InputEvent event, float x, float y) { goToEncounter(EncounterCode.ELF_COMPANION); }	} );
		}
		if (trudy) {
			buttons.get(nextButtonIndex++).addListener(new ClickListener() { @Override public void clicked(InputEvent event, float x, float y) { goToEncounter(EncounterCode.TRUDY_COMPANION); }	} );
		}
		if (lewd) {
			buttons.get(nextButtonIndex++).addListener(new ClickListener() { @Override public void clicked(InputEvent event, float x, float y) { goToEncounter(EncounterCode.CAMP_MASTURBATE); }	} );
		}
		
		departButton = buttons.get(nextButtonIndex);		
		
		departButton.addListener(new ClickListener() {
	        @Override
	        public void clicked(InputEvent event, float x, float y) {
	        	buttonSound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
	        	saveService.saveDataValue(SaveEnum.CONTEXT, SaveManager.GameContext.WORLD_MAP);
	        	showScreen(ScreenEnum.CONTINUE);    
	        }
	    });
		
        this.addActor(background);
        
        Image characterUI = new Image(characterUITexture);
		this.addActor(characterUI);
		characterUI.setScale(1.1f);
        
		Texture portrait = assetManager.get(character.getPortraitPath());
        Image characterPortrait = new Image(portrait);
		characterPortrait.setBounds(-1, 27, portrait.getWidth() / (portrait.getHeight() / 200f), 200);
        this.addActor(characterPortrait);
		
        Image foodIcon = new Image(food);
		foodIcon.setSize(75, 75);
		this.addActor(foodIcon);
        
        this.addActor(table);
        this.addActor(arrow);
        
        arrow.setSize(45, 75);
        setArrowPosition();
        arrow.setPosition(arrow.getX(), arrow.getY() + 60 * (buttons.size-1));
        
		this.addActor(console);
		console.setPosition(900, 150);
		console.setAlignment(Align.top);
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
        
        drawText();
		camera.update();
		batch.setProjectionMatrix(camera.combined);
        checkForage();
	}
	
	// this should be replaced with label actors
	private void drawText() {
		batch.begin();
		OrthographicCamera camera = (OrthographicCamera) getCamera();
		batch.setColor(1.0f, 1.0f, 1.0f, 1);
		font.draw(batch, String.valueOf(character.getCurrentHealth()), camera.position.x + 310.2f, camera.position.y + 139.7f);
		font.draw(batch, "Day: " + (time / 6 + 1), camera.position.x + 350, camera.position.y + 150);
		font.draw(batch, getTime(), camera.position.x + 370, camera.position.y + 125);
		font.draw(batch, "X " + character.getFood(), camera.position.x + 23, camera.position.y + 25);
		batch.end();
	}
	
	private void setArrowPosition() {
		Vector2 buttonPosition = buttons.get(selection).localToStageCoordinates(new Vector2(0,0));
		arrow.setPosition(buttonPosition.x-43, buttonPosition.y-8);
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
}

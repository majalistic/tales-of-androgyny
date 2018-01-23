package com.majalis.screens;

import static com.majalis.asset.AssetEnum.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.majalis.asset.AssetEnum;
import com.majalis.character.PlayerCharacter;
import com.majalis.encounter.Encounter;
import com.majalis.encounter.EncounterBuilder.Branch;
import com.majalis.save.LoadService;
import com.majalis.save.SaveEnum;
/*
 *Screen for displaying Encounters.  UI that Handles player input while in an encounter.
 */
public class EncounterScreen extends AbstractScreen {
	private static final Array<AssetDescriptor<?>> resourceRequirements = new Array<AssetDescriptor<?>>();
	private static Array<AssetDescriptor<?>> requirementsToDispose = new Array<AssetDescriptor<?>>();
	static {
		resourceRequirements.add(AssetEnum.UI_SKIN.getSkin());
		resourceRequirements.add(AssetEnum.BUTTON_SOUND.getSound());
		AssetEnum[] assets = new AssetEnum[] { NULL, DEFAULT_BACKGROUND, BATTLE_HOVER, STANCE_ARROW, PORTRAIT_NEUTRAL, PORTRAIT_AHEGAO,
				PORTRAIT_FELLATIO, PORTRAIT_MOUTHBOMB, PORTRAIT_GRIN, PORTRAIT_HIT, PORTRAIT_LOVE, PORTRAIT_LUST,
				PORTRAIT_SMILE, PORTRAIT_SURPRISE, PORTRAIT_GRIMACE, PORTRAIT_POUT, PORTRAIT_HAPPY, MARS_ICON_0,
				MARS_ICON_1, MARS_ICON_2, MARS_ICON_3, MARS_ICON_4, STUFFED_BELLY, FULL_BELLY, BIG_BELLY, FLAT_BELLY,
				TRAP_BONUS, APPLE, EXP, GOLD, TIME, HEART};
		for (AssetEnum asset : assets) {
			resourceRequirements.add(asset.getTexture());
		}

		resourceRequirements.add(AssetEnum.ENCOUNTER_MUSIC.getMusic());
	}
	private final LoadService loadService;
	private final Encounter encounter;
	private final TextButton saveButton;
	private final TextButton skipButton;
	private final TextButton autoplayButton;
	private final Label dateLabel;
	private final Skin skin;
	private boolean skipHeld;
	
	protected EncounterScreen(ScreenFactory screenFactory, ScreenElements elements, LoadService loadService, Encounter encounter) {
		super(screenFactory, elements, null);
		this.loadService = loadService;
		this.encounter = encounter;
		PlayerCharacter character = loadService.loadDataValue(SaveEnum.PLAYER, PlayerCharacter.class);
		skin = assetManager.get(AssetEnum.UI_SKIN.getSkin());
		saveButton = new TextButton("Save", skin);
		skipButton = new TextButton("Skip", skin);
		autoplayButton = new TextButton("Auto", skin);
		dateLabel = new DateLabel(character);
		if (Gdx.app.getPreferences("tales-of-androgyny-preferences").getBoolean("autoplay", false)) autoplayButton.setColor(Color.YELLOW);
	}

	@Override
	public void buildStage() {
		for (Actor actor : encounter.getActors()) {
			this.addActor(actor);
		}
		
		final Sound buttonSound = assetManager.get(AssetEnum.BUTTON_SOUND.getSound());
		
		dateLabel.setPosition(1650, 325);
		this.addActor(dateLabel);
		
		saveButton.setPosition(1650, 100);
		saveButton.setWidth(150);
		saveButton.addListener(
			new ClickListener() {
				@Override
		        public void clicked(InputEvent event, float x, float y) {
					buttonSound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
					showScreen(ScreenEnum.SAVE);
		        }
			}
		);	
		
		this.addActor(saveButton);
		
		skipButton.setPosition(1650, 175);
		skipButton.setWidth(150);
		skipButton.addListener(
			new ClickListener() {
				@Override
		        public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
					skipHeld = false;	
		        }
				
				@Override
		        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
					skipHeld = true;
					return super.touchDown(event, x, y, pointer, button);				
		        }
			}
		);	
		
		this.addActor(skipButton);
		
		autoplayButton.setPosition(1650, 250);
		autoplayButton.setWidth(150);
		autoplayButton.addListener(
			new ClickListener() {
				@Override
		        public void clicked(InputEvent event, float x, float y) {
					Gdx.app.getPreferences("tales-of-androgyny-preferences").putBoolean("autoplay", !Gdx.app.getPreferences("tales-of-androgyny-preferences").getBoolean("autoplay", false));
					if (Gdx.app.getPreferences("tales-of-androgyny-preferences").getBoolean("autoplay", false)) autoplayButton.setColor(Color.YELLOW);
					else autoplayButton.setColor(Color.WHITE);
		        }
			}
		);	
		
		this.addActor(autoplayButton);
	}

	@Override
	public void render(float delta) {
		super.render(delta);
		encounter.gameLoop();
		switchMusic((AssetEnum)loadService.loadDataValue(SaveEnum.MUSIC, AssetEnum.class));
		if (skipHeld) {
			encounter.poke();
		}
		
		if (encounter.showSave) {
			saveButton.addAction(Actions.show());
			skipButton.addAction(Actions.show());
			autoplayButton.addAction(Actions.show());
			dateLabel.addAction(Actions.show());
		}
		else {
			skipHeld = false;
			saveButton.addAction(Actions.hide());
			skipButton.addAction(Actions.hide());
			autoplayButton.addAction(Actions.hide());
			dateLabel.addAction(Actions.hide());
		}
		if (encounter.isSwitching()) {
			showScreen(ScreenEnum.CONTINUE);
		} else if (encounter.gameExit) {
			showScreen(ScreenEnum.MAIN_MENU);
		} else {
			draw();
		}
	}

	public void draw() {
		batch.begin();
		OrthographicCamera camera = (OrthographicCamera) getCamera();
		batch.setTransformMatrix(camera.view);
		batch.setProjectionMatrix(camera.combined);
		camera.update();
		batch.end();
	}

	@Override
	public void dispose() {
		for (AssetDescriptor<?> path : requirementsToDispose) {
			if (path.fileName.equals(AssetEnum.BUTTON_SOUND.getSound().fileName) || path.type == Music.class)
				continue;
			assetManager.unload(path.fileName);
		}
		requirementsToDispose = new Array<AssetDescriptor<?>>();
	}
	
	public static Array<AssetDescriptor<?>> getRequirements(Branch encounter) {
		Array<AssetDescriptor<?>> requirements = new Array<AssetDescriptor<?>>(resourceRequirements);
		requirements.addAll(encounter.getRequirements());
		requirementsToDispose = requirements;
		return requirements;
	}

	private class DateLabel extends Label {
		private final PlayerCharacter character;
		private DateLabel(PlayerCharacter character) {
			super("Day: " + (character.getTime() / 6 + 1) + "\n" + TimeOfDay.getTime(character.getTime()).getDisplay(), skin);
			this.character = character;
		}
		@Override
		public void draw(Batch batch, float parentAlpha) {
			setText("Day: " + (character.getTime() / 6 + 1) + "\n" + TimeOfDay.getTime(character.getTime()).getDisplay());
			setColor(TimeOfDay.getTime(character.getTime()).getColor());
			super.draw(batch, parentAlpha);
		}
	}
}

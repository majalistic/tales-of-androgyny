package com.majalis.screens;

import static com.majalis.asset.AssetEnum.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.majalis.asset.AssetEnum;
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
		AssetEnum[] assets = new AssetEnum[] { 
			NULL, DEFAULT_BACKGROUND, BASIC_BOX, BATTLE_HOVER, STANCE_ARROW, LEVEL_UP_SKIN, PORTRAIT_NEUTRAL, PORTRAIT_AHEGAO, 
			PORTRAIT_FELLATIO, PORTRAIT_MOUTHBOMB, PORTRAIT_GRIN, PORTRAIT_HIT, PORTRAIT_LOVE, PORTRAIT_LUST,
			PORTRAIT_SMILE, PORTRAIT_SURPRISE, PORTRAIT_GRIMACE, PORTRAIT_POUT, PORTRAIT_HAPPY, 
			PORTRAIT_NEUTRAL_FEMME, PORTRAIT_AHEGAO_FEMME, PORTRAIT_FELLATIO_FEMME, PORTRAIT_MOUTHBOMB_FEMME, PORTRAIT_GRIN_FEMME, PORTRAIT_HIT_FEMME, PORTRAIT_LOVE_FEMME, PORTRAIT_LUST_FEMME, PORTRAIT_SMILE_FEMME, PORTRAIT_SURPRISE_FEMME, PORTRAIT_GRIMACE_FEMME, PORTRAIT_POUT_FEMME, PORTRAIT_HAPPY_FEMME, 
			MARS_ICON_0, MARS_ICON_1, MARS_ICON_2, MARS_ICON_3, MARS_ICON_4,
			TRAP_BONUS, APPLE, EXP, GOLD, TIME, HEART, CRYSTAL, SKILL_POINTS, PERK_POINTS, HEALTH_ICON_0, HEALTH_ICON_1, HEALTH_ICON_2, HEALTH_ICON_3
		};
		for (AssetEnum asset : assets) {
			resourceRequirements.add(asset.getTexture());
		}
		resourceRequirements.add(AssetEnum.BELLY_ANIMATION.getAnimation());
		resourceRequirements.add(AssetEnum.ENCOUNTER_MUSIC.getMusic());
	}
	private final LoadService loadService;
	private final Encounter encounter;
	
	protected EncounterScreen(ScreenFactory screenFactory, ScreenElements elements, LoadService loadService, Encounter encounter) {
		super(screenFactory, elements, null);
		this.loadService = loadService;
		this.encounter = encounter;
		encounter.addSaveListener(
			new ClickListener() {
				@Override
		        public void clicked(InputEvent event, float x, float y) {
					assetManager.get(AssetEnum.BUTTON_SOUND.getSound()).play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
					showScreen(ScreenEnum.SAVE);
		        }
			}
		);
	}

	@Override
	public void buildStage() {
		this.addActor(encounter.getSceneGroup());
	}

	@Override
	public void render(float delta) {
		super.render(delta);
		encounter.gameLoop();
		switchMusic((AssetEnum)loadService.loadDataValue(SaveEnum.MUSIC, AssetEnum.class));
		if (encounter.isSwitching()) {
			showScreen(ScreenEnum.CONTINUE);
		} 
		else if (encounter.gameExit) {
			showScreen(ScreenEnum.MAIN_MENU);
		}
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
}

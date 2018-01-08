package com.majalis.screens;

import static com.majalis.asset.AssetEnum.*;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.utils.Array;
import com.majalis.asset.AssetEnum;
import com.majalis.battle.Battle;
import com.majalis.battle.BattleAttributes;
import com.majalis.save.SaveEnum;
import com.majalis.save.SaveManager;
import com.majalis.save.SaveService;
/*
 * Screen for displaying battles.
 */
public class BattleScreen extends AbstractScreen{

	public static final Array<AssetDescriptor<?>> resourceRequirements = new Array<AssetDescriptor<?>>();
	public static Array<AssetDescriptor<?>> requirementsToDispose = new Array<AssetDescriptor<?>>();
	static {
		resourceRequirements.add(BATTLE_SKIN.getSkin());
		resourceRequirements.add(BATTLE_MUSIC.getMusic());
		AssetEnum[] soundAssets = new AssetEnum[]{
			BUTTON_SOUND, UNPLUGGED_POP, MOUTH_POP, ATTACK_SOUND, SWORD_SLASH_SOUND, FIREBALL_SOUND, INCANTATION, HIT_SOUND, THWAPPING, PARRY_SOUND, BLOCK_SOUND
		};
		for (AssetEnum asset: soundAssets) {
			resourceRequirements.add(asset.getSound());
		}
		
		// need to refactor to get all stance textures
		AssetEnum[] assets = new AssetEnum[]{
			NULL, SLASH, BATTLE_HOVER, BATTLE_TEXTBOX, BATTLE_UI, BLEED, ARMOR_0, ARMOR_1,	
			NULL_STANCE, ANAL, BLITZ, BALANCED, CASTING, COUNTER, DEFENSIVE, SEDUCTION, DOGGY, ERUPT, FELLATIO, FULL_NELSON, GROUND_WRESTLE, KNEELING, HANDS_AND_KNEES, HANDY, ITEM, COWGIRL, OFFENSIVE, OUROBOROS, FACEFUCK, PRONE, SUPINE, STANDING, AIRBORNE, FACE_SITTING, SIXTY_NINE, KNOTTED, SPREAD, PENETRATED, PRONEBONE, REVERSE_COWGIRL, WRAPPED,
			PORTRAIT_NEUTRAL, PORTRAIT_AHEGAO, PORTRAIT_FELLATIO, PORTRAIT_MOUTHBOMB, PORTRAIT_GRIN, PORTRAIT_HIT, PORTRAIT_LOVE, PORTRAIT_LUST, PORTRAIT_SMILE, PORTRAIT_SURPRISE, PORTRAIT_GRIMACE, PORTRAIT_POUT, PORTRAIT_HAPPY, 
			HEALTH_ICON_0, STAMINA_ICON_0, BALANCE_ICON_0, MANA_ICON_0, HEALTH_ICON_1, STAMINA_ICON_1, BALANCE_ICON_1, MANA_ICON_1, HEALTH_ICON_2, STAMINA_ICON_2, BALANCE_ICON_2, MANA_ICON_2, HEALTH_ICON_3, STAMINA_ICON_3, BALANCE_ICON_3, MANA_ICON_3, 
			MARS_ICON_0, MARS_ICON_1, MARS_ICON_2, MARS_ICON_3, MARS_ICON_4, SMALL_DONG_0, SMALL_DONG_1, SMALL_DONG_2, SMALL_DONG_CHASTITY, STUFFED_BELLY, FULL_BELLY, BIG_BELLY, FLAT_BELLY, ARMOR_DOLL
		};
		for (AssetEnum asset: assets) {
			resourceRequirements.add(asset.getTexture());
		}
	}
	private final SaveService saveService;
	private final Battle battle;
	
	protected BattleScreen(ScreenFactory screenFactory, ScreenElements elements, SaveService saveService, Battle battle) {
		super(screenFactory, elements, battle.getMusicPath());
		this.saveService = saveService;
		this.battle = battle;
	}

	@Override
	public void buildStage() {
		addActor(battle);
	}

	@Override
	public void render(float delta) {
		super.render(delta);
		battle.battleLoop();
		if (battle.gameExit) {
			showScreen(ScreenEnum.MAIN_MENU);
		}
		// this terminates the battle
		else if (battle.isBattleOver()) {	
			saveService.saveDataValue(SaveEnum.CONTEXT, SaveManager.GameContext.ENCOUNTER);
			saveService.saveDataValue(SaveEnum.SCENE_CODE, battle.getOutcomeScene());
			saveService.saveDataValue(SaveEnum.ENEMY, null); // this may need to be removed if the enemy needs to persist until the end of the encounter; endScenes would have to perform this save or the encounter screen itself
			showScreen(ScreenEnum.ENCOUNTER);
		}
	}
	
	@Override
	public void dispose() {
		for(AssetDescriptor<?> path: requirementsToDispose) {
			if (path.fileName.equals(AssetEnum.BUTTON_SOUND.getSound().fileName) || path.type == Music.class) continue;
			assetManager.unload(path.fileName);
		}
		requirementsToDispose = new Array<AssetDescriptor<?>>();
	}

	// this should simply return the battlecode's requirements, rather than use a switch
	public static Array<AssetDescriptor<?>> getRequirements(BattleAttributes battleAttributes) {
		Array<AssetDescriptor<?>> requirements = new Array<AssetDescriptor<?>>(BattleScreen.resourceRequirements);
		requirements.addAll(battleAttributes.getRequirements());
		requirementsToDispose = requirements;
		return requirements;
	}
	
}

package com.majalis.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.utils.Array;
import com.majalis.asset.AssetEnum;
import com.majalis.character.AbstractCharacter.Stat;
/*
 * Screen that displays while a new screen is loading.
 */
public class LoadScreen extends AbstractScreen {
	private final BitmapFont largeFont;
	private final Image loadingImage;
	private final ScreenEnum screenRequest;
	
	public LoadScreen(ScreenFactory factory, ScreenElements elements, ScreenEnum screenRequest) {
		super(factory, elements, null);
		this.loadingImage = new Image(assetManager.get(AssetEnum.LOADING.getTexture()));
		this.largeFont = fontFactory.getFont(72);
		this.screenRequest = screenRequest;
	}

	@Override
	public void buildStage() {
		LabelStyle style = new LabelStyle();
		style.font = largeFont;
		Label loading = new Label("Loading...", style);
		loading.setPosition(1500, 50);
		loading.addAction(Actions.forever(Actions.sequence(Actions.color(Color.GRAY, 1), Actions.color(Color.WHITE, 1))));
		this.addActor(loading);
		this.addActor(loadingImage);
		loadingImage.setPosition(1350, 50);
		loadingImage.setScale(.25f);
	}
	@Override
	protected boolean doesFade() { return false; }
	
	@Override
	public void render(float delta) {
		super.render(delta);
		if (assetManager.update(100)) {
			showScreen(screenRequest);
		}		
	}
	
	public final static Array<String> randomTooltip = new Array<String>(new String[]{
		// UI tooltips
		"Press TAB to toggle hiding or showing the UI.", 
		"Press CTRL to skip text in encounters. Text will be skipped until the next branching choice.", 
		"Press SHIFT to alternate certain images and animations.", 
		"Click and drag or use the arrow keys to navigate the world map.",
		// Gameplay tooltips
		"Your stance is the primary determinant in what techniques you can use at a given time. Using a technique will often change your stance. Pay close attention to your stance, the enemy's stance, and what stance a given technique will leave you in if it's successful.",
		"Some techniques get conditional bonuses - having greater or extra effects if, for example, an enemy is on the ground or on unstable footing, or if you're more agile than the enemy, or simply for being more proficient at the technique.",
		"While grappling, your available techniques will be limited based on your position in the grapple - some techniques will only be available if you have the advantage, others if you're at a disadvantage, and this is true of your opponent as well!",
		"When your health is low, you'll receive penalties to the three physical stats - Strength, Endurance, and Agility. When your stamina is low, you'll receive penalties to Strength and Agility. These penalties apply in encounter checks as well as battle.",
		"Some weapons cause bleed - the amount inflicted depends on the technique. Higher Endurance will reduce damage from bleeding. Bleed can be cured by bandages and rest - Endurance also increases bleed recovery.",
		"Scouting improves the outcomes from certain encounters and reveals hidden paths. High perception boosts your effective scouting score, making it valuable for getting the most out of the areas you traverse, as well as avoiding pitfalls.",
		"Foraging outcomes are better in the day than at night. Foraging at night carries its own unique risks!",
		"Damage done by attacks is never random! Damage may be higher or lower than predicted damage because the enemy guards, or is armored, or has damage reduction.",
		"Damage is reduced by armor, which is broken over the course of a battle. Sundering an enemy's armor early in the fight can be a useful investment.",
		"Be wary when using Techniques that cause instability - the enemy can also cause you to overbalance, causing you to trip before you expected to! A technique labelled in red will cause you to fall - one in yellow puts you in dangerous territory the enemy might capitalize on.",
		"On the Main Menu, Continue will load your last autosave, restoring you to the same position as when you last closed the game. Load will load your last Quick Save - Quick Saves can be created by hitting \"Save\" on the Main Menu.",
		"Certain enemies can be defeated by achieving certain conditions in battle, rather than reducing their HP to 0. Similarly, certain enemies can defeat you without reducing your HP to 0!",
		"Certain attacks can be dodged entirely by being above or below them - techniques that involve kneeling or jumping can help avoid such attacks.",
		"To attempt to mount enemies, you must be erect.",
		"The \"Catamite\" perk opens up various opportunities and abilities.",
		"The Hunger Charm reduces your metabolic rate, reducing the amount of food you must consume.",
		"Make sure to buy a weapon! Armed combat is more effective than unarmed. For this same reason, try to disarm enemies - although be warned that might change their behavior.",
		"If you run out of gold, you can still stay at the inn...",
		"Some shops will refill portions of their stock over time.",
		"The enemy can force you into a different stance - and vice-versa! Make sure to take notice of when that happens.",
		"Taunting an enemy will increase both their lust and yours.",
		"There are rumors of a variant of the Centaur species that is attracted exclusively to virgins...",
		"Goblins have incredible... stamina.",
		"Beware werewolves - because of their anatomy, they may get... stuck inside.  Drain them before they do! And avoid showing off how strong you are!",
		"Reducing an enemy to zero health isn't the only way to win - and may not always lead to the best outcome.",
		// Goofy
		"There is no secret ice cream level.",
		"In the end, it's not about the destination, it's about the friends you made along the way."
	});
	static {
		for (Stat stat : Stat.values()) {
			randomTooltip.add(stat.getDescription());
		}
	}
}
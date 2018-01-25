package com.majalis.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.majalis.asset.AssetEnum;

public class HelpScreen extends AbstractScreen{
	public static final Array<AssetDescriptor<?>> resourceRequirements = new Array<AssetDescriptor<?>>();
	static {
		resourceRequirements.add(AssetEnum.UI_SKIN.getSkin());
		resourceRequirements.add(AssetEnum.BUTTON_SOUND.getSound());
		resourceRequirements.add(AssetEnum.MAIN_MENU_MUSIC.getMusic());
		resourceRequirements.add(AssetEnum.CAMP_BG0.getTexture());
		resourceRequirements.add(AssetEnum.CAMP_BG1.getTexture());
		resourceRequirements.add(AssetEnum.CAMP_BG2.getTexture());
	}
	
	private int display = 0;
	
	protected HelpScreen(ScreenFactory screenFactory, ScreenElements elements, AssetManager assetManager) {
		super(screenFactory, elements, null);
		this.addActor(getCampBackground());
		final Array<Table> slides = new Array<Table>();
		Skin skin = assetManager.get(AssetEnum.UI_SKIN.getSkin());
		final Sound sound = assetManager.get(AssetEnum.BUTTON_SOUND.getSound());

		Array<Array<Label>> info = new Array<Array<Label>>();
		info.add(new Array<Label>(new Label[]{new Label(
			"CONTROLS:\n\n" +
			"Press TAB to toggle hiding or showing the UI.\n\n" +
			"Press CTRL to skip text in encounters. Text will be skipped until the next branching choice.\n\n" +
			"Press SHIFT to alternate certain images and animations.\n\n" + 
			"Click and drag or use the arrow keys to navigate the world map.", 
			skin
		)}));
		info.add(new Array<Label>(new Label[]{new Label(
			"BASIC GAMEPLAY:\n\n" + 
			"Your stance is the primary determinant in what techniques you can use at a given time. Using a technique will often change your stance. Pay close attention to your stance, the enemy's stance, and what stance a given technique will leave you in if it's successful.\n\n" + 
			"While grappling, your available techniques will be limited based on your position in the grapple - some techniques will only be available if you have the advantage, others if you're at a disadvantage, and this is true of your opponent as well!\n\n" + 
			"Certain enemies can be defeated by achieving certain conditions in battle, rather than reducing their HP to 0. Similarly, certain enemies can defeat you without reducing your HP to 0!\nBe wary when using skills that cause instability - the enemy can also cause you to overbalance, causing you to trip before you expected to! A technique labelled in red will cause you to fall - one in yellow puts you in dangerous territory the enemy might capitalize on.", 
			skin
		)}));
		info.add(new Array<Label>(new Label[]{new Label(
			"BASIC GAMEPLAY (2):\n\n" + 
			"Your character and the enemy are not always at their best - various afflictions temporarily lower their attributes, making them less effective.\n\n" +
			"When your health is low, you'll receive penalties to the three physical stats - Strength, Endurance, and Agility.\n\n" + 
			"When your stamina is low, you'll receive penalties to Strength and Agility. These penalties apply in encounter checks as well as battle.", 
			skin
		)}));
		info.add(new Array<Label>(new Label[]{new Label(
			"ATTRIBUTES:\n\n" + 
			"Strength increases damage and allows you to overpower your enemy in various situations (grappling, for instance).\n\n" +
			"Endurance increases stamina regen.\n\n" + 
			"Agility increases stability.", 
			skin
		)}));
		info.add(new Array<Label>(new Label[]{new Label(
			"COMBAT:\n\n" + 
			"Damage is INCREASED by\n" +
			"    Strength (for physical attacks) / Magic (for spells)\n" +
			"    Weapon Damage (increased by stats)\n" +
			"    Skill modifier\n\n" +
			"Damage is DECREASED by\n" +
			"    Target Base Defense\n" + 
			"    Target Armor (if armor isn't ignored) - Damage is reduced by armor, which is broken over the course of a battle. Sundering an enemy's armor early in the fight can be a useful investment.\n" + 
			"    Target Guarding/Parrying (if opponent attack has them)\n" + 
			"    Skill modifier\n",
			skin
		)}));	
		info.add(new Array<Label>(new Label[]{new Label(
			"COMBAT TECHNIQUES:\n\n" + 
			"An attack that does 0 damage is not without its use, if it inflicts damage to the opponent's armor/shield or destabilizes them, or if it changes the character's stance to a more advantageous one.\n\n" + 
			"Some techniques get conditional bonuses - having greater or extra effects if, for example, an enemy is on the ground or on unstable footing, or if you're more agile than the enemy, or simply for being more proficient at the technique.\n\n" + 
			"Some weapons cause bleed - the amount inflicted depends on the technique. Higher Endurance will reduce damage from bleeding. Bleed can be cured by bandages and rest - Endurance also increases bleed recovery.", 
			skin
		)}));
		
		for (int ii = 0; ii < info.size; ii++) {
			Table newTable = new Table();
			newTable.setPosition(100, 1000);
			newTable.align(Align.topLeft);
			Array<Label> currentInfo = info.get(ii);
			for (int jj = 0; jj < currentInfo.size; jj++) {
				newTable.add(currentInfo.get(jj)).width(850).row();
				currentInfo.get(jj).setWrap(true);
			}
			
			slides.add(newTable);
		}
		
		final Group imageGroup = new Group();
		this.addActor(imageGroup);
		
		final Group uiGroup = new Group();
		this.addActor(uiGroup);
		
		imageGroup.addActor(slides.get(0));
		
		final TextButton back = new TextButton("Back", skin);
		
		back.addListener(
			new ClickListener() {
				@Override
		        public void clicked(InputEvent event, float x, float y) {
					sound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
					imageGroup.clear();
					display--;
					imageGroup.addActor(slides.get(display));
					if (display == 0) uiGroup.removeActor(back);
		        }
			}
		);
		
		back.setPosition(1553, 200);
		back.setWidth(100);
		
		final TextButton next = new TextButton("Next", skin);
		
		next.addListener(
			new ClickListener() {
				@Override
		        public void clicked(InputEvent event, float x, float y) {
					sound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
					if (display == 0) uiGroup.addActor(back);
					if (display < slides.size - 1) {
						imageGroup.clear();			
						display++;
						imageGroup.addActor(slides.get(display));
					}
					else {
						exitScreen();
					}
		        }
			}
		);
		
		next.setWidth(100);
		next.setPosition(1668, 200);
		this.addActor(next);
	
		final TextButton done = new TextButton("Done", skin);
		
		done.addListener(
			new ClickListener() {
				@Override
		        public void clicked(InputEvent event, float x, float y) {
					sound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
					exitScreen();	   
		        }
			}
		);
		done.setPosition(1523, 120);
		this.addActor(done);
	}
	
	@Override
	public void buildStage() {
		
	}
	
	@Override
	public void render(float delta) {
		super.render(delta);
		if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
			exitScreen();
		}
	}
	
	private void exitScreen() {
		showScreen(ScreenEnum.MAIN_MENU);
	}

	
	@Override
	public void show() {
		super.show();
	    getRoot().getColor().a = 0;
	    getRoot().addAction(Actions.fadeIn(0.5f));
	}
	
}

package com.majalis.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.majalis.asset.AssetEnum;
import com.majalis.save.Achievement;
import com.majalis.save.LoadService;
import com.majalis.save.ProfileEnum;

public class ProgressScreen extends AbstractScreen {


	public static final Array<AssetDescriptor<?>> resourceRequirements = new Array<AssetDescriptor<?>>();
	static {
		resourceRequirements.add(AssetEnum.UI_SKIN.getSkin());
		resourceRequirements.add(AssetEnum.BUTTON_SOUND.getSound());
		resourceRequirements.add(AssetEnum.MAIN_MENU_MUSIC.getMusic());
		resourceRequirements.add(AssetEnum.CAMP_BG0.getTexture());
		resourceRequirements.add(AssetEnum.CAMP_BG1.getTexture());
		resourceRequirements.add(AssetEnum.CAMP_BG2.getTexture());
		resourceRequirements.addAll(MainMenuScreen.resourceRequirements);
	}
	private final Skin skin;
	
	public ProgressScreen(ScreenFactory factory, ScreenElements elements, LoadService loadService) {
		super(factory, elements, null);
		this.addActor(getCampBackground());
		skin = assetManager.get(AssetEnum.UI_SKIN.getSkin());
		final Sound sound = assetManager.get(AssetEnum.BUTTON_SOUND.getSound());
		
		// need to add a display for achievements
		// need a display for bonus points
		// need to display a list for available starting bonuses
		
		addLabelActor("Achievements Unlocked", 100, 1025, Color.TAN);
		
		Table achievementTable = new Table(skin);
		
		ObjectMap<String, Integer> achievements = loadService.loadDataValue(ProfileEnum.ACHIEVEMENT, ObjectMap.class);
		int bonusPoints = 0;
		for (ObjectMap.Entry<String, Integer> entry : achievements) {
			if (entry.value > 0) {
				bonusPoints++;
				Achievement achievement = Achievement.valueOf(entry.key);
				achievementTable.add(achievement.getLabel(), "default-font", Color.GOLD).align(Align.center).width(300);
				achievementTable.add(achievement.getDescription(), "default-font", Color.WHITE).align(Align.left).row();
			}
		}
		
		achievementTable.align(Align.topLeft);
		
		addActorAndListen(achievementTable, 200, 1025);
		
		addLabelActor("Bonus Points: " + bonusPoints, 100, 500, Color.TAN);
		
		addLabelActor("Start Bonuses Unlocked", 100, 450, Color.TAN);
		
		Table unlockTable = new Table(skin);
		
		String[] unlocks = new String[]{"Bonus Stat Points", "Bonus Skill Points", "Bonus Soul Crystals", "Bonus Perk Points", "Bonus Gold", "Bonus Food"};
		for (String s : unlocks) {
			unlockTable.add(s, "default-font", Color.GOLD).align(Align.left).row();
		}
		
		unlockTable.align(Align.topLeft);
		
		addActorAndListen(unlockTable, 200, 450);
		
		final TextButton done = new TextButton("Done", skin);
		
		done.addListener(
			new ClickListener() {
				@Override
		        public void clicked(InputEvent event, float x, float y) {
					if (!debug) {
						sound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
						saveAndExit();	 
					}
		        }
			}
		);
		addActorAndListen(done, 1550, 50);
	}
	
	@Override
	public void buildStage() {}
	
	@Override
	public void render(float delta) {
		super.render(delta);
		if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
			saveAndExit();
		}
		if(Gdx.input.isKeyJustPressed(Keys.SHIFT_LEFT)) {
			debug = !debug;
		}
	}
	
	private void saveAndExit() { showScreen(ScreenEnum.MAIN_MENU); }
	
	private Label addLabelActor(String label, int x, int y, Color color) {
		Label newLabel = new Label(label, skin);
		newLabel.setColor(color);
		addActorAndListen(newLabel, x, y);
		return newLabel;
	}
	
	@Override
	public void show() {
		super.show();
	    getRoot().getColor().a = 0;
	    getRoot().addAction(Actions.fadeIn(0.5f));
	}
}

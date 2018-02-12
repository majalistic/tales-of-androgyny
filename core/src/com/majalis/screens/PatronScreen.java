package com.majalis.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
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

public class PatronScreen extends AbstractScreen{
	public static final Array<AssetDescriptor<?>> resourceRequirements = new Array<AssetDescriptor<?>>();
	static {
		resourceRequirements.add(AssetEnum.UI_SKIN.getSkin());
		resourceRequirements.add(AssetEnum.BUTTON_SOUND.getSound());
		resourceRequirements.add(AssetEnum.MAIN_MENU_MUSIC.getMusic());
		resourceRequirements.add(AssetEnum.CAMP_BG0.getTexture());
		resourceRequirements.add(AssetEnum.CAMP_BG1.getTexture());
		resourceRequirements.add(AssetEnum.CAMP_BG2.getTexture());
	}
	private final Skin skin;
	protected PatronScreen(ScreenFactory screenFactory, ScreenElements elements) {
		super(screenFactory, elements, null);
		this.addActor(getCampBackground());
		skin = assetManager.get(AssetEnum.UI_SKIN.getSkin());
		final Sound sound = assetManager.get(AssetEnum.BUTTON_SOUND.getSound());
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
		done.setWidth(150);
		done.setPosition(1623, 50);
		this.addActor(done);
	}

	@Override
	public void buildStage() {
		Table creditsTable = new Table();
		this.addActor(creditsTable);
		creditsTable.align(Align.topLeft);
		creditsTable.setPosition(50, 1075);
		
		creditsTable.add(new Label("Special thanks to our patrons:", skin)).align(Align.left).row();
		creditsTable.row();
		creditsTable.add().row();
		
		Array<String> patrons = new Array<String>(new String[]{"Crufl", "Laersect", "Reaper", "Jenna Tran", "Kaz", "Dementiaus", "Purpleness", "Brody swainson", "Travis Wright", "ninjahuinja", "Sau", "Alessandro Cicuta", "AngelaD96", "xxTwinkletoexx", "maxime perreault", "Daveonnie Thomas", "James S",
			"Seraph27", "Ivan Evans", "Brandon Leary", "Bret Sawyer", "Aika", "Andrew Lennon", "Rai Fenix", "tj mauser", "Thomas Boglari", "PeterJK", "Gariored", "Gray Layne", "zazsu", "Mackenzie", "Christian Fish Jr", "Nicolas", "Sascha Looser", "Twister3388", "Matt Harris", "Michael L Stevers", 
			"Realityville", "FunnyFresh", "Dnice Templer", "Catove", "FusionTech", "Bobstick5", "Meep Meepersons", "Kyle Long", "Drew", "John-Luke Irwin", "Anthony Wheeler", "Cogius", "Khalil", "Steven King", "Necroa Virus", "Evzen", "Amy Dupont", "MonsoonMoon", "tim timerson", "Angus Carson Smith",
			"Kiyu69", "Kushi", "Haydn S Jergens", "Attrau", "MB", "Mathies", "Fatty God", "Christopher Danly", "Strikel", "Vermillion", "Ben Rutherford", "John", "Yarzu", "Exalted", "Lulu Jo Nisei", "xLed", "Preusk", "Booger10", "Knight-Lord Xander", "Rashid", "Zachary Blizzard", "Bliss", "khy375",
			"Dirk Gently", "Wrymm Alstier", "Zmue", "The Daskling", "Demoneater", "Ivory Thomas", "Eri Saykaiten", "Isolated Peace", "Ruleram", "darthrevan150", "Optimus", "Brandon Wasson", "Philip Potvin", "LordChtulhu", "Lazarus", "Lauren Smith", "Spyhard", "bloodline", "Ozwald Otaku", "Azmodan412",
			"Sabrent", "Lecter", "Fenrakk Xeme", "BigBossBlack", "Mark", "Michael Jensen", "Brandom Winegar", "dc", "Alex Peralta-Castro", "Kurt Wagner", "Samuli Hurme", "Kitsune", "Adam Hogsett", "Honeuma", "Gremzyx", "Sissy Cum Bottom", "Devante Vickers", "Christopher Connolly", "Pope",
			"Connor S Thomas", "IDEDOnline", "ray S.", "Big Pants", "Raider", "Jonah Hollie", "J Young", "Ratrace", "Skellum", "Deadmanhunter", "Vysirez", "Booger", "Petter", "rusty shackleford", "Unnie", "Jzzb", "Sinister", "Thomas Davis"
		});
		int ii = 0;
		for (String s : patrons) {
			Label newLabel = new Label(s, skin);
			newLabel.setWrap(true);
			creditsTable.add(newLabel).width(400).align(Align.left);
			if (ii % 5 == 4) creditsTable.row();
			ii++;
		}	
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
	
	@Override
	public void dispose() {
		for(AssetDescriptor<?> path: resourceRequirements) {
			if (path.fileName.equals(AssetEnum.BUTTON_SOUND.getSound().fileName) || path.type == Music.class) continue;
			assetManager.unload(path.fileName);
		}
	}
}

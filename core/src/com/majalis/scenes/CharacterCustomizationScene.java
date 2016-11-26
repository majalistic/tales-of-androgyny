package com.majalis.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.OrderedMap;
import com.majalis.asset.AssetEnum;
import com.majalis.character.PlayerCharacter;
import com.majalis.encounter.Background;
import com.majalis.save.SaveEnum;
import com.majalis.save.SaveService;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class CharacterCustomizationScene extends Scene {

	private final SaveService saveService;
	private final BitmapFont font;
	private final Skin skin;
	private final Sound buttonSound;
	private final PlayerCharacter character;
	private String console;
	// customize name, face, body, skin, hair, length, facial markings
	public CharacterCustomizationScene(OrderedMap<Integer, Scene> sceneBranches, int sceneCode, final SaveService saveService, BitmapFont font, Background background, AssetManager assetManager, PlayerCharacter character) {
		super(sceneBranches, sceneCode);
		this.saveService = saveService;
		this.font = font;
		this.addActor(background);
		this.character = character;
		
		skin = assetManager.get(AssetEnum.UI_SKIN.getPath(), Skin.class);
		buttonSound = assetManager.get(AssetEnum.BUTTON_SOUND.getPath(), Sound.class);
		
		console = "";
	}
	
	@Override
    public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		font.setColor(0.5f,0.4f,0,1);
		font.draw(batch, "Character Customization", 900, 900);
		font.setColor(0.4f,0.4f,0.4f,1);
		int base = 75;
		font.draw(batch,  "Name:", base + 100, 710);
		font.draw(batch, console, base + 100, 825);
    }
	
	@Override
	public void setActive() {
		isActive = true;
		this.removeAction(Actions.hide());
		this.addAction(Actions.visible(true));
		this.addAction(Actions.show());
		this.setBounds(0, 0, 2000, 2000);
		saveService.saveDataValue(SaveEnum.SCENE_CODE, sceneCode);

		final TextButton done = new TextButton("Done", skin);
		
		done.setSize(180, 40); 
		done.addListener(
			new ClickListener(){
				@Override
		        public void clicked(InputEvent event, float x, float y) {
					buttonSound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
					nextScene();		   
		        }
			}
		);
		done.setPosition(1522, 30);
		addActor(done);
		
		final Table table = new Table();
		
		for (final PlayerCharacter.Bootyliciousness buttSize : PlayerCharacter.Bootyliciousness.values()){
			final TextButton button = new TextButton(buttSize.toString(), skin);
			button.addListener(new ClickListener(){
				@Override
		        public void clicked(InputEvent event, float x, float y) {
					buttonSound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
					console = "You now have a " + buttSize.toString().toLowerCase() + " booty.";
					character.setBootyliciousness(buttSize);
					saveService.saveDataValue(SaveEnum.PLAYER, character);
		        }
			});
			table.add(button).size(220, 40).row();
		}
		table.setPosition(383, 525);
		addActor(table);
		
		final TextField nameField = new TextField("", skin);
		nameField.setPosition(233, 675);
		addActor(nameField);
		this.addListener(new InputListener() {
	        @Override
	        public boolean keyUp(InputEvent event, int keycode) {
	        	if (!nameField.getText().equals(character.getName())){
	        		character.setName(nameField.getText());
	        		saveService.saveDataValue(SaveEnum.PLAYER, character);
	        	}
	        	return false;
	        }
	    });
	}
	
	private void nextScene(){
		sceneBranches.get(sceneBranches.orderedKeys().get(0)).setActive();
		isActive = false;
		addAction(Actions.hide());
	}
}

package com.majalis.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
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
import com.majalis.character.Item;
import com.majalis.encounter.Background.BackgroundBuilder;
import com.majalis.save.SaveEnum;
import com.majalis.save.SaveManager.GameContext;
import com.majalis.save.SaveService;
/*
 * Screen for displaying the character's inventory.
 */
public class InventoryScreen extends AbstractScreen {

	public static final Array<AssetDescriptor<?>> resourceRequirements = new Array<AssetDescriptor<?>>();
	static {
		resourceRequirements.add(AssetEnum.UI_SKIN.getSkin());
		resourceRequirements.add(AssetEnum.CLICK_SOUND.getSound());
		resourceRequirements.add(AssetEnum.MOUNTAIN_ACTIVE.getTexture()); 
		resourceRequirements.add(AssetEnum.FOREST_ACTIVE.getTexture());
		resourceRequirements.add(AssetEnum.FOREST_INACTIVE.getTexture());
		resourceRequirements.add(AssetEnum.CASTLE.getTexture());
		resourceRequirements.add(AssetEnum.APPLE.getTexture());
		resourceRequirements.add(AssetEnum.MEAT.getTexture());
		resourceRequirements.add(AssetEnum.CLOUD.getTexture());
		resourceRequirements.add(AssetEnum.ROAD.getTexture());
		resourceRequirements.add(AssetEnum.WORLD_MAP_UI.getTexture());
		resourceRequirements.add(AssetEnum.WORLD_MAP_HOVER.getTexture());
		resourceRequirements.add(AssetEnum.ARROW.getTexture());
		resourceRequirements.add(AssetEnum.CHARACTER_SCREEN.getTexture());
		resourceRequirements.add(AssetEnum.STRENGTH.getTexture());
		resourceRequirements.add(AssetEnum.ENDURANCE.getTexture());
		resourceRequirements.add(AssetEnum.AGILITY.getTexture());
		resourceRequirements.add(AssetEnum.PERCEPTION.getTexture());
		resourceRequirements.add(AssetEnum.MAGIC.getTexture());
		resourceRequirements.add(AssetEnum.CHARISMA.getTexture());
		resourceRequirements.add(AssetEnum.WARRIOR.getTexture());
		resourceRequirements.add(AssetEnum.PALADIN.getTexture());
		resourceRequirements.add(AssetEnum.THIEF.getTexture());
		resourceRequirements.add(AssetEnum.RANGER.getTexture());
		resourceRequirements.add(AssetEnum.MAGE.getTexture());
		resourceRequirements.add(AssetEnum.ENCHANTRESS.getTexture());
		resourceRequirements.add(AssetEnum.WORLD_MAP_MUSIC.getMusic());
		resourceRequirements.addAll(WorldMapScreen.resourceRequirements);
	}
	
	private final Sound buttonSound;
	private final PlayerCharacter character;
	private final Label consoleText;
	private final Label hoverText;
	private final SaveService saveService;
	private final Table inventoryTable;
	private final Table weaponTable;
	private final Label weaponText;
	private final Label shieldText;
	private final Label armorText;
	private final Label legwearText;
	private final Label underwearText;
	private final Label headgearText;
	private final Label armwearText;
	private final Label footwearText;
	private final Label accessoryText;
	private final Label plugText;
	private final Label cageText;
	private final Skin skin;
	private String result;
	
	public InventoryScreen(ScreenFactory factory, ScreenElements elements, final SaveService saveService, final PlayerCharacter character) {
		super(factory, elements, null);
		this.addActor(new BackgroundBuilder(assetManager.get(AssetEnum.CHARACTER_SCREEN.getTexture())).build()); 
		
		skin = assetManager.get(AssetEnum.UI_SKIN.getSkin());
		buttonSound = assetManager.get(AssetEnum.CLICK_SOUND.getSound()); 
		final TextButton done = new TextButton("Done", skin);
		this.saveService = saveService;
		this.character = character;
		
		Image characterImage = new Image(assetManager.get(character.getJobClass().getTexture()));
		characterImage.setPosition(1250, 0);
		this.addActor(characterImage);
		
		done.setSize(180, 60);
		done.addListener(
			new ClickListener() {
				@Override
		        public void clicked(InputEvent event, float x, float y) {
					buttonSound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
					saveService.saveDataValue(SaveEnum.CONTEXT, GameContext.WORLD_MAP);
					showScreen(ScreenEnum.CONTINUE);		   
		        }
			}
		);
		done.setPosition(1700, 30);
		this.addActor(done);
		
		final TextButton characterButton = new TextButton("Character", skin);
		characterButton.setSize(250, 60);
		characterButton.addListener(
			new ClickListener() {
				@Override
		        public void clicked(InputEvent event, float x, float y) {
					buttonSound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
					showScreen(ScreenEnum.CHARACTER);		   
		        }
			}
		);
		characterButton.setPosition(1450, 30);
		this.addActor(characterButton);
		
		inventoryTable = new Table();
		inventoryTable.add(getLabel("Inventory", skin, Color.BLACK)).row();
		inventoryTable.setPosition(100, 550);
		inventoryTable.align(Align.topLeft);
		this.addActor(inventoryTable);
		weaponTable = new Table();
		consoleText = new Label("", skin);
		consoleText.setPosition(425, 1075);
		consoleText.setAlignment(Align.topLeft);
		consoleText.setColor(Color.BROWN);
		this.addActor(consoleText);
		hoverText = new Label("", skin);
		hoverText.setPosition(900, 1075);
		hoverText.setAlignment(Align.topLeft);
		hoverText.setColor(Color.GOLDENROD);
		this.addActor(hoverText);
		weaponTable.add(getLabel("Equipment", skin, Color.BLACK)).row();
		weaponTable.setPosition(950, 1000);
		weaponTable.align(Align.top);
		this.addActor(weaponTable);
		
		Table equipmentTable = new Table();
		equipmentTable.align(Align.topLeft);
		weaponText = getLabel(character.getWeapon() != null ? character.getWeapon().getName() : "Unarmed", skin, character.getWeapon() != null ? Color.GOLD : Color.BROWN);
		shieldText = getLabel(character.getShield() != null ? character.getShield().getName() : "Unarmed", skin, character.getShield() != null ? Color.GOLD : Color.BROWN);
		armorText = getLabel(character.getArmor() != null ? character.getArmor().getName() : "None", skin, character.getArmor() != null ? Color.GOLD : Color.BROWN);
		legwearText = getLabel(character.getLegwear() != null ? character.getLegwear().getName() : "None", skin, character.getLegwear() != null ? Color.GOLD : Color.BROWN);
		underwearText = getLabel(character.getUnderwear() != null ? character.getUnderwear().getName() : "None", skin, character.getUnderwear() != null ? Color.GOLD : Color.BROWN);
		headgearText = getLabel(character.getHeadgear() != null ? character.getHeadgear().getName() : "None", skin, character.getHeadgear() != null ? Color.GOLD : Color.BROWN);
		armwearText = getLabel(character.getArmwear() != null ? character.getArmwear().getName() : "None", skin, character.getArmwear() != null ? Color.GOLD : Color.BROWN);
		footwearText = getLabel(character.getFootwear() != null ? character.getFootwear().getName() : "None", skin, character.getFootwear() != null ? Color.GOLD : Color.BROWN);
		accessoryText = getLabel(character.getFirstAccessory() != null ? character.getFirstAccessory().getName() : "None", skin, character.getFirstAccessory() != null ? Color.GOLD : Color.BROWN);
		plugText = getLabel(character.getPlug() != null ? character.getPlug().getName() : "None", skin, character.getPlug() != null ? Color.GOLD : Color.BROWN);
		cageText = getLabel(character.getCage() != null ? character.getCage().getName() : "None", skin, character.getCage() != null ? Color.GOLD : Color.BROWN);
		
		weaponText.addListener(new ClickListener() { @Override public void clicked(InputEvent e, float x, float y) { resetWeaponTable(character.unequipWeapon()); }});
		shieldText.addListener(new ClickListener() { @Override public void clicked(InputEvent e, float x, float y) { resetWeaponTable(character.unequipShield()); }});
		armorText.addListener(new ClickListener() { @Override public void clicked(InputEvent e, float x, float y) { resetWeaponTable(character.unequipArmor()); }});
		legwearText.addListener(new ClickListener() { @Override public void clicked(InputEvent e, float x, float y) { resetWeaponTable(character.unequipLegwear()); }});
		underwearText.addListener(new ClickListener() { @Override public void clicked(InputEvent e, float x, float y) { resetWeaponTable(character.unequipUnderwear()); }});
		headgearText.addListener(new ClickListener() { @Override public void clicked(InputEvent e, float x, float y) { resetWeaponTable(character.unequipHeadgear()); }});
		armwearText.addListener(new ClickListener() { @Override public void clicked(InputEvent e, float x, float y) { resetWeaponTable(character.unequipArmwear()); }});
		footwearText.addListener(new ClickListener() { @Override public void clicked(InputEvent e, float x, float y) { resetWeaponTable(character.unequipFootwear()); }});
		accessoryText.addListener(new ClickListener() { @Override public void clicked(InputEvent e, float x, float y) { resetWeaponTable(character.unequipAccessory()); }});
		plugText.addListener(new ClickListener() { @Override public void clicked(InputEvent e, float x, float y) { resetWeaponTable(character.unequipPlug()); }});
		cageText.addListener(new ClickListener() { @Override public void clicked(InputEvent e, float x, float y) { resetWeaponTable(character.unequipCage()); }});
		
		int xBuffer = 160;
		
		equipmentTable.setPosition(50, 1075);
		this.addActor(equipmentTable);
		equipmentTable.add(getLabel("Weapon:", skin, Color.DARK_GRAY)).width(xBuffer).align(Align.left);
		equipmentTable.add(weaponText).align(Align.left).row();
		equipmentTable.add(getLabel("Shield:", skin, Color.DARK_GRAY)).width(xBuffer).align(Align.left);
		equipmentTable.add(shieldText).align(Align.left).row();
		equipmentTable.add(getLabel("Armor:", skin, Color.DARK_GRAY)).width(xBuffer).align(Align.left);
		equipmentTable.add(armorText).align(Align.left).row();
		equipmentTable.add(getLabel("Legwear:", skin, Color.DARK_GRAY)).width(xBuffer).align(Align.left);
		equipmentTable.add(legwearText).align(Align.left).row();
		equipmentTable.add(getLabel("Underwear:", skin, Color.DARK_GRAY)).width(xBuffer).align(Align.left);
		equipmentTable.add(underwearText).align(Align.left).row();
		equipmentTable.add(getLabel("Headgear:", skin, Color.DARK_GRAY)).width(xBuffer).align(Align.left);
		equipmentTable.add(headgearText).align(Align.left).row();
		equipmentTable.add(getLabel("Armwear:", skin, Color.DARK_GRAY)).width(xBuffer).align(Align.left);
		equipmentTable.add(armwearText).align(Align.left).row();
		equipmentTable.add(getLabel("Footwear:", skin, Color.DARK_GRAY)).width(xBuffer).align(Align.left);
		equipmentTable.add(footwearText).align(Align.left).row();	
		equipmentTable.add(getLabel("Accessory:", skin, Color.DARK_GRAY)).width(xBuffer).align(Align.left);
		equipmentTable.add(accessoryText).align(Align.left).row();
		equipmentTable.add(getLabel("Buttwear:", skin, Color.DARK_GRAY)).width(xBuffer).align(Align.left);
		equipmentTable.add(plugText).align(Align.left).row();
		equipmentTable.add(getLabel("Dickwear:", skin, Color.DARK_GRAY)).width(xBuffer).align(Align.left);
		equipmentTable.add(cageText).align(Align.left).row();
		
		int inventoryColumn = 0;
		boolean equipmentColumn = false;
		for (final Item item : character.getInventory()) {
			final TextButton itemButton = new TextButton(item.getName(), skin);
			if (item.isConsumable()) {
				itemButton.addListener(getItemListener(item));
				inventoryTable.add(itemButton).size(450, 40);
				if (inventoryColumn == 2) inventoryTable.row();
				inventoryColumn++;
				inventoryColumn %= 3;
			}
			else if (item.isEquippable()) { // this needs to properly equip the item in the correct slot
				itemButton.addListener(
					getWeaponListener(item)
				);
				if (character.isEquipped(item)) {
					itemButton.setColor(Color.GOLD);
				}
				weaponTable.add(itemButton).size(500, 40);
				if (equipmentColumn) weaponTable.row();
				equipmentColumn = !equipmentColumn;
			}
		}	
	}
	
	private ClickListener getItemListener(final Item item) {
		return new ClickListener() {
			@Override
	        public void clicked(InputEvent event, float x, float y) {
				buttonSound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
				result = character.consumeItem(item).getResult();
				consoleText.setText(result);
				saveService.saveDataValue(SaveEnum.PLAYER, character);
				inventoryTable.clear();
				inventoryTable.add(getLabel("Inventory", skin, Color.BLACK)).row();
				int inventoryColumn = 0;
				for (Item newItem : character.getInventory()) {
					final TextButton newItemButton = new TextButton(newItem.getName(), skin);
					if (newItem.isConsumable()) {
						newItemButton.addListener(getItemListener(newItem));
						inventoryTable.add(newItemButton).size(450, 40);
						if (inventoryColumn == 2) inventoryTable.row();
						inventoryColumn++;
						inventoryColumn %= 3;
					}
				}
	        }
			@Override
	        public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
				hoverText.setText(item.getDescription());
			}
			@Override
	        public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
				hoverText.setText("");
			}
		};
	}
	
	private void resetWeaponTable(String result) {
		buttonSound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
		consoleText.setText(result);
		weaponText.setText(character.getWeapon() != null ? character.getWeapon().getName() : "Unarmed");
		shieldText.setText(character.getShield() != null ? character.getShield().getName() : "Unarmed");
		armorText.setText(character.getArmor() != null ? character.getArmor().getName() : "None");
		legwearText.setText(character.getLegwear() != null ? character.getLegwear().getName() : "None");
		underwearText.setText(character.getUnderwear() != null ? character.getUnderwear().getName() : "None");
		headgearText.setText(character.getHeadgear() != null ? character.getHeadgear().getName() : "None");
		armwearText.setText(character.getArmwear() != null ? character.getArmwear().getName() : "None");			
		footwearText.setText(character.getFootwear() != null ? character.getFootwear().getName() : "None");			
		accessoryText.setText(character.getFirstAccessory() != null ? character.getFirstAccessory().getName() : "None");
		plugText.setText(character.getPlug() != null ? character.getPlug().getName() : "None");
		cageText.setText(character.getCage() != null ? character.getCage().getName() : "None");
		
		weaponText.setColor(character.getWeapon() != null ? Color.GOLD : Color.BROWN);
		shieldText.setColor(character.getShield() != null ? Color.GOLD : Color.BROWN);
		armorText.setColor(character.getArmor() != null ? Color.GOLD : Color.BROWN);
		legwearText.setColor(character.getLegwear() != null ? Color.GOLD : Color.BROWN);
		underwearText.setColor(character.getUnderwear() != null ? Color.GOLD : Color.BROWN);
		headgearText.setColor(character.getHeadgear() != null ? Color.GOLD : Color.BROWN);
		armwearText.setColor(character.getArmwear() != null ? Color.GOLD : Color.BROWN);
		footwearText.setColor(character.getFootwear() != null ? Color.GOLD : Color.BROWN);
		accessoryText.setColor(character.getFirstAccessory() != null ? Color.GOLD : Color.BROWN);
		plugText.setColor(character.getPlug() != null ? Color.GOLD : Color.BROWN);
		cageText.setColor(character.getCage() != null ? Color.GOLD : Color.BROWN);	
		
		saveService.saveDataValue(SaveEnum.PLAYER, character);
		weaponTable.clear();
		weaponTable.add(getLabel("Equipment", skin, Color.BLACK)).row();
		boolean equipmentColumn = false;
		for (Item newItem : character.getInventory()) {
			final TextButton newItemButton = new TextButton(newItem.getName(), skin);
			if (newItem.isEquippable()) {
				newItemButton.addListener(getWeaponListener(newItem));
				if (character.isEquipped(newItem)) {
					newItemButton.setColor(Color.GOLD);
				}
				weaponTable.add(newItemButton).size(500, 40);
				if (equipmentColumn) weaponTable.row();
				equipmentColumn = !equipmentColumn;
			}
		}
	}
	
	private ClickListener getWeaponListener(final Item item) {
		return new ClickListener() {
			@Override
	        public void clicked(InputEvent event, float x, float y) {
				resetWeaponTable(character.equipItem(item));
	        }
			@Override
	        public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
				hoverText.setText(item.getDescription());
			}
			@Override
	        public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
				hoverText.setText("");
			}
		};
	}
	
	private Label getLabel(String label, Skin skin, Color color) {
		Label newLabel = new Label(label, skin);
		newLabel.setColor(color);
		return newLabel;
	}
	
	@Override
	public void buildStage() {}
	
	@Override
	public void render(float delta) {
		super.render(delta);
		if (Gdx.input.isKeyJustPressed(Keys.ENTER) || Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
			showScreen(ScreenEnum.CONTINUE);
		}
	}
	
	@Override
	public void dispose() {
		for(AssetDescriptor<?> path: resourceRequirements) {
			if (path.fileName.equals(AssetEnum.BUTTON_SOUND.getSound().fileName) || path.type == Music.class) continue;
			assetManager.unload(path.fileName);
		}
	}
}
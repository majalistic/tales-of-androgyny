package com.majalis.screens;

import static com.majalis.asset.AssetEnum.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.majalis.asset.AssetEnum;
import com.majalis.character.PlayerCharacter;
import com.majalis.character.Item;
import com.majalis.character.Item.Weapon;
import com.majalis.encounter.Background.BackgroundBuilder;
import com.majalis.save.SaveEnum;
import com.majalis.save.SaveManager.GameContext;
import com.majalis.screens.TownScreen.TownCode;
import com.majalis.save.SaveService;
/*
 * Screen for displaying the character's inventory.
 */
public class InventoryScreen extends AbstractScreen {

	public static final Array<AssetDescriptor<?>> resourceRequirements = new Array<AssetDescriptor<?>>();
	static {
		resourceRequirements.add(AssetEnum.UI_SKIN.getSkin());
		resourceRequirements.add(AssetEnum.CLICK_SOUND.getSound());
		resourceRequirements.add(AssetEnum.WORLD_MAP_MUSIC.getMusic());
	
		AssetEnum[] assets = new AssetEnum[]{ DESTROY_UP, DESTROY_DOWN, DESTROY_HIGHLIGHT, ARROW, CHARACTER_SCREEN, WARRIOR, PALADIN, THIEF, RANGER, MAGE, ENCHANTRESS };
		for (AssetEnum asset: assets) {
			resourceRequirements.add(asset.getTexture());
		}
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
	private final Label rangedWeaponText;
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
	private final Label mouthwearText;
	private final Skin skin;
	
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
		
		if (character.getWeapon() != null && !character.getWeapon().isMelee()) {
			Weapon temp = character.getWeapon();
			character.unequipWeapon();
			character.equipItem(temp);
		}
		
		weaponText = getLabel(character.getWeapon() != null ? character.getWeapon().getName() : "Unarmed", skin, character.getWeapon() != null ? Color.GOLD : Color.BROWN);
		rangedWeaponText  = getLabel(character.getRangedWeapon() != null ? character.getRangedWeapon().getName() : "Unarmed", skin, character.getRangedWeapon() != null ? Color.GOLD : Color.BROWN);
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
		mouthwearText = getLabel(character.getMouthwear() != null ? character.getMouthwear().getName() : "None", skin, character.getMouthwear() != null ? Color.GOLD : Color.BROWN);
		
		weaponText.addListener(new ClickListener() { @Override public void clicked(InputEvent e, float x, float y) { resetWeaponTable(character.unequipWeapon()); }});
		rangedWeaponText.addListener(new ClickListener() { @Override public void clicked(InputEvent e, float x, float y) { resetWeaponTable(character.unequipRangedWeapon()); }});
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
		mouthwearText.addListener(new ClickListener() { @Override public void clicked(InputEvent e, float x, float y) { resetWeaponTable(character.unequipMouthwear()); }});
		
		int xBuffer = 160;
		
		equipmentTable.setPosition(50, 1065);
		this.addActor(equipmentTable);
		equipmentTable.add(getLabel("Weapon:", skin, Color.DARK_GRAY)).width(xBuffer).align(Align.left);
		equipmentTable.add(weaponText).align(Align.left).row();
		equipmentTable.add(getLabel("Ranged:", skin, Color.DARK_GRAY)).width(xBuffer).align(Align.left);
		equipmentTable.add(rangedWeaponText).align(Align.left).row();
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
		equipmentTable.add(getLabel("Mouthwear:", skin, Color.DARK_GRAY)).width(xBuffer).align(Align.left);
		equipmentTable.add(mouthwearText).align(Align.left).row();
		
		
		int inventoryColumn = 0;
		boolean equipmentColumn = false;
		for (final Item item : character.getInventory()) {
			final TextButton itemButton = new TextButton(item.getName(), skin);
			final Button toss = getTossButton();
			if (item.isConsumable()) {
				itemButton.addListener(getItemListener(item));
				toss.addListener(getItemTossListener(item));
				inventoryTable.add(itemButton).size(400, 40);
				inventoryTable.add(toss).size(40, 40);
				if (inventoryColumn == 2) inventoryTable.row();
				inventoryColumn++;
				inventoryColumn %= 3;
			}
			else if (item.isEquippable()) { // this needs to properly equip the item in the correct slot
				itemButton.addListener(getWeaponListener(item));
				toss.addListener(getWeaponTossListener(item));
				if (character.isEquipped(item)) {itemButton.setColor(Color.GOLD); }
				weaponTable.add(itemButton).size(400, 40);
				weaponTable.add(toss).size(40, 40);
				if (equipmentColumn) weaponTable.row();
				equipmentColumn = !equipmentColumn;
			}
		}	
	}
	
	private void resetItemTable(String result) {
		buttonSound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
		consoleText.setText(result);
		saveService.saveDataValue(SaveEnum.PLAYER, character);
		inventoryTable.clear();
		inventoryTable.add(getLabel("Inventory", skin, Color.BLACK)).row();
		ButtonStyle buttonStyle = new ButtonStyle();
		buttonStyle.up = new TextureRegionDrawable(new TextureRegion(assetManager.get(AssetEnum.MINUS.getTexture())));
		buttonStyle.down = new TextureRegionDrawable(new TextureRegion(assetManager.get(AssetEnum.MINUS_DOWN.getTexture())));
		buttonStyle.over = new TextureRegionDrawable(new TextureRegion(assetManager.get(AssetEnum.MINUS_HIGHLIGHT.getTexture())));		
		
		int inventoryColumn = 0;
		for (Item newItem : character.getInventory()) {
			final TextButton newItemButton = new TextButton(newItem.getName(), skin);
			if (newItem.isConsumable()) {
				newItemButton.addListener(getItemListener(newItem));
				final Button toss = getTossButton();
				toss.addListener(getItemTossListener(newItem));
				inventoryTable.add(newItemButton).size(400, 40);
				inventoryTable.add(toss).size(40, 40);
				if (inventoryColumn == 2) inventoryTable.row();
				inventoryColumn++;
				inventoryColumn %= 3;
			}
		}
	}
	
	private void resetWeaponTable(String result) {
		buttonSound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
		consoleText.setText(result);
		weaponText.setText(character.getWeapon() != null ? character.getWeapon().getName() : "Unarmed");
		rangedWeaponText.setText(character.getRangedWeapon() != null ? character.getRangedWeapon().getName() : "Unarmed");
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
		mouthwearText.setText(character.getMouthwear() != null ? character.getMouthwear().getName() : "None");
		
		weaponText.setColor(character.getWeapon() != null ? Color.GOLD : Color.BROWN);
		rangedWeaponText.setColor(character.getRangedWeapon() != null ? Color.GOLD : Color.BROWN);
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
		mouthwearText.setColor(character.getMouthwear() != null ? Color.GOLD : Color.BROWN);	
		
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
				weaponTable.add(newItemButton).size(400, 40);
				final Button toss = getTossButton();
				toss.addListener(getWeaponTossListener(newItem));
				weaponTable.add(toss).size(40, 40);				
				if (equipmentColumn) weaponTable.row();
				equipmentColumn = !equipmentColumn;
			}
		}
	}
	
	private Button getTossButton() {
		ButtonStyle buttonStyle = new ButtonStyle();
		buttonStyle.up = new TextureRegionDrawable(new TextureRegion(assetManager.get(AssetEnum.DESTROY_UP.getTexture())));
		buttonStyle.down = new TextureRegionDrawable(new TextureRegion(assetManager.get(AssetEnum.DESTROY_DOWN.getTexture())));
		buttonStyle.over = new TextureRegionDrawable(new TextureRegion(assetManager.get(AssetEnum.DESTROY_HIGHLIGHT.getTexture())));		
		return new Button(buttonStyle);
	}
	
	private ClickListener getItemListener(final Item item) {
		return new ClickListener() {
			@Override
	        public void clicked(InputEvent event, float x, float y) {
				resetItemTable(character.consumeItem(item).getResult());
				if (item.isTownPortalScroll()) {
					saveService.saveDataValue(SaveEnum.NODE_CODE, 1000);										
					saveService.saveDataValue(SaveEnum.TOWN, TownCode.TOWN);	
					saveService.saveDataValue(SaveEnum.RETURN_CONTEXT, GameContext.TOWN);
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
	
	private ClickListener getItemTossListener(final Item item) {
		return new ClickListener() {
			@Override
	        public void clicked(InputEvent event, float x, float y) {
				resetItemTable(character.discardItem(item));
	        }
			@Override
	        public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
				hoverText.setText("This will discard the " + item.getName() + ".");
			}
			@Override
	        public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
				hoverText.setText("");
			}
		};
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
	
	private ClickListener getWeaponTossListener(final Item item) {
		return new ClickListener() {
			@Override
	        public void clicked(InputEvent event, float x, float y) {
				resetWeaponTable(character.discardItem(item));
	        }
			@Override
	        public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
				hoverText.setText("This will discard the " + item.getName() + ".");
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
		super.dispose();
	}
}
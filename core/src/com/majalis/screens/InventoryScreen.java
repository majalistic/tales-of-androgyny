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
import com.majalis.character.Item.Equipment;
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
		resourceRequirements.add(AssetEnum.EQUIP.getSound());
		resourceRequirements.add(AssetEnum.WORLD_MAP_MUSIC.getMusic());
	
		AssetEnum[] assets = new AssetEnum[]{ DESTROY_UP, DESTROY_DOWN, DESTROY_HIGHLIGHT, MINUS, MINUS_DOWN, MINUS_HIGHLIGHT, ARROW, CHARACTER_SCREEN, WARRIOR, PALADIN, THIEF, RANGER, MAGE, ENCHANTRESS };
		for (AssetEnum asset: assets) {
			resourceRequirements.add(asset.getTexture());
		}
		resourceRequirements.addAll(WorldMapScreen.resourceRequirements);
	}
	
	private final SaveService saveService;
	private final PlayerCharacter character;
	private final Skin skin;
	private final Sound buttonSound;
	private final Sound equipSound;
	private final Label consoleText;
	private final Label hoverText;
	private final Table inventoryTable;
	private final Table weaponTable;
	private final Table equipmentTable;

	public InventoryScreen(ScreenFactory factory, ScreenElements elements, final SaveService saveService, final PlayerCharacter character) {
		super(factory, elements, null);
		this.addActor(new BackgroundBuilder(assetManager.get(AssetEnum.CHARACTER_SCREEN.getTexture())).build()); 
		this.saveService = saveService;
		this.character = character;
		skin = assetManager.get(AssetEnum.UI_SKIN.getSkin());
		buttonSound = assetManager.get(AssetEnum.CLICK_SOUND.getSound()); 
		equipSound = assetManager.get(AssetEnum.EQUIP.getSound()); 
		final TextButton done = new TextButton("Done", skin);
		
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
		inventoryTable.setPosition(100, 550);
		inventoryTable.align(Align.topLeft);
		this.addActor(inventoryTable);
		weaponTable = new Table();
		weaponTable.setPosition(450, 950);
		weaponTable.align(Align.topLeft);
		this.addActor(weaponTable);
		equipmentTable = new Table();
		equipmentTable.align(Align.topLeft);
		equipmentTable.setPosition(50, 1065);
		this.addActor(equipmentTable);
		consoleText = new Label("", skin);
		consoleText.setPosition(425, 1075);
		consoleText.setAlignment(Align.topLeft);
		consoleText.setColor(Color.BROWN);
		this.addActor(consoleText);
		hoverText = new Label("", skin);
		hoverText.setPosition(800, 1075);
		hoverText.setAlignment(Align.topLeft);
		hoverText.setColor(Color.GOLDENROD);
		this.addActor(hoverText);
	}
	
	@Override
	public void buildStage() {
		setItemTable("");
		setWeaponTable("");
	}
	
	private void setItemTable(String result) {
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
	
	private class ItemSlot extends Table {
		private ItemSlot(String slotName, String emptyName, Equipment item) {
			this.add(getLabel(slotName, skin, Color.DARK_GRAY)).width(160).align(Align.left);
			this.add(getLabel(item != null ? item.getName() : emptyName, skin, item != null ? (item.isCursed() ? Color.RED : Color.GOLD) : Color.BROWN)).width(160).align(Align.left);
			if (item != null) {
				this.add(getTossButton()).size(32, 32);
				this.addListener(new ClickListener() { @Override public void clicked(InputEvent e, float x, float y) { equipSound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f); setWeaponTable(character.unequip(item)); }});
			}
		}
	}
	
	private void setWeaponTable(String result) {
		consoleText.setText(result);
		saveService.saveDataValue(SaveEnum.PLAYER, character);
		equipmentTable.clear();
		equipmentTable.add(new ItemSlot("Weapon:", "Unarmed", character.getWeapon())).align(Align.left).row();
		equipmentTable.add(new ItemSlot("Ranged:", "Unarmed", character.getRangedWeapon())).align(Align.left).row();
		equipmentTable.add(new ItemSlot("Shield:", "Unarmed", character.getShield())).align(Align.left).row();
		equipmentTable.add(new ItemSlot("Armor:", "None", character.getArmor())).align(Align.left).row();
		equipmentTable.add(new ItemSlot("Legwear:", "None", character.getLegwear())).align(Align.left).row();
		equipmentTable.add(new ItemSlot("Underwear:", "None", character.getUnderwear())).align(Align.left).row();;
		equipmentTable.add(new ItemSlot("Headgear:", "None", character.getHeadgear())).align(Align.left).row();
		equipmentTable.add(new ItemSlot("Armwear:", "None", character.getArmwear())).align(Align.left).row();
		equipmentTable.add(new ItemSlot("Footwear:", "None", character.getFootwear())).align(Align.left).row();
		equipmentTable.add(new ItemSlot("Accessory:", "None", character.getFirstAccessory())).align(Align.left).row();
		equipmentTable.add(new ItemSlot("Buttwear:", "None", character.getPlug())).align(Align.left).row();
		equipmentTable.add(new ItemSlot("Dickwear:", "None", character.getCage())).align(Align.left).row();
		equipmentTable.add(new ItemSlot("Mouthwear:", "None", character.getMouthwear())).align(Align.left).row();	
		weaponTable.clear();
		weaponTable.add(getLabel("Equipment", skin, Color.BLACK)).align(Align.left).padLeft(150).row();
		
		boolean equipmentColumn = false;
		for (Item newItem : character.getInventory()) {
			if (newItem.isEquippable()) {
				final TextButton newItemButton = new TextButton(newItem.getName(), skin);
				if (newItem.isCursed()) newItemButton.setColor(Color.RED);
				newItemButton.addListener(getWeaponListener(newItem));
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
				buttonSound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
				setItemTable(character.consumeItem(item).getResult());
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
				buttonSound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
				setItemTable(character.discardItem(item));
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
				equipSound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
				setWeaponTable(character.equip((Equipment)item));
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
				buttonSound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
				setWeaponTable(character.discardItem(item));
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
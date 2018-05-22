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
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
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
	
	private final SaveService saveService;
	private final PlayerCharacter character;
	private final Skin skin;
	private final Sound buttonSound;
	private final Label consoleText;
	private final Label hoverText;
	private final Table inventoryTable;
	private final Table weaponTable;
	private final Table equipmentTable;
	private Label weaponText;
	private Label rangedWeaponText;
	private Label shieldText;
	private Label armorText;
	private Label legwearText;
	private Label underwearText;
	private Label headgearText;
	private Label armwearText;
	private Label footwearText;
	private Label accessoryText;
	private Label plugText;
	private Label cageText;
	private Label mouthwearText;
	private Button weaponUnequip;
	private Button rangedWeaponUnequip;
	private Button shieldUnequip;
	private Button armorUnequip;
	private Button legwearUnequip;
	private Button underwearUnequip;
	private Button headgearUnequip;
	private Button armwearUnequip;
	private Button footwearUnequip;
	private Button accessoryUnequip;
	private Button plugUnequip;
	private Button cageUnequip;
	private Button mouthwearUnequip;
	
	public InventoryScreen(ScreenFactory factory, ScreenElements elements, final SaveService saveService, final PlayerCharacter character) {
		super(factory, elements, null);
		this.addActor(new BackgroundBuilder(assetManager.get(AssetEnum.CHARACTER_SCREEN.getTexture())).build()); 
		this.saveService = saveService;
		this.character = character;
		skin = assetManager.get(AssetEnum.UI_SKIN.getSkin());
		buttonSound = assetManager.get(AssetEnum.CLICK_SOUND.getSound()); 
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
		weaponTable.setPosition(950, 1000);
		weaponTable.align(Align.top);
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
		hoverText.setPosition(900, 1075);
		hoverText.setAlignment(Align.topLeft);
		hoverText.setColor(Color.GOLDENROD);
		this.addActor(hoverText);

		if (character.getWeapon() != null && !character.getWeapon().isMelee()) {
			Weapon temp = character.getWeapon();
			character.unequipWeapon();
			character.equip(temp);
		}
		
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
	
	private void setWeaponTable(String result) {
		consoleText.setText(result);
		saveService.saveDataValue(SaveEnum.PLAYER, character);
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
		
		weaponText.addListener(new ClickListener() { @Override public void clicked(InputEvent e, float x, float y) { setWeaponTable(character.unequipWeapon()); }});
		rangedWeaponText.addListener(new ClickListener() { @Override public void clicked(InputEvent e, float x, float y) { setWeaponTable(character.unequipRangedWeapon()); }});
		shieldText.addListener(new ClickListener() { @Override public void clicked(InputEvent e, float x, float y) { setWeaponTable(character.unequipShield()); }});
		armorText.addListener(new ClickListener() { @Override public void clicked(InputEvent e, float x, float y) { setWeaponTable(character.unequipArmor()); }});
		legwearText.addListener(new ClickListener() { @Override public void clicked(InputEvent e, float x, float y) { setWeaponTable(character.unequipLegwear()); }});
		underwearText.addListener(new ClickListener() { @Override public void clicked(InputEvent e, float x, float y) { setWeaponTable(character.unequipUnderwear()); }});
		headgearText.addListener(new ClickListener() { @Override public void clicked(InputEvent e, float x, float y) { setWeaponTable(character.unequipHeadgear()); }});
		armwearText.addListener(new ClickListener() { @Override public void clicked(InputEvent e, float x, float y) { setWeaponTable(character.unequipArmwear()); }});
		footwearText.addListener(new ClickListener() { @Override public void clicked(InputEvent e, float x, float y) { setWeaponTable(character.unequipFootwear()); }});
		accessoryText.addListener(new ClickListener() { @Override public void clicked(InputEvent e, float x, float y) { setWeaponTable(character.unequipAccessory()); }});
		plugText.addListener(new ClickListener() { @Override public void clicked(InputEvent e, float x, float y) { setWeaponTable(character.unequipPlug()); }});
		cageText.addListener(new ClickListener() { @Override public void clicked(InputEvent e, float x, float y) { setWeaponTable(character.unequipCage()); }});
		mouthwearText.addListener(new ClickListener() { @Override public void clicked(InputEvent e, float x, float y) { setWeaponTable(character.unequipMouthwear()); }});
		
		weaponUnequip = getTossButton();
		rangedWeaponUnequip = getTossButton();
		shieldUnequip = getTossButton();
		armorUnequip = getTossButton();
		legwearUnequip = getTossButton();
		underwearUnequip = getTossButton();
		headgearUnequip = getTossButton();
		armwearUnequip = getTossButton();
		footwearUnequip = getTossButton();
		accessoryUnequip = getTossButton();
		plugUnequip = getTossButton();
		cageUnequip = getTossButton();
		mouthwearUnequip = getTossButton();
		
		weaponUnequip.addListener(new ClickListener() { @Override public void clicked(InputEvent e, float x, float y) { setWeaponTable(character.unequipWeapon()); }});
		rangedWeaponUnequip.addListener(new ClickListener() { @Override public void clicked(InputEvent e, float x, float y) { setWeaponTable(character.unequipRangedWeapon()); }});
		shieldUnequip.addListener(new ClickListener() { @Override public void clicked(InputEvent e, float x, float y) { setWeaponTable(character.unequipShield()); }});
		armorUnequip.addListener(new ClickListener() { @Override public void clicked(InputEvent e, float x, float y) { setWeaponTable(character.unequipArmor()); }});
		legwearUnequip.addListener(new ClickListener() { @Override public void clicked(InputEvent e, float x, float y) { setWeaponTable(character.unequipLegwear()); }});
		underwearUnequip.addListener(new ClickListener() { @Override public void clicked(InputEvent e, float x, float y) { setWeaponTable(character.unequipUnderwear()); }});
		headgearUnequip.addListener(new ClickListener() { @Override public void clicked(InputEvent e, float x, float y) { setWeaponTable(character.unequipHeadgear()); }});
		armwearUnequip.addListener(new ClickListener() { @Override public void clicked(InputEvent e, float x, float y) { setWeaponTable(character.unequipArmwear()); }});
		footwearUnequip.addListener(new ClickListener() { @Override public void clicked(InputEvent e, float x, float y) { setWeaponTable(character.unequipFootwear()); }});
		accessoryUnequip.addListener(new ClickListener() { @Override public void clicked(InputEvent e, float x, float y) { setWeaponTable(character.unequipAccessory()); }});
		plugUnequip.addListener(new ClickListener() { @Override public void clicked(InputEvent e, float x, float y) { setWeaponTable(character.unequipPlug()); }});
		cageUnequip.addListener(new ClickListener() { @Override public void clicked(InputEvent e, float x, float y) { setWeaponTable(character.unequipCage()); }});
		mouthwearUnequip.addListener(new ClickListener() { @Override public void clicked(InputEvent e, float x, float y) { setWeaponTable(character.unequipMouthwear()); }});
		
		if (character.getWeapon() == null) weaponUnequip.addAction(Actions.hide());
		if (character.getRangedWeapon() == null) rangedWeaponUnequip.addAction(Actions.hide());
		if (character.getShield() == null) shieldUnequip.addAction(Actions.hide());
		if (character.getArmor() == null) armorUnequip.addAction(Actions.hide());
		if (character.getLegwear() == null)	legwearUnequip.addAction(Actions.hide());	
		if (character.getUnderwear() == null) underwearUnequip.addAction(Actions.hide());
		if (character.getHeadgear() == null) headgearUnequip.addAction(Actions.hide());	
		if (character.getArmwear() == null) armwearUnequip.addAction(Actions.hide());
		if (character.getFootwear() == null) footwearUnequip.addAction(Actions.hide());
		if (character.getFirstAccessory() == null) accessoryUnequip.addAction(Actions.hide());
		if (character.getPlug() == null) plugUnequip.addAction(Actions.hide());
		if (character.getCage() == null) cageUnequip.addAction(Actions.hide());
		if (character.getMouthwear() == null) mouthwearUnequip.addAction(Actions.hide());
		
		int xBuffer = 160;
		
		equipmentTable.clear();
		equipmentTable.add(getLabel("Weapon:", skin, Color.DARK_GRAY)).width(xBuffer).align(Align.left);
		equipmentTable.add(weaponText).align(Align.left);
		equipmentTable.add(weaponUnequip).size(35, 35).row();
		equipmentTable.add(getLabel("Ranged:", skin, Color.DARK_GRAY)).width(xBuffer).align(Align.left);
		equipmentTable.add(rangedWeaponText).align(Align.left);
		equipmentTable.add(rangedWeaponUnequip).size(35, 35).row();
		equipmentTable.add(getLabel("Shield:", skin, Color.DARK_GRAY)).width(xBuffer).align(Align.left);
		equipmentTable.add(shieldText).align(Align.left);
		equipmentTable.add(shieldUnequip).size(35, 35).row();
		equipmentTable.add(getLabel("Armor:", skin, Color.DARK_GRAY)).width(xBuffer).align(Align.left);
		equipmentTable.add(armorText).align(Align.left);
		equipmentTable.add(armorUnequip).size(35, 35).row();
		equipmentTable.add(getLabel("Legwear:", skin, Color.DARK_GRAY)).width(xBuffer).align(Align.left);
		equipmentTable.add(legwearText).align(Align.left);
		equipmentTable.add(legwearUnequip).size(35, 35).row();
		equipmentTable.add(getLabel("Underwear:", skin, Color.DARK_GRAY)).width(xBuffer).align(Align.left);
		equipmentTable.add(underwearText).align(Align.left);
		equipmentTable.add(underwearUnequip).size(35, 35).row();
		equipmentTable.add(getLabel("Headgear:", skin, Color.DARK_GRAY)).width(xBuffer).align(Align.left);
		equipmentTable.add(headgearText).align(Align.left);
		equipmentTable.add(headgearUnequip).size(35, 35).row();
		equipmentTable.add(getLabel("Armwear:", skin, Color.DARK_GRAY)).width(xBuffer).align(Align.left);
		equipmentTable.add(armwearText).align(Align.left);
		equipmentTable.add(armwearUnequip).size(35, 35).row();
		equipmentTable.add(getLabel("Footwear:", skin, Color.DARK_GRAY)).width(xBuffer).align(Align.left);
		equipmentTable.add(footwearText).align(Align.left);
		equipmentTable.add(footwearUnequip).size(35, 35).row();
		equipmentTable.add(getLabel("Accessory:", skin, Color.DARK_GRAY)).width(xBuffer).align(Align.left);
		equipmentTable.add(accessoryText).align(Align.left);
		equipmentTable.add(accessoryUnequip).size(35, 35).row();
		equipmentTable.add(getLabel("Buttwear:", skin, Color.DARK_GRAY)).width(xBuffer).align(Align.left);
		equipmentTable.add(plugText).align(Align.left);
		equipmentTable.add(plugUnequip).size(35, 35).row();
		equipmentTable.add(getLabel("Dickwear:", skin, Color.DARK_GRAY)).width(xBuffer).align(Align.left);
		equipmentTable.add(cageText).align(Align.left);
		equipmentTable.add(cageUnequip).size(35, 35).row();
		equipmentTable.add(getLabel("Mouthwear:", skin, Color.DARK_GRAY)).width(xBuffer).align(Align.left);
		equipmentTable.add(mouthwearText).align(Align.left);
		equipmentTable.add(mouthwearUnequip).size(35, 35).row();
		
		weaponTable.clear();
		weaponTable.add(getLabel("Equipment", skin, Color.BLACK)).row();
		
		boolean equipmentColumn = false;
		for (Item newItem : character.getInventory()) {
			final TextButton newItemButton = new TextButton(newItem.getName(), skin);
			if (newItem.isEquippable()) {
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
				buttonSound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
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
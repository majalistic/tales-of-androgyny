package com.majalis.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.OrderedMap;
import com.majalis.asset.AssetEnum;
import com.majalis.character.Item;
import com.majalis.character.Item.EffectType;
import com.majalis.character.Item.Potion;
import com.majalis.character.Item.Weapon;
import com.majalis.character.Item.WeaponType;
import com.majalis.character.PlayerCharacter;
import com.majalis.encounter.Background;
import com.majalis.save.SaveEnum;
import com.majalis.save.SaveService;
import com.majalis.screens.TimeOfDay;

public class ShopScene extends Scene {

	private final SaveService saveService;
	private final Skin skin;
	private final Sound buttonSound;
	private final Sound itemSound;
	private final Shop shop;
	private final PlayerCharacter character;
	private final Label console;
	private final Label money;
	
	public static class Shop {
		private Array<Weapon> weapons;
		private Array<Potion> consumables;
		private ShopCode shopCode;
		private boolean done;
		private Shop() {}
		private Shop(ShopCode shopCode) {
			this.shopCode = shopCode;
			this.weapons = new Array<Weapon>();
			this.consumables = new Array<Potion>();
			done = false;
		}
		public String getShopCode() {
			return shopCode.toString();
		}
	}
	
	public ShopScene(OrderedMap<Integer, Scene> sceneBranches, int sceneCode, final SaveService saveService, AssetManager assetManager, final PlayerCharacter character, Background background, final ShopCode shopCode, Shop loadedShop) {
		super(sceneBranches, sceneCode);
		this.saveService = saveService;
		this.addActor(background);
		this.character = character;
		
		skin = assetManager.get(AssetEnum.UI_SKIN.getSkin());
		buttonSound = assetManager.get(AssetEnum.BUTTON_SOUND.getSound());
		itemSound = assetManager.get(AssetEnum.EQUIP.getSound());
		
		final Group inventoryGroup = new Group();
		Image inventoryBox = new Image(assetManager.get(AssetEnum.BATTLE_TEXTBOX.getTexture()));
		Image moneyBox = new Image(assetManager.get(AssetEnum.TEXT_BOX.getTexture()));
		
		inventoryGroup.addActor(inventoryBox);
		this.addActor(inventoryGroup);
		this.addActor(moneyBox);
		inventoryBox.setBounds(100, 100, 800, 1000);
		moneyBox.setBounds(1500, 900, 300, 100);
		
		final Image hoverBox = new Image(assetManager.get(AssetEnum.BATTLE_HOVER.getTexture()));
		
		hoverBox.setBounds(950, 400, 850, 400);
		
		this.console = new Label("", skin);
		this.money = new Label(String.valueOf(character.getMoney())+" Gold", skin);
		this.money.setColor(Color.GOLDENROD);
		console.setPosition(1300, 850);
		this.addActor(console);
		money.setPosition(1600, 940);
		this.addActor(money);
		
		final TextButton done = new TextButton("Done", skin);
		
		done.addListener(
			new ClickListener() {
				@Override
		        public void clicked(InputEvent event, float x, float y) {
					buttonSound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
					nextScene();		   
		        }
			}
		);
		done.setPosition(1522, 100);
		
		// need to create methods for selling
		// need to show description of items (should be an attribute of an item)	
		// need to let a player equip items as they're purchased	
		// need to split different types of items into different lists - weapons, armors, accessories, consumables, skills
		
		shop = initShop(shopCode, loadedShop);
		
		saveService.saveDataValue(SaveEnum.SHOP, shop);
		saveService.saveDataValue(SaveEnum.PLAYER, character);
		
		final Table table = new Table();
		table.align(Align.top);
		
		ScrollPane techniquePane = new ScrollPane(table);
		techniquePane.setScrollingDisabled(true, false);
		techniquePane.setOverscroll(false, false);
		techniquePane.setBounds(200, 175, 675, 825);
		
		this.addActor(techniquePane);
		
		for (final Weapon weapon: shop.weapons) {
			final TextButton weaponButton = new TextButton(weapon.getName() + " - " + weapon.getValue() + "G", skin);
			final Label description = new Label(weapon.getDescription(), skin);		
			description.setWrap(true);
			description.setColor(Color.FOREST);
			description.setAlignment(Align.top);
			final ScrollPane pane = new ScrollPane(description);
			pane.setBounds(1060, 415, 675, 350);
			pane.setScrollingDisabled(true, false);
			weaponButton.addListener(new ClickListener() {
				@Override
		        public void clicked(InputEvent event, float x, float y) {
					if (buyItem(weapon, shopCode)) {
						itemSound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
						addActor(done);
						weaponButton.addAction(Actions.removeActor());
						shop.weapons.removeValue(weapon, true);
						shop.done = true;
						money.setText(String.valueOf(character.getMoney())+" Gold");
						console.setText("You purchase the " + weapon.getName() + ".");
						saveService.saveDataValue(SaveEnum.SHOP, shop);
						saveService.saveDataValue(SaveEnum.PLAYER, character);
					}
					else {
						console.setText("You can't afford the " + weapon.getName());
					}
					
		        }
				@Override
		        public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
					inventoryGroup.addActor(hoverBox);
					inventoryGroup.addActor(pane);
				}
				@Override
		        public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
					inventoryGroup.removeActor(hoverBox);
					inventoryGroup.removeActor(pane);
				}
			});
			
			table.add(weaponButton).size(500, 60).row();
		}
		
		for (final Potion potion: shop.consumables) {
			shop.done = true; // temporary measure to make the potion shop function properly
			final TextButton potionButton = new TextButton(potion.getName() + " - " + potion.getValue() / ( shopCode == ShopCode.GADGETEER_SHOP ? 3 : 1 ) + "G", skin);
			final Label description = new Label(potion.getDescription(), skin);
			description.setWrap(true);
			description.setColor(Color.FOREST);
			description.setAlignment(Align.top);
			final ScrollPane pane = new ScrollPane(description);
			pane.setBounds(1060, 415, 675, 350);
			pane.setScrollingDisabled(true, false);
			
			potionButton.addListener(new ClickListener() {
				@Override
		        public void clicked(InputEvent event, float x, float y) {
					if (buyItem(potion, shopCode)) {
						itemSound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
						addActor(done);
						potionButton.addAction(Actions.removeActor());
						shop.consumables.removeValue(potion, true);
						
						money.setText(String.valueOf(character.getMoney())+" Gold");
						console.setText("You purchase the " + potion.getName() + ".");
						saveService.saveDataValue(SaveEnum.SHOP, shop);
						saveService.saveDataValue(SaveEnum.PLAYER, character);
					}
					else {
						console.setText("You can't afford the " + potion.getName());
					}
					
		        }
				@Override
		        public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
					inventoryGroup.addActor(hoverBox);
					inventoryGroup.addActor(pane);
				}
				@Override
		        public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
					inventoryGroup.removeActor(hoverBox);
					inventoryGroup.removeActor(pane);
				}
			});
			table.add(potionButton).size(500, 60).row();
		}
		
		if (shopCode.isTinted()) {
			background.setColor(TimeOfDay.getTime(character.getTime()).getColor());
		}
		
		if (shop.done || shopCode != ShopCode.FIRST_STORY) addActor(done);
	}

	private Shop initShop(ShopCode shopCode, Shop shop) {
		if (shop != null) {
			if (shopCode == ShopCode.SHOP) {
				shop.consumables.addAll(getShopRestock(shopCode));
			}
			return shop;
		}
		shop = new Shop(shopCode);
		switch (shopCode) {
			case FIRST_STORY:
			case WEAPON_SHOP:
				for (WeaponType type: WeaponType.values()) {
					if (type.isBuyable()) {
						shop.weapons.add(new Weapon(type));	
					}									
				}
				if (shopCode == ShopCode.WEAPON_SHOP) {
					for (WeaponType type: WeaponType.values()) {
						if (type.isBuyable()) {
							shop.weapons.add(new Weapon(type, 1));	
						}									
					}
				}
				break;
			case SHOP:
				for (int ii = 0; ii < 4; ii++) {
					shop.consumables.add(new Potion(5, EffectType.MEAT));
				}
				shop.consumables.add(new Potion(10, EffectType.BANDAGE));
				shop.consumables.add(new Potion(10, EffectType.BANDAGE));
				shop.consumables.add(new Potion(10, EffectType.BANDAGE));
				for (int ii = 10; ii <= 20; ii += 10) {
					shop.consumables.add(new Potion(ii));
					shop.consumables.add(new Potion(ii));
				}
				shop.consumables.add(new Potion(3, EffectType.BONUS_STRENGTH));
				shop.consumables.add(new Potion(3, EffectType.BONUS_AGILITY));
				shop.consumables.add(new Potion(3, EffectType.BONUS_ENDURANCE));
				break;
			case GADGETEER_SHOP:
				shop.consumables.add(new Potion(15));
				shop.consumables.add(new Potion(15));
				shop.consumables.add(new Potion(3, EffectType.BONUS_STRENGTH));
				shop.consumables.add(new Potion(3, EffectType.BONUS_STRENGTH));
				shop.consumables.add(new Potion(3, EffectType.BONUS_AGILITY));
				shop.consumables.add(new Potion(3, EffectType.BONUS_AGILITY));
				shop.consumables.add(new Potion(3, EffectType.BONUS_ENDURANCE));
				shop.consumables.add(new Potion(3, EffectType.BONUS_ENDURANCE));
				break;
		}
		if (shopCode == ShopCode.SHOP) {
			shop.consumables.addAll(getShopRestock(shopCode));
		}
		return shop;
	}
	
	private Array<? extends Potion> getShopRestock(ShopCode shopCode) {
		Array<Potion> restock = new Array<Potion>();
		for (int ii = 0; ii < character.needShopRestock(shopCode); ii++) {
			switch(shopCode) {
				case SHOP:
					restock.add(new Potion(5, EffectType.MEAT));
					restock.add(new Potion(5, EffectType.MEAT));
					restock.add(new Potion(10, EffectType.BANDAGE));
					restock.add(new Potion(15));
					restock.add(new Potion(15));
					break;
				default: ;
			}
		}
		return restock;
	}

	private boolean buyItem(Item item, ShopCode shopCode) {
		return character.buyItem(item, item.getValue() / (shopCode == ShopCode.GADGETEER_SHOP ? 3 : 1));
	}

	@Override
	public void setActive() {
		isActive = true;
		this.removeAction(Actions.hide());
		this.addAction(Actions.visible(true));
		this.addAction(Actions.show());
		this.setBounds(0, 0, 2000, 2000);
		saveService.saveDataValue(SaveEnum.SCENE_CODE, sceneCode);
	}

	private void nextScene() {
		sceneBranches.get(sceneBranches.orderedKeys().get(0)).setActive();
		isActive = false;
		addAction(Actions.hide());
	}
	
	public enum ShopCode {
		FIRST_STORY, SHOP, WEAPON_SHOP, GADGETEER_SHOP;
	
		public AssetDescriptor<Texture> getBackground() {
			switch(this) {
				case GADGETEER_SHOP:
					return AssetEnum.DEFAULT_BACKGROUND.getTexture();
				case WEAPON_SHOP:
				case SHOP:
				case FIRST_STORY:
					return AssetEnum.TOWN_BG.getTexture();
				default:
					break;
				
				}
			return null;
		}

		public AssetDescriptor<Texture> getForeground() {
			switch(this) {
				case FIRST_STORY:
				case SHOP:
					return AssetEnum.SHOPKEEP.getTexture();
				case GADGETEER_SHOP:
					return AssetEnum.GADGETEER.getTexture();
				case WEAPON_SHOP:
					return AssetEnum.TRAINER.getTexture();
				default:
					break;
			}
			return null;
		}
		
		public int getX() {
			return 800;
		}

		public int getY() {
			return -100;
		}

		public boolean isTinted() {
			return this != GADGETEER_SHOP;
		}
	}
	
}

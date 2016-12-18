package com.majalis.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.OrderedMap;
import com.majalis.asset.AssetEnum;
import com.majalis.character.Item;
import com.majalis.character.Item.Potion;
import com.majalis.character.Item.Weapon;
import com.majalis.character.PlayerCharacter;
import com.majalis.encounter.Background;
import com.majalis.save.SaveEnum;
import com.majalis.save.SaveService;

public class ShopScene extends Scene {

	private final SaveService saveService;
	private final Skin skin;
	private final Sound buttonSound;
	private final Shop shop;
	private final PlayerCharacter character;
	private final Label console;
	private final Label money;
	
	public static class Shop{
		private Array<Weapon> weapons;
		private Array<Potion> consumables;
		private ShopCode shopCode;
		private boolean done;
		private Shop(){}
		private Shop(ShopCode shopCode){
			this.shopCode = shopCode;
			this.weapons = new Array<Weapon>();
			this.consumables = new Array<Potion>();
			done = false;
		}
		public String getShopCode(){
			return shopCode.toString();
		}
	}
	
	public ShopScene(OrderedMap<Integer, Scene> sceneBranches, int sceneCode, final SaveService saveService, AssetManager assetManager, final PlayerCharacter character, Background background, ShopCode shopCode, Shop loadedShop) {
		super(sceneBranches, sceneCode);
		this.saveService = saveService;
		this.addActor(background);
		this.character = character;
		
		skin = assetManager.get(AssetEnum.UI_SKIN.getPath(), Skin.class);
		buttonSound = assetManager.get(AssetEnum.BUTTON_SOUND.getPath(), Sound.class);
		
		this.console = new Label("", skin);
		this.money = new Label(String.valueOf(character.getMoney())+" Gold", skin);
		console.setPosition(1300, 800);
		this.addActor(console);
		money.setPosition(1500, 900);
		this.addActor(money);
		
		final TextButton done = new TextButton("Done", skin);
		
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
		
		// need to create methods for selling
		// need to show description of items (should be an attribute of an item)	
		// need to let a player equip items as they're purchased	
		// need to split different types of items into different lists - weapons, armors, accessories, consumables, skills
		
		shop = initShop(shopCode, loadedShop);
		
		final Table table = new Table();
		
		for (final Weapon weapon: shop.weapons){
			final TextButton weaponButton = new TextButton(weapon.getName() + " (" + weapon.getValue() + ")", skin);
			weaponButton.addListener(new ClickListener(){
				@Override
		        public void clicked(InputEvent event, float x, float y) {
					buttonSound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
					if (buyItem(weapon)){
						addActor(done);
						weaponButton.addAction(Actions.removeActor());
						shop.weapons.removeValue(weapon, true);
						shop.done = true;
						money.setText(String.valueOf(character.getMoney())+" Gold");
						saveService.saveDataValue(SaveEnum.SHOP, shop);
						saveService.saveDataValue(SaveEnum.PLAYER, character);
					}
					else {
						console.setText("You can't afford the " + weapon.getName());
					}
					
		        }
			});
			
			table.add(weaponButton).size(400, 60).row();
		}
		
		for (final Potion potion: shop.consumables){
			final TextButton potionButton = new TextButton(potion.getName() + " (" + potion.getValue() + ")", skin);
			potionButton.addListener(new ClickListener(){
				@Override
		        public void clicked(InputEvent event, float x, float y) {
					buttonSound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
					if (buyItem(potion)){
						addActor(done);
						potionButton.addAction(Actions.removeActor());
						shop.consumables.removeValue(potion, true);
						shop.done = true;
						money.setText(String.valueOf(character.getMoney())+" Gold");
						saveService.saveDataValue(SaveEnum.SHOP, shop);
						saveService.saveDataValue(SaveEnum.PLAYER, character);
					}
					else {
						console.setText("You can't afford the " + potion.getName());
					}
					
		        }
			});
			table.add(potionButton).size(400, 60).row();
		}
		
		table.setPosition(500, 800);
		this.addActor(table);	
		if (shop.done) addActor(done);
	}

	private Shop initShop(ShopCode shopCode, Shop shop) {
		if (shop != null) return shop;
		shop = new Shop(shopCode);
		switch (shopCode) {
			case FIRST_STORY:
				shop.weapons.add(new Weapon("Rapier"));
				shop.weapons.add(new Weapon("Cutlass"));
				shop.weapons.add(new Weapon("Broadsword"));
				break;
			case SHOP:
				for (int ii = 10; ii <= 20; ii += 10){
					shop.consumables.add(new Potion(ii));
					shop.consumables.add(new Potion(ii));
					shop.consumables.add(new Potion(ii));
					shop.consumables.add(new Potion(ii));
				}
				break;
			default:
				break;
		}
		return shop;
	}
	
	private boolean buyItem(Item item){
		return character.buyItem(item, item.getValue());
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
		FIRST_STORY, SHOP
	}
	
}

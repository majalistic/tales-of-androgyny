package com.majalis.character;

import com.badlogic.gdx.utils.IntArray;

/*
 * Contains all the information about a character's armor.
 */
public class Armor extends Item{
	/* Armor */
	private final ArmorType type;
	private int durability;
	
	public Armor() { type = null; }
	
	public Armor(ArmorType type) {
		this.type = type;
		this.durability = type.getMaxDurability();
	}
	
	public void modDurability(int mod) {
		durability += mod;
		if (durability < 0 ) durability = 0;
	}
	
	@Override
	public int getValue() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected ItemEffect getUseEffect() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		return type.getLabel();
	}

	@Override
	public String getDescription() {
		return type.getLabel();
	}
	
	@Override
	public boolean isEquippable() {
		return true;
	}
	
	@Override 
	public boolean equals(Object o) {
		if (o == null || o.getClass() != Armor.class) return false;
		Armor compare = (Armor) o;
		return super.equals(o) && compare.type == this.type;
	}
	
	public void refresh() { durability = type.getMaxDurability(); }
	
	public int getDurability() {
		return durability;
	}
	public boolean coversTop() { return type.coversTop(); }
	public boolean coversBottom() { return type.coversBottom(); }
	public boolean isUnderwear() { return type.isUnderwear(); }
	public ChastityCage getCage() { return type.getCage(); }
	public Plug getPlug() { return type.getPlug(); }
	public int getShockAbsorption() { 
		int durabilityLoss = type.getMaxDurability() - durability;
		int shockIndex = 0;
		for (int value : type.getDurability().items) {
			if (durabilityLoss >= value) {
				shockIndex++;
				durabilityLoss -= value;
			}
		}
		// if shockIndex == type.getShockAbsorption, the durability is 0 and the item is broken
		if (shockIndex >= type.getShockAbsorption().size) return 0;
		return type.getShockAbsorption().get(shockIndex);
	}
	public boolean coversNipples() { return type.coversNipples(); }
	public boolean isPorous() { return type.isPorous(); }
	public boolean isHeatResistant() { return type.isHeatResistant(); }
	public boolean isFlameRetardant() { return type.isFlameRetardant(); }
	public boolean isAcidResistant() { return type.isAcidResistant(); }
	public boolean isAlive() { return type.isAlive(); }
	public boolean coversAnus() { return type.coversAnus(); }
	public boolean slipOff() { return type.slipOff(); }
	public boolean showsErection() { return type.showsErection(); }
	public boolean showsRear() { return type.showsRear(); }
	public boolean showsHips() { return type.showsHips(); }
	
	// may want to refactor this into dedicated tops/bottoms/overalls
	public enum ArmorType {
		NO_TOP ("None"), // currently not used - should be used in place of 'null'
		NO_BOTTOM ("None"), // currently not used - should be used in place of 'null'
		CLOTH_TOP ("Cloth Armor"),
		BREASTPLATE ("Breastplate"),
		SKIRT ("Skirt"),
		BATTLE_SKIRT ("Battle Skirt"),
		SHORTS ("Shorts"),
		UNDERWEAR ("Underwear"),
		
		LIGHT_ENEMY_ARMOR ("Light Armor"),
		MEDIUM_ENEMY_ARMOR ("Medium Armor"),
		HEAVY_ENEMY_ARMOR ("Heavy Armor"), 
		LIGHT_ENEMY_LEGWEAR ("Light Legwear"),
		MEDIUM_ENEMY_LEGWEAR ("Medium Legwear"),
		;
		private final String label;
		private ArmorType(String label) { this.label = label; }
		
		public String getLabel() { return label; }
		
		private boolean coversTop() { return this == NO_TOP || this == CLOTH_TOP || this == BREASTPLATE; }
		private boolean coversBottom() { return this == NO_BOTTOM || this == SKIRT || this == BATTLE_SKIRT || this == SHORTS; }
		private boolean isUnderwear() { return this == UNDERWEAR; }
		private ChastityCage getCage() { return null; }
		private Plug getPlug() { return null; }
		private IntArray getDurability() { return this == UNDERWEAR ? new IntArray(new int[]{2}) : this == LIGHT_ENEMY_ARMOR || this == LIGHT_ENEMY_LEGWEAR ? new IntArray(new int[]{3, 3}) : new IntArray(new int[]{5, 5});  }
		private IntArray getShockAbsorption() { return this == UNDERWEAR ? new IntArray(new int[]{1}) : this == LIGHT_ENEMY_ARMOR || this == LIGHT_ENEMY_LEGWEAR ? new IntArray(new int[]{4, 1}) : new IntArray(new int[]{6, 2}); }
		private boolean coversNipples() { return this == CLOTH_TOP || this == BREASTPLATE; }
		private boolean isPorous() { return this != BREASTPLATE; }
		private boolean isHeatResistant() { return false; }
		private boolean isFlameRetardant() { return this == BREASTPLATE; }
		private boolean isAcidResistant() { return false; }
		private boolean isAlive() { return false; }
		private boolean coversAnus() { return this == SHORTS; }
		private boolean slipOff() { return false; }
		private boolean showsErection() { return true; }
		private boolean showsRear() { return true; }
		private boolean showsHips() { return true; }
		private int getMaxDurability() { int maxDurability = 0; for (int value : getDurability().items) maxDurability += value; return maxDurability; }
	}
}

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
	public int getValue() { return type.getValue(); }

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
		return type.getDescription();
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
	public int getDestructionLevel() {
		int durabilityLoss = type.getMaxDurability() - durability;
		int destructionLevel = 0;
		for (int value : type.getDurability().items) {
			if (durabilityLoss >= value) {
				destructionLevel++;
				durabilityLoss -= value;
			}
		}
		return destructionLevel;		
	}
	public int getShockAbsorption() { 
		int shockIndex = getDestructionLevel();
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
	public boolean isShield() { return type.isShield(); }
	public boolean isArmwear() { return type.isArmwear(); }
	public boolean isFootwear() { return type.isFootwear(); }
	public boolean isHeadgear() { return type.isHeadgear(); }
	
	// may want to refactor this into dedicated tops/bottoms/overalls
	public enum ArmorType {
		NO_TOP ("None", new int[]{0}, new int[]{0}), // currently not used - should be used in place of 'null'
		NO_BOTTOM ("None", new int[]{0}, new int[]{0}), // currently not used - should be used in place of 'null'
		CLOTH_TOP ("Cloth Armor", 10, new int[]{3, 3}, new int[]{3, 1}),
		BREASTPLATE ("Breastplate", 50, new int[]{8, 8}, new int[]{6, 2}),
		DIAMOND_PLATE ("Diamond Plate", 200, new int[]{5, 5}, new int[]{8, 4}),
		SKIRT ("Skirt", 10, new int[]{3, 3}, new int[]{3, 1}),
		BATTLE_SKIRT ("Battle Skirt", 50, new int[]{6, 6}, new int[]{6, 2}),
		SHORTS ("Shorts", new int[]{3, 3}, new int[]{3, 1}),
		UNDERWEAR ("Underwear", new int[]{2}, new int[]{1}),
		
		SHIELD ("Shield", 10, new int[]{15, 15}, new int[]{50, 50}),
		REINFORCED_SHIELD ("Reinforced Shield", 50, new int[]{30, 30}, new int[]{50, 50}),
		
		GAUNTLET ("Gauntlet", 5, new int[]{0}, new int[]{0}),
		HELMET ("Helmet", 5, new int[]{0}, new int[]{0}),
		SHOES ("Shoes", 1, new int[]{0}, new int[]{0}),
		SABATONS ("Sabatons", 5, new int[]{15, 15}, new int[]{50, 50}),
		
		LIGHT_ENEMY_ARMOR ("Light Armor", new int[]{2, 2}, new int[]{2, 1}),
		MEDIUM_ENEMY_ARMOR ("Medium Armor", new int[]{6, 6}, new int[]{6, 2}),
		HEAVY_ENEMY_ARMOR ("Heavy Armor", new int[]{12, 6, 6}, new int[]{10, 4, 2}), 
		LIGHT_ENEMY_LEGWEAR ("Light Legwear", new int[]{2, 2}, new int[]{2, 1}),
		MEDIUM_ENEMY_LEGWEAR ("Medium Legwear", new int[]{6, 6}, new int[]{6, 2}),
		;
		private final String label;
		private final int value;
		private final IntArray durability;
		private final IntArray shockAbsorption;
		private ArmorType(String label, int[] durability, int[] shockAbsorption) {
			this(label, 0, durability, shockAbsorption);
		}
		
		private ArmorType(String label, int value, int[] durability, int[] shockAbsorption) {
			this.label = label; 
			this.value = value;
			this.durability = new IntArray(durability);
			this.shockAbsorption = new IntArray(shockAbsorption);
		}
		
		private String getLabel() { return label; }
		private String getDescription() { 
			return 
			isArmwear() ? "Covers the arms." :
			isFootwear() ? "Covers the feet and shins." :
			isHeadgear() ? "Covers the head." :
			isShield() ? "A shield which will block attacks while guarding.\nCan absorb " + getMaxDurability() + " damage before breaking." :
			((coversTop() ? (coversBottom() ? "Protects both upper and lower body." : "Protects upper body.") : coversBottom() ? "Protects lower body." : isUnderwear() ? "Worn under clothing" : "") + "\n" +
			(coversAnus() ? "This protects the backdoor.\n" : "") +
			getDurabilityDescription()); 
		}
		private int getValue() { return value; }
		
		private boolean isArmwear() { return this == GAUNTLET; }
		private boolean isFootwear() { return this == SHOES || this == SABATONS; }
		private boolean isHeadgear() { return this == HELMET; }
		private boolean isShield() { return this == SHIELD || this == REINFORCED_SHIELD; }
		private boolean coversTop() { return this == NO_TOP || this == CLOTH_TOP || this == BREASTPLATE || this == DIAMOND_PLATE || this == LIGHT_ENEMY_ARMOR || this == MEDIUM_ENEMY_ARMOR || this == HEAVY_ENEMY_ARMOR;  }
		private boolean coversBottom() { return this == NO_BOTTOM || this == SKIRT || this == BATTLE_SKIRT || this == SHORTS ||  this == LIGHT_ENEMY_LEGWEAR || this == MEDIUM_ENEMY_LEGWEAR; }
		private boolean isUnderwear() { return this == UNDERWEAR; }
		private ChastityCage getCage() { return null; }
		private Plug getPlug() { return null; }
		private IntArray getDurability() { return durability; }
		private IntArray getShockAbsorption() { return shockAbsorption; }
		private boolean coversNipples() { return this == CLOTH_TOP || this == BREASTPLATE || this == DIAMOND_PLATE; }
		private boolean isPorous() { return this != BREASTPLATE && this != SHIELD && this != REINFORCED_SHIELD; }
		private boolean isHeatResistant() { return false; }
		private boolean isFlameRetardant() { return this == BREASTPLATE || this == DIAMOND_PLATE; }
		private boolean isAcidResistant() { return false; }
		private boolean isAlive() { return false; }
		private boolean coversAnus() { return this == SHORTS || this == UNDERWEAR; }
		private boolean slipOff() { return true; }
		private boolean showsErection() { return true; }
		private boolean showsRear() { return true; }
		private boolean showsHips() { return true; }
		private int getMaxDurability() { int maxDurability = 0; for (int value : getDurability().items) maxDurability += value; return maxDurability; }
		private String getDurabilityDescription() {
			String durabilityDescription = "";
			int ii = 0;
			for (int durabilityScore : durability.items) {
				durabilityDescription += "Blocks " + (shockAbsorption.size > ii ? shockAbsorption.get(ii) : 0) + " damage for " + durabilityScore + " durability points" + (ii < durability.size - 1 ? ", then\n" : ".\n"); 
				ii++;
			}
			return durabilityDescription;
		}
	}

	
}

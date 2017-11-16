package com.majalis.character;

import com.badlogic.gdx.utils.ObjectMap;
import com.majalis.character.AbstractCharacter.Stat;

/*
 * Represents an individual, discrete item.
 */
public abstract class Item {

	protected Item() {}
	public abstract int getValue();
	protected abstract ItemEffect getUseEffect();
	public abstract String getName();
	public abstract String getDescription();
	public boolean isConsumable() {
		return false;
	}	
	public boolean instantUse() {
		return false;
	}
	public boolean isEquippable() {
		return false;
	}
	
	
	@Override 
	public boolean equals(Object o) {
		Item compare = (Item) o;
		return o != null && o.getClass() == this.getClass() && compare.getValue() == this.getValue() && compare.getName().equals(this.getName()) && compare.getDescription().equals(this.getDescription());
	}
	
	public static class Misc extends Item {
		private MiscType type;

		@SuppressWarnings("unused")
		private Misc() {}
		
		public Misc (MiscType type) {
			this.type = type;
		}
		
		@Override
		public int getValue() {
			return type == MiscType.KEY ? 100 : 0;
		}

		@Override
		protected ItemEffect getUseEffect() {
			return null;
		}

		@Override
		public String getName() {
			return type == MiscType.HUNGER_CHARM ? "Hunger Charm" : type == MiscType.ICE_CREAM ? "Ice Cream" : "Key";
		}

		@Override
		public String getDescription() {
			return type.getDescription();
		}
	}
	
	public enum MiscType {
		ICE_CREAM,
		HUNGER_CHARM,
		KEY;

		public String getDescription() {
			switch (this) {
				case HUNGER_CHARM:
					return "Wards off hunger.";
				case ICE_CREAM:
					return "Is Ice Cream.";
				case KEY:
					return "Opens chastity cage.";
				default:
					return "";
			}
		}
	}
	
	public static class Plug extends Item {
		public Plug() {}

		@Override
		public int getValue() {
			return 45;
		}

		@Override
		protected ItemEffect getUseEffect() {
			return null;
		}
		
		@Override
		public boolean isEquippable() {
			return true;
		}	

		@Override
		public String getName() {
			return "Butt Plug";
		}

		@Override
		public String getDescription() {
			return "A thing which will fill up your rectum pretty snugly.";
		}
	}
	
	public static class ChastityCage extends Item {
		public ChastityCage() {}

		@Override
		public int getValue() {
			return 15;
		}

		@Override
		protected ItemEffect getUseEffect() {
			return null;
		}
		
		@Override
		public boolean isEquippable() {
			return true;
		}	

		@Override
		public String getName() {
			return "Chastity Cage";
		}

		@Override
		public String getDescription() {
			return "A thing which will fit around your penis and prevent erections.";
		}
	}
	
	public static class Weapon extends Item {
		
		private WeaponType type;
		private int bonus;

		@SuppressWarnings("unused")
		private Weapon() {}
		
		public Weapon(WeaponType type) {
			this(type, 0);
		}
		
		public Weapon(WeaponType type, int bonus) {
			this.type = type;
			this.bonus = bonus;
		}
		
		@Override 
		public boolean equals(Object o) {
			if (o == null || o.getClass() != Weapon.class) return false;
			Weapon compare = (Weapon) o;
			return super.equals(o) && compare.type == this.type && compare.bonus == this.bonus;
		}

		@Override
		public boolean isEquippable() {
			return true;
		}			
		
		@Override
		public int getValue() {
			return bonus == 0 ? 10 : 50;
		}

		@Override
		protected ItemEffect getUseEffect() {
			return null;
		}

		@Override
		public String getName() {
			return type.toString() + (bonus != 0 ? " +" + bonus : "");
		}

		@Override
		public String getDescription() {
			switch (type) {
				case Dagger: return "Thrusting weapon whose efficacy is dependent on the wielder's agility. Causes bleed. [Damage: " + bonus + " + Agility / 2]";
				case Rapier: return "Thrusting weapon whose efficacy is dependent on the wielder's agility. Causes bleed. [Damage: " + (bonus + 1) + " + Agility / 3]";
				case Gladius: return "Thrusting and slashing weapon whose efficacy is dependent on both the wielder's strength and agility. Causes bleed. [Damage: " + (bonus + 1) + " + (Strength + Agility) / 5]";
				case Cutlass: return "Slashing weapon Weapon whose efficacy is dependent on both the wielder's strength and agility. Causes bleed. [Damage: " + (bonus + 1) + " + (Strength + Agility) / 5]";
				case Broadsword: return "Thrusting and slashing weapon whose efficacy is dependent on the wielder's strength. Causes bleed. [Damage: " + (bonus + 1) + " + Strength / 3]";
				default: return "Unknown Weapon!";
			}
		}
		
		public int getDamage(ObjectMap<Stat, Integer> stats) {
			switch (type) {
				case Dagger: return stats.get(Stat.AGILITY) / 2 + bonus;
				case Rapier: return (stats.get(Stat.AGILITY)) / 3 + 1 + bonus;
				case Axe: 
				case Club: return (stats.get(Stat.STRENGTH)) / 3 + 2 + bonus;
				case Gladius:
				case Cutlass: return (stats.get(Stat.STRENGTH) + stats.get(Stat.AGILITY)) / 5 + 1 + bonus;
				case Broadsword: return (stats.get(Stat.STRENGTH)) / 3 + 1 + bonus;
				case Bow: return 1;
				case Sickle: return (stats.get(Stat.AGILITY)) / 3 + 2 + bonus;
				case Flail: return (stats.get(Stat.STRENGTH)) / 3 + 2 + bonus;
				case Talon: 
				case Claw: return 0;
				default: return 0;
			}
		}

		public boolean isDisarmable() {
			return type.disarmable;
		}
		
		public boolean causesBleed() {
			return type.causeBleed;
		}
	}
	
	public enum WeaponType {
		Dagger (true, false, true),
		Rapier,
		Gladius,
		Cutlass,
		Broadsword,
		Axe (true, false, true),
		Bow (false, false, true), 
		Flail (true, false, true),
		Sickle (true, false, true),
		Claw (false, false, true),
		Talon (false, false, true), 
		Club (true, false, false), 
		;
		
		private final boolean disarmable;
		private final boolean buyable;
		private final boolean causeBleed;
		private WeaponType() { 
			this(true, true, true);
		}
		
		private WeaponType(boolean disarmable, boolean buyable, boolean causeBleed) {
			this.disarmable = disarmable;
			this.buyable = buyable;
			this.causeBleed = causeBleed;
		}
		
		public boolean isBuyable() {
			return buyable;
		}
	}
	
	public static class Potion extends Item {

		private final int magnitude;
		private final EffectType effect;
		public Potion() {
			this(10);
		}
		
		public Potion(int magnitude) {
			this(magnitude, EffectType.HEALING);
		}
		
		public Potion(int magnitude, EffectType effect) {
			this.magnitude = magnitude;
			this.effect = effect;	
		}
		
		@Override
		public boolean isConsumable() {
			return true;
		}	
		
		@Override
		public int getValue() {
			switch (effect) {
				case BONUS_AGILITY:			
				case BONUS_ENDURANCE:
				case BONUS_STRENGTH:
					return magnitude * 15;
				case HEALING:
					return magnitude / 2;
				case MEAT:
					return magnitude * 2;
				case BANDAGE:
					return 2;
				default:
					return 0;
			}
		}
		
		@Override
		public boolean instantUse() {
			return effect == EffectType.MEAT;
		}

		@Override
		protected ItemEffect getUseEffect() {
			return new ItemEffect(effect, magnitude);
		}

		@Override
		public String getName() {
			return effect == EffectType.SLIME ? (magnitude == 1 ? "Worthless Slime" : "Primo Slime") : effect == EffectType.MANA ? (magnitude == 20 ? "Mana Crystal" : "Mana Chunk") : effect.getDisplay() + (effect == EffectType.SPIDER || effect == EffectType.MEAT || effect == EffectType.BANDAGE ? "" : " (" + magnitude + ")"); 
		}

		@Override
		public String getDescription() {
			switch (effect) {
				case BONUS_AGILITY:		
					return "Imbibe to increase Agility for the duration by " + magnitude + ".";
				case BONUS_ENDURANCE:
					return "Imbibe to increase Endurance for the duration by " + magnitude + ".";
				case BONUS_STRENGTH:
					return "Imbibe to increase Strength for the duration by " + magnitude + ".";
				case HEALING:
					return "Heals the imbiber for " + magnitude + " health.";
				case MANA:
					return "Restores " + magnitude + " mana.";
				case MEAT:
					return "Eat to restore " + magnitude + " hunger.";
				case SPIDER:
					return "Eat to restore " + magnitude + " hunger...?!";
				case SLIME:
					return "Eat to bolster defense by " + magnitude + ".";
				case BANDAGE:
					return "Use to heal blood loss by " + magnitude + ".";
				default:
					return "Unknown potion.";
			}
		}
	}
	
	public enum EffectType {
		SPIDER ("Spider"),
		HEALING ("Health Pot."),
		BONUS_STRENGTH ("Ox Pot."),
		BONUS_AGILITY ("Cat Pot."),
		BONUS_ENDURANCE ("Bear Pot."),
		SLIME ("Slime"),
		MEAT ("Meat"),
		BANDAGE ("Bandage"),
		MANA ("Mana Crystal");
		
		private final String display;
		private EffectType (String display) {
			this.display = display;
		}
		
		public String getDisplay() { return display; }
	}
	
	public class ItemEffect {
	
		private final EffectType type;
		private final int magnitude;
		
		private ItemEffect(EffectType type, int magnitude) {
			this.type = type;
			this.magnitude = magnitude;
		}
		
		public EffectType getType() { return type; }
		public int getMagnitude() { return magnitude; }
	}
}

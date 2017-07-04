package com.majalis.character;

import com.badlogic.gdx.utils.ObjectMap;
import com.majalis.character.AbstractCharacter.Stat;

/*
 * Represents an individual, discrete item.
 */
public abstract class Item {

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
	
	public static class Weapon extends Item {
		
		private WeaponType type;
		private String name;
		private int bonus;

		@SuppressWarnings("unused")
		private Weapon() {}
		
		public Weapon(WeaponType type) {
			this(type, 0);
		}
		
		public Weapon(WeaponType type, int bonus) {
			this.type = type;
			this.name = type.toString() + (bonus != 0 ? " +" + bonus : "");
			this.bonus = bonus;
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
			return name;
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
		Claw (false, false, true),
		Talon (false, false, true), 
		Club (true, false, false)
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
			return effect.getDisplay() + (effect == EffectType.MEAT || effect == EffectType.BANDAGE ? "" : " (" + magnitude + ")"); 
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
				case MEAT:
					return "Increases food stores by " + magnitude + ".";
				case BANDAGE:
					return "Use to heal blood loss by " + magnitude + ".";
				default:
					return "Unknown potion.";
			}
		}
	}
	
	public enum EffectType {
		HEALING ("Health Pot."),
		BONUS_STRENGTH ("Ox Pot."),
		BONUS_AGILITY ("Cat Pot."),
		BONUS_ENDURANCE ("Bear Pot."),
		MEAT ("Meat"),
		BANDAGE ("Bandage");
		
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

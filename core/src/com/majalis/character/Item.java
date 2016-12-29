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
	
	public static class Weapon extends Item {
		
		private WeaponType type;
		private String name;

		public Weapon(){}
		
		public Weapon(WeaponType type){
			this.type = type;
			this.name = type.toString();
		}
		
		@Override
		public int getValue() {
			return 10;
		}

		@Override
		protected ItemEffect getUseEffect() {
			return null;
		}

		@Override
		public String getName() {
			return name;
		}

		public int getDamage(ObjectMap<Stat, Integer> stats) {
			switch (type){
				case Rapier: return (stats.get(Stat.AGILITY)) / 3 + 1;
				case Cutlass: return (stats.get(Stat.STRENGTH) + stats.get(Stat.AGILITY)) / 5 + 1;
				case Broadsword: return (stats.get(Stat.STRENGTH)) / 3 + 1;
				default: return 0;
			}
		}
	}
	
	public enum WeaponType {
		Rapier,
		Cutlass,
		Broadsword
	}
	
	public static class Potion extends Item {

		private final int magnitude;
		public Potion() {
			this(10);
		}
		
		public Potion(int magnitude){
			this.magnitude = magnitude;
		}

		@Override
		public int getValue() {
			return magnitude / 2;
		}

		@Override
		protected ItemEffect getUseEffect() {
			return new ItemEffect(EffectType.HEALING, magnitude);
		}

		@Override
		public String getName() {
			return "Potion (" + magnitude + ")"; 
		}
	}
	
	public enum EffectType {
		HEALING		
	}
	
	public class ItemEffect {
	
		private final EffectType type;
		private final int magnitude;
		
		private ItemEffect(EffectType type, int magnitude){
			this.type = type;
			this.magnitude = magnitude;
		}
		
		public EffectType getType() { return type; }
		public int getMagnitude() { return magnitude; }
	}	
}

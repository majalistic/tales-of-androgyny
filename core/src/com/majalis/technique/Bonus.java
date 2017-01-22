package com.majalis.technique;

import com.badlogic.gdx.utils.OrderedMap;
/* Represents a bonus, which is a wrapper around a condition tied to a map of BonusType to Magnitude */
public class Bonus {
	private final BonusCondition condition;
	private final OrderedMap<BonusType, Integer> typeWithMagnitude;
	private final int bonusLevel;
	// convenience constructor
	protected Bonus(BonusCondition condition, BonusType type, int magnitude) {
		this.condition = condition;
		typeWithMagnitude = new OrderedMap<BonusType, Integer>();
		typeWithMagnitude.put(type, magnitude);
		this.bonusLevel = 1;
	}
	
	private Bonus(BonusCondition condition, OrderedMap<BonusType, Integer> typeWithMagnitude, int bonusLevel) {
		this.condition = condition;
		this.typeWithMagnitude = typeWithMagnitude;
		this.bonusLevel = bonusLevel;
	}
	
	public OrderedMap<BonusType, Integer> getBonusMap() {
		return typeWithMagnitude;
	}

	public Bonus combine(int bonusLevel) {
		OrderedMap<BonusType, Integer> newBonusMap = new OrderedMap<BonusType, Integer>();
		for (OrderedMap.Entry<BonusType, Integer> bonus : typeWithMagnitude.entries()) {
			newBonusMap.put(bonus.key, bonus.value * bonusLevel);
		}
		return new Bonus(condition, newBonusMap, bonusLevel);
	}
	
	public int getBonusLevel() {
		return bonusLevel;
	}
	
	public String getDescription(String user) {
		String description = "";
		switch (condition) {
			case ENEMY_BLOODY:
				break;
			case ENEMY_LOW_STABILITY:
				break;
			case ENEMY_ON_GROUND:
				description += "Enemy is on the ground!\n";
				break;
			case OUTMANEUVER:
				description += user + " outmaneuvered the opponent by " + bonusLevel + "!\n";
				break;
			case OUTMANUEVER_STRONG:
				break;
			case SKILL_LEVEL:
				break;
			case STRENGTH_OVERPOWER:
				break;
			case STRENGTH_OVERPOWER_STRONG:
				break;
			default:
				break;
			}
		for (OrderedMap.Entry<BonusType, Integer> bonus : typeWithMagnitude.entries()) {
			if (condition != BonusCondition.SKILL_LEVEL) {
				switch (bonus.key) {
					case ARMOR_SUNDER:
						break;
					case GUARD_MOD:
						break;
					case GUT_CHECK:
						break;
					case KNOCKDOWN:
						break;
					case MANA_COST:
						break;
					case POWER_MOD:
						description += "CRITICAL HIT! Power increased by " + bonus.value + "!";
						break;
					case PRIORITY:
						description += user + " gained the initiative!";
						break;
					case STABILTIY_COST:
						break;
					case STAMINA_COST:
						break;
					default:
						break;
				}
			}
		}
		return description.equals("") ? null : description;
	}

	public String getDescription() {
		String description = "";
		for (OrderedMap.Entry<BonusType, Integer> bonus : typeWithMagnitude.entries()) {
			description += "  ";
			switch (bonus.key) {
				case ARMOR_SUNDER:
					description += "Armor sundering multiplier +" + bonus.value * 100 + "%";
					break;
				case GUARD_MOD:
					description += "Guard +" + bonus.value+"%";
					break;
				case PARRY:
					description += "Parry +" + bonus.value+"%";
					break;
				case GUT_CHECK:
					description += "Stamina destruction +" + bonus.value;
					break;
				case KNOCKDOWN:
					description += "Destabilize +" + bonus.value;
					break;
				case MANA_COST:
					description += "Mana cost -" + Math.abs(bonus.value);
					break;
				case POWER_MOD:
					description += "Power +" + bonus.value;
					break;
				case PRIORITY:
					description += "Technique has priority";
					break;
				case STABILTIY_COST:
					description += "Stability cost -" + Math.abs(bonus.value);
					break;
				case STAMINA_COST:
					description += "Stamina cost -" + Math.abs(bonus.value);
					break;
			}
			description += "\n";
		}
		return description;
	}
	
	public enum BonusCondition {
		SKILL_LEVEL ("For each skill level: "),
		ENEMY_LOW_STABILITY ("When enemy is on unstable footing: "),
		ENEMY_ON_GROUND ("When enemy is on the ground: "),
		ENEMY_BLOODY ("When enemy is bleeding: "),
		STRENGTH_OVERPOWER ("The more you overpower the enemy: "),
		STRENGTH_OVERPOWER_STRONG ("When you greatly overpower the enemy: "),
		OUTMANEUVER ("The faster you are than the enemy: "),
		OUTMANUEVER_STRONG ("When you are much quicker than the enemy: ");

		private final String description;
		private BonusCondition (String description) {
			this.description = description;
		}
		
		public String getDescription() { return description; }
	}
	
	public enum BonusType {
		POWER_MOD,
		STAMINA_COST,
		STABILTIY_COST,
		MANA_COST,
		KNOCKDOWN,
		ARMOR_SUNDER,
		GUT_CHECK,
		GUARD_MOD,
		PRIORITY, 
		PARRY;
	}
}

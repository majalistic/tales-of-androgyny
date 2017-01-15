package com.majalis.technique;

import com.badlogic.gdx.utils.ObjectMap;
/* Represents a bonus, which is a wrapper around a condition tied to a map of BonusType to Magnitude */
public class Bonus {
	private final BonusCondition condition;
	private final ObjectMap<BonusType, Integer> typeWithMagnitude;
	private final int bonusLevel;
	// convenience constructor
	protected Bonus(BonusCondition condition, BonusType type, int magnitude) {
		this.condition = condition;
		typeWithMagnitude = new ObjectMap<BonusType, Integer>();
		typeWithMagnitude.put(type, magnitude);
		this.bonusLevel = 1;
	}
	
	private Bonus(BonusCondition condition, ObjectMap<BonusType, Integer> typeWithMagnitude, int bonusLevel) {
		this.condition = condition;
		this.typeWithMagnitude = typeWithMagnitude;
		this.bonusLevel = bonusLevel;
	}
	
	public ObjectMap<BonusType, Integer> getBonusMap() {
		return typeWithMagnitude;
	}

	public Bonus combine(int bonusLevel) {
		ObjectMap<BonusType, Integer> newBonusMap = new ObjectMap<BonusType, Integer>();
		for (ObjectMap.Entry<BonusType, Integer> bonus : typeWithMagnitude.entries()) {
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
		for (ObjectMap.Entry<BonusType, Integer> bonus : typeWithMagnitude.entries()) {
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
	

	public enum BonusCondition {
		SKILL_LEVEL,
		ENEMY_LOW_STABILITY,
		ENEMY_ON_GROUND,
		ENEMY_BLOODY,
		STRENGTH_OVERPOWER,
		STRENGTH_OVERPOWER_STRONG,
		OUTMANEUVER,
		OUTMANUEVER_STRONG
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
		PRIORITY;
	}
	
}

package com.majalis.character;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.IntArray;
import com.majalis.battle.BattleFactory.EnemyEnum;

/*
 * Abstract class that all enemies extend - currently concrete to represent a generic "enemy".
 */
public class EnemyCharacter extends AbstractCharacter {

	private transient Texture texture;
	
	@SuppressWarnings("unused")
	private EnemyCharacter(){}
	public EnemyCharacter(Texture texture, EnemyEnum enemyType){
		super(true);
		this.enemyType = enemyType;
		init(texture);
		switch(enemyType){
			case WERESLUT:
				baseStrength = 6;
				baseAgility = 6;
				break;
			case HARPY:
				baseStrength = 5;
				baseAgility = 5;
				break;
			case BRIGAND:
				baseStrength = 4;
				break;
			case SLIME:
				baseStrength = 2;
				baseEndurance = 4;
				break;
		}
		staminaTiers.add(5);
		
		setStaminaToMax();
		lust = 0;
		label = enemyType.toString();
		this.currentHealth = getMaxHealth();
		this.stance = Stance.BALANCED;
	}
	
	private boolean willPounce(){
		return lust >= 10 && stance != Stance.PRONE && stance != Stance.SUPINE && stance != Stance.AIRBORNE && stance != Stance.FELLATIO && stance != Stance.DOGGY && stance != Stance.ERUPT;
	}
	
	public Technique getTechnique(AbstractCharacter target){
		int rand = getRandomWeighting(); 
	
		if (lust < 10) lust++;
		
		if (willPounce()){
			if (target.stance == Stance.PRONE || target.stance == Stance.SUPINE){
				return getTechnique(Techniques.POUNCE);
			}
			else if (target.stance == Stance.KNEELING){
				return getTechnique(Techniques.SAY_AHH);
			}
			else if (enemyType == EnemyEnum.HARPY){
				return getTechnique(Techniques.FLY);
			}
		}
		
		if (enemyType == EnemyEnum.SLIME && stance != Stance.DOGGY && stance != Stance.FELLATIO){
			if (currentStamina > 3 && (stance == Stance.BALANCED || currentStamina > 9)){
				return getTechnique(Techniques.SLIME_ATTACK); 
			}
			else {
				return getTechnique(Techniques.SLIME_QUIVER); 
			}
		}
		
		switch(stance){
			case OFFENSIVE:
				if (stability < 5) return getTechnique(Techniques.RESERVED_ATTACK);
				if (stability < 6) return getTechnique(Techniques.TEMPO_ATTACK);
				switch (rand){					
					case 0:
						if (currentStamina < 5) return getTechnique(Techniques.RESERVED_ATTACK);
						if (currentStamina < 7) return getTechnique(Techniques.TEMPO_ATTACK);
						return getTechnique(Techniques.STRONG_ATTACK);	
					case 1:
						if (currentStamina < 5) return getTechnique(Techniques.RESERVED_ATTACK);
						return getTechnique(Techniques.TEMPO_ATTACK);	
					case 2:	
						return getTechnique(Techniques.RESERVED_ATTACK);
				}
			case BALANCED:
				if (currentStamina < 4 || stability < 4) return getTechnique(Techniques.CAUTIOUS_ATTACK);
				switch (rand){
					case 0:
						if (currentStamina < 8 || stability < 6) return getTechnique(Techniques.NEUTRAL_ATTACK);
						return getTechnique(Techniques.SPRING_ATTACK);	
					case 1:
						if (currentStamina < 3 || stability < 2) return getTechnique(Techniques.CAUTIOUS_ATTACK);
						return getTechnique(Techniques.NEUTRAL_ATTACK);	
					case 2:	
						return getTechnique(Techniques.CAUTIOUS_ATTACK);
				}
			case DEFENSIVE:
				if (currentStamina < 4) return getTechnique(Techniques.SECOND_WIND);
				if (stability < 5) return getTechnique(Techniques.GUARD);
				switch (rand){
					case 0:
						if (currentStamina < 8 || stability < 9) return getTechnique(Techniques.CAREFUL_ATTACK);
						return getTechnique(Techniques.REVERSAL_ATTACK);	
					case 1:
						if (currentStamina < 4) return getTechnique(Techniques.GUARD);
						return getTechnique(Techniques.CAREFUL_ATTACK);	
					case 2:	
						if (currentStamina < 7) return getTechnique(Techniques.SECOND_WIND);
						return getTechnique(Techniques.GUARD);
				}
			case PRONE:
			case SUPINE:
				if (currentStamina > 5){
					return getTechnique(Techniques.KIP_UP);
				}	
				else if (currentStamina > 2){
					return getTechnique(Techniques.STAND_UP);
				}
				else {
					return getTechnique(Techniques.REST);
				}				
			case DOGGY:
				lust++;
				if (enemyType != EnemyEnum.WERESLUT && lust > 14){
					return getTechnique(Techniques.ERUPT);
				}
				if (enemyType == EnemyEnum.WERESLUT && lust > 17){
					return getTechnique(Techniques.KNOT);
				}
				else {
					return getTechnique(Techniques.POUND);
				}
			case KNOTTED:
				return getTechnique(Techniques.KNOT_BANG);
			case AIRBORNE:
				return getTechnique(Techniques.DIVEBOMB);
			case FELLATIO:
				lust++;
				if (lust > 14){
					return getTechnique(Techniques.ERUPT);
				}
				else {
					return getTechnique(Techniques.IRRUMATIO);
				}	
			case ERUPT:
				stance = Stance.BALANCED;
				return getTechnique(target);
			default: return null;
		}
	}
	
	private Technique getTechnique(Techniques technique){
		return new Technique(technique.getTrait(), getStrength());
	}
	
	private int getRandomWeighting(){
		int[] weightArray;
		switch (enemyType){
			case WERESLUT: weightArray = new int[]{0, 0, 0, 0, 0, 0, 1, 1, 2}; break;
			case BRIGAND: weightArray = new int[]{0, 1, 1, 2, 2, 2}; break;
			default: weightArray = new int[]{0,1,2};
		}
		
		return new IntArray(weightArray).random();
	}
	
	@Override
    public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		batch.draw(texture, 0, 0, 1280, 720);
    }
	
	public void init(Texture texture){
		this.texture = texture;
	}
	// can put text here for an enemy getting more aroused
	@Override
	protected String increaseLust() {
		switch (stance){
		case DOGGY:
		case KNOTTED:
		case FELLATIO:
			return increaseLust(1);
		default: return null;
	}
	}
	// can put text here for an enemy getting more aroused
	@Override
	protected String increaseLust(int lustIncrease) {
		lust += lustIncrease;
		return null;
	}
} 

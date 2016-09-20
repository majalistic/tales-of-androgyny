package com.majalis.character;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.IntArray;
import com.majalis.battle.BattleFactory.EnemyEnum;

/*
 * Abstract class that all enemies extend - currently concrete to represent a generic "enemy".
 */
public class EnemyCharacter extends AbstractCharacter {

	private transient Texture texture;
	private Vector2 position;
	private int lust; 
	
	@SuppressWarnings("unused")
	private EnemyCharacter(){}
	public EnemyCharacter(Texture texture, EnemyEnum enemyType){
		super(true);
		this.enemyType = enemyType;
		init(texture);
		baseStrength = 5;
		lust = 0;
		label = enemyType.toString();
		this.currentHealth = getMaxHealth();
		this.stance = Stance.BALANCED;
	}
	
	private boolean willPounce(){
		return lust >= 10;
	}
	
	public Technique getTechnique(AbstractCharacter target){
		int rand = getRandomWeighting(); 
	
		if (lust < 10) lust++;
		
		// need to add techniques for falling down and getting up and such
		if ((target.stance == Stance.PRONE || target.stance == Stance.SUPINE) && willPounce()){
			return new Technique(Techniques.POUNCE, getStrength());
		}
		else if (enemyType == EnemyEnum.HARPY && willPounce() && stance != Stance.AIRBORNE && stance != Stance.FELLATIO){
			return new Technique(Techniques.FLY, getStrength());
		}
		
		switch(stance){
			case OFFENSIVE:
				if (currentStamina < 3 || stability < 3) return new Technique(Techniques.RESERVED_ATTACK, getStrength());
				switch (rand){
					case 0:
						return new Technique(Techniques.STRONG_ATTACK, getStrength());	
					case 1:
						return new Technique(Techniques.TEMPO_ATTACK, getStrength());	
					case 2:	
						return new Technique(Techniques.RESERVED_ATTACK, getStrength());
				}
			case BALANCED:
				if (currentStamina < 3 || stability < 3) return new Technique(Techniques.CAUTIOUS_ATTACK, getStrength());
				switch (rand){
					case 0:
						return new Technique(Techniques.SPRING_ATTACK, getStrength());	
					case 1:
						return new Technique(Techniques.NEUTRAL_ATTACK, getStrength());	
					case 2:	
						return new Technique(Techniques.CAUTIOUS_ATTACK, getStrength());
				}
			case DEFENSIVE:
				switch (rand){
					case 0:
						return new Technique(Techniques.REVERSAL_ATTACK, getStrength());	
					case 1:
						return new Technique(Techniques.CAREFUL_ATTACK, getStrength());	
					case 2:	
						return new Technique(Techniques.GUARD, getStrength());
				}
			case PRONE:
			case SUPINE:
				if (currentStamina > 5){
					return new Technique(Techniques.KIP_UP, getStrength());
				}	
				else {
					return new Technique(Techniques.STAND_UP, getStrength());
				}
				
			case DOGGY:
				lust++;
				if (lust > 14){
					if (enemyType == EnemyEnum.HARPY){
						return new Technique(Techniques.ERUPT, getStrength());
					}
					else {
						return new Technique(Techniques.KNOT, getStrength());
					}
				}
				else {
					return new Technique(Techniques.POUND, getStrength());
				}
			case KNOTTED:
				return new Technique(Techniques.KNOT_BANG, getStrength());
			case AIRBORNE:
				return new Technique(Techniques.DIVEBOMB, getStrength());
			case FELLATIO:
				lust++;
				if (lust > 14){
					return new Technique(Techniques.ERUPT, getStrength());
				}
				else {
					return new Technique(Techniques.IRRUMATIO, getStrength());
				}	
			default: return null;
		}
	}
	
	private int getRandomWeighting(){
		int[] weightArray;
		switch (enemyType){
			case WERESLUT: weightArray = new int[]{0, 0, 0, 0, 0, 1, 1, 2}; break;
			case BRIGAND: weightArray = new int[]{0, 1, 1, 2, 2, 2}; break;
			default: weightArray = new int[]{0,1,2};
		}
		
		return new IntArray(weightArray).random();
	}
	
	@Override
    public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		batch.draw(texture, position.x, position.y, 1280, 720);
    }
	
	public void init(Texture texture){
		this.texture = texture;
		position = enemyType.getPosition();
	}
} 

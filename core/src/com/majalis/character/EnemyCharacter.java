package com.majalis.character;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.majalis.battle.BattleFactory.EnemyEnum;

/*
 * Abstract class that all enemies extend - currently concrete to represent a generic "enemy".
 */
public class EnemyCharacter extends AbstractCharacter {

	public EnemyEnum enemyType;
	private Texture texture;
	private Vector2 position;
	@SuppressWarnings("unused")
	private EnemyCharacter(){}
	public EnemyCharacter(Texture texture, EnemyEnum enemyType){
		super(true);
		this.enemyType = enemyType;
		init(texture);
		baseStrength = 6;
		label = enemyType.toString();
		this.currentHealth = getMaxHealth();
		this.stance = Stance.BALANCED;
	}
	
	public Technique getTechnique(AbstractCharacter target){
		int rand = new IntArray(new int[]{0,1,2}).random();
		switch(stance){
			case OFFENSIVE:
				switch (rand){
					case 0:
						return new Technique(Techniques.STRONG_ATTACK, getStrength());	
					case 1:
						return new Technique(Techniques.TEMPO_ATTACK, getStrength());	
					case 2:	
						return new Technique(Techniques.RESERVED_ATTACK, getStrength());
				}
			case BALANCED:
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
						return new Technique(Techniques.SPRING_ATTACK, getStrength());	
					case 1:
						return new Technique(Techniques.CAREFUL_ATTACK, getStrength());	
					case 2:	
						return new Technique(Techniques.GUARD, getStrength());
				}
		}
		return null;
	}
	
	@Override
    public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		batch.draw(texture, position.x, position.y);
    }
	
	@Override
	public void write(Json json) {
		super.write(json);
	}
	@Override
	public void read(Json json, JsonValue jsonData) {
		super.read(json, jsonData);
	}
	
	public void init(Texture texture){
		this.texture = texture;
		position = enemyType.getPosition();
	}
} 

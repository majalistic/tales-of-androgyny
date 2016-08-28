package com.majalis.traprpg;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;

/*
 * Abstract class that all enemies extend - currently concrete to represent a generic "enemy".
 */
public class EnemyCharacter extends Character {

	private final Texture texture;
	private final Vector2 position;
	public EnemyCharacter(Texture texture, boolean werewolf){
		this.texture = texture;
		position = werewolf ? new Vector2(600, 400) : new Vector2(150, -40);
		this.currentHealth = 3;
	}
	
	@Override
    public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		batch.draw(texture, position.x, position.y);
    }
} 

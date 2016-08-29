package com.majalis.character;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

/*
 * Abstract class that all enemies extend - currently concrete to represent a generic "enemy".
 */
public class EnemyCharacter extends AbstractCharacter {

	private Texture texture;
	private Vector2 position;
	@SuppressWarnings("unused")
	private EnemyCharacter(){}
	public EnemyCharacter(Texture texture, boolean werewolf){
		super(true);
		this.texture = texture;
		setOwnPosition(werewolf);
		label = (werewolf ? "Werebitch" : "Harpy");
		this.currentHealth = getMaxHealth();
	}
	
	public Technique getTechnique(AbstractCharacter target){
		return new Technique(getStrength());
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
	
	public void setTexture(Texture texture) {
		this.texture = texture;
	}
	public void setOwnPosition(boolean werewolf) {
		position = werewolf ? new Vector2(600, 400) : new Vector2(150, -40);
	}
} 

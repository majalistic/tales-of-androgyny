package com.majalis.traprpg;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Group;

public class Battle extends Group{

	private final int victoryScene;
	private final int defeatScene;
	private final Texture enemy;
	public boolean battleOver;
	public boolean victory;
	
	public Battle(int victoryScene, int defeatScene, Texture enemy) {
		this.victoryScene = victoryScene;
		this.defeatScene = defeatScene;
		this.enemy = enemy;
		battleOver = false;
	}

	public void battleLoop() {
		if (Gdx.input.isKeyJustPressed(Keys.ENTER)){
			victory = true;
			battleOver = true;
		}
		else if (Gdx.input.isKeyJustPressed(Keys.SPACE)){
			victory = false;
			battleOver = true;
		}
	}
	
	@Override
    public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		batch.draw(enemy, 600, 400);
    }
	
	public int getVictoryScene(){
		return victoryScene;
	}
	
	public int getDefeatScene(){
		return defeatScene;
	}
}

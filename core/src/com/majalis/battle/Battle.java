package com.majalis.battle;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.majalis.asset.AssetEnum;
import com.majalis.character.EnemyCharacter;
import com.majalis.character.PlayerCharacter;
import com.majalis.encounter.Background;
import com.majalis.save.MutationResult;
import com.majalis.save.SaveService;

public class Battle {
	public final AssetEnum musicPath;
	public final Background battleBackground;
	public final Background battleUI;
	public final AssetManager assetManager;
	public final PlayerCharacter character;
	public final EnemyCharacter enemy;
	public final ObjectMap<String, Integer> outcomes;
	public final Array<MutationResult> battleResults;
	public final Array<MutationResult> consoleText;
	public final Array<MutationResult> dialogText;
	
	public Battle(SaveService saveService, AssetManager assetManager, final PlayerCharacter character, final EnemyCharacter enemy, ObjectMap<String, Integer> outcomes, Background battleBackground, Background battleUI, Array<MutationResult> consoleText, Array<MutationResult> dialogText, Array<MutationResult> battleResults, AssetEnum musicPath) {
		this.musicPath = musicPath;
		this.battleBackground = battleBackground;
		this.battleUI = battleUI;
		this.assetManager = assetManager;
		this.character = character;
		this.enemy = enemy;
		this.outcomes = outcomes;
		this.battleResults = battleResults;
		this.consoleText = consoleText;
		this.dialogText = dialogText;
	}	
}

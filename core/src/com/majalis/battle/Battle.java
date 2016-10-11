package com.majalis.battle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.majalis.battle.BattleFactory.EnemyEnum;
import com.majalis.character.AbstractCharacter;
import com.majalis.character.AbstractCharacter.Stance;
import com.majalis.character.Attack;
import com.majalis.character.PlayerCharacter.Stat;
import com.majalis.character.EnemyCharacter;
import com.majalis.character.PlayerCharacter;
import com.majalis.character.Technique;
import com.majalis.save.SaveEnum;
import com.majalis.save.SaveService;
/*
 * Represents the logic for the flow of a battle.  Currently only supports 1 on 1.
 */
public class Battle extends Group{

	private static int[] POSSIBLE_KEYS = new int[]{Keys.A, Keys.S, Keys.D, Keys.F, Keys.G, Keys.H, Keys.J, Keys.K, Keys.L, Keys.Z, Keys.X, Keys.C, Keys.V};
	private static char[] POSSIBLE_KEYS_CHAR = new char[]{'A','S','D','F','G','H','J','K','L','Z','X','C','V'};
	
	private final PlayerCharacter character;
	private final EnemyCharacter enemy;
	private final SaveService saveService;
	private final AssetManager assetManager;
	private final BitmapFont font;
	private final int victoryScene;
	private final int defeatScene;
	private final Table table;
	private final Skin skin;
	private final Sound buttonSound;
	private String console;
	private Array<Technique> options;
	private Technique selectedTechnique;
	public boolean battleOver;
	public boolean victory;
	public boolean gameExit;
	public int struggle;
	public boolean inRear;
	public int battleEndCount;
	
	public Battle(SaveService saveService, AssetManager assetManager, BitmapFont font, PlayerCharacter character, EnemyCharacter enemy,  int victoryScene, int defeatScene) {
		this.saveService = saveService;
		this.assetManager = assetManager;
		this.font = font;
		this.character = character;
		this.enemy = enemy;
		this.victoryScene = victoryScene;
		this.defeatScene = defeatScene;
		console = "";
		battleOver = false;
		gameExit = false;	
		this.addActor(character);
		this.addActor(enemy);
		skin = assetManager.get("uiskin.json", Skin.class);
		buttonSound = assetManager.get("sound.wav", Sound.class);
		table = new Table();
		this.addActor(table);
		displayTechniqueOptions();
		struggle = 0;
		inRear = false;
		battleEndCount = 0;
	}
	
	private void displayTechniqueOptions(){
		table.clear();
		options = character.getPossibleTechniques();
		for (int ii = 0; ii < options.size; ii++){
			TextButton button;
			Technique option = options.get(ii);
			button = new TextButton(option.getTechniqueName() + (ii > POSSIBLE_KEYS_CHAR.length ? "" : " ("+POSSIBLE_KEYS_CHAR[ii]+")"), skin);
			button.addListener(getListener(option, buttonSound));
			table.add(button).width(220).height(40).row();
			if(character.outOfStaminaOrStability(option)){
				TextButtonStyle style = new TextButtonStyle(button.getStyle());
				style.fontColor = Color.RED;
				button.setStyle(style);
			}
			else if (character.lowStaminaOrStability(option)){
				TextButtonStyle style = new TextButtonStyle(button.getStyle());
				style.fontColor = Color.ORANGE;
				button.setStyle(style);
			}

		}
        table.setFillParent(true);
        table.addAction(Actions.moveTo(1077, 150));
        
	}

	public void battleLoop() {
		if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)){
			gameExit = true;
		}
		else {
			Technique playerTechnique = getTechnique();
			if (playerTechnique != null){		
				// possibly construct a separate class for this
				resolveTechniques(character, playerTechnique, enemy, enemy.getTechnique(character));
				displayTechniqueOptions();
				
				saveService.saveDataValue(SaveEnum.PLAYER, character);
				saveService.saveDataValue(SaveEnum.ENEMY, enemy);
			}
		}
		
		if (character.getCurrentHealth() <= 0){
			victory = false;
			battleOver = true;
		}
		if (enemy.getCurrentHealth() <= 0){
			victory = true;
			battleOver = true;
		}
		if (battleOver){
			character.refresh();
			saveService.saveDataValue(SaveEnum.ENEMY, null);
		}
	}
	
	private Technique getTechnique() {
		if (selectedTechnique != null){
			Technique temp = selectedTechnique;
			selectedTechnique = null;
			return temp;
		}
		int ii = 0;
		for (int possibleKey : POSSIBLE_KEYS){
			if (Gdx.input.isKeyJustPressed(possibleKey)){
				if (ii < options.size){
					return options.get(ii);
				}
			}
			ii++;
		}
		return null;
	}
	
	private void printToConsole(Array<String> results){
		for (String result: results){
			printToConsole(result);
		}
	}
	
	private void printToConsole(String result){ 
		console += result + "\n";
	
	}
	
	// should probably use String builder to build a string to display in the console - needs to properly be getting information from the interactions - may need to be broken up into its own class
	private void resolveTechniques(AbstractCharacter firstCharacter, Technique firstTechnique, AbstractCharacter secondCharacter, Technique secondTechnique) {
		console = "";
		printToConsole(firstCharacter.getStanceTransform(firstTechnique));
		printToConsole(secondCharacter.getStanceTransform(secondTechnique));
		
		firstTechnique = firstCharacter.extractCosts(firstTechnique);
		secondTechnique = secondCharacter.extractCosts(secondTechnique);
		
		Attack attackForFirstCharacter = secondCharacter.doAttack(secondTechnique.resolve(firstTechnique));
		Attack attackForSecondCharacter = firstCharacter.doAttack(firstTechnique.resolve(secondTechnique));

		printToConsole(firstCharacter.receiveAttack(attackForFirstCharacter));
		printToConsole(secondCharacter.receiveAttack(attackForSecondCharacter));		
		
		if (secondCharacter.battleOver >= 5){
			battleOver = true;
			victory = false;
		}
	}
	
	@Override
    public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		font.draw(batch, "Health: " + String.valueOf(character.getCurrentHealth()) + "\nStamina: " + String.valueOf(character.getCurrentStamina()) + "\nBalance: " + String.valueOf(character.getStability()) + (character.getStat(Stat.MAGIC) > 1 ? "\nMana: " + String.valueOf(character.getCurrentMana()) : "")  + "\nStance: " + character.getStance().toString(), 70, 695);		
		batch.draw(getStanceImage(character.stance), 330, 540, 100, 115);
		batch.draw(getLustImage(character.lust, PhallusType.SMALL), 60, 450, 100, 115);
		font.draw(batch, "Health: " + String.valueOf(enemy.getCurrentHealth()) + "\nStance: " + enemy.getStance().toString(), 1100, 650);		
		batch.draw(getStanceImage(enemy.stance), 920, 540, 100, 115);
		batch.draw(getLustImage(enemy.lust, enemy.enemyType == EnemyEnum.BRIGAND ? PhallusType.NORMAL : PhallusType.MONSTER), 1150, 450, 100, 115);
		font.draw(batch, console, 80, 270);
    }
	
	private enum PhallusType {
		SMALL("Trap"),
		NORMAL("Human"),
		MONSTER("Monster");
		private final String label;

		PhallusType(String label) {
		    this.label = label;
		 }
	}
	
	private Texture getLustImage(int lust, PhallusType type){
		int lustLevel = lust > 7 ? 2 : lust > 3 ? 1 : 0;
		return assetManager.get("arousal/"+ type.label + lustLevel + ".png", Texture.class);
	}
	
	private Texture getStanceImage(Stance stance){
		switch(stance){
			case BALANCED:
				return assetManager.get("stances/Balanced.png", Texture.class);
			case DEFENSIVE:
				return assetManager.get("stances/Defensive.png", Texture.class);
			case DOGGY:
				return assetManager.get("stances/Doggy.png", Texture.class);
			case ERUPT:
				return assetManager.get("stances/Erupt.png", Texture.class);
			case FELLATIO:
				return assetManager.get("stances/Fellatio.png", Texture.class);
			case KNEELING:
				return assetManager.get("stances/Kneeling.png", Texture.class);
			case OFFENSIVE:
				return assetManager.get("stances/Offensive.png", Texture.class);
			case PRONE:
				return assetManager.get("stances/Prone.png", Texture.class);
			case SUPINE:
				return assetManager.get("stances/Supine.png", Texture.class);
			case AIRBORNE:
				return assetManager.get("stances/Airborne.png", Texture.class);
			case CASTING:
				return assetManager.get("stances/Casting.png", Texture.class);
			case KNOTTED:
				return assetManager.get("stances/Knotted.png", Texture.class);
			default:
				return assetManager.get("stances/Balanced.png", Texture.class);
			}
	}
	
	public int getVictoryScene(){
		return victoryScene;
	}
	
	public int getDefeatScene(){
		return defeatScene;
	}
	
	public void dispose(){
		assetManager.unload("uiskin.json");
		assetManager.unload("sound.wav");
	}
	
	// this should pass in a technique that will be used if this button is pressed
	private ClickListener getListener(final Technique technique, final Sound buttonSound){
		return new ClickListener(){
	        @Override
	        public void clicked(InputEvent event, float x, float y) {
	        	buttonSound.play(.5f);
	        	selectedTechnique = technique;
	        }
	    };
	}
}

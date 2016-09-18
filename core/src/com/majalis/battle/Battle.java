package com.majalis.battle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
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
import com.majalis.character.AbstractCharacter;
import com.majalis.character.AbstractCharacter.Stance;
import com.majalis.character.EnemyCharacter;
import com.majalis.character.PlayerCharacter;
import com.majalis.character.Technique;
import com.majalis.save.SaveEnum;
import com.majalis.save.SaveService;
/*
 * Represents the logic for the flow of a battle.  Currently only supports 1 on 1.
 */
public class Battle extends Group{

	private static int[] POSSIBLE_KEYS = new int[]{Keys.A, Keys.S, Keys.D, Keys.F, Keys.G, Keys.H, Keys.J};
	private static char[] POSSIBLE_KEYS_CHAR = new char[]{'A','S','D','F','G','H','J'};
	
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
		displayTechniqueOptions();
	}
	
	private void displayTechniqueOptions(){
		table.clear();
		options = character.getPossibleTechniques();
		// this should map key presses to buttons based on this order, but not pass those key presses to the click listener
		
		//this should be in its own method which is called here and whenever a turn finishes
		for (int ii = 0; ii < options.size; ii++){
			TextButton button;
			Technique option = options.get(ii);
			button = new TextButton(option.getTechniqueName() + " ("+POSSIBLE_KEYS_CHAR[ii]+")", skin);
			button.addListener(getListener(option, buttonSound));
			table.add(button).row();
		}
        table.setFillParent(true);
        table.addAction(Actions.moveTo(1050, 150));
        this.addActor(table);
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

	// should probably use String builder to build a string to display in the console - needs to properly be getting information from the interactions - may need to be broken up into its own class
	private void resolveTechniques(AbstractCharacter firstCharacter, Technique firstTechnique, AbstractCharacter secondCharacter, Technique secondTechnique) {
		int rand = (int) Math.floor(Math.random() * 100);
		
		// this needs to be refactored to reduce code duplication and possibly to condense the whole algorithm down
		// this should probably display the attack you attempted to use, and then display that you used Fall Down / Trip instead.
		// can return extracted costs later for printing
		// will cause a character to fall over / lose endurance of its own volition
		firstTechnique = firstCharacter.extractCosts(firstTechnique);
		secondTechnique = secondCharacter.extractCosts(secondTechnique);
		
		double firstBlockMod = firstTechnique.getBlock() > rand * 2 ? 0 : (firstTechnique.getBlock() > rand ? .5 : 1);
		double secondBlockMod = secondTechnique.getBlock() > rand * 2 ? 0 : (secondTechnique.getBlock() > rand ? .5 : 1);
		
		// these attacks should be generated with all the information from the opposing technique that's relevant, then passed to the character, which will determine the results and return the result string
		Attack attackForFirst = new Attack((int)Math.floor(secondTechnique.getDamage() * firstBlockMod), secondTechnique.getForceStance());
		Attack attackForSecond = new Attack((int)Math.floor(firstTechnique.getDamage() * secondBlockMod), firstTechnique.getForceStance());
				
		console = "";
		Stance firstStance = firstTechnique.getStance();
		Stance secondStance = secondTechnique.getStance();
		// this should only display a message if stance has actually changed - current stance of player and enemy should be visible in UI
		console += getStanceString(firstCharacter, firstStance);
		console += getStanceString(secondCharacter, secondStance);
		firstCharacter.setStance(firstStance);
		secondCharacter.setStance(secondStance);
		
		Stance forcedStance = attackForFirst.getForceStance();
		
		if (forcedStance != null){
			firstCharacter.setStance(forcedStance);
		}
		
		console += getResultString(firstCharacter, secondCharacter, firstTechnique.getTechniqueName(), attackForSecond, secondBlockMod != 1);
		if (secondTechnique.getTechniqueName().equals("Erupt")){
			console += "The " + secondCharacter.getLabel() + " spews hot, thick semen into your bowels!\n";
		}
		else if (firstCharacter.getStance() == Stance.DOGGY){
			// need some way to get info from sex techniques to display here.  For now, some random fun text
			console += "You are being anally violated! Your hole is stretched by her fat dick! Your hole feels like it's on fire! Her cock glides smoothly through your irritated anal mucosa!\n"
					+ "Her rhythmic thrusting in and out of your asshole is emasculating! You are red-faced and embarassed because of her butt-stuffing! Your cock is ignored!\n";
		}
		else if (firstCharacter.getStance() == Stance.KNOTTED){
			console += "Her powerful hips try to force something big inside! You struggle... but can't escape!  Her grapefruit-sized knot slips into your rectum!  You take 4 damage!\n"
					+ "You can't dislodge it; it's too large! Your anus is permanently stretched! You learned about Anatomy(Wereslut)! You are being bred!\n"
					+ "The battle is over, but your ordeal has just begun! She's going to ejaculate her runny dog cum in your bowels!\n";
			if (secondTechnique.forceBattleOver()){	
				// player character should also be able to force battle over
				battleOver = true;
				victory = false;
			}
		}
		else {
			console += getResultString(secondCharacter, firstCharacter, secondTechnique.getTechniqueName(), attackForFirst, firstBlockMod != 1);
		}		
	}

	private String getStanceString(AbstractCharacter character, Stance stance) {
		return character.getLabel() + (character.getSecondPerson() ? " adopt " : " adopts ") + " a(n) " + stance.toString() + " stance!\n";
	}
	// this should be the result based on nested methods - iff a response is needed from the defender, call 
	private String getResultString(AbstractCharacter firstCharacter, AbstractCharacter secondCharacter, String technique, Attack attackForSecond, boolean blocked){
		return firstCharacter.doAttack(technique, secondCharacter, attackForSecond);
	}
	
	@Override
    public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		font.draw(batch, "HP/STAM/BAL " + String.valueOf(character.getCurrentHealth()) + "/" + String.valueOf(character.getCurrentStamina()) + "/" + String.valueOf(character.getStability()) + " (" + character.getStance().toString() + ")", 350, 160);
		font.draw(batch, "Enemy health: " + String.valueOf(enemy.getCurrentHealth()) + " (" + enemy.getStance().toString() + ")", 700, 160);		
		font.draw(batch, console, 170, 120);
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
	        	buttonSound.play();
	        	selectedTechnique = technique;
	        }
	    };
	}
}

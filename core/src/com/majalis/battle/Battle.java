package com.majalis.battle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.majalis.asset.AnimatedImage;
import com.majalis.asset.AssetEnum;
import com.majalis.character.AbstractCharacter;
import com.majalis.character.AbstractCharacter.Stance;
import com.majalis.character.Attack;
import com.majalis.character.PlayerCharacter.Stat;
import com.majalis.character.EnemyCharacter;
import com.majalis.character.PlayerCharacter;
import com.majalis.character.Technique;
import com.majalis.encounter.Background;
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
	private final Texture stanceArrow;
	private String console;
	private String skillDisplay;
	private Array<Technique> options;
	private Technique selectedTechnique;
	private Texture hoverStance;
	public boolean battleOver;
	public boolean victory;
	public boolean gameExit;
	public int struggle;
	public boolean inRear;
	public int battleEndCount;
	private int selection;
	private final AnimatedImage slash;
	
	public Battle(SaveService saveService, AssetManager assetManager, BitmapFont font, PlayerCharacter character, EnemyCharacter enemy, int victoryScene, int defeatScene, Background battleBackground, Background battleUI){
		this.saveService = saveService;
		this.assetManager = assetManager;
		this.font = font;
		this.character = character;
		this.enemy = enemy;
		this.victoryScene = victoryScene;
		this.defeatScene = defeatScene;
		console = "";
		skillDisplay = "";
		battleOver = false;
		gameExit = false;	
		this.addActor(battleBackground);
		this.addCharacter(character);
		this.addCharacter(enemy);
		this.addActor(battleUI);
		
		stanceArrow = assetManager.get(AssetEnum.STANCE_ARROW.getPath(), Texture.class);
		
		StanceActor newActor = new StanceActor(character, new Vector2(330, 540));
		this.addActor(newActor);
		newActor = new StanceActor(enemy, new Vector2(920, 540));
		this.addActor(newActor);
		skin = assetManager.get("uiskin.json", Skin.class);
		buttonSound = assetManager.get("sound.wav", Sound.class);
		table = new Table();
		this.addActor(table);
		displayTechniqueOptions();
		struggle = 0;
		inRear = false;
		battleEndCount = 0;
		selection = 0;
		
		Texture slashSheet = assetManager.get(AssetEnum.SLASH.getPath(), Texture.class);
		Array<TextureRegion> frames = new Array<TextureRegion>();
		for (int ii = 0; ii < 6; ii++){
			frames.add(new TextureRegion(slashSheet, ii * 512, 0, 512, 512));
		}
		
		Animation animation = new Animation(.07f, frames);
		slash = new AnimatedImage(animation, Scaling.fit, Align.right);
		slash.setState(1);
		
		slash.addAction(Actions.moveTo(500, 0));
		
		this.addActor(slash);
	}
	
	private void addCharacter(AbstractCharacter character){
		Group g = new Group();
		g.addActor(character);
		addActor(g);
	}
	
	private class StanceActor extends Actor{
		
		private final AbstractCharacter character;
		private final Vector2 position;
		private boolean hover;
		public StanceActor(AbstractCharacter character, Vector2 position) {
			this.character = character;
			this.position = position;
			this.setBounds(position.x, position.y, getStanceImage(character.getStance()).getWidth(), getStanceImage(character.getStance()).getHeight());
			hover = false;
			this.addListener(new ClickListener(){ 
				@Override
		        public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
					hover = true;
				}
				@Override
		        public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
					hover = false;
				}
			});
		}

		@Override
	    public void draw(Batch batch, float parentAlpha) {
			super.draw(batch, parentAlpha);
			batch.draw(getStanceImage(character.getStance()), position.x, position.y, 100, 115);
			if (hover){
				font.setColor(Color.GOLD);
				font.draw(batch, character.getStance().name(), position.x, position.y);
			}
	    }
		
	}
	
	private void displayTechniqueOptions(){
		table.clear();
		options = character.getPossibleTechniques();
		for (int ii = 0; ii < options.size; ii++){
			TextButton button;
			Technique option = options.get(ii);
			button = new TextButton(option.getTechniqueName() + (ii > POSSIBLE_KEYS_CHAR.length ? "" : " ("+POSSIBLE_KEYS_CHAR[ii]+")"), skin);
			button.addListener(getListener(option, ii));
			table.add(button).width(220).height(35).row();
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
		if(Gdx.input.isKeyJustPressed(Keys.UP)){
        	if (selection > 0) selection--;
        }
        else if(Gdx.input.isKeyJustPressed(Keys.DOWN)){
        	if (selection < options.size- 1) selection++;
        }
        else if(Gdx.input.isKeyJustPressed(Keys.ENTER)){
        	selectedTechnique = options.get(selection);
        }
		
		if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)){
			gameExit = true;
		}
		else {
			Technique playerTechnique = getTechnique();
			if (playerTechnique != null){		
				buttonSound.play(.5f);
	        	selection = 0;
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
		
		if (attackForFirstCharacter.isAttack()){
			slash.setState(0);
		}
		
		printToConsole(firstCharacter.receiveAttack(attackForFirstCharacter));
		printToConsole(secondCharacter.receiveAttack(attackForSecondCharacter));		
		
		if (secondCharacter.getBattleOver() >= 5){
			battleOver = true;
			victory = false;
		}
	}
	
	@Override
    public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		if (hoverStance != null){
			batch.draw(stanceArrow, 450, 550, 50, 90);
			batch.draw(hoverStance, 515, 540, 100, 115);
		}
		batch.draw(stanceArrow, 935, (147 + (int) ( (35/2.) * (options.size - 2) ) ) - 35 * selection, 30, 40);
		
		font.setColor(Color.WHITE);
		font.draw(batch, "Health: ", 70, 700);
		int healthDeg = character.getHealthDegradation();
		font.setColor(getDegradataionColor(healthDeg));
		font.draw(batch, String.valueOf(character.getCurrentHealth()) + " " + getHealthDescription(healthDeg), 125, 700);
		font.setColor(Color.WHITE);
		font.draw(batch, "Stamina: ", 70, 680);
		int staminaDeg = character.getStaminaDegradation();
		font.setColor(getDegradataionColor(staminaDeg));
		font.draw(batch, String.valueOf(character.getCurrentStamina()) + " " + getStaminaDescription(staminaDeg), 125, 680);
		font.setColor(Color.WHITE);
		font.draw(batch, "Balance: ", 70, 660);
		font.setColor(character.getStability() > 10 ? Color.WHITE : character.getStability() > 5 ? Color.GOLDENROD : Color.RED);
		font.draw(batch, String.valueOf(character.getStability()) + " " + (character.getStability() > 10 ? "(Stable)" : character.getStability() > 5 ? "(Unbalanced)" : "(Teetering)"), 125, 660);
		
		font.setColor(Color.WHITE);
		font.draw(batch, (character.getStat(Stat.MAGIC) > 1 ? "Mana: " + String.valueOf(character.getCurrentMana()) : "")  + "\nStance: " + character.getStance().toString(), 70, 640);		
		batch.draw(assetManager.get(character.getLustImagePath(), Texture.class), 60, 450, 100, 115);
		
		font.draw(batch, "Health: ", 1100, 650);
		int enemyHealthDeg = enemy.getHealthDegradation();
		font.setColor(getDegradataionColor(enemyHealthDeg));
		font.draw(batch, String.valueOf(enemy.getCurrentHealth()) + " " + getHealthDescription(enemyHealthDeg), 1150, 650);
		font.setColor(Color.WHITE);
		font.draw(batch, "Stance: " + enemy.getStance().toString(), 1100, 630);	
		// calls to enemy.getType() should all be replaced with polymorphic behavior of enemies
		batch.draw(assetManager.get(enemy.getLustImagePath(), Texture.class), 1150, 450, 100, 115);
		if (skillDisplay.equals("")){
			font.draw(batch, console, 80, 270);
		}
		else {
			font.draw(batch, skillDisplay, 80, 270);
		}
		
    }
	// this should be on abstract character
	private String getHealthDescription(int val){
		switch (val){
			case 0: return "(Good)";
			case 1: return "(Fair)";
			case 2: return "(Weakened)";
			case 3: return "(Critical)";
			default: return "(ERROR)";
		}
	}
	// this should be on abstract character
	private String getStaminaDescription(int val){
		switch (val){
			case 0: return "(Good)";
			case 1: return "(Winded)";
			case 2: return "(Tired)";
			case 3: return "(Exhausted)";
			default: return "(ERROR)";
		}
	}
	// this should be on abstract character
	private Color getDegradataionColor(int val){
		switch (val){
			case 0: return Color.WHITE;
			case 1: return Color.GOLDENROD;
			case 2: return Color.ORANGE;
			case 3: return Color.RED;
			default: return Color.MAGENTA;
		}
	}
	
	private Texture getStanceImage(Stance stance){
		return assetManager.get(stance.getPath(), Texture.class);
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
	private ClickListener getListener(final Technique technique, final int index){
		return new ClickListener(){
	        @Override
	        public void clicked(InputEvent event, float x, float y) {
	        	selectedTechnique = technique;
	        }
	        @Override
	        public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
				skillDisplay = technique.getTechniqueDescription();
				hoverStance = getStanceImage(technique.getStance());
				selection = index;
			}
			@Override
	        public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
				skillDisplay = "";
				hoverStance = null;
			}
	    };
	}
}

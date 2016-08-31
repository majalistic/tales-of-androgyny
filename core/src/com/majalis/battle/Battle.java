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

public class Battle extends Group{

	private final PlayerCharacter character;
	private final EnemyCharacter enemy;
	private final SaveService saveService;
	private final AssetManager assetManager;
	private final BitmapFont font;
	private final int victoryScene;
	private final int defeatScene;
	private final Array<TextButton> buttons;
	private String console;
	public boolean battleOver;
	public boolean victory;
	public boolean gameExit;
	public int recentKeyPress;
	
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
		Skin skin = assetManager.get("uiskin.json", Skin.class);
		Sound buttonSound = assetManager.get("sound.wav", Sound.class);
		buttons = new Array<TextButton>();
		
		Table table = new Table();
		Array<String> options = character.getPossibleTechniques();
		int[] possibleKeys = new int[]{Keys.A, Keys.S, Keys.D, Keys.F};
		for (int ii = 0; ii < 4; ii++){
			TextButton button;
			if (ii < options.size){
				button = new TextButton(options.get(ii), skin);
			}
			else {
				button = new TextButton("-", skin);
			}			
			button.addListener(getListener(possibleKeys[ii], buttonSound));
			buttons.add(button);
			table.add(button).row();
		}
        table.setFillParent(true);
        table.addAction(Actions.moveTo(330, 70));
        this.addActor(table);
        
        recentKeyPress = -1;
	}

	public void battleLoop() {
		if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)){
			gameExit = true;
		}
		// attack with balanced attack
		else {
			int keyPress = getKeyPress();
			if (keyPress != -1){				
				// handle synchronous attacks
					
				// possibly construct a separate class for this
				resolveTechniques(character, character.getTechnique(keyPress), enemy, enemy.getTechnique(character));
				
				saveService.saveDataValue(SaveEnum.PLAYER, character);
				saveService.saveDataValue(SaveEnum.ENEMY, enemy);
			}
		}
		
		if (character.currentHealth <= 0){
			victory = false;
			battleOver = true;
		}
		if (enemy.currentHealth <= 0){
			victory = true;
			battleOver = true;
		}
	}
	
	private int getKeyPress() {
		if (recentKeyPress != -1){
			int temp = recentKeyPress;
			recentKeyPress = -1;
			return temp;
		}
		int[] possibleKeys = new int[]{Keys.A, Keys.S, Keys.D, Keys.F};
		for (int possibleKey : possibleKeys){
			if (Gdx.input.isKeyJustPressed(possibleKey)){
				return possibleKey;
			}
		}
		return -1;
	}

	private void resolveTechniques(AbstractCharacter firstCharacter, Technique firstTechnique, AbstractCharacter secondCharacter, Technique secondTechnique) {
		int rand = (int) Math.floor(Math.random() * 100);
		
		// this should probably display the attack you attempted to use, and then display that you used Fall Down / Trip instead.
		// can return extracted costs later for printing
		// will cause a character to fall over / lose endurance of its own volition
		firstTechnique = firstCharacter.extractCosts(firstTechnique);
		secondTechnique = secondCharacter.extractCosts(secondTechnique);
		
		double firstBlockMod = firstTechnique.getBlock() > rand * 2 ? 0 : (firstTechnique.getBlock() > rand ? .5 : 1);
		double secondBlockMod = secondTechnique.getBlock() > rand * 2 ? 0 : (secondTechnique.getBlock() > rand ? .5 : 1);
		
		// these block rolls should roll half the block to determine if the block is a full block - that way additional block grants higher chance of full block
		Attack attackForFirst = new Attack((int)Math.floor(secondTechnique.getDamage() * firstBlockMod));
		Attack attackForSecond = new Attack((int)Math.floor(firstTechnique.getDamage() * secondBlockMod));
				
		console = "";
		Stance firstStance = firstTechnique.getStance();
		Stance secondStance = secondTechnique.getStance();
		console += getStanceString(firstCharacter, firstStance);
		console += getStanceString(secondCharacter, secondStance);
		
		firstCharacter.stance = firstStance;
		secondCharacter.stance = secondStance;
		
		console += "\n";
		
		console += getResultString(firstCharacter, secondCharacter, firstTechnique, attackForSecond, secondBlockMod != 1);
		console += getResultString(secondCharacter, firstCharacter, secondTechnique, attackForFirst, firstBlockMod != 1);		
	}

	private String getStanceString(AbstractCharacter character, Stance stance) {
		return character.label + (character.secondPerson ? " adopt " : " adopts ") + " a(n) " + stance.toString() + " stance!\n";
	}
	
	private String getResultString(AbstractCharacter firstCharacter, AbstractCharacter secondCharacter, Technique technique, Attack attackForSecond, boolean blocked){
		return firstCharacter.label + (firstCharacter.secondPerson ? " use " : " uses ") + technique.getTechniqueName() + " against " + (secondCharacter.secondPerson ? secondCharacter.label.toLowerCase() : secondCharacter.label) + (blocked ? " and is blocked " : "") + " for " + secondCharacter.receiveAttack(attackForSecond) + " damage!\n";
	}
	
	@Override
    public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		font.draw(batch, "Your health: " + String.valueOf(character.currentHealth), 450, 160);
		font.draw(batch, "Enemy health: " + String.valueOf(enemy.currentHealth), 700, 160);
		Array<String> options = character.getPossibleTechniques();
		for (int ii = 0; ii < 4; ii++){
			buttons.get(ii).setText(ii < options.size ? options.get(ii) : "-");
		}
		
		font.draw(batch, console, 450, 100);
    }
	
	public int getVictoryScene(){
		return victoryScene;
	}
	
	public int getDefeatScene(){
		return defeatScene;
	}
	
	private ClickListener getListener(final int keyPress, final Sound buttonSound){
		return new ClickListener(){
	        @Override
	        public void clicked(InputEvent event, float x, float y) {
	        	buttonSound.play();
	        	recentKeyPress = keyPress;
	        }
	    };
	}
}

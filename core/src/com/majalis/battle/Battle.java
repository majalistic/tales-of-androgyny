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
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.majalis.asset.AnimatedImage;
import com.majalis.asset.AssetEnum;
import com.majalis.character.AbstractCharacter;
import com.majalis.character.AbstractCharacter.Stance;
import com.majalis.character.Attack;
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
	private final AnimatedImage slash;
	private final Sound pop;
	private final Sound attackSound;
	private final Sound hitSound;
	private final Sound thwapping;
	private final Array<SoundTimer> soundBuffer;
	private final Image hoverImage;
	private final Group hoverGroup;
	private final Label console;
	private final Label skillDisplay;
	private final ProgressBar characterHealth;
	private final ProgressBar characterStamina;
	private final ProgressBar characterBalance;
	private final ProgressBar characterMana;
	private final ProgressBar enemyHealth;
	private final Image healthIcon;
	private final Image staminaIcon;
	private final Image balanceIcon;
	private final Image manaIcon;
	private final Image enemyHealthIcon;
	private String consoleText;
	private Array<TextButton> optionButtons;
	private Technique selectedTechnique;
	private Image characterArousal;
	private Image enemyArousal;
	public boolean battleOver;
	public boolean victory;
	public boolean gameExit;
	public int struggle;
	public boolean inRear;
	public int battleEndCount;
	private int selection;
	private boolean debug = false;
	private float scaler = 1.5f; //scale distances
	private final float hoverXPos = 317; 
	private final float hoverYPos = 35; 
	private final float consoleXPos = 800;
	private final float consoleYPos = 5;
	
	public Battle(SaveService saveService, AssetManager assetManager, BitmapFont font, PlayerCharacter character, EnemyCharacter enemy, int victoryScene, int defeatScene, Background battleBackground, Background battleUI, String consoleText){
		this.saveService = saveService;
		this.assetManager = assetManager;
		this.font = font;
		this.character = character;
		this.enemy = enemy;
		this.victoryScene = victoryScene;
		this.defeatScene = defeatScene;
		battleOver = false;
		gameExit = false;	
		this.addActor(battleBackground);
		this.addCharacter(character);
		this.addCharacter(enemy);
		this.addActor(battleUI);
		
		skin = assetManager.get(AssetEnum.BATTLE_SKIN.getPath(), Skin.class);
		buttonSound = assetManager.get(AssetEnum.BUTTON_SOUND.getPath(), Sound.class);
		
		characterHealth = new ProgressBar(0, 1, .05f, false, skin);
		characterHealth.setWidth(350);
		addActorAndListen(characterHealth, 130, 690);
		characterHealth.setValue(character.getHealthPercent());
		healthIcon = new Image(assetManager.get(character.getHealthDisplay(), Texture.class));
		addActorAndListen(healthIcon, 130, 690);
		
		characterStamina = new ProgressBar(0, 1, .05f, false, skin);
		characterStamina.setWidth(350);
		addActorAndListen(characterStamina, 130, 660);
		characterStamina.setValue(character.getStaminaPercent());
		staminaIcon = new Image(assetManager.get(character.getStaminaDisplay(), Texture.class));
		addActorAndListen(staminaIcon, 135, 660);
		
		characterBalance = new ProgressBar(0, 1, .05f, false, skin);
		characterBalance.setWidth(350);
		addActorAndListen(characterBalance, 130, 630);
		characterBalance.setValue(character.getBalancePercent());
		balanceIcon = new Image(assetManager.get(character.getBalanceDisplay(), Texture.class));
		addActorAndListen(balanceIcon, 130, 630);
		

		characterMana = new ProgressBar(0, 1, .05f, false, skin);
		characterMana.setWidth(350);
		if (character.hasMagic()){
			addActorAndListen(characterMana, 130, 600);
		}
		characterMana.setValue(character.getManaPercent());	
		manaIcon = new Image(assetManager.get(character.getManaDisplay(), Texture.class));
		addActorAndListen(manaIcon, 130, 600);	
		

		enemyHealth = new ProgressBar(0, 1, .05f, false, skin);
		enemyHealth.setWidth(350);
		addActorAndListen(enemyHealth, 1000, 640);
		enemyHealth.setValue(enemy.getHealthPercent());
		enemyHealthIcon = new Image(assetManager.get(character.getHealthDisplay(), Texture.class));
		addActorAndListen(enemyHealthIcon, 1000, 640);
		
		Image consoleBox = new Image(assetManager.get(AssetEnum.BATTLE_TEXTBOX.getPath(), Texture.class));
		addActorAndListen(consoleBox, consoleXPos, consoleYPos);

		
		this.hoverImage = new Image(assetManager.get(AssetEnum.BATTLE_HOVER.getPath(), Texture.class));
		hoverImage.setPosition(hoverXPos, hoverYPos);		
		hoverGroup = new Group();
		hoverGroup.addActor(hoverImage);
		
		struggle = 0;
		inRear = false;
		battleEndCount = 0;
		
		Image characterPortrait = new Image(assetManager.get(AssetEnum.CHARACTER_POTRAIT.getPath(), Texture.class));
		addActorAndListen(characterPortrait, 0, 618);

		characterArousal = new Image(assetManager.get(character.getLustImagePath(), Texture.class));
		addActorAndListen(characterArousal, 102, 490);
		characterArousal.setSize(150, 150);
		
		enemyArousal = new Image(assetManager.get(enemy.getLustImagePath(), Texture.class));
		addActorAndListen(enemyArousal, 1073, 505);
		enemyArousal.setSize(150, 150);
		
		StanceActor newActor = new StanceActor(character);
		addActorAndListen(newActor, 397, 565);
		newActor.setSize(100*scaler, 115*scaler);
		newActor = new StanceActor(enemy);
		addActorAndListen(newActor, 866, 574);
		newActor.setSize(100*scaler, 115*scaler);
		
		hoverGroup.addAction(Actions.visible(false));
		this.addActor(hoverGroup);
		
		table = new Table();
		this.addActor(table);
		displayTechniqueOptions();
		
		Texture slashSheet = assetManager.get(AssetEnum.SLASH.getPath(), Texture.class);
		Array<TextureRegion> frames = new Array<TextureRegion>();
		for (int ii = 0; ii < 6; ii++){
			frames.add(new TextureRegion(slashSheet, ii * 512, 0, 512, 512));
		}
		
		Animation animation = new Animation(.07f, frames);
		slash = new AnimatedImage(animation, Scaling.fit, Align.right);
		slash.setState(1);
		
		slash.setPosition(500, 0);
		this.addActor(slash);
		
		pop = assetManager.get(AssetEnum.UNPLUGGED_POP.getPath(), Sound.class);
		attackSound = assetManager.get(AssetEnum.ATTACK_SOUND.getPath(), Sound.class);
		hitSound = assetManager.get(AssetEnum.HIT_SOUND.getPath(), Sound.class);
		thwapping = assetManager.get(AssetEnum.THWAPPING.getPath(), Sound.class);
		soundBuffer = new Array<SoundTimer>();
		
		this.consoleText = consoleText;
		console = new Label(consoleText, skin);
		console.setSize(700, 200);
		console.setWrap(true);
		console.setColor(Color.BLACK);
		console.setAlignment(Align.top);
		ScrollPane pane = new ScrollPane(console);
		pane.setBounds(consoleXPos+450, 50, 625, 350);
		pane.setScrollingDisabled(true, false);
		this.addActor(pane);
		
		skillDisplay = new Label("", skin);
		skillDisplay.setSize(350, 500);
		skillDisplay.setWrap(true);
		skillDisplay.setColor(Color.BLACK);
		skillDisplay.setAlignment(Align.top);
		ScrollPane pane2 = new ScrollPane(skillDisplay);
		pane2.setBounds(hoverXPos + 80, hoverYPos - 155, 500, 600);
		pane2.setScrollingDisabled(true, false);
		hoverGroup.addActor(pane2);
	}
	
	public int getVictoryScene(){
		return victoryScene;
	}
	
	public int getDefeatScene(){
		return defeatScene;
	}
	
	public void battleLoop() {
		Array<SoundTimer> toRemove = new Array<SoundTimer>();
		for (SoundTimer timer : soundBuffer){
			if (timer.decreaseTime()){
				toRemove.add(timer);
			}
		}
		soundBuffer.removeAll(toRemove, true);
		
		if(Gdx.input.isKeyJustPressed(Keys.UP)){
        	if (selection > 0) changeSelection(selection - 1);
        	else changeSelection(optionButtons.size-1);
		}
        else if(Gdx.input.isKeyJustPressed(Keys.DOWN)){
        	if (selection < optionButtons.size- 1) changeSelection(selection + 1);
        	else changeSelection(0);
        }
        else if(Gdx.input.isKeyJustPressed(Keys.ENTER)){
        	clickButton(optionButtons.get(selection));
        }
		
		if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)){
			gameExit = true;
		}
		else {
			if (selectedTechnique == null){
				int ii = 0;
				for (int possibleKey : POSSIBLE_KEYS){
					if (Gdx.input.isKeyJustPressed(possibleKey)){
						if (ii < optionButtons.size){
							selectedTechnique = clickButton(optionButtons.get(ii));
							break;
						}
					}
					ii++;
				}
			}
				
			if (selectedTechnique != null){		
				buttonSound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
				// possibly construct a separate class for this
				resolveTechniques(character, selectedTechnique, enemy, enemy.getTechnique(character));
				selectedTechnique = null;
				displayTechniqueOptions();
				saveService.saveDataValue(SaveEnum.PLAYER, character);
				saveService.saveDataValue(SaveEnum.ENEMY, enemy);
				saveService.saveDataValue(SaveEnum.CONSOLE, consoleText);				
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
			saveService.saveDataValue(SaveEnum.CONSOLE, "");
		}
	}
	
	private void displayTechniqueOptions(){
		table.clear();
		Array<Technique> options = character.getPossibleTechniques(enemy);
		optionButtons = new Array<TextButton>();
		
		for (int ii = 0; ii < options.size; ii++){
			SkillButton button;
			Technique option = options.get(ii);
			button = new SkillButton(option.getTechniqueName() + (ii > POSSIBLE_KEYS_CHAR.length ? "" : " ("+POSSIBLE_KEYS_CHAR[ii]+")"), skin, assetManager.get(option.getStance().getPath(), Texture.class));
			table.add(button).size(440, 76).row();
			optionButtons.add(button);
			boolean outOfStamina = false;
			boolean outOfStability = false;
			if(character.outOfStaminaOrStability(option)){
				outOfStamina = character.outOfStamina(option);
				outOfStability = !outOfStamina;
				TextButtonStyle style = new TextButtonStyle(button.getStyle());
				style.fontColor = Color.RED;
				button.setStyle(style);
			}
			else if (character.lowStaminaOrStability(option)){
				TextButtonStyle style = new TextButtonStyle(button.getStyle());
				style.fontColor = Color.ORANGE;
				button.setStyle(style);
			}
			button.addListener(getListener(option, (outOfStamina ? "THIS WILL CAUSE YOU TO COLLAPSE!\n" : outOfStability ? "THIS WILL CAUSE YOU TO LOSE YOUR FOOTING!\n" : "") + option.getTechniqueDescription(), ii, button));
		}
        table.setFillParent(true);
        table.align(Align.top);
        table.setPosition(25, 600);
        table.addAction(Actions.moveBy(125, 0, .1f));
        newSelection(0);
	}
	
	// should probably use String builder to build a string to display in the console - needs to properly be getting information from the interactions - may need to be broken up into its own class
	private void resolveTechniques(AbstractCharacter firstCharacter, Technique firstTechnique, AbstractCharacter secondCharacter, Technique secondTechnique) {
		consoleText = "";
		
		Stance oldStance = firstCharacter.getStance();
		
		printToConsole(firstCharacter.getStanceTransform(firstTechnique));
		printToConsole(secondCharacter.getStanceTransform(secondTechnique));
		
		firstTechnique = firstCharacter.extractCosts(firstTechnique);
		secondTechnique = secondCharacter.extractCosts(secondTechnique);
		
		Attack attackForFirstCharacter = secondCharacter.doAttack(secondTechnique.resolve(firstTechnique));
		Attack attackForSecondCharacter = firstCharacter.doAttack(firstTechnique.resolve(secondTechnique));
		
		if (attackForFirstCharacter.isAttack()){
			slash.setState(0);
			soundBuffer.add(new SoundTimer(attackSound, 0, .5f));
			if (attackForFirstCharacter.isSuccessful()){
				soundBuffer.add(new SoundTimer(hitSound, 20, .3f));
			}
		}
		if (attackForSecondCharacter.isAttack()){
			soundBuffer.add(new SoundTimer(attackSound, 15, .5f));
			if (attackForSecondCharacter.isSuccessful()){
				soundBuffer.add(new SoundTimer(hitSound, 35, .3f));
				enemy.hitAnimation();
			}
		}
		
		printToConsole(firstCharacter.receiveAttack(attackForFirstCharacter));
		printToConsole(secondCharacter.receiveAttack(attackForSecondCharacter));		
		
		if ( (oldStance == Stance.ANAL || oldStance == Stance.DOGGY || oldStance == Stance.STANDING || oldStance == Stance.COWGIRL) && (firstCharacter.getStance() == Stance.ANAL || firstCharacter.getStance() == Stance.DOGGY || firstCharacter.getStance() == Stance.STANDING || firstCharacter.getStance() == Stance.COWGIRL)){
			thwapping.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
		}
		
		if ( (oldStance == Stance.ANAL || oldStance == Stance.DOGGY || oldStance == Stance.STANDING || oldStance == Stance.COWGIRL) && (firstCharacter.getStance() != Stance.ANAL && firstCharacter.getStance() != Stance.DOGGY && firstCharacter.getStance() != Stance.STANDING && firstCharacter.getStance() != Stance.COWGIRL)){
			thwapping.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
			soundBuffer.add(new SoundTimer(pop, 105, .3f));
		}
		
		if (secondCharacter.getBattleOver() >= 5){
			battleOver = true;
			victory = false;
		}
		
		console.setText(consoleText);
		
		characterArousal.setDrawable(new TextureRegionDrawable(new TextureRegion(assetManager.get(character.getLustImagePath(), Texture.class))));
		enemyArousal.setDrawable(new TextureRegionDrawable(new TextureRegion(assetManager.get(enemy.getLustImagePath(), Texture.class))));
		characterHealth.setValue(character.getHealthPercent());
		characterStamina.setValue(character.getStaminaPercent());
		characterBalance.setValue(character.getBalancePercent());
		enemyHealth.setValue(enemy.getHealthPercent());
		healthIcon.setDrawable(new TextureRegionDrawable(new TextureRegion(assetManager.get(character.getHealthDisplay(), Texture.class))));
		staminaIcon.setDrawable(new TextureRegionDrawable(new TextureRegion(assetManager.get(character.getStaminaDisplay(), Texture.class))));
		balanceIcon.setDrawable(new TextureRegionDrawable(new TextureRegion(assetManager.get(character.getBalanceDisplay(), Texture.class))));
		manaIcon.setDrawable(new TextureRegionDrawable(new TextureRegion(assetManager.get(character.getManaDisplay(), Texture.class))));
		enemyHealthIcon.setDrawable(new TextureRegionDrawable(new TextureRegion(assetManager.get(enemy.getHealthDisplay(), Texture.class))));
	}
	
	private void printToConsole(Array<String> results){
		for (String result: results){
			printToConsole(result);
		}
	}
	
	private void printToConsole(String result){ 
		consoleText += result + "\n";
	}
	
	private void changeSelection(int newSelection){
		optionButtons.get(selection).addAction(Actions.sequence(Actions.delay(.05f), Actions.moveBy(-50, 0)));
    	newSelection(newSelection);
    	
	}
	
	private void newSelection(int newSelection){
    	optionButtons.get(newSelection).addAction(Actions.sequence(Actions.delay(.05f), Actions.moveBy(50, 0)));
		selection = newSelection;
	}
	
	/* Helper methods */
	
	private Texture getStanceImage(Stance stance){
		return assetManager.get(stance.getPath(), Texture.class);
	}
	
	private ClickListener getListener(final Technique technique, final String description, final int index, final TextButton button){
		return new ClickListener(){
	        @Override
	        public void clicked(InputEvent event, float x, float y) {
	        	selectedTechnique = technique;
	        }
	        @Override
	        public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
				skillDisplay.setText(description);
				changeSelection(index);		
				hoverGroup.clearActions();
				hoverGroup.addAction(Actions.visible(true));
				hoverGroup.addAction(Actions.fadeIn(.25f));
				
			}
			@Override
	        public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
				hoverGroup.addAction(Actions.fadeOut(2f));
			}
	    };
	}

	// creates a wrapper group for a character to be added to so that they can be removed and re-inserted during serialization
	private void addCharacter(AbstractCharacter character){
		Group g = new Group();
		g.addActor(character);
		addActor(g);
	}
	// simulates a button click for the button param
	private Technique clickButton(TextButton button){
		InputEvent event1 = new InputEvent();
        event1.setType(InputEvent.Type.touchDown);
        button.fire(event1);
        InputEvent event2 = new InputEvent();
        event2.setType(InputEvent.Type.touchUp);
        button.fire(event2);
        return selectedTechnique;
	}
	
	/* REFACTOR AND REMOVE BEYOND THIS LINE */
	
	private class StanceActor extends Actor{
		
		private final AbstractCharacter character;
		private final Texture hoverBox;
		private boolean hover;
		public StanceActor(AbstractCharacter character) {
			this.character = character;
			hover = false;
			hoverBox = assetManager.get(AssetEnum.BATTLE_HOVER.getPath(), Texture.class);
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
			batch.draw(getStanceImage(character.getStance()), getX(), getY(), getWidth(), getHeight());
			if (hover){
				batch.draw(hoverBox, getX() - 45, getY() - 60, getWidth() + 100, 40);
				font.setColor(Color.BLACK);
				font.draw(batch, character.getStance().name(), getX(), getY() - 30, 100, Align.center, false);
			}
	    }
	}
	
	private class SkillButton extends TextButton{
		Texture stanceIcon;
		public SkillButton(String text, Skin skin, Texture stanceIcon) {
			super(text, skin);
			this.stanceIcon = stanceIcon;
		}
		@Override
	    public void draw(Batch batch, float parentAlpha) {
			super.draw(batch, parentAlpha);
			batch.draw(stanceIcon, getX() + 372, getY()+3, 63, 76);
		}		
	}
	
	// this should be refactored into a delayed action
	private class SoundTimer{
		int timeLeft;
		Sound sound;
		float volume;
		
		SoundTimer(Sound sound, int timeLeft, float volume){
			this.sound = sound;
			this.timeLeft = timeLeft;
			this.volume = volume;
		}
		
		public boolean decreaseTime(){
			timeLeft--;
			boolean played = timeLeft <= 0;
			if (played){
				sound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *volume);
				
			}
			return played;
		}
	}
	
	/*
	 * Temporary debug helper methods for positioning scene2d actors 
	 */
	private void addActorAndListen(Actor actor, float x, float y){
		this.addActor(actor);
		actor.setPosition(x*scaler, y*scaler);
		addDragListener(actor);
	}
	
	private void addDragListener(final Actor actor){
		actor.addListener(new DragListener(){
			@Override
		    public void drag(InputEvent event, float x, float y, int pointer) {
				if (debug){
			        actor.moveBy(x - actor.getWidth() / 2, y - actor.getHeight() / 2);
			        System.out.println(actor.getX() + ", " + actor.getY());
				}
		    }
		});
	}
}

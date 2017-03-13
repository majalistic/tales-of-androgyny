package com.majalis.character;

import static com.majalis.character.Techniques.*;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.ObjectMap;
import com.majalis.asset.AnimatedActor;
import com.majalis.asset.AssetEnum;
import com.majalis.battle.Battle.Outcome;
import com.majalis.battle.BattleFactory.EnemyEnum;
import com.majalis.character.Item.Weapon;
import com.majalis.character.Item.WeaponType;

/*
 * Abstract class that all enemies extend - currently concrete to represent a generic "enemy".
 */
public class EnemyCharacter extends AbstractCharacter {

	private transient ObjectMap<Stance, Texture> textures;
	private transient Texture defaultTexture;
	private String imagePath;
	private ObjectMap<String, String> textureImagePaths;
	private String bgPath;
	private int holdLength;
	private int climaxCounter;
	private transient AnimatedActor animation;
	private String currentDisplay;
	private Techniques nextMove;
	private boolean initializedMove;
	private int range;
	
	@SuppressWarnings("unused")
	private EnemyCharacter() {}
	
	public EnemyCharacter(Texture texture, ObjectMap<Stance, Texture> textures, EnemyEnum enemyType) {
		super(true);
		this.enemyType = enemyType;
		init(texture, textures);
		initializedMove = false;
		phallus = PhallusType.MONSTER;
		textureImagePaths = new ObjectMap<String, String>();
		bgPath = AssetEnum.FOREST_BG.getPath();
		switch(enemyType) {
			case WERESLUT:
				weapon = new Weapon(WeaponType.Claw);
				baseStrength = 5;
				baseAgility = 5;
				imagePath = AssetEnum.WEREBITCH.getPath();
				break;
			case HARPY:
				weapon = new Weapon(WeaponType.Talon);
				baseStrength = 4;
				textureImagePaths.put(Stance.FELLATIO.toString(), AssetEnum.HARPY_FELLATIO.getPath());
				imagePath = AssetEnum.HARPY.getPath();
				break;
			case BRIGAND:
				weapon = new Weapon(WeaponType.Gladius);
				phallus = PhallusType.NORMAL;
				baseStrength = 3;
				baseAgility = 4;
				imagePath = AssetEnum.BRIGAND.getPath();
				break;
			case SLIME:
				baseStrength = 2;
				baseEndurance = 4;
				baseAgility = 4;
				textureImagePaths.put(Stance.DOGGY.toString(), AssetEnum.SLIME_DOGGY.getPath());
				imagePath = AssetEnum.SLIME.getPath();
				break;
			case CENTAUR:
			case UNICORN:
				weapon = new Weapon(WeaponType.Bow);
				baseAgility = 4;
				basePerception = 5;
				baseEndurance = 4;
				bgPath = AssetEnum.PLAINS_BG.getPath();
				if (enemyType == EnemyEnum.CENTAUR) {
					imagePath =  AssetEnum.CENTAUR.getPath();
				}
				else {
					imagePath = AssetEnum.UNICORN.getPath();
					lust = 20;
				}
				break;
			case GOBLIN:
				weapon = new Weapon(WeaponType.Dagger);
				baseStrength = 4;
				baseEndurance = 4;
				bgPath = AssetEnum.ENCHANTED_FOREST_BG.getPath();
				imagePath = AssetEnum.GOBLIN.getPath();
				textureImagePaths.put(Stance.FACE_SITTING.toString(), AssetEnum.GOBLIN_FACE_SIT.getPath());
				textureImagePaths.put(Stance.SIXTY_NINE.toString(), AssetEnum.GOBLIN_FACE_SIT.getPath());
				baseAgility = 5;
				break;
			case ORC:
				weapon = new Weapon(WeaponType.Flail);
				baseDefense = 6;
				healthTiers.add(10);
				baseStrength = 7;
				baseEndurance = 5;
				baseAgility = 4;
				imagePath = AssetEnum.ORC.getPath();
				break;
		}
		staminaTiers.removeIndex(staminaTiers.size-1);
		staminaTiers.add(10);
		setStaminaToMax();
		lust = 0;
		label = enemyType.toString();
		this.currentHealth = getMaxHealth();
		this.stance = Stance.BALANCED;
	}
	
	public String getImagePath() {
		return imagePath;
	}
	
	public ObjectMap<String, String> getTextureImagePaths() {
		return textureImagePaths;
	}
	
	public String getBGPath() {
		return bgPath;
	}
	
	@Override
	public Attack doAttack(Attack resolvedAttack) {
		if (resolvedAttack.getGrapple() > 0) {
			struggle -= resolvedAttack.getGrapple();
			resolvedAttack.addMessage("They struggle to get you off!");
			if (struggle >= 3 && stance == Stance.COWGIRL) {
				resolvedAttack.addMessage("It's still stuck up inside of you!");
				resolvedAttack.addMessage("Well, I guess you know that.");
				resolvedAttack.addMessage("Kind of a colon crusher.");
			}
			else if (struggle > 0 && stance == Stance.COWGIRL) {
				resolvedAttack.addMessage("They're difficult to ride on top of!");
			}
			else if (struggle <= 0 && stance == Stance.COWGIRL) {
				struggle = 0;
				resolvedAttack.addMessage("They're about to buck you off!");
			}
		}
		if (resolvedAttack.isSuccessful()) {
			if (resolvedAttack.getForceStance() == Stance.FULL_NELSON && enemyType == EnemyEnum.BRIGAND) {
				resolvedAttack.addDialog("\"Got ya!\" she says, as she manhandles you from behind.");
			}
			
			if (oldStance != Stance.OFFENSIVE && stance == Stance.OFFENSIVE) {
				switch (enemyType) {
					case BRIGAND:
						resolvedAttack.addDialog("\"Oorah!\"");
						break;
					case WERESLUT:
						resolvedAttack.addDialog("\"Raaawr!\"");
						break;
					default:
				}
			}
			if (!oldStance.isErotic() && stance.isErotic()) {
				switch (enemyType) {
					case BRIGAND:
						if (stance.isAnalPenetration()) {
							resolvedAttack.addDialog("\"Oooooryah!\"");
						}
						else if (stance.isOralPenetration()) {
							resolvedAttack.addDialog("\"Yeah, that's right, suck it!\"");
						} 
						break;
					case CENTAUR:
						if (stance.isAnalPenetration()) {
							resolvedAttack.addDialog("\"Hmph.\"");
						}
						else if (stance.isOralPenetration()) {
							resolvedAttack.addDialog("\"Open up.\"");
						} 
						break;
					case GOBLIN:
						if (stance.isAnalPenetration()) {
							resolvedAttack.addDialog("\"Nyahaha! Up the butt!\"");
						}
						else if (stance.isOralPenetration()) {
							resolvedAttack.addDialog("\"That's right, pinkskin, suck on that gobbo dick!\"");
						} 
						break;
					case HARPY:
						break;
					case SLIME:
						break;
					case UNICORN:
						break;
					case WERESLUT:
						break;
					case ORC:
						if (stance.isAnalPenetration()) {
							resolvedAttack.addDialog("\"Oooooryah!\"");
						}	
						else if (stance.isOralPenetration()) {
							resolvedAttack.addDialog("\"Open wide!\"");
						}	
				}
			}
			
			if (resolvedAttack.isClimax()) {
				switch (enemyType) {
					case SLIME:
						resolvedAttack.addDialog("\"Here comes the slime, honey!\" she cries.");
						break;
					case BRIGAND:
						break;
					case CENTAUR:
						break;
					case GOBLIN:
						break;
					case HARPY:
						break;
					case UNICORN:
						break;
					case WERESLUT:
						break;
					case ORC:
						if (stance.isAnalPenetration() || stance.isOralPenetration()) {
							resolvedAttack.addDialog("\"Catch it in your gut, pinkskin!\" she bellows.");
						}
						break;						
					}
				climaxCounter++;
			}
		}
		return super.doAttack(resolvedAttack);
	}
	
	private boolean willFaceSit(AbstractCharacter target) {
		return target.getStance() == Stance.SUPINE && !isErect() && enemyType != EnemyEnum.CENTAUR && enemyType != EnemyEnum.UNICORN;
	}
	
	private Array<Techniques> getPossibleTechniques(AbstractCharacter target, Stance stance) {
		
		if (enemyType == EnemyEnum.SLIME && !stance.isIncapacitatingOrErotic()) {
			return getTechniques(target, SLIME_ATTACK, SLIME_QUIVER); 			
		}
		
		Array<Techniques> possibles = new Array<Techniques>();
		switch(stance) {
			case OFFENSIVE:
				if (willFaceSit(target)) {
					possibles.addAll(getTechniques(target, FACE_SIT));
				}				
				if (!target.stance.receivesMediumAttacks) {
					possibles.addAll(getTechniques(target, POWER_ATTACK, TEMPO_ATTACK, RESERVED_ATTACK));
				}
				else {
					if (enemyType == EnemyEnum.BRIGAND || enemyType == EnemyEnum.ORC) {
						possibles.addAll(getTechniques(target, ARMOR_SUNDER));
					}
					possibles.addAll(getTechniques(target, POWER_ATTACK, GUT_CHECK, RECKLESS_ATTACK, KNOCK_DOWN, TEMPO_ATTACK, RESERVED_ATTACK));
				}
				return possibles;
			case BALANCED:
				if (willFaceSit(target)) {
					possibles.addAll(getTechniques(target, FACE_SIT));
				}				
				if (!target.stance.receivesMediumAttacks) {
					possibles.addAll(getTechniques(target, SPRING_ATTACK, NEUTRAL_ATTACK));
				}
				else {
					possibles.addAll(getTechniques(target, SPRING_ATTACK, NEUTRAL_ATTACK, CAUTIOUS_ATTACK, BLOCK));
				}
				return possibles;
			case DEFENSIVE:
				if (!target.stance.receivesMediumAttacks) {
					possibles.addAll(getTechniques(target, REVERSAL_ATTACK, GUARD, SECOND_WIND));
				}
				else {
					possibles.addAll(getTechniques(target, REVERSAL_ATTACK, CAREFUL_ATTACK, GUARD, SECOND_WIND));
				}
				if (enemyType == EnemyEnum.BRIGAND) {
					possibles.addAll(getTechniques(target, PARRY));
				}
				return possibles;
			case COUNTER:
				return getTechniques(target, RIPOSTE, EN_GARDE);
			case PRONE:
			case SUPINE:
				return getTechniques(target, KIP_UP, STAND_UP, KNEE_UP, stance == Stance.PRONE ? REST_FACE_DOWN : REST);
			case KNEELING:
				return getTechniques(target, STAND_UP, STAY_KNELT);
			case FULL_NELSON:
				if (holdLength > 2) {
					holdLength = 0;
					return getTechniques(target, PENETRATE_STANDING);
				}
				else {
					holdLength++;
				}
				return getTechniques(target, HOLD);
			case FACE_SITTING:
				lust++;
				if (isErect()) {
					return getTechniques(target, SITTING_ORAL);
				}
				return getTechniques(target, RIDE_FACE);
			case SIXTY_NINE:
				lust++;
				if (lust > 14) {
					return getTechniques(target, ERUPT_SIXTY_NINE);
				}
				else {
					return getTechniques(target, RECIPROCATE);
				}	
			case DOGGY:
			case ANAL:
			case STANDING:
				lust++;
				if (enemyType != EnemyEnum.WERESLUT && lust > 15) {
					return getTechniques(target, ERUPT_ANAL);
				}
				else if (enemyType == EnemyEnum.WERESLUT && lust > 18) {
					return getTechniques(target, KNOT);
				}
				else {
					if (stance == Stance.ANAL) {
						return getTechniques(target, POUND_ANAL);
					}
					else if (stance == Stance.DOGGY) {
						return getTechniques(target, POUND_DOGGY);
					}
					else {
						return getTechniques(target, POUND_STANDING);
					}		
				}
			case COWGIRL:
				lust++;
				if (enemyType == EnemyEnum.WERESLUT && lust > 20) {
					return getTechniques(target, KNOT);
				}
				else if (enemyType != EnemyEnum.WERESLUT && lust > 20) {
					struggle = 0;
					return getTechniques(target, ERUPT_COWGIRL);
				}
				if (struggle <= 0) {
					return getTechniques(target, PUSH_OFF);
				}
				else {
					return getTechniques(target, BE_RIDDEN);
				}
			case HANDY:
				lust++;
				if (lust > 18) {
					return getTechniques(target, ERUPT_FACIAL);
				}
				return getTechniques(target, RECEIVE_HANDY);
			case KNOTTED:
				return getTechniques(target, KNOT_BANG);
			case AIRBORNE:
				return getTechniques(target, DIVEBOMB);
			case FELLATIO:
				lust++;
				if (lust > 14) {
					return getTechniques(target, ERUPT_ORAL);
				}
				else {
					return getTechniques(target, IRRUMATIO);
				}	
			case ERUPT:
				stance = Stance.BALANCED;
				return getPossibleTechniques(target, stance);
			case DOGGY_BOTTOM:
				if (struggle <= 0) {
					return getTechniques(target, RECEIVE_DOGGY);
				}
				return getTechniques(target, RECEIVE_DOGGY);
			case ANAL_BOTTOM:
				if (struggle <= 0) {
					return getTechniques(target, RECEIVE_ANAL);
				}
				return getTechniques(target, RECEIVE_ANAL);
			case FELLATIO_BOTTOM:
				if (struggle <= 0) {
					return getTechniques(target, SUCK_IT);
				}
				return getTechniques(target, SUCK_IT);
		default: return null;
		}
	}

	private Array<Techniques> getTechniques(AbstractCharacter target, Techniques... possibilities) {
		return new Array<Techniques>(possibilities);
	}
	
	public Technique getTechnique(AbstractCharacter target) {
		if (initializedMove && nextMove != null) {
			initializedMove = false;
			return getTechnique(target, nextMove);
		}
		
		if (lust < 10 && enemyType != EnemyEnum.CENTAUR) lust++;
		
		Array<Techniques> possibleTechniques = getPossibleTechniques(target, stance);
		
		if (willPounce()) {
			if (target.stance == Stance.PRONE ) {
				possibleTechniques = getTechniques(target, POUNCE_DOGGY);
			}
			else if (target.stance == Stance.SUPINE) {
				possibleTechniques = getTechniques(target, POUNCE_ANAL);
			}
			else if (target.stance == Stance.KNEELING) {
				possibleTechniques =  getTechniques(target, SAY_AHH);
			}
			else if (enemyType == EnemyEnum.HARPY) {
				possibleTechniques.add(FLY);
			}
			else if (target.stance.receivesMediumAttacks && enemyType == EnemyEnum.BRIGAND) {
				possibleTechniques.add(FULL_NELSON);
			}
		}
		
		ObjectMap<Technique, Techniques> techniqueToToken = new ObjectMap<Technique, Techniques>();
		Array<Technique> candidates = new Array<Technique>();
		for (Techniques token : possibleTechniques) {
			Technique candidate = getTechnique(target, token);
			techniqueToToken.put(candidate, token);
			candidates.add(candidate);
		}

		int choice = getRandomWeighting(candidates.size); 
		candidates.sort(new Technique.StaminaComparator());
		candidates.reverse();
		Technique technique = candidates.get(choice);
		while (outOfStamina(technique) && choice < candidates.size) {
			technique = candidates.get(choice);
			choice++;
		}
		candidates.sort(new Technique.StabilityComparator());
		candidates.reverse();
		int ii = 0;
		for (Technique possibleTechnique : candidates) {
			if (possibleTechnique == technique) choice = ii;
			ii++;
		}
		while (outOfStability(technique) && choice < candidates.size) {
			technique = candidates.get(choice);
			choice++;
		}
		nextMove = techniqueToToken.get(technique);
		return technique;	
	}
	
	@Override
	protected String getLeakMessage() {
		String message = "";
		
		if (buttful >= 20) {
			message = "Their belly looks pregnant, full of baby batter! It drools out of their well-used hole! Their movements are sluggish! -2 Agility.";
		}
		else if (buttful >= 10) {
			message = "Their gut is stuffed with semen!  It drools out!  They're too queasy to move quickly! -1 Agility.";
		}
		else if (buttful >= 5) {
			message = "Cum runs out of their full ass!";
		}
		else if (buttful > 1) {
			message = "They drool cum from their hole!";
		}
		else if (buttful == 1) {
			message = " The last of the cum runs out of their hole!";
		}
		drainButt();
		return message;
	}

	@Override
	protected String getDroolMessage() {
		String message = "";
		if (mouthful > 10) {
			message = "They vomit a tremendous load onto the ground!";
		}
		else if (mouthful > 5) {
			message = "They spew a massive load onto the ground!";
		}
		else {
			message = "They spit all of the cum in their mouth out onto the ground!";
		}
		drainMouth();
		return message;
	}
	
	@Override
	protected String climax() {
		Array<String> results = new Array<String>();
		switch (enemyType) {
			case GOBLIN: lust -= 2; break;
			default: lust -= 14;
		}
		if (oldStance == Stance.FELLATIO) {
			if (enemyType == EnemyEnum.HARPY) {
				results.add("A harpy semen bomb explodes in your mouth!  It tastes awful!");
				results.add("You are going to vomit!");
				results.add("You spew up harpy cum!  The harpy preens her feathers.");
			}
			else {
				results.add("Her cock erupts in your mouth!");
				results.add("You're forced to swallow her semen!");
			}
		}
		else if (oldStance == Stance.COWGIRL) {
			results.add("The " + getLabel() + " blasts off in your intestines while you bounce\non their cumming cock! You got butt-bombed!");
		}
		else if (oldStance == Stance.ANAL) {
			results.add("The " + getLabel() + "'s lovemaking reaches a climax!");
			results.add("They don't pull out! It twitches and throbs in your rectum!");
			results.add("They cum up your ass! Your stomach receives it!");				
		}
		else if (oldStance == Stance.HANDY) {
			results.add("Their cock jerks in your hand! They're gonna spew!");
			results.add("Their eyes roll into the back of their head! Here it comes!");
			results.add("It's too late to dodge! They blast a rope of cum on your face!");
			results.add("Rope after rope lands all over face!");
			results.add("They spewed cum all over your face!");
			results.add("You look like a glazed donut! Hilarious!");
			results.add("You've been bukkaked!");
		}
		else if (oldStance == Stance.STANDING || oldStance == Stance.DOGGY) {
			results.add("The " + getLabel() + " spews hot, thick semen into your bowels!");
			results.add("You are anally inseminated!");
			results.add("You're going to be farting cum for days!");
		}
		else if (oldStance == Stance.SIXTY_NINE) {
			results.add("Her cock erupts in your mouth!");
			results.add("You spit it up around her pulsing balls!!");
		}
		stance = Stance.ERUPT;
		String[] foo = results.toArray(String.class);
		return String.join("\n", foo);
	}
	
	private boolean willPounce() {
		IntArray randomValues = new IntArray(new int[]{10, 11, 12, 13});
		return enemyType != EnemyEnum.UNICORN && lust >= randomValues.random() && !stance.isIncapacitatingOrErotic();
	}
	
	private Technique getTechnique(AbstractCharacter target, Techniques technique) {
		return new Technique(technique.getTrait(), getCurrentState(target), enemyType == EnemyEnum.BRIGAND && technique == Techniques.PARRY ? 3 : 1);
	}
	
	private int getRandomWeighting(int size) {
		IntArray randomOptions = new IntArray();
		for (int ii = 0; ii < size; ii++) {
			randomOptions.add(ii);
		}
		
		int randomResult = randomOptions.random();
		
		IntArray randomWeighting = new IntArray();
		for (int ii = -1; ii < 2; ii++) {
			randomWeighting.add(ii);
		}
		switch (enemyType) {
			case WERESLUT:
			case GOBLIN:
				randomWeighting.add(-1); break;
			case BRIGAND: randomWeighting.add(0); randomWeighting.add(1); break;
			default: break;
		}
		
		randomResult += randomWeighting.random();
		if (randomResult >= size) {
			randomResult = size - 1;
		}
		else if (randomResult < 0) {
			randomResult = 0;
		}
		return randomResult;
	}
	
	@Override
    public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		Texture texture = textures.get(stance, defaultTexture);
		int x = (enemyType == EnemyEnum.HARPY && stance == Stance.FELLATIO) ? 150 : (enemyType == EnemyEnum.GOBLIN && stance == Stance.FACE_SITTING || stance == Stance.SIXTY_NINE) ? 400 : 600;
		int y = (enemyType == EnemyEnum.HARPY && stance != Stance.FELLATIO) ? 105 : (enemyType == EnemyEnum.GOBLIN && stance == Stance.FACE_SITTING || stance == Stance.SIXTY_NINE) ? 0 : 20;
		int width = (enemyType == EnemyEnum.GOBLIN && stance == Stance.FACE_SITTING || stance == Stance.SIXTY_NINE) ? (int) (texture.getWidth() / (texture.getHeight() / 1080.)) : (int) (texture.getWidth() / (texture.getHeight() / 975.));
		int height = (enemyType == EnemyEnum.GOBLIN && stance == Stance.FACE_SITTING || stance == Stance.SIXTY_NINE) ? 1080 : 975;
		range = 0;
		if (animation == null || (enemyType == EnemyEnum.HARPY && stance == Stance.FELLATIO) || (enemyType == EnemyEnum.BRIGAND && !(stance == Stance.DOGGY || stance == Stance.STANDING))) {
			if (range == 0) {
				batch.draw(texture, x, y, width, height);
			}
			else if (range == 1) {
				batch.draw(texture, x + 150, y + 150, width / 2, height / 2);
			}
			else {
				batch.draw(texture, x + 200, y + 350, width / 3, height / 3);
			}
			if (animation != null) animation.addAction(Actions.hide());
		}
		else {
			animation.addAction(Actions.show());
			animation.draw(batch, parentAlpha);
		}
    }
	
	public void init(Texture defaultTexture, ObjectMap<Stance, Texture> textures) {
		this.defaultTexture = defaultTexture;
		this.textures = textures;
		animation = getAnimatedActor(enemyType);
		currentDisplay = enemyType == EnemyEnum.BRIGAND ? "IFOS100N" :"Idle Erect";
		initializedMove = true;
	}
	
	public static AnimatedActor getAnimatedActor(EnemyEnum enemyType) {
		AnimatedActor animation = null; 
		if (enemyType == EnemyEnum.HARPY || enemyType == EnemyEnum.CENTAUR || enemyType == EnemyEnum.UNICORN || enemyType == EnemyEnum.BRIGAND) {
					
			animation = new AnimatedActor(enemyType.getAnimationPath() + ".atlas", enemyType.getAnimationPath() + ".json", enemyType == EnemyEnum.HARPY ? .75f : enemyType == EnemyEnum.BRIGAND ? .475f : .60f, enemyType == EnemyEnum.HARPY || enemyType == EnemyEnum.BRIGAND ? 1f : 1.8f);
			
			if (enemyType == EnemyEnum.HARPY) {
				animation.setSkeletonPosition(900, 550);
			}
			else if (enemyType == EnemyEnum.BRIGAND) {
				animation.setSkeletonPosition(775, 505);
			}
			else {
				animation.setSkeletonPosition(1000, 550);
			}
			
			if (enemyType == EnemyEnum.CENTAUR) {
				animation.setSkeletonSkin("BrownCentaur");
			}
			else if (enemyType == EnemyEnum.UNICORN) {
				animation.setSkeletonSkin("WhiteUnicorn");
			}
			animation.setAnimation(0, enemyType == EnemyEnum.BRIGAND ? "IFOS100N" :"Idle Erect", true);
		}
		return animation;
	}
	
	public void hitAnimation() {
		if (animation != null && enemyType != EnemyEnum.BRIGAND) {
			animation.setAnimation(0, "Hit Erect", false);
			animation.addAnimation(0, "Idle Erect", true, 1.0f);
		}
	}
	
	// for init in battlefactory
	public void setLust(int lust) { this.lust = lust; }
	
	// can put text here for an enemy getting more aroused
	@Override
	protected String increaseLust() {
		switch (stance) {
			case DOGGY:
			case ANAL:
			case STANDING:
			case KNOTTED:
			case FELLATIO:
				return increaseLust(1);
			default: return null;
		}
	}
	// can put text here for an enemy getting more aroused
	@Override
	protected String increaseLust(int lustIncrease) {
		lust += lustIncrease;
		return null;
	}
	
	@Override
	public String getDefeatMessage() {
		switch (enemyType) {
			case BRIGAND:
				return "You defeated the Brigand!\nShe kneels to the ground, unable to lift her weapon.";
			case CENTAUR:
				return "You defeated the Centaur!\nShe smiles, haggardly, acknowledging your strength, and bows slightly.";
			case GOBLIN:
				return "The Goblin is knocked to the ground, defeated!";
			case HARPY:
				return "The Harpy falls out of the sky, crashing to the ground!\nShe is defeated!";
			case SLIME:
				return "The Slime becomes unable to hold her form!\nShe is defeated!";
			case UNICORN:
			case WERESLUT:
			default:
				return super.getDefeatMessage();		
		}
	}
	
	public Outcome getOutcome(AbstractCharacter enemy) {
		if (currentHealth <= 0) return Outcome.VICTORY;
		else if (enemy.getCurrentHealth() <= 0) return Outcome.DEFEAT;
		switch(enemyType) {
			case BRIGAND:
				if (climaxCounter >= 2) return Outcome.SATISFIED;
				break;
			case CENTAUR:
				if (climaxCounter >= 1) return Outcome.SATISFIED;
				break;
			case GOBLIN:
				break;
			case ORC:
				if (climaxCounter >= 5) return Outcome.SATISFIED;
				break;
			case HARPY:
				if (climaxCounter >= 2) return Outcome.SATISFIED;
				break;
			case SLIME:
				break;
			case UNICORN:
				break;
			case WERESLUT:
				if (climaxCounter >= 2) return Outcome.SATISFIED;
				if (knotInflate >= 5) return Outcome.KNOT;
				break;
			default:
		
		}
		return null;
	}
	
	public String getOutcomeText(AbstractCharacter enemy) {
		switch (getOutcome(enemy)) {
			case KNOT: return "You've been knotted!!!\nYou are at her whims, now.";
			case SATISFIED: return enemyType == EnemyEnum.CENTAUR ? "You've been dominated by the centaur's massive horsecock." : "She seems satisfied. She's no longer hostile.";
			case DEFEAT: return enemy.getDefeatMessage();
			case VICTORY: return getDefeatMessage();
		}
		return null;
	}
	@Override
	protected int getClimaxVolume() {
		super.getClimaxVolume();
		switch(enemyType) {
			case CENTAUR: 
			case UNICORN: return 21;
			case GOBLIN: return 10;
			default: return 5;
		}
	}
	
	@Override
	protected boolean canBleed() { return enemyType != EnemyEnum.SLIME; }

	public void setClimaxCounter(int climaxCounter) {
		this.climaxCounter = climaxCounter;
	}

	public void toggle() {
		if (enemyType == EnemyEnum.BRIGAND) {
			currentDisplay = currentDisplay.equals("IFOS100") ? "IFOS100N" : "IFOS100";
			animation.setAnimation(0, currentDisplay, true);
		}
	}
} 

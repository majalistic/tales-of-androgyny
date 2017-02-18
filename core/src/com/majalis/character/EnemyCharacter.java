package com.majalis.character;

import static com.majalis.character.Techniques.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.ObjectMap;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.SkeletonJson;
import com.esotericsoftware.spine.SkeletonMeshRenderer;
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
	private transient TextureAtlas atlas;
	private transient SkeletonMeshRenderer renderer;
	private transient AnimationState state;
	private transient Skeleton skeleton;
	private String imagePath;
	private ObjectMap<String, String> textureImagePaths;
	private String bgPath;
	private int holdLength;
	private int climaxCounter;
	
	@SuppressWarnings("unused")
	private EnemyCharacter() {}
	
	public EnemyCharacter(Texture texture, ObjectMap<Stance, Texture> textures, EnemyEnum enemyType) {
		super(true);
		this.enemyType = enemyType;
		init(texture, textures);
		phallus = PhallusType.MONSTER;
		textureImagePaths = new ObjectMap<String, String>();
		bgPath = AssetEnum.FOREST_BG.getPath();
		switch(enemyType) {
			case WERESLUT:
				baseStrength = 5;
				baseAgility = 5;
				imagePath = AssetEnum.WEREBITCH.getPath();
				break;
			case HARPY:
				baseStrength = 4;
				textureImagePaths.put(Stance.FELLATIO.toString(), AssetEnum.HARPY_FELLATIO.getPath());
				imagePath = AssetEnum.HARPY.getPath();
				break;
			case BRIGAND:
				weapon = new Weapon(WeaponType.Gladius);
				phallus = PhallusType.NORMAL;
				baseStrength = 3;
				baseAgility = 3;
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
						if (stance.isAnal()) {
							resolvedAttack.addDialog("\"Oooooryah!\"");
						}
						else if (stance.isAnal()) {
							resolvedAttack.addDialog("\"Yeah, that's right, suck it!\"");
						} 
						break;
					case CENTAUR:
						if (stance.isAnal()) {
							resolvedAttack.addDialog("\"Hmph.\"");
						}
						else if (stance.isAnal()) {
							resolvedAttack.addDialog("\"Open up.\"");
						} 
						break;
					case GOBLIN:
						if (stance.isAnal()) {
							resolvedAttack.addDialog("\"Nyahaha! Up the butt!\"");
						}
						else if (stance.isAnal()) {
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
					}
				climaxCounter++;
			}
		}
		return super.doAttack(resolvedAttack);
	}
	
	private boolean willFaceSit(AbstractCharacter target) {
		return target.getStance() == Stance.SUPINE && !isErect() && enemyType != EnemyEnum.CENTAUR && enemyType != EnemyEnum.UNICORN;
	}
	
	private Array<Technique> getPossibleTechniques(AbstractCharacter target, Stance stance) {
		
		if (enemyType == EnemyEnum.SLIME && stance != Stance.DOGGY && stance != Stance.FELLATIO && stance != Stance.SUPINE && stance != Stance.PRONE && stance != Stance.COWGIRL && stance != Stance.STANDING && stance != Stance.HANDY
				&& stance != Stance.FACE_SITTING && stance != Stance.SIXTY_NINE) {
			return getTechniques(target, SLIME_ATTACK, SLIME_QUIVER); 			
		}
		
		Array<Technique> possibles = new Array<Technique>();
		switch(stance) {
			case OFFENSIVE:
				if (willFaceSit(target)) {
					possibles.addAll(getTechniques(target, FACE_SIT));
				}				
				if (!target.stance.receivesMediumAttacks) {
					possibles.addAll(getTechniques(target, POWER_ATTACK, TEMPO_ATTACK, RESERVED_ATTACK));
				}
				else {
					if (enemyType == EnemyEnum.BRIGAND) {
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
					possibles.addAll(getTechniques(target, ARMOR_SUNDER));
				}
				return possibles;
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
		default: return null;
		}
	}

	private Array<Technique> getTechniques(AbstractCharacter target, Techniques... possibilities) {
		Array<Technique> possibleTechniques = new Array<Technique>();
		
		for (Techniques technique : possibilities) {
			possibleTechniques.add(getTechnique(target, technique));
		}
		
		return possibleTechniques;
	}
	
	public Technique getTechnique(AbstractCharacter target) {
		if (lust < 10 && enemyType != EnemyEnum.CENTAUR) lust++;
		
		Array<Technique> possibleTechniques = getPossibleTechniques(target, stance);
		
		if (willPounce()) {
			if (target.stance == Stance.PRONE ) {
				return getTechnique(target, POUNCE_DOGGY);
			}
			else if (target.stance == Stance.SUPINE) {
				return getTechnique(target, POUNCE_ANAL);
			}
			else if (target.stance == Stance.KNEELING) {
				return getTechnique(target, SAY_AHH);
			}
			else if (enemyType == EnemyEnum.HARPY) {
				possibleTechniques.add(getTechnique(target, FLY));
			}
			else if (target.stance.receivesMediumAttacks && enemyType == EnemyEnum.BRIGAND) {
				possibleTechniques.add(getTechnique(target, FULL_NELSON));
			}
		}
		
		int choice = getRandomWeighting(possibleTechniques.size); 
		possibleTechniques.sort(new Technique.StaminaComparator());
		possibleTechniques.reverse();
		Technique technique = possibleTechniques.get(choice);
		while (outOfStamina(technique) && choice < possibleTechniques.size) {
			technique = possibleTechniques.get(choice);
			choice++;
		}
		possibleTechniques.sort(new Technique.StabilityComparator());
		possibleTechniques.reverse();
		int ii = 0;
		for (Technique possibleTechnique : possibleTechniques) {
			if (possibleTechnique == technique) choice = ii;
			ii++;
		}
		while (outOfStability(technique) && choice < possibleTechniques.size) {
			technique = possibleTechniques.get(choice);
			choice++;
		}
		return technique;
		
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
		else if (oldStance == Stance.STANDING || oldStance == Stance.DOGGY){
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
		return enemyType != EnemyEnum.UNICORN && lust >= randomValues.random() && stance != Stance.PRONE && stance != Stance.SUPINE && stance != Stance.AIRBORNE && stance != Stance.FELLATIO && stance != Stance.DOGGY && stance != Stance.ANAL && stance != Stance.ERUPT && stance != Stance.COWGIRL && stance != Stance.STANDING && stance != Stance.HANDY;
	}
	
	private Technique getTechnique(AbstractCharacter target, Techniques technique) {
		return new Technique(technique.getTrait(), getCurrentState(target), 1);
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
		if (atlas == null || enemyType == EnemyEnum.HARPY && stance == Stance.FELLATIO) {
			batch.draw(texture, x, y, width, height);
		}
		else {
			state.update(Gdx.graphics.getDeltaTime());
			state.apply(skeleton);
			skeleton.updateWorldTransform();
			renderer.draw((PolygonSpriteBatch)batch, skeleton);
		}
    }
	
	public void init(Texture defaultTexture, ObjectMap<Stance, Texture> textures) {
		this.defaultTexture = defaultTexture;
		this.textures = textures;
		
		if (enemyType == EnemyEnum.HARPY || enemyType == EnemyEnum.CENTAUR || enemyType == EnemyEnum.UNICORN) {
			
			renderer = new SkeletonMeshRenderer();
			renderer.setPremultipliedAlpha(true);
			atlas = new TextureAtlas(Gdx.files.internal(enemyType == EnemyEnum.HARPY ? "animation/Harpy.atlas" : "animation/Centaur.atlas"));
			SkeletonJson json = new SkeletonJson(atlas); // This loads skeleton JSON data, which is stateless.
			json.setScale(enemyType == EnemyEnum.HARPY ? .75f : .60f);
			SkeletonData skeletonData = json.readSkeletonData(Gdx.files.internal(enemyType == EnemyEnum.HARPY ? "animation/Harpy.json" : "animation/Centaur.json"));
			
			skeleton = new Skeleton(skeletonData); // Skeleton holds skeleton state (bone positions, slot attachments, etc).
			skeleton.setPosition(enemyType == EnemyEnum.HARPY ? 900 : 1000, 550);
			
			if (enemyType == EnemyEnum.CENTAUR) {
				skeleton.setSkin("BrownCentaur");
			}
			else if (enemyType == EnemyEnum.UNICORN) {
				skeleton.setSkin("WhiteUnicorn");
			}
			
			AnimationStateData stateData = new AnimationStateData(skeletonData); // Defines mixing (crossfading) between animations.

			state = new AnimationState(stateData); // Holds the animation state for a skeleton (current animation, time, etc).
			state.setTimeScale(enemyType == EnemyEnum.HARPY ? 1f : 1.8f); 

			// Queue animations on tracks 0 and 1.
			state.setAnimation(0, "Idle Erect", true);
			
		}
	}
	
	public void hitAnimation() {
		if (state != null) {
			state.setAnimation(0, "Hit Erect", false);
			state.addAnimation(0, "Idle Erect", true, 1.0f);
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
				if (climaxCounter >= 3) return Outcome.SATISFIED;
				break;
			case CENTAUR:
				if (climaxCounter >= 1) return Outcome.SATISFIED;
				break;
			case GOBLIN:
				break;
			case HARPY:
				if (climaxCounter >= 3) return Outcome.SATISFIED;
				break;
			case SLIME:
				break;
			case UNICORN:
				break;
			case WERESLUT:
				if (climaxCounter >= 3) return Outcome.SATISFIED;
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
} 

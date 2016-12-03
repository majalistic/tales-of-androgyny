package com.majalis.character;

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
import com.majalis.battle.BattleFactory.EnemyEnum;

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
	
	@SuppressWarnings("unused")
	private EnemyCharacter(){}
	
	public EnemyCharacter(Texture texture, ObjectMap<Stance, Texture> textures, EnemyEnum enemyType){
		super(true);
		this.enemyType = enemyType;
		init(texture, textures);
		phallus = PhallusType.MONSTER;
		textureImagePaths = new ObjectMap<String, String>();
		bgPath = AssetEnum.FOREST_BG.getPath();
		switch(enemyType){
			case WERESLUT:
				baseStrength = 5;
				baseAgility = 8;
				imagePath = AssetEnum.WEREBITCH.getPath();
				break;
			case HARPY:
				baseStrength = 4;
				baseAgility = 7;
				textureImagePaths.put(Stance.FELLATIO.toString(), AssetEnum.HARPY_FELLATIO.getPath());
				imagePath = AssetEnum.HARPY.getPath();
				break;
			case BRIGAND:
				phallus = PhallusType.NORMAL;
				baseStrength = 4;
				baseAgility = 5;
				imagePath = AssetEnum.BRIGAND.getPath();
				break;
			case SLIME:
				baseStrength = 2;
				baseEndurance = 4;
				baseAgility = 5;
				textureImagePaths.put(Stance.DOGGY.toString(), AssetEnum.SLIME_DOGGY.getPath());
				imagePath = AssetEnum.SLIME.getPath();
				break;
			case CENTAUR:
			case UNICORN:
				baseStrength = 4;
				baseAgility = 4;
				basePerception = 5;
				bgPath = AssetEnum.PLAINS_BG.getPath();
				if (enemyType == EnemyEnum.CENTAUR){
					imagePath =  AssetEnum.CENTAUR.getPath();
				}
				else {
					imagePath = AssetEnum.UNICORN.getPath();
					lust = 20;
				}
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
	
	public String getImagePath(){
		return imagePath;
	}
	
	public ObjectMap<String, String> getTextureImagePaths(){
		return textureImagePaths;
	}
	
	public String getBGPath(){
		return bgPath;
	}
	
	@Override
	public Attack doAttack(Attack resolvedAttack) {
		if (resolvedAttack.getGrapple() > 0){
			struggle -= resolvedAttack.getGrapple();
			resolvedAttack.addMessage("They struggle to get you off!");
			if (struggle >= 3 && stance == Stance.COWGIRL){
				resolvedAttack.addMessage("It's still stuck up inside of you!");
				resolvedAttack.addMessage("Well, I guess you know that.");
				resolvedAttack.addMessage("Kind of a colon crusher.");
			}
			else if (struggle > 0 && stance == Stance.COWGIRL){
				resolvedAttack.addMessage("They're difficult to ride on top of!");
			}
			else if (struggle <= 0 && stance == Stance.COWGIRL){
				struggle = 0;
				resolvedAttack.addMessage("They're about to buck you off!");
			}
		}	
		return super.doAttack(resolvedAttack);
	}
	
	private boolean willFaceSit(AbstractCharacter target){
		return target.getStance() == Stance.SUPINE && !isErect() && enemyType != EnemyEnum.CENTAUR && enemyType != EnemyEnum.UNICORN;
	}
	
	private Array<Technique> getPossibleTechniques(AbstractCharacter target, Stance stance){
		
		if (enemyType == EnemyEnum.SLIME && stance != Stance.DOGGY && stance != Stance.FELLATIO && stance != Stance.SUPINE && stance != Stance.PRONE && stance != Stance.COWGIRL && stance != Stance.STANDING && stance != Stance.HANDY){
			return getTechniques(Techniques.SLIME_ATTACK, Techniques.SLIME_QUIVER); 			
		}
		
		Array<Technique> possibles = new Array<Technique>();
		switch(stance){
			case OFFENSIVE:
				if (willFaceSit(target)){
					possibles.addAll(getTechniques(Techniques.FACE_SIT));
				}				
				if (!target.stance.receivesMediumAttacks){
					possibles.addAll(getTechniques(Techniques.POWER_ATTACK, Techniques.TEMPO_ATTACK, Techniques.RESERVED_ATTACK));
				}
				else {
					possibles.addAll(getTechniques(Techniques.POWER_ATTACK, Techniques.GUT_CHECK, Techniques.RECKLESS_ATTACK, Techniques.KNOCK_DOWN, Techniques.TEMPO_ATTACK, Techniques.RESERVED_ATTACK));
				}
				return possibles;
			case BALANCED:
				if (willFaceSit(target)){
					possibles.addAll(getTechniques(Techniques.FACE_SIT));
				}				
				if (!target.stance.receivesMediumAttacks){
					possibles.addAll(getTechniques(Techniques.SPRING_ATTACK, Techniques.NEUTRAL_ATTACK));
				}
				else {
					possibles.addAll(getTechniques(Techniques.SPRING_ATTACK, Techniques.NEUTRAL_ATTACK, Techniques.CAUTIOUS_ATTACK, Techniques.BLOCK));
				}
				return possibles;
			case DEFENSIVE:
				if (!target.stance.receivesMediumAttacks){
					getTechniques(Techniques.REVERSAL_ATTACK, Techniques.GUARD, Techniques.SECOND_WIND);
				}
				return getTechniques(Techniques.REVERSAL_ATTACK, Techniques.CAREFUL_ATTACK, Techniques.GUARD, Techniques.SECOND_WIND);
			case PRONE:
			case SUPINE:
				return getTechniques(Techniques.KIP_UP, Techniques.STAND_UP, Techniques.KNEE_UP, stance == Stance.PRONE ? Techniques.REST_FACE_DOWN : Techniques.REST);
			case KNEELING:
				return getTechniques(Techniques.STAND_UP, Techniques.STAY_KNELT);
			case FULL_NELSON:
				if (holdLength > 2){
					holdLength = 0;
					return getTechniques(Techniques.PENETRATE_STANDING);
				}
				else {
					holdLength++;
				}
				return getTechniques(Techniques.HOLD);
			case FACE_SITTING:
				lust++;
				if (isErect()){
					return getTechniques(Techniques.SITTING_ORAL);
				}
				return getTechniques(Techniques.RIDE_FACE);
			case SIXTY_NINE:
				lust++;
				if (lust > 14){
					return getTechniques(Techniques.ERUPT_SIXTY_NINE);
				}
				else {
					return getTechniques(Techniques.RECIPROCATE);
				}	
			case DOGGY:
			case ANAL:
			case STANDING:
				lust++;
				if (enemyType != EnemyEnum.WERESLUT && lust > 15){
					return getTechniques(Techniques.ERUPT_ANAL);
				}
				else if (enemyType == EnemyEnum.WERESLUT && lust > 18){
					return getTechniques(Techniques.KNOT);
				}
				else {
					if (stance == Stance.ANAL){
						return getTechniques(Techniques.POUND_ANAL);
					}
					else if (stance == Stance.DOGGY){
						return getTechniques(Techniques.POUND_DOGGY);
					}
					else {
						return getTechniques(Techniques.POUND_STANDING);
					}		
				}
			case COWGIRL:
				lust++;
				if (enemyType == EnemyEnum.WERESLUT && lust > 20){
					return getTechniques(Techniques.KNOT);
				}
				else if (enemyType != EnemyEnum.WERESLUT && lust > 20){
					struggle = 0;
					return getTechniques(Techniques.ERUPT_COWGIRL);
				}
				if (struggle <= 0){
					return getTechniques(Techniques.PUSH_OFF);
				}
				else {
					return getTechniques(Techniques.BE_RIDDEN);
				}
			case HANDY:
				lust++;
				if (lust > 18){
					return getTechniques(Techniques.ERUPT_FACIAL);
				}
				return getTechniques(Techniques.RECEIVE_HANDY);
			case KNOTTED:
				return getTechniques(Techniques.KNOT_BANG);
			case AIRBORNE:
				return getTechniques(Techniques.DIVEBOMB);
			case FELLATIO:
				lust++;
				if (lust > 14){
					return getTechniques(Techniques.ERUPT_ORAL);
				}
				else {
					return getTechniques(Techniques.IRRUMATIO);
				}	
			case ERUPT:
				stance = Stance.BALANCED;
				return getPossibleTechniques(target, stance);
		default: return null;
		}
	}

	private Array<Technique> getTechniques(Techniques... possibilities) {
		Array<Technique> possibleTechniques = new Array<Technique>();
		
		for (Techniques technique : possibilities){
			possibleTechniques.add(new Technique(technique.getTrait(), technique.getTrait().isSpell() ? getMagic() : technique.getTrait().isTaunt() ? getCharisma() : getStrength()));
		}
		
		return possibleTechniques;
	}
	
	public Technique getTechnique(AbstractCharacter target){
		if (lust < 10) lust++;
		
		Array<Technique> possibleTechniques = getPossibleTechniques(target, stance);
		
		if (willPounce()){
			if (target.stance == Stance.PRONE ){
				return getTechnique(Techniques.POUNCE_DOGGY);
			}
			else if (target.stance == Stance.SUPINE){
				return getTechnique(Techniques.POUNCE_ANAL);
			}
			else if (target.stance == Stance.KNEELING){
				return getTechnique(Techniques.SAY_AHH);
			}
			else if (enemyType == EnemyEnum.HARPY){
				possibleTechniques.add(getTechnique(Techniques.FLY));
			}
			else if (target.stance.receivesMediumAttacks && enemyType == EnemyEnum.BRIGAND){
				possibleTechniques.add(getTechnique(Techniques.FULL_NELSON));
			}
		}
		
		int choice = getRandomWeighting(possibleTechniques.size); 
		possibleTechniques.sort(new Technique.StaminaComparator());
		possibleTechniques.reverse();
		Technique technique = possibleTechniques.get(choice);
		while (outOfStamina(technique) && choice < possibleTechniques.size){
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
		while (outOfStability(technique) && choice < possibleTechniques.size){
			technique = possibleTechniques.get(choice);
			choice++;
		}
		
		return technique;
		
	}
	
	private boolean willPounce(){
		IntArray randomValues = new IntArray(new int[]{10, 11, 12, 13});
		return enemyType != EnemyEnum.UNICORN && lust >= randomValues.random() && stance != Stance.PRONE && stance != Stance.SUPINE && stance != Stance.AIRBORNE && stance != Stance.FELLATIO && stance != Stance.DOGGY && stance != Stance.ANAL && stance != Stance.ERUPT && stance != Stance.COWGIRL && stance != Stance.STANDING && stance != Stance.HANDY;
	}
	
	private Technique getTechnique(Techniques technique){
		return new Technique(technique.getTrait(), getStrength());
	}
	
	private int getRandomWeighting(int size){
		IntArray randomOptions = new IntArray();
		for (int ii = 0; ii < size; ii++){
			randomOptions.add(ii);
		}
		
		int randomResult = randomOptions.random();
		
		IntArray randomWeighting = new IntArray();
		for (int ii = -1; ii < 2; ii++) {
			randomWeighting.add(ii);
		}
		switch (enemyType){
			case WERESLUT: randomWeighting.add(-1); break;
			case BRIGAND: randomWeighting.add(0); randomWeighting.add(1); break;
			default: break;
		}
		
		randomResult += randomWeighting.random();
		if (randomResult >= size){
			randomResult = size - 1;
		}
		else if (randomResult < 0){
			randomResult = 0;
		}
		return randomResult;
	}
	
	@Override
    public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		Texture texture = textures.get(stance, defaultTexture);
		int width = enemyType == EnemyEnum.HARPY && stance == Stance.FELLATIO ? 150 : 600;
		int height = enemyType == EnemyEnum.HARPY && stance != Stance.FELLATIO ? 140 : 20;
		if (atlas == null || enemyType == EnemyEnum.HARPY && stance == Stance.FELLATIO){
			batch.draw(texture, width, height, (int) (texture.getWidth() / (texture.getHeight() / 975.)), 975);
		}
		else {
			state.update(Gdx.graphics.getDeltaTime());
			state.apply(skeleton);
			skeleton.updateWorldTransform();
			renderer.draw((PolygonSpriteBatch)batch, skeleton);
		}
    }
	
	public void init(Texture defaultTexture, ObjectMap<Stance, Texture> textures){
		this.defaultTexture = defaultTexture;
		this.textures = textures;
		
		if (enemyType == EnemyEnum.HARPY){
			
			renderer = new SkeletonMeshRenderer();
			renderer.setPremultipliedAlpha(true);
			atlas = new TextureAtlas(Gdx.files.internal("animation/Harpy.atlas"));
			SkeletonJson json = new SkeletonJson(atlas); // This loads skeleton JSON data, which is stateless.
			json.setScale(.75f); // Load the skeleton at .65% the size it was in Spine.
			SkeletonData skeletonData = json.readSkeletonData(Gdx.files.internal("animation/Harpy.json"));

			skeleton = new Skeleton(skeletonData); // Skeleton holds skeleton state (bone positions, slot attachments, etc).
			skeleton.setPosition(900, 550);

			AnimationStateData stateData = new AnimationStateData(skeletonData); // Defines mixing (crossfading) between animations.

			state = new AnimationState(stateData); // Holds the animation state for a skeleton (current animation, time, etc).
			state.setTimeScale(1f); // Slow all animations down to 60% speed.

			// Queue animations on tracks 0 and 1.
			state.setAnimation(0, "Idle Erect", true);
			
		}
	}
	
	public void hitAnimation(){
		if (state != null){
			state.setAnimation(0, "Hit Erect", false);
			state.addAnimation(0, "Idle Erect", true, 1.0f);
		}
	}
	
	// for init in battlefactory
	public void setLust(int lust){ this.lust = lust; }
	
	// can put text here for an enemy getting more aroused
	@Override
	protected String increaseLust() {
		switch (stance){
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
	
} 

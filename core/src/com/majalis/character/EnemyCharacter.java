package com.majalis.character;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import com.majalis.asset.AssetEnum;
import com.majalis.battle.BattleFactory.EnemyEnum;

/*
 * Abstract class that all enemies extend - currently concrete to represent a generic "enemy".
 */
public class EnemyCharacter extends AbstractCharacter {

	private transient Texture texture;
	private String imagePath;
	private String bgPath;
	
	@SuppressWarnings("unused")
	private EnemyCharacter(){}
	public EnemyCharacter(Texture texture, EnemyEnum enemyType){
		super(true);
		this.enemyType = enemyType;
		init(texture);
		phallus = PhallusType.MONSTER;
		switch(enemyType){
			case WERESLUT:
				baseStrength = 5;
				baseAgility = 8;
				imagePath = AssetEnum.WEREBITCH.getPath();
				bgPath = "enemies/WereUI.png";
				break;
			case HARPY:
				baseStrength = 4;
				baseAgility = 7;
				imagePath = AssetEnum.HARPY.getPath();
				bgPath = "enemies/HarpyUI.png";
				break;
			case BRIGAND:
				phallus = PhallusType.NORMAL;
				baseStrength = 4;
				baseAgility = 5;
				imagePath = AssetEnum.BRIGAND.getPath();
				bgPath = "enemies/BrigandUI.png";
				break;
			case SLIME:
				baseStrength = 2;
				baseEndurance = 4;
				baseAgility = 5;
				imagePath = AssetEnum.SLIME.getPath();
				bgPath = "enemies/SlimeUI.png";
				break;
		}
		staminaTiers.add(5);
		
		setStaminaToMax();
		lust = 0;
		label = enemyType.toString();
		this.currentHealth = getMaxHealth();
		this.stance = Stance.BALANCED;
	}
	
	public String getImagePath(){
		return imagePath;
	}
	
	public String getBGPath(){
		return bgPath;
	}
	
	private Array<Technique> getPossibleTechniques(AbstractCharacter target, Stance stance){
		
		if (enemyType == EnemyEnum.SLIME && stance != Stance.DOGGY && stance != Stance.FELLATIO && stance != Stance.SUPINE && stance != Stance.PRONE){
			return getTechniques(Techniques.SLIME_ATTACK, Techniques.SLIME_QUIVER); 			
		}
		
		switch(stance){
			case OFFENSIVE:
				return getTechniques(Techniques.STRONG_ATTACK, Techniques.RECKLESS_ATTACK, Techniques.KNOCK_DOWN, Techniques.TEMPO_ATTACK, Techniques.RESERVED_ATTACK);
			case BALANCED:
				return getTechniques(Techniques.SPRING_ATTACK, Techniques.NEUTRAL_ATTACK, Techniques.CAUTIOUS_ATTACK, Techniques.BLOCK);
			case DEFENSIVE:
				return getTechniques(Techniques.REVERSAL_ATTACK, Techniques.CAREFUL_ATTACK, Techniques.GUARD, Techniques.SECOND_WIND);
			case PRONE:
			case SUPINE:
				return getTechniques(Techniques.KIP_UP, Techniques.STAND_UP, Techniques.KNEE_UP, stance == Stance.PRONE ? Techniques.REST_FACE_DOWN : Techniques.REST);
			case KNEELING:
				return getTechniques(Techniques.STAND_UP);
			case DOGGY:
				lust++;
				if (enemyType != EnemyEnum.WERESLUT && lust > 14){
					return getTechniques(Techniques.ERUPT_ANAL);
				}
				if (enemyType == EnemyEnum.WERESLUT && lust > 17){
					return getTechniques(Techniques.KNOT);
				}
				else {
					return getTechniques(Techniques.POUND);
				}
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
		
		if (willPounce()){
			if (target.stance == Stance.PRONE || target.stance == Stance.SUPINE){
				return getTechnique(Techniques.POUNCE);
			}
			else if (target.stance == Stance.KNEELING){
				return getTechnique(Techniques.SAY_AHH);
			}
			else if (enemyType == EnemyEnum.HARPY){
				return getTechnique(Techniques.FLY);
			}
		}
		
		Array<Technique> possibleTechniques = getPossibleTechniques(target, stance);
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
		return lust >= 10 && stance != Stance.PRONE && stance != Stance.SUPINE && stance != Stance.AIRBORNE && stance != Stance.FELLATIO && stance != Stance.DOGGY && stance != Stance.ERUPT;
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
		batch.draw(texture, 500, enemyType == EnemyEnum.HARPY ? 100 : 0, 450, 600);
    }
	
	public void init(Texture texture){
		this.texture = texture;
	}
	// for init in battlefactory
	public void setLust(int lust){ this.lust = lust; }
	
	// can put text here for an enemy getting more aroused
	@Override
	protected String increaseLust() {
		switch (stance){
		case DOGGY:
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

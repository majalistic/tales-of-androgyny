package com.majalis.character;

import static com.majalis.character.Techniques.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.majalis.asset.AnimatedActor;
import com.majalis.battle.Battle.Outcome;
import com.majalis.character.Item.Weapon;
import com.majalis.technique.ClimaxTechnique.ClimaxType;

/*
 * Abstract class that all enemies extend - currently concrete to represent a generic "enemy".
 */
public class EnemyCharacter extends AbstractCharacter {

	private transient ObjectMap<Stance, Array<Texture>> textures;
	private transient Array<Texture> defaultTextures;
	private Array<String> imagePaths;
	private ObjectMap<String, Array<String>> textureImagePaths;
	private String bgPath;
	private ObjectMap <String, Integer> climaxCounters;
	
	private transient Array<AnimatedActor> animations;
	private transient ObjectSet<AnimatedActor> currentAnimationsPlaying;
	private String currentDisplay;
	private Techniques nextMove;
	private boolean initializedMove;
	private int range;
	private int currentFrame;
	private int selfRessurect;
	
	private boolean storyMode;
	
	@SuppressWarnings("unused")
	private EnemyCharacter() {}
	
	public EnemyCharacter(Array<Texture> textures, ObjectMap<Stance, Array<Texture>> textureMap, Array<AnimatedActor> animations, EnemyEnum enemyType) {
		this(textures, textureMap, animations, enemyType, Stance.BALANCED);
	}
	
	public EnemyCharacter(Array<Texture> textures, ObjectMap<Stance, Array<Texture>> textureMap, Array<AnimatedActor> animations, EnemyEnum enemyType, Stance stance) {
		this(textures, textureMap, animations, enemyType, stance, false);
	}
	
	public EnemyCharacter(Array<Texture> textures, ObjectMap<Stance, Array<Texture>> textureMap, Array<AnimatedActor> animations, EnemyEnum enemyType, Stance stance, boolean storyMode) {
		super(true);
		this.enemyType = enemyType;
		this.stance = stance;
		this.storyMode = storyMode;
		init(textures, textureMap, animations);
		initializedMove = false;
		climaxCounters = new ObjectMap<String, Integer>();
		currentFrame = enemyType == EnemyEnum.GHOST && !Gdx.app.getPreferences("tales-of-androgyny-preferences").getBoolean("blood", true) ? 1 : 0;
		if (enemyType == EnemyEnum.BUNNY) {
			String bunnyType = Gdx.app.getPreferences("tales-of-androgyny-preferences").getString("bunny", "CREAM");
			if (bunnyType.equals("CREAM")) currentFrame = 0;
			if (bunnyType.equals("VANILLA")) currentFrame = 1;
			if (bunnyType.equals("CARAMEL")) currentFrame = 2;
			if (bunnyType.equals("CHOCOLATE")) currentFrame = 3;
			if (bunnyType.equals("DARK-CHOCOLATE")) currentFrame = 4;
		}
		
		weapon = enemyType.getWeaponType() != null ? new Weapon (enemyType.getWeaponType()): null;
		armor = enemyType.getArmorType() != null ? new Armor (enemyType.getArmorType()): null;
		legwear = enemyType.getLegwearType() != null ? new Armor (enemyType.getLegwearType()): null;
		underwear = enemyType.getUnderwearType() != null ? new Armor (enemyType.getUnderwearType()): null;
		baseStrength = enemyType.getStrength();
		baseAgility = enemyType.getAgility();
		baseEndurance = enemyType.getEndurance();
		basePerception = enemyType.getPerception();
		baseMagic = enemyType.getMagic();
		baseCharisma = enemyType.getCharisma();
		baseDefense = enemyType.getDefense();
		healthTiers = enemyType.getHealthTiers();
		manaTiers = enemyType.getManaTiers();
		imagePaths = enemyType.getPaths();
		textureImagePaths = enemyType.getImagePaths();
		phallus = enemyType.getPhallusType();
		bgPath = enemyType.getBGPath();
		pronouns = enemyType.getPronounSet();
		lust = enemyType.getStartingLust();
		label = enemyType.toString();
		selfRessurect = enemyType == EnemyEnum.ANGEL ? 1 : 0;
		staminaTiers.removeIndex(staminaTiers.size - 1);
		staminaTiers.add(10);
		setStaminaToMax();
		setManaToMax();		
		this.currentHealth = getMaxHealth();
	}
	
	public Array<String> getImagePaths() {
		return imagePaths;
	}
	
	public ObjectMap<String, Array<String>> getTextureImagePaths() {
		return textureImagePaths;
	}
	
	public Array<Texture> getTextures(AssetManager assetManager) {
		Array<Texture> textures = new Array<Texture>();
		for (String path : imagePaths) {
			textures.add(assetManager.get(path, Texture.class));
		}
		return textures;
	}
	
	public String getBGPath() {
		return bgPath;
	}
	
	// rather than override doAttack, doAttack should call an abstract processAttack method in AbstractCharacter and this functionality should be built there, instead of calling return super.doAttack
	@Override
	public Attack doAttack(Attack resolvedAttack) {
		// if golem uses a certain attack, it should activate her dong
		if (enemyType == EnemyEnum.GOLEM) {
			if (resolvedAttack.getSelfEffect() != null && resolvedAttack.getSelfEffect().type == StatusType.ACTIVATE) {
				currentFrame = 1;
				lust = 100;
				baseStrength += 4;
				baseDefense += 3;
			}
		}
		
		if (stance == Stance.FELLATIO && enemyType == EnemyEnum.HARPY) {
			if (currentFrame == 3) currentFrame = 0; else currentFrame++;
		}
		
		if (enemyType == EnemyEnum.HARPY && oldStance.isOralPenetration() && !stance.isOralPenetration()) {
			currentFrame = 0;
		}
		
		if (resolvedAttack.getGrapple() != GrappleStatus.NULL && (stance == Stance.COWGIRL || stance == Stance.REVERSE_COWGIRL)) {
			// this will need to be aware of who the character receiving is - the check might need to be COWGIRL_BOTTOM instead of COWGIRL
			if (resolvedAttack.getGrapple().isDisadvantage()) {
				resolvedAttack.addMessage("It's still stuck up inside of you!");
				resolvedAttack.addMessage("Well, I guess you know that.");
				resolvedAttack.addMessage("Kind of a colon crusher.");
			}
			else if (resolvedAttack.getGrapple() == GrappleStatus.SCRAMBLE) {
				resolvedAttack.addMessage("They're difficult to ride on top of!");
			}
			else if (resolvedAttack.getGrapple().isAdvantage()) {
				resolvedAttack.addMessage("They're about to buck you off!");
			}
		}
		
		// play the dive bomb animation for now
		if (resolvedAttack.getForceStance() == Stance.FELLATIO_BOTTOM && enemyType == EnemyEnum.HARPY && !oldStance.isOralPenetration()) {
			attackAnimation();
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
			if (stance == Stance.HOLDING) {
				resolvedAttack.addDialog("The ogre grunts as he hoists you up by your legs, bringing your small hole to his throbbing mast.");
			}
			
			if (oldStance != Stance.KNOTTED && stance == Stance.KNOTTED) {
				if (enemyType == EnemyEnum.WERESLUT) {
					resolvedAttack.addDialog("\"Woof!  We're stuck together now! Woof!\"");
				}
			}
			if (oldStance != Stance.OVIPOSITION && stance == Stance.OVIPOSITION) {
				if (enemyType == EnemyEnum.SPIDER) {
					resolvedAttack.addDialog("\"Now hold still... while I lay my babies inside of you.\"");
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
					case GOBLIN_MALE:
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
						if (stance.isAnalPenetration()) {
							resolvedAttack.addDialog("\"Woof, mating! Mating! Take my puppies!\"");
						}	
						break;
					case ORC:
						if (stance.isAnalPenetration()) {
							resolvedAttack.addDialog("\"Oooooryah!\"");
						}	
						else if (stance == Stance.OUROBOROS) {
							resolvedAttack.addDialog("\"Nice try! Caught you!\"");
						}
						else if (stance.isOralPenetration()) {
							resolvedAttack.addDialog("\"Open wide!\"");
						}	
						break;
					case ADVENTURER:
						if (stance.isAnalPenetration()) {
							resolvedAttack.addDialog("\"Mmm... you'll do,\" he says, as he slips his wet one inside.");
						}
						if (stance.isAnalReceptive()) {
							resolvedAttack.addDialog("\"W-wait! Nn--nnff!\"");
						}
						break;
					case OGRE:
						if (stance.isAnalPenetration()) {
							resolvedAttack.addDialog("He vocalizes gruffly as he impales you on his massive phallus.");
						}
						break;
					case BEASTMISTRESS:
						break;
					case SPIDER:
						break;
					case GOLEM:
						resolvedAttack.addDialog("\"Initializing mating routine.\"");
						break;
					case GHOST:
						break;
					case BUNNY:
						break;
					case ANGEL:
						resolvedAttack.addDialog("\"Oh, why didn't you just say so?\" she says, gleefully sitting on your face, ass first.");
						break;
					case NAGA:
						break;
				}
			}
			
			if (resolvedAttack.isClimax()) {
				switch (enemyType) {
					case SLIME:
						resolvedAttack.addDialog("\"Here comes the slime, honey!\" she cries.");
						break;
					case BRIGAND:
						if (stance.isAnalPenetration()) {
							resolvedAttack.addDialog("\"Oof, here it comes! Right up your fat arse!\"");
						}
						else if (stance.isOralPenetration()) {
							resolvedAttack.addDialog("\"Swallow that greasy nut, cake boy!\"");
						}
						else if (stance == Stance.HANDY) {
							resolvedAttack.addDialog("\"Catch it on your face, boyo!\"");
						}
						break;
					case CENTAUR:
						if (stance.isAnalPenetration()) {
							resolvedAttack.addDialog("\"Apologies!\" she cries, as your intestines get hosed.");
						}
						else if (stance.isOralPenetration()) {
							resolvedAttack.addDialog("\"Oh... oh no!\" she cries, as she lets off a firehose in your stomach.");
						}
						break;
					case GOBLIN:
						if (stance.isAnalPenetration()) {
							resolvedAttack.addDialog("\"Nyahaha! Right up the shithole!\"");
						}
						else if (stance.isOralPenetration()) {
							resolvedAttack.addDialog("\"Nyahaha! Swallow my slime, pinkskin!\"");
						}
						break;
					case GOBLIN_MALE:
						break;
					case HARPY:
						if (stance.isAnalPenetration()) {
							resolvedAttack.addDialog("She screeches while dumping it in your rectum.");
						}
						else if (stance.isOralPenetration()) {
							resolvedAttack.addDialog("She lets out a high-pitched screech, and your uvula gets slammed with bird goo.");
						}
					case UNICORN:
						break;
					case WERESLUT:
						break;
					case ORC:
						if (stance.isAnalPenetration() || stance.isOralPenetration()) {
							resolvedAttack.addDialog("\"Catch it in your gut, pinkskin!\" she bellows.");
						}
						break;						
					case ADVENTURER:
						break;
					case OGRE:
						if (stance.isAnalPenetration()) {
							resolvedAttack.addDialog("He roars as he fills you with his thick, disgusting ogre semen.");
						}
					case BEASTMISTRESS:
						break;
					case SPIDER:
						break;
					case GOLEM:
						resolvedAttack.addDialog("\"Critical status! Ejecting semen collection tank overflow.\"");
						break;
					case GHOST:
						break;
					case BUNNY:
						resolvedAttack.addDialog("\"Looks like you're the one collecting, now.\"");
						break;
					case ANGEL:
						break;
					case NAGA:
						break;
				}
					
				climaxCounters.put(resolvedAttack.getClimaxType().toString(), climaxCounters.get(resolvedAttack.getClimaxType().toString(), 0) + 1);
				
				if ((enemyType == EnemyEnum.GOBLIN || enemyType == EnemyEnum.GOBLIN_MALE) && getClimaxCount() % 5 == 0) {
					setLust(3);
				}
			}
		}
		return super.doAttack(resolvedAttack);
	}
	
	private int getClimaxCount() {
		int climaxCount = 0;
		for (ObjectMap.Entry<String, Integer> entry : climaxCounters.entries()) {
			climaxCount += entry.value;
		}
		return climaxCount;
	}
	
	private int getToppingClimaxCount() {
		return getClimaxCount() - getReceptiveClimaxCount();
	}
	private int getReceptiveClimaxCount() {
		int climaxCount = 0;
		climaxCount += climaxCounters.get(ClimaxType.ANAL_RECEPTIVE.toString(), 0);
		climaxCount += climaxCounters.get(ClimaxType.ORAL_RECEPTIVE.toString(), 0);
		return climaxCount;
	}
	
	private boolean willFaceSit(AbstractCharacter target) {
		return target.getStance() == Stance.SUPINE && !isErect() && enemyType.willFaceSit();
	}
	
	private boolean isEnragedGolem() {
		return enemyType == EnemyEnum.GOLEM && currentFrame == 1;
	}
	
	private Array<Techniques> getPossibleTechniques(AbstractCharacter target, Stance stance) {
				
		if (enemyType == EnemyEnum.SLIME && !stance.isIncapacitatingOrErotic()) {
			return getTechniques(SLIME_ATTACK, SLIME_QUIVER); 			
		}
		else if (enemyType == EnemyEnum.OGRE && stance != Stance.KNEELING && !stance.isIncapacitatingOrErotic() && stance != Stance.HOLDING) {
			if (willPounce() && lust > 50) {
				if (target.getLegwearScore() <= 0 && target.getUnderwearScore() <= 0) {
					setLust(50);
					return getTechniques(SEIZE);	
				}
				else {
					return getTechniques(RIP);	
				}
			}
			if (weapon != null) {
				if (stance == Stance.OFFENSIVE) {
					return getTechniques(SMASH);
				}
				if (stance == Stance.BALANCED) {
					return getTechniques(LIFT_WEAPON);
				}
			}
			else {
				return getTechniques(SLAM);
			}	
		}
		else if (enemyType == EnemyEnum.GHOST) {
			if (stance == Stance.CASTING) {
				return currentMana > 10 ? getTechniques(COMBAT_FIRE) : getTechniques(FOCUS_ENERGY);
			}
			else {
				return getTechniques(INCANTATION);
			}			
		}
		else if (enemyType == EnemyEnum.ANGEL && !alreadyIncapacitated()) {
			if (selfRessurect == 2) selfRessurect = 3;
			if (currentHealth == 0 && selfRessurect == 1) {
				selfRessurect = 2;
				return getTechniques(ANGELIC_GRACE);
			}
			else if (stance == Stance.CASTING) {
				return getTechniques(HEAL);
			}
			else if (currentHealth < getMaxHealth() || selfRessurect > 1) {
				if (currentHealth < 40 && currentMana > 10) {
					return getTechniques(INCANTATION);
				}
				return getTechniques(TRUMPET);
			}
			else if (target.getStance() == Stance.SUPINE) {
				return getTechniques(FACE_SIT);
			}
			else {
				return getTechniques(DO_NOTHING);
			}
		}
		
		Array<Techniques> possibles = new Array<Techniques>();
		switch(stance) {
			case BLITZ:
				return getTechniques(ALL_OUT_BLITZ, HOLD_BACK);
			case OFFENSIVE:
				if (willFaceSit(target)) {
					possibles.addAll(getTechniques(FACE_SIT));
				}				
				if (!target.stance.receivesMediumAttacks()) {
					possibles.addAll(getTechniques(POWER_ATTACK, TEMPO_ATTACK, RESERVED_ATTACK));
				}
				else {
					if (enemyType.willArmorSunder()) {
						possibles.addAll(getTechniques(ARMOR_SUNDER));
					}
					if (enemyType == EnemyEnum.BEASTMISTRESS || isEnragedGolem()) {
						possibles.addAll(getTechniques(BLITZ_ATTACK, POWER_ATTACK,RECKLESS_ATTACK, KNOCK_DOWN, TEMPO_ATTACK, RESERVED_ATTACK));
					}
					else {
						possibles.addAll(getTechniques(POWER_ATTACK, GUT_CHECK, RECKLESS_ATTACK, KNOCK_DOWN, TEMPO_ATTACK, RESERVED_ATTACK));
					}
				}
				return possibles;
			case BALANCED:
				if (willFaceSit(target)) {
					possibles.addAll(getTechniques(FACE_SIT));
				}				
				if (enemyType == EnemyEnum.BEASTMISTRESS) {
					possibles.addAll(getTechniques(SPRING_ATTACK, NEUTRAL_ATTACK, DO_NOTHING));
				}
				else if (!target.stance.receivesMediumAttacks()) {
					possibles.addAll(getTechniques(SPRING_ATTACK, NEUTRAL_ATTACK, BLOCK));
				}
				else {
					possibles.addAll(getTechniques(SPRING_ATTACK, NEUTRAL_ATTACK, CAUTIOUS_ATTACK, BLOCK));
				}
				if (isEnragedGolem() && currentMana > 3) {
					possibles.addAll(getTechniques(INCANTATION));
				}
				if ((enemyType == EnemyEnum.GOLEM && currentFrame == 0 && (baseDefense <= 3 || currentHealth <= 30)) || 
					(enemyType == EnemyEnum.ADVENTURER && (((currentHealth < 30 && currentMana >= 10) || (currentMana % 10 != 0 && currentMana > 2 && statuses.get(StatusType.STRENGTH_BUFF.toString(), 0) == 0))))) {
					return getTechniques(INCANTATION);
				}
				return possibles;
			case DEFENSIVE:
				if (!target.stance.receivesMediumAttacks()) {
					possibles.addAll(getTechniques(REVERSAL_ATTACK, GUARD, SECOND_WIND));
				}
				else {
					possibles.addAll(getTechniques(REVERSAL_ATTACK, CAREFUL_ATTACK, GUARD, SECOND_WIND));
				}
				if (enemyType.willParry()) {
					possibles.addAll(getTechniques(PARRY));
				}
				if (enemyType == EnemyEnum.ADVENTURER) {
					possibles.addAll(getTechniques(TAUNT));
				}
				return possibles;
			case COUNTER:
				return getTechniques(RIPOSTE, EN_GARDE);
			case CASTING:
				if (enemyType == EnemyEnum.GOLEM) {
					if (currentFrame == 1) {
						return getTechniques(COMBAT_FIRE);
					}
					return getTechniques(ACTIVATE);
				}
				if (currentHealth < 30 && currentMana >= 7) {
					return getTechniques(COMBAT_HEAL);
				}
				if (currentMana % 7 != 0 && currentMana > 2 && statuses.get(StatusType.STRENGTH_BUFF.toString(), 0) == 0) {
					return getTechniques(TITAN_STRENGTH);
				}
				return getTechniques(ITEM_OR_CANCEL);
			case PRONE:
				return getTechniques(KIP_UP, STAND_UP, KNEE_UP, PUSH_UP, REST_FACE_DOWN, ROLL_OVER_UP);
			case SUPINE:
				return getTechniques(KIP_UP, STAND_UP, KNEE_UP, REST, ROLL_OVER_DOWN);
			case HANDS_AND_KNEES:
				return getTechniques(KNEE_UP_HANDS, STAND_UP_HANDS, STAY);
			case KNEELING:
				return getTechniques(STAND_UP, STAY_KNELT);
			case FULL_NELSON:
				if (grappleStatus == GrappleStatus.HOLD) {
					if (enemyType == EnemyEnum.SPIDER) {
						return getTechniques(OVIPOSITION);
					}
					else {
						return getTechniques(PENETRATE_STANDING);
					}
				}
				if (currentStamina > 4) {
					return getTechniques(GRIP);
				}
				else {
					return getTechniques(HOLD);
				}
			case GROUND_WRESTLE:
				if (currentStamina <= 0 || grappleStatus == GrappleStatus.HELD) {
					return getTechniques(REST_WRESTLE);
				}
				possibles.addAll(getTechniques(GRAPPLE, HOLD_WRESTLE, REST_WRESTLE));
				if (enemyType == EnemyEnum.ORC || enemyType == EnemyEnum.BRIGAND) {
					possibles.addAll(getTechniques(CHOKE));
				}
				
				if (target.getStance() == Stance.GROUND_WRESTLE_FACE_UP) {
					if (grappleStatus == GrappleStatus.HOLD) {
						return getTechniques(PENETRATE_MISSIONARY);
					}
					else if (hasGrappleAdvantage() && enemyType.prefersProneBone()) {
						possibles.addAll(getTechniques(FLIP_PRONE));
					}
				}
				else if (target.getStance() == Stance.GROUND_WRESTLE_FACE_DOWN) {
					if (grappleStatus == GrappleStatus.HOLD) {
						return getTechniques(PENETRATE_PRONE);
					}
					else if (hasGrappleAdvantage() && enemyType.prefersMissionary()) {
						possibles.addAll(getTechniques(FLIP_SUPINE));
					}
				}
				if (hasGrappleAdvantage()) {
					if (target.getStrength() + 3 < getStrength()) {
						return getTechniques(PIN);
					}
				}
				return possibles;
			case GROUND_WRESTLE_FACE_DOWN:
				if (currentStamina <= 0 || grappleStatus == GrappleStatus.HELD) {
					return getTechniques(REST_GROUND_DOWN);
				}
				possibles.addAll(getTechniques(REST_GROUND_DOWN, GRIND));
				if (hasGrappleAdvantage()) {
					possibles.addAll(getTechniques(BREAK_FREE_GROUND));
				}
				else {
					possibles.addAll(getTechniques(STRUGGLE_GROUND));
				}
				return possibles;
			case GROUND_WRESTLE_FACE_UP:
				if (currentStamina <= 0 || grappleStatus == GrappleStatus.HELD) {
					return getTechniques(REST_GROUND_UP);
				}
				possibles.addAll(getTechniques(REST_GROUND_UP));
				if (hasGrappleAdvantage()) {
					possibles.addAll(getTechniques(BREAK_FREE_GROUND_UP, FULL_REVERSAL));
				}
				else {
					if (grappleStatus.isDisadvantage()) {
						possibles.addAll(getTechniques(REVERSAL));
					}
					possibles.addAll(getTechniques(STRUGGLE_GROUND_UP));
				}
				return possibles;
			case FACE_SITTING:
				if (isErect() && !target.isChastitied()) {
					return getTechniques(SITTING_ORAL);
				}
				return getTechniques(RIDE_FACE);
			case SIXTY_NINE:
				if (lust > 14) {
					return getTechniques(ERUPT_SIXTY_NINE);
				}
				else {
					return getTechniques(RECIPROCATE);
				}	
			case DOGGY:
			case ANAL:
			case STANDING:
			case PRONE_BONE:
				if (enemyType != EnemyEnum.WERESLUT && lust > 15) {
					return getTechniques(ERUPT_ANAL);
				}
				else if (enemyType == EnemyEnum.WERESLUT && lust > 18) {
					return getTechniques(KNOT);
				}
				else {
					if (stance == Stance.ANAL) {
						return getTechniques(POUND_ANAL);
					}
					else if (stance == Stance.DOGGY) {
						return getTechniques(POUND_DOGGY, CRUSH_ASS);
					}
					else if (stance == Stance.PRONE_BONE) {
						return getTechniques(POUND_PRONE_BONE);
					}
					else {
						return getTechniques(POUND_STANDING);
					}		
				}
			case COWGIRL:
				if (enemyType == EnemyEnum.WERESLUT && lust > 20) {
					return getTechniques(KNOT);
				}
				else if (enemyType != EnemyEnum.WERESLUT && lust > 20) {;
					return getTechniques(ERUPT_COWGIRL);
				}
				return getTechniques(BE_RIDDEN);
			case REVERSE_COWGIRL:
				if (enemyType == EnemyEnum.WERESLUT && lust > 20) {
					return getTechniques(KNOT);
				}
				else if (enemyType != EnemyEnum.WERESLUT && lust > 20) {
					return getTechniques(ERUPT_COWGIRL);
				}
				return getTechniques(BE_RIDDEN_REVERSE);
			case HANDY:
				if (lust > 18) {
					return getTechniques(ERUPT_FACIAL);
				}
				return getTechniques(RECEIVE_HANDY);
			case KNOTTED:
				return getTechniques(KNOT_BANG);
			case MOUTH_KNOTTED:
				return getTechniques(MOUTH_KNOT_BANG);
			case AIRBORNE:
				return getTechniques(DIVEBOMB);
			case FELLATIO:
				if (enemyType != EnemyEnum.WERESLUT && lust > 14) {
					return getTechniques(ERUPT_ORAL);
				}
				else if (enemyType == EnemyEnum.WERESLUT && lust > 18) {
					return getTechniques(MOUTH_KNOT);
				}
				else {
					return getTechniques(IRRUMATIO, FORCE_DEEPTHROAT);
				}	
			case FACEFUCK:
				if (lust > 14) {
					return getTechniques(ERUPT_ORAL);
				}
				else {
					return getTechniques(FACEFUCK);
				}	
			case OUROBOROS:
				if (lust > 14) {
					return getTechniques(ERUPT_ORAL);
				}
				else {
					return getTechniques(ROUND_AND_ROUND);
				}	
			case OVIPOSITION:
				return getTechniques(LAY_EGGS);
			case ERUPT:
				stance = Stance.BALANCED;
				return getPossibleTechniques(target, stance);
			case DOGGY_BOTTOM:
				return getTechniques(RECEIVE_DOGGY);
			case ANAL_BOTTOM:
				return getTechniques(RECEIVE_ANAL);
			case PRONE_BONE_BOTTOM:
				return getTechniques(RECEIVE_PRONE_BONE);
			case FELLATIO_BOTTOM:
				return getTechniques(SUCK_IT);
			case FACEFUCK_BOTTOM:
				return getTechniques(GET_FACEFUCKED);
			case OUROBOROS_BOTTOM:
				return getTechniques(RECEIVE_OUROBOROS);
			case COWGIRL_BOTTOM:
				return getTechniques(RIDE_ON_IT, BOUNCE_ON_IT, SQUEEZE_IT);
			case REVERSE_COWGIRL_BOTTOM:
				return getTechniques(RIDE_ON_IT_REVERSE, BOUNCE_ON_IT_REVERSE, SQUEEZE_IT_REVERSE);
			case HOLDING:
				return getTechniques(OGRE_SMASH);
			case CRUSHING:
				if (lust > 58) {
					return getTechniques(ERUPT_ANAL);
				}
				else {
					return getTechniques(target.stance == Stance.SPREAD ? CRUSH : PULL_UP);
				}
			case WRAPPED:
				if (currentStamina <= 0 || grappleStatus.isDisadvantage()) {
					return getTechniques(SQUEEZE_RELEASE);
				}
				if (grappleStatus == GrappleStatus.HOLD) {
					return getTechniques(SQUEEZE_CRUSH);
				}
				return getTechniques(SQUEEZE, BITE);					
			default: return getTechniques(DO_NOTHING);
		}
	}

	private Array<Techniques> getTechniques(Techniques... possibilities) { return new Array<Techniques>(possibilities); }
	
	public Technique getTechnique(AbstractCharacter target) {
		if (initializedMove && nextMove != null) {
			initializedMove = false;
			return getTechnique(target, nextMove);
		}
		
		if (lust < 10 || enemyType == EnemyEnum.OGRE) {
			if (enemyType != EnemyEnum.CENTAUR && enemyType != EnemyEnum.BEASTMISTRESS && enemyType != EnemyEnum.GOLEM) lust++;
		}
		
		Array<Techniques> possibleTechniques = getPossibleTechniques(target, stance);
		
		if (enemyType == EnemyEnum.ADVENTURER && target.stance == Stance.SUPINE && target.isErect() && !stance.isIncapacitatingOrErotic()) {
			possibleTechniques = getTechniques(SIT_ON_IT);
		}
		
		if ((target.stance == Stance.PRONE || target.stance == Stance.SUPINE) && enemyType == EnemyEnum.NAGA && !stance.isIncapacitatingOrErotic()) {
			possibleTechniques = getTechniques(WRAP);
		}
		
		if (willPounce() && enemyType != EnemyEnum.OGRE) {
			
			if (target.stance == Stance.PRONE && enemyType.canProneBone() && enemyType.canWrestle()) {
				possibleTechniques = getTechniques(WRESTLE_TO_GROUND);
			}
			else if (target.stance == Stance.HANDS_AND_KNEES) {
				possibleTechniques = getTechniques(POUNCE_DOGGY);
			}
			else if (target.stance == Stance.SUPINE) {
				if (enemyType.canWrestle()) {
					possibleTechniques = getTechniques(WRESTLE_TO_GROUND_UP);
				}
				else if (enemyType == EnemyEnum.GOBLIN || enemyType == EnemyEnum.GOBLIN_MALE || enemyType == EnemyEnum.HARPY) {
					possibleTechniques = getTechniques(MOUNT_FACE);
				}
			}
			else if (target.stance == Stance.KNEELING && enemyType != EnemyEnum.HARPY) {
				possibleTechniques = getTechniques(SAY_AHH);
			}
			else if (target.stance == Stance.AIRBORNE && enemyType == EnemyEnum.ORC) {
				possibleTechniques = getTechniques(OUROBOROS);
			}
			else if (enemyType == EnemyEnum.HARPY) {
				possibleTechniques.add(FLY);
			}
			else if (target.stance.receivesMediumAttacks() && enemyType == EnemyEnum.BRIGAND || enemyType == EnemyEnum.SPIDER || (enemyType == EnemyEnum.ORC && weapon == null)) {
				possibleTechniques.add(FULL_NELSON);
			}
		}
		
		Array<Techniques> toRemove = new Array<Techniques>();
		for (Techniques technique : possibleTechniques) {
			// this should be seperate checks for plugged and bottom covered - bottom covered should pull down the outermost pants item if possible - otherwise need to think through the unremoveable cover case
			if (enemyType.isCorporeal() && technique.getTrait().getResultingStance().isAnalPenetration() && (target.isPlugged() || (target.getLegwearScore() > 0 && target.legwear.coversAnus()) || (target.getUnderwearScore() > 0 && target.underwear.coversAnus()))) {
				possibleTechniques.add(technique.getPluggedAlternate());
				toRemove.add(technique);
			}
		}
		possibleTechniques.removeAll(toRemove, true);
		
		ObjectMap<Technique, Techniques> techniqueToToken = new ObjectMap<Technique, Techniques>();
		Array<Technique> candidates = new Array<Technique>();
		// for all possible techniques, generate an actual technique ready to be used
		for (Techniques token : possibleTechniques) {
			Technique candidate = getTechnique(target, token);
			techniqueToToken.put(candidate, token);
			candidates.add(candidate);
		}

		// choose a random skill to start
		int choice = getRandomWeighting(candidates.size); 
		candidates.sort(new Technique.StaminaComparator());
		candidates.reverse();
		Technique technique = candidates.get(choice);
		// if the skill would lead to 0 stamina, choose a skill that would take less stamina
		while (outOfStamina(technique) && choice < candidates.size) {
			technique = candidates.get(choice);
			choice++;
		}
		candidates.sort(new Technique.StabilityComparator());
		candidates.reverse();
		int ii = 0;
		// start at the selection based on stamina
		for (Technique possibleTechnique : candidates) {
			if (possibleTechnique == technique) choice = ii;
			ii++;
		}
		// if the skill would lead to 0 stability, choose a skill that would take less stability
		while (outOfStability(technique) && choice < candidates.size) {
			technique = candidates.get(choice);
			choice++;
		}
		// if no technique can be used to prevent a falldown, use the technique that takes the least stamina (ideally recovering it)
		if (outOfStamina(technique)) {
			candidates.sort(new Technique.StaminaComparator());
			if (candidates.size > 0) {
				technique = candidates.get(0);
			}
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
			case GOBLIN: 
			case GOBLIN_MALE: 
				lust -= 2; break;
			default: lust -= 14;
		}
		
		switch (oldStance) {
			case ANAL:
				results.add("The " + getLabel() + "'s lovemaking reaches a climax!");
				results.add("They don't pull out! It twitches and throbs in your rectum!");
				results.add("They cum up your ass! Your stomach receives it!");		
				break;
			case COWGIRL:
				results.add("The " + getLabel() + " blasts off in your intestines while you bounce\non their cumming cock! You got butt-bombed!");
				break;
			case FELLATIO:
				if (enemyType == EnemyEnum.HARPY) {
					results.add("A harpy semen bomb explodes in your mouth!  It tastes awful!");
					results.add("You are going to vomit!");
					results.add("You spew up harpy cum!  The harpy preens her feathers.");
				}
				else {
					results.add("Her cock erupts in your mouth!");
					results.add("You're forced to swallow her semen!");
				}
				break;
			case HANDY:
				results.add("Their cock jerks in your hand! They're gonna spew!");
				results.add("Their eyes roll into the back of their head! Here it comes!");
				results.add("It's too late to dodge! They blast a rope of cum on your face!");
				results.add("Rope after rope lands all over face!");
				results.add("They spewed cum all over your face!");
				results.add("You look like a glazed donut! Hilarious!");
				results.add("You've been bukkaked!");
				break;
			case SIXTY_NINE:
				results.add("Her cock erupts in your mouth!");
				results.add("You spit it up around her pulsing balls!!");
				break;
			case ANAL_BOTTOM:
			case DOGGY_BOTTOM:
			case STANDING_BOTTOM:
			case COWGIRL_BOTTOM:
			case FELLATIO_BOTTOM:
			case SIXTY_NINE_BOTTOM:
				results.add("The " + getLabel() + " ejaculates!");
				ClimaxType climaxType = stance.isAnalReceptive() ? ClimaxType.ANAL_RECEPTIVE : ClimaxType.ORAL_RECEPTIVE;
				climaxCounters.put(climaxType.toString(), climaxCounters.get(climaxType.toString(), 0) + 1);
				break;
			case STANDING:
			case DOGGY:
				results.add("The " + getLabel() + " spews hot, thick semen into your bowels!");
				results.add("You are anally inseminated!");
				results.add("You're going to be farting cum for days!");
				break;
			default:
		}
	
		if (stance.isEroticPenetration()) {
			stance = Stance.ERUPT;
		}
		String joined = "";
		for (String foo : (String[]) results.toArray(String.class)) {
			joined += foo + "\n";
		}
		joined = joined.trim();
		
		return joined;
	}
	
	private boolean willPounce() {
		IntArray randomValues = new IntArray(new int[]{10, 11, 12, 13});
		return enemyType.willPounce() && lust >= randomValues.random() && !stance.isIncapacitatingOrErotic() && grappleStatus == GrappleStatus.NULL && (enemyType != EnemyEnum.BRIGAND || !storyMode);
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
			case ORC:
			case ADVENTURER:
			case SPIDER:
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
		if (enemyType == EnemyEnum.BRIGAND) {
			if (stance == Stance.DOGGY || stance == Stance.STANDING) {
				currentAnimationsPlaying.add(animations.get(1));
				currentAnimationsPlaying.remove(animations.get(0));
			}
			else {
				currentAnimationsPlaying.add(animations.get(0));
				currentAnimationsPlaying.remove(animations.get(1));
			}
		}
		if (currentAnimationsPlaying.size == 0 && enemyType == EnemyEnum.HARPY && !stance.isOralPenetration()) currentAnimationsPlaying.add(animations.get(0));
		if (currentAnimationsPlaying.size == 0 || (enemyType == EnemyEnum.ORC && stance == Stance.PRONE_BONE) || (enemyType == EnemyEnum.CENTAUR && (stance == Stance.DOGGY || stance == Stance.FELLATIO)) || (enemyType == EnemyEnum.BRIGAND && (stance == Stance.FELLATIO || stance == Stance.FACEFUCK || stance == Stance.ANAL))) {
			Array<Texture> textureCandidates = textures.get(stance, defaultTextures);
			if (textureCandidates == null) return;
			Texture texture;
			if (currentFrame >= textureCandidates.size) {
				if (textureCandidates.size > 0) texture = textureCandidates.get(0);
				else return;
			}
			else {
				texture = textureCandidates.get(currentFrame);
			}

			if (texture == null) return;
			
			int x = 600;
			int y = 20;
			float width =  (int) (texture.getWidth() / (texture.getHeight() / 975.));
			float height = 975;
			
			if (
				(enemyType == EnemyEnum.ADVENTURER && stance == Stance.COWGIRL_BOTTOM) ||
				(enemyType == EnemyEnum.HARPY && stance == Stance.FELLATIO) ||
				enemyType == EnemyEnum.BRIGAND ||
				(enemyType == EnemyEnum.ORC && stance == Stance.PRONE_BONE) ||
				enemyType == EnemyEnum.CENTAUR || 
				((enemyType == EnemyEnum.GOBLIN || enemyType == EnemyEnum.GOBLIN_MALE) && (stance == Stance.FACE_SITTING || stance == Stance.SIXTY_NINE || stance == Stance.DOGGY || stance == Stance.PRONE_BONE))) {
				x = ((enemyType == EnemyEnum.BRIGAND && stance != Stance.ANAL) || (enemyType == EnemyEnum.GOBLIN || enemyType == EnemyEnum.GOBLIN_MALE) && (stance == Stance.FACE_SITTING || stance == Stance.SIXTY_NINE)) ? 400 : 0;
				y = 0;
				width = (int) (texture.getWidth() / (texture.getHeight() / 1080.));
				height = 1080;
			}
			
			range = 0;
			if (range == 0) {
				batch.draw(texture, x + getX(), y + getY(), width, height);
			}
			/*else if (range == 1) {
				batch.draw(texture, x + 150, y + 150, width / 2, height / 2);
			}
			else {
				batch.draw(texture, x + 200, y + 350, width / 3, height / 3);
			}*/
		}
		else {
			for (AnimatedActor animation: currentAnimationsPlaying) {
				animation.draw(batch,  parentAlpha);
			}
		}
    }
	// this should be refactored so that the enemy simply receives the assetManager and uses what it requires to reinitialize itself
	public void init(Array<Texture> defaultTextures, ObjectMap<Stance, Array<Texture>> textures, Array<AnimatedActor> animations) {
		this.defaultTextures = defaultTextures == null ? new Array<Texture>() : defaultTextures;
		this.textures = textures;
		this.animations = animations;
		this.currentAnimationsPlaying = new ObjectSet<AnimatedActor>();
		if (animations.size > 0 && (enemyType != EnemyEnum.HARPY || stance != Stance.FELLATIO) && (enemyType != EnemyEnum.BRIGAND || (stance != Stance.DOGGY && stance != Stance.STANDING))) {
			currentAnimationsPlaying.add(animations.first());
		}
		
		currentDisplay = enemyType == EnemyEnum.BRIGAND ? "IFOS100N" :"Idle Erect";
		initializedMove = true;
	}

	@Override
	public void act(float delta) {
		super.act(delta);
		if (animations.size > 0) {
			animations.get(0).act(delta);
		}
	}
	
	public void attackAnimation() {
		final Boolean fellatio = stance.isOralPenetration();
		if (animations.size > 1) {
			if (enemyType == EnemyEnum.HARPY) {
				final String preAttack = fellatio ? "Air BJ" : "Attack Erect";
				final String attack = fellatio ? "bj" : "attack";
				animations.get(0).addAction(Actions.sequence(
					Actions.delay(1), 
					new Action() {
						@Override
						public boolean act(float delta) {
							animations.get(0).setAnimation(0, preAttack, false);
							animations.get(0).addAnimation(0, "Idle Erect", true, 1.6f);
							return true;
					}},	
					Actions.delay(1), 
					new Action() {
						@Override
						public boolean act(float delta) {
							animations.get(1).setAnimation(0, attack, false);
							currentAnimationsPlaying.add(animations.get(1));
							currentAnimationsPlaying.remove(animations.get(0));
							return true;
					}}, Actions.delay(2), new Action() {
					@Override
					public boolean act(float delta) {
						if (!stance.isOralPenetration()) currentAnimationsPlaying.add(animations.get(0));
						currentAnimationsPlaying.remove(animations.get(1));
						return true;
					}
				}));
			}
		}
		if (enemyType == EnemyEnum.CENTAUR || enemyType == EnemyEnum.UNICORN) {
			animations.get(0).setAnimation(0, "Attack Erect", false);
			animations.get(0).addAnimation(0, "Idle Erect", true, 1.6f);
		}
	}
	
	public void hitAnimation() {
		if (animations.size > 0 && currentAnimationsPlaying.contains(animations.get(0)) && enemyType != EnemyEnum.BRIGAND && enemyType != EnemyEnum.ORC) {
			animations.get(0).setAnimation(0, "Hit Erect", false);
			animations.get(0).addAnimation(0, "Idle Erect", true, 1.0f);
			if (enemyType == EnemyEnum.HARPY) {
				animations.get(0).addAction(Actions.sequence(
					new Action() {
						@Override
						public boolean act(float delta) {
							currentAnimationsPlaying.add(animations.get(2));
							currentAnimationsPlaying.add(animations.get(3));
							return true;
					}},	
					Actions.delay(1), 
					new Action() {
						@Override
						public boolean act(float delta) {
							currentAnimationsPlaying.remove(animations.get(2));
							currentAnimationsPlaying.remove(animations.get(3));
							return true;
					}}
				));
			}
		}
	}
	
	// for init in battlefactory
	public void setLust(int lust) { 
		if (enemyType != EnemyEnum.GOLEM || lust == 100) {
			this.lust = lust; 
		}
	}

	@Override
	protected String increaseLust() {
		if (stance.isAnalReceptive()) {
			return increaseLust(2);
		}
		else if (stance.isEroticPenetration() || stance.isOralReceptive()) {
			return increaseLust(1);
		}
		return null;
	}
	
	@Override
	protected String increaseLust(int lustIncrease) {
		String spurt = "";
		setLust(lust + lustIncrease);
		if (lust > 16 && stance.isEroticReceptive()) {
			spurt = climax();
		}
		return !spurt.isEmpty() ? spurt : null;
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
		if (currentHealth <= 0 && (enemyType != EnemyEnum.ANGEL || selfRessurect == 3)) return Outcome.VICTORY;
		else if (enemy.getCurrentHealth() <= 0 && (enemyType != EnemyEnum.NAGA || stance != Stance.WRAPPED)) return Outcome.DEFEAT;
		switch(enemyType) {
			case BRIGAND:
				if (!storyMode) {
					if (getToppingClimaxCount() >= 2) return Outcome.SATISFIED;
				}
				break;
			case CENTAUR:
				if (getToppingClimaxCount() >= 1) return Outcome.SATISFIED;
				break;
			case GOBLIN:
			case GOBLIN_MALE:
				break;
			case ORC:
				if (getToppingClimaxCount() >= 5) return Outcome.SATISFIED;
				break;
			case HARPY:
				if (storyMode) {
					if (stance == Stance.FELLATIO && oldStance == Stance.AIRBORNE) return Outcome.KNOT_ORAL;
					if (stance.isAnalPenetration()) return Outcome.KNOT_ANAL;
				}
				else {
					if (getToppingClimaxCount() >= 2) return Outcome.SATISFIED;
				}
				
				break;
			case SLIME:
				break;
			case UNICORN:
				break;
			case WERESLUT:
				if (climaxCounters.get(ClimaxType.FACIAL.toString(), 0) >= 1) return Outcome.SATISFIED;
				if (knotInflate >= 5) {
					if (stance == Stance.KNOTTED) {
						return Outcome.KNOT_ANAL;
					}
					else {
						return Outcome.KNOT_ORAL;
					}
				}
				break;
			case ADVENTURER:
				if (getReceptiveClimaxCount() >= 1 ) return Outcome.SUBMISSION;
				if (getToppingClimaxCount() >= 1) return Outcome.SATISFIED;
				break;
			case OGRE:
				if (climaxCounters.get(ClimaxType.ANAL.toString(), 0) >= 1) return Outcome.SATISFIED;
				break;
			case BEASTMISTRESS:
				if (isErect()) return Outcome.SATISFIED;
				break;
			case SPIDER:
				if (knotInflate >= 5) return Outcome.KNOT_ANAL;
			case ANGEL:
				if (stance == Stance.FACE_SITTING && oldStance == Stance.FACE_SITTING) return Outcome.SUBMISSION;
				if (lust == 8 && currentHealth == getMaxHealth() && selfRessurect == 1) return Outcome.SATISFIED;
			case NAGA:
				if (enemy.getCurrentHealth() <= 0 && stance == Stance.WRAPPED) return Outcome.DEATH;
			default:
		
		}
		return null;
	}
	
	public String getOutcomeText(AbstractCharacter enemy) {
		switch (getOutcome(enemy)) {
			case KNOT_ORAL: return enemyType == EnemyEnum.WERESLUT ? "It's stuck behind your teeth. Your mouth is stuffed!" : "You've been stuffed in the face!";
			case KNOT_ANAL: return enemyType == EnemyEnum.WERESLUT ? "You've been knotted!!!\nYou are at her whims, now." : enemyType == EnemyEnum.SPIDER ? "You've been stuffed full of eggs." : "You've been stuffed in the ass!";
			case SATISFIED: return enemyType == EnemyEnum.CENTAUR ? "You've been dominated by the centaur's massive horsecock."
				: enemyType == EnemyEnum.OGRE ? "The ogre has filled your guts with ogre cum.  You are well and truly fucked."
				: enemyType == EnemyEnum.ANGEL ? "She notes your lack of aggression, and stands down."
				: properCase(pronouns.getNominative()) + " seems satisfied. " + properCase(pronouns.getNominative()) + "'s no longer hostile.";
			
			case SUBMISSION: return enemyType == EnemyEnum.ANGEL ? "She's sitting on your face and can't be dislodged - you can no longer fight!" : "They're completely fucked silly! They're no longer hostile.";
			case DEFEAT: return enemy.getDefeatMessage();
			case VICTORY: return getDefeatMessage();
			case DEATH: return enemyType == EnemyEnum.NAGA ? "You are being crushed!" : "";
		}
		return null;
	}
	@Override
	protected int getClimaxVolume() {
		super.getClimaxVolume();
		switch(enemyType) {
			case OGRE:
			case CENTAUR: 
			case UNICORN: return 21;
			case GOBLIN: return 10;
			default: return 5;
		}
	}
	
	@Override
	protected boolean canBleed() { return enemyType.canBleed(); }

	public void setClimaxCounter(int climaxCounter) {
		climaxCounters.put(ClimaxType.BACKWASH.toString(), climaxCounter);
	}

	public void toggle() {
		if (enemyType == EnemyEnum.BRIGAND) {
			currentDisplay = currentDisplay.equals("IFOS100") ? "IFOS100N" : "IFOS100";
			animations.get(1).setAnimation(0, currentDisplay, true);
		}
		else if (enemyType == EnemyEnum.CENTAUR) {
			currentFrame = 1 - currentFrame;
		}
	}

	public Array<AnimatedActor> getAnimations(AssetManager assetManager) {
		return enemyType.getAnimations(assetManager);
	}
	
	@Override
	public void setPosition(float x, float y) {
		super.setPosition(x, y);
		for (AnimatedActor animation : animations) {
			animation.setPosition(x, y);
		}
	}
} 

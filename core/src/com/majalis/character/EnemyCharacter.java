package com.majalis.character;

import static com.majalis.character.Techniques.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.ObjectMap;
import com.majalis.asset.AnimatedActor;
import com.majalis.screens.BattleScreen.Outcome;
import com.majalis.character.Arousal.ArousalLevel;
import com.majalis.character.Item.Weapon;
import com.majalis.character.SexualExperience.SexualExperienceBuilder;
import com.majalis.save.MutationResult;
import com.majalis.technique.ClimaxTechnique.ClimaxType;

/*
 * Abstract class that all enemies extend - currently concrete to represent a generic "enemy".
 */
public class EnemyCharacter extends AbstractCharacter {

	private transient ObjectMap<Stance, Array<Image>> textures;
	private transient Array<Image> defaultTextures;
	private ObjectMap <String, Integer> climaxCounters;
	private transient Group currentAnimations;
	private transient Array<AnimatedActor> animations;
	private String currentDisplay;
	private int currentFrame;
	private int selfRessurect;
	private boolean storyMode;
	private Disposition disposition;
	private RandomXS128 random;
	private int delay;
	private transient boolean init;
	@SuppressWarnings("unused") private String bgPath; // deprecated
	
	@SuppressWarnings("unused")
	private EnemyCharacter() { disposition = Disposition.HAPPY; }
	public EnemyCharacter(Array<Texture> textures, ObjectMap<Stance, Array<Texture>> textureMap, Array<AnimatedActor> animations, EnemyEnum enemyType) { this(textures, textureMap, animations, enemyType, Stance.BALANCED, false); }
	public EnemyCharacter(Array<Texture> textures, ObjectMap<Stance, Array<Texture>> textureMap, Array<AnimatedActor> animations, EnemyEnum enemyType, Stance stance, boolean storyMode) {
		super(true);
		this.enemyType = enemyType;
		this.storyMode = storyMode;
		init(textures, textureMap, animations, stance);
		climaxCounters = new ObjectMap<String, Integer>();
		currentFrame = 0;		
		random = new RandomXS128((int)(Math.random() * 10000) % 10000);
		equip(enemyType.getWeaponType() != null && enemyType.getWeaponType().isMelee() ? new Weapon(enemyType.getWeaponType()): null);
		equip(enemyType.getWeaponType() != null && !enemyType.getWeaponType().isMelee() ? new Weapon(enemyType.getWeaponType()): null);
		equip(enemyType.getArmorType() != null ? new Armor(enemyType.getArmorType()): null);
		equip(enemyType.getLegwearType() != null ? new Armor(enemyType.getLegwearType()): null);
		equip(enemyType.getUnderwearType() != null ? new Armor(enemyType.getUnderwearType()): null);
		equip(enemyType.getShieldType() != null ? new Armor(enemyType.getShieldType()): null);
		baseStrength = enemyType.getStrength();
		baseAgility = enemyType.getAgility();
		baseEndurance = enemyType.getEndurance();
		basePerception = enemyType.getPerception();
		baseMagic = enemyType.getMagic();
		baseCharisma = enemyType.getCharisma();
		baseDefense = enemyType.getDefense();
		healthTiers = enemyType.getHealthTiers();
		manaTiers = enemyType.getManaTiers();
		phallus = enemyType.getPhallusType();
		pronouns = enemyType.getPronounSet();
		arousal = enemyType.getArousal();
		perks.putAll(enemyType.getPerks());
		label = enemyType.toString();
		selfRessurect = enemyType == EnemyEnum.ANGEL ? 1 : 0;
		staminaTiers.removeIndex(staminaTiers.size - 1);
		staminaTiers.add(10);
		setHealthToMax();
		setStaminaToMax();
		setManaToMax();		
		disposition = enemyType == EnemyEnum.ANGEL ? Disposition.PEACEFUL : Disposition.HAPPY;
	}
	
	public Array<String> getImagePaths() { return enemyType.getPaths(); }
	public ObjectMap<String, Array<String>> getTextureImagePaths() { return enemyType.getImagePaths(); }	
	public Array<Texture> getTextures(AssetManager assetManager) {
		Array<Texture> textures = new Array<Texture>();
		for (String path : enemyType.getPaths()) {
			textures.add(assetManager.get(path, Texture.class));
		}
		return textures;
	}
	public AssetDescriptor<Texture> getBGPath() { return enemyType.getBGPath(); }
	
	// this should be refactored so that the enemy simply receives the assetManager and uses what it requires to reinitialize itself
	public void init(Array<Texture> defaultTextures, ObjectMap<Stance, Array<Texture>> textures, Array<AnimatedActor> animations, Stance stance) {
		this.defaultTextures = defaultTextures == null ? new Array<Image>() : initImages(defaultTextures, Stance.BALANCED);
		this.textures = initImagesBystance(textures);
		this.animations = animations;
		currentAnimations = new Group();
		this.addActor(currentAnimations);
		if (animations.size > 0 && (enemyType != EnemyEnum.HARPY || stance != Stance.FELLATIO) && (enemyType != EnemyEnum.BRIGAND || (stance != Stance.DOGGY && stance != Stance.STANDING))) {
			currentAnimations.addActor(animations.first());
			if (enemyType == EnemyEnum.GHOST && !Gdx.app.getPreferences("tales-of-androgyny-preferences").getBoolean("blood", true)) {
				animations.first().setSkeletonSkin("RealityClean");
			}
		}
		
		currentDisplay = enemyType == EnemyEnum.BRIGAND ? "IFOS100N" :"Idle Erect";
		setStance(stance);
	}
	
	private ObjectMap<Stance, Array<Image>> initImagesBystance(ObjectMap<Stance, Array<Texture>> textures) {
		ObjectMap<Stance, Array<Image>> imagesByStance = new ObjectMap<Stance, Array<Image>>();
		for (ObjectMap.Entry<Stance, Array<Texture>> entry : textures) {
			imagesByStance.put(entry.key, initImages(entry.value, entry.key));
		}
		return imagesByStance;
	}
	
	private Array<Image> initImages(Array<Texture> textures, Stance stance) {
		Array<Image> images = new Array<Image>();
		for (Texture texture : textures) { initImage(texture, images, stance); }
		return images;
	}
	
	private void initImage(Texture texture, Array<Image> images, Stance stance) {
		Image newImage = new Image(texture);
		
		int x = 600;
		int y = 20;
		float width =  (int) (texture.getWidth() / (texture.getHeight() / 975.));
		float height = 975;
		
		if (
			(enemyType == EnemyEnum.GIANTESS) ||
			(enemyType == EnemyEnum.WERESLUT && (stance == Stance.DOGGY || stance == Stance.KNOTTED)) ||
			(enemyType == EnemyEnum.ADVENTURER && stance == Stance.COWGIRL_BOTTOM) ||
			(enemyType == EnemyEnum.HARPY && (stance == Stance.FELLATIO || stance == Stance.DOGGY)) ||
			enemyType == EnemyEnum.BRIGAND ||
			enemyType == EnemyEnum.CENTAUR || 
			((enemyType == EnemyEnum.GOBLIN || enemyType == EnemyEnum.GOBLIN_MALE) && (stance == Stance.FACE_SITTING || stance == Stance.SIXTY_NINE || stance == Stance.DOGGY || stance == Stance.PRONE_BONE))) {
			x = ((enemyType == EnemyEnum.BRIGAND && stance != Stance.ANAL) || (enemyType == EnemyEnum.GOBLIN || enemyType == EnemyEnum.GOBLIN_MALE) && (stance == Stance.FACE_SITTING || stance == Stance.SIXTY_NINE)) ? 400 : 0;
			y = 0;
			width = (int) (texture.getWidth() / (texture.getHeight() / 1080.));
			height = 1080;
		}
		
		if (enemyType == EnemyEnum.OGRE) { y = 0; }
		
		newImage.setBounds(x + getX(),  y + getY(), width, height);
		images.add(newImage);
	}
	
	private boolean getInit() {
		boolean temp = !init;
		init = true;
		return temp;
	}
	
	@Override
	protected void updateDisplay() {
		this.clearChildren();
		this.addActor(currentAnimations);
		
		boolean init = getInit();
		Action rangeAction = Actions.parallel(Actions.moveTo(getRangeX(), getRangeY(), init ? 0 : 1), Actions.scaleTo(getRangeScale(), getRangeScale(), init ? 0 : 1));
		
		if (enemyType == EnemyEnum.BRIGAND) {
			if (stance == Stance.DOGGY || stance == Stance.STANDING) {
				currentAnimations.addActor(animations.get(1));
				currentAnimations.removeActor(animations.get(0));
			}
			else {
				currentAnimations.addActor(animations.get(0));
				currentAnimations.removeActor(animations.get(1));
			}
		}
		else if (enemyType == EnemyEnum.ORC) {
			if (stance == Stance.DOGGY || stance == Stance.PRONE_BONE) {
				currentAnimations.addActor(animations.get(1));
				currentAnimations.removeActor(animations.get(0));
			}
			else {
				currentAnimations.addActor(animations.get(0));
				currentAnimations.removeActor(animations.get(1));
			}
		}
		if (enemyType == EnemyEnum.HARPY && !stance.isOralPenetration()) currentAnimations.addActor(animations.get(0));
		if (currentAnimations.getChildren().size == 0 || 
				((enemyType == EnemyEnum.GOBLIN || enemyType == EnemyEnum.GOBLIN_MALE) && (stance == Stance.FACE_SITTING || stance == Stance.SIXTY_NINE || stance == Stance.PRONE_BONE || stance == Stance.DOGGY || stance == Stance.PRONE_BONE || stance == Stance.SIXTY_NINE)) ||
				(enemyType == EnemyEnum.CENTAUR && (stance == Stance.DOGGY || stance == Stance.FELLATIO)) ||
				(enemyType == EnemyEnum.WERESLUT && (stance == Stance.DOGGY || stance == Stance.KNOTTED)) ||
				(enemyType == EnemyEnum.ADVENTURER && (stance == Stance.COWGIRL)) ||
				(enemyType == EnemyEnum.HARPY && stance == Stance.DOGGY) ||
				(enemyType == EnemyEnum.BRIGAND && (stance == Stance.FELLATIO || stance == Stance.FACEFUCK || stance == Stance.ANAL))) {
			Array<Image> textureCandidates = textures.get(stance, defaultTextures);
			if (textureCandidates == null) return;
			Image texture;

			if (currentFrame >= textureCandidates.size) {
				if (textureCandidates.size > 0) texture = textureCandidates.get(0);
				else return;
			}
			else { texture = textureCandidates.get(currentFrame); }

			if (texture == null) return;
			
			this.addActor(texture);
			if (!defaultTextures.contains(texture, true)) { rangeAction = Actions.parallel( Actions.moveTo(0, 0), Actions.scaleTo(1, 1)); } 
		}
		for (int ii = 1; ii < animations.size; ii++) {
			if (currentAnimations.getChildren().contains(animations.get(ii), true)) { rangeAction = Actions.parallel(Actions.moveTo(0, 0), Actions.scaleTo(1, 1)); } 
		}

		this.clearActions();
		this.addAction(rangeAction);
	}
	
	private float getRangeScale() { return range <= 1 ? 1 : (enemyType == EnemyEnum.OGRE ? .75f : .5f); }
	private float getRangeX() { return range <= 1 ? 0 : (enemyType == EnemyEnum.OGRE ? 225 : 350); }
	private float getRangeY() { return range <= 1 ? 0 : (enemyType == EnemyEnum.OGRE ? 0 : 100); }
	
	@Override
	public void setRange(int range) { super.setRange(range); init = false; updateDisplay(); }	
	public void setDelay(int delay) { this.delay = delay; }
	
	@Override
	public void setStance(Stance stance) { 
		super.setStance(stance);	
		updateDisplay();		
	}
	
	public void attackAnimation() {
		final Boolean fellatio = stance.isOralPenetration();
		if (animations.size > 1) {
			if (enemyType == EnemyEnum.HARPY) {
				final String preAttack = fellatio ? "Air BJ" : "Attack Erect";
				final String attack = fellatio ? "bj" : "attack";
				currentAnimations.addAction(Actions.sequence(
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
							currentAnimations.addActor(animations.get(1));
							currentAnimations.removeActor(animations.get(0));
							return true;
					}}, Actions.delay(2), new Action() {
					@Override
					public boolean act(float delta) {
						currentAnimations.removeActor(animations.get(1));
						if (!stance.isOralPenetration()) currentAnimations.addActor(animations.get(0));
						else { setStance(stance); }
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
		if (animations.size > 0 && currentAnimations.getChildren().contains(animations.get(0), true) && enemyType.hasHitAnimation()) {
			animations.get(0).setAnimation(0, "Hit Erect", false);
			animations.get(0).addAnimation(0, "Idle Erect", true, 1.0f);
			if (enemyType == EnemyEnum.HARPY) {
				animations.get(0).addAction(Actions.sequence(
					new Action() {
						@Override
						public boolean act(float delta) {
							currentAnimations.addActor(animations.get(2));
							currentAnimations.addActor(animations.get(3));
							return true;
					}},	
					Actions.delay(1), 
					new Action() {
						@Override
						public boolean act(float delta) {
							currentAnimations.removeActor(animations.get(2));
							currentAnimations.removeActor(animations.get(3));
							return true;
					}}
				));
			}
		}
	}
	
	// for init in battlefactory
	public void setArousal(ArousalLevel newArousalLevel) { arousal.setArousalLevel(newArousalLevel); }
	
	@Override
	public String getDefeatMessage() {
		switch (enemyType) {
			case BRIGAND: return "You defeated the Brigand!\nShe kneels to the ground, unable to lift her weapon.";
			case CENTAUR: return "You defeated the Centaur!\nShe smiles, haggardly, acknowledging your strength, and bows slightly.";
			case GOBLIN: return "The Goblin is knocked to the ground, defeated!";
			case HARPY: return "The Harpy falls out of the sky, crashing to the ground!\nShe is defeated!";
			case SLIME: return "The Slime becomes unable to hold her form!\nShe is defeated!";
			case UNICORN:
			case WERESLUT:
			default:
				return super.getDefeatMessage();		
		}
	}
	
	public Outcome getOutcome(AbstractCharacter enemy) {
		if (getCurrentHealth() <= 0 && (enemyType != EnemyEnum.ANGEL || selfRessurect == 3)) return Outcome.VICTORY;
		else if (enemy.getCurrentHealth() <= 0 && (enemyType != EnemyEnum.NAGA || stance != Stance.WRAPPED)) return Outcome.DEFEAT;
		switch(enemyType) {
			case BRIGAND:
				if (!storyMode) {
					if (getToppingClimaxCount() >= 2) {
						if (climaxCounters.get(ClimaxType.ANAL.toString(), 0) >= 1) return Outcome.SATISFIED_ANAL;
						else return Outcome.SATISFIED_ORAL;
					}
					if (getReceptiveClimaxCount() >= 1 ) return Outcome.SUBMISSION;
				}
				break;
			case CENTAUR:
				if (getToppingClimaxCount() >= 1) return Outcome.SATISFIED_ANAL;
				break;
			case GOBLIN:
			case GOBLIN_MALE:
				if (!storyMode) {
					if (enemy.getCumInflation() > 1 || climaxCounters.get(ClimaxType.ANAL.toString(), 0) >= 5) {
						if (climaxCounters.get(ClimaxType.ANAL.toString(), 0) >= 1) return Outcome.SATISFIED_ANAL;
						return Outcome.SATISFIED_ORAL;
					}
				}
				break;
			case ORC:
				if (getToppingClimaxCount() >= 5) return Outcome.SATISFIED_ANAL;
				if (storyMode && getReceptiveClimaxCount() >= 1) return Outcome.SUBMISSION; 
				break;
			case HARPY:
				if (storyMode) {
					if (stance == Stance.FELLATIO && oldStance == Stance.AIRBORNE) return Outcome.KNOT_ORAL;
					if (stance.isAnalPenetration()) return Outcome.KNOT_ANAL;
				}
				else {
					if (getToppingClimaxCount() >= 2) {
						if (climaxCounters.get(ClimaxType.ANAL.toString(), 0) >= 1) return Outcome.SATISFIED_ANAL;
						else return Outcome.SATISFIED_ORAL;
					}
				}
				break;
			case SLIME:
				break;
			case UNICORN:
				break;
			case WERESLUT:
				if (!storyMode) {
					if (climaxCounters.get(ClimaxType.FACIAL.toString(), 0) >= 1) return Outcome.SATISFIED_ANAL;
					if (getReceptiveClimaxCount() >= 1 ) return Outcome.SUBMISSION;
					if (knotInflate >= 5) {
						if (stance == Stance.KNOTTED) {
							return Outcome.KNOT_ANAL;
						}
						else if (stance == Stance.MOUTH_KNOTTED) {
							return Outcome.KNOT_ORAL;
						}
					}
				}
				else if (knotInflate >= 5 && stance == Stance.MOUTH_KNOTTED) {
					if (stance == Stance.MOUTH_KNOTTED) {
						return Outcome.KNOT_ORAL;
					}
				}
				break;
			case ADVENTURER:
				if (getReceptiveClimaxCount() >= 1 ) return Outcome.SUBMISSION;
				if (getToppingClimaxCount() >= 1) return Outcome.SATISFIED_ANAL;
				break;
			case OGRE:
				if (climaxCounters.get(ClimaxType.ANAL.toString(), 0) >= 1) return Outcome.SATISFIED_ANAL;
				break;
			case BEASTMISTRESS:
				if (isErect()) return Outcome.SATISFIED_ANAL;
				break;
			case SPIDER:
				if (knotInflate >= 5) return Outcome.KNOT_ANAL;
				break;
			case ANGEL:
				if (stance == Stance.FACE_SITTING && oldStance == Stance.FACE_SITTING) return Outcome.SUBMISSION;
				if (arousal.isErect() && getCurrentHealth() == getMaxHealth() && selfRessurect == 1) return Outcome.SATISFIED_ANAL;
				break;
			case NAGA:
				if (enemy.getCurrentHealth() <= 0 && stance == Stance.WRAPPED) return Outcome.DEATH;
				break;
			case QUETZAL:
				if (enemy.getStance() == Stance.SPREAD) return Outcome.KNOT_ANAL;
				break;
			case MERMAID:
				if (stance == Stance.COWGIRL_BOTTOM || stance == Stance.REVERSE_COWGIRL_BOTTOM) return Outcome.SUBMISSION;
				break;
			default:
		}
		return null;
	}
	
	public String getOutcomeText(AbstractCharacter enemy) {
		switch (getOutcome(enemy)) {
			case KNOT_ORAL: return enemyType == EnemyEnum.WERESLUT ? "It's stuck behind your teeth. Your mouth is stuffed!" : "You've been stuffed in the face!";
			case KNOT_ANAL: return enemyType == EnemyEnum.WERESLUT ? "You've been knotted!!!\nYou are at her whims, now." : enemyType == EnemyEnum.SPIDER ? "You've been stuffed full of eggs." : enemyType == EnemyEnum.QUETZAL ? "Your stomach balloons painfully with the goddess' semen!" : "You've been stuffed in the ass!";
			case SATISFIED_ORAL:
			case SATISFIED_ANAL: return enemyType == EnemyEnum.CENTAUR ? "You've been dominated by the centaur's massive horsecock."
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
	public int getMagicResistance() { return enemyType == EnemyEnum.ANGEL || enemyType == EnemyEnum.NAGA || enemyType == EnemyEnum.QUETZAL ? 1 : 0; }
	
	// rather than override doAttack, doAttack should call an abstract processAttack method in AbstractCharacter and this functionality should be built there, instead of calling return super.doAttack
	@Override
	public Attack doAttack(Attack resolvedAttack) {
		// if golem uses a certain attack, it should activate her dong
		if (enemyType == EnemyEnum.GOLEM) {
			if (resolvedAttack.getSelfEffect() != null && resolvedAttack.getSelfEffect().type == StatusType.ACTIVATE) {
				currentFrame = 1;
				arousal.setArousalLevel(ArousalLevel.EDGING);
				baseStrength += 4;
			}
		}
		
		if (enemyType == EnemyEnum.HARPY) {
			if (stance.isOralPenetration()) {
				if (currentFrame == 3) currentFrame = 2; else currentFrame++;
				if (oldStance.isOralPenetration() && !stance.isOralPenetration()) currentFrame = 0;
				// play the dive bomb animation for now
				if (resolvedAttack.getForceStance() == Stance.FELLATIO_BOTTOM && !oldStance.isOralPenetration()) attackAnimation();
			}
		}
				
		if (resolvedAttack.getGrapple() != GrappleStatus.NULL && (stance == Stance.COWGIRL || stance == Stance.REVERSE_COWGIRL)) {
			// this will need to be aware of who the character receiving is - the check might need to be COWGIRL_BOTTOM instead of COWGIRL
			if (resolvedAttack.getGrapple().isDisadvantage()) {
				resolvedAttack.addMessageToAttacker(new MutationResult("It's still stuck up inside of you!"));
				resolvedAttack.addMessageToAttacker(new MutationResult("Well, I guess you know that."));
				resolvedAttack.addMessageToAttacker(new MutationResult("Kind of a colon crusher."));
			}
			else if (resolvedAttack.getGrapple() == GrappleStatus.SCRAMBLE) {
				resolvedAttack.addMessageToAttacker(new MutationResult("They're difficult to ride on top of!"));
			}
			else if (resolvedAttack.getGrapple().isAdvantage()) {
				resolvedAttack.addMessageToAttacker(new MutationResult("They're about to buck you off!"));
			}
		}
	
		if (resolvedAttack.isSuccessful()) {
			if (resolvedAttack.getForceStance() == Stance.FULL_NELSON && enemyType == EnemyEnum.BRIGAND) {
				resolvedAttack.addDialog("\"Got ya!\" she says, as she manhandles you from behind.");
			}
			
			if (oldStance != Stance.DOGGY_KYLIRA && stance == Stance.DOGGY_KYLIRA) {
				resolvedAttack.addDialog("\"Huh?! What are you... ahn! Nngh!\"");
			}
			
			if (oldStance != Stance.DOGGY_TRUDY && stance == Stance.DOGGY_TRUDY) {
				resolvedAttack.addDialog("\"Wait, what's tha... hnnngh!\"");
			}
			if (oldStance == Stance.DOGGY_TRUDY && stance == Stance.DOGGY_TRUDY) {
				resolvedAttack.addDialog("\"Don't just stand there!  get her off!\"");
			}
			
			if (oldStance != Stance.OFFENSIVE && stance == Stance.OFFENSIVE) {
				switch (enemyType) {
					case BRIGAND:
						resolvedAttack.addDialog("\"Oorah!\"");
						break;
					case WERESLUT:
						resolvedAttack.addDialog("\"Raaawr!\"");
						break;
					case ORC:
						resolvedAttack.addDialog("\"Raaaargh!\"");
						break;
					default:
				}
			}
			if (stance == Stance.HOLDING && enemyType == EnemyEnum.OGRE) {
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
			
			if (enemyType == EnemyEnum.QUETZAL) {
				if (stance == Stance.HOLDING && oldStance != Stance.HOLDING) {
					resolvedAttack.addDialog("\"Yeah... hmph.  Time to mate.\"");
				}
				else if (stance == Stance.HOLDING) {
					switch (heartbeat % 5) {
						case 0: resolvedAttack.addDialog("\"Hm... you'll do nicely,\" she says, turning you over."); break;
						case 1: resolvedAttack.addDialog("\"Very nice... your goddess approves.\""); break;
						case 2: resolvedAttack.addDialog("\"Not much of a hero, after all.\""); break;
						case 3: resolvedAttack.addDialog("\"Your time is up.\""); break;
						case 4: resolvedAttack.addDialog("\"Now... it's my turn.\""); break;
					}
				}
				if (stance == Stance.CRUSHING && oldStance != Stance.CRUSHING) {
					resolvedAttack.addDialog("\"Hooah! That's a tight fit!\"");
				}
				else if (stance == Stance.CRUSHING && heartbeat % 3 == 0) {
					resolvedAttack.addDialog("\"Where'd that fighting spirit go?! Don't worry - I'll find it.\"");
				}
				if (heartbeat == 5) {
					resolvedAttack.addDialog("\"Hmm... hehehe...\"");
				}
				if (heartbeat == 9) {
					resolvedAttack.addDialog("\"Mmhm, gonna blast your ass once you're done playing...\"");
				}
				if (heartbeat == 14) {
					resolvedAttack.addDialog("\"I'm going to pump it all into your shithole, hero...\"");
				}
			}
			
			if (!oldStance.isErotic() && stance.isErotic()) {
				switch (enemyType) {
					case BRIGAND:
						if (stance.isAnalPenetration()) {
							if (stance == Stance.STANDING) {
								resolvedAttack.addDialog("\"Ah yeah, there it is! Get cornholed, slut!\"");
							}
							else {
								resolvedAttack.addDialog("\"Oooooryah!\"");
							}
						}
						else if (stance.isOralPenetration()) {
							resolvedAttack.addDialog("\"Yeah, that's right, suck it!\"");
						} 
						else if (stance.isAnalReceptive()) {
							resolvedAttack.addDialog("\"Oof! That's my shithole, ya git!\"");
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
						else if (stance.isAnalReceptive()) {
							resolvedAttack.addDialog("\"Hey, not up MY butt!  Nyahaha!\"");
						}
						break;
					case HARPY:
						if (stance.isAnalReceptive()) {
							resolvedAttack.addDialog("The harpy squawks frantically!");
						}
						break;
					case SLIME:
						break;
					case UNICORN:
						break;
					case WERESLUT:
						if (stance.isAnalPenetration()) {
							resolvedAttack.addDialog("\"Woof, mating! Mating! Take my puppies!\"");
						}	
						if (stance.isAnalReceptive()) {
							resolvedAttack.addDialog("\"Nnngh...!\"");
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
						else if (stance.isAnalReceptive()) {
							resolvedAttack.addDialog("\"Whoahoho! Come on, fuck me, pinkskin!\"");
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
						resolvedAttack.addDialog("\"You were saying?\"");
						break;
					case QUETZAL:
						break;
					case MERMAID:
						break;
					case WARLOCK:
						break;
					case GIANTESS:
						break;
					case DULLAHAN:
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
						resolvedAttack.addDialog("\"Explode for me, won't you?\"");
						break;
					case QUETZAL:
						switch (heartbeat % 5) {
							case 0: resolvedAttack.addDialog("\"Accept your goddess' seed!\""); break;
							case 1: resolvedAttack.addDialog("She silently fills you with her deific semen. You worry you may explode."); break;
							case 2: resolvedAttack.addDialog("You are filled to bursting with monster sperm."); break;
							case 3: resolvedAttack.addDialog("Every inch of your intestines is stuffed and bloated."); break;
							case 4: resolvedAttack.addDialog("\"Mmmm...\" she moans, patting your full stomach."); break;
						}
						break;
					case MERMAID:
						break;
					case WARLOCK:
						break;
					case GIANTESS:
						break;
					case DULLAHAN:
						break;
				}
					
				climaxCounters.put(resolvedAttack.getClimaxType().toString(), climaxCounters.get(resolvedAttack.getClimaxType().toString(), 0) + 1);
				arousal.climax(resolvedAttack.getClimaxType(), perks);
				
				if ((enemyType == EnemyEnum.GOBLIN || enemyType == EnemyEnum.GOBLIN_MALE) && getClimaxCount() % 5 == 0) {
					arousal.setArousalLevel(ArousalLevel.SEMI_ERECT);
				}
			}
		}
		return super.doAttack(resolvedAttack);
	}
	
	@Override
	public AttackResult receiveAttack(Attack resolvedAttack) {
		AttackResult result = super.receiveAttack(resolvedAttack);
		if (resolvedAttack.getDamage() > 0 && disposition == Disposition.PEACEFUL) { disposition = Disposition.ANGRY; }
		if (arousal.isErect() && disposition == Disposition.HAPPY)  { disposition = Disposition.HORNY; }
		else if (disposition == Disposition.HORNY) { disposition = Disposition.HAPPY; }
		return result;
	}
	
	public void setClimaxCounter(int climaxCounter) { climaxCounters.put(ClimaxType.BACKWASH.toString(), climaxCounter); }

	public void toggle() {
		if (enemyType == EnemyEnum.BRIGAND) {
			currentDisplay = currentDisplay.equals("IFOS100") ? "IFOS100N" : "IFOS100";
			animations.get(1).setAnimation(0, currentDisplay, true);
		}
		else if (enemyType == EnemyEnum.CENTAUR) {
			currentFrame = 1 - currentFrame;
		}
		updateDisplay();	
	}
	
	public boolean canToggle() { return (enemyType == EnemyEnum.BRIGAND && currentAnimations.getChildren().contains(animations.get(1), true)) || (enemyType == EnemyEnum.CENTAUR && stance == Stance.DOGGY);  }

	public Array<AnimatedActor> getAnimations(AssetManager assetManager) { return enemyType.getAnimations(assetManager); }
	
	@Override
	public void setPosition(float x, float y) {
		super.setPosition(x, y);
		for (AnimatedActor animation : animations) {
			animation.setPosition(x, y);
		}
	}
	
	@Override
	public void setPosition(float x, float y, int align) {
		super.setPosition(x, y, align);
		for (AnimatedActor animation : animations) {
			animation.setPosition(x, y);
		}
	}
	
	public Technique getTechnique(AbstractCharacter target) {		
		if (!arousal.isErect() || enemyType == EnemyEnum.OGRE) {
			if (enemyType != EnemyEnum.CENTAUR && enemyType != EnemyEnum.BEASTMISTRESS && enemyType != EnemyEnum.GOLEM && enemyType != EnemyEnum.QUETZAL) increaseLust(new SexualExperienceBuilder().setAssTeasing(1).build());
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
				if (enemyType == EnemyEnum.WERESLUT) {
					possibleTechniques = getTechniques(MATING);
				}
				else {
					possibleTechniques = getTechniques(POUNCE_DOGGY);
				}
			}
			else if (target.stance == Stance.SUPINE) {
				possibleTechniques = new Array<Techniques>();
				if (enemyType.canWrestle()) {
					possibleTechniques.addAll(getTechniques(WRESTLE_TO_GROUND_UP));
				}
				if (enemyType == EnemyEnum.GOBLIN || enemyType == EnemyEnum.GOBLIN_MALE || enemyType == EnemyEnum.HARPY) {
					possibleTechniques.addAll(getTechniques(MOUNT_FACE));
				}
				if (willFaceSit(target)) {
					possibleTechniques.addAll(getTechniques(FACE_SIT));
				}	
			}
			else if (target.stance == Stance.KNEELING && enemyType != EnemyEnum.HARPY) {
				possibleTechniques = getTechniques(SAY_AHH);
			}
			else if (target.stance == Stance.AIRBORNE && enemyType == EnemyEnum.ORC) {
				possibleTechniques = getTechniques(OUROBOROS);
			}
			else if (enemyType == EnemyEnum.HARPY && stance != Stance.AIRBORNE) {
				possibleTechniques.add(FLY);
			}
			else if (target.stance.receivesMediumAttacks() && enemyType == EnemyEnum.BRIGAND || (enemyType == EnemyEnum.SPIDER && !target.fullOfEggs()) || (enemyType == EnemyEnum.ORC && getWeapon() == null) && getCurrentStamina() > 10) {
				possibleTechniques.add(FULL_NELSON);
			}
			else if (target.kyliraAvailable() && enemyType.willPounceKylira()) {
				possibleTechniques.add(CORNHOLE_KYLIRA);
			}
			else if (target.trudyAvailable() && enemyType.willPounceTrudy()) {
				possibleTechniques.add(CORNHOLE_TRUDY);
			}
		}
		
		Array<Techniques> toRemove = new Array<Techniques>();
		for (Techniques technique : possibleTechniques) {
			// this should be seperate checks for plugged and bottom covered - bottom covered should pull down the outermost pants item if possible - otherwise need to think through the unremoveable cover case
			if (enemyType.isCorporeal() && technique.getTrait().getResultingStance().isAnalPenetration() && (target.isPlugged() || (target.getLegwearScore() > 0 && target.getLegwear().coversAnus()) || (target.getUnderwearScore() > 0 && target.getUnderwear().coversAnus()))) {
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

		if (candidates.size == 0) {
			Technique candidate = getTechnique(target, Techniques.DO_NOTHING);
			techniqueToToken.put(candidate, Techniques.DO_NOTHING);
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
		if (delay > 0) {
			technique = getTechnique(target, Techniques.DO_NOTHING);
			delay--;
		}
		return technique;	
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
	
	@Override
	protected Array<Techniques> getTechniques(AbstractCharacter target, Techniques ... candidates) { 
		Array<Techniques> trueCandidates = super.getTechniques(target, candidates);
		Array<Techniques> techniques = new Array<Techniques>(trueCandidates);		
		for (Techniques candidate : trueCandidates) {
			if (candidate == FACE_SIT && !willFaceSit(target)) { techniques.removeValue(candidate, true); }
			else if (candidate != DIVEBOMB && ((candidate.getTrait().getTechniqueHeight().isHigh() && !target.getStance().receivesHighAttacks()) || // this should not exclude divebomb - the harpy should just land or do something else if the player is on the ground already, possibly face-sitting or a steeper divebomb
					(candidate.getTrait().getTechniqueHeight().isMedium() && !target.getStance().receivesMediumAttacks()) || 
					(candidate.getTrait().getTechniqueHeight().isLow() && !target.getStance().receivesLowAttacks()))) { techniques.removeValue(candidate, true); }
			else if (candidate == ARMOR_SUNDER && !enemyType.willArmorSunder()) { techniques.removeValue(candidate, true); }
			else if (candidate == BLITZ_ATTACK && (enemyType != EnemyEnum.BEASTMISTRESS && !isEnragedGolem())) { techniques.removeValue(candidate, true); }			
			else if (inTechniques(candidate, BLOCK, CAUTIOUS_ATTACK, GUT_CHECK) && enemyType == EnemyEnum.BEASTMISTRESS) { techniques.removeValue(candidate, true); }
			else if (inTechniques(candidate, BLOCK, CAUTIOUS_ATTACK) && !enemyType.usesDefensiveTechniques()) { techniques.removeValue(candidate, true); }
			else if (candidate == PARRY && !enemyType.willParry()) { techniques.removeValue(candidate, true); }
			else if (candidate == TAUNT && !enemyType.willSeduce()) { techniques.removeValue(candidate, true); }
			else if (inTechniques(candidate, SLAP_ASS, GESTURE, PUCKER_LIPS, RUB, REVERSAL_ATTACK, BLOCK) && (arousal.isBottomReady() && stance == Stance.SEDUCTION)) { techniques.removeValue(candidate, true); }			
			else if (candidate != OVIPOSITION && techniques.contains(OVIPOSITION, true) && enemyType == EnemyEnum.SPIDER && grappleStatus == GrappleStatus.HOLD) { techniques.removeValue(candidate, true); }
			else if (candidate == OVIPOSITION && (enemyType != EnemyEnum.SPIDER || grappleStatus != GrappleStatus.HOLD)) { techniques.removeValue(candidate, true); }
			else if (candidate != RECEIVE_DOGGY && techniques.contains(RECEIVE_DOGGY, true))  { techniques.removeValue(candidate, true); }
			else if (candidate != RECEIVE_PRONE_BONE && techniques.contains(RECEIVE_PRONE_BONE, true))  { techniques.removeValue(candidate, true); }
			else if (candidate != RECEIVE_ANAL && techniques.contains(RECEIVE_ANAL, true))  { techniques.removeValue(candidate, true); }
			else if (candidate != SUCK_IT && techniques.contains(SUCK_IT, true))  { techniques.removeValue(candidate, true); }
			else if (candidate != GET_FACEFUCKED && techniques.contains(GET_FACEFUCKED, true))  { techniques.removeValue(candidate, true); }
			else if (candidate != RECEIVE_OUROBOROS && techniques.contains(RECEIVE_OUROBOROS, true))  { techniques.removeValue(candidate, true); }
			else if (candidate == CHOKE && (enemyType != EnemyEnum.ORC && enemyType != EnemyEnum.BRIGAND)) { techniques.removeValue(candidate, true); }		
			else if (candidate == FLIP_PRONE && !enemyType.prefersProneBone()) { techniques.removeValue(candidate, true); }		
			else if (candidate == FLIP_SUPINE && !enemyType.prefersMissionary()) { techniques.removeValue(candidate, true); }		
			else if (candidate == PIN && target.getStrength() + 3 >= getStrength()) { techniques.removeValue(candidate, true); }		
			else if (candidate != KNOT && techniques.contains(KNOT, true) && enemyType == EnemyEnum.WERESLUT && arousal.isEdging()) { techniques.removeValue(candidate, true); }		
			else if (candidate == KNOT && (enemyType != EnemyEnum.WERESLUT || !arousal.isEdging())) { techniques.removeValue(candidate, true); }	
			else if (candidate == ERUPT_ANAL && (enemyType == EnemyEnum.BRIGAND || enemyType == EnemyEnum.GOBLIN || enemyType == EnemyEnum.ORC)) { techniques.removeValue(candidate, true); }		
			else if (candidate == BLOW_LOAD && !(enemyType == EnemyEnum.BRIGAND || enemyType == EnemyEnum.GOBLIN || enemyType == EnemyEnum.ORC)) { techniques.removeValue(candidate, true); }		
			else if (candidate == PROSTATE_GRIND && !enemyType.willProstatePound()) { techniques.removeValue(candidate, true); }		
			else if (candidate == INCANTATION && (!isSpellCaster() || (!canIncant()))) { techniques.removeValue(candidate, true); } // remove incantation if not a spellcaster or can't currently incant
			else if (candidate != INCANTATION && techniques.contains(INCANTATION, true) && mustIncant()) { techniques.removeValue(candidate, true); } // remove all other options if must incant
			else if (candidate != ACTIVATE && techniques.contains(ACTIVATE, true) && (enemyType == EnemyEnum.GOLEM && !isEnragedGolem()))  { techniques.removeValue(candidate, true); }
			else if (candidate == ACTIVATE && enemyType != EnemyEnum.GOLEM) { techniques.removeValue(candidate, true); }
			else if (candidate != COMBAT_FIRE && techniques.contains(COMBAT_FIRE, true) && isEnragedGolem()) { techniques.removeValue(candidate, true); }
			else if (candidate != COMBAT_HEAL && techniques.contains(COMBAT_HEAL, true) && mustCastHealing()) { techniques.removeValue(candidate, true); }
			else if (candidate != TITAN_STRENGTH && techniques.contains(TITAN_STRENGTH, true) && mustCastTitanStrength() && !mustCastHealing()) { techniques.removeValue(candidate, true); }				
			else if (!inSpellbook(candidate)) { techniques.removeValue(candidate, true); }	
			else if (candidate == SUDDEN_ADVANCE && !enemyType.isOffensive()) { techniques.removeValue(candidate, true); }	
			else if (candidate == MOUTH_KNOT && !storyMode) { techniques.removeValue(candidate, true); }
			else if (inTechniques(candidate, SUBMIT, GET_FACE_RIDDEN, SUCK_IT, REST) && winsGrapples()) { techniques.removeValue(candidate, true); }
			else if (inTechniques(candidate, MOUNT_FACE, FACE_SIT, POUNCE_DOGGY, WRESTLE_TO_GROUND, WRESTLE_TO_GROUND_UP, CENTER, REEL_BACK, BERSERK, STONEWALL, VAULT, FEINT_AND_STRIKE, SLIDE, DUCK, HIT_THE_DECK, KICK_OVER_FACE_UP, KICK_OVER_FACE_DOWN, SIT_ON_IT, TURN_AND_SIT, DUCK, UPPERCUT, GRAB_IT, JUMP_ATTACK, VAULT_OVER, STAND_OFF_IT, FULL_NELSON, TAKEDOWN, PULL_OUT, PULL_OUT_ORAL, PULL_OUT_ANAL, PULL_OUT_STANDING, RELEASE_PRONE, RELEASE_SUPINE, SAY_AHH)) { techniques.removeValue(candidate, true); }	
		}
				
		return techniques;
	}
	
	public boolean inSpellbook(Techniques technique) {
		return !(enemyType == EnemyEnum.WARLOCK && stance == Stance.CASTING) || inTechniques(technique, COMBAT_FIRE, COMBAT_HEAL, HYPNOSIS, PARALYZE, WEAKENING_CURSE, TITAN_STRENGTH);
	}
	
	@Override
	protected String getLeakMessage() {
		String message = "";
		
		if (ass.getFullnessAmount() >= 20) {
			message = "Their belly looks pregnant, full of baby batter! It drools out of their well-used hole! Their movements are sluggish! -2 Agility.";
		}
		else if (ass.getFullnessAmount() >= 10) {
			message = "Their gut is stuffed with semen!  It drools out!  They're too queasy to move quickly! -1 Agility.";
		}
		else if (ass.getFullnessAmount() >= 5) {
			message = "Cum runs out of their full ass!";
		}
		else if (ass.getFullnessAmount() > 1) {
			message = "They drool cum from their hole!";
		}
		else if (ass.getFullnessAmount() == 1) {
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
		arousal.climax(stance.getClimaxType(), perks);
		modStamina(-(enemyType == EnemyEnum.GOBLIN || enemyType == EnemyEnum.GOBLIN_MALE ? 2 : enemyType == EnemyEnum.GOLEM || enemyType == EnemyEnum.QUETZAL ? 0 : 10));
		switch (oldStance) {
			case ANAL:
				results.add("The " + getLabel() + "'s lovemaking reaches a climax!");
				results.add("They don't pull out! It twitches and throbs in your rectum!");
				results.add("They cum up your ass! Your stomach receives it!");		
				break;
			case COWGIRL:
				results.add("The " + getLabel() + " blasts off in your intestines while you bounce\non their cumming cock! You got butt-bombed!");
				break;
			case FACEFUCK:
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
			case FACEFUCK_BOTTOM:
			case PRONE_BONE_BOTTOM:
			case REVERSE_COWGIRL_BOTTOM:
			case SIXTY_NINE_BOTTOM:
				results.add("The " + getLabel() + " ejaculates!");
				ClimaxType climaxType = stance.isAnalReceptive() ? ClimaxType.ANAL_RECEPTIVE : ClimaxType.ORAL_RECEPTIVE;
				climaxCounters.put(climaxType.toString(), climaxCounters.get(climaxType.toString(), 0) + 1);
				break;
			case STANDING:
			case DOGGY:
			case PRONE_BONE:
				results.add("The " + getLabel() + " spews hot, thick semen into your bowels!");
				results.add("You are anally inseminated!");
				results.add("You're going to be farting cum for days!");
				break;
			default:
				break;
		}
	
		if (stance.isEroticPenetration() && enemyType != EnemyEnum.QUETZAL) {
			setStance(Stance.ERUPT);
		}
		String joined = "";
		for (String foo : (String[]) results.toArray(String.class)) {
			joined += foo + "\n";
		}
		joined = joined.trim();
		return joined;
	}
	
	private int getClimaxCount() {
		int climaxCount = 0;
		for (ObjectMap.Entry<String, Integer> entry : climaxCounters.entries()) {
			climaxCount += entry.value;
		}
		return climaxCount;
	}
	
	private int getToppingClimaxCount() { return getClimaxCount() - getReceptiveClimaxCount(); }

	private int getReceptiveClimaxCount() {
		int climaxCount = 0;
		climaxCount += climaxCounters.get(ClimaxType.ANAL_RECEPTIVE.toString(), 0);
		climaxCount += climaxCounters.get(ClimaxType.ORAL_RECEPTIVE.toString(), 0);
		return climaxCount;
	}
	
	private boolean willFaceSit(AbstractCharacter target) { return target.getStance() == Stance.SUPINE && enemyType.willFaceSit(); }
	
	private boolean isEnragedGolem() { return enemyType == EnemyEnum.GOLEM && currentFrame == 1; }
	
	private Array<Techniques> getPossibleTechniques(AbstractCharacter target, Stance stance) {
		if (enemyType == EnemyEnum.SLIME && !stance.isIncapacitatingOrErotic() && grappleStatus == GrappleStatus.NULL) { return getTechniques(SLIME_ATTACK, SLIME_QUIVER); }
		else if (enemyType == EnemyEnum.OGRE && stance != Stance.KNEELING && !stance.isIncapacitatingOrErotic() && stance != Stance.HOLDING) {
			if (willPounce() && arousal.isErect()) {
				if (target.getLegwearScore() <= 0 && target.getUnderwearScore() <= 0) {
					return getTechniques(SEIZE);	
				}
				else {
					return getTechniques(RIP);	
				}
			}
			if (getWeapon() != null) {
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
				return getCurrentMana() > 10 ? getTechniques(COMBAT_FIRE) : getTechniques(FOCUS_ENERGY);
			}
			else {
				return getTechniques(INCANTATION);
			}			
		}
		else if (enemyType == EnemyEnum.ANGEL && !alreadyIncapacitated()) {
			if (selfRessurect == 2) selfRessurect = 3;
			if (getCurrentHealth() == 0 && selfRessurect == 1) {
				selfRessurect = 2;
				return getTechniques(ANGELIC_GRACE);
			}
			else if (stance == Stance.CASTING) {
				return getTechniques(HEAL);
			}
			else if (disposition != Disposition.PEACEFUL) {
				if (getCurrentHealth() < 40 && getCurrentMana() > 10) {
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
		else if (enemyType == EnemyEnum.QUETZAL) {
			if (stance == Stance.HOLDING) {
				increaseLust(new SexualExperienceBuilder().setAnal(1).build());
				if (arousal.isEdging()) {
					return getTechniques(SKEWER);
				}
				return getTechniques(LICK_LIPS_HOLDING);
			}
			if (stance.isAnalPenetration()) {
				increaseLust(new SexualExperienceBuilder().setAnal(1).build());
				if (arousal.isSuperEdging()) {
					return getTechniques(FERTILIZE);
				}
				else if (arousal.isClimax()) {
					return getTechniques(PLUG);
				}
				return getTechniques(SCREW);
			}
			if (!alreadyIncapacitated()) {
				if (heartbeat % 15 == 14) {
					if (isErect()) {
						if (heartbeat > 60) {
							return getTechniques(SEIZE);
						}
						return getTechniques(LUBE_UP);
					}
					return getTechniques(HARDEN);
				}
				if (heartbeat < 10) {				
					return getTechniques(DO_NOTHING, WATCH, LICK_LIPS, PREPARE_OILS);
				}
			}
		}
		else if (enemyType == EnemyEnum.GIANTESS) {
			return getTechniques(COCK_SLAM);
		}
		
		Array<Techniques> possibles = getDefaultTechniqueOptions(target);
		if (stance == Stance.CASTING) {
			if (possibles.contains(Techniques.DO_NOTHING, true) || possibles.size == 0) return getTechniques(ITEM_OR_CANCEL); // fail-safe in case a spellcaster has no spells to cast
			return possibles;	
		}
		else if (stance == Stance.ERUPT) {
			stance = Stance.BALANCED;
			return getPossibleTechniques(target, stance);
		}
		return possibles.size == 0 ? getTechniques(DO_NOTHING) : possibles;
	}
	
	private Array<Techniques> getTechniques(Techniques ... techniques) { return new Array<Techniques>(techniques); }
	private boolean isSpellCaster() { return enemyType == EnemyEnum.GOLEM || enemyType == EnemyEnum.ADVENTURER || enemyType == EnemyEnum.WARLOCK; } // currently Ghost and Angel have custom move pools, so do not count in this
	private boolean canIncant() { return (isEnragedGolem() && getCurrentMana() > 3) || (enemyType == EnemyEnum.WARLOCK && getCurrentMana() >= 3) || mustIncant(); }
	private boolean mustIncant() { 
		return (enemyType == EnemyEnum.GOLEM && !isEnragedGolem() && getCurrentHealth() <= 30) || // activate
			(enemyType == EnemyEnum.ADVENTURER && (((mustCastHealing()) || mustCastTitanStrength()))); // cast heal or titan strength
	}
	private boolean mustCastHealing() { return enemyType != EnemyEnum.GOLEM && getCurrentHealth() < 30 && getCurrentMana() >= 10; }
	private boolean mustCastTitanStrength() { return enemyType != EnemyEnum.GOLEM && getCurrentMana() % 10 != 0 && getCurrentMana() > 2 && statuses.get(StatusType.STRENGTH_BUFF.toString(), 0) == 0; }
	private boolean willPounce() { return enemyType.willPounce() && arousal.isErect() && !stance.isIncapacitatingOrErotic() && grappleStatus == GrappleStatus.NULL && ((enemyType != EnemyEnum.BRIGAND && enemyType != EnemyEnum.WERESLUT) || !storyMode); }	
	private Technique getTechnique(AbstractCharacter target, Techniques technique) { return new Technique(technique.getTrait(), getCurrentState(target), enemyType == EnemyEnum.BRIGAND && technique == Techniques.PARRY ? 3 : 1); }
	private int getRandomWeighting(int size) {
		int randomResult = random.nextInt(size);
		IntArray randomWeighting = new IntArray();
		for (int ii = -1; ii < 2; ii++) { randomWeighting.add(ii); }
		if (enemyType.isOffensive()) { randomWeighting.add(-1); }
		else if (enemyType.isDefensive()) { randomWeighting.add(0); randomWeighting.add(1); }		
		randomResult = Math.max(Math.min(randomResult + randomWeighting.get(random.nextInt(randomWeighting.size)), size - 1), 0);
		return randomResult;
	}
	
	public enum Disposition {
		PEACEFUL,
		HAPPY,
		HORNY,
		ANGRY
	}
} 

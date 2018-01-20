package com.majalis.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.OrderedMap;
import com.majalis.asset.AssetEnum;
import com.majalis.character.Perk;
import com.majalis.character.PlayerCharacter;
import com.majalis.character.Stance;
import com.majalis.character.Techniques;
import com.majalis.encounter.Background;
import com.majalis.save.SaveEnum;
import com.majalis.save.SaveService;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class SkillSelectionScene extends Scene {

	private static int tableHeight = 900;
	
	private final SaveService saveService;
	private final Skin skin;
	private final Sound buttonSound;
	private final PlayerCharacter character;
	private final Texture arrowImage;
	private final AssetManager assetManager;
	private final Group skillGroup;
	private final Group magicGroup;
	private final Group perkGroup;
	private Array<StanceSkillDisplay> allDisplay;
	private Array<Table> perkDisplay;
	private int stanceSelection;
	private int perkSelection;
	private StanceTransition stanceTransition;
	private Label skillDisplay;
	private Label bonusDisplay;
	private Table skillDisplayTable;
	private Table consoleTable;
	private Label console;
	private Label skillPointsDisplay;
	private Label magicPointsDisplay;
	private Label perkPointsDisplay;
	private int skillPoints;
	private int magicPoints;
	private int perkPoints;
	private ObjectMap<Techniques, Integer> cachedSkills;
	private ObjectMap<Perk, Integer> cachedPerks;
	private ObjectMap<Techniques, Integer> skills;
	private ObjectMap<Perk, Integer> perks;
	private ObjectMap<Techniques, Array<Image>> techniquesToButtons;
	private Row selectedRow;
	private boolean locked;
	private boolean justUnlocked;
	
	public SkillSelectionScene(OrderedMap<Integer, Scene> sceneBranches, int sceneCode, final SaveService saveService, Background background, AssetManager assetManager, PlayerCharacter character) {
		super(sceneBranches, sceneCode);
		this.saveService = saveService;
		this.character = character;
		this.addActor(background);
		this.assetManager = assetManager;
		this.arrowImage = assetManager.get(AssetEnum.STANCE_ARROW.getTexture());
		skillGroup = new Group();
		magicGroup = new Group();
		perkGroup = new Group();
		this.addActor(skillGroup);
		this.addActor(magicGroup);
		this.addActor(perkGroup);
		skin = assetManager.get(AssetEnum.UI_SKIN.getSkin());
		buttonSound = assetManager.get(AssetEnum.BUTTON_SOUND.getSound());
		allDisplay = new Array<StanceSkillDisplay>();
		perkDisplay  = new Array<Table>();
		techniquesToButtons = new ObjectMap<Techniques, Array<Image>>();
		locked = false;
		justUnlocked = false;
		stanceSelection = 0;
		perkSelection = 0;
	}
	
	private void setSelectedRow(Row row) { // row your boat
		if (selectedRow != null) selectedRow.setUnselected();
		selectedRow = row;
		row.setSelected();
	}
	
	private void setUnselectedRow(Row row) { // gently down the stream
		row.setUnselected();
		selectedRow = null;
	}
	
	private ClickListener getListener(final Row row) { // merrily merrily merrily merrily
		return new ClickListener() {
			@Override
	        public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
				setSelectedRow(row);
			}
			@Override
	        public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
				if (!locked) {
					if (!justUnlocked) {
						setUnselectedRow(row);
					}
					else {
						justUnlocked = false;
					}
				}	
			}
		};
	}
	
	@Override
	public void setActive() {
		isActive = true;
		this.addAction(Actions.show());
		this.setBounds(0, 0, 2000, 2000);
		saveService.saveDataValue(SaveEnum.SCENE_CODE, sceneCode);
		
		this.skillPoints = character.getSkillPoints();
		this.magicPoints = character.getMagicPoints();
		this.perkPoints = character.getPerkPoints();
		
		skillPointsDisplay = addLabel("Skill Points: " + skillPoints, skin, null, Color.WHITE, 10, 90);
		magicPointsDisplay = addLabel("Magic Points: " + magicPoints, skin, null, Color.WHITE, 10, 50);
		perkPointsDisplay = addLabel("Perk Points: " + perkPoints, skin, null, Color.WHITE, 10, 10);

		addImage(skillGroup, assetManager.get(AssetEnum.SKILL_TITLE.getTexture()), Color.WHITE, 25, 1000);
		addImage(magicGroup, assetManager.get(AssetEnum.MAGIC_TITLE.getTexture()), Color.WHITE, 25, 1000);
		addImage(perkGroup, assetManager.get(AssetEnum.PERK_TITLE.getTexture()), Color.WHITE, 25, 1000);
		
		Image temp = addImage(assetManager.get(AssetEnum.SKILL_CONSOLE_BOX.getTexture()), Color.WHITE, 940 + 420, 0, 560, 1080); 
		temp.addAction(Actions.alpha(.9f));
		temp = addImage(skillGroup, assetManager.get(AssetEnum.SKILL_BOX_0.getTexture()), Color.WHITE, 200, 0);
		temp.addAction(Actions.alpha(.9f));
		temp = addImage(magicGroup, assetManager.get(AssetEnum.SKILL_BOX_1.getTexture()), Color.WHITE, 200, 0);
		temp.addAction(Actions.alpha(.9f));
		temp = addImage(perkGroup, assetManager.get(AssetEnum.SKILL_BOX_2.getTexture()), Color.WHITE, 200, 0);		
		temp.addAction(Actions.alpha(.9f));
		
		int consoleX = 1665;
		int consoleY = 975;
		int consoleWidth = 470;
		
		consoleTable = new Table();
		consoleTable.setPosition(consoleX,  consoleY);
		console = new Label("", skin);
		console.setWrap(true);
		console.setColor(Color.BLACK);
		consoleTable.add(console).width(consoleWidth);
		consoleTable.align(Align.top);
		this.addActor(consoleTable);
		
		stanceTransition = new StanceTransition();
		stanceTransition.setPosition(consoleX, consoleY);
		stanceTransition.align(Align.top);
		this.addActor(stanceTransition);
		
		skillDisplayTable = new Table();
		skillDisplayTable.setPosition(consoleX,  consoleY - 55);
		skillDisplay = new Label("", skin);
		skillDisplay.setWrap(true);
		skillDisplay.setColor(Color.BLACK);
		skillDisplayTable.add(skillDisplay).width(consoleWidth).row();
		skillDisplayTable.align(Align.top);
		
		bonusDisplay = new Label("", skin);
		bonusDisplay.setWrap(true);
		bonusDisplay.setColor(Color.FOREST);
		skillDisplayTable.add(bonusDisplay).width(consoleWidth);		
		
		this.addActor(skillDisplayTable);
		
		this.cachedSkills = new ObjectMap<Techniques, Integer>(character.getSkills());
		this.cachedPerks = new ObjectMap<Perk, Integer>(character.getPerks());
		skills = new ObjectMap<Techniques, Integer>(cachedSkills);
		perks = new ObjectMap<Perk, Integer>(cachedPerks);
		
		for(Stance stance : Stance.values()) {
			if (!stance.hasLearnableSkills()) continue;
			StanceSkillDisplay newStanceSkillDisplay = new StanceSkillDisplay(stance, false, assetManager);
			newStanceSkillDisplay.setPosition(495, tableHeight - 210);
			newStanceSkillDisplay.addAction(Actions.hide());
			skillGroup.addActor(newStanceSkillDisplay);
			allDisplay.add(newStanceSkillDisplay);
		}
		
		Array<Perk> learnablePerks = new Array<Perk>();
		for (final Perk perk: Perk.values()) {
			if (perk.isLearnable()) {
				learnablePerks.add(perk);
			}
		}
		
		allDisplay.get(0).addAction(Actions.show());
		for (int ii = 0; ii < 2; ii++) {
			final Table perkTable = new Table();
			int jj = 0;
			for (final Perk perk: learnablePerks) {
				if (!(jj >= ii * learnablePerks.size / 2 && jj < (ii + 1) * learnablePerks.size / 2)) { jj++; continue; }
				Integer level = perks.get(perk, 0);
				final Label label = new Label(perk.getLabel(), skin);
				label.setColor(Color.WHITE);
				label.setAlignment(Align.right);
				final Label value = new Label(level > 0 ? "(" + level + ")" : "", skin);
				value.setAlignment(Align.right);
				
				final Row row = new Row(perk, label, skillDisplay, bonusDisplay, skillDisplayTable, consoleTable);
				final Button plusButton = getPlusButton();
				final Button minusButton = getMinusButton();
				
				label.addListener(new ClickListener() {
					@Override
			        public void clicked(InputEvent event, float x, float y) {
						locked = !locked;
						justUnlocked = !locked;
					}
				});
				label.addListener(getListener(row));
				plusButton.addListener(getListener(row));
				minusButton.addListener(getListener(row));
				
				plusButton.addListener(new ClickListener() {
					@Override
			        public void clicked(InputEvent event, float x, float y) {
						buttonSound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
						if (perkPoints > 0) {
							Integer level = perks.get(perk, 0);
							if (level < perk.getMaxRank()) {
								if (level + 1 <= perkPoints) {
									perkPoints -= level + 1;
									perkPointsDisplay.setText("Perk Points: " + perkPoints);
									if (perk == Perk.SKILLED) {
										skillPoints += 2;
										skillPointsDisplay.setText("Skill Points: " + skillPoints);
									}
									perks.put(perk, ++level);						
									console.setText("You gained the " + perk.getLabel() + " Rank " + level +".");
									value.setText(level == 0 ? "" : "(" + level + ")");
								}
								else {
									console.setText("You do not have enough perk points!");
								}	
							}
							else {
								console.setText("You cannot improve on that perk any further!");
							}		
						}
						else {
							console.setText("You have no perk points!");
						}
			        }
				});
				minusButton.addListener(new ClickListener() {
					@Override
			        public void clicked(InputEvent event, float x, float y) {
						buttonSound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
						
						Integer level = perks.get(perk, 0);
						if (level == 0) {
							console.setText("You do not yet possess that perk!");
						}
						else {
							Integer cachedLevel = cachedPerks.get(perk, 0);
							if (--level >= cachedLevel) {
								perkPoints += level + 1;
								perkPointsDisplay.setText("Perk Points: " + perkPoints);
								perks.put(perk, level);
								if (level > 0) {
									console.setText("You have reduced " + perk.getLabel() + " to Rank " + level +".");
								}
								else {
									console.setText("You have removed " + perk.getLabel() + ".");
								}
								if (perk == Perk.SKILLED) {
									skillPoints -= 2;
									skillPointsDisplay.setText("Skill Points: " + skillPoints);
									handleNegativeSkillPoints();
								}
								
								value.setText(level == 0 ? "" : "(" + level + ")");
							}
							else {
								console.setText("You cannot reduce " + perk.getLabel() + " below Rank " + cachedLevel +".");
							}
						}
			        }
				});
				perkTable.add(label).size(200, 45).padRight(10);
				perkTable.add(value).size(30, 45).padRight(10);
				perkTable.add(plusButton);
				perkTable.add(minusButton).row();
				jj++;
			}
			perkTable.setPosition(530, tableHeight - 215);
			perkTable.align(Align.top);
			perkTable.addAction(Actions.hide());
			perkDisplay.add(perkTable);
			perkGroup.addActor(perkTable);
		}
		
		
		if (character.hasMagic()) {
			StanceSkillDisplay newStanceSkillDisplay = new StanceSkillDisplay(Stance.CASTING, true, assetManager);
			newStanceSkillDisplay.setPosition(495, tableHeight - 210);
			magicGroup.addActor(newStanceSkillDisplay);
		}
		Image arrow = new Image(arrowImage);
        arrow.setHeight(300);
        arrow.setWidth(120);
        arrow.setPosition(780, 320);
        arrow.addListener(new ClickListener() {
        	@Override
	        public void clicked(InputEvent event, float x, float y) {
        		changeStanceDisplay(1);
        	}
        });
        skillGroup.addActor(arrow);
        
        TextureRegion flipped = new TextureRegion(arrowImage);
        flipped.flip(true, false);
        Image arrow2 = new Image(flipped);
        arrow2.setHeight(300);
        arrow2.setWidth(120);
        arrow2.setPosition(140, 320);
        arrow2.addListener(new ClickListener() {
        	@Override
	        public void clicked(InputEvent event, float x, float y) {
        		changeStanceDisplay(-1);
        	}
        });
        skillGroup.addActor(arrow2);
        
        Image arrow3 = new Image(arrowImage);
        arrow3.setHeight(300);
        arrow3.setWidth(120);
        arrow3.setPosition(780, 320);
        arrow3.addListener(new ClickListener() {
        	@Override
	        public void clicked(InputEvent event, float x, float y) {
        		changePerkDisplay(1);
        	}
        });
        perkGroup.addActor(arrow3);
        
        Image arrow4 = new Image(flipped);
        arrow4.setHeight(300);
        arrow4.setWidth(120);
        arrow4.setPosition(140, 320);
        arrow4.addListener(new ClickListener() {
        	@Override
	        public void clicked(InputEvent event, float x, float y) {
        		changePerkDisplay(-1);
        	}
        });
        perkGroup.addActor(arrow4);
        
        final Table navigationButtons = new Table();
		final TextButton showSkills = new TextButton("Skills", skin);
		showSkills.addListener(
			new ClickListener() {
				@Override
		        public void clicked(InputEvent event, float x, float y) {
					buttonSound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
					skillGroup.addAction(Actions.show());
					magicGroup.addAction(Actions.hide());
					perkGroup.addAction(Actions.hide());
		        }
			}
		);
		final TextButton showMagic = new TextButton("Magic", skin);
		showMagic.addListener(
			new ClickListener() {
				@Override
		        public void clicked(InputEvent event, float x, float y) {
					buttonSound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
					skillGroup.addAction(Actions.hide());
					magicGroup.addAction(Actions.show());
					perkGroup.addAction(Actions.hide());
		        }
			}
		);
		final TextButton showPerks = new TextButton("Perks", skin);
		showPerks.addListener(
			new ClickListener() {
				@Override
		        public void clicked(InputEvent event, float x, float y) {
					buttonSound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
					perkDisplay.get(perkSelection).addAction(Actions.show());
					skillGroup.addAction(Actions.hide());
					magicGroup.addAction(Actions.hide());
					perkGroup.addAction(Actions.show());
		        }
			}
		);
		final TextButton done = new TextButton("Done", skin);
		done.addListener(
			new ClickListener() {
				@Override
		        public void clicked(InputEvent event, float x, float y) {
					buttonSound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
					nextScene();		   
		        }
			}
		);
	
		navigationButtons.setPosition(1675, 75);
		navigationButtons.add(showSkills).size(125, 75);
		navigationButtons.add(showMagic).size(125, 75);
		navigationButtons.add(showPerks).size(125, 75).row();
		navigationButtons.add().size(50);
		navigationButtons.add(done).width(200);
		this.addActor(navigationButtons);
		
		magicGroup.addAction(Actions.hide());
		perkGroup.addAction(Actions.hide());
	}

	private void changeStanceDisplay(int delta) {
		allDisplay.get(stanceSelection).addAction(Actions.hide());
		stanceSelection += delta;
		if (stanceSelection < 0) stanceSelection += allDisplay.size;
		if (stanceSelection >= allDisplay.size) stanceSelection -= allDisplay.size;
		allDisplay.get(stanceSelection).addAction(Actions.show());
	}
	
	private void changePerkDisplay(int delta) {
		perkDisplay.get(perkSelection).addAction(Actions.hide());
		perkSelection += delta;
		if (perkSelection < 0) perkSelection += perkDisplay.size;
		if (perkSelection >= perkDisplay.size) perkSelection -= perkDisplay.size;
		perkDisplay.get(perkSelection).addAction(Actions.show());
	}
	
	private void handleNegativeSkillPoints() {
		while (skillPoints < 0) {
			for (Techniques technique : skills.keys()) {
				Integer cachedLevel = cachedSkills.get(technique, 0);
				Integer currentLevel = skills.get(technique, 0);
				if (currentLevel > cachedLevel) {
					skillPoints += currentLevel;
					skills.put(technique, --currentLevel);
					console.setText(console.getText() + (currentLevel == 0 ? "\nRemoved " + technique.getTrait().getName() + "." : "\nReduced " + technique.getTrait().getName() + " to Rank " + currentLevel +"."));
					Array<Image> baubles = techniquesToButtons.get(technique);
										
					for (int ii = cachedLevel; ii < currentLevel; ii++) {
						baubles.get(ii).setDrawable(new TextureRegionDrawable(new TextureRegion(assetManager.get(AssetEnum.ADDED_BAUBLE.getTexture()))));
					}
					for (int ii = currentLevel; ii < baubles.size; ii++) {
						baubles.get(ii).setDrawable(new TextureRegionDrawable(new TextureRegion(assetManager.get(AssetEnum.EMPTY_BAUBLE.getTexture()))));
					}
					break;
				}
			}
		}
		skillPointsDisplay.setText("Skill Points: " + skillPoints);
	}
	
	private void nextScene() {
		character.setSkillPoints(skillPoints);
		character.setMagicPoints(magicPoints);
		character.setPerkPoints(perkPoints);
		character.setSkills(skills);
		character.setPerks(perks);
		saveService.saveDataValue(SaveEnum.PLAYER, character);
	
		sceneBranches.get(sceneBranches.orderedKeys().get(0)).setActive();
		isActive = false;
		addAction(Actions.hide());
	}
	
	private class StanceSkillDisplay extends Group {
		private final Stance stance;
		private final Table table;
		private final boolean magic;
		private StanceSkillDisplay(Stance stance, boolean magic, AssetManager assetManager) { 
			this.stance = stance;
			this.magic = magic;
			this.table = new Table();
			this.addActor(table);
			Image stanceIcon = new Image(assetManager.get(stance.getTexture()));
			stanceIcon.setPosition(-40, 115);
			stanceIcon.setScale(.75f);
			this.addActor(stanceIcon);
			init();
		}
		
		private void init() {			
			for (final Techniques technique: Techniques.values()) {
				if (!technique.isLearnable() || technique.getTrait().getUsableStance() != stance) continue;
				final Integer level = skills.get(technique, 0);
				final Label label = new Label(technique.getTrait().getName(), skin);
				label.setAlignment(Align.right);
				label.setColor(Color.WHITE);
				label.setAlignment(Align.right);
				
				final Button plusButton = getPlusButton();
				final Button minusButton = getMinusButton();
				final Array<Image> baubles = new Array<Image>();
				if (!magic) techniquesToButtons.put(technique, baubles);
				
				for (int ii = 0; ii < level; ii++) baubles.add(new Image(assetManager.get(AssetEnum.FILLED_BAUBLE.getTexture())));
				for (int ii = 0; ii < technique.getMaxRank() - level; ii++) baubles.add(new Image(assetManager.get(AssetEnum.EMPTY_BAUBLE.getTexture())));
				
				final Row row = new Row(technique, label, skillDisplay, bonusDisplay, skillDisplayTable, consoleTable);
				
				label.addListener(new ClickListener() {
					@Override
			        public void clicked(InputEvent event, float x, float y) {
						locked = !locked;
						justUnlocked = !locked;
					}
				});
				label.addListener(getListener(row));
				plusButton.addListener(getListener(row));
				minusButton.addListener(getListener(row));
				plusButton.addListener(new ClickListener() {
					@Override
			        public void clicked(InputEvent event, float x, float y) {
						buttonSound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
						if (skillPoints > 0) {
							Integer newLevel = skills.get(technique);
							if (newLevel == null) newLevel = 0;
							if (newLevel < technique.getMaxRank()) {
								if (newLevel + 1 <= (magic ? magicPoints : skillPoints)) {
									if (!magic) {
										skillPoints -= newLevel + 1;
										skillPointsDisplay.setText("Skill Points: " + skillPoints);
										skills.put(technique, ++newLevel);
									}
									else {
										magicPoints -= newLevel + 1;
										magicPointsDisplay.setText("Magic Points: " + magicPoints);
									}																		
									console.setText("You have learned " + technique.getTrait().getName() + " Rank " + newLevel +".");
									for (int ii = level; ii < newLevel; ii++) {
										baubles.get(ii).setDrawable(new TextureRegionDrawable(new TextureRegion(assetManager.get(AssetEnum.ADDED_BAUBLE.getTexture()))));
									}
								}
								else {
									console.setText("You do not have enough " + (magic ? "magic" : "skill") + " points!");
								}
							}
							else {
								console.setText("You cannot improve on that " + (magic ? "spell" : "skill") + " any further!");
							}	
						}
						else {
							console.setText("You have no " + (magic ? "magic" : "skill") + " points!");
						}
			        }
				});
				
				minusButton.addListener(new ClickListener() {
					@Override
			        public void clicked(InputEvent event, float x, float y) {
						buttonSound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
						
						Integer newLevel = skills.get(technique, 0);
						if (newLevel == 0) {
							console.setText("You do not yet possess that " + (magic ? "spell" : "skill") + "!");
						}
						else {
							Integer cachedLevel = cachedSkills.get(technique, 0);
							if (--newLevel >= cachedLevel) {
								if (!magic) {
									skillPoints += newLevel + 1;
									skillPointsDisplay.setText("Skill Points: " + skillPoints);
									
								}
								else {
									magicPoints += level + 1;
									magicPointsDisplay.setText("Magic Points: " + magicPoints);
								}
								skills.put(technique, newLevel);
								if (newLevel > 0) {
									console.setText("You have reduced " + technique.getTrait().getName() + " to Rank " + newLevel +".");
								}
								else {
									console.setText("You have unlearned " + technique.getTrait().getName() + ".");
								}
								for (int ii = level; ii < newLevel; ii++) {
									baubles.get(ii).setDrawable(new TextureRegionDrawable(new TextureRegion(assetManager.get(AssetEnum.ADDED_BAUBLE.getTexture()))));
								}
								for (int ii = newLevel; ii < baubles.size; ii++) {
									baubles.get(ii).setDrawable(new TextureRegionDrawable(new TextureRegion(assetManager.get(AssetEnum.EMPTY_BAUBLE.getTexture()))));
								}
							}
							else {
								console.setText("You cannot reduce " + technique.getTrait().getName() + " below Rank " + cachedLevel +".");
							}
						}
			        }
				});
				Image resultingStanceIcon = new Image(assetManager.get(technique.getTrait().getResultingStance().getTexture()));
				table.add(resultingStanceIcon).size(resultingStanceIcon.getWidth() * .2f, resultingStanceIcon.getHeight() * .2f);
				table.add(label).size(210, 45).padRight(10);
				table.add(minusButton);
				for (Image bauble : baubles) {
					table.add(bauble).size(bauble.getWidth(), bauble.getHeight());
				}
				for (int ii = 0; ii < 5 - baubles.size; ii++) {
					table.add().width(baubles.get(0).getWidth());
				}
				table.add(plusButton).row();
			}
			table.align(Align.top);
		}
		
	}
	
	private class StanceTransition extends Table {
		protected void setTransition(Stance startStance, Stance endStance) {
			this.clearChildren();
			Image stanceIcon = new Image(assetManager.get(startStance.getTexture()));
			this.add(stanceIcon).size(stanceIcon.getWidth() * .3f, stanceIcon.getHeight() * .3f);
			Image arrow = new Image(assetManager.get(AssetEnum.STANCE_ARROW.getTexture()));
			this.add(arrow).size(50, 50).padRight(10);
			stanceIcon = new Image(assetManager.get(endStance.getTexture()));
			this.add(stanceIcon).size(stanceIcon.getWidth() * .3f, stanceIcon.getHeight() * .3f);
		}
	}
	
	
	private Button getPlusButton() {
		ButtonStyle buttonStyle = new ButtonStyle();
		buttonStyle.up = new TextureRegionDrawable(new TextureRegion(assetManager.get(AssetEnum.PLUS.getTexture())));
		buttonStyle.down = new TextureRegionDrawable(new TextureRegion(assetManager.get(AssetEnum.PLUS_DOWN.getTexture())));
		buttonStyle.over = new TextureRegionDrawable(new TextureRegion(assetManager.get(AssetEnum.PLUS_HIGHLIGHT.getTexture())));		
		Button button = new Button(buttonStyle);
		return button;
	}
	
	private Button getMinusButton() {
		ButtonStyle buttonStyle = new ButtonStyle();
		buttonStyle.up = new TextureRegionDrawable(new TextureRegion(assetManager.get(AssetEnum.MINUS.getTexture())));
		buttonStyle.down = new TextureRegionDrawable(new TextureRegion(assetManager.get(AssetEnum.MINUS_DOWN.getTexture())));
		buttonStyle.over = new TextureRegionDrawable(new TextureRegion(assetManager.get(AssetEnum.MINUS_HIGHLIGHT.getTexture())));		
		Button button = new Button(buttonStyle);
		return button;
	}
	
	private class Row {
		private final Techniques technique;
		private final Perk perk;
		private final Label label;
		private final Label skillDisplay;
		private final Label bonusDisplay;
		private final Table skillDisplayTable;
		private final Table consoleTable;
		
		private Row(Techniques technique, Label label, Label skillDisplay, Label bonusDisplay, Table skillDisplayTable, Table consoleTable) {
			this(technique, null, label, skillDisplay, bonusDisplay, skillDisplayTable, consoleTable);
		}
		
		private Row(Perk perk, Label label, Label skillDisplay, Label bonusDisplay, Table skillDisplayTable, Table consoleTable) {
			this(null, perk, label, skillDisplay, bonusDisplay, skillDisplayTable, consoleTable);
		}
		
		private Row(Techniques technique, Perk perk, Label label, Label skillDisplay, Label bonusDisplay, Table skillDisplayTable, Table consoleTable) {
			this.technique = technique;
			this.perk = perk;
			this.label = label;
			this.skillDisplay = skillDisplay;
			this.bonusDisplay = bonusDisplay;
			this.skillDisplayTable = skillDisplayTable;
			this.consoleTable = consoleTable;
		}

		private void setSelected() {
			label.setColor(Color.FOREST);
			if (technique != null) {
				stanceTransition.setTransition(technique.getTrait().getUsableStance(), technique.getTrait().getResultingStance());
				stanceTransition.addAction(Actions.show());
				skillDisplay.setText(technique.getTrait().getLightDescription());
				bonusDisplay.setText(technique.getTrait().getBonusInfo());
			}
			else {
				skillDisplay.setText(perk.getDescription());
				bonusDisplay.setText("");
				stanceTransition.addAction(Actions.hide());
			}

			skillDisplayTable.addAction(Actions.show());
			consoleTable.addAction(Actions.hide());
		}		
		
		private void setUnselected() {
			label.setColor(Color.WHITE);
			consoleTable.addAction(Actions.show());
			stanceTransition.addAction(Actions.hide());
			skillDisplayTable.addAction(Actions.hide());
		}
	}
}

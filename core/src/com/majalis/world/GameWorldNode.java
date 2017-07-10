package com.majalis.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectSet;
import com.majalis.asset.AssetEnum;
import com.majalis.character.PlayerCharacter;
import com.majalis.encounter.EncounterCode;
import com.majalis.save.SaveEnum;
import com.majalis.save.SaveManager;
import com.majalis.save.SaveService;
/*
 * Represents a node on the world map.
 */
public class GameWorldNode extends Group implements Comparable<GameWorldNode> {
	private final ObjectSet<GameWorldNode> connectedNodes;
	private final AssetManager assetManager;
	private final Array<Path> paths;
	private final SaveService saveService;
	// temporary
	private final BitmapFont font;
	private final int nodeCode;
	private final GameWorldNodeEncounter encounter;
	// for determining where to draw this node at
	private final Vector2 position;
	private final boolean visited;
	private final Sound sound;
	private final PlayerCharacter character;
	private boolean selected;
	private boolean current;
	private boolean active;
	private int visibility;
	private boolean hover;
	private Texture activeImage;
	private Texture roadImage;
	private Texture hoverImage;
	private Texture arrowImage;
	private int arrowHeight;
	private int arrowShift;
	
	// all the nodes need are the encounter CODES, not the actual encounter - should probably pass in some kind of object that contains the encounter generation logic, rather than an encounter and defaultEncounter code - at least, need a description of the encounter attached
	public GameWorldNode(SaveService saveService, BitmapFont font, final int nodeCode, GameWorldNodeEncounter encounter, Vector2 position, boolean visited, Sound sound, PlayerCharacter character, AssetManager assetManager) {
		this.connectedNodes = new ObjectSet<GameWorldNode>();
		this.assetManager = assetManager;
		paths = new Array<Path>();
		this.saveService = saveService;
		this.font = font;
		this.encounter = encounter;
		this.position = position;
		this.nodeCode = nodeCode;
		this.visited = visited;

		activeImage = getNodeTexture(encounter.getCode());
		hoverImage = assetManager.get(AssetEnum.WORLD_MAP_HOVER.getTexture());
		roadImage = assetManager.get(AssetEnum.ROAD.getTexture());
		arrowImage = assetManager.get(AssetEnum.ARROW.getTexture());
		this.sound = sound;
		this.character = character;
		selected = false;
		current = false;
		hover = false;
		active = false;
		
		this.addAction(Actions.visible(true));
		this.addAction(Actions.show());
		this.setBounds(position.x, position.y, activeImage.getWidth(), activeImage.getHeight());
		arrowHeight = 0;
		arrowShift = 1;
		visibility = -1;
	}
	
	protected int getNodeCode() { return nodeCode; }
	
	private Texture getNodeTexture(EncounterCode encounterCode) {
		return assetManager.get(encounterCode.getTexture().getTexture());
	}

	protected boolean isAdjacent(GameWorldNode otherNode) {
		return position.dst2(otherNode.getPosition()) < 67700;
	}
	
	protected Vector2 getPosition() {
		return position;
	}
	
	protected void connectTo(GameWorldNode otherNode) {
		if (connectedNodes.contains(otherNode)) {
			return;
		}
		connectedNodes.add(otherNode);
		Vector2 centering = new Vector2(activeImage.getWidth()/2-10, activeImage.getHeight()/2);
		paths.add(new Path(roadImage, new Vector2(position).add(centering), new Vector2(otherNode.getPosition()).add(centering)));
		otherNode.getConnected(this);
	}
	
	private void getConnected(GameWorldNode otherNode) {
		connectedNodes.add(otherNode);
	}
	
	protected void setAsCurrentNode() {
		for (GameWorldNode connectedNode : connectedNodes) {
			connectedNode.setActive();
		}
		ObjectSet<GameWorldNode> visibleSet = new ObjectSet<GameWorldNode>();
		visibleSet.add(this);
		setNeighborsVisibility(getPerceptionLevel(character.getScoutingScore()), 1, visibleSet);
	}
	
	private void setActive() {
		active = true;
		this.addListener(new ClickListener() { 
			@Override
	        public void clicked(InputEvent event, float x, float y) {
				if (active) {
					fire(new ChangeListener.ChangeEvent());
					active = false;
					for (GameWorldNode connectedNode : connectedNodes) {
						connectedNode.setClickedAndAdjacentClicked();						
					}
					sound.play(Gdx.app.getPreferences("tales-of-androgyny-preferences").getFloat("volume") *.5f);
					visit();
				}
			}
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
	
	private void setClickedAndAdjacentClicked() {
		active = false;
		for (GameWorldNode connectedNode : connectedNodes) {
			connectedNode.active = false;
		}
	}

	private void setVisibility(int visibility) {
		this.visibility = visibility;
		if (!active && !current) {
			this.addListener(new ClickListener() { 
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
	}
	
	private void setNeighborsVisibility(int visibility, int diminishingFactor, ObjectSet<GameWorldNode> visibleSet) {
		ObjectSet<GameWorldNode> nodesToSetVisible = new ObjectSet<GameWorldNode>(connectedNodes);
		while (visibility >= 0) {
			for (GameWorldNode connectedNode : nodesToSetVisible) {
				connectedNode.setVisibility(visibility);
				visibleSet.add(connectedNode);
			}
			ObjectSet<GameWorldNode> nextBatch = new ObjectSet<GameWorldNode>();
			for (GameWorldNode connectedNode : nodesToSetVisible) {
				ObjectSet<GameWorldNode> newNeighbors = connectedNode.getNeighbors(visibleSet);
				nextBatch.addAll(newNeighbors);
			}
			nodesToSetVisible = nextBatch;
			visibility -= diminishingFactor;	
		}
		
	}
	
	private ObjectSet<GameWorldNode> getNeighbors(ObjectSet<GameWorldNode> visibleSet) {
		ObjectSet<GameWorldNode> neighbors = new ObjectSet<GameWorldNode>();
		for (GameWorldNode node : connectedNodes) {
			if (!visibleSet.contains(node)) {
				neighbors.add(node);
			}
		}
		return neighbors;
	}
	
	
	protected void visit() {
		this.addAction(Actions.sequence(Actions.delay(1.5f), new Action() {
			@Override
			public boolean act(float delta) {
				delayedVisit();
				return true;
			}}));
	}
	
	private void delayedVisit() {
		selected = true;
		// all of this logic should be elsewhere - likely it should be something the world map itself handles
		
		int foodLeft = character.getFood() - 4;
		saveService.saveDataValue(SaveEnum.FOOD, -4);
		saveService.saveDataValue(SaveEnum.TIME, 1);
		if (foodLeft < 0) {
			saveService.saveDataValue(SaveEnum.HEALTH, 5 * foodLeft);
		}
		if (character.getCurrentHealth() <= 0) {
			if (foodLeft < 0) {
				saveService.saveDataValue(SaveEnum.ENCOUNTER_CODE, EncounterCode.STARVATION);			
				saveService.saveDataValue(SaveEnum.CONTEXT, SaveManager.GameContext.ENCOUNTER);
				saveService.saveDataValue(SaveEnum.RETURN_CONTEXT, SaveManager.GameContext.WORLD_MAP);
			}
			else {
				saveService.saveDataValue(SaveEnum.ENCOUNTER_CODE, EncounterCode.CAMP_AND_EAT);			
				saveService.saveDataValue(SaveEnum.CONTEXT, SaveManager.GameContext.ENCOUNTER);
				saveService.saveDataValue(SaveEnum.RETURN_CONTEXT, SaveManager.GameContext.WORLD_MAP);
			}
		}
		else {
			if (!visited) {
				saveService.saveDataValue(SaveEnum.ENCOUNTER_CODE, encounter.getCode());
				saveService.saveDataValue(SaveEnum.VISITED_LIST, nodeCode);
				saveService.saveDataValue(SaveEnum.CONTEXT, encounter.getContext());
				saveService.saveDataValue(SaveEnum.RETURN_CONTEXT, SaveManager.GameContext.WORLD_MAP);
			}
			else {
				if (encounter.getDefaultCode() == EncounterCode.DEFAULT) {
					selected = false;
					setAsCurrentNode();
				}
				else {
					saveService.saveDataValue(SaveEnum.ENCOUNTER_CODE, encounter.getDefaultCode());
					saveService.saveDataValue(SaveEnum.CONTEXT, encounter.getDefaultContext());
					saveService.saveDataValue(SaveEnum.RETURN_CONTEXT, SaveManager.GameContext.WORLD_MAP);
				}
			}
			saveService.saveDataValue(SaveEnum.NODE_CODE, nodeCode);
			saveService.saveDataValue(SaveEnum.CAMERA_POS, position);
		}
	}
	
	@Override
    public void draw(Batch batch, float parentAlpha) {
		if (hover && active) {
			Color cache = batch.getColor();
			batch.setColor(Color.GREEN);
			batch.draw(activeImage, position.x, position.y);
			batch.setColor(cache);
		}
		else {
			batch.draw(activeImage, position.x, position.y);
		}
		
		if(active) {
			Color cache = batch.getColor();
			batch.setColor(Color.WHITE);
			batch.draw(arrowImage, position.x+25, position.y+45+arrowHeight/5);
			batch.setColor(cache);
			arrowHeight += arrowShift;
			if (arrowHeight > 100 || arrowHeight < 0) arrowShift = 0 - arrowShift;
		}
		
		super.draw(batch, parentAlpha);
    }
	
	@Override
	public void setColor(Color color) {
		super.setColor(color);
		for (Actor actor : getChildren()) {
			actor.setColor(color);
		}
	}
	// this could be an event on the world map screen that draws the hover image and gets the text from the hovered over node
	public void drawHover(Batch batch, Vector2 hoverPosition) {
		if (hover) {
			// render hover box
			batch.draw(hoverImage, hoverPosition.x, hoverPosition.y);
			// render hover text
			font.setColor(0f,0,0,1);
			font.draw(batch, getHoverText(), hoverPosition.x+50, hoverPosition.y+170, 150, Align.center, true);	
		}
	}

	private String getHoverText() {
		return encounter.getDescription(visibility, visited);
	}
	
	private int getPerceptionLevel(int perception) {
		if (perception >= 8) {
			return 3;
		}
		if (perception >= 5) {
			return 2;
		}
		else if (perception >= 2) {
			return 1;
		}
		else {
			return 0;
		}
	}

	protected Array<Path> getPaths() {
		return paths;
	}
	
	protected boolean isSelected() {
		return selected;
	}

	protected boolean isOverlapping(GameWorldNode otherNode) {
		return isOverlapping(otherNode.getPosition());
	}
	
	protected boolean isOverlapping(Vector2 otherNode) {
		return Intersector.overlaps(new Circle(position, 105), new Circle(otherNode, 25));
	}
	
	@Override
	public int compareTo(GameWorldNode otherNode) {
		if (otherNode.getX() >= position.x) {
			return 1;
		}
		else {
			return -1;
		}
	}
}

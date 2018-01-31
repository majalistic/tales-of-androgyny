package com.majalis.world;

import com.badlogic.gdx.utils.Array;

public class GameWorld {

	private final Array<GameWorldNode> nodes;
	protected GameWorld(Array<GameWorldNode> nodes) {
		this.nodes = nodes;
	}
	public Array<GameWorldNode> getNodes() { return nodes; }

}

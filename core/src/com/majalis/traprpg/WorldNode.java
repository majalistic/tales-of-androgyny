package com.majalis.traprpg;

import com.badlogic.gdx.utils.Array;

public class WorldNode {

	private final Array<WorldNode> connectedNodes;
	
	public WorldNode(Array<WorldNode> connectedNodes){
		this.connectedNodes = connectedNodes;
	}
}

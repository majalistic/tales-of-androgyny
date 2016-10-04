package com.majalis.asset;

public enum AssetEnum {
	MOUNTAIN_ACTIVE("worldmap/MountainNode0.png"),
	MOUNTAIN_INACTIVE("worldmap/MountainV.png"),
	FOREST_ACTIVE("worldmap/ForestNode0.png"),
	FOREST_INACTIVE("worldmap/ForestV.png"),
	GRASS0("worldmap/BaseGrass0.png"),
	GRASS1("worldmap/BaseGrass1.png"),
	GRASS2("worldmap/BaseGrass2.png"),
	CLOUD("worldmap/Cloud.png"),
	APPLE("worldmap/Apple.png"),
	MEAT("worldmap/Meat.png"),
	ROAD("worldmap/Road.png");
	
	private final String path;

	AssetEnum(String path) {
	    this.path = path;
	 }
	public String getPath(){return path;}
}

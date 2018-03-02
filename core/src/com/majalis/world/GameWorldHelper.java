package com.majalis.world;

import com.badlogic.gdx.math.Vector2;

public class GameWorldHelper {
	private static int scalingFactor = 54;
	private static int xFactor = -9;
	private final static int tileWidth = 61;
	private final static int tileHeight = 55;
	
	public static int getTileWidth() { return tileWidth; }
	public static int getTileHeight() { return tileHeight; }
	
	public static int getTrueX(int x) { return (x - 16) * (scalingFactor + xFactor); }
	public static int getTrueY(int x, int y) { return (y - 85) * scalingFactor + (x - 16) * scalingFactor / 2; }
	public static Vector2 calculatePosition(int x, int y) { return new Vector2(getTrueX(x), getTrueY(x, y)); }
	public static int distance(int x, int y, int x2, int y2) { return Math.max(Math.max(Math.abs(x - x2), Math.abs(y - y2)), Math.abs((0 - (x + y)) - (0 - (x2 + y2)))); }
}

package com.majalis.world;

import com.badlogic.gdx.math.Vector2;

public class GameWorldHelper {
	private static int scalingFactor = 54;
	private static int xFactor = -9;
	
	public static int getTrueX(int x) {
		return (x - 16) * (scalingFactor + xFactor);
	}
	
	public static int getTrueY(int x, int y) {
		return (y - 85) * scalingFactor + (x - 16) * scalingFactor / 2;
	}
	
	public static Vector2 calculatePosition(int x, int y) {
		return new Vector2(getTrueX(x), getTrueY(x, y));
	}
}

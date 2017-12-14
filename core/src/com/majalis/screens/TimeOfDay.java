package com.majalis.screens;

import com.badlogic.gdx.graphics.Color;

public enum TimeOfDay {
	DAWN ("Dawn", getColor(156, 154, 32), getColor(0, 0, 0), .5f, 110, 1.2f),
	MORNING ("Morning", getColor(255, 255, 214), getColor(0, 0, 0), .7f, 142, .8f),
	AFTERNOON ("Afternoon", getColor(255, 255, 255), getColor(0, 0, 0), .8f, 165, .7f),
	DUSK ("Dusk", getColor(246, 212, 181), getColor(0, 0, 0), .7f, 206, .8f),
	EVENING ("Evening", getColor(75, 125, 217), getColor(0, 0, 0), .5f, 238, 1.4f),
	NIGHT ("Night", getColor(35, 55, 120), getColor(0, 0, 0), .3f, 40, .5f)
	;

	private final String display;
	private final Color color;
	private final Color shadowColor;
	private final float shadowAlpha;
	private final int shadowDirection;
	private final float shadowLength;
	
	private TimeOfDay(String display, Color color, Color shadowColor, float shadowAlpha, int shadowDirection, float shadowLength) {
		this.display = display;
		this.color = color;
		this.shadowColor = shadowColor;
		this.shadowAlpha = shadowAlpha;
		this.shadowDirection = shadowDirection;
		this.shadowLength = shadowLength;		
	}
	
	private static Color getColor(float r, float g, float b) { return new Color(r/256f, g/256f, b/256f, 1); }
	
	protected String getDisplay() { return display; }
	public Color getColor() { return color; }
	public Color getShadowColor() { return shadowColor; }
	public float getShadowAlpha() { return shadowAlpha; }
	public int getShadowDirection() { return shadowDirection; }
	public float getShadowLength() { return shadowLength; } 
	public static TimeOfDay getTime(int time) { return TimeOfDay.values()[time % 6]; }

	
	public static int timeTillNext(TimeOfDay targetTime, int time) { return targetTime.ordinal() == time % 6 ? 6 : timeTill(targetTime, time); }
	
	// returns 0 if targetTime is current time
	public static int timeTill(TimeOfDay targetTime, int time) {
		int currentTimeOrdinal = time % 6;
		int diff = 0;
		while (currentTimeOrdinal != targetTime.ordinal()) {
			currentTimeOrdinal++;
			diff++;
			currentTimeOrdinal %= 6;
		}
		return diff;
	}

	public boolean isDay() {
		return this == DAWN || this == MORNING || this == AFTERNOON;
	}	
}


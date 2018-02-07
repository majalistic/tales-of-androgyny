package com.majalis.screens;

import com.badlogic.gdx.graphics.Color;

public enum TimeOfDay {
	DAWN ("Dawn", getColor(156, 154, 32), getColor(0, 0, 0, .5f), 1, 1.2f),
	MORNING ("Morning", getColor(255, 255, 214), getColor(0, 0, 0, .7f), .5f, .8f),
	AFTERNOON ("Afternoon", getColor(255, 255, 255), getColor(0, 0, 0, .8f), .1f, .7f),
	DUSK ("Dusk", getColor(246, 212, 181), getColor(0, 0, 0, .7f), -.2f, .8f),
	EVENING ("Evening", getColor(75, 125, 217), getColor(0, 0, 0, .5f), -1f, 1.2f),
	NIGHT ("Night", getColor(35, 55, 120), getColor(0, 0, 0, .1f), -1f, .1f)
	;

	private static Color getColor(float r, float g, float b) { return getColor(r, g, b, 1); }
	private static Color getColor(float r, float g, float b, float a) { return new Color(r/256f, g/256f, b/256f, a); }
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
	
	private final String display;
	private final Color color;
	private final Color shadowColor;
	private final float shadowDirection;
	private final float shadowLength;
	
	private TimeOfDay(String display, Color color, Color shadowColor, float shadowDirection, float shadowLength) {
		this.display = display;
		this.color = color;
		this.shadowColor = shadowColor;
		this.shadowDirection = shadowDirection;
		this.shadowLength = shadowLength;		
	}
	
	protected String getDisplay() { return display; }
	public Color getColor() { return color; }
	public Color getShadowColor() { return shadowColor; }
	public float getShadowDirection() { return shadowDirection; }
	public float getShadowLength() { return shadowLength; } 
	public boolean isDay() { return this == DAWN || this == MORNING || this == AFTERNOON; }
}


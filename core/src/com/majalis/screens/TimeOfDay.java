package com.majalis.screens;

import com.badlogic.gdx.graphics.Color;

public enum TimeOfDay {
	DAWN ("Dawn", getColor(156, 154, 32)),
	MORNING ("Morning", getColor(255, 255, 214)),
	AFTERNOON ("Afternoon", getColor(251, 255, 255)),
	DUSK ("Dusk", getColor(246, 212, 181)),
	EVENING ("Evening", getColor(75, 125, 217)),
	NIGHT ("Night", getColor(35, 55, 120))

	;

	private final String display;
	private final Color color;
	private TimeOfDay(String display, Color color) {
		this.display = display;
		this.color = color;
	}
	
	private static Color getColor(float r, float g, float b) { return new Color(r/256f, g/256f, b/256f, 1); }
	
	protected String getDisplay() { return display; }
	protected Color getColor(){ return color; }
	protected static TimeOfDay getTime(int time) { return TimeOfDay.values()[time % 6]; }	
}


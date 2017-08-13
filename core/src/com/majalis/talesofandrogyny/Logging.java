package com.majalis.talesofandrogyny;

public class Logging {
	private static long lastTime;
	private static long total;
	
	public static void logTime(String display) {
		if (lastTime == 0) {
			lastTime = System.currentTimeMillis();
		}
		
		long currentTime = System.currentTimeMillis();
		long delta = currentTime - lastTime;
		total += delta;
		System.out.println(display + " - delta: " + delta + " total: " + total);
		lastTime = currentTime;
	}
}

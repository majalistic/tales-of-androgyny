package com.majalis.talesofandrogyny;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

public class Logging {
	private static long lastTime;
	private static long total;
	private static StringBuilder buffer;
	
	public static void logTime(String display) {
		if (lastTime == 0) {
			buffer = new StringBuilder();
			lastTime = System.currentTimeMillis();
		}
		
		long currentTime = System.currentTimeMillis();
		long delta = currentTime - lastTime;
		total += delta;
		buffer.append(delta + ": delta - " + total + ": total - " + display + "\n");
		lastTime = currentTime;
	}
	
	public static void flush() {
		FileHandle log = Gdx.files.local("log.txt");
		log.writeString(buffer.toString(), false);
	}
}

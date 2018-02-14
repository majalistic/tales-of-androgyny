package com.majalis.encounter;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.OrderedMap;
import com.majalis.scenes.Scene;

public class LogDisplay extends Label {
	private final IntArray log;
	private final OrderedMap<Integer, Scene> sceneMap;
	private String displayText;
	protected LogDisplay(IntArray log, OrderedMap<Integer, Scene> sceneMap, Skin skin) {
		super("", skin);
		this.log = log;
		this.sceneMap = sceneMap;
	}
	
	public void displayLog() {
		if (sceneMap == null || log == null) return;
		String logResult = "";
		for (int sceneCode : log.toArray()) {
			Scene temp = sceneMap.get(sceneCode);
			if (temp != null && !temp.getText().equals("")) {
				logResult += temp.getText() + "\n";
			}
		}
		displayText = logResult.equals("") ? "" : logResult.trim();
		setText(displayText);
	}
}

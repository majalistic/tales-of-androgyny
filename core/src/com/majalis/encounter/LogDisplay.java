package com.majalis.encounter;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.OrderedMap;
import com.majalis.scenes.Scene;

public class LogDisplay extends Label {
	private final IntArray log;
	private final OrderedMap<Integer, Scene> sceneMap;
	private boolean displaying;
	protected LogDisplay(IntArray log, OrderedMap<Integer, Scene> sceneMap, Skin skin) {
		super("Show Log", skin);
		this.log = log;
		this.sceneMap = sceneMap;
		displaying = false;
		this.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (displaying) {
					setText("Show Log");
					displaying = false;
				}
				else {
					displayLog();
					displaying = true;
				}
				event.handle();
			}
		});
	}
	
	public void displayLog() {
		String logResult = "";
		for (int sceneCode : log.toArray()) {
			Scene temp = sceneMap.get(sceneCode);
			if (temp != null) logResult += temp.getText() + "\n";
		}
		
		setText("Hide Log\n\n" + logResult.trim());
	}
}

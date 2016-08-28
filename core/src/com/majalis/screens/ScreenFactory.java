package com.majalis.screens;

import com.badlogic.gdx.Game;

/*
 * ScreenFactory interface to enable mocking.
 */
public interface ScreenFactory {
	Game getGame();
	AbstractScreen getScreen(ScreenEnum screenRequest);
}

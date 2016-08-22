package com.majalis.traprpg;
/*
 * ScreenFactory interface to enable mocking.
 */
public interface ScreenFactory {
	AbstractScreen getScreen(ScreenEnum screenRequest, ScreenService screenService);
}

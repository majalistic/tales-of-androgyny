package com.majalis.traprpg;

public interface ScreenFactory {
	AbstractScreen getScreen(ScreenEnum screenRequest, ScreenService screenService);
}

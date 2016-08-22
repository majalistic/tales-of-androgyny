package com.majalis.traprpg;
/*
 * Message service - gives AbstractScreen the ability to get screens from the ScreenFactory using an enum without having direct access.
 */
public class ScreenService {

	private final ScreenFactory factory;
	
	public ScreenService(ScreenFactory factory) {
		this.factory = factory;
	}

	public AbstractScreen getScreen(ScreenEnum screenRequest){
		return factory.getScreen(screenRequest, this);
	}
	
}

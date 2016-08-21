package com.majalis.traprpg;

public class ScreenService {

	private final ScreenFactory factory;
	
	public ScreenService(ScreenFactory factory) {
		this.factory = factory;
	}

	public AbstractScreen getScreen(ScreenEnum screenRequest){
		return factory.getScreen(screenRequest, this);
	}
	
}

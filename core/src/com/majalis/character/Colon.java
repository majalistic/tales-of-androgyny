package com.majalis.character;

public class Colon {
	private ColonContents contents;
	
	protected Colon() {
		contents = ColonContents.EMPTY;
	}
			
	private enum ColonContents { // Your colon is currently full of cum.  Had a good time?, Your colon is currently full of giant dick.  Did you really need to check?
		EMPTY, FILLED_WITH_EGGS, FILLED_WITH_CUM, FILL_WITH_COCK // only for very long cocks
	}
}

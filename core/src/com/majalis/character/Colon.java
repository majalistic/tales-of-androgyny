package com.majalis.character;

public class Colon {
	private ColonContents contents;
	
	protected Colon() {
		contents = ColonContents.EMPTY;
	}
			
	public String getContents() { return toLabel(contents.toString()); }
	
	private String toLabel(String identifier) {
		char[] chars = identifier.replace("_", "-").toLowerCase().toCharArray();
		boolean found = false;
		for (int i = 0; i < chars.length; i++) {
			if (!found && Character.isLetter(chars[i])) {
				chars[i] = Character.toUpperCase(chars[i]);
				found = true;
		    } 
			else if (Character.isWhitespace(chars[i])) {
				found = false;
		    }
		}		
		return String.valueOf(chars);
	}
	
	private enum ColonContents { // Your colon is currently full of cum.  Had a good time?, Your colon is currently full of giant dick.  Did you really need to check?
		EMPTY, FILLED_WITH_EGGS, FILLED_WITH_CUM, FILL_WITH_COCK // only for very long cocks
	}
}

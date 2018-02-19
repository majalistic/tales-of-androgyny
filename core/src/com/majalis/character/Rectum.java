package com.majalis.character;

public class Rectum {
	
	private RectumContents contents;
	private RectumFullness fullness;
	private RectumTightness tightness;

	protected Rectum() {
		contents = RectumContents.DRY;
		fullness = RectumFullness.EMPTY;
		tightness = RectumTightness.NORMAL;
	}
	
	public String getContents() { return toLabel(contents.toString()); }
	public String getFullness() { return toLabel(fullness.toString()); }
	public String getTightness() { return toLabel(tightness.toString()); }
	
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
	
	private enum RectumContents {
		DRY, CUM_HOLSTER, SLOPPY_CUM_MESS, CUM_STUFFED, EGGED, DICK_ACCOMMODATING, CUMMING_DICK
	}
	
	private enum RectumFullness {
		EMPTY, RECENTLY_VACATED, OCCUPIED
	}
	
	private enum RectumTightness {
		NORMAL, LOOSE, BAGGY
	}
	
}

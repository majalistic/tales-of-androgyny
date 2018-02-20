package com.majalis.character;

public class Rectum {
	
	private RectumContents contents;
	private RectumFullness fullness;
	private RectumTightness tightness;
	private int cum = 0;
	private int eggs = 0;
	
	protected Rectum() {
		contents = RectumContents.EMPTY;
		fullness = RectumFullness.EMPTY;
		tightness = RectumTightness.NORMAL;
		cum = 0;
	}
	
	protected int getFullnessAmount() { return cum + eggs; }
	
	protected void fillButtWithCum(int cumMod) { 
		this.cum += cumMod; 
		if (cum <= 0) {
			cum = 0;
			contents = RectumContents.EMPTY;
		}	
		else if (contents != RectumContents.EGGED) {
			contents = cum >= 20 ? RectumContents.CUM_STUFFED : cum >= 10 ? RectumContents.SLOPPY_CUM_MESS : RectumContents.CUM_HOLSTER;
		}
	} 
	
	protected void togglePlug() {
		contents = contents == RectumContents.PLUGGED ? RectumContents.EMPTY : RectumContents.PLUGGED;
	}
	
	protected void fillButtWithEggs(int eggs) {
		this.eggs += eggs; 
		if (eggs <= 0) {
			eggs = 0;
			contents = RectumContents.EMPTY;
		}	
		else {
			contents = RectumContents.EGGED;
		}
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
		EMPTY, CUM_HOLSTER, SLOPPY_CUM_MESS, CUM_STUFFED, EGGED, DICK_ACCOMMODATING, CUMMING_DICK, PLUGGED
	}
	
	private enum RectumFullness {
		EMPTY, RECENTLY_VACATED, OCCUPIED
	}
	
	private enum RectumTightness {
		NORMAL, LOOSE, BAGGY
	}
	
}

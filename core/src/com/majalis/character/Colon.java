package com.majalis.character;

public class Colon {
	private ColonContents contents;
	private int cum;
	private int eggs;
	
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
	
	protected void fillWithCum(int cumOverflow) {
		this.cum += cumOverflow;
		setContents();
	}
	
	protected void fillWithEggs(int eggOverflow) {
		this.eggs += eggOverflow;
		setContents();
	}
	
	protected int getFullnessAmount() { return cum + eggs; }
	protected int drain(int cumUnderflow) {
		cum -= cumUnderflow;
		setContents();
		if (cum < 0) {
			int temp = cum + cumUnderflow;
			cum = 0;
			return temp;
		}
		return cumUnderflow;
	}
	
	public void flushEggs() { eggs = 0; setContents(); }
	
	private void setContents() {
		contents = eggs > 0 ? ColonContents.FILLED_WITH_EGGS : contents == ColonContents.EMPTY && cum > 0 ? ColonContents.FILLED_WITH_CUM : cum == 0 && eggs == 0 ? ColonContents.EMPTY : contents; 
	}
	
	private enum ColonContents { // Your colon is currently full of cum.  Had a good time?, Your colon is currently full of giant dick.  Did you really need to check?
		EMPTY, FILLED_WITH_EGGS, FILLED_WITH_CUM, FILL_WITH_COCK // only for very long cocks
	}

}

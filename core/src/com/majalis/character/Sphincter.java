package com.majalis.character;

public class Sphincter {

	private SphincterFriction friction;
	private SphincterWetness wetness;
	private SphincterDilation dilation;
	private SphincterTightness tightness;

	protected Sphincter() {
		friction = SphincterFriction.NORMAL;
		wetness = SphincterWetness.DRY;
		dilation = SphincterDilation.NORMAL;
		tightness = SphincterTightness.TIGHT;
	}
	
	public String getFriction() { return toLabel(friction.toString()); }
	public String getWetness() { return toLabel(wetness.toString()); }
	public String getDilation() { return toLabel(dilation.toString()); }
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
	
	private enum SphincterFriction { // you are currently engaged in dry anal sex - fire in the hole! this one increases quickly from dry anal sex
		NORMAL, IRRITATED, SWOLLEN, RED_RING_OF_FIRE
	}
			
	private enum SphincterWetness {
		DRY, MOIST, WET, CUM_SOAKED
	}
			
	private enum SphincterDilation {
		NORMAL, PENETRATED, STRETCHED_WIDE
	}
			
	private enum SphincterTightness {
		TIGHT, LOOSE, GAPING
	}
	
}

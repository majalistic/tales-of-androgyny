package com.majalis.character;

public class Sphincter {

	private SphincterFriction friction;
	private SphincterWetness wetness;
	private SphincterDilation dilation;
	private SphincterTightness tightness;

	private int receivedSexRecently;
	private int receivedSexTotal;
	private int wetnessAmount;
	private int time;
	
	protected Sphincter() {
		friction = SphincterFriction.NORMAL;
		wetness = SphincterWetness.DRY;
		dilation = SphincterDilation.NORMAL;
		tightness = SphincterTightness.TIGHT;
		time = 0;
	}
	
	public String getFriction() { return toLabel(friction.toString()); }
	public String getWetness() { return toLabel(wetness.toString()); }
	public String getDilation() { return toLabel(dilation.toString()); }
	public String getTightness() { return toLabel(tightness.toString()); }
	
	private void modReceivedSexRecently(int mod) {
		receivedSexRecently += mod;
		if (receivedSexRecently <= 0) receivedSexRecently = 0;
		friction = receivedSexRecently >= 5 ? SphincterFriction.RED_RING_OF_FIRE : receivedSexRecently >= 3 ? SphincterFriction.SWOLLEN : receivedSexRecently >= 1 ? SphincterFriction.IRRITATED :SphincterFriction.NORMAL;
	}
	
	protected void receiveSex(SexualExperience sex) {
		int amount = sex.getAnalSex() + sex.getAnal();
		receivedSexTotal += amount;
		tightness = receivedSexTotal >= 30 ? SphincterTightness.GAPING : receivedSexTotal >= 15 ? SphincterTightness.LOOSE : SphincterTightness.TIGHT;
		modReceivedSexRecently(amount);
		modWetness(sex.getCreampies());
	}
	
	protected void modWetness(int mod) {
		wetnessAmount += mod;
		if (wetnessAmount <= 0) wetnessAmount = 0;
		wetness = wetnessAmount >= 10 ? SphincterWetness.CUM_SOAKED : wetnessAmount >= 5 ? SphincterWetness.WET : wetnessAmount >= 1 ? SphincterWetness.MOIST : SphincterWetness.DRY;
	}
	
	protected void tick(int time) {
		int oldVal = this.time / 5;
		this.time += time;
		modReceivedSexRecently(-1 * (this.time / 5 - oldVal));		
		modWetness(-1);
	}
	
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

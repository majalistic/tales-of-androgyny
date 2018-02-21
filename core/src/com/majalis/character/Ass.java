package com.majalis.character;

public class Ass {
	private final Sphincter sphincter;
	private final Rectum rectum;
	private final Colon colon;
	@SuppressWarnings("unused")
	private Ass() { sphincter = null; rectum = null; colon = null; }
	protected Ass(Sphincter sphincter, Rectum rectum, Colon colon) {
		this.sphincter = sphincter;
		this.rectum = rectum;
		this.colon = colon;
	}
	
	protected void fillButtWithCum(int cumMod) { 
		rectum.fillWithCum(cumMod); 
		colon.fillWithCum(rectum.getCumOverflow());
		rectum.fillWithCum(colon.drain(rectum.getCumUnderflow()));
	} 
	protected void fillButtWithEggs(int eggs) { rectum.fillButtWithEggs(eggs); }	
	protected void receiveSex(SexualExperience sex) { sphincter.receiveSex(sex); }
	protected void tick(int time) {
		sphincter.tick(time); 
		if (rectum.getFullnessAmount() > 0 && rectum.notPlugged()) {
			sphincter.modWetness(1);
		}
	}
	
	protected int getFullnessAmount() { return rectum.getFullnessAmount(); } 
	
	protected void togglePlug() { rectum.togglePlug(); }
	
	public Sphincter getSphincter() { return sphincter; }
	public Rectum getRectum() { return rectum; }
	public Colon getColon() { return colon; }
}

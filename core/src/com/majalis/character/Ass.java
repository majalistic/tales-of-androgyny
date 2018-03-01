package com.majalis.character;

import com.majalis.asset.AnimatedActor;

public class Ass {
	private final Sphincter sphincter;
	private final Rectum rectum;
	private final Colon colon;
	private transient AnimatedActor belly;
	@SuppressWarnings("unused")
	private Ass() { sphincter = null; rectum = null; colon = null; }
	protected Ass(Sphincter sphincter, Rectum rectum, Colon colon) {
		this.sphincter = sphincter;
		this.rectum = rectum;
		this.colon = colon;
	}
	
	private int getFullnessLevel() { 
		int fullnessAmount = getFullnessAmount();
		if (fullnessAmount >= 20) return 4;
		if (fullnessAmount >= 10) return 3;
		if (fullnessAmount >= 5) return 2;
		return 1;
	}
	
	protected void fillButtWithCum(int cumMod) { 
		int fullness = getFullnessLevel();
		rectum.fillWithCum(cumMod); 
		colon.fillWithCum(rectum.getCumOverflow());
		rectum.fillWithCum(colon.drain(rectum.getCumUnderflow()));
		if (fullness != getFullnessLevel() && rectum.getEggs() <= 0) belly.setAnimation(0, fullness + "to" + getFullnessLevel(), false);
	} 
	protected void fillButtWithEggs(int eggs) { 
		int fullness = getFullnessLevel();
		rectum.fillWithEggs(eggs);
		colon.fillWithEggs(rectum.getEggOverflow());
		if (eggs > 0) belly.setAnimation(0, fullness + "toegg", false);
	}	
	protected void receiveSex(SexualExperience sex) { sphincter.receiveSex(sex); }
	protected void tick(int time) {
		sphincter.tick(time); 
		if (rectum.getFullnessAmount() > 0 && rectum.notPlugged()) {
			sphincter.modWetness(1);
		}
	}
	
	protected int getFullnessAmount() { return rectum.getFullnessAmount() + colon.getFullnessAmount(); } 
	
	protected void togglePlug() { rectum.togglePlug(); }
	
	public Sphincter getSphincter() { return sphincter; }
	public Rectum getRectum() { return rectum; }
	public Colon getColon() { return colon; }
	public void emptyEggs() { rectum.flushEggs(); colon.flushEggs(); belly.setAnimation(0, "eggto1", false); }
	public void setBelly(AnimatedActor belly) { 
		this.belly = belly; 
		belly.setAnimation(0, rectum.getEggs() > 0 ? "egg" : "" + getFullnessLevel(), false);
	}
}

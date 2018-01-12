package com.majalis.character;

import com.majalis.character.AbstractCharacter.PhallusType;

public class SexualExperience {

	private final int analSexTop;
	private final int oralSexTop;
	private final int ejaculationInButt;
	private final int ejaculationInMouth;
	private final int analSex; // anal penetration
	private final int anal;	// anal sex post-penetration
	private final int creampies; // anal creampie
	private final int analEjaculation; // ejaculation from receiving anal
	private final int oralSex; // oral penetration
	private final int oral; // oral sex post-penetration
	private final int oralCreampies; // receiving oral
	private final int fellatioEjaculation; // ejaculation from receiving oral
	private final int bellyful; // generic fluid, unclear if taken orally or anally
	private final int handy; // handjob start
	private final int assTeasing; // teased by an ass
	private final int assBottomTeasing; // teased with the idea of being assfucked
	private final int mouthTeasing; // teased by a mouth
	private final int mouthBottomTeasing; // teased with the idea of being mouthfucked
	private final boolean horse; // involves an equine cock
	private final boolean ogre; // involves an ogre cock
	private final boolean beast; // involves a beast
	private final boolean bird; // involves an avian
	private final boolean knot; // involves a knot
	private final boolean prostitution; // is an act of prostitution
	
	public static class SexualExperienceBuilder {
		private int analSexTop;
		private int oralSexTop;
		private int analSex;
		private int anal;
		private int creampies;
		private int analEjaculation;
		private int oralSex;
		private int oral;
		private int oralCreampies;
		private int fellatioEjaculation;
		private int assTeasing; // teased by an ass
		private int assBottomTeasing; // teased with the idea of being assfucked
		private int mouthTeasing; // teased by a mouth
		private int mouthBottomTeasing; // teased with the idea of being mouthfucked
		private boolean horse;
		private boolean ogre;
		private boolean bird;
		private boolean knot;
		
		public SexualExperienceBuilder() {}
		
		public SexualExperienceBuilder setAnalSexTop(int analSexTop) {
			this.analSexTop = analSexTop;
			return this;
		}
		
		public SexualExperienceBuilder setOralSexTop(int oralSexTop) {
			this.oralSexTop = oralSexTop;
			return this;
		}
		
		public SexualExperienceBuilder setAnalSex(int analSex) {
			this.analSex = analSex;
			return this;
		}
		
		public SexualExperienceBuilder setAnal(int anal) {
			this.anal = anal;
			return this;
		}
		
		public void setCreampie(int creampies) {
			this.creampies = creampies;
		}
		
		public SexualExperienceBuilder setAnalEjaculations(int num) {
			analEjaculation = num;
			return this;
		}
		
		public SexualExperienceBuilder setOralSex(int num) {
			oralSex = num;
			return this;
		}
		
		public SexualExperienceBuilder setOral(int oral) {
			this.oral = oral;
			return this;
		}
		
		public SexualExperienceBuilder setOralCreampie(int num) {
			oralCreampies = num;
			return this;
		}
		
		public SexualExperienceBuilder setAssTeasing(int assTeasing) {
			this.assTeasing = assTeasing;
			return this;
		}
		
		public SexualExperienceBuilder setAssBottomTeasing(int assBottomTeasing) {
			this.assBottomTeasing = assBottomTeasing;
			return this;
		}
		
		public SexualExperienceBuilder setMouthTeasing(int mouthTeasing) {
			this.mouthTeasing = mouthTeasing;
			return this;
		}
		
		public SexualExperienceBuilder setMouthBottomTeasing(int mouthBottomTeasing) {
			this.mouthBottomTeasing = mouthBottomTeasing;
			return this;
		}
		
		public SexualExperienceBuilder setHorse() {
			horse = true;
			return this;
		}
		
		public SexualExperienceBuilder setOgre() {
			ogre = true;
			return this;
		}
		
		public SexualExperienceBuilder setBird() {
			bird = true;
			return this;
		}
		
		public SexualExperienceBuilder increaseTeasing(int totalPower) {
			if (!isTeasing()) return this;
			assTeasing += assTeasing == 0 ? 0 : totalPower;
			assBottomTeasing += assBottomTeasing == 0 ? 0 : totalPower;
			mouthTeasing += mouthTeasing == 0 ? 0 : totalPower;
			mouthBottomTeasing += mouthBottomTeasing == 0 ? 0 : totalPower; 
			return this;
		}	
		
		public boolean isTeasing() { return assTeasing > 0 || assBottomTeasing > 0 || mouthTeasing > 0 || assBottomTeasing > 0; }
		public int getAssTeasing() { return assTeasing; }
		public int getAssBottomTeasing() { return assBottomTeasing; }
		public int getMouthTeasing() { return mouthTeasing; }
		public int getMouthBottomTeasing() { return mouthBottomTeasing; }	
		
		public SexualExperienceBuilder setPhallusType(PhallusType phallusType) {
			switch(phallusType) {
				case BIRD:
					bird = true;
					break;
				case DOG:
					knot = true;
					break;
				case GIANT:
					ogre = true;
					break;
				case HORSE:
					horse = true;
					break;
				default:
					break;
			}
			
			return this;
		}
		
		public void combine(SexualExperienceBuilder addedBuilder) {
			this.analSexTop += addedBuilder.analSexTop;
			this.oralSexTop += addedBuilder.oralSexTop;
			this.analSex += addedBuilder.analSex;
			this.anal += addedBuilder.anal;
			this.creampies += addedBuilder.creampies;
			this.analEjaculation += addedBuilder.analEjaculation;
			this.oralSex += addedBuilder.oralSex;
			this.oral += addedBuilder.oral;
			this.oralCreampies += addedBuilder.oralCreampies;
			this.fellatioEjaculation += addedBuilder.fellatioEjaculation;
			this.assTeasing += addedBuilder.assTeasing;
			this.assBottomTeasing += addedBuilder.assBottomTeasing;
			this.mouthTeasing += addedBuilder.mouthTeasing;
			this.mouthBottomTeasing += addedBuilder.mouthBottomTeasing;
			this.horse = this.horse || addedBuilder.horse;
			this.horse = this.ogre || addedBuilder.ogre;
			this.horse = this.bird || addedBuilder.bird;
			this.horse = this.knot || addedBuilder.knot;
		}		
		
		public SexualExperience build() {
			return new SexualExperience(analSexTop, oralSexTop, 0, 0, analSex, anal, creampies, analEjaculation, oralSex, oral, oralCreampies, fellatioEjaculation, 0, 0, assTeasing, assBottomTeasing, mouthTeasing, mouthBottomTeasing, horse, ogre, false, false, bird, knot);
		}
	}
	
	protected SexualExperience() { this(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, false, false, false, false, false, false); }
	
	private SexualExperience(int analSexTop, int oralSexTop, int ejaculationInButt, int ejaculationInMouth, int analSex, int anal, int creampies, int analEjaculation, int oralSex, int oral, int oralCreampies, int fellatioEjaculation, int bellyful, int handy, int assTeasing, int assBottomTeasing, int mouthTeasing, int mouthBottomTeasing,
		boolean horse, boolean ogre, boolean prostitution, boolean beast, boolean bird, boolean knot) {
		this.analSexTop = analSexTop;
		this.oralSexTop = oralSexTop;
		this.ejaculationInButt = ejaculationInButt;
		this.ejaculationInMouth = ejaculationInMouth;		
		this.analSex = analSex;
		this.anal = anal;
		this.creampies = creampies;
		this.analEjaculation = analEjaculation;
		this.oralSex = oralSex;
		this.oral = oral;
		this.oralCreampies = oralCreampies;
		this.fellatioEjaculation = fellatioEjaculation;
		this.assTeasing = assTeasing;
		this.assBottomTeasing = assBottomTeasing;
		this.mouthTeasing = mouthTeasing;
		this.mouthBottomTeasing = mouthBottomTeasing;
		this.bellyful = bellyful;
		this.handy = handy;
		this.horse = horse;
		this.ogre = ogre;
		this.prostitution = prostitution;
		this.beast = beast;
		this.bird = bird;
		this.knot = knot;
	}
	
	protected int getAnalSexTop() { return analSexTop; }
	protected int getOralSexTop() { return oralSexTop; }
	protected int getEjaculationInButt() { return ejaculationInButt; }
	protected int getEjaculationInMouth() { return ejaculationInMouth; }
	protected int getAnalSex() { return analSex; }
	protected int getAnal() { return anal; }
	protected int getCreampies() { return creampies; }
	protected int getAnalEjaculations() { return analEjaculation; }
	protected int getOralSex() { return oralSex; }
	protected int getOral() { return oral; }
	protected int getOralCreampies() { return oralCreampies; }
	protected int getFellatioEjaculations() { return fellatioEjaculation; }
	protected int getBellyful() { return bellyful; }
	protected int getHandy() { return handy; }
	protected int getAssTeasing() { return assTeasing; }
	protected int getAssBottomTeasing() { return assBottomTeasing; }
	protected int getMouthTeasing() { return mouthTeasing; }
	protected int getMouthBottomTeasing() { return mouthBottomTeasing; }
	protected boolean isCentaurSex() { return horse; }
	protected boolean isOgreSex() { return ogre; }
	protected boolean isProstitution() { return prostitution; }
	protected boolean isBeast() { return beast; }
	protected boolean isBird() { return bird; }
	protected boolean isKnot() { return knot; }
	protected boolean isSex() { return analSexTop + oralSexTop + analSex + anal + creampies + analEjaculation + oralSex + oral + oralCreampies + fellatioEjaculation + bellyful + handy > 0; }
	protected boolean isTeasing() { return assTeasing + assBottomTeasing + mouthTeasing + assBottomTeasing > 0; }
	protected boolean isEmpty() { return !(isSex() || isTeasing()); }
	@Override
	public String toString() { 
		return "Anal sex top: " + analSexTop + "\n" + "Oral sex top: " + oralSexTop + "\n" + "Anal sex: " + analSex + "\n" + "Anal: " + anal + "\n" + "Creampies: " + creampies + "\n" + "Anal ejaculation: " + analEjaculation + "\n" + "Oral sex: " + oralSex + "\n" + "Oral: " + oral + "\n" +
				"Oral creampies: " + oralCreampies + "\n" + "Fellatio ejaculation: " + fellatioEjaculation + "\n" + "Bellyful: " + bellyful + "\n" + "Handy: " + handy + "\n" + "Ass teasing: " + assTeasing + "\n" + "Ass Bottom teasing " + assBottomTeasing + "\n" + 
				"Mouth teasing: " + mouthTeasing + "\n" + "Mouth bottom teasing: " + mouthBottomTeasing + "\n";
	}
}

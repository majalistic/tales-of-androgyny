package com.majalis.character;

public class SexualExperience {

	private final int analSex;
	private final int creampies;
	private final int analEjaculation;
	private final int oralSex;
	private final int oralCreampies;
	private final int fellatioEjaculation;
	private final boolean horse;
	private final boolean ogre;
	
	public static class SexualExperienceBuilder {
		
		private int analSex;
		private int creampies;
		private int analEjaculation;
		private int oralSex;
		private int oralCreampies;
		private int fellatioEjaculation;
		private boolean horse;
		private boolean ogre;
		
		public SexualExperienceBuilder() {
			this (0);
		}
		
		public SexualExperienceBuilder(int anal) {
			this.analSex = anal;
			horse = false;
		}

		public SexualExperienceBuilder setAnalSex(int anal, int analCreampies, int analEjaculation) {
			this.analSex = anal;
			this.creampies = analCreampies;
			this.analEjaculation = analEjaculation;
			return this;
		}
		
		public SexualExperienceBuilder setAnalEjaculations(int num) {
			analEjaculation = num;
			return this;
		}
		
		public SexualExperienceBuilder setOralSex(int num) {
			oralSex = num;
			return this;
		}
		
		public SexualExperienceBuilder setOralCreampie(int num) {
			oralCreampies = num;
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
		
		public SexualExperience build() {
			return new SexualExperience(analSex, creampies, analEjaculation, oralSex, oralCreampies, fellatioEjaculation, horse, ogre);
		}		
	}
	
	private SexualExperience() { this(0, 0, 0, 0, 0, 0, false, false); }
	
	private SexualExperience(int analSex, int creampies, int analEjaculation, int oralSex, int oralCreampies, int fellatioEjaculation, boolean horse, boolean ogre) {
		this.analSex = analSex;
		this.creampies = creampies;
		this.analEjaculation = analEjaculation;
		this.oralSex = oralSex;
		this.oralCreampies = oralCreampies;
		this.fellatioEjaculation = fellatioEjaculation;
		this.horse = horse;
		this.ogre = ogre;
	}
	
	protected int getAnalSex() { return analSex; }
	protected int getCreampies() { return creampies; }
	protected int getAnalEjaculations() { return analEjaculation; }
	protected int getOralSex() { return oralSex; }
	protected int getOralCreampies() { return oralCreampies; }
	protected int getFellatioEjaculations() { return fellatioEjaculation; }
	protected boolean isCentaurSex() { return horse; }
	protected boolean isOgreSex() { return ogre; }
	
}

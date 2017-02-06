package com.majalis.character;

public class SexualExperience {

	private final int analSex;
	private final int creampies;
	private final int analEjaculation;
	private final int oralSex;
	private final int oralCreampies;
	private final int fellatioEjaculation;
	private final boolean horse;
	
	public static class SexualExperienceBuilder {
		
		private int analSex;
		private int creampies;
		private int analEjaculation;
		private int oralSex;
		private int oralCreampies;
		private int fellatioEjaculation;
		private boolean horse;
		
		public SexualExperienceBuilder() {
			this (0);
		}
		
		public SexualExperienceBuilder(int anal) {
			this.analSex = anal;
			horse = false;
		}

		public SexualExperienceBuilder setAnalSex(int anal, int analCreampies, int analEjaculation) {
			this.analSex = anal;
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
		
		public SexualExperience build() {
			return new SexualExperience(analSex, creampies, analEjaculation, oralSex, oralCreampies, fellatioEjaculation, horse);
		}		
	}
	
	private SexualExperience(int analSex, int creampies, int analEjaculation, int oralSex, int oralCreampies, int fellatioEjaculation, boolean horse) {
		this.analSex = analSex;
		this.creampies = creampies;
		this.analEjaculation = analEjaculation;
		this.oralSex = oralSex;
		this.oralCreampies = oralCreampies;
		this.fellatioEjaculation = fellatioEjaculation;
		this.horse = horse;
	}
	
	protected int getAnalSex() { return analSex; }
	protected int getCreampies() { return creampies; }
	protected int getAnalEjaculations() { return analEjaculation; }
	protected int getOralSex() { return oralSex; }
	protected int getOralCreampies() { return oralCreampies; }
	protected int getFellatioEjaculations() { return fellatioEjaculation; }
	protected boolean isCentaurSex() { return horse; }
	
}

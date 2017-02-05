package com.majalis.character;

public class SexualExperience {

	private final int analSex;
	private final int creampies;
	private final int analEjaculation;
	private final int oralSex;
	private final int oralCreampies;
	private final int fellatioEjaculation;
	
	public static class SexualExperienceBuilder {
		
		private int analSex;
		private int creampies;
		private int analEjaculation;
		private int oralSex;
		private int oralCreampies;
		private int fellatioEjaculation;
		
		public SexualExperienceBuilder() {}
		
		public SexualExperienceBuilder(int anal) {
			this.analSex = anal;
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
		
		public SexualExperience build() {
			return new SexualExperience(analSex, creampies, analEjaculation, oralSex, oralCreampies, fellatioEjaculation);
		}		
	}
	
	private SexualExperience(int analSex, int creampies, int analEjaculation, int oralSex, int oralCreampies, int fellatioEjaculation) {
		this.analSex = analSex;
		this.creampies = creampies;
		this.analEjaculation = analEjaculation;
		this.oralSex = oralSex;
		this.oralCreampies = oralCreampies;
		this.fellatioEjaculation = fellatioEjaculation;
	}
	
	public int getAnalSex() { return analSex; }
	public int getCreampies() { return creampies; }
	public int getAnalEjaculations() { return analEjaculation; }
	public int getOralSex() { return oralSex; }
	public int getOralCreampies() { return oralCreampies; }
	public int getFellatioEjaculations() { return fellatioEjaculation; }
	
}

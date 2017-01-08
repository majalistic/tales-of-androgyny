package com.majalis.character;

public class SexualExperience {

	private final int analSex;
	private final int creampies;
	private final int oralSex;
	private final int oralCreampies;
	
	public static class SexualExperienceBuilder {
		
		private int analSex;
		private int creampies;
		private int oralSex;
		private int oralCreampies;
		
		public SexualExperienceBuilder() {
			this.analSex = 1;
			this.creampies = 1;
		}
		
		public SexualExperience build() {
			return new SexualExperience(analSex, creampies, oralSex, oralCreampies);
		}
		
	}
	
	private SexualExperience(int analSex, int creampies, int oralSex, int oralCreampies) {
		this.analSex = analSex;
		this.creampies = creampies;
		this.oralSex = oralSex;
		this.oralCreampies = oralCreampies;
	}
	
	public int getAnalSex() { return analSex; }
	public int getCreampies() { return creampies; }
	public int getOralSex() { return oralSex; }
	public int getOralCreampies() { return oralCreampies; }
	
}

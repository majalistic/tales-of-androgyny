package com.majalis.character;


public enum GrappleStatus {
	NULL (""),
	HOLD ("Grapple: Holding"),
	DOMINANT("Grapple: Dominant"),
	ADVANTAGE("Grapple: Advantage"),
	SCRAMBLE("Grapple: Scramble"),
	DISADVANTAGE("Grapple: Disadvantage"),
	SUBMISSION("Grapple: Submission"),
	HELD("Grapple: Held")
	;
	private final String label;
	private GrappleStatus(String label) {
		this.label = label;
	}
	public String getLabel() { return label; }
	
	protected GrappleStatus inverse() {
		return this == NULL ? NULL : GrappleStatus.values()[((GrappleStatus.values().length - 1) - this.ordinal()) + 1];
	}
	
	protected boolean isAdvantage() {
		return this != NULL && this.ordinal() < 4;
	}
		
	protected boolean isDisadvantage() {
		return this.ordinal() > 4;
	}
	
	protected GrappleStatus modifyGrappleStatus(GrappleType grapple) {
		switch (grapple) {
			case ADVANTAGE:
				return this == NULL ? SCRAMBLE : GrappleStatus.values()[Math.max(1, this.ordinal() - 1)];
			case PIN:
				return HOLD;
			case REVERSAL:
				return this == NULL ? SCRAMBLE : GrappleStatus.values()[Math.max(1, this.ordinal() - 3)];
			case BREAK:
				return NULL;
			case HOLD:
			case SUBMIT:
			default:
				return this == NULL ? SCRAMBLE : this;
		}
	}
}

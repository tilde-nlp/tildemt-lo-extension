package com.tilde.mt.lotranslator.models;

public class ErrorResult {
	public Object Result = null;
	public TildeMTDocTranslateState Error = null;
	
	@Override
	public String toString() {
		return String.format("%s [Result: %s, Error: %s]", this.getClass().getSimpleName(), Result, Error);
	}
}

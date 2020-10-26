package com.tilde.mt.lotranslator.models;

public class TildeMTDocTranslateState {
	public String ErrorCode;
	public String ErrorMessage;
	
    public String Filename;
    public String Id;
    public int Segments;
    public int Size;
    public String Status;
    public String System;
    public int TranslatedSegments;
    public String WarningsString;
    
	@Override
	public String toString() {
		return String.format("%s [Code: %s, Message: %s, Status: %s]", this.getClass().getSimpleName(), ErrorCode, ErrorMessage, Status);
	}
}

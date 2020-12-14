package com.tilde.mt.lotranslator.models;

/**
 * Document translation result
 * @author guntars.puzulis
 *
 */
public class TildeMTDocTranslateState extends TildeMTError{
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

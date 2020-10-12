package com.tilde.mt.lotranslator.models;

public class TildeMTSystemLanguage {
	
	private String Code;
    private TildeMTSystemLocalizedText Name;

    public String getCode ()
    {
        return Code;
    }
    public TildeMTSystemLocalizedText getName ()
    {
        return Name;
    }

    @Override
    public String toString()
    {
        return String.format("%s [Code: %s Name: %s]", this.getClass().getName(), Code, Name);
    }
}

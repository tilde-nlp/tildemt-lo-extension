package com.tilde.mt.lotranslator.models;

public class TildeMTSystemLocalizedText {
	
	private String Language;
    private String Text;

    public String getLanguage ()
    {
        return Language;
    }
    public String getText ()
    {
        return Text;
    }

    @Override
    public String toString()
    {
        return String.format("%s [Language: %s, Text: %s]", this.getClass().getSimpleName(), Language, Text);
    }
}

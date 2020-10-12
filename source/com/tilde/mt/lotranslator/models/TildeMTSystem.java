package com.tilde.mt.lotranslator.models;

public class TildeMTSystem {
	
	private TildeMTSystemLanguage SourceLanguage;
	private TildeMTSystemLanguage TargetLanguage;
    private TildeMTSystemLocalizedText Description;
    private TildeMTSystemMetadata[] Metadata;
    private TildeMTSystemLocalizedText Title;
    private String ID;
    private String Domain;
    
    public TildeMTSystemLanguage getSourceLanguage ()
    {
        return SourceLanguage;
    }
    public TildeMTSystemLocalizedText getDescription ()
    {
        return Description;
    }
    public TildeMTSystemMetadata[] getMetadata ()
    {
        return Metadata;
    }
    public TildeMTSystemLocalizedText getTitle ()
    {
        return Title;
    }
    public String getID ()
    {
        return ID;
    }
    public String getDomain ()
    {
        return Domain;
    }
    public TildeMTSystemLanguage getTargetLanguage ()
    {
        return TargetLanguage;
    }

    @Override
    public String toString()
    {
        return String.format("%s [Title: %s, ID: %s, SourceLang: %s, TargetLang: %s]", this.getClass().getName(), ID, SourceLanguage, TargetLanguage);
    }
}

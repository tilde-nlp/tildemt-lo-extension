package com.tilde.mt.lotranslator.models;

import java.util.HashMap;

public class TildeMTSystem {
	
	private TildeMTSystemLanguage SourceLanguage;
	private TildeMTSystemLanguage TargetLanguage;
    private TildeMTSystemLocalizedText Description;
    private TildeMTSystemMetadata[] Metadata;
    private TildeMTSystemLocalizedText Title;
    private String ID;
    private String Domain;
    
    private HashMap<String, String> MetadataCache = null;
    
    public TildeMTSystemLanguage getSourceLanguage ()
    {
        return SourceLanguage;
    }
    public TildeMTSystemLocalizedText getDescription ()
    {
        return Description;
    }
    public HashMap<String, String> getMetadata ()
    {
    	if(MetadataCache == null) {
	    	HashMap<String, String> metadata = new HashMap<String, String>();
	    	for (int i = 0; i < Metadata.length; i++) {
	    		metadata.put(Metadata[i].getKey(), Metadata[i].getValue());
	    	}
	    	MetadataCache = metadata;
    	}
    	return MetadataCache;
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
    
    /**
     * System is available to use
     * @return
     */
    public Boolean IsAvailable() {
    	HashMap<String, String> metadata = this.getMetadata();
    	
    	if(metadata.containsKey("status")) {
    		String status = metadata.get("status");
    		if(status.equals("running")) {
    			return true;
    		}
    	}
    	return false;
    }
    @Override
    public String toString()
    {
        return String.format("%s [ID: %s, SourceLang: %s, TargetLang: %s]", this.getClass().getSimpleName(), ID, SourceLanguage, TargetLanguage);
    }
}

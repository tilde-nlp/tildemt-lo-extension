package com.tilde.mt.lotranslator.models;

public class TildeMTSystemMetadata {
	
	private String Value;
    private String Key;

    public String getValue ()
    {
        return Value;
    }
    public String getKey ()
    {
        return Key;
    }
    
    @Override
    public String toString()
    {
        return String.format("%s [Key: %s Value: %s]", this.getClass().getName(), Key, Value);
	}
}

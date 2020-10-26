package com.tilde.mt.lotranslator.models;

import com.google.gson.annotations.SerializedName;

public class TildeMTStartDocTranslate {
	@SerializedName("appID")
	public String AppID;
	
	@SerializedName("fileName")
    public String FileName;
	
	@SerializedName("content")
    public short[] Content;
	
	@SerializedName("systemID")
    public String SystemID;
	
	@Override
	public String toString() {
		return String.format("%s [File: %s MT System: %s]", this.getClass().getSimpleName(), FileName, SystemID);
	}
}

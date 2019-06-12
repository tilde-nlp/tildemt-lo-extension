package org.libreoffice.example.helper.LetsMT;

public class TranslatePayloadM {
	private String systemID;
	private String text;

	public TranslatePayloadM (String systemID, String text) {
		this.systemID = systemID;
		this.text = text;
	}

	public String getSystemID () {
		return systemID;
	}

	public void setSystemID (String systemID) {
		this.systemID = systemID;
	}

	public String getText () {
		return text;
	}

	public void setText (String text) {
		this.text = text;
	}
}

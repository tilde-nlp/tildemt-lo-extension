package com.tilde.mt.lotranslator.dialog;

import com.sun.star.uno.XComponentContext;
import com.tilde.mt.lotranslator.TildeMTClient;
import com.tilde.mt.lotranslator.helper.ContentHelper;

public class ActionReplace {

	/** Translate dialog */
	private XComponentContext xContext;
	private static com.sun.star.text.XTextViewCursor xTextViewCursor;
	private TildeMTClient apiClient;
	
	public ActionReplace(XComponentContext xContext, TildeMTClient apiClient) {
		this.xContext = xContext;
		this.apiClient = apiClient;
	}
	
	/**
	 * If translated text is not empty, replace it with the translation.
	 * Clean the variable that contains the translation.
	 */
	public void process(String systemID) {
		String translated = ContentHelper.combineTranslatedParagraphs(this.xContext, apiClient, systemID);
		if(translated.length() > 1) {
			translated = translated.substring(1); //remove unnecessary "\n" at the beginning
			replace(translated);
		}
	}

	/**
	 * Replace selected text with it's translation
	 *
	 * @param translation	String containing all translated text
	 */
	private void replace(String translation) {
		xTextViewCursor.setString(translation);
	}
}

package com.tilde.mt.lotranslator.dialog;

import com.sun.star.text.XTextViewCursor;
import com.sun.star.uno.XComponentContext;
import com.tilde.mt.lotranslator.TildeMTClient;
import com.tilde.mt.lotranslator.helper.ContentHelper;
import com.tilde.mt.lotranslator.helper.DocumentHelper;

public class ActionReplace {

	/** Translate dialog */
	private XComponentContext xContext;
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
		com.sun.star.text.XTextDocument xTextDoc = DocumentHelper.getCurrentDocument(xContext);
		com.sun.star.frame.XController xController = xTextDoc.getCurrentController();
		com.sun.star.text.XTextViewCursorSupplier xTextViewCursorSupplier = DocumentHelper.getCursorSupplier(xController);
		XTextViewCursor xTextViewCursor = xTextViewCursorSupplier.getViewCursor();
		
		xTextViewCursor.setString(translation);
	}
}

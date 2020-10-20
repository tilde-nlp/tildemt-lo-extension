package com.tilde.mt.lotranslator.dialog;

import com.sun.star.text.XTextViewCursor;
import com.sun.star.uno.XComponentContext;
import com.tilde.mt.lotranslator.TildeMTClient;
import com.tilde.mt.lotranslator.helper.ContentHelper;
import com.tilde.mt.lotranslator.helper.DocumentHelper;

public class ActionAppend {

	/** Translate dialog */
	private XComponentContext xContext;
	private TildeMTClient apiClient;

	public ActionAppend(XComponentContext xContext, TildeMTClient apiClient) {
		this.xContext = xContext;
		this.apiClient = apiClient;
	}
	
	/**
	 * Append translation to the end of selected area.
	 * Clean the variable that contains the translation.
	 */
	public void process(String systemID) {
		String translation = ContentHelper.combineTranslatedParagraphs(this.xContext, this.apiClient, systemID);
		append(translation);
	}

	/**
	 * Moves cursor to the end of selection to insert translation there.
	 *
	 * @param translation	String containing translated text
	 */
	private void append(String translation) {
		com.sun.star.text.XTextDocument xTextDoc = DocumentHelper.getCurrentDocument(xContext);
		com.sun.star.frame.XController xController = xTextDoc.getCurrentController();
		com.sun.star.text.XTextViewCursorSupplier xTextViewCursorSupplier = DocumentHelper.getCursorSupplier(xController);
		XTextViewCursor xTextViewCursor = xTextViewCursorSupplier.getViewCursor();
		
		xTextViewCursor.collapseToEnd();
		xTextViewCursor.setString(translation);
	}
}

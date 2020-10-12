package com.tilde.mt.lotranslator.dialog;

import com.sun.star.uno.XComponentContext;
import com.tilde.mt.lotranslator.TildeMTClient;
import com.tilde.mt.lotranslator.helper.ContentHelper;

public class ActionAppend {

	/** Translate dialog */
	private XComponentContext xContext;
	private static com.sun.star.text.XTextViewCursor xTextViewCursor;
	private ContentHelper contentHelper;
	private TildeMTClient apiClient;

	public ActionAppend(XComponentContext xContext, TildeMTClient apiClient) {
		this.xContext = xContext;
		this.apiClient = apiClient;
		this.contentHelper = new ContentHelper(this.xContext);
	}
	
	/**
	 * Append translation to the end of selected area.
	 * Clean the variable that contains the translation.
	 */
	public void process() {
		String translation = this.contentHelper.combineTranslatedParagraphs(this.apiClient);
		append(translation);
	}

	/**
	 * Moves cursor to the end of selection to insert translation there.
	 *
	 * @param translation	String containing translated text
	 */
	private void append(String translation) {
		xTextViewCursor.collapseToEnd();
		xTextViewCursor.setString(translation);
	}
}

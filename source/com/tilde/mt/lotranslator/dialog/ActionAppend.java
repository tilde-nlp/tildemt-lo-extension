package com.tilde.mt.lotranslator.dialog;

import com.sun.star.text.XTextViewCursor;
import com.sun.star.uno.XComponentContext;
import com.tilde.mt.lotranslator.TildeMTClient;
import com.tilde.mt.lotranslator.helper.DocumentHelper;

/**
 * Action for direct translation and insertion of translation result after selected text in document
 * @author guntars.puzulis
 *
 */
public class ActionAppend extends ActionTranslateWithProgress {
	private XComponentContext xContext;

	public ActionAppend(XComponentContext xContext, TildeMTClient apiClient, String systemID) {
		super(xContext, apiClient, systemID);
		
		this.xContext = xContext;
	}

	@Override
	public void onProgressResult(String seperator, String[] translation) {
		String wholeTranslation = String.join(seperator, translation);
		
		com.sun.star.text.XTextDocument xTextDoc = DocumentHelper.getCurrentDocument(xContext);
		com.sun.star.frame.XController xController = xTextDoc.getCurrentController();
		com.sun.star.text.XTextViewCursorSupplier xTextViewCursorSupplier = DocumentHelper.getCursorSupplier(xController);
		XTextViewCursor xTextViewCursor = xTextViewCursorSupplier.getViewCursor();
		
		xTextViewCursor.collapseToEnd();
		xTextViewCursor.setString(wholeTranslation);
	}
}

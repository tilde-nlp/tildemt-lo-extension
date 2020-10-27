package com.tilde.mt.lotranslator.dialog;

import com.sun.star.text.XTextViewCursor;
import com.sun.star.uno.XComponentContext;
import com.tilde.mt.lotranslator.TildeMTClient;
import com.tilde.mt.lotranslator.helper.DocumentHelper;

/**
 * Action for direct translation and substitution of selected text with translation result in document
 * @author guntars.puzulis
 *
 */
public class ActionReplace extends ActionTranslateWithProgress{

	private XComponentContext xContext;
	
	public ActionReplace(XComponentContext xContext, TildeMTClient apiClient, String systemID) {
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
		
		xTextViewCursor.setString(wholeTranslation);
	}
}

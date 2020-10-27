package com.tilde.mt.lotranslator.helper;

import com.sun.star.beans.XPropertySet;
import com.sun.star.lang.Locale;
import com.sun.star.text.XTextCursor;
import com.sun.star.text.XTextViewCursor;
import com.sun.star.uno.XComponentContext;
import com.tilde.mt.lotranslator.Logger;
import com.tilde.mt.lotranslator.models.SelectedText;

public class ContentHelper {
	private static Logger logger = new Logger(ContentHelper.class.getName());
	/**
	 * Gets the user translatable input text
	 *
	 * @return	String that user wrote in dialog's translation box
	 */
	public static SelectedText getSelectedText(XComponentContext xContext) {
		SelectedText selection = new SelectedText();

		com.sun.star.text.XTextDocument xTextDoc = DocumentHelper.getCurrentDocument(xContext);
		com.sun.star.frame.XController xController = xTextDoc.getCurrentController();
		com.sun.star.text.XTextViewCursorSupplier xTextViewCursorSupplier = DocumentHelper.getCursorSupplier(xController);
		XTextViewCursor xTextViewCursor = xTextViewCursorSupplier.getViewCursor();

		// if there are multiple languages in text, select first one?
		XTextCursor startCursor = xTextViewCursor.getText().createTextCursorByRange(xTextViewCursor.getStart());
		XPropertySet xCursorProps = DocumentHelper.getPageCursorProps(startCursor);
		
		selection.Text = xTextViewCursor.getString();
		try {
			Object rawLocale = xCursorProps.getPropertyValue("CharLocale");
			Locale charLocale = (Locale) rawLocale;
			selection.Locale = charLocale;
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		logger.info(String.format("Selection: %s", selection));
		
		return selection;
	}

	public static String getTextNewlineType(String text) {
		String newLine = "\n";
		if(text.indexOf("\r\n") > -1) {
			newLine = "\r\n";
		}
		
		return newLine;
	}
}

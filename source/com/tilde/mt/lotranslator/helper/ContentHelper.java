package com.tilde.mt.lotranslator.helper;


import java.util.ArrayList;

import com.sun.star.beans.UnknownPropertyException;
import com.sun.star.beans.XPropertySet;
import com.sun.star.lang.Locale;
import com.sun.star.lang.WrappedTargetException;
import com.sun.star.text.XTextCursor;
import com.sun.star.text.XTextViewCursor;
import com.sun.star.uno.XComponentContext;
import com.tilde.mt.lotranslator.Logger;
import com.tilde.mt.lotranslator.TildeMTClient;
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

	/**
	 * To avoid errors from "\n" characters in translatable text,
	 * each paragraph is translated separately.
	 * To make insertion of translated text as one step for user,
	 * all translated paragraphs are combined in a single variable.
	 */
	public static String combineTranslatedParagraphs(XComponentContext xContext, TildeMTClient apiClient, String systemID)  {
		ArrayList<String> result = new ArrayList<String>();
		
		String selectedText = ContentHelper.getSelectedText(xContext).Text;
		String newLine = "\n";
		if(selectedText.indexOf("\r\n") > -1) {
			newLine = "\r\n";
		}
		
		if(selectedText.length() > 0) {
			String paragraphs[] = selectedText.split("\\r?\\n");
			int paragraphLength = paragraphs.length;
			
			for(int i = 0; i != paragraphLength; i++) {
				String translation = "";
				try {
					translation = apiClient.Translate(systemID, paragraphs[i]).get();
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				result.add(translation);
			}
		} 
		
		return String.join(newLine, result);
	}
}

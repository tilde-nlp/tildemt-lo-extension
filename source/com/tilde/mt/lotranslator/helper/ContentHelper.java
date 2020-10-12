package com.tilde.mt.lotranslator.helper;


import com.sun.star.uno.XComponentContext;
import com.tilde.mt.lotranslator.Configuration;
import com.tilde.mt.lotranslator.TildeMTClient;

public class ContentHelper {

	private XComponentContext xContext;
	/** Cursor in the document */
	private static com.sun.star.text.XTextViewCursor xTextViewCursor;
	/** Contains all translation paragraphs */
	

	/**
	 * @param xContext
	 */
	public ContentHelper (XComponentContext xContext) {
		this.xContext = xContext;
	}

	/**
	 * Gets the user translatable input text
	 *
	 * @return	String that user wrote in dialog's translation box
	 */
	private String getSelectedText() {
		com.sun.star.text.XTextDocument xTextDoc = DocumentHelper.getCurrentDocument(xContext);
		com.sun.star.frame.XController xController = xTextDoc.getCurrentController();
		com.sun.star.text.XTextViewCursorSupplier xTextViewCursorSupplier = DocumentHelper.getCursorSupplier(xController);
		xTextViewCursor = xTextViewCursorSupplier.getViewCursor();

		return xTextViewCursor.getString();
	}

	/**
	 * To avoid errors from "\n" characters in translatable text,
	 * each paragraph is translated separately.
	 * To make insertion of translated text as one step for user,
	 * all translated paragraphs are combined in a single variable.
	 *
	 * @throws Exception	if getting translation failed
	 */
	public String combineTranslatedParagraphs(TildeMTClient apiClient)  {
		String result = "";
		
		String selectedText = getSelectedText();
		String smt = Configuration.getSystemID();
		String translation = null;
		if(selectedText.length() > 0) {
			String paragraphs[] = selectedText.split("\\r?\\n");
			int paralength = paragraphs.length;
			// translate each paragraph separately
			for(int i = 0; i != paralength; i++) {
				translation = apiClient.translate(smt, paragraphs[i]);
				if(translation.length() > 0) {
					combine(result, paralength, i, translation);
				}
			}
		} 
		
		return result;
	}

	/**
	 * Combine() changes combination logic based on how many paragraphs are translated.
	 *   If only one paragraph is translated, put a single space before.
	 *   If more than one paragraph is translated, start new paragraph
	 *     and then split translated paragraphs too.
	 *
	 * @param paragraphCount			total paragraph count
	 * @param currentParagraphNumber	current paragraph in the cycle
	 * @param text						paragraph's content
	 */
	private static void combine(String result, int paragraphCount, int currentParagraphNumber, String text){
		if (paragraphCount == 1) {
			result = result.concat(" ");
			result = result.concat(text);
		}
		if (paragraphCount > 1 && currentParagraphNumber == 0) {
			result = result.concat("\n");
		}
		if (paragraphCount > 1) {
			result = result.concat(text);
			result = result.concat("\n");
		}
	}

}

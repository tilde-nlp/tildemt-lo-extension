package org.libreoffice.example.dialog;

import org.libreoffice.example.comp.TildeTranslatorImpl;
import org.libreoffice.example.helper.DocumentHelper;
import org.libreoffice.example.helper.TranslateAPI;

import com.sun.star.uno.XComponentContext;
/**
 * This action translates selected text and
 *  (1)appends it after the selected text if ActionTwo button is pressed or
 *  (2)replaces selected text with it's translation if ActionThree button is pressed.
 * Translation languages are the same as previously used and
 * can be set in ActionOne dialog.
 *
 * @author arta.zena
 */
public class ActionTwoAndThree {

	private XComponentContext xContext;
	/** Cursor in the document */
	private static com.sun.star.text.XTextViewCursor xTextViewCursor;
	/** Contains all translation paragraphs */
	private static String combined = "";

	/**
	 * @param xContext
	 */
	public ActionTwoAndThree (XComponentContext xContext) {
		this.xContext = xContext;
	}

	/**
	 * Append tranlation to the end of selected area.
	 * Clean the variable that contains the translation.
	 *
	 * @throws Exception if getting translation while combining paragraphs failed
	 */
	public void appendAction() throws Exception {
		combineTranslatedParagraphs();
		append(combined);
		combined = "";

	}

	/**
	 * Moves cursour to the end of selection to insert translation there.
	 *
	 * @param translation	String containing translated text
	 */
	private void append(String translation) {
		xTextViewCursor.collapseToEnd();
		xTextViewCursor.setString(translation);
	}

	/**
	 * If translated text is not empty, replace it with the translation.
	 * Clean the variable that contains the translation.
	 *
	 * @throws Exception if getting translation while combining paragraphs failed
	 */
	public void replaceAction() throws Exception {
		combineTranslatedParagraphs();
		if(combined.length() > 1) {
			combined = combined.substring(1); //remove unnecessary "\n" at the beginning
			replace(combined);
			combined = "";
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
	 * each paragraph is translated seperately.
	 * To make insertion of translated text as one step for user,
	 * all translated paragraphs are combined in a single variable.
	 *
	 * @throws Exception	if getting translation failed
	 */
	private void combineTranslatedParagraphs() throws Exception {
		String selectedText = getSelectedText();
		String smt = TildeTranslatorImpl.getSystemID();
		String clientID = TildeTranslatorImpl.getClientID();
		String translation = null;
		if(selectedText.length() > 0) {
			String paragraphs[] = selectedText.split("\\r?\\n");
			int paralength = paragraphs.length;
			// translate each paragraph seperately
			for(int i = 0; i != paralength; i++) {
				translation = TranslateAPI.translate(clientID, smt, paragraphs[i]);
				if(translation.length() > 0) {
					combine(paralength, i, translation);
				}
			}
		} else {
			combined = "";
		}
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
	private static void combine(int paragraphCount, int currentParagraphNumber, String text){
		if (paragraphCount == 1) {
			combined = combined.concat(" ");
			combined = combined.concat(text);
		}
		if (paragraphCount > 1 && currentParagraphNumber == 0) {
			combined = combined.concat("\n");
		}
		if (paragraphCount > 1) {
			combined = combined.concat(text);
			combined = combined.concat("\n");
		}
	}

}

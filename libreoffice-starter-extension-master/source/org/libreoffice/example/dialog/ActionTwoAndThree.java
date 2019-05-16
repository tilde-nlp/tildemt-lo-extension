package org.libreoffice.example.dialog;

import org.libreoffice.example.helper.DocumentHelper;

import com.sun.star.uno.XComponentContext;
/**
 * This action translates selected text and
 *  (1)inserts it after the selected text if ActionTwo button is pressed or
 *  (2)replaces selected text with it's translation if ActionThree button is pressed.
 * Translation languages are the same as previously used and
 * can be set in ActionOne dialog.
 */
public class ActionTwoAndThree {

	private XComponentContext xContext;
	private static com.sun.star.text.XTextViewCursor xTextViewCursor;
	private static String combined = "";

	public ActionTwoAndThree (XComponentContext xContext) {
		this.xContext = xContext;
	}

	public void insertAction() throws Exception {
		combineTranslatedParagraphs();
		insertAfter(combined);
		combined = "";

	}

	private void insertAfter(String translation) {
		xTextViewCursor.collapseToEnd();
		xTextViewCursor.setString(translation);
	}

	public void replaceAction() throws Exception {
		combineTranslatedParagraphs();
		if(combined.length() > 1) {
			combined = combined.substring(1); //remove unnecessary "\n" at the beginning
			replace(combined);
			combined = "";
		}
	}

	private void replace(String translation) {
		System.out.println("replace with:\t" + translation);
		xTextViewCursor.setString(translation);
	}

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
	 */
	private void combineTranslatedParagraphs() throws Exception {
		String selectedText = getSelectedText();
		if(selectedText.length() > 0) {
			Translate translate = new Translate();
			String translated = null;

			String paragraphs[] = selectedText.split("\\r?\\n");
			int paralength = paragraphs.length;
			for(int i = 0; i != paralength; i++) {
				translated = translate.getTranslation(
						null,
						null,
						paragraphs[i] );
				if(translated.length() > 0) {
					combine(paralength, i, translated);
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

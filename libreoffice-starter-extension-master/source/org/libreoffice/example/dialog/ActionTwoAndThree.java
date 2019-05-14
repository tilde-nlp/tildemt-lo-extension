package org.libreoffice.example.dialog;

import org.libreoffice.example.helper.DocumentHelper;

import com.sun.star.uno.XComponentContext;
/**
 * @author arta.zena
 *
 * This action autmatically translates selected text and inserts it
 * after the selected text. Translation languages are the same as previously used.
 *
 */
public class ActionTwoAndThree {

	private XComponentContext xContext;
	private static com.sun.star.text.XTextViewCursor xTextViewCursor;
	private static String combined = "";

	public ActionTwoAndThree (XComponentContext xContext) {
		System.out.println(">> ActionTwoAndThree constructor");
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
		combined = combined.substring(1); //remove unnecessary "\n" at the beginning
		replace(combined);
		combined = "";

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

	// translates each paragraph seperately, combines them in "combine" variable
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
				combine(paralength, i, translated);
			}
		}
	}

	private static void combine(int full, int now, String par){
		//if only one paragraph is translated, put space before
		if (full == 1) {
			combined = combined.concat(" ");
			combined = combined.concat(par);
		}
		// if more than one paragraph is translated, start new paragraph
		if (full > 1 && now == 0) {
			combined = combined.concat("\n");
		}
		// if more than one paragraph is translated, split translations too
		if (full > 1) {
			combined = combined.concat(par);
			combined = combined.concat("\n");
		}
	}

}

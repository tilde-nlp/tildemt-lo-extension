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

	public ActionTwoAndThree (XComponentContext xContext) {
		System.out.println(">> ActionTwoAndThree constructor");
		this.xContext = xContext;
	}

	public void insertAction() throws Exception {
		String selectedText = getSelectedText();
		if(selectedText != "") {
			Translate translate = new Translate();
			String translated = translate.getTranslation(
				null,
				null,
				selectedText);
			if(translated != "") {
				insertAfter(selectedText, translated);
			}
		}
	}

	private void insertAfter(String selected, String translation) {
		System.out.println("insertAfter:\t" + translation);
		xTextViewCursor.collapseToEnd();
		xTextViewCursor.setString(" " + translation);
	}

	public void replaceAction() throws Exception {
		String selectedText = getSelectedText();
		if(selectedText != "") {
			Translate translate = new Translate();
			String translated = translate.getTranslation(
				null,
				null,
				selectedText);
			if(translated != "") {
				replace(selectedText, translated);
			}
		}
	}

	private void replace(String selected, String translation) {
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

}

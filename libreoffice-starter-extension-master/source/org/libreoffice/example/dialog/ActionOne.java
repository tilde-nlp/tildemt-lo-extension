package org.libreoffice.example.dialog;

import org.libreoffice.example.helper.DialogHelper;
import org.libreoffice.example.helper.DocumentHelper;

import com.sun.star.awt.XDialog;
import com.sun.star.awt.XDialogEventHandler;
import com.sun.star.awt.XListBox;
import com.sun.star.awt.XTextComponent;
import com.sun.star.lang.WrappedTargetException;
import com.sun.star.uno.XComponentContext;

/**
 * This action opens a set up (MT system's ID) and translation dialog.
 * From here user can change translation languages and insert
 * the translated text in the selected area of the text.
 *
 * @author arta.zena
 */

public class ActionOne implements XDialogEventHandler {

	private XDialog dialog;
	private XComponentContext xContext;
	private static final String actionClose = "actionClose";
	private static final String actionTranslate = "translateNow";
	private static final String actionInsert = "insertNow";
	private String[] supportedActions = new String[] { actionClose, actionTranslate, actionInsert };
	private static XTextComponent textFieldFrom;
	private static XTextComponent textFieldTo;
	private static XListBox languageBoxFrom;
	private static XListBox languageBoxTo;
	private static String selectedText = null;

	/**
	 * Constructor.
	 * Creates dialog and sets context.
	 *
	 * @param xContext
	 */
	public ActionOne(XComponentContext xContext) {
		this.dialog = DialogHelper.createDialog("ActionOneDialog.xdl", xContext, this);
		this.xContext = xContext;
	}

	/**
	 * Public method to start dialog.
	 */
	public void show(){
		dialog.execute();
	}

	/**
	 * Save the language selection, close the dialog.
	 */
	private void onCloseButtonPressed() {
		getFields();
		boolean selectedSysExists = setSmtIfSystemExists();
		if(selectedSysExists) {
			dialog.endExecute();
			textFieldTo.setText(""); // Clean memory for insert button
		}
	}

	/**
	 * If selected system exists, sends input text
	 * to the translation class and sets returned
	 * value to appear in output textfield.
	 *
	 * @throws Exception	getting translation failed
	 */
	private void onTranslateButtonPressed() throws Exception {
		getFields();
		boolean selectedSysExists = setSmtIfSystemExists();
		if(selectedSysExists) {
			Translate translate = new Translate(xContext);
			String translated = translate.getTranslation(
					textFieldFrom.getText());

			//pass translated text to the dialog text field
			textFieldTo.setText(translated);
		}
	}

	/**
	 * If translation is not empty,
	 *  get the cursor and insert translated text
	 *  where the it is located in the document
	 * Else do nothing
	 */
	private void onInsertButtonPressed() {
		if (!textFieldTo.getText().equals("")) {
			com.sun.star.text.XTextDocument xTextDoc =
					DocumentHelper.getCurrentDocument(xContext);
			com.sun.star.frame.XController xController =
					xTextDoc.getCurrentController();
			com.sun.star.text.XTextViewCursorSupplier xTextViewCursorSupplier =
					DocumentHelper.getCursorSupplier(xController);
			com.sun.star.text.XTextViewCursor xTextViewCursor =
					xTextViewCursorSupplier.getViewCursor();

			xTextViewCursor.setString(textFieldTo.getText());
		} else {
			System.out.println("Insert:\tnothing to insert");
		}
	}

	/**
	 * Updates variables based on dialog fields user can edit.
	 */
	private void getFields() {
		textFieldFrom = DialogHelper.getEditField( this.dialog, "TextFieldFrom" );
		textFieldTo = DialogHelper.getEditField( this.dialog, "TextFieldTo" );
		languageBoxFrom = DialogHelper.getListBox( this.dialog , "ListBox1");
		languageBoxTo = DialogHelper.getListBox( this.dialog , "ListBox2");

		System.out.println("--------");
		System.out.println("To translate:\t" + textFieldFrom.getText());
		System.out.println("Language from:\t" + languageBoxFrom.getSelectedItem());
		System.out.println("Language to:\t" + languageBoxTo.getSelectedItem());
	}

	/**
	 * Checks whether selected system exists.
	 * Prompts if does not.
	 *
	 * @return 	whether systems exists
	 */
	private boolean setSmtIfSystemExists() {
		//TODO: check if Translate.java is not repeating this
		Translate translate = new Translate(xContext);
		boolean smt_exists = translate.setSmt(
				languageBoxFrom.getSelectedItem(),
				languageBoxTo.getSelectedItem());
		if(!smt_exists) {
			DialogHelper.showInfoMessage(
					xContext,
					dialog,
					"Cannot translate to selected language direction!\nSelect again.");
			return false;
		} else {
			return true;
		}
	}

	@Override
	public boolean callHandlerMethod(XDialog dialog, Object eventObject, String methodName)
			throws WrappedTargetException {
		if (methodName.equals(actionClose)) {
			onCloseButtonPressed();
			return true;
		}
		else if (methodName.equals(actionTranslate)) {
			try {
				onTranslateButtonPressed();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return true;
		}
		else if (methodName.equals(actionInsert)) {
			onInsertButtonPressed();
			return true;
		}
		return false; // Event was not handled
	}

	@Override
	public String[] getSupportedMethodNames() {
		return supportedActions;
	}

}

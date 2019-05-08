package org.libreoffice.example.dialog;

import java.io.IOException;

import org.libreoffice.example.helper.DialogHelper;
import org.libreoffice.example.helper.DocumentHelper;
import org.libreoffice.example.helper.TranslateAPI;

import com.sun.star.awt.XDialog;
import com.sun.star.awt.XDialogEventHandler;
import com.sun.star.awt.XListBox;
import com.sun.star.awt.XTextComponent;
import com.sun.star.lang.WrappedTargetException;
import com.sun.star.uno.XComponentContext;

public class ActionOneDialog implements XDialogEventHandler {

	private XDialog dialog;
	private static final String actionClose = "actionClose";
	private static final String actionTranslate = "translateNow";
	private static final String actionInsert = "insertNow";
	private static final String actionDetails = "showDetails";
	private String[] supportedActions = new String[] { actionClose, actionTranslate, actionInsert, actionDetails };
	private XComponentContext xContext;
	private static XTextComponent textFieldFrom;
	private static XTextComponent textFieldTo;
	private static XListBox languageBoxFrom;
	private static XListBox languageBoxTo;
	private static String clientID = "u-72738618-8461-4ed4-a20b-33031a7ac036"; //TODO: change clientID set up
	private static String selectedText = null;

	public ActionOneDialog(XComponentContext xContext) {
		this.dialog = DialogHelper.createDialog("ActionOneDialog.xdl", xContext, this);
		this.xContext = xContext;
	}

	public void show(){
		dialog.execute();
	}

	private void onCloseButtonPressed() {
		System.out.println("Close button is pressed");
		dialog.endExecute();
		textFieldTo.setText(""); // Clean memory for insert button
		System.out.println("Textfield has been emptied"); //TODO not working in the beginning
	}

	private void onTranslateButtonPressed() throws Exception {
		getFields();

		// Translate selected text if it exists
		String text;
		if(selectedText != null) {
			textFieldFrom.setText(selectedText);
			selectedText = null;
		}

		//get selected MT system ID
		String smt = getSmtID(languageBoxFrom.getSelectedItem(), languageBoxTo.getSelectedItem());
		text = textFieldFrom.getText();
		String translated = translate(smt, text);

		//pass translated text to the dialog
		textFieldTo.setText(translated);
	}

	//insert translated text at the end of the current document
	private void onInsertButtonPressed() {
		if (!textFieldTo.getText().equals("")) {
			com.sun.star.text.XTextDocument xTextDoc = DocumentHelper.getCurrentDocument(xContext);
			com.sun.star.frame.XController xController = xTextDoc.getCurrentController();
			com.sun.star.text.XTextViewCursorSupplier xTextViewCursorSupplier = DocumentHelper.getCursorSupplier(xController);
			com.sun.star.text.XTextViewCursor xTextViewCursor = xTextViewCursorSupplier.getViewCursor();

			xTextViewCursor.setString(textFieldTo.getText());
	        System.out.println("Insert:\t\tdone");
		} else {
			System.out.println("Insert:\tnothing to insert");
		}
	}

	private void getSelectedText() {
		com.sun.star.text.XTextDocument xTextDoc = DocumentHelper.getCurrentDocument(xContext);
		com.sun.star.frame.XController xController = xTextDoc.getCurrentController();
		com.sun.star.text.XTextViewCursorSupplier xTextViewCursorSupplier = DocumentHelper.getCursorSupplier(xController);
		com.sun.star.text.XTextViewCursor xTextViewCursor = xTextViewCursorSupplier.getViewCursor();

		selectedText = xTextViewCursor.getString();
	}

	//updates variables based on dialog fields
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

	//returns MT system's ID
	private String getSmtID (String languageFrom, String languageTo) {
		String smt = "";
		String lv = "Latvian";
		String en = "English";
		if (languageFrom.contentEquals(lv) && languageTo.contentEquals(en)) {
			smt = "smt-9c8cade7-91d9-434d-ae62-8ce69f7223de";
		}
		else if (languageFrom.contentEquals(en) && languageTo.contentEquals(lv)) {
			smt = "smt-16d2a887-317f-4ef4-976b-90bd8c5e1a46";
		}
		System.out.println("System ID:\t" + smt);
//		return smt;
		return "smt-b0b7cc68-1bb3-4a35-a5de-f2f86d4dadf1"; //TODO: remove when getSmtID() is done
	}

	private String translate (String smt, String text) throws Exception {
		String translated = "";
		if (!smt.isEmpty()) {
			try {
				TranslateAPI translator = new TranslateAPI();
				translated = translator.translate(clientID, smt, text);
				System.out.println("translated:\t" + translated);
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Error: translating failed");
			}
		} else {
			System.out.println("translation:\t not translated");
			translated = text; // if requested system does not exist, insert the same text
		}
		return translated;
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
		else if (methodName.equals(actionDetails)) {
			System.out.println(">>> ACTIONONEDIALOG.JAVA GOT THE PANEL <<<");
			return true;
		}

		return false; // Event was not handled
	}

	@Override
	public String[] getSupportedMethodNames() {
		return supportedActions;
	}

}

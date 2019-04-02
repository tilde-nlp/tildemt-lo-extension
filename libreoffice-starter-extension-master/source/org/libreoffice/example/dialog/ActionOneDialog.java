package org.libreoffice.example.dialog;

import java.io.IOException;

import org.libreoffice.example.helper.TranslateAPI;
import org.libreoffice.example.helper.DialogHelper;

import com.sun.star.awt.XDialog;
import com.sun.star.awt.XDialogEventHandler;
import com.sun.star.awt.XListBox;
import com.sun.star.awt.XTextComponent;
import com.sun.star.lang.WrappedTargetException;
import com.sun.star.uno.XComponentContext;

public class ActionOneDialog implements XDialogEventHandler {
	
	private XDialog dialog;
	private static final String actionOk = "actionOk";
	private static final String actionTranslate = "translateNow";
	private String[] supportedActions = new String[] { actionOk, actionTranslate };
	
	public ActionOneDialog(XComponentContext xContext) {
		this.dialog = DialogHelper.createDialog("ActionOneDialog.xdl", xContext, this);
	}

	public void show() {
		dialog.execute();
	}
	
	private void onOkButtonPressed() {
		System.out.println("OK button is pressed");
		dialog.endExecute();
	}
	
	private void onTranslateButtonPressed() {
		//get info from dialog fields
		XTextComponent textFieldFrom = DialogHelper.getEditField( this.dialog, "TextFieldFrom" );
		XTextComponent textFieldTo = DialogHelper.getEditField( this.dialog, "TextFieldTo" );
		XListBox languageBoxFrom = DialogHelper.getListBox( this.dialog , "ListBox1");
		XListBox languageBoxTo = DialogHelper.getListBox( this.dialog , "ListBox2");
		
		System.out.println("To translate:\t" + textFieldFrom.getText());
		System.out.println("Language from:\t" + languageBoxFrom.getSelectedItem());
		System.out.println("Language to:\t" + languageBoxTo.getSelectedItem());
		
		//get MT system ID
		String smt = "";
		smt = getSmtID(languageBoxFrom.getSelectedItem(), languageBoxTo.getSelectedItem());
		System.out.println("System ID:\t" + smt);
		String clientID = null; //TODO: change passing over and set up clientID
		String text = textFieldFrom.getText();
		smt = "smt-b0b7cc68-1bb3-4a35-a5de-f2f86d4dadf1"; //TODO: remove when getSmtID() is done
		
		//translating text ...
		String translated = "";
		textFieldTo.setText("...");
		System.out.println("smt.length():\t" + smt.length());
		System.out.println("smt.isEmpty():\t" + smt.isEmpty());
		if (!smt.isEmpty()) { //!
			try {
//				System.out.println("entered try block...");
				TranslateAPI translator = new TranslateAPI();
//				System.out.println("...and got past \"new TranslateAPI\"");
				translated = translator.connPOST(clientID, smt, text);
			} catch (IOException e) {
//				System.out.println("entered exception block");
				e.printStackTrace();
			}
		} else {
			System.out.println("smt is... \tempty");
			translated = text;
		}
		System.out.println("translated:\t" + translated);
		
		//pass translated text to the dialog
		textFieldTo.setText("--->" + translated + "<---");
//		System.out.println("onTranslateButtonPressed done");
	}
	
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
		return smt;
	}
	
	@Override
	public boolean callHandlerMethod(XDialog dialog, Object eventObject, String methodName) 
			throws WrappedTargetException {
		if (methodName.equals(actionOk)) {
			onOkButtonPressed();
			return true; // Event was handled
		}
		else if (methodName.equals(actionTranslate)) {
			onTranslateButtonPressed();
			return true; // Event was handled
		}
		
		return false; // Event was not handled
	}

	@Override
	public String[] getSupportedMethodNames() {
		return supportedActions;
	}

}

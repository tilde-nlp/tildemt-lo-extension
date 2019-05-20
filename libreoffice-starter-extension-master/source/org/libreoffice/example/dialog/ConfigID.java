package org.libreoffice.example.dialog;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.libreoffice.example.comp.TildeTranslatorImpl;
import org.libreoffice.example.helper.DialogHelper;
import org.libreoffice.example.helper.TranslateAPI;

import com.sun.star.awt.XDialog;
import com.sun.star.awt.XDialogEventHandler;
import com.sun.star.awt.XFixedText;
import com.sun.star.awt.XTextComponent;
import com.sun.star.lang.WrappedTargetException;
import com.sun.star.uno.XComponentContext;

/**
 * This dialog is shown, if UserID cannot be found or is invalid.
 * User is asked to go to website where UserID can be copied.
 * When user pastes the ID in the textbox and presses the button,
 * this class checks whether it is valid.
 *   If it is, then data file and variable in TildeTranslatorImpl are updated.
 *   If not, user cannot use translation service.
 */

public class ConfigID implements XDialogEventHandler{

	private XDialog dialog;
	private XComponentContext xContext;
	private static final String actionCheck = "checkNow";
	private String[] supportedActions = new String[] { actionCheck };
	private String id;

	public ConfigID(XComponentContext xContext) {
		this.xContext = xContext;
		this.dialog = DialogHelper.createDialog("config_dialog.xdl", xContext, this);
	}

	public void show(){
		dialog.execute();
	}

	/**
	 * When user presses button, inputed id is checked.
	 * If it's valid, then it is saved in a file and set to variable.
	 *  Dialog has ended and user can use translation services.
	 * If id is not valid, user has to try again in order to
	 * translate anything.
	 */
	private void onCheckButtonPressed() {
		XTextComponent idField = DialogHelper.getEditField( this.dialog, "clientIDField" );
		id = idField.getText();
		boolean valid = checkID(id);
		if(valid) {
			String homeFolder = System.getProperty("user.home");
			try {
				FileWriter dataFile = new FileWriter(homeFolder + File.separator +"tildeID");
				dataFile.write(id);
				dataFile.close();
				dialog.endExecute();
				TildeTranslatorImpl t = new TildeTranslatorImpl(xContext);
				t.setClientID(id);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			setInfoFieldToFalse();
		}
	}

	/**
	 * To check if ID is valid, test translation is done.
	 * If it returns "", then it is valid. If null, then not.
	 * As first translation always takes a bit longer time,
	 * this also speeds up translation for later use.
	 * @return true if given ID is valid
	 */
	public boolean checkID (String inputID) {
		String answer = null;
		try {
			TranslateAPI api = new TranslateAPI();
			//TODO: change for production
			answer = api.translate(inputID, "smt-7060bc9b-7f6d-4978-a21b-591a13dbdea8", "");
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (answer != null) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * If user inputs invalid ID, the dialog prompts it.
	 */
	private void setInfoFieldToFalse() {
		XFixedText infoField = DialogHelper.getLabel(this.dialog, "infoField");
		infoField.setText("Client ID is not valid!");
	}

	@Override
	public boolean callHandlerMethod(XDialog dialog, Object eventObject, String methodName)
			throws WrappedTargetException {
		if (methodName.equals(actionCheck)) {
			onCheckButtonPressed();
			return true;
		}
		return false;
	}

	@Override
	public String[] getSupportedMethodNames() {
		return supportedActions;
	}

}

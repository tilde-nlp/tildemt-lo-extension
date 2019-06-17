package org.libreoffice.example.dialog;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.libreoffice.example.helper.DialogHelper;

import com.sun.star.awt.XDialog;
import com.sun.star.awt.XDialogEventHandler;
import com.sun.star.awt.XFixedText;
import com.sun.star.awt.XTextComponent;
import com.sun.star.lang.WrappedTargetException;
import com.sun.star.uno.XComponentContext;

public class ConfigDialog implements XDialogEventHandler{
	private XDialog dialog;
	private XComponentContext xContext;
	private String homeFolder = null;
	private static final String actionCheck = "checkNow";
	/** String of knownc actions */
	private String[] supportedActions = new String[] { actionCheck };
	/** User entered client id */
	private String id;

	public ConfigDialog (XComponentContext xContext2, String homeFolder) {
		this.dialog = DialogHelper.createDialog("config_dialog.xdl", xContext2, this);
		this.xContext = xContext2;
		this.homeFolder = homeFolder;
	}

	public void show(){
		dialog.execute();
	}

	/**
	 * When user presses button, input id is checked.
	 * If it's valid, then it is saved in a file and set to variable.
	 *   Dialog has ended and user can use translation services.
	 * If id is not valid, user has to try again in order to
	 *   translate anything. Info field is set to explain.
	 */
	private void onCheckButtonPressed() {
		XTextComponent idField = DialogHelper.getEditField( this.dialog, "clientIDField" );
		id = idField.getText();
		boolean valid = ConfigID.checkID(id);
		if(valid) {
			try {
				FileWriter dataFile = new FileWriter(homeFolder + File.separator +"tildeID");
				dataFile.write(id);
				dataFile.close();
				dialog.endExecute();
				ConfigID.setClientAndSystemIDs(id);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			setInfoFieldToFalse();
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

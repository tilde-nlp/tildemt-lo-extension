package com.tilde.mt.lotranslator.dialog;

import com.sun.star.awt.XDialog;
import com.sun.star.awt.XDialogEventHandler;
import com.sun.star.awt.XFixedText;
import com.sun.star.awt.XTextComponent;
import com.sun.star.lang.WrappedTargetException;
import com.sun.star.uno.XComponentContext;
import com.tilde.mt.lotranslator.Configuration;
import com.tilde.mt.lotranslator.LetsMTConfiguration;
import com.tilde.mt.lotranslator.TildeMTClient;
import com.tilde.mt.lotranslator.helper.DialogHelper;

public class AuthDialog implements XDialogEventHandler{
	private XDialog dialog = null;
	private static final String actionSignIn = "signInAction";
	private static final String actionSignOut = "signOutAction";
	/** String of known actions */
	private String[] supportedActions = new String[] { actionSignIn, actionSignOut };
	/** User entered client id */
	private String id;
	private Boolean isPrivateConfig;
	
	private XComponentContext xContext;

	public AuthDialog (XComponentContext xContext) {
		this.xContext = xContext;
	}

	private XDialog getDialog(Boolean authIsNotValid) {
		isPrivateConfig = Configuration.IsPrivateConfiguration();
		XDialog dialog;
		
		if(isPrivateConfig) {
			dialog = DialogHelper.createDialog("SignOutDialog.xdl", xContext, this);
			
			XFixedText infoField = DialogHelper.getLabel(dialog, "infoText");
			
			
			if(authIsNotValid) {
				infoField.setText("Your private authentication is not valid");
			}
			else {
				infoField.setText("You have already successfully authenticated");
			}
		}
		else {
			dialog = DialogHelper.createDialog("SignInDialog.xdl", xContext, this);
		}
		
		return dialog;
	}
	
	public void show(Boolean authIsNotValid){
		if(dialog != null) {
			dialog.endExecute();
		}
		
		dialog = getDialog(authIsNotValid);
		dialog.execute();
	}

	private void signOut() {
		LetsMTConfiguration config = Configuration.Read();
		config.ClientID = null;
		Configuration.Write(config);
		dialog.endExecute();
	}
	
	/**
	 * When user presses button, input id is checked.
	 * If it's valid, then it is saved in a file and set to variable.
	 *   Dialog has ended and user can use translation services.
	 * If id is not valid, user has to try again in order to
	 *   translate anything. Info field is set to explain.
	 */
	private void signIn() {
		XTextComponent idField = DialogHelper.getEditField( this.dialog, "clientIDField" );
		id = idField.getText();
		
		TildeMTClient client = new TildeMTClient(id);
		if(client.GetSystemList() != null) {
			LetsMTConfiguration config = new LetsMTConfiguration();
			config.ClientID = id;
			
			Configuration.Write(config);
			
			dialog.endExecute();
		} 
		else {
			XFixedText infoField = DialogHelper.getLabel(this.dialog, "infoField");
			infoField.setText("Client ID is not valid!");
		}
	}

	@Override
	public boolean callHandlerMethod(XDialog dialog, Object eventObject, String methodName) throws WrappedTargetException {
		if (methodName.equals(actionSignIn)) {
			signIn();
			return true;
		}
		else if (methodName.equals(actionSignOut)) {
			signOut();
			return true;
		}
		return false;
	}

	@Override
	public String[] getSupportedMethodNames() {
		return supportedActions;
	}
}


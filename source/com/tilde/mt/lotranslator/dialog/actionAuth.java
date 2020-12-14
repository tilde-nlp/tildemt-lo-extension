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
import com.tilde.mt.lotranslator.models.TildeMTSystemList;

/**
 * Authentication of users with custom clientID
 * @author guntars.puzulis
 *
 */
public class actionAuth implements XDialogEventHandler{
	private XComponentContext xContext;
	private XDialog dialog = null;
	/** Dialog events */
	private static final String actionSignIn = "signInAction";
	private static final String actionSignOut = "signOutAction";
	private String[] supportedActions = new String[] { actionSignIn, actionSignOut };

	private Boolean isPrivateConfig;
	
	public actionAuth (XComponentContext xContext) {
		this.xContext = xContext;
	}

	/**
	 * Get UI dialog for different scenarios [authenticated | non-authenticated | invalid-authentication]
	 * @param authIsNotValid
	 * @return
	 */
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

	/**
	 * Sign out user
	 */
	private void signOut() {
		LetsMTConfiguration config = Configuration.Read();
		config.ClientID = null;
		Configuration.Write(config);
		dialog.endExecute();
	}
	
	/**
	 * Sign in user with clientID that user has provided
	 */
	private void signIn() {
		XTextComponent idField = DialogHelper.getEditField( this.dialog, "clientIDField" );
		String userClientID = idField.getText();
		
		TildeMTClient client = new TildeMTClient(userClientID);
		TildeMTSystemList systemList = client.GetSystemList();
		if(systemList != null && systemList.System != null) {
			LetsMTConfiguration config = new LetsMTConfiguration();
			config.ClientID = userClientID;
			
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


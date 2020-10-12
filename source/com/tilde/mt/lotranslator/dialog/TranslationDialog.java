package com.tilde.mt.lotranslator.dialog;

import com.sun.star.awt.XDialog;
import com.sun.star.awt.XDialogEventHandler;
import com.sun.star.lang.WrappedTargetException;
import com.sun.star.uno.XComponentContext;
import com.tilde.mt.lotranslator.helper.DialogHelper;


public class TranslationDialog implements XDialogEventHandler {
	
	private XDialog dialog;
	private static final String actionOk = "actionOk";
	private String[] supportedActions = new String[] { actionOk };
	
	public TranslationDialog(XComponentContext xContext) {
		this.dialog = DialogHelper.createDialog("TranslationDialog.xdl", xContext, this);
	}

	public void show() {
		dialog.execute();
	}
	
	private void onOkButtonPressed() {
		dialog.endExecute();
	}
	
	@Override
	public boolean callHandlerMethod(XDialog dialog, Object eventObject, String methodName) throws WrappedTargetException {
		if (methodName.equals(actionOk)) {
			onOkButtonPressed();
			return true; // Event was handled
		}
		return false; // Event was not handled
	}

	@Override
	public String[] getSupportedMethodNames() {
		return supportedActions;
	}

}

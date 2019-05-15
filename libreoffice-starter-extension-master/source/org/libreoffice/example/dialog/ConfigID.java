package org.libreoffice.example.dialog;

import org.libreoffice.example.helper.DialogHelper;

import com.sun.star.awt.XDialog;
import com.sun.star.awt.XDialogEventHandler;
import com.sun.star.lang.WrappedTargetException;
import com.sun.star.uno.XComponentContext;

public class ConfigID implements XDialogEventHandler{

	private XDialog dialog;
	private XComponentContext xContext;
	private static final String actionCheck = "checkNow";
	private String[] supportedActions = new String[] { actionCheck };

	public ConfigID(XComponentContext xContext) {
		System.out.println("~~~~~ ConfigID constructor");
		this.xContext = xContext;
		System.out.println("~~~~~ context added");
		this.dialog = DialogHelper.createDialog("config_dialog.xdl", xContext, this);
		System.out.println("~~~~~ Dialog created");
	}

	public void show(){
		dialog.execute();
	}

	private void onCheckButtonPressed() {
		System.out.println("~~~~~ Check button pressed");
	}

	@Override
	public boolean callHandlerMethod(XDialog dialog, Object eventObject, String methodName) throws WrappedTargetException {
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

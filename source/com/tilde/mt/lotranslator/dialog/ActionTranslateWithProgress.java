package com.tilde.mt.lotranslator.dialog;

import java.util.concurrent.CompletableFuture;

import com.sun.star.awt.XDialog;
import com.sun.star.awt.XDialogEventHandler;
import com.sun.star.awt.XFixedText;
import com.sun.star.lang.WrappedTargetException;
import com.sun.star.uno.XComponentContext;
import com.tilde.mt.lotranslator.Logger;
import com.tilde.mt.lotranslator.TildeMTClient;
import com.tilde.mt.lotranslator.helper.ContentHelper;
import com.tilde.mt.lotranslator.helper.DialogHelper;
import com.tilde.mt.lotranslator.models.TildeMTTranslation;

public abstract class ActionTranslateWithProgress implements XDialogEventHandler {
	private XComponentContext xContext;
	protected XDialog dialog;
	
	protected Boolean disposed = false;
	protected Boolean initialized = false;
	
	private final String dialogOpenAction = "dialogOpenAction";
	private final String cancelTranslationAction = "dialogCancelAction";
	private String[] supportedActions = new String[] { dialogOpenAction, cancelTranslationAction };
	
	private Logger logger = new Logger(this.getClass().getName());

	private TildeMTClient apiClient;
	private String systemID;
	
	XFixedText descriptionLabel;
	
	public ActionTranslateWithProgress(XComponentContext xContext, TildeMTClient apiClient, String systemID) {
		this.xContext = xContext;
		this.apiClient = apiClient;
		this.systemID = systemID;
		
		this.dialog = DialogHelper.createDialog("ProgressDialog.xdl", xContext, this);
	}
	
	public final void show() {
		dialog.execute();
	}
	
	public final void onDialogCancel() {
		if(disposed) {
			logger.info("Progress dialog is already disposed");
			return;
		}
		disposed = true;
		
		dialog.endExecute();
	}
	
	public final void onDialogOpen() {
		if(initialized) {
			logger.info("Progress dialog is already initialized");
			return;
		}
		initialized = true;
		
		String selectedText = ContentHelper.getSelectedText(xContext).Text;

		String newLineType = ContentHelper.getTextNewlineType(selectedText);
		
		descriptionLabel = DialogHelper.getLabel(dialog, "inputDescription");
		
		showProgress(0);
		
		CompletableFuture.runAsync(() -> {
			logger.info("Starting to translate paragraphs");
			
			String paragraphs[] = selectedText.split("\\r?\\n");
			String[] result = new String[paragraphs.length];
			
			if(selectedText.length() > 0) {
				
				for(int i = 0; i != paragraphs.length; i++) {
					String translation = "";
					try {
						TildeMTTranslation translationResult = apiClient.Translate(systemID, paragraphs[i]).get();
						logger.info("Received next paragraph");
						if(disposed) {
							break;
						}
						if(translationResult.hasError()) {
							DialogHelper.showErrorMessage(xContext, dialog, translationResult.toErrorMessage());
							disposed = true;
							break;
						}
						else {
							translation = translationResult.translation;
						}
					} catch (Exception e) {
						e.printStackTrace();
						
						if(disposed) {
							DialogHelper.showErrorMessage(xContext, dialog, "Failed to translate content");
						}
						disposed = true;
						break;
					}
					
					result[i] = translation;
					
					showProgress(i * 100 / paragraphs.length);
				}
			} 
			
			if(!disposed) {
				showProgress(100);
				onProgressResult(newLineType, result);
			}
			
			logger.info("Finishing translation paragraphs");
			
			dialog.endExecute();
		});
	}
	
	private final void showProgress(int percentDone) {
		descriptionLabel.setText(String.format("Progress: %d%%", percentDone));
	}
	
	public abstract void onProgressResult(String seperator, String[] translation);
	
	@Override
	public final boolean callHandlerMethod(XDialog dialog, Object eventObject, String methodName) throws WrappedTargetException {
		if(methodName.equals(dialogOpenAction)) {
			onDialogOpen();
			return true;
		}
		else if(methodName.equals(cancelTranslationAction)) {
			onDialogCancel();
			return true;
		}
		return false; // Event was not handled
	}

	@Override
	public final String[] getSupportedMethodNames() {
		return supportedActions;
	}
}

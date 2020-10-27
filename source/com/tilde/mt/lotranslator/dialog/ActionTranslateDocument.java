package com.tilde.mt.lotranslator.dialog;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.sun.star.awt.XDialog;
import com.sun.star.awt.XDialogEventHandler;
import com.sun.star.awt.XFixedText;
import com.sun.star.frame.XStorable;
import com.sun.star.lang.WrappedTargetException;
import com.sun.star.text.XTextDocument;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;
import com.sun.star.util.CloseVetoException;
import com.tilde.mt.lotranslator.Logger;
import com.tilde.mt.lotranslator.TildeMTClient;
import com.tilde.mt.lotranslator.helper.DialogHelper;
import com.tilde.mt.lotranslator.helper.DocumentHelper;
import com.tilde.mt.lotranslator.models.ErrorResult;
import com.tilde.mt.lotranslator.models.TildeMTDocTranslateState;
import com.tilde.mt.lotranslator.models.TildeMTStartDocTranslate;

/**
 * This action opens a set up (MT system's ID) and translation dialog. From here
 * user can change translation languages and insert the translated text in the
 * selected area of the text.
 *
 * @author arta.zena
 */

public class ActionTranslateDocument implements XDialogEventHandler {

	/** Translate dialog */
	private XDialog dialog;
	private XComponentContext xContext;
	/** Known actions to react to */
	private static String cancelTranslationAction = "cancelTranslationAction";
	private static String statusTranslationAction = "statusTranslationAction";

	/** Array of supported actions */
	private String[] supportedActions = new String[] { cancelTranslationAction, statusTranslationAction };
	/** Dialog fields */

	private XTextDocument document;
	private Boolean translationStarted = false;
	private TildeMTClient apiClient;

	private String documentPath = null;
	private String translationDocumentID = null;
	private Boolean initialized = false;	
	private XFixedText progressInfoLabel = null;
	private Boolean disposed = false;
	private CompletableFuture<Void> statusCheck = null;
	/**
	 * Refresh interval in seconds
	 */
	private int documentStatusRefreshInterval = 5;
	
	private Logger logger = new Logger(this.getClass().getName());

	public ActionTranslateDocument(XComponentContext xContext, TildeMTClient apiClient) {
		this.dialog = DialogHelper.createDialog("DocumentTranslationDialog.xdl", xContext, this);
		this.xContext = xContext;
		this.apiClient = apiClient;
		this.document = DocumentHelper.getCurrentDocument(xContext);
	}

	public void show(String systemID) {
		if (startTranslation(systemID)) {
			dialog.execute();
			// make sure that translation is stopped when we close dialog.
			stopTranslation();
		}
	}

	/**
	 * Save document in TMP folder for upload to Translation service. The document
	 * may also be online document.
	 * 
	 * @return
	 */
	private String saveDocumentBeforeTranslation() {
		// Conditions: sURL = "file:///home/target.htm"
		// xDocument = m_xLoadedDocument
		// Export can be achieved by saving the document and using
		// a special filter which can write the desired format.
		// Normally this filter should be searched inside the filter
		// configuration (using service com.sun.star.document.FilterFactory)
		// but here we use well known filter names directly.
		String sFilter = null;

		// Detect document type by asking XServiceInfo
		com.sun.star.lang.XServiceInfo xInfo = (com.sun.star.lang.XServiceInfo) UnoRuntime
				.queryInterface(com.sun.star.lang.XServiceInfo.class, document);
		// Determine suitable HTML filter name for export.
		if (xInfo != null) {
			if (xInfo.supportsService("com.sun.star.text.TextDocument") == true) {
				// sFilter = "HTML (StarWriter)";
				sFilter = "StarOffice XML (Writer)";
			} else if (xInfo.supportsService("com.sun.star.text.WebDocument") == true) {
				sFilter = "HTML";
			}
		}
		if (sFilter != null) {
			// Build necessary argument list for store properties.
			// Use flag "Overwrite" to prevent exceptions, if file already exists.
			com.sun.star.beans.PropertyValue[] propertyValue = new com.sun.star.beans.PropertyValue[2];
			propertyValue[0] = new com.sun.star.beans.PropertyValue();
			propertyValue[0].Name = "Overwrite";
			propertyValue[0].Value = true;
			propertyValue[1] = new com.sun.star.beans.PropertyValue();
			propertyValue[1].Name = "FilterName";
			propertyValue[1].Value = sFilter;

			XStorable xStorable = (XStorable) UnoRuntime.queryInterface(XStorable.class, document);

			String title = getDocumentTitle();
			String saveUrl = System.getProperty("java.io.tmpdir") + title;
			saveUrl = saveUrl.replace('\\', '/');
			logger.info(String.format("Saving document for translation: %s", saveUrl));

			try {
				xStorable.storeToURL("file:///" + saveUrl, propertyValue);
				return saveUrl;
			} catch (Exception e) {
				e.printStackTrace();
			}

			com.sun.star.util.XCloseable xCloseable = (com.sun.star.util.XCloseable) UnoRuntime.queryInterface(com.sun.star.util.XCloseable.class, document);

			if (xCloseable != null) {
				try {
					xCloseable.close(false);
				} catch (CloseVetoException e) {
					e.printStackTrace();
				}
			} else {
				com.sun.star.lang.XComponent xComp = (com.sun.star.lang.XComponent) UnoRuntime.queryInterface(com.sun.star.lang.XComponent.class, document);
				xComp.dispose();
			}
		}
		return null;
	}

	/**
	 * Start document translation
	 */
	private Boolean startTranslation(String systemID) {
		if (translationStarted) {
			return false;
		}
		translationStarted = true;

		final String filePreamble = "file:///";
		String documentURL = document.getURL();
		if(!documentURL.contains(filePreamble)) {
			DialogHelper.showErrorMessage(xContext, dialog, "Please save document locally before preceding with translation");
			return false;
		}
		documentPath = java.net.URLDecoder.decode(documentURL.substring(filePreamble.length()), StandardCharsets.UTF_8);
		if (documentPath == null) {
			DialogHelper.showErrorMessage(xContext, dialog, "Failed to save document for translation");
			dialog.endExecute();
		} else {
			Boolean sent = sendDocumentToTranslation(documentPath, systemID);
			if (!sent) {
				DialogHelper.showErrorMessage(xContext, dialog, "Failed to send document to Translation");
			} 
			else {
				logger.info("Document is sent to translation");
				return true;
			}
		}
		return false;
	}

	private Boolean sendDocumentToTranslation(String documentPath, String systemID) {
		String filename = getDocumentTitle();
		
		try {
			byte[] fileContent = Files.readAllBytes(Paths.get(documentPath));
			TildeMTStartDocTranslate reqData = new TildeMTStartDocTranslate();

			reqData.Content = new short[fileContent.length]; //new byte[] { 65, 65, 65 };
			
			for(int i = 0; i < fileContent.length; i++) {
				reqData.Content[i] = (short) Byte.toUnsignedInt(fileContent[i]);
			}
			
			reqData.FileName = filename; // "test.txt";
			reqData.SystemID = systemID;

			ErrorResult<String> result = this.apiClient.StartDocumentTranslation(reqData).get();
			if (result.Result != null) {
				translationDocumentID = result.Result;
				logger.info(String.format("Translation started, docid: %s", translationDocumentID));
			} 
			else {
				DialogHelper.showErrorMessage(xContext, dialog, result.Error.ErrorMessage);
			}

			return true;
		} catch (InterruptedException | ExecutionException | java.io.IOException ex) {
			ex.printStackTrace();
		}
		return false;
	}

	/**
	 * Implemented manually because XDocumentProperties.getTitle() is somewhat
	 * broken and returns "" even if document is saved with title
	 * 
	 * @return
	 */
	private String getDocumentTitle() {
		String title = "";

		XStorable xStorable = (XStorable) UnoRuntime.queryInterface(XStorable.class, document);

		String[] path = xStorable.getLocation().split("/");
		title = path[path.length - 1];
		title = URLDecoder.decode(title, StandardCharsets.UTF_8);

		logger.info(String.format("Document title: %s", title));
		return title;

		/*
		 * XDesktop xDesktop = null; Object desktop = null; try { desktop =
		 * xContext.getServiceManager().createInstanceWithContext(
		 * "com.sun.star.frame.Desktop", xContext); } catch (Exception e) {
		 * e.printStackTrace(); } xDesktop = (XDesktop)
		 * UlnoRuntime.queryInterface(com.sun.star.frame.XDesktop.class, desktop);
		 * XComponent xComponent = xDesktop.getCurrentComponent();
		 * XDocumentPropertiesSupplier xSupplier = (XDocumentPropertiesSupplier)
		 * UnoRuntime.queryInterface(XDocumentPropertiesSupplier.class, xComponent);
		 * XDocumentProperties xDocumentProperties = xSupplier.getDocumentProperties();
		 * 
		 * logger.warn(xDocumentProperties.getTitle());
		 */
	}

	private void showTranslatedDocument() {
		logger.info("Displaying translated document");
		
		ErrorResult<byte[]> result = null;
		try {
			result = this.apiClient.DownloadDocumentTranslation(translationDocumentID).get();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(result.Error != null) {
			DialogHelper.showErrorMessage(xContext, dialog, result.Error.toErrorMessage());
		}
		else {
			byte[] translatedFileContents = (byte[])result.Result;
			
			String[] splittedPath = documentPath.split("\\.");
			
			LocalDateTime currentTime = LocalDateTime.now();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH_mm_ss");

			String timestamp = currentTime.format(formatter);
			splittedPath[splittedPath.length - 2] = String.format("%s_translated_%s", splittedPath[splittedPath.length - 2], timestamp);
			
			Path path = Path.of(String.join(".", splittedPath));
			logger.info("Translated file location: " + path.toString());
			
			logger.info(translatedFileContents.toString());
			
			try {
				Files.write(path, translatedFileContents);
				
				Desktop.getDesktop().open(new File(path.toString()));
			} catch (IOException e) {
				DialogHelper.showErrorMessage(xContext, dialog, "Failed to save translated document");
				e.printStackTrace();
			}
		}
		dialog.endExecute();
	}
	
	private void stopTranslation() {
		if(disposed) {
			logger.info("Already disposed");
			return;
		}
		
		logger.info("Cancelling translation...");
		disposed = true;
		
		try {
			statusCheck.get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		
		logger.info("Translation canceled");
		dialog.endExecute();
	}

	private CompletableFuture<Void> updateStatus() {
		return CompletableFuture.runAsync(() -> {
			while (true) {
				TildeMTDocTranslateState status;
				try {
					status = this.apiClient.GetDocumentTranslationState(translationDocumentID).get();
					if(disposed) {
						break;
					}
					
					logger.info(status.toString());
					if(status.Status.equals("completed")) {
						progressInfoLabel.setText(String.format("Translation Completed"));
						showTranslatedDocument();
						break;
					}
					else if(status.Status.equals("error")) {
						DialogHelper.showErrorMessage(xContext, dialog, String.format("Error: %s", status.ErrorCode));
						this.dialog.endExecute();
						break;
					}
					else if(status.Status.equals("translating")){
						if(status.Segments > 0) {
							progressInfoLabel.setText(String.format("Translation progress: %d%%", status.TranslatedSegments * 100 / status.Segments));
						}
					}
					else {
						progressInfoLabel.setText(String.format("Translation status: %s", status.Status));
					}
				} 
				catch (Exception e) {
					e.printStackTrace();
					
					DialogHelper.showErrorMessage(xContext, dialog, String.format("Failed to translate document due to unknown error"));
					this.dialog.endExecute();
					
					break;
				}
				
				try {
					TimeUnit.SECONDS.sleep(documentStatusRefreshInterval);
				} catch (InterruptedException e) {
					e.printStackTrace();
					break;
				}
			}
		});
	}
	
	private void checkTranslationStatus() {
		if(initialized) {
			logger.info("Status check already initialized");
			return;
		}
		initialized = true;
		
		logger.info("Check status...");
		
		progressInfoLabel = DialogHelper.getLabel(dialog, "progressInfoLabel");
		progressInfoLabel.setText(String.format("Document is submitted to translation..."));
		
		statusCheck = updateStatus();
	}

	@Override
	public boolean callHandlerMethod(XDialog dialog, Object eventObject, String methodName) throws WrappedTargetException {
		if (methodName.equals(cancelTranslationAction)) {
			stopTranslation();
			return true;
		} else if (methodName.equals(statusTranslationAction)) {
			checkTranslationStatus();
			return true;
		}
		return false; // Event was not handled
	}

	@Override
	public String[] getSupportedMethodNames() {
		return supportedActions;
	}

}

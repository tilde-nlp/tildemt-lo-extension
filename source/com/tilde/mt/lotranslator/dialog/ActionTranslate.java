package com.tilde.mt.lotranslator.dialog;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.sun.star.awt.XControl;
import com.sun.star.awt.XControlContainer;
import com.sun.star.awt.XDialog;
import com.sun.star.awt.XDialogEventHandler;
import com.sun.star.awt.XListBox;
import com.sun.star.awt.XTextComponent;
import com.sun.star.beans.PropertyVetoException;
import com.sun.star.beans.UnknownPropertyException;
import com.sun.star.beans.XPropertySet;
import com.sun.star.lang.IllegalArgumentException;
import com.sun.star.lang.WrappedTargetException;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;
import com.tilde.mt.lotranslator.Configuration;
import com.tilde.mt.lotranslator.Logger;
import com.tilde.mt.lotranslator.TildeMTClient;
import com.tilde.mt.lotranslator.helper.DialogHelper;
import com.tilde.mt.lotranslator.helper.DocumentHelper;
import com.tilde.mt.lotranslator.models.TildeMTSystem;

/**
 * This action opens a set up (MT system's ID) and translation dialog. From here
 * user can change translation languages and insert the translated text in the
 * selected area of the text.
 *
 * @author arta.zena
 */

public class ActionTranslate implements XDialogEventHandler {

	/** Translate dialog */
	private XDialog dialog;
	private XComponentContext xContext;
	/** Known actions to react to */
	private static final String actionClose = "actionClose";
	private static final String actionTranslate = "translateNow";
	private static final String actionInsert = "insertNow";
	private static final String actionChangeSourceLang = "changeSourceLang";
	/** Array of supported actions */
	private String[] supportedActions = new String[] { actionClose, actionTranslate, actionInsert,
			actionChangeSourceLang };
	/** Dialog fields */
	private static XTextComponent sourceTextField;
	private static XTextComponent targetTextField;
	private static XListBox sourceLanguageBox;
	private static XListBox targetLanguageBox;
	private static String savedSourceLang = "";
	private static String savedTargetLang = "";
	private XControlContainer m_xControlContainer;

	private TildeMTClient apiClient;

	private Logger logger = new Logger(this.getClass().getName());

	public ActionTranslate(XComponentContext xContext, TildeMTClient apiClient) {
		this.dialog = DialogHelper.createDialog("TranslationDialog.xdl", xContext, this);
		this.xContext = xContext;
		this.apiClient = apiClient;
	}

	public void show() {
		configureListBoxOnOpening();
		dialog.execute();
	}

	private void onCloseButtonPressed() {
		getFields();
		savedSourceLang = sourceLanguageBox.getSelectedItem();
		savedTargetLang = targetLanguageBox.getSelectedItem();

		Configuration.setSystemID(getSystemID(savedSourceLang, savedTargetLang));

		dialog.endExecute();
	}

	/**
	 * If selected system exists, sends input text to the translation class and sets
	 * returned value to appear in output text field.
	 *
	 * @throws Exception getting translation failed
	 */
	private void onTranslateButtonPressed() throws Exception {
		getFields();
		String systemID = getSystemID(sourceLanguageBox.getSelectedItem(), targetLanguageBox.getSelectedItem());
		String text = sourceTextField.getText();
		String translation = this.apiClient.translate(systemID, text);

		targetTextField.setText(translation);
	}

	/**
	 * If translation is not empty, get the cursor and insert translated text where
	 * the it is located in the document Else do nothing
	 */
	private void onInsertButtonPressed() {
		if (!targetTextField.getText().equals("")) {
			com.sun.star.text.XTextDocument xTextDoc = DocumentHelper.getCurrentDocument(xContext);
			com.sun.star.frame.XController xController = xTextDoc.getCurrentController();
			com.sun.star.text.XTextViewCursorSupplier xTextViewCursorSupplier = DocumentHelper
					.getCursorSupplier(xController);
			com.sun.star.text.XTextViewCursor xTextViewCursor = xTextViewCursorSupplier.getViewCursor();

			xTextViewCursor.setString(targetTextField.getText());
		} else {
			logger.info("Insert:\tnothing to insert");
		}
	}

	/**
	 * When source language is changed, target language list box is updated to fit
	 * the new source language. If previously set target language is available also
	 * for the new source language it stays the same. Else it is set to the first
	 * one in the list.
	 */
	private void onSourceLangChanged() {
		getFields();
		String selectedSourceLang = sourceLanguageBox.getSelectedItem();
		String selectedTargetLang = targetLanguageBox.getSelectedItem();
		String[] targetLanguageArray = getTargetLanguageList(selectedSourceLang);

		// empty the target box before adding new values
		targetLanguageBox.removeItems((short) 0, targetLanguageBox.getDropDownLineCount());
		Boolean targetStaysTheSame = false;
		for (short i = 0; i < targetLanguageArray.length; i++) {
			targetLanguageBox.addItem(targetLanguageArray[i], i);
			if (targetLanguageArray[i].contentEquals(selectedTargetLang)) {
				targetStaysTheSame = true;
			}
		}
		// if target language if available also for the new source language, don't
		// change the selection
		if (targetStaysTheSame) {
			targetLanguageBox.selectItem(selectedTargetLang, true);
		} else {
			targetLanguageBox.selectItemPos((short) 0, true);
		}

		targetLanguageBox.setDropDownLineCount((short) targetLanguageArray.length);
		sourceLanguageBox.setDropDownLineCount((short) getSourceLanguageList().length);
	}

	/**
	 * Updates variables based on dialog fields user can edit.
	 */
	private void getFields() {
		sourceTextField = DialogHelper.getEditField(this.dialog, "TextFieldFrom");
		targetTextField = DialogHelper.getEditField(this.dialog, "TextFieldTo");
		sourceLanguageBox = DialogHelper.getListBox(this.dialog, "SourceLanguages");
		targetLanguageBox = DialogHelper.getListBox(this.dialog, "TargetLanguages");

		logger.info("--------");
		logger.info("To translate:\t" + sourceTextField.getText());
		logger.info("Source language:\t" + sourceLanguageBox.getSelectedItem());
		logger.info("Target language:\t" + targetLanguageBox.getSelectedItem());
	}

	/**
	 * Based on languages passed via parameters, first available running system's id
	 * is returned. If there is no available system, info dialog shows up.
	 *
	 * @param sourceLang
	 * @param targetLang
	 * @return String containing system id
	 */
	private String getSystemID(String sourceLang, String targetLang) {
		TildeMTSystem[] systems = this.apiClient.GetSystemList().System;

		for (int i = 0; i < systems.length; i++) {

			// check whether languages fit
			if (systems[i].getSourceLanguage().getName().getText().contentEquals(sourceLang)
					&& systems[i].getTargetLanguage().getName().getText().contentEquals(targetLang)) {

				// check again if machine's status (to avoid error when there are > 1 machines
				// with same languages)
				for (int k = 0; k < systems[i].getMetadata().length; k++) {
					String key = systems[i].getMetadata()[k].getKey();
					if (key.contentEquals("status")) {
						if (systems[i].getMetadata()[k].getValue().contentEquals("running")) {
							return systems[i].getID();
						}
					}
				}
			}
		}
		DialogHelper.showInfoMessage(xContext, dialog, "System not available!");
		logger.info("System not found! Using default system to translate...");
		targetTextField.setText("");
		return null;
	}

	/**
	 * Adds available system language lists to list boxes before the dialog is open.
	 *
	 */
	private void configureListBoxOnOpening() {
		// get properties for the source list box
		m_xControlContainer = UnoRuntime.queryInterface(XControlContainer.class, dialog);
		XControl sourceLangBoxControl = m_xControlContainer.getControl("SourceLanguages");
		XPropertySet sourceLangBoxProps = UnoRuntime.queryInterface(XPropertySet.class,
				sourceLangBoxControl.getModel());

		String[] sourceLanguages = getSourceLanguageList();
		String selectedSourceLanguage = null;
		try {
			// add String[] contents to the list box
			sourceLangBoxProps.setPropertyValue("StringItemList", sourceLanguages);

			// reset language choice with which user closed the dialog last time
			// (or just set first languages if the dialog is open for the first time)
			short[] selectedNr = new short[1];
			selectedNr[0] = (short) 0;
			selectedSourceLanguage = sourceLanguages[0];
			for (int i = 0; i < sourceLanguages.length; i++) {
				if (sourceLanguages[i].contentEquals(savedSourceLang)) {
					selectedNr[0] = (short) i;
					selectedSourceLanguage = savedSourceLang;
					break;
				}
			}
			sourceLangBoxProps.setPropertyValue("SelectedItems", selectedNr);
		} catch (IllegalArgumentException | UnknownPropertyException | PropertyVetoException
				| WrappedTargetException e) {
			e.printStackTrace();
		}

		// adjust target language box based on what is set in source box. Similar to
		// upper code.
		String[] targetLanguages = getTargetLanguageList(selectedSourceLanguage);
		XControl targetLangBoxControl = m_xControlContainer.getControl("TargetLanguages");
		XPropertySet targetLangBoxProps = UnoRuntime.queryInterface(XPropertySet.class,
				targetLangBoxControl.getModel());
		try {
			targetLangBoxProps.setPropertyValue("StringItemList", targetLanguages);
			short[] selectedNr = new short[] { (short) 0 };
			for (int i = 0; i < targetLanguages.length; i++) {
				if (targetLanguages[i].contentEquals(savedTargetLang)) {
					selectedNr[0] = (short) i;
					break;
				}
			}
			targetLangBoxProps.setPropertyValue("SelectedItems", selectedNr);
		} catch (IllegalArgumentException | UnknownPropertyException | PropertyVetoException
				| WrappedTargetException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Based on given source language returns non-repeating language list with all
	 * available target languages for systems that are running.
	 *
	 * @param sourceLanguage
	 * @return String array with target languages
	 */
	private String[] getTargetLanguageList(String sourceLanguage) {
		TildeMTSystem[] systems = this.apiClient.GetSystemList().System;

		List<String> targetLanguageList = new ArrayList<String>();

		// update target list: get systems where source language is the selected one
		for (int i = 0; i < systems.length; i++) {
			String systemsSourceLang = systems[i].getSourceLanguage().getName().getText();
			String systemsTargetLang = systems[i].getTargetLanguage().getName().getText();
			// put them in non-repeating array
			if (systemsSourceLang.equals(sourceLanguage) && (!targetLanguageList.contains(systemsTargetLang))) {
				// check if machine's status is running
				for (int k = 0; k < systems[i].getMetadata().length; k++) {
					String key = systems[i].getMetadata()[k].getKey();
					if (key.contentEquals("status")
							&& systems[i].getMetadata()[k].getValue().contentEquals("running")) {
						targetLanguageList.add(systemsTargetLang);
						break;
					}
				}
			}
		}
		targetLanguageList.sort(Comparator.naturalOrder());
		// change from List<String> to String[]
		String[] targetLanguageArray = new String[targetLanguageList.size()];
		for (int i = 0; i < targetLanguageList.size(); i++) {
			targetLanguageArray[i] = targetLanguageList.get(i);
		}
		return targetLanguageArray;
	}

	/**
	 * Extracts available source languages that have at least one running system.
	 * 
	 * @return String[] with non-repeating source languages
	 */
	private String[] getSourceLanguageList() {
		// create array containing available languages
		TildeMTSystem[] systems = this.apiClient.GetSystemList().System;
		List<String> sourceLanguageList = new ArrayList<String>();

		// get full language list of available machines for source box
		for (int i = 0; i < systems.length; i++) {
			// check whether this language is already in the list and add it if not
			String sourceLang = systems[i].getSourceLanguage().getName().getText();
			if (!sourceLanguageList.contains(sourceLang)) {
				// check is system is running
				for (int k = 0; k < systems[i].getMetadata().length; k++) {
					String key = systems[i].getMetadata()[k].getKey();
					if (key.contentEquals("status")
							&& systems[i].getMetadata()[k].getValue().contentEquals("running")) {
						sourceLanguageList.add(sourceLang);
						break;
					}
				}
			}
		}
		// sort in alphabetical order
		sourceLanguageList.sort(Comparator.naturalOrder());
		// change from List<String> to String[]
		String[] sourceLanguageArray = new String[sourceLanguageList.size()];
		for (int i = 0; i < sourceLanguageList.size(); i++) {
			sourceLanguageArray[i] = sourceLanguageList.get(i);
		}
		return sourceLanguageArray;
	}

	@Override
	public boolean callHandlerMethod(XDialog dialog, Object eventObject, String methodName)
			throws WrappedTargetException {
		if (methodName.equals(actionClose)) {
			onCloseButtonPressed();
			return true;
		} else if (methodName.equals(actionTranslate)) {
			try {
				onTranslateButtonPressed();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return true;
		} else if (methodName.equals(actionInsert)) {
			onInsertButtonPressed();
			return true;
		} else if (methodName.contentEquals(actionChangeSourceLang)) {
			onSourceLangChanged();
			return true;
		}
		return false; // Event was not handled
	}

	@Override
	public String[] getSupportedMethodNames() {
		return supportedActions;
	}

}

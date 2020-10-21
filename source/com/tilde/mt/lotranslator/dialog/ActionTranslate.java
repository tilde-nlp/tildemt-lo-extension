package com.tilde.mt.lotranslator.dialog;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.SortedSet;
import java.util.TreeSet;

import com.sun.star.awt.XControl;
import com.sun.star.awt.XControlContainer;
import com.sun.star.awt.XDialog;
import com.sun.star.awt.XDialogEventHandler;
import com.sun.star.awt.XFixedText;
import com.sun.star.awt.XListBox;
import com.sun.star.awt.XTextComponent;
import com.sun.star.beans.XPropertySet;
import com.sun.star.lang.WrappedTargetException;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;
import com.tilde.mt.lotranslator.Configuration;
import com.tilde.mt.lotranslator.Logger;
import com.tilde.mt.lotranslator.TildeMTClient;
import com.tilde.mt.lotranslator.helper.ContentHelper;
import com.tilde.mt.lotranslator.helper.DialogHelper;
import com.tilde.mt.lotranslator.helper.DocumentHelper;
import com.tilde.mt.lotranslator.models.SelectedText;
import com.tilde.mt.lotranslator.models.TildeMTSystem;
import com.tilde.mt.lotranslator.models.TildeMTUserData;

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
	private static final String actionChangeTargetLang = "changeTargetLang";
	private static final String actionChangeDomain = "changeDomain";

	/** Array of supported actions */
	private String[] supportedActions = new String[] { actionClose, actionTranslate, actionInsert, actionChangeSourceLang, actionChangeTargetLang, actionChangeDomain };
	/** Dialog fields */
	private static XTextComponent sourceTextField;
	private static XTextComponent targetTextField;
	private static XListBox sourceLanguageBox;
	private static XListBox targetLanguageBox;
	private static XListBox domainBox;
	private static String savedSourceLang = "";
	private static String savedTargetLang = "";
	private static String savedDomain = "";
	
	private TildeMTUserData userData;
	
	private HashMap<String, String> namedSourceLangCodes = new HashMap<String, String>();
	
	private XControlContainer m_xControlContainer;

	private TildeMTClient apiClient;

	private Logger logger = new Logger(this.getClass().getName());

	public ActionTranslate(XComponentContext xContext, TildeMTClient apiClient, TildeMTUserData userData) {
		this.dialog = DialogHelper.createDialog("TranslationDialog.xdl", xContext, this);
		this.xContext = xContext;
		this.apiClient = apiClient;
		this.userData = userData;
	}

	public void show() {
		configureListBoxOnOpening();
		dialog.execute();
	}

	private void saveConfiguration() {
		getFields();
		savedSourceLang = sourceLanguageBox.getSelectedItem();
		savedTargetLang = targetLanguageBox.getSelectedItem();
		savedDomain = domainBox.getSelectedItem();

		Configuration.setSystemID(getSystemID(savedSourceLang, savedTargetLang, savedDomain));
	}

	/**
	 * If selected system exists, sends input text to the translation class and sets
	 * returned value to appear in output text field.
	 *
	 * @throws Exception getting translation failed
	 */
	private void onTranslateButtonPressed() throws Exception {
		getFields();
		String systemID = getSystemID(sourceLanguageBox.getSelectedItem(), targetLanguageBox.getSelectedItem(), domainBox.getSelectedItem());
		String text = sourceTextField.getText();
		
		targetTextField.setText(this.apiClient.Translate(systemID, text).get());
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
			DialogHelper.showInfoMessage(this.xContext, null, "Please translate text you wish to replace");
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
		String selectedDomain = domainBox.getSelectedItem();
		
		SortedSet<String> domains = getDomains(selectedSourceLang, selectedTargetLang);
		SortedSet<String> targetLanguages = getTargetLanguages(selectedSourceLang);

		domainBox.removeItems((short)0, domainBox.getDropDownLineCount());
		domainBox.addItems(domains.toArray(String[]::new), (short) domains.size());
		
		targetLanguageBox.removeItems((short)0, targetLanguageBox.getDropDownLineCount());
		targetLanguageBox.addItems(targetLanguages.toArray(String[]::new), (short) targetLanguages.size());

		domainBox.setDropDownLineCount((short) domains.size());
		targetLanguageBox.setDropDownLineCount((short) targetLanguages.size());
		sourceLanguageBox.setDropDownLineCount((short) getSourceLanguages().length);
		
		if(targetLanguages.contains(selectedTargetLang)) {
			targetLanguageBox.selectItem(selectedTargetLang, true);
		}
		else {
			targetLanguageBox.selectItemPos((short) 0, true);
		}
		
		if(domains.contains(selectedDomain)) {
			domainBox.selectItem(selectedDomain, true);
		}
		else {
			domainBox.selectItemPos((short) 0, true);
		}
		
		saveConfiguration();
	}

	/**
	 * Updates variables based on dialog fields user can edit.
	 */
	private void getFields() {
		sourceTextField = DialogHelper.getEditField(this.dialog, "TextFieldFrom");
		targetTextField = DialogHelper.getEditField(this.dialog, "TextFieldTo");
		sourceLanguageBox = DialogHelper.getListBox(this.dialog, "SourceLanguages");
		targetLanguageBox = DialogHelper.getListBox(this.dialog, "TargetLanguages");
		domainBox = DialogHelper.getListBox(this.dialog, "Domains");

		logger.info("--------");
		logger.info("Translation text:\t" + sourceTextField.getText());
		logger.info("Source language:\t" + sourceLanguageBox.getSelectedItem());
		logger.info("Target language:\t" + targetLanguageBox.getSelectedItem());
		logger.info("To Domain:\t" + domainBox.getSelectedItem());
	}

	/**
	 * Based on languages passed via parameters, first available running system's id
	 * is returned. If there is no available system, info dialog shows up.
	 *
	 * @param sourceLang
	 * @param targetLang
	 * @return String containing system id
	 */
	private String getSystemID(String selectedSourceLang, String selectedTargetLang, String selectedDomain) {
		TildeMTSystem[] systems = this.apiClient.GetSystemList().System;

		for (int i = 0; i < systems.length; i++) {
			TildeMTSystem system = systems[i];
			String sourceLang = system.getSourceLanguage().getName().getText();
			String targetLang = system.getTargetLanguage().getName().getText();
			String domain = system.getDomain();
			
			// check whether languages fit
			if (sourceLang.equals(selectedSourceLang) && targetLang.equals(selectedTargetLang) && domain.equals(selectedDomain) && system.IsAvailable()){
				return system.getID();
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
		XFixedText userGroupLabel = DialogHelper.getLabel(dialog, "userGroupLabel");
		userGroupLabel.setText(String.format("User group: %s", userData.ActiveGroup));
		
		// get properties for the source list box
		m_xControlContainer = UnoRuntime.queryInterface(XControlContainer.class, dialog);
		XControl sourceLangBoxControl = m_xControlContainer.getControl("SourceLanguages");
		XPropertySet sourceLangBoxProps = UnoRuntime.queryInterface(XPropertySet.class, sourceLangBoxControl.getModel());
				
		String[] sourceLanguages = getSourceLanguages();
		
		SelectedText selection = ContentHelper.getSelectedText(this.xContext);
		if(namedSourceLangCodes.containsKey(selection.Locale.Language)) {
			savedSourceLang = namedSourceLangCodes.get(selection.Locale.Language);
			logger.warn(String.format("Setting initial language to detected: %s", selection.Locale.Language));
		}
		else {
			logger.warn(String.format("Language that was detected from text was not resolved to MT system: %s", selection.Locale.Language));
		}
		
		sourceTextField = DialogHelper.getEditField(this.dialog, "TextFieldFrom");
		sourceTextField.setText(selection.Text);
		
		String selectedSourceLanguage = null;
		String selectedTargetLanguage = null;
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
		} catch (Exception e) {
			e.printStackTrace();
		}

		// adjust target language box based on what is set in source box. Similar to
		// upper code.
		String[] targetLanguages = getTargetLanguages(selectedSourceLanguage).toArray(String[]::new);
		XControl targetLangBoxControl = m_xControlContainer.getControl("TargetLanguages");
		XPropertySet targetLangBoxProps = UnoRuntime.queryInterface(XPropertySet.class, targetLangBoxControl.getModel());
		try {
			targetLangBoxProps.setPropertyValue("StringItemList", targetLanguages);
			short[] selectedNr = new short[] { (short) 0 };
			selectedTargetLanguage = targetLanguages[0];
			for (int i = 0; i < targetLanguages.length; i++) {
				if (targetLanguages[i].contentEquals(savedTargetLang)) {
					selectedNr[0] = (short) i;
					selectedTargetLanguage = savedTargetLang;
					break;
				}
			}
			targetLangBoxProps.setPropertyValue("SelectedItems", selectedNr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String[] domains = getDomains(selectedSourceLanguage, selectedTargetLanguage).toArray(String[]::new);
		XControl domainBoxControl = m_xControlContainer.getControl("Domains");
		XPropertySet domainBoxProps = UnoRuntime.queryInterface(XPropertySet.class, domainBoxControl.getModel());
		try {
			domainBoxProps.setPropertyValue("StringItemList", domains);
			short[] selectedNr = new short[] { (short) 0 };
			for (int i = 0; i < domains.length; i++) {
				if (domains[i].contentEquals(savedDomain)) {
					selectedNr[0] = (short) i;
					break;
				}
			}
			domainBoxProps.setPropertyValue("SelectedItems", selectedNr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		saveConfiguration();
	}

	private SortedSet<String> getDomains(String selectedSourceLang, String selectedTargetLang) {
		TildeMTSystem[] systems = this.apiClient.GetSystemList().System;
		
		TreeSet<String> domains = new TreeSet<String>();

		for (int i = 0; i < systems.length; i++) {
			TildeMTSystem system = systems[i];
			String sourceLang = system.getSourceLanguage().getName().getText();
			String targetLang = system.getTargetLanguage().getName().getText();
			String domain = system.getDomain();
			
			if(sourceLang.equals(selectedSourceLang) && targetLang.equals(selectedTargetLang) && system.IsAvailable()) {
				domains.add(domain);
			}
		}
		
		return domains;
	}
	
	/**
	 * Based on given source language returns non-repeating language list with all
	 * available target languages for systems that are running.
	 *
	 * @param selectedSourceLang
	 * @return String array with target languages
	 */
	private SortedSet<String> getTargetLanguages(String selectedSourceLang) {
		TildeMTSystem[] systems = this.apiClient.GetSystemList().System;
		
		TreeSet<String> targetLanguages = new TreeSet<String>();

		for (int i = 0; i < systems.length; i++) {
			TildeMTSystem system = systems[i];
			String sourceLang = system.getSourceLanguage().getName().getText();
			String targetLang = system.getTargetLanguage().getName().getText();
			
			if(sourceLang.equals(selectedSourceLang) && system.IsAvailable()) {
				targetLanguages.add(targetLang);
			}
		}
		
		return targetLanguages;
	}

	/**
	 * Extracts available source languages that have at least one running system.
	 * 
	 * @return String[] with non-repeating source languages
	 */
	private String[] getSourceLanguages() {
		TildeMTSystem[] systems = this.apiClient.GetSystemList().System;
		
		HashSet<String> sourceLanguages = new HashSet<String>();

		for (int i = 0; i < systems.length; i++) {
			TildeMTSystem system = systems[i];
			String sourceLang = system.getSourceLanguage().getName().getText();
			String sourceLangCode = system.getSourceLanguage().getCode();
			namedSourceLangCodes.put(sourceLangCode, sourceLang);
			
			if(system.IsAvailable()) {
				sourceLanguages.add(sourceLang);
			}
		}
		String[] sortedSourceLanguages = sourceLanguages.toArray(String[]::new);
		Arrays.sort(sortedSourceLanguages, Comparator.naturalOrder());
		return sortedSourceLanguages;
	}

	
	@Override
	public boolean callHandlerMethod(XDialog dialog, Object eventObject, String methodName) throws WrappedTargetException {
		if (methodName.equals(actionClose)) {
			saveConfiguration();
			dialog.endExecute();
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
		else if (methodName.contentEquals(actionChangeTargetLang)) {
			saveConfiguration();
			return true;
		}
		else if (methodName.contentEquals(actionChangeDomain)) {
			saveConfiguration();
			return true;
		}
		return false; // Event was not handled
	}

	@Override
	public String[] getSupportedMethodNames() {
		return supportedActions;
	}

}

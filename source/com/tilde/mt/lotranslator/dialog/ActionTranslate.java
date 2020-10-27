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
 * Translation workspace dialog with ability to choose MT system and translate custom texts.
 * @author guntars.puzulis, arta.zena
 *
 */
public class ActionTranslate implements XDialogEventHandler {
	private XDialog dialog;
	private XComponentContext xContext;
	private XControlContainer m_xControlContainer;
	/** Dialog events */
	private final String actionClose = "actionClose";
	private final String actionTranslate = "translateNow";
	private final String actionInsert = "insertNow";
	private final String actionChangeSourceLang = "changeSourceLang";
	private final String actionChangeTargetLang = "changeTargetLang";
	private final String actionChangeDomain = "changeDomain";
	private String[] supportedActions = new String[] { actionClose, actionTranslate, actionInsert, actionChangeSourceLang, actionChangeTargetLang, actionChangeDomain };
	/** Dialog fields */
	private XTextComponent sourceTextField;
	private XTextComponent targetTextField;
	private XListBox sourceLanguageBox;
	private XListBox targetLanguageBox;
	private XListBox domainBox;
	
	private String savedSourceLang = "";
	private String savedTargetLang = "";
	private String savedDomain = "";
	private Boolean silentSelection = false;
	private TildeMTUserData userData;
	private HashMap<String, String> namedSourceLangCodes = new HashMap<String, String>();
	
	private TildeMTClient apiClient;
	private Logger logger = new Logger(this.getClass().getName());

	public ActionTranslate(XComponentContext xContext, TildeMTClient apiClient, TildeMTUserData userData) {
		this.dialog = DialogHelper.createDialog("TranslationDialog.xdl", xContext, this);
		this.xContext = xContext;
		this.apiClient = apiClient;
		this.userData = userData;
	}

	public void show() {
		configureInitialSetup();
		dialog.execute();
	}

	private void saveConfiguration() {
		getFields();
		savedSourceLang = sourceLanguageBox.getSelectedItem();
		savedTargetLang = targetLanguageBox.getSelectedItem();
		savedDomain = domainBox.getSelectedItem();

		logger.info("--- Saving system configuration ---");
		logger.info("Source language:\t" + savedSourceLang);
		logger.info("Target language:\t" + savedTargetLang);
		logger.info("Domain:\t" + savedDomain);
		
		Configuration.setSystemID(getSystemID());
	}


	/**
	 * Translate user text
	 */
	private void onTranslateButtonPressed() {
		getFields();
		String systemID = getSystemID();
		String text = sourceTextField.getText();
		
		this.apiClient.Translate(systemID, text).thenAccept((result) -> {
			if(result.hasError()) {
				DialogHelper.showErrorMessage(xContext, dialog, result.toErrorMessage());
			}
			else {
				targetTextField.setText(result.translation);
			}			
		});
	}
	

	/**
	 * Insert translation in document where user cursor is
	 */
	private void onInsertButtonPressed() {
		if (!targetTextField.getText().equals("")) {
			com.sun.star.text.XTextDocument xTextDoc = DocumentHelper.getCurrentDocument(xContext);
			com.sun.star.frame.XController xController = xTextDoc.getCurrentController();
			com.sun.star.text.XTextViewCursorSupplier xTextViewCursorSupplier = DocumentHelper.getCursorSupplier(xController);
			com.sun.star.text.XTextViewCursor xTextViewCursor = xTextViewCursorSupplier.getViewCursor();

			xTextViewCursor.setString(targetTextField.getText());
		} 
		else {
			DialogHelper.showInfoMessage(this.xContext, null, "Please translate text you wish to replace");
		}
	}

	/**
	 * When query parameters [srcLang|trgLang|domain] changes, update other fields, so that together query makes valid MT system.
	 */
	private void onQueryChanged() {
		logger.info("Query changed...");
		
		silentSelection = true;
		getFields();
		
		String selectedSourceLang = sourceLanguageBox.getSelectedItem();
		savedSourceLang = selectedSourceLang;
		
		String selectedTargetLang = targetLanguageBox.getSelectedItem();
		String selectedDomain = domainBox.getSelectedItem();
		
		SortedSet<String> targetLanguages = getTargetLanguages(selectedSourceLang);
		
		targetLanguageBox.removeItems((short)0, targetLanguageBox.getDropDownLineCount());
		targetLanguageBox.addItems(targetLanguages.toArray(String[]::new), (short) targetLanguages.size());
		targetLanguageBox.setDropDownLineCount((short) targetLanguages.size());
		
		if(targetLanguages.contains(selectedTargetLang)) {
			targetLanguageBox.selectItem(selectedTargetLang, true);
			savedTargetLang = selectedTargetLang;
		}
		else {
			targetLanguageBox.selectItemPos((short) 0, true);
			savedTargetLang = targetLanguageBox.getItems()[0];
		}
		
		SortedSet<String> domains = getDomains(selectedSourceLang, savedTargetLang);
		
		domainBox.removeItems((short)0, domainBox.getDropDownLineCount());
		domainBox.addItems(domains.toArray(String[]::new), (short) domains.size());
		domainBox.setDropDownLineCount((short) domains.size());
		
		if(domains.contains(selectedDomain)) {
			domainBox.selectItem(selectedDomain, true);
			savedDomain = selectedDomain;
		}
		else {
			domainBox.selectItemPos((short) 0, true);
			savedDomain = domainBox.getItems()[0];
		}
		
		saveConfiguration();
		
		silentSelection = false;
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
	}

	/**
	 * Based on languages passed via parameters, first available running system's id
	 * is returned. If there is no available system, info dialog shows up.
	 *
	 * @param sourceLang
	 * @param targetLang
	 * @return String containing system id
	 */
	private String getSystemID() {
		TildeMTSystem[] systems = this.apiClient.GetSystemList().System;

		for (int i = 0; i < systems.length; i++) {
			TildeMTSystem system = systems[i];
			String sourceLang = system.getSourceLanguage().getName().getText();
			String targetLang = system.getTargetLanguage().getName().getText();
			String domain = system.getDomain();
			
			// check whether languages fit
			if (sourceLang.equals(savedSourceLang) && targetLang.equals(savedTargetLang) && domain.equals(savedDomain) && system.IsAvailable()){
				return system.getID();
			}
		}
		DialogHelper.showWarningMessage(xContext, dialog, "Saved MT system not available!");
		logger.info("System not found! Using default system to translate...");
		targetTextField.setText("");
		return null;
	}

	/**
	 * Adds available system language lists to list boxes before the dialog is open.
	 */
	private void configureInitialSetup() {
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
			this.savedSourceLang = selectedSourceLanguage;
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
			this.savedTargetLang = selectedTargetLanguage;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String[] domains = getDomains(selectedSourceLanguage, selectedTargetLanguage).toArray(String[]::new);
		XControl domainBoxControl = m_xControlContainer.getControl("Domains");
		XPropertySet domainBoxProps = UnoRuntime.queryInterface(XPropertySet.class, domainBoxControl.getModel());
		try {
			domainBoxProps.setPropertyValue("StringItemList", domains);
			String selectedDomain = domains[0];
			short[] selectedNr = new short[] { (short) 0 };
			for (int i = 0; i < domains.length; i++) {
				if (domains[i].contentEquals(savedDomain)) {
					selectedNr[0] = (short) i;
					selectedDomain = savedDomain;
					break;
				}
			}
			domainBoxProps.setPropertyValue("SelectedItems", selectedNr);
			this.savedDomain = selectedDomain;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		saveConfiguration();
	}

	/**
	 * Get all available domains for currently selected sourceLanguage and targetLanguage
	 * @param selectedSourceLang
	 * @param selectedTargetLang
	 * @return
	 */
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
	 * Get all available target languages for currently selected sourceLanguage
	 * @param selectedSourceLang
	 * @return
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
	 * Get all available sourceLanguages
	 * @return
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
			onTranslateButtonPressed();
			return true;
		} else if (methodName.equals(actionInsert)) {
			onInsertButtonPressed();
			return true;
		} else if (methodName.contentEquals(actionChangeSourceLang)) {
			if(!silentSelection) {
				onQueryChanged();
			}
			return true;
		}
		else if (methodName.contentEquals(actionChangeTargetLang)) {
			if(!silentSelection) {
				onQueryChanged();
			}
			return true;
		}
		else if (methodName.contentEquals(actionChangeDomain)) {
			if(!silentSelection) {
				saveConfiguration();
			}
			return true;
		}
		return false; // Event was not handled
	}

	@Override
	public String[] getSupportedMethodNames() {
		return supportedActions;
	}

}

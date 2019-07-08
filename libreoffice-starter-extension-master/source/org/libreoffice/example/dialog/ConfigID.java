package org.libreoffice.example.dialog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.libreoffice.example.comp.TildeTranslatorImpl;
import org.libreoffice.example.helper.GetSystemList;

import com.sun.star.uno.XComponentContext;

/**
 * This dialog is shown, if UserID cannot be found or is invalid.
 * User is asked to go to website where UserID can be copied.
 * When user pastes the ID in the textbox and presses the button,
 * this class checks whether it is valid.
 *   If it is, then data file and variable in TildeTranslatorImpl are updated.
 *   If not, user cannot use translation service.
 *
 * @author arta.zena
 */

public class ConfigID {
	private static XComponentContext xContext;
	private static final String homeFolder = System.getProperty("user.home");

	/**
	 * @param xContext
	 */
	public ConfigID(XComponentContext xContext) {
		this.xContext = xContext;
	}

	/**
	 * ClientID is stored in a file that is located in users home folder.
	 * If there is no ID in the file or the ID is invalid or the file does not exist,
	 * user gets ID configurationID.
	 * If it is valid, it is set to ClientID variable.
	 *
	 * @exception IOException if reading/creating file failed
	 */
	public static void configureID() {
		File dataFile = new File(homeFolder + File.separator +"tildeID");
		if (dataFile.isFile()) {
			try {
				BufferedReader reader = new BufferedReader(new FileReader(dataFile));
				String line = reader.readLine();
				if(line == null) {
					show();
				} else {
					boolean valid = checkID(line);
					if(valid) {
						setClientAndSystemIDs(line);
					} else {
						show();
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			Boolean isCreated = false;
			try {
				isCreated = dataFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
	    	if(isCreated) {
	        	show();
	    	}
		}
	}

	/**
	 * Public metod to launch dialog.
	 */
	private void show(){
		ConfigDialog configDialog = new ConfigDialog(xContext, homeFolder);
		configDialog.show();
	}

	/**
	 * To check if ID is valid, test translation is done.
	 * If it returns "", then it is valid. If null, then not.
	 * As first translation always takes a bit longer time (few seconds),
	 * this also speeds up translation for later use.
	 *
	 * @param inputID			ID that user wrote
	 * @exception IOException	if translation failed
	 * @return 					true if given ID is valid
	 */
	static boolean checkID (String inputID) {
		//TODO: Should call TildeTranslatorImpl.setClientID(clientID); And should make it to fail on GetSystemList call if the ClientID is not valid
		GetSystemList gsl = new GetSystemList();
		Boolean valid = gsl.checkIfValid(inputID);
		return valid;
	}

	public static void setClientAndSystemIDs (String clientID) {
		TildeTranslatorImpl.setClientID(clientID);
	}

}

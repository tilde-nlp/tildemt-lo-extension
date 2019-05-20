package org.libreoffice.example.dialog;

import java.io.IOException;

import org.libreoffice.example.comp.TildeTranslatorImpl;
import org.libreoffice.example.helper.TranslateAPI;

import com.sun.star.uno.XComponentContext;

public class Translate {

	private static String clientID;
	private static String smt = "smt-7060bc9b-7f6d-4978-a21b-591a13dbdea8"; // TODO: change to correct default. save to appdata?

	public Translate(XComponentContext xContext) {
		TildeTranslatorImpl t = new TildeTranslatorImpl(xContext);
		clientID = t.getClientID();
	}

	public String getTranslation(String text) throws Exception {
		return translate(smt, text);
	}

	/** if requested MT system exists, it is set;
	 * saves information of the system for ActionTwo and ActionThree to use*/
	public boolean setSmt (String languageFrom, String languageTo) {
		String new_smt = getSmtID(languageFrom, languageTo);
		if (new_smt == "") {
			return false;
		} else {
			this.smt = new_smt;
			return true;
		}
	}

	/** if all necessary data is ready, translate text via API */
	private String translate (String smt, String text){
		String translated = "";
		if (!smt.isEmpty()) {
			try {
				TranslateAPI translator = new TranslateAPI();
				translated = translator.translate(clientID, smt, text); //uses API
				System.out.println("translated:\t" + translated);
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Error: translating failed");
			}
		} else {
			System.out.println("translation:\tmt system not available");
			translated = text; // if requested system does not exist, return the original text
		}
		return translated;
	}

	/** returns MT system's ID based on selected languages
	 * TODO: dialog saves info too */
	private String getSmtID (String languageFrom, String languageTo) {
		String smt = "";
		String lv = "Latvian";
		String en = "English";
		String lt = "Lithuanian";
		String et = "Estonian";
		if (languageFrom.contentEquals(en) && languageTo.contentEquals(lt)) {
			smt = "smt-7060bc9b-7f6d-4978-a21b-591a13dbdea8";
		}
		else if (languageFrom.contentEquals(lt) && languageTo.contentEquals(en)) {
			smt = "smt-986e31a9-938a-4507-b2ce-f78b0fe13cf4";
		}
		else if (languageFrom.contentEquals(en) && languageTo.contentEquals(et)) {
			smt = "smt-e0a590e8-e45f-4206-a78e-ed9fde4762d5";
		}
		else if (languageFrom.contentEquals(en) && languageTo.contentEquals(lv)) {
			smt = "smt-b0b7cc68-1bb3-4a35-a5de-f2f86d4dadf1";
		}
		return smt;
	}

}
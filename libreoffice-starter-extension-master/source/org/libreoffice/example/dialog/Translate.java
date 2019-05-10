package org.libreoffice.example.dialog;

import java.io.IOException;

import org.libreoffice.example.helper.TranslateAPI;

public class Translate {

	private static String clientID = "u-72738618-8461-4ed4-a20b-33031a7ac036"; //TODO: change clientID set up
	private String smt = "smt-b0b7cc68-1bb3-4a35-a5de-f2f86d4dadf1"; // TODO: change; default to en->lv probably

	public Translate () {
		System.out.println("Translate constructor");
	}

	public String getTranslation(String from, String to, String text) throws Exception {
		if (from != null && to != null) {
			setSmt(from, to);
		}
		String result = translate(smt, text);
		return result;
	}

	public void setSmt (String languageFrom, String languageTo) {
		this.smt = getSmtID(languageFrom, languageTo);
	}

	private String translate (String smt, String text) throws Exception {
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
			System.out.println("translation:\t not translated");
			translated = text; // if requested system does not exist, return the same text
		}
		return translated;
	}

	//returns MT system's ID
	private String getSmtID (String languageFrom, String languageTo) {
		String smt = "";
		String lv = "Latvian";
		String en = "English";
		if (languageFrom.contentEquals(lv) && languageTo.contentEquals(en)) {
			smt = "smt-9c8cade7-91d9-434d-ae62-8ce69f7223de";
		}
		else if (languageFrom.contentEquals(en) && languageTo.contentEquals(lv)) {
			smt = "smt-16d2a887-317f-4ef4-976b-90bd8c5e1a46";
		}
		System.out.println("System ID:\t" + smt);
//		return smt;
		return "smt-b0b7cc68-1bb3-4a35-a5de-f2f86d4dadf1"; //TODO: remove when getSmtID() is done
	}

}

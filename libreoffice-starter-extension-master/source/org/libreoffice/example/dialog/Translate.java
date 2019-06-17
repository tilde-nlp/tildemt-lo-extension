package org.libreoffice.example.dialog;

/**
 * This class manages variables (ClientID and smt) that are
 * neccessary to translate text.
 * Memorises MT system's ID and users ClientID.
 *
 * @author arta.zena
 */
public class Translate {
//
//	private static String clientID;
//	/** MT system's id; now to default */
//	private static String smt = "smt-f2e35605-1d5e-4d69-8664-d27a71a1ac26"; // TODO
//
//	/**
//	 * @param xContext
//	 */
//	public Translate(XComponentContext xContext) {
//		TildeTranslatorImpl t = new TildeTranslatorImpl(xContext);
//		clientID = t.getClientID();
//	}
//
//	/**
//	 * @param text			one paragraph of translatable text
//	 * @return				paragraphs translation
//	 * @throws Exception	if transaltion failed
//	 */
//	public String getTranslation(String text) throws Exception {
//		String translated = "";
//		if (!smt.isEmpty()) {
//			try {
//				TranslateAPI translator = new TranslateAPI();
//				translated = translator.translate(clientID, smt, text); //uses API
//				System.out.println("translated:\t" + translated);
//			} catch (IOException e) {
//				e.printStackTrace();
//				System.out.println("Error: translating failed");
//			}
//		} else {
//			System.out.println("translation:\tmt system not available");
//			translated = text; // if requested system does not exist, return the original text
//		}
//		return translated;
//	}
//
//	/**
//	 * If requested MT system exists, it is set;
//	 * saves information of the system for ActionTwo and ActionThree to use
//	 *
//	 * @param languageFrom	language to translate from
//	 * @param languageTo	language to translate to
//	 * @return				whether selected system exists
//	 */
//	public boolean setSmt (String languageFrom, String languageTo) {
//		String new_smt = getSmtID(languageFrom, languageTo);
//		if (new_smt == "") {
//			return false;
//		} else {
//			this.smt = new_smt;
//			return true;
//		}
//	}
//
//	/**
//	 * Returns MT system's ID based on selected languages
//	 * TODO: dialog saves info too
//	 *
//	 * @param languageFrom	language to translate from
//	 * @param languageTo	language to translate to
//	 * @return				system ID or empty string if doesn't exist
//	 */
//	private String getSmtID (String languageFrom, String languageTo) {
//		String smt = "";
//		String lv = "Latvian";
//		String en = "English";
//		String lt = "Lithuanian";
//		String et = "Estonian";
//		String sw = "Swedish";
//		if (languageFrom.contentEquals(en) && languageTo.contentEquals(lt)) {
//			smt = "smt-7060bc9b-7f6d-4978-a21b-591a13dbdea8";
//		}
//		else if (languageFrom.contentEquals(lt) && languageTo.contentEquals(en)) {
//			smt = "smt-986e31a9-938a-4507-b2ce-f78b0fe13cf4";
//		}
//		else if (languageFrom.contentEquals(en) && languageTo.contentEquals(lv)) {
//			smt = "smt-16d2a887-317f-4ef4-976b-90bd8c5e1a46";
//		}
//		else if (languageFrom.contentEquals(lv) && languageTo.contentEquals(en)) {
//			smt = "smt-0ad66ead-c181-4d0d-859a-50a0f51e2e03";
//		}
//		else if (languageFrom.contentEquals(sw) && languageTo.contentEquals(en)) {
//			smt = "smt-abf49352-0264-4480-bcfb-a7c7fabd2b0f";
//		}
//		else if (languageFrom.contentEquals(en) && languageTo.contentEquals(sw)) {
//			smt = "smt-f2e35605-1d5e-4d69-8664-d27a71a1ac26";
//		}
//		return smt;
//	}

}
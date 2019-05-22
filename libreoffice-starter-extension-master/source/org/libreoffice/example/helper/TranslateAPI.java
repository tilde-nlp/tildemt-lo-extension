package org.libreoffice.example.helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * API used that returns translation of the given text
 * using given MT system and ClientID
 *
 * @author arta.zena
 */
public class TranslateAPI {

	public TranslateAPI() throws IOException {
	}

	/**
	 * This method uses parameters to connect to "Tilde MT"
	 * server and get the translation of the text.
	 *
	 * @param clientID is individual user's ClientID
	 * @param systemID is MT system's ID for specified languages
	 * @param text is text that user wants to translate
	 * @return translation
	 */
	public String translate (String clientID, String systemID, String text){
		String answer = "";
		try {
			HttpURLConnection postConnection = getConnection(clientID, systemID, text);
		    int responseCode = postConnection.getResponseCode();
		    System.out.println("POST Response:\t" + responseCode
		    		+ " " + postConnection.getResponseMessage());

		    // 200 is a response code for successful connection. Receiving data:
		    if (responseCode == 200) {
		    	InputStream inputStream = postConnection.getInputStream();
		    	BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
	            String inputLine;
	            StringBuffer response = new StringBuffer();

	            while ((inputLine = in .readLine()) != null) {
	                response.append(inputLine); // TODO: atbilde nesatur garumzīmes
	            } in .close();

	            //extract and save the translation
	            answer = response.toString();
	            answer = getTranslationFromJSON(answer);
		    } else if (responseCode == 401){
		    	answer = null; //for ConfigID to check
		    } else {
		        System.out.println("POST did not work");
		    }
		} catch (Exception e) {
			e.printStackTrace();
		}
		return answer;
	}

	/**
	 * @param clientID		users individual Client ID
	 * @param systemID		MT system's ID
	 * @param text			translatable text
	 * @return				connection to system
	 * @throws Exception	if any process in getting the connecton fails
	 */
	private HttpURLConnection getConnection(String clientID, String systemID, String text) throws Exception {
		String POST_PARAMS = 	"{	\"systemID\": \"" 	+ 	systemID +
									"\", \"text\": \"" 		+ 	text + "\" }";
		URL obj = new URL("https://letsmtdev.tilde.lv/ws/service.svc/json/TranslateEx");

		HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
	    connection.setRequestMethod("POST");
	    connection.setRequestProperty("client-id", clientID);
	    connection.setRequestProperty("Content-Type", "application/json");

	    //sending data
	    connection.setDoOutput(true);
	    OutputStream os = connection.getOutputStream();
	    os.write(POST_PARAMS.getBytes());
	    os.flush();
	    os.close();

		return connection;
	}

	/**
	 * Parses JSON string to retrieve only specified value
	 *
	 * @param str is JSON string
	 * @return value for specified key
	 */
	public String getTranslationFromJSON (String str) {
		String translation = "";
		String value = "translation";
		try {
			JSONObject obj = new JSONObject(str);
			translation = obj.getString(value);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return translation;
	}

}















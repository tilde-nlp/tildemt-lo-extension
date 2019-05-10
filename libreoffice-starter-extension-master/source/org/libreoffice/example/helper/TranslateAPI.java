package org.libreoffice.example.helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

public class TranslateAPI {

	public TranslateAPI() throws IOException {
	}

	public String translate (String clientID, String systemID, String text){
		String answer = null;
		try {
			//TODO: ends with return -> null -> translate

			HttpURLConnection postConnection = getConnection(clientID, systemID, text);
		    int responseCode = postConnection.getResponseCode();
		    System.out.println("POST Response:\t" + responseCode
		    		+ " " + postConnection.getResponseMessage());

		    // 200 is a response code for successful connection. Receiving data:
		    if (responseCode == 200) {
		    	BufferedReader in = new BufferedReader(new InputStreamReader(postConnection.getInputStream()));
	            String inputLine;
	            StringBuffer response = new StringBuffer();

	            while ((inputLine = in .readLine()) != null) {
	                response.append(inputLine);
	            } in .close();

	            //extract and save the translation
	            answer = response.toString();
	            System.out.println("Full response:\t" + answer);
	            answer = getTranslationFromJSON(answer);
		    } else {
		        System.out.println("POST did not work");
		    }
		} catch (Exception e) {
			e.printStackTrace();
		}
		return answer;
	}

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

	public String getTranslationFromJSON (String str) {
		String translation = "";
		try {
			JSONObject obj = new JSONObject(str);
			translation = obj.getString("translation");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return translation;
	}

}















package org.libreoffice.example.helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

//import org.json.JSONException;
//import org.json.JSONObject;

public class TranslateAPI {

	public TranslateAPI() throws IOException {
 	}
	
	public String connPOST (String clientID, String systemID, String text) throws IOException {

		String answer = null;
		try {
			//TODO: ends with return -> null
			//set parameters
			String POST_PARAMS = 	"{ \"systemID\": \"" 	+ 	systemID +
									"\", \"text\": \"" 		+ 	text + "\" }";
			URL obj = new URL("https://letsmtdev.tilde.lv/ws/service.svc/json/TranslateEx");
			
			//configure connection
			HttpURLConnection postConnection = (HttpURLConnection) obj.openConnection();
		    postConnection.setRequestMethod("POST");
		    postConnection.setRequestProperty("client-id", clientID);
		    postConnection.setRequestProperty("Content-Type", "application/json");
			
		    //sending data
		    postConnection.setDoOutput(true);
		    OutputStream os = postConnection.getOutputStream();
		    os.write(POST_PARAMS.getBytes());
		    os.flush();
		    os.close();
		    
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
//	            answer = getTranslationFromJSON(answer);
//	            System.out.println("Back in game:\t" + answer);
		    } else {
		        System.out.println("POST did not work");
		    }
		} catch (IOException e) {//nope
			e.printStackTrace();
		}
		return answer;
	}
	
//	private String getTranslationFromJSON (String str) {
//		String translation = null;
//		String json = "{\"translation\":\"ye!\"}";
//		System.out.println("  JSONtest1 =\t" + json);
//		try {
//			JSONObject obj = new JSONObject(str);
//			translation = obj.getString("translation");
//		} catch (JSONException e) {
//			e.printStackTrace();
//			System.out.println(e.getMessage());
//		}
////			System.out.println("  JSONtest2 =\t" + translation + "......." + obj);
//		
//			System.out.println("  JSONtest3 =\t" + translation);
//		return translation;
//	}
	
}















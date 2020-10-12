package com.tilde.mt.lotranslator;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.json.JSONObject;
import org.json.JSONTokener;

import com.google.gson.Gson;
import com.tilde.mt.lotranslator.models.TildeMTSystemList;

public class TildeMTAPIClient {
	private String ClientID = null;
    
    private final String TranslationAPI = "https://ltmt.tilde.lv/ws/Service.svc/json";
    private final String AppID = "TildeMT|Plugin|LibreOffice";
    
    public TildeMTAPIClient(String clientID) {
    	this.ClientID = clientID;
    }
    
	public String translate(String systemID, String inputText) throws UnsupportedEncodingException{
		String translation = String.format(this.TranslationAPI + "/TranslateEx?appID=%s&systemID=%s&text=%s", this.AppID, systemID, URLEncoder.encode(inputText, "UTF-8"));
		
		if(translation != null) {
			JSONObject object = (JSONObject) new JSONTokener(translation).nextValue();
			return object.getString("translation");
		}
		else {
			return null;
		}
	}

	public TildeMTSystemList GetSystemList() {
		String systems = this.Request(this.TranslationAPI + "/GetSystemList?appID=" + this.AppID); 
		
		if(systems != null) {
			Gson gson = new Gson();
			TildeMTSystemList systemList = gson.fromJson(systems, TildeMTSystemList.class);
			
			return systemList;
		}
		return null;
	}
	
	private String Request(String url) {
		HttpURLConnection connection = null;
		InputStream in = null;
		BufferedReader reader = null;
		
		String res = "";
		Boolean success = false;
		try {
			URL urls = new URL(url);

			connection = (HttpURLConnection) urls.openConnection();
			connection.setRequestMethod("GET");
			connection.setRequestProperty("client-id", this.ClientID);

			in = new BufferedInputStream(connection.getInputStream());
			reader = new BufferedReader(new InputStreamReader(in));

			String line = "";
			while ((line = reader.readLine()) != null) {
				res += line;
			}
			
			success = true;
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if(connection != null) {
				connection.disconnect();
			}
			if(in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return success ? res: null;
	}
}

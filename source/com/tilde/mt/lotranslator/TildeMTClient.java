package com.tilde.mt.lotranslator;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import com.google.gson.Gson;
import com.tilde.mt.lotranslator.models.TildeMTSystemList;
import com.tilde.mt.lotranslator.models.TildeMTTranslation;

public class TildeMTClient {
	private String ClientID = null;
    
    private final String TranslationAPI = "https://ltmt.tilde.lv/ws/Service.svc/json";
    private final String AppID = "TildeMT|Plugin|LibreOffice";
    private final Logger logger = new Logger(this.getClass().getName());
    
    public TildeMTClient(String clientID) {
    	this.ClientID = clientID;
    }
    
	public String translate(String systemID, String inputText) {
		logger.info(String.format("translate text: system: %s, text: %s", systemID, inputText));
		
		String encodedText = null;
		try {
			encodedText = URLEncoder.encode(inputText, "UTF-8");
		}
		catch(Exception ex) {
			ex.printStackTrace();
			return null;
		}
		
		String translationUrl = String.format(this.TranslationAPI + "/TranslateEx?appID=%s&systemID=%s&text=%s", this.AppID, systemID, encodedText);
		String translation = this.Request(translationUrl); 
		
		if(translation != null) {
			Gson gson = new Gson();
			TildeMTTranslation translated = gson.fromJson(translation, TildeMTTranslation.class);
			
			logger.info(String.format("translation: %s", translated.translation));
			return translated.translation;
		}
		else {
			return null;
		}
	}

	public TildeMTSystemList GetSystemList() {
		logger.info(String.format("Get systems"));
		
		String systems = this.Request(this.TranslationAPI + "/GetSystemList?appID=" + this.AppID); 
		
		if(systems != null) {
			Gson gson = new Gson();
			TildeMTSystemList systemList = gson.fromJson(systems, TildeMTSystemList.class);
			
			logger.info(String.format("Systems found: %s", systemList.System.length));
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
			reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));

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

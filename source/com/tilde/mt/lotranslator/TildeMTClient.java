package com.tilde.mt.lotranslator;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

import com.google.gson.Gson;
import com.tilde.mt.lotranslator.models.TildeMTSystemList;
import com.tilde.mt.lotranslator.models.TildeMTTranslation;

public class TildeMTClient {
	private String ClientID = null;
    
    private final String TranslationAPI = "https://ltmt.tilde.lv/ws/Service.svc/json";
    private final String AppID = "TildeMT|Plugin|LibreOffice";
    private final Logger logger = new Logger(this.getClass().getName());
    
    private TildeMTSystemList cachedSystemList = null;
    
    public TildeMTClient(String clientID) {
    	this.ClientID = clientID;
    }
    
	public CompletableFuture<String> Translate(String systemID, String inputText) {
		logger.info(String.format("translate text: system: %s, text: %s", systemID, inputText));
		
		String translationUrl = String.format(this.TranslationAPI + "/TranslateEx?appID=%s&systemID=%s&text=%s", URLEncoder.encode(this.AppID, StandardCharsets.UTF_8), systemID, URLEncoder.encode(inputText, StandardCharsets.UTF_8));
		
		return this.Request(translationUrl).thenApply(translation -> {
			Gson gson = new Gson();
			TildeMTTranslation translated = gson.fromJson(translation, TildeMTTranslation.class);
			
			logger.info(String.format("translation: %s", translated.translation));
			return translated.translation;
		});
	}

	public TildeMTSystemList GetSystemList() {
		if(cachedSystemList == null) {
			logger.info(String.format("Get systems"));
		
			String systems;
			try {
				// TODO: convert GetSystemList to non-blocking
				systems = this.Request(this.TranslationAPI + "/GetSystemList?appID=" + URLEncoder.encode(this.AppID, StandardCharsets.UTF_8)).get();
				
				if(systems != null && !systems.equals("")) {
					Gson gson = new Gson();
					TildeMTSystemList systemList = gson.fromJson(systems, TildeMTSystemList.class);
					logger.info(systems);
					logger.info(String.format("Systems found: %s", systemList.System.length));
					this.cachedSystemList = systemList;
					return systemList;
				}
			} 
			catch (Exception e) {
				e.printStackTrace();
			}
			
			return null;
		}
		else {
			return this.cachedSystemList;
		}
	}
	
	private CompletableFuture<String> Request(String url) {
		URI uri = null;
		
		try {
			uri = new URL(url).toURI();
		} 
		catch (MalformedURLException | URISyntaxException e1) {
			e1.printStackTrace();
		}
		
		HttpClient client = HttpClient.newHttpClient();
	    HttpRequest request = HttpRequest.newBuilder()
	          .uri(uri)
	          .header("client-id", this.ClientID)
	          .build();

	    return client.sendAsync(request, BodyHandlers.ofString()).thenApply(HttpResponse::body);
	}
}

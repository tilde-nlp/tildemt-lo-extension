package com.tilde.mt.lotranslator;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

import com.google.gson.Gson;
import com.tilde.mt.lotranslator.models.ErrorResult;
import com.tilde.mt.lotranslator.models.TildeMTDocTranslateState;
import com.tilde.mt.lotranslator.models.TildeMTStartDocTranslate;
import com.tilde.mt.lotranslator.models.TildeMTSystemList;
import com.tilde.mt.lotranslator.models.TildeMTTranslation;
import com.tilde.mt.lotranslator.models.TildeMTUserData;

public class TildeMTClient {
	private String ClientID = null;
    
    private final String TranslationAPI = "https://ltmt.tilde.lv/ws/Service.svc/json";
    private final String AppID = "TildeMT|Plugin|LibreOffice";
    private final Logger logger = new Logger(this.getClass().getName());
    private final Gson gson = new Gson();
    
    private TildeMTSystemList cachedSystemList = null;
    
    public TildeMTClient(String clientID) {
    	this.ClientID = clientID;
    }
    
	public CompletableFuture<TildeMTTranslation> Translate(String systemID, String inputText) {
		logger.info(String.format("translate text: system: %s, text: %s", systemID, inputText));
		
		String url = String.format(this.TranslationAPI + "/TranslateEx?appID=%s&systemID=%s&text=%s", 
			URLEncoder.encode(this.AppID, StandardCharsets.UTF_8), 
			systemID, 
			URLEncoder.encode(inputText, StandardCharsets.UTF_8)
		);
		
		return this.Request(url, false, null).thenApply(rawResult -> {
			TildeMTTranslation result = gson.fromJson(rawResult, TildeMTTranslation.class);
			
			logger.info(String.format("translation: %s", rawResult));
			return result;
		});
	}

	public CompletableFuture<TildeMTUserData> GetUserData(){
		logger.info(String.format("fetching user data..."));
		
		String url = String.format(this.TranslationAPI + "/GetUserInfo?appID=%s", 
			URLEncoder.encode(this.AppID, StandardCharsets.UTF_8)
		);
		
		return this.Request(url, false, null).thenApply(data -> {
			TildeMTUserData userData = gson.fromJson(data, TildeMTUserData.class);
			
			logger.info(String.format("user data: %s", userData));
			return userData;
		});
	}
	
	public CompletableFuture<ErrorResult<byte[]>> DownloadDocumentTranslation(String documentID){
		logger.info("Download document translation");

		String url = String.format(this.TranslationAPI + "/DownloadDocumentTranslation?appID=%s&id=%s", 
			URLEncoder.encode(this.AppID, StandardCharsets.UTF_8),
			documentID
		);
		
		return this.Request(url, false, null).thenApply(rawResult -> {
			ErrorResult<byte[]> result = new ErrorResult<byte[]>();
			
			try {
				result.Error = gson.fromJson(rawResult, TildeMTDocTranslateState.class);
			}
			catch(Exception ex) {
				result.Result = gson.fromJson(rawResult, byte[].class);
			}
			
			logger.info(String.format("DocTranslate result: %s", result));
			return result;
		});
	}

	public CompletableFuture<ErrorResult<String>> StartDocumentTranslation(TildeMTStartDocTranslate data){
		logger.info("Start document translation");
		
		data.AppID = this.AppID;
		String reqData = gson.toJson(data);
		
		String url = String.format(this.TranslationAPI + "/StartDocumentTranslation");
		
		return this.Request(
			url, 
			true, 
			reqData
		).thenApply(rawResult -> {
			ErrorResult<String> result = new ErrorResult<String>();
			
			try {
				TildeMTDocTranslateState tildeError = gson.fromJson(rawResult, TildeMTDocTranslateState.class);
				result.Error = tildeError;
			}
			catch(Exception ex) {
				result.Result = rawResult.replace("\"", "");
			}
			
			logger.info(String.format("DocTranslate result: %s", result));
			return result;
		});
	}
	
	public CompletableFuture<TildeMTDocTranslateState> GetDocumentTranslationState(String documentID){
		logger.info("Document translation status");

		String url = String.format(this.TranslationAPI + "/GetDocumentTranslationState?appID=%s&id=%s", 
			URLEncoder.encode(this.AppID, StandardCharsets.UTF_8),
			documentID
		);
		
		return this.Request(url, false, null).thenApply(rawResult -> {
			TildeMTDocTranslateState result = gson.fromJson(rawResult, TildeMTDocTranslateState.class);

			logger.info(String.format("DocTranslate result: %s", result));
			return result;
		});
	}
	

	public TildeMTSystemList GetSystemList() {
		if(cachedSystemList == null) {
			logger.info(String.format("Get systems"));
		
			String systems;
			try {
				// TODO: convert GetSystemList to non-blocking
				systems = this.Request(this.TranslationAPI + "/GetSystemList?appID=" + URLEncoder.encode(this.AppID, StandardCharsets.UTF_8), false, null).get();
				
				if(systems != null && !systems.equals("")) {
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
	
	private CompletableFuture<String> Request(String url, Boolean isPOST, String postData) {
		URI uri = null;
		
		try {
			uri = new URL(url).toURI();
		} 
		catch (MalformedURLException | URISyntaxException e1) {
			e1.printStackTrace();
		}
		
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest.Builder rawRequest = HttpRequest.newBuilder()
	          .uri(uri)
	          .header("client-id", this.ClientID);
		
		if(isPOST) {
			rawRequest.POST(BodyPublishers.ofString(postData));
		}
	          
      	HttpRequest request = rawRequest.build();

	    return client.sendAsync(request, BodyHandlers.ofString()).thenApply(HttpResponse::body);
	}
}

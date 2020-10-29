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

/**
 * API wrapper for Tilde MT API in Java
 * @author guntars.puzulis
 *
 */
public class TildeMTClient {
	private String ClientID = null;
    
    private final String TranslationAPI = "https://ltmt.tilde.lv/ws/Service.svc/json";
    private final String AppID = "TildeMT|Plugin|LibreOffice";
    private final Logger logger = new Logger(this.getClass().getName());
    private final Gson gson = new Gson();
    
    /**
     * System list cache, quick optimization as this extension will not be in memory for long anyways.
     */
    private TildeMTSystemList cachedSystemList = null;
    
    public TildeMTClient(String clientID) {
    	this.ClientID = clientID;
    }
    
	public CompletableFuture<TildeMTTranslation> Translate(String systemID, String inputText) {
		logger.info(String.format("Translate... system: %s, text: %s", systemID, inputText));
		
		String url = String.format(this.TranslationAPI + "/TranslateEx?appID=%s&systemID=%s&text=%s", 
			URLEncoder.encode(this.AppID, StandardCharsets.UTF_8), 
			systemID, 
			URLEncoder.encode(inputText, StandardCharsets.UTF_8)
		);
		
		return this.Request(url, false, null).thenApply(rawResult -> {
			TildeMTTranslation result = gson.fromJson(rawResult, TildeMTTranslation.class);
			
			logger.info("Translate: " + rawResult);
			return result;
		});
	}

	public CompletableFuture<TildeMTUserData> GetUserData(){
		logger.info(String.format("GetUserData..."));
		
		String url = String.format(this.TranslationAPI + "/GetUserInfo?appID=%s", 
			URLEncoder.encode(this.AppID, StandardCharsets.UTF_8)
		);
		
		return this.Request(url, false, null).thenApply(rawData -> {
			TildeMTUserData userData = gson.fromJson(rawData, TildeMTUserData.class);
			
			logger.info("GetUserData: " + rawData);
			return userData;
		});
	}
	
	public CompletableFuture<ErrorResult<byte[]>> DownloadDocumentTranslation(String documentID){
		logger.info("DownloadDocumentTranslation...");

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
			
			logger.info(String.format("DocTranslate: %s", result));
			return result;
		});
	}

	public CompletableFuture<ErrorResult<String>> StartDocumentTranslation(TildeMTStartDocTranslate data){
		logger.info("StartDocumentTranslation...");
		
		data.AppID = this.AppID;
		String reqData = gson.toJson(data);
		
		String url = String.format(this.TranslationAPI + "/StartDocumentTranslation");
		
		return this.Request(url, true, reqData).thenApply(rawResult -> {
			ErrorResult<String> result = new ErrorResult<String>();
			
			try {
				TildeMTDocTranslateState tildeError = gson.fromJson(rawResult, TildeMTDocTranslateState.class);
				result.Error = tildeError;
			}
			catch(Exception ex) {
				result.Result = rawResult.replace("\"", "");
			}
			
			logger.info(String.format("StartDocumentTranslation: %s", rawResult));
			return result;
		});
	}
	
	public CompletableFuture<Boolean> RemoveDocumentTranslation(String documentID){
		logger.info("RemoveDocumentTranslation...");
		String url = String.format(this.TranslationAPI + "/RemoveDocumentTranslation?appID=%s&id=%s", 
			URLEncoder.encode(this.AppID, StandardCharsets.UTF_8),
			documentID
		);
		
		return this.Request(url, false, null).thenApply(rawResult -> {
			logger.info(String.format("RemoveDocumentTranslation: %s", rawResult));
			return true;
		});
	}
	
	public CompletableFuture<TildeMTDocTranslateState> GetDocumentTranslationState(String documentID){
		logger.info("GetDocumentTranslationState...");

		String url = String.format(this.TranslationAPI + "/GetDocumentTranslationState?appID=%s&id=%s", 
			URLEncoder.encode(this.AppID, StandardCharsets.UTF_8),
			documentID
		);
		
		return this.Request(url, false, null).thenApply(rawResult -> {
			TildeMTDocTranslateState result = gson.fromJson(rawResult, TildeMTDocTranslateState.class);

			logger.info(String.format("GetDocumentTranslationState: %s", rawResult));
			return result;
		});
	}
	
	public TildeMTSystemList GetSystemList() {
		if(cachedSystemList == null) {
			logger.info(String.format("GetSystemList..."));
		
			String systems;
			try {
				systems = this.Request(this.TranslationAPI + "/GetSystemList?appID=" + URLEncoder.encode(this.AppID, StandardCharsets.UTF_8), false, null).get();
				
				if(systems != null && !systems.equals("")) {
					TildeMTSystemList systemList = gson.fromJson(systems, TildeMTSystemList.class);

					logger.info(String.format("GetSystemList: %s", systems));
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

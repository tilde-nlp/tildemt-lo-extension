package org.libreoffice.example.helper;

import java.io.IOException;

import org.libreoffice.example.helper.LetsMT.TranslatePayloadM;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * API used that returns translation of the given text
 * using given MT system and ClientID
 *
 * @author arta.zena
 */
public class TildeMTAPIClient {
	private String clientID = null;
	private final LetsMTAPI service;

	public TildeMTAPIClient() {
		// Create TildeMT service proxy
		if (service == null) {
			Retrofit retrofit = null;
			try {
			retrofit = new Retrofit.Builder()
					.baseUrl("https://www.letsmt.eu/ws/service.svc/json/")
					.addConverterFactory(GsonConverterFactory.create())
					.build();
			} catch (Error e) {
				e.printStackTrace();
			}

			service = retrofit.create(LetsMTAPI.class);
		}

		return service;
	}

	public void setClientID(String id) {
    	clientID = id;
	}

	public void getClientID(String id) {
    	return clientID;
	}

	public String translate(String systemID, String text) {
		TranslatePayloadM translatable = new TranslatePayloadM(systemID, text);
		Response<String> result = null;

		try {
			Call<String> call = service.getTranslation(clientID, translatable);
			result = call.execute();
		} catch (Exception e1) {
			e1.printStackTrace();
			//TODO: should fail with message on error
		}

		String translation = result.body();
		System.out.println("translation = " + translation);

		return translation;
	}

	private SystemListM getSystemList() {
		try {
			Call<SystemListM> call = service.getSystemList(clientID); //TODO
			Response<SystemListM> result = call.execute();
			return result.body();
		} catch (Exception e1) {
			e1.printStackTrace();
			//TODO: should fail with message on error
		}
	}
}

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
public class TranslateAPI {

	public TranslateAPI() throws IOException {
	}

	public String translate (String clientID, String systemID, String text){
		String translated = getRetrofitConnection(
				"u-f08e4de3-8eed-4c78-abe7-7332619d13c0",
				"smt-8d6f52a3-7f5a-4cca-a664-da222afe18b5",
				"tekstas"); //TODO
		return translated;
	}

	private String getRetrofitConnection(String clientID, String systemID, String text) {
		TranslatePayloadM translatable = new TranslatePayloadM(systemID, text);

		Retrofit retrofit = null;
		try {
		retrofit = new Retrofit.Builder()
			    .baseUrl("https://www.letsmt.eu/ws/service.svc/json/")
			    .addConverterFactory(GsonConverterFactory.create())
			    .build();
		} catch (Error e) {
			e.printStackTrace();
		}

		LetsMTAPI service = retrofit.create(LetsMTAPI.class);
		Call<String> call = null;
		try {
			call = service.getTranslation(clientID, translatable);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		Response<String> result = null;
		try {
			result = call.execute();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		String translation = result.body();
		System.out.println("translation = " + translation);

		return translation;
	}

}

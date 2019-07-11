//TODO: remove this implementation as the functionality is moved to TranslateAPI.java
package org.libreoffice.example.helper;

import java.io.IOException;

import org.libreoffice.example.comp.TildeTranslatorImpl;
import org.libreoffice.example.helper.LetsMT.SystemListM;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
/**
 * make connection
 * set params and headers
 * get response json with system list
 * parse the json
 * put system list in dictionary
 *
 * @author arta.zena
 *
 */
public class GetSystemList {

	public static void set (String id) {
		Response<SystemListM> result = getRetrofitResult(id);
		TildeTranslatorImpl.setSystemList(result.body());
	}

	public Boolean checkIfValid (String id) {
		Response<SystemListM> result = getRetrofitResult(id);
		Boolean valid = (result.code() == 200);
		return valid;
	}

	private static Response<SystemListM> getRetrofitResult (String id) {
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
		Call<SystemListM> call = null;
		try {
			call = service.getSystemList(id); //TODO
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		Response<SystemListM> result = null;
		try {
			result = call.execute();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		return result;
	}
}

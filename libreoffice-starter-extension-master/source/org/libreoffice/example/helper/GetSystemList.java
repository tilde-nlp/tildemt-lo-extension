package org.libreoffice.example.helper;

import java.io.IOException;

import org.libreoffice.example.helper.LetsMT.SystemList;

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

	public GetSystemList () {}

	public static void set (String id) {
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
		Call<SystemList> call = null;
		try {
			call = service.getSystemList("u-f08e4de3-8eed-4c78-abe7-7332619d13c0"); //TODO
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		Response<SystemList> result = null;
		try {
			result = call.execute();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		SystemList systemList = result.body();
		System.out.println("systemlist = " + systemList.toString());
	}
}

package org.libreoffice.example.helper;

import org.libreoffice.example.helper.LetsMT.SystemList;
import org.libreoffice.example.helper.LetsMT.TranslatePayload;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface LetsMTAPI {
	@GET("GetSystemList?appID=libre-extension")
	Call<SystemList> getSystemList(
			@Header("client-id") String clientID
			);

	@POST("Translate?appID=libre-extension")
	Call<String> getTranslation(
			@Body TranslatePayload translatePayload
			);
}

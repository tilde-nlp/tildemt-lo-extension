package org.libreoffice.example.helper;

import org.libreoffice.example.helper.LetsMT.SystemListM;
import org.libreoffice.example.helper.LetsMT.TranslatePayloadM;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface LetsMTAPI {
	@GET("GetSystemList?appID=libre-extension")
	Call<SystemListM> getSystemList(
			@Header("client-id") String clientID
			);

	@POST("Translate?appID=libre-extension")
	Call<String> getTranslation(
			@Header("client-id") String clientID,
			@Body TranslatePayloadM translatePayload
			);
}

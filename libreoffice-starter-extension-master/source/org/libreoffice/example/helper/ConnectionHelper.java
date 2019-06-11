package org.libreoffice.example.helper;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;


public class ConnectionHelper {

	public ConnectionHelper () {
	}

	public static HttpURLConnection getConnection(
			String URL,
			Map<String, String> get_params,
			String clientID,
			String method) // TODO enum
					throws Exception {
		URL url = null;
		if (method == "GET") {
			String params_string = getParamsString(get_params);
			System.out.println(params_string);
			url = new URL(URL + "?" + params_string);
		} else {
			url = new URL(URL);
		}

		Map<String, String> headers = new  HashMap<String, String>();
		headers.put("client-id", clientID); // TODO: change clientId
		headers.put("Content-Type", "application/json");

		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	    connection.setRequestMethod(method);

	    // set headers
	    for (Map.Entry<String,String> param : headers.entrySet()) {
	    	connection.setRequestProperty(param.getKey(), param.getValue());
	    }
	    connection.setDoOutput(true);

	    if (method == "POST") {
		    // create json string from post params
		    String get_params_json = JsonHelper.mapToJSON(get_params);
		    System.out.println(get_params_json);
		    // sending data
		    OutputStream os = connection.getOutputStream();
		    os.write(get_params_json.getBytes());
		    os.flush();
		    os.close();
	    }
		return connection;
	}

	public static String getParamsString(Map<String, String> params) //GET
	throws UnsupportedEncodingException{
		StringBuilder result = new StringBuilder();
		for (Map.Entry<String, String> entry : params.entrySet()) {
			result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
			result.append("=");
			result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
			result.append("&");
		}
		String resultString = result.toString();
		return resultString.length() > 0 ? resultString.substring(0, resultString.length() - 1) : resultString;
	}
}


package org.libreoffice.example.helper;

import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonHelper {
	/**
	 * Parses JSON string to retrieve only specified value
	 *
	 * @param json full json string
	 * @param key for whoch value will  be returned
	 * @return value for specified key
	 */
	public static String getValue (String json, String key) {
		String value = "";
		try {
			JSONObject jsonObject = new JSONObject(json);
			value = jsonObject.getString(key);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return value;
	}

	public static String mapToJSON(Map<String, String> map) {
		JSONObject json = new JSONObject();
		for (Map.Entry<String,String> i : map.entrySet()) {
			try {
				json.put(i.getKey(), i.getValue());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		String str_json = json.toString();
		return str_json;
	}
}

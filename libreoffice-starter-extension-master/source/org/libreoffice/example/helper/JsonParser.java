package org.libreoffice.example.helper;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonParser {
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
}

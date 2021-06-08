package io.mkeasy.utils;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class JSONUtil2 {

	public static JSONObject getJSONObject(JSONObject innerObj, String key) {
		try {
            return (JSONObject) innerObj.get(key);
		} catch (Exception e) {
			return new JSONObject();
		}
	}

	public static JSONArray getJSONArray(JSONObject innerObj, String key) {
		try {
			return (JSONArray) innerObj.get(key);
		} catch (Exception e) {
			return new JSONArray();
		}
	}

	public static String getValue(JSONObject tmpObj, String key) throws Exception {
		boolean has = tmpObj.has(key);
		String defaultValue = null;
		String value = has ? String.valueOf(tmpObj.get(key)):defaultValue;
		
		if(StringUtils.equals(value, defaultValue))
			return value;
		
		if(StringUtils.equals(value, "null"))
			return defaultValue;

		return value;
	}

}

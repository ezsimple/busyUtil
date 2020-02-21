package io.mkeasy.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;


public class JSONUtil {

	public static String getString(JSONObject params, String key) {
		if(isEmpty(params))
			return null;
		JSONArray arr = params.getJSONArray(key);
		return arr.getString(0);
	}

	public static void clear(Object obj) {

		if (obj instanceof JSONObject) {
			((JSONObject) obj).clear();
		}

		if (obj instanceof JSONArray) {
			((JSONArray) obj).clear();
		}

		if (obj instanceof org.json.JSONObject) {
			while(((org.json.JSONObject) obj).length()>0)
				((org.json.JSONObject)obj).remove((String) ((org.json.JSONObject)obj).keys().next());
		}

		if (obj instanceof org.json.JSONArray) {
			for(int i=0; i<((org.json.JSONArray) obj).length(); i++) {
				((org.json.JSONArray) obj).remove(i);
			}
		}

	}

	public static JSONObject toJSON(String strJsonObject) throws Exception {
		try {
            return new JSONObject().fromObject(strJsonObject);
		} catch (Exception e) {
			throw new Exception("JSONObject Format이 아닙니다. : "+e.getMessage());
		}
	}

	public static JSONObject toJSON(Map<String, Object> map) throws Exception {
		try {
            return new JSONObject().fromObject(map);
		} catch (Exception e) {
			throw new Exception("JSONObject Format이 아닙니다. : "+e.getMessage());
		}
	}

	public static Object toJSON(Object object) throws JSONException {
		if (object instanceof Iterable) {
			return new JSONArray().fromObject(object);
		}
		return new JSONObject().fromObject(object);
	}	

	public static boolean isEmpty(JSONObject object) {
		if(object == null) return true;
		return object.names() == null;
	}

	public static Map<String, Object> getMap(JSONObject object, String key) throws JSONException {
		return toMap(object.getJSONObject(key));
	}

	public static Map<String, Object> toMap(JSONObject object) throws JSONException {
		Map<String, Object> map = new HashMap();
		Iterator keys = object.keys();
		while (keys.hasNext()) {
			String key = (String) keys.next();
			map.put(key, fromJson(object.get(key)));
		}
		return map;
	}

	public static List toList(JSONArray array) throws JSONException {
		List list = new ArrayList();
		for (int i = 0; i < array.size(); i++) {
			list.add(fromJson(array.get(i)));
		}
		return list;
	}

	private static Object fromJson(Object json) throws JSONException {
		if (json == null) {
			return null;
		} else if (json instanceof JSONObject) {
			return toMap((JSONObject) json);
		} else if (json instanceof JSONArray) {
			return toList((JSONArray) json);
		} else {
			return json;
		}
	}

	public static boolean isValid(String json) {
		try {
			new JSONObject().fromObject(json);
		} catch (JSONException e1) {
			try {
				new JSONArray().fromObject(json);
			} catch (JSONException e2) {
				return false;
			}
		}
		return true;
	}

	public static String toPretty(JSONObject json) {
		return json==null?null:json.toString(2);
	}

	// org.json.JSONObject, org.json.JSONArray만 사용해야 합니다.
	// net.sf.json, org.json.simple 의 경우 날짜 변환에서 invalide json format string 을 만듭니다.
	@SuppressWarnings({ "unchecked" })
	public static org.json.JSONArray convertListToJson(List<Map<String, Object>> bankCdList) {
		org.json.JSONArray jsonArray = new org.json.JSONArray();
		for (Map<String, Object> map : bankCdList) {
			jsonArray.put(convertMapToJson(map));
		}
		return jsonArray;
	}

	// map 을 json 형태로 변형
	@SuppressWarnings({ "unchecked" })
	public static org.json.JSONObject convertMapToJson(Map<String, Object> map) {
		org.json.JSONObject json = new org.json.JSONObject();
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();
			json.put(key, value);
		}
		return json;
	}

}

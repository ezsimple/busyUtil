package io.mkeasy.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONUtil {

	public static String getString(JSONObject params, String key) {
		if(isEmpty(params))
			return null;
		JSONArray arr = params.getJSONArray(key);
		return arr.getString(0);
	}

	public static void clear(Object obj) {

		if (obj instanceof net.sf.json.JSONObject) {
			((net.sf.json.JSONObject) obj).clear();
		}

		if (obj instanceof net.sf.json.JSONArray) {
			((net.sf.json.JSONArray) obj).clear();
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

	public static Object toJSON(Object object) throws JSONException {
		if (object instanceof Map) {
			JSONObject json = new JSONObject();
			Map map = (Map) object;
			for (Object key : map.keySet()) {
				json.put(key.toString(), toJSON(map.get(key)));
			}
			return json;
		} else if (object instanceof Iterable) {
            JSONArray jsonArry = new JSONArray();
			for (Object value : ((Iterable)object)) {
				jsonArry.put(toJSON(value));
			}
			return jsonArry;
		} else {
			return object;
		}
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
		for (int i = 0; i < array.length(); i++) {
			list.add(fromJson(array.get(i)));
		}
		return list;
	}

	private static Object fromJson(Object json) throws JSONException {
		if (json == JSONObject.NULL) {
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
			new JSONObject(json);
		} catch (JSONException e1) {
			try {
				new JSONArray(json);
			} catch (JSONException e2) {
				return false;
			}
		}
		return true;
	}

	public static String toPretty(JSONObject json) {
		return json==null?null:json.toString(2);
	}

}
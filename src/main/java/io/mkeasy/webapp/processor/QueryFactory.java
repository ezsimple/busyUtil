package io.mkeasy.webapp.processor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.apache.commons.collections.map.CaseInsensitiveMap;
import org.json.JSONArray;

import io.mkeasy.utils.JSONUtil;
import io.mkeasy.utils.ListUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class QueryFactory {

	// Non Transaction Query
	public static Object execute(String ns, String nsId, CaseInsensitiveMap params) throws Exception {
		return ProcessorServiceFactory.executeQuery(ns, nsId, params);
	}

	// Transaction Query
	@Transactional
	public static Object executeTx(String ns, String nsId, CaseInsensitiveMap params) throws Exception {
		List<String> processorList = new ArrayList<String>();
		processorList.add("mybatis");
		ProcessorParam processorParam = new ProcessorParam(null);
		processorParam.setProcessorList(processorList);
		processorParam.setQueryPath(ns);
		processorParam.setAction(nsId);
		processorParam.setParams(params);
		return ProcessorServiceFactory.executeMainTransaction(processorParam);
	}

	// Get Query Results using CRUD query result
	public static Object getResult(final String ns, final String nsId, final Object result) {
		String key = ns+"."+nsId;
		Object map = ((Map<String, Object>) result).get(key);
		return map;
	}
	
	// result는 getResult(ns,nsId,result)의 값입니다.
	// result에서 첫번째 Integer 값만 가져옵니다.
	public static Integer toInt(Object result) {
		if(result instanceof Integer)
			return (Integer) result;
		Object[] obj = ListUtil.toArray(result);
		return (Integer) obj[0];
	}
	
	// result는 getResult(ns,nsId,result)의 값입니다.
	// result에서 첫번째 String 값만 가져옵니다.
	public static String toString(Object result) {
		if (result instanceof String)
			return String.valueOf(result);
		Object[] obj = ListUtil.toArray(result);
		return String.valueOf(obj[0]);
	}

	// result는 getResult(ns,nsId,result)의 값입니다.
	// result에서 List를 가져옵니다.
	public static List<Map<String, Object>> toList(Object result) {
		return ListUtil.toList((Map<String, Object>) result);
	}

	// result는 getResult(ns,nsId,result)의 값입니다.
	// result에서 JSONArray로 변환시켜 줍니다.
	public static JSONArray toJSONArray(Object result) {
		JSONArray json = (JSONArray) JSONUtil.toJSON(result);
		return json;
	}

}

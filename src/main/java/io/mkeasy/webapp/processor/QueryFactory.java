package io.mkeasy.webapp.processor;

import java.util.Map;

import javax.transaction.Transactional;

import org.apache.commons.collections.map.CaseInsensitiveMap;

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
		ProcessorParam processorParam = new ProcessorParam(null);
		processorParam.setQueryPath(ns);
		processorParam.setAction(nsId);
		processorParam.setParams(params);
		return ProcessorServiceFactory.executeMainTransaction(processorParam);
	}
	
	// Get Query Results
	public static Object getResultList(String ns, String nsId, Object result) {
		String key = ns+"."+nsId;
		return ((Map<String, Object>) result).get(key);
	}
}

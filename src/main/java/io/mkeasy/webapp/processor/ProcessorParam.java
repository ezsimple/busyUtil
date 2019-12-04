package io.mkeasy.webapp.processor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.map.CaseInsensitiveMap;

public class ProcessorParam {
	private String queryPath;
	private String action;
	private String loopId;
	private CaseInsensitiveMap params;
	private Map<String, Object> context;
	private ServletRequest request;
	private ServletResponse response;
	private List<String> processorList;
	private Map<String, Object> processorResult;
	
	public String getLoopId() {
		return loopId;
	}
	public ServletResponse getResponse() {
		return response;
	}
	public void setResponse(ServletResponse response) {
		this.response = response;
	}
	public Map<String, Object> getProcessorResult() {
		return processorResult;
	}
	public Object getProcessorResultByid(String key) {
		return processorResult.get(key);
	}
	public void setProcessorResult(Map<String, Object> processorResult) {
		this.processorResult = processorResult;
	}
	public void setProcessorList(List<String> processorList) {
		this.processorList = processorList;
	}
	public List<String> getProcessorList() {
		return processorList;
	}
	public void addProcessor(String processorId){
		processorList.add(processorId);
	}
	public ProcessorParam(String loopId){
		context = new HashMap<String, Object>();
		processorList = new ArrayList<String>();
		this.loopId = loopId;
	}
	public String getQueryPath() {
		return queryPath;
	}
	public void setQueryPath(String queryPath) {
		this.queryPath = queryPath;
	}
	public CaseInsensitiveMap getParams() {
		return params;
	}
	public void setParams(CaseInsensitiveMap params) {
		this.params = params;
		ProcessorServiceFactory.setReqParam((HttpServletRequest)request, params, loopId);

	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public ServletRequest getRequest() {
		return request;
	}
	public void setRequest(ServletRequest request) {
		this.request = request;
		ProcessorServiceFactory.setReqParam((HttpServletRequest)request, params, loopId);
	}
	public Map<String, Object> getContext() {
		return context;
	}
	public void setAttribute(String key, Object value){
		context.put(key, value);
		
	}
	public Object getAttribute(String key){
		return context.get(key);
		
	}
}

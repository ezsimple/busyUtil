package io.mkeasy.resolver;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.collections.map.CaseInsensitiveMap;
import org.apache.commons.lang.StringUtils;

import io.mkeasy.utils.JSONUtil;
import io.mkeasy.utils.MapUtil;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class CommandMap {

	Map<String,Object> map = new HashMap<String,Object>();

    public Object get(String key){
        return map.get(key);
    }
    
    // 단일 파라미터의 경우
    // 주의 : trim 이 추가됨
    public String getParam(String key){
    	Object obj = this.get(key);
    	if(obj == null) return null;
    	if(obj instanceof Integer 
    			|| obj instanceof Double
    			|| obj instanceof String) {
    		return StringUtils.trim(String.valueOf(obj));
    	}
        return StringUtils.trim(((String[])obj)[0]);
    }

    public void put(String key, Object value){
        map.put(key, value);
    }

    public Object remove(String key){
        return map.remove(key);
    }

    public boolean containsKey(String key){
        return map.containsKey(key);
    }

    public boolean containsValue(Object value){
        return map.containsValue(value);
    }

    public void clear(){
        map.clear();
    }

    public Set<Entry<String, Object>> entrySet(){
        return map.entrySet();
    }

    public Set<String> keySet(){
        return map.keySet();
    }

    public boolean isEmpty(){
        return map.isEmpty();
    }

    public void putAll(Map<? extends String, ?extends Object> m){
        map.putAll(m);
    }

    public Map<String,Object> getMap(){
        return map;
    }
    
    public CaseInsensitiveMap getQueryMap() {
		String key = null;
		String[] values = null;
		CaseInsensitiveMap map = new CaseInsensitiveMap();
    	for(Entry<String, Object> entry : this.getMap().entrySet()) {
    		key = entry.getKey();
    		Object o = entry.getValue();
    		if(o instanceof String) {
                map.put(key, o);
    		}
    		if(o instanceof String[]) {
                values = (String[]) entry.getValue();
                map.put(key, (values.length>1?values:values[0]));
    		}
    	}
    	return map;
    }
    
    public void debugParams() {
//    	for(Entry<String, Object> entry : this.getMap().entrySet()) {
//    		log.debug("{}:{}",entry.getKey(), entry.getValue());
//    	}
    	// Class<?> caller = sun.reflect.Reflection.getCallerClass(); // java.lang.InternalError: CallerSensitive annotation expected at frame 1
    	Class<?> source = org.slf4j.helpers.Util.getCallingClass();
    	StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
    	StackTraceElement e = stacktrace[2];//maybe this number needs to be corrected
    	log.debug("{}.{}:{}, params => {}" ,
    			source.getSimpleName(), // className
    			e.getMethodName(), // methodName
    			e.getLineNumber(), 		// sourceLineNumber
    			this.getMap());			// parameters
    }
    
    // for CacheKeyGenerator
    public String toString() {
		return JSONUtil.convertMapToJson(this.getMap()).toString();
    }
    
}
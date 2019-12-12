package io.mkeasy.resolver;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommandMap implements Serializable {

	private static final long serialVersionUID = -3540606202757918L;

	Map<String,Object> map = new HashMap<String,Object>();

    public Object get(String key){
        return map.get(key);
    }
    
    // 단일 파라미터의 경우
    public String getParam(String key){
    	Object obj = this.get(key);
    	if(obj == null) return null;
    	if(obj instanceof Integer 
    			|| obj instanceof Double
    			|| obj instanceof String) {
    		return String.valueOf(obj);
    	}
        return ((String[])obj)[0];
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
    
    public Map<String, Object> getQueryMap() {
		String key = null;
		String[] values = null;
		Map<String, Object> map = new HashMap<String, Object>();
    	for(Entry<String, Object> entry : this.getMap().entrySet()) {
    		key = entry.getKey();
    		values = (String[]) entry.getValue();
    		map.put(key, (values.length>1?values:values[0]));
    	}
    	return map;
    }
    
    public void debugParams() {
    	for(Entry<String, Object> entry : this.getMap().entrySet()) {
    		log.debug("{}:{}",entry.getKey(), entry.getValue());
    	}
    }
    
}
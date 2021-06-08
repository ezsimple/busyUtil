package io.mkeasy.webapp.processor;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletContext;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.hubspot.jinjava.Jinjava;

import io.mkeasy.resolver.CommandMap;
import io.mkeasy.utils.JSONUtil2;
import io.mkeasy.utils.WebUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EsFactory {

	@Autowired
	WebUtil webUtil;

	@Autowired
	ServletContext context;

	public String getEsQuery(String path, String fileName) throws IOException {
		String queryPath = "/WEB-INF/sql/elastic";
        queryPath += "/"+path+"/"+fileName+".json";
		InputStream is = context.getResourceAsStream(queryPath);
		String jsonQuery = IOUtils.toString(is, "UTF-8"); 
		return jsonQuery;
	}

	public JSONObject renderEsQuery(String entity, CommandMap commandMap) {

		Jinjava jinjava = new Jinjava();
		String template = jinjava.render(entity, commandMap.getEgovMap());
		JSONObject renderedObj = new JSONObject(template);

		return renderedObj;
	}

	public JSONObject getJSONObject(JSONObject innerObj, String key) {
		return JSONUtil2.getJSONObject(innerObj, key);
	}

	public String getValue(JSONObject tmpObj, String key, String defaultValue, boolean roundOk) {
		return JSONUtil2.getValue(tmpObj, key, defaultValue, roundOk);
	}

}
